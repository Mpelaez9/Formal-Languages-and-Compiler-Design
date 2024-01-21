
import domain.Grammar;
import domain.Parser;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


/*
 * Marina Pelaez Martinez
 * 2024
 */

public class Main {
    public static void main(String[] args) throws IOException {

        
    	String fileName = "src\\input\\g1.txt";

        // Sequence for G1
        String g1SeqFilename = "src\\input\\seq.txt";
        
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
                case "8":
                    parser.createParseTable();
                    System.out.println(parser.getParserOutput());
                    break;
                case "9":
                // first and follow are called and then it is produced
                    parser.createParseTable();
                    List<String> sequence = readSequence(g1SeqFilename);
                    System.out.println(parser.parseSequence(sequence));
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
        System.out.println("8 - Parsing table");
        System.out.println("9 - Parse sequence G1");
        System.out.println("0 - Exit \n");
    }

    private static List<String> readSequence(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        Scanner scanner = new Scanner(file);
        List<String> output = new ArrayList<>();

        while (scanner.hasNextLine()) {
            String sequence = scanner.nextLine();
            output.addAll(Arrays.asList(sequence.replace("\n", "").split(" ")));
        }

        return output;
    }

}