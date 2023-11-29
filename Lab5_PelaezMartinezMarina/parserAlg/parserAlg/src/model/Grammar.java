package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Grammar {

    private List<String> setOfNonTerminals;
    private Set<String> setOfTerminals;
    private List<Production> setOfProductions;
    private String startingSymbol;
    private String fileName;

    public Grammar(String fileName) {
        this.fileName = fileName;
        this.setOfNonTerminals = new LinkedList<>();
        this.setOfTerminals = new HashSet<>();
        this.setOfProductions = new LinkedList<>();
        this.startingSymbol = "";
    }

    public void readFromFile() throws FileNotFoundException {
        File file = new File(this.fileName);
        Scanner scanner = new Scanner(file);

        // Non-terminals
        scanner.nextLine();
        String setOfNonTerminals = scanner.nextLine();
        this.setOfNonTerminals = Arrays.asList(setOfNonTerminals.split(","));

        // Terminals
        scanner.nextLine();
        String setOfTerminals = scanner.nextLine();
        this.setOfTerminals.addAll(Arrays.asList(setOfTerminals.split(",")));

        // Productions
        scanner.nextLine();
        String production = "";

        while (true) {
            production = scanner.nextLine();
            if (production.equals("STARTING SYMBOL")) {
                break;
            }

            List<String> productions = Arrays.asList(production.split(" -> "));
            String[] states = productions.get(1).split(" \\| ");

            List<List<String>> LLS = new ArrayList<>();
            for (String state: states) {
                List<String> splitted = Arrays.asList(state.split(" "));
                LLS.add(splitted);
            }

            Production model = new Production(productions.get(0), LLS);

            this.setOfProductions.add(model);
        }

        // Starting symbol
        this.startingSymbol = scanner.nextLine();

        scanner.close();
    }

    public List<String> getSetOfNonTerminals() {
        return setOfNonTerminals;
    }

    public Set<String> getSetOfTerminals() {
        return setOfTerminals;
    }

    public List<Production> getSetOfProductions() {
        return setOfProductions;
    }

    public String getStartingSymbol() {
        return startingSymbol;
    }

    public List<Production> productionForNonTerminal(String nonTerminal) {
        List<Production> productionsForNonTerminal = new LinkedList<>();
        for (Production production : this.setOfProductions) {
            if (production.getStart().equals(nonTerminal)) {
                productionsForNonTerminal.add(production);
            }
        }
        return productionsForNonTerminal;
    }
    
    public boolean isContextFree() {
    	/*
    	 * A grammar is context-free if left-hand sides of
    	 * all productions contain exactly one non-terminal symbol.
    	 * By definition, if one exists, then the language is context-free.
    	 */
    	for(Production production: setOfProductions) {
    		if(production.getStart().trim().length() != 1) {
    			System.out.println("This grammar is NOT context free.");
    			return false;
    		}
    	}
    	System.out.println("This grammar IS context free.");
    	return true;
    }
}
