import domain.Grammar;
import domain.Parser;

import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {

        // G1
    	String fileName = "src/input/g1.txt";

        Grammar grammar = new Grammar(fileName);
        grammar.readFromFile();
        Parser parser = new Parser(grammar);
        parser.generateSets();

        while (true) {
            display_menu();
            String command = "";
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter command: ");
            command = reader.readLine();

            switch (command) {
                case "1":
                    System.out.println("Non-terminals: ");
                    System.out.println(grammar.getSetOfNonTerminals());
                    System.out.println("\n");
                    break;
                case "2":
                    System.out.println("Terminals: ");
                    System.out.println(grammar.getSetOfTerminals());
                    System.out.println("\n");
                    break;
                case "3":
                    System.out.println("Production: ");
                    System.out.println(grammar.getSetOfProductions());
                    System.out.println("\n");
                    break;
                case "4":
                    System.out.println("Starting symbol: ");
                    System.out.println(grammar.getStartingSymbol());
                    System.out.println("\n");
                    break;
                case "5":
                    System.out.println("Enter non-terminal: ");
                    Scanner input = new Scanner(System.in);
                    String non_terminal = input.next();
                    System.out.println(grammar.productionForNonTerminal(non_terminal));
                    System.out.println("\n");
                    break;
                case "6":
                    System.out.println(parser.getFirstSet());
                    break;
                case "7":
                    System.out.println(parser.getFollowSet());
                    break;
                case "0":
                    System.exit(0);
                default:
                    System.err.println("Unrecognized option");
                    break;
            }
        }
    }

    private static void display_menu() {
        System.out.println("1 - Show non-terminals");
        System.out.println("2 - Show terminals");
        System.out.println("3 - Show productions");
        System.out.println("4 - Show starting symbol");
        System.out.println("5 - Production for given non-terminal");
        System.out.println("6 - First set");
        System.out.println("7 - Follow set");
        System.out.println("0 - Exit \n");
    }

}