public class Lexer {
    private String allCommand;
    private int position;
    private char currentSymbol;
    public Lexer(String allCommand){
        this.allCommand=allCommand;
        this.position=0;
        this.currentSymbol = allCommand.charAt(0);
    }
}
