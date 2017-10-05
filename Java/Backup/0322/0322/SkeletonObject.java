package PacMan;

import java.lang.*;
import java.io.*;

/**
 * Minden általunk definiált osztályt a SkeletonObjectbol származtattunk, így
 * ez az osztály tulajdonképpen egy debuggoló/szervíz keretet jelent a program
 * kürül.
 */ 
class SkeletonObject extends Object {
	static int objNumberCounter = 0;
	static int printBegin = 0;
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
	}
	
	/**
	 * Az objektum megsemmisülésekor hírt adunk errol.
	 */
	public void finalize() throws Throwable {
		System.out.println (margine() + "<<< " + this + " has been overwritten.");
	}
	
	/**
	 * A kényelmes kezelhetoség érdekében, amikor bármely osztály olyan kontextusban
	 * szerepel, ahol az értékére, mint string volna szükség, ez a függvény fog lefutni. Hatására
	 * osztálynév.metódusnév [ egyedi azonosító ]  jelenik meg a kimeneten. Pl.:
	 *    PacMan.Maze[2]
	 */
	public String toString() {
		return getClass().getName() + "[" + objNumber + "]";
	}
	
	/**
	 * Margó generálásához használt függvény. A margineIn() illetve a margineOut() metódusok
	 * állítanak a margón, jelezvén a függvényhívások mélységét, így növelve az áttekinthetoséget.
	 * Ez a metódus nem tesz mást, mint az elozo két függvény által állított szélességu margót ad,
	 * vagyis printBegin számú space-t.
	 */
	String margine() {
		String s = "";
		for (int i=0; i < printBegin; i++)
			s = s + ' ';
		return s;	
	}
	
	/**
	 * Mélység növelése egy szinttel a margóban.
	 */
	public void margineIn() {
		printBegin += 2;
	}

	/**
	 * Mélység csökkentése egy szinttel a margóban.
	 */
	public void margineOut() {
		printBegin -= 2;
	}

	/**
	 * A szkeletonban gyakran szükséges a felhaszáló felé kérdést intézni, a
	 * metódus ezt a feladatot látja el, soremelésig olvassa a begépelt karaktereket
	 * majd ezekkel visszatér a hívó félhez.
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
	 * el egymástól.
	 * !!! tesztelni kell, hogy a hibás helyekre elhelyezett '|' jelek mit eredményeznek.
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
	 */
	String Ask(String q) {
		System.out.print (margine() + "??? " + q + " ");
		return Answer();
	}

	/**
	 * Feltesz egy kérdést a felhaszánlónak, valamint kiírja a lehetséges
	 * válaszok halmazát, amennyben a halmaz üres, bármilyen választ elfogad,
	 * ha egy elemet is definiálunk, addig ismétli a kérdést a felhasználónak,
	 * míg nem ad egy olyan választ, ami egyértelmûen köthetõ a lehetséges 
	 * válaszok egyikéhez.
	 */
	String Ask(String q, String ans) {
		System.out.print (margine() + "??? " + q + " [" + ans + "]");
		String answer;
		if (ans.length()==0) answer=Answer();
		else while ((answer=completeAnswer(ans,Answer()))=="");
		return answer;
	}
	
	/**
	 * Minden függvény, elsoként ennek a metódusnak a hívását tartalmazza, a metódus
	 * egyfelol kiírja a kimenetre, hogy mely osztály, milyen függvénye, miféle
	 * paraméterekkel hívódott meg, továbbá a margó mélységét növeli egy szinttel.
	 */
	void In(String func, String m) {
		Println("in " + GetName(func) + (m.equals("")?"":(" [" + m + "]")) + ".");
		margineIn();
	}
	
	/**
	 * Függvénynol/Metódusból való kilépés elötti utolsó lépésként, az meghívja ezt a
	 * metódust, amely eloször lezárja a margót, vagyis csökkenti a mélységét egy szinttel,
	 * majd kiirata a függvénybol való kilépést is a meghívás paramétereivel együtt. Egy
	 * egyszeru leave is elég volna ugyan, de az áttekinthetoséget ez a fajta jelölésmód
	 * nagymértékben növeli.
	 */
	void Leave(String func, String m) {
		margineOut();
		Println("leave " + GetName(func) + (m.equals("")?"":(" [" + m + "]")) + ".");
	}
	
	/**
	 * Kiírja a paraméterben kapott stringet, margóval a sorba
	 */
	void Print(String s) {
		System.out.print(margine() + s);
	}

	/**
	 * Kiírja a paraméterben kapott stringet, margóval, illetve soremeléssel.
	 */
	void Println(String s) {
		System.out.println(margine() + s);
	}
	
	/**
	 * Amennyiben a programban valami hiba történik, röviden leírást ad róla.
	 */
	void Error (String s, Exception e) {
		System.err.println ("\n" + margine() + "!!! Error in " + this + (s.equals("")?"":(", problem: " + s)) + ".");
		e.printStackTrace(System.err);
	}
	
	/**
	 * A függvény tulajdonképpen nem tesz mást mint egy stringosszeguzést,
	 * packagenév.osztálynév[példányosodássorszáma].s() stringel tér vissza, ahol az
	 * s helyére a paraméterben kapott string helyettesítodik.
	 */
	String GetName (String s) {
		return this + "." + s + "()";
	}
}