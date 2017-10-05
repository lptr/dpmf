// $Id: Coordinates.java,v 1.13 2001/04/27 03:15:44 lptr Exp $
// $Date: 2001/04/27 03:15:44 $
// $Author: lptr $

package PacMan;

import java.lang.*;

/**
 * Egy koordinátapár tárolására alkalmas osztály
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

	final static String[] DIRS = {"up", "right", "down", "left", "stopped"};

	/**
	 * A pályaelem pozíciója.
	 */
	public double x,y;

	/**
	 * Egy koordináta pár létrehozása.
	 *
	 * @author	Lóci
	 * @param   _x  X koordináta.
	 * @param   _y  Y koordináta.
	 * @param	_dir	Irány.
	 * @param	_speed	Sebesség.
	 */
	Coordinates(double _x, double _y) {
		x = _x;
		y = _y;
	}

	/**
	 * Copy-konstruktor
	 *
	 * @param   p  A másolandó koordináta.
	 */
	Coordinates(Coordinates p) {
		this (p.x, p.y);
	}

	/**
	 * Egy koordinátapár létrehozása. A paraméterek a mezõ számát jelölik ki, aminek
	 * a közepét (x+0.5,y+0.5) fogja jelölni az objektum.
	 */
	Coordinates(int _x, int _y) {
		this (_x + 0.5d, _y + 0.5d);
	}

	/**
	 * Visszaadja a koordinátát tartalmazó mezõ x koordinátáját.
	 */
	public int getX() {
		return (int)x;
	}

	/**
	 * Visszaadja a koordinátát tartalmazó mezõ y koordinátáját.
	 */
	public int getY() {
		return (int)y;
	}

	/**
	 * Összehasonlít két koordinátát.
	 *
	 * @return	true-val tér vissza, ha a két koordináta egyezik.
	 */
	public boolean equals(Coordinates p) {
		return p.x == x && p.y == y;
	}

	/**
	 * Szöveges irányból int-et képez.
	 *
	 * @return	Az irány DIR_* szerint.
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
	 * Összehasonlít két irányt.
	 *
	 * @return	true-val tér vissza, ha mindkét paraméterben megadott irány vízszintes 
	 * 			ill. függõleges.
	 */
	public static boolean CompareDirections(int dira, int dirb) {
		return
			dira != DIR_STOP &&
			dirb != DIR_STOP &&
			dira + 1 != dirb &&
			dira - 1 != dirb;
	}

	/**
	 * Visszaadja az ellenkezõ irányt.
	 *
	 * @return	Az elenkezõ irány.
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
	 * Visszaadja, milyen irányban kell elindulni az adott pont felé.
	 *
	 * @return	A pont felé vezetõ irány.
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
	 * Visszaadja, milyen irányban kell elindulni a mezõ közepe felé.
	 *
	 * @return	A mezõ közepét mutató irány.
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
	 * Visszaadja, milyen távol van a pont a mezõ közepétõl.
	 *
	 * @return	A mezõ közepétõl mért távolság.
	 */
	public double DistanceFromCenter() {
		return Math.max(Math.abs (x - Math.floor (x) - 0.5), 
						Math.abs (y - Math.floor (y) - 0.5));
	}

	/**
	 * Visszaadja, milyen távol van a pont két pont.
	 *
	 * @return	A két pont távolsága.
	 */
	public double GetDistance(Coordinates p) {
		return Math.sqrt ((x - p.x)*(x - p.x) + (y - p.y)*(y - p.y));
	}

	/**
	 * A koordinátát a mezõ közepére állítja. ((int)x + 0.5, (int)y + 0.5)
	 */
	public void CenterOnField() {
		x = (int)x + 0.5;
		y = (int)y + 0.5;
	}

	/**
	 * Ellenõrzi, hogy a két pont azonos mezõn van-e.
	 *
	 * @return	A két pont távolsága.
	 */
	public boolean SameField(Coordinates p) {
		return (int)p.x == (int)x && (int)p.y == (int)y;
	}

	/**
	 * Ellenõrzi, hogy két pont adott távolságon belül van-e.
	 *
	 * @return	true-val tér vissza, ha közelebb vannak.
	 */
	public boolean CloserThan(Coordinates p, double distance) {
		double ax = p.x - x;
		double ay = p.y - y;
		return (ax*ax + ay*ay) <= (distance*distance);
	}

	/**
	 * "x y" formában adja vissza a koordinátákat.
	 */
	public String toString() {
		return "" + (double)(Math.round(x*100))/100 + " " + (double)(Math.round(y*100))/100;
	}

	/**
	 * Egy mezõ adott irányban levõ szomszédjának koordinátáit adja vissza.
	 *
	 * @return	A szomszéd koordinátái.
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