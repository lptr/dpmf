package PacMan;

import java.lang.*;
import java.io.*;
import java.util.*;
import java.awt.*;

/**
 * A j�t�kt�r labirintus r�sz�t megval�s�t� objektum. � kezeli a p�ly�n mozg� vagy �ll�
 * p�lyaelemeket - t�rben �s id�ben. Az � feladata az id� m�l�s�nak kezel�se is.<br>
 * A {@link Game} oszt�ly hozza l�tre.
 *
 * @author L�ci
 */
class Maze extends SkeletonObject {

	/**
	 * A j�t�kos �ltal ir�ny�tott PacMan objektum
	 */
	PacMan pacman;

	/**
	 * A p�ly�n tal�lhat� sz�rnyek list�ja
	 */
	LinkedList monsters = new LinkedList();

	/**
	 * A p�ly�n tal�lhat� b�nuszok list�ja
	 */
	LinkedList bonuses = new LinkedList();

	/**
	 * A p�ly�n tal�lhat� bomb�k list�ja
	 */
	LinkedList bombs = new LinkedList();

	/**
	 * A m�g �ssze nem szedett krist�lyok list�ja
	 */
	LinkedList crystals = new LinkedList();

	/**
	 * A sz�rnyek ezen a helyen sz�letnek �jj�.
	 */
	Coordinates rebornPlace;

	/**
	 * A labirintust fel�gyel� {@link Game} objektum
	 */
	Game parentGame;
	int time = 0;

	/**
	 * L�trehozza a p�ly�t.
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
	 * Bet�lti a levelNum �ltal megjel�lt p�ly�t. Fel�p�ti a mez�ket tartalmaz� t�mb�t,
	 * l�trehozza a megfelel� sz�m� sz�rnyet, a PacMan-t, a krist�lyokat.
	 *
	 * @throws IOException Ha nem tudta megnyitni a p�ly�t tartalmaz� file-t.
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
	 * V�grehajt egy szimul�ci�s l�p�st. Cs�kkenti a h�tralev� id�t: V�gigh�vja a p�lyaelemek
	 * {@link MazeItem#Act()} met�dusait. Ellen�rzi a p�lyaelemek �tk�z�s�t.
	 * Lekezeli a b�nuszok felv�tel�t, a PacMan hal�l�t, az id� lej�rt�t, a p�lya teljes�t�s�t.
	 *
	 * @return	True �rt�kkel t�r vissza, ha a j�t�k folytathat�. False �rt�k eset�n a j�t�k
	 			befejez�d�tt
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

		// �tk�z�sek ellen�rz�se
		if (!CheckCollisions()) {
			// PacMan died
			Leave ("Step", "" + false + " - pacman died.");
			return false;
		}

		// B�nuszok v�letlenszer� megjelen�t�se
		PutBonuses();

		boolean cont = true;
		if (Ask("one more turn?", "yes|no").equalsIgnoreCase("no"))
			cont = false;
		
		Leave ("Step", "" + cont);
		return cont;
	}

	/**
	 * �tk�z�sek ellen�rz�se. A PacMan-t minden p�lyaelemmel, a sz�rnyeket pedig a
	 * bomb�kkal kapcsolatban vizsg�lja.
	 *
	 * @return	True �rt�kkel t�r vissza, ha a PacMan �letben maradt (nem �tk�z�tt
	 			sz�rnnyel ill. �les�tett bomb�val) - a j�t�k folytathat�.
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
	 * Egy �tk�z�s tesztel�se. A �s B p�lyaelem �tk�zik, ha a k�zt�k l�v� t�vols�g kisebb,
	 * mint sugaraik �sszege.
	 *
	 * @param a		A master p�lyaelem.
	 * @param b		A slave p�lyaelem.
	 * @return		True �rt�kkel t�r vissza, ha a k�t p�lyaelem �tk�zik.
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
	 * V�letlenszer�en megjelen� b�nuszok elhejez�se a p�ly�n.
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
	 * Pontsz�m n�vel�se. Callback a {@link ScoreBonus#PickUp} sz�m�ra.
	 *
	 * @param value	Szerzett pontok sz�ma
	 * @see	ScoreBonus
	 * @see Maze#IncreaseScore(long)
	 */
	void IncreaseScore(long value) {
		In ("IncreaseScore", "" + value);
		parentGame.IncreaseScore (value);
		Leave ("IncreaseScore", "");
	}

	/**
	 * Pontsz�m n�vel�se. Callback a {@link BombBonus#PickUp} sz�m�ra.
	 *
	 * @param value	Felvett bomb�k sz�ma
	 * @see	BombBonus
	 * @see PacMan#IncreaseBombs
	 */
	void IncreaseBombs(int value) {
		In ("IncreaseBombs", "" + value);
		pacman.IncreaseBombs (value);
		Leave ("IncreaseBombs", "");
	}

	/**
	 * H�tralev� id� n�vel�se. Callback a {@link TimeBonus#PickUp} sz�m�ra.
	 *
	 * @param value	Megszerzett plussz id� hossza.
	 * @see	TimeBonus
	 */
	void IncreaseTime(long value) {
		In ("IncreaseTime", "" + value);
		time += value;
		Println ("[i] time increased to " + time + ".");
		Leave ("IncreaseTime", "" + time);
	}

	/**
	 * Visszaadja a sz�rnyek �jj�sz�let�si hely�t.
	 *
	 * @return A sz�rnyek �jj�sz�let�si helye.
	 */
	public Coordinates GetRebornPlace() {
		In("GetRebornPlace()", "");
		Leave("GetRebornPlace()", "" + rebornPlace);
		return rebornPlace;
	}
}