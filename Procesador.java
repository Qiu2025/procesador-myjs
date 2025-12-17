
import java.io.*;

public class Procesador {
	
	public static void main(String[] args) throws IOException {
		String rutaEntrada = "entrada.txt";
		String rutaTokens = "tokens.txt";
		String rutaParse = "parse.txt";
		String rutaTS = "tablas.txt";

		TablaSimbolos ts = new TablaSimbolos(rutaTS);
		ts.init();

		ASSGemini aSint = new ASSGemini(rutaEntrada, rutaTokens, rutaParse, ts);
		aSint.start();

		// ASintacticoSemantico aSint = new ASintacticoSemantico(rutaEntrada, rutaTokens, rutaParse, ts);
		// aSint.start();

		ts.print();
		ts.destroyAll();
	}
}