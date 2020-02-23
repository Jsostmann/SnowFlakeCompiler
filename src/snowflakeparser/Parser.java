package snowflakeparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.text.ParseException;

public class Parser {
    
    public static ArrayList<Token> tokens;
    public static int currentToken;
    public static int size;
   
    
    public static void compile() {
        
         try {
           
            BufferedReader inFile = new BufferedReader(new FileReader(new File("snowflake.txt")));
            currentToken = 0;
            tokens = Scanner.lexicalScan(inFile);
            size = tokens.size();
            
            for (Token thing : tokens) {
                System.out.println(thing.getValue() + thing.type);
           }
            
            System.out.println("");
            
            System.out.println("------- Starting Parse Phase ---------");
            
            boolean completeTruth = Parser.parse();
            
            if (completeTruth) {
                System.out.println("The Snowflake program scans and parses correctly");
            } else {
                System.out.println("?? Error ??");
            }
            
        } catch(IOException | ParseException e) {
            System.out.println("* Source file I/O error *"+ e.getMessage());
            e.printStackTrace();
        }
         
    }
    
    public static boolean parse() {
        return SF();
    }
    
    public static boolean SF() {
        
        if(!parmstmnt()) {
            return false;
        }
        
        if(!code()) {
            return false;
        }
        
        return ret();
    }
    
    public static boolean parmstmnt() {
        
        if(!tokens.get(currentToken).isKeyWord()) {
            return false;
        }
        
        currentToken++;
        
        if(!varlist()) {
            return false;
        }
        
        
        if(!tokens.get(currentToken).isPunc()) {
            return false;
        }
        
        currentToken++;
        
        return true;
    }
    
    public static boolean code() {
        
        if(!line()) {
            return false;
        }
        
        code();
        
        return true;
    }
    
    public static boolean ret() {
        
       if(!tokens.get(currentToken).isKeyWord()) {
           return false;
       } 
       
       currentToken++;
       
       if(!tokens.get(currentToken).isVarConst()) {
           return false;
       }
       
       currentToken++;
       
       return tokens.get(currentToken).isPunc();
    }
    
    public static boolean varlist() {
        
        if(!tokens.get(currentToken).isName()) {
            return false;
        }
        
        currentToken++;
        
        varlist();
        
        return true;
    }
    
    public static boolean varclist() {
       
        if(!tokens.get(currentToken).isVarConst()) {
            return false;
        }
        
        currentToken++;
        
        varclist();
        
        return true;
        
        
    }
    
    public static boolean pattern() {
        
      if(!tokens.get(currentToken).isVarConst()) {
          return false;
      }
      
      currentToken++;
      
      if(tokens.get(currentToken).getValue().equalsIgnoreCase("|")) { 
          currentToken++;
          return pattern();
      }
      
      return true;
      
    }
    
    public static boolean line() {
        if(assign()) {
          return true;
        }
        return loop();
        
    }
    
    public static boolean assign() {
        
      if(tokens.get(currentToken).isVarConst()) {
          currentToken++;
          if(tokens.get(currentToken).isPunc()) {
              currentToken++;
              if(varclist()) {
                  if(tokens.get(currentToken).isPunc()) {
                      currentToken++;
                      return true;
                  }
              }
          } else if(pattern()) {
              if(tokens.get(currentToken).isPunc()) {
                  currentToken++;
                  if(varclist()) {
                      if(tokens.get(currentToken).isPunc()) {
                          currentToken++;
                          return true;
                      }
                  }
              }
          }
      } 
      
      return false;
    }   
    
    public static boolean loop() {
        
        if(!tokens.get(currentToken).getValue().equalsIgnoreCase("while")) { 
            return false;
        }
        currentToken++;
        
        if(!tokens.get(currentToken).isVarConst()) {
            return false;
        }
        
        currentToken++;
        
        if(!pattern()) {
            return false;
        }
        
        
        if(!tokens.get(currentToken).getValue().equalsIgnoreCase("{")) {  
            return false;
        }
        
        currentToken++;
        
        if(!code()) {
            return false;
        }
        
        if(!tokens.get(currentToken).getValue().equalsIgnoreCase("}")) {  
            return false;
        }
        
        currentToken++;
        
        return true;
    }
    
    
}