
/* Clase que internamente gestiona la clase TS_Gestor de la libreria de TS 
 * de la pagina de documentacion de la asignatura. El objetivo es crear los metodos
 * que realicen exactamente lo que necesitemos, añadiendo una capa mas de abstraccion */
public class TablaSimbolos {
    
    private TS_Gestor gestorTS;

	public boolean existeTSL;

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
		String[] nombres = {"Despl","NumParam","TipoParam","ModoParam","TipoRetorno","EtiqFuncion","Param"};
		TS_Gestor.DescripcionAtributo[] descripcion = {
			TS_Gestor.DescripcionAtributo.DIR,
			TS_Gestor.DescripcionAtributo.NUM_PARAM,
			TS_Gestor.DescripcionAtributo.TIPO_PARAM,
			TS_Gestor.DescripcionAtributo.MODO_PARAM,
			TS_Gestor.DescripcionAtributo.TIPO_RET,
			TS_Gestor.DescripcionAtributo.ETIQUETA,
			TS_Gestor.DescripcionAtributo.PARAM,
		};
		TS_Gestor.TipoDatoAtributo[] tipos = {
			TS_Gestor.TipoDatoAtributo.ENTERO,
			TS_Gestor.TipoDatoAtributo.ENTERO,
			TS_Gestor.TipoDatoAtributo.LISTA,
			TS_Gestor.TipoDatoAtributo.LISTA,
			TS_Gestor.TipoDatoAtributo.CADENA,
			TS_Gestor.TipoDatoAtributo.CADENA,
			TS_Gestor.TipoDatoAtributo.ENTERO,
		};
		for (int i = 0; i < nombres.length; i++) gestorTS.createAtributo(nombres[i], descripcion[i], tipos[i]);
	}

	/**********************************************************************************************************************************/

	public void createTSLocal() {
		gestorTS.createTSLocal();
		existeTSL = true;
	}

	public void createTSGlobal() {
		gestorTS.createTSGlobal();
	}

	/**********************************************************************************************************************************/

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

	/**********************************************************************************************************************************/

    public void print() {
        gestorTS.write(TS_Gestor.Tabla.GLOBAL);
    }

    /**********************************************************************************************************************************/
	// METODOS LLAMADOS POR EL LEXICO
	// *******************************
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
	 * @return id.pos o 0 si no existe
	 */
	public int buscaEnTSA(String lex) {
		return gestorTS.getEntradaTS(lex);
	}

	/**
	 * Inserta ese lexema en la Tabla de Simbolos Activa
	 * @param lex Lexema del identificador
	 * @return id.pos o 0 si no se ha podido insertar
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

	/**********************************************************************************************************************************/

	// Llamado por el semantico, para insertar el tipo de un identificador
	public int insertaTipo(int pos, String tipo) {
		return gestorTS.setTipo(pos, tipo);
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
	 * @return su lista de parametros o null si hay error
	 */
	public String[] buscaParam(int pos) {
		return gestorTS.getValorAtributoLista(pos, ATR_PARAM);
	}

	/**
	 * Devuelve el tipo de retorno de una funcion
	 * @param pos id.pos de la funcion
	 * @return su tipo de retorno o null si hay error
	 */
	public String buscaTipoRet(int pos) {
		return gestorTS.getValorAtributoCad(pos, ATR_TIPO_RETORNO);
	}

	public int getValorAtributoEnt(int pos, String atr) {
		return gestorTS.getValorAtributoEnt(pos, atr);
	}

	public String getValorAtributoCad(int pos, String atr) {
		return gestorTS.getValorAtributoCad(pos, atr);
	}

	public String[] getValorAtributoLista(int pos, String atr) {
		return gestorTS.getValorAtributoLista(pos, atr);
	}

	/**********************************************************************************************************************************/

	public int setTipo(int pos, String tipo_id) {
		return gestorTS.setTipo(pos, tipo_id);
	}

	public int setValorAtributoEnt(int pos, String atr, int valor) {
		return gestorTS.setValorAtributoEnt(pos, atr, valor);
	}

	public int setValorAtributoCad(int pos, String atr, String valor) {
		return gestorTS.setValorAtributoCad(pos, atr, valor);
	}

	public int setValorAtributoLista(int pos, String atr, String[] valor) {
		return gestorTS.setValorAtributoLista(pos, atr, valor);
	}
}
