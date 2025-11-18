import java.io.*;

public class Procesador {
	
	public static void main(String[] args) throws IOException {
		String rutaEntrada = "entrada.txt";
		String rutaTokens = "tokens.txt";
		String rutaParse = "parse.txt";
		String rutaTS = "tablas.txt";

		AnalizadorSintactico aSint = new AnalizadorSintactico(rutaEntrada, rutaTokens, rutaParse, rutaTS);
		aSint.start();
	}
}