package domain;

import java.util.List;

/*
 * Marina Pelaez Martinez
 * Class Production -->  represents a single production rule within a context-free grammar. It stores the left-hand side (start symbol) and a list of alternative right-hand side rules. The toString method provides a formatted string representation of the production rule.
 */

public class Production {

    private String start;
    private List<List<String>> rules;

    public Production(String start, List<List<String>> rules) {
        this.start = start;
        this.rules = rules;
    }

    public String getStart() {
        return start;
    }

    public List<List<String>> getRules() {
        return rules;
    }

    @Override
    public String toString() {
        return start + " -> " + rules;
    }
}
