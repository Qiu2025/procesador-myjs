
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * Represertaci�n de una Tabla de S�mbolos.
 * 
 * @author Carolina Garza Bravo
 */

public class TS {
	/**
	 * Estructura de datos en la que se guardan todos los datos de la tabla de s�mbolos.
	 */
	private HashMap<Integer,Entrada> ts;
	/**
	 * Identificador de la tabla.
	 */
	private int id;
	/**
	 * Posici�n siguiente donde se a�adir� la siguiente entrada.
	 */
	private int pos;
	
	/**
	 * Crea una tabla de s�mbolos
	 * @param id Identificador de la tabla.
	 */
	public TS(int id) {
		ts=new HashMap<>();
		this.id=id;
		if(id<2) {
			pos=1;
		}
		else {
			pos=-1;
		}
	}
	
	/**
	 * A�ade un nuevo identificador a la tabla.
	 * @param lex Lexema del identificador a a�adir.
	 * @return La posici�n en la que se ha a�adido o 0 si el lexema a a�adir ya est� en la tabla.
	 */
	public int addEntrada(String lex) {
		int res=0;
		if(getEntrada(lex)==0) {
			Entrada nueva=new Entrada(lex);
			ts.put(pos,nueva);
			res=pos;
			if(id<2) {
				pos++;
			}
			else {
				pos--;
			}
		}
		return res;
	}
	
	/**
	 * Busca un identificador en la tabla.
	 * @param lex Lexema del identificador a buscar.
	 * @return La posici�n en la que est� el lexema o 0 si se ha encontrado.
	 */
	public int getEntrada(String lex) {
		int res=0;
		Iterator<Entry<Integer,Entrada>> it=ts.entrySet().iterator();
		while(it.hasNext() && res==0) {
			Entry<Integer,Entrada> entrada=it.next();
			if(entrada.getValue().getLexema().equals(lex)) {
				res=entrada.getKey();
			}
		}
		return res;
	}
	
	/**
	 * Da valor al tipo de un identificador de la tabla.
	 * @param pos Posici�n del identificador.
	 * @param tipo Tipo del identificador. Los tipos posibles son: funci�n, procedimiento, entero, cadena,
	 * real, l�gico, puntero y vector.
	 * @return 0 si ha salido bien, 5 si la posici�n no es correcta, 6 si el tipo no es correcto o 7 si el tipo
	 * ya estaba definido
	 */
	public int setTipo(int pos,String tipo) {
		int res=0;
		if((((id==0||id==1)&&(pos<this.pos))||((id>1)&&(pos>this.pos)))&&pos!=0) {
			res=ts.get(pos).setTipo(tipo);
		}
		else {
			res=5;
		}
		return res;
	}
	
	/**
	 * Devuelve el tipo del identificador que est� en una posici�n concreta.
	 * @param pos Posici�n del identificador.
	 * @return El tipo del identificador de esa posici�n o null si la posici�n es err�nea.
	 */
	public String getTipo(int pos) {
		if(((id==0||id==1)&&(pos<this.pos))||((id>1)&&(pos>this.pos))) {
			return ts.get(pos).getTipo();
		}
		else {
			return null;
		}
	}
	
	/**
	 * A�ade un atributo a un identificador.
	 * @param pos Posici�n del identificador.
	 * @param atr Nombre del atributo.
	 * @param des Descripci�n del atributo.
	 * @param td Tipo de dato del atributo.
	 * @return 0 si todo ha ido bien, 5 si la posici�n no es correcta 0 13 si el nombre del atributo no es v�lido.
	 */
	public int setAtributo(int pos,String atr,TS_Gestor.DescripcionAtributo des,TS_Gestor.TipoDatoAtributo td) {
		int res=0;
		if(((id==0||id==1)&&(pos<this.pos))||((id>1)&&(pos>this.pos))) {
			res=ts.get(pos).setAtributo(atr, des, td);
		}
		else {
			res=5;
		}
		return res;
	}
	
	/**
	 * Da valor entero a un atributo del identificador de una posici�n concreta.
	 * @param pos Posici�n del identificador.
	 * @param atr Nombre del atributo al que se le quiere poner valor.
	 * @param valor Valor que le va a asignar al atributo.
	 * @return 0 si todo ha salido bien, 5 si la posici�n no es correcta, 7 si el atributo ya ten�a un
	 * valor asignado, 8 si el tipo de dato del atributo no est� definido como ENTERO o 9 si el atributo
	 * no existe.
	 */
	public int setValorAtributoEnt(int pos,String atr,int valor) {
		int res=0;
		if(((id==0||id==1)&&(pos<this.pos))||((id>1)&&(pos>this.pos))) {
			res=ts.get(pos).setValorAtributoEnt(atr, valor);
		}
		else {
			res=5;
		}
		return res;
	}
	
	/**
	 * Devuelve el valor entero de un atributo del identificador que est� en una posici�n concreta.
	 * @param pos Posici�n del identificador.
	 * @param atr Nombre del atributo.
	 * @return El valor entero del atributo o -1 si hay error, que puede ser que el atributo o
	 * la posici�n no sean correctos o que el atributo no est� declarado como ENTERO.
	 */
	public int getValorAtributoEnt(int pos,String atr) {
		if(((id==0||id==1)&&(pos<this.pos))||((id>1)&&(pos>this.pos))) {
			return ts.get(pos).getValorAtributoEnt(atr);
		}
		else {
			return -1;
		}
	}
	
	/**
	 * Da valor de cadena a un atributo del identificador de una posici�n concreta.
	 * @param pos Posici�n del identificador.
	 * @param atr Nombre del atributo al que se le va a dar valor.
	 * @param valor El valor que se le va a asignar al atributo.
	 * @return 0 si todo ha salido bien, 5 si la posici�n no es correcta, 7 si el atributo ya ten�a un
	 * valor asignado, 8 si el tipo de dato del atributo no est� definido como CADENA o 9 si el atributo
	 * no existe.
	 */
	public int setValorAtributoCad(int pos,String atr,String valor) {
		int res=0;
		if(((id==0||id==1)&&(pos<this.pos))||((id>1)&&(pos>this.pos))) {
			res=ts.get(pos).setValorAtributoCad(atr, valor);
		}
		else {
			res=5;
		}
		return res;
	}
	
	/**
	 * Devuelve el valor de cadena de un atributo del identificador que est� en una posici�n concreta.
	 * @param pos Posici�n del identificador.
	 * @param atr Nombre del atributo.
	 * @return El valor de cadena del atributo o null si hay error, que puede ser que el atributo o la
	 * posici�n no sean correctos o que el atributo no est� declarado como CADENA.
	 */
	public String getValorAtributoCad(int pos,String atr) {
		if(((id==0||id==1)&&(pos<this.pos))||((id>1)&&(pos>this.pos))) {
			return ts.get(pos).getValorAtributoCad(atr);
		}
		else {
			return null;
		}
	}
	
	/**
	 * Da valor en forma de lista de cadenas a un atributo del identificador de una posici�n concreta.
	 * @param pos Posici�n del identificador.
	 * @param atr Nombre del atributo al que se le va a dar valor.
	 * @param valor Valor que se le va a asignar al atributo.
	 * @return 0 si todo ha salido bien, 5 si la posici�n no es correcta, 7 si el atributo ya ten�a un
	 * valor asignado, 8 si el tipo de dato del atributo no est� definido como LISTA o 9 si el atributo no existe.
	 */
	public int setValorAtributoLista(int pos,String atr,String[] valor) {
		int res=0;
		if(((id==0||id==1)&&(pos<this.pos))||((id>1)&&(pos>this.pos))) {
			res=ts.get(pos).setValorAtributoLista(atr, valor);
		}
		else {
			res=5;
		}
		return res;
	}
	
	/**
	 * Devuelve el valor en forma de lista de cadenas de un atributo del identificador que est� en una posici�n
	 * concreta.
	 * @param pos Posici�n del identificador.
	 * @param atr Nombre del atributo.
	 * @return El valor en forma de lista de cadenas del atributo o null si hay error, que puede ser que el
	 * atributo o la posici�n no sean correctos o que el atributo no est� declarado como LISTA.
	 */
	public String[] getValorAtributoLista(int pos,String atr) {
		if(((id==0||id==1)&&(pos<this.pos))||((id>1)&&(pos>this.pos))) {
			return ts.get(pos).getValorAtributoLista(atr);
		}
		else {
			return null;
		}
	}
	
	/**
	 * Muestra por pantalla la representaci�n de la tabla de s�mbolos.
	 */
	public void show() {
		if(id==0) {
			System.out.println("TABLA DE PALABRAS RESERVADAS #" + id + ":");
		}
		else if(id==1){
			System.out.println("TABLA PRINCIPAL #" + id + ":");
		}
		else {
			System.out.println("TABLA LOCAL #" + id + ":");
		}
		Iterator<Entry<Integer,Entrada>> it=ts.entrySet().iterator();
		while(it.hasNext()) {
			Entry<Integer,Entrada> entrada=it.next();
			entrada.getValue().show();
		}
		if(id>1) {
			System.out.println("----------------------------------------");
		}
	}
	
	/**
	 * Escribe en un fichero la representaci�n de una entrada.
	 * @param fich Fichero donde se va a escribir.
	 * @param primera_escritura Si es la primera escritura o no.
	 */
	public void write(FileWriter fich,boolean primera_escritura) {
		if(id==0) {
			escribirFichero(fich,"TABLA DE PALABRAS RESERVADAS #" + id + ":\n",primera_escritura);
		}
		else if(id==1){
			escribirFichero(fich,"TABLA PRINCIPAL #" + id + ":\n",primera_escritura);
		}
		else {
			escribirFichero(fich,"TABLA LOCAL #" + id + ":\n",primera_escritura);
		}
		Iterator<Entry<Integer,Entrada>> it=ts.entrySet().iterator();
		while(it.hasNext()) {
			Entry<Integer,Entrada> entrada=it.next();
			entrada.getValue().write(fich,primera_escritura);
		}
		if(id>1) {
			escribirFichero(fich,"----------------------------------------\n",primera_escritura);
		}
	}
	
	/**
	 * Destruye la tabla de s�mbolos.
	 */
	public void destroy() {
		Iterator<Entry<Integer,Entrada>> it=ts.entrySet().iterator();
		while(it.hasNext()) {
			Entry<Integer,Entrada> entrada=it.next();
			//ts.remove(entrada.getKey());
			it.remove();
		}
		if(id<2) {
			pos=1;
		}
		else {
			pos=-1;
		}
	}
	
	/**
	 * Encapsulamiento de la funci�n write de la clase FileWriter.
	 * @param fich Fichero en el que se va a escribir.
	 * @param texto Texto que se va a escribir.
	 * @param primera_escritura Si es la primera escritura o no.
	 */
	private void escribirFichero(FileWriter fich,String texto,boolean primera_escritura) {
		if(!primera_escritura) {
			try {
				fich.write(texto);
			} catch (IOException e) {
				System.err.println("Ha habido un error en la entrada-salida del fichero.");
			}
		}
		else {
			try {
				fich.append(texto);
			} catch (IOException e) {
				System.err.println("Ha habido un error en la entrada-salida del fichero.");
			}
		}
	}
}
