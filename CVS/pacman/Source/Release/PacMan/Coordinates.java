// $Id: Coordinates.java,v 1.2 2001/05/17 12:34:18 lptr Exp $
// $Date: 2001/05/17 12:34:18 $
// $Author: lptr $

package PacMan;

import java.lang.*;

/**
 * Egy koordin�tap�r t�rol�s�ra alkalmas oszt�ly
 */
public class Coordinates {

	/*
	 * Konstansok
	 */
	public final static int DIR_UP = 0;
	public final static int DIR_RIGHT = 1;
	public final static int DIR_DOWN = 2;
	public final static int DIR_LEFT = 3;
	public final static int DIR_STOP = 4;

	final static String[] DIRS = {"Up", "Right", "Down", "Left", "Stopped"};

	/**
	 * A p�lyaelem poz�ci�ja.
	 */
	public double x,y;

	/**
	 * Egy koordin�ta p�r l�trehoz�sa.
	 *
	 * @author	L�ci
	 * @param   _x  X koordin�ta.
	 * @param   _y  Y koordin�ta.
	 * @param	_dir	Ir�ny.
	 * @param	_speed	Sebess�g.
	 */
	Coordinates(double _x, double _y) {
		x = _x;
		y = _y;
	}

	/**
	 * Copy-konstruktor
	 *
	 * @param   p  A m�soland� koordin�ta.
	 */
	Coordinates(Coordinates p) {
		this (p.x, p.y);
	}

	/**
	 * Egy koordin�tap�r l�trehoz�sa. A param�terek a mez� sz�m�t jel�lik ki, aminek
	 * a k�zep�t (x+0.5,y+0.5) fogja jel�lni az objektum.
	 */
	Coordinates(int _x, int _y) {
		this (_x + 0.5d, _y + 0.5d);
	}

	/**
	 * Visszaadja a koordin�t�t tartalmaz� mez� x koordin�t�j�t.
	 */
	public int getX() {
		return (int)x;
	}

	/**
	 * Visszaadja a koordin�t�t tartalmaz� mez� y koordin�t�j�t.
	 */
	public int getY() {
		return (int)y;
	}

	/**
	 * �sszehasonl�t k�t koordin�t�t.
	 *
	 * @return	true-val t�r vissza, ha a k�t koordin�ta egyezik.
	 */
	public boolean equals(Coordinates p) {
		return p.x == x && p.y == y;
	}

	/**
	 * Sz�veges ir�nyb�l int-et k�pez.
	 *
	 * @return	Az ir�ny DIR_* szerint.
	 */
	public static int ConvertDirection (String dir) {
		if (dir.equalsIgnoreCase("up")) {
			return DIR_UP;
		} else
		if (dir.equalsIgnoreCase("right")) {
			return DIR_RIGHT;
		} else
		if (dir.equalsIgnoreCase("down")) {
			return DIR_DOWN;
		} else
		if (dir.equalsIgnoreCase("left")) {
			return DIR_LEFT;
		} else
		if (dir.equalsIgnoreCase("stop") ||
			dir.equalsIgnoreCase("stopped")) {
			return DIR_STOP;
		}
		return -1;
	}

	/**
	 * �sszehasonl�t k�t ir�nyt.
	 *
	 * @return	true-val t�r vissza, ha mindk�t param�terben megadott ir�ny v�zszintes 
	 * 			ill. f�gg�leges.
	 */
	public static boolean CompareDirections(int dira, int dirb) {
		return
			dira != DIR_STOP &&
			dirb != DIR_STOP &&
			dira + 1 != dirb &&
			dira - 1 != dirb;
	}

	/**
	 * Visszaadja az ellenkez� ir�nyt.
	 *
	 * @return	Az elenkez� ir�ny.
	 */
	public static int OppositeDirection (int dir) {
		switch (dir) {
		case DIR_LEFT:
			return DIR_RIGHT;
		case DIR_RIGHT:
			return DIR_LEFT;
		case DIR_UP:
			return DIR_DOWN;
		case DIR_DOWN:
			return DIR_UP;
		default:
			return DIR_STOP;
		}
	}

	/**
	 * Visszaadja, milyen ir�nyban kell elindulni az adott pont fel�.
	 *
	 * @return	A pont fel� vezet� ir�ny.
	 */
	public int DirectionTowards(Coordinates p) {
		double xd = Math.abs (x - p.x);
		double yd = Math.abs (y - p.y);

		if (xd < 0.001 && yd < 0.001) {
			return DIR_STOP;
		}
		if (xd > yd) {
			return (x - p.x < 0)?DIR_RIGHT:DIR_LEFT;
		} else {
			return (y - p.y < 0)?DIR_DOWN:DIR_UP;
		}
	}

	/**
	 * Visszaadja, milyen ir�nyban kell elindulni a mez� k�zepe fel�.
	 *
	 * @return	A mez� k�zep�t mutat� ir�ny.
	 */
	public int DirectionToCenter() {
		if (x - Math.floor(x) < 0.499)
			return DIR_RIGHT;
		else
		if (x - Math.floor(x) > 0.501)
			return DIR_LEFT;
		else
		if (y - Math.floor(y) < 0.499)
			return DIR_DOWN;
		else
		if (y - Math.floor(y) > 0.501)
			return DIR_UP;

		return DIR_STOP;
	}

	/**
	 * Visszaadja, milyen t�vol van a pont a mez� k�zep�t�l.
	 *
	 * @return	A mez� k�zep�t�l m�rt t�vols�g.
	 */
	public double DistanceFromCenter() {
		return Math.max(Math.abs (x - Math.floor (x) - 0.5), 
						Math.abs (y - Math.floor (y) - 0.5));
	}

	/**
	 * Visszaadja, milyen t�vol van a pont k�t pont.
	 *
	 * @return	A k�t pont t�vols�ga.
	 */
	public double GetDistance(Coordinates p) {
		return Math.sqrt ((x - p.x)*(x - p.x) + (y - p.y)*(y - p.y));
	}

	/**
	 * A koordin�t�t a mez� k�zep�re �ll�tja. ((int)x + 0.5, (int)y + 0.5)
	 */
	public void CenterOnField() {
		x = (int)x + 0.5;
		y = (int)y + 0.5;
	}

	/**
	 * Ellen�rzi, hogy a k�t pont azonos mez�n van-e.
	 *
	 * @return	A k�t pont t�vols�ga.
	 */
	public boolean SameField(Coordinates p) {
		return (int)p.x == (int)x && (int)p.y == (int)y;
	}

	/**
	 * Ellen�rzi, hogy k�t pont adott t�vols�gon bel�l van-e.
	 *
	 * @return	true-val t�r vissza, ha k�zelebb vannak.
	 */
	public boolean CloserThan(Coordinates p, double distance) {
		double ax = p.x - x;
		double ay = p.y - y;
		return (ax*ax + ay*ay) <= (distance*distance);
	}

	/**
	 * "x y" form�ban adja vissza a koordin�t�kat.
	 */
	public String toString() {
		return "" + (double)(Math.round(x*100))/100 + " " + (double)(Math.round(y*100))/100;
	}

	/**
	 * Egy mez� adott ir�nyban lev� szomsz�dj�nak koordin�t�it adja vissza.
	 *
	 * @return	A szomsz�d koordin�t�i.
	 */
	public Coordinates GetNeighbour(int dir) {
		Coordinates temp=null;
		switch (dir) {
			case DIR_UP:
				temp = new Coordinates(getX(),getY()-1);
				break;
			case DIR_DOWN:
				temp = new Coordinates(getX(),getY()+1);
				break;
			case DIR_LEFT:
				temp = new Coordinates(getX()-1,getY());
				break;
			case DIR_RIGHT:
				temp = new Coordinates(getX()+1,getY());
				break;
		}
		return temp;
	}
} 