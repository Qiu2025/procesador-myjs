import java.io.*;
import java.util.*;

public class ASintacticoSemantico {
    
	// Tabla de simbolos
	private TablaSimbolos ts;

	// Analizador sintactico
    private String token; // token devuelto por el lexico
	private int id_pos;	// atributo del token cuando es un ID
	
    private ALexico aLex;
    private BufferedWriter bwParse;

	public static final String T_ENTERO   = "entero";
	public static final String T_REAL     = "real";
	public static final String T_LOGICO   = "logico";
	public static final String T_CADENA   = "cadena";
	public static final String T_VACIO    = "vacio";
	public static final String T_FUNCION  = "funcion";

	// Claves de atributos semánticos (no terminales)
	public static final String TIPO        = "tipo";
	public static final String TIPO_IZQ    = "tipoIzq";
	public static final String TIPO_RET    = "tipoRet";
	public static final String N           = "n";
	public static final String LLAMAFUNC   = "llamaFunc";
	public static final String VALOR       = "valor";
	public static final String TAMANO      = "tamano";    // tamaño de T
	public static final String FUNCTION    = "function";  // flag C.function / S.function

	// Tipos auxiliares de comprobación (sentencias/bloques)
	public static final String T_OK       = "tipo_ok";

    public ASintacticoSemantico(String rutaEntrada, String rutaTokens, String rutaParse, TablaSimbolos ts) throws IOException {
		this.ts = ts;
        aLex = new ALexico(rutaEntrada, rutaTokens, ts);
        bwParse = new BufferedWriter(new FileWriter(rutaParse));
		bwParse.write("descendente");
    }
    
	public void start() throws IOException {
		token = aLex.nextToken();
        formatearToken();
		P();
		if (!token.equals("$")) {
			errorSintactico("fin de archivo '$'");
		}

        aLex.cerrarRecursos();
		cerrarRecursos();
	}

    /**********************************************************************************************************************************/

	private void equipara(String tokenEsperado) throws IOException {
		if (token.equals(tokenEsperado)) {
			token = aLex.nextToken();
			formatearToken();
		} else {
			errorSintactico(tokenEsperado);
		}
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
		throw new MiExcepcion("Análisis abortado por error sintáctico" + 
							"\n------------------------------------------------------------------------------");
    }

	private void errorSemantico(String motivo) {
		System.out.println("[Error semántico] línea " + aLex.getLinea());
		System.out.println("Motivo: " + motivo);

		System.out.println("------------------------------------------------------------------------------");
		throw new MiExcepcion("Análisis abortado por error semántico" + 
							"\n------------------------------------------------------------------------------");
	}

	private void cerrarRecursos() throws IOException {
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
			equipara("<");
			String r = R();	// R.tipo

			if ( (!tipoIzq.equals(T_ENTERO) && !tipoIzq.equals(T_REAL)) || !r.equals(tipoIzq) ) {
				errorSemantico("tipos de operandos de '<' incorrectos");
			} else {
				String e_p = E_p(T_LOGICO);	// E_p2.tipo
				ret = e_p;
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
			equipara("%");
			String u = U();	// U.tipo
			String r_p = R_p();	// R_p.tipo

			if (!u.equals(T_ENTERO) && !u.equals(T_REAL)) {
				errorSemantico("el operando de '%' debe ser entero o real");
			} else if (r_p.equals(T_VACIO)) {
				ret = u;
			} else if (!u.equals(r_p)) {
				errorSemantico("hay operandos de tipos distintos en '%'");
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
			equipara("!");
			String v = V();	// V.tipo

			if (!v.equals(T_LOGICO)) {
				errorSemantico("el operando de '!' debe ser un logico");
			} else {
				ret = T_LOGICO;
			}
		} else if (compararTokens(token, new String[]{"(", "cadena", "numEntero", "ID", "numReal"})) {
			bwParse.write(" 8");
			String v = V();	// V.tipo
			ret = v;
		} else {
            errorSintactico("una expresión");
        }

		return ret;
	}

	// Sintetizado: V.tipo
	private String V() throws IOException {
		String ret = null;

		if (token.equals("ID")) {
			int copy_id_pos = id_pos;	// guardar porque 'equipara' puede cambiar id_pos

			bwParse.write(" 9");
			equipara("ID");
			HashMap<String, Object> v_p = V_p();

			boolean v_p_llamaFunc = (boolean) v_p.get(LLAMAFUNC);	// v_p.llamaFunc
			if (v_p_llamaFunc) {
				if (ts.buscaTipo(copy_id_pos).equals(T_FUNCION)) {
					String v_p_tipo = (String) v_p.get(TIPO);
					String[] args = v_p_tipo.equals(T_VACIO) ? new String[0] : v_p_tipo.split(" x ");
					if (compararTipos(args, ts.buscaParam(copy_id_pos))) {
						ret = ts.buscaTipoRet(copy_id_pos);
					} else {
						errorSemantico("llamada a función con parámetros incorrectos");
					}
				} else {
					errorSemantico("función no declarada");
				}
			} else {
				ret = ts.buscaTipo(copy_id_pos);
			}
		} else if (token.equals("(")) {
			bwParse.write(" 10");
			equipara("(");
			String e = E();
			equipara(")");

			ret = e;
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
			equipara(")");

			ret.put(TIPO, tipoL);
			ret.put(LLAMAFUNC, true);
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

	// Sintetizado: S.tipo, S.tipoRet
	// Heredado: S.funcion
	private HashMap<String,Object> S(boolean funcion) throws IOException {
		HashMap<String,Object> ret = new HashMap<>();

		if (token.equals("ID")) {
			int copy_id_pos = id_pos;	// guardar porque 'equipara' puede cambiar id_pos

			bwParse.write(" 16");
			equipara("ID");
			HashMap<String,Object> s_p = S_p();

			String s_p_tipo = (String) s_p.get(TIPO);
			boolean s_p_llamaFunc = (boolean) s_p.get(LLAMAFUNC);
			if (s_p_llamaFunc) {	// es una llamada a funcion
				
				String tipo = ts.buscaTipo(copy_id_pos);
				if (tipo.equals(T_FUNCION)) {
					String[] args = s_p_tipo.equals(T_VACIO) ? new String[0] : s_p_tipo.split(" x ");
					if (compararTipos(args, ts.buscaParam(copy_id_pos))) {
						ret.put(TIPO, T_OK);
						ret.put(TIPO_RET, T_VACIO);
					} else {
						errorSemantico("llamada a función con parámetros incorrectos");
					}
				} else {
					errorSemantico("se intenta llamar a una variable '" + tipo + "' como si fuera una función");
				}

			} else {	// es una asignacion o asignacion con suma
				if (s_p_tipo != null && s_p_tipo.equals(ts.buscaTipo(copy_id_pos))) {
					ret.put(TIPO, T_OK);
					ret.put(TIPO_RET, T_VACIO);
				} else {
					errorSemantico("tipos incompatibles en asignación");
				}
			}
		} else if (token.equals("write")) {
			bwParse.write(" 17");
			equipara("write");
			String e = E();
			equipara(";");

			if (e.equals(T_ENTERO) || e.equals(T_REAL) || e.equals(T_CADENA)) {
				ret.put(TIPO, T_OK);
			} else {
				errorSemantico("la expresión de 'write' debe ser entero, real o cadena");
			}
			ret.put(TIPO_RET, T_VACIO);
		} else if (token.equals("read")) {
			bwParse.write(" 18");
			equipara("read");
			int copy_id_pos = id_pos;
			equipara("ID");
			equipara(";");

			String tipo = ts.buscaTipo(copy_id_pos);
			if (tipo != null && !tipo.equals(T_ENTERO) && !tipo.equals(T_REAL) && !tipo.equals(T_CADENA)) {
				errorSemantico("identificador del read debe ser entero, real o cadena");
			} else {
				ret.put(TIPO, T_OK);
				ret.put(TIPO_RET, T_VACIO);
			}
		} else if (token.equals("return")) {
			bwParse.write(" 19");
			equipara("return");
			String x = X();
			equipara(";");

			if (!funcion) errorSemantico("return debe ir dentro de una funcion");
			ret.put(TIPO, T_OK);
			ret.put(TIPO_RET, x);
		} else {
            errorSintactico("una sentencia válida (ID, write, read, return)");
        }

		return ret;
	}

	// Sintetizado: S_p.tipo, S_p.llamaFunc
	private HashMap<String,Object> S_p() throws IOException {

		HashMap<String,Object> ret = new HashMap<>();
		
		if (token.equals("=")) {
			bwParse.write(" 20");
			equipara("=");
			String e = E();
			equipara(";");
			
			ret.put(TIPO, e);
			ret.put(LLAMAFUNC, false);
			
		} else if (token.equals("+=")) {
			bwParse.write(" 21");
			equipara("+=");
			String e = E();
			equipara(";");
			
			ret.put(TIPO, e);
			ret.put(LLAMAFUNC, false);
			
		} else if (token.equals("(")) {
			bwParse.write(" 22");
			equipara("(");
			String l = L();
			equipara(")");
			equipara(";");
			
			ret.put(TIPO, l);
			ret.put(LLAMAFUNC, true);
			
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
			String q = Q();	// Q.tipo

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
		
		String tipo = null;
		if (compararTokens(token, new String[] { "!", "(", "cadena", "numEntero", "ID", "numReal" })) {
			bwParse.write(" 27");
			tipo = E();
		} else if (compararTokens(token, new String[] { ")", ";" })) {
			bwParse.write(" 28");
			tipo = T_VACIO;
		} else {
            errorSintactico("una expresión o ';'");
        }

		return tipo;
	}

	// Sintetizado: B.tipo, B.tipoRet
	// Heredado: B.function
	private HashMap<String, Object> B(boolean function) throws IOException {
		HashMap<String, Object> b1 = new HashMap<>();

		if (token.equals("if")) {
			bwParse.write(" 29");
			equipara("if");
			equipara("(");
			String tE = E();                 // E.tipo
			equipara(")");
			HashMap<String, Object> s = S(function);   // S.tipo, S.tipoRet

			if (!tE.equals(T_LOGICO)) {
				errorSemantico("la condición del if debe ser de tipo lógico");
			}

			b1.put(TIPO, s.get(TIPO));
			b1.put(TIPO_RET, s.get(TIPO_RET));

		} else if (token.equals("let")) {
			bwParse.write(" 30");
			equipara("let");

			ts.zonaDecl = true;
			HashMap<String, Object> t = T();  // T.tipo, T.tamano

			int id = id_pos;
			equipara("ID");

			ts.zonaDecl = false;
			
			equipara(";");

			ts.insertaAtributosVariable(id, (String)t.get(TIPO), (int)t.get(TAMANO));

			b1.put(TIPO, T_OK);
			b1.put(TIPO_RET, T_VACIO);

		} else if (compararTokens(token, new String[] { "ID", "read", "return", "write" })) {
			bwParse.write(" 31");
			HashMap<String, Object> s = S(function);   // S.tipo, S.tipoRet
			b1.put(TIPO, s.get(TIPO));
			b1.put(TIPO_RET, s.get(TIPO_RET));

		} else if (token.equals("for")) {
			bwParse.write(" 32");
			equipara("for");
			equipara("(");

			String y1 = Y();  // Y.tipo
			equipara(";");
			String tE = E();                  // E.tipo

			if (!tE.equals(T_LOGICO)) {
				errorSemantico("la condición del for debe ser de tipo lógico");
			}

			equipara(";");
			String y2 = Y();  // Y.tipo

			equipara(")");
			equipara("{");
			HashMap<String, Object> c = C(function); // C.tipo, C.tipoRet
			equipara("}");

			if (y1.equals(T_OK) && y2.equals(T_OK) && c.get(TIPO).equals(T_OK)) {
				b1.put(TIPO, T_OK);
			}
			b1.put(TIPO_RET, c.get(TIPO_RET));

		} else {
			errorSintactico("una sentencia válida (if, let, ID, read, return, write, for)");
		}

		return b1;
	}

	// Sintetizado: Y.tipo
	private String Y() throws IOException {
		String tipo = null;
		
		if (token.equals("ID")) {
			bwParse.write(" 33");
			tipo = W();
		} else { // Y no tiene follow
			bwParse.write(" 34");
			tipo = T_OK;
            // lanzarError("una asignación o declaración de variable en el for");
        }

		return tipo;
	}

	// Sintetizado: T.tipo, T.tamaño
	private HashMap<String, Object> T() throws IOException {
		HashMap<String, Object> t1 = new HashMap<>();

		if (token.equals("int")) {
			bwParse.write(" 35");
			equipara("int");
			t1.put(TIPO, T_ENTERO);
			t1.put(TAMANO, 1);

		} else if (token.equals("float")) {
			bwParse.write(" 36");
			equipara("float");
			t1.put(TIPO, T_REAL);
			t1.put(TAMANO, 2);

		} else if (token.equals("boolean")) {
			bwParse.write(" 37");
			equipara("boolean");
			t1.put(TIPO, T_LOGICO);
			t1.put(TAMANO, 1);

		} else if (token.equals("string")) {
			bwParse.write(" 38");
			equipara("string");
			t1.put(TIPO, T_CADENA);
			t1.put(TAMANO, 64);

		} else {
			errorSintactico("un tipo válido (int, float, boolean, string)");
		}

		return t1;
	}

	// Sintetizado: W.tipo
	private String W() throws IOException {
		if (token.equals("ID")) {
			bwParse.write(" 39");

			int id = id_pos;   // guardar porque equipara puede cambiar id_pos
			equipara("ID");

			String tipoExp = W_p();     // W_p.tipo
			String tipoId  = ts.buscaTipo(id);

			if (tipoExp.equals(tipoId)) {
				return T_OK;
			} else {
				errorSemantico("tipos incompatibles en asignación");
			}
		}

		errorSintactico("un identificador");
		return null; // inalcanzable, para que no se queje el compilador
	}

	// 
	private String W_p() throws IOException {

		if (token.equals("=")) {
			bwParse.write(" 40");
			equipara("=");
			return E();   // E.tipo
		}

		if (token.equals("+=")) {
			bwParse.write(" 41");
			equipara("+=");
			return E();   // E.tipo
		}

		errorSintactico("una asignación (=, +=)");
		return null; // inalcanzable
	}

	private void F() throws IOException {
		if (token.equals("function")) {
			bwParse.write(" 42");
			equipara("function");

			ts.zonaDecl = true;

			String hTipo = H();
			int idFunc = id_pos;     // pos en TS del identificador de la función
			equipara("ID");

			ts.createTSLocal();

			equipara("(");

			HashMap<String, Object> a = A();	// A.n, A.tipo

			ts.zonaDecl = false;
			
			String aTipo = (String) a.get(TIPO);
			String[] params = aTipo.equals(T_VACIO) ? new String[0] : aTipo.split(" x ");
			ts.insertaAtributosFuncion(idFunc, (Integer)a.get(N), params, hTipo);

			equipara(")");
			equipara("{");

			// { C.function := true}
			HashMap<String, Object> c = C(true);   // C.tipoRet

			equipara("}");

			// if C.tipoRet ≠ H.tipo then error
			if (!hTipo.equals(c.get(TIPO_RET))) {
				errorSemantico("tipo de retorno incorrecto");
			}

			// LiberaTabla(TSL)
			ts.write(-1);
			ts.destroyTSLocal();

		} else {
			errorSintactico("declaración de función (function)");
		}
	}

	// Sintetizado: H.tipo
	private String H() throws IOException {
		// H -> T
		if (compararTokens(token, new String[] { "boolean", "float", "int", "string" })) {
			bwParse.write(" 43");
			HashMap<String, Object> t = T();          // T.tipo, T.tamano
			return (String) t.get(TIPO);              // H.tipo := T.tipo
		}

		// H -> void
		if (token.equals("void")) {
			bwParse.write(" 44");
			equipara("void");
			return T_VACIO;                           // H.tipo := vacio
		}

		errorSintactico("tipo de retorno de función (boolean, float, int, string, void)");
		return null; // inalcanzable
	}

	// Sintetizado: A.n, A.tipo
	private HashMap<String, Object> A() throws IOException {
		HashMap<String, Object> a1 = new HashMap<>();

		if (compararTokens(token, new String[] { "boolean", "float", "int", "string" })) {
			bwParse.write(" 45");

			HashMap<String, Object> t = T();   // T.tipo, T.tamano

			int id = id_pos;                  // atributo del token ID
			equipara("ID");

			HashMap<String, Object> k = K();   // K.n, K.tipo

			ts.insertaAtributosVariable(id, (String) t.get(TIPO), (int) t.get(TAMANO));

			// A.n := K.n + 1
			a1.put(N, (Integer) k.get(N) + 1);

			// A.tipo := T.tipo x K.tipo   (si K.tipo = vacio -> solo T.tipo)
			String tTipo = (String) t.get(TIPO);
			String kTipo = (String) k.get(TIPO);

			if (kTipo.equals(T_VACIO)) {
				a1.put(TIPO, tTipo);
			} else {
				a1.put(TIPO, tTipo + " x " + kTipo);
			}

		// A -> void
		} else if (token.equals("void")) {
			bwParse.write(" 46");
			equipara("void");

			a1.put(N, 0);
			a1.put(TIPO, T_VACIO);

		} else {
			errorSintactico("lista de parámetros de función o void");
		}

		return a1;
	}

	// Sintetizado: K.n, K.tipo
	private HashMap<String, Object> K() throws IOException {
		HashMap<String, Object> k1 = new HashMap<>();

		if (compararTokens(token, new String[] { "," })) {
			bwParse.write(" 47");
			equipara(",");

			HashMap<String, Object> t = T();   // T.tipo, T.tamano

			int id = id_pos;
			equipara("ID");

			HashMap<String, Object> k2 = K();  // K2.n, K2.tipo

			ts.insertaAtributosVariable(id, (String)t.get(TIPO), (int)t.get(TAMANO));

			// K1.n := K2.n + 1
			k1.put(N, (Integer) k2.get(N) + 1);

			// K1.tipo := T.tipo x K2.tipo   (si K2.tipo = vacio -> solo T.tipo)
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

	// Sintetizado: C.tipoRet, C.tipo
	// Heredado: C.function
	private HashMap<String,Object> C(boolean function) throws IOException {
		HashMap<String, Object> c1 = new HashMap<>();

		if (compararTokens(token, new String[] { "for", "ID", "if", "let", "read", "return", "write" })) {
			bwParse.write(" 49");

			HashMap<String, Object> b = B(function);
			HashMap<String, Object> c2 = C(function);

			// C.tipo
			if (b.get(TIPO).equals(c2.get(TIPO)) && b.get(TIPO).equals(T_OK)) {
				c1.put(TIPO, T_OK);
			}

			String retB = (String) b.get(TIPO_RET);
			String retC2 = (String) c2.get(TIPO_RET);

			// C.tipoRet
			if (retB.equals(T_VACIO)) {
				c1.put(TIPO_RET, retC2);
			} else if (retC2.equals(T_VACIO)) {
				c1.put(TIPO_RET, retB);
			} else {
				// Ambos tienen return
				if (retB.equals(retC2)) {
					c1.put(TIPO_RET, retB);
				} else {
					errorSemantico("tipos de retorno incompatibles en el mismo bloque");
				}
			}

		} else if (token.equals("}")) {
			bwParse.write(" 50");
			c1.put(TIPO, T_OK);
			c1.put(TIPO_RET, T_VACIO);

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