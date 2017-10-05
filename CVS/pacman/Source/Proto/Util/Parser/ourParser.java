// $Id: ourParser.java,v 1.5 2001/04/27 03:15:44 lptr Exp $
// $Date: 2001/04/27 03:15:44 $
// $Author: lptr $

package Util.Parser;

import java.io.*;
import java.lang.*;
import java.util.*;

/**
 * A StreamTokenizer általánosítása. IMHO kényelmesebb, hekkeld ki forrásból mitcsinál... :)
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
	 * Inicializálja a parsert string bemenetre.
	 */
	myStreamTokenizer(String is) {
		this(is, STD_WORD, STD_WSPC);
	}

	/**
	 * Inicializálja a parsert string bemenetre, a szó karakterek megadásával 
	 */
	myStreamTokenizer(String is, String wordChars) {
		this(is, wordChars, STD_WSPC);
	}

	/**
	 * Inicializálja a parsert string bemenetre, a szó karakterek megadásával,
	 * továbbá a whitespace karakterek is megadhatóak.
	 */
	myStreamTokenizer(String is, String wordChars, String whitespaceChars) {
		this(new BufferedReader(new StringReader(is)));
		initWordChars(wordChars);
		initWhitespaceChars(whitespaceChars);
		slashSlashComments( true );
		slashStarComments( true );
	}

	/**
	 * Inicializálja a parsert bemeneti streamre.
	 */
	myStreamTokenizer(InputStream is) {
		this(is, STD_WORD, STD_WSPC);
	}

	/**
	 * Inicializálja a parsert bemeneti streamre, a szó karakterek megadásával
	 */
	myStreamTokenizer(InputStream is, String wordChars) {
		this(is, wordChars, STD_WSPC);
	}

	/**
	 * Inicializálja a parsert bemeneti streamre, a szó karakterek megadásával,
	 * továbbá a whitespace karakterek is megadhatóak.
	 */
	myStreamTokenizer(InputStream is, String wordChars, String whitespaceChars) {
		this(new BufferedReader(new InputStreamReader(is)));
		initWordChars(wordChars);
		initWhitespaceChars(whitespaceChars);
		slashSlashComments( true );
		slashStarComments( true );
	}


	/**
	 * Beállítja a paraméterben kapott stringben szereplõ karaktereket, mint word karakterek.<br>
	 * A string karakterintervallumokat is tartalmazhat:<br>
	 * a-z  :  abcdefghijklmnopqrstuvwxyz<br>
	 * tehát ha [a,b] intervallum összes karakterét jelöljük, akkor a-b -t kell írni.<br>
	 * a '-' így értelemszerûen csak a string legelején vagy legvégén szerepelhet<br>
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
	 * Beállítja a paraméterben kapott stringben szereplõ karaktereket, mint whitespace karakterek.<br>
	 * A string karakterintervallumokat is tartalmazhat:<br>
	 * a-z  :  abcdefghijklmnopqrstuvwxyz<br>
	 * tehát ha [a,b] intervallum összes karakterét jelöljük, akkor a-b -t kell írni.<br>
	 * a '-' így értelemszerûen csak a string legelején vagy legvégén szerepelhet<br>
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
	 * Beállítja a paraméterben kapott stringben szereplõ karaktereket, mint ordinary karakterek.<br>
	 * A string karakterintervallumokat is tartalmazhat:<br>
	 * a-z  :  abcdefghijklmnopqrstuvwxyz<br>
	 * tehát ha [a,b] intervallum összes karakterét jelöljük, akkor a-b -t kell írni.<br>
	 * a '-' így értelemszerûen csak a string legelején vagy legvégén szerepelhet<br>
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
 * heReguláris kifejezés egy darabját tároló osztály.
 *
 *	haReguláris kifejezés egy darabja a következõképpen néz ki:
 *		- a kifejezést körbeölelheti {}, [], () zárójelek valamelyike. a sima zárójel opcionális, akár el is hagyható.
 *	a zárójelek jelentése a következõ:
 *		[]				- opcionális elemek listáját jelenti, ezek az elemek min 0 max 1 alkalommal fordulhatnak elõ a vizsgált kifejezésben
 *		{}				- ismétlõdõ elemek. az elemek akárhányszor ismétlõdhetnek, 0..inf intervalumon.
 *		() vagy semmi	- az elemek közül az egyiknek illeszkednie kell
 *	a zárójeleken belül található karaktersorozatban pipe-al kell elválasztani a lista tagjait.
 *
 *	Egykét példa:
 *		[1|2|3|4]		- az 1,2,3,4 számok valamelyike, vagy semmi
 *		{0|1}			- egy bináris szám, vagy semmi
 *
 *	A listaelemek között található két speciális is, ezek:
 *		%n				- tetszõleges számot jelöl
 *		%c				- tetszõleges karaktersorozatot jelöl
 *
 *  A a zárójeleken belül 33-126 kódú akármilyen karakter állhat, ezalól egyedül a | vonal (ASCII 124) a kivétel.
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
		static final int REITT_NONE = 0; // csak itt van használva

	/**
	 * létrehoz egy Stringben tárolt heRegExp részkivejezésbõl egy regExpItem objektumot. beállítja isNum, isChr értékét
	 * valamint type-ot és feltölti a data Stringtömböt.
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
	 * megvizsgálja a string illeszthetõ e a regExpItemre.
	 */
		public boolean matchString(String str) {
			if (str == null) {
				return false;
			} else if (str.length()==0) {
				return false;
			} else if (isChr && !Character.isDigit(str.charAt(0))) {
				return true; //akármit is karakternek veszünk :)
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
	 * Elértük a strem végét
	 */
	public static int TT_EOS = 1;
	/**
	 * Minden rendben
	 */
	public static int TT_OK = 2;
	/**
	 * Hiba a parsing közben
	 */ 
	public static int TT_ERROR = 3;

//DEBUG
//	public boolean A = true;
	/**
	 * feldolgozza a következõ sort.
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
//						case StreamTokenizer.TT_NUMBER: // ez a 3 sor nem használt, mert 
//							line.add("" + st.nval);		// a számot is stringnek vesszük
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
	 * tokenekre bont egy stringet, a szó karakterek definiálhatóak.
	 */
	public static String[] tokenizeString (String s, String wordChars) {
		ourParser op = new ourParser(s, wordChars);
//		op.A = false;
		op.getNextLine();
		return op.lineTokens;
	}

	/**
	 * tokenekre bont egy stringet, a szó karakterek és whitespacek definiálhatóak.
	 */
	public static String[] tokenizeString (String s, String wordChars, String whitespaceChars) {
		ourParser op = new ourParser(s, wordChars, whitespaceChars);
//		op.A = false;
		op.getNextLine();
		return op.lineTokens;
	}

	/**
	 * megvizsgálja az s string illeszthetõ e exp heReguláris kifejezésre
	 */
	public static boolean matchString(String s, String exp) {
		ourParser op = new ourParser(s);
		op.getNextLine();
		return op.matchLine(exp);
	}
	// ezt még meg kéne irni wordChars és whitespaceChars extensionnal

	// összead két String tömböt. a két tömbnek inicializálva kell lennie, különben nagy gáz van...
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

	// kiszûri a b tömbbõl azokat az elemeket, amik a-val kezdõdnek
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
			
	// az a tömb Stringjeinek elejére leghosszabban illeszkedõ szót adja vissza
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
				for (int i=0; i<len; i++) // lehetne a[0].startsWidth(a[c].substring(0,len))-t is használni
					if (a[0].charAt(i)!=a[counter].charAt(i)) len=i;
			}
			return a[0].substring(0,len);
		}
	}

	/**
	 * megvizsgál egy stringet a kapott heRegExp-el, visszatérési értéke "" amennyiben nem volt illeszkedés, vagy nem egyértelmû
	 * milyen karakter következhet.
	 * !! célszerû volna úgy megírni, null-t ad vissza, ha nincs egyezés!!
	 */
	public static String completeString(String str, String exp) {


		String[] matcher = ourParser.tokenizeString(exp,"!-~"); // feldarabolja az str-t
		String[] lineTokens = ourParser.tokenizeString(str,"!-~"); // darabolja a heRegExp-et

		// !! ez igy csúnya
		if (matcher.length==0 || lineTokens.length==0) return ""; // ha bármelyik string üres, esély sincs egyezésre


		int linePos = 0; // egyezéskeresés az elejérõl indul
		int matcherPos = 0; // egyezéskeresés az elejérõl indul
//!!!!! mitcsinálhat "" bemenetre  		// wsAtEnd true az értéke ha whitespace van a legutolsó token után
		boolean wsAtEnd = !str.regionMatches(str.length()-lineTokens[lineTokens.length-1].length(),
											lineTokens[lineTokens.length-1],
											0, lineTokens[lineTokens.length-1].length());
		// wsAtEnd-nek az a lényege, ha nincs ws a szó végén, akkor a legutolsó token félbemaradt, ilyenkor más szabályok vonatkoznak a kiegészítésre

		try {
				while ((linePos<lineTokens.length && wsAtEnd) || (linePos<lineTokens.length-1 && !wsAtEnd)) //a legutolsó egész tokenig
				{
					regExpItem examine = new regExpItem(matcher[matcherPos]); // beolvassuk a következõ heRegExp tokent

					boolean found = examine.matchString(lineTokens[linePos]); // van e egyezés?
						
					switch (examine.type) {
						case regExpItem.REITT_OPTIONAL : // []-be rakott rész
							if (found)
								linePos++;
							matcherPos++;
							break;
						case regExpItem.REITT_REPEATING : // {} - akárhányszor ismétlõdhet
							if (found)
								linePos++;
							else
								matcherPos++;
							break;
						case regExpItem.REITT_NORMAL : // () vagy sima, valamelyikre egyezés kell vagy meghaltunk
							if (found) {
								linePos++;
								matcherPos++;
							} else {
								throw new Exception(); // elakadtunk a matchingben
							}
					}
				}
		} catch (Exception e) {
			return ""; // valami patterhiba, vagy a matchingben elakadtunk, vagy hosszabb a string mint a regexp (tehát túlfutott a matcher)
		}
/*	A lineTokens és a matcher listákon belüli helyzet:
		ha wsAtEnd==true
			ha OPTIONAL vagy NORMAL típusú volt az utolsó match, akkor adódhat olyan helyzet, hogy itt a matchPos==matcher.length
			elenkezõ esetben még matchPos<matcher.length
		ha wsAtEnd==false
			a helyzet megint ugyan az, annyi difivel, hogy a lineTokensben van még egy 
*/
		if (matcherPos==matcher.length) return ""; // vége a patternnek de még tart a string. simán szopás... nincs illeszkedés
/*	Itt most tehát már legalább egy elem van a matcherben.
*/
		String plus = ""; // ebben gyûjtjük mit lehet a végére biggyeszteni
		try {
/*	Elértünk a string végére, ha wsAtEnd értéke true, az annyit jelent, hogy sikerült végig matchet találnuk, vagyis tisztán
	folytatódik az illesztéskeresés. Amennyiben wsAtEnd==false, a string nem volt lezárva, következésképp a legutolsó tokenre
	még nem tudtunk egyezést keresni. Ilyenkor tehát elõször azt kell vizsgálni, ennek az utolsó kis foszlánynak van-e
	egyáltalán olyan kiegészítése, ami illeszthetõ rá. Ha a foszlányra sikeresen illesztettünk, úgy hogy az egyértelmû illesztés
	volt, akkor a wsAtEnd==true algoritmusával folytathatjuk a futást...

	wsAtEnd==false algoritmusa :
			amíg [] vagy {} jön
			   ha %c vagy %n akkor végeztünk
			   összegyûjtjük õket egy listába, csak az kell aminek az elejére illeszthetõ az utsó Tokenünk
// ezek totál nem kellenek
//			ha egyelemû a a listánk wsAtEnd==true-n folytatjuk  (plusba bele)
//			ha többelemû akkor keressük a legrövidebb közös részt, és ezzel vége az illesztésnekb (plus)
//			itt akkor vagyunk ha a lista üres
// ez az 5 sor nem kellett
			most NORMALra tesztelünk
			csak az kell ha Token eleje illeszthetõ rá
			ha üres a lista fail
			ha többelemû akkor keressük a legrövidebb közös részt, és ezzel vége az illesztésnek (plus)
			ha egyelemû akkor wsAtEnd=true;

	wsAtEnd==true algoritmusa :
			amíg egyelemû normal jön, nem %c, %n
				plus-hoz hozzáadjuk
			amig [] vagy {} jön
				listához hozzáadjuk
			a normalt is hozzáadjuk a listához
			megnézzük meddig egyeztethetõek, ez megy plus-ba
*/
			regExpItem examine = null;

// ez a rész akkor játszik, ha az utolsó token félbemaradt
			if (!wsAtEnd) {
				String head = lineTokens[linePos]; // ez a token eleje

				boolean isNum = Character.isDigit(head.charAt(0)); // ha a a token eleje szám, akkor a token szám (charAt(0) biztos létezik)
				String[] list = new String[0]; // ide gyûlik a lista
				do {
					examine = new regExpItem((matcherPos<matcher.length?matcher[matcherPos++]:null)); // amíg van heRegExp token, azt parseoljuk, ha elfogyott, akkor üres tokent parseoltatunk
					if (examine.isChr && !isNum) return "";//plus;	//match van, végére hozzárakni nemtudunk, mert %n illeszkedik
					if (examine.isNum && isNum) return "";//plus;	//match van, végére hozzárakni nemtudunk, mert %c illeszkedik
//					if (examine.type==regExpItem.REITT_OPTIONAL || examine.type==regExpItem.REITT_REPEATING)
						list = addStringArray(list, examine.data);
				} while (examine.type==regExpItem.REITT_OPTIONAL || examine.type==regExpItem.REITT_REPEATING);

				list = startsWith(list, head); // a listából csak az kell ami illeszkedik head-re
				
				if (list.length>1) { // ha több egyezés is van
					plus += commonPart(list).substring(head.length());
					return plus; //match van, nem egyértelmû a folytatás
				} else if (list.length==0) { // nem illeszkedik a patternra
					throw new Exception(); // simán lehaltunk
				} else { // egyértelmû az egyezés, innen kezdve úgy mehetünk, mintha wsAtEnd==true lett volna már mióta
					plus += list[0].substring(head.length());
					wsAtEnd = true;	// ha itt tartunk, akkor egyértelmûen ki lehett egészíteni a fébemaradt utsó paramétert.
				}
			}

			if (wsAtEnd) {
				// a normál toldalékokat hozzávesszük
				examine = new regExpItem((matcherPos<matcher.length?matcher[matcherPos++]:null));
				while (examine.type==regExpItem.REITT_NORMAL && examine.length==1) {
					plus += " " + examine.data[0];
					examine = new regExpItem((matcherPos<matcher.length?matcher[matcherPos++]:null));
				}
				if (examine.isChr || examine.isNum) return plus;

				// addig gyûjtünk innen listába, amíg elhagyható paraméterek jönnek
				String[] list = new String[0];
				while (examine.type==regExpItem.REITT_OPTIONAL || examine.type==regExpItem.REITT_REPEATING) {
					if (examine.isChr) return plus;//KI KELL LÉPNI, MATCH vége
					if (examine.isNum) return plus;//KI KELL LÉPNI, MATCH vége
					list = addStringArray(list, examine.data);
					examine = new regExpItem((matcherPos<matcher.length?matcher[matcherPos++]:null));
				}
				// most vagy egy normal, vagy egy error van az examineban, ez utóbbi esetén üreslistát ad az examine.data
				list = addStringArray(list, examine.data);
				String temp = commonPart(list);
				if (temp.length()>0)
					plus += " " + temp; // a lista közös része kerül plusba, vége
			}
		} catch (Exception e) {
			return plus;
			// ha az utolsó félbemaradt patternra nem találtunk illesztést.
		}
			
		return plus; // plusban van a kiegészítés, ezt simán visszaadjuk
	}
	
	/**
	 * megvizsgálja az éppen feldolgozott sor illeszthetõ e exp-re.
	 * mûködési elve ugyan az mint a completeString-é, annyiban egyszerûbb, hogy itt nem kell az utolsó token lezáratlansága miatt szívni
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