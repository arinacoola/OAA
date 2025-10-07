public class Token {
    private String type,value;
    public Token(String type,String value){
        this.type = type;
        this.value = value;
    }
    public String getType(){
        return type;
    }
    public String getValue(){
        return value;
    }


    public String toString() {
       return "Token(" + type + ", " + value + ")";

    }
}
