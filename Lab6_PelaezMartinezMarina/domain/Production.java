package domain;

import java.util.List;

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
