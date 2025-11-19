import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

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

        ArrayList<BigDecimal> numbers = new ArrayList<>();

        while (!currentToken.getValue().equals(")")) {
            if (currentToken.getType() != TokenType.NUMBER) {
                System.out.println("Expected number after INSERT");
                return;
            }
            BigDecimal num = new BigDecimal(currentToken.getValue());
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


            BigDecimal rightNum = null;
            String rightColumn = null;
            boolean compareColumn = false;
            if (currentToken.getType() == TokenType.NUMBER) {
                rightNum =new BigDecimal(currentToken.getValue());
            }
            if (currentToken.getType() == TokenType.IDENTIFIER) {
                compareColumn = true;
                rightColumn = currentToken.getValue();
            }

            ArrayList<ArrayList<BigDecimal>> filteredRows = applyWhere(foundTable, leftColumn, compareColumn, rightColumn, rightNum);

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
        if (!table.getName().startsWith("Grouped ")) {

            ArrayList<String> newCols = new ArrayList<>();
            ArrayList<Boolean> newInd = new ArrayList<>();
            ArrayList<BigDecimal> resultRow = new ArrayList<>();

            for (int i = 0; i < funcs.size(); i++) {

                String func = funcs.get(i);
                String colName = cols.get(i);

                int colIdx = table.getColumns().indexOf(colName);
                if (colIdx == -1) {
                    System.out.println("Column '" + colName + "' not found");
                    resultRow.add(BigDecimal.ZERO);
                    continue;
                }

                ArrayList<BigDecimal> values = new ArrayList<>();
                for (ArrayList<BigDecimal> r : table.getRows()) {
                    values.add(r.get(colIdx));
                }

                BigDecimal res = compute(func, values);

                newCols.add(func + "(" + colName + ")");
                newInd.add(false);
                resultRow.add(res);
            }

            Table result = new Table("Aggregation", newCols, newInd);
            result.rows.add(resultRow);
            return result;
        }
        Table grouped = table;
        original = (grouped.original != null ? grouped.original : grouped);

        ArrayList<String> groupCols = grouped.getColumns();

        ArrayList<String> resultCols = new ArrayList<>(groupCols);
        ArrayList<Boolean> resultInd = new ArrayList<>(grouped.getIndexed());
        for (int i = 0; i < funcs.size(); i++) {
            resultCols.add(funcs.get(i) + "(" + cols.get(i) + ")");
            resultInd.add(false);
        }

        ArrayList<ArrayList<BigDecimal>> resultRows = new ArrayList<>();

        for (ArrayList<BigDecimal> groupKey : grouped.getRows()) {
            ArrayList<ArrayList<BigDecimal>> matching = new ArrayList<>();
            for (ArrayList<BigDecimal> row : original.getRows()) {

                boolean ok = true;

                for (int k = 0; k < groupCols.size(); k++) {
                    String gcol = groupCols.get(k);
                    int colIdx = original.getColumns().indexOf(gcol);

                    if (colIdx == -1 || row.get(colIdx).compareTo(groupKey.get(k)) != 0) {
                        ok = false;
                        break;
                    }
                }

                if (ok){
                    matching.add(row);
                }
            }
            ArrayList<BigDecimal> newRow = new ArrayList<>(groupKey);
            for (int i = 0; i < funcs.size(); i++) {

                String func = funcs.get(i);
                String colName = cols.get(i);

                int colIdx = original.getColumns().indexOf(colName);

                if (colIdx == -1) {
                    System.out.println("Column '" + colName + "' not found");
                    newRow.add(BigDecimal.ZERO);
                    continue;
                }

                ArrayList<BigDecimal> values = new ArrayList<>();
                for (ArrayList<BigDecimal> r : matching) {
                    values.add(r.get(colIdx));
                }

                newRow.add(compute(func, values));
            }

            resultRows.add(newRow);
        }

        Table result = new Table("GroupedAgg " + original.getName(), resultCols, resultInd);
        result.rows = resultRows;
        return result;
    }

    private BigDecimal compute(String func, ArrayList<BigDecimal> values) {

        if (func.equals("COUNT")) {
            return BigDecimal.valueOf(values.size());
        }

        if (func.equals("MAX")) {
            return values.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        }

        if (func.equals("AVG")) {
            if (values.isEmpty()) return BigDecimal.ZERO;
            BigDecimal sum = BigDecimal.ZERO;
            for (BigDecimal v : values) {
                sum = sum.add(v);
            }
            return sum.divide(BigDecimal.valueOf(values.size()), 20, RoundingMode.HALF_UP);
        }

        return BigDecimal.ZERO;
    }


    public ArrayList<ArrayList<BigDecimal>> applyWhere(Table foundTable, String leftColumn, boolean compareColumn, String rightColumn, BigDecimal rightNum) {
        ArrayList<ArrayList<BigDecimal>> filteredRows = new ArrayList<>();
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
            BigDecimal leftValue = foundTable.getRows().get(row).get(leftIndx);
            if (compareColumn) {
                BigDecimal rightValue = foundTable.getRows().get(row).get(rightIndx);
                if (leftValue.compareTo(rightValue) == 0) {
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

        Table result = new Table("Grouped " + foundTable.getName(), newCols, newInd);

        ArrayList<ArrayList<BigDecimal>> grouped = new ArrayList<>();

        for (ArrayList<BigDecimal> row : foundTable.getRows()) {
            ArrayList<BigDecimal> key = new ArrayList<>();
            for (int idx : groupIdx) {
                key.add(row.get(idx));
            }
            if (!grouped.contains(key)) {
                grouped.add(key);
            }
        }

        result.rows = grouped;
        return result;
    }
}
