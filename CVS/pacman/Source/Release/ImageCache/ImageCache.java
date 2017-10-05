// $Id: ImageCache.java,v 1.9 2001/05/30 13:49:52 lptr Exp $
// $Date: 2001/05/30 13:49:52 $
// $Author: lptr $

package ImageCache;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.util.zip.*;

	/**
		betolt egy, a parameterben atadott kepet
		@author: tom
	*/

class ImageLoader {
	static Image loadImage(ZipFile world, ZipEntry file) throws IOException { 
//		System.out.print ("ic: [" + world + "] " + file);
		DataInputStream is = new DataInputStream (world.getInputStream(file));
//		System.out.println(" - " + is.available());
		byte[] imageData = new byte[is.available()];
		is.readFully(imageData);
		return Toolkit.getDefaultToolkit().createImage(imageData);
//		return Toolkit.getDefaultToolkit().getImage(name);
	}
}

	/**
		ImageCache: a Pacman megfelelo vilagahoz es szintjehez tartozo kepeket huzza
		be, adminisztralja es szolgalja ki.
		@author: tom
	*/

public class ImageCache extends Object implements FileFilter {

	/**
		Az ImageCache egy hashtablaban tarolodik, hivatkozasi nev
		az objektumneve_allapota_fazisa
	*/

	JLabel Observer;

	Hashtable IC = new Hashtable();

	/**
		Az animaciok fazisainak szamlalasa
	*/

	Hashtable Anim = new Hashtable();
	
	Hashtable Size = new Hashtable();

	/**
		Az aktualis hatterkep
	*/

	Image BG;

	/**
		Felugyeli a kepek betoltodeset
	*/

	MediaTracker MT=null;

	/**
		Milyen tipusu fileokat toltunk be
		Konkretan png kiterjesztesueket, a FileFilter implementacioja
	*/

	public boolean accept(File ami) {
		return ami.isFile() && ami.getName().endsWith(".png");
	}

	/**
		Kiirja az adott hashtablaba levo kulcsokat es elemeket a standard kimenetre

	*/

	public Dimension getBounds(String alfa) {
		try {
			return (Dimension)Size.get(alfa);
		} catch (NullPointerException e) {
			return null;
		}
		
	}

	public void getElements(Hashtable alfa) {
		String s = null;
		Enumeration it = alfa.keys();
		while (it.hasMoreElements()) {
			s = (String) it.nextElement();
			System.out.println(s);
			System.out.println(alfa.get(s));
		}
	}

	/**
		Betolti a hatterkepet
	*/
	
	public void loadBG(ZipFile world, ZipEntry  bg) throws IOException {
		BG = ImageLoader.loadImage(world, bg);
		MT.addImage((Image)BG,1000);
			new Thread(new Runnable() {
				public void run() {
					try {
						MT.waitForID(1000);
					} catch (InterruptedException e) {
						return;
					}
				}
			}).start();
/*		try {
			MT.waitForID(1000);
		} catch (InterruptedException e) {
			BG = null;
			MT.removeImage((Image)BG,1000);
			System.out.println("ERROR: loading background picture "+bg+" failed.");
			return;
		}*/
	}

	/**
		Visszaadja, hogy az obj objektum es anim animaciobol hany fazis van
	*/

	
	public int getAnim(String obj, String anim) {
		try {
			return ((Integer)Anim.get(obj+"_"+anim)).intValue();
		} catch (NullPointerException e) {
			return 0;
		}
	}

	/**
		Visszaadja, hogy az obj animaciobol (egyben tartalmazza az objektum es az animacio
		nevet) hany fazis van
	*/


	public int getAnim(String obj) {
		try {
			return ((Integer)Anim.get(obj)).intValue();
		} catch (NullPointerException e) {
			return 0;
		}
	}

	/**
		Visszaadja a hatterkepet
	*/


	public Image getBG() {
		return (Image)BG;
	}

	/**
		Egy objektum (obj) valamilyen animaciobeli (anim) fazisat (state) adja vissza
	*/

	public Image getPic(String obj, String anim, int state) {
		try {
			return (Image)IC.get(obj+"_"+anim+"_"+state+".png");
		} catch (NullPointerException e) {
			return null;
		}
	}
	
	/**
		Listak torlese uj palya toltesekor
	*/

	public void dropElements() {
		IC.clear();
		Anim.clear();
	}
	
	/**
		A betoltottseg kijelzese, ha true, minden kep, amit be tudott tolteni,
		rendelkezesre all. Amelyik kepeknel hiba tortent a betoltes soran, azok
		mar a betoltodeskor jeleztek.
		bar a waitForID nem nagyon latszik mukodni linux alatt (-w------- siman olvassa,
		legalabbis nem jelez exception-t)
	*/

	public boolean getState() {
		return MT.checkAll()/* || (IC.keys().hasMoreElements()==false)*/;
	}

/**
	JUST FOR TESTING
	public static void main(String[] args) {
		System.out.println("Hello World!");
		ImageCache ic = new ImageCache("/tmp/hehe",new JButton("hehe"));
		ic.getElements(ic.IC);
		ic.getElements(ic.Anim);
		System.out.println(ic.getAnim("pacman","dies"));
		ic.loadBG("/tmp/hehe/a.png");
		while (!ic.getState()) {
			System.out.println("varunk...");
		}
		ic.dropElements();
		System.exit(0);
	}
*/	

	/**
		Ennek a konstruktornak adjuk azt a ZipFile-t, amiben az adott
		"vilag" leirasa van, ezen belul gfx konyvtarban vannak a grafikak.
		A comp komponens hatarozza meg, hogy mihez kotjuk a MediaTrackert.
	*/

	public ImageCache (ZipFile olvasando, Component comp) throws IOException {

	/**
		MediaTracker peldanyositas
	*/

		MT = new MediaTracker(comp);

	/**
		FileFilternek megfeleloen beolvassuk a megfelelo file-okat egy tombbe
	*/

		LinkedList fileok = new LinkedList();
		for (Enumeration e = olvasando.entries(); e.hasMoreElements() ;) {
			ZipEntry file = (ZipEntry)e.nextElement();
			if (file.getName().startsWith("gfx/") && file.getName().endsWith(".png"))
				fileok.addLast(file);
		}


		/**
			Ha legalabb egy file illeszkedett.
		*/

		if (!fileok.isEmpty()) {
			for (int i=0; i<fileok.size(); i++) {
				ZipEntry file = (ZipEntry)fileok.get(i);

				/**
					Be az ImageCache-be.
				*/
	
				Image temp = ImageLoader.loadImage(olvasando, file);

				String filenev = file.getName();
				filenev = filenev.substring(filenev.lastIndexOf("/") +1);
				IC.put(filenev, temp);

				/**
					Ha nagyobb a fazis szama, akkor be a Anim hashbe
				*/
				int width = temp.getWidth(Observer);
				int height = temp.getHeight(Observer);
				
				Size.put(filenev.substring(0,filenev.indexOf("_")),new Dimension(width,height));
				
				if (getAnim(filenev.substring(0,filenev.lastIndexOf("_")))<
					Integer.parseInt(filenev.substring(filenev.lastIndexOf("_")+1,filenev.indexOf(".")))) {
						Anim.put((Object)filenev.substring(0,filenev.lastIndexOf("_")),
							new Integer (Integer.parseInt(filenev.substring(filenev.lastIndexOf("_")+1,filenev.indexOf(".")))));
				}

	/**
		MediaTracker illesztes
	*/

				MT.addImage((Image)IC.get(filenev),0);

	/**
		Hiba eseten jelzes
	*/
			}
			new Thread(new Runnable() {
				public void run() {
					try {
						MT.waitForID(0);
					} catch (InterruptedException e) {
//						MT.removeImage((Image)IC.get(filenev),i);
//						IC.remove(filenev);
//						Anim.remove(filenev.substring(1,filenev.lastIndexOf("_")));
//						System.out.println("ERROR: loading picture "+filenev+" failed.");
						return;
					}
				}
			}).start();
		}
	}	
}
