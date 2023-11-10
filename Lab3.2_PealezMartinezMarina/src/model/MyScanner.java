package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


/*
 * Class to perform lexical analysis on a source code file. 
 */

public class MyScanner {
	private String fileName;
	
	private List<String> tokenList ;
	private List<String> separatorList;
	private List<String> specialRelational;
	private List<String> regularRelational;
	
	private int currentLine;
	private int capacity;
	private MySymbolTable ST;
	private List<Pair<String, Integer>> PIF;
	
	private boolean isStringLexicallyCorrect;
	private boolean isCharLexicallyCorrect;
	private String stringConstant = "";
	private String charConstant = "";


/*
 * Constructor: 
 * - Initializes various data structures and variables, including lists for tokens, separators, and relational operators.
 * - Reads tokens and separators/operators from the "input/token.in" file.
 */
	public MyScanner(String fileName) {
		this.fileName=fileName;
		this.currentLine=0;
		this.capacity=97;
		
		this.isStringLexicallyCorrect = true;
		this.isCharLexicallyCorrect = true;
		
		this.ST = new MySymbolTable(capacity);
		this.PIF = new ArrayList<>();
		this.tokenList = new ArrayList<String>();
		this.separatorList = new ArrayList<String>();
		this.specialRelational = new ArrayList<String>();
		this.regularRelational  = new ArrayList<String>();		
		
		this.specialRelational.add(">=");
	    this.specialRelational.add("<=");
	    this.specialRelational.add("==");
	    this.regularRelational.add(">");
	    this.regularRelational.add("<");
		// Read tokens:
		this.readTokens();
		this.readSeparatorsOperators();
	}
	
	// HELPER METHODS:

	/*
	 * readTokens() and readSeparatorsOperators(): 
	 * Read tokens and separators/operators from the "input/token.in" file, filling the corresponding lists.
	 */
	private void readTokens() {
	       try {
	          File myObj = new File("input/token.in");
	          Scanner myReader = new Scanner(myObj);
	          while (myReader.hasNextLine()) {
	             String data = myReader.nextLine();
	             tokenList.add(data);
	          }
	          myReader.close();
	       } catch (FileNotFoundException e) {
	          System.out.println("An error occurred.");
	          e.printStackTrace();
	       }
	    }

	private void readSeparatorsOperators() {
	       try {
	          File myObj = new File("input/token.in");
	          Scanner myReader = new Scanner(myObj);
	          for (int i = 0; i < 24; i++){
	             String data = myReader.nextLine();
	             separatorList.add(data);
	          }
	          myReader.close();
	       } catch (FileNotFoundException e) {
	          System.out.println("An error occurred.");
	          e.printStackTrace();
	       }
	    }


		/*
		 * isConstant(), isIdentifier(), isStringConstant(), isCharConstant(): 
		 * - Check if a given token is a constant, identifier, string constant or char constant.
		 * "\\-?[1-9]+[0-9]*|0"   :
		 * 		\\-?: Matches an optional negative sign.
		 * 		[1-9]+: Matches one or more digits from 1 to 9 (ensuring that the number is not a leading zero).
		 * 		[0-9]*: Matches zero or more additional digits.
		 * 		|: Represents the logical OR in a regular expression.
		 * 		0: Matches the digit 0.
		 * "\"[a-zA-Z0-9 _]+\"    :
		 * 		\": Matches the opening double quote.
		 * 		[a-zA-Z0-9 _]+: Matches one or more alphanumeric characters, underscores, or spaces.
		 * 		\": Matches the closing double quote.
		 */
	private Boolean isConstant(String token) {
        return token.matches("\\-?[1-9]+[0-9]*|0")
                || token.matches("\"[a-zA-Z0-9 _]+\"");
                /*|| token.equals("True")
                || token.equals("False");*/
    }

	/*
	 * Takes a string (token) and returns a boolean
	 * (^[a-zA-Z][a-zA-Z0-9 _]*)     :
	 * 		^: Shows the start of the line.
	 * 		[a-zA-Z]: Matches the first character, which must be a letter (either lowercase or uppercase).
	 * 		[a-zA-Z0-9 _]*: Matches zero or more additional characters, which can be letters (lowercase or uppercase), digits, underscores, or spaces.
	 */
    private Boolean isIdentifier(String token){
        return token.matches("(^[a-zA-Z][a-zA-Z0-9 _]*)");
    }

	/*
	 * Checks if the first character of the token is a double quote (") and if the second-to-last character -penúltimo- (at index token.length() - 2) is also a double quote. 
	 * This is done to verify that the token is enclosed in double quotes.
	 * If the token passes the check in step 1, the method then extracts the substring between the first and second-to-last characters (excluding the enclosing double quotes). 
	 * This substring is stored in the variable withoutQuote.
	 * False if: the initial check fails or the length of the substring is not greater than 1.
	 */
    private Boolean isStringConstant(String token) {
        if (token.charAt(0) == '"' && token.charAt(token.length() - 2) == '"') {
            String withoutQuote = token.substring(1, token.length() - 2);
            return withoutQuote.length() > 1;
        } else {
            return false; //if the token does not match the pattern
        }
    }


	/*(
	 * Makes the exact same check as the previous one but instead of "", '' (simple ones).
	 */
    private Boolean isCharConstant(String token) {
        if (String.valueOf(token.charAt(0)).equals("'") && String.valueOf(token.charAt(token.length() - 2)).equals("'")) {
            String withoutQuote = token.substring(1, token.length() - 2);
            return withoutQuote.length() <= 1;
        } else {
            return false;
        }
    }

	/*
	 * Check if a given token is a reserved operator (p.e. +, -, =, <) or separator (p.e. [] ; {}).
	 * tokenList contains reserved opertatos and separators. So if it is equal it returns true, false in other case.
	 */
    private Boolean isReservedOperatorSeparator(String myToken) {
        for (String token : this.tokenList) {
            if (myToken.equals(token)) {
                return true;
            }
        }
        return false;
    }


	// LEXICAL ANALYSIS METHODS:

	/*
	 * Main method. 
	 * First analyses, then classifies tokens into identifiers, constants, and operators, and writes the results to "output/pif.out".
	 * 
	 */
    public void tokensClasification() throws FileNotFoundException {
    	PrintWriter pw = new PrintWriter("output/pif.out"); // For writing out the file 
        pw.printf("%-20s %s\n", "Token", "ST_Pos");
        
        
        Integer lastLine = 0;
		// iterates through each pair (Pair<String, Integer> pair) in the Program Internal Form (this.PIF).
        for (Pair<String, Integer> pair: this.PIF) {
			// If the token is a reserved operator or separator, it writes the token and a special value (-1) to the output file.
            if (isReservedOperatorSeparator(pair.getKey())) {
                pw.printf("%-20s %d\n", pair.getKey(), -1);
			// If the token is an identifier, it inserts the identifier into the symbol table (ST), finds its position, and writes "IDENTIFIER" and the position to the output file.
            }else if (isIdentifier(pair.getKey())) {
            	ST.insert(pair.getKey());
                int position = ST.find(pair.getKey());
                pw.printf("%-20s %d\n", "IDENTIFIER", position);
			// If the token is a constant (numeric, string, or character), it inserts the constant into the symbol table, finds its position, and writes "CONSTANT" and the position to the output file.
            }else if(isConstant(pair.getKey())|| isStringConstant(pair.getKey()) || isCharConstant(pair.getKey())) {
            	ST.insert(pair.getKey());
                int position = ST.find(pair.getKey());
                pw.printf("%-20s %d\n", "CONSTANT", position);
            }else { 
                System.out.println("LEXICAL ERROR " + pair.getKey() + " AT LINE " + (pair.getValue()));
            }
            lastLine = pair.getValue(); // Tracking of the last line for error reporting
        }


		// After processing all tokens, it checks whether there are unclosed double quotes "" (!isStringLexicallyCorrect) or unclosed single quotes '' (!isCharLexicallyCorrect). If so, it prints lexical error messages to the console.
        if (!isStringLexicallyCorrect) {
            System.out.println("LEXICAL ERROR: DOUBLE QUOTES NOT CLOSED AT LINE " + lastLine);
        }
        if (!isCharLexicallyCorrect) {
            System.out.println("LEXICAL ERROR: SINGLE QUOTES NOT CLOSED AT LINE " + lastLine);
        }
        pw.close();
    }
    
	/*
	 *  Writes the symbol table to "output/st.out".
	 * It iterates through the symbol table array and writes non-null entries to the output file. For each non-null entry, it prints the symbol and its position in the symbol table.
	 */
    public void writeSymbolTable() throws FileNotFoundException {
    	PrintWriter pw = new PrintWriter("output/st.out");
    	pw.printf("%-20s %s\n", "Symbol Table as:", "Hash Table");
        pw.printf("%-20s %s\n", "Symbol", "ST Position");
        String[] symTable = ST.getSymbolTable();

        for(int i = 0; i < capacity; i++) {
            if (symTable[i] != null) {
                pw.printf("%-20s %s\n", symTable[i], i);
            }
        }
        pw.close();
    }
    
	// LEXICAL SCANNER METHOD:

	/*
	 * Reads the source code file, passes the code to tokens so that we can use readTokens(), and classifies the tokens.
	 * It creates a File (myObj), it initializes a Scanner (myReader), it enters a loop that iterates through each line in the file.
	 * For each line, it creates another Scanner (data) to tokenize the line into words.
	 * The processes each word like this:
	 * It checks if the word contains any separator from the separatorList.
	 * 		If a separator is found, it calls the splitWordWithSeparator method to further process the word.
	 * 		If no separator is found, it checks and accumulates the word for string and character constants based on the state of isStringLexicallyCorrect and isCharLexicallyCorrect.
	 * 		If no separator is found and both string and character lexically correct flags are true, it adds the word to the Program Internal Form (PIF) with its line number.
	 */
    public void scanner() {
    	File myObj = new File(this.fileName);
    	try {
			Scanner myReader = new Scanner(myObj); //  creates a file.
			while(myReader.hasNextLine()) {
				Scanner data = new Scanner(myReader.nextLine());
				currentLine++;
				while(data.hasNext()) {
					String word = data.next();
					boolean hasSeparator = false;
					for(String separator : separatorList) {
						if(word.contains(separator)) {
							hasSeparator = true;
							this.splitWordWithSeparator(word,separator,currentLine);
							break;
						}
					}
					if (!hasSeparator && !isStringLexicallyCorrect) {
	                    stringConstant += word + " ";
	                }
	                if (!hasSeparator && !isCharLexicallyCorrect) {
	                    charConstant += word + " ";
	                }
	                if (!hasSeparator && isStringLexicallyCorrect && isCharLexicallyCorrect) {
	                     PIF.add(new Pair<String, Integer>(word, currentLine));
	                }
					
					
				}
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    	
    }
    

	/*
	 * This method checks for different cases where a word contains a separator (f.e. "" for strings, '' for chars and special relational separators).
	 * It handles special cases for string and character constants, updating the lexically correct flags accordingly.
	 * 		For relational separators like '(' and ')', it splits the word and adds the corresponding parts to the Program Internal Form (PIF).
	 * 		For '[' and ']', it splits the word and handles the cases where the content inside '[' and ']' is present.
	 * 		For special characters like '+', and '.', it adds them to the PIF.
	 * 		If none of the special cases apply, it splits the word using the separator and adds the parts to the PIF.
	 */
    private void splitWordWithSeparator(String word, String separator, Integer line) {
    	String[] splitedWord;
    	
    	boolean specialCase = false;
    	boolean containsRelational = false;
    	char doubleQuotes = '"';
    	String stringDoubleQuotes = String.valueOf(doubleQuotes);
    	
    	// If contains ""
    	if(word.contains(stringDoubleQuotes) && !isStringLexicallyCorrect) {
    		specialCase=true;
    		this.isStringLexicallyCorrect = true;
    		// STRING ENDS IN ;
    		if(word.charAt(word.length()-1) == ';') {
    			String newWord = word.substring(0,word.length()-1);
    			stringConstant += newWord + " ";
    			PIF.add(new Pair<String,Integer>(stringConstant, currentLine));
    			PIF.add(new Pair<String, Integer>(";",currentLine));
    			stringConstant = "";
    			return;
    		}else {
    			stringConstant += word + " ";
                PIF.add(new Pair<String, Integer>(stringConstant, currentLine));
                stringConstant = "";
                return;
    		}
    	}
    	
    	// If the word is inside of a string that is not already processed correctly.
    	if(!isStringLexicallyCorrect) {
    		specialCase = true;
    		//We add the word to the String chain
    		stringConstant += word + " ";
    	}
    	
    	// String starts with ""
    	if(separator.charAt(0) == '"' && isStringLexicallyCorrect) {
    		specialCase = true;
    		// Entramos en un String no correcto
    		isStringLexicallyCorrect = false;
    		stringConstant += word + " ";
    		return;
    	}
    	
    	// Contains ''
    	if (word.contains("'") && !isCharLexicallyCorrect) {
    		specialCase = true;
    		this.isCharLexicallyCorrect = true;
    		// Ends in ;
    		if(word.charAt(word.length()-1) == ';') {
    			String newWord = word.substring(0, word.length() - 1);
                charConstant += newWord + " ";
                PIF.add(new Pair<String, Integer>(charConstant, currentLine));
                PIF.add(new Pair<String, Integer>(";", currentLine));
                charConstant = "";
                return;
    		}else {
    			charConstant += word + " ";
                PIF.add(new Pair<String, Integer>(charConstant, currentLine));
                charConstant = "";
                return;
    		}
    	}
    	
    	if(!isCharLexicallyCorrect) {
    		specialCase=true;
    		charConstant += word + " ";
    		return;
    	}
    	
    	if (separator.equals("'") && isCharLexicallyCorrect) {
            specialCase = true;
            isCharLexicallyCorrect = false;
            charConstant += word + " ";
            return;
        }
    	
    	if(separator.equals("(")){
    		specialCase = true;
    		String[] Lhs;
    		String[] Rhs;
    		
    		for(String specialSeparator : this.specialRelational) {
    			if(word.contains(specialSeparator)) {
    				containsRelational = true;
    				splitedWord = word.split(specialSeparator);
    				Rhs = splitedWord[0].split("\\(");
    				Lhs = splitedWord[1].split("\\)");
    				PIF.add(new Pair<String, Integer>("(", currentLine));
                    PIF.add(new Pair<String, Integer>(Rhs[1], currentLine));
                    PIF.add(new Pair<String, Integer>(specialSeparator, currentLine));
                    PIF.add(new Pair<String, Integer>(Lhs[0], currentLine));
                    PIF.add(new Pair<String, Integer>(")", currentLine));  				
    			}
    		}
    		for (String regularSeparator : this.regularRelational) {
                if (word.contains(regularSeparator) && !containsRelational) {
                    containsRelational = true;
                    splitedWord = word.split(regularSeparator);
                    Rhs = splitedWord[0].split("\\(");
    				Lhs = splitedWord[1].split("\\)");
                    PIF.add(new Pair<String, Integer>("(", currentLine));
                    PIF.add(new Pair<String, Integer>(Rhs[1], currentLine));
                    PIF.add(new Pair<String, Integer>(regularSeparator, currentLine));
                    PIF.add(new Pair<String, Integer>(Lhs[0], currentLine));
                    PIF.add(new Pair<String, Integer>(")", currentLine));
                }
            }
    	}
    	if (separator.equals(")")) {
            specialCase = true;
            splitedWord = word.split("\\)");
            PIF.add(new Pair<String, Integer>(splitedWord[0], currentLine));
            PIF.add(new Pair<String, Integer>(separator, currentLine));
        }
    	
    	if (separator.equals("[")) {
            specialCase = true;
            splitedWord = word.split("\\[");
            PIF.add(new Pair<String, Integer>(splitedWord[0], currentLine));
            PIF.add(new Pair<String, Integer>(separator, currentLine));
            String[] LHS = splitedWord[1].split("\\]");
            if (LHS.length == 1) {
                PIF.add(new Pair<String, Integer>(LHS[0], currentLine));
                PIF.add(new Pair<String, Integer>("]", currentLine));
            } else if (LHS.length == 2) {
                PIF.add(new Pair<String, Integer>(LHS[0], currentLine));
                PIF.add(new Pair<String, Integer>("]", currentLine));
                PIF.add(new Pair<String, Integer>(LHS[1], currentLine));
            }
        }
    	
    	if (separator.equals("+")) {
            PIF.add(new Pair<String, Integer>(separator, currentLine));
            specialCase = true;
        }

        if (separator.equals(".")) {
        	splitedWord = word.split("\\.");
            PIF.add(new Pair<String, Integer>(splitedWord[0], currentLine));
            PIF.add(new Pair<String, Integer>(separator, currentLine));
            specialCase = true;
        }

        if (!specialCase) {
        	splitedWord = word.split(separator);
            if (splitedWord.length == 0) {
                PIF.add(new Pair<String, Integer>(separator, currentLine));
            }

            if (splitedWord.length == 1) {
                PIF.add(new Pair<String, Integer>(splitedWord[0], currentLine));
                PIF.add(new Pair<String, Integer>(separator, currentLine));
            }

            if (splitedWord.length == 2) {
                if (!splitedWord[0].equals("")) {
                    PIF.add(new Pair<String, Integer>(splitedWord[0], currentLine));
                }
                PIF.add(new Pair<String, Integer>(separator, currentLine));
                PIF.add(new Pair<String, Integer>(splitedWord[1], currentLine));
            }
        }
    }
}
