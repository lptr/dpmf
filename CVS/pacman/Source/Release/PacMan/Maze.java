// $Id: Maze.java,v 1.9 2001/05/30 22:35:50 vava Exp $
// $Date: 2001/05/30 22:35:50 $
// $Author: vava $

package PacMan;

import java.lang.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import Util.Parser.*;
import ImageCache.*;

/**
 * A játéktér labirintus részét megvalósító objektum. Õ kezeli a pályán mozgó vagy álló
 * pályaelemeket - térben és idõben. Az õ feladata az idõ múlásának kezelése is.<br>
 * A {@link Game} osztály hozza létre.
 *
 * @author Lóci
 */
class Maze {

	static final int CELLSIZE = 32;

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
	 * A pacman sebessége.
	 */
	double pacmanSpeed;

	/**
	 * A pacman ezen a helyen születik újjá.
	 */
	Coordinates pacmanBirthPlace;

	/**
	 * A szörnyek ezen a helyen születnek újjá.
	 */
	Coordinates monsterBirthPlace;

	/**
	 * A labirintust felügyelõ {@link Game} objektum
	 */
	Game parentGame;

	/**
	 * A pálya teljesítéséhez rendelkezésre álló tick-ek száma
	 */
	long time;
	long mazetime;

	/**
	 * A pálya szélessége.
	 */
	protected int mazeWidth;

	/**
	 * A pálya magassága.
	 */
	protected int mazeHeight;

	/**
	 * A pálya mezõi.
	 */
	private FieldItem[] mazeData;

	/**
	 * A bónuszok felbukkanási valószínûsége.
	 */
	private int bonusProbability;

	/**
	 * Automatikus bónusz-elhelyezés.
	 */
	protected boolean debugAutoBonuses = true;

	/**
	 * A szörnyek maguktól mozognak-e.
	 */
	protected boolean debugAutoMove = true;


	/**
	 * A képeket tároló cache.
	 */
	ImageCache cache;

	/**
	 * Ezt az elemet adjuk át ImageObserver-nek.
	 */
	JLabel observer = new JLabel();

	/**
	 * Létrehozza a pályát.
	 */
	Maze(Game _parentGame, InputStream mazeStream, ImageCache _cache, Graphics2D buffer) throws BadDataException {
		parentGame = _parentGame;
		this.cache = _cache;

		Load(mazeStream);
//		buffer.setClip(0, 0, mazeWidth * CELLSIZE, mazeHeight * CELLSIZE);
//		cache.loadBG(fileName.substring(0, fileName.lastIndexOf(".")) + ".png");
	}

	/**
	 * Kiírja az összes mazeitem információját.
	 */
	void OutputItems() {
		int i;

		pacman.OutputInfo();
		for (i = 0; i < monsters.size(); i++)
			((Monster)monsters.get(i)).OutputInfo();
		for (i = 0; i < bonuses.size(); i++)
			((Bonus)bonuses.get(i)).OutputInfo();
		for (i = 0; i < bombs.size(); i++)
			((Bomb)bombs.get(i)).OutputInfo();
		for (i = 0; i < crystals.size(); i++)
			((Crystal)crystals.get(i)).OutputInfo();
	}


	/**
	 * Betölti a fileName által megjelölt pályát. Felépíti a mezõket tartalmazó tömböt,
	 * létrehozza a megfelelõ számú szörnyet, a PacMan-t, a kristályokat.
	 *
	 * @throws IOException Ha nem tudta megnyitni a pályát tartalmazó file-t.
	 */
	void Load(InputStream mazeStream) throws BadDataException {
		ourParser parser = new ourParser(mazeStream);

		mazeWidth = -1;
		mazeHeight = -1;
		time = -1;
		bonusProbability = -1;

		monsterBirthPlace = null;
		pacman = null;
		mazeData = null;
		int mazeDataPos = 0;

		monsters = new LinkedList();
		bombs = new LinkedList();
		bonuses = new LinkedList();
		crystals = new LinkedList();

		while (parser.getNextLine() == ourParser.TT_OK)	{
			if (parser.matchLine("pacman %n %n %n")) {
				pacmanBirthPlace = new Coordinates(
					Double.parseDouble(parser.lineTokens[1]),
					Double.parseDouble(parser.lineTokens[2]));
				pacmanSpeed = Double.parseDouble(parser.lineTokens[3]);
				pacman = new PacMan (
					this,
					new Coordinates (pacmanBirthPlace),
					pacmanSpeed);
			} else
			if (parser.matchLine("time %n")) {
				mazetime = time = Long.parseLong(parser.lineTokens[1]);
			} else
			if (parser.matchLine("size %n %n")) {
				mazeWidth = Integer.parseInt(parser.lineTokens[1]);
				mazeHeight = Integer.parseInt(parser.lineTokens[2]);
				mazeData = new FieldItem[mazeWidth*mazeHeight];
			} else
			if (parser.matchLine("monster clever %n %n %n")) { //monster clever <x> <y> <speed>
				monsters.add (
					new CleverMonster(this, new Coordinates (Double.parseDouble(parser.lineTokens[2]),
															 Double.parseDouble(parser.lineTokens[3])),
															 Double.parseDouble(parser.lineTokens[4])
				));
			} else
			if (parser.matchLine("monster dumb %n %n %n")) { //monster dumb <x> <y> <speed>
				monsters.add (
					new DumbMonster(this, new Coordinates (Double.parseDouble(parser.lineTokens[2]),
														   Double.parseDouble(parser.lineTokens[3])),
														   Double.parseDouble(parser.lineTokens[4])
				));
			} else
			if (parser.matchLine("probability %n")) {
				bonusProbability = Integer.parseInt(parser.lineTokens[1]);
			} else
			if (parser.matchLine("birthplace %n %n")) {
				monsterBirthPlace = new Coordinates(Integer.parseInt(parser.lineTokens[1]),
											 Integer.parseInt(parser.lineTokens[2]));
			} else
			if (parser.matchLine("crystal %n %n")) {
				crystals.add (new Crystal (this, new Coordinates (Double.parseDouble(parser.lineTokens[1]),
																  Double.parseDouble(parser.lineTokens[2]))));
			} else
			if (parser.matchLine("fields {%n}")) {
				if (mazeData==null) {
					throw new BadDataException ("undefined maze size", parser.lineno());
				} else {
					int i;
					for (i=1; i<parser.lineTokens.length && mazeDataPos+i-1<mazeData.length; i++) {
						mazeData[mazeDataPos+i-1] = new FieldItem(parser.lineTokens[i]);
						if (parser.lineTokens[i].length()!=4)
							throw new BadDataException("incorrect maze data", parser.lineno());
					}
					mazeDataPos+=i-1;
					if (i<parser.lineTokens.length)
						throw new BadDataException("too much maze data", parser.lineno());
				}
			}
		}

		if (mazeWidth < 0 ||
			mazeHeight < 0 ||
			mazeData == null ||
			mazeDataPos < mazeWidth*mazeHeight ||
			pacman == null)
			throw new BadDataException ("illegal or undefined maze description");
	}

	public void RestartMaze() {
		time = mazetime;
	}
	
	private PathItem pathfirst(LinkedList gec) {
		PathItem best = (PathItem)gec.get(0);
		for (int i=0; i<gec.size(); i++)
			if (((PathItem)gec.get(i)).getKey()<best.getKey())
				best = (PathItem)gec.get(i);
		return best;
	}
	
	/**
	 * Az A* algoritmus segítségével utat keres a labirintusban start és stop koordinátájú pontok között. A labirintusban
	 * nem engedélyezi a wraparoundot. Esetlegesen azzal volna bõvíthetõ, hogy meglehessen adni, csak bizonyos mélységig
	 * keressen utat.
	 *
	 * @author	VaVa
	 */
	public LinkedList PathFinder(Coordinates start, Coordinates stop) {
		LinkedList myset = new LinkedList();
		myset.add(new PathItem(start,(int)start.GetDistance(stop),0,null));

		clearBlocked();
		getFieldAt(start).getSetBlocked();
		PathItem min=null;
		// addig folyik a keresés, míg célba nem érünk, vagy el nem fogynak az üres mezõk
		while ((myset.size()>0) && !(min=(PathItem)pathfirst(myset)).getPos().SameField(stop)) {
//		while (((min=(PathItem)myset.first())!=null) && (!min.getPos().SameField(stop))) {
			// a halmazból kivett elemet ténylegesen ki is vesszük
			myset.remove(min);
			Coordinates minpos = min.getPos();
			// a procedúra mind a négy irányra azonos
			if (isNeighbour(minpos,Coordinates.DIR_UP,false)) { // megvizsgáljuk hogy adott irányba lehet e haladni
				Coordinates temp = minpos.GetNeighbour(Coordinates.DIR_UP); // amnnyiben igen, akkor lekérdezzük ezeket a koordinátákat
				if (!getFieldAt(temp).getSetBlocked()) // ha még nem jártunk ezen a mezõn
					myset.add(new PathItem(temp,(int)temp.GetDistance(stop),min.getDist()+1,min)); // akkor felvesszük az új mezõt új adataival a kupacunkba
			}
			if (isNeighbour(minpos,Coordinates.DIR_DOWN,false)) { // vizsgálat lefele
				Coordinates temp = minpos.GetNeighbour(Coordinates.DIR_DOWN);
				if (!getFieldAt(temp).getSetBlocked())
					myset.add(new PathItem(temp,(int)temp.GetDistance(stop),min.getDist()+1,min));
			}
			if (isNeighbour(minpos,Coordinates.DIR_LEFT,false)) { // vizsgálat balra
				Coordinates temp = minpos.GetNeighbour(Coordinates.DIR_LEFT);
				if (!getFieldAt(temp).getSetBlocked())
					myset.add(new PathItem(temp,(int)temp.GetDistance(stop),min.getDist()+1,min));
			}
			if (isNeighbour(minpos,Coordinates.DIR_RIGHT,false)) { // vizsgálat jobbra
				Coordinates temp = minpos.GetNeighbour(Coordinates.DIR_RIGHT);
				if (!getFieldAt(temp).getSetBlocked())
					myset.add(new PathItem(temp,(int)temp.GetDistance(stop),min.getDist()+1,min));
			}
			min = null;
		}

		// amennyiben nemjutottunk el a stopba, vagy nem is létezik út, akkor null-t adunk vissza
		if (min==null)
			return null;
		
		LinkedList retValue = new LinkedList(); 

		while (min!=null) {
			retValue.addFirst(new Coordinates(min.getPos().getX(),min.getPos().getY()));
			//ln("" + min.getPos());
			min = min.getParent();
		}

		return retValue;
	}



	/* field handling */

	/**
	 * 
	 */
	public FieldItem getFieldAt(int x, int y) {
		return mazeData[mazeWidth*y + x];
	}
	public FieldItem getFieldAt(Coordinates pos) {
		return mazeData[mazeWidth*pos.getY() + pos.getX()];
	}

	/**
	 * Az útvonalkeresésnél használt eljárás, szerepe, hogy törölje a jelzõk listáját ami alapján megállapíthatjuk, hogy
	 * jártunk-e már az adott mezõn. Ez a módszer nem a legszebb, célszerû egy binfában letárolni a mezõk azonosítóját, amin
	 * már jártunk, és onnan keresgélni. Igaz ez a megoldás konstans idejû, az pedig log(n), viszont ennél kiinduláskor az
	 * egész lab területére törölni kell...<br>
	 * Mezõk egyértelmû azonosítására alkalmazható az a BSZ szagú módszer, amikor az origóból kiindulva a ferde szakaszokon
	 * megyünk mindíg végig. vagyis tkp úgy kell elképzelni, mintha a koordinátarendszert 45 fokkal óramut járásával megegyezõen
	 * elforgatná az ember, az így kapott sorokon balról jobbra, fentrõl le számozunk. Egy sor az x+y=const egyenletet kielégítõ
	 * pontok halmaza. A sorszámot az (x+y)*(x+y-1)/2+x = (x^2+y^2+2xy-x-y)/2 + x = (x^2+y^2+2xy+x-y)/2 --> (x+y)^2+x-y képlet
	 * adja.
	 * @author VaVa
	 */
	public void clearBlocked() {
		for (int i=0; i<mazeWidth*mazeHeight; i++)
			mazeData[i].clearBlocked();
	}

	/**
	 * Eldönti hogy egy megadott koordinátájú mezû valamely oldani szomszédja elérhetõ-e. Ez attól is függhet, hogy megengedjük-e
	 * a warparoundot
	 * @author VaVa
	 */
	public boolean isNeighbour(Coordinates pos, int direction, boolean warp) {
		return isNeighbour(pos.getX(), pos.getY(), direction, warp);
	}

	/**
	 * Eldönti hogy egy megadott koordinátájú mezû valamely oldani szomszédja elérhetõ-e. Ez attól is függhet, hogy megengedjük-e
	 * a warparoundot
	 * @author VaVa
	 */
	public boolean isNeighbour(int x, int y, int direction, boolean warp) {
		if (!warp) {
			if (x==0 && direction == Coordinates.DIR_LEFT)
				return false;
			else if (x==mazeWidth-1 && direction==Coordinates.DIR_RIGHT)
				return false;
			else if (y==0 && direction==Coordinates.DIR_UP)
				return false;
			else if (y==mazeHeight-1 && direction==Coordinates.DIR_DOWN)
				return false;
		}

		FieldItem temp = getFieldAt(x, y);
		if (temp != null)
			return !temp.isWall(direction);
		else
			return false;
	}

	/* parameters */
	public Coordinates GetRebornPlace() {
		return monsterBirthPlace;
	}

	public void IncreaseBombs (int count) {
		pacman.IncreaseBombs(count);
	}

	public void IncreaseScore (long count) {
		parentGame.IncreaseScore(count);
	}

	public void IncreaseTime (long count) {
		time += count;
	}

	public Monster GetMonster (int number) {
		for (int i=0; i < monsters.size(); i++)
			if (((Monster)monsters.get(i)).GetItemNumber() == number)
				return (Monster)monsters.get(i);
		return null;
	}

	public Bonus GetBonus (int number) {
		for (int i=0; i < bonuses.size(); i++)
			if (((Bonus)bonuses.get(i)).GetItemNumber() == number)
				return (Bonus)bonuses.get(i);
		return null;
	}

	public Bomb GetBomb (int number) {
		for (int i=0; i < bombs.size(); i++)
			if (((Bomb)bombs.get(i)).GetItemNumber() == number)
				return (Bomb)bombs.get(i);
		return null;
	}

	public Crystal GetCrystal (int number) {
		for (int i=0; i < crystals.size(); i++)
			if (((Crystal)crystals.get(i)).GetItemNumber() == number)
				return (Crystal)crystals.get(i);
		return null;
	}

	public MazeItem GetItem (int number) {
		MazeItem item;

		if ((item = GetMonster(number)) != null ||
		    (item = GetBonus(number)) != null ||
			(item = GetBomb(number)) != null ||
			(item = GetCrystal(number)) != null) {
			return item;
		}

		return null;
	}


	/* commands */

	public void PutBomb() {
		pacman.PutBomb();
	}


	public void SetDirection (int dir) {
		pacman.SetDirection (dir);
	}

	/**
	 * Végrehajt egy szimulációs lépést. Csökkenti a hátralevo idot: Végighívja a pályaelemek
	 * {@link MazeItem#Act()} metódusait. Ellenorzi a pályaelemek ütközését.
	 * Lekezeli a bónuszok felvételét, a PacMan halálát, az ido lejártát, a pálya teljesítését.
	 *
	 * @throws EndGameException		Ha valami miatt véget ér a játék.
	 * @throws NextLevelException	Ha a játékos teljesítette az adott pályát.
	 */
	void Step() throws EndGameException, NextLevelException, LifeLostException {
		if (--time <= 0) {
			System.out.println("timed out");
			throw new EndGameException();
		}		

		// call Act() methods
		pacman.Act();

		// create a bomb if pacman has put one down
		if (pacman.GetBombPutState()) {
			for (int i = 0; i < bombs.size(); i++) {
				if (((Bomb)bombs.get(i)).GetCoordinates().SameField(pacman.GetCoordinates())) {
					pacman.SetBombPutState(false);
				}
			}
			
			if (pacman.GetBombPutState()) {
				bombs.add(new Bomb(this, new Coordinates(pacman.GetCoordinates()), 100));
				pacman.SetBombPutState(false);
			}
		}

		int i;
		Bomb b;

		/*
		 * Bombák aktiválása
		 */
		for (i = 0; i < bombs.size(); i++) {
			b = (Bomb)bombs.get(i);
			if (!b.isActive()) {
				if (!CheckCollision(pacman, b)) {
					b.Activate();
				}
			}
		}

		/*
		 * Act() hívás
		 */
		for (i = 0; i < monsters.size(); i++)
			((Monster)(monsters.get(i))).Act();

		for (i = 0; i < bonuses.size(); i++)
			if (!((Bonus)(bonuses.get(i))).Act()) {
				bonuses.remove(i);
			}

		for (i = 0; i < bombs.size(); i++)
			if (!((Bomb)(bombs.get(i))).Act()) {
				bombs.remove(i);
			}

		for (i = 0; i < crystals.size(); i++)
			((Crystal)(crystals.get(i))).Act();

		// Ütközések ellenorzése
		CheckCollisions();

		// Bónuszok véletlenszeru megjelenítése
		PutBonuses();
	}

	/**
	 * Kirajzolja a labirintust, ascii karakterekkel.
	 */
	public void ShowMaze() {
		String line;
		for (int y = 0; y < mazeHeight; y++) {
			line = "";
			for (int x = 0; x < mazeWidth; x++) {
				line += getFieldAt(x, y).Show(0);
			}
			System.out.println (line);

			line = "";
			for (int x = 0; x < mazeWidth; x++) {
				line += getFieldAt(x, y).Show(1);
			}
			System.out.println (line);

			line = "";
			for (int x = 0; x < mazeWidth; x++) {
				line += getFieldAt(x, y).Show(2);
			}
			System.out.println (line);
		}
	}

	void KillPacman() throws EndGameException, LifeLostException {
		pacman = new PacMan(this, new Coordinates (pacmanBirthPlace), pacmanSpeed);
		for (int i = 0; i < monsters.size(); i++)
			((Monster)monsters.get(i)).Reborn();

		while (bonuses.size() > 0)
			bonuses.removeFirst();

		while (bombs.size() > 0)
			bombs.removeFirst();

		throw new LifeLostException();
	}

	/**
	 * Ütközések ellenorzése. A PacMan-t minden pályaelemmel, a szörnyeket pedig a
	 * bombákkal kapcsolatban vizsgálja.
	 *
	 * @throws	EndGameException	Ha a PacMan meghalt (szörnnyel ill. élesített bombával ütközött)
	 * @throws	NextLevelException	Ha elfogytak a gyémántok.
	 */
	void CheckCollisions() throws EndGameException, NextLevelException, LifeLostException {
		int i;

		for (i = 0; i < monsters.size(); i++)
			if (((Monster)monsters.get(i)).isActive() && 
				CheckCollision(pacman, ((MazeItem)monsters.get(i)))) {
				KillPacman();
			}

		for (i = 0; i < bombs.size(); i++)
			if (((Bomb)bombs.get(i)).isActive() &&
				CheckCollision(pacman, ((MazeItem)bombs.get(i)))) {
				KillPacman();
			}

		for (i = 0; i < bonuses.size(); i++)
			if (CheckCollision(pacman, ((MazeItem)bonuses.get(i)))) {
				((Bonus)bonuses.get(i)).PickUp();
				bonuses.remove(i);
			}

		for (i = 0; i < crystals.size(); i++)
			if (CheckCollision(pacman, ((MazeItem)crystals.get(i)))) {
				((Crystal)crystals.get(i)).PickUp();
				crystals.remove(i);
				if (crystals.isEmpty())
					throw new NextLevelException();				
			}

		for (i = 0; i < monsters.size(); i++)
			for (int j = 0; j < bombs.size(); j++)
				if (((Monster)monsters.get(i)).isActive() &&
					((Bomb)bombs.get(j)).isActive() &&
					CheckCollision((MazeItem)monsters.get(i), (MazeItem)bombs.get(j))) {
					bombs.remove(j);
					j--;
					((Monster)monsters.get(i)).Deactivate((long)(100 + Math.random() * 50));
				}
	}
	
	/**
	 * Egy ütközés tesztelése. A és B pályaelem ütközik, ha a köztük lévo távolság kisebb,
	 * mint sugaraik összege.
	 *
	 * @param a		A master pályaelem.
	 * @param b		A slave pályaelem.
	 * @return		True értékkel tér vissza, ha a két pályaelem ütközik.
	 */
	boolean CheckCollision(MazeItem a, MazeItem b) {
		return a.GetCoordinates().CloserThan(b.GetCoordinates(), a.GetR() + b.GetR());
	}

	/**
	 * Véletlenszerûen megjelenõ bónuszok elhejezése a pályán.
	 */
	void PutBonuses() {
		if (debugAutoBonuses) {
			Bonus b = null;
			Coordinates pos = new Coordinates (Math.random() * mazeWidth, Math.random() * mazeHeight);
			pos.CenterOnField();
			long timeToLive = 40*4 + (long)(Math.random() * 40*2);

			if (bonusProbability * Math.random() < 1) {
				int whichBonus = (int)(((double)Math.random()) * 3);
				switch (whichBonus) {
				case 0 :
					b = new BombBonus (this, pos, timeToLive, (int)(1 + Math.random()*2));
					break;
				case 1 :
					b = new TimeBonus (this, pos, timeToLive, (long)(40 + Math.random()*40));
					break;
				case 2 :
					b = new ScoreBonus (this, pos, timeToLive, 50 + (long)(Math.random()*6)*10);
					break;
				}
				bonuses.add (b);
			}
		}		
	}

	public void paint(Graphics2D g, ImageCache cache) {
		Image bg = cache.getBG();
		g.setClip(0, 0, mazeWidth * CELLSIZE, mazeHeight * CELLSIZE);
		g.drawImage(bg, 0, 0, mazeWidth * CELLSIZE, mazeHeight * CELLSIZE, observer);
		paintItems(g, cache);
	}

	public void paintItems(Graphics2D g, ImageCache cache) {
		int i;

		for (i = 0; i < bonuses.size(); i++)
			((MazeItem)bonuses.get(i)).paint(g, cache);
		for (i = 0; i < bombs.size(); i++)
			((MazeItem)bombs.get(i)).paint(g, cache);
		for (i = 0; i < crystals.size(); i++)
			((MazeItem)crystals.get(i)).paint(g, cache);
		pacman.paint(g, cache);
		for (i = 0; i < monsters.size(); i++)
			((MazeItem)monsters.get(i)).paint(g, cache);

	}
}


/**
 * A labirintus egy mezõjének adatait tárolja.
 * a konstansok használata csúnya. célszerû lenne majd bugfixelni..
 *
 * @author VaVa
 */
class FieldItem {
	private boolean[] wall;
	private boolean blocked;

// mezõ falainak inicializálása, alapértelmezés szerint sehol sincs fal
	FieldItem() {
		wall = new boolean[4];
		for (int i=0; i<=3; i++)
			wall[i]=false;
	}

// mezõ falainak inicializálása Stringbõl. amennyiben a string rövidebb a maradék helyen nem lesz fal. Ahol 0 van a stringben
// ott neml lesz fal, ahol bármi más, ott lesz.
	FieldItem(String s) {
		this();
//		wall = new boolean[4];
		for (int i=0; i<=3 && i<s.length(); i++)
			wall[i]=s.charAt(i)=='0'?false:true;
	}

// blokkoltságjelzõ törlése
	public void clearBlocked() {
		blocked = false;
	}

// lekérdezzük és beállítjuk egyidõben a blokkoltságjelzõt.
	public boolean getSetBlocked() {
		boolean temp = blocked;
		blocked = true;
		return temp;
	}

// megadja adott irányban van-e fal.
	boolean isWall(int direction) {
		return wall[direction]; // lehal jól ha nem normális paramétert kap... ehhe
	}

// beállítja a falat
	void setWall(int direction) {
		wall[direction] = true;
//		checkBlocked();
	}

// törli a falat adott irányban.
	void clearWall(int direction) {
		wall[direction] = false;
//		checkBlocked();
	}

// átkapcsolja a falat.
	void switchWall(int direction) {
		wall[direction] = !wall[direction];
//		checkBlocked();
	}

/*	private void checkBlocked() {
		blocked = 
			wall[Coordinates.DIR_UP] && 
			wall[Coordinates.DIR_DOWN] && 
			wall[Coordinates.DIR_LEFT] && 
			wall[Coordinates.DIR_RIGHT];
	}*/

	/**
	 * Lekérdezi hogy blokkolva van-e a fal. getBlocked jobb volna. szóval ez így deprecated.
	 *
	 * @deprecated	mint állat.
	 */
	public boolean isBlocked() {
		return blocked;
	}

	/**
	 * A falat leíró string
	 *
	 * @return	A falat leíró String.
	 */
	public String toString() {
		return	(wall[Coordinates.DIR_UP]?"1":"0") + 
				(wall[Coordinates.DIR_RIGHT]?"1":"0") + 
				(wall[Coordinates.DIR_DOWN]?"1":"0") + 
				(wall[Coordinates.DIR_LEFT]?"1":"0");
	}

	public String Show(int line) {
		switch (line) {
		case 0 :
			return
				(isWall(Coordinates.DIR_LEFT) || isWall(Coordinates.DIR_UP)?"+":" ") +
			    (isWall(Coordinates.DIR_UP)?"-":" ") +
				(isWall(Coordinates.DIR_RIGHT) || isWall(Coordinates.DIR_UP)?"+":" ");
		case 1 :
			return
				(isWall(Coordinates.DIR_LEFT)?"|":" ") +
			    " " +
				(isWall(Coordinates.DIR_RIGHT)?"|":" ");
		case 2 :
			return
				(isWall(Coordinates.DIR_LEFT) || isWall(Coordinates.DIR_DOWN)?"+":" ") +
			    (isWall(Coordinates.DIR_DOWN)?"-":" ") +
				(isWall(Coordinates.DIR_RIGHT) || isWall(Coordinates.DIR_DOWN)?"+":" ");
		}
		return "";
	}
}


/**
 * PathFinder elemeinek rendezésére, definiálja, hogy PathItem két eleme között milyen reláció áll fent.
 *
 * @author VaVa
 */

class PathComparator extends Object implements Comparator {
	public int compare(Object o1, Object o2) {
		PathItem p1 = (PathItem)o1;
		PathItem p2 = (PathItem)o2;
		return p1.getKey()-p2.getKey();
	}
}

/**
 * PathFinder elemei. A keresett útvonal egy darabjáját tárolja.
 *
 * @author VaVa
 */

class PathItem extends Object {
	Coordinates pos;
	private int key; // kulcs
	private int dist; // távolság a céltól, becsült hátrelevõ út
	private int weight; // az út eddigi súlya
	PathItem parent; // a szülõ, hogy mozogni tudjunk a felépülõ fában
//	PathItem child; // nincs szükség a childra. a jelenlegi algoritmus mellett felesleges...

	PathItem(int x, int y, int weight, int dist, PathItem parent) {
		this(new Coordinates(x, y), weight, dist, parent);
	}

	PathItem(Coordinates pos, int weight, int dist, PathItem parent) {
		this.pos = pos;
		this.dist = dist;
		this.weight = weight;
		this.key = dist + weight;
		this.parent = parent;
//		child = null;
//		parent.setChild(this);
	}

	public int getKey() {
		return key;
	}

	public int getDist() {
		return dist;
	}

	public Coordinates getPos() {
		return pos;
	}

	public PathItem getParent() {
		return parent;
	}

//	public Coordinates getChild() {
//		return child;
//	}

//	public int setChild(PathItem child) {
//		this.child = child;
//	}
}

