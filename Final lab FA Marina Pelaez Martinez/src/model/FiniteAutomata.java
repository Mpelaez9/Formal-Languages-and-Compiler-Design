package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/*
 * Marina Peláez Martínez
 * Class responsible for analyzing and handling a finite automaton (FA) through various operations.
 * Provides methods to read the automaton's structure from a file, check if it's a deterministic finite automaton (DFA), 
 * and determine if a given sequence is accepted by the automaton.
 */
public class FiniteAutomata {
	// ATTRIBUTES:
	private String initialState; // Initial state of the finite automaton.
	private List<String> finalStates; // Holds the final states of the automaton. These states signify the end or acceptance states of the automaton.
	private List<Transition> transitionsList; // Each Transition object represents a transition in the automaton, consisting of an initial state, a value, and a list of final states reached after the transition.
	private List<String> alphabet;  // Symbols that the automaton can read as input.
	private List<String> statesList; // List of all states present in the finite automaton.
	

	/*
	 * Initializes the FA by reading information from a file (fileName) and initializes the FA's properties 
	 * (finalStates, transitionsList, alphabet, statesList, and initialState).
	 */
	public FiniteAutomata(String fileName) {
		this.finalStates=new ArrayList<>();
		this.transitionsList=new ArrayList<>();
		this.alphabet=new ArrayList<>();
		this.statesList=new ArrayList<>();
		readFile(fileName);
	}
	

	// GETTERS (accesing to the private propierties of the FA):

	public List<String> getFinalStates(){
		return this.finalStates;
	}
	
	public List<Transition> getTransitionsList(){
		return this.transitionsList;
	}
	
	public List<String> getAlphabet(){
		return this.alphabet;
	}
	
	public List<String> getStatesList(){
		return this.statesList;
	}
	
	public String getInitialState() {
		return this.initialState;
	}
	
	/*
	 * Reads the file containing FA information and extracts states, alphabet, transitions, final states, and the initial state to complete the FA's internal data structures.
	 * It parses and extracts information from a file.
	 */
	private void readFile(String fileName) {
		try {
			Scanner scanner = new Scanner(new File(fileName)); // We open the "fileName" with a scanner.
			// 								PARSING THE FILE						//
			// STATES LISTS: 
			scanner.nextLine(); // Reads the next line in the file (assuming it's the line indicating the states list) and moves the scanner's position to the following line.
			this.statesList=Arrays.asList(scanner.nextLine()); // Reads the line containing the states list, splits it (assuming the states are comma-separated), and stores the states in the statesList.
			
			// ALPHABET:
			scanner.nextLine(); // Reads the line with the alphabet information and moves the scanner to the next line.
			this.alphabet=Arrays.asList(scanner.nextLine()); // Reads and stores the alphabet symbols in the alphabet list.
			
			// TRANSITIONS:
			scanner.nextLine(); // Reads the line indicating the start of transitions and moves the scanner forward.
			
			String transition;
			while(!scanner.hasNext("FINAL")) {  // Reads lines until it finds the string "FINAL" (end of transitions).
				transition = scanner.nextLine(); // Reads each line that represents a transition in the finite automaton.
				List<String> splitedTransition = Arrays.asList(transition.split(",")); // Splits the transition line into its components (start state, value, end states) based on the comma (,) separator.
				
				// Creates a new Transition object and sets its start state, value, and end states based on the parsed data. 
				Transition tran= new Transition(); 
				tran.setStartState(splitedTransition.get(0));
				tran.setValue(splitedTransition.get(1));
				List<String> endStates = new ArrayList<>();
				for(int i=2;i<splitedTransition.size();i++) { // Adds this transition to the transitionsList.
					 endStates.add(splitedTransition.get(i));
				}
				tran.setEndState(endStates);
	            this.transitionsList.add(tran);
			}

			// FINAL STATE:
			scanner.nextLine(); // Reads the line indicating the start of final states and moves the scanner forward.
			this.finalStates = Arrays.asList(scanner.next().split(",")); // Reads and stores the final states in the finalStates list.
			
			// INITIAL STATE:
			scanner.nextLine(); // Skips one line.
			scanner.nextLine(); // Skips another line.
			this.initialState = scanner.nextLine(); // Reads and stores the initial state of the finite automaton.
			

		// Try catch exception handling to handle the possibility of the file not being found (FileNotFoundException). 
		} catch (FileNotFoundException e) {
			e.printStackTrace(); // If an exception occurs, it prints the stack trace.
		}
	}
	
	/*
	 * Given a start state and a value, this method searches through the transitions list to determine the next state. 
	 * If the transition is not found or leads to multiple states, it returns "No state".
	 */
	private String nextState(String startState, String value) {
		for(Transition tran: transitionsList) { 
			if(tran.getStartState().equals(startState) && tran.getValue().equals(value)) { //  Checks if the current transition in the loop matches the provided startState and value.
				if(tran.getEndState().size() == 1){ 
					// If the transition leads to a single state (indicating a deterministic transition), it recovers and returns that state as the next state.
					return tran.getEndState().get(0);
				}
			}
		}
		return "No state"; // If no valid state is found in the transitions that match the provided startState and value, or if the transition does not lead to a single state.
	}
	
	/*
	 * This method checks a property of a DFA: In a DFA, for each state and input symbol, the transition function must lead to exactly one next state.
	 * If any transition violates this property by leading to multiple states, then the structure is not a DFA.
	 */
	public boolean AfIsDFA() {
		for(Transition tran : transitionsList) {
			// For each transition in the FA, it checks if the number of possible end states (the states the transition leads to) is greater than 1.
			if (tran.getEndState().size() > 1) {
				// If any transition is found that leads to multiple states (non-deterministic behavior) --> returns false.
				return false;
			}
		}
		return true;
	}
	
	/*
	 * Checks whether a provided sequence is accepted by the finite automaton. 
	 * It goes through the sequence character by character, utilizing the nextState() method to determine the next state for each character. 
	 * If the sequence ends in a final state (of the defined final states), it returns true; otherwise, it returns false.
	 */
	public boolean isAccepted(String sequence) {
		String actualState = this.initialState;  
		String [] splittedSequence = sequence.split(""); // Splits the input sequence into an array of individual characters.
			for(String character : splittedSequence) {  
				// For each character in the sequence, it calculates the next state based on the current state (actualState, being the first actual in the first loop) and the character it takes it from the nextState() method.
				String nextState = nextState(actualState, character);
				if(nextState.equals("No state")) {  // Analyzing the nextState returned by the nextState() method.
					return false;
				}
				actualState = nextState; // Updates  the actualState to the nextState for the next iteration.
			}
			if(this.finalStates.contains(actualState)) {
				// Checks if the actualState (state after processing the entire sequence, bc it passed all through conditions) is contained in the finalStates list.
				return true; // If the actualState is a final state --> sequence has been processed successfully
			}
			return false; // If the final state check fails --> actualState not contained in the finalStates.
	}
}
