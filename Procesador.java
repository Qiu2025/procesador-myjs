import java.io.IOException;

public class Procesador {
	
	public static void main(String[] args) throws IOException {
		String rutaEntrada = "entrada.txt";
		String rutaTokens = "tokens.txt";
		String rutaParse = "parse.txt";
		String rutaTS = "tablas.txt";

		// Si esta activado este modo, el procesador seguira con el analisis lexico en caso de error sintactico o semantico,
		// imprimiendo mensajes de si exito o error lexico.
		boolean modoDebug = true;

		TablaSimbolos ts = new TablaSimbolos(rutaTS);
		ts.init();

		ASintacticoSemantico aSint = new ASintacticoSemantico(rutaEntrada, rutaTokens, rutaParse, ts, modoDebug);
		aSint.start();

		ts.write(1);
		ts.destroyAll();
	}
}