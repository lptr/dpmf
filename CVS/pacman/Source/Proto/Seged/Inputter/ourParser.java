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
	static final int REITT_NONE = 0; // csak itt van használva

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
				for (int i=0; i<len; i++) // lehetne a[0].startsWidth(a[c].substring(0,len))-t is használni
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
//!!!!! mitcsinálhat "" bemenetre  		// wsAtEnd true az értéke ha whitespace van a legutolsó token után
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
								System.out.println("tetü");
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
//			System.out.println("" + matcherPos);
/*	Itt most tehát már legalább egy elem van a matcherben.
*/
		String plus = "";
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
					if (examine.isChr && !isNum) return plus;	//KI KELL LÉPNI, MATCH vége	// itt még plus=""
					if (examine.isNum && isNum) return plus;	//KI KELL LÉPNI, MATCH vége	// itt még plus=""
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
					//KI KELL LÉPNI, MATCH vége
				} else if (list.length==0) { // nem illeszkedik a patternra
					throw new Exception();
				} else {
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

				String[] list = new String[0];
				while (examine.type==regExpItem.REITT_OPTIONAL || examine.type==regExpItem.REITT_REPEATING) {
					if (examine.isChr) return plus;//KI KELL LÉPNI, MATCH vége
					if (examine.isNum) return plus;//KI KELL LÉPNI, MATCH vége
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
			// ha az egyeztetés közben a végére értünk a matcher token listának, akkor kerül ide a vezérlés
		}// akkor kerül ide a vezérlés, ha még van mód kiegészítésre
			
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
