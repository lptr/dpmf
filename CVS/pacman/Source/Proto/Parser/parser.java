// toall : a stringTokenizer qrvára nem tudja supportálni a commenteket, ezért gányolok egy ilyen májert :)

/** Kommentezett adatfolyam parseoldására alkalmas osztály. Az adatfolyamra annyi megszorítás van, hogy jelenleg ha
 * az adatfolyamból olvasva, a token a buffer végébe ütközik, akkor, mivel nem áll módjában kérelmezni az adatfolyam
 * folytatását, úgyveszi, mintha a tokennek itt lenne vége, vagyis tulajdonképpen, úgy viselkedik, mintha a buffer vége
 * whitespace lenne. Ha úgy olvasunk a bufferbõl, hogy abban már nincsen Token, akkor a "" üres stringet kapjuk vissza.
 * A buffer tartalma bármikor tovább bõvíthetõ, jelenleg csupán string fûzhetõ hozzá...
 */
class parser extends Object {
	/**
	 * ez a String tárolja a parseolandó karaktereket. 
	 */
	private String dataString;
	/**
	 * dataStringen belüli pozíció. azért van erre szükség, mert hosszabb Stringek esetén kivitelezhetetlen, hogy
	 * az ember karakterenként, vagy akár 5 karakterenként törölje az elsõ n karaktert. Ugyanis ilyenkor létrejön
	 * egy új String objektum, átmásolódik bele a régi. Ez mondjuk már 10000 karakteres String esetén nem annyira
	 * elhanyagolható egy jelenség. Ezt kiküszöbölendõ használom a dataIndexet. Persze ez így megint nem jó,
	 * lehetne mondani rá, merthogy ilyenkor meg sok memóriát eszik, hogy a string már érdektelen része még mindíg
	 * a memóriában van. Az aranyközépút, ha mondjuk a dataIndex értékének 5000 fölé emelkedése esetén töröljük
	 * a dataString elejét... </I>(ez utóbbi még nincs implementálva)</I><br>
	 * Hogy ez a varázslás láthatatlan maradjon a függvények számára, illetve kényelmi szempontokból, 
	 * újradefiniáltam az osztályon belül a String osztály felhasznált függvényeit...
	 */
	private int dataIndex;
	/**
	 * Jelzi hogy a legutóbb, mikor a buffer kiürült, épp comment közepén voltunk.
	 */
	private boolean comment;

	/**
	 * A whitespace karakterek egy String-be fûzve.<br>
	 * <I>megfontolandó többkarakteres whitespacek supportálása, bár ez akár kvázi baromság :)</I>
	 */
	static private String whiteSpace = " \n\t\r";
	/**
	 * A comment elejét jelzõ String.
	 */
	static private String commentBegin = "/*";
	/**
	 * A comment végét jelzõ String.
	 */
	static private String commentEnd = "*/";

	/**
	 * A bufferbe történõ írás elött egy ilyen szeparátorstringet helyez el a rendszer. Kivétel ezalól a
	 * konstruktorban megadott buffertartalom (a buffer kezdõ tartalma)
	 */
	static private String additionSeparator = " ";

	/**
	 * Mûködése azonos a dataString.substring(dataIndex) Stringre meghívott azonos nevû függvénnyével.
	 */
	private int indexOf(int ch) {
		return indexOf(ch, 0);
	}

	/**
	 * Mûködése azonos a dataString.substring(dataIndex) Stringre meghívott azonos nevû függvénnyével.
	 */
	private int indexOf(String str) {
		return indexOf(str, 0);
	}

	/**
	 * Mûködése azonos a dataString.substring(dataIndex) Stringre meghívott azonos nevû függvénnyével.
	 */
	private int indexOf(String str, int fromIndex) {
		int temp = dataString.indexOf(str, fromIndex + dataIndex);
		if (temp==-1) 
			return temp;
		else
			return temp - dataIndex;
	}

	/**
	 * Mûködése azonos a dataString.substring(dataIndex) Stringre meghívott azonos nevû függvénnyével.
	 */
	private int charAt(int index) {
		return dataString.charAt(index + dataIndex);
	}


	/**
	 * Mûködése azonos a dataString.substring(dataIndex) Stringre meghívott azonos nevû függvénnyével.<br>
	 * </I>elméletileg az értéke nem lehet negatív, ez akkor fordulna elõ amikor a stringben túlszaladtunk. ez viszont csak akkor lehet ha bugos az osztály. ez jó eséllyel nem áll fenn:)</I>
	 */
	private int length() {
		return dataString.length() - dataIndex;
	}

	/**
	 * Mûködése azonos a dataString.substring(dataIndex) Stringre meghívott azonos nevû függvénnyével.
	 */
	private int indexOf(int ch, int fromIndex) {
		int temp = dataString.indexOf(ch,fromIndex + dataIndex);
		if (temp==-1)
			return temp;
		else
			return temp - dataIndex;
	}

	/**
	 * Mûködése azonos a dataString.substring(dataIndex) Stringre meghívott azonos nevû függvénnyével.
	 */
	private String substring(int from) {
		return dataString.substring(from + dataIndex);
	}

	/**
	 * Mûködése azonos a dataString.substring(dataIndex) Stringre meghívott azonos nevû függvénnyével.
	 */
	private String substring(int from, int to) {
		return dataString.substring(from + dataIndex, to + dataIndex);
	}

	/**
	 * legelsõ elõfordulást keres a set akármelyik karakterére.
	 * pl.: WhiteSpacek keresésére...
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
	 * legelsõ elõfordulást keres a set akármelyik karakterére, a dataStringen belüli megadott pozíciójától.<br>
	 * pl.: WhiteSpacek keresésére...
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
	 * megkeresi a legelsõ karaktert ami nincs a paraméterként átadott halmazban
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
	 * Kommentek átugrása. Amennyien épp kommentben vagyunk, vagy épp egy komment kezdõdik, akkor ezt a kommentet megpróbálja
	 * átugrani, ha a buffer végére érve, még mindíg nem találtuk meg a komment végét azonosító karaktersorozatot, a
	 * {@link comment} változó értéke true lesz, ezzel jelezzük, hogy mikor legközelebb hívódik meg ez a függvény, alapból
	 * úgy vegye, hogy komment elején vagyunk...
	 */
	private boolean skipComment() {
		if (indexOf(commentBegin) == 0 || comment) {
			if (!comment) dataIndex += commentBegin.length();
			if (indexOf(commentEnd) == -1) { // ez gázos lehet
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
	 * Tényleges whitespace-k átugrása. A whitespace karaktereket a {@link parser#whiteSpace} változó tárolja.
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
	 * Az osztály terminológiájában whitespace-nek minõsül a komment és a ténylegesen whitespacenek nevezett karakterek.
	 * A komment tágabb értelemben felfogható tulajdonképpen egy olyan whitespace jellegû elemnek, ami több karakterbõl
	 * állhat, és hossza változó lehet :) (tudom, baromság... :))
	 */
	private void skipWhitespace() {
		while (skipComment() || skipOnlyWhitespace()); // amíg whitespace-t és komment-et sem találunk, ismételjük a ciklust...
	}

/*				*/
/* itt kezdõdnek a public metódusok 	*/
/*				*/

	/**
	 * soronkövetkezõ Token kiolvasása a bufferbõl. A buffermutató értékét módosítja
	 */
	public String getNextToken() {
		String temp = peekNextToken();
		dataIndex += temp.length();
		return temp;
	}

	/**
	 * a soronkövetkezõ Tokennek tér vissza. kiolvasás a bufferbõl nem következik be
	 */
	public String peekNextToken() {
		skipWhitespace();
		int index = indexOfSet(whiteSpace);
		if (index == -1)
			index = length();
		String next = substring(0,index);
		if (!next.equals("")) {// "" csak akkor lehet ha a stream végéhez értünk
			if (next.charAt(0)=='*') // a * mint külön token
				next = substring(0,1); 
		}
		if (next.indexOf(commentBegin) != -1) // nem adhat vissza 0-t, akkor a skipwhitespace levágta a commentet.., ez a skipWhiteSpace feladata
			next = substring(0,next.indexOf(commentBegin));
		return next;
	}

	/**
	 * alapkonstruktor. Üres bufferrel inicializál.
	 */
	public parser() {
		dataString = "";
		dataIndex = 0;
		comment = false;
	}

	/**
	 * a paraméterként kapott Stringgel inicializálja a buffert.
	 */
	public parser(String initString) {
		dataString = initString;
		dataIndex = 0;
		comment = false;
	}

	/**
	 * új string hozzáfûzése a bufferhez. A hozzáfûzendõ string elé beszúrja az {@link parser#additionalSeparator}-t
	 */
	public void addString(String additionalString) {
		dataString += additionSeparator + additionalString; // must be a whitespace i think :)
	}

	/**
	 * visszaadja, a buffer végére értünk-e.
	 */
	public boolean EOF() {
		skipWhitespace();
		return length()==0;
	}
}