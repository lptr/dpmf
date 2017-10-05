// toall : a stringTokenizer qrv�ra nem tudja support�lni a commenteket, ez�rt g�nyolok egy ilyen m�jert :)

/** Kommentezett adatfolyam parseold�s�ra alkalmas oszt�ly. Az adatfolyamra annyi megszor�t�s van, hogy jelenleg ha
 * az adatfolyamb�l olvasva, a token a buffer v�g�be �tk�zik, akkor, mivel nem �ll m�dj�ban k�relmezni az adatfolyam
 * folytat�s�t, �gyveszi, mintha a tokennek itt lenne v�ge, vagyis tulajdonk�ppen, �gy viselkedik, mintha a buffer v�ge
 * whitespace lenne. Ha �gy olvasunk a bufferb�l, hogy abban m�r nincsen Token, akkor a "" �res stringet kapjuk vissza.
 * A buffer tartalma b�rmikor tov�bb b�v�thet�, jelenleg csup�n string f�zhet� hozz�...
 */
class parser extends Object {
	/**
	 * ez a String t�rolja a parseoland� karaktereket. 
	 */
	private String dataString;
	/**
	 * dataStringen bel�li poz�ci�. az�rt van erre sz�ks�g, mert hosszabb Stringek eset�n kivitelezhetetlen, hogy
	 * az ember karakterenk�nt, vagy ak�r 5 karakterenk�nt t�r�lje az els� n karaktert. Ugyanis ilyenkor l�trej�n
	 * egy �j String objektum, �tm�sol�dik bele a r�gi. Ez mondjuk m�r 10000 karakteres String eset�n nem annyira
	 * elhanyagolhat� egy jelens�g. Ezt kik�sz�b�lend� haszn�lom a dataIndexet. Persze ez �gy megint nem j�,
	 * lehetne mondani r�, merthogy ilyenkor meg sok mem�ri�t eszik, hogy a string m�r �rdektelen r�sze m�g mind�g
	 * a mem�ri�ban van. Az aranyk�z�p�t, ha mondjuk a dataIndex �rt�k�nek 5000 f�l� emelked�se eset�n t�r�lj�k
	 * a dataString elej�t... </I>(ez ut�bbi m�g nincs implement�lva)</I><br>
	 * Hogy ez a var�zsl�s l�thatatlan maradjon a f�ggv�nyek sz�m�ra, illetve k�nyelmi szempontokb�l, 
	 * �jradefini�ltam az oszt�lyon bel�l a String oszt�ly felhaszn�lt f�ggv�nyeit...
	 */
	private int dataIndex;
	/**
	 * Jelzi hogy a legut�bb, mikor a buffer ki�r�lt, �pp comment k�zep�n voltunk.
	 */
	private boolean comment;

	/**
	 * A whitespace karakterek egy String-be f�zve.<br>
	 * <I>megfontoland� t�bbkarakteres whitespacek support�l�sa, b�r ez ak�r kv�zi baroms�g :)</I>
	 */
	static private String whiteSpace = " \n\t\r";
	/**
	 * A comment elej�t jelz� String.
	 */
	static private String commentBegin = "/*";
	/**
	 * A comment v�g�t jelz� String.
	 */
	static private String commentEnd = "*/";

	/**
	 * A bufferbe t�rt�n� �r�s el�tt egy ilyen szepar�torstringet helyez el a rendszer. Kiv�tel ezal�l a
	 * konstruktorban megadott buffertartalom (a buffer kezd� tartalma)
	 */
	static private String additionSeparator = " ";

	/**
	 * M�k�d�se azonos a dataString.substring(dataIndex) Stringre megh�vott azonos nev� f�ggv�nny�vel.
	 */
	private int indexOf(int ch) {
		return indexOf(ch, 0);
	}

	/**
	 * M�k�d�se azonos a dataString.substring(dataIndex) Stringre megh�vott azonos nev� f�ggv�nny�vel.
	 */
	private int indexOf(String str) {
		return indexOf(str, 0);
	}

	/**
	 * M�k�d�se azonos a dataString.substring(dataIndex) Stringre megh�vott azonos nev� f�ggv�nny�vel.
	 */
	private int indexOf(String str, int fromIndex) {
		int temp = dataString.indexOf(str, fromIndex + dataIndex);
		if (temp==-1) 
			return temp;
		else
			return temp - dataIndex;
	}

	/**
	 * M�k�d�se azonos a dataString.substring(dataIndex) Stringre megh�vott azonos nev� f�ggv�nny�vel.
	 */
	private int charAt(int index) {
		return dataString.charAt(index + dataIndex);
	}


	/**
	 * M�k�d�se azonos a dataString.substring(dataIndex) Stringre megh�vott azonos nev� f�ggv�nny�vel.<br>
	 * </I>elm�letileg az �rt�ke nem lehet negat�v, ez akkor fordulna el� amikor a stringben t�lszaladtunk. ez viszont csak akkor lehet ha bugos az oszt�ly. ez j� es�llyel nem �ll fenn:)</I>
	 */
	private int length() {
		return dataString.length() - dataIndex;
	}

	/**
	 * M�k�d�se azonos a dataString.substring(dataIndex) Stringre megh�vott azonos nev� f�ggv�nny�vel.
	 */
	private int indexOf(int ch, int fromIndex) {
		int temp = dataString.indexOf(ch,fromIndex + dataIndex);
		if (temp==-1)
			return temp;
		else
			return temp - dataIndex;
	}

	/**
	 * M�k�d�se azonos a dataString.substring(dataIndex) Stringre megh�vott azonos nev� f�ggv�nny�vel.
	 */
	private String substring(int from) {
		return dataString.substring(from + dataIndex);
	}

	/**
	 * M�k�d�se azonos a dataString.substring(dataIndex) Stringre megh�vott azonos nev� f�ggv�nny�vel.
	 */
	private String substring(int from, int to) {
		return dataString.substring(from + dataIndex, to + dataIndex);
	}

	/**
	 * legels� el�fordul�st keres a set ak�rmelyik karakter�re.
	 * pl.: WhiteSpacek keres�s�re...
	 */
	private int indexOfSet(String set) {
		int retValue = -1;
		int counter = 0;
		while (counter<set.length()) {
			int temp = indexOf(set.charAt(counter++));
			if ( retValue == -1 || (temp<retValue && temp!=-1)) retValue=temp;
		}
		return retValue;
	}

	/**
	 * legels� el�fordul�st keres a set ak�rmelyik karakter�re, a dataStringen bel�li megadott poz�ci�j�t�l.<br>
	 * pl.: WhiteSpacek keres�s�re...
	 */
	private int indexOfSet(String set, int fromIndex) {
		int retValue = -1;
		int counter = 0;
		while (counter<set.length()) {
			int temp = indexOf(set.charAt(counter++),fromIndex);
			if ( retValue == -1 || (temp<retValue && temp!=-1)) retValue=temp;
		}
		return retValue;
	}

	/**
	 * megkeresi a legels� karaktert ami nincs a param�terk�nt �tadott halmazban
	 */
	private int indexNotOfSet(String set) {
		int index = 0;
		while (index<length()) {
			if (set.indexOf(charAt(index))==-1) return index;
			index++;
		}
		return -1;		
	}

	/**
	 * Kommentek �tugr�sa. Amennyien �pp kommentben vagyunk, vagy �pp egy komment kezd�dik, akkor ezt a kommentet megpr�b�lja
	 * �tugrani, ha a buffer v�g�re �rve, m�g mind�g nem tal�ltuk meg a komment v�g�t azonos�t� karaktersorozatot, a
	 * {@link comment} v�ltoz� �rt�ke true lesz, ezzel jelezz�k, hogy mikor legk�zelebb h�v�dik meg ez a f�ggv�ny, alapb�l
	 * �gy vegye, hogy komment elej�n vagyunk...
	 */
	private boolean skipComment() {
		if (indexOf(commentBegin) == 0 || comment) {
			if (!comment) dataIndex += commentBegin.length();
			if (indexOf(commentEnd) == -1) { // ez g�zos lehet
				comment = true;
				dataIndex += length();
			} else {
				comment = false;
				dataIndex += indexOf(commentEnd) + commentEnd.length();
			}
			return true;
		} else
			return false;
	}
	
	/**
	 * T�nyleges whitespace-k �tugr�sa. A whitespace karaktereket a {@link parser#whiteSpace} v�ltoz� t�rolja.
	 */
	private boolean skipOnlyWhitespace() {
		int index = indexNotOfSet(whiteSpace);
		if (index == -1)
			index = length();
		if (index == 0)
			return false;
		else {
			dataIndex += index;
			return true;
		}
	}

	/**
	 * Az oszt�ly terminol�gi�j�ban whitespace-nek min�s�l a komment �s a t�nylegesen whitespacenek nevezett karakterek.
	 * A komment t�gabb �rtelemben felfoghat� tulajdonk�ppen egy olyan whitespace jelleg� elemnek, ami t�bb karakterb�l
	 * �llhat, �s hossza v�ltoz� lehet :) (tudom, baroms�g... :))
	 */
	private void skipWhitespace() {
		while (skipComment() || skipOnlyWhitespace()); // am�g whitespace-t �s komment-et sem tal�lunk, ism�telj�k a ciklust...
	}

/*				*/
/* itt kezd�dnek a public met�dusok 	*/
/*				*/

	/**
	 * soronk�vetkez� Token kiolvas�sa a bufferb�l. A buffermutat� �rt�k�t m�dos�tja
	 */
	public String getNextToken() {
		String temp = peekNextToken();
		dataIndex += temp.length();
		return temp;
	}

	/**
	 * a soronk�vetkez� Tokennek t�r vissza. kiolvas�s a bufferb�l nem k�vetkezik be
	 */
	public String peekNextToken() {
		skipWhitespace();
		int index = indexOfSet(whiteSpace);
		if (index == -1)
			index = length();
		String next = substring(0,index);
		if (!next.equals("")) {// "" csak akkor lehet ha a stream v�g�hez �rt�nk
			if (next.charAt(0)=='*') // a * mint k�l�n token
				next = substring(0,1); 
		}
		if (next.indexOf(commentBegin) != -1) // nem adhat vissza 0-t, akkor a skipwhitespace lev�gta a commentet.., ez a skipWhiteSpace feladata
			next = substring(0,next.indexOf(commentBegin));
		return next;
	}

	/**
	 * alapkonstruktor. �res bufferrel inicializ�l.
	 */
	public parser() {
		dataString = "";
		dataIndex = 0;
		comment = false;
	}

	/**
	 * a param�terk�nt kapott Stringgel inicializ�lja a buffert.
	 */
	public parser(String initString) {
		dataString = initString;
		dataIndex = 0;
		comment = false;
	}

	/**
	 * �j string hozz�f�z�se a bufferhez. A hozz�f�zend� string el� besz�rja az {@link parser#additionalSeparator}-t
	 */
	public void addString(String additionalString) {
		dataString += additionSeparator + additionalString; // must be a whitespace i think :)
	}

	/**
	 * visszaadja, a buffer v�g�re �rt�nk-e.
	 */
	public boolean EOF() {
		skipWhitespace();
		return length()==0;
	}
}