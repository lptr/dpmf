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
 * m�g implement�land� : hint
 */

/**
 * A parancsbeviteli mez�t rajzol�, illetve funkci�it, kezel�s�t megval�s�t� ablakelem
 * 
 */
class myCanvas extends Canvas implements KeyListener {

	/**
	 * Az aktu�lis kimeneti String-et t�rol� bels� v�ltoz�
	 */
	private String outString;
	/**
	 * Az eddig bevitt parancsok list�ja. Amikor egy r�gebben m�r be�rt parancsot �jra kiadunk, az automatikusan a lista
	 * tetej�re ker�l. (Ez akkor van �gy ha visszalapozva a historyban, entert �t�nk a parancsra. Amennyiben �jra beg�pelj�k,
	 * a fent le�rt hat�s nem k�vetkezik be)
	 */
	private LinkedList history;
	/**
	 * History tall�z�sakor haszn�lt seg�dv�ltoz�
	 */
	private int historyPos;
	/**
	 * kimeneti Stringen bel�li kurzorpoz�ci�
	 */
	private int linePos;
	/**
	 * az elfogadhat� kifejez�sek heRegExp list�ja
	 */
	String[] regExp;

	/**
	 * Inicializ�lja az objektumot. Be�ll�tja a grafikai elemeket, illetve a kiadhat� parancsok heRegExp list�j�t inicializ�lja
	 */
	myCanvas() {
		outString = "";
		history = new LinkedList();
		history.add(outString);
		historyPos = 0; // history.size() - 1;
		linePos = outString.length();
		setSize(400, 120);
		addKeyListener(this);
// Az elfogadhat� kimeneti parancsok list�j�nak inicializ�l�sa
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
	 * Hogy a vibr�l�st elker�lj�k, az update-t �tdefini�ljuk, a paint is azt a k�dot haszn�lja.<br>
	 * PS.: jelenleg a vibr�l�st teljesen kiv�d� doublebuffer r�szt kivettem, mert nagyon visszafogta a rendszer
	 * sebess�g�t. Tekintve, hogy a program a tesztk�rnyezet r�sz�nek k�sz�l, �s nem egy reprezent�ci�s munka,
	 * a funkcional�t�s �s k�nnyfontosabb mint a kin�zet...
	 */
	public void paint(Graphics g) {
		update(g);
	}

	/**
	 * Az inputter designos bar�ti k�ls�je.<br>
	 * Jelenleg annyit tesz, megjelen�ti a be�rt sz�veget, illetve a history-t. Tov�bbfejleszt�si lehet�s�g annak bevezet�se,
	 * hogy kell�en sz�k folytat�si lehet�s�g eset�n hintet ad, hogy a parancsnak hogy k�ne kin�znie...
	 */
	public void update(Graphics g2) {
//		BufferedImage img = (BufferedImage)createImage(400 ,250);
//		Graphics g2 = img.createGraphics();
		g2.clearRect(0,0,400,120);
		// history
		for(int i=history.size()-6;i<history.size()-1;i++)
			if (i>=0 && i < history.size())
				g2.drawString((String)history.get(i),10,110+(i-history.size()+1)*15-1);

		// az �pp szerkesztett sz�veg
		int fontWidth = (int)g2.getFontMetrics().getStringBounds(outString.substring(0,linePos),g2).getWidth();
		g2.drawString(outString.substring(0,linePos),10,110);
		g2.drawLine(fontWidth+10,110,fontWidth+15,110);
		g2.drawString(outString.substring(linePos),fontWidth + 17,110);
		g2.drawLine(7,97,387,97);
		// blit
//		g.drawImage(img,0,0,this);
	}

	/**
	 * Hozz�csatol egy String[]-hez egy Stringet
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
	 * Kisz�ri egy String[] k�z�s illeszked� r�sz�t
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
	 * Megpr�b�lja kieg�sz�teni a kapott param�tert regExp-el
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
	 * Kigy�jti, milyen regexpek illeszkedhetnek m�g a kifejez�sre !! EGYENL�RE NEMM�XIK MINT ATOM !!
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
	 * Ellen�rzi, hogy a kapott v�lasz illeszthet�e b�rmelyik heRegExp-re
	 */
	private boolean checkAnswer(String toChk) {
		for(int i=0; i<regExp.length; i++)
			if (ourParser.matchString(toChk,regExp[i])) return true;
		return false;
	}

	/**
	 * Gombok lenyom�s�nak vizsg�lata.<br>
	 * CTRL+bal/jobb eset�n egy eg�sz sz�t �tugrik<br>
	 * le/fel eset�n a historyban l�pked<br>
	 * jobb/bal eset�n a szerkesztett szavon bel�l l�pked<br>
	 * home/end eset�n a sz� elej�re/v�g�re ugrik<br>
	 * delete eset�n t�rli a kurzor ut�ni poz�ci� karakter�t<br>
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
	 * Gombok megnyom�s�nak vizsg�lata:
	 * Enter eset�n vizsg�ljuk, hogy az adott parancs kiadhat�-e, amennyiben igen, akkor a rendszer ezt megteszi, t�rli a
	 * beviteli sort, illetve ha a kiadott parancs a historyban szerepelt t�rl�dik onnan, majd be�r�dik a historyba mint
	 * legutols�nak kiadott parancs. Ez biztos�tja, hogy azonos parancs t�bbsz�r kiadva is csak egyszer szerepel a historyban,
	 * illetve a gyakran haszn�lt parancsok el�r�s�hez nem kell sokat keresg�lni benne.<br>
	 * Backspace eset�n t�rli a kurzor el�tti utols� karaktert<br>
	 * Tab eset�n megk�s�rli kieg�sz�teni a kurzor el�tt �ll� r�szt, �gy, hogy egy kiadhat� parancsot kapjunk. A kieg�sz�t�st
	 * addig teszi meg, am�g az egy�rtelm�s�thet�. Amennyiben t�bb lehets�ges folytat�s is el�fordul, �rtelemszer�en nem t�rt�nik 
	 * kieg�sz�t�s.<br>
	 * Norm�l karakter, vagyis minden egy�b karakter eset�n a ' '..'~' intervallumban, a karakter ki�r�dik a kimeneti stringbe<br>
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
 * pacman ir�ny�t�s�st megk�nny�t� ablakelem
 */
class myCanvas2 extends Canvas implements KeyListener {

	Inputter parent;

	/**
	 * Inicializ�lja az objektumot
	 */
	myCanvas2(Inputter parent) {
		this.parent = parent;
		setSize(400, 40);
		addKeyListener(this);
	}

	/**
	 * Hogy a vibr�l�st elker�lj�k, az update-t �tdefini�ljuk, a paint is azt a k�dot haszn�lja.
	 */
	public void paint(Graphics g) {
		update(g);
	}

	/**
	 * Ki�rjuk az adott elem haszn�lat�t. A kimenet statikus, semmi nem v�ltozik rajta, hiszen az elem k�nyege pont az, hogy
	 * billenyt�lenyom�sokkal adhatunk ki eg�sz parancsokat...
	 */
	public void update(Graphics g2) {
		g2.setColor(Color.blue);
		g2.fillRect(0,0,400,40);
		g2.setColor(Color.white);
		g2.drawString("up, down, left, right - set pacman's direction",10,16);
		g2.drawString("space - step",10,36);
	}

	/**
	 * Gomb lenyom�s�nak vizsg�lata.<br>
	 * ir�ny gomb eset�n be�ll�t�dik a pacman megadott ir�nya : setdirection (up|down|left|right)<br>
	 * space eset�n egy step parancs ad�dik ki
	 * b�rmi egy�b esetben ki�r�dik commentben a parancs
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
	 * Az inputter az el�bb defini�lt k�t canvasb�l �p�l fel. �gy l�nyeg�ben k�t hasznos seg�dprogram �tv�zete. Hogy
	 * �ppen melyik akt�v, hogy melyiket is akarjuk haszn�lni, azt a focussal d�nthetj�k el, vagyis amelyikre kattintunk,
	 * az kapja az esem�nyeket, �s �gy az� az ir�ny�t�s...
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

// a myCanvas r�gi kin�zete:
/* A R�GI KIN�ZET
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
