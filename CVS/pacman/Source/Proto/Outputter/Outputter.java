// $Id: Outputter.java,v 1.19 2001/04/27 21:13:45 tom Exp $
// $Date: 2001/04/27 21:13:45 $
// $Author: tom $

package Outputter;

import java.awt.*;
import java.awt.image.*;
import java.awt.Color.*;
import java.awt.BorderLayout;
import java.awt.ScrollPane;

import java.awt.event.*;
//import javax.swing.*;
import java.io.*;
import java.util.*;
import java.lang.String;
import Util.Parser.*;

/**
 * Egy koordinátapár tárolására alkalmas osztály
 */
class Coordinates extends Object {
	private double x,y;

	/**
	 *	Koordinátapár létrehozása
	 *	@author		Vava (modified tRehak)
	 */
	Coordinates(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
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
	 *	Collection megvalositas by Vava
	 *	@author		Vava
	 */

class myList {

	private Vector lista;

	myList() {
		lista = new Vector();
	}

	public void add(double x, double y) {
		add(new Coordinates(x,y));
	}

	public boolean contains(double x, double y) {
		return contains(new Coordinates(x,y));
	}

	public void remove(double x, double y) {
		remove(new Coordinates(x,y));
	}

	public void clear() {
		lista = new Vector();
	}

	public void add(Coordinates c) {
		lista.add((Object)c);
	}

	public int get(Coordinates c) {
		int i=0;
		while (i<length() && !get(i).equals(c)) i++;
		return i<length()?i:-1;
	}
	
	public boolean contains(Coordinates c) {
		return get(c)!=-1;
	}
	
	public void remove(Coordinates c) {
		int a = get(c);
		if (a!=-1)
			lista.remove(get(a));
	}

	public void invert(int x, int y) {
		invert(new Coordinates(x,y));
	}

	public void invert(Coordinates c) {
		if (contains(c))
			remove(c);
		else
			add(c);
	}

	public int length() {
		return lista.size();
	}

	public Coordinates get(int index) {
		return (Coordinates)lista.get(index);
	}
}

	/**
	 *	Azon objektumok tárolására, melyeknek számít az
	 *	iránya és a pozíciója
	 *	@author		tRehak
	 */

class directionItem {

	private double X;
	private double Y;
	private int Dir;
	
	directionItem(double x, double y, int dir) {
		X = x;
		Y = y;
		Dir = dir;
	}
	
	public double GetX() {
		return X;
	}
	public double GetY() {
		return Y;
	}
	public int GetDir() {
		return Dir;
	}
	
	public void SetX(double x) {
		X = x;
	}
	public void SetY(double y) {
		Y = y;
	}
	public void SetDir(int dir) {
		Dir = dir;
	}
}

	/**
	 *	Bombák tárolására
	 *	Kell: pozíció, timetolive
	 *	@author		tRehak
	 */

class bombItem {

	private double X;
	private double Y;
	private int Timetolive;

	bombItem(double x, double y, int time) {
		X = x;
		Y = y;
		Timetolive = time;
	}
	
	public double GetX() {
		return X;
	}
	public double GetY() {
		return Y;
	}
	public int GetTimetolive() {
		return Timetolive;
	}
	
	public void SetX(double x) {
		X = x;
	}
	public void SetY(double y) {
		Y = y;
	}
	public void SetTimetolive(int dir) {
		Timetolive = dir;
	}
}

	/**
	 *	Bonus-ok tárolására
	 *	Kell: pozíció, timetolive, value
	 *	@author		tRehak
	 */

class bonusItem {

	private double X;
	private double Y;
	private int Timetolive;
	private int Value;
	
	bonusItem(double x, double y, int timetolive, int value) {
		X = x;
		Y = y;
		Timetolive = timetolive;
		Value = value;
	}

	bonusItem(double x, double y, int timetolive) {
		X = x;
		Y = y;
		Timetolive = timetolive;
		Value = 0;
	}
	
	public double GetX() {
		return X;
	}
	public double GetY() {
		return Y;
	}
	public int GetTimetolive() {
		return Timetolive;
	}
	public int GetValue() {
		return Value;
	}
	
	public void SetX(double x) {
		X = x;
	}
	public void SetY(double y) {
		Y = y;
	}
	public void SetTimetolive(int timetolive) {
		Timetolive = timetolive;
	}
	public void SetValue(int value) {
		Value = value;
	}
}

	/**
	 *	Egy cella a labirintusban
	 *	@author		Vava
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

// lehal jól ha nem normális paramétert kap... ehhe
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
	 *	Információk megjelenítése a felület jobb oldalán
	 *	@author		tRehak
	 */

class infoCanvas extends Canvas {

	private Outputter prg;
	
	/**
	 *	Méret
	 */

	private int Y = 360;
	private int X = 160;
	private int menuItemY = 20;
	private static int szegely = 2;
	private static int szegelyInner = 2;

	/**
	 *	Dirty code from Vava.
	 *	Nem tudom, hogy mûködik, nem volt idõ elemezni. Csak átvettem
	 *	@author		Vava
	 */

	private Color backgroundColor = Color.white;

	private static final int ITEM_BACK = 0;
	private static final int ITEM_STR1 = 1;
	private static final int ITEM_STR2 = 2;
	private Color[] activeColor = {Color.black,Color.white,Color.yellow};
	private Color[] normalColor = {Color.blue,Color.white,Color.yellow};

	infoCanvas(Outputter parent) {
		prg = parent;
		setSize(X, Y);
	}
	
	/**
	 *	Egy infomezõ megrajzolása
	 *	@author		Vava
	 */
	
	private void drawBlock(Graphics g, int pos, String str1, String str2) {
		Color[] color = normalColor;
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
	 *	Adatok publikálása
	 *	@author		tRehak (original from Vava)
	 */

	public void update(Graphics g) {
		BufferedImage img = (BufferedImage)createImage(X, Y);
		Graphics g2 = img.createGraphics();
		drawBlock(g2,0,"time", ": " + prg.outData.getTime());
		drawBlock(g2,1,"lives", ": " + prg.outData.getLives());
		drawBlock(g2,2,"score", ": " + prg.outData.getScore());
		drawBlock(g2,3,"bombs@pacman", ": " + prg.outData.getPacmansBombs());
		drawBlock(g2,4,"crystals2collect", ": " + prg.outData.crystalList.length());
		drawBlock(g2,5,"probability", ": " + prg.outData.getProbability());
		g.drawImage(img,0,0,this);
	}
}

	/**
	 *	A játék megjelenítése a felület bal oldalán
	 *	@author		tRehak
	 */

class mazeCanvas extends Canvas {

	private Outputter prg;

	/**
	 *	Méret
	 *	@author		tRehak
	 */
	
	private int X = 400;
	private int Y = 400;
	private int fieldX = 30;
	private int fieldY = 30;
	int selX = 0;
	int selY = 0;
	
	mazeCanvas(Outputter parent) {
		prg = parent;
		setSize(prg.maze.getSizeX()*fieldX, prg.maze.getSizeY()*fieldY);
	}
	
	public void paint(Graphics g) {
		update(g);
	}

	/**
	 *	Egy cella kirajzolása
	 *	@author		Vava
	 */

	private void drawField(Graphics g, int x, int y, FieldItem f) {
		if (f.isBlocked()) {
			g.setColor(Color.gray);
		} else {
			g.setColor(Color.white);
		}
		g.fillRect(fieldX*x, fieldY*y, fieldX, fieldY);
		g.setColor(Color.gray);
		g.drawRect(fieldX*x, fieldY*y, fieldX-1, fieldY-1);
		g.setColor(Color.black);
		if (f.isWall(FieldItem.UP)) {
			g.fillRect(fieldX*x, fieldY*y, fieldX, 2);
		}
		if (f.isWall(FieldItem.DN)) {
			g.fillRect(fieldX*x, fieldY*(y+1)-2, fieldX, 2);
		}
		if (f.isWall(FieldItem.LEFT)) {
			g.fillRect(fieldX*x, fieldY*y, 2, fieldY);
		}
		if (f.isWall(FieldItem.RIGHT)) {
			g.fillRect(fieldX*(x+1)-2, fieldY*y, 2, fieldY);
		}
	}
	
	/**
	 *	A pacman vagy a szörnyek irányának megrajzolása
	 *	Késõbb felesleges lesz a megfelelõ grafika miatt
	 *	@author		tRehak
	 */

	public void drawDir(Graphics g, double x, double y, int dir) {
		double X = 0;
		double Y = 0;
		switch (dir) {
			case 0:X=fieldX*x;Y=fieldY*y-(fieldY*0.25);break;
			case 1:X=fieldX*x+(fieldX*0.25);Y=fieldY*y;break;
			case 2:X=fieldX*x;Y=fieldY*y+(fieldY*0.25);break;
			case 3:X=fieldX*x-(fieldX*0.25);Y=fieldY*y;break;
			case 4:X=fieldX*x;Y=fieldY*y;break;
		}
		g.setColor(Color.red);
		g.drawLine((int)(fieldX*x),(int)(fieldY*y),(int)X,(int)Y);
	}
	
	/**
	 *	Kirajzolja a bonus timetolive param-jat
	 *	@author		tRehak
	 */

	public void drawT(Graphics g, double x, double y, int Timetolive) {
		g.setColor(Color.white);
		g.drawString(Integer.toString(Timetolive), (int)(fieldX*(x-0.25)), (int)(fieldY*(y+0.15)));
		
	}

	/**
	 *	Az egyes entitások kirajzolása
	 *	@author		tRehak (original by Vava)
	 */

	public void drawBPlace(Graphics g, double x, double y) {
		g.setColor(Color.green);
		g.fillRect((int)(fieldX*(x+0.25)),(int)(fieldY*(y+0.25)),
				(int)(fieldX*0.5),(int)(fieldY*0.5));
	}

	public void drawCrystal(Graphics g, double x, double y) {
		g.setColor(Color.red);
		g.fillRect((int)(fieldX*(x-0.25)), (int)(fieldY*(y-0.25)),
				(int)(fieldX*0.5), (int)(fieldY*0.5));
	}

	public void drawTimeBonus(Graphics g, double x, double y, int Timetolive) {
		g.setColor(Color.magenta);
		g.fillRect((int)(fieldX*(x-0.25)),(int)(fieldY*(y-0.25)),
				(int)(fieldX*0.5),(int)(fieldY*0.5));
		drawT(g,x,y,Timetolive);
	}

	public void drawScoreBonus(Graphics g, double x, double y, int Timetolive) {
		g.setColor(Color.pink);
		g.fillRect((int)(fieldX*(x-0.25)),(int)(fieldY*(y-0.25)),
				(int)(fieldX*0.5),(int)(fieldY*0.5));
		drawT(g,x,y,Timetolive);
	}
	public void drawBombBonus(Graphics g, double x, double y, int Timetolive) {
		g.setColor(Color.orange);
		g.fillRect((int)(fieldX*(x-0.25)),(int)(fieldY*(y-0.25)),
				(int)(fieldX*0.5),(int)(fieldY*0.5));
		drawT(g,x,y,Timetolive);
	}
	
	public void drawActiveBomb(Graphics g, double x, double y, int Timetolive) {
		g.setColor(Color.darkGray);
		g.fillRect((int)(fieldX*(x-0.25)),(int)(fieldY*(y-0.25)),
				(int)(fieldX*0.5),(int)(fieldY*0.5));
		drawT(g,x,y,Timetolive);
	}
	
	public void drawInactiveBomb(Graphics g, double x, double y, int Timetolive) {
		g.setColor(Color.lightGray);
		g.fillRect((int)(fieldX*(x-0.25)),(int)(fieldY*(y-0.25)),
				(int)(fieldX*0.5),(int)(fieldY*0.5));
		drawT(g,x,y,Timetolive);
	}

	public void drawDumbMonster(Graphics g, double x, double y, int dir) {
		g.setColor(Color.cyan);
		g.fillRect((int)(fieldX*(x-0.25)),(int)(fieldY*(y-0.25)),
				(int)(fieldX*0.5),(int)(fieldY*0.5));
		drawDir(g,x,y,dir);
	}
	public void drawCleverMonster(Graphics g, double x, double y, int dir) {
			g.setColor(Color.blue);
			g.fillRect((int)(fieldX*(x-0.25)),(int)(fieldY*(y-0.25)),
					(int)(fieldX*0.5),(int)(fieldY*0.5));
		drawDir(g,x,y,dir);
	}
	
	public void drawPacman(Graphics g, double x, double y, int dir) {
		g.setColor(Color.yellow);
		g.fillRect((int)(fieldX*(x-0.25)),(int)(fieldY*(y-0.25)),
				(int)(fieldX*0.5),(int)(fieldY*0.5));
		drawDir(g,x,y,dir);
	}

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
		x0 = 0;
		y0 = 0;

		BufferedImage img = (BufferedImage)createImage(X, Y);
		Graphics g2 = img.createGraphics();

	/**
	 *	Rohangálás az entitásokon, azok kirajzolása
	 *	@author		tRehak
	 */

		for (int y=0; y<sizeY-y0 && y<yh; y++) {
			for (int x=0; x<sizeX-x0 && x<xw; x++) {
				drawField(g2,x,y,prg.maze.getFieldAt(x+x0,y+y0));
			}
		}
		for (int i=0; i<prg.outData.crystalList.length(); i++) {
			drawCrystal(g2,prg.outData.crystalList.get(i).getX()-x0,
					prg.outData.crystalList.get(i).getY()-y0);
		}

		for (int i=0; i<prg.outData.bombBonusList.size(); i++ ) {
			bonusItem temp = (bonusItem)prg.outData.bombBonusList.get(i);
			drawBombBonus(g2,(temp.GetX()-x0),
						(temp.GetY()-y0),
						temp.GetTimetolive()
					);
		}

		for (int i=0; i<prg.outData.timeBonusList.size(); i++ ) {
			bonusItem temp = (bonusItem)prg.outData.timeBonusList.get(i);
			drawTimeBonus(g2,(temp.GetX()-x0),
						(temp.GetY()-y0),
						temp.GetTimetolive()
					);
		}

		for (int i=0; i<prg.outData.scoreBonusList.size(); i++ ) {
			bonusItem temp = (bonusItem)prg.outData.scoreBonusList.get(i);
			drawScoreBonus(g2,(temp.GetX()-x0),
						(temp.GetY()-y0),
						temp.GetTimetolive()
					);
		}

		for (int i=0; i<prg.outData.activeBombList.size(); i++ ) {
			bombItem temp = (bombItem)prg.outData.activeBombList.get(i);
			drawActiveBomb(g2,(temp.GetX()-x0),
						(temp.GetY()-y0),
						temp.GetTimetolive()
					);
		}

		for (int i=0; i<prg.outData.inactiveBombList.size(); i++ ) {
			System.out.println ("faszom");
			bombItem temp = (bombItem)prg.outData.inactiveBombList.get(i);
			drawInactiveBomb(g2,(temp.GetX()-x0),
						(temp.GetY()-y0),
						temp.GetTimetolive()
					);
		}

		if (prg.maze.birthPlace!=null) {
			drawBPlace(g2,prg.maze.birthPlace.getX()-x0,
					prg.maze.birthPlace.getY()-y0);
		}

		for (int i=0; i<prg.outData.dumbMonsterList.size(); i++ ) {
			directionItem temp = (directionItem)prg.outData.dumbMonsterList.get(i);
			drawDumbMonster(g2,(temp.GetX()-x0),
						(temp.GetY()-y0),
						temp.GetDir()
					);
		}
		for (int i=0; i<prg.outData.cleverMonsterList.size(); i++ ) {
			directionItem temp = (directionItem)prg.outData.cleverMonsterList.get(i);
			drawCleverMonster(g2,(temp.GetX()-x0),
						(temp.GetY()-y0),
						temp.GetDir()
					);
		}

		if (prg.outData.pacman!=null) {
			drawPacman(g2,(prg.outData.pacman.GetX()-x0),
					(prg.outData.pacman.GetY()-y0),
					(prg.outData.pacman.GetDir()));
		}
		g2.setColor(Color.black);
		g.drawImage(img,0,0,this);
	}

}

	/**
	 *	Egy állapot tárolására alkalmas objektum.
	 *	@author		tRehak
	 */

class StepData extends Object {

	/**
	 *	Alap adatok
	 *	@author		tRehak
	 */

	private int X;
	private int Y;
	private int time;
	private int lives;
	private int score;
	public int monsterDumb;
	public int monsterClever;
	private double probability;
	private int pacmansbombs;

	/**
	 *	Collection-ök
	 *	@author		tRehak
	 */

	public myList crystalList = new myList();
	public Vector dumbMonsterList = new Vector();
	public Vector cleverMonsterList = new Vector();
	public Vector activeBombList = new Vector();
	public Vector inactiveBombList = new Vector();
	public Vector bombBonusList = new Vector();
	public Vector scoreBonusList = new Vector();
	public Vector timeBonusList = new Vector();
	public directionItem pacman;

	/**
	 *	Inicializáció
	 *	@author		tRehak
	 */
	
	StepData () {
		time = 0;
		probability = 0;
		monsterDumb = 0;
		monsterClever = 0;
		pacmansbombs = 0;
		crystalList = new myList();
		dumbMonsterList = new Vector();
		cleverMonsterList = new Vector();
		activeBombList = new Vector();
		inactiveBombList = new Vector();

		bombBonusList = new Vector();
		scoreBonusList = new Vector();
		timeBonusList = new Vector();
		pacman = null;
	}

	/**
	 *	Lekérdezõ és beállító függvények
	 *	@author		tRehak
	 */

	public int getCrystals() {
		return crystalList.length();
	}
	
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

	public int getSizeY() {
		return Y;
	}
	
	public int getTime() {
		return time;
	}
	
	public void setTime(int time) {
		this.time = time;
	}
	
	public int getLives() {
		return lives;
	}
	
	public void setLives(int lives) {
		this.lives = lives;
	}
	
	public int getScore() {
		return score;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	public int getPacmansBombs() {
		return pacmansbombs;
	}
	
	public void setPacmansBombs(int pacmansbombs) {
		this.pacmansbombs = pacmansbombs;
	}
	
	public double getProbability() {
		return probability;
	}

	public void setProbability(double p) {
		probability = p;
	}

}

	/**
	 *	A labirintust leíró objektum
	 *	@author		tRehak
	 */

class Maze extends Object {

	public int X;
	public int Y;

	/**
	 *	A mezõk listája
	 *	@author		tRehak
	 */

	public FieldItem[] mazeData;
	
	/**
	 *	Szörnyek születési helye
	 *	@author		tRehak
	 */

	public Coordinates birthPlace;
	
	Maze (int sizeX, int sizeY) {
		mazeData = new FieldItem[sizeX*sizeY];
		for (int i=0; i<sizeX*sizeY; i++) {
			mazeData[i]=new FieldItem();
		}
		birthPlace = null;
		X = sizeX;
		Y = sizeY;
	}

	public FieldItem getFieldAt(int x, int y) {
		return mazeData[this.X*y + x];
	}

	public int getSizeX() {
		return X;
	}

	public int getSizeY() {
		return Y;
	}
}

	/**
	 *	A labirintus leíró file feldolgozója
	 *	@author		tRehak
	 */

class LParser extends Object {

	Outputter prg;
	
	/**
	 *	Parsolás
	 *	@param	fname	file neve
	 *	@return		a megfelelõ objektumok tulajdonságait beáálítja
	 *	@author		tRehak
	 */

	LParser(String fname, Outputter parent) {
		prg = parent;
		InputStream is;
		try {
			is = new FileInputStream(fname);
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: File NOT found!");
			System.exit(-1);
			return;
		}
		ourParser parser = new ourParser(is);
		int mazeDataPos = 0;
		while (parser.getNextLine()==ourParser.TT_OK) {
			if (parser.matchLine("pacman %n %n %n")) {
				prg.inData.pacman = new directionItem(Double.valueOf(parser.lineTokens[1]).doubleValue(), Double.valueOf(parser.lineTokens[2]).doubleValue(), 1);
			} else if (parser.matchLine("time %n")) {
				prg.inData.setTime ( Integer.valueOf(parser.lineTokens[1]).intValue() );
			} else if (parser.matchLine("size %n %n")) {
				prg.maze.X = Integer.valueOf(parser.lineTokens[1]).intValue();
				prg.maze.Y = Integer.valueOf(parser.lineTokens[2]).intValue();
				prg.maze.mazeData = new FieldItem[prg.maze.X*prg.maze.Y];
			} else if (parser.matchLine("monster clever %n %n %n")) {
						// monster clever x y dir speed
				prg.inData.monsterClever++;
				prg.inData.cleverMonsterList.add(new directionItem(Double.valueOf(parser.lineTokens[2]).doubleValue(),
									Double.valueOf(parser.lineTokens[3]).doubleValue(),
									1
							));
			} else if (parser.matchLine("monster dumb %n %n %n")) {
						// monster dumb x y speed
				prg.inData.monsterDumb++;
				prg.inData.dumbMonsterList.add(new directionItem(Double.valueOf(parser.lineTokens[2]).doubleValue(),
									Double.valueOf(parser.lineTokens[3]).doubleValue(),
									1
							));
			} else if (parser.matchLine("probability %n")) {
				prg.inData.setProbability ( Integer.valueOf(parser.lineTokens[1]).intValue());
			} else if (parser.matchLine("birthplace %n %n")) {
				prg.maze.birthPlace = new Coordinates(Integer.valueOf(parser.lineTokens[1]).intValue(),
								Integer.valueOf(parser.lineTokens[2]).intValue());
			} else if (parser.matchLine("crystal %n %n")) {
				prg.inData.crystalList.add(Double.valueOf(parser.lineTokens[1]).doubleValue(),
						Double.valueOf(parser.lineTokens[2]).doubleValue());
			} else if (parser.matchLine("fields {%n}")) {
				if (prg.maze.mazeData==null) {
					System.out.println("ERROR: undefinied maze size!");
					System.exit(-1);
				} else {
					int i;
					for (i=1; i<parser.lineTokens.length && mazeDataPos+i-1<prg.maze.mazeData.length; i++) {
						prg.maze.mazeData[mazeDataPos+i-1] = new FieldItem(parser.lineTokens[i]);
						if (parser.lineTokens[i].length()!=4) {
							System.out.println("ERROR: incorrect maze data!");
							System.exit(-1);
						}
					}
					mazeDataPos+=i-1;
					if (i<parser.lineTokens.length) {
						System.out.println("ERROR: too much maze data!");
						System.exit(-1);
					}
				}
			}
		}
		if (prg.maze.X<0 || prg.maze.Y<0) {
			System.out.println("ERROR: buggy maze size!");
			System.exit(-1);
		}
		if (prg.maze.mazeData==null) {
			System.out.println("ERROR: undefined maze!");
			System.exit(-1);
		}
		if (mazeDataPos<prg.maze.X*prg.maze.Y) {
			System.out.println("ERROR: missing mazedata!");
			System.exit(-1);
		}
	}
}

	/**
	 *	Fõ osztály, ez tud mindent :)))
	 *	@author		tRehak
	 */

public class Outputter extends Object {

	/**
	 *	Az ablakunk
	 *	@author		tRehak
	 */

	Frame frame;
	
	/**
	 *	A labirintus rajzfelülete
	 *	@author		tRehak
	 */
	
	mazeCanvas MazeCanvas;

	/**
	 *	infomezõk rajzfelülete
	 *	@author		tRehak
	 */

	infoCanvas InfoCanvas;
	
	/**
	 *	Labirintus
	 *	@author		tRehak
	 */

	Maze maze;
	
	/**
	 *	Üzenetmezõ
	 *	@author		tRehak
	 */

	TextArea uzenet;

	/**
	 *	Továbblépéshez gomb
	 *	@author		tRehak
	 */

	Button step;

	/**
	 *	Ebbe olvassuk az inputot
	 *	@author		tRehak
	 */

	StepData inData;
	
	/**
	 *	Ezt rajzoljuk ki
	 *	@author		tRehak
	 */

	StepData outData;
	
	/**
	 *	Labirintus file feldolgozó 1 példánya
	 *	@author		tRehak
	 */

	LParser lparser;

	/**
	 *	Itt ragadt, késõbb mégis felhasznált változó
	 *	Pl. azt dönti el, hogy felismerjük-e a bemenetet avagy sem
	 *	@author		tRehak
	 */
	boolean go = false;
	
	/**
	 *	A felsõ panel. Ezen van a step gomb és a cb checkbox
	 *	@author		tRehak
	 */

	Panel felul;
	
	/**
	 *	Azt vezérli, hogy kell-e a step gomb egy lépés megjelenítéséhez
	 *	vagy automatikusan update-el
	 *	@author		tRehak
	 */

	Checkbox cb;
	
	/**
	 *	A labirintus scroller-e
	 *	@author		tRehak
	 */

	public ScrollPane scroller;
	
	/**
	 *	A textarea scroller-e
	 *	@author		tRehak
	 */

	public ScrollPane scrollerdown;
	
	/**
	 *	Konstruktor, inicializáció
	 *	@author		tRehak
	 */

	Outputter() {
		inData = new StepData();
		outData = new StepData();
		maze = new Maze(10,10);
		uzenet = new TextArea("Outputter v0.02\n",10,80);
		uzenet.setEditable(false);
		initView();
	}
	
	void refresh() {
		frame.pack();
	}
	
	/**
	 *	Felpakolja a grafikus kinézetet
	 *	@author		tRehak
	 */

	private void initView() {

	/**
	 *	ablak generálás
	 *	@author		tRehak
	 */

		frame = new Frame("Outputter");

	/**
	 *	Nem árt, ha be is csukódik, ha bezárják
	 *	@author		tRehak
	 */

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				frame.setVisible(false);
				System.out.println("Bye");
				System.exit(0);
			}
		});
		
		frame.setLayout(new BorderLayout());

	/**
	 *	A labirintus rajzfelületének készítése
	 *	Létrehozás, scroller hozzáadás, felrakás
	 *	@author		tRehak
	 */

		scroller = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
		MazeCanvas = new mazeCanvas(this);
		
		scroller.setSize(400,400);
		scroller.add(MazeCanvas);
		frame.add(scroller, BorderLayout.CENTER);
		
	/**
	 *	Az információs mezõ rajzfelülete
	 *	@author		tRehak
	 */
		
		InfoCanvas = new infoCanvas(this);
		frame.add(InfoCanvas, BorderLayout.EAST);

	/**
	 *	A felsõ panel a button-al és a checkbox-al
	 *	@author		tRehak
	 */

		felul = new Panel();
		felul.setLayout(new BorderLayout());

		step = new Button("Step");
		step.setEnabled(false);

	/**
	 *	Ha megnyomják a gombot, mikor enabled, kirajzolja a soron
	 *	következõ lépést
	 *	@author		tRehak
	 */

		class StepListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
					outData=inData;
					inData = new StepData();
					MazeCanvas.repaint();
					InfoCanvas.repaint();
					step.setEnabled(false);
			}
		}
		step.addActionListener(new StepListener());
		felul.add(step, BorderLayout.CENTER);

	/**
	 *	Ha ki van "pipálva", akkor nem kell a step gomb egy
	 *	újrarajzoláshoz
	 *	@author		tRehak
	 */

		cb = new Checkbox("Auto",false);
		felul.add(cb, BorderLayout.EAST);

		frame.add(felul, BorderLayout.NORTH);

		scrollerdown = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
		scrollerdown.setSize(400,200);
		scrollerdown.add(uzenet);
		frame.add(scrollerdown, BorderLayout.SOUTH);
		
		frame.pack();
		frame.setSize(600, 480);
		frame.show();
	}

	/**
	 *	A beolvasás rutinja, nem túl jó helyen, de most itt van.
	 *	@param	text	mi az olvasott szöveg
	 *	@return	int	ugyanez számmal ábrázolva
	 *	@author		tRehak
	 */

	private int getDir(String text) {
		if (text.equalsIgnoreCase("UP")) {
			return 0;
		} else if (text.equalsIgnoreCase("RIGHT")) {
			return 1;
		} else if (text.equalsIgnoreCase("DOWN")) {
			return 2;
		} else if (text.equalsIgnoreCase("LEFT")) {
			return 3;
		} else if (text.equalsIgnoreCase("STOPPED")) {
			return 4;
		} else return -1;
	}
	

	/**
	 *	main() függvény, a belépési pont
	 *	@author		tRehak
	 */

	public static void main(String[] args) {
	
		Outputter e = new Outputter();

	/**
	 *	valahol el kell kezdeni... :)
	 *	@author		tRehak
	 */

		System.out.println("HelloWorld!");
		
		int b=0;

	/**
	 *	parsolás kötése a stdin-hez
	 *	@author		tRehak
	 */

		ourParser parser = new ourParser(System.in);
		while (parser.getNextLine()==ourParser.TT_OK) {
				String ki = new String();
				for (int i=0; i<parser.lineTokens.length; i++) {
					ki = ki + parser.lineTokens[i] + " ";
				}

	/**
	 *	echozás
	 *	@author		tRehak
	 */

				System.out.println(ki);
				e.go = true;
	/**
	 *	showmenu
	 *	action:		echo
	 *	@author		tRehak
	 */

				if (parser.matchLine("showmenu")) {
					e.uzenet.append("showmenu\n");
	/**
	 *	highscore
	 *	action:		kiírja a nevet és az értéket
	 *	@author		tRehak
	 */

				} else if (parser.matchLine("highscore %c %n")) {
					e.uzenet.append("Highscore entry: + " + parser.lineTokens[1] + " " + parser.lineTokens[2] + "\n");
	/**
	 *	load
	 *	action:		betölt egy pályát
	 *	@author		tRehak
	 */

				} else if (parser.matchLine("load %c")) {
					e.lparser = new LParser(parser.lineTokens[1],e);
					e.MazeCanvas.repaint();
	/**
	 *	game
	 *	action:		beállítja a score-t és a lives-t
	 *	@author		tRehak
	 */

				} else if (parser.matchLine("game %n %n")) {
					e.inData.setScore(Integer.valueOf(parser.lineTokens[1]).intValue());
					e.inData.setLives(Integer.valueOf(parser.lineTokens[2]).intValue());
	/**
	 *	maze
	 *	action:		beállítja az time-ot
	 *	@author		tRehak
	 */

				} else if (parser.matchLine("maze %n")) {
					e.inData.setTime(Integer.valueOf(parser.lineTokens[1]).intValue());
	/**
	 *	pacman
	 *	action		pacman entitást machinál, felveszi, hogy hány
	 *			bombája van
	 *	@author		tRehak
	 */

				} else if (parser.matchLine("pacman %n %n %n %c %n")) {
					e.inData.pacman = new directionItem(Double.valueOf(parser.lineTokens[2]).doubleValue(),
										Double.valueOf(parser.lineTokens[3]).doubleValue(),
										e.getDir(parser.lineTokens[4]));
					e.inData.setPacmansBombs(Integer.valueOf(parser.lineTokens[5]).intValue());
	/**
	 *	monster
	 *	action:		hozzáadja a dumb vagy clever listához
	 *	@author		tRehak
	 */

				} else if (parser.matchLine("monster %n (dumb|clever) %n %n %c")) {
					if (parser.lineTokens[2].equals("dumb")) {
						e.inData.dumbMonsterList.add(new directionItem(Double.valueOf(parser.lineTokens[3]).doubleValue(),
												Double.valueOf(parser.lineTokens[4]).doubleValue(),
												e.getDir(parser.lineTokens[5])
										));
					}
					if (parser.lineTokens[2].equals("clever")) {
						e.inData.cleverMonsterList.add(new directionItem(Double.valueOf(parser.lineTokens[3]).doubleValue(),
												Double.valueOf(parser.lineTokens[4]).doubleValue(),
												e.getDir(parser.lineTokens[5])
										));
					}
	/**
	 *	bomb
	 *	action:		hozzáad egy bombát az aktív vagy inaktív listához
	 *	@author		tRehak
	 */

				} else if (parser.matchLine("bomb %n %n %n (active|inactive) %n")) {
					if (parser.lineTokens[4].equals("active")) {
						e.inData.activeBombList.add(new bombItem (Double.valueOf(parser.lineTokens[2]).doubleValue(),
											Double.valueOf(parser.lineTokens[3]).doubleValue(),
											Integer.valueOf(parser.lineTokens[5]).intValue()
										));
					}
					if (parser.lineTokens[4].equals("inactive")) {
						e.inData.inactiveBombList.add(new bombItem (Double.valueOf(parser.lineTokens[2]).doubleValue(),
											Double.valueOf(parser.lineTokens[3]).doubleValue(),
											Integer.valueOf(parser.lineTokens[5]).intValue()
										));
					}
	/**
	 *	bonus
	 *	action:		hozzáadja a bonust a score, time vagy bomb
	 *			listához
	 *	@author		tRehak
	 */

				} else if (parser.matchLine("bonus %n %n %n %c %n %n")) {
					if (parser.lineTokens[4].equals("bomb")) {
						e.inData.bombBonusList.add(new bonusItem(Double.valueOf(parser.lineTokens[2]).doubleValue(),
											Double.valueOf(parser.lineTokens[3]).doubleValue(),
											Integer.valueOf(parser.lineTokens[5]).intValue()
									));
					}
					if (parser.lineTokens[4].equals("score")) {
						e.inData.scoreBonusList.add(new bonusItem(Double.valueOf(parser.lineTokens[2]).doubleValue(),
											Double.valueOf(parser.lineTokens[3]).doubleValue(),
											Integer.valueOf(parser.lineTokens[5]).intValue(),
											Integer.valueOf(parser.lineTokens[6]).intValue()
									));
					}
					if (parser.lineTokens[4].equals("time")) {
						e.inData.timeBonusList.add(new bonusItem(Double.valueOf(parser.lineTokens[2]).doubleValue(),
											Double.valueOf(parser.lineTokens[3]).doubleValue(),
											Integer.valueOf(parser.lineTokens[5]).intValue(),
											Integer.valueOf(parser.lineTokens[6]).intValue()
									));
					}
	/**
	 *	crystal
	 *	action:		hozzáad egy kristályt a listához
	 *	@author		tRehak
	 */

				} else if (parser.matchLine("crystal %n %n %n")) {
					e.inData.crystalList.add(new Coordinates(Double.valueOf(parser.lineTokens[2]).doubleValue(),
										Double.valueOf(parser.lineTokens[3]).doubleValue()
								));
	/**
	 *	step
	 *	action:		végrehajt egy lépést, frissíti a kimenetet
	 *	@author		tRehak
	 */

				} else if (parser.matchLine("step")) {
					if (e.cb.getState()) {
						e.go = true;
						e.outData=e.inData;
						e.inData = new StepData();
						e.MazeCanvas.repaint();
						e.InfoCanvas.repaint();
						e.step.setEnabled(false);
					} else {
						e.step.setEnabled(true);
						e.uzenet.append("STEP - push button!\n");
						while (e.step.isEnabled()) {}
					}

	/**
	 *	warning
	 *	action:		warning-ot átviszi a textarea-ba
	 *	@author		tRehak
	 */

				} else if (parser.matchLine("warning {%c}")) {
					e.uzenet.append("warning:");
					for (int i=1;i<parser.lineTokens.length;i++) {
						e.uzenet.append(" " + parser.lineTokens[i]);
					}
					e.uzenet.append("\n");
	/**
	 *	error
	 *	action:		error-t átviszi a textarea-ba
	 *	@author		tRehak
	 */

				} else if (parser.matchLine("error {%c}")) {
					e.uzenet.append("error:");
					for (int i=1;i<parser.lineTokens.length;i++) {
						e.uzenet.append(" " + parser.lineTokens[i]);
					}
					e.uzenet.append("\n");
				} else if (parser.matchLine("event (pacman|bomb|bonus|crystal|monster) %n (died|born) %n %n")) {
					e.uzenet.append("event: " + parser.lineTokens[1] + " " + parser.lineTokens[3] + " at (" + parser.lineTokens[4] + ";" + parser.lineTokens[5] + ")\n");
				} else if (parser.matchLine("event (startgame|exitgame|exithighscore|enterhighscore|exit|timeout)")) {
					System.out.println("event: " + parser.lineTokens[1] + "\n");
				} else if (parser.matchLine("event bomb %n activate")) {
					System.out.println("event: bomb " + parser.lineTokens[2] + " activated\n");
				} else if (parser.matchLine("remark {%c}")) {
					e.uzenet.append("remark:");
					for (int i=1;i<parser.lineTokens.length;i++) {
						e.uzenet.append(" " + parser.lineTokens[i]);
					}
					e.uzenet.append("\n");
				} else {
					e.go = false;
				}
	/**
	 *	Ha eddig nem tudta parsolni, hibát dobunk
	 *	@author		tRehak
	 */

				if (e.go) {e.uzenet.append(ki + "\n");}
				else {e.uzenet.append("Unknown command: " + ki + "\n");}
		}

	}
}
