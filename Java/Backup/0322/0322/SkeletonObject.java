package PacMan;

import java.lang.*;
import java.io.*;

/**
 * Minden �ltalunk defini�lt oszt�lyt a SkeletonObjectbol sz�rmaztattunk, �gy
 * ez az oszt�ly tulajdonk�ppen egy debuggol�/szerv�z keretet jelent a program
 * k�r�l.
 */ 
class SkeletonObject extends Object {
	static int objNumberCounter = 0;
	static int printBegin = 0;
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
	}
	
	/**
	 * Az objektum megsemmis�l�sekor h�rt adunk errol.
	 */
	public void finalize() throws Throwable {
		System.out.println (margine() + "<<< " + this + " has been overwritten.");
	}
	
	/**
	 * A k�nyelmes kezelhetos�g �rdek�ben, amikor b�rmely oszt�ly olyan kontextusban
	 * szerepel, ahol az �rt�k�re, mint string volna sz�ks�g, ez a f�ggv�ny fog lefutni. Hat�s�ra
	 * oszt�lyn�v.met�dusn�v [ egyedi azonos�t� ]  jelenik meg a kimeneten. Pl.:
	 *    PacMan.Maze[2]
	 */
	public String toString() {
		return getClass().getName() + "[" + objNumber + "]";
	}
	
	/**
	 * Marg� gener�l�s�hoz haszn�lt f�ggv�ny. A margineIn() illetve a margineOut() met�dusok
	 * �ll�tanak a marg�n, jelezv�n a f�ggv�nyh�v�sok m�lys�g�t, �gy n�velve az �ttekinthetos�get.
	 * Ez a met�dus nem tesz m�st, mint az elozo k�t f�ggv�ny �ltal �ll�tott sz�less�gu marg�t ad,
	 * vagyis printBegin sz�m� space-t.
	 */
	String margine() {
		String s = "";
		for (int i=0; i < printBegin; i++)
			s = s + ' ';
		return s;	
	}
	
	/**
	 * M�lys�g n�vel�se egy szinttel a marg�ban.
	 */
	public void margineIn() {
		printBegin += 2;
	}

	/**
	 * M�lys�g cs�kkent�se egy szinttel a marg�ban.
	 */
	public void margineOut() {
		printBegin -= 2;
	}

	/**
	 * A szkeletonban gyakran sz�ks�ges a felhasz�l� fel� k�rd�st int�zni, a
	 * met�dus ezt a feladatot l�tja el, soremel�sig olvassa a beg�pelt karaktereket
	 * majd ezekkel visszat�r a h�v� f�lhez.
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
	 * el egym�st�l.
	 * !!! tesztelni kell, hogy a hib�s helyekre elhelyezett '|' jelek mit eredm�nyeznek.
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
	 */
	String Ask(String q) {
		System.out.print (margine() + "??? " + q + " ");
		return Answer();
	}

	/**
	 * Feltesz egy k�rd�st a felhasz�nl�nak, valamint ki�rja a lehets�ges
	 * v�laszok halmaz�t, amennyben a halmaz �res, b�rmilyen v�laszt elfogad,
	 * ha egy elemet is defini�lunk, addig ism�tli a k�rd�st a felhaszn�l�nak,
	 * m�g nem ad egy olyan v�laszt, ami egy�rtelm�en k�thet� a lehets�ges 
	 * v�laszok egyik�hez.
	 */
	String Ask(String q, String ans) {
		System.out.print (margine() + "??? " + q + " [" + ans + "]");
		String answer;
		if (ans.length()==0) answer=Answer();
		else while ((answer=completeAnswer(ans,Answer()))=="");
		return answer;
	}
	
	/**
	 * Minden f�ggv�ny, elsok�nt ennek a met�dusnak a h�v�s�t tartalmazza, a met�dus
	 * egyfelol ki�rja a kimenetre, hogy mely oszt�ly, milyen f�ggv�nye, mif�le
	 * param�terekkel h�v�dott meg, tov�bb� a marg� m�lys�g�t n�veli egy szinttel.
	 */
	void In(String func, String m) {
		Println("in " + GetName(func) + (m.equals("")?"":(" [" + m + "]")) + ".");
		margineIn();
	}
	
	/**
	 * F�ggv�nynol/Met�dusb�l val� kil�p�s el�tti utols� l�p�sk�nt, az megh�vja ezt a
	 * met�dust, amely elosz�r lez�rja a marg�t, vagyis cs�kkenti a m�lys�g�t egy szinttel,
	 * majd kiirata a f�ggv�nybol val� kil�p�st is a megh�v�s param�tereivel egy�tt. Egy
	 * egyszeru leave is el�g volna ugyan, de az �ttekinthetos�get ez a fajta jel�l�sm�d
	 * nagym�rt�kben n�veli.
	 */
	void Leave(String func, String m) {
		margineOut();
		Println("leave " + GetName(func) + (m.equals("")?"":(" [" + m + "]")) + ".");
	}
	
	/**
	 * Ki�rja a param�terben kapott stringet, marg�val a sorba
	 */
	void Print(String s) {
		System.out.print(margine() + s);
	}

	/**
	 * Ki�rja a param�terben kapott stringet, marg�val, illetve soremel�ssel.
	 */
	void Println(String s) {
		System.out.println(margine() + s);
	}
	
	/**
	 * Amennyiben a programban valami hiba t�rt�nik, r�viden le�r�st ad r�la.
	 */
	void Error (String s, Exception e) {
		System.err.println ("\n" + margine() + "!!! Error in " + this + (s.equals("")?"":(", problem: " + s)) + ".");
		e.printStackTrace(System.err);
	}
	
	/**
	 * A f�ggv�ny tulajdonk�ppen nem tesz m�st mint egy stringosszeguz�st,
	 * packagen�v.oszt�lyn�v[p�ld�nyosod�ssorsz�ma].s() stringel t�r vissza, ahol az
	 * s hely�re a param�terben kapott string helyettes�todik.
	 */
	String GetName (String s) {
		return this + "." + s + "()";
	}
}