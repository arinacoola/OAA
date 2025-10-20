public class Parser {
    Lexer lexer;
    Token currentToken;

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
                parserCreate();
            }
            else if (currentToken.getValue().equalsIgnoreCase("SELECT")) {
                System.out.println("Unknown keyword");
            }
        }
    }
    public void parserCreate(){

    }



}
