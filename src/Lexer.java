public class Lexer {
    private String allCommand;
    private int position;
    private char currentSymbol;
    public Lexer(String allCommand){
        this.allCommand=allCommand;
        this.position=0;
        this.currentSymbol = allCommand.charAt(0);
    }

    public void Movement(){
        this.position++;
        if(this.position>=this.allCommand.length()){
            this.currentSymbol = '\0';
        }
        else{
            this.currentSymbol=allCommand.charAt(position);
        }
    }

    public void skipSpace(){
        while (currentSymbol!='\0' && Character.isWhitespace(currentSymbol)){;
            Movement();
        }
    }
}
