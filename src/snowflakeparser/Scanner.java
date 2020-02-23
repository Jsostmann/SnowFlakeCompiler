/* COMP360 Snowflake Scanner */
package snowflakeparser;
/**
 * Lexical scanner suitable for the Snowflake language.
 * @author Ken Williams at North Carolina A&T State University February 6, 2020
 */
public class Scanner {
    
    /* Input symbol group indexes */
    private static final int SYMLETTER = 0;      	// upper and lowercase letters
    private static final int SYMNUM    = 1;		// number
    private static final int SYMSPACE  = 2;      	// space or tab
    private static final int SYMHASH   = 3;      	// # hash mark
    private static final int SYMBANG   = 4;      	// ! exclamation point
    private static final int SYMEOL    = 5;      	// End Of Line
    private static final int SYMQUOTE  = 6;      	// ' quote character
    private static final int SYMPUNC   = 7;		// punctuation
    private static final int SYMOTHER  = 8;      	// none of the above 

    private static final int[][] STATETABLE = {
               /* state      0   1   2   3   4   5   6 */
	/* SYMLETTER    */ { 0,  2,  2,  3,  4,  4,  6},
	/* SYMNUM	*/ { 0, -1,  2,  3,  4,  4,  6},
	/* SYMSPACE	*/ { 0,  1,  1,  3,  4,  4,  6},
	/* SYMHASH	*/ { 0,  3,  3,  3,  4,  1,  6},
	/* SYMBANG	*/ { 0, -1, -1,  4,  5,  5,  6},
	/* SYMEOL	*/ { 1,  1,  1,  1,  4,  4, -1},
	/* SYMQUOTE	*/ { 0,  6,  6,  3,  4,  4,  1},
	/* SYMPUNC	*/ { 0,  1,  1,  3,  4,  4,  6},
	/* SYMOTHER	*/ { 0, -1, -1,  3,  4,  4,  6}
    };

    /* Action values */
    private static final int ACTNOTHING = 0;            // take no action
    private static final int ACTCREATE  = 1;            // create token with input symbol
    private static final int ACTADD     = 2;            // append character to symbol text
    private static final int ACTQUOTE   = 3;            // create empty string token
    
    private static final int[][] ACTIONTABLE = {
               /* state      0   1   2   3   4   5   6 */
	/* SYMLETTER    */ { 0,  1,  2,  0,  0,  0,  2},
	/* SYMNUM	*/ { 0,  0,  2,  0,  0,  0,  2},
	/* SYMSPACE	*/ { 0,  0,  0,  0,  0,  0,  2},
	/* SYMHASH	*/ { 0,  0,  0,  0,  0,  0,  2},
	/* SYMBANG	*/ { 0,  0,  0,  0,  0,  0,  2},
	/* SYMEOL	*/ { 0,  0,  0,  0,  0,  0,  0},
	/* SYMQUOTE	*/ { 0,  3,  3,  0,  0,  0,  0},
	/* SYMPUNC	*/ { 0,  1,  1,  0,  0,  0,  2},
	/* SYMOTHER	*/ { 0,  0,  0,  0,  0,  0,  2}
    };

    
    public static java.util.ArrayList<Token> lexicalScan(java.io.BufferedReader inFile)
                          throws java.text.ParseException, java.io.IOException {
        int     state = 1;                  // current state
        char    symbol;                     // input symbol
        int     intSymbol;                  // input symbol as an int
        int     action;                     // action to be taken
        int     inx;                        // input symbol index
        int     lineNumber = 1;             // current line number in source code
        int     column = 1;                 // current column in source code
        Token   tok = null;                 // newly created token
        java.util.ArrayList<Token> tokenList = new java.util.ArrayList<>();
        
        intSymbol = inFile.read();
        while (intSymbol != -1) {           //while not EOF
            symbol = (char)intSymbol;       // convert input to character
            if (symbol == '\n') {           // if end of line
                lineNumber++;
                column = 0;
            } else {
                column++;
            }
            if (symbol != '\r') {           // ignore carriage return characters
                /* Map input symbol to symbol group. */
                if (Character.isLetter(symbol)) {
                    inx = SYMLETTER;
                } else if (Character.isDigit(symbol)) {
                    inx = SYMNUM;
                } else if (symbol == ' ' || symbol == '\t') {	// space is a space or tab
                    inx = SYMSPACE;
                } else if (symbol == '#') {
                    inx = SYMHASH;
                } else if (symbol == '!') {
                    inx = SYMBANG;
                } else if (symbol == '\n') {
                    inx = SYMEOL;
                } else if (symbol == '\'') {
                    inx = SYMQUOTE;
                } else if ("=|{};".indexOf(symbol) != -1) {
                    inx = SYMPUNC;
                } else {
                    inx = SYMOTHER;
                }
                action = ACTIONTABLE[inx][state];
                state  = STATETABLE[inx][state];
                
                /* Check for syntax error. */
                if (state == -1) {                   // if invalid state
                    throw new java.text.ParseException("Invalid symbol:"+symbol, lineNumber*1000+column);
                } 
                /* Perform any action for this transition */
                switch(action) {
                    case ACTCREATE:         // create a token with the current symbol
                        tok = new Token(symbol, lineNumber, column);
                        tokenList.add(tok);
                        break;
                    case ACTADD:            // add symbol to token
                        tok.add2Token(symbol);
                        break;
                    case ACTQUOTE:          // create empty token for string
                        tok = new Token( lineNumber, column);
                        tokenList.add(tok);
                        break;
                }
            }
            intSymbol = inFile.read();      // read next token from input file
        }
        return tokenList; // return list of tokens
    }

}
