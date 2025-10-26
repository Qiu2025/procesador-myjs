import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;

public class Procesador {

	// Dimensiones de la matriz de transiciones
	final static int NUMERO_ESTADOS = 11;
	final static int NUMERO_CARACTERES = 21;

	// Estado inicial y final
	final static int ESTADO_INICIAL = 0;
	final static int ESTADO_FINAL = 10;

	// Posibles caracteres de entrada
	final static int LETRA = 0;
	final static int DIGITO = 1;
	final static int COMILLA = 2;
	final static int MAS = 3;
	final static int IGUAL = 4;
	final static int COMA = 5;
	final static int PUNTO = 6;
	final static int PUNTO_COMA = 7;
	final static int PAR_IZQ = 8;
	final static int PAR_DCH = 9;
	final static int LLAVE_IZQ = 10;
	final static int LLAVE_DCH = 11;
	final static int MODULO = 12;
	final static int NEGACION = 13;
	final static int MENOR = 14;
	final static int DEL = 15;
	final static int BARRA = 16;
	final static int ASTERISCO = 17;
	final static int OTRO = 18;
	final static int SALTO = 19;
	final static int EOF = 20;

	// Códigos de acción
	final static int G1 = 1;
	final static int G2 = 2;
	final static int G3 = 3;
	final static int G4 = 4;
	final static int G5 = 5;
	final static int G6 = 6;
	final static int G7 = 7;
	final static int G8 = 8;
	final static int G9 = 9;
	final static int G10 = 10;
	final static int G11 = 11;
	final static int G12 = 12;
	final static int G13 = 13;
	final static int G14 = 14;
	final static int C = 15;
	final static int C_P = 16; // C'
	final static int C_PP = 17; // C''
	final static int L = 18;
	final static int G = 19;
	final static int S = 20;
	final static int S_P = 21; // S'
	final static int S_PP = 22; // S''
	final static int ERROR = 23;

	// Codigos de error
	final static int CARACTER_NO_RECONOCIDO = 30;
	final static int MISSING_COMILLA_CIERRE = 31;
	final static int MISSING_IGUAL = 32;
	final static int MISSING_ASTERISCO = 33;
	final static int MISSING_DIGITO = 34;
	final static int MISSING_FINAL_COMENTARIO = 35;
	final static int REAL_OVERFLOW = 36;
	final static int ENTERO_OVERFLOW = 37;
	final static int CADENA_OVERFLOW = 38;

	// Estructuras de datos / librerias
	static final Entry<Integer, List<Integer>>[][] MT_AFD = new Entry[NUMERO_ESTADOS][NUMERO_CARACTERES];
	static final HashMap<String, String> tablaPR = new HashMap<>();
	static HashMap<String, Integer> tablaSimbolos = new HashMap<>();
	static BufferedReader br;
	static BufferedWriter bwTokens;
	static BufferedWriter bwTablaSimbolos;

	// Variables del analizador lexico
	static int estado; // estado actual
	static int car; // caracter leido

	static String lexema = ""; // construye cadena/identificador
	static int longCadena = 0; // su longitud

	static double num = 0; // valor acumulado (tanto si es entero como real)
	static int exponente = 1; // para el calculo de parte decimal en caso de real

	static int linea = 1; // contador de lineas
	static int startLine; // línea de comienzo de un comentario

	static String token; // token que genera

	/**********************************************************************************************************************************/
	// === MAIN ===

	public static void main(String[] args) {
		inicializarTablaTransicion();
		inicializarTablaPR();

		try {
			br = new BufferedReader(new FileReader("entrada.txt"));
			bwTokens = new BufferedWriter(new FileWriter("salida.txt"));

			car = br.read(); // Primer caracter, para el estado 0
			while (car != -1) {
				token = analizadorLexico();
				bwTokens.write(token + "\n");
			}

			bwTablaSimbolos = new BufferedWriter(new FileWriter("tablasSimbolos.txt"));
			imprimirTablaGlobal();

			bwTokens.close();
			bwTablaSimbolos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// === MAIN ===
	/**********************************************************************************************************************************/

	/**********************************************************************************************************************************/
	// === ACCIONES SEMÁNTICAS ===

	static void C() {
		lexema = "";
		lexema += (char) car;
		longCadena = 1;
	}

	static void C_P() {
		lexema += (char) car;
		longCadena++;
	}

	static void C_PP() {
		lexema = "";
		longCadena = 0;
	}

	static void S() {
		num = car - '0';
		exponente = 1;
	}

	static void S_P() {
		num = num * 10 + (car - '0');
	}

	static void S_PP() {
		int valor = car - '0';
		num += valor / (Math.pow(10, exponente));
		exponente++;
	}

	static void L() {
		try {
			car = br.read();
			if (car == '\n') linea++;
			if (estado == 6) startLine = linea;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static void G() {
		String value = tablaPR.get(lexema);
		if (value != null) {
			token = "<" + value + ",>";
		} else {
			Integer id = tablaSimbolos.get(lexema);
			if (id == null) {
				id = tablaSimbolos.size() + 1;
				tablaSimbolos.put(lexema, id);
			}
			token = "<ID," + id + ">";
		}
	}

	static void G1() {
		token = "<masIgual,>";
	}

	static void G2() {
		token = "<igual,>";
	}

	static void G3() {
		token = "<coma,>";
	}

	static void G4() {
		token = "<puntoComa,>";
	}

	static void G5() {
		token = "<abrirParentesis,>";
	}

	static void G6() {
		token = "<cerrarParentesis,>";
	}

	static void G7() {
		token = "<abrirLlave,>";
	}

	static void G8() {
		token = "<cerrarLlave,>";
	}

	static void G9() {
		token = "<modulo,>";
	}

	static void G10() {
		token = "<negacion,>";
	}

	static void G11() {
		token = "<menorQue,>";
	}

	static void G12() {
		if (num > 117549436.0) {
			tratarError(REAL_OVERFLOW);
			reiniciarVariables();
		} else {
			token = "<numReal," + new BigDecimal(String.valueOf(num)).toPlainString() + ">";
		}
	}

	static void G13() {
		int n = (int) num;
		if (n > 32767) {
			tratarError(ENTERO_OVERFLOW);
			reiniciarVariables();
		} else {
			token = "<numEntero," + n + ">";
		}
	}

	static void G14() {
		if (longCadena > 64) {
			tratarError(CADENA_OVERFLOW);
			reiniciarVariables();
		} else {
			token = "<cadena,\"" + lexema + "\">";
		}
	}

	// === ACCIONES SEMÁNTICAS ===
	/**********************************************************************************************************************************/

	/**********************************************************************************************************************************/
	// === ANALIZADOR LÉXICO ===

	private static String analizadorLexico() throws IOException {
		reiniciarVariables();
		while (estado != ESTADO_FINAL) {

			int tipoCar = tipoCaracter((char) car);
			Entry<Integer, List<Integer>> transicion = MT_AFD[estado][tipoCar];
			Integer nuevoEstado = transicion.getKey();
			List<Integer> acciones = transicion.getValue();

			if (nuevoEstado == null) {
				tratarError(acciones.get(0));
				reiniciarVariables();
			} else {
				estado = nuevoEstado;
				for (int accion : acciones) {
					switch (accion) {
						case G1 -> G1();
						case G2 -> G2();
						case G3 -> G3();
						case G4 -> G4();
						case G5 -> G5();
						case G6 -> G6();
						case G7 -> G7();
						case G8 -> G8();
						case G9 -> G9();
						case G10 -> G10();
						case G11 -> G11();
						case G12 -> G12();
						case G13 -> G13();
						case G14 -> G14();
						case C -> C();
						case C_P -> C_P();
						case C_PP -> C_PP();
						case L -> L();
						case G -> G();
						case S -> S();
						case S_P -> S_P();
						case S_PP -> S_PP();
						default -> throw new IllegalArgumentException("Acción desconocida: " + accion);
					}
				}
			}
		}

		return token;
	}

	// === ANALIZADOR LÉXICO ===
	/**********************************************************************************************************************************/

	/**********************************************************************************************************************************/
	// === OTRAS FUNCIONES ===

	private static void inicializarTablaPR() {
		tablaPR.put("boolean", "tipoBoolean");
		tablaPR.put("float", "tipoFloat");
		tablaPR.put("for", "for");
		tablaPR.put("function", "function");
		tablaPR.put("if", "if");
		tablaPR.put("int", "tipoInt");
		tablaPR.put("let", "let");
		tablaPR.put("read", "read");
		tablaPR.put("return", "return");
		tablaPR.put("string", "tipoString");
		tablaPR.put("void", "void");
		tablaPR.put("write", "write");
	}

	private static void inicializarTablaTransicion() {

		// Estado 0: Estado inicial
		for (int i = 0; i < NUMERO_CARACTERES; i++) {
			MT_AFD[0][i] = new AbstractMap.SimpleEntry<>(null, Arrays.asList(CARACTER_NO_RECONOCIDO));
		}
		// Identificadores y palabras reservadas
		MT_AFD[0][LETRA] = new AbstractMap.SimpleEntry<>(1, Arrays.asList(C, L));
		// Números enteros y reales
		MT_AFD[0][DIGITO] = new AbstractMap.SimpleEntry<>(3, Arrays.asList(S, L));
		// Cadenas (comillas simples)
		MT_AFD[0][COMILLA] = new AbstractMap.SimpleEntry<>(2, Arrays.asList(C_PP, L));
		// Operador de suma y asignación
		MT_AFD[0][MAS] = new AbstractMap.SimpleEntry<>(5, Arrays.asList(L));
		// Delimitadores (espacio, tabulador, saltos de línea)
		MT_AFD[0][DEL] = new AbstractMap.SimpleEntry<>(0, Arrays.asList(L));
		MT_AFD[0][BARRA] = new AbstractMap.SimpleEntry<>(6, Arrays.asList(L));
		MT_AFD[0][SALTO] = new AbstractMap.SimpleEntry<>(0, Arrays.asList(L));

		MT_AFD[0][EOF] = new AbstractMap.SimpleEntry<>(ESTADO_FINAL, new ArrayList<>());	// Si hay EOF, salir
		MT_AFD[0][IGUAL] = new AbstractMap.SimpleEntry<>(ESTADO_FINAL, Arrays.asList(L, G2));
		MT_AFD[0][COMA] = new AbstractMap.SimpleEntry<>(ESTADO_FINAL, Arrays.asList(L, G3));
		MT_AFD[0][PUNTO_COMA] = new AbstractMap.SimpleEntry<>(ESTADO_FINAL, Arrays.asList(L, G4));
		MT_AFD[0][PAR_IZQ] = new AbstractMap.SimpleEntry<>(ESTADO_FINAL, Arrays.asList(L, G5));
		MT_AFD[0][PAR_DCH] = new AbstractMap.SimpleEntry<>(ESTADO_FINAL, Arrays.asList(L, G6));
		MT_AFD[0][LLAVE_IZQ] = new AbstractMap.SimpleEntry<>(ESTADO_FINAL, Arrays.asList(L, G7));
		MT_AFD[0][LLAVE_DCH] = new AbstractMap.SimpleEntry<>(ESTADO_FINAL, Arrays.asList(L, G8));
		MT_AFD[0][MODULO] = new AbstractMap.SimpleEntry<>(ESTADO_FINAL, Arrays.asList(L, G9));
		MT_AFD[0][NEGACION] = new AbstractMap.SimpleEntry<>(ESTADO_FINAL, Arrays.asList(L, G10));
		MT_AFD[0][MENOR] = new AbstractMap.SimpleEntry<>(ESTADO_FINAL, Arrays.asList(L, G11));

		// Estado 1: Identificadores y palabras reservadas
		for (int i = 0; i < NUMERO_CARACTERES; i++) {
			MT_AFD[1][i] = new AbstractMap.SimpleEntry<>(ESTADO_FINAL, Arrays.asList(G));
		}
		MT_AFD[1][LETRA] = new AbstractMap.SimpleEntry<>(1, Arrays.asList(C_P, L));
		MT_AFD[1][DIGITO] = new AbstractMap.SimpleEntry<>(1, Arrays.asList(C_P, L));

		// Estado 2: Cadenas
		for (int i = 0; i < NUMERO_CARACTERES; i++) {
			MT_AFD[2][i] = new AbstractMap.SimpleEntry<>(2, Arrays.asList(C_P, L));
		}
		MT_AFD[2][COMILLA] = new AbstractMap.SimpleEntry<>(ESTADO_FINAL, Arrays.asList(L, G14));
		MT_AFD[2][SALTO] = new AbstractMap.SimpleEntry<>(null, Arrays.asList(MISSING_COMILLA_CIERRE, L));
		MT_AFD[2][EOF] = new AbstractMap.SimpleEntry<>(null, Arrays.asList(MISSING_COMILLA_CIERRE));

		// Estado 3: Números enteros y reales
		for (int i = 0; i < NUMERO_CARACTERES; i++) {
			MT_AFD[3][i] = new AbstractMap.SimpleEntry<>(ESTADO_FINAL, Arrays.asList(G13));
		}
		MT_AFD[3][DIGITO] = new AbstractMap.SimpleEntry<>(3, Arrays.asList(S_P, L));
		MT_AFD[3][PUNTO] = new AbstractMap.SimpleEntry<>(9, Arrays.asList(L));

		// Estado 4: Parte decimal de números reales
		for (int i = 0; i < NUMERO_CARACTERES; i++) {
			MT_AFD[4][i] = new AbstractMap.SimpleEntry<>(ESTADO_FINAL, Arrays.asList(G12));
		}
		MT_AFD[4][DIGITO] = new AbstractMap.SimpleEntry<>(4, Arrays.asList(S_PP, L));

		// Estado 5: Operador de suma y asignación
		for (int i = 0; i < NUMERO_CARACTERES; i++) {
			MT_AFD[5][i] = new AbstractMap.SimpleEntry<>(null, Arrays.asList(MISSING_IGUAL));
		}
		MT_AFD[5][IGUAL] = new AbstractMap.SimpleEntry<>(ESTADO_FINAL, Arrays.asList(L, G1));

		// Estado 6: Posible comentario o división
		for (int i = 0; i < NUMERO_CARACTERES; i++) {
			MT_AFD[6][i] = new AbstractMap.SimpleEntry<>(null, Arrays.asList(MISSING_ASTERISCO));
		}
		MT_AFD[6][ASTERISCO] = new AbstractMap.SimpleEntry<>(7, Arrays.asList(L)); // Comentario de línea
		MT_AFD[6][EOF] = new AbstractMap.SimpleEntry<>(null, Arrays.asList(MISSING_ASTERISCO));

		// Estado 7: Comentario de línea
		for (int i = 0; i < NUMERO_CARACTERES; i++) {
			MT_AFD[7][i] = new AbstractMap.SimpleEntry<>(7, Arrays.asList(L));
		}
		MT_AFD[7][ASTERISCO] = new AbstractMap.SimpleEntry<>(8, Arrays.asList(L));
		MT_AFD[7][EOF] = new AbstractMap.SimpleEntry<>(null, Arrays.asList(MISSING_FINAL_COMENTARIO));

		// Estado 8: Fin de comentario de línea
		for (int i = 0; i < NUMERO_CARACTERES; i++) {
			MT_AFD[8][i] = new AbstractMap.SimpleEntry<>(7, Arrays.asList(L));
		}
		MT_AFD[8][BARRA] = new AbstractMap.SimpleEntry<>(0, Arrays.asList(L));
		MT_AFD[8][ASTERISCO] = new AbstractMap.SimpleEntry<>(8, Arrays.asList(L));
		MT_AFD[8][EOF] = new AbstractMap.SimpleEntry<>(null, Arrays.asList(MISSING_FINAL_COMENTARIO));

		// Estado 9: Punto en número real
		for (int i = 0; i < NUMERO_CARACTERES; i++) {
			MT_AFD[9][i] = new AbstractMap.SimpleEntry<>(null, Arrays.asList(MISSING_DIGITO));
		}
		MT_AFD[9][DIGITO] = new AbstractMap.SimpleEntry<>(4, Arrays.asList(S_PP, L));
	}

	private static void reiniciarVariables() {
		estado = ESTADO_INICIAL;
		token = "";
		lexema = "";
		longCadena = 0;
		num = 0;
		exponente = 1;
	}

	private static void imprimirTablaGlobal() throws IOException {
		bwTablaSimbolos.append("TABLA PRINCIPAL #1:\n");
		for (Map.Entry<String, Integer> entry : tablaSimbolos.entrySet()) {
			bwTablaSimbolos.append("* LEXEMA: '" + entry.getKey() + "'\n");
		}
	}

	private static int tipoCaracter(char car) {
		if ((car >= 'a' && car <= 'z') || (car >= 'A' && car <= 'Z') || car == '_')
			return LETRA;
		else if (car >= '0' && car <= '9')
			return DIGITO;
		else if (car == '\'')
			return COMILLA;
		else if (car == '+')
			return MAS;
		else if (car == '=')
			return IGUAL;
		else if (car == ',')
			return COMA;
		else if (car == '.')
			return PUNTO;
		else if (car == ';')
			return PUNTO_COMA;
		else if (car == '(')
			return PAR_IZQ;
		else if (car == ')')
			return PAR_DCH;
		else if (car == '{')
			return LLAVE_IZQ;
		else if (car == '}')
			return LLAVE_DCH;
		else if (car == '%')
			return MODULO;
		else if (car == '!')
			return NEGACION;
		else if (car == '<')
			return MENOR;
		else if (car == ' ' || car == '\t')
			return DEL;
		else if (car == '/')
			return BARRA;
		else if (car == '*')
			return ASTERISCO;
		else if (car == '\n')
			return SALTO;
		else if (car == (char) -1)
			return EOF;
		else
			return OTRO;
	}

	private static void tratarError(int codError) {

		String charLeido;		  // para poder imprimir caracteres especiales como \n, \t
		int lineaMostrar = linea; // por defecto, imprimimos el número de línea actual

		if (car == '\n') {
			charLeido = "\\n";
			lineaMostrar = linea - 1;
		} else if (car == '\t') {
			charLeido = "\\t";
		} else if (car == ' ') {
			charLeido = "espacio";
		} else if (car == -1) {
			charLeido = "EOF";
		} else {
			charLeido = String.valueOf((char) car);
		}

		switch (codError) {
			case CARACTER_NO_RECONOCIDO:
				System.out.println("Error léxico en línea " + lineaMostrar +
						", leyendo carácter '" + charLeido +
						"', motivo: carácter no reconocido.");
				L();
				break;

			case MISSING_COMILLA_CIERRE:
				System.out.println("Error léxico en línea " + lineaMostrar +
						", leyendo carácter '\\n', motivo: se espera una comilla de cierre.");
				L();
				break;

			case MISSING_FINAL_COMENTARIO:
				System.out.println("Error léxico en línea " + startLine +
						", leyendo EOF, motivo: se espera una '/' que termine el comentario.");
				break;

			case MISSING_IGUAL:
				System.out.println("Error léxico en línea " + lineaMostrar +
						", leyendo carácter '" + charLeido +
						"', motivo: se espera el carácter '='.");
				break;

			case MISSING_ASTERISCO:
				System.out.println("Error léxico en línea " + lineaMostrar +
						", leyendo carácter '" + charLeido +
						"', motivo: se espera el carácter '*'.");
				break;

			case MISSING_DIGITO:
				System.out.println("Error léxico en línea " + lineaMostrar +
						", leyendo carácter '" + charLeido +
						"', motivo: se espera un dígito.");
				break;

			case ENTERO_OVERFLOW:
				System.out.println("Error léxico en línea " + lineaMostrar +
						", número entero demasiado grande: '" + (int) num + "'");
				break;

			case REAL_OVERFLOW:
				System.out.println("Error léxico en línea " + lineaMostrar +
						", número real demasiado grande: '" + new BigDecimal(String.valueOf(num)).toPlainString()
						+ "'");
				break;

			case CADENA_OVERFLOW:
				System.out.println("Error léxico en línea " + lineaMostrar +
						", cadena demasiado larga: " + lexema);
				L();
				break;

			default:
				System.out.println("Error léxico no cubierto por el gestor de errores");
				break;
		}
	}

	// === OTRAS FUNCIONES ===
	/**********************************************************************************************************************************/

}