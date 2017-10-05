// $Id: Inputter.java,v 1.8 2001/04/27 13:15:14 vava Exp $
// $Date: 2001/04/27 13:15:14 $
// $Author: vava $

package Inputter;

import java.lang.reflect.*;
import java.lang.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.*;
import Util.Parser.*;

/*
 * PacMan protohoz Inputter<br>
 * még implementálandó : hint
 */

/**
 * A parancsbeviteli mezõt rajzoló, illetve funkcióit, kezelését megvalósító ablakelem
 * 
 */
class myCanvas extends Canvas implements KeyListener {

	/**
	 * Az aktuális kimeneti String-et tároló belsõ változó
	 */
	private String outString;
	/**
	 * Az eddig bevitt parancsok listája. Amikor egy régebben már beírt parancsot újra kiadunk, az automatikusan a lista
	 * tetejére kerül. (Ez akkor van így ha visszalapozva a historyban, entert ütünk a parancsra. Amennyiben újra begépeljük,
	 * a fent leírt hatás nem következik be)
	 */
	private LinkedList history;
	/**
	 * History tallózásakor használt segédváltozó
	 */
	private int historyPos;
	/**
	 * kimeneti Stringen belüli kurzorpozíció
	 */
	private int linePos;
	/**
	 * az elfogadható kifejezések heRegExp listája
	 */
	String[] regExp;

	/**
	 * Inicializálja az objektumot. Beállítja a grafikai elemeket, illetve a kiadható parancsok heRegExp listáját inicializálja
	 */
	myCanvas() {
		outString = "";
		history = new LinkedList();
		history.add(outString);
		historyPos = 0; // history.size() - 1;
		linePos = outString.length();
		setSize(400, 120);
		addKeyListener(this);
// Az elfogadható kimeneti parancsok listájának inicializálása
		regExp = null;
		regExp = addStringArrayItem(regExp,"startgame %c");
		regExp = addStringArrayItem(regExp,"highscore");
		regExp = addStringArrayItem(regExp,"exit");
		regExp = addStringArrayItem(regExp,"exithighscore");
		regExp = addStringArrayItem(regExp,"step");
		regExp = addStringArrayItem(regExp,"exitgame %c");
		regExp = addStringArrayItem(regExp,"setdirection (up|right|down|left)");
		regExp = addStringArrayItem(regExp,"putbomb");
		regExp = addStringArrayItem(regExp,"remark {%c}");
		regExp = addStringArrayItem(regExp,"show (maze|info)");
		regExp = addStringArrayItem(regExp,"hack load %c");
		regExp = addStringArrayItem(regExp,"hack nextlevel");
		regExp = addStringArrayItem(regExp,"hack score %n");
		regExp = addStringArrayItem(regExp,"hack lives %n");
		regExp = addStringArrayItem(regExp,"hack time %n");
		regExp = addStringArrayItem(regExp,"hack pacman set %n %n (up|right|down|left) %n");
		regExp = addStringArrayItem(regExp,"hack monster (auto|manual)");
		regExp = addStringArrayItem(regExp,"hack monster create (clever|dumb) (%n|*) (%n|*)");
		regExp = addStringArrayItem(regExp,"hack monster %n set (%n|*) (%n|*) (up|right|down|left|*)");
		regExp = addStringArrayItem(regExp,"hack monster %n kill  %n");
		regExp = addStringArrayItem(regExp,"hack monster %n (reborn|remove)");
		regExp = addStringArrayItem(regExp,"hack bomb create (%n|*) (%n|*) (%n|*)");
		regExp = addStringArrayItem(regExp,"hack bomb %n set (%n|*) (%n|*) (%n|*)");
		regExp = addStringArrayItem(regExp,"hack bomb %n (activate|deactivate)");
		regExp = addStringArrayItem(regExp,"hack bomb %n remove");
		regExp = addStringArrayItem(regExp,"hack bonus (auto|manual)");
		regExp = addStringArrayItem(regExp,"hack bonus create (time|score|bomb) (%n|*) (%n|*) (%n|*) (%n|*)");
		regExp = addStringArrayItem(regExp,"hack bonus %n set (%n|*) (%n|*) (%n|*) (%n|*)");
		regExp = addStringArrayItem(regExp,"hack bonus %n remove ");
		regExp = addStringArrayItem(regExp,"hack crystal create (%n|*) (%n|*)");
		regExp = addStringArrayItem(regExp,"hack crystal %n set (%n|*) (%n|*)");
		regExp = addStringArrayItem(regExp,"hack crystal %n remove");
		regExp = addStringArrayItem(regExp,"hack wall (on|off) %n %n (up|right|down|left) (single|both)");


	}

	/**
	 * Hogy a vibrálást elkerüljük, az update-t átdefiniáljuk, a paint is azt a kódot használja.<br>
	 * PS.: jelenleg a vibrálást teljesen kivédõ doublebuffer részt kivettem, mert nagyon visszafogta a rendszer
	 * sebességét. Tekintve, hogy a program a tesztkörnyezet részének készül, és nem egy reprezentációs munka,
	 * a funkcionalítás és könnyfontosabb mint a kinézet...
	 */
	public void paint(Graphics g) {
		update(g);
	}

	/**
	 * Az inputter designos baráti külsõje.<br>
	 * Jelenleg annyit tesz, megjeleníti a beírt szöveget, illetve a history-t. Továbbfejlesztési lehetõség annak bevezetése,
	 * hogy kellõen szûk folytatási lehetõség esetén hintet ad, hogy a parancsnak hogy kéne kinéznie...
	 */
	public void update(Graphics g2) {
//		BufferedImage img = (BufferedImage)createImage(400 ,250);
//		Graphics g2 = img.createGraphics();
		g2.clearRect(0,0,400,120);
		// history
		for(int i=history.size()-6;i<history.size()-1;i++)
			if (i>=0 && i < history.size())
				g2.drawString((String)history.get(i),10,110+(i-history.size()+1)*15-1);

		// az épp szerkesztett szöveg
		int fontWidth = (int)g2.getFontMetrics().getStringBounds(outString.substring(0,linePos),g2).getWidth();
		g2.drawString(outString.substring(0,linePos),10,110);
		g2.drawLine(fontWidth+10,110,fontWidth+15,110);
		g2.drawString(outString.substring(linePos),fontWidth + 17,110);
		g2.drawLine(7,97,387,97);
		// blit
//		g.drawImage(img,0,0,this);
	}

	/**
	 * Hozzácsatol egy String[]-hez egy Stringet
	 */
	private String[] addStringArrayItem(String[] a,String b) {
		if (a==null) {
			String[] temp = new String[1];
			temp[0]=b;
			return temp;
		} else {
			String[] temp = new String[a.length+(b==null?0:1)];
			for (int i=0; i<a.length; i++)
				temp[i]=a[i];
			if (b!=null)
				temp[a.length]=b;
			return temp;
		}
	}

	/**
	 * Kiszûri egy String[] közös illeszkedõ részét
	 */
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
				for (int i=0; i<len; i++)
					if (a[0].charAt(i)!=a[counter].charAt(i)) len=i;
			}
			return a[0].substring(0,len);
		}
	}

	/**
	 * Megpróbálja kiegészíteni a kapott paramétert regExp-el
	 */
	private String completeAnswer(String toFind) {
		String[] tempList = null;
		for(int i=0; i<regExp.length; i++) {
			String temp = ourParser.completeString(toFind,regExp[i]);
			if (!temp.equals(""))
				tempList = addStringArrayItem(tempList,temp);
		}
		return toFind + commonPart(tempList);
	}

	/**
	 * Kigyûjti, milyen regexpek illeszkedhetnek még a kifejezésre !! EGYENLÕRE NEMMÖXIK MINT ATOM !!
	 */
	private String[] hintAnswer(String toFind) {
		String[] tempList = null;
		for(int i=0; i<regExp.length; i++) {
			if (ourParser.matchString(toFind,regExp[i]))
				tempList = addStringArrayItem(tempList,regExp[i]);
		}
		return tempList;
	}

	/**
	 * Ellenõrzi, hogy a kapott válasz illeszthetõe bármelyik heRegExp-re
	 */
	private boolean checkAnswer(String toChk) {
		for(int i=0; i<regExp.length; i++)
			if (ourParser.matchString(toChk,regExp[i])) return true;
		return false;
	}

	/**
	 * Gombok lenyomásának vizsgálata.<br>
	 * CTRL+bal/jobb esetén egy egész szót átugrik<br>
	 * le/fel esetén a historyban lépked<br>
	 * jobb/bal esetén a szerkesztett szavon belül lépked<br>
	 * home/end esetén a szó elejére/végére ugrik<br>
	 * delete esetén törli a kurzor utáni pozíció karakterét<br>
	 */
	public void keyPressed(KeyEvent e) {
		switch (e.getModifiers())
		{
			case InputEvent.CTRL_MASK :
				int newPos = linePos;
				switch (e.getKeyCode()) {
					case KeyEvent.VK_LEFT:
						try {
							while (outString.charAt(newPos-1)==' ')
								newPos--;
							while (outString.charAt(newPos-1)!=' ')
								newPos--;
							linePos = newPos-1;
						} catch (Exception ex) {
							linePos = 0;
						}
						update(getGraphics());
						break;
					case KeyEvent.VK_RIGHT:
						try {
							while (outString.charAt(newPos)==' ')
								newPos++;
							while (outString.charAt(newPos)!=' ')
								newPos++;
							linePos = newPos;
						} catch (Exception ex) {
							linePos = outString.length();
						}
						update(getGraphics());
						break;
				}
				break;		
			default :
				switch (e.getKeyCode()) {
					case KeyEvent.VK_UP :
						if (historyPos>0) {
							if (historyPos == history.size()-1) {
								history.removeLast();
								history.add(outString);
							}
							historyPos--;
							outString = (String)history.get(historyPos);
							linePos = outString.length();
							update(getGraphics());
						}
						break;
					case KeyEvent.VK_DOWN :
						if (historyPos < history.size()-1)
						{
							historyPos++;
							outString = (String)history.get(historyPos);
							linePos = outString.length();
							update(getGraphics());
						}
						break;
					case KeyEvent.VK_LEFT :
						if (linePos > 0) {
							linePos--;
							update(getGraphics());
						}
		
							break;
					case KeyEvent.VK_RIGHT :
						if (linePos < outString.length()) {
							linePos++;
							update(getGraphics());
						}
						break;
					case KeyEvent.VK_DELETE : //delete
						if (linePos<outString.length()) {
							historyPos = history.size()-1;
							outString = outString.substring(0,linePos) + outString.substring(linePos+1);
							update(getGraphics());
						}
						break;
					case KeyEvent.VK_HOME :
						linePos = 0;
						update(getGraphics());
						break;
					case KeyEvent.VK_END :
						linePos = outString.length();
						update(getGraphics());
						break;
				}
		}
	}
	public void keyReleased(KeyEvent e) {
	}
	/**
	 * Gombok megnyomásának vizsgálata:
	 * Enter esetén vizsgáljuk, hogy az adott parancs kiadható-e, amennyiben igen, akkor a rendszer ezt megteszi, törli a
	 * beviteli sort, illetve ha a kiadott parancs a historyban szerepelt törlõdik onnan, majd beíródik a historyba mint
	 * legutolsónak kiadott parancs. Ez biztosítja, hogy azonos parancs többször kiadva is csak egyszer szerepel a historyban,
	 * illetve a gyakran használt parancsok eléréséhez nem kell sokat keresgélni benne.<br>
	 * Backspace esetén törli a kurzor elötti utolsó karaktert<br>
	 * Tab esetén megkísérli kiegészíteni a kurzor elött álló részt, úgy, hogy egy kiadható parancsot kapjunk. A kiegészítést
	 * addig teszi meg, amíg az egyértelmûsíthetõ. Amennyiben több lehetséges folytatás is elõfordul, értelemszerûen nem történik 
	 * kiegészítés.<br>
	 * Normál karakter, vagyis minden egyéb karakter esetén a ' '..'~' intervallumban, a karakter kiíródik a kimeneti stringbe<br>
	 */
	public void keyTyped(KeyEvent e) {
		char ch = e.getKeyChar();
		historyPos = history.size()-1;
		switch (ch) {
			case '\n' : //enter
//				if (outString.equals("exit"))
//					System.exit(0);
				if (checkAnswer(outString))
				{
					history.removeLast();
					history.remove(outString);
					history.add(outString);
					System.out.println(outString);
					outString = "";
					linePos=0;
					history.add(outString);
					historyPos = history.size() - 1;
				}
				break;
			case '\b' : //backspace
				if (linePos>0) {
					outString = outString.substring(0,linePos-1) + outString.substring(linePos);
					linePos--;
				}
				break;
			case '`' :
				String temp = completeAnswer(outString.substring(0,linePos));
				if (!temp.equals("")) {
					outString = temp + outString.substring(linePos);
					linePos = temp.length();
				}
//				System.out.print("COMPLETE        ");
				break;
			default :
				ch = Character.toLowerCase(ch);
				if ((ch>=' ' && ch<='~')) {
					outString = outString.substring(0,linePos) + ch + outString.substring(linePos);
					linePos++;
				}
//				System.out.print("notspecial ");
		}
		update(getGraphics());
//		System.out.println(e.getKeyCode() + " '" + e.getKeyChar() + "'" + e.isActionKey());
	}
}

/**
 * pacman irányításást megkönnyítõ ablakelem
 */
class myCanvas2 extends Canvas implements KeyListener {

	Inputter parent;

	/**
	 * Inicializálja az objektumot
	 */
	myCanvas2(Inputter parent) {
		this.parent = parent;
		setSize(400, 40);
		addKeyListener(this);
	}

	/**
	 * Hogy a vibrálást elkerüljük, az update-t átdefiniáljuk, a paint is azt a kódot használja.
	 */
	public void paint(Graphics g) {
		update(g);
	}

	/**
	 * Kiírjuk az adott elem használatát. A kimenet statikus, semmi nem változik rajta, hiszen az elem kényege pont az, hogy
	 * billenytûlenyomásokkal adhatunk ki egész parancsokat...
	 */
	public void update(Graphics g2) {
		g2.setColor(Color.blue);
		g2.fillRect(0,0,400,40);
		g2.setColor(Color.white);
		g2.drawString("up, down, left, right - set pacman's direction",10,16);
		g2.drawString("space - step",10,36);
	}

	/**
	 * Gomb lenyomásának vizsgálata.<br>
	 * irány gomb esetén beállítódik a pacman megadott iránya : setdirection (up|down|left|right)<br>
	 * space esetén egy step parancs adódik ki
	 * bármi egyéb esetben kiíródik commentben a parancs
	 */
	public void keyPressed(KeyEvent e) {
		boolean step = parent.stepcheckbox.getState();
		switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				System.out.println("setdirection left");
				if (step) {
					System.out.println("step");
				}
				break;
			case KeyEvent.VK_RIGHT:
				System.out.println("setdirection right");
				if (step) {
					System.out.println("step");
				}
				break;
			case KeyEvent.VK_UP :
				System.out.println("setdirection up");
				if (step) {
					System.out.println("step");
				}
				break;
			case KeyEvent.VK_DOWN :
				System.out.println("setdirection down");
				if (step) {
					System.out.println("step");
				}
				break;
			case KeyEvent.VK_SPACE :
				System.out.println("step");
				break;
			default:
				System.out.println("/* unknown key, keycode : '"+e.getKeyCode()+"' */");
		}
	}
	public void keyReleased(KeyEvent e) {
	}
	public void keyTyped(KeyEvent e) {
	}
}


public class Inputter extends Object {

	Checkbox stepcheckbox;

	/*
	 * Az inputter az elöbb definiált két canvasból épül fel. Így lényegében két hasznos segédprogram ötvözete. Hogy
	 * éppen melyik aktív, hogy melyiket is akarjuk használni, azt a focussal dönthetjük el, vagyis amelyikre kattintunk,
	 * az kapja az eseményeket, és így azé az irányítás...
	 */
	Inputter() {
		Frame f = new Frame("Input Console");
		myCanvas c = new myCanvas();
		myCanvas2 c2 = new myCanvas2(this);
		f.setSize(420, 50);
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
//				f.setVisible(false);
				System.out.println("Bye");
				System.exit(0);
			}
		});

		f.setLayout(new BorderLayout());
		f.add(c, BorderLayout.CENTER);
		f.add(c2,  BorderLayout.NORTH);
		Panel p = new Panel();
		stepcheckbox = new Checkbox("send \"step\" after movement keys");
		FocusListener[] fl = (FocusListener[])(stepcheckbox.getListeners(FocusListener.class));
		for (int i = 0; i < fl.length; i++)
			stepcheckbox.removeFocusListener (fl[i]);
		p.add(stepcheckbox);
		f.add(p, BorderLayout.SOUTH);
		f.pack();
		f.setVisible(true);
	}
		
	public static void main (String args[]) {
		new Inputter();
	}
}

// a myCanvas régi kinézete:
/* A RÉGI KINÉZET
		g2.clearRect(0,0,400,250);
		for(int i=historyPos-5;i<historyPos;i++)
			if (i>=0 && i < history.size())
				g2.drawString((String)history.get(i),10,110+(i-historyPos)*15-1);
		int fontWidth = (int)g2.getFontMetrics().getStringBounds(outString.substring(0,linePos),g2).getWidth();
		g2.drawString(outString.substring(0,linePos),10,110);
		g2.drawLine(fontWidth+10,110,fontWidth+15,110);
		g2.drawString(outString.substring(linePos),fontWidth + 17,110);
		g2.drawRect(7,97,380,16);
		for(int i=historyPos+1;i<=historyPos+5;i++)
			if (i>=0 && i < history.size())
				g2.drawString((String)history.get(i),10,110+(i-historyPos)*15+1);*/
