public class Lexer {
    private String allCommand;
    private int position;
    private char currentSymbol;

    public Lexer(String allCommand) {
        this.allCommand = allCommand;
        this.position = 0;
        this.currentSymbol = allCommand.charAt(0);
    }

    public void Movement() {
        this.position++;
        if (this.position >= this.allCommand.length()) {
            this.currentSymbol = '\0';
        } else {
            this.currentSymbol = allCommand.charAt(position);
        }
    }

    public void skipSpace() {
        while (currentSymbol != '\0' && Character.isWhitespace(currentSymbol)) {
            Movement();
        }
    }


    public Token newToken() {
        skipSpace();
        if (currentSymbol == '\0') {
            return new Token(TokenType.EOF, "");
        }
        if (Character.isLetter(currentSymbol)) {
            StringBuilder word1 = new StringBuilder();
            while (Character.isLetter(currentSymbol) || Character.isDigit(currentSymbol) || currentSymbol == '_') {
                word1.append(currentSymbol);
                Movement();
            }
            String newWord = word1.toString();
            if (isKeyword(newWord)) {
                return new Token(TokenType.KEYWORD, newWord.toUpperCase());
            } else {
                return new Token(TokenType.IDENTIFIER, newWord);
            }
        }
        if (Character.isDigit(currentSymbol) || currentSymbol == '-') {
            StringBuilder number = new StringBuilder();
            if (currentSymbol == '-') {
                number.append('-');
                Movement();
            }
            if (!Character.isDigit(currentSymbol)) {
                return new Token(TokenType.UNKNOWN, number.toString());
            }
            while (Character.isDigit(currentSymbol)) {
                number.append(currentSymbol);
                Movement();
            }
            return new Token(TokenType.NUMBER, number.toString());
        }
        if (currentSymbol == '(') {
            Movement();
            return new Token(TokenType.LPAREN, "(");
        }
        if (currentSymbol == ')') {
            Movement();
            return new Token(TokenType.RPAREN, ")");
        }
        if (currentSymbol == ',') {
            Movement();
            return new Token(TokenType.COMMA, ",");
        }
        if (currentSymbol == ';') {
            Movement();
            return new Token(TokenType.SEMICOLON, ";");
        }
        if (currentSymbol == '=') {
            Movement();
            return new Token(TokenType.EQUALS, "=");
        }

        char unknown = currentSymbol;
        Movement();
        return new Token(TokenType.UNKNOWN, Character.toString(unknown));
    }
    private boolean isKeyword(String word1) {
        String[] keyword = {"CREATE", "INSERT", "INTO", "SELECT", "FROM", "WHERE", "GROUP_BY", "COUNT", "MAX", "AVG", "INDEXED"};
        for (String key : keyword) {
            if (key.equalsIgnoreCase(word1)) {
                return true;
            }
        }
        return false;
    }

}
