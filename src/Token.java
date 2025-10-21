enum TokenType {
    KEYWORD,
    IDENTIFIER,
    NUMBER,
    LPAREN,
    RPAREN,
    COMMA,
    SEMICOLON,
    EQUALS,
    EOF,
    UNKNOWN
}
public class Token {
    private TokenType type;
    private String value;
    public Token(TokenType type, String value){
        this.type = type;
        this.value = value;
    }

    public TokenType getType(){
        return type;
    }
    public String getValue(){
        return value;
    }

    public String toString() {
       return "Token(" + type + ", " + value + ")";

    }

}
