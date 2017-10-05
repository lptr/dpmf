// $Id: MazeItem.java,v 1.11 2001/05/30 13:49:52 lptr Exp $
// $Date: 2001/05/30 13:49:52 $
// $Author: lptr $

package PacMan;

import java.lang.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import ImageCache.*;

// BASE CLASSES

/**
 * Az �sszes p�lyaelem alaposzt�lya. Tartalmazza a p�lyaelemek viselked�s�t megval�s�t�
 * alapvet� met�dusokat ({@link #Act()}, {@link #GetCoordinates()}, {@link #GetR()}).
 */
abstract class MazeItem {
	static int mazeItemCount = 0;

	int mazeItemNumber = mazeItemCount++;

	String name;
	String state;
	int phase = 0;
	int phases = 0;
	/* VaVa vette ki, mivel ezt a paintben haszn�ljuk lok�slis jelleggel, itt feleslegess� v�llt */
	/*Dimension imgBounds;;*/

	/**
	 * A p�lyaelemet tartalmaz� Maze objektum
	 */
	Maze currentMaze;

	/**
	 * A p�lyaelem poz�ci�ja, sebess�ge �s ir�nya.
	 */
	Coordinates pos;

	/**
	 * A p�lyaelem sugara (m�rete).
	 */
	double radius;

	/**
	 * Hozz�rendeli a p�lyaelemet a p�ly�hoz, �s elhelyezi a p�ly�n.
	 *
	 * @param _currentMaze	A p�lyaelemhez rendelt Maze objektum
	 * @param _pos			A p�lyaelem poz�ci�ja
	 */
	MazeItem(Maze _currentMaze, Coordinates _pos) {
		currentMaze = _currentMaze;
		pos = _pos;
		name = getClass().getName();
		name = name.substring (name.indexOf(".") + 1);
		state = "";
		ChangeState("Lives");
/*		imgBounds = currentMaze.cache.getBounds(name);*/
	}

	/**
	 * �llapotot v�lt a grafik�ban
	 *
	 * @author VaVa
	 */
	protected void ChangeState(String state) {
		if (!this.state.equals(state)) {
			phases = currentMaze.cache.getAnim(name, state);
			if (phases == 0) {
				if (!this.state.equals("Lives"))
					phase = 1;
				this.state = "Lives";
				phases = currentMaze.cache.getAnim(name,"Lives");
			} else {
				this.state = state;
				phase = 1;
			}
		}
		if (phases == 0)
			phases = 1;
	}

	/**
	 * Lehet�s�get ad a p�lyaelemnek, hogy az �letciklus�t folytassa.
	 *
	 * @return	False �rt�kkel t�r vissza, ha a p�lyaelem elpusztult (pl. lej�rt a b�nusz 
				{@link TimedMazeItem#timeToLive timeToLive}-je)
	 */
	public boolean Act() {
		phase = (phase % phases) + 1;
		return true;
	}

	/**
	 * Visszaadja a p�lyaelem poz�ci�j�t.
	 *
	 * @return A p�lyaelem poz�ci�ja.
	 */
	public Coordinates GetCoordinates() {
		return pos;
	}

	/**
	 * Visszaadja a p�lyaelem sugar�t (m�ret�t).
	 *
	 * @return A p�lyaelem sugara.
	 */
	public double GetR() {
		return radius;
	}

	public int GetItemNumber() {
		return mazeItemNumber;
	}

	public abstract void OutputInfo();

	public void paint(Graphics2D g, ImageCache cache) {
		Image i = cache.getPic (name, state, phase);
		if (i!=null) {
/*			Dimension imgBounds = new Dimension(
				i.getWidth(currentMaze.observer),
				i.getHeight(currentMaze.observer));*/
			int width = i.getWidth(currentMaze.observer);
			int height = i.getHeight(currentMaze.observer);
			g.drawImage (i,
				(int)(pos.x * Maze.CELLSIZE) - width / 2, 
				(int)(pos.y * Maze.CELLSIZE) - height / 2,
				width,height,currentMaze.observer);
	/* Ez a r�sz felel�s a wrap around helyes megjelen�t�s��rt */
			if ((pos.x * Maze.CELLSIZE) - width / 2<0)
				g.drawImage (i,
					(int)((pos.x+currentMaze.mazeWidth) * Maze.CELLSIZE) - width / 2, 
					(int)(pos.y * Maze.CELLSIZE) - height / 2,
					width,height,currentMaze.observer);
			if ((pos.x * Maze.CELLSIZE) + width / 2>=currentMaze.mazeWidth*Maze.CELLSIZE)
				g.drawImage (i,
					(int)((pos.x-currentMaze.mazeWidth) * Maze.CELLSIZE) - width / 2, 
					(int)(pos.y * Maze.CELLSIZE) - height / 2,
					width,height,currentMaze.observer);
			if ((pos.y * Maze.CELLSIZE) - height / 2<0)
				g.drawImage (i,
					(int)(pos.x * Maze.CELLSIZE) - width / 2, 
					(int)((pos.y+currentMaze.mazeHeight) * Maze.CELLSIZE) - height / 2,
					width,height,currentMaze.observer);
			if ((pos.y * Maze.CELLSIZE) + height / 2>=currentMaze.mazeHeight*Maze.CELLSIZE)
				g.drawImage (i,
					(int)(pos.x * Maze.CELLSIZE) - width / 2, 
					(int)((pos.y-currentMaze.mazeHeight)  * Maze.CELLSIZE) - height / 2,
					width,height,currentMaze.observer);


		} else System.out.println(name+"_"+state+"_"+phase+" gives null!!!");
	}
}

/**
 * Mozg� p�lyaelem. A p�lyaelemet a {@link #direction} �s a {@link #speed}
 * attrib�tumokkal b�v�ti ki.
 */
abstract class ActiveMazeItem extends MazeItem {

	/**
	 * A p�lyaelem ir�ny�t adja meg. (Erre n�z, erre halad. Esetleg semerre.)
	 */
	public int direction;

	/**
	 * A p�lyaelem erre akar ir�nyt v�ltani.
	 */
	public int changeDirection;

	/**
	 * A p�lyaelem sebess�ge.
	 */
	double speed;
	
	/**
	 * �tmehet-e a p�lyaelem a p�lya sz�lein.
	 */
	boolean warps;

	/**
	 * Hozz�rendeli a p�lyaelemet a p�ly�hoz, �s elhelyezi a p�ly�n.
	 *
	 * @param _currentMaze	A p�lyaelemhez rendelend� Maze objektum.
	 * @param _pos			A p�lyaelem poz�ci�ja.
	 * @param _dir			A p�lyaelem ir�nya.
	 * @param _speed		A p�lyaelem sebess�ge (mez�/tick)
	 */
	ActiveMazeItem(Maze _currentMaze, Coordinates _pos, int _dir, double _speed) {
		super (_currentMaze, _pos);
		direction = _dir;
		changeDirection = _dir;
		speed = _speed;
		state = "";
		ChangeState("Stopped");
		warps = false;
	}

	/**
	 * Be�ll�tja a p�lyaelem ir�ny�t.
	 *
	 * @param dir	A p�lyaeleme ir�nya.
	 * @see	#direction
	 */
	void SetDirection(int dir) {
		changeDirection = dir;
	}

	/**
	 * Be�ll�tja a p�lyaelem ir�ny�t.
	 *
	 * @param dir	A p�lyaeleme ir�nya.
	 * @see	#direction
	 */
	void SetDirection(String dir) {
		if (dir.equalsIgnoreCase("up")) {
			SetDirection (Coordinates.DIR_UP);
		} else
		if (dir.equalsIgnoreCase("right")) {
			SetDirection (Coordinates.DIR_RIGHT);
		} else
		if (dir.equalsIgnoreCase("down")) {
			SetDirection (Coordinates.DIR_DOWN);
		} else
		if (dir.equalsIgnoreCase("left")) {
			SetDirection (Coordinates.DIR_LEFT);
		} else
		if (dir.equalsIgnoreCase("stop") ||
			dir.equalsIgnoreCase("stopped")) {
			SetDirection (Coordinates.DIR_STOP);
		}
	}

	/**
	 * Meg�llap�tja, hogy mehet-e a p�lyaelem az adott ir�nyba.
	 */
	boolean CanGoDirection (int dir) {
		int dirtocenter = pos.DirectionToCenter();
		double distfromcenter = pos.DistanceFromCenter();
		boolean waythrough = currentMaze.isNeighbour (pos, dir, warps);

		return
			(
				dirtocenter == Coordinates.DIR_STOP &&
				waythrough
			) ||
			(
				dirtocenter == dir &&
				(
					distfromcenter >= speed ||
					waythrough
				)
			) ||
			(
				!Coordinates.CompareDirections (dirtocenter, dir) &&
				distfromcenter <= speed &&
				waythrough
			) ||
			(
				dirtocenter == Coordinates.OppositeDirection(dir)
			);
	}

	/**
	 * Mozgatja a p�lyaelemet.
	 */
	public void Move() {
		if (!currentMaze.debugAutoMove)
			return;

		if (changeDirection == Coordinates.DIR_STOP) {
			direction = changeDirection;
			return;
		}

		if (direction != changeDirection &&
			CanGoDirection (changeDirection)) {
			direction = changeDirection;
		} else {
			if (direction == Coordinates.DIR_STOP)
				return;

			if (!CanGoDirection (direction)) {
				pos.CenterOnField();
				direction = changeDirection = Coordinates.DIR_STOP;
				return;
			}
		}

		double speedleft = speed;

		if (!Coordinates.CompareDirections (pos.DirectionToCenter(), direction)) {
			speedleft -= pos.DistanceFromCenter();
			pos.CenterOnField();
		}

		switch (direction) {
		case Coordinates.DIR_UP :
			pos.y -= speedleft;
			break;
		case Coordinates.DIR_DOWN :
			pos.y += speedleft;
			break;
		case Coordinates.DIR_LEFT :
			pos.x -= speedleft;
			break;
		case Coordinates.DIR_RIGHT :
			pos.x += speedleft;
		}

		if (pos.x < 0)
			pos.x += currentMaze.mazeWidth;
		else
		if (pos.x > currentMaze.mazeWidth)
			pos.x -= currentMaze.mazeWidth;

		if (pos.y < 0)
			pos.y += currentMaze.mazeHeight;
		else
		if (pos.y > currentMaze.mazeHeight)
			pos.y -= currentMaze.mazeHeight;
	}

	/**
	 * Lehet�s�get ad a p�lyaelemnek, hogy az �letciklus�t folytassa.
	 *
	 * @return	false �rt�kkel t�r vissza, ha a p�lyaelem elpusztult..
	 */
	public boolean Act() {
		Move();
		return super.Act();
	}	
}

/**
 * Id�szakos p�lyaelemek alaposzt�lya. A MazeItem oszt�lyt a {@link #timeToLive}
 * attrib�tummal b�v�ti ki.
 */
abstract class TimedMazeItem extends MazeItem {

	/**
	 * A p�lyaelem �lettartama. Ennyi "tick" ut�n sz�nik meg l�tezni.
	 */
	protected long timeToLive;
	
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
	}
	
	/**
	 * Visszaadja a {@link #timeToLive} �rt�k�t.
	 */
	public long GetTimeToLive() {
		return timeToLive;
	}
	
	/**
	 * Lehet�s�get ad a p�lyaelemnek, hogy az �letciklus�t folytassa.
	 *
	 * @return	False �rt�kkel t�r vissza, ha a p�lyaelem elpusztult (lej�rt a {@link #timeToLive}-je).
	 */
	public boolean Act() {
		super.Act();
		timeToLive--;
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
	int bombs = 0;

	/**
	 * A pacman csak akkor j�tsza a Stopped anim�ci�t, ha egy ideig nem ny�ltak hozz�, ezt t�rolja ez..
	 */
	int stopcount = 0;

	/**
	 * Hozz�rendeli a PacMan-t a p�ly�hoz, �s elhelyezi a p�ly�n.
	 *
	 * @param _currentMaze	A PacMan-hez rendelend� Maze objektum.
	 * @param _pos			A PacMan poz�ci�ja.
	 * @param _speed		A PacMan sebess�ge.
	 */
	PacMan(Maze _currentMaze, Coordinates _pos, double _speed) {
		super (_currentMaze, _pos, Coordinates.DIR_STOP, _speed);
		radius = 0.25;
		warps = true;
	}

	/**
	 * A {@link #putbomb} v�ltoz� be�ll�t�sa (bomba lerak�sa).
	 *
	 * @param state		A be�ll�tand� �llapot
	 */
	public void SetBombPutState(boolean state) {
		putbomb = state;
	}

	/**
	 * A {@link #putbomb} v�ltoz� lek�rdez�se (rak-e le bomb�t ide a PacMan).
	 *
	 * @return	A {@link #putbomb} �rt�ke.
	 */
	public boolean GetBombPutState() {
		return putbomb;
	}
	
	/**
	 * Lerak egy bomb�t, ha m�g nem volt ott bomba.
	 */
	public void PutBomb() {
		if (bombs > 0) {
			SetBombPutState(true);
			bombs--;
		}
	}

	public boolean Act() {
		boolean temp = super.Act();
		if (Coordinates.DIRS[direction].equals("Stopped") && !state.equals("Stopped")) {
			if (stopcount++==10)
				ChangeState("Stopped");
		} else {
			stopcount = 0;
			ChangeState(Coordinates.DIRS[direction]);
		}
		return temp;
/*		name = "PacMan" + Coordinates.DIRS[direction];*/
	}

	/**
	 * A bomb�k sz�m�nak n�vel�se.
	 *
	 * @param value		Mennyivel n�velj�k a bomb�k sz�m�t.
	 */
	void IncreaseBombs(int value) {
		bombs += value;
	}

	public void OutputInfo() {
		System.out.println("pacman " + mazeItemNumber + " " + pos + " " + Coordinates.DIRS[direction] + " " + bombs);
	}

	public void hackBombs(int value) {
		bombs = value;
	}
}

/**
 * A j�t�kban szerepl� sz�rnyek alaposzt�lya. A MazeItem oszt�lyt az {@link #active}
 * attrib�tummal b�v�ti ki.
 */
abstract class Monster extends ActiveMazeItem {

	/**
	 * Akt�v-e a sz�rny. Ha a sz�rny elpusztult, nem t�rl�dik, csak inakt�vv� v�lik.
	 * Egy id� ut�n ({@link #timeToRebirth}) �jra akt�v lesz.
	 */
	protected boolean active;

	/**
	 * Ha a sz�rny inakt�v, ennyi "tick" ut�n sz�letik �jj�.
	 */
	protected long timeToRebirth;
	
	/**
	 * Mennyi id� (tick) m�lva fog �jra elgondolkodni, hogy merre menjen.
	 */
	protected long timeToDecide;

	/**
	 * Hozz�rendeli a sz�rnyet a p�ly�hoz, �s elhelyezi a p�ly�n.
	 *
	 * @param _currentMaze	A sz�rnyh�z rendelend� Maze objektum.
	 * @param _pos			A sz�rny poz�ci�ja.
	 * @param _speed		A sz�rny sebess�ge.
	 */
	public Monster(Maze _currentMaze, Coordinates _pos, double _speed) {
		super (_currentMaze, _pos, Coordinates.DIR_STOP, _speed);
		radius = 0.25;
		active = true;
		timeToRebirth = 0;
		timeToDecide = 0;
	}
	
	/**
	 * Aktiv�lja a sz�rnyet.
	 */
	public void Activate() {
		active = true;
	}

	/**
	 * Deaktiv�lja a sz�rnyet. Be�ll�tja az �jj�sz�let�sig h�tralev� id�t.
	 *
	 * @param _timeToRebirth	Az �jj�sz�let�sig h�tralev� id�
	 */
	public void Deactivate(long _timeToRebirth) {
		active = false;
		timeToRebirth = _timeToRebirth;
//		System.out.println("event monster " + mazeItemNumber + " died " + pos);
	}

	/**
	 * �jj�sz�li a sz�rnyet az �jj�sz�let�-helyen.
	 */
	public void Reborn() {
		Activate();
		pos = new Coordinates (currentMaze.GetRebornPlace());
		timeToDecide = 0;
//		System.out.println("event monster " + mazeItemNumber + " born " + pos);
	}

	/**
	 * Visszaadja, hogy akt�v-e a sz�rny.
	 *
	 * @return	Akt�v-e a sz�rny.
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Lehet�s�get ad a p�lyaelemnek, hogy az �letciklus�t folytassa: Ha inakt�v,
	 * cs�kkenti a {@link #timeToRebirth} �rt�k�t. Ha az el�rt 0-ra, a sz�rny �jj�sz�letik.
	 *
	 * @return	False �rt�kkel t�r vissza, ha a p�lyaelem elpusztult..
	 */
	public boolean Act() {
		/* Ha lej�rt a gondolkod�sok k�zti id�, vagy ha meg�lltunk,
		   �jra d�nt�nk, merre menj�nk.. */
		if (active) {
			if (timeToDecide <= 0 || direction == Coordinates.DIR_STOP) {
				Decide();
			} else {
				timeToDecide--;
			}

			/* Ha elpusztult, visszat�r�nk */
			if (!super.Act())
				return false;
		} else {
			if (--timeToRebirth <= 0) {
				Reborn();
			}
		}

		return true;
	}

	public void Move() {
		if (active)
			super.Move();
	}

	/**
	 * A sz�rny intelligenci�j�t implement�l� met�dus. � �ll�tja a {@link #timeToDecide} �rt�k�t.
	 */
	abstract void Decide();

	/**
	 * Kirajzol�s. Csak akkor rajzolunk, ha a sz�rny akt�v.
	 */
	public void paint(Graphics2D g, ImageCache cache) {
		if (active)
			super.paint(g, cache);
	}
}

/**
 * Buta sz�rny
 */
class DumbMonster extends Monster {
	public DumbMonster(Maze _currentMaze, Coordinates _pos, double _speed) {
		super (_currentMaze, _pos,  _speed);
	}

	public boolean Act() {
		boolean temp = super.Act();
		ChangeState(Coordinates.DIRS[direction]);
		return temp;
	}

	void Decide() {
		int dir;
		long duration;

		if (Math.random() < 0.3) {
			/* abszol�t v�letlen ir�ny */
			dir = (int)(Math.random() * 5);
			duration = 2 + (long)(Math.random() * 3);
		} else {
			/* �rtelmesebb d�nt�s: a pacman ir�ny�ba l�p�nk */
			dir = pos.DirectionTowards(currentMaze.pacman.GetCoordinates());
			duration = 5 + (long)(Math.random() * 4);
		}

		SetDirection (dir);

		timeToDecide = duration;
	}

	public void OutputInfo() {
		if (isActive())
			System.out.println("monster " + mazeItemNumber + " dumb " + pos + " " + Coordinates.DIRS[direction]);
	}
}

/**
 * Okos sz�rny
 */
class CleverMonster extends Monster {
	LinkedList path;
	Coordinates currentField;

	public CleverMonster(Maze _currentMaze, Coordinates _pos, double _speed) {
		super (_currentMaze, _pos, _speed);
	}

	void Decide() {
		path = currentMaze.PathFinder(pos, currentMaze.pacman.GetCoordinates());
		if (path == null)
			currentField = pos;
		else
			currentField = (Coordinates)(path.removeFirst());
		timeToDecide = (long)(0.25/speed * pos.GetDistance(currentMaze.pacman.GetCoordinates()));
	}

	Coordinates GetNextFieldOnPath() {
		if (path==null)
				return null;
		if (path.size() == 0) {
//			if (!pos.SameField(currentMaze.pacman.GetCoordinates())) {
//				Decide();
				return null;
//			} else {
//				return pos;
//			}
		}

		return (Coordinates)path.removeFirst();
		
	}

//	public boolean Move() {
//		return super.Move();
//	}

	public int dirTowards(Coordinates to) {
		if (to.getX()<pos.getX())
			return Coordinates.DIR_LEFT;
		else if (to.getX()>pos.getX())
			return Coordinates.DIR_RIGHT;
		else if (to.getY()>pos.getY())
			return Coordinates.DIR_DOWN;
		else if (to.getY()<pos.getY())
			return Coordinates.DIR_UP;
		else 
			return Coordinates.DIR_STOP;
	}

	public boolean Act() {
		boolean ret = super.Act();

		int dir;
		if (currentField==null) {
			dir = dirTowards(currentMaze.pacman.GetCoordinates());
		} else {
			dir = dirTowards(currentField);
			if (dir==Coordinates.DIR_STOP) {
				currentField = GetNextFieldOnPath();
				if (currentField==null)
					dir = dirTowards(currentMaze.pacman.GetCoordinates());
				else
					dir = dirTowards(currentField);
			}
		}
		SetDirection(dir);

		ChangeState(Coordinates.DIRS[direction]);
		return ret;
	}

	public void OutputInfo() {
		if (isActive())
			System.out.println("monster " + mazeItemNumber + " clever " + pos + " " + Coordinates.DIRS[direction]);
	}
}

/**
 * A PacMan �ltal a p�ly�n elhelyezhet� bomb�t megval�s�t� oszt�ly.
 */
class Bomb extends TimedMazeItem {

	/**
	 * �les�tve van-e m�r a bomba.
	 */
	protected boolean active;
	
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
		radius = 0.15;
		ChangeState("Inactive");
	}
	
	/**
	 * �les�ti a bomb�t.
	 */
	void Activate() {
		active = true;
	}

	public boolean Act () {
		boolean temp = super.Act();
		if (isActive())
			ChangeState("Active");
		return temp;
	}

	/**
	 * �les�tett-e a bomba.
	 *
	 * @return A bomba �llapota.
	 */
	boolean isActive() {
		return active;
	}

	public void OutputInfo() {
		System.out.println("bomb " + mazeItemNumber + " " + pos + (active?" active ":" inactive ") + timeToLive);
	}
}

// BONUSES

/**
 * A p�ly�n n�ha bizonyos id�re megjelen� b�nuszok alaposzt�lya.
 */
abstract class Bonus extends TimedMazeItem {

	/**
	 * Hozz�rendeli a bonuszt a p�ly�hoz, elhelyezi a p�ly�n, �s be�ll�tja az �lettartam�t.
	 *
	 * @param _currentMaze	A b�nuszhoz rendelend� Maze objektum.
	 * @param _pos			A b�nusz poz�ci�ja.
	 * @param _timeToLive	A b�nusz �lettartama.
	 */
	Bonus(Maze _currentMaze, Coordinates _pos, long _timeToLive) {
		super (_currentMaze, _pos, _timeToLive);
		radius = 0.15;
	}

	/**
	 * A b�nusz felv�telekor bek�vetkez� esem�ny. Az egyes b�nuszok fel�ldefini�lj�k ezt
	 * a met�dust, �s ezen kereszt�l "hatnak" (innen h�vj�k meg a {@link Maze#IncreaseScore}, 
	 * {@link Maze#IncreaseTime}, {@link Maze#IncreaseBombs} met�dusokat).
	 */
	abstract void PickUp();

	abstract void hackValue (long value);
	abstract long hackGetValue();
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
		radius = 0.15;
		bombsCount = _bombsCount;
	}

	/**
	 * A b�nusz felv�tele. A currentMaze-en kereszt�l megh�vja a {@link Maze#IncreaseBombs} met�dust.
	 */
	public void PickUp() {
		currentMaze.IncreaseBombs(bombsCount);
	}

	public void OutputInfo() {
		System.out.println("bonus " + mazeItemNumber + " " + pos + " bomb " + timeToLive + " " + bombsCount);
	}

	void hackValue(long value) {
		bombsCount = (int)value;
	}

	long hackGetValue() {
		return bombsCount;
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
		radius = 0.15;
		bonusScore = _bonusScore;
	}

	/**
	 * A b�nusz felv�tele. A currentMaze-en kereszt�l megh�vja a {@link Maze#IncreaseScore} met�dust.
	 */
	void PickUp() {
		currentMaze.IncreaseScore(bonusScore);
	}

	public void OutputInfo() {
		System.out.println("bonus " + mazeItemNumber + " " + pos + " score " + timeToLive + " " + bonusScore);
	}

	void hackValue(long value) {
		bonusScore = value;
	}

	long hackGetValue() {
		return bonusScore;
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
		radius = 0.15;
		bonusTime = _bonusTime;
	}

	/**
	 * A b�nusz felv�tele. A currentMaze-en kereszt�l megh�vja a {@link Maze#IncreaseTime} met�dust.
	 */
	void PickUp() {
		currentMaze.IncreaseTime(bonusTime);
	}

	public void OutputInfo() {
		System.out.println("bonus " + mazeItemNumber + " " + pos + " time " + timeToLive + " " + bonusTime);
	}

	void hackValue(long value) {
		bonusTime = value;
	}

	long hackGetValue() {
		return bonusTime;;
	}
}

// CRYSTAL

/**
 * A p�ly�n elhelyezked� krist�lyok oszt�lya. Ezeknek a krist�lyoknak az �sszegy�jt�se a p�lya
 * teljes�t�s�nek felt�tele.
 */
class Crystal extends MazeItem {

	/**
	 * A krist�ly �rt�ke.
	 */
	int value = 20;

	/**
	 * Hozz�rendeli a krist�lyt a p�ly�hoz, �s elhelyezi a p�ly�n.
	 *
	 * @param _currentMaze	A krist�lyhoz rendelend� Maze objektum.
	 * @param _pos			A krist�ly poz�ci�ja.
	 */
	Crystal(Maze _currentMaze, Coordinates _pos) {
		super (_currentMaze, _pos);
		radius = 0.10;
	}

	public void OutputInfo() {
		System.out.println("crystal " + mazeItemNumber + " " + pos);
	}

	/**
	 * A krist�ly felv�tele. A currentMaze-en kereszt�l megh�vja a {@link Maze#IncreaseScore} met�dust.
	 */
	void PickUp() {
		currentMaze.IncreaseScore(value);
	}
}