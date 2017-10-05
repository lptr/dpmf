package PacMan;

import java.lang.*;
import java.io.*;
import java.util.*;
import java.awt.*;

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
		pacman = new PacMan(this, new Coordinates(20, 20), 9);

		Println ("");
		Println ("--- monsters");
		monsters.add(new Monster(this, new Coordinates(40, 60), 7));
		monsters.add(new Monster(this, new Coordinates(20, 60), 7));

		Leave("Load", "");
	}
	
	boolean Step() {
		In ("Step", "");

		Println ("making active items Act()..");
		
		// call Act() methods
		Println ("--- act: pacman");
		pacman.Act();

		// create a bomb if pacman has put one down
		if (pacman.GetBombPutState()) {
			if (Ask("is there a bomb already? [y|n]").equalsIgnoreCase("y")) {
				Println ("so f@ck off..");
			} else {
				bombs.add(new Bomb(this, pacman.GetCoordinates(), 5, 2));
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

		Println ("");
		Println ("--- check collisions");

		for (i = 0; i < monsters.size(); i++)
			CheckCollision(pacman, ((MazeItem)(monsters.get(i))));

		for (i = 0; i < bombs.size(); i++)
			CheckCollision(pacman, ((MazeItem)(bombs.get(i))));

		for (i = 0; i < bonuses.size(); i++)
			CheckCollision(pacman, ((MazeItem)(bonuses.get(i))));

		Println ("");
		Println ("--- put bonuses");

		String ans = Ask ("what bonus do you want?", "none|bomb|time|score");
		if (!ans.equals("none")) {
			long xc = Long.parseLong ( Ask ("x coordinate?"));
			long yc = Long.parseLong ( Ask ("y coordinate?"));

			bonuses.add(new Bonus(this, new Coordinates (xc, yc), 10, 5));
		}

		// strictly for debug purposes
		if (Ask("show maze?", "no|yes").equalsIgnoreCase("yes")) {
			Frame f = new Frame("maze");
			Panel p = new Panel();
			f.add (p);

			Canvas c = new Canvas() {
				void paintObject (Graphics g, MazeItem o, Color color) {
					Coordinates c = o.GetCoordinates();
					int r = o.GetR();

					g.setColor(color);
					g.drawOval (((int)(c.x-r)), ((int)(c.y-r)), 2*r, 2*r);
					g.setColor(Color.black);
				}

				public void paint(Graphics g) {
					Rectangle rect = g.getClipBounds();
					g.clearRect (0, 0, rect.width, rect.height);

					paintObject(g, ((MazeItem)(pacman)), Color.blue);

					int i;
					for (i=0; i < monsters.size(); i++)
						paintObject(g, ((MazeItem)(monsters.get(i))), Color.red);
				}
			};

			c.setSize(120, 120);
			p.add(c);
			f.pack();
			f.setSize(140, 140);
			f.show();
		}

		boolean cont = true;
		if (Ask("one more turn?", "yes|no").equalsIgnoreCase("no"))
			cont = false;
		
		Leave ("Step", "" + cont);
		return cont;
	}

	void CheckCollision(MazeItem a, MazeItem b) {
		In ("CheckCollision", "" + a + " - " + b);

		if (Ask("does " + a + " collide with " + b + "?", "yes|no").equalsIgnoreCase("yes")) {
			Println("good.");
		} else {
			Println("bad.");
		}

		Leave ("CheckCollision", "");
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
	
	public Coordinates GetCoordinates() {
		In ("GetCoordinates", "");
		Leave ("GetCoordinates", "" + pos);
		return pos;
	}

	public int GetR() {
		In ("GetR", "");
		Leave ("GetR", "" + radius);
		return radius;
	}

	// Skeleton model method - says where the item is located
	void Where() {
		Println ("<*> " + this + " is @ " + pos + " - r: " + radius);
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
	
	public void SetBombPutState(boolean state) {
		In ("SetBombPutState", "");

		putbomb = state;
		Println ("putbomb state set to " + putbomb + ".");
		
		Leave ("SetBombPutState", "");
	}

	public boolean GetBombPutState() {
		In ("GetBombPutState", "");
		Leave ("GetBombPutState", "" + putbomb);
		return putbomb;
	}
	
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