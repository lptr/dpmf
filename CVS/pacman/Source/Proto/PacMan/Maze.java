// $Id: Maze.java,v 1.16 2001/04/27 13:41:41 lptr Exp $
// $Date: 2001/04/27 13:41:41 $
// $Author: lptr $

package PacMan;

import java.lang.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import Util.Parser.*;

/**
 * A j�t�kt�r labirintus r�sz�t megval�s�t� objektum. � kezeli a p�ly�n mozg� vagy �ll�
 * p�lyaelemeket - t�rben �s id�ben. Az � feladata az id� m�l�s�nak kezel�se is.<br>
 * A {@link Game} oszt�ly hozza l�tre.
 *
 * @author L�ci
 */
class Maze extends ProtoObject {

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
	 * A pacman sebess�ge.
	 */
	double pacmanSpeed;

	/**
	 * A pacman ezen a helyen sz�letik �jj�.
	 */
	Coordinates pacmanBirthPlace;

	/**
	 * A sz�rnyek ezen a helyen sz�letnek �jj�.
	 */
	Coordinates monsterBirthPlace;

	/**
	 * A labirintust fel�gyel� {@link Game} objektum
	 */
	Game parentGame;

	/**
	 * A p�lya teljes�t�s�hez rendelkez�sre �ll� tick-ek sz�ma
	 */
	long time;

	/**
	 * A p�lya sz�less�ge.
	 */
	protected int mazeWidth;

	/**
	 * A p�lya magass�ga.
	 */
	protected int mazeHeight;

	/**
	 * A p�lya mez�i.
	 */
	private FieldItem[] mazeData;

	/**
	 * A b�nuszok felbukkan�si val�sz�n�s�ge.
	 */
	private int bonusProbability;

	/**
	 * Automatikus b�nusz-elhelyez�s.
	 */
	protected boolean debugAutoBonuses = true;

	/**
	 * A sz�rnyek magukt�l mozognak-e.
	 */
	protected boolean debugAutoMove = true;


	/**
	 * L�trehozza a p�ly�t.
	 */
	Maze(Game _parentGame, String fileName) throws Exception {
		parentGame = _parentGame;
		Load(fileName);
	}
	
	/**
	 * Ki�r egy sor sz�veget - csak a /step parancs kiad�sa ut�n jelenik meg a kimeneten.
	 *
	 * @param   text  A ki�rand� sz�veg.
	 */
	void Output(String text) {
		parentGame.Output(text);
	}

	/**
	 * Ki�rja az �sszes mazeitem inform�ci�j�t.
	 *
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
	 * Bet�lti a fileName �ltal megjel�lt p�ly�t. Fel�p�ti a mez�ket tartalmaz� t�mb�t,
	 * l�trehozza a megfelel� sz�m� sz�rnyet, a PacMan-t, a krist�lyokat.
	 *
	 * @throws IOException Ha nem tudta megnyitni a p�ly�t tartalmaz� file-t.
	 */
	void Load(String fileName) throws Exception {
		Output("load " + fileName);
		parentGame.DumpOutput();

		InputStream is = new FileInputStream(fileName);
		ourParser parser = new ourParser(is);

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
				time = Long.parseLong(parser.lineTokens[1]);
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
					ThrowParseException("undefined maze size", parser.lineno());
				} else {
					int i;
					for (i=1; i<parser.lineTokens.length && mazeDataPos+i-1<mazeData.length; i++) {
						mazeData[mazeDataPos+i-1] = new FieldItem(parser.lineTokens[i]);
						if (parser.lineTokens[i].length()!=4)
							ThrowParseException("incorrect maze data", parser.lineno());
					}
					mazeDataPos+=i-1;
					if (i<parser.lineTokens.length)
						ThrowParseException("too much maze data", parser.lineno());
				}
			}
		}

		if (mazeWidth < 0 ||
			mazeHeight < 0 ||
			mazeData == null ||
			mazeDataPos < mazeWidth*mazeHeight ||
			pacman == null)
			throw new IOException("illegal or undefined maze description");
	}

	private PathItem pathfirst(LinkedList gec) {
		PathItem best = (PathItem)gec.get(0);
		for (int i=0; i<gec.size(); i++)
			if (((PathItem)gec.get(i)).getKey()<best.getKey())
				best = (PathItem)gec.get(i);
		return best;
	}
	
	/**
	 * Az A* algoritmus seg�ts�g�vel utat keres a labirintusban start �s stop koordin�t�j� pontok k�z�tt. A labirintusban
	 * nem enged�lyezi a wraparoundot. Esetlegesen azzal volna b�v�thet�, hogy meglehessen adni, csak bizonyos m�lys�gig
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
		// addig folyik a keres�s, m�g c�lba nem �r�nk, vagy el nem fogynak az �res mez�k
		while ((myset.size()>0) && !(min=(PathItem)pathfirst(myset)).getPos().SameField(stop)) {
//		while (((min=(PathItem)myset.first())!=null) && (!min.getPos().SameField(stop))) {
			// a halmazb�l kivett elemet t�nylegesen ki is vessz�k
			myset.remove(min);
			Coordinates minpos = min.getPos();
			// a proced�ra mind a n�gy ir�nyra azonos
			if (isNeighbour(minpos,Coordinates.DIR_UP,false)) { // megvizsg�ljuk hogy adott ir�nyba lehet e haladni
				Coordinates temp = minpos.GetNeighbour(Coordinates.DIR_UP); // amnnyiben igen, akkor lek�rdezz�k ezeket a koordin�t�kat
				if (!getFieldAt(temp).getSetBlocked()) // ha m�g nem j�rtunk ezen a mez�n
					myset.add(new PathItem(temp,(int)temp.GetDistance(stop),min.getDist()+1,min)); // akkor felvessz�k az �j mez�t �j adataival a kupacunkba
			}
			if (isNeighbour(minpos,Coordinates.DIR_DOWN,false)) { // vizsg�lat lefele
				Coordinates temp = minpos.GetNeighbour(Coordinates.DIR_DOWN);
				if (!getFieldAt(temp).getSetBlocked())
					myset.add(new PathItem(temp,(int)temp.GetDistance(stop),min.getDist()+1,min));
			}
			if (isNeighbour(minpos,Coordinates.DIR_LEFT,false)) { // vizsg�lat balra
				Coordinates temp = minpos.GetNeighbour(Coordinates.DIR_LEFT);
				if (!getFieldAt(temp).getSetBlocked())
					myset.add(new PathItem(temp,(int)temp.GetDistance(stop),min.getDist()+1,min));
			}
			if (isNeighbour(minpos,Coordinates.DIR_RIGHT,false)) { // vizsg�lat jobbra
				Coordinates temp = minpos.GetNeighbour(Coordinates.DIR_RIGHT);
				if (!getFieldAt(temp).getSetBlocked())
					myset.add(new PathItem(temp,(int)temp.GetDistance(stop),min.getDist()+1,min));
			}
			min = null;
		}

		// amennyiben nemjutottunk el a stopba, vagy nem is l�tezik �t, akkor null-t adunk vissza
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
	 * Az �tvonalkeres�sn�l haszn�lt elj�r�s, szerepe, hogy t�r�lje a jelz�k list�j�t ami alapj�n meg�llap�thatjuk, hogy
	 * j�rtunk-e m�r az adott mez�n. Ez a m�dszer nem a legszebb, c�lszer� egy binf�ban let�rolni a mez�k azonos�t�j�t, amin
	 * m�r j�rtunk, �s onnan keresg�lni. Igaz ez a megold�s konstans idej�, az pedig log(n), viszont enn�l kiindul�skor az
	 * eg�sz lab ter�let�re t�r�lni kell...<br>
	 * Mez�k egy�rtelm� azonos�t�s�ra alkalmazhat� az a BSZ szag� m�dszer, amikor az orig�b�l kiindulva a ferde szakaszokon
	 * megy�nk mind�g v�gig. vagyis tkp �gy kell elk�pzelni, mintha a koordin�tarendszert 45 fokkal �ramut j�r�s�val megegyez�en
	 * elforgatn� az ember, az �gy kapott sorokon balr�l jobbra, fentr�l le sz�mozunk. Egy sor az x+y=const egyenletet kiel�g�t�
	 * pontok halmaza. A sorsz�mot az (x+y)*(x+y-1)/2+x = (x^2+y^2+2xy-x-y)/2 + x = (x^2+y^2+2xy+x-y)/2 --> (x+y)^2+x-y k�plet
	 * adja.
	 * @author VaVa
	 */
	public void clearBlocked() {
		for (int i=0; i<mazeWidth*mazeHeight; i++)
			mazeData[i].clearBlocked();
	}

	/**
	 * Eld�nti hogy egy megadott koordin�t�j� mez� valamely oldani szomsz�dja el�rhet�-e. Ez att�l is f�gghet, hogy megengedj�k-e
	 * a warparoundot
	 * @author VaVa
	 */
	public boolean isNeighbour(Coordinates pos, int direction, boolean warp) {
		return isNeighbour(pos.getX(), pos.getY(), direction, warp);
	}

	/**
	 * Eld�nti hogy egy megadott koordin�t�j� mez� valamely oldani szomsz�dja el�rhet�-e. Ez att�l is f�gghet, hogy megengedj�k-e
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

	/**
	 * V�grehajt egy szimul�ci�s l�p�st. Cs�kkenti a h�tralevo idot: V�gigh�vja a p�lyaelemek
	 * {@link MazeItem#Act()} met�dusait. Ellenorzi a p�lyaelemek �tk�z�s�t.
	 * Lekezeli a b�nuszok felv�tel�t, a PacMan hal�l�t, az ido lej�rt�t, a p�lya teljes�t�s�t.
	 *
	 * @throws EndGameException		Ha valami miatt v�get �r a j�t�k.
	 * @throws NextLevelException	Ha a j�t�kos teljes�tette az adott p�ly�t.
	 */
	void Step() throws EndGameException, NextLevelException, LifeLostException {
		if (--time <= 0) {
			Output("event timeout");
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
		 * Bomb�k aktiv�l�sa
		 */
		for (i = 0; i < bombs.size(); i++) {
			b = (Bomb)bombs.get(i);
			if (!b.isActive()) {
				Output ("event faszom");
				if (!CheckCollision(pacman, b)) {
					Output ("event bomb " + b.GetItemNumber() + " activated " + b.GetCoordinates());
					b.Activate();
				}
			}
		}

		/*
		 * Act() h�v�s
		 */
		for (i = 0; i < monsters.size(); i++)
			((Monster)(monsters.get(i))).Act();

		for (i = 0; i < bonuses.size(); i++)
			if (!((Bonus)(bonuses.get(i))).Act()) {
				Output ("event bonus " + ((Bonus)bonuses.get(i)).GetItemNumber() + " died " + 
						((Bonus)bonuses.get(i)).GetCoordinates());
				bonuses.remove(i);
			}

		for (i = 0; i < bombs.size(); i++)
			if (!((Bomb)(bombs.get(i))).Act()) {
				Output ("event bomb " + ((Bomb)bombs.get(i)).GetItemNumber() + " died " + 
						((Bomb)bombs.get(i)).GetCoordinates());
				bombs.remove(i);
			}

		// �tk�z�sek ellenorz�se
		CheckCollisions();

		// B�nuszok v�letlenszeru megjelen�t�se
		PutBonuses();
	}

	/**
	 * Kirajzolja a labirintust, ascii karakterekkel.
	 */
	public void ShowMaze() {
		String line;
		Output ("/*");
		for (int y = 0; y < mazeHeight; y++) {
			line = "";
			for (int x = 0; x < mazeWidth; x++) {
				line += getFieldAt(x, y).Show(0);
			}
			Output (line);

			line = "";
			for (int x = 0; x < mazeWidth; x++) {
				line += getFieldAt(x, y).Show(1);
			}
			Output (line);

			line = "";
			for (int x = 0; x < mazeWidth; x++) {
				line += getFieldAt(x, y).Show(2);
			}
			Output (line);
		}
		Output ("*/");
	}

	void KillPacman() throws EndGameException, LifeLostException {
		if (--parentGame.lives == 0) {
			throw new EndGameException();
		}

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
	 * �tk�z�sek ellenorz�se. A PacMan-t minden p�lyaelemmel, a sz�rnyeket pedig a
	 * bomb�kkal kapcsolatban vizsg�lja.
	 *
	 * @throws	EndGameException	Ha a PacMan meghalt (sz�rnnyel ill. �les�tett bomb�val �tk�z�tt)
	 * @throws	NextLevelException	Ha elfogytak a gy�m�ntok.
	 */
	void CheckCollisions() throws EndGameException, NextLevelException, LifeLostException {
		int i;

		for (i = 0; i < monsters.size(); i++)
			if (((Monster)monsters.get(i)).isActive() && 
				CheckCollision(pacman, ((MazeItem)monsters.get(i)))) {
				Output ("event pacman " + pacman.GetItemNumber() + " died " + pacman.GetCoordinates());
				KillPacman();
			}

		for (i = 0; i < bombs.size(); i++)
			if (((Bomb)bombs.get(i)).isActive() &&
				CheckCollision(pacman, ((MazeItem)bombs.get(i)))) {
				Output ("event pacman " + pacman.GetItemNumber() + " died " + pacman.GetCoordinates());
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
					((Monster)monsters.get(i)).Deactivate(5);
				}
	}
	
	/**
	 * Egy �tk�z�s tesztel�se. A �s B p�lyaelem �tk�zik, ha a k�zt�k l�vo t�vols�g kisebb,
	 * mint sugaraik �sszege.
	 *
	 * @param a		A master p�lyaelem.
	 * @param b		A slave p�lyaelem.
	 * @return		True �rt�kkel t�r vissza, ha a k�t p�lyaelem �tk�zik.
	 */
	boolean CheckCollision(MazeItem a, MazeItem b) {
		return a.GetCoordinates().CloserThan(b.GetCoordinates(), a.GetR() + b.GetR());
	}

	/**
	 * V�letlenszeruen megjeleno b�nuszok elhejez�se a p�ly�n.
	 */
	void PutBonuses() {
		if (debugAutoBonuses) {
			Bonus b = null;
			Coordinates pos = new Coordinates (Math.random() * mazeWidth, Math.random() * mazeHeight);
			pos.CenterOnField();
			long timeToLive = 10 + (long)(Math.random() * 10);

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
}


/**
 * A labirintus egy mez�j�nek adatait t�rolja.
 * a konstansok haszn�lata cs�nya. c�lszer� lenne majd bugfixelni..
 *
 * @author VaVa
 */
class FieldItem {
	private boolean[] wall;
	private boolean blocked;

// mez� falainak inicializ�l�sa, alap�rtelmez�s szerint sehol sincs fal
	FieldItem() {
		wall = new boolean[4];
		for (int i=0; i<=3; i++)
			wall[i]=false;
	}

// mez� falainak inicializ�l�sa Stringb�l. amennyiben a string r�videbb a marad�k helyen nem lesz fal. Ahol 0 van a stringben
// ott neml lesz fal, ahol b�rmi m�s, ott lesz.
	FieldItem(String s) {
		this();
//		wall = new boolean[4];
		for (int i=0; i<=3 && i<s.length(); i++)
			wall[i]=s.charAt(i)=='0'?false:true;
	}

// blokkolts�gjelz� t�rl�se
	public void clearBlocked() {
		blocked = false;
	}

// lek�rdezz�k �s be�ll�tjuk egyid�ben a blokkolts�gjelz�t.
	public boolean getSetBlocked() {
		boolean temp = blocked;
		blocked = true;
		return temp;
	}

// megadja adott ir�nyban van-e fal.
	boolean isWall(int direction) {
		return wall[direction]; // lehal j�l ha nem norm�lis param�tert kap... ehhe
	}

// be�ll�tja a falat
	void setWall(int direction) {
		wall[direction] = true;
//		checkBlocked();
	}

// t�rli a falat adott ir�nyban.
	void clearWall(int direction) {
		wall[direction] = false;
//		checkBlocked();
	}

// �tkapcsolja a falat.
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
	 * Lek�rdezi hogy blokkolva van-e a fal. getBlocked jobb volna. sz�val ez �gy deprecated.
	 *
	 * @deprecated	mint �llat.
	 */
	public boolean isBlocked() {
		return blocked;
	}

	/**
	 * A falat le�r� string
	 *
	 * @return	A falat le�r� String.
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
 * PathFinder elemeinek rendez�s�re, defini�lja, hogy PathItem k�t eleme k�z�tt milyen rel�ci� �ll fent.
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
 * PathFinder elemei. A keresett �tvonal egy darabj�j�t t�rolja.
 *
 * @author VaVa
 */

class PathItem extends Object {
	Coordinates pos;
	private int key; // kulcs
	private int dist; // t�vols�g a c�lt�l, becs�lt h�trelev� �t
	private int weight; // az �t eddigi s�lya
	PathItem parent; // a sz�l�, hogy mozogni tudjunk a fel�p�l� f�ban
//	PathItem child; // nincs sz�ks�g a childra. a jelenlegi algoritmus mellett felesleges...

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

