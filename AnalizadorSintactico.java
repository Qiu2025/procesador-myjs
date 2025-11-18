import java.io.*;

public class AnalizadorSintactico {
    
    private String token; // token devuelto por el lexico
    private AnalizadorLexico aLex;
    private BufferedWriter bwParse;

    public AnalizadorSintactico(String rutaEntrada, String rutaTokens, String rutaParse, String rutaTS) throws IOException {
        aLex = new AnalizadorLexico(rutaEntrada, rutaTokens, rutaTS);
        bwParse = new BufferedWriter(new FileWriter(rutaParse));
		bwParse.write("descendente");
    }
    
	public void start() throws IOException {
		token = aLex.nextToken();
        formatearToken();
		P();
		if (!token.equals("$")) {
			System.out.println("[Error sintáctico] línea " + aLex.getLinea());
			System.out.println("Fin de archivo no encontrado");
			System.out.println("Se esperaba: $");
			System.out.println("Se encontró: " + token);
		}

        // Hacer el volcado a los ficheros y cerrar recursos
        aLex.imprimirTablaGlobal();
        aLex.cerrarRecursos();

        bwParse.close();
	}

	private void equipara(String tokenEsperado) throws IOException {
		if (token.equals(tokenEsperado)) {
			token = aLex.nextToken();
			formatearToken();
		} else {
			System.out.println("[Error sintáctico] línea " + aLex.getLinea());
			System.out.println("Se esperaba: " + tokenEsperado);
			System.err.println("Se encontró: " + token);
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
			default -> {
				if (token.contains("numReal"))
					token = "numReal";
				else if (token.contains("numEntero"))
					token = "numEntero";
				else if (token.contains("cadena"))
					token = "cadena";
				else if (token.contains("ID"))
					token = "ID";
			}
		}
	}

	private void E() throws IOException {
		bwParse.write(" 1");
		R();
		E_p();
	}

	private void E_p() throws IOException {
		if (token.equals("<")) {
			bwParse.write(" 2");
			equipara("<");
			R();
			E_p();
		} else if (compararTokens(token, new String[] {")", ",", ";"})) { // Follow(E_p)
			bwParse.write(" 3");
		}
	}

	private void R() throws IOException {
		bwParse.write(" 4");
		U();
		R_p();
	}

	private void R_p() throws IOException {
		if (token.equals("%")) {
			bwParse.write(" 5");
			equipara("%");
			U();
			R_p();
		} else if (compararTokens(token, new String[] { ")", ",", ";", "<" })) { // Follow(R_p)
			bwParse.write(" 6");
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
		}
	}

	private void L() throws IOException { // First(L) = First(EQ) = First(E)
		if (compararTokens(token, new String[] { "!", "(", "cadena", "numEntero", "ID", "numReal" })) {
			bwParse.write(" 23");
			E();
			Q();
		} else if (token.equals(")")) {
			bwParse.write(" 24");
		}
	}

	private void Q() throws IOException {
		if (token.equals(",")) {
			bwParse.write(" 25");
			equipara(",");
			E();
			Q();
		} else if (token.equals(")")) { // Follow(Q)
			bwParse.write(" 26");
		}
	}

	private void X() throws IOException {
		if (compararTokens(token, new String[] { "!", "(", "cadena", "numEntero", "ID", "numReal" })) {
			bwParse.write(" 27");
			E();
		} else if (compararTokens(token, new String[] { ")", ";" })) { // Follow(X)
			bwParse.write(" 28");
		}
	}

	private void B() throws IOException {
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
			B();
			equipara("}");
		}
	}

	private void Y() throws IOException {
		if (token.equals("ID")) {
			bwParse.write(" 33");
			W();
		} else { // Y no tiene follow
			bwParse.write(" 34");
		}
	}

	private void T() throws IOException {
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
		}
	}

	private void W() throws IOException {
		if (token.equals("ID")) {
			bwParse.write(" 39");
			equipara("ID");
			W_p();
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
		}
	}

	private void F() throws IOException {
		if (token.equals("function")) {
			bwParse.write(" 42");
			equipara("function");
			H();
			equipara("ID");
			equipara("(");
			A();
			equipara(")");
			equipara("{");
			C();
			equipara("}");
		}
	}

	private void H() throws IOException {
		if (compararTokens(token, new String[] { "boolean", "float", "int", "string" })) {
			bwParse.write(" 43");
			T();
		} else if (token.equals("void")) {
			bwParse.write(" 44");
			equipara("void");
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
		}
	}

	private void K() throws IOException {
		if (compararTokens(token, new String[] { "boolean", "float", "int", "string" })) {
			bwParse.write(" 47");
			T();
			equipara("ID");
			K();
		} else if (token.equals(")")) { // Follow(K)
			bwParse.write(" 48");
		}
	}

	private void C() throws IOException {
		if (compararTokens(token, new String[] { "for", "ID", "if", "let", "read", "return", "write" })) {
			bwParse.write(" 49");
			B();
			C();
		} else if (token.equals("}")) { // Follow(C)
			bwParse.write(" 50");
		}
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
		} else if (token.equals("$")) { // Follow(P)
			bwParse.write(" 53");
		}
	}
}