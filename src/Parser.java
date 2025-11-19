import java.util.ArrayList;
import java.util.Collections;

public class Parser {
    Lexer lexer;
    Token currentToken;
    ArrayList<Table> tables;
    private Table original;

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
            } else if (currentToken.getValue().equalsIgnoreCase("SELECT")) {
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
            if (t.getName().equals(tableName)) {
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

            boolean indexed = false;
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
            if (t.getName().equals(tableName)) {
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
                } else {
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
            if (t.getName().equals(tableName)) {
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
            Table savedOriginal = foundTable;
            Table grouped = applyGroupBy(foundTable, groupColumns);
            grouped.original = savedOriginal;
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

    public Table applyAggr(Table table, ArrayList<String> funcs, ArrayList<String> cols) {
        if (!table.getName().startsWith("Grouped_")) {

            ArrayList<String> newCols = new ArrayList<>();
            ArrayList<Boolean> inds = new ArrayList<>();
            ArrayList<Double> resultRow = new ArrayList<>();

            for (int i = 0; i < funcs.size(); i++) {
                String func = funcs.get(i);
                String colName = cols.get(i);

                int colIdx = table.getColumns().indexOf(colName);
                if (colIdx == -1) {
                    System.out.println("Column '" + colName + "' not found");
                    continue;
                }

                ArrayList<Double> values = new ArrayList<>();
                for (ArrayList<Double> r : table.getRows()) {
                    values.add(r.get(colIdx));
                }
                double res = 0;
                if (func.equals("COUNT")) {
                    res = values.size();
                }

                if (func.equals("MAX")) {
                    res = Collections.max(values);
                }

                if (func.equals("AVG")) {
                    double sum = 0;
                    for (double v : values){
                        sum += v;
                    }
                    if (values.size() > 0) {
                        res = sum / values.size();
                    }
                    else {
                        res = 0;
                    }
                }

                resultRow.add(res);
                newCols.add(func + "(" + colName + ")");
                inds.add(false);
            }

            Table result = new Table("Aggregation", newCols, inds);
            result.rows.add(resultRow);
            return result;
        }

        Table grouped = table;
        if (grouped.original != null) {
            original = grouped.original;
        } else {
            original = grouped;
        }

        ArrayList<String> groupCols = grouped.getColumns();
        ArrayList<String> resultCols = new ArrayList<>(groupCols);
        ArrayList<Boolean> resultInd = new ArrayList<>(grouped.getIndexed());
        ArrayList<ArrayList<Double>> resultRows = new ArrayList<>();

        for (int i = 0; i < funcs.size(); i++) {
            resultCols.add(funcs.get(i) + "(" + cols.get(i) + ")");
            resultInd.add(false);
        }

        for (ArrayList<Double> groupKey : grouped.getRows()) {
            ArrayList<ArrayList<Double>> matching = new ArrayList<>();
            for (ArrayList<Double> row : original.getRows()) {
                boolean ok = true;
                for (int k = 0; k < groupCols.size(); k++) {
                    String gcol = groupCols.get(k);
                    int origIdx = original.getColumns().indexOf(gcol);

                    if (!row.get(origIdx).equals(groupKey.get(k))) {
                        ok = false;
                        break;
                    }
                }

                if (ok) {
                    matching.add(row);
                }
            }

            ArrayList<Double> newRow = new ArrayList<>(groupKey);

            for (int i = 0; i < funcs.size(); i++) {
                String func = funcs.get(i);
                String colName = cols.get(i);

                int colIdx = original.getColumns().indexOf(colName);
                if (colIdx == -1) {
                    System.out.println("Column '" + colName + "' not found.");
                    newRow.add(0.0);
                    continue;
                }

                ArrayList<Double> values = new ArrayList<>();
                for (ArrayList<Double> r : matching) values.add(r.get(colIdx));

                double res = 0;
                if (func.equals("COUNT")) {
                    res = values.size();
                }

                if (func.equals("MAX")) {
                    res = Collections.max(values);
                }

                if (func.equals("AVG")) {
                    double sum = 0;
                    for (double v : values){
                        sum += v;
                    }
                    if (values.size() > 0){
                        res = sum / values.size();
                    }
                    else {
                        res = 0;
                    }
                }
                newRow.add(res);
            }
            resultRows.add(newRow);
        }
        Table result = new Table("GroupedAgg_" + original.getName(), resultCols, resultInd);
        result.rows = resultRows;
        return result;
    }


    public ArrayList<ArrayList<Double>> applyWhere(Table foundTable, String leftColumn, boolean compareColumn, String rightColumn, Double rightNum) {
        ArrayList<ArrayList<Double>> filteredRows = new ArrayList<>();
        int leftIndx = -1;
        int rightIndx = -1;

        for (int i = 0; i < foundTable.getColumns().size(); i++) {
            if (foundTable.getColumns().get(i).equals(leftColumn)) {
                leftIndx = i;
            }
            if (compareColumn) {
                if (foundTable.getColumns().get(i).equals(rightColumn)) {
                    rightIndx = i;
                }
            }
        }
        if (leftIndx == -1) {
            System.out.println("Column '" + leftColumn + "' does not exist in table");
            return new ArrayList<>();
        }
        if (compareColumn && rightIndx == -1) {
            System.out.println("Column '" + rightColumn + "' does not exist in table");
            return new ArrayList<>();
        }


        for (int row = 0; row < foundTable.getRows().size(); row++) {
            Double leftValue = foundTable.getRows().get(row).get(leftIndx);
            if (compareColumn) {
                Double rightValue = foundTable.getRows().get(row).get(rightIndx);
                if (leftValue.equals(rightValue)) {
                    filteredRows.add(foundTable.getRows().get(row));
                }
            }
            if (!compareColumn) {
                if (leftValue.equals(rightNum)) {
                    filteredRows.add(foundTable.getRows().get(row));
                }
            }

        }

        return filteredRows;

    }

    public Table applyGroupBy(Table foundTable, ArrayList<String> groupColumns) {
        ArrayList<Integer> groupIdx = new ArrayList<>();
        for (String col : groupColumns) {
            int idx = foundTable.getColumns().indexOf(col);
            if (idx == -1) {
                System.out.println("Column '" + col + "' not found");
                return foundTable;
            }
            groupIdx.add(idx);
        }
        ArrayList<String> newCols = new ArrayList<>(groupColumns);
        ArrayList<Boolean> newInd = new ArrayList<>();
        for (int idx : groupIdx) newInd.add(foundTable.getIndexed().get(idx));

        Table result = new Table("Grouped_" + foundTable.getName(), newCols, newInd);

        ArrayList<ArrayList<Double>> grouped = new ArrayList<>();

        for (ArrayList<Double> row : foundTable.getRows()) {
            ArrayList<Double> key = new ArrayList<>();
            for (int idx : groupIdx) key.add(row.get(idx));

            boolean exists = false;
            for (ArrayList<Double> g : grouped) {
                if (g.equals(key)) {
                    exists = true;
                    break;
                }
            }
            if (!exists){
                grouped.add(key);
            }
        }
        result.rows = grouped;
        return result;
    }
}
