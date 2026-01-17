
import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class ALexico {

	// Tabla de simbolos
	private TablaSimbolos ts;
	
	private final int NUMERO_ESTADOS = 11;
	private final int NUMERO_CARACTERES = 21;
	private final int ESTADO_INICIAL = 0;
	private final int ESTADO_FINAL = 10;

	@SuppressWarnings("unchecked")
	private final Entry<Integer, List<Integer>>[][] MT_AFD = new Entry[NUMERO_ESTADOS][NUMERO_CARACTERES];
	private BufferedReader br;
	private BufferedWriter bwTokens;

	private final int LETRA = 0;
	private final int DIGITO = 1;
	private final int COMILLA = 2;
	private final int MAS = 3;
	private final int IGUAL = 4;
	private final int COMA = 5;
	private final int PUNTO = 6;
	private final int PUNTO_COMA = 7;
	private final int PAR_IZQ = 8;
	private final int PAR_DCH = 9;
	private final int LLAVE_IZQ = 10;
	private final int LLAVE_DCH = 11;
	private final int MODULO = 12;
	private final int NEGACION = 13;
	private final int MENOR = 14;
	private final int DEL = 15;
	private final int BARRA = 16;
	private final int ASTERISCO = 17;
	private final int OTRO = 18;
	private final int SALTO = 19;
	private final int EOF = 20;

	private final int G1 = 1;
	private final int G2 = 2;
	private final int G3 = 3;
	private final int G4 = 4;
	private final int G5 = 5;
	private final int G6 = 6;
	private final int G7 = 7;
	private final int G8 = 8;
	private final int G9 = 9;
	private final int G10 = 10;
	private final int G11 = 11;
	private final int G12 = 12;
	private final int G13 = 13;
	private final int G14 = 14;
	private final int G15 = 15;
	private final int C = 16;
	private final int C_P = 17; // C'
	private final int C_PP = 18; // C''
	private final int L = 19;
	private final int G = 20;
	private final int S = 21;
	private final int S_P = 22; // S'
	private final int S_PP = 23; // S''

	private final int CARACTER_NO_RECONOCIDO = 30;
	private final int MISSING_COMILLA_CIERRE = 31;
	private final int MISSING_IGUAL = 32;
	private final int MISSING_ASTERISCO = 33;
	private final int MISSING_DIGITO = 34;
	private final int MISSING_FINAL_COMENTARIO = 35;
	private final int REAL_OVERFLOW = 36;
	private final int ENTERO_OVERFLOW = 37;
	private final int CADENA_OVERFLOW = 38;

	private final int DOBLE_DECLARACION = 39;

	private int estado; // estado actual
	private int car; // caracter leido

	private String lexema = ""; // construye cadena/identificador
	private int longCadena = 0; // su longitud

	private double num = 0; // valor acumulado (tanto si es entero como real)
	private int exponente = 1; // para el calculo de parte decimal en caso de real

	private int linea = 1; // contador de lineas, usado internamente (no se imprime)
	private int tokenLine = 1; // línea donde empezó el token que se está devolviendo, se imprime
	private int startLine; // línea de comienzo de un comentario

	private String token; // token que genera

    /**********************************************************************************************************************************/

    public ALexico(String rutaEntrada, String rutaTokens, TablaSimbolos ts) throws IOException {
        br = new BufferedReader(new FileReader(rutaEntrada));
		bwTokens = new BufferedWriter(new FileWriter(rutaTokens));
		this.ts = ts;

        car = br.read();    // El caracter de entrada para el estado 0

        inicializarTablaTransicion();
    }
    
    public String nextToken() throws IOException {
		reiniciarVariables();
    	tokenLine = linea;	// guardar la línea del token actual

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
						case G15 -> G15();
						case C -> C();
						case C_P -> C_P();
						case C_PP -> C_PP();
						case L -> L();
						case G -> G();
						case S -> S();
						case S_P -> S_P();
						case S_PP -> S_PP();
						default -> {
							System.out.println("[Error interno] Accion léxica desconocida");
							System.out.printf("Código de acción: %d\n", accion);
						}
					}
				}
			}
		}

		bwTokens.write(token + "\n");
		return token;
	}

	public int getLinea() { return tokenLine; }

	public void cerrarRecursos() throws IOException {
		br.close();
		bwTokens.close();
	}

    /**********************************************************************************************************************************/

    private void C() {
		lexema = "";
		lexema += (char) car;
		longCadena = 1;
	}

    private void C_P() {
        lexema += (char) car;
        longCadena++;
    }

    private void C_PP() {
        lexema = "";
        longCadena = 0;
    }

    private void S() {
        num = car - '0';
        exponente = 1;
    }

    private void S_P() {
        num = num * 10 + (car - '0');
    }

    private void S_PP() {
        int valor = car - '0';
        num += valor / (Math.pow(10, exponente));
        exponente++;
    }

    private void L() {
        try {
            car = br.read();
            if (car == '\n')
                linea++;
            if (estado == 6)
                startLine = linea;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void G() {
        String value = ts.buscaEnTPR(lexema);
        if (value != null) {
            token = "<" + value + ",>";
			return;
		}
	
		// Estar aqui -> lexema es un identificador
		int id_pos;
		if(ASintacticoSemantico.zonaDecl) {
			id_pos = ts.buscaEnTSA(lexema);
			if (id_pos != 0)
				tratarError(DOBLE_DECLARACION);
			else
				id_pos = ts.insertaLexemaEnTSA(lexema);
		} else {
			id_pos = ts.buscaEnTSA(lexema);
			if (id_pos == 0) {
				id_pos = ts.insertaLexemaEnTSA(lexema);
				ts.insertaTipo(id_pos, ASintacticoSemantico.T_ENTERO);
			}
		}
		
		token = "<ID," + id_pos + ">";
    }

    private void G1() { token = "<masIgual,>"; }
    
    private void G2() { token = "<igual,>"; }

    private void G3() { token = "<coma,>"; }

    private void G4() { token = "<puntoComa,>"; }

    private void G5() { token = "<abrirParentesis,>"; }

    private void G6() { token = "<cerrarParentesis,>"; }

    private void G7() { token = "<abrirLlave,>"; }

    private void G8() { token = "<cerrarLlave,>"; }

    private void G9() { token = "<modulo,>"; }

    private void G10() { token = "<negacion,>"; }

    private void G11() { token = "<menorQue,>"; }

    private void G12() {
        if (num > 117549436.0) {
            tratarError(REAL_OVERFLOW);
            reiniciarVariables();
        } else {
            token = "<numReal," + num + ">";
        }
    }

    private void G13() {
        int n = (int) num;
        if (n > 32767) {
            tratarError(ENTERO_OVERFLOW);
            reiniciarVariables();
        } else {
            token = "<numEntero," + n + ">";
        }
    }

    private void G14() {
        if (longCadena > 64) {
            tratarError(CADENA_OVERFLOW);
            reiniciarVariables();
        } else {
            token = "<cadena,\"" + lexema + "\">";
        }
    }

	private void G15() { token = "<eof,>"; }

	/**********************************************************************************************************************************/

	private void reiniciarVariables() {
		estado = ESTADO_INICIAL;
		token = "";
		lexema = "";
		longCadena = 0;
		num = 0;
		exponente = 1;
	}

	private int tipoCaracter(char car) {
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
		else if (car == ' ' || car == '\t' || car == '\r')
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

	private void tratarError(int codError) {
		String charLeido; // para poder imprimir caracteres especiales como \n, \t, \r

		if (car == '\n') {
			charLeido = "\\n";
		} else if (car == '\t') {
			charLeido = "\\t";
		} else if (car == ' ') {
			charLeido = "espacio";
		} else if (car == '\r') {
    		charLeido = "\\r";
		} else if (car == -1) {
			charLeido = "EOF";
		} else {
			charLeido = String.valueOf((char) car);
		}

		switch (codError) {
			case CARACTER_NO_RECONOCIDO -> {
                System.out.printf("[Error léxico] línea %d\n", tokenLine);
				System.out.printf("Leyendo: %s\n", charLeido);
				System.out.println("Motivo: carácter no reconocido.");
				L();
			}
			case MISSING_COMILLA_CIERRE -> {
				System.out.printf("[Error léxico] línea %d\n", tokenLine);
				System.out.println("Leyendo: '\\n'");
				System.out.println("Motivo: se espera una comilla de cierre");
				L();
			}
			case MISSING_FINAL_COMENTARIO -> {
				System.out.printf("[Error léxico] línea %d\n", startLine);
				System.out.println("Leyendo: EOF");
				System.out.println("Motivo: se espera una '/' que termine el comentario.");
			}
			case MISSING_IGUAL -> {
				System.out.printf("[Error léxico] línea %d\n", tokenLine);
				System.out.printf("Leyendo: '%s'\n", charLeido);
				System.out.println("Motivo: se espera el carácter '='.");
			}
			case MISSING_ASTERISCO -> {
				System.out.printf("[Error léxico] línea %d\n", tokenLine);
				System.out.printf("Leyendo: '%s'\n", charLeido);
				System.out.println("Motivo: se espera el carácter '*'.");
			}
			case MISSING_DIGITO -> {
				System.out.printf("[Error léxico] línea %d\n", tokenLine);
				System.out.printf("Leyendo: '%s'\n", charLeido);
				System.out.println("Motivo: se espera un dígito.");
			}
			case ENTERO_OVERFLOW -> {
				System.out.printf("[Error léxico] línea %d\n", tokenLine);
				System.out.printf("Leyendo: '%d'\n", (int) num);
				System.out.println("Motivo: número entero demasiado grande.");
			}
			case REAL_OVERFLOW -> {
				System.out.printf("[Error léxico] línea %d\n", tokenLine);
				System.out.printf("Leyendo: '%s'\n", Double.toString(num));
				System.out.println("Motivo: número real demasiado grande.");
			}
			case CADENA_OVERFLOW -> {
				System.out.printf("[Error léxico] línea %d\n", tokenLine);
				System.out.printf("Leyendo: \"%s\"\n", lexema);
				System.out.println("Motivo: cadena demasiado larga.");
				L();
			}
			case DOBLE_DECLARACION -> {
				System.out.printf("[Error semántico] línea %d\n", tokenLine);
				System.out.printf("Leyendo: \"%s\"\n", lexema);
				System.out.println("Motivo: doble declaracion.");
			}
			default -> System.out.println("Error léxico no cubierto por el gestor de errores");
		}

		System.out.println("--------------------------------------------------------------------------");
		throw new MiExcepcion("Análisis abortado por error léxico" + 
							"\n--------------------------------------------------------------------------");
	}

	private void inicializarTablaTransicion() {
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

		MT_AFD[0][EOF] = new AbstractMap.SimpleEntry<>(ESTADO_FINAL, Arrays.asList(G15)); // Generar token EOF
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
}