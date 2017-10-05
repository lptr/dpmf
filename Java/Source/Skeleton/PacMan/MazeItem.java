package PacMan;

import java.lang.*;
import java.io.*;
import java.util.*;
import java.awt.*;

// BASE CLASSES

/**
 * Az �sszes p�lyaelem alaposzt�lya. Tartalmazza a p�lyaelemek viselked�s�t megval�s�t�
 * alapvet� met�dusokat ({@link #Act()}, {@link #GetCoordinates()}, {@link #GetR()}).
 */
class MazeItem extends SkeletonObject {

	/**
	 * A p�lyaelemet tartalmaz� Maze objektum
	 */
	Maze currentMaze;

	/**
	 * A p�lyaelem poz�ci�ja.
	 */
	Coordinates pos;

	/**
	 * A p�lyaelem sugara (m�rete).
	 */
	int radius;

	/**
	 * Hozz�rendeli a p�lyaelemet a p�ly�hoz, �s elhelyezi a p�ly�n.
	 *
	 * @param _currentMaze	A p�lyaelemhez rendelt Maze objektum
	 * @param _pos			A p�lyaelem poz�ci�ja
	 */
	MazeItem(Maze _currentMaze, Coordinates _pos) {
		currentMaze = _currentMaze;
		pos = _pos;
		Where();
	}
	
	/**
	 * Lehet�s�get ad a p�lyaelemnek, hogy az �letciklus�t folytassa.
	 *
	 * @return	False �rt�kkel t�r vissza, ha a p�lyaelem elpusztult (pl. lej�rt a b�nusz 
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
	 * Visszaadja a p�lyaelem poz�ci�j�t.
	 *
	 * @return A p�lyaelem poz�ci�ja.
	 */
	public Coordinates GetCoordinates() {
		In ("GetCoordinates", "");
		Leave ("GetCoordinates", "" + pos);
		return pos;
	}

	/**
	 * Visszaadja a p�lyaelem sugar�t (m�ret�t).
	 *
	 * @return A p�lyaelem sugara.
	 */
	public int GetR() {
		In ("GetR", "");
		Leave ("GetR", "" + radius);
		return radius;
	}

	// Skeleton model method - says where the item is located
	/**
	 * Ki�rja a p�lyaelem poz�ci�j�t. Csak a skeletonban szerepel.
	 */
	void Where() {
		Println ("<*> " + this + " is @ " + pos + " - r: " + radius);
	} 
}

/**
 * A p�ly�n mozg� p�lyaelemek alaposzt�lya. A MazeItem oszt�lyt a {@link #direction}
 * �s a {@link #speed} attrib�tumokkal b�v�ti ki.
 */
class ActiveMazeItem extends MazeItem {

	/**
	 * A p�lyaelem ir�ny�t adja meg. (Erre n�z, erre halad.)
	 */
	int direction;

	/**
	 * A p�lyaelem sebess�ge.
	 */
	double speed;
	
	/**
	 * Hozz�rendeli a p�lyaelemet a p�ly�hoz, �s elhelyezi a p�ly�n.
	 *
	 * @param _currentMaze	A p�lyaelemhez rendelend� Maze objektum
	 * @param _pos			A p�lyaelem poz�ci�ja
	 */
	ActiveMazeItem(Maze _currentMaze, Coordinates _pos) {
		super (_currentMaze, _pos);
	}
}

/**
 * Id�szakos p�lyaelemek alaposzt�lya. A MazeItem oszt�lyt a {@link #timeToLive}
 * attrib�tummal b�v�ti ki.
 */
class TimedMazeItem extends MazeItem {

	/**
	 * A p�lyaelem �lettartama. Ennyi "tick" ut�n sz�nik meg l�tezni.
	 */
	private long timeToLive;
	
	/**
	 * Hozz�rendeli a p�lyaelemet a p�ly�hoz, elhelyezi a p�ly�n �s be�ll�tja az �lettartam�t.
	 *
	 * @param _currentMaze	A p�lyaelemhez rendelend� Maze objektum.
	 * @param _pos			A p�lyaelem poz�ci�ja.
	 * @param _timeToLive	A p�lyaelem �lettartama.
	 */
	TimedMazeItem (Maze _currentMaze, Coordinates _pos, long _timeToLive) {
		super (_currentMaze, _pos);
		timeToLive = _timeToLive;
		Println (this + " has " + timeToLive + " to live.");
	}
	
	/**
	 * Visszaadja a {@link #timeToLive} �rt�k�t.
	 */
	public long GetTimeToLive() {
		In ("GetTimeToLive", "");

		Leave ("GetTimeToLive", "" + timeToLive);

		return timeToLive;
	}
	
	/**
	 * Lehet�s�get ad a p�lyaelemnek, hogy az �letciklus�t folytassa.
	 *
	 * @return	False �rt�kkel t�r vissza, ha a p�lyaelem elpusztult (lej�rt a {@link #timeToLive}-je).
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
 * A j�t�kos �ltal vez�relt PacMan-t megval�s�t� oszt�ly. K�pes {@link Bomb bomb�t} lerakni,
 * {@link Bonus b�nuszokat} �s {@link Crystal krist�lyokat} felvenni.
 */
class PacMan extends ActiveMazeItem {
	/**
	 * Rakott-e le bomb�t az aktu�lis mez�re a PacMan.
	 */
	private boolean putbomb = false;

	/**
	 * A PacMan-n�l lev� bomb�k sz�ma.
	 */
	private int bombs = 0;

	/**
	 * Hozz�rendeli a PacMan-t a p�ly�hoz, �s elhelyezi a p�ly�n.
	 *
	 * @param _currentMaze	A PacMan-hez rendelend� Maze objektum.
	 * @param _pos			A PacMan poz�ci�ja.
	 */
	PacMan(Maze _currentMaze, Coordinates _pos) {
		super (_currentMaze, _pos);
		radius = 8;
	}

	/**
	 * A {@link #putbomb} v�ltoz� be�ll�t�sa (bomba lerak�sa).
	 *
	 * @param state		A be�ll�tand� �llapot
	 */
	public void SetBombPutState(boolean state) {
		In ("SetBombPutState", "");

		putbomb = state;
		Println ("putbomb state set to " + putbomb + ".");
		
		Leave ("SetBombPutState", "");
	}

	/**
	 * A {@link #putbomb} v�ltoz� lek�rdez�se (rak-e le bomb�t ide a PacMan).
	 *
	 * @return	A {@link #putbomb} �rt�ke.
	 */
	public boolean GetBombPutState() {
		In ("GetBombPutState", "");

		Leave ("GetBombPutState", "" + putbomb);
		return putbomb;
	}
	
	/**
	 * Lehet�s�get ad a PacMannek, hogy az �letciklus�t folytassa.
	 *
	 * @return	False �rt�kkel t�r vissza, ha elpusztult.
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
	 * A bomb�k sz�m�nak n�vel�se.
	 *
	 * @param value		Mennyivel n�velj�k a bomb�k sz�m�t.
	 */
	void IncreaseBombs(int value) {
		In ("IncreaseBombs", "" + value);
		bombs += value;
		Println ("[i] bombs increased to " + bombs + ".");
		Leave ("IncreaseBombs", "" + bombs);
	}
}

/**
 * A j�t�kban szerepl� sz�rnyek alaposzt�lya. A MazeItem oszt�lyt az {@link #active}
 * attrib�tummal b�v�ti ki.
 */
class Monster extends ActiveMazeItem {

	/**
	 * Akt�v-e a sz�rny. Ha a sz�rny elpusztult, nem t�rl�dik, csak inakt�vv� v�lik.
	 * Egy id� ut�n ({@link #timeToRebirth})�jra akt�v lesz.
	 */
	private boolean active;

	/**
	 * Ha a sz�rny inakt�v, ennyi "tick" ut�n sz�letik �jj�.
	 */
	private long timeToRebirth;
	
	/**
	 * Hozz�rendeli a sz�rnyet a p�ly�hoz, �s elhelyezi a p�ly�n.
	 *
	 * @param _currentMaze	A sz�rnyh�z rendelend� Maze objektum.
	 * @param _pos			A sz�rny poz�ci�ja.
	 */
	public Monster(Maze _currentMaze, Coordinates _pos) {
		super (_currentMaze, _pos);
		radius = 8;
		active = true;
		timeToRebirth = 0;
	}
	
	/**
	 * Aktiv�lja a sz�rnyet.
	 */
	public void Activate() {
		In ("Activate", "");
		active = true;
		Leave ("Activate", "");
	}

	/**
	 * Deaktiv�lja a sz�rnyet. Be�ll�tja az �jj�sz�let�sig h�tralev� id�t.
	 *
	 * @param _timeToRebirth	Az �jj�sz�let�sig h�tralev� id�
	 */
	public void Deactivate(long _timeToRebirth) {
		In ("Deactivate", "");
		active = false;
		timeToRebirth = _timeToRebirth;
		Leave ("Deactivate", "");
	}

	/**
	 * �jj�sz�li a sz�rnyet az �jj�sz�let�-helyen.
	 */
	private void Reborn() {
		In ("Reborn", "");

		Activate();

		pos = currentMaze.GetRebornPlace();
		Println (this + " reborns @ " + pos + ".");

		Leave ("Reborn", "");
	}

	/**
	 * Visszaadja, hogy akt�v-e a sz�rny.
	 *
	 * @return	Akt�v-e a sz�rny.
	 */
	public boolean isActive() {
		In ("isActive", "");
		Leave ("isActive", "" + active);

		return active;
	}

	/**
	 * Lehet�s�get ad a p�lyaelemnek, hogy az �letciklus�t folytassa: Ha inakt�v,
	 * cs�kkenti a {@link #timeToRebirth} �rt�k�t. Ha az el�rt 0-ra, a sz�rny �jj�sz�letik.
	 *
	 * @return	False �rt�kkel t�r vissza, ha a p�lyaelem elpusztult..
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
 * A PacMan �ltal a p�ly�n elhelyezhet� bomb�t megval�s�t� oszt�ly.
 */
class Bomb extends TimedMazeItem {

	/**
	 * �les�tve van-e m�r a bomba.
	 */
	private boolean active;
	
	/**
	 * Hozz�rendeli a bomb�t a p�ly�hoz, elhelyezi a p�ly�n, �s be�ll�tja az �lettartam�t.
	 *
	 * @param _currentMaze	A bomb�hoz rendelend� Maze objektum.
	 * @param _pos			A bomba poz�ci�ja.
	 * @param _timeToLive	A bomba �lettartama.
	 */
	Bomb(Maze _currentMaze, Coordinates _pos, long _timeToLive) {
		super (_currentMaze, _pos, _timeToLive);
		active = false;
	}
	
	/**
	 * �les�ti a bomb�t.
	 */
	void Activate() {
		In ("Activate", "");
		active = true;
		Leave ("Activate", "");
	}
	
	/**
	 * �les�tett-e a bomba.
	 *
	 * @return A bomba �llapota.
	 */
	boolean isActive() {
		In ("isActive", "");
		Leave ("isActive", "" + active);
		return active;
	}

	/**
	 * Lehet�s�get ad a p�lyaelemnek, hogy az �letciklus�t folytassa.
	 *
	 * @return	False �rt�kkel t�r vissza, ha a p�lyaelem elpusztult (lej�rt a {@link #timeToLive}-je).
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
 * A p�ly�n n�ha bizonyos id�re megjelen� b�nuszok alaposzt�lya.
 */
class Bonus extends TimedMazeItem {

	/**
	 * Hozz�rendeli a bonuszt a p�ly�hoz, elhelyezi a p�ly�n, �s be�ll�tja az �lettartam�t.
	 *
	 * @param _currentMaze	A b�nuszhoz rendelend� Maze objektum.
	 * @param _pos			A b�nusz poz�ci�ja.
	 * @param _timeToLive	A b�nusz �lettartama.
	 */
	Bonus(Maze _currentMaze, Coordinates _pos, long _timeToLive) {
		super (_currentMaze, _pos, _timeToLive);
		radius = 4;
	}

	/**
	 * A b�nusz felv�telekor bek�vetkez� esem�ny. Az egyes b�nuszok fel�ldefini�lj�k ezt
	 * a met�dust, �s ezen kereszt�l "hatnak" (innen h�vj�k meg a {@link Maze#IncreaseScore}, 
	 * {@link Maze#IncreaseTime}, {@link Maze#IncreaseBombs} met�dusokat).
	 */
	void PickUp() {
		In ("PickUp", "");
		Leave("PickUp", "");
	}
}

/**
 * Bomba b�nusz.
 */
class BombBonus extends Bonus {

	/**
	 * H�ny bomba van a b�nuszban.
	 */
	int bombsCount;

	/**
	 * Hozz�rendeli a bonuszt a p�ly�hoz, elhelyezi a p�ly�n, �s be�ll�tja az �lettartam�t.
	 *
	 * @param _currentMaze	A b�nuszhoz rendelend� Maze objektum.
	 * @param _pos			A b�nusz poz�ci�ja.
	 * @param _bombsCount	H�ny bomba van a b�nuszban.
	 * @param _timeToLive	A b�nusz �lettartama.
	 */
	public BombBonus(Maze _currentMaze, Coordinates _pos, long _timeToLive, int _bombsCount) {
		super (_currentMaze, _pos, _timeToLive);
		radius = 5;
		bombsCount = _bombsCount;
		Println(this + " holds " + bombsCount + " bombs.");
	}

	/**
	 * A b�nusz felv�tele. A currentMaze-en kereszt�l megh�vja a {@link Maze#IncreaseBombs} met�dust.
	 */
	public void PickUp() {
		In ("PickUp", "" + bombsCount);

		currentMaze.IncreaseBombs(bombsCount);

		Leave("PickUp", "");
	}
}

/**
 * Pontsz�mb�nusz.
 */
class ScoreBonus extends Bonus {

	/**
	 * H�ny pont van a b�nuszban.
	 */
	private long bonusScore;

	/**
	 * Hozz�rendeli a bonuszt a p�ly�hoz, elhelyezi a p�ly�n, �s be�ll�tja az �lettartam�t.
	 *
	 * @param _currentMaze	A b�nuszhoz rendelend� Maze objektum.
	 * @param _pos			A b�nusz poz�ci�ja.
	 * @param _bonusScore	H�ny pont van a b�nuszban.
	 * @param _timeToLive	A b�nusz �lettartama.
	 */
	public ScoreBonus(Maze _currentMaze, Coordinates _pos, long _timeToLive, long _bonusScore) {
		super (_currentMaze, _pos, _timeToLive);
		radius = 5;
		bonusScore = _bonusScore;
		Println(this + " holds " + bonusScore + " points.");
	}

	/**
	 * A b�nusz felv�tele. A currentMaze-en kereszt�l megh�vja a {@link Maze#IncreaseScore} met�dust.
	 */
	void PickUp() {
		In ("PickUp", "" + bonusScore);

		currentMaze.IncreaseScore(bonusScore);

		Leave("PickUp", "");
	}
}

/**
 * Id�b�nusz.
 */
class TimeBonus extends Bonus {

	/**
	 * Mennyi plussz id� van a b�nuszban.
	 */
	private long bonusTime;

	/**
	 * Hozz�rendeli a bonuszt a p�ly�hoz, elhelyezi a p�ly�n, �s be�ll�tja az �lettartam�t.
	 *
	 * @param _currentMaze	A b�nuszhoz rendelend� Maze objektum.
	 * @param _pos			A b�nusz poz�ci�ja.
	 * @param _bonusTime	Mennyi plussz id� van a b�nuszban.
	 * @param _timeToLive	A b�nusz �lettartama.
	 */
	public TimeBonus(Maze _currentMaze, Coordinates _pos, long _timeToLive, long _bonusTime) {
		super (_currentMaze, _pos, _timeToLive);
		radius = 5;
		bonusTime = _bonusTime;
		Println(this + " holds " + bonusTime + " ticks.");
	}

	/**
	 * A b�nusz felv�tele. A currentMaze-en kereszt�l megh�vja a {@link Maze#IncreaseTime} met�dust.
	 */
	void PickUp() {
		In ("PickUp", "" + bonusTime);

		currentMaze.IncreaseTime(bonusTime);

		Leave("PickUp", "");
	}
}

// CRYSTAL

/**
 * A p�ly�n elhelyezked� krist�lyok oszt�lya. Ezeknek a krist�lyoknak az �sszegy�jt�se a p�lya
 * teljes�t�s�nek felt�tele.
 */
class Crystal extends MazeItem {

	/**
	 * Hozz�rendeli a krist�lyt a p�ly�hoz, �s elhelyezi a p�ly�n.
	 *
	 * @param _currentMaze	A krist�lyhoz rendelend� Maze objektum.
	 * @param _pos			A krist�ly poz�ci�ja.
	 */
	Crystal(Maze _currentMaze, Coordinates _pos) {
		super (_currentMaze, _pos);
		radius = 3;
	}
}