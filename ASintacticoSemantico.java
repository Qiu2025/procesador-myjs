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
	public static boolean zonaDecl;

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

	/* REGLAS
		1. E → R { E_p.tipoIzq := R.tipo } E_p { E.tipo := E_p.tipo }
	 */
	// Sintetizado: E.tipo
	private String E() throws IOException {
		bwParse.write(" 1");
		String r = R();	// R.tipo
		String ret = E_p(r);	// E.tipo

		return ret;
	}

	/* REGLAS
		2. E_p1 → < R 
			{
				if (E_p1.tipoIzq ∉ {entero, real}  ||  R.tipo ≠ E_p1.tipoIzq) then
					error("Tipos de operandos de '<' incorrectos")
				else
					E_p2.tipoIzq := lógico
			} 
			E_p2 
			{
				E_p1.tipo := E_p2.tipo
			}

		3. E_p → λ { E_p.tipo := E_p.tipoIzq }
	*/
	// Sintetizado: E_p.tipo
	// Heredado: E_p.tipoIzq
	private String E_p(String tipoIzq) throws IOException {
		String ret = null;

		if (token.equals("<")) {
			bwParse.write(" 2");
			equipara("<");
			String r = R();	// R.tipo

			if ( (!tipoIzq.equals(T_ENTERO) && !tipoIzq.equals(T_REAL)) || !r.equals(tipoIzq) ) {
				errorSemantico("Tipos de operandos de '<' incorrectos");
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

	/* REGLAS
		4. R → U R_p 	
			{
				if R_p.tipo = vacio then
					R.tipo := U.tipo
				else if U.tipo ∉ {entero, real} then
					error("Primer operando de '%' debe ser entero o real")
				else if U.tipo ≠ R_p.tipo then
					error("Hay operandos de tipos distintos en '%'")
				else
					R.tipo := U.tipo
			}
	*/
	// Sintetizado: R.tipo
	private String R() throws IOException {
		bwParse.write(" 4");
		String u = U();	// U.tipo
		String r_p = R_p();	// R_p.tipo

		if (r_p.equals(T_VACIO)) {	// Si no hay operacion de modulo
			return u;
		} else if (!u.equals(T_ENTERO) && !u.equals(T_REAL)) {	// Si el primer operando no es valido
			errorSemantico("Primer operando de '%' debe ser entero o real");
		} else if (!u.equals(r_p)) {	// Si el segundo operando no es valido
			errorSemantico("Hay operandos de tipos distintos en '%'");
		}

		// Los operandos son validos, tanto u.tipo como r_p.tipo son validos
		return u;
	}

	/* REGLAS
		5. R_p1 → % U R_p2 
			{ 
				if U.tipo ∉ {entero, real} then
					error("El operando de '%' debe ser entero o real")
				else if R_p2.tipo = vacio then
					R_p1.tipo = U.tipo
				else if U.tipo ≠ R_p2.tipo then
					error("Hay operandos de tipos distintos en '%'")
				else
					R_p1.tipo := U.tipo
			}
		6. R_p → λ { R_p.tipo := vacio }
	*/
	// Sintetizado: R_p.tipo
	private String R_p() throws IOException {
		String ret = null;

		if (token.equals("%")) {
			bwParse.write(" 5");
			equipara("%");
			String u = U();	// U.tipo
			String r_p = R_p();	// R_p.tipo

			if (!u.equals(T_ENTERO) && !u.equals(T_REAL)) {
				errorSemantico("El operando de '%' debe ser entero o real");
			} else if (r_p.equals(T_VACIO)) {
				ret = u;
			} else if (!u.equals(r_p)) {
				errorSemantico("Hay operandos de tipos distintos en '%'");
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

	/* REGLAS
		7. U → ! V
			{
				if V.tipo ≠ lógico then
					error("El operando de '!' debe ser un logico")
				else
					U.tipo := logico
			}
		8. U → V { U.tipo := V.tipo }
	*/
	// Sintetizado: U.tipo
	private String U() throws IOException {
		String ret = null;

		if (token.equals("!")) {
			bwParse.write(" 7");
			equipara("!");
			String v = V();	// V.tipo

			if (!v.equals(T_LOGICO)) {
				errorSemantico("El operando de '!' debe ser un logico");
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

	/* REGLAS
		9. V → id V_p
			{
				if V_p.llamaFunc = true then
					if BuscaTipo(id.pos) = tipoFunction then
						if V_p.tipo = BuscaParam(id.pos) then
							V.tipo := BuscaTipoRet(id.pos)
						else
							error("Parámetros incorrectos")
					else
						error("Función no declarada")
				else
					V.tipo := BuscaTipo(id.pos)
			}
		10. V → ( E ) { V.tipo := E.tipo }
		11. V → entero { V.tipo := entero }
		12. V → cadena { V.tipo := cadena }
		13. V → real { V.tipo := real }
	*/
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
						errorSemantico("Parámetros incorrectos");
					}
				} else {
					errorSemantico("Función no declarada");
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

	/* REGLAS
		15. V_p → ( L ) { V_p.tipo := L.tipo, V_p.llamaFunc := true}
		14. V_p → λ { V_p.tipo := vacío, V_p.llamaFunc := false}
	 */
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

	/* REGLAS
		16. S → id S_p 
			{
				if S_p.llamaFunc = true then
					if S_p.tipo = BuscaParam(id.pos) then
						S.tipo := tipo_ok
					else
						error("Parámetros incorrectos")
				else
					if BuscaTipo(id.pos) = S_p.tipo then
						S.tipo := tipo_ok
					else
						error("Tipos incompatibles en asignación")
			}
		17. S → write E ; { if E.tipo ∈ {entero, real, cadena} then S.tipo := tipo_ok else S.tipo := tipo_error }
		18. S → read id ; 
			{
				if BuscaTipo(id.pos) ∉ {entero, real, cadena} 
					error(“Identificador del read no es entero, real ni cadena”)
				else
					S.tipo := tipo_ok
			}
		19. S → return X ; 
			{
				if S.funcion = false then Error(“Return debe ir dentro de una funcion”);
				S.tipo := X.tipo
				S.tipoRet := X.tipo
			}
	*/
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
				String[] args = s_p_tipo.equals(T_VACIO) ? new String[0] : s_p_tipo.split(" x ");
				if (compararTipos(args, ts.buscaParam(copy_id_pos))) {
					ret.put(TIPO, T_OK);
				} else {
					errorSemantico("Parámetros incorrectos");
				}
			} else {	// es una asignacion o asignacion con suma
				if (s_p_tipo != null && s_p_tipo.equals(ts.buscaTipo(copy_id_pos))) {
					ret.put(TIPO, T_OK);
				} else {
					errorSemantico("Tipos incompatibles en asignación");
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
				ret.put(TIPO, T_ERROR);
			}
		} else if (token.equals("read")) {
			bwParse.write(" 18");
			equipara("read");
			int copy_id_pos = id_pos;
			equipara("ID");
			equipara(";");

			String tipo = ts.buscaTipo(copy_id_pos);
			if (tipo != null && !tipo.equals(T_ENTERO) && !tipo.equals(T_REAL) && !tipo.equals(T_CADENA)) {
				errorSemantico("Identificador del read debe ser entero, real o cadena");
			} else {
				ret.put(TIPO, T_OK);
			}
		} else if (token.equals("return")) {
			bwParse.write(" 19");
			equipara("return");
			String x = X();
			equipara(";");

			if (!funcion) errorSemantico("Return debe ir dentro de una funcion");
			ret.put(TIPO, T_OK);
			ret.put(TIPO_RET, x);
		} else {
            errorSintactico("una sentencia válida (ID, write, read, return)");
        }

		return ret;
	}

	/* REGLAS
		20. S_p → = E ; { S_p.tipo := E.tipo; S_p.llamaFunc := false }
		21. S_p → += E ; { S_p.tipo := E.tipo; S_p.llamaFunc := false }
		22. S_p → ( L ) ; { S_p.tipo := L.tipo; S_p.llamaFunc := true }
	 */
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

	/* REGLAS
		23. L → E Q { L.tipo := if Q.tipo = vacío then E.tipo else E.tipo x Q.tipo } 
		24. L → λ { L.tipo := vacío }
	*/
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

	/* REGLAS
		25. Q1 → , E Q2 
			{
				if Q2.tipo = vacío then
					Q1.tipo := E.tipo
				else
					Q1.tipo := E.tipo × Q2.tipo
			}
		26. Q → λ {Q.tipo := vacío}
	*/
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


	/* REGLAS
		27. X → E { X.tipo := E.tipo }
		28. X → λ { X.tipo := vacío }
	*/
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

	/* REGLAS
		29. B → if ( E ) S 
			{
				if E.tipo ≠ lógico then
					error("La condición del if debe ser un booleano")
				else if S.tipo ≠ tipo_ok then
					error("Cuerpo del if incorrecto")
				else
					B.tipo := tipo_ok
					B.tipoRet := S.tipoRet
			}
		30. B → let T { zonaDecl := true } id ;
			{
				if TSL = null then
					AñadeTipo(id.pos, T.tipo)
					AñadeDesp(id.pos, despG)
					despG := despG + T.tamaño
				else
					AñadeTipo(id.pos, T.tipo)
					AñadeDesp(id.pos, despL)
					despL := despL + T.tamaño

				B.tipo := tipo_ok
				B.tipoRet := vacío
				zonaDecl := false
			}
		31. B → {S.function := B.function }S { B.tipo := S.tipo; B.tipoRet := S.tipoRet}
		32. B → for ( Y1 ; E ; Y2 ) { C } 
			{
				if E.tipo ≠ lógico then error(“Condición del for debe ser boolean");
				B.tipo := tipo_ok;
				B.tipoRet := C.tipoRet; 
			}
	*/
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
				errorSemantico("La condición del if debe ser de tipo lógico");
			}

			b1.put(TIPO, s.get(TIPO));
			b1.put(TIPO_RET, s.get(TIPO_RET));

		} else if (token.equals("let")) {
			bwParse.write(" 30");
			equipara("let");

			zonaDecl = true;
			HashMap<String, Object> t = T();  // T.tipo, T.tamano

			int id = id_pos;
			equipara("ID");
			equipara(";");

			// Declaración: tipo + desplazamiento
			int res = ts.setTipo(id, (String) t.get(TIPO));
			System.out.println(res);
			ts.setValorAtributoEnt(id, TablaSimbolos.ATR_DESPL, ts.existeTSL ? despL : despG);
			if (ts.existeTSL) despL += (Integer) t.get(TAMANO);
			else             despG += (Integer) t.get(TAMANO);

			zonaDecl = false;

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
			equipara(";");
			String y2 = Y();  // Y.tipo

			equipara(")");
			equipara("{");
			HashMap<String, Object> c = C(function); // C.tipo, C.tipoRet
			equipara("}");

			if (!tE.equals(T_LOGICO)) {
				errorSemantico("La condición del for debe ser de tipo lógico");
			}

			if (y1.equals(T_OK) && y2.equals(T_OK) && c.get(TIPO).equals(T_OK)) {
				b1.put(TIPO, T_OK);
			} else {
				b1.put(TIPO, T_ERROR);
			}

			b1.put(TIPO_RET, c.get(TIPO_RET));

		} else {
			errorSintactico("una sentencia válida (if, let, ID, read, return, write, for)");
		}

		return b1;
	}


	/* REGLAS 
		33. Y → W { Y.tipo := W.tipo }
		34. Y → λ { Y.tipo := tipo_ok }
	*/
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

	/* REGLAS
		35. T → int { T.tipo := entero ; T.tamaño := 2 }
		36. T → float { T.tipo := real ; T.tamaño := 4 }
		37. T → boolean { T.tipo := logico ; T.tamaño := 2 }
		38. T → string { T.tipo := cadena ; T.tamaño := 128 }
	*/
	// Sintetizado: T.tipo, T.tamaño
	private HashMap<String, Object> T() throws IOException {
		HashMap<String, Object> t1 = new HashMap<>();

		if (token.equals("int")) {
			bwParse.write(" 35");
			equipara("int");
			t1.put(TIPO, T_ENTERO);
			t1.put(TAMANO, 2);

		} else if (token.equals("float")) {
			bwParse.write(" 36");
			equipara("float");
			t1.put(TIPO, T_REAL);
			t1.put(TAMANO, 4);

		} else if (token.equals("boolean")) {
			bwParse.write(" 37");
			equipara("boolean");
			t1.put(TIPO, T_LOGICO);
			t1.put(TAMANO, 2);

		} else if (token.equals("string")) {
			bwParse.write(" 38");
			equipara("string");
			t1.put(TIPO, T_CADENA);
			t1.put(TAMANO, 128);

		} else {
			errorSintactico("un tipo válido (int, float, boolean, string)");
		}

		return t1;
	}


	/* REGLAS
		39. W → id W_p { W.tipo := if BuscaTipoTS(id.pos) = W_p.tipo 	then tipo_ok
								   else 								error(“Tipos incompatibles en asignación” }
	*/
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
				errorSemantico("Tipos incompatibles en asignación");
			}
		}

		errorSintactico("un identificador");
		return null; // inalcanzable, para que no se queje el compilador
	}

	/* REGLAS
		40. W_p → = E { W_p.tipo := E.tipo }
		41. W_p → += E { W_p.tipo := E.tipo }
	*/

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

			// H.tipo
			String hTipo = H();

			zonaDecl = true;
			int idFunc = id_pos;     // pos en TS del identificador de la función
			equipara("ID");

			ts.createTSLocal();
			despL = 0;

			equipara("(");

			// A.n, A.tipo
			HashMap<String, Object> a = A();

			zonaDecl = false;
			
			// AñadeNumParam(idFunc, A.n)
			ts.setValorAtributoEnt(idFunc, TablaSimbolos.ATR_NUM_PARAM ,(Integer)a.get(N));
			// AñadeTipo(idFunc, tipoFunction)
			ts.setTipo(idFunc, T_FUNCION);
			// AñadeParam(idFunc, A.tipo)
			String aTipo = (String) a.get(TIPO);
			String[] params = aTipo.equals(T_VACIO) ? new String[0] : aTipo.split(" x ");
			ts.setValorAtributoLista(idFunc, TablaSimbolos.ATR_TIPO_PARAM, params);
			// AñadeTipoRet(idFunc, H.tipo)
			ts.setValorAtributoCad(idFunc, TablaSimbolos.ATR_TIPO_RETORNO, hTipo);
			// AñadeEtiq(idFunc, nuevaEtiq())
			// ts.setValorAtributoCad(idFunc, TablaSimbolos.ATR_ETIQ_FUNCION, nuevaEtiq()); //???????????????????????????????????????????????????????

			equipara(")");
			equipara("{");

			// { C.function := true}
			HashMap<String, Object> c = C(true);   // C.tipoRet

			equipara("}");

			// if C.tipoRet ≠ H.tipo then error
			if (!hTipo.equals(c.get(TIPO_RET))) {
				errorSemantico("Tipo de retorno incorrecto");
			}

			// LiberaTabla(TSL)
			ts.destroyTSLocal();

		} else {
			errorSintactico("declaración de función (function)");
		}
	}

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

	private HashMap<String, Object> A() throws IOException {
		HashMap<String, Object> a1 = new HashMap<>();

		// A -> T ID K
		if (compararTokens(token, new String[] { "boolean", "float", "int", "string" })) {
			bwParse.write(" 45");

			HashMap<String, Object> t = T();   // T.tipo, T.tamano

			int id = id_pos;                  // atributo del token ID
			equipara("ID");

			HashMap<String, Object> k = K();   // K.n, K.tipo

			// Semántica: primer parámetro
			ts.setTipo(id, (String) t.get(TIPO));
			ts.setValorAtributoEnt(id, TablaSimbolos.ATR_DESPL, ts.existeTSL ? despL : despG);
			if (ts.existeTSL) despL += (Integer) t.get(TAMANO);
			else             despG += (Integer) t.get(TAMANO);

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
			errorSintactico("lista de parámetros de función");
		}

		return a1;
	}

	private HashMap<String, Object> K() throws IOException {
		HashMap<String, Object> k1 = new HashMap<>();

		if (compararTokens(token, new String[] { "," })) {
			bwParse.write(" 47");
			equipara(",");

			HashMap<String, Object> t = T();   // T.tipo, T.tamano

			int id = id_pos;
			equipara("ID");

			HashMap<String, Object> k2 = K();  // K2.n, K2.tipo

			// Semántica: parámetro actual
			ts.setTipo(id, (String) t.get(TIPO));
			ts.setValorAtributoEnt(id, TablaSimbolos.ATR_DESPL, ts.existeTSL ? despL : despG);
			if (ts.existeTSL) despL += (Integer) t.get(TAMANO);
			else             despG += (Integer) t.get(TAMANO);

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

	private HashMap<String, Object> C(boolean function) throws IOException {
		HashMap<String, Object> c1 = new HashMap<>();

		if (compararTokens(token, new String[] { "for", "ID", "if", "let", "read", "return", "write" })) {
			bwParse.write(" 49");

			HashMap<String, Object> b = B(function);
			HashMap<String, Object> c2 = C(function);

			// C.tipo
			if (b.get(TIPO).equals(c2.get(TIPO)) && b.get(TIPO).equals(T_OK)) {
				c1.put(TIPO, T_OK);
			} else {
				c1.put(TIPO, T_ERROR);
			}

			// C.tipoRet
			if (b.get(TIPO_RET).equals(T_VACIO)) {
				c1.put(TIPO_RET, c2.get(TIPO_RET));
			} else {
				if (c2.get(TIPO_RET).equals(T_VACIO)) {
					c1.put(TIPO_RET, b.get(TIPO_RET));
				} else {
					errorSemantico("múltiples sentencias return en el mismo bloque");
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