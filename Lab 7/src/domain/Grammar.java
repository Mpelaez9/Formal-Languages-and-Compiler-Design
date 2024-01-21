package domain;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/*
 * Marina Pelaez Martinez
 * 2024
 * Class Grammar -->  used for reading and representing a context-free grammar from a file.
 */

public class Grammar {

    private List<String> setOfNonTerminals; // List of non-terminals.
    private Set<String> setOfTerminals; // A set of terminals in the grammar.
    private List<Production> setOfProductions; // A list of production rules for the grammar.
    private String startingSymbol; // The starting symbol of the grammar.
    private String fileName; // The name of the file from which the grammar will be read.

    /*
     * Constructor -->  takes a filename as an argument and initializes the instance variables.
     */
    public Grammar(String fileName) {
        this.fileName = fileName;
        this.setOfNonTerminals = new LinkedList<>();
        this.setOfTerminals = new HashSet<>();
        this.setOfProductions = new LinkedList<>();
        this.startingSymbol = "";
    }

    /*
     * readFromFile method that reads the grammar information from a file and fills the instance variables accordingly.
     * It opens the specified file and reads the non-terminals, terminals, production rules, and starting symbol.
     * Production rules are expected to be in the form NonTerminal -> Production1 | Production2 | ....
     * It splits the production rules and creates Production objects to store them. It stops reading when "STARTING SYMBOL" line.
     */
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

    // GETTERS:

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

    /*
     * Takes a non-terminal as inINPUT and returns a list of productions that have the specified non-terminal as their START SYMBOL.
     * Non-terminal define the structure and rules of a lenguage.
     * Non-terminals help specify the hierarchical structure of sentences or expressions in a language.
     */
    public List<Production> productionForNonTerminal(String nonTerminal) {
        List<Production> productionsForNonTerminal = new LinkedList<>();
        for (Production production : this.setOfProductions) {
            if (production.getStart().equals(nonTerminal)) {
                productionsForNonTerminal.add(production);
            }
        }
        return productionsForNonTerminal;
    }

    /*
     * takes a non-terminal as input and returns a set of productions that contain the specified non-terminal in their RIGHT-HAND SIDE (production rules).
     */
    Set<Production> productionContainingNonTerminal(String nonTerminal) {
        Set<Production> prod = new HashSet<>();
        for (Production p : this.setOfProductions) {
            for (List<String> rule : p.getRules())
                if (rule.contains(nonTerminal))
                    prod.add(p);
        }
        return prod;
    }
}
