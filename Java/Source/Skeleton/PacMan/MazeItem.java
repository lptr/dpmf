package PacMan;

import java.lang.*;
import java.io.*;
import java.util.*;
import java.awt.*;

// BASE CLASSES

/**
 * Az összes pályaelem alaposztálya. Tartalmazza a pályaelemek viselkedését megvalósító
 * alapvetõ metódusokat ({@link #Act()}, {@link #GetCoordinates()}, {@link #GetR()}).
 */
class MazeItem extends SkeletonObject {

	/**
	 * A pályaelemet tartalmazó Maze objektum
	 */
	Maze currentMaze;

	/**
	 * A pályaelem pozíciója.
	 */
	Coordinates pos;

	/**
	 * A pályaelem sugara (mérete).
	 */
	int radius;

	/**
	 * Hozzárendeli a pályaelemet a pályához, és elhelyezi a pályán.
	 *
	 * @param _currentMaze	A pályaelemhez rendelt Maze objektum
	 * @param _pos			A pályaelem pozíciója
	 */
	MazeItem(Maze _currentMaze, Coordinates _pos) {
		currentMaze = _currentMaze;
		pos = _pos;
		Where();
	}
	
	/**
	 * Lehetõséget ad a pályaelemnek, hogy az életciklusát folytassa.
	 *
	 * @return	False értékkel tér vissza, ha a pályaelem elpusztult (pl. lejárt a bónusz 
				{@link TimedMazeItem#timeToLive timeToLive}-je)
	 */
	public boolean Act() {
		In ("Act", "");
		
		// Act!
		Where();

		Leave ("Act", "" + true);		
		return true;
	}	
	
	/**
	 * Visszaadja a pályaelem pozícióját.
	 *
	 * @return A pályaelem pozíciója.
	 */
	public Coordinates GetCoordinates() {
		In ("GetCoordinates", "");
		Leave ("GetCoordinates", "" + pos);
		return pos;
	}

	/**
	 * Visszaadja a pályaelem sugarát (méretét).
	 *
	 * @return A pályaelem sugara.
	 */
	public int GetR() {
		In ("GetR", "");
		Leave ("GetR", "" + radius);
		return radius;
	}

	// Skeleton model method - says where the item is located
	/**
	 * Kiírja a pályaelem pozícióját. Csak a skeletonban szerepel.
	 */
	void Where() {
		Println ("<*> " + this + " is @ " + pos + " - r: " + radius);
	} 
}

/**
 * A pályán mozgó pályaelemek alaposztálya. A MazeItem osztályt a {@link #direction}
 * és a {@link #speed} attribútumokkal bõvíti ki.
 */
class ActiveMazeItem extends MazeItem {

	/**
	 * A pályaelem irányát adja meg. (Erre néz, erre halad.)
	 */
	int direction;

	/**
	 * A pályaelem sebessége.
	 */
	double speed;
	
	/**
	 * Hozzárendeli a pályaelemet a pályához, és elhelyezi a pályán.
	 *
	 * @param _currentMaze	A pályaelemhez rendelendõ Maze objektum
	 * @param _pos			A pályaelem pozíciója
	 */
	ActiveMazeItem(Maze _currentMaze, Coordinates _pos) {
		super (_currentMaze, _pos);
	}
}

/**
 * Idõszakos pályaelemek alaposztálya. A MazeItem osztályt a {@link #timeToLive}
 * attribútummal bõvíti ki.
 */
class TimedMazeItem extends MazeItem {

	/**
	 * A pályaelem élettartama. Ennyi "tick" után szûnik meg létezni.
	 */
	private long timeToLive;
	
	/**
	 * Hozzárendeli a pályaelemet a pályához, elhelyezi a pályán és beállítja az élettartamát.
	 *
	 * @param _currentMaze	A pályaelemhez rendelendõ Maze objektum.
	 * @param _pos			A pályaelem pozíciója.
	 * @param _timeToLive	A pályaelem élettartama.
	 */
	TimedMazeItem (Maze _currentMaze, Coordinates _pos, long _timeToLive) {
		super (_currentMaze, _pos);
		timeToLive = _timeToLive;
		Println (this + " has " + timeToLive + " to live.");
	}
	
	/**
	 * Visszaadja a {@link #timeToLive} értékét.
	 */
	public long GetTimeToLive() {
		In ("GetTimeToLive", "");

		Leave ("GetTimeToLive", "" + timeToLive);

		return timeToLive;
	}
	
	/**
	 * Lehetõséget ad a pályaelemnek, hogy az életciklusát folytassa.
	 *
	 * @return	False értékkel tér vissza, ha a pályaelem elpusztult (lejárt a {@link #timeToLive}-je).
	 */
	public boolean Act() {
		In("Act", "");

		super.Act();
		
		timeToLive--;
		Println ("time left to live is " + timeToLive);

		Leave("Act", "" + (timeToLive > 0));
		
		return timeToLive > 0;
	}
}

/**
 * A játékos által vezérelt PacMan-t megvalósító osztály. Képes {@link Bomb bombát} lerakni,
 * {@link Bonus bónuszokat} és {@link Crystal kristályokat} felvenni.
 */
class PacMan extends ActiveMazeItem {
	/**
	 * Rakott-e le bombát az aktuális mezõre a PacMan.
	 */
	private boolean putbomb = false;

	/**
	 * A PacMan-nél levõ bombák száma.
	 */
	private int bombs = 0;

	/**
	 * Hozzárendeli a PacMan-t a pályához, és elhelyezi a pályán.
	 *
	 * @param _currentMaze	A PacMan-hez rendelendõ Maze objektum.
	 * @param _pos			A PacMan pozíciója.
	 */
	PacMan(Maze _currentMaze, Coordinates _pos) {
		super (_currentMaze, _pos);
		radius = 8;
	}

	/**
	 * A {@link #putbomb} változó beállítása (bomba lerakása).
	 *
	 * @param state		A beállítandó állapot
	 */
	public void SetBombPutState(boolean state) {
		In ("SetBombPutState", "");

		putbomb = state;
		Println ("putbomb state set to " + putbomb + ".");
		
		Leave ("SetBombPutState", "");
	}

	/**
	 * A {@link #putbomb} változó lekérdezése (rak-e le bombát ide a PacMan).
	 *
	 * @return	A {@link #putbomb} értéke.
	 */
	public boolean GetBombPutState() {
		In ("GetBombPutState", "");

		Leave ("GetBombPutState", "" + putbomb);
		return putbomb;
	}
	
	/**
	 * Lehetõséget ad a PacMannek, hogy az életciklusát folytassa.
	 *
	 * @return	False értékkel tér vissza, ha elpusztult.
	 */
	public boolean Act() {
		In ("Act", "");

		super.Act();

		String ans = Ask ("what to do?", "none|bomb");

		if (ans.equalsIgnoreCase("bomb")) {
			SetBombPutState(true);
		}
		
		Leave("Act", "" + true);
		return true;
	}

	/**
	 * A bombák számának növelése.
	 *
	 * @param value		Mennyivel növeljük a bombák számát.
	 */
	void IncreaseBombs(int value) {
		In ("IncreaseBombs", "" + value);
		bombs += value;
		Println ("[i] bombs increased to " + bombs + ".");
		Leave ("IncreaseBombs", "" + bombs);
	}
}

/**
 * A játékban szereplõ szörnyek alaposztálya. A MazeItem osztályt az {@link #active}
 * attribútummal bõvíti ki.
 */
class Monster extends ActiveMazeItem {

	/**
	 * Aktív-e a szörny. Ha a szörny elpusztult, nem törlõdik, csak inaktívvá válik.
	 * Egy idõ után ({@link #timeToRebirth})újra aktív lesz.
	 */
	private boolean active;

	/**
	 * Ha a szörny inaktív, ennyi "tick" után születik újjá.
	 */
	private long timeToRebirth;
	
	/**
	 * Hozzárendeli a szörnyet a pályához, és elhelyezi a pályán.
	 *
	 * @param _currentMaze	A szörnyhöz rendelendõ Maze objektum.
	 * @param _pos			A szörny pozíciója.
	 */
	public Monster(Maze _currentMaze, Coordinates _pos) {
		super (_currentMaze, _pos);
		radius = 8;
		active = true;
		timeToRebirth = 0;
	}
	
	/**
	 * Aktiválja a szörnyet.
	 */
	public void Activate() {
		In ("Activate", "");
		active = true;
		Leave ("Activate", "");
	}

	/**
	 * Deaktiválja a szörnyet. Beállítja az újjászületésig hátralevõ idõt.
	 *
	 * @param _timeToRebirth	Az újjászületésig hátralevõ idõ
	 */
	public void Deactivate(long _timeToRebirth) {
		In ("Deactivate", "");
		active = false;
		timeToRebirth = _timeToRebirth;
		Leave ("Deactivate", "");
	}

	/**
	 * Újjászüli a szörnyet az újjászületõ-helyen.
	 */
	private void Reborn() {
		In ("Reborn", "");

		Activate();

		pos = currentMaze.GetRebornPlace();
		Println (this + " reborns @ " + pos + ".");

		Leave ("Reborn", "");
	}

	/**
	 * Visszaadja, hogy aktív-e a szörny.
	 *
	 * @return	Aktív-e a szörny.
	 */
	public boolean isActive() {
		In ("isActive", "");
		Leave ("isActive", "" + active);

		return active;
	}

	/**
	 * Lehetõséget ad a pályaelemnek, hogy az életciklusát folytassa: Ha inaktív,
	 * csökkenti a {@link #timeToRebirth} értékét. Ha az elért 0-ra, a szörny újjászületik.
	 *
	 * @return	False értékkel tér vissza, ha a pályaelem elpusztult..
	 */
	public boolean Act() {
		In ("Act", "");

		boolean ret = super.Act();

		if (ret) {
			if (!active) {
				if (--timeToRebirth <= 0) {
					Reborn();
				}
			}

			Println (this + " is " + (active?"active":("inactive for " + timeToRebirth)) + ".");
		}

		Leave ("Act", "" + ret);
		return ret;
	}
}

/**
 * A PacMan által a pályán elhelyezhetõ bombát megvalósító osztály.
 */
class Bomb extends TimedMazeItem {

	/**
	 * Élesítve van-e már a bomba.
	 */
	private boolean active;
	
	/**
	 * Hozzárendeli a bombát a pályához, elhelyezi a pályán, és beállítja az élettartamát.
	 *
	 * @param _currentMaze	A bombához rendelendõ Maze objektum.
	 * @param _pos			A bomba pozíciója.
	 * @param _timeToLive	A bomba élettartama.
	 */
	Bomb(Maze _currentMaze, Coordinates _pos, long _timeToLive) {
		super (_currentMaze, _pos, _timeToLive);
		active = false;
	}
	
	/**
	 * Élesíti a bombát.
	 */
	void Activate() {
		In ("Activate", "");
		active = true;
		Leave ("Activate", "");
	}
	
	/**
	 * Élesített-e a bomba.
	 *
	 * @return A bomba állapota.
	 */
	boolean isActive() {
		In ("isActive", "");
		Leave ("isActive", "" + active);
		return active;
	}

	/**
	 * Lehetõséget ad a pályaelemnek, hogy az életciklusát folytassa.
	 *
	 * @return	False értékkel tér vissza, ha a pályaelem elpusztult (lejárt a {@link #timeToLive}-je).
	 */
	public boolean Act() {
		In ("Act", "");

		boolean ret = super.Act();

		if (ret) {
			Println (this + " is " + (active?"active":"inactive") + ".");

			if (!active &&
				Ask("does " + this + " activate?", "no|yes").equalsIgnoreCase("yes"))
				Activate();
		}

		Leave ("Act", "" + ret);
		return ret;
	}
}

// BONUSES

/**
 * A pályán néha bizonyos idõre megjelenõ bónuszok alaposztálya.
 */
class Bonus extends TimedMazeItem {

	/**
	 * Hozzárendeli a bonuszt a pályához, elhelyezi a pályán, és beállítja az élettartamát.
	 *
	 * @param _currentMaze	A bónuszhoz rendelendõ Maze objektum.
	 * @param _pos			A bónusz pozíciója.
	 * @param _timeToLive	A bónusz élettartama.
	 */
	Bonus(Maze _currentMaze, Coordinates _pos, long _timeToLive) {
		super (_currentMaze, _pos, _timeToLive);
		radius = 4;
	}

	/**
	 * A bónusz felvételekor bekövetkezõ esemény. Az egyes bónuszok felüldefiniálják ezt
	 * a metódust, és ezen keresztül "hatnak" (innen hívják meg a {@link Maze#IncreaseScore}, 
	 * {@link Maze#IncreaseTime}, {@link Maze#IncreaseBombs} metódusokat).
	 */
	void PickUp() {
		In ("PickUp", "");
		Leave("PickUp", "");
	}
}

/**
 * Bomba bónusz.
 */
class BombBonus extends Bonus {

	/**
	 * Hány bomba van a bónuszban.
	 */
	int bombsCount;

	/**
	 * Hozzárendeli a bonuszt a pályához, elhelyezi a pályán, és beállítja az élettartamát.
	 *
	 * @param _currentMaze	A bónuszhoz rendelendõ Maze objektum.
	 * @param _pos			A bónusz pozíciója.
	 * @param _bombsCount	Hány bomba van a bónuszban.
	 * @param _timeToLive	A bónusz élettartama.
	 */
	public BombBonus(Maze _currentMaze, Coordinates _pos, long _timeToLive, int _bombsCount) {
		super (_currentMaze, _pos, _timeToLive);
		radius = 5;
		bombsCount = _bombsCount;
		Println(this + " holds " + bombsCount + " bombs.");
	}

	/**
	 * A bónusz felvétele. A currentMaze-en keresztül meghívja a {@link Maze#IncreaseBombs} metódust.
	 */
	public void PickUp() {
		In ("PickUp", "" + bombsCount);

		currentMaze.IncreaseBombs(bombsCount);

		Leave("PickUp", "");
	}
}

/**
 * Pontszámbónusz.
 */
class ScoreBonus extends Bonus {

	/**
	 * Hány pont van a bónuszban.
	 */
	private long bonusScore;

	/**
	 * Hozzárendeli a bonuszt a pályához, elhelyezi a pályán, és beállítja az élettartamát.
	 *
	 * @param _currentMaze	A bónuszhoz rendelendõ Maze objektum.
	 * @param _pos			A bónusz pozíciója.
	 * @param _bonusScore	Hány pont van a bónuszban.
	 * @param _timeToLive	A bónusz élettartama.
	 */
	public ScoreBonus(Maze _currentMaze, Coordinates _pos, long _timeToLive, long _bonusScore) {
		super (_currentMaze, _pos, _timeToLive);
		radius = 5;
		bonusScore = _bonusScore;
		Println(this + " holds " + bonusScore + " points.");
	}

	/**
	 * A bónusz felvétele. A currentMaze-en keresztül meghívja a {@link Maze#IncreaseScore} metódust.
	 */
	void PickUp() {
		In ("PickUp", "" + bonusScore);

		currentMaze.IncreaseScore(bonusScore);

		Leave("PickUp", "");
	}
}

/**
 * Idõbónusz.
 */
class TimeBonus extends Bonus {

	/**
	 * Mennyi plussz idõ van a bónuszban.
	 */
	private long bonusTime;

	/**
	 * Hozzárendeli a bonuszt a pályához, elhelyezi a pályán, és beállítja az élettartamát.
	 *
	 * @param _currentMaze	A bónuszhoz rendelendõ Maze objektum.
	 * @param _pos			A bónusz pozíciója.
	 * @param _bonusTime	Mennyi plussz idõ van a bónuszban.
	 * @param _timeToLive	A bónusz élettartama.
	 */
	public TimeBonus(Maze _currentMaze, Coordinates _pos, long _timeToLive, long _bonusTime) {
		super (_currentMaze, _pos, _timeToLive);
		radius = 5;
		bonusTime = _bonusTime;
		Println(this + " holds " + bonusTime + " ticks.");
	}

	/**
	 * A bónusz felvétele. A currentMaze-en keresztül meghívja a {@link Maze#IncreaseTime} metódust.
	 */
	void PickUp() {
		In ("PickUp", "" + bonusTime);

		currentMaze.IncreaseTime(bonusTime);

		Leave("PickUp", "");
	}
}

// CRYSTAL

/**
 * A pályán elhelyezkedõ kristályok osztálya. Ezeknek a kristályoknak az összegyûjtése a pálya
 * teljesítésének feltétele.
 */
class Crystal extends MazeItem {

	/**
	 * Hozzárendeli a kristályt a pályához, és elhelyezi a pályán.
	 *
	 * @param _currentMaze	A kristályhoz rendelendõ Maze objektum.
	 * @param _pos			A kristály pozíciója.
	 */
	Crystal(Maze _currentMaze, Coordinates _pos) {
		super (_currentMaze, _pos);
		radius = 3;
	}
}