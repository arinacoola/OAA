import java.util.ArrayList;

public class Parser {
    Lexer lexer;
    Token currentToken;
    ArrayList<Table> tables = new ArrayList<>();

    public Parser(Lexer lexer, Token currentToken) {
        this.lexer = lexer;
        this.currentToken = currentToken;
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
            result.printTable();

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
            while (true) {
                if (currentToken.getType() != TokenType.IDENTIFIER) {
                    System.out.println("Expected column name in GROUP BY");
                    return;
                }

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
            foundTable.printTable();
    }

    public ArrayList<ArrayList<Double>> applyWhere(Table foundTable,String leftColumn,boolean compareColumn,String rightColumn,Double rightNum){
        ArrayList<ArrayList<Double>> filteredRows=new ArrayList<>();
        int leftIndx=-1;
        int rightIndx=-1;

        for( int i=0;i<foundTable.getColumns().size();i++){
            if(foundTable.getColumns().get(i).equals(leftColumn)){
                leftIndx=i;
            }
            if(compareColumn == true){
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
            if(compareColumn == true){
                Double rightValue=foundTable.getRows().get(row).get(rightIndx);
                if (leftValue.equals(rightValue)) {
                    filteredRows.add(foundTable.getRows().get(row));
                }
            }
            if(compareColumn==false){
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

        for (int row = 0; row < foundTable.getRows().size(); row++) {
            ArrayList<Double> currentRow = foundTable.getRows().get(row);
            ArrayList<Double> groupKey = new ArrayList<>();
            for (int idx : groupIndx) {
                groupKey.add(currentRow.get(idx));
            }
            boolean exists = false;
            for (ArrayList<Double> existingRow : groupedRows) {
                boolean same = true;
                for (int idx : groupIndx) {
                    if (!existingRow.get(idx).equals(currentRow.get(idx))) {
                        same = false;
                        break;
                    }
                }
                if (same) {
                    exists = true;
                    break;
                }

            }

            if (!exists) groupedRows.add(new ArrayList<>(currentRow));
        }

        result.rows = groupedRows;


        return result;
    }




}
