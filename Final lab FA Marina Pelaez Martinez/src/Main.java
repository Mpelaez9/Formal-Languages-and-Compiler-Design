import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import model.FiniteAutomata;

/*
 * Marina Peláez Martínez
 * Main class with an interactive command-line interface (CLI) application to operate and display information about a finite automaton (FA) based on user input.
 * It can display its properties, verify if it's a DFA, and test sequences for acceptance inside the defined automaton.
 */

public class Main {

	public static void main(String[] args) throws IOException {

		// Initializes a FiniteAutomata object by reading information from a file named "FA.in" located in the "files" directory.
		FiniteAutomata finiteAutomata = new FiniteAutomata("files/FA.in");

		while(true) { // Continuously displays a menu and waits for user input until the program is terminated by entering 0.
			printMenu();
			BufferedReader optReader = new BufferedReader(new InputStreamReader(System.in)); // Read user input from the command line.
	        System.out.println("Enter command: ");
	        String command = optReader.readLine(); 

	        if(command.equals("0")) {
	        	System.exit(0);


			// Displays information about states, alphabet, transitions, final states, or the initial state of the finite automaton (options 1 to 5).
	        }else if(command.equals("1")){
	        	System.out.println("States");
	        	System.out.println(finiteAutomata.getStatesList() + "\n");
	        }else if(command.equals("2")){
	        	System.out.println("Alphabet");
	        	System.out.println(finiteAutomata.getAlphabet() + "\n");
		    }else if(command.equals("3")){
		    	System.out.println("Transitions");
	        	System.out.println(finiteAutomata.getTransitionsList() + "\n");
		    }else if(command.equals("4")){
		    	System.out.println("Final states");
	        	System.out.println(finiteAutomata.getFinalStates() + "\n");
		    }else if(command.equals("5")){
		    	System.out.println("Initial state");
	        	System.out.println(finiteAutomata.getInitialState() + "\n");


			//Determines if the finite automaton is a DFA (AfIsDFA()) and outputs the result (option 6).
		    }else if(command.equals("6")){
		    	if(finiteAutomata.AfIsDFA()) {
		    		System.out.println("It is a DFA \n");
		    	}else {
		    		System.out.println("It is a DFA \n");
		    	}


			//Verifies whether a sequence entered by the user is accepted by the automaton (isAccepted(sequence)) and provides feedback on acceptance (option 7).
		    }else if(command.equals("7")){
		    	BufferedReader seqReader = new BufferedReader(new InputStreamReader(System.in));
		    	System.out.println("Sequence: ");
		    	String sequence = seqReader.readLine();
		    	if(finiteAutomata.AfIsDFA()) {
		    		if(finiteAutomata.isAccepted(sequence)) {
			    		System.out.println("The introduced sequence IS accepted");
			    	}else {
			    		System.out.println("The introduced sequence IS NOT accepted");
			    	}
		    	}else {
		    		System.out.println("The finite automata IS NOT deterministic");
		    	}	
				

			//Handles unknown options and displays an error message if the user enters an invalid option.
		    }else {
		    	System.err.println("Unknown option");
	        }
		}
	}
	
	private static void printMenu() { // Prints options for the user to choose from.
		System.out.println("1 - Show states");
        System.out.println("2 - Show alphabet");
        System.out.println("3 - Show transitions");
        System.out.println("4 - Show the final states");
        System.out.println("5 - Show the initial state");
        System.out.println("6 - Is DFA?");
        System.out.println("7 - Verify a sequence");
        System.out.println("0 - Exit");
	}

}
