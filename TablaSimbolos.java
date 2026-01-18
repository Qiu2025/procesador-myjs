
/** 
 * Clase que internamente gestiona la clase TS_Gestor de la libreria de TS 
 * de la pagina de documentacion de la asignatura. El objetivo es crear una capa
 * mas de abstraccion para dejar el codigo limpio en los otros archivos.
 */
public class TablaSimbolos {
    
    private TS_Gestor gestorTS;

	// Relacionados con Analizador Semantico
	public boolean existeTSL;
	public int despG;	// desplazamiento global
	public int despL;	// desplazamiento local
	public boolean zonaDecl;
	private int contadorEtiqueta = 1;

	// Atributos de TS
	public static final String ATR_DESPL        = "Despl";
	public static final String ATR_NUM_PARAM    = "NumParam";
	public static final String ATR_TIPO_PARAM   = "TipoParam";
	public static final String ATR_MODO_PARAM   = "ModoParam";
	public static final String ATR_TIPO_RETORNO = "TipoRetorno";
	public static final String ATR_ETIQ_FUNCION = "EtiqFuncion";
	public static final String ATR_PARAM        = "Param";

	public String[] palabrasReservadasEntrada = {"boolean","float","for","function","if","int","let","read","return","string","void","write"};
	public String[] palabrasReservadasSalida = {"tipoBoolean","tipoFloat","for","function","if","tipoInt","let","read","return","tipoString","void","write"};

    public TablaSimbolos(String rutaTS) {
        gestorTS = new TS_Gestor(rutaTS);
    }

    public void init() {
		// Palabras reservadas
		gestorTS.createTPalabrasReservadas();
		for (String pr : palabrasReservadasEntrada) gestorTS.addEntradaTPalabrasReservadas(pr);

		// Atributos
		String[] nombres = {"Despl","NumParam","TipoParam","TipoRetorno","EtiqFuncion"};
		TS_Gestor.DescripcionAtributo[] descripcion = {
			TS_Gestor.DescripcionAtributo.DIR,
			TS_Gestor.DescripcionAtributo.NUM_PARAM,
			TS_Gestor.DescripcionAtributo.TIPO_PARAM,
			TS_Gestor.DescripcionAtributo.TIPO_RET,
			TS_Gestor.DescripcionAtributo.ETIQUETA,
		};
		TS_Gestor.TipoDatoAtributo[] tipos = {
			TS_Gestor.TipoDatoAtributo.ENTERO,
			TS_Gestor.TipoDatoAtributo.ENTERO,
			TS_Gestor.TipoDatoAtributo.LISTA,
			TS_Gestor.TipoDatoAtributo.CADENA,
			TS_Gestor.TipoDatoAtributo.CADENA,
		};
		for (int i = 0; i < nombres.length; i++) {
			gestorTS.createAtributo(nombres[i], descripcion[i], tipos[i]);
		}

		despG = 0;
		despL = 0;
		zonaDecl = false;
		gestorTS.createTSGlobal();
	}

	/**********************************************************************************************************************************/

	public void createTSLocal() {
		gestorTS.createTSLocal();
		existeTSL = true;
		despL = 0;
	}

	public void destroyTSLocal() {
		gestorTS.destroy(TS_Gestor.Tabla.LOCAL);
		existeTSL = false;
	}

    public void destroyAll() {
		gestorTS.destroy(TS_Gestor.Tabla.LOCAL);
        gestorTS.destroy(TS_Gestor.Tabla.GLOBAL);
		gestorTS.destroy(TS_Gestor.Tabla.PALRES);
		existeTSL = false;
    }

    public void write(int tabla) {
		if (tabla == 1) {
        	gestorTS.write(TS_Gestor.Tabla.GLOBAL);
		} else if (tabla == -1) {
			gestorTS.write(TS_Gestor.Tabla.LOCAL);
		} else {
			throw new ExcepcionSintacticoSemantico("TablaSimbolos.write(): parametro no valido");
		}
    }

    /**********************************************************************************************************************************/

	/**
	 * Busca ese lexema en la tabla de palabras reservadas
	 * @param lex Lexema a buscar
	 * @return Codigo de la palabra reservada o null si lex no es una palabra reservada
	 */
	public String buscaEnTPR(String lex) {
		int pos_palRes = gestorTS.getEntradaTPalabrasReservadas(lex);
		if (pos_palRes == 0)
			return null;

		return palabrasReservadasSalida[pos_palRes - 1];
	}

	/**
	 * Busca ese lexema en la Tabla de Simbolos Activa
	 * @param lex Lexema del identificador
	 * @return id.pos o 0 si error
	 */
	public int buscaEnTSA(String lex) {
		return gestorTS.getEntradaTS(lex);
	}

	/**
	 * Inserta ese lexema en la Tabla de Simbolos Activa
	 * @param lex Lexema del identificador
	 * @return id.pos o 0 si error
	 */
	public int insertaLexemaEnTSA(String lex) {
		int pos = 0;
		if (existeTSL) {
			pos = gestorTS.addEntradaTSLocal(lex);
		}
		else {
			pos = gestorTS.addEntradaTSGlobal(lex);
		}

		return pos;
	}

	/**
	 * Inserta los atributos de un id (los que solo tienen atributos tipo y desplazamiento)
	 * @param pos posicion del identificador en TS
	 * @param tipo el tipo a insertar
	 * @param tamano el tamano del tipo
	 * @return 0 si exito, fallo en otro caso
	 */
	public int insertaAtributosVariable(int pos, String tipo, int tamano) {
		int res = gestorTS.setTipo(pos, tipo);

		if (existeTSL) {
			res += gestorTS.setValorAtributoEnt(pos, TablaSimbolos.ATR_DESPL, despL);
			despL += tamano;
		} else {
			res += gestorTS.setValorAtributoEnt(pos, TablaSimbolos.ATR_DESPL, despG);
			despG += tamano;
		}

		return res;
	}

	/**
	 * Inserta los atributos de un id funcion (tipo, numParams, tipoParams, tipoRet, etiq)
	 * @param pos posicion del identificador en TS
	 * @param numParams numero de parametros de la funcion
	 * @param tipoParams tipo de cada uno de los parametros
	 * @param tipoRet tipo de retorno de la funcion
	 * @return 0 si exito, fallo en otro caso
	 */
	public int insertaAtributosFuncion(int pos, int numParams, String[] tipoParams, String tipoRet) {
		int res = gestorTS.setTipo(pos, ASintacticoSemantico.T_FUNCION);
		res += gestorTS.setValorAtributoEnt(pos, TablaSimbolos.ATR_NUM_PARAM, numParams);
		res += gestorTS.setValorAtributoLista(pos, TablaSimbolos.ATR_TIPO_PARAM, tipoParams);
		res += gestorTS.setValorAtributoCad(pos, TablaSimbolos.ATR_TIPO_RETORNO, tipoRet);
		res += gestorTS.setValorAtributoCad(pos, TablaSimbolos.ATR_ETIQ_FUNCION, nuevaEtiqueta());

		return res;
	}

	private String nuevaEtiqueta() {
		return "et" + contadorEtiqueta++;
	}

	/**
	 * Devuelve el tipo de un identificador
	 * @param pos posicion del identificador
	 * @return su tipo o null si error
	 */
	public String buscaTipo(int pos) {
		return gestorTS.getTipo(pos);
	}

	/**
	 * Devuelve la lista de parametros de una funcion
	 * @param pos id.pos de la funcion
	 * @return su lista de parametros o null si error
	 */
	public String[] buscaParam(int pos) {
		return gestorTS.getValorAtributoLista(pos, ATR_TIPO_PARAM);
	}

	/**
	 * Devuelve el tipo de retorno de una funcion
	 * @param pos id.pos de la funcion
	 * @return su tipo de retorno o null si error
	 */
	public String buscaTipoRet(int pos) {
		return gestorTS.getValorAtributoCad(pos, ATR_TIPO_RETORNO);
	}
}
