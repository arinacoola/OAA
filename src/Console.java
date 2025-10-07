import java.util.Scanner;

public class Console {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the relational model with support for data aggregation (numbers);\n" +
                "Enter “HELP” if you want to see a list of commands that can be entered, “EXIT” if you want to exit the program:");
        while (true) {
            System.out.print("Please enter the command: ");
            String ask = scanner.nextLine();
            if (ask.equals("HELP")) {
                System.out.println(
                        "Available commands:\n\n" +

                                "1. CREATE table_name (column_name [, ...]);\n" +
                                "   Example:\n" +
                                "       CREATE students (age, average_grade);\n\n" +

                                "2. CREATE table_name (column_name INDEXED [, ...]);\n" +
                                "   Example:\n" +
                                "       CREATE measurements (id INDEXED, height INDEXED, weight);\n\n" +

                                "3. INSERT [INTO] table_name (N [, ...]);\n" +
                                "   Example:\n" +
                                "       INSERT INTO measurements (1, 180, 75);\n" +
                                "       INSERT measurements (2, 175, 72);\n\n" +

                                "4. SELECT FROM table_name;\n" +
                                "   Example:\n" +
                                "       SELECT FROM measurements;\n\n" +

                                "5. SELECT FROM table_name WHERE column = value;\n" +
                                "   Example:\n" +
                                "       SELECT FROM measurements WHERE id = 1;\n\n" +

                                "6. SELECT agg_function(column) [, ...]\n" +
                                "       FROM table_name\n" +
                                "       [WHERE condition]\n" +
                                "       [GROUP_BY column_name [, ...]];\n" +
                                "   Supported functions:\n" +
                                "       COUNT(column) - number of values in the group\n" +
                                "       MAX(column)   - maximum value of column\n" +
                                "       AVG(column)   - average value of column\n"
                );
            }
            if (ask.equals("EXIT")) {
                break;
            } else if (!ask.isEmpty()) {
                System.out.println("Unknown command. Type HELP to see available commands.");
            }


        }
    }
}


