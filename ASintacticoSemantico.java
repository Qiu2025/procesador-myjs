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

	// Analizador semantico
	private int despG;	// desplazamiento global
	private int despL;	// desplazamiento local
	private boolean zonaDecl;	// zona declaracion

	public static final String T_ENTERO   = "entero";
	public static final String T_REAL     = "real";
	public static final String T_LOGICO   = "logico";
	public static final String T_CADENA   = "cadena";
	public static final String T_VACIO    = "vacio";
	public static final String T_FUNCION  = "funcion";

	// Atributos de TS
	public static final String ATR_DESPL        = "Despl";
	public static final String ATR_NUM_PARAM    = "NumParam";
	public static final String ATR_TIPO_PARAM   = "TipoParam";
	public static final String ATR_MODO_PARAM   = "ModoParam";
	public static final String ATR_TIPO_RETORNO = "TipoRetorno";
	public static final String ATR_ETIQ_FUNCION = "EtiqFuncion";
	public static final String ATR_PARAM        = "Param";

	// ===== Claves de atributos semánticos (no terminales) =====
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
	public static final String T_ERROR    = "tipo_error";

    public ASintacticoSemantico(String rutaEntrada, String rutaTokens, String rutaParse, TablaSimbolos ts) throws IOException {
		this.ts = ts;
        aLex = new ALexico(rutaEntrada, rutaTokens, ts);
        bwParse = new BufferedWriter(new FileWriter(rutaParse));
		bwParse.write("descendente");
    }
    
	public void start() throws IOException {
		ts.createTSGlobal();
		despG = 0;
		// TSL = false;
		despL = 0;
		zonaDecl = false;


		token = aLex.nextToken();
        formatearToken();
		P();
		if (!token.equals("$")) {
			System.out.println("[Error sintáctico] línea " + aLex.getLinea());
			System.out.println("Fin de archivo no encontrado");
			System.out.println("Se esperaba: $");
			System.out.println("Se encontró: " + token);
		}

		ts.destroyAll();

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

	private boolean compararTokens(String token, String[] tokensComparar) {
		boolean res = false;
		for (String s : tokensComparar) {
			if (res) break;
			res = res || token.equals(s);
		}
		return res;
	}

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

	private void errorSemantico(String mensaje) {
		System.out.println("[Error semántico] línea " + aLex.getLinea());
		System.out.println(mensaje);

		System.out.println("------------------------------------------------------------------------------");
		throw new MiExcepcion("Análisis abortado por error semántico" + 
							"\n------------------------------------------------------------------------------");
	}

	private void cerrarRecursos() throws IOException {
		bwParse.close();
	}

    /**********************************************************************************************************************************/

	// E → R { E_p.tipoIzq := R.tipo } E_p { E.tipo := E_p.tipo }
	private HashMap<String,Object> E() throws IOException {
		bwParse.write(" 1");

		HashMap<String,Object> r = R();
		return E_p((String) r.get(TIPO));
	}

	private HashMap<String,Object> E_p(String tipoIzq) throws IOException {
		HashMap<String, Object> ep1 = new HashMap<>();
		if (token.equals("<")) {
			bwParse.write(" 2");
			equipara("<");
			R();
			E_p(null);
		} else if (compararTokens(token, new String[] {")", ",", ";"})) {
			bwParse.write(" 3");
		} else {
            errorSintactico("'<' ')' ',' ';'");
        }
		return ep1;
	}

	private HashMap<String, Object> R() throws IOException {
		HashMap<String,Object> r1 = new HashMap<>();
		bwParse.write(" 4");
		U();
		R_p();
		return r1;
	}

	private void R_p() throws IOException {
		if (token.equals("%")) {
			bwParse.write(" 5");
			equipara("%");
			U();
			R_p();
		} else if (compararTokens(token, new String[] { ")", ",", ";", "<" })) {
			bwParse.write(" 6");
		} else {
            errorSintactico("'%' ')' ',' ';' '<'");
        }
	}

	private void U() throws IOException {
		if (token.equals("!")) {
			bwParse.write(" 7");
			equipara("!");
			V();
		} else if (compararTokens(token, new String[]{"(", "cadena", "numEntero", "ID", "numReal"})) {
			bwParse.write(" 8");
			V();
		} else {
            errorSintactico("una expresión");
        }
	}

	private void V() throws IOException {
		if (token.equals("ID")) {
			bwParse.write(" 9");
			equipara("ID");
			V_p();
		} else if (token.equals("(")) {
			bwParse.write(" 10");
			equipara("(");
			E();
			equipara(")");
		} else if (token.equals("numEntero")) {
			bwParse.write(" 11");
			equipara("numEntero");
		} else if (token.equals("cadena")) {
			bwParse.write(" 12");
			equipara("cadena");
		} else if (token.equals("numReal")) {
			bwParse.write(" 13");
			equipara("numReal");
		} else {
            errorSintactico("una expresión");
        }
	}

	private void V_p() throws IOException {
		if (token.equals("(")) {
			bwParse.write(" 15");
			equipara("(");
			L();
			equipara(")");
		} else if (compararTokens(token, new String[] { "%", ")", ",", ";", "<" })) {
			bwParse.write(" 14");
		} else {
            errorSintactico("un llamado a función o continuación de expresión");
        }
	}

	private void S() throws IOException {
		if (token.equals("ID")) {
			bwParse.write(" 16");
			equipara("ID");
			S_p();
		} else if (token.equals("write")) {
			bwParse.write(" 17");
			equipara("write");
			E();
			equipara(";");
		} else if (token.equals("read")) {
			bwParse.write(" 18");
			equipara("read");
			equipara("ID");
			equipara(";");
		} else if (token.equals("return")) {
			bwParse.write(" 19");
			equipara("return");
			X();
			equipara(";");
		} else {
            errorSintactico("una sentencia válida (ID, write, read, return)");
        }
	}

	private void S_p() throws IOException {
		if (token.equals("=")) {
			bwParse.write(" 20");
			equipara("=");
			E();
			equipara(";");
		} else if (token.equals("+=")) {
			bwParse.write(" 21");
			equipara("+=");
			E();
			equipara(";");
		} else if (token.equals("(")) {
			bwParse.write(" 22");
			equipara("(");
			L();
			equipara(")");
			equipara(";");
		} else {
            errorSintactico("una continuación de sentencia (=, +=, llamada a función)");
        }
	}

	private void L() throws IOException {
		if (compararTokens(token, new String[] { "!", "(", "cadena", "numEntero", "ID", "numReal" })) {
			bwParse.write(" 23");
			E();
			Q();
		} else if (token.equals(")")) {
			bwParse.write(" 24");
		} else {
            errorSintactico("una expresión");
        }
	}

	private void Q() throws IOException {
		if (token.equals(",")) {
			bwParse.write(" 25");
			equipara(",");
			E();
			Q();
		} else if (token.equals(")")) {
			bwParse.write(" 26");
		} else {
            errorSintactico("una expresión o ')'");
        }
	}

	private void X() throws IOException {
		if (compararTokens(token, new String[] { "!", "(", "cadena", "numEntero", "ID", "numReal" })) {
			bwParse.write(" 27");
			E();
		} else if (compararTokens(token, new String[] { ")", ";" })) {
			bwParse.write(" 28");
		} else {
            errorSintactico("una expresión o ';'");
        }
	}

	private HashMap<String, Object> B() throws IOException {
		HashMap<String,Object> b1 = new HashMap<>();
		if (token.equals("if")) {
			bwParse.write(" 29");
			equipara("if");
			equipara("(");
			E();
			equipara(")");
			S();
		} else if (token.equals("let")) {
			bwParse.write(" 30");
			equipara("let");
			T();
			equipara("ID");
			equipara(";");
		} else if (compararTokens(token, new String[] { "ID", "read", "return", "write" })) {
			bwParse.write(" 31");
			S();
		} else if (token.equals("for")) {
			bwParse.write(" 32");
			equipara("for");
			equipara("(");
			Y();
			equipara(";");
			E();
			equipara(";");
			Y();
			equipara(")");
			equipara("{");
			C();
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
            // lanzarError("una asignación o declaración de variable en el for");
        }
	}

	private HashMap<String, Object> T() throws IOException {
		HashMap<String,Object> at = new HashMap<>();
		if (token.equals("int")) {
			bwParse.write(" 35");
			equipara("int");
		} else if (token.equals("float")) {
			bwParse.write(" 36");
			equipara("float");
		} else if (token.equals("boolean")) {
			bwParse.write(" 37");
			equipara("boolean");
		} else if (token.equals("string")) {
			bwParse.write(" 38");
			equipara("string");
		} else {
            errorSintactico("un tipo válido (int, float, boolean, string)");
        }
		return at;
	}

	private void W() throws IOException {
		if (token.equals("ID")) {
			bwParse.write(" 39");
			equipara("ID");
			W_p();
		} else {
            errorSintactico("un identificador");
        }
	}

	private void W_p() throws IOException {
		if (token.equals("=")) {
			bwParse.write(" 40");
			equipara("=");
			E();
		} else if (token.equals("+=")) {
			bwParse.write(" 41");
			equipara("+=");
			E();
		} else {
            errorSintactico("una asignación (=, +=)");
        }
	}

	private void F() throws IOException {
		if (token.equals("function")) {
			bwParse.write(" 42");
			equipara("function");
			H();

			zonaDecl = true;
			int idFunc = id_pos;

			equipara("ID");

			ts.createTSLocal();
			despL = 0;

			equipara("(");
			A();

			zonaDecl = false;
			ts.setTipo(idFunc,T_FUNCION);
			
			

			equipara(")");
			equipara("{");
			C();
			equipara("}");
		} else {
            errorSintactico("declaración de función (function)");
        }
	}

	private void H() throws IOException {
		if (compararTokens(token, new String[] { "boolean", "float", "int", "string" })) {
			bwParse.write(" 43");
			T();
		} else if (token.equals("void")) {
			bwParse.write(" 44");
			equipara("void");
		} else {
            errorSintactico("tipo de retorno de función (boolean, float, int, string, void)");
        }
	}

	private void A() throws IOException {
		if (compararTokens(token, new String[] { "boolean", "float", "int", "string" })) {
			bwParse.write(" 45");
			T();
			equipara("ID");
			K();
		} else if (token.equals("void")) {
			bwParse.write(" 46");
			equipara("void");
		} else {
            errorSintactico("lista de parámetros de función");
        }
	}

	private HashMap<String, Object> K() throws IOException {
		HashMap<String,Object> k1 = new HashMap<>();
		if (compararTokens(token, new String[] { "," })) {
			bwParse.write(" 47");
            equipara(",");
			HashMap<String, Object> t = T();
			int id = id_pos;
			equipara("ID");
			HashMap<String, Object> k2 = K();
			ts.setTipo(id, (String)t.get(TIPO));
			ts.setValorAtributoEnt(id, ATR_DESPL, ts.existeTSL ? despL : despG);
			if(ts.existeTSL) despL+=(Integer)t.get(TAMANO); else despG+=(Integer)t.get(TAMANO);
			k1.put(N, (Integer)k2.get(N)+1);
			List<String> tipos = Arrays.asList((String[])k2.get(TIPO));
			tipos.addFirst((String)t.get(TIPO));
			k1.put(TIPO, tipos.toArray());
		} else if (token.equals(")")) {
			bwParse.write(" 48");
			k1.put(N, 0);
		} else {
            errorSintactico("lista de parámetros de función o ')'");
        }
		return k1;
	}

	private HashMap<String,Object> C() throws IOException {
		HashMap<String,Object> at = new HashMap<>();
		if (compararTokens(token, new String[] { "for", "ID", "if", "let", "read", "return", "write" })) {
			bwParse.write(" 49");
			HashMap<String, Object> b = B();
			HashMap<String, Object> c = C();

			if(b.get(TIPO).equals(c.get(TIPO)) && b.get(TIPO).equals(T_OK)){
				at.put(TIPO, T_OK);
			}else{
				at.put(TIPO, T_ERROR);
			}

			if(b.get(TIPO_RET).equals(T_VACIO)){
				at.put(TIPO_RET, c.get(TIPO_RET));
			}else{
				if(c.get(TIPO_RET).equals(T_VACIO)){
					at.put(TIPO_RET, b.get(TIPO_RET));
				}else{
					errorSemantico("múltiples sentencias return en el mismo bloque");
				}
			}
		} else if (token.equals("}")) {
			bwParse.write(" 50");
			at.put(TIPO, T_OK);
			at.put(TIPO_RET, T_VACIO);
		} else {
            errorSintactico("cuerpo de función o '}'");
        }
		return at;
	}

	private void P() throws IOException {
		if (compararTokens(token, new String[] {"for", "ID", "if", "let", "read", "return", "write" })) {
			bwParse.write(" 51");
			B();
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