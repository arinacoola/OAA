import java.util.ArrayList;
import java.util.Collections;

public class Parser {
    Lexer lexer;
    Token currentToken;
    ArrayList<Table> tables;

    public Parser(Lexer lexer, Token currentToken, ArrayList<Table> tables) {
        this.lexer = lexer;
        this.currentToken = currentToken;
        this.tables = tables;
    }

    public void getToken() {
        currentToken = lexer.newToken();
    }

    public void recognizeCmnd(Token currentToken) {
        if (currentToken.getType() != TokenType.KEYWORD) {
            return;
        } else {
            if (currentToken.getValue().equalsIgnoreCase("CREATE")) {
                parserCreate();
            } else if (currentToken.getValue().equalsIgnoreCase("INSERT")) {
                parserInsert();
            }else if (currentToken.getValue().equalsIgnoreCase("SELECT")) {
            parserSelect();
            }
        }
    }


    public void parserCreate() {
        getToken();

        if (currentToken.getType() != TokenType.IDENTIFIER) {
            System.out.println("Incorrect table name");
            return;
        }
        String tableName = currentToken.getValue();

        for (Table t : tables) {
            if (t.getName().equalsIgnoreCase(tableName)) {
                System.out.println("Table '" + tableName + "' already exists.");
                return;
            }
        }

        getToken();
        if (!currentToken.getValue().equals("(")) {
            System.out.println("Expected '(' after table name");
            return;
        }
        getToken();

        ArrayList<String> columns = new ArrayList<>();
        ArrayList<Boolean> indexedValues = new ArrayList<Boolean>();

        while (!currentToken.getValue().equals(")")) {
            if (currentToken.getType() != TokenType.IDENTIFIER) {
                System.out.println("Column name expected");
                return;
            }
            String columnName = currentToken.getValue();
            getToken();

            boolean indexed=false;
            if (currentToken.getValue().equalsIgnoreCase("INDEXED")) {
                indexed = true;
                getToken();
            }
            columns.add(columnName);
            indexedValues.add(indexed);

            if (currentToken.getValue().equals(",")) {
                getToken();
                continue;
            }
            if (currentToken.getValue().equals(")")) {
                break;
            }
            if (!currentToken.getValue().equals(",") && !currentToken.getValue().equals(")")) {
                System.out.println("Expected ',' or ')' after column definition");
            }
            getToken();
        }

        getToken();
        if (!currentToken.getValue().equals(";")) {
            System.out.println("Expected ';' at the end of CREATE command");
            return;
        }

        Table newTable = new Table(tableName, columns, indexedValues);
        tables.add(newTable);
        System.out.println("Table " + tableName + " has been created");
    }

    public void parserInsert() {
        getToken();

        if (currentToken.getValue().equalsIgnoreCase("INTO")) {
            getToken();
        }

        if (currentToken.getType() != TokenType.IDENTIFIER) {
            System.out.println("Incorrect table name");
            return;
        }
        String tableName = currentToken.getValue();

        Table foundTable = null;
        for (Table t : tables) {
            if (t.getName().equalsIgnoreCase(tableName)) {
                foundTable = t;
                break;
            }
        }
        if (foundTable == null) {
            System.out.println("Table '" + tableName + "' does not exist.");
            return;
        }

        getToken();

        if (!currentToken.getValue().equals("(")) {
            System.out.println("Expected '(' after table name");
            return;
        }

        getToken();

        ArrayList<Double> numbers = new ArrayList<>();

        while (!currentToken.getValue().equals(")")) {
            if (currentToken.getType() != TokenType.NUMBER) {
                System.out.println("Expected number after INSERT");
                return;
            }
            double num = Double.parseDouble(currentToken.getValue());
            numbers.add(num);
            getToken();

            if (currentToken.getValue().equals(",")) {
                getToken();
                continue;
            }
            if (currentToken.getValue().equals(")")) {
                break;
            }
            if (!currentToken.getValue().equals(",") && !currentToken.getValue().equals(")")) {
                System.out.println("Expected ',' or ')' after numeric value");
                return;

            }
            getToken();

        }

        if (numbers.size() != foundTable.getColumns().size()) {
            System.out.println("Number of values doesn't match number of columns in table");
            return;
        }

        getToken();
        if (!currentToken.getValue().equals(";")) {
            System.out.println("Expected ';' at the end of INSERT command");
            return;
        }


        foundTable.addRow(numbers);
        System.out.println("1 row has been inserted into " + tableName);


    }

    public void parserSelect() {
        getToken();

        ArrayList<String> aggFunc = new ArrayList<>();
        ArrayList<String> aggColumns = new ArrayList<>();
        if (currentToken.getType() == TokenType.KEYWORD && (currentToken.getValue().equalsIgnoreCase("COUNT") || currentToken.getValue().equalsIgnoreCase("MAX") || currentToken.getValue().equalsIgnoreCase("AVG"))) {
            while (true) {
                String func = currentToken.getValue().toUpperCase();
                getToken();
                if (!currentToken.getValue().equals("(")) {
                    System.out.println("Expected '(' after " + func);
                    return;
                }
                getToken();
                if (currentToken.getType() != TokenType.IDENTIFIER) {
                    System.out.println("Expected column name in " + func);
                    return;
                }
                String column = currentToken.getValue();
                getToken();
                if (!currentToken.getValue().equals(")")) {
                    System.out.println("Expected ')' after column name");
                    return;
                }
                aggFunc.add(func);
                aggColumns.add(column);
                getToken();
                if (currentToken.getValue().equals(",")) {
                    getToken();
                    continue;
                }
                else {
                    break;
                }
            }
        }
            if (!currentToken.getValue().equalsIgnoreCase("FROM")) {
            System.out.println("Expected FROM after SELECT");
            return;
        }
        getToken();

        if (currentToken.getType() != TokenType.IDENTIFIER) {
            System.out.println("Expected table name after FROM");
            return;
        }
        String tableName = currentToken.getValue();

        Table foundTable = null;
        for (Table t : tables) {
            if (t.getName().equalsIgnoreCase(tableName)) {
                foundTable = t;
                break;
            }
        }
        if (foundTable == null) {
            System.out.println("Table '" + tableName + "' does not exist.");
            return;
        }

        getToken();


        if (currentToken.getValue().equalsIgnoreCase("WHERE")) {
            getToken();
            if (currentToken.getType() != TokenType.IDENTIFIER) {
                System.out.println("Expected column name after WHERE");
                return;
            }
            String leftColumn = currentToken.getValue();
            getToken();
            if (!currentToken.getValue().equals("=")) {
                System.out.println("Expected '=' in WHERE condition");
                return;
            }
            getToken();


            Double rightNum = null;
            String rightColumn = null;
            boolean compareColumn = false;
            if (currentToken.getType() == TokenType.NUMBER) {
                rightNum = Double.parseDouble(currentToken.getValue());
            }
            if (currentToken.getType() == TokenType.IDENTIFIER) {
                compareColumn = true;
                rightColumn = currentToken.getValue();
            }

            ArrayList<ArrayList<Double>> filteredRows = applyWhere(foundTable, leftColumn, compareColumn, rightColumn, rightNum);

            Table result = new Table(foundTable.getName(), foundTable.getColumns(), foundTable.getIndexed());
            result.rows = filteredRows;
            foundTable = result;
            getToken();

        }

        if (currentToken.getValue().equalsIgnoreCase("GROUP")) {
            getToken();
            if (!currentToken.getValue().equalsIgnoreCase("BY")) {
                System.out.println("Expected 'BY' after GROUP");
                return;
            }
            getToken();
            ArrayList<String> groupColumns = new ArrayList<>();

            if (currentToken.getType() != TokenType.IDENTIFIER) {
                System.out.println("Expected column name after GROUP BY");
                return;
            }
            while (true) {
                groupColumns.add(currentToken.getValue());
                getToken();

                if (currentToken.getValue().equals(",")) {
                    getToken();
                    continue;
                }
                else {
                    break;
                }
            }
            Table grouped = applyGroupBy(foundTable, groupColumns);
            foundTable = grouped;
        }
        if (!currentToken.getValue().equals(";")) {
            System.out.println("Expected ';' at the end of SELECT command");
            return;
        }

        if (!aggFunc.isEmpty()) {
            Table aggr = applyAggr(foundTable, aggFunc, aggColumns);
            aggr.printTable();
            return;
        }
            foundTable.printTable();
    }

    public Table applyAggr(Table table, ArrayList<String> func, ArrayList<String> columns) {
        ArrayList<String> newColumns = new ArrayList<>();
        ArrayList<Boolean> ind = new ArrayList<>();
        ArrayList<ArrayList<Double>> newRows = new ArrayList<>();

        if (table.getName().startsWith("Grouped_")) {
            newColumns.addAll(table.getColumns());
            for (String f : func) {
                for (String c : columns) {
                    newColumns.add(f + "(" + c + ")");
                }
            }

            ArrayList<Integer> groupInd = new ArrayList<>();
            for (int i = 0; i < table.getColumns().size(); i++) {
                groupInd.add(i);
            }

            for (ArrayList<Double> groupRow : table.getRows()) {
                ArrayList<Double> newRow = new ArrayList<>(groupRow);
                ArrayList<ArrayList<Double>> matching = new ArrayList<>();

                for (ArrayList<Double> row : table.getRows()) {
                    boolean same = true;
                    for (int idx : groupInd) {
                        if (!row.get(idx).equals(groupRow.get(idx))) {
                            same = false;
                            break;
                        }
                    }
                    if (same) {
                        matching.add(row);
                    }
                }

                for (int i = 0; i < func.size(); i++) {
                    String func1 = func.get(i);
                    String colName = columns.get(i);
                    int colInd = table.getColumns().indexOf(colName);
                    ArrayList<Double> values = new ArrayList<>();
                    for (ArrayList<Double> r : matching) {
                        values.add(r.get(colInd));
                    }
                    if (values.isEmpty()) {
                        newRow.add(0.0);
                        continue;
                    }

                    double result = 0;
                    if (func1.equals("COUNT")) {
                        result = values.size();
                    }
                    if (func1.equals("MAX")) {
                        result = Collections.max(values);
                    }
                    if (func1.equals("AVG")) {
                        double sum = 0;
                        for (double v : values) {
                            sum += v;
                        }
                        if (values.size() > 0) {
                            result = sum / values.size();
                        }
                        else {
                            result = 0;
                        }
                    }
                    newRow.add(result);
                }
                newRows.add(newRow);
            }

            Table resultTable = new Table("GroupedAgg_" + table.getName(), newColumns, ind);
            resultTable.rows = newRows;
            return resultTable;
        }

        ArrayList<Double> resultRow = new ArrayList<>();
        for (int i = 0; i < func.size(); i++) {
            String func1 = func.get(i);
            String colName = columns.get(i);
            int colInd = table.getColumns().indexOf(colName);
            if (colInd == -1) {
                System.out.println("Column '" + colName + "' not found");
                continue;
            }
            ArrayList<Double> val = new ArrayList<>();
            for (ArrayList<Double> row : table.getRows()) {
                val.add(row.get(colInd));
            }

            double result = 0;
            if (func1.equals("COUNT")) {
                result = val.size();
            }
            if (func1.equals("MAX")) {
                result = Collections.max(val);
            }
            if (func1.equals("AVG")) {
                double sum = 0;
                for (int k = 0; k < val.size(); k++) {
                    sum += val.get(k);
                }
                if (val.size() > 0) {
                    result = sum / val.size();
                }
                else {
                    result = 0;
                }
            }
            resultRow.add(result);
            newColumns.add(func1 + "(" + colName + ")");
        }
        newRows.add(resultRow);
        Table resultTable = new Table("Aggregation", newColumns, ind);
        resultTable.rows = newRows;
        return resultTable;
    }


    public ArrayList<ArrayList<Double>> applyWhere(Table foundTable,String leftColumn,boolean compareColumn,String rightColumn,Double rightNum){
        ArrayList<ArrayList<Double>> filteredRows=new ArrayList<>();
        int leftIndx=-1;
        int rightIndx=-1;

        for( int i=0;i<foundTable.getColumns().size();i++){
            if(foundTable.getColumns().get(i).equals(leftColumn)){
                leftIndx=i;
            }
            if(compareColumn){
                if(foundTable.getColumns().get(i).equals(rightColumn)){
                    rightIndx=i;
                }
            }
        }
        if(leftIndx==-1){
            System.out.println("Column '" + leftColumn + "' does not exist in table");
            return new ArrayList<>();
        }
        if (compareColumn && rightIndx == -1) {
            System.out.println("Column '" + rightColumn + "' does not exist in table");
            return new ArrayList<>();
        }



        for(int row=0;row<foundTable.getRows().size();row++){
            Double leftValue=foundTable.getRows().get(row).get(leftIndx);
            if(compareColumn){
                Double rightValue=foundTable.getRows().get(row).get(rightIndx);
                if (leftValue.equals(rightValue)) {
                    filteredRows.add(foundTable.getRows().get(row));
                }
            }
            if(!compareColumn){
                if(leftValue.equals(rightNum)){
                    filteredRows.add(foundTable.getRows().get(row));
                }
            }

        }

        return filteredRows;

    }

    public Table applyGroupBy(Table foundTable, ArrayList<String> groupColumns) {
        Table result = new Table("Grouped_" + foundTable.getName(), foundTable.getColumns(), foundTable.getIndexed());
        ArrayList<ArrayList<Double>> groupedRows = new ArrayList<>();
        ArrayList<Integer> groupIndx = new ArrayList<>();
        for (int i = 0; i < foundTable.getColumns().size(); i++) {
            if (groupColumns.contains(foundTable.getColumns().get(i))) {
                groupIndx.add(i);
            }
        }
        for (ArrayList<Double> currentRow : foundTable.getRows()) {
            ArrayList<Double> groupKey = new ArrayList<>();
            for (int idx : groupIndx) {
                groupKey.add(currentRow.get(idx));
            }
            boolean exists = false;
            for (ArrayList<Double> existingRow : groupedRows) {
                if (existingRow.equals(groupKey)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                groupedRows.add(groupKey);
            }
        }

        result.rows = groupedRows;
        return result;
    }





}
