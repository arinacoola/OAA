import java.util.ArrayList;
import java.util.Scanner;

public class Console {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the relational model with support for data aggregation (numbers);\n" +
                "Enter “HELP” if you want to see a list of commands that can be entered, “EXIT” if you want to exit the program:");
        ArrayList<Table> tables = new ArrayList<>();
        while (true) {
            System.out.print("Please enter the command: ");
            StringBuilder commandBuilder = new StringBuilder();
            while (true) {
                String line = scanner.nextLine().trim();
                commandBuilder.append(line).append(" ");
                if (line.contains(";")) {
                    break;
                }
            }
            String ask = commandBuilder.toString().trim();
            int semicolonInd = ask.indexOf(';');
            if (semicolonInd != -1) {
                ask = ask.substring(0, semicolonInd + 1).trim();
            }
            if (ask.isBlank()) {
                continue;
            }

            if (ask.equalsIgnoreCase("HELP;")) {
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
                continue;
            }
            if (ask.equalsIgnoreCase("EXIT;")) {
                break;
            }
            System.out.println("Your command:  " + ask);
            Lexer lexer = new Lexer(ask);
            Token token = lexer.newToken();
            Parser parser = new Parser(lexer, token, tables);
            parser.recognizeCmnd(token);

        }
    }
}



