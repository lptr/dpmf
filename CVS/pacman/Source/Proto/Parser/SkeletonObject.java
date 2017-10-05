import java.lang.*;
import java.io.*;
import java.util.*;

/**
 * Minden �ltalunk defini�lt oszt�lyt a SkeletonObjectb�l sz�rmaztattunk, �gy
 * ez az oszt�ly tulajdonk�ppen egy debuggol�/szerv�z keretet jelent a program
 * k�r�l.
 */ 
class SkeletonObject extends Object {
	/**
	 * [VaVa]
	 */
//	public static seqDiagList seqDiag = new seqDiagList();

	/**
	 * Statikus sz�ml�l� az objektum sorsz�m gener�l�s�hoz. Minden �j objektum l�trej�ttekor
	 * n�vekszik eggyel.
	 */
	private static int objNumberCounter = 0;

	/**
	 * A beh�z�s m�rt�ke karakterben.
	 */
	protected static int margineWidth = 0;

	/**
	 * [VaVa]
	 */
	static LinkedList objList = new LinkedList();
	/**
	 * Az objektum sorsz�ma, l�trehoz�skor gener�l�dik. Egyedi azonos�t�.
	 */
	int objNumber;

	/**
	 * A konstruktor minden saj�t t�pus� oszt�ly p�ld�nyos�t�s�n�l lefut, �s �gy a
	 * p�ld�nyosod�sok alkalm�val folyamatosan n�vekvo objNumberCounter aktu�lis
	 * �rt�k�t az objNumber-hez rendelve, a l�trej�vo objektumot egy egyedi azonos�t�val
	 * l�tja el. Tov�bb� a p�l�dnyos�t�sr�l h�rt is ad a kimeneten.
	 */
	public SkeletonObject() {
		objNumber = objNumberCounter++;
		System.out.println (margine() + ">>> " + this + " created.");
		objList.add(this);
//		seqDiag.add(objNumber);
	}
	
	/**
	 * H�rt ad az objektum megsemmis�l�s�r�l.
	 */
	public void finalize() throws Throwable {
		System.out.println (margine() + "<<< " + this + " has been overwritten.");
	}
	
	/**
	 * String-kontextusban az oszt�ly nev�t helyettes�ti be.
	 * A k�nyelmes kezelhet�s�g �rdek�ben, amikor b�rmely oszt�ly olyan kontextusban
	 * szerepel, ahol az �rt�k�re, mint string volna sz�ks�g, ez a f�ggv�ny fog lefutni. Hat�s�ra
	 * oszt�lyn�v[objektum sorsz�m] jelenik meg a kimeneten.<br> Pl.:
	 *    Maze[2]
	 *
	 * @return		az oszt�ly neve, illetve hogy a SkeletonObjectbol sz�rmaz� objektumok k�z�l h�nyadikk�nt p�ld�nyosodott
	 */
	public String toString() {
		return (getClass().getName() + "[" + objNumber + "]").substring(getClass().getName().indexOf('.') + 1);
	}
	
	/**
	 * Marg� gener�l�s�hoz haszn�lt f�ggv�ny. A {@link SkeletonObject#margineIn()} illetve a {@link SkeletonObject#margineOut()}
	 * met�dusok �ll�tanak a beh�z�son, jelezv�n a f�ggv�nyh�v�sok m�lys�g�t, �gy n�velve az
	 * �ttekinthetos�get.
	 * Ez a met�dus nem tesz m�st, mint az elozo k�t f�ggv�ny �ltal be�ll�tott sz�less�gu beh�z�st,
	 * vagyis {@link SkeletonObject#margineWidth} sz�m� space-t ad vissza.
	 *
	 * @return 		egy {@link SkeletonObject#margineWidth} sz�m� space-t tartalmaz� String.
	 */
	String margine() {
		String s = "";
		for (int i=0; i < margineWidth; i++)
			s = s + ' ';
		return s;	
	}
	
	/**
	 * N�veli a beh�z�s m�rt�k�t
	 */
	public void margineIn() {
		margineWidth += 2;
	}

	/**
	 * Cs�kkenti a beh�z�s m�rt�k�t
	 */
	public void margineOut() {
		margineWidth -= 2;
	}

	/**
	 * A szkeletonban gyakran sz�ks�ges a felhasz�l� fel� k�rd�st int�zni, a
	 * met�dus ezt a feladatot l�tja el, soremel�sig olvassa a beg�pelt karaktereket
	 * majd ezekkel visszat�r a h�v� f�lhez.
	 *
	 * @return A SYSTEM.IN-en �rkezo sort�r�ssel z�rt karaktersorozat.
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
	 * A set param�terben megadott halmazban l�v� szavakra pr�b�lja illeszteni a toFind
	 * tartalm�t, ez 3 k�rben folyik. Amennyiben a toFind=="" akkor a set legels� szav�val
	 * t�r vissza. Amennyiben ez nem siker�lt, ponton egyez�st keres a set szavaival, vagyis
	 * ha a set-ben megtal�lhat� a toFind sz�, akkor ezzel visszat�r. Legutols� k�rben
	 * azt felt�telezz�k hogy a toFind a set-ben tal�lhat� PONTOSAN EGY sz�nak a befejezetlen,
	 * f�lig beg�pelt p�ld�nya. Vagyis ha a toFinf pontosan egy sz� elej�re illeszthet� a
	 * set-ben, akkor a set-ben tal�lhat� teljes sz�val t�r vissza. Minden m�s esetben az �res
	 * string, vagyis "" a visszat�r�si �rt�k. A set-ben l�v� szavakat '|' karakterek v�lasztj�k
	 * el egym�st�l.<br>
	 * <B>TODO:</B>!!! tesztelni kell, hogy a hib�s helyekre elhelyezett '|' jelek mit eredm�nyeznek.
	 *
	 * @param set		a v�laszok lehets�ges list�ja, '|' jelekkel elv�lasztva
	 * @param toFind		a kieg�sz�tend� sz�veg
	 *
	 * @return		�res String-gel t�r vissza amennyiben a <code>toFind</code> semmi m�don nem illesztheto a <code>set</code> egyetlen elem�re sem
	*  <br>			a <code>set</code> egy eleme, amennyiben tal�lhat� illeszt�s
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

// buta r�gi verzi�ja a NotIn-nek, nemszeress�k, az �j verzi� a designos comleteAnswer n�ven fut... :)
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
	 * Feltesz egy k�rd�st a felhaszn�l�nak, majd megh�vja az Answer() met�dust,
	 * melynek feladata a v�lasz beolvas�sa, �s visszaad�sa. Ezt a v�laszt azut�n
	 * egyszeruen tov�bbpasszoljuk a h�v� f�lnek.
	 *
	 * @param q		a k�rd�s sz�vege
	 *
	 * @return		a system.in bementere �rkezo v�lasz
	 */
	String Ask(String q) {
		System.out.print (margine() + "[?] " + q + " ");
		return Answer();
	}

	/**
	 * Feltesz egy k�rd�st a felhasz�nl�nak, valamint ki�rja a lehets�ges
	 * v�laszok halmaz�t, amennyben a halmaz �res, b�rmilyen v�laszt elfogad,
	 * ha m�r legal�bb egy elemet is defini�lunk, addig v�r v�laszra,
	 * m�g az nem olyan, ami egy�rtelm�en k�thet� a lehets�ges v�laszok egyik�hez.
	 * A k�rd�st csak egyszer teszi fel...
	 *
	 * @param q		a k�rd�s sz�vege
	 * @param ans		a lehets�ges v�laszok pipe-al elv�lasztva (pl. "yes|no")
	 *
	 * @return		a lehets�ges v�laszok halmaz�nak egyik eleme
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
	 * Minden f�ggv�ny, elsok�nt ennek a met�dusnak a h�v�s�t tartalmazza, a met�dus
	 * egyfel�l ki�rja a kimenetre, hogy mely oszt�ly, milyen f�ggv�nye, mif�le
	 * param�terekkel h�v�dott meg, tov�bb� a beh�z�s m�lys�g�t n�veli egy szinttel.
	 *
	 * @param func		a f�ggv�ny neve
	 * @param m		egy�b �zenet - pl. a kapott param�terek ismertet�se
	 */
	void In(String func, String m) {
		Println("in " + GetName(func) + (m.equals("")?"":(" [" + m + "]")) + ".");
		margineIn();
//		seqDiag.In(objNumber,func);
	}
	
	/**
	 * F�ggv�nynol/Met�dusb�l val� kil�p�s el�tti utols� l�p�sk�nt, az megh�vja ezt a
	 * met�dust, amely elosz�r visszavesz a beh�z�s m�lys�g�b�l egy szintet,
	 * majd ki�rja a f�ggv�nyb�l val� kil�p�st. Egy egyszer� leave is el�g volna ugyan,
	 * de az �ttekinthet�s�get ez a fajta jel�l�sm�d nagym�rt�kben n�veli.
	 *
	 * @param func		a f�ggv�ny neve
	 * @param m		egy�b �zenet - pl. a kapott a visszat�r�si �rt�k
	 */
	void Leave(String func, String m) {
		margineOut();
		Println("leave " + GetName(func) + (m.equals("")?"":(" [" + m + "]")) + ".");
//		seqDiag.Leave(objNumber,"");
	}
	
	/**
	 * Ki�rja a param�terben kapott stringet, marg�val
	 *
	 * @param		s a ki�rni k�v�nt sz�veg
	 */
	void Print(String s) {
		System.out.print(margine() + s);
	}

	/**
	 * Ki�rja a param�terben kapott stringet, marg�val, illetve soremel�ssel.
	 *
	 * @param s		a ki�rni k�v�nt sz�veg
	 */
	void Println(String s) {
		System.out.println(margine() + s);
	}
	
	/**
	 * Amennyiben a programban valami hiba t�rt�nik, r�viden le�r�st ad r�la.
	 *
	 * @param s		a hiba egy�b adatait le�r� sz�veg
	 * @param e		a k�pz�d�tt kiv�tel
	 */
	void Error (String s, Exception e) {
		System.err.println ("\n" + margine() + "!!! Error in " + this + (s.equals("")?"":(", problem: " + s)) + ".");
		e.printStackTrace(System.err);
	}
	
	/**
	 * Az aktu�lis f�ggv�ny nev�b�l k�pez egy "oszt�lyn�v.f�ggv�nyn�v()" alak� sztringet.
	 *
	 * @param s		a visszaadand� f�ggv�ny neve
	 *
	 * @return		a {@link SkeletonObject#toString} �rt�ke �sszetapasztva az <code>s</code> param�terrel
	 */
	public String GetName (String s) {
		return this + "." + s + "()";
	}
}