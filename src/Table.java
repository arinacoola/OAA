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
        ;
    }

    public void addRow(List<Double> values) {
        if (values.size() != columns.size()) {
            System.out.println("Number of values doesn't match number of columns");
        } else {
            rows.add(new ArrayList<>(values));
        }
    }

    public void printTable() {
        int[] width = new int[columns.size()];

        for (int clmn = 0; clmn < columns.size(); clmn++) {
            width[clmn] = columns.get(clmn).length();
            for (int row = 0; row < rows.size(); row++) {
                ArrayList<Double> currentRow = rows.get(row);
                Double value = currentRow.get(clmn);
                String text = String.valueOf(value);

                if (text.length() > width[clmn]) {
                    width[clmn] = text.length();
                }
            }
        }

        printLine(width);

        String header = "|";
        for (int clmn = 0; clmn < columns.size(); clmn++) {
            String text = columns.get(clmn);
            int numSpaces = width[clmn] - text.length();
            header = header + " " + text + " ".repeat(numSpaces) + " |";
        }
        System.out.println(header);

        printLine(width);

        //рядки
        if (rows.isEmpty()) {
            System.out.println("No data");
        }
        else {
            for (int row = 0; row < rows.size(); row++) {
                ArrayList<Double> currentRow = rows.get(row);
                String rowLine = "|";
                for (int clmn = 0; clmn < columns.size(); clmn++) {
                    Double value = currentRow.get(clmn);
                    String text = String.format("%.2f", value);
                    int leftSpaces = width[clmn] - text.length();
                    rowLine = rowLine + " " + " ".repeat(leftSpaces) + text + " |";

                }
                System.out.println(rowLine);
            }
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
