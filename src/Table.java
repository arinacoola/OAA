import java.util.ArrayList;
import java.util.List;

public class Table {
    String name;
    ArrayList<String> columns;
    ArrayList<Boolean> indexed;
    ArrayList<ArrayList<Double>> rows;

    public Table(String name, ArrayList<String> columns, ArrayList<Boolean> indexed) {
        this.name = name;
        this.columns = columns;
        this.indexed = indexed;
        this.rows = new ArrayList<>();
    }

    public void addRow(List<Double> values) {
        if (values.size() != columns.size()) {
            System.out.println("Number of values doesn't match number of columns");
        } else {
            rows.add(new ArrayList<>(values));
        }
    }

    public void printTable() {
        if (columns == null || columns.isEmpty()) {
            System.out.println("Table has no columns defined");
            return;
        }
        if (rows == null) {
            rows = new ArrayList<>();
        }

        int[] width = new int[columns.size()];

        for (int clmn = 0; clmn < columns.size(); clmn++) {
            width[clmn] = columns.get(clmn).length();
            for (ArrayList<Double> row : rows) {
                if (clmn >= row.size()){
                    continue;
                }
                String text = String.valueOf(row.get(clmn));
                if (text.length() > width[clmn]) {
                    width[clmn] = text.length();
                }
            }
        }

        printLine(width);

        StringBuilder header = new StringBuilder("|");
        for (int clmn = 0; clmn < columns.size(); clmn++) {
            String text = columns.get(clmn);
            int numSpaces = Math.max(0, width[clmn] - text.length());
            header.append(" ").append(text).append(" ".repeat(numSpaces)).append(" |");
        }
        System.out.println(header);

        printLine(width);
        if (rows.isEmpty()) {
            System.out.println("| (no data) " + " ".repeat(Math.max(0, header.length() - 12)) + "|");
            printLine(width);
            return;
        }

        for (ArrayList<Double> row : rows) {
            StringBuilder rowLine = new StringBuilder("|");
            for (int clmn = 0; clmn < columns.size(); clmn++) {
                Double value = row.get(clmn);
                String text = String.format("%.2f", value);
                int leftSpaces = Math.max(0, width[clmn] - text.length());
                rowLine.append(" ").append(" ".repeat(leftSpaces)).append(text).append(" |");
            }
            System.out.println(rowLine);
        }

        printLine(width);
    }


    private void printLine ( int[] width){
        String line = "";
        for (int clmn = 0; clmn < columns.size(); clmn++) {
            line = line + "+";
            for (int i = 0; i < width[clmn] + 1; i++) {
                line = line + "-";
            }
            line = line + "-";
        }
        line = line + "+";
        System.out.println(line);

    }

    public String getName () {
        return name;
    }

    public ArrayList<String> getColumns () {
        return columns;
    }

    public ArrayList<Boolean> getIndexed () {
        return indexed;
    }

    public ArrayList<ArrayList<Double>> getRows () {
        return rows;
    }
}
