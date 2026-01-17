
import java.io.*;

public class Procesador {
	
	public static void main(String[] args) throws IOException {
		String rutaEntrada = "entrada.txt";
		String rutaTokens = "tokens.txt";
		String rutaParse = "parse.txt";
		String rutaTS = "tablas.txt";

		TablaSimbolos ts = new TablaSimbolos(rutaTS);
		try {
			ts.init();

			ASintacticoSemantico aSint = new ASintacticoSemantico(rutaEntrada, rutaTokens, rutaParse, ts);
			aSint.start();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			ts.write(1);
			ts.destroyAll();
		}
	}
}