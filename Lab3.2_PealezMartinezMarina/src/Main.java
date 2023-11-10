import java.io.IOException;

import model.MyScanner;

public class Main {

	public static void main(String[] args) throws IOException {
		MyScanner scanner = new MyScanner("input/p2.txt");
        scanner.scanner();
        scanner.tokensClasification();
        scanner.writeSymbolTable();
	}

}