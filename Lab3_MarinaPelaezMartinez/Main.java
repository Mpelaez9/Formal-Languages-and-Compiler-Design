import java.io.IOException;

import model.CustomScanner;

public class Main {

	public static void main(String[] args) throws IOException {
		CustomScanner scanner = new CustomScanner("input/p2.txt");
        scanner.scan();
        scanner.classifyTokens();
        scanner.writeToSymbolTable();
	}

}