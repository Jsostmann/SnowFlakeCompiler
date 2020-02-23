package snowflakeparser;

public class Token {
    
    
    public static final int CONSTANT    = 1;  
    public static final int KEYWORD     = 2;   
    public static final int PUNCTUATION = 3;   
    public static final int NAME        = 4;   
    
    private String      value = "";         
    public int         type;              
    private final int   lineNumber;         
    private final int   column;             
    
    public static final String[] KEYWORDS = {"PARM", "RETURN", "WHILE"}; 
    
  
    public Token(char symbol1, int line, int col) {
        value      = value + symbol1;
        lineNumber = line;
        column     = col;
        if (Character.isLetter(symbol1)) {
            type = NAME;
        } else {
            type = PUNCTUATION;
        }
    }
    

    public Token(int line, int col) {
        lineNumber = line;
        column     = col;
        type = CONSTANT;
    }
    
    
    public void add2Token(char nextSymbol) {
        value = value + nextSymbol;
        if (type == KEYWORD) {          
            type = NAME;
        } else if (type == NAME) {      
            for (String key : KEYWORDS) {
                if (value.equalsIgnoreCase(key)) {
                    type = KEYWORD;
                }
            }
        }
    }
    
   
    public boolean isConstant() {
        return type == CONSTANT;
    }
    
    
    public boolean isName() {
        return type == NAME;
    }
    
    
    public boolean isVarConst() {
        return type == NAME || type == CONSTANT;
    }
    
    
    public boolean isKeyWord() {
        return type==KEYWORD;
    }
    
   
    public boolean isPunc() {
        return type == PUNCTUATION;
    }
    
    
    public String getValue() {
        return value;
    }
    
    
    public int getLineNumber() {
        return lineNumber;
    }

    public int getColumn() {
        return column;
    }
    
    
    public int getPosition() {
        return 1000 * lineNumber + column;
    }
    
    @Override
    public String toString() {
        return  value + " from line "+lineNumber+" col "+column+" type "+type;
    }
}