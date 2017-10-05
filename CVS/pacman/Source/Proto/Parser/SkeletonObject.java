import java.lang.*;
import java.io.*;
import java.util.*;

/**
 * Minden általunk definiált osztályt a SkeletonObjectbõl származtattunk, így
 * ez az osztály tulajdonképpen egy debuggoló/szervíz keretet jelent a program
 * kürül.
 */ 
class SkeletonObject extends Object {
	/**
	 * [VaVa]
	 */
//	public static seqDiagList seqDiag = new seqDiagList();

	/**
	 * Statikus számláló az objektum sorszám generálásához. Minden új objektum létrejöttekor
	 * növekszik eggyel.
	 */
	private static int objNumberCounter = 0;

	/**
	 * A behúzás mértéke karakterben.
	 */
	protected static int margineWidth = 0;

	/**
	 * [VaVa]
	 */
	static LinkedList objList = new LinkedList();
	/**
	 * Az objektum sorszáma, létrehozáskor generálódik. Egyedi azonosító.
	 */
	int objNumber;

	/**
	 * A konstruktor minden saját típusú osztály példányosításánál lefut, és így a
	 * példányosodások alkalmával folyamatosan növekvo objNumberCounter aktuális
	 * értékét az objNumber-hez rendelve, a létrejövo objektumot egy egyedi azonosítóval
	 * látja el. Továbbá a péládnyosításról hírt is ad a kimeneten.
	 */
	public SkeletonObject() {
		objNumber = objNumberCounter++;
		System.out.println (margine() + ">>> " + this + " created.");
		objList.add(this);
//		seqDiag.add(objNumber);
	}
	
	/**
	 * Hírt ad az objektum megsemmisülésérõl.
	 */
	public void finalize() throws Throwable {
		System.out.println (margine() + "<<< " + this + " has been overwritten.");
	}
	
	/**
	 * String-kontextusban az osztály nevét helyettesíti be.
	 * A kényelmes kezelhetõség érdekében, amikor bármely osztály olyan kontextusban
	 * szerepel, ahol az értékére, mint string volna szükség, ez a függvény fog lefutni. Hatására
	 * osztálynév[objektum sorszám] jelenik meg a kimeneten.<br> Pl.:
	 *    Maze[2]
	 *
	 * @return		az osztály neve, illetve hogy a SkeletonObjectbol származó objektumok közül hányadikként példányosodott
	 */
	public String toString() {
		return (getClass().getName() + "[" + objNumber + "]").substring(getClass().getName().indexOf('.') + 1);
	}
	
	/**
	 * Margó generálásához használt függvény. A {@link SkeletonObject#margineIn()} illetve a {@link SkeletonObject#margineOut()}
	 * metódusok állítanak a behúzáson, jelezvén a függvényhívások mélységét, így növelve az
	 * áttekinthetoséget.
	 * Ez a metódus nem tesz mást, mint az elozo két függvény által beállított szélességu behúzást,
	 * vagyis {@link SkeletonObject#margineWidth} számú space-t ad vissza.
	 *
	 * @return 		egy {@link SkeletonObject#margineWidth} számú space-t tartalmazó String.
	 */
	String margine() {
		String s = "";
		for (int i=0; i < margineWidth; i++)
			s = s + ' ';
		return s;	
	}
	
	/**
	 * Növeli a behúzás mértékét
	 */
	public void margineIn() {
		margineWidth += 2;
	}

	/**
	 * Csökkenti a behúzás mértékét
	 */
	public void margineOut() {
		margineWidth -= 2;
	}

	/**
	 * A szkeletonban gyakran szükséges a felhaszáló felé kérdést intézni, a
	 * metódus ezt a feladatot látja el, soremelésig olvassa a begépelt karaktereket
	 * majd ezekkel visszatér a hívó félhez.
	 *
	 * @return A SYSTEM.IN-en érkezo sortöréssel zárt karaktersorozat.
	 */
	String Answer() {
		char ch = 0;
		
		String s = "";
		
		while (true) {
			try {
				ch = (char)System.in.read();
			} catch (IOException e) {
			}
			
			if (ch == '\r')
				continue;

			if (ch == '\n')
				return s;
			s = s + ch;
		}
	}

	/**
	 * A set paraméterben megadott halmazban lévõ szavakra próbálja illeszteni a toFind
	 * tartalmát, ez 3 körben folyik. Amennyiben a toFind=="" akkor a set legelsõ szavával
	 * tér vissza. Amennyiben ez nem sikerült, ponton egyezést keres a set szavaival, vagyis
	 * ha a set-ben megtalálható a toFind szó, akkor ezzel visszatér. Legutolsó körben
	 * azt feltételezzük hogy a toFind a set-ben található PONTOSAN EGY szónak a befejezetlen,
	 * félig begépelt példánya. Vagyis ha a toFinf pontosan egy szó elejére illeszthetõ a
	 * set-ben, akkor a set-ben található teljes szóval tér vissza. Minden más esetben az üres
	 * string, vagyis "" a visszatérési érték. A set-ben lévõ szavakat '|' karakterek választják
	 * el egymástól.<br>
	 * <B>TODO:</B>!!! tesztelni kell, hogy a hibás helyekre elhelyezett '|' jelek mit eredményeznek.
	 *
	 * @param set		a válaszok lehetséges listája, '|' jelekkel elválasztva
	 * @param toFind		a kiegészítendõ szöveg
	 *
	 * @return		üres String-gel tér vissza amennyiben a <code>toFind</code> semmi módon nem illesztheto a <code>set</code> egyetlen elemére sem
	*  <br>			a <code>set</code> egy eleme, amennyiben található illesztés
	 */ 
	String completeAnswer(String set, String toFind) {
		if (toFind.length()==0) {
			if (set.indexOf('|')==-1) return set;
			else return set.substring(0,set.indexOf('|'));
		}
		int temp=0,next;
		do {
			next=set.indexOf('|',temp); if (next==-1) next=set.length();
			if (set.regionMatches(true,temp,toFind,0,next-temp) && toFind.length()==next-temp) return toFind;
			temp=next+1;
		} while (next<set.length());
		temp=0; int hit=0; String match="";
		do {
			next=set.indexOf('|',temp); if (next==-1) next=set.length();
			if (set.regionMatches(true,temp,toFind,0,toFind.length())) {
				hit++; match=set.substring(temp,next);
			}
			temp=next+1;
		} while (next<set.length());
		if (hit==1) return match; else return "";
	}

// buta régi verziója a NotIn-nek, nemszeressük, az új verzió a designos comleteAnswer néven fut... :)
//	boolean NotIn(String set, String toFind) {
//		int temp=0,next;
//		do {
//			next=set.indexOf('|',temp); if (next==-1) next=set.length();
//			Println("Notintest" + (next-temp) );
//			if (set.regionMatches(true,temp,toFind,0,next-temp)) return false;
//			temp=next+1;
//		} while (next<set.length());
//		return true;
//	}
	
	/**
	 * Feltesz egy kérdést a felhasználónak, majd meghívja az Answer() metódust,
	 * melynek feladata a válasz beolvasása, és visszaadása. Ezt a választ azután
	 * egyszeruen továbbpasszoljuk a hívó félnek.
	 *
	 * @param q		a kérdés szövege
	 *
	 * @return		a system.in bementere érkezo válasz
	 */
	String Ask(String q) {
		System.out.print (margine() + "[?] " + q + " ");
		return Answer();
	}

	/**
	 * Feltesz egy kérdést a felhaszánlónak, valamint kiírja a lehetséges
	 * válaszok halmazát, amennyben a halmaz üres, bármilyen választ elfogad,
	 * ha már legalább egy elemet is definiálunk, addig vár válaszra,
	 * míg az nem olyan, ami egyértelmûen köthetõ a lehetséges válaszok egyikéhez.
	 * A kérdést csak egyszer teszi fel...
	 *
	 * @param q		a kérdés szövege
	 * @param ans		a lehetséges válaszok pipe-al elválasztva (pl. "yes|no")
	 *
	 * @return		a lehetséges válaszok halmazának egyik eleme
	 */
	String Ask(String q, String ans) {
		String answer;
		do {
			System.out.print (margine() + "[?] " + q + " [" + ans + "]");
			if (ans.equals("")) {
				answer = Answer();
			} else {
				answer = completeAnswer(ans, Answer());
			}
		} while (!ans.equals("") && answer.equals(""));

		return answer;
	}
	
	/**
	 * Minden függvény, elsoként ennek a metódusnak a hívását tartalmazza, a metódus
	 * egyfelõl kiírja a kimenetre, hogy mely osztály, milyen függvénye, miféle
	 * paraméterekkel hívódott meg, továbbá a behúzás mélységét növeli egy szinttel.
	 *
	 * @param func		a függvény neve
	 * @param m		egyéb üzenet - pl. a kapott paraméterek ismertetése
	 */
	void In(String func, String m) {
		Println("in " + GetName(func) + (m.equals("")?"":(" [" + m + "]")) + ".");
		margineIn();
//		seqDiag.In(objNumber,func);
	}
	
	/**
	 * Függvénynol/Metódusból való kilépés elötti utolsó lépésként, az meghívja ezt a
	 * metódust, amely eloször visszavesz a behúzás mélységébõl egy szintet,
	 * majd kiírja a függvénybõl való kilépést. Egy egyszerû leave is elég volna ugyan,
	 * de az áttekinthetõséget ez a fajta jelölésmód nagymértékben növeli.
	 *
	 * @param func		a függvény neve
	 * @param m		egyéb üzenet - pl. a kapott a visszatérési érték
	 */
	void Leave(String func, String m) {
		margineOut();
		Println("leave " + GetName(func) + (m.equals("")?"":(" [" + m + "]")) + ".");
//		seqDiag.Leave(objNumber,"");
	}
	
	/**
	 * Kiírja a paraméterben kapott stringet, margóval
	 *
	 * @param		s a kiírni kívánt szöveg
	 */
	void Print(String s) {
		System.out.print(margine() + s);
	}

	/**
	 * Kiírja a paraméterben kapott stringet, margóval, illetve soremeléssel.
	 *
	 * @param s		a kiírni kívánt szöveg
	 */
	void Println(String s) {
		System.out.println(margine() + s);
	}
	
	/**
	 * Amennyiben a programban valami hiba történik, röviden leírást ad róla.
	 *
	 * @param s		a hiba egyéb adatait leíró szöveg
	 * @param e		a képzõdött kivétel
	 */
	void Error (String s, Exception e) {
		System.err.println ("\n" + margine() + "!!! Error in " + this + (s.equals("")?"":(", problem: " + s)) + ".");
		e.printStackTrace(System.err);
	}
	
	/**
	 * Az aktuális függvény nevébõl képez egy "osztálynév.függvénynév()" alakú sztringet.
	 *
	 * @param s		a visszaadandó függvény neve
	 *
	 * @return		a {@link SkeletonObject#toString} értéke összetapasztva az <code>s</code> paraméterrel
	 */
	public String GetName (String s) {
		return this + "." + s + "()";
	}
}