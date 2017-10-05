package PacMan;

import java.lang.*;
import java.io.*;
import java.util.*;

class Maze extends SkeletonObject {
	PacMan pacman;
	LinkedList monsters = new LinkedList();
	LinkedList bonuses = new LinkedList();
	LinkedList bombs = new LinkedList();
	LinkedList crystals = new LinkedList();
	Game parentGame;
	int time = 0;

	Maze(Game _parentGame, int levelNum) {
		parentGame = _parentGame;
		try {
			Load(levelNum);
		} catch (IOException e) {
			Error ("see bellow", e);
		}
		
		Println ("");
	}
	
	void Load(int levelNum) throws IOException {
		In("Load", "");
		
		Println ("loading level " + levelNum);

		Println ("loading mazeitems:");

		Println ("");
		Println ("--- pacman");
		pacman = new PacMan(this, new Coordinates(2, 2), 20);

		Println ("");
		Println ("--- monsters");
		monsters.add(new Monster(this, new Coordinates(4, 5), 18));
		monsters.add(new Monster(this, new Coordinates(2, 5), 18));

		// Create MazeItems
		// Create monsters
//		throw new IOException("couldn't open x.dat (level "  + levelNum + ")");

		Leave("Load", "");
	}
	
	boolean Step() {
		In ("Step", "");

		int i;
		
		Println ("making active items Act()..");
		
		// call Act() methods
		Println ("--- act: pacman");
		pacman.Act();

		Println ("--- act: monsters");
		for (i = 0; i < monsters.size(); i++)
			((Monster)(monsters.get(i))).Act();

		Println ("--- act: bonuses");
		for (i = 0; i < bonuses.size(); i++)
			((Bonus)(bonuses.get(i))).Act();

		Println ("--- act: bombs");
		for (i = 0; i < bombs.size(); i++)
			((Bomb)(bombs.get(i))).Act();

		Println ("--- act: crystals");
		for (i = 0; i < crystals.size(); i++)
			((Crystal)(crystals.get(i))).Act();
		
		boolean cont = true;

		if (Ask("one more turn?").equalsIgnoreCase("n"))
			cont = false;
		
		Leave ("Step", "" + cont);
		return cont;
	}
}

class MazeItem extends SkeletonObject {
	Maze currentMaze;
	Coordinates pos;
	int radius;

	MazeItem(Maze _currentMaze, Coordinates _pos, int _radius) {
		currentMaze = _currentMaze;
		pos = _pos;
		radius = _radius;
		Where();
	}
	
	public boolean Act() {
		In ("Act", "");
		
		// Act!
		Where();

		Leave ("Act", "" + true);		
		return true;
	}	
	
	// Skeleton model method - says where the item is located
	void Where() {
		Println ("<*> " + this + " is @ " + pos.x + "," + pos.y + " - r: " + radius);
	} 
}

class ActiveMazeItem extends MazeItem {
	int direction;
	double speed;
	
	ActiveMazeItem(Maze _currentMaze, Coordinates _pos, int _radius) {
		super (_currentMaze, _pos, _radius);
	}
}

class PacMan extends ActiveMazeItem {
	boolean putbomb = false;
	int bombs = 0;
	
	PacMan(Maze _currentMaze, Coordinates _pos, int _radius) {
		super (_currentMaze, _pos, _radius);
	}
	
	public void SetBomb(boolean state) {
		putbomb = state;
		Println ("Putbomb state set to " + putbomb + ".");
	}
	
	public boolean Act() {
		In ("Act", "");

		super.Act();

		Ask ("micsinajjon a pacman?");
		
		Leave("Act", "" + true);
		return true;
	}
}

class Monster extends ActiveMazeItem {
	boolean active;
	
	public Monster(Maze _currentMaze, Coordinates _pos, int _radius) {
		super (_currentMaze, _pos, _radius);
		active = true;
	}
	
	public void SetActive(boolean state) {
		In ("SetActive", "");
		active = state;
		Leave ("SetActive", "");
	}
	
	public boolean isActive() {
		In ("SetActive", "");
		Leave ("SetActive", "" + active);

		return active;
	}
}

class TimedMazeItem extends MazeItem {
	long timeToLive;
	
	TimedMazeItem (Maze _currentMaze, Coordinates _pos, int _radius, long _timeToLive) {
		super (_currentMaze, _pos, _radius);
		timeToLive = _timeToLive;
		Println (this + " has " + timeToLive + " to live.");
	}
	
	long GetTimeToLive() {
		In ("GetTimeToLive", "");

		Leave ("GetTimeToLive", "" + timeToLive);

		return timeToLive;
	}
	
	public boolean Act() {
		In("Act", "");

		super.Act();
		
		timeToLive--;
		Println ("time left to live is " + timeToLive);

		Leave("Act", "" + (timeToLive > 0));
		
		return timeToLive > 0;
	}
}

class Bomb extends TimedMazeItem {
	boolean active;
	
	Bomb(Maze _currentMaze, Coordinates _pos, int _radius, long _timeToLive) {
		super (_currentMaze, _pos, _radius, _timeToLive);
		Activate();
	}
	
	void Activate() {
		In ("Activate", "");
		active = true;
		Leave ("Activate", "");
	}
	
	boolean isActive() {
		In ("isActive", "");
		Leave ("isActive", "" + active);
		return active;
	}
}


class Bonus extends TimedMazeItem {

	Bonus(Maze _currentMaze, Coordinates _pos, int _radius, long _timeToLive) {
		super (_currentMaze, _pos, _radius, _timeToLive);
	}
}

class Crystal extends MazeItem {

	Crystal(Maze _currentMaze, Coordinates _pos, int _radius) {
		super (_currentMaze, _pos, _radius);
	}
}