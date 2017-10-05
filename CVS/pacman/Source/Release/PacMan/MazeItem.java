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
 * Az összes pályaelem alaposztálya. Tartalmazza a pályaelemek viselkedését megvalósító
 * alapvetõ metódusokat ({@link #Act()}, {@link #GetCoordinates()}, {@link #GetR()}).
 */
abstract class MazeItem {
	static int mazeItemCount = 0;

	int mazeItemNumber = mazeItemCount++;

	String name;
	String state;
	int phase = 0;
	int phases = 0;
	/* VaVa vette ki, mivel ezt a paintben használjuk lokáslis jelleggel, itt feleslegessé vállt */
	/*Dimension imgBounds;;*/

	/**
	 * A pályaelemet tartalmazó Maze objektum
	 */
	Maze currentMaze;

	/**
	 * A pályaelem pozíciója, sebessége és iránya.
	 */
	Coordinates pos;

	/**
	 * A pályaelem sugara (mérete).
	 */
	double radius;

	/**
	 * Hozzárendeli a pályaelemet a pályához, és elhelyezi a pályán.
	 *
	 * @param _currentMaze	A pályaelemhez rendelt Maze objektum
	 * @param _pos			A pályaelem pozíciója
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
	 * Állapotot vált a grafikában
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
	 * Lehetõséget ad a pályaelemnek, hogy az életciklusát folytassa.
	 *
	 * @return	False értékkel tér vissza, ha a pályaelem elpusztult (pl. lejárt a bónusz 
				{@link TimedMazeItem#timeToLive timeToLive}-je)
	 */
	public boolean Act() {
		phase = (phase % phases) + 1;
		return true;
	}

	/**
	 * Visszaadja a pályaelem pozícióját.
	 *
	 * @return A pályaelem pozíciója.
	 */
	public Coordinates GetCoordinates() {
		return pos;
	}

	/**
	 * Visszaadja a pályaelem sugarát (méretét).
	 *
	 * @return A pályaelem sugara.
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
	/* Ez a rész felelõs a wrap around helyes megjelenítéséért */
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
 * Mozgó pályaelem. A pályaelemet a {@link #direction} és a {@link #speed}
 * attribútumokkal bõvíti ki.
 */
abstract class ActiveMazeItem extends MazeItem {

	/**
	 * A pályaelem irányát adja meg. (Erre néz, erre halad. Esetleg semerre.)
	 */
	public int direction;

	/**
	 * A pályaelem erre akar irányt váltani.
	 */
	public int changeDirection;

	/**
	 * A pályaelem sebessége.
	 */
	double speed;
	
	/**
	 * Átmehet-e a pályaelem a pálya szélein.
	 */
	boolean warps;

	/**
	 * Hozzárendeli a pályaelemet a pályához, és elhelyezi a pályán.
	 *
	 * @param _currentMaze	A pályaelemhez rendelendõ Maze objektum.
	 * @param _pos			A pályaelem pozíciója.
	 * @param _dir			A pályaelem iránya.
	 * @param _speed		A pályaelem sebessége (mezõ/tick)
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
	 * Beállítja a pályaelem irányát.
	 *
	 * @param dir	A pályaeleme iránya.
	 * @see	#direction
	 */
	void SetDirection(int dir) {
		changeDirection = dir;
	}

	/**
	 * Beállítja a pályaelem irányát.
	 *
	 * @param dir	A pályaeleme iránya.
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
	 * Megállapítja, hogy mehet-e a pályaelem az adott irányba.
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
	 * Mozgatja a pályaelemet.
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
	 * Lehetõséget ad a pályaelemnek, hogy az életciklusát folytassa.
	 *
	 * @return	false értékkel tér vissza, ha a pályaelem elpusztult..
	 */
	public boolean Act() {
		Move();
		return super.Act();
	}	
}

/**
 * Idõszakos pályaelemek alaposztálya. A MazeItem osztályt a {@link #timeToLive}
 * attribútummal bõvíti ki.
 */
abstract class TimedMazeItem extends MazeItem {

	/**
	 * A pályaelem élettartama. Ennyi "tick" után szûnik meg létezni.
	 */
	protected long timeToLive;
	
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
	}
	
	/**
	 * Visszaadja a {@link #timeToLive} értékét.
	 */
	public long GetTimeToLive() {
		return timeToLive;
	}
	
	/**
	 * Lehetõséget ad a pályaelemnek, hogy az életciklusát folytassa.
	 *
	 * @return	False értékkel tér vissza, ha a pályaelem elpusztult (lejárt a {@link #timeToLive}-je).
	 */
	public boolean Act() {
		super.Act();
		timeToLive--;
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
	int bombs = 0;

	/**
	 * A pacman csak akkor játsza a Stopped animációt, ha egy ideig nem nyúltak hozzá, ezt tárolja ez..
	 */
	int stopcount = 0;

	/**
	 * Hozzárendeli a PacMan-t a pályához, és elhelyezi a pályán.
	 *
	 * @param _currentMaze	A PacMan-hez rendelendõ Maze objektum.
	 * @param _pos			A PacMan pozíciója.
	 * @param _speed		A PacMan sebessége.
	 */
	PacMan(Maze _currentMaze, Coordinates _pos, double _speed) {
		super (_currentMaze, _pos, Coordinates.DIR_STOP, _speed);
		radius = 0.25;
		warps = true;
	}

	/**
	 * A {@link #putbomb} változó beállítása (bomba lerakása).
	 *
	 * @param state		A beállítandó állapot
	 */
	public void SetBombPutState(boolean state) {
		putbomb = state;
	}

	/**
	 * A {@link #putbomb} változó lekérdezése (rak-e le bombát ide a PacMan).
	 *
	 * @return	A {@link #putbomb} értéke.
	 */
	public boolean GetBombPutState() {
		return putbomb;
	}
	
	/**
	 * Lerak egy bombát, ha még nem volt ott bomba.
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
	 * A bombák számának növelése.
	 *
	 * @param value		Mennyivel növeljük a bombák számát.
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
 * A játékban szereplõ szörnyek alaposztálya. A MazeItem osztályt az {@link #active}
 * attribútummal bõvíti ki.
 */
abstract class Monster extends ActiveMazeItem {

	/**
	 * Aktív-e a szörny. Ha a szörny elpusztult, nem törlõdik, csak inaktívvá válik.
	 * Egy idõ után ({@link #timeToRebirth}) újra aktív lesz.
	 */
	protected boolean active;

	/**
	 * Ha a szörny inaktív, ennyi "tick" után születik újjá.
	 */
	protected long timeToRebirth;
	
	/**
	 * Mennyi idõ (tick) múlva fog újra elgondolkodni, hogy merre menjen.
	 */
	protected long timeToDecide;

	/**
	 * Hozzárendeli a szörnyet a pályához, és elhelyezi a pályán.
	 *
	 * @param _currentMaze	A szörnyhöz rendelendõ Maze objektum.
	 * @param _pos			A szörny pozíciója.
	 * @param _speed		A szörny sebessége.
	 */
	public Monster(Maze _currentMaze, Coordinates _pos, double _speed) {
		super (_currentMaze, _pos, Coordinates.DIR_STOP, _speed);
		radius = 0.25;
		active = true;
		timeToRebirth = 0;
		timeToDecide = 0;
	}
	
	/**
	 * Aktiválja a szörnyet.
	 */
	public void Activate() {
		active = true;
	}

	/**
	 * Deaktiválja a szörnyet. Beállítja az újjászületésig hátralevõ idõt.
	 *
	 * @param _timeToRebirth	Az újjászületésig hátralevõ idõ
	 */
	public void Deactivate(long _timeToRebirth) {
		active = false;
		timeToRebirth = _timeToRebirth;
//		System.out.println("event monster " + mazeItemNumber + " died " + pos);
	}

	/**
	 * Újjászüli a szörnyet az újjászületõ-helyen.
	 */
	public void Reborn() {
		Activate();
		pos = new Coordinates (currentMaze.GetRebornPlace());
		timeToDecide = 0;
//		System.out.println("event monster " + mazeItemNumber + " born " + pos);
	}

	/**
	 * Visszaadja, hogy aktív-e a szörny.
	 *
	 * @return	Aktív-e a szörny.
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Lehetõséget ad a pályaelemnek, hogy az életciklusát folytassa: Ha inaktív,
	 * csökkenti a {@link #timeToRebirth} értékét. Ha az elért 0-ra, a szörny újjászületik.
	 *
	 * @return	False értékkel tér vissza, ha a pályaelem elpusztult..
	 */
	public boolean Act() {
		/* Ha lejárt a gondolkodások közti idõ, vagy ha megálltunk,
		   újra döntünk, merre menjünk.. */
		if (active) {
			if (timeToDecide <= 0 || direction == Coordinates.DIR_STOP) {
				Decide();
			} else {
				timeToDecide--;
			}

			/* Ha elpusztult, visszatérünk */
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
	 * A szörny intelligenciáját implementáló metódus. Õ állítja a {@link #timeToDecide} értékét.
	 */
	abstract void Decide();

	/**
	 * Kirajzolás. Csak akkor rajzolunk, ha a szörny aktív.
	 */
	public void paint(Graphics2D g, ImageCache cache) {
		if (active)
			super.paint(g, cache);
	}
}

/**
 * Buta szörny
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
			/* abszolút véletlen irány */
			dir = (int)(Math.random() * 5);
			duration = 2 + (long)(Math.random() * 3);
		} else {
			/* értelmesebb döntés: a pacman irányába lépünk */
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
 * Okos szörny
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
 * A PacMan által a pályán elhelyezhetõ bombát megvalósító osztály.
 */
class Bomb extends TimedMazeItem {

	/**
	 * Élesítve van-e már a bomba.
	 */
	protected boolean active;
	
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
		radius = 0.15;
		ChangeState("Inactive");
	}
	
	/**
	 * Élesíti a bombát.
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
	 * Élesített-e a bomba.
	 *
	 * @return A bomba állapota.
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
 * A pályán néha bizonyos idõre megjelenõ bónuszok alaposztálya.
 */
abstract class Bonus extends TimedMazeItem {

	/**
	 * Hozzárendeli a bonuszt a pályához, elhelyezi a pályán, és beállítja az élettartamát.
	 *
	 * @param _currentMaze	A bónuszhoz rendelendõ Maze objektum.
	 * @param _pos			A bónusz pozíciója.
	 * @param _timeToLive	A bónusz élettartama.
	 */
	Bonus(Maze _currentMaze, Coordinates _pos, long _timeToLive) {
		super (_currentMaze, _pos, _timeToLive);
		radius = 0.15;
	}

	/**
	 * A bónusz felvételekor bekövetkezõ esemény. Az egyes bónuszok felüldefiniálják ezt
	 * a metódust, és ezen keresztül "hatnak" (innen hívják meg a {@link Maze#IncreaseScore}, 
	 * {@link Maze#IncreaseTime}, {@link Maze#IncreaseBombs} metódusokat).
	 */
	abstract void PickUp();

	abstract void hackValue (long value);
	abstract long hackGetValue();
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
		radius = 0.15;
		bombsCount = _bombsCount;
	}

	/**
	 * A bónusz felvétele. A currentMaze-en keresztül meghívja a {@link Maze#IncreaseBombs} metódust.
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
		radius = 0.15;
		bonusScore = _bonusScore;
	}

	/**
	 * A bónusz felvétele. A currentMaze-en keresztül meghívja a {@link Maze#IncreaseScore} metódust.
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
		radius = 0.15;
		bonusTime = _bonusTime;
	}

	/**
	 * A bónusz felvétele. A currentMaze-en keresztül meghívja a {@link Maze#IncreaseTime} metódust.
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
 * A pályán elhelyezkedõ kristályok osztálya. Ezeknek a kristályoknak az összegyûjtése a pálya
 * teljesítésének feltétele.
 */
class Crystal extends MazeItem {

	/**
	 * A kristály értéke.
	 */
	int value = 20;

	/**
	 * Hozzárendeli a kristályt a pályához, és elhelyezi a pályán.
	 *
	 * @param _currentMaze	A kristályhoz rendelendõ Maze objektum.
	 * @param _pos			A kristály pozíciója.
	 */
	Crystal(Maze _currentMaze, Coordinates _pos) {
		super (_currentMaze, _pos);
		radius = 0.10;
	}

	public void OutputInfo() {
		System.out.println("crystal " + mazeItemNumber + " " + pos);
	}

	/**
	 * A kristály felvétele. A currentMaze-en keresztül meghívja a {@link Maze#IncreaseScore} metódust.
	 */
	void PickUp() {
		currentMaze.IncreaseScore(value);
	}
}