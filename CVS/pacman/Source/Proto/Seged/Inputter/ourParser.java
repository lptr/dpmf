import java.io.*;
import java.lang.*;
import java.util.*;


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

				data = ourParser.tokenizeString(s,"*/%","|");
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

class ourParser extends Object {
	Reader r;
	StreamTokenizer st;
	public String[] lineTokens;

	ourParser(Reader r) {
		this.r = r;
		st = new StreamTokenizer(r);
		st.lowerCaseMode( true );
		st.eolIsSignificant( true );
		st.slashSlashComments( true );
		st.slashStarComments( true );
		lineTokens = null;
	}

	ourParser(String is) {
		this(is, "", "");
	}

	ourParser(String is, String wordChars) {
		this(is, wordChars, "");
	}

	ourParser(String is, String wordChars, String whitespaceChars) {
		this(new BufferedReader(new StringReader(is)));
		for (int i=0; i<wordChars.length(); i++)
			st.wordChars(wordChars.charAt(i),wordChars.charAt(i));
		for (int i=0; i<whitespaceChars.length(); i++)
			st.whitespaceChars(whitespaceChars.charAt(i),whitespaceChars.charAt(i));
	}

	ourParser(InputStream is) {
		this(is, "");
	}

	ourParser(InputStream is, String wordChars) {
		this(new BufferedReader(new InputStreamReader(is)));
		for (int i=0; i<wordChars.length(); i++)
			st.wordChars(wordChars.charAt(i),wordChars.charAt(i));
	}

	public static int TT_EOS = 1;
	public static int TT_OK = 2;
	public static int TT_ERROR = 3;
	/**
	 * processes a line
	 * @return TT_EOS if end of stream, TT_OK if next line parsed successfuly, TT_ERROR if there was an error.
	 */
	public int getNextLine() {
		LinkedList line = new LinkedList();
		int tokenType;
		try {
				while (StreamTokenizer.TT_EOF!=(tokenType=st.nextToken()) && StreamTokenizer.TT_EOL!=tokenType) {

					switch (tokenType) {
						case StreamTokenizer.TT_WORD:
							line.add(st.sval);
							break;
						case StreamTokenizer.TT_NUMBER:
							line.add("" + st.nval);
							break;
						default :
							line.add("UNKNOWN");
					}
				}
		} catch (Exception e) {
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

	public static String[] tokenizeString (String s) {
		ourParser op = new ourParser(s);
		op.getNextLine();
		return op.lineTokens;
	}

	public static String[] tokenizeString (String s, String wordChars) {
		ourParser op = new ourParser(s, wordChars);
		op.getNextLine();
		return op.lineTokens;
	}

	public static String[] tokenizeString (String s, String wordChars, String whitespaceChars) {
		ourParser op = new ourParser(s, wordChars, whitespaceChars);
		op.getNextLine();
		return op.lineTokens;
	}

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

/*debug*/
	private static void printStringArray(String[] a) {
		if (a==null)
			return;
		else if (a.length==0)
			return;
		else
			for (int i=0; i<a.length; i++)
				System.out.println(a[i]);
	}

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
	 * null-t ad vissza ha nincs match
	 */

	public static String completeString(String str, String exp) {

		if (str.equals("") || exp.equals("")) return "";

		String[] matcher = ourParser.tokenizeString(exp,"(){}[]|/%");
		String[] lineTokens = ourParser.tokenizeString(str,"/*%");
/*		System.out.println("String:");
			printStringArray(lineTokens);
		System.out.println("Expression:");
			printStringArray(matcher);
		System.out.println("EOComment");*/

		int linePos = 0;
		int matcherPos = 0;
//!!!!! mitcsin�lhat "" bemenetre  		// wsAtEnd true az �rt�ke ha whitespace van a legutols� token ut�n
		boolean wsAtEnd = !str.regionMatches(str.length()-lineTokens[lineTokens.length-1].length(),
											lineTokens[lineTokens.length-1],
											0, lineTokens[lineTokens.length-1].length());

		try {
				while ((linePos<lineTokens.length && wsAtEnd) || (linePos<lineTokens.length-1 && !wsAtEnd))
				{
//System.out.println(str+" :: "+exp);
					regExpItem examine = new regExpItem(matcher[matcherPos]);
///					int type = NONE;
///					String[] subList = ourParser.tokenizeString(examine,"*/%","|");

					boolean found = examine.matchString(lineTokens[linePos]);
						
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
								System.out.println("tet�");
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
//			System.out.println("" + matcherPos);
/*	Itt most teh�t m�r legal�bb egy elem van a matcherben.
*/
		String plus = "";
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

//			System.out.println("" + matcherPos);
//			printStringArray(matcher);
//			System.out.println(".");
			if (!wsAtEnd) {
				String head = lineTokens[linePos];
//				System.out.println(matcher[matcherPos]);

				boolean isNum = Character.isDigit(head.charAt(0));
				String[] list = new String[0];
				do {
					examine = new regExpItem((matcherPos<matcher.length?matcher[matcherPos++]:null));
					if (examine.isChr && !isNum) return plus;	//KI KELL L�PNI, MATCH v�ge	// itt m�g plus=""
					if (examine.isNum && isNum) return plus;	//KI KELL L�PNI, MATCH v�ge	// itt m�g plus=""
//					if (examine.type==regExpItem.REITT_OPTIONAL || examine.type==regExpItem.REITT_REPEATING)
						list = addStringArray(list, examine.data);
				} while (examine.type==regExpItem.REITT_OPTIONAL || examine.type==regExpItem.REITT_REPEATING);
//			printStringArray(list);
//			System.out.println(".");
				list = startsWith(list, head);
//			printStringArray(list);
//			System.out.println(".");
				if (list.length>1) {
					plus += commonPart(list).substring(head.length());
					return plus;
					//KI KELL L�PNI, MATCH v�ge
				} else if (list.length==0) { // nem illeszkedik a patternra
					throw new Exception();
				} else {
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

				String[] list = new String[0];
				while (examine.type==regExpItem.REITT_OPTIONAL || examine.type==regExpItem.REITT_REPEATING) {
					if (examine.isChr) return plus;//KI KELL L�PNI, MATCH v�ge
					if (examine.isNum) return plus;//KI KELL L�PNI, MATCH v�ge
					list = addStringArray(list, examine.data);
					examine = new regExpItem((matcherPos<matcher.length?matcher[matcherPos++]:null));
				}
				list = addStringArray(list, examine.data);
				plus += " " + commonPart(list);
			}
		} catch (Exception e) {
//e.printStackTrace();
//		System.out.println("linePos=");
			return plus;
			// ha az egyeztet�s k�zben a v�g�re �rt�nk a matcher token list�nak, akkor ker�l ide a vez�rl�s
		}// akkor ker�l ide a vez�rl�s, ha m�g van m�d kieg�sz�t�sre
			
		return plus;
	}
	
	/**
	 * exp form :
	 *      
	 */

	public boolean matchLine(String exp) {

		final int NONE = 0;
		final int OPTIONAL = 1;
		final int REPEATING = 2;
		final int NORMAL = 3;

		String[] matcher = ourParser.tokenizeString(exp,"(){}[]|/%");
		int linePos = 0;
		int matcherPos = 0;
		try {
				while (linePos<lineTokens.length || matcherPos<matcher.length)
				{
					String examine =  matcher[matcherPos];
					char temp = examine.charAt(0);
					int type = NONE;
					if (temp=='[') type = OPTIONAL;
					if (temp=='{') type = REPEATING;
					if (temp=='(') type = NORMAL;
					if (type!=NONE) examine = examine.substring(1,examine.length()-1); else type = NORMAL;
					String[] subList = ourParser.tokenizeString(examine,"*/%","|");

					boolean found;
					if (linePos<lineTokens.length) {
						int i=0;
						while (i<subList.length) {
							if (subList[i].equals("%n") && Character.isDigit(lineTokens[linePos].charAt(0)))
								break;
							else if (subList[i].equals("%c"))
								break;
							else if (subList[i].equals(lineTokens[linePos]))
								break;
							i++;
						}
						found = i<subList.length;
					} else
						found = false;
						
					switch (type) {
						case OPTIONAL :
							if (found)
								linePos++;
							matcherPos++;
							break;
						case REPEATING :
							if (found)
								linePos++;
							else
								matcherPos++;
							break;
						case NORMAL :
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
