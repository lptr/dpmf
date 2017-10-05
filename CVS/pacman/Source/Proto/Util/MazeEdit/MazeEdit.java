// $Id: MazeEdit.java,v 1.9 2001/04/27 15:19:34 vava Exp $
// $Date: 2001/04/27 15:19:34 $
// $Author: vava $

package Util.MazeEdit;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.event.*; // MouseMotionAdapter
import java.io.*;
import java.util.*;
import Util.Parser.*;

/**
 * Egy koordin�tap�r t�rol�s�ra alkalmas oszt�ly
 */
class Coordinates extends Object {
	private int x,y;

	Coordinates(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean equals(Coordinates c) {
		return (c.getX()==x && c.getY()==y);
	}

	public String toString() {
		return "(" + x + ";" + y + ")";
	}
} 


/**
 * Koordin�t�k t�rol�s�ra alkalmas lista. Tkp. egy koordin�ta halmaz.
 */
class myList {
	private final static int NOTINLIST = -1;

	private Vector lista;

	myList() {
		clear();
	}


	/**
	 * t�rli a list�t
	 */
	public void clear() {
		lista = new Vector();
	}

	/**
	 * hozz� ad egy elemet a list�hoz, amennyiben az m�g nem volt eleme
	 */
	public void add(int x, int y) {
		add(new Coordinates(x,y));
	}

	/**
	 * hozz� ad egy elemet a list�hoz, amennyiben az m�g nem volt eleme
	 */
	public void add(Coordinates c) {
		if (!contains(c))
			lista.add((Object)c);
	}

	/**
	 * lek�rdezi egy adott elem poz�ci�j�t. amennyiben az elem nem tal�lhat� meg a list�ban, NOTINLISTtel t�r vissza
	 */ 
	public int get(Coordinates c) {
		int i=0;
		while (i<length() && !get(i).equals(c)) i++;
		return i<length()?i:NOTINLIST;
	}
	
	/**
	 * megvizsg�lja, hogy egy adott elem benne van-e a list�ban
	 */ 
	public boolean contains(int x, int y) {
		return contains(new Coordinates(x,y));
	}

	/**
	 * megvizsg�lja, hogy egy adott elem benne van-e a list�ban
	 */ 
	public boolean contains(Coordinates c) {
		return get(c)!=NOTINLIST;
	}

	/**
	 * t�r�l egy elemet a list�b�l
	 */
	public void remove(int x, int y) {
		remove(new Coordinates(x,y));
	}

	/**
	 * t�r�l egy elemet a list�b�l
	 */
	public void remove(Coordinates c) {
		int a = get(c);
		if (a!=NOTINLIST)
			lista.remove(get(a));
	}

	/**
	 * invert�l egy elemet, vagyis amennyiben tagja volt a list�nak t�rli, ha nem volt tag, akkor belehelyezi.
	 */
	public void invert(int x, int y) {
		invert(new Coordinates(x,y));
	}

	/**
	 * invert�l egy elemet, vagyis amennyiben tagja volt a list�nak t�rli, ha nem volt tag, akkor belehelyezi.
	 */
	public void invert(Coordinates c) {
		if (contains(c))
			remove(c);
		else
			add(c);
	}

	/**
	 * lista hossza, list�ban lev� elemek sz�ma
	 */
	public int length() {
		return lista.size();
	}

	/**
	 * megadott koordin�t�j� elem lek�rdez�se
	 */
	public Coordinates get(int index) {
		return (Coordinates)lista.get(index);
	}
}

// nincs r� sz�ks�g, mivel a java api nem support�lja ezt win alatt :))
/*class myFileFilter implements FilenameFilter {
	public boolean accept(File dir, String name) {
		return name.endsWith(".maze");
	}
}*/

/**
 * K�pek bet�lt�s�t v�gz� Statikus oszt�ly.<br>
 * jelenleg nem tartalmaz semmi komplexet, az az el�nye hogy �gy egy helyen �sszevannak fogva a k�p bet�lt�sek, �gy ha a
 * k�s�bbiekben cachelni kell azokat, vagy ilyesmi, akkor az k�nnyebben megval�s�that�.
 */
class ImageLoader {
	static String imagePath = "images/";
	static Image loadImage(String str) { 
		return Toolkit.getDefaultToolkit().getImage(imagePath + str);
	}
}

/**
 * A jobb-fels� sarokban tal�lhat� men� kin�zeti �s m�k�d�si k�dja.
 */
class menuCanvas extends Canvas implements MouseListener {

	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	/**
	 * Az egyed�li �rtelmezett parancs ha r�klikkelnek, ilyenkor a klikkel�s hely�t�l f�gg�en l�vi be a programm�dot,
	 * ez �gy csunya, BUGFIXelni kell.
	 */
	public void mousePressed(MouseEvent e) {
		int temp = (int)(e.getY() / menuItemY);
		if (temp < 9) {
			prg.selectedMode=temp;
			prg.refresh();
		}
	}
	public void mouseReleased(MouseEvent e) {}

	
	private MazeEdit prg; // link a f�progira
	
	private int Y = 360; // a magass�gunk
	private int X = 120; // sz�less�g�nk
	private int menuItemY = 40; // egy men�elem magass�ga
	private static int szegely = 2; // sz�veg elhelyez�s�nek poz�cion�l�s�hoz a men�elemen bel�l
	private static int szegelyInner = 2; // szint�n sz�vegpoz�cion�l�s

	private Color backgroundColor = Color.white; // h�tt�rsz�n

	// az �tl�that�s�g kedv��rt, a k�vetkez� 2 3elem� t�mb elemeinek �ttekinthet� c�mz�s��rt.
	private static final int ITEM_BACK = 0;
	private static final int ITEM_STR1 = 1;
	private static final int ITEM_STR2 = 2;
	private Color[] activeColor = {Color.black,Color.white,Color.yellow}; // aktiv�lt men�pont sz�nei
	private Color[] normalColor = {Color.blue,Color.white,Color.yellow}; // norm�l men�pont sz�nei

	/**
	 * inicializ�l�s. be�ll�tjuk a sz�l�t, a m�ret�nket illetve a mouselistenert.
	 */
	menuCanvas(MazeEdit parent) {
		prg = parent;
		setSize(X, Y);
		addMouseListener(this);
//		addKeyListener(this);
// initialize autocomplete		
	}
	
	/**
	 * egy men�pont kirajzol�s�t v�gz� met�dus
	 */
	private void drawBlock(Graphics g, int pos, String str1, String str2, Color[] color) {
		g.setColor(backgroundColor);
		g.fillRect(0, menuItemY * pos, X, menuItemY);
		g.setColor(color[ITEM_BACK]);
		g.fillRect(0 + szegely, menuItemY * pos + szegely, X - 2*szegely, menuItemY - szegely);
		g.setColor(color[ITEM_STR1]);
		g.drawString(str1,0+szegely+szegelyInner, menuItemY*(pos+1)-szegely-szegelyInner);
		g.setColor(color[ITEM_STR2]);
		int strFontWidth = g.getFontMetrics().stringWidth(str2);
		g.drawString(str2,X-szegely-szegelyInner-strFontWidth, menuItemY*(pos+1)-szegely-szegelyInner);
	}
	
	public void paint(Graphics g) {
		update(g);
	}

	/**
	 * Kirajzolja DoubleBufferelve a men�pontokat.
	 */
	public void update(Graphics g) {
		BufferedImage img = (BufferedImage)createImage(X, Y);
		Graphics g2 = img.createGraphics();
		drawBlock(g2,MazeEdit.MODE_MENU,"M E N U", "", prg.selectedMode==MazeEdit.MODE_MENU?activeColor:normalColor);
		drawBlock(g2,MazeEdit.MODE_SIZE,"size ", "(" + prg.maze.getSizeX()+ "," +prg.maze.getSizeY()+")", prg.selectedMode==MazeEdit.MODE_SIZE?activeColor:normalColor);
		drawBlock(g2,MazeEdit.MODE_TIME,"time:", "" + prg.maze.getTime(), prg.selectedMode==MazeEdit.MODE_TIME?activeColor:normalColor);
		drawBlock(g2,MazeEdit.MODE_PROBAB,"probab:", "" + prg.maze.getProbab(), prg.selectedMode==MazeEdit.MODE_PROBAB?activeColor:normalColor);
		drawBlock(g2,MazeEdit.MODE_BPLACE,"bpalce", "", prg.selectedMode==MazeEdit.MODE_BPLACE?activeColor:normalColor);
		drawBlock(g2,MazeEdit.MODE_MONSTER,"monster", "D"+prg.maze.getMonsterDumb()+"C"+prg.maze.getMonsterClever(), prg.selectedMode==MazeEdit.MODE_MONSTER?activeColor:normalColor);
		drawBlock(g2,MazeEdit.MODE_CRYSTAL,"crystal", "", prg.selectedMode==MazeEdit.MODE_CRYSTAL?activeColor:normalColor);
		drawBlock(g2,MazeEdit.MODE_WALL,"wall", "", prg.selectedMode==MazeEdit.MODE_WALL?activeColor:normalColor);
		drawBlock(g2,MazeEdit.MODE_PACMAN,"PacMan", "", prg.selectedMode==MazeEdit.MODE_PACMAN?activeColor:normalColor);
		g.drawImage(img,0,0,this);
	}
}

/**
 * A bal oldalon tal�lhat� labirintus n�zetet megval�s�t� oszt�ly.
 */
class mazeCanvas extends Canvas implements MouseListener {

	/**
	 * duplafalas eg�szmez�s rajzol�s.
	 */
	private void drawDoubleFullField(int X, int Y) {
		drawdoubleWall(X, Y, FieldItem.UP);
		drawdoubleWall(X, Y, FieldItem.DOWN);
		drawdoubleWall(X, Y, FieldItem.LEFT);
		drawdoubleWall(X, Y, FieldItem.RIGHT);
	}

	/**
	 * szimplafalas eg�szmez�s rajzol�s.
	 */
	private void drawFullField(int X, int Y) {
		drawWall(X, Y, FieldItem.UP);
		drawWall(X, Y, FieldItem.DOWN);
		drawWall(X, Y, FieldItem.LEFT);
		drawWall(X, Y, FieldItem.RIGHT);
	}

	/**
	 * dupla fal rajzol�sa. Kirajzoljuk a norm�l falat, majd a poz�ci�nak megfelel�en egy szomsz�dos falat is.
	 */
	private void drawdoubleWall(int X, int Y, int dir) {
		drawWall(X, Y, dir);
		switch (dir) {
			case FieldItem.UP:
				dir=FieldItem.DOWN;
				Y=Y>0?Y-1:prg.maze.getSizeY()-1;
				break;
			case FieldItem.DOWN:
				dir=FieldItem.UP;
				Y=Y<prg.maze.getSizeY()-1?Y+1:0;
				break;
			case FieldItem.LEFT:
				dir=FieldItem.RIGHT;
				X=X>0?X-1:prg.maze.getSizeX()-1;
				break;
			case FieldItem.RIGHT:
				dir=FieldItem.LEFT;
				X=X<prg.maze.getSizeX()-1?X+1:0;
				break;
		}
		drawWall(X, Y, dir);
	}

	/**
	 * fal kirajzol�sa. Ez att�l f�gg, hogy milyen rajzol�si m�dban vagyunk, azaz lehet be�ll�t�, t�rl� �s invert�l�.
	 */
	private void drawWall(int X, int Y, int dir) {
		switch (prg.drawMode) {
			case MazeEdit.DRAW_MODE_SET:
				prg.maze.getFieldAt(X,Y).setWall(dir);
				break;
			case MazeEdit.DRAW_MODE_CLEAR:
				prg.maze.getFieldAt(X,Y).clearWall(dir);
				break;
			case MazeEdit.DRAW_MODE_INVERT:
				prg.maze.getFieldAt(X,Y).switchWall(dir);
				break;
		}
		repaint(X*fieldX,Y*fieldY,fieldX,fieldY);
	}

	public void mouseClicked(MouseEvent e) {}

	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {
		int tempselY=(int)(e.getY() / fieldY);
		int tempselX=(int)(e.getX() / fieldX);
		int modX=e.getX()-tempselX*fieldX;
		int modY=e.getY()-tempselY*fieldY;
		if (tempselY<prg.maze.getSizeY() && tempselX<prg.maze.getSizeX()) {
			repaint(selX*fieldX,selY*fieldY,fieldX,fieldY);
			selY=tempselY; selX=tempselX;
			repaint(selX*fieldX,selY*fieldY,fieldX,fieldY);
		}
		switch(prg.selectedMode) {
			case MazeEdit.MODE_WALL:
				switch (prg.drawShape) {
					case MazeEdit.DRAW_SHAPE_NORMAL:
						if (tempselY<prg.maze.getSizeY() && tempselX<prg.maze.getSizeX()) {
							int direction = modX+modY<fieldX?(modX>modY?FieldItem.UP:FieldItem.LEFT):(modX>modY?FieldItem.RIGHT:FieldItem.DOWN);
							if (prg.drawStyle==MazeEdit.DRAW_STYLE_NORMAL)
								drawWall(selX, selY, direction);
							else if (prg.drawStyle==MazeEdit.DRAW_STYLE_DOUBLE)
								drawdoubleWall(selX, selY, direction);
						}
						break;
					case MazeEdit.DRAW_SHAPE_FULLFIELD:
						if (tempselY<prg.maze.getSizeY() && tempselX<prg.maze.getSizeX()) {
							if (prg.drawStyle==MazeEdit.DRAW_STYLE_NORMAL)
								drawFullField(selX, selY);
							else if (prg.drawStyle==MazeEdit.DRAW_STYLE_DOUBLE)
								drawDoubleFullField(selX, selY);
						}
						break;
				}
				break;
			case MazeEdit.MODE_CRYSTAL:
				prg.maze.crystalList.invert(selX,selY);
				break;
			case MazeEdit.MODE_PACMAN:
				Coordinates temp = new Coordinates(selX,selY); 
				if (prg.maze.pacman==null || !prg.maze.pacman.equals(temp))
					prg.maze.pacman = temp;
				else {
					repaint(prg.maze.pacman.getX()*fieldX,prg.maze.pacman.getY()*fieldY,fieldX,fieldY);
					prg.maze.pacman = null;
				}
				repaint(selX*fieldX,selY*fieldY,fieldX,fieldY);
				break;
			case MazeEdit.MODE_BPLACE:
				Coordinates temp2 = new Coordinates(selX,selY); 
				if (prg.maze.birthPlace==null || !prg.maze.birthPlace.equals(temp2))
					prg.maze.birthPlace = temp2;
				else {
					repaint(prg.maze.birthPlace.getX()*fieldX,prg.maze.birthPlace.getY()*fieldY,fieldX,fieldY);
					prg.maze.birthPlace = null;
				}
				repaint(selX*fieldX,selY*fieldY,fieldX,fieldY);
				break;
		}
	}
	public void mouseReleased(MouseEvent e) {}

	
	private MazeEdit prg; // link az �s�kre	

//	private int X = 400; // n�zet m�rete
//	private int Y = 400; // n�zet m�rete
	private int fieldX=40; // mez� X m�ret
	private int fieldY=40; // mez� Y m�ret
	int selX = 0; // az aktu�lisan kiv�lasztott mez� X koordin�t�ja
	int selY = 0; // az aktu�lisan kiv�lasztott mez� Y koordin�t�ja

	/**
	 * inicializ�l�s, be�ll�tjuk a m�retet, az �st, �s a mousekezel�t.
	 */
	mazeCanvas(MazeEdit parent) {
		prg = parent;
//		setSize(X, Y);
		setSize(prg.maze.getSizeX()*40, prg.maze.getSizeY()*40);
		addMouseListener(this);
//		addKeyListener(this);
// initialize autocomplete		
	}
	
	public void paint(Graphics g) {
		update(g);
	}

	/**
	 * egy mez�elem kirajzol�sa megadott rajzlapra, megadott helyre.
	 */
	private void drawField(Graphics g, int x, int y, FieldItem f) {
		if (f.isBlocked())
			g.setColor(Color.gray);
		else
			g.setColor(Color.white);
		g.fillRect(fieldX*x, fieldY*y, fieldX, fieldY);
		g.setColor(Color.gray);
		g.drawRect(fieldX*x, fieldY*y, fieldX-1, fieldY-1);
		g.setColor(Color.black);
		if (f.isWall(FieldItem.UP))
			g.fillRect(fieldX*x, fieldY*y, fieldX, 2);
		if (f.isWall(FieldItem.DN))
			g.fillRect(fieldX*x, fieldY*(y+1)-2, fieldX, 2);
		if (f.isWall(FieldItem.LEFT))
			g.fillRect(fieldX*x, fieldY*y, 2, fieldY);
		if (f.isWall(FieldItem.RIGHT))
			g.fillRect(fieldX*(x+1)-2, fieldY*y, 2, fieldY);
	}

	/**
	 * Egy krist�ly kirajozl�sa.
	 */
	public void drawCrystal(Graphics g, int x, int y) {
		g.setColor(Color.red);
		g.fillRect((int)(fieldX*(x+0.25)),(int)(fieldY*(y+0.25)),(int)(fieldX*0.5),(int)(fieldY*0.5));
	}

	/**
	 * PacMan figur�j�nak kirajzol�sa
	 */
	public void drawPacman(Graphics g, int x, int y) {
		g.setColor(Color.yellow);
		g.fillRect((int)(fieldX*(x+0.25)),(int)(fieldY*(y+0.25)),(int)(fieldX*0.5),(int)(fieldY*0.5));
		g.setColor(Color.black);
		g.drawRect((int)(fieldX*(x+0.25)),(int)(fieldY*(y+0.25)),(int)(fieldX*0.5),(int)(fieldY*0.5));
	}

	/**
	 * BirthPlace kirajzol�sa
	 */
	public void drawBPlace(Graphics g, int x, int y) {
		g.setColor(Color.green);
		g.fillRect((int)(fieldX*(x+0.25)),(int)(fieldY*(y+0.25)),(int)(fieldX*0.5),(int)(fieldY*0.5));
		g.setColor(Color.black);
		g.drawRect((int)(fieldX*(x+0.25)),(int)(fieldY*(y+0.25)),(int)(fieldX*0.5),(int)(fieldY*0.5));
	}

	/**
	 * n�zet ujrarajzol�sa. A k�d kicsit tal�n bonyolult, ez annak k�sz�nhet�, hogy mivel a n�zet lehet nagyon nagy, �s ilyenkor
	 * a scrollerbarnak k�sz�nhet�en nagyr�sze nem is l�tszik, felesleges kirajzolni a labirintus nagyr�sz�t. Az am�gy teh�t szimpla
	 * egyszer� k�d, azzal bonyol�dik, hogy csak egy kisebb ter�let megrajzol�sa a feladat, tov�bb� a doublebufferez�s miatt m�g
	 * k�t transzl�ci�t is v�ghez kell vinni a rajzol�s k�zben.
	 */ 
	public void update(Graphics g) {
		int clipX = (int)(g.getClipBounds().getX()/fieldX);
		int clipY = (int)(g.getClipBounds().getY()/fieldY);
		int clipW = ((int)(g.getClipBounds().getWidth()/fieldX)+2);
		int clipH = ((int)(g.getClipBounds().getHeight()/fieldY)+2);
		int sizeX = prg.maze.getSizeX();
		int sizeY = prg.maze.getSizeY();
		int xw = ((int)(prg.scroller.getViewportSize().getWidth()/fieldX)+2);
		int yh = ((int)(prg.scroller.getViewportSize().getHeight()/fieldY)+2);
		int x0 = (int)(prg.scroller.getScrollPosition().getX()/fieldX);
		int y0 = (int)(prg.scroller.getScrollPosition().getY()/fieldY);
		BufferedImage img = (BufferedImage)createImage(xw*fieldX, yh*fieldY);
		Graphics g2 = img.createGraphics();
		// mez�elemek rajzol�sa
		for (int y=0; y<sizeY-y0 && y<yh; y++)
			for (int x=0; x<sizeX-x0 && x<xw; x++)
				drawField(g2,x,y,prg.maze.getFieldAt(x+x0,y+y0));
		// krist�lyok rajzol�sa
		for (int i=0; i<prg.maze.crystalList.length(); i++)
				drawCrystal(g2,prg.maze.crystalList.get(i).getX()-x0,prg.maze.crystalList.get(i).getY()-y0);
		// BPlace rajzol�sa, amennyiben megadtuk
		if (prg.maze.birthPlace!=null)
			drawBPlace(g2,prg.maze.birthPlace.getX()-x0,prg.maze.birthPlace.getY()-y0);
		// PacMan rajzol�sa amennyiben megadtuk
		if (prg.maze.pacman!=null)
			drawPacman(g2,prg.maze.pacman.getX()-x0,prg.maze.pacman.getY()-y0);

		// aktu�lis mez�re egy jelz�s rajzol�sa
		g2.setColor(Color.black);
		g2.fillOval((selX-x0)*fieldX+(int)(fieldX*0.25),(selY-y0)*fieldY+(int)(fieldY*0.25),(int)(fieldX*0.5),(int)(fieldY*0.5));
		// blit
		g.drawImage(img,x0*fieldX,y0*fieldY,this);
	}


/***
	public void update(Graphics g) {
//		System.out.println(g.getClip());
		int sizeX = prg.maze.getSizeX();
		int sizeY = prg.maze.getSizeY();
		int xw = ((int)(prg.scroller.getViewportSize().getWidth()/fieldX)+1);
		int yh = ((int)(prg.scroller.getViewportSize().getHeight()/fieldY)+1);
		int x0 = (int)(prg.scroller.getScrollPosition().getX()/fieldX);
		int y0 = (int)(prg.scroller.getScrollPosition().getY()/fieldY);
//System.out.println("x0="+x0+" y0="+y0+" xw="+xw+" yh="+yh);
		BufferedImage img = (BufferedImage)createImage(sizeX*fieldX, sizeY*fieldY);
		Graphics g2 = img.createGraphics();
		for (int y=0; y<sizeY-y0 && y<=yh; y++)
			for (int x=0; x<sizeX-x0 && x<=xw; x++)
				drawField(g2,x,y,prg.maze.getFieldAt(x+x0,y+y0));
		g2.fillOval((selX-x0)*fieldX+(int)(fieldX*0.25),(selY-y0)*fieldY+(int)(fieldY*0.25),(int)(fieldX*0.5),(int)(fieldY*0.5));
//		g.drawImage(img,(int)prg.scroller.getScrollPosition().getX(),(int)prg.scroller.getScrollPosition().getY(),this);
		g.drawImage(img,x0*fieldX,y0*fieldY,this);
//		System.out.println("" + prg.scroller.getScrollPosition());
	}
*//////
}

class submenuCanvas extends Canvas implements MouseMotionListener, MouseListener{

	private boolean inBox(int x, int y, int w, int h, int x2, int y2) {
		return (x2>=x && y2>=y && x2<x+w && y2<y+h);
	}

		//Invoked when the mouse has been clicked on a component. 
	public void mouseClicked(MouseEvent e) {
		switch (prg.selectedMode) {
			case MazeEdit.MODE_TIME: //n�vel�s.cs�kkent�s
				if (inBox(30,55,10,15,e.getX(),e.getY())) {
					prg.maze.setTime(prg.maze.getTime()+1);
					prg.refresh();
				}
				if (inBox(30,70,10,15,e.getX(),e.getY())) {
					int temp = prg.maze.getTime()-1;
					prg.maze.setTime(temp>0?temp:0);
					prg.refresh();
				}
				break;
			case MazeEdit.MODE_PROBAB: //n�vel�s.cs�kkent�s
				if (inBox(30,55,10,15,e.getX(),e.getY())) {
					prg.maze.setProbab(prg.maze.getProbab()+1);
					prg.refresh();
				}
				if (inBox(30,70,10,15,e.getX(),e.getY())) {
					int temp = prg.maze.getProbab()-1;
					prg.maze.setProbab(temp>0?temp:0);
					prg.refresh();
				}
				break;
			case MazeEdit.MODE_SIZE: //n�vel�s.cs�kkent�s SizeX
				if (inBox(55,40,10,15,e.getX(),e.getY())) {
					prg.maze.setSizeX(prg.maze.getSizeX()+1);
					prg.refresh();
				}
				if (inBox(55,55,10,15,e.getX(),e.getY())) {
					int temp = prg.maze.getSizeX()-1;
					prg.maze.setSizeX(temp>1?temp:1);
					prg.refresh();
				}					 //n�vel�s.cs�kkent�s SizeY
				if (inBox(55,80,10,15,e.getX(),e.getY())) {
					prg.maze.setSizeY(prg.maze.getSizeY()+1);
					prg.refresh();
				}
				if (inBox(55,95,10,15,e.getX(),e.getY())) {
					int temp = prg.maze.getSizeY()-1;
					prg.maze.setSizeY(temp>1?temp:1);
					prg.refresh();
				}
				break;
			case MazeEdit.MODE_MONSTER: //n�vel�s.cs�kkent�s MonsterDumb
				if (inBox(55,40,10,15,e.getX(),e.getY())) {
					prg.maze.setMonsterDumb(prg.maze.getMonsterDumb()+1);
					prg.refresh();
				}
				if (inBox(55,55,10,15,e.getX(),e.getY())) {
					int temp = prg.maze.getMonsterDumb()-1;
					prg.maze.setMonsterDumb(temp>0?temp:0);
					prg.refresh();
				}					 //n�vel�s.cs�kkent�s MonsterClever
				if (inBox(55,80,10,15,e.getX(),e.getY())) {
					prg.maze.setMonsterClever(prg.maze.getMonsterClever()+1);
					prg.refresh();
				}
				if (inBox(55,95,10,15,e.getX(),e.getY())) {
					int temp = prg.maze.getMonsterClever()-1;
					prg.maze.setMonsterClever(temp>0?temp:0);
					prg.refresh();
				}
				break;
			case MazeEdit.MODE_MENU:
				if (inBox(60,0,60,60,e.getX(),e.getY())) { //SAVE
					FileDialog fileDialog = new FileDialog(prg.frame);
					fileDialog.setMode(FileDialog.SAVE);
//					fileDialog.setFilenameFilter(new myFileFilter());
//					fileDialog.setFile("*.maze");
					fileDialog.show();
					if (fileDialog.getFile()!=null) {
						String fileName = fileDialog.getDirectory()+fileDialog.getFile();
						System.out.println(fileDialog.getDirectory()+fileDialog.getFile());
						try {
							FileOutputStream fos = new FileOutputStream(fileName);
							PrintStream ps = new PrintStream(fos);
							prg.maze.print(ps);
							ps.flush();
							fos.close();
						} catch (IOException e1) {
							System.out.println("Error during filesave"); 
						}
					}
				}
				if (inBox(0,60,60,60,e.getX(),e.getY())) { //CLEAR
					prg.maze = new Maze(prg.maze.getSizeX(),prg.maze.getSizeY());
					prg.refresh();
				}
				if (inBox(0,0,60,60,e.getX(),e.getY())) { //LOAD
					FileDialog fileDialog = new FileDialog(prg.frame);
					fileDialog.setMode(FileDialog.LOAD);
//					fileDialog.setFilenameFilter(new myFileFilter());
//					fileDialog.setFile("*.maze");
					fileDialog.show();
					if (fileDialog.getFile()!=null) {
						String fileName = fileDialog.getDirectory()+fileDialog.getFile();
						prg.maze = new Maze(fileName);
						prg.refresh();
					}
				}
				if (inBox(60,60,60,60,e.getX(),e.getY())) { //EXIT
					System.exit(0);
				}
				break;
		}
	}

// ahol lefele �s felfele g�rget�k vannak, ottan van ez haszn�lva
	int grabX;
	int grabY;
	int grab = 0;
	int grabValue;

// a wall k�perny� v�ltoz�i:

	private static final Rectangle DRAWREC_STYLE_NORMAL =    new Rectangle( 18   ,31   ,25,25);
	private static final Rectangle DRAWREC_STYLE_DOUBLE =    new Rectangle( 18+30,31   ,25,25);
	private static final Rectangle DRAWREC_MODE_CLEAR =      new Rectangle( 18+60,31   ,25,25);
	private static final Rectangle DRAWREC_MODE_SET =        new Rectangle( 18+60,31+30,25,25);
	private static final Rectangle DRAWREC_MODE_INVERT =     new Rectangle( 18+60,31+60,25,25);
	private static final Rectangle DRAWREC_SHAPE_NORMAL =    new Rectangle( 18   ,31+30,25,25);
	private static final Rectangle DRAWREC_SHAPE_LINE =      new Rectangle( 18+30,31+30,25,25);
	private static final Rectangle DRAWREC_SHAPE_BOX =       new Rectangle( 18   ,31+60,25,25);
	private static final Rectangle DRAWREC_SHAPE_FULLFIELD = new Rectangle( 18+30,31+60,25,25);

		//Invoked when a mouse button is pressed on a component and then dragged. 
	public void mouseDragged(MouseEvent e) {
		int temp=0;
		if (grab!=0)
			switch (prg.selectedMode) {
				case MazeEdit.MODE_TIME: // scrolloz�s tr�kk k�zepe TIME
					temp = prg.maze.getTime()+(grabY-e.getY());
					grabValue = temp>0?temp:0;
					repaint();
					break;
				case MazeEdit.MODE_PROBAB: // scrolloz�s tr�kk k�zepe PROBAB
					temp = prg.maze.getProbab()+(grabY-e.getY());
					grabValue = temp>0?temp:0;
					repaint();
					break;
				case MazeEdit.MODE_SIZE: // scrolloz�s tr�kk k�zepe SIZE
					if (grab==1)
						temp = prg.maze.getSizeX()+(int)((grabY-e.getY())*0.2);
					if (grab==2)
						temp = prg.maze.getSizeY()+(int)((grabY-e.getY())*0.2);
					grabValue = temp>1?temp:1;
					repaint();
					break;
				case MazeEdit.MODE_MONSTER: // scrolloz�s tr�kk k�zepe MONSTER
					if (grab==1)
						temp = prg.maze.getMonsterDumb()+(int)((grabY-e.getY())*0.066);
					if (grab==2)
						temp = prg.maze.getMonsterClever()+(int)((grabY-e.getY())*0.066);
					grabValue = temp>0?temp:0;
					repaint();
					break;
			}
	}
		//Invoked when the mouse enters a component. 
	public void mouseEntered(MouseEvent e) {}
		//Invoked when the mouse exits a component. 
	public void mouseExited(MouseEvent e) {}
		//Invoked when the mouse button has been moved on a component (with no buttons no down). 
	public void mouseMoved(MouseEvent e) {}
		//Invoked when a mouse button has been pressed on a component. 
	public void mousePressed(MouseEvent e) {
		switch (prg.selectedMode) {
			case MazeEdit.MODE_TIME: // scrolloz�s tr�kk, start TIME/time
				if (inBox(30,55,10,30,e.getX(),e.getY())) {
					grabX=e.getX();
					grabY=e.getY();
					grabValue=prg.maze.getTime();
					grab=1;
				}
				break;
			case MazeEdit.MODE_PROBAB: // scrolloz�s tr�kk, start PROBAB/probab
				if (inBox(30,55,10,30,e.getX(),e.getY())) {
					grabX=e.getX();
					grabY=e.getY();
					grabValue=prg.maze.getProbab();
					grab=1;
				}
				break;
			case MazeEdit.MODE_SIZE: // scrolloz�s tr�kk, start Size/SizeX
				if (inBox(55,40,10,30,e.getX(),e.getY())) {
					grabX=e.getX();
					grabY=e.getY();
					grabValue=prg.maze.getSizeX();
					grab=1;
				}						// scrolloz�s tr�kk, start Size/SizeY
				if (inBox(55,80,10,30,e.getX(),e.getY())) {
					grabX=e.getX();
					grabY=e.getY();
					grabValue=prg.maze.getSizeY();
					grab=2;
				}
				break;
			case MazeEdit.MODE_MONSTER: // scrolloz�s tr�kk, start Monster/Dumb
				if (inBox(55,40,10,30,e.getX(),e.getY())) {
					grabX=e.getX();
					grabY=e.getY();
					grabValue=prg.maze.getMonsterDumb();
					grab=1;
				}						// scrolloz�s tr�kk, start Monster/Clever
				if (inBox(55,80,10,30,e.getX(),e.getY())) {
					grabX=e.getX();
					grabY=e.getY();
					grabValue=prg.maze.getMonsterClever();
					grab=2;
				}
				break;
			case MazeEdit.MODE_WALL:
				if (DRAWREC_STYLE_NORMAL.contains(e.getX(),e.getY())) 
					prg.drawStyle=MazeEdit.DRAW_STYLE_NORMAL;
				if (DRAWREC_STYLE_DOUBLE.contains(e.getX(),e.getY())) 
					prg.drawStyle=MazeEdit.DRAW_STYLE_DOUBLE;

				if (DRAWREC_MODE_CLEAR.contains(e.getX(),e.getY())) 
					prg.drawMode=MazeEdit.DRAW_MODE_CLEAR;
				if (DRAWREC_MODE_SET.contains(e.getX(),e.getY())) 
					prg.drawMode=MazeEdit.DRAW_MODE_SET;
				if (DRAWREC_MODE_INVERT.contains(e.getX(),e.getY())) 
					prg.drawMode=MazeEdit.DRAW_MODE_INVERT;

				if (DRAWREC_SHAPE_NORMAL.contains(e.getX(),e.getY())) 
					prg.drawShape=MazeEdit.DRAW_SHAPE_NORMAL;
				if (DRAWREC_SHAPE_LINE.contains(e.getX(),e.getY())) 
					prg.drawShape=MazeEdit.DRAW_SHAPE_LINE;
				if (DRAWREC_SHAPE_BOX.contains(e.getX(),e.getY())) 
					prg.drawShape=MazeEdit.DRAW_SHAPE_BOX;
				if (DRAWREC_SHAPE_FULLFIELD.contains(e.getX(),e.getY())) 
					prg.drawShape=MazeEdit.DRAW_SHAPE_FULLFIELD;
				repaint();
				break;

		}
	}
		//Invoked when a mouse button has been released on a component 
	public void mouseReleased(MouseEvent e) {
		if (grab!=0)
			switch (prg.selectedMode) {
				case MazeEdit.MODE_TIME:  // scrolloz�s tr�kk befejez�se TIME
					prg.maze.setTime(grabValue);
					grab=0;
					prg.refresh();
					break;
				case MazeEdit.MODE_PROBAB:  // scrolloz�s tr�kk befejez�se PROBAB
					prg.maze.setProbab(grabValue);
					grab=0;
					prg.refresh();
					break;
				case MazeEdit.MODE_SIZE:  // scrolloz�s tr�kk befejez�se SIZE
					if (grab==1)
						prg.maze.setSizeX(grabValue);
					if (grab==2)
						prg.maze.setSizeY(grabValue);
					grab=0;
					prg.refresh();
					break;
				case MazeEdit.MODE_MONSTER:  // scrolloz�s tr�kk befejez�se MONSTER
					if (grab==1)
						prg.maze.setMonsterDumb(grabValue);
					if (grab==2)
						prg.maze.setMonsterClever(grabValue);
					grab=0;
					prg.refresh();
					break;
			}
	}
	
	
	private MazeEdit prg;

	private int X = 120;
	private int Y = 120;
	Image iMAGE_MENU;
	Image iMAGE_SIZE;
	Image iMAGE_TIME;
	Image iMAGE_PROBAB;
	Image iMAGE_BPLACE;
	Image iMAGE_CRYSTAL;
	Image iMAGE_MONSTER;
	Image iMAGE_WALL;
	Image iMAGE_WALLSEL;
	Image iMAGE_PACMAN;

	/**
	 * inicializ�l�s. �s, m�ret be�ll�t�sa, a haszn�lt k�pek bet�lt�se.
	 */
	submenuCanvas(MazeEdit parent) {
		prg = parent;
		setSize(X, Y);
		addMouseListener(this);
		addMouseMotionListener(this);
		iMAGE_MENU = ImageLoader.loadImage("Menu.jpg");
		iMAGE_SIZE = ImageLoader.loadImage("Size.jpg");
		iMAGE_TIME = ImageLoader.loadImage("Time.jpg");
		iMAGE_PROBAB = ImageLoader.loadImage("Probab.jpg");
		iMAGE_BPLACE = ImageLoader.loadImage("Bplace.jpg");
		iMAGE_CRYSTAL = ImageLoader.loadImage("Crystal.jpg");
		iMAGE_MONSTER = ImageLoader.loadImage("Monster.jpg");
		iMAGE_WALL = ImageLoader.loadImage("Wall.jpg");
		iMAGE_WALLSEL = ImageLoader.loadImage("WallSel.jpg");
		iMAGE_PACMAN = ImageLoader.loadImage("icon3.jpg");
//		addKeyListener(this);
// initialize autocomplete		
	}

	/**
	 * Transzform�ci�, hogy melyik m�dhoz milyen k�p tartozik.
	 */

	public Image Mode2Image(int mode) {
		switch (mode) {
		case MazeEdit.MODE_MENU : return iMAGE_MENU;
		case MazeEdit.MODE_SIZE : return iMAGE_SIZE;
		case MazeEdit.MODE_TIME : return iMAGE_TIME;
		case MazeEdit.MODE_PROBAB : return iMAGE_PROBAB;
		case MazeEdit.MODE_BPLACE : return iMAGE_BPLACE;
		case MazeEdit.MODE_MONSTER : return iMAGE_MONSTER;
		case MazeEdit.MODE_CRYSTAL : return iMAGE_CRYSTAL;
		case MazeEdit.MODE_WALL : return iMAGE_WALL;
		case MazeEdit.MODE_PACMAN : return iMAGE_PACMAN;
		}
		return null;
	}
	
	private static final int H_LEFT = 0;
	private static final int H_RIGHT = 1;
	private static final int H_CENTER = 2;
	private static final int V_TOP = 3;
	private static final int V_BOTTOM = 4;
	private static final int V_CENTER = 5;

	/**
	 * Sz�veg ki�rat�sa egy megadott t�glalpba, m�ghozz� a megadott poz�cion�l�ssal.
	 */
	private void drawString(Graphics g, int x, int y, int width, int height, String str, int hPos, int vPos) {
		int strWidth = g.getFontMetrics().stringWidth(str);
		int strHeight = g.getFontMetrics().getHeight();
		int ascent =  g.getFontMetrics().getAscent();
		int newX=x, newY=y;

		switch (hPos) {
		case H_LEFT:
			newX = x;
			break;
		case H_CENTER:
			newX = x + (int)((width - strWidth)*0.5);
			break;
		case H_RIGHT:
			newX = x + width - strWidth;
			break;
		}

		switch (vPos) {
		case V_TOP:
			newY = y + ascent;
			break;
		case V_CENTER:
			newY = y + (int)((height - strHeight)*0.5) + ascent;
			break;
		case V_BOTTOM:
			newY = y + height - strHeight + ascent;
			break;
		}

		g.drawString(str,newX,newY);
	}

	public void paint(Graphics g) {
		update(g);
	}

	public void update(Graphics g) {
		BufferedImage img = (BufferedImage)createImage(X, Y);
		Graphics g2 = img.createGraphics();
		g2.drawImage(Mode2Image(prg.selectedMode),0,0,this);
		g2.setColor(Color.black);
		switch (prg.selectedMode) {
			case MazeEdit.MODE_SIZE:
				drawString(g2,65,40,35-5,30,"" + (grab==1?grabValue:prg.maze.getSizeX()), H_RIGHT,V_CENTER);
				drawString(g2,65,80,35-5,30,"" + (grab==2?grabValue:prg.maze.getSizeY()), H_RIGHT,V_CENTER);
				break;
			case MazeEdit.MODE_TIME:
				drawString(g2,40,55,50-5,30,"" + (grab==1?grabValue:prg.maze.getTime()), H_RIGHT,V_CENTER);
				break;
			case MazeEdit.MODE_PROBAB:
				drawString(g2,40,55,50-5,30,"" + (grab==1?grabValue:prg.maze.getProbab()), H_RIGHT,V_CENTER);
				break;
			case MazeEdit.MODE_MONSTER:
				drawString(g2,65,40,35-5,30,"" + (grab==1?grabValue:prg.maze.getMonsterDumb()), H_RIGHT,V_CENTER);
				drawString(g2,65,80,35-5,30,"" + (grab==2?grabValue:prg.maze.getMonsterClever()), H_RIGHT,V_CENTER);
				break;
			case MazeEdit.MODE_WALL:
				Rectangle styleClip=null, modeClip=null, shapeClip=null;
				switch(prg.drawStyle) {
					case MazeEdit.DRAW_STYLE_NORMAL:  
						styleClip = DRAWREC_STYLE_NORMAL;
						break;
					case MazeEdit.DRAW_STYLE_DOUBLE:   
						styleClip = DRAWREC_STYLE_DOUBLE;
						break;
				}
				switch(prg.drawMode) {
					case MazeEdit.DRAW_MODE_CLEAR:   
						modeClip = DRAWREC_MODE_CLEAR;
						break;
					case MazeEdit.DRAW_MODE_SET:   
						modeClip = DRAWREC_MODE_SET;
						break;
					case MazeEdit.DRAW_MODE_INVERT:   
						modeClip = DRAWREC_MODE_INVERT;
						break;
				}
				switch(prg.drawShape) {
					case MazeEdit.DRAW_SHAPE_NORMAL:   
						shapeClip = DRAWREC_SHAPE_NORMAL;
						break;
					case MazeEdit.DRAW_SHAPE_LINE:   
						shapeClip = DRAWREC_SHAPE_LINE;
						break;
					case MazeEdit.DRAW_SHAPE_BOX:   
						shapeClip = DRAWREC_SHAPE_BOX;
						break;
					case MazeEdit.DRAW_SHAPE_FULLFIELD:   
						shapeClip = DRAWREC_SHAPE_FULLFIELD;
						break;
				}
				g2.setClip(shapeClip);
				g2.drawImage(iMAGE_WALLSEL,0,0,this);
				g2.setClip(styleClip);
				g2.drawImage(iMAGE_WALLSEL,0,0,this);
				g2.setClip(modeClip);
				g2.drawImage(iMAGE_WALLSEL,0,0,this);
				break;
		}	
		g.drawImage(img,0,0,this);
	}
}

/**
 * labirintus egy mez�j�nek adatai
 */
class FieldItem {
	private boolean[] wall;
	private boolean blocked;

	public static final int UP = 0;
	public static final int RIGHT = 1;
	public static final int DOWN = 2;
	public static final int LEFT = 3;
	public static final int NORTH = UP;
	public static final int EAST = LEFT;
	public static final int SOUTH = DOWN;
	public static final int WEST = RIGHT;
	public static final int DN = DOWN;

	FieldItem() {
		wall = new boolean[4];
		for (int i=0; i<=3; i++)
			wall[i]=false;
	}

	FieldItem(String s) {
		wall = new boolean[4];
		for (int i=0; i<=3; i++)
			wall[i]=s.charAt(i)=='0'?false:true;
	}

// lehal j�l ha nem norm�lis param�tert kap... ehhe
	boolean isWall(int direction) {
		return wall[direction];
	}

	void setWall(int direction) {
		wall[direction] = true;
		checkBlocked();
	}

	void clearWall(int direction) {
		wall[direction] = false;
		checkBlocked();
	}

	void switchWall(int direction) {
		wall[direction] = !wall[direction];
		checkBlocked();
	}

	private void checkBlocked() {
		blocked = wall[UP] && wall[DOWN] && wall[LEFT] && wall[RIGHT];
	}

	public boolean isBlocked() {
		return blocked;
	}

	public String toString() {
		return (wall[UP]?"1":"0") + (wall[RIGHT]?"1":"0") + (wall[DOWN]?"1":"0") + (wall[LEFT]?"1":"0");
	}
}

/**
 * A labirintust �s adatait t�rol� oszt�ly.
 */
class Maze extends Object {
	private int X; // oszlopok sz�ma
	private int Y; // sorok sz�ma
	private FieldItem[] mazeData; // labirintus adatai
	private int probab; // b�nuszmegjelen�si val�sz�n�s�g
	private int time; // teljes�t�shez adott id�
	private int monsterDumb; // bolond sz�rnyek sz�ma
	private int monsterClever; // okos sz�rnyek sz�ma
	public myList crystalList; // kris�lyok list�ja
	public Coordinates birthPlace; // sz�let�si helye
	public Coordinates pacman; // pacman sz�let�si helye

	/**
	 * Labirintus inicializ�l�sa. L�trehoz egy a param�terekben kapott m�ret� labirintust, felveszi az alap�rtelmezett �rt�keket.
	 */

	Maze(int sizeX, int sizeY) {
		mazeData = new FieldItem[sizeX*sizeY];
		for (int i=0; i<sizeX*sizeY ;i++)
			mazeData[i]=new FieldItem();
		X = sizeX;
		Y = sizeY;
		time = 120;
		probab = 60;
		monsterDumb = 0;
		monsterClever = 0;
		crystalList = new myList();
		pacman = null;
		birthPlace = null;
	}

	/**
	 * labirintus inicializ�l�sa fileb�l
	 */
	Maze(String fname) {
		InputStream is;
		try {
			System.out.println("Opening : " + fname);
			is = new FileInputStream(fname);
		} catch (FileNotFoundException e) {
			// amennyiben a file bet�lt�se sikertelen, kre�l egy alap�rtelmezett labirintust.
			System.out.println("ERROR: File NOT found!!");
			X = 10;
			Y = 10;
			mazeData = new FieldItem[X*Y];
			for (int i=0; i<X*Y ;i++)
			mazeData[i]=new FieldItem();
			time = 120;
			probab = 60;
			monsterDumb = 0;
			monsterClever = 0;
			crystalList = new myList();
			pacman = null;
			birthPlace = null;
			return;
		}
		ourParser parser = new ourParser(is);
		// buta kezd��rt�kekkel inicializ�l, ha ezek bet�lt�s ut�n is �gy n�znek ki, akkor hib�s volt a labfile
		X = -1;
		Y = -1;
		time = -1;
		probab = -1;
		monsterDumb = 0;
		monsterClever = 0;
		crystalList = new myList();
		birthPlace = null;
		pacman = null;
		mazeData = null;
		int mazeDataPos = 0;
		while (parser.getNextLine()==ourParser.TT_OK)	{
			for(int i=0;i<parser.lineTokens.length;i++)
				System.out.print(parser.lineTokens[i] + " ");
			System.out.println();
			if (parser.matchLine("pacman %n %n [%n]")) {
				System.out.println("pacman %n %n [%n]");
				pacman = new Coordinates((int)Double.parseDouble(parser.lineTokens[1]),
										(int)Double.parseDouble(parser.lineTokens[2]));
			} else if (parser.matchLine("time %n")) {
				System.out.println("time %n");
				time = Integer.valueOf(parser.lineTokens[1]).intValue();
			} else if (parser.matchLine("size %n %n")) {
				System.out.println("size %n %n");
				X = Integer.valueOf(parser.lineTokens[1]).intValue();
				Y = Integer.valueOf(parser.lineTokens[2]).intValue();
				mazeData = new FieldItem[X*Y];
			} else if (parser.matchLine("monster clever %n")) {
				System.out.println("monster clever %n");
				monsterClever = Integer.valueOf(parser.lineTokens[2]).intValue();
			} else if (parser.matchLine("monster dumb %n")) {
				System.out.println("monster dumb %n");
				monsterDumb = Integer.valueOf(parser.lineTokens[2]).intValue();
			} else if (parser.matchLine("monster clever %n %n %n")) { //monster [clever|dumb] <x> <y> <speed>
				System.out.println("monster clever %n %n %n");
				monsterClever++;
			} else if (parser.matchLine("monster dumb %n %n %n")) { //monster [clever|dumb] <x> <y> <speed>
				System.out.println("monster dumb %n %n %n");
				monsterDumb++;
			} else if (parser.matchLine("probability %n")) {
				System.out.println("probability %n");
				probab = Integer.valueOf(parser.lineTokens[1]).intValue();
			} else if (parser.matchLine("birthplace %n %n")) {
				System.out.println("birthplace %n %n");
				birthPlace = new Coordinates(Integer.valueOf(parser.lineTokens[1]).intValue(),
											Integer.valueOf(parser.lineTokens[2]).intValue());
			} else if (parser.matchLine("crystal %n %n")) {
				System.out.println("crystal %n %n");
				crystalList.add((int)Double.parseDouble(parser.lineTokens[1]),
								(int)Double.parseDouble(parser.lineTokens[2]));
			} else if (parser.matchLine("fields {%n}")) {
				System.out.println("fields {%n}");
				if (mazeData==null)
					System.out.println("ERROR: undefined maze size!!");
				else {
					int i;
					for (i=1; i<parser.lineTokens.length && mazeDataPos+i-1<mazeData.length; i++) {
						mazeData[mazeDataPos+i-1] = new FieldItem(parser.lineTokens[i]);
						if (parser.lineTokens[i].length()!=4)
							System.out.println("ERROR: incorrect maze data!!");
					}
					mazeDataPos+=i-1;
					if (i<parser.lineTokens.length)
						System.out.println("ERROR: too much maze data!!");
				}
			}
		}
		// amennyiben valami jelent�s hib�t tal�lt, sz�l �rte, �s megpr�b�lja koorig�lni
		if (X<0 || Y<0)	{
			System.out.println("ERROR: illegal or undefined maze size!!");
			X = 10; Y = 10;
		}
		if (mazeData==null) {
			System.out.println("ERROR: undefined maze!!");
			mazeData = new FieldItem[X*Y];
			for (int i=0; i<X*Y ;i++)
				mazeData[i]=new FieldItem();
		}
		if (mazeDataPos<X*Y) {
			System.out.println("ERROR: missing mazedata!!");
			for (int i=mazeDataPos; i<X*Y ;i++)
				mazeData[i]=new FieldItem();
		}
	}

	/**
	 * lek�rdezi egy mez� adatait
	 */
	public FieldItem getFieldAt(int x, int y) {
		return mazeData[this.X*y + x];
	}

// az itt k�vetkez� met�dusok a labirintus param�tereinek lek�rdez�s�t, illetve be�ll�t�s�t szolg�lj�k.
	public int getMonsterDumb() {
		return monsterDumb;
	}

	public void setMonsterDumb(int val) {
		monsterDumb = val;
	}

	public int getMonsterClever() {
		return monsterClever;
	}

	public void setMonsterClever(int val) {
		monsterClever = val;
	}

	public int getSizeX() {
		return X;
	}
	
	public void setSizeX(int valX) {
		if (valX!=X) {
			FieldItem[] tempData;
			tempData = new FieldItem[valX*Y];
			for(int x=0; x<valX; x++)
				for(int y=0; y<Y; y++){
					tempData[valX*y+x]=(x<X?getFieldAt(x,y):new FieldItem());
				}
			X = valX;
			mazeData = tempData;		
		}
	}

	public int getSizeY() {
		return Y;
	}

	public void setSizeY(int valY) {
		if (valY!=Y) {
			FieldItem[] tempData;
			tempData = new FieldItem[X*valY];
			for(int x=0;x<X;x++)
				for(int y=0;y<valY;y++)
					tempData[X*y+x]=(y<Y?getFieldAt(x,y):new FieldItem());
			Y = valY;
			mazeData = tempData;		
		}
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getProbab() {
		return probab;
	}

	public void setProbab(int probab) {
		this.probab = probab;
	}

	/**
	 * Labirintus kiirat�sa kimeneti Streamre. Ezt haszn�ljuk amikor a labirintust fileba kell menteni.
	 */
	public void print(PrintStream out) {
		out.println("// mazefile generated by MazeEditV1.2");
		out.println("time "+time);
		out.println("size "+X+" "+Y);
		out.print("fields");
		for(int i=0;i<mazeData.length;i++)
			out.print(" " + mazeData[i]);
		out.println();
		out.println("probability " + probab);
		for(int i=0;i<crystalList.length();i++)
			out.println("crystal " + (crystalList.get(i).getX()+0.5) + " " + (crystalList.get(i).getY()+0.5));
		for (int i=0;i<monsterDumb;i++)
			out.println("monster dumb " + (birthPlace.getX()+0.5) + " " + (birthPlace.getY()+0.5) + "0.1");
		for (int i=0;i<monsterDumb;i++)
			out.println("monster clever " + (birthPlace.getX()+0.5) + " " + (birthPlace.getY()+0.5) + "0.1");
		if (birthPlace!=null)
			out.println("birthplace " + birthPlace.getX() + " " + birthPlace.getY());
		if (pacman!=null)
			out.println("pacman " + (pacman.getX()+0.5) + " " + (pacman.getY()+0.5) + " " + "0.1");
	}
}

/**
 * A f�program
 */
class MazeEdit extends Object {
	Frame frame;
	menuCanvas MenuCanvas; // a labirintusn�zet
	submenuCanvas SubMenuCanvas; // a jobb als� almen� n�zet
	mazeCanvas MazeCanvas; // a men�
	int selectedMode=0; // menuben milyen m�d lett kiv�lasztva
	Maze maze;

// rajzol�si m�dok
	public static final int DRAW_STYLE_NORMAL = 1;   /* 18   ,31   ,25,25*/
	public static final int DRAW_STYLE_DOUBLE = 2;   /* 18+30,31   ,25,25*/
	public static final int DRAW_MODE_CLEAR = 1;     /* 18+60,31   ,25,25*/
	public static final int DRAW_MODE_SET = 2;       /* 18+60,31+30,25,25*/
	public static final int DRAW_MODE_INVERT = 3;    /* 18+60,31+60,25,25*/
	public static final int DRAW_SHAPE_NORMAL = 1;   /* 18   ,31+30,25,25*/
	public static final int DRAW_SHAPE_LINE = 2;     /* 18+30,31+30,25,25*/
	public static final int DRAW_SHAPE_BOX = 3;      /* 18   ,31+60,25,25*/
	public static final int DRAW_SHAPE_FULLFIELD = 4;/* 18+30,31+60,25,25*/

	int drawStyle = DRAW_STYLE_NORMAL; // rajzol�si st�lus
	int drawMode = DRAW_MODE_SET; // rajzol�si m�d
	int drawShape = DRAW_SHAPE_NORMAL; // rajzol�s t�pusa

	/**
	 * A n�zetek friss�t�s��rt felel�s. Nem a legszebb megold�s, mind a 3 n�zet friss�t�se j�p�r esetben felesleges...
	 */
	void refresh() {
		MazeCanvas.setSize(maze.getSizeX()*40, maze.getSizeY()*40);
		frame.pack();
//		MenuCanvas.revalidate();
		MenuCanvas.repaint();
		SubMenuCanvas.repaint();
		MazeCanvas.repaint();
		scroller.repaint();
	}

	// a k�l�nb�z� men�pontok
	public static final int MODE_MENU=0;
	public static final int MODE_SIZE=1;
	public static final int MODE_TIME=2;
	public static final int MODE_PROBAB=3;
	public static final int MODE_BPLACE=4;
	public static final int MODE_MONSTER=5;
	public static final int MODE_CRYSTAL=6;
	public static final int MODE_WALL=7;
	public static final int MODE_PACMAN=8;

	public ScrollPane scroller; // a labirintus r�sz scrollerje

	/**
	 * inicializ�l�s. L�trehoz egy 10*10es labirintust, a men�ben a legels� men�pontot �ll�tja be, �s inicializ�lja a n�zetet.
	 */
	MazeEdit() {
		selectedMode=MODE_MENU;
		maze = new Maze(10,10);
		initView();
	}

	/**
	 * n�zet inicializ�l�sa.
	 */

	private void initView() {
		frame = (Frame)(new Frame("Maze Editor"));
		frame.setLayout(new BorderLayout());
		frame.setIconImage(ImageLoader.loadImage("icon2.jpg")); // bet�ltj�k a progi ikonj�t

		Panel panel = new Panel();
		panel.setLayout(new BorderLayout());
		panel.add(MenuCanvas = new menuCanvas(this), BorderLayout.CENTER);
		panel.add(SubMenuCanvas = new submenuCanvas(this), BorderLayout.SOUTH);
		MazeCanvas = new mazeCanvas(this);
		scroller = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);

        Adjustable vadjust = scroller.getVAdjustable();
        Adjustable hadjust = scroller.getHAdjustable();
        hadjust.setUnitIncrement(40); // eg�sz mez�ket ugr�lunk
        vadjust.setUnitIncrement(40); // eg�sz mez�ket ugr�lunk

        scroller.setSize(400, 400);

		scroller.add(MazeCanvas);
		frame.add(scroller, BorderLayout.CENTER);
		frame.add(panel, BorderLayout.EAST);
		frame.pack();
		frame.setSize(600, 480);
        frame.show();

	}	
	public static void main(String[] args) {
		MazeEdit e = new MazeEdit();
		System.out.println("Hello World!");
	}
}
