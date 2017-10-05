// $Id: ourParser.java,v 1.5 2001/04/27 03:15:44 lptr Exp $
// $Date: 2001/04/27 03:15:44 $
// $Author: lptr $

package Util.Parser;

import java.io.*;
import java.lang.*;
import java.util.*;

/**
 * A StreamTokenizer �ltal�nos�t�sa. IMHO k�nyelmesebb, hekkeld ki forr�sb�l mitcsin�l... :)
 */
class myStreamTokenizer extends StreamTokenizer {

	myStreamTokenizer(Reader r) {
		super(r);
		resetSyntax();
		lowerCaseMode( false );
		eolIsSignificant( true );
		slashSlashComments( true );
		slashStarComments( true );
	}
	
	static String STD_WORD = "!-.0-~";
	static String STD_WSPC = " \n\r\t";
	
	/**
	 * Inicializ�lja a parsert string bemenetre.
	 */
	myStreamTokenizer(String is) {
		this(is, STD_WORD, STD_WSPC);
	}

	/**
	 * Inicializ�lja a parsert string bemenetre, a sz� karakterek megad�s�val 
	 */
	myStreamTokenizer(String is, String wordChars) {
		this(is, wordChars, STD_WSPC);
	}

	/**
	 * Inicializ�lja a parsert string bemenetre, a sz� karakterek megad�s�val,
	 * tov�bb� a whitespace karakterek is megadhat�ak.
	 */
	myStreamTokenizer(String is, String wordChars, String whitespaceChars) {
		this(new BufferedReader(new StringReader(is)));
		initWordChars(wordChars);
		initWhitespaceChars(whitespaceChars);
		slashSlashComments( true );
		slashStarComments( true );
	}

	/**
	 * Inicializ�lja a parsert bemeneti streamre.
	 */
	myStreamTokenizer(InputStream is) {
		this(is, STD_WORD, STD_WSPC);
	}

	/**
	 * Inicializ�lja a parsert bemeneti streamre, a sz� karakterek megad�s�val
	 */
	myStreamTokenizer(InputStream is, String wordChars) {
		this(is, wordChars, STD_WSPC);
	}

	/**
	 * Inicializ�lja a parsert bemeneti streamre, a sz� karakterek megad�s�val,
	 * tov�bb� a whitespace karakterek is megadhat�ak.
	 */
	myStreamTokenizer(InputStream is, String wordChars, String whitespaceChars) {
		this(new BufferedReader(new InputStreamReader(is)));
		initWordChars(wordChars);
		initWhitespaceChars(whitespaceChars);
		slashSlashComments( true );
		slashStarComments( true );
	}


	/**
	 * Be�ll�tja a param�terben kapott stringben szerepl� karaktereket, mint word karakterek.<br>
	 * A string karakterintervallumokat is tartalmazhat:<br>
	 * a-z  :  abcdefghijklmnopqrstuvwxyz<br>
	 * teh�t ha [a,b] intervallum �sszes karakter�t jel�lj�k, akkor a-b -t kell �rni.<br>
	 * a '-' �gy �rtelemszer�en csak a string legelej�n vagy legv�g�n szerepelhet<br>
	 */
	public void initWordChars(String s) {
		if (s==null) return;
		int pos = 0;

		while (pos<s.length())
		{
			int first = s.charAt(pos++);
			if (pos==s.length() || s.charAt(pos)!='-')
				wordChars(first,first);
			else {
				int second = ++pos==s.length()?first:s.charAt(pos++);
				wordChars(first,second);
			} 
		}
	}		

	/**
	 * Be�ll�tja a param�terben kapott stringben szerepl� karaktereket, mint whitespace karakterek.<br>
	 * A string karakterintervallumokat is tartalmazhat:<br>
	 * a-z  :  abcdefghijklmnopqrstuvwxyz<br>
	 * teh�t ha [a,b] intervallum �sszes karakter�t jel�lj�k, akkor a-b -t kell �rni.<br>
	 * a '-' �gy �rtelemszer�en csak a string legelej�n vagy legv�g�n szerepelhet<br>
	 */
	public void initWhitespaceChars(String s) {
		if (s==null) return;
		int pos = 0;

		while (pos<s.length())
		{
			int first = s.charAt(pos++);
			if (pos==s.length() || s.charAt(pos)!='-') {
				whitespaceChars(first,first);
			}
			else {
				int second = ++pos==s.length()?first:s.charAt(pos++);
				whitespaceChars(first,second);
			} 
		}
	}		

	/**
	 * Be�ll�tja a param�terben kapott stringben szerepl� karaktereket, mint ordinary karakterek.<br>
	 * A string karakterintervallumokat is tartalmazhat:<br>
	 * a-z  :  abcdefghijklmnopqrstuvwxyz<br>
	 * teh�t ha [a,b] intervallum �sszes karakter�t jel�lj�k, akkor a-b -t kell �rni.<br>
	 * a '-' �gy �rtelemszer�en csak a string legelej�n vagy legv�g�n szerepelhet<br>
	 */
	public void initOrdinaryChars(String s) {
		if (s==null) return;
		int pos = 0;

		while (pos<s.length())
		{
			int first = s.charAt(pos++);
			if (pos==s.length() || s.charAt(pos)!='-') {
				ordinaryChar(first);
			}
			else {
				int second = ++pos==s.length()?first:s.charAt(pos++);
				ordinaryChars(first,second);
			} 
		}
	}		
	
}


/**
 * heRegul�ris kifejez�s egy darabj�t t�rol� oszt�ly.
 *
 *	haRegul�ris kifejez�s egy darabja a k�vetkez�k�ppen n�z ki:
 *		- a kifejez�st k�rbe�lelheti {}, [], () z�r�jelek valamelyike. a sima z�r�jel opcion�lis, ak�r el is hagyhat�.
 *	a z�r�jelek jelent�se a k�vetkez�:
 *		[]				- opcion�lis elemek list�j�t jelenti, ezek az elemek min 0 max 1 alkalommal fordulhatnak el� a vizsg�lt kifejez�sben
 *		{}				- ism�tl�d� elemek. az elemek ak�rh�nyszor ism�tl�dhetnek, 0..inf intervalumon.
 *		() vagy semmi	- az elemek k�z�l az egyiknek illeszkednie kell
 *	a z�r�jeleken bel�l tal�lhat� karaktersorozatban pipe-al kell elv�lasztani a lista tagjait.
 *
 *	Egyk�t p�lda:
 *		[1|2|3|4]		- az 1,2,3,4 sz�mok valamelyike, vagy semmi
 *		{0|1}			- egy bin�ris sz�m, vagy semmi
 *
 *	A listaelemek k�z�tt tal�lhat� k�t speci�lis is, ezek:
 *		%n				- tetsz�leges sz�mot jel�l
 *		%c				- tetsz�leges karaktersorozatot jel�l
 *
 *  A a z�r�jeleken bel�l 33-126 k�d� ak�rmilyen karakter �llhat, ezal�l egyed�l a | vonal (ASCII 124) a kiv�tel.
 */
	class regExpItem extends Object {
		public String[] data;
		public int type;
		public int length;
		public boolean isNum;
		public boolean isChr;

	// RegExpItemTokenType-ok
		static final int REITT_ERR = -1;
		static final int REITT_OPTIONAL = 1;
		static final int REITT_REPEATING = 2;
		static final int REITT_NORMAL = 3;
		static final int REITT_NONE = 0; // csak itt van haszn�lva

	/**
	 * l�trehoz egy Stringben t�rolt heRegExp r�szkivejez�sb�l egy regExpItem objektumot. be�ll�tja isNum, isChr �rt�k�t
	 * valamint type-ot �s felt�lti a data Stringt�mb�t.
	 */
		regExpItem(String s) {
			isNum = isChr = false;
			if (s==null) {
				data = new String[0];
				type = REITT_ERR;
				length = 0;
			} else {
				type = REITT_NONE;
				switch (s.charAt(0)) {
				case '[':
						type = REITT_OPTIONAL;
						break;
					case '{':
						type = REITT_REPEATING;
						break;
					case '(':
						type = REITT_NORMAL;
				}
				if (type!=REITT_NONE)
					s = s.substring(1,s.length()-1);
				else 
					type = REITT_NORMAL;

				data = ourParser.tokenizeString(s,"!-{}~","|");
				String[] temp = new String[data.length];
				length = 0;
				for (int i=0; i<data.length; i++) {
					if (data[i].equals("%n")) {
						isNum = true;
					} else if (data[i].equals("%c")) {
						isChr = true;
					} else {
						temp[length]=data[i];length++;
					}
				}

				data = new String[length];
				for (int i=0; i<length; i++)
					data[i] = temp[i];
			}
		}

	/**
	 * megvizsg�lja a string illeszthet� e a regExpItemre.
	 */
		public boolean matchString(String str) {
			if (str == null) {
				return false;
			} else if (str.length()==0) {
				return false;
			} else if (isChr && !Character.isDigit(str.charAt(0))) {
				return true; //ak�rmit is karakternek vesz�nk :)
			} else if (isNum && Character.isDigit(str.charAt(0))) {
				return true;
			} else {
				int pos = 0;
				while (pos<length && !data[pos].equals(str))
					pos++;
				return pos!=length;
			}
		}
	}

public class ourParser extends myStreamTokenizer {
	public String[] lineTokens = null;

//	ourParser(Reader r) {
//		super(r);
//		System.out.println("DEBUUUG");
//		lineTokens = null;
//	}

	public ourParser(String s1) { super(s1); }
	public ourParser(String s1,String s2) { super(s1,s2); }
	public ourParser(String s1,String s2,String s3) { super(s1,s2,s3); }
	public ourParser(InputStream s1) { super(s1); }
	public ourParser(InputStream s1,String s2) { super(s1,s2); }
	public ourParser(InputStream s1,String s2,String s3) { super(s1,s2,s3); }
	/**
	 * El�rt�k a strem v�g�t
	 */
	public static int TT_EOS = 1;
	/**
	 * Minden rendben
	 */
	public static int TT_OK = 2;
	/**
	 * Hiba a parsing k�zben
	 */ 
	public static int TT_ERROR = 3;

//DEBUG
//	public boolean A = true;
	/**
	 * feldolgozza a k�vetkez� sort.
	 * @return TT_EOS if end of stream, TT_OK if next line parsed successfuly, TT_ERROR if there was an error.
	 */
	public int getNextLine() {
		LinkedList line = new LinkedList();
		int tokenType;
		try {
				while (StreamTokenizer.TT_EOF!=(tokenType=nextToken()) && !(StreamTokenizer.TT_EOL==tokenType && line.size()!=0)) {

//					if (A) System.out.println("" + tokenType + ":" + sval);
					switch (tokenType) {
						case StreamTokenizer.TT_WORD:
							line.add(sval);
							break;
//						case StreamTokenizer.TT_NUMBER: // ez a 3 sor nem haszn�lt, mert 
//							line.add("" + st.nval);		// a sz�mot is stringnek vessz�k
//							break;
						default :
							if (!(tokenType==StreamTokenizer.TT_EOL && line.size()==0))
								line.add("UNKNOWN");
					}
				}
		} catch (Exception e) {
			System.out.println("ERROR in getNextLine$");
			return TT_ERROR;
		}

		lineTokens = new String[line.size()];
		for (int i=0; i<line.size(); i++)
			lineTokens[i] = (String)line.get(i);

		if (StreamTokenizer.TT_EOF==tokenType)
			return TT_EOS;
		else
			return TT_OK;
	}

	/**
	 * tokenekre bont egy stringet
	 */
	public static String[] tokenizeString (String s) {
		ourParser op = new ourParser(s);
//		op.A = false;
		op.getNextLine();
		return op.lineTokens;
	}

	/**
	 * tokenekre bont egy stringet, a sz� karakterek defini�lhat�ak.
	 */
	public static String[] tokenizeString (String s, String wordChars) {
		ourParser op = new ourParser(s, wordChars);
//		op.A = false;
		op.getNextLine();
		return op.lineTokens;
	}

	/**
	 * tokenekre bont egy stringet, a sz� karakterek �s whitespacek defini�lhat�ak.
	 */
	public static String[] tokenizeString (String s, String wordChars, String whitespaceChars) {
		ourParser op = new ourParser(s, wordChars, whitespaceChars);
//		op.A = false;
		op.getNextLine();
		return op.lineTokens;
	}

	/**
	 * megvizsg�lja az s string illeszthet� e exp heRegul�ris kifejez�sre
	 */
	public static boolean matchString(String s, String exp) {
		ourParser op = new ourParser(s);
		op.getNextLine();
		return op.matchLine(exp);
	}
	// ezt m�g meg k�ne irni wordChars �s whitespaceChars extensionnal

	// �sszead k�t String t�mb�t. a k�t t�mbnek inicializ�lva kell lennie, k�l�nben nagy g�z van...
	private static String[] addStringArray(String[] a, String[] b) {
		String[] temp = new String[a.length + b.length];
		int pos;
		for(pos=0; pos<a.length; pos++)
			temp[pos] = a[pos];
		for(pos=0; pos<b.length; pos++)
			temp[pos + a.length] = b[pos];
		return temp;
	}

	//debug
	private static void printStringArray(String[] a) {
		if (a==null)
			return;
		else if (a.length==0)
			return;
		else
			for (int i=0; i<a.length; i++)
				System.out.println(a[i]);
	}

	// kisz�ri a b t�mbb�l azokat az elemeket, amik a-val kezd�dnek
	private static String[] startsWith(String[] b, String a) {
		if (b==null) {
			return new String[0];
		} else {
			String[] temp = new String[b.length];
			int pos = 0;
			for(int i=0; i<b.length; i++)
				if (b[i].startsWith(a))
					temp[pos++]=b[i];
			String[] temp2 = new String[pos];
			for(int i=0; i<pos; i++)
				temp2[i]=temp[i];
			return temp2;
		}
	}
			
	// az a t�mb Stringjeinek elej�re leghosszabban illeszked� sz�t adja vissza
	private static String commonPart(String[] a) {
		if (a==null)
			return "";
		else if (a.length==0)
			return "";
		else {
			int len=a[0].length();
			int counter=0;
			for(counter=0; counter<a.length; counter++) {
				if (a[counter].length()<len) len = a[counter].length();
				for (int i=0; i<len; i++) // lehetne a[0].startsWidth(a[c].substring(0,len))-t is haszn�lni
					if (a[0].charAt(i)!=a[counter].charAt(i)) len=i;
			}
			return a[0].substring(0,len);
		}
	}

	/**
	 * megvizsg�l egy stringet a kapott heRegExp-el, visszat�r�si �rt�ke "" amennyiben nem volt illeszked�s, vagy nem egy�rtelm�
	 * milyen karakter k�vetkezhet.
	 * !! c�lszer� volna �gy meg�rni, null-t ad vissza, ha nincs egyez�s!!
	 */
	public static String completeString(String str, String exp) {


		String[] matcher = ourParser.tokenizeString(exp,"!-~"); // feldarabolja az str-t
		String[] lineTokens = ourParser.tokenizeString(str,"!-~"); // darabolja a heRegExp-et

		// !! ez igy cs�nya
		if (matcher.length==0 || lineTokens.length==0) return ""; // ha b�rmelyik string �res, es�ly sincs egyez�sre


		int linePos = 0; // egyez�skeres�s az elej�r�l indul
		int matcherPos = 0; // egyez�skeres�s az elej�r�l indul
//!!!!! mitcsin�lhat "" bemenetre  		// wsAtEnd true az �rt�ke ha whitespace van a legutols� token ut�n
		boolean wsAtEnd = !str.regionMatches(str.length()-lineTokens[lineTokens.length-1].length(),
											lineTokens[lineTokens.length-1],
											0, lineTokens[lineTokens.length-1].length());
		// wsAtEnd-nek az a l�nyege, ha nincs ws a sz� v�g�n, akkor a legutols� token f�lbemaradt, ilyenkor m�s szab�lyok vonatkoznak a kieg�sz�t�sre

		try {
				while ((linePos<lineTokens.length && wsAtEnd) || (linePos<lineTokens.length-1 && !wsAtEnd)) //a legutols� eg�sz tokenig
				{
					regExpItem examine = new regExpItem(matcher[matcherPos]); // beolvassuk a k�vetkez� heRegExp tokent

					boolean found = examine.matchString(lineTokens[linePos]); // van e egyez�s?
						
					switch (examine.type) {
						case regExpItem.REITT_OPTIONAL : // []-be rakott r�sz
							if (found)
								linePos++;
							matcherPos++;
							break;
						case regExpItem.REITT_REPEATING : // {} - ak�rh�nyszor ism�tl�dhet
							if (found)
								linePos++;
							else
								matcherPos++;
							break;
						case regExpItem.REITT_NORMAL : // () vagy sima, valamelyikre egyez�s kell vagy meghaltunk
							if (found) {
								linePos++;
								matcherPos++;
							} else {
								throw new Exception(); // elakadtunk a matchingben
							}
					}
				}
		} catch (Exception e) {
			return ""; // valami patterhiba, vagy a matchingben elakadtunk, vagy hosszabb a string mint a regexp (teh�t t�lfutott a matcher)
		}
/*	A lineTokens �s a matcher list�kon bel�li helyzet:
		ha wsAtEnd==true
			ha OPTIONAL vagy NORMAL t�pus� volt az utols� match, akkor ad�dhat olyan helyzet, hogy itt a matchPos==matcher.length
			elenkez� esetben m�g matchPos<matcher.length
		ha wsAtEnd==false
			a helyzet megint ugyan az, annyi difivel, hogy a lineTokensben van m�g egy 
*/
		if (matcherPos==matcher.length) return ""; // v�ge a patternnek de m�g tart a string. sim�n szop�s... nincs illeszked�s
/*	Itt most teh�t m�r legal�bb egy elem van a matcherben.
*/
		String plus = ""; // ebben gy�jtj�k mit lehet a v�g�re biggyeszteni
		try {
/*	El�rt�nk a string v�g�re, ha wsAtEnd �rt�ke true, az annyit jelent, hogy siker�lt v�gig matchet tal�lnuk, vagyis tiszt�n
	folytat�dik az illeszt�skeres�s. Amennyiben wsAtEnd==false, a string nem volt lez�rva, k�vetkez�sk�pp a legutols� tokenre
	m�g nem tudtunk egyez�st keresni. Ilyenkor teh�t el�sz�r azt kell vizsg�lni, ennek az utols� kis foszl�nynak van-e
	egy�ltal�n olyan kieg�sz�t�se, ami illeszthet� r�. Ha a foszl�nyra sikeresen illesztett�nk, �gy hogy az egy�rtelm� illeszt�s
	volt, akkor a wsAtEnd==true algoritmus�val folytathatjuk a fut�st...

	wsAtEnd==false algoritmusa :
			am�g [] vagy {} j�n
			   ha %c vagy %n akkor v�gezt�nk
			   �sszegy�jtj�k �ket egy list�ba, csak az kell aminek az elej�re illeszthet� az uts� Token�nk
// ezek tot�l nem kellenek
//			ha egyelem� a a list�nk wsAtEnd==true-n folytatjuk  (plusba bele)
//			ha t�bbelem� akkor keress�k a legr�videbb k�z�s r�szt, �s ezzel v�ge az illeszt�snekb (plus)
//			itt akkor vagyunk ha a lista �res
// ez az 5 sor nem kellett
			most NORMALra tesztel�nk
			csak az kell ha Token eleje illeszthet� r�
			ha �res a lista fail
			ha t�bbelem� akkor keress�k a legr�videbb k�z�s r�szt, �s ezzel v�ge az illeszt�snek (plus)
			ha egyelem� akkor wsAtEnd=true;

	wsAtEnd==true algoritmusa :
			am�g egyelem� normal j�n, nem %c, %n
				plus-hoz hozz�adjuk
			amig [] vagy {} j�n
				list�hoz hozz�adjuk
			a normalt is hozz�adjuk a list�hoz
			megn�zz�k meddig egyeztethet�ek, ez megy plus-ba
*/
			regExpItem examine = null;

// ez a r�sz akkor j�tszik, ha az utols� token f�lbemaradt
			if (!wsAtEnd) {
				String head = lineTokens[linePos]; // ez a token eleje

				boolean isNum = Character.isDigit(head.charAt(0)); // ha a a token eleje sz�m, akkor a token sz�m (charAt(0) biztos l�tezik)
				String[] list = new String[0]; // ide gy�lik a lista
				do {
					examine = new regExpItem((matcherPos<matcher.length?matcher[matcherPos++]:null)); // am�g van heRegExp token, azt parseoljuk, ha elfogyott, akkor �res tokent parseoltatunk
					if (examine.isChr && !isNum) return "";//plus;	//match van, v�g�re hozz�rakni nemtudunk, mert %n illeszkedik
					if (examine.isNum && isNum) return "";//plus;	//match van, v�g�re hozz�rakni nemtudunk, mert %c illeszkedik
//					if (examine.type==regExpItem.REITT_OPTIONAL || examine.type==regExpItem.REITT_REPEATING)
						list = addStringArray(list, examine.data);
				} while (examine.type==regExpItem.REITT_OPTIONAL || examine.type==regExpItem.REITT_REPEATING);

				list = startsWith(list, head); // a list�b�l csak az kell ami illeszkedik head-re
				
				if (list.length>1) { // ha t�bb egyez�s is van
					plus += commonPart(list).substring(head.length());
					return plus; //match van, nem egy�rtelm� a folytat�s
				} else if (list.length==0) { // nem illeszkedik a patternra
					throw new Exception(); // sim�n lehaltunk
				} else { // egy�rtelm� az egyez�s, innen kezdve �gy mehet�nk, mintha wsAtEnd==true lett volna m�r mi�ta
					plus += list[0].substring(head.length());
					wsAtEnd = true;	// ha itt tartunk, akkor egy�rtelm�en ki lehett eg�sz�teni a f�bemaradt uts� param�tert.
				}
			}

			if (wsAtEnd) {
				// a norm�l toldal�kokat hozz�vessz�k
				examine = new regExpItem((matcherPos<matcher.length?matcher[matcherPos++]:null));
				while (examine.type==regExpItem.REITT_NORMAL && examine.length==1) {
					plus += " " + examine.data[0];
					examine = new regExpItem((matcherPos<matcher.length?matcher[matcherPos++]:null));
				}
				if (examine.isChr || examine.isNum) return plus;

				// addig gy�jt�nk innen list�ba, am�g elhagyhat� param�terek j�nnek
				String[] list = new String[0];
				while (examine.type==regExpItem.REITT_OPTIONAL || examine.type==regExpItem.REITT_REPEATING) {
					if (examine.isChr) return plus;//KI KELL L�PNI, MATCH v�ge
					if (examine.isNum) return plus;//KI KELL L�PNI, MATCH v�ge
					list = addStringArray(list, examine.data);
					examine = new regExpItem((matcherPos<matcher.length?matcher[matcherPos++]:null));
				}
				// most vagy egy normal, vagy egy error van az examineban, ez ut�bbi eset�n �reslist�t ad az examine.data
				list = addStringArray(list, examine.data);
				String temp = commonPart(list);
				if (temp.length()>0)
					plus += " " + temp; // a lista k�z�s r�sze ker�l plusba, v�ge
			}
		} catch (Exception e) {
			return plus;
			// ha az utols� f�lbemaradt patternra nem tal�ltunk illeszt�st.
		}
			
		return plus; // plusban van a kieg�sz�t�s, ezt sim�n visszaadjuk
	}
	
	/**
	 * megvizsg�lja az �ppen feldolgozott sor illeszthet� e exp-re.
	 * m�k�d�si elve ugyan az mint a completeString-�, annyiban egyszer�bb, hogy itt nem kell az utols� token lez�ratlans�ga miatt sz�vni
	 *      
	 */
	public boolean matchLine(String exp) {
		String[] matcher = ourParser.tokenizeString(exp/*,"!-~"*/);
		int linePos = 0;
		int matcherPos = 0;
		try {
				while (linePos<lineTokens.length || matcherPos<matcher.length)
				{
					regExpItem examine = new regExpItem(matcher[matcherPos]);

					boolean found = false;
					if (linePos<lineTokens.length)
						found = examine.matchString(lineTokens[linePos]);
						
					switch (examine.type) {
						case regExpItem.REITT_OPTIONAL :
							if (found)
								linePos++;
							matcherPos++;
							break;
						case regExpItem.REITT_REPEATING :
							if (found)
								linePos++;
							else
								matcherPos++;
							break;
						case regExpItem.REITT_NORMAL :
							if (!found)
								throw new Exception();
							linePos++;
							matcherPos++;
					}
				}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}