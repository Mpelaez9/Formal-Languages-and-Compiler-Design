package model;

import java.util.List;

/*
 * Marina Peláez Martínez
 * This calss represents transitions in a finite automaton with its information.
 * It provides methods to set and retrieve the initial state, transition value, and final states. The toString() method gives a readable representation of a transition.
 */

public class Transition {
    // ATTRIBUTES:
	private String sourceState; // Represents the initial state of the transition.
	private String value; // Represents the value of the transition.
	private List<String> destinationState; // Represents the list of final states the transition leads to.
	
    /*
     * Default constructor.
     */
	public Transition() {
    }

    // SETTERS:
    public void setStartState(String startState) {
        this.sourceState = startState;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setEndState(List<String> endState) {
        this.destinationState = endState;
    }

    // GETTERS:
    public String getStartState() {
        return sourceState;
    }

    public String getValue() {
        return value;
    }

    public List<String> getEndState() {
        return destinationState;
    }

    /*
     * Overrides the toString() to provide a formatted string representation of the transition.
     * δ represents the TRANSITION function.
     * initialState is the initial state.
     * value is the triggering value.
     * finalState is the list of final states.
     */
    @Override
    public String toString() {
        return "δ(" + sourceState + "," + value + ") = " + destinationState;
    }
	
}
