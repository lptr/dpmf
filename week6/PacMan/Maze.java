package PacMan;

import java.lang.*;
import java.io.*;
import java.util.*;
import java.awt.*;

/**
 * A játéktér labirintus részét megvalósító objektum. Õ kezeli a pályán mozgó vagy álló
 * pályaelemeket - térben és idõben. Az õ feladata az idõ múlásának kezelése is.<br>
 * A {@link Game} osztály hozza létre.
 *
 * @author Lóci
 */
class Maze extends SkeletonObject {

	/**
	 * A játékos által irányított PacMan objektum
	 */
	PacMan pacman;

	/**
	 * A pályán található szörnyek listája
	 */
	LinkedList monsters = new LinkedList();

	/**
	 * A pályán található bónuszok listája
	 */
	LinkedList bonuses = new LinkedList();

	/**
	 * A pályán található bombák listája
	 */
	LinkedList bombs = new LinkedList();

	/**
	 * A még össze nem szedett kristályok listája
	 */
	LinkedList crystals = new LinkedList();

	/**
	 * A szörnyek ezen a helyen születnek újjá.
	 */
	Coordinates rebornPlace;

	/**
	 * A labirintust felügyelõ {@link Game} objektum
	 */
	Game parentGame;
	int time = 0;

	/**
	 * Létrehozza a pályát.
	 */
	Maze(Game _parentGame, int levelNum) {
		parentGame = _parentGame;
		try {
			Load(levelNum);
		} catch (IOException e) {
			Error ("see bellow", e);
		}
		
		Println ("");
	}
	
	/**
	 * Betölti a levelNum által megjelölt pályát. Felépíti a mezõket tartalmazó tömböt,
	 * létrehozza a megfelelõ számú szörnyet, a PacMan-t, a kristályokat.
	 *
	 * @throws IOException Ha nem tudta megnyitni a pályát tartalmazó file-t.
	 */
	void Load(int levelNum) throws IOException {
		In("Load", "");
		
		Println ("loading level " + levelNum);

		time = 20;

		Println ("setting time to " + time);

		rebornPlace = new Coordinates (10, 10);
		Println ("reborn place set to " + rebornPlace);

		Println ("loading mazeitems:");

		pacman = new PacMan(this, new Coordinates(20, 20));

		monsters.add(new Monster(this, new Coordinates(40, 60)));
		monsters.add(new Monster(this, new Coordinates(20, 60)));

		Leave("Load", "");
	}
	
	/**
	 * Végrehajt egy szimulációs lépést. Csökkenti a hátralevõ idõt: Végighívja a pályaelemek
	 * {@link MazeItem#Act()} metódusait. Ellenõrzi a pályaelemek ütközését.
	 * Lekezeli a bónuszok felvételét, a PacMan halálát, az idõ lejártát, a pálya teljesítését.
	 *
	 * @return	True értékkel tér vissza, ha a játék folytatható. False érték esetén a játék
	 			befejezõdött
	 */
	boolean Step() {
		Canvas mazeCanvas = null;

		In ("Step", "");

		if (--time <= 0) {
			Println ("[!] out of time");
			Leave ("Step", "out of time");
			return false;
		}		

		Println ("[i] time: " + time);
		Println ("[i] monsters: " + monsters);
		Println ("[i] bombs: " + bombs);
		Println ("[i] bonuses: " + bonuses);

		Println ("making active items Act()..");
		
		// call Act() methods
		Println ("--- act: pacman");
		pacman.Act();

		// create a bomb if pacman has put one down
		if (pacman.GetBombPutState()) {
			if (Ask("is there a bomb already?","yes|no").equalsIgnoreCase("yes")) {
				Println ("so f@ck off..");
			} else {
				bombs.add(new Bomb(this, pacman.GetCoordinates(), 3));
			}
			pacman.SetBombPutState(false);
		}

		int i;
		
		Println ("--- act: monsters");
		for (i = 0; i < monsters.size(); i++)
			((Monster)(monsters.get(i))).Act();

		Println ("--- act: bonuses");
		for (i = 0; i < bonuses.size(); i++)
			if (!((Bonus)(bonuses.get(i))).Act())
				bonuses.remove(i);

		Println ("--- act: bombs");
		for (i = 0; i < bombs.size(); i++)
			if (!((Bomb)(bombs.get(i))).Act())
				bombs.remove(i);

		Println ("--- act: crystals");
		for (i = 0; i < crystals.size(); i++)
			if (!((Crystal)(crystals.get(i))).Act())
				crystals.remove(i);

		// Ütközések ellenõrzése
		if (!CheckCollisions()) {
			// PacMan died
			Leave ("Step", "" + false + " - pacman died.");
			return false;
		}

		// Bónuszok véletlenszerû megjelenítése
		PutBonuses();

		boolean cont = true;
		if (Ask("one more turn?", "yes|no").equalsIgnoreCase("no"))
			cont = false;
		
		Leave ("Step", "" + cont);
		return cont;
	}

	/**
	 * Ütközések ellenõrzése. A PacMan-t minden pályaelemmel, a szörnyeket pedig a
	 * bombákkal kapcsolatban vizsgálja.
	 *
	 * @return	True értékkel tér vissza, ha a PacMan életben maradt (nem ütközött
	 			szörnnyel ill. élesített bombával) - a játék folytatható.
	 */
	boolean CheckCollisions() {
		In ("CheckCollisions", "");

		int i;

		for (i = 0; i < monsters.size(); i++)
			if (((Monster)monsters.get(i)).isActive() &&
				CheckCollision(pacman, ((MazeItem)monsters.get(i)))) {
				Println ("XXX " + pacman + " dies.");
				Leave ("CheckCollisions", "" + false);
				return false;
			}

		for (i = 0; i < bombs.size(); i++)
			if (((Bomb)bombs.get(i)).isActive() &&
				CheckCollision(pacman, ((MazeItem)bombs.get(i)))) {
				Println ("XXX " + pacman + " dies.");
				Leave ("CheckCollisions", "" + false);
				return false;
			}

		for (i = 0; i < bonuses.size(); i++)
			if (CheckCollision(pacman, ((MazeItem)bonuses.get(i)))) {
				Println ("XXX " + pacman + " picks up bonus " + bonuses.get(i) + ".");
				((Bonus)bonuses.get(i)).PickUp();
				bonuses.remove(i);
			}

		for (i = 0; i < monsters.size(); i++)
			for (int j = 0; j < bombs.size(); j++)
				if (((Monster)monsters.get(i)).isActive() &&
					((Bomb)bombs.get(j)).isActive() &&
					CheckCollision((MazeItem)monsters.get(i), (MazeItem)bombs.get(j))) {
					Println ("XXX " + monsters.get(i) + " dies, when hitting " + bombs.get(j) + ".");
					bombs.remove(j);
					j--;
					((Monster)monsters.get(i)).Deactivate(5);
				}

		Leave("CheckCollisions", "" + true);
		return true;
	}
	
	/**
	 * Egy ütközés tesztelése. A és B pályaelem ütközik, ha a köztük lévõ távolság kisebb,
	 * mint sugaraik összege.
	 *
	 * @param a		A master pályaelem.
	 * @param b		A slave pályaelem.
	 * @return		True értékkel tér vissza, ha a két pályaelem ütközik.
	 */
	boolean CheckCollision(MazeItem a, MazeItem b) {
		In ("CheckCollision", "" + a + " - " + b);

		boolean Coll = false;

		if (Ask("does " + a + " collide with " + b + "?", "no|yes").equalsIgnoreCase("yes")) {
			Println ("XXX " + a + " collides with " + b + ".");
			Coll = true;
		}

		Leave ("CheckCollision", "" + Coll);
		return Coll;
	}

	/**
	 * Véletlenszerûen megjelenõ bónuszok elhejezése a pályán.
	 */
	void PutBonuses() {
		In ("PutBonuses", "");

		String ans = Ask ("what bonus do you want?", "none|bomb|time|score");
		long xc = 0;
		long yc = 0;
		;
		if (!ans.equals("none")) {
			xc = Long.parseLong ( Ask ("x coordinate?"));
			yc = Long.parseLong ( Ask ("y coordinate?"));
		}

		if (ans.equalsIgnoreCase("bomb")) {
			int bombs = Integer.parseInt (Ask ("how many bombs does the bonus hold?", "1|2|3|4"));
			bonuses.add(new BombBonus(this, new Coordinates (xc, yc), 5, bombs));
		} else
		if (ans.equalsIgnoreCase("score")) {
			long score = Long.parseLong (Ask ("how many points does the bonus hold?", "10|20|50|100"));
			bonuses.add(new ScoreBonus(this, new Coordinates (xc, yc), 5, score));
		} else
		if (ans.equalsIgnoreCase("time")) {
			long time = Long.parseLong (Ask ("how many ticks does the bonus give?", "1|2|5|10"));
			bonuses.add(new TimeBonus(this, new Coordinates (xc, yc), 5, time));
		}

		Leave("PutBonuses", "");
	}

	/**
	 * Pontszám növelése. Callback a {@link ScoreBonus#PickUp} számára.
	 *
	 * @param value	Szerzett pontok száma
	 * @see	ScoreBonus
	 * @see Maze#IncreaseScore(long)
	 */
	void IncreaseScore(long value) {
		In ("IncreaseScore", "" + value);
		parentGame.IncreaseScore (value);
		Leave ("IncreaseScore", "");
	}

	/**
	 * Pontszám növelése. Callback a {@link BombBonus#PickUp} számára.
	 *
	 * @param value	Felvett bombák száma
	 * @see	BombBonus
	 * @see PacMan#IncreaseBombs
	 */
	void IncreaseBombs(int value) {
		In ("IncreaseBombs", "" + value);
		pacman.IncreaseBombs (value);
		Leave ("IncreaseBombs", "");
	}

	/**
	 * Hátralevõ idõ növelése. Callback a {@link TimeBonus#PickUp} számára.
	 *
	 * @param value	Megszerzett plussz idõ hossza.
	 * @see	TimeBonus
	 */
	void IncreaseTime(long value) {
		In ("IncreaseTime", "" + value);
		time += value;
		Println ("[i] time increased to " + time + ".");
		Leave ("IncreaseTime", "" + time);
	}

	/**
	 * Visszaadja a szörnyek újjászületési helyét.
	 *
	 * @return A szörnyek újjászületési helye.
	 */
	public Coordinates GetRebornPlace() {
		In("GetRebornPlace()", "");
		Leave("GetRebornPlace()", "" + rebornPlace);
		return rebornPlace;
	}
}