import java.util.ArrayList;

public class Parser {
    Lexer lexer;
    Token currentToken;
    ArrayList<Table> tables = new ArrayList<>();

    public Parser(Lexer lexer,Token currentToken){
        this.lexer=lexer;
        this.currentToken=currentToken;
    }

    public void  getToken(){
        currentToken=lexer.newToken();
    }
    public void recognizeCmnd(Token currentToken){
        if(currentToken.getType()!=TokenType.KEYWORD){
            return;
        }
        else {
            if(currentToken.getValue().equalsIgnoreCase("CREATE")){
                parserCreate();
            }
            else if(currentToken.getValue().equalsIgnoreCase("INSERT")){
                parserInsert();
            }
            else if (currentToken.getValue().equalsIgnoreCase("SELECT")) {
                System.out.println("Unknown keyword");
            }
        }
    }


    public void parserCreate(){
        getToken();

        if(currentToken.getType()!=TokenType.IDENTIFIER){
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
        if(!currentToken.getValue().equals("(")){
            System.out.println("Expected '(' after table name");
            return;
        }
        getToken();

        ArrayList<String> columns = new ArrayList<>();
        ArrayList<Boolean> indexedValues =new ArrayList<Boolean>();

        while (!currentToken.getValue().equals(")")){
            if (currentToken.getType()!=TokenType.IDENTIFIER){
                System.out.println("Column name expected");
                return;
            }
            String columnName= currentToken.getValue();
            getToken();
            boolean indexed;
            if(currentToken.getValue().equalsIgnoreCase("INDEXED")){
               indexed = true;
                getToken();
            }
            else{
                indexed=false;
            }
            columns.add(columnName);
            indexedValues.add(indexed);

            if (currentToken.getValue().equals(",")){
                getToken();
                continue;
            }
            if (currentToken.getValue().equals(")")) {
                break;
            }
            if(!currentToken.getValue().equals(",")&&!currentToken.getValue().equals(")")){
                System.out.println("Expected ',' or ')' after column definition");
            }
            getToken();
        }
        getToken();
        if(currentToken.getValue().equals(";")){
            System.out.println("Table " + tableName + " has been created");
        }
        else {
            System.out.println("Expected ';' at the end of CREATE command");
        }

        Table newTable = new Table(tableName, columns, indexedValues);
        tables.add(newTable);
    }

    public void parserInsert() {
    }



}
