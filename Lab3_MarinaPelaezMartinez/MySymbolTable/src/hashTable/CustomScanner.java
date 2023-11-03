package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CustomScanner {
	private String fileName;
	
	private List<String> tokenList ;
	private List<String> separatorList;
	private List<String> specialRelational;
	private List<String> regularRelational;
	
	private int currentLine;
	private int capacity;
	private CustomHashTable ST;
	private List<Pair<String, Integer>> PIF;
	
	private boolean isStringLexicallyCorrect;
	private boolean isCharLexicallyCorrect;
	private String stringConstant = "";
	private String charConstant = "";

	public CustomScanner(String fileName) {
		this.fileName=fileName;
		this.currentLine=0;
		this.capacity=97;
		
		this.isStringLexicallyCorrect = true;
		this.isCharLexicallyCorrect = true;
		
		this.ST = new CustomHashTable(capacity);
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
		
		this.readTokens();
		this.readSeparatorsOperators();
	}
	
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

	private Boolean isConstant(String token) {
        return token.matches("\\-?[1-9]+[0-9]*|0")
                || token.matches("\"[a-zA-Z0-9 _]+\"");
                /*|| token.equals("True")
                || token.equals("False");*/
    }

    private Boolean isIdentifier(String token){
        return token.matches("(^[a-zA-Z][a-zA-Z0-9 _]*)");
    }

    private Boolean isStringConstant(String token) {
        if (token.charAt(0) == '"' && token.charAt(token.length() - 2) == '"') {
            String withoutQuote = token.substring(1, token.length() - 2);
            return withoutQuote.length() > 1;
        } else {
            return false;
        }
    }

    private Boolean isCharConstant(String token) {
        if (String.valueOf(token.charAt(0)).equals("'") && String.valueOf(token.charAt(token.length() - 2)).equals("'")) {
            String withoutQuote = token.substring(1, token.length() - 2);
            return withoutQuote.length() <= 1;
        } else {
            return false;
        }
    }

    private Boolean isReservedOperatorSeparator(String myToken) {
        for (String token : this.tokenList) {
            if (myToken.equals(token)) {
                return true;
            }
        }
        return false;
    }

    public void classifyTokens() throws FileNotFoundException {
    	PrintWriter pw = new PrintWriter("output/pif.out");
        pw.printf("%-20s %s\n", "Token", "ST_Pos");
        
        
        Integer lastLine = 0;
        for (Pair<String, Integer> pair: this.PIF) {
            if (isReservedOperatorSeparator(pair.getKey())) {
                pw.printf("%-20s %d\n", pair.getKey(), -1);
            }else if (isIdentifier(pair.getKey())) {
            	ST.insert(pair.getKey());
                int position = ST.find(pair.getKey());
                pw.printf("%-20s %d\n", "IDENTIFIER", position);
            }else if(isConstant(pair.getKey())|| isStringConstant(pair.getKey()) || isCharConstant(pair.getKey())) {
            	ST.insert(pair.getKey());
                int position = ST.find(pair.getKey());
                pw.printf("%-20s %d\n", "CONSTANT", position);
            }else {
                System.out.println("LEXICAL ERROR " + pair.getKey() + " AT LINE " + (pair.getValue()));
            }
            lastLine = pair.getValue();
        }
        if (!isStringLexicallyCorrect) {
            System.out.println("LEXICAL ERROR: DOUBLE QUOTES NOT CLOSED AT LINE " + lastLine);
        }
        if (!isCharLexicallyCorrect) {
            System.out.println("LEXICAL ERROR: SINGLE QUOTES NOT CLOSED AT LINE " + lastLine);
        }
        pw.close();
    }
    
    public void writeToSymbolTable() throws FileNotFoundException {
    	PrintWriter pw = new PrintWriter("output/st.out");
    	pw.printf("%-20s %s\n", "Symbol Table as:", "Hash Table");
        pw.printf("%-20s %s\n", "Symbol", "ST Position");
        String[] symTable = ST.getSymTable();

        for(int i = 0; i < capacity; i++) {
            if (symTable[i] != null) {
                pw.printf("%-20s %s\n", symTable[i], i);
            }
        }
        pw.close();
    }
    
    public void scan() {
    	File myObj = new File(this.fileName);
    	try {
			Scanner myReader = new Scanner(myObj);
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
    
    private void splitWordWithSeparator(String word, String separator, Integer line) {
    	String[] splitedWord;
    	
    	boolean specialCase = false;
    	boolean containsRelational = false;
    	char doubleQuotes = '"';
    	String stringDoubleQuotes = String.valueOf(doubleQuotes);
    	
    	// Si contiene doble comillas
    	if(word.contains(stringDoubleQuotes) && !isStringLexicallyCorrect) {
    		specialCase=true;
    		this.isStringLexicallyCorrect = true;
    		// Si el string acaba en ;
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
    	
    	// Si la palabra está dentro en un String que aun no es correcto
    	if(!isStringLexicallyCorrect) {
    		specialCase = true;
    		// Añadimos la palabra a la cadena String
    		stringConstant += word + " ";
    	}
    	
    	// El string empieza por doble comillas
    	if(separator.charAt(0) == '"' && isStringLexicallyCorrect) {
    		specialCase = true;
    		// Entramos en un String no correcto
    		isStringLexicallyCorrect = false;
    		stringConstant += word + " ";
    		return;
    	}
    	
    	//Si contiene comillas simples
    	if (word.contains("'") && !isCharLexicallyCorrect) {
    		specialCase = true;
    		this.isCharLexicallyCorrect = true;
    		// Si el string acaba en ;
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
