package domain;

import java.util.*;

/*
 * Marina Pelaez Martinez
 * 2024
 * Class Parser --> top-down (algo. de analisis de arriba hacia abajo) parsing algorithm for context-free grammars that include various methods and data structures for parsing and generating parsing tables.
 */

public class Parser {
    private Grammar grammar;
    private Map<String, Set<String>> firstSet;
    private Map<String, Set<String>> followSet;
    private Map<Pair<String, List<String>>, Integer> productionsNumbered = new HashMap<>();
    private static Stack<List<String>> rules = new Stack<>();
    private ParserOutput parserOutput = new ParserOutput();
    private Stack<String> alfa = new Stack<>();
    private Stack<String> beta = new Stack<>();
    private Stack<String> pi = new Stack<>();

     /*
     * Notes:
     * First sets: used to determine the possible initial symbols (terminals) that can begin a string derived from a non-terminal in a context-free grammar.
     *          They are calculated for each non-terminal and represent the set of terminals that can appear as the first symbol(s) in the strings generated by that non-terminal.
     * Follow sets: used to determine the possible symbols that can follow a non-terminal in a context-free grammar.
     *          They are calculated for each non-terminal and represent the set of terminals that can appear immediately after occurrences of that non-terminal in a string.
     */ 

    /*
     * Constructor -->  takes a Grammar object as an argument and initializes the instance variables.
     */
    public Parser(Grammar grammar) {
        this.grammar = grammar;
        this.firstSet = new HashMap<>();
        this.followSet = new HashMap<>();
    }

     /*
     * Generates the First and Follow sets for the non-terminals in the grammar.
     */
    public void generateSets() {
        generateFirstSet();
        generateFollowSet();
    }

    // These methods compute the First and Follow sets for the non-terminals based on the grammar's rules and structure:
    private void generateFirstSet() {
        for (String nonTerminal : grammar.getSetOfNonTerminals()) {
            firstSet.put(nonTerminal, this.firstOf(nonTerminal));
        }
    }

    private void generateFollowSet() {
        for (String nonTerminal : grammar.getSetOfNonTerminals()) {
            followSet.put(nonTerminal, this.followOf(nonTerminal, nonTerminal));
        }
    }

    /*
     *  Assigns unique numbers to the grammar's production rules.
     */
    private void numberingProductions() {
        int index = 1;
        for (Production production: grammar.getSetOfProductions())
            for (List<String> rule: production.getRules())
                productionsNumbered.put(new Pair<>(production.getStart(), rule), index++);
    }

     /*
     * Calculates the First set for a given non-terminal.
     * Compute the set of terminals that can appear as the first symbol(s) of strings derived from the specified non-terminal symbol (nonTerminal).
     * 
     */
    private Set<String> firstOf(String nonTerminal) {
        if (firstSet.containsKey(nonTerminal))
            return firstSet.get(nonTerminal);
        Set<String> temp = new HashSet<>();
        Set<String> terminals = grammar.getSetOfTerminals();
        for (Production production : grammar.productionForNonTerminal(nonTerminal))
            for (List<String> rule : production.getRules()) {
                String firstSymbol = rule.get(0);
                if (firstSymbol.equals("ε"))
                    temp.add("ε");
                else if (terminals.contains(firstSymbol))
                    temp.add(firstSymbol);
                else
                    temp.addAll(firstOf(firstSymbol));
            }
        return temp;
    }

     /*
     * The Follow set is important in predictive parsing and error recovery to determine when to pop symbols from the parsing stack and how to handle syntactic errors.
     */
    private Set<String> followOf(String nonTerminal, String initialNonTerminal) {
        if (followSet.containsKey(nonTerminal))
            return followSet.get(nonTerminal);
        Set<String> temp = new HashSet<>();
        Set<String> terminals = grammar.getSetOfTerminals();

        if (nonTerminal.equals(grammar.getStartingSymbol()))
            temp.add("$");

        for (Production production : grammar.productionContainingNonTerminal(nonTerminal)) {
            String productionStart = production.getStart();
            for (List<String> rule : production.getRules()){
                List<String> ruleConflict = new ArrayList<>();
                ruleConflict.add(nonTerminal);
                ruleConflict.addAll(rule);
                if (rule.contains(nonTerminal) && !rules.contains(ruleConflict)) {
                    rules.push(ruleConflict);
                    int indexNonTerminal = rule.indexOf(nonTerminal);
                    temp.addAll(followOperation(nonTerminal, temp, terminals, productionStart, rule, indexNonTerminal, initialNonTerminal));

                    List<String> sublist = rule.subList(indexNonTerminal + 1, rule.size());
                    if (sublist.contains(nonTerminal))
                        temp.addAll(followOperation(nonTerminal, temp, terminals, productionStart, rule, indexNonTerminal + 1 + sublist.indexOf(nonTerminal), initialNonTerminal));

                    rules.pop();
                }
            }
        }

        return temp;
    }

     /*
     * Calculate and update the Follow set (temp) for the given nonTerminal symbol based on the context of a specific production rule within which the nonTerminal appears. 
     * This method is used to handle different situations when determining what symbols can follow the nonTerminal within the context of the grammar.
     */
    private Set<String> followOperation(String nonTerminal, Set<String> temp, Set<String> terminals, String productionStart, List<String> rule, int indexNonTerminal, String initialNonTerminal) {
        if (indexNonTerminal == rule.size() - 1) {
            if (productionStart.equals(nonTerminal))
                return temp;
            if (!initialNonTerminal.equals(productionStart)){
                temp.addAll(followOf(productionStart, initialNonTerminal));
            }
        }
        else
        {
            String nextSymbol = rule.get(indexNonTerminal + 1);
            if (terminals.contains(nextSymbol))
                temp.add(nextSymbol);
            else{
                if (!initialNonTerminal.equals(nextSymbol)) {
                    Set<String> fists = new HashSet<>(firstSet.get(nextSymbol));
                    if (fists.contains("ε")) {
                        temp.addAll(followOf(nextSymbol, initialNonTerminal));
                        fists.remove("ε");
                    }
                    temp.addAll(fists);
                }
            }
        }
        return temp;
    }

     /*
     * This method creates a parsing table based on the computed First and Follow sets and assigns actions to table entries for parsing.
     * NOTE: the parsing table is a data structure used to guide the parsing process and determine which actions (such as shifting, reducing, or accepting) to take when parsing input based on the current state and lookahead symbol.
     */
    public void createParseTable() {
        numberingProductions();

        List<String> columnSymbols = new LinkedList<>(grammar.getSetOfTerminals());
        columnSymbols.add("$");

        parserOutput.put(new Pair<>("$", "$"), new Pair<>(Collections.singletonList("acc"), -1));
        for (String terminal: grammar.getSetOfTerminals()) {
            parserOutput.put(new Pair<>(terminal, terminal), new Pair<>(Collections.singletonList("pop"), -1));
        }

        productionsNumbered.forEach((key, value) -> {
            String rowSymbol = key.getKey();
            List<String> rule = key.getValue();
            Pair<List<String>, Integer> parserOutputValue = new Pair<>(rule, value);

            for (String columnSymbol : columnSymbols) {
                Pair<String, String> parserOutputKey = new Pair<>(rowSymbol, columnSymbol);

                if (rule.get(0).equals(columnSymbol) && !columnSymbol.equals("ε")) {
                    parserOutput.put(parserOutputKey, parserOutputValue);
                } else if (grammar.getSetOfNonTerminals().contains(rule.get(0))
                        && firstSet.get(rule.get(0)).contains(columnSymbol)) {
                    if (!parserOutput.containsKey(parserOutputKey)) {
                        parserOutput.put(parserOutputKey, parserOutputValue);
                    }
                }
                else {
                    if (rule.get(0).equals("ε")) {
                        for (String b : followSet.get(rowSymbol)) {
                            parserOutput.put(new Pair<>(rowSymbol, b), parserOutputValue);
                        }
                    } else {
                        Set<String> firsts = new HashSet<>();
                        for (String symbol : rule)
                            if (grammar.getSetOfNonTerminals().contains(symbol))
                                firsts.addAll(firstSet.get(symbol));
                        if (firsts.contains("ε")) {
                            for (String b : firstSet.get(rowSymbol)) {
                                if (b.equals("ε"))
                                    b = "$";
                                parserOutputKey = new Pair<>(rowSymbol, b);
                                if (!parserOutput.containsKey(parserOutputKey)) {
                                    parserOutput.put(parserOutputKey, parserOutputValue);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    /*
     * This method performs the parsing of an input sequence using the parsing table and returns true if the input sequence is valid according to the grammar.
     */
    public boolean parseSequence(List<String> sequence) {
        initializeStacks(sequence);

        boolean go = true;
        boolean result = true;

        while (go) {
            String betaHead = beta.peek();
            String alfaHead = alfa.peek();

            if (betaHead.equals("$") && alfaHead.equals("$")) {
                return true;
            }

            Pair<String, String> heads = new Pair<>(betaHead, alfaHead);
            Pair<List<String>, Integer> parseTableEntry = parserOutput.get(heads);

            if (parseTableEntry == null) {
                heads = new Pair<>(betaHead, "ε");
                parseTableEntry = parserOutput.get(heads);
                if (parseTableEntry != null) {
                    beta.pop();
                    continue;
                }

            }

            if (parseTableEntry == null) {
                go = false;
                result = false;
            } else {
                List<String> production = parseTableEntry.getKey();
                Integer productionPos = parseTableEntry.getValue();

                if (productionPos == -1 && production.get(0).equals("acc")) {
                    go = false;
                } else if (productionPos == -1 && production.get(0).equals("pop")) {
                    beta.pop();
                    alfa.pop();
                } else {
                    beta.pop();
                    if (!production.get(0).equals("ε")) {
                        pushAsChars(production, beta);
                    }
                    pi.push(productionPos.toString());
                }
            }
        }

        return result;
    }
    
    /*
     * This method initializes the parsing stacks (alfa, beta, and pi) with the input sequence and the starting symbol.
     */
    private void initializeStacks(List<String> w) {
        alfa.clear();
        alfa.push("$");
        pushAsChars(w, alfa);

        beta.clear();
        beta.push("$");
        beta.push(grammar.getStartingSymbol());

        pi.clear();
        pi.push("ε");
    }

     /*
     * This method is used to push a list of characters (symbols) onto a stack.
     */
    private void pushAsChars(List<String> sequence, Stack<String> stack) {
        for (int i = sequence.size() - 1; i >= 0; i--) {
            stack.push(sequence.get(i));
        }
    }

    // GETTERS:
    public Map<String, Set<String>> getFirstSet() {
        return firstSet;
    }

    public Map<String, Set<String>> getFollowSet() {
        return followSet;
    }

    public ParserOutput getParserOutput() {
        return parserOutput;
    }
}
