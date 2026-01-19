import java.io.*;
import java.util.*;

public class ASintacticoSemantico {
    
	private ALexico aLex;
	private TablaSimbolos ts;
	private BufferedWriter bwParse;

	// Analizador sintactico
    private String token; // token devuelto por el lexico
	private int id_pos;	// atributo del token cuando es un ID
	private boolean modoDebug;

	public static final String T_ENTERO   = "entero";
	public static final String T_REAL     = "real";
	public static final String T_LOGICO   = "logico";
	public static final String T_CADENA   = "cadena";
	public static final String T_VACIO    = "vacio";
	public static final String T_FUNCION  = "funcion";

	// Atributos semanticos
	private static final String TIPO        = "tipo";
	private static final String TIPO_IZQ    = "tipoIzq";
	private static final String TIPO_RET    = "tipoRet";
	private static final String N           = "n";
	private static final String LLAMAFUNC   = "llamaFunc";
	private static final String TAMANO      = "tamano";
	private static final String FUNCTION    = "function";
	private static final String ES_SUMA     = "esSuma";

    public ASintacticoSemantico(String rutaEntrada, String rutaTokens, String rutaParse, TablaSimbolos ts, boolean modoDebug) throws IOException {
		this.ts = ts;
		this.modoDebug = modoDebug;
        aLex = new ALexico(rutaEntrada, rutaTokens, ts);
		bwParse = new BufferedWriter(new FileWriter(rutaParse));
		bwParse.write("descendente");
    }
    
	public void start() throws IOException {
		try {
			token = aLex.nextToken();
			formatearToken();
			P();
			if (!token.equals("$")) {
				errorSintactico("fin de archivo '$'");
			}

			aLex.cerrarRecursos();
			cerrarRecursos();
		} catch (ExcepcionLexico el) {
			// Si hubo error lexico terminamos inmediatamente
			el.printStackTrace();

			aLex.cerrarRecursos();
			cerrarRecursos();
		} catch (ExcepcionSintacticoSemantico ess) {
			// Si es error semantico o sintactico, solo si estamos en modo Debug
			// seguimos con el analisis lexico

			if (modoDebug) {
				ess.printStackTrace();

				// Para escribir todo el fichero de tokens aunque haya error sintactico/semantico
				System.out.println("\nHUBO ERROR SINTACTICO/SEMANTICO, PERO SEGUIMOS CON ANALISIS LEXICO");
				System.out.println("--------------------------------------------------------------------------");
				try {
					while (token != null && !token.equals("$")) {
						token = aLex.nextToken();
						formatearToken();
					}
				} catch (ExcepcionLexico el) {
					// Si ocurre un error LEXICO en el proceso abortamos
					el.printStackTrace();
					aLex.cerrarRecursos();
					cerrarRecursos();
					return;
				}

				System.out.println("ANALISIS COMPLETADO, tokens.txt CORRECTO GENERADO");
				System.out.println("--------------------------------------------------------------------------");
			}

			aLex.cerrarRecursos();
			cerrarRecursos();
		}
	}

    /**********************************************************************************************************************************/

	private InfoToken equipara(String tokenEsperado) throws IOException {
		// Guardar el token consumido para mensajes de error correctos
		InfoToken info = new InfoToken();
		info.linea = aLex.getLinea();
        info.id_pos = id_pos;
        info.lexema = aLex.getLexema();

		if (token.equals(tokenEsperado)) {
			token = aLex.nextToken();
			formatearToken();
		} else {
			errorSintactico(tokenEsperado);
		}

		return info;
	}

	/**
	 * Dados un token y una lista de tokens, comprueba si el token aparece de la lista
	 * @param token el token a buscar
	 * @param tokensComparar la lista de tokens
	 * @return true si token pertenece a la lista, false en caso contrario
	 */
	private boolean compararTokens(String token, String[] tokensComparar) {
		boolean res = false;
		for (String s : tokensComparar) {
			if (res) break;
			res = res || token.equals(s);
		}
		return res;
	}

	/**
	 * Dados dos listas de tipos, compara si coinciden
	 * @param tipos1 primera lista
	 * @param tipos2 segunda lista
	 * @return true si son del mismo tipo, false en caso contrario
	 */
	private boolean compararTipos(String[] tipos1, String[] tipos2) {
		if (tipos1.length != tipos2.length) {
			return false;
		}
		for (int i = 0; i < tipos1.length; i++) {
			if (!tipos1[i].equals(tipos2[i])) return false;
		}
		return true;
	}

	/**
	 * Simplifica 'token' (variable global) a un string mas simple y
	 * actualiza su atributo id.pos (variable global) si es un identificador
	 */
	private void formatearToken() {
		switch (token) {
			case "<tipoBoolean,>" -> token = "boolean";
			case "<tipoFloat,>" -> token = "float";
			case "<for,>" -> token = "for";
			case "<function,>" -> token = "function";
			case "<if,>" -> token = "if";
			case "<tipoInt,>" -> token = "int";
			case "<let,>" -> token = "let";
			case "<read,>" -> token = "read";
			case "<return,>" -> token = "return";
			case "<tipoString,>" -> token = "string";
			case "<void,>" -> token = "void";
			case "<write,>" -> token = "write";
			case "<masIgual,>" -> token = "+=";
			case "<igual,>" -> token = "=";
			case "<coma,>" -> token = ",";
			case "<puntoComa,>" -> token = ";";
			case "<abrirParentesis,>" -> token = "(";
			case "<cerrarParentesis,>" -> token = ")";
			case "<abrirLlave,>" -> token = "{";
			case "<cerrarLlave,>" -> token = "}";
			case "<modulo,>" -> token = "%";
			case "<negacion,>" -> token = "!";
			case "<menorQue,>" -> token = "<";
            case "<eof,>" -> token = "$";
			default -> {
				if (token.contains("numReal"))
					token = "numReal";
				else if (token.contains("numEntero"))
					token = "numEntero";
				else if (token.contains("cadena"))
					token = "cadena";
				else if (token.contains("ID"))
				{
					int coma = token.indexOf(',');
					int fin = token.indexOf('>');
					id_pos = Integer.parseInt(token.substring(coma+1, fin));
					token = "ID";
				}
			}
		}
	}

    private void errorSintactico(String esperado) {
        System.out.println("[Error sintáctico] línea " + aLex.getLinea());
        System.out.println("Se esperaba: " + esperado);
        System.out.println("Se encontró: " + token);

        System.out.println("------------------------------------------------------------------------------");
		throw new ExcepcionSintacticoSemantico("Análisis abortado por error sintáctico" + 
							"\n------------------------------------------------------------------------------");
    }

	private void errorSemantico(String motivo) {
		System.out.println("[Error semántico] línea " + aLex.getLinea());
		System.out.println("Motivo: " + motivo);

		System.out.println("------------------------------------------------------------------------------");
		throw new ExcepcionSintacticoSemantico("Análisis abortado por error semántico" + 
							"\n------------------------------------------------------------------------------");
	}

	private void errorSemantico(String motivo, int linea, String leyendo) {
		System.out.println("[Error semántico] línea " + linea);
		System.out.println("Leyendo: " + leyendo);
		System.out.println("Motivo: " + motivo);

		System.out.println("------------------------------------------------------------------------------");
		throw new ExcepcionSintacticoSemantico("Análisis abortado por error semántico" + 
							"\n------------------------------------------------------------------------------");
	}

	public void cerrarRecursos() throws IOException {
		bwParse.close();
	}

    /**********************************************************************************************************************************/

	// Sintetizado: E.tipo
	private String E() throws IOException {
		bwParse.write(" 1");
		String r = R();	// R.tipo
		String ret = E_p(r);	// E.tipo

		return ret;
	}

	// Sintetizado: E_p.tipo
	// Heredado: E_p.tipoIzq
	private String E_p(String tipoIzq) throws IOException {
		String ret = null;

		if (token.equals("<")) {
			bwParse.write(" 2");
			InfoToken it = equipara("<");
			String r = R();	// R.tipo

			if ( (!tipoIzq.equals(T_ENTERO) && !tipoIzq.equals(T_REAL)) || !r.equals(tipoIzq) ) {
				errorSemantico("tipos de operandos de '<' incorrectos", it.linea, "<");
			} else {
				ret = E_p(T_LOGICO);	// E_p2.tipo
			}
		} else if (compararTokens(token, new String[] {")", ",", ";"})) {
			bwParse.write(" 3");
			ret = tipoIzq;
		} else {
            errorSintactico("'<' ')' ',' ';'");
        }
		
		return ret;
	}

	// Sintetizado: R.tipo
	private String R() throws IOException {
		bwParse.write(" 4");
		String u = U();	// U.tipo
		String r_p = R_p();	// R_p.tipo

		if (r_p.equals(T_VACIO)) {	// Si no hay operacion de modulo
			return u;
		} else if (!u.equals(T_ENTERO) && !u.equals(T_REAL)) {	// Si el primer operando no es valido
			errorSemantico("primer operando de '%' debe ser entero o real");
		} else if (!u.equals(r_p)) {	// Si el segundo operando no es valido
			errorSemantico("hay operandos de tipos distintos en '%'");
		}

		// Los operandos son validos, tanto u.tipo como r_p.tipo son validos
		return u;
	}

	// Sintetizado: R_p.tipo
	private String R_p() throws IOException {
		String ret = null;

		if (token.equals("%")) {
			bwParse.write(" 5");
			InfoToken it = equipara("%");
			String u = U();	// U.tipo

			if (!u.equals(T_ENTERO) && !u.equals(T_REAL)) {
				errorSemantico("el operando de '%' debe ser entero o real", it.linea, "%");
			}

			String r_p = R_p();	// R_p.tipo

			if (r_p.equals(T_VACIO)) {
				ret = u;
			} else if (!u.equals(r_p)) {
				errorSemantico("hay operandos de tipos distintos en '%'", it.linea, "%");
			} else {
				ret = u;
			}
		} else if (compararTokens(token, new String[] { ")", ",", ";", "<" })) {
			bwParse.write(" 6");
			ret = T_VACIO;
		} else {
            errorSintactico("'%' ')' ',' ';' '<'");
        }

		return ret;
	}

	// Sintetizado: U.tipo
	private String U() throws IOException {
		String ret = null;

		if (token.equals("!")) {
			bwParse.write(" 7");
			InfoToken it = equipara("!");
			String v = V();	// V.tipo

			if (!v.equals(T_LOGICO)) {
				errorSemantico("el operando de '!' debe ser un logico", it.linea, "!");
			} else {
				ret = T_LOGICO;
			}
		} else if (compararTokens(token, new String[]{"(", "cadena", "numEntero", "ID", "numReal"})) {
			bwParse.write(" 8");
			ret = V();	// V.tipo
		} else {
            errorSintactico("una expresión");
        }

		return ret;
	}

	// Sintetizado: V.tipo
	private String V() throws IOException {
		String ret = null;

		if (token.equals("ID")) {
			bwParse.write(" 9");
			InfoToken it = equipara("ID");
			HashMap<String, Object> v_p = V_p();

			String v_p_tipo = (String) v_p.get(TIPO);
			boolean v_p_llamaFunc = (boolean) v_p.get(LLAMAFUNC);
			if (v_p_llamaFunc) {
				String tipo = ts.buscaTipo(it.id_pos);
				if (tipo.equals(T_FUNCION)) {
					String[] args = v_p_tipo.equals(T_VACIO) ? new String[0] : v_p_tipo.split(" x ");
					if (compararTipos(args, ts.buscaParam(it.id_pos))) {
						ret = ts.buscaTipoRet(it.id_pos);
					} else {
						errorSemantico("llamada a función con parámetros incorrectos", it.linea, it.lexema);
					}
				} else {
					errorSemantico("se intenta llamar a una variable '" + tipo + "' como si fuera una función", it.linea, it.lexema);
				}
			} else {
				ret = ts.buscaTipo(it.id_pos);
			}
		} else if (token.equals("(")) {
			bwParse.write(" 10");
			equipara("(");
			ret = E();
			equipara(")");
		} else if (token.equals("numEntero")) {
			bwParse.write(" 11");
			equipara("numEntero");
			ret = T_ENTERO;
		} else if (token.equals("cadena")) {
			bwParse.write(" 12");
			equipara("cadena");
			ret = T_CADENA;
		} else if (token.equals("numReal")) {
			bwParse.write(" 13");
			equipara("numReal");
			ret = T_REAL;
		} else {
            errorSintactico("una expresión");
        }

		return ret;
	}

	// Sintetizado: V_p.tipo, V_p.llamaFunc
	private HashMap<String, Object> V_p() throws IOException {
		HashMap<String,Object> ret = new HashMap<>();

		if (token.equals("(")) {
			bwParse.write(" 15");
			equipara("(");
			String tipoL = L();

			ret.put(TIPO, tipoL);
			ret.put(LLAMAFUNC, true);

			equipara(")");
		} else if (compararTokens(token, new String[] { "%", ")", ",", ";", "<" })) {
			bwParse.write(" 14");
			ret.put(TIPO, T_VACIO);
			ret.put(LLAMAFUNC, false);
		} else {
            errorSintactico("un llamado a función o continuación de expresión");
			return null;
        }

		return ret;
	}

	// Sintetizado: S.tipoRet
	// Heredado: S.funcion
	private String S(boolean funcion) throws IOException {
		String ret = null;

		if (token.equals("ID")) {
			bwParse.write(" 16");
			InfoToken it = equipara("ID");
			HashMap<String,Object> s_p = S_p();

			String s_p_tipo = (String) s_p.get(TIPO);
			boolean s_p_llamaFunc = (boolean) s_p.get(LLAMAFUNC);
			if (s_p_llamaFunc) {	// es una llamada a funcion
				String tipo = ts.buscaTipo(it.id_pos);
				if (tipo.equals(T_FUNCION)) {
					String[] args = s_p_tipo.equals(T_VACIO) ? new String[0] : s_p_tipo.split(" x ");
					if (compararTipos(args, ts.buscaParam(it.id_pos))) {
						ret = T_VACIO;
					} else {
						errorSemantico("llamada a función con parámetros incorrectos", it.linea, it.lexema);
					}
				} else {
					errorSemantico("se intenta llamar a una variable '" + tipo + "' como si fuera una función", it.linea, it.lexema);
				}
			} else {	// es una asignacion o asignacion con suma
				if (s_p_tipo.equals(ts.buscaTipo(it.id_pos))) {
					boolean s_p_esSuma = (boolean) s_p.get(ES_SUMA);
					if (s_p_esSuma) {
						// Restriccion de entero/real solo si es '+='
						if (s_p_tipo.equals(T_ENTERO) || s_p_tipo.equals(T_REAL)) {
							ret = T_VACIO;
						} else {
							errorSemantico("el operador '+=' solo admite tipos enteros o reales", it.linea, it.lexema);
						}
					} else {
						// Si es '=', y los tipos coinciden (ya comprobado arriba), es válido.
						ret = T_VACIO;
					}
				} else {
					errorSemantico("tipos incompatibles en asignación", it.linea, it.lexema);
				}
			}
		} else if (token.equals("write")) {
			bwParse.write(" 17");
			InfoToken it = equipara("write");
			String e = E();

			if (e.equals(T_ENTERO) || e.equals(T_REAL) || e.equals(T_CADENA)) {
				ret = T_VACIO;
			} else {
				errorSemantico("la expresión del 'write' debe ser entero, real o cadena", it.linea, "write");
			}

			equipara(";");
		} else if (token.equals("read")) {
			bwParse.write(" 18");
			equipara("read");
			InfoToken it = equipara("ID");

			String tipo = ts.buscaTipo(it.id_pos);
			if (tipo.equals(T_ENTERO) || tipo.equals(T_REAL) || tipo.equals(T_CADENA)) {
				ret = T_VACIO;
			} else {
				errorSemantico("identificador del read debe ser entero, real o cadena", it.linea, "read");
			}

			equipara(";");
		} else if (token.equals("return")) {
			bwParse.write(" 19");
			InfoToken it = equipara("return");

			if (!funcion) errorSemantico("return debe ir dentro de una funcion", it.linea, "return");

			ret = X();
			equipara(";");
		} else {
            errorSintactico("una sentencia válida (ID, write, read, return)");
        }

		return ret;
	}

	// Sintetizado: S_p.tipo, S_p.llamaFunc, S_p.esSuma
	private HashMap<String,Object> S_p() throws IOException {
		HashMap<String,Object> ret = new HashMap<>();
		
		if (token.equals("=")) {
			bwParse.write(" 20");
			equipara("=");
			String e = E();

			ret.put(TIPO, e);
			ret.put(LLAMAFUNC, false);
			ret.put(ES_SUMA, false);

			equipara(";");
		} else if (token.equals("+=")) {
			bwParse.write(" 21");
			equipara("+=");
			String e = E();

			ret.put(TIPO, e);
			ret.put(LLAMAFUNC, false);
			ret.put(ES_SUMA, true);

			equipara(";");
		} else if (token.equals("(")) {
			bwParse.write(" 22");
			equipara("(");
			String l = L();

			ret.put(TIPO, l);
			ret.put(LLAMAFUNC, true);
			ret.put(ES_SUMA, false);

			equipara(")");
			equipara(";");
		} else {
            errorSintactico("una continuación de sentencia (=, +=, llamada a función)");
        }

		return ret;
	}

	// Sintetizado: L.tipo
	private String L() throws IOException {
		String ret = null;

		if (compararTokens(token, new String[] { "!", "(", "cadena", "numEntero", "ID", "numReal" })) {
			bwParse.write(" 23");
			String e = E();
			String q = Q();

			if (q.equals(T_VACIO)) {
				ret = e;
			} else {
				ret = e + " x " + q; 
			}
		} else if (token.equals(")")) {
			bwParse.write(" 24");
			ret = T_VACIO;
		} else {
            errorSintactico("una expresión");
        }

		return ret;
	}

	// Sintetizado: Q.tipo
	private String Q() throws IOException {
		String ret = null;
		
		if (token.equals(",")) {
			bwParse.write(" 25");
			equipara(",");
			String e = E();
			String q = Q();

			if (q.equals(T_VACIO)) {
				ret = e;
			} else {
				ret = e + " x " + q;
			}
		} else if (token.equals(")")) {
			bwParse.write(" 26");
			ret = T_VACIO;
		} else {
            errorSintactico("una expresión o ')'");
        }

		return ret;
	}

	// Sintetizado: X.tipo
	private String X() throws IOException {
		String ret = null;

		if (compararTokens(token, new String[] { "!", "(", "cadena", "numEntero", "ID", "numReal" })) {
			bwParse.write(" 27");
			ret = E();
		} else if (compararTokens(token, new String[] { ")", ";" })) {
			bwParse.write(" 28");
			ret = T_VACIO;
		} else {
            errorSintactico("una expresión o ';'");
        }

		return ret;
	}

	// Sintetizado: B.tipoRet
	// Heredado: B.function
	private String B(boolean function) throws IOException {
		String b1 = null;

		if (token.equals("if")) {
			bwParse.write(" 29");
			equipara("if");
			equipara("(");

			int lineaError = aLex.getLinea();
			String lexemaError = aLex.getLexema().isEmpty() ? token : aLex.getLexema();

			String tE = E();
			
			if (!tE.equals(T_LOGICO)) {
				errorSemantico("la condición del if debe ser de tipo lógico", lineaError, lexemaError);
			}

			equipara(")");
			b1 = S(function);
		} else if (token.equals("let")) {
			bwParse.write(" 30");
			equipara("let");

			ts.zonaDecl = true;
			
			HashMap<String, Object> t = T();
			InfoToken it = equipara("ID");

			ts.zonaDecl = false;
			ts.insertaAtributosVariable(it.id_pos, (String)t.get(TIPO), (int)t.get(TAMANO));

			equipara(";");

			b1 = T_VACIO;
		} else if (compararTokens(token, new String[] { "ID", "read", "return", "write" })) {
			bwParse.write(" 31");
			b1 = S(function);
		} else if (token.equals("for")) {
			bwParse.write(" 32");
			equipara("for");
			equipara("(");
			Y();
			equipara(";");

			int lineaError = aLex.getLinea();
    		String lexemaError = aLex.getLexema().isEmpty() ? token : aLex.getLexema();

			String tE = E();

			if (!tE.equals(T_LOGICO)) {
				errorSemantico("la condición del for debe ser de tipo lógico", lineaError, lexemaError);
			}
			equipara(";");
			Y();
			equipara(")");
			equipara("{");
			b1 = C(function);
			equipara("}");
		} else {
			errorSintactico("una sentencia válida (if, let, ID, read, return, write, for)");
		}

		return b1;
	}

	private void Y() throws IOException {
		if (token.equals("ID")) {
			bwParse.write(" 33");
			W();
		} else { // Y no tiene follow
			bwParse.write(" 34");
        }
	}

	// Sintetizado: T.tipo, T.tamaño
	private HashMap<String, Object> T() throws IOException {
		HashMap<String, Object> ret = new HashMap<>();

		if (token.equals("int")) {
			bwParse.write(" 35");
			equipara("int");
			ret.put(TIPO, T_ENTERO);
			ret.put(TAMANO, 1);
		} else if (token.equals("float")) {
			bwParse.write(" 36");
			equipara("float");
			ret.put(TIPO, T_REAL);
			ret.put(TAMANO, 2);
		} else if (token.equals("boolean")) {
			bwParse.write(" 37");
			equipara("boolean");
			ret.put(TIPO, T_LOGICO);
			ret.put(TAMANO, 1);
		} else if (token.equals("string")) {
			bwParse.write(" 38");
			equipara("string");
			ret.put(TIPO, T_CADENA);
			ret.put(TAMANO, 64);
		} else {
			errorSintactico("un tipo válido (int, float, boolean, string)");
		}

		return ret;
	}

	private void W() throws IOException {
		if (token.equals("ID")) {
			bwParse.write(" 39");
			InfoToken it = equipara("ID");

			HashMap<String,Object> w_p = W_p();
			boolean w_p_esSuma = (boolean) w_p.get(ES_SUMA);
			String w_p_tipo = (String) w_p.get(TIPO);
			
			if (w_p_tipo.equals(ts.buscaTipo(it.id_pos))) {
				if (w_p_esSuma && !w_p_tipo.equals(T_ENTERO) && !w_p_tipo.equals(T_REAL)) {
					errorSemantico("el operador '+=' solo admite tipos enteros o reales", it.linea, it.lexema);
				}
			} else {
				errorSemantico("tipos incompatibles en asignación", it.linea, it.lexema);
			}
		} else {
			errorSintactico("un identificador");
		}
	}

	// Sintetizado: W_p.tipo, W_p.esSuma
	private HashMap<String,Object> W_p() throws IOException {
		HashMap<String,Object> ret = new HashMap<>();

		if (token.equals("=")) {
			bwParse.write(" 40");
			equipara("=");
			String e = E();

			ret.put(TIPO, e);
			ret.put(ES_SUMA, false);
		} else if (token.equals("+=")) {
			bwParse.write(" 41");
			equipara("+=");
			String e = E();

			ret.put(TIPO, e);
			ret.put(ES_SUMA, true);
		} else {
			errorSintactico("una asignación (=, +=)");
		}

		return ret;
	}

	private void F() throws IOException {
		if (token.equals("function")) {
			bwParse.write(" 42");
			equipara("function");

			ts.zonaDecl = true;

			String hTipo = H();
			InfoToken it = equipara("ID");

			ts.createTSLocal();

			equipara("(");
			HashMap<String, Object> a = A();

			ts.zonaDecl = false;

			String aTipo = (String) a.get(TIPO);
			String[] params = aTipo.equals(T_VACIO) ? new String[0] : aTipo.split(" x ");
			ts.insertaAtributosFuncion(it.id_pos, (Integer)a.get(N), params, hTipo);

			equipara(")");
			equipara("{");
			String c = C(true);

			if (!hTipo.equals(c)) {
				errorSemantico("tipo de retorno incorrecto", it.linea, it.lexema);
			}

			ts.write(-1);
			ts.destroyTSLocal();

			equipara("}");
		} else {
			errorSintactico("declaración de función (function)");
		}
	}

	// Sintetizado: H.tipo
	private String H() throws IOException {
		String ret = null;

		if (compararTokens(token, new String[] { "boolean", "float", "int", "string" })) {
			bwParse.write(" 43");
			HashMap<String, Object> t = T();
			ret = (String) t.get(TIPO);
		} else if (token.equals("void")) {
			bwParse.write(" 44");
			equipara("void");
			ret = T_VACIO;
		} else {
			errorSintactico("tipo de retorno de función (boolean, float, int, string, void)");
		}

		return ret;
	}

	// Sintetizado: A.n, A.tipo
	private HashMap<String, Object> A() throws IOException {
		HashMap<String, Object> a1 = new HashMap<>();

		if (compararTokens(token, new String[] { "boolean", "float", "int", "string" })) {
			bwParse.write(" 45");
			HashMap<String, Object> t = T();
			InfoToken it = equipara("ID");

			ts.insertaAtributosVariable(it.id_pos, (String) t.get(TIPO), (int) t.get(TAMANO));

			HashMap<String, Object> k = K();

			a1.put(N, (Integer) k.get(N) + 1);
			String tTipo = (String) t.get(TIPO);
			String kTipo = (String) k.get(TIPO);
			if (kTipo.equals(T_VACIO)) {
				a1.put(TIPO, tTipo);
			} else {
				a1.put(TIPO, tTipo + " x " + kTipo);
			}
		} else if (token.equals("void")) {
			bwParse.write(" 46");
			equipara("void");
			a1.put(TIPO, T_VACIO);
			a1.put(N, 0);
		} else {
			errorSintactico("lista de parámetros de función o void");
		}

		return a1;
	}

	// Sintetizado: K.n, K.tipo
	private HashMap<String, Object> K() throws IOException {
		HashMap<String, Object> k1 = new HashMap<>();

		if (token.equals(",")) {
			bwParse.write(" 47");
			equipara(",");
			HashMap<String, Object> t = T();
			InfoToken it = equipara("ID");

			ts.insertaAtributosVariable(it.id_pos, (String)t.get(TIPO), (int)t.get(TAMANO));

			HashMap<String, Object> k2 = K();

			k1.put(N, (Integer) k2.get(N) + 1);
			String tTipo  = (String) t.get(TIPO);
			String k2Tipo = (String) k2.get(TIPO);
			if (k2Tipo.equals(T_VACIO)) {
				k1.put(TIPO, tTipo);
			} else {
				k1.put(TIPO, tTipo + " x " + k2Tipo);
			}
		} else if (token.equals(")")) {
			bwParse.write(" 48");
			k1.put(N, 0);
			k1.put(TIPO, T_VACIO);
		} else {
			errorSintactico("lista de parámetros de función o ')'");
		}

		return k1;
	}

	// Sintetizado: C.tipoRet
	// Heredado: C.function
	private String C(boolean function) throws IOException {
		String c1 = null;

		if (compararTokens(token, new String[] { "for", "ID", "if", "let", "read", "return", "write" })) {
			bwParse.write(" 49");

			String b = B(function);
			String c2 = C(function);
			if (b.equals(T_VACIO)) {
				c1 = c2;
			} else if (c2.equals(T_VACIO)) {
				c1 = b;
			} else {
				// Ambos tienen return
				if (b.equals(c2)) {
					c1 = b;
				} else {
					errorSemantico("tipos de retorno incompatibles en la misma función");
				}
			}
		} else if (token.equals("}")) {
			bwParse.write(" 50");
			c1 = T_VACIO;
		} else {
			errorSintactico("cuerpo de función o '}'");
		}

		return c1;
	}

	private void P() throws IOException {
		if (compararTokens(token, new String[] {"for", "ID", "if", "let", "read", "return", "write" })) {
			bwParse.write(" 51");
			B(false);
			P();
		} else if (token.equals("function")) {
			bwParse.write(" 52");
			F();
			P();
		} else if (token.equals("$")) {
			bwParse.write(" 53");
		} else {
            errorSintactico("inicio/fin de programa válido");
        }
	}

	/**********************************************************************************************************************************/
}