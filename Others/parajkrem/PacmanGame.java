import java.util.Random;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.lang.*;
import java.awt.image.*;
import java.lang.Math;
import java.awt.font.*;
import java.awt.geom.*;


//-------------------------------	PacmanGame osztaly	---------------------------------------
public class PacmanGame {

	// Bezaras osztaly
	//----------------
	     class Bezaras extends WindowAdapter   {
	     public void windowClosing(WindowEvent e)   {
	          System.exit(0);
	     }
	}


	//-----------
	// Adattagok (PacmanGame)
	//-----------

	public Random r = new Random();		// Kozos munkavaltozo, a random fuggveny megvalositasahoz

	//	-	-	-	-	-	-	KONSTANSOK	-	-	-	-	-	-	-	-	-	-	-						|
	static final int SCREEN_X=670, SCREEN_Y=570;	// A kepernyo merete									|
	static final int LAB_X=25,LAB_Y=25;				// Hany elem lesz a kepernyon							|
	static final int KEZ_X=120, KEZ_Y=20;			// Honnan kezdodik a jatekter							|
	static final int K_X=(SCREEN_X-KEZ_X)/LAB_X,K_Y=(SCREEN_Y-KEZ_Y)/LAB_Y;	// Hany pixel lesz egy kocka	|
	//																										|
	static final int MONSTER_MAX = 5;		// Hany monster lehet osszesen a palyan egyszerre				|
	static final int DIAMOND_MAX = 10;		// Hany gyemant van												|
	static final int PRESENT_MAX = 7;		// Hany ajandek van												|
	static final int ACTIVEB_MAX = 5;		// Hany aktiv bomba van											|

	static final int DIAMOND_SCORE = 50;	// Mennyi pontot ad egy gyemant									|
	static final int BONUS_MAX = 500;		// Mennyi pontot adhat maximum egy bonusz						|

	static final int SPEED = 1;			// Ideiglenes hardver kesleletetes								|

	static final int LIVES_MAX = 5;			// A jatekos maximalis eleteinek szama							|
	static final int BOMB_MAX = 5;			// A jatekos maximalis bombainak szama							|

	static final int PRESENT_TIME = 200000;	// Az ajandekok ideje
	static final int ACTIVE_BOMB_TIME = 200000; // Az aktiv bombak ideje

	static final int MONSTER_PROB = 500;		// szornyek valoszinusege
	static final int PRESENT_PROB = 50;		// ajandekok valoszinusege
	static final int CLEVER_PROB = 35;			// okos
	static final int STUPID_PROB = 100;			// buta arany

	static final int PAC_TIME = 1;
	static final int MON_TIME = 2;
	static final int GEN_TIME = 2;

	static final int LOGO_X = 570;
	static final int LOGO_Y = 30;
	static final int LIVES_X = 555;
	static final int LIVES_Y = SCREEN_Y-100;
	static final int BOMBS_X = 555;
	static final int BOMBS_Y = SCREEN_Y-125;
	static final int SCORE_X = 560;
	static final int SCORE_Y = SCREEN_Y-300;

	static final int MENU1_X = 260;
	static final int MENU1_Y = 200;
	static final int MENU2_X = 260;
	static final int MENU2_Y = 240;
	static final int MENU3_X = 490;
	static final int MENU3_Y = 510;

	static final int LEVEL_MAX = 13;		// palyak szama

	static final int SCORE_LAB_X = 557;
	static final int SCORE_LAB_Y = 160;

	static final int SCORE_BOX_XK = 565;
	static final int SCORE_BOX_YK = 180;
	static final int SCORE_BOX_W = 	95;		// szelesseg
	static final int SCORE_BOX_H = 40;		// magassag

	static final int SCORE_NUM_X = 573;
	static final int SCORE_NUM_Y = 210;


	static final int LEVEL_LAB_X = 557;
	static final int LEVEL_LAB_Y = 270;

	static final int LEVEL_BOX_XK = 565;
	static final int LEVEL_BOX_YK = 290;
	static final int LEVEL_BOX_W = 	62;		// szelesseg
	static final int LEVEL_BOX_H = 40;		// magassag

	static final int LEVEL_NUM_X = 573;
	static final int LEVEL_NUM_Y = 320;

	static final int HIGHS_X = 135;
	static final int HIGHS_Y = 190;

	static final int HIGHS_XW = 300;		// A ket iras tavolsaga

	static final int HIGHS_YH = 32;		// Vizszintesen

	// Nevbekeres

	// field
	static final int FX = 300;
	static final int FY = 225;
	static final int FW = 90;
	static final int FH = 20;

	// button
	static final int BX = 300;
	static final int BY = 260;
	static final int BW = 90;
	static final int BH = 40;

	// text
	static final int TX = 300;
	static final int TY = 200;





	//-------------
	// Metodusok (PacmanGame)
	//-------------

	Main main;

	// main fuggveny
	//---------------
	public static void main(String args[]) {
		PacmanGame game = new PacmanGame();
	}

	// PacmanGame konstruktor
	//------------------
	public PacmanGame() {
		main = new Main();
		main.start();
	}

	public void keyLeft() {
		main.keyLeft();
	}

	public void keyRight() {
		main.keyRight();
	}

	public void keyUpp() {
		main.keyUp();
	}

	public void keyDownn() {
		main.keyDown();
	}

	public void keySpace() {
		main.keySpace();
	}

	public void keyEnter() {
		main.keyEnter();
	}

	public void keyEsc() {
		main.keyEsc();
	}




//-------------------------------	Main osztaly	---------------------------------------
class Main {

	//-----------
	// Adattagok (Main)
	//-----------
	String menuItems[];
	int activeMenuItem;
	NewGame theNewGame;
	Highscore theHighscore;
	Gfx graph;

	boolean isGame = false;
	int gameNumber = 0;		// 0 - menu, 1 - new game, 2 - high score
	int menuNumberAct;
	int menuNumberPrev;
	boolean menuEnter;
	boolean exitGame = false;



	//-------------
	// Metodusok (Main)
	//-------------

	public void start() {
		theNewGame = new NewGame();
		theHighscore = new Highscore();
		graph = new Gfx();
		graph.show();

		chooseMenu();
		System.exit(0);
	}

	public void paint() {};
	public void chooseMenu() {
		while (!exitGame) {
			graph.menuPaint();
			graph.menuItemPaint(1,2);
			isGame = false;
			gameNumber = 0;
			menuEnter = false;
			menuNumberAct = menuNumberPrev = 1;

			while (!menuEnter) {
			}
			switch (menuNumberAct) {
				case 1:
					isGame = true;
					gameNumber = 1;
					theNewGame.start();
					break;
				case 2:
					isGame = true;
					gameNumber = 2;
					theHighscore.start();
					break;
				case 3:
					exitGame = true;

			}
		}
	};


	public void placeName(int score) {		// Highscore-kommunikacio
		theHighscore.placeName(score);
	};

	public void keyLeft() {
		if (!isGame) {
		}
		else {
			switch(gameNumber) {
				case 1:
					theNewGame.keyLeft();
					break;
				case 2:
					break;
			}
		}
	}

	public void keyRight() {
		if (!isGame) {
		}
		else {
			switch(gameNumber) {
				case 1:
					theNewGame.keyRight();
					break;
				case 2:
					break;
			}
		}
	}

	public void keyUp() {
		if (!isGame) {
			if (menuNumberAct >1) {
				menuNumberAct--;
				graph.menuItemPaint(menuNumberAct,menuNumberPrev);
				menuNumberPrev = menuNumberAct;
			}

		}
		else {
			switch(gameNumber) {
				case 1:
					theNewGame.keyUp();
					break;
				case 2:
					break;
			}
		}
	}

	public void keyDown() {
		if (!isGame) {
			if (menuNumberAct <3) {
				menuNumberAct++;
				graph.menuItemPaint(menuNumberAct,menuNumberPrev);
				menuNumberPrev = menuNumberAct;
			}
		}
		else {
			switch(gameNumber) {
				case 1:
					theNewGame.keyDown();
					break;
				case 2:
					break;
			}
		}
	}

	public void keySpace() {
		if (!isGame) {
		}
		else {
			switch(gameNumber) {
				case 1:
					theNewGame.keySpace();
					break;
				case 2:
					break;
			}
		}
	}


	public void keyEnter() {
		if (!isGame) {
			menuEnter = true;
		}
		else {
			switch(gameNumber) {
				case 1:
					break;
				case 2:
					theHighscore.keyEnter();
					break;
			}
		}
	}

	public void keyEsc() {
		if (!isGame) {
		}
		else {
			switch(gameNumber) {
				case 1:
					theNewGame.keyEsc();
					break;
				case 2:
					theHighscore.keyEsc();
					break;
			}
		}
	}




//	-------------------------------		Highscore		--------------------------------------------------
class Highscore {
	String names[];
	int score[];
	boolean exit;

	public void load() {
		String s;
		names = new String[10];
		score = new int[10];
		try {
			LineNumberReader hfile = new LineNumberReader(new FileReader("highscores.dat"));
			for (int i=0;i<10;i++) {
				s = hfile.readLine();
				names[i] = s;
				s = hfile.readLine();
				score[i] = new Integer(s).intValue();
			}
			hfile.close();

		} catch (FileNotFoundException e) { System.out.println("File not found: highscores.dat"); System.exit(-1);}
		  catch (IOException e) { System.out.println("IO error (highscores.dat)"); System.exit(-1); }
	}


	public void save() {
		try {
			FileOutputStream outFilet = new FileOutputStream("highscores.dat");
			PrintWriter outFile = new PrintWriter(outFilet);

			for (int i=0;i<10;i++) {
				outFile.println(names[i]);
				outFile.println(Integer.toString(score[i]));
			}
			outFile.flush();
			outFile.close();

		} catch (FileNotFoundException e) { System.out.println("File not found: highscores.dat"); System.exit(-1);}
		  catch (IOException e) { System.out.println("IO error (highscores.dat)"); System.exit(-1); }
	}


	public void start() {
		load();
		exit = false;
		graph.generateGameStart();
		graph.highscorePaint();
		for (int i=0;i<10;i++)
			graph.namePaint(i,names[i],score[i]);
		graph.generateGameStop();
		while (!exit);
	};
	public void placeName(int actScore) {
		load();
		for (int i=0;i<10;i++)
			if (score[i] < actScore) {
				for (int j=i;j<9;j++) {
					score[9+i-j] = score[9+i-j-1];
					names[9+i-j] = names[9+i-j-1];
				}
				score[i] = actScore;
				names[i] = graph.getName();
				save();
				gameNumber = 2;
				start();
				gameNumber = 1;
				break;
			}
	};

	public void keyEnter() {
		exit = true;
	}
	public void keyEsc() {
		exit = true;
	}

}



//	-------------------------------		NewGame		--------------------------------------------------
class NewGame {

	//-----------
	// Adattagok (NewGame)
	//-----------

	int level;
	int numberOfDiamonds;
	int numberOfPresents;
	int numberOfMonsters;
	int numberOfActiveBombs;
	Pacman thePacman;
	Monster monsters[]; // 5 elemû
	Diamond diamonds[]; // 20 elemû
	Present presents[]; // 10 elemû
	StaticElement labyrinth[][];
	ActiveBomb activeBombs[];
	boolean esc=false;
	int labStat[][] = new int[LAB_X][LAB_Y];		// Falak es utak. 0 - fal, 1 - ut
	int labDin[][] = new int[LAB_X][LAB_Y];		// Ajandekok es gyemantok. 0 - semmi, 1 - gyemant, 2 - bonusz, 3 - elixir, 4 - passziv bomba, 5 - aktiv bomba
	int labMon[][] = new int[LAB_X][LAB_Y];		// 0 - semmi, 1 - okos szorny, 2 - buta szorny

	public Graphics g;

	int pacx,pacy;					// a labirintusban a pacman indulo koordinatai
	boolean pacStep, monStep, genStep;


	//-------------
	// Metodusok (NewGame)
	//-------------


	public void keyLeft() {
		if (thePacman != null) thePacman.left();
	}

	public void keyRight() {
		if (thePacman != null) thePacman.right();
	}

	public void keyUp() {
		if (thePacman != null) thePacman.up();
	}

	public void keyDown() {
		if (thePacman != null) thePacman.down();
	}

	public void keySpace() {
		if (thePacman != null) thePacman.placeBombEn();
	}

	public void keyEsc() {
		esc=true;
	}




	// start metodus
	//--------------
	public void start() {

		graph.generateGameStart();

		level = 1;
		numberOfDiamonds = 0;
		numberOfPresents = 0;
		numberOfMonsters = 0;
		numberOfActiveBombs = 0;
		esc = false;

     	graph.newGamePaint();

		thePacman = new Pacman(coordCalc(12,12));
		monsters = new Monster[MONSTER_MAX];
		diamonds = new Diamond[DIAMOND_MAX];
		presents = new Present[PRESENT_MAX];
		labyrinth = new StaticElement[LAB_X][LAB_Y];
		activeBombs = new ActiveBomb[ACTIVEB_MAX];

		graph.livesPaint(thePacman.getLives());
		graph.bombsPaint(thePacman.getBombNumber());

		generateLabyrinth();

		graph.generateGameStop();

		step();
	};


	//	step metodus
	//---------------
	public void step() {
		int oldScore=thePacman.getScore();
		int oldLives=thePacman.getLives();
		int oldBombs=thePacman.getBombNumber();

		Timer pacTimer = new Timer(PAC_TIME, new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent ae) {
				pacStep=true;
			}
		});
		pacTimer.start();

		Timer monTimer = new Timer(MON_TIME, new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent ae) {
				monStep=true;
			}
		});
		monTimer.start();

		Timer genTimer = new Timer(GEN_TIME, new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent ae) {
				genStep=true;
			}
		});
		genTimer.start();



		do {
			long sp=0;
			int i;
			do {
				if ( (sp++%SPEED)==0 ) {
				graph.restoreAll();
					thePacman.makeAStep();
					for (i=0;i<MONSTER_MAX;i++)
					if (monsters[i] != null) monsters[i].makeAStep();

				generatePresent();
				generateMonster();
				thePacman.placeBomb();
				meeting();

				if (thePacman == null) break;

				if (oldScore != thePacman.getScore()) {
					graph.scorePaint(thePacman.getScore());
					oldScore = thePacman.getScore();
				}

				if (thePacman.getLives() != oldLives) {
					oldLives = thePacman.getLives();
					graph.livesPaint(oldLives);

				}

				if (thePacman.getBombNumber() != oldBombs) {
					oldBombs = thePacman.getBombNumber();
					graph.bombsPaint(oldBombs);
				}

				for (i=0;i<PRESENT_MAX;i++)
					if (presents[i] != null) presents[i].countTime();
				for (i=0;i<ACTIVEB_MAX;i++)
					if (activeBombs[i] != null) activeBombs[i].countTime();


				graph.gamePaint();
			}

			} while ((numberOfDiamonds > 0) && (!esc));

			if (esc) break;
			if (thePacman == null) break;
			level++;
			resetLabyrinth();
			generateLabyrinth();
			thePacman.setLives(3);
			graph.levelPaint(level);
		} while (0==0);
		if (thePacman != null) placeName(thePacman.getScore());
	}



	// generateLabyrinth metodus
	//---------------------------
	public void generateLabyrinth() {
		String s;
		try {
			s = new String("lab"+Integer.toString((level-1)%LEVEL_MAX)+".dat");
			LineNumberReader labyrinthFile = new LineNumberReader(new FileReader(s));
			s = labyrinthFile.readLine();		// x koordinata
			pacx = new Integer(s).intValue();

			s = labyrinthFile.readLine();		// y koordinata
			pacy = new Integer(s).intValue();

			thePacman.setCoord(coordCalc(pacx,pacy));
			int i,j;																	// munkavaltozok

			for (i=0;i<LAB_Y;i++)
				for (j=0;j<LAB_X;j++) {
					if (labyrinthFile.read() == 'X') {
						labyrinth[i][j] = new Wall(coordCalc(j,i));
						labStat[i][j] = 0;
					}
					else {
						labyrinth[i][j] = new Path(coordCalc(j,i));
						labStat[i][j] = 1;
					}
					labDin[i][j] = 0;
				}
			labyrinthFile.close();
		} catch (FileNotFoundException e) { System.out.println("File not found: labyrinth.dat"); System.exit(-1);}
		  catch (IOException e) { System.out.println("IO error (labyrinth.dat)"); System.exit(-1); }

		int i,j;

		while (numberOfDiamonds < DIAMOND_MAX) {
			i = r.nextInt(LAB_Y);
			j = r.nextInt(LAB_X);
			if ((labStat[i][j] == 1) && (labDin[i][j] == 0)) {
				labDin[i][j] = 1;
				diamonds[numberOfDiamonds] = new Diamond(coordCalc(j,i),numberOfDiamonds);
				numberOfDiamonds++;
			}
		}
	}


	// resetLabyrinth metodus
	//------------------------
	public void resetLabyrinth() {
		int i;

		for (i=0;i<MONSTER_MAX;i++)
			if (monsters[i] != null)
				monsters[i].disactivate();


		for (i=0;i<PRESENT_MAX;i++)
			if (presents[i] != null)
				presents[i].disactivate();

		for (i=0;i<ACTIVEB_MAX;i++)
			if (activeBombs[i] != null)
				activeBombs[i].disactivate();

		numberOfPresents = 0;
		numberOfMonsters = 0;
		numberOfActiveBombs = 0;
		thePacman.setCoord(coordCalc(pacx,pacy));
	};


	// generatePresent metodus
	//-------------------------
	public void generatePresent() {
		int i,j,k;

		switch (r.nextInt(PRESENT_PROB)) {
			// bonusz
			case 0:	if (numberOfPresents < PRESENT_MAX) {
						do {
							i = r.nextInt(LAB_Y);
							j = r.nextInt(LAB_X);
						} while (!accPut(j,i));
						for (k=0;k<PRESENT_MAX;k++)
							if (presents[k] == null) break;
						presents[k] = new Bonus(coordCalc(j,i),k);
					}
					break;

			// elixir
			case 1:	if (numberOfPresents < PRESENT_MAX) {
						do {
							i = r.nextInt(LAB_Y);
							j = r.nextInt(LAB_X);
						} while (!accPut(j,i));
						for (k=0;k<PRESENT_MAX;k++)
							if (presents[k] == null) break;
						presents[k] = new Elixir(coordCalc(j,i),k);
					}
					break;
			// bomba
			case 2:	if (numberOfPresents < PRESENT_MAX) {
						do {
							i = r.nextInt(LAB_Y);
							j = r.nextInt(LAB_X);
						} while (!accPut(j,i));
						for (k=0;k<PRESENT_MAX;k++)
							if (presents[k] == null) break;
						presents[k] = new PassiveBomb(coordCalc(j,i),k);
					}
					break;
		}
	}


	// generateMonster metodus
	//------------------------
	public void generateMonster() {
		int i,j,k;

		i=r.nextInt(MONSTER_PROB);
			// okos
			if (i<CLEVER_PROB) {
						do {
							i = r.nextInt(LAB_Y);
							j = r.nextInt(LAB_X);
						} while (!accPut(j,i));
						for (k=0;k<MONSTER_MAX;k++)
							if (monsters[k] == null) break;
						if (k!=MONSTER_MAX)
							monsters[k] = new CleverMonster(coordCalc(j,i),k);
			}

			// buta
			else if ((i>CLEVER_PROB) && (i<STUPID_PROB+CLEVER_PROB)) {
						do {
							i = r.nextInt(LAB_Y);
							j = r.nextInt(LAB_X);
						} while (!accPut(j,i));
						for (k=0;k<MONSTER_MAX;k++)
							if (monsters[k] == null) break;
						if (k!=MONSTER_MAX)
							monsters[k] = new StupidMonster(coordCalc(j,i),k);
			}
	};




	// meeting metodus
	//-----------------
	public void meeting() {
		int i,j;
		Point pp,pm;
		pp = thePacman.getCoordLab();
		Point ppp,ppm;
		ppp = thePacman.getCoord();


		// pacman-gyemant
			for (i=0;i<DIAMOND_MAX;i++)
				if ((diamonds[i] != null) && (ppp.meet(diamonds[i].getCoord()))) {
		 			diamonds[i].effect();
		 			diamonds[i].disactivate();
		 			graph.pacmanMeet();
				}

		// pacman-bonusz
			for (i=0;i<PRESENT_MAX;i++)
				if ((presents[i] != null) && (ppp.meet(presents[i].getCoord()))) {
					ppm = presents[i].getCoordLab();
					if ((labDin[ppm.y][ppm.x] == 3) && (thePacman.getLives() == LIVES_MAX)) break;
					if ((labDin[ppm.y][ppm.x] == 4) && (thePacman.getBombNumber() == BOMB_MAX)) break;
					presents[i].effect();
					presents[i].disactivate();
		 			graph.pacmanMeet();
				}


		// pacman-aktiv bomba
			for (i=0;i<ACTIVEB_MAX;i++)
				if ((activeBombs[i] != null) && (pp.compareTo(activeBombs[i].getCoordLab()))) {
		 			graph.pacmanMeet();
					resetLabyrinth();
					thePacman.decLives();
					return;
				}

		// szorny - valami
		for (j=0;j<MONSTER_MAX;j++) {
			if (monsters[j] == null) continue;
			pm = monsters[j].getCoordLab();
			ppm = monsters[j].getCoord();

			// szorny - gyemant
				for (i=0;i<DIAMOND_MAX;i++)
					if ((diamonds[i] != null) && (ppm.meet(diamonds[i].getCoord()))) {
			 			diamonds[i].paint();
					}

			// szorny-ajandek
				for (i=0;i<PRESENT_MAX;i++)
					if ((presents[i] != null) && (ppm.meet(presents[i].getCoord()))) {
						presents[i].paint();
					}

			// szorny - pacman
			if (ppp.meet(ppm)) {
				thePacman.restore();
				resetLabyrinth();
				thePacman.decLives();
				return;
			}

			// szorny - aktiv bomba
				for (i=0;i<ACTIVEB_MAX;i++)
					if ((activeBombs[i] != null) && (ppm.meet(activeBombs[i].getCoord()))) {
						activeBombs[i].disactivate();
						monsters[j].disactivate();
					}
		}
	}



	public void placeActiveBomb(Point x) {
	};

	public void escPressed() {
	};
	public void keyPressed() {
	}

	public int getLevel() {
		return level;
	};
	public void endGame() {};


	//	-	-	-	-	-	-	-	-	 Segedmetodusok	-	-	-	-	-	-	-

	// coordCalc metodus
	//-------------------
	public Point coordCalc(int x, int y) {
		return new Point(KEZ_X+x*K_X+K_X/2,KEZ_Y+y*K_Y+K_Y/2);
	}
	public Point coordCalc(Point a) {
		return new Point(KEZ_X+a.x*K_X+K_X/2,KEZ_Y+a.y*K_Y+K_Y/2);
	}




	//	coordCheck metodus
	//--------------------

		//	Egy labirintusban levo mozgo elem (MovingElement) uj koordinatajanak vizsgalja labirintusbeli helyet.
		//	Ha az uj koordinata jo, azaz az elem lehet az uj helyen, akkor true ertekkel ter vissza, kulonben pedig false.
	public boolean coordCheck(Point p) {
		int lx,ly;	//	Melyik labirintuselem

		lx = (p.x-KEZ_X)/K_X;
		ly = (p.y-KEZ_Y)/K_Y;

		Point p2 = new Point(p.x,p.y);
		p2.x = (p.x-KEZ_X) % K_X;
		p2.y = (p.y-KEZ_Y) % K_Y;

		if (p2.x == K_X/2) {
			if (p2.y > K_Y/2) {
				if (ly >= LAB_Y-1) return false;
				if (labStat[ly+1][lx] == 1) return true;
				else return false;
			}
			if (p2.y < K_Y/2) {
				if (ly <= 0) return false;
				if (labStat[ly-1][lx] == 1) return true;
				else return false;
			}
			else return true;
		}
		if (p2.y == K_Y/2) {
			if (p2.x > K_X/2) {
				if (lx >= LAB_X-1) return false;
				if (labStat[ly][lx+1] == 1) return true;
				else return false;
			}
			if (p2.x < K_X/2) {
				if (lx <= 0) return false;
				if (labStat[ly][lx-1] == 1) return true;
				else return false;
			}
			else return true;
		}
	return false;
	}

	public boolean accPut(int x, int y) {
		if ((labStat[y][x] == 1) && (labDin[y][x] == 0)) {
			Point p=thePacman.getCoordLab();
			if ((p.x==x) && (p.y == y)) return false;
			// szornyek vegignezese
			return true;
		}
		return false;
	}



//	-------------------------------		Element		--------------------------------------------------
class Element {
	Point coord;
	Point coordLab;

	public Element(Point a) {
		coord = new Point(a.x,a.y);
		coordLabCalc();
	}

	public Point getCoord() {
		return coord;
	};

	public Point getCoordLab() {
		return coordLab;
	}

	public void setCoord(Point a) {
		coord=new Point(a.x,a.y);
		coordLabCalc();
	};

	public void paint() {};
	private void store() {};
	private void restore() {};



	//	-	-	-	-	-	Segedfuggvenyek	-	-	-	-	-


	protected void coordLabCalc() {
		coordLab = new Point((coord.x-KEZ_X)/K_X,(coord.y-KEZ_Y)/K_Y);
	}


}


//	-------------------------------		MovingElement		--------------------------------------------------
class MovingElement extends Element  {
	int direction;		// 0 - up, 1 - down, 2 - left, 3 - right, 4 - stop
	boolean active;

	public MovingElement(Point a) {
		super(a);
	}

	public void makeAStep() {
	};
	public void activate() {
		active = true;
	};
	public void disactivate() {
		active = false;
	};
	public boolean isActive() {
		return true;
	};

}


//	-------------------------------		StaticElement		--------------------------------------------------
class StaticElement extends Element {
	public StaticElement(Point a) {
		super(a);
	}

}



//	-------------------------------		Pacman		--------------------------------------------------
class Pacman extends MovingElement {
	int score;
	int lives;
	int bombNumber;
	int directionNew;
	boolean bombActive;
	Point oldcoord;

	public Pacman(Point a) {
		super(a);
		oldcoord = new Point(a.getX(), a.getY());
		lives = 3;
		direction = 4;
		directionNew = 4;
		score = 0;
		bombNumber = 0;
		bombActive=false;
		graph.pacmanCreate(coord.x,coord.y);
	}
	public void up() {directionNew = 0;};
	public void down() {directionNew = 1;};
	public void left() {directionNew = 2;};
	public void right() {directionNew = 3;};


	public void placeBombEn() {
		if (bombNumber > 0) bombActive=true;
	}

	public void placeBomb() {
		if (bombActive) {
			if (numberOfActiveBombs < ACTIVEB_MAX) {
				int i;
				for (i=0;i<ACTIVEB_MAX;i++)
					if (activeBombs[i] == null) break;
				switch (direction) {
					case 0:	if (accPut(coordLab.x,coordLab.y+1)) {
								activeBombs[i] = new ActiveBomb(coordCalc(coordLab.x,coordLab.y+1),i);
								bombNumber--;
							}
							break;
					case 1:	if  (accPut(coordLab.x,coordLab.y-1)) {
								activeBombs[i] = new ActiveBomb(coordCalc(coordLab.x,coordLab.y-1),i);
								bombNumber--;
							}
							break;
					case 2:	if (accPut(coordLab.x+1,coordLab.y)) {
								activeBombs[i] = new ActiveBomb(coordCalc(coordLab.x+1,coordLab.y),i);
								bombNumber--;
							}
							break;
					case 3:	if (accPut(coordLab.x-1,coordLab.y)) {
								activeBombs[i] = new ActiveBomb(coordCalc(coordLab.x-1,coordLab.y),i);
								bombNumber--;
							}
							break;
				}

				if (activeBombs[i] == null) {
					if ((accPut(coordLab.x,coordLab.y-1)) && (direction != 0)) {
						activeBombs[i] = new ActiveBomb(coordCalc(coordLab.x,coordLab.y-1),i);
						bombNumber--;
					}
					else if ((accPut(coordLab.x+1,coordLab.y)) && (direction != 3)) {
						activeBombs[i] = new ActiveBomb(coordCalc(coordLab.x+1,coordLab.y),i);
						bombNumber--;
					}
					else if ((accPut(coordLab.x,coordLab.y+1)) && (direction != 1)) {
						activeBombs[i] = new ActiveBomb(coordCalc(coordLab.x,coordLab.y+1),i);
						bombNumber--;
					}
					else if ((accPut(coordLab.x-1,coordLab.y)) && (direction != 2)) {
						activeBombs[i] = new ActiveBomb(coordCalc(coordLab.x-1,coordLab.y),i);
						bombNumber--;
					}
				}

			}
			bombActive = false;
		}
	}


	public void setLives(int x) {
		if ((x>=0) && (x<=LIVES_MAX))
			lives = x;
	};

	public int getLives() {
		return lives;
	};

	public void incLives() {
		if (lives < LIVES_MAX)
			lives++;
	};

	public void decLives() {
		if (lives > 0)
			lives--;
		if (lives == 0)
			death();
	};

	public void addScore(int x) {
		score += x;
	};

	public int getScore() {
		return score;
	};

	public void incBombNumber() {
		if (bombNumber < BOMB_MAX)
			bombNumber++;
	};

	public int getBombNumber() {
		return bombNumber;
	};

	public void death() {
		placeName(score);
		thePacman = null;
	};

	public void paint() {
		graph.pacmanPaint(coord.x,coord.y);
	};

	private void store() {};
	private void restore() {
	};

	public void makeAStep() {
		int directionNewC = directionNew;

		switch (directionNewC) {
			case 0:	coord.y--;
					if (coordCheck(coord)) direction = directionNewC;
					coord.y++;
					break;
			case 1:	coord.y++;
					if (coordCheck(coord)) direction = directionNewC;
					coord.y--;
					break;
			case 2:	coord.x--;
					if (coordCheck(coord)) direction = directionNewC;
					coord.x++;
					break;
			case 3:	coord.x++;
					if (coordCheck(coord)) direction = directionNewC;
					coord.x--;
					break;
		}

		switch (direction) {
			case 0:	coord.y--;
					if (!coordCheck(coord)) coord.y++;
					break;
			case 1:	coord.y++;
					if (!coordCheck(coord)) coord.y--;
					break;
			case 2:	coord.x--;
					if (!coordCheck(coord)) coord.x++;
					break;
			case 3:	coord.x++;
					if (!coordCheck(coord)) coord.x--;
					break;
		}

			paint();
			coordLabCalc();
			oldcoord = new Point(coord.x,coord.y);
	};

}



//	-------------------------------		Monster		--------------------------------------------------
class Monster extends MovingElement {
	int arrayIndex;
	public Monster(Point a,int ai) {
		super(a);
		arrayIndex = ai;
	}
	private void store() {};
	private void restore() {};
}


//	-------------------------------		CleverMonster		--------------------------------------------------
class CleverMonster extends Monster {
	int directionPrev = direction;

	public CleverMonster(Point a,int ai) {
		super(a,ai);
		direction = r.nextInt(4);
		labMon[coordLab.y][coordLab.x] = 2;
		numberOfMonsters++;
		graph.cleverMonsterCreate(coord.x,coord.y,ai);

	}

	public void makeAStep() {
		Point p = new Point(coord.x,coord.y);
		int i;
			Point h = new Point(coord.x,coord.y);
			int xkul, ykul;
			int dir[] = new int[4];

			Point g = thePacman.getCoordLab();
			xkul = coordLab.getX() - g.getX();
			ykul = coordLab.getY() - g.getY();

			if ( ((Math.abs(xkul) <= Math.abs(ykul))) && (ykul>0)) dir[0] = 0;
			else if ( ((Math.abs(xkul) <= Math.abs(ykul))) && (ykul<0)) dir[0] = 1;
			else if ( ((Math.abs(xkul) > Math.abs(ykul))) && (xkul>0)) dir[0] = 2;
			else if ( ((Math.abs(xkul) > Math.abs(ykul))) && (xkul<0)) dir[0] = 3;

			if ((dir[0] == 0) || (dir[0] == 1)) {
				if (xkul>0) dir[1] = 2;
			    else dir[1] = 3;
			}

			if ((dir[0] == 2) || (dir[0] == 3)) {
				if (ykul>0) dir[1] = 0;
				else dir[1] = 1;
			}

			if (dir[0] == 0) dir[2] = 1;
			else if (dir[0] == 1) dir[2] = 0;
			else if (dir[0] == 2) dir[2] = 3;
			else if (dir[0] == 3) dir[2] = 2;

			if (dir[1] == 0) dir[3] = 1;
			else if (dir[1] == 1) dir[3] = 0;
			else if (dir[1] == 2) dir[3] = 3;
			else if (dir[1] == 3) dir[3] = 2;

			i=0;
			do {
				p.x = coord.x;
				p.y = coord.y;
				switch(dir[i]) {
					case 0: p.y -= 1;
							break;
					case 1: p.y += 1;
							break;
					case 2: p.x -= 1;
							break;
					case 3: p.x += 1;
							break;
				}
				i++;
			} while (!coordCheck(p));
			i--;
			if ((directionPrev != 0) && (dir[i] == 1)) direction = 1;
			else if ((directionPrev != 1) && (dir[i] == 0)) direction = 0;
			else if ((directionPrev != 3) && (dir[i] == 2)) direction = 2;
			else if ((directionPrev != 2) && (dir[i] == 3)) direction = 3;


		p.x = coord.x;
		p.y = coord.y;
		switch(direction) {
			case 0: p.y -= 1;
					break;
			case 1: p.y += 1;
					break;
			case 2: p.x -= 1;
					break;
			case 3: p.x += 1;
					break;
		}

		if (!coordCheck(p)) {
			i=0;

			h = new Point(coord.x,coord.y);
			dir = new int[4];

			g = thePacman.getCoordLab();
			xkul = coordLab.getX() - g.getX();
			ykul = coordLab.getY() - g.getY();

			if ( ((Math.abs(xkul) <= Math.abs(ykul))) && (ykul>0)) dir[0] = 0;
			else if ( ((Math.abs(xkul) <= Math.abs(ykul))) && (ykul<0)) dir[0] = 1;
			else if ( ((Math.abs(xkul) > Math.abs(ykul))) && (xkul>0)) dir[0] = 2;
			else if ( ((Math.abs(xkul) > Math.abs(ykul))) && (xkul<0)) dir[0] = 3;

			if ((dir[0] == 0) || (dir[0] == 1)) {
				if (xkul>0) dir[1] = 2;
			    else dir[1] = 3;
			}

			if ((dir[0] == 2) || (dir[0] == 3)) {
				if (ykul>0) dir[1] = 0;
				else dir[1] = 1;
			}

			if (dir[0] == 0) dir[2] = 1;
			else if (dir[0] == 1) dir[2] = 0;
			else if (dir[0] == 2) dir[2] = 3;
			else if (dir[0] == 3) dir[2] = 2;

			if (dir[1] == 0) dir[3] = 1;
			else if (dir[1] == 1) dir[3] = 0;
			else if (dir[1] == 2) dir[3] = 3;
			else if (dir[1] == 3) dir[3] = 2;


			do {
				direction = dir[i];
				p.x = coord.x;
				p.y = coord.y;
				switch(dir[i]) {
					case 0: p.y -= 1;
							break;
					case 1: p.y += 1;
							break;
					case 2: p.x -= 1;
							break;
					case 3: p.x += 1;
							break;
				}
				i++;
			} while (!coordCheck(p));

		}

		directionPrev = direction;
		restore();
		coord.y = p.y;
		coord.x = p.x;
		coordLabCalc();
		paint();
	};

	public void disactivate() {
		graph.cleverMonsterDelete(coord.x,coord.y,arrayIndex);
		numberOfMonsters--;
		labMon[coordLab.y][coordLab.x] = 0;
		monsters[arrayIndex] = null;
	}

	public void paint() {
		graph.cleverMonsterPaint(coord.x,coord.y,arrayIndex);

	};
	private void store() {};
	private void restore() {
	};
}


//	-------------------------------		StupidMonster		--------------------------------------------------
class StupidMonster extends Monster {
	public StupidMonster(Point a,int ai) {
		super(a,ai);
		direction = r.nextInt(4);
		labMon[coordLab.y][coordLab.x] = 2;
		numberOfMonsters++;
		graph.stupidMonsterCreate(coord.x,coord.y,ai);
	}

	public void makeAStep() {
		Point p = new Point(coord.x,coord.y);
		int i;

		p.x = coord.x;
		p.y = coord.y;
		switch(direction) {
			case 0: p.y -= 1;
					break;
			case 1: p.y += 1;
					break;
			case 2: p.x -= 1;
					break;
			case 3: p.x += 1;
					break;
		}
		if (!coordCheck(p)) {
			do {
				p.x = coord.x;
				p.y = coord.y;

				direction = r.nextInt(4);
				switch(direction) {
					case 0: p.y -= 1;
							break;
					case 1: p.y += 1;
							break;
					case 2: p.x -= 1;
							break;
					case 3: p.x += 1;
							break;
				}
			} while (!coordCheck(p));
		}

		restore();
		coord.y = p.y;
		coord.x = p.x;
		coordLabCalc();
		paint();
	};


	public void disactivate() {
		graph.stupidMonsterDelete(coord.x,coord.y,arrayIndex);
		restore();
		numberOfMonsters--;
		labMon[coordLab.y][coordLab.x] = 0;
		monsters[arrayIndex] = null;
	}

	public void paint() {
		graph.stupidMonsterPaint(coord.x,coord.y,arrayIndex);
	};
	private void store() {};
	private void restore() {
	};
}



//	-------------------------------		Wall		--------------------------------------------------
class Wall extends StaticElement {
	public Wall(Point a) {
		super(a);
		graph.wallCreate(coordLab.x,coordLab.y);
	}

	public void paint() {
	};
}



//	-------------------------------		Path		--------------------------------------------------
class Path extends StaticElement {
	public Path(Point a) {
		super(a);
		graph.pathCreate(coordLab.x,coordLab.y);
	}

	public void paint() {
	};
}



//	-------------------------------		Diamond		--------------------------------------------------
class Diamond extends StaticElement {
	int arrayIndex;

	public Diamond(Point a,int ai) {
		super(a);
		arrayIndex = ai;
		labDin[coordLab.y][coordLab.x] = 1;
		graph.diamondCreate(coordLab.x,coordLab.y);
	}
	public void paint() {
	};

	public void disactivate() {
		graph.diamondDelete(coordLab.x,coordLab.y);
		labDin[coordLab.y][coordLab.x] = 0;
		numberOfDiamonds--;
		diamonds[arrayIndex]=null;
	};

	public void effect() {
		thePacman.addScore(DIAMOND_SCORE);
	}

	public void activate() {};
	private void store() {};
	private void restore() {
	};

}



//	-------------------------------		Present		--------------------------------------------------
class Present extends StaticElement {
	boolean active;
	int arrayIndex;

	public Present(Point a,int ai) {
		super(a);
		arrayIndex=ai;
		active=true;
	}

	public void countTime() {};
	public void activate() {};
	public void disactivate() {};

	public boolean isActive() {
		return active;
	};

	public void effect() {};

	private void store() {};
	private void restore() {};
	public void paint() {};

}



//	-------------------------------		PassiveBomb		--------------------------------------------------
class PassiveBomb extends Present {
	public PassiveBomb(Point a,int ai) {
		super(a,ai);
		labDin[coordLab.y][coordLab.x] = 4;
		numberOfPresents++;

		new Timer(PRESENT_TIME, new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent ae) {
				active=false;
			}
		}).start();

		graph.passiveBombCreate(coordLab.x,coordLab.y);
	}

	public void disactivate() {
		graph.passiveBombDelete(coordLab.x,coordLab.y);
		labDin[coordLab.y][coordLab.x] = 0;
		restore();
		numberOfPresents--;
		presents[arrayIndex]=null;
	}

	public void countTime() {
		if (!active)
			disactivate();
	};

	public void effect() {
		thePacman.incBombNumber();
	};

	private void store() {};
	private void restore() {
	};
	public void paint() {
	};

}



//	-------------------------------		ActiveBomb		--------------------------------------------------
class ActiveBomb extends Present {
	public ActiveBomb(Point a,int ai) {
		super(a,ai);
		labDin[coordLab.y][coordLab.x] = 5;
		numberOfActiveBombs++;
		store();
		paint();

		new Timer(ACTIVE_BOMB_TIME, new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent ae) {
				active=false;
			}
		}).start();

		graph.activeBombCreate(coordLab.x,coordLab.y);
	}

	public void disactivate() {
		graph.activeBombDelete(coordLab.x,coordLab.y);
		labDin[coordLab.y][coordLab.x] = 0;
		restore();
		numberOfActiveBombs--;
		activeBombs[arrayIndex]=null;
	}

	public void effect()  {};

	public void countTime() {
		if (!active)
			disactivate();
	};

	public void paint() {
	};
	private void store() {};

	private void restore() {
	};

}




//	-------------------------------		Elixir		--------------------------------------------------
class Elixir extends Present {
	public Elixir(Point a,int ai) {
		super(a,ai);
		labDin[coordLab.y][coordLab.x] = 3;
		numberOfPresents++;
		new Timer(PRESENT_TIME, new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent ae) {
				active=false;
			}
		}).start();

		graph.elixirCreate(coordLab.x,coordLab.y);
	}

	public void disactivate() {
		graph.elixirDelete(coordLab.x,coordLab.y);
		labDin[coordLab.y][coordLab.x] = 0;
		restore();
		numberOfPresents--;
		presents[arrayIndex]=null;
	}

	public void countTime() {
		if (!active)
			disactivate();
	};

	public void effect()  {
		thePacman.incLives();
	};
	private void store() {};
	private void restore() {
	};
	public void paint() {
	};
}



//	-------------------------------		Bonus		--------------------------------------------------
class Bonus extends Present {

	int value;

	public Bonus(Point a,int ai) {
		super(a,ai);
		value = r.nextInt(BONUS_MAX);
		labDin[coordLab.y][coordLab.x] = 2;
		numberOfPresents++;

		new Timer(PRESENT_TIME, new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent ae) {
				active=false;
			}
		}).start();

		graph.bonusCreate(coordLab.x,coordLab.y);

	}

	public void disactivate() {
		graph.bonusDelete(coordLab.x,coordLab.y);
		labDin[coordLab.y][coordLab.x] = 0;
		restore();
		numberOfPresents--;
		presents[arrayIndex]=null;
	}

	public void countTime() {
		if (!active)
			disactivate();
	};

	public void effect()  {
		thePacman.addScore(value);
	};

	private void store() {};

	private void restore() {
	};

	public void paint() {
	};

}


//	-------------------------------		Point		--------------------------------------------------
} // new game vege

} // main vege





class Gfx extends Frame {

	BufferedImage pacmanGfx;
	BufferedImage cleverMonsterGfx;
	BufferedImage stupidMonsterGfx;
	BufferedImage bonusGfx;
	BufferedImage elixirGfx;
	BufferedImage passiveBombGfx;
	BufferedImage activeBombGfx;
	BufferedImage wallGfx;
	BufferedImage pathGfx;
	BufferedImage diamondGfx;

	BufferedImage panelGfx;
	BufferedImage lifeYesGfx;
	BufferedImage lifeNoGfx;
	BufferedImage bombYesGfx;
	BufferedImage bombNoGfx;
	BufferedImage logoGfx;


	BufferedImage menuBkGfx;
	BufferedImage menu1ActiveGfx;
	BufferedImage menu1PassiveGfx;
	BufferedImage menu2ActiveGfx;
	BufferedImage menu2PassiveGfx;
	BufferedImage menu3ActiveGfx;
	BufferedImage menu3PassiveGfx;

	BufferedImage highscoreBkGfx;


	String pacmanGfxFile 			= new String("pacman.gif");
	String cleverMonsterGfxFile 	= new String("clever_monster.gif");
	String stupidMonsterGfxFile		= new String("stupid_monster.gif");
	String bonusGfxFile				= new String("bonus.gif");
	String elixirGfxFile			= new String("elixir.gif");
	String passiveBombGfxFile		= new String("passive_bomb.gif");
	String activeBombGfxFile		= new String("active_bomb.gif");
	String wallGfxFile				= new String("wall.gif");
	String pathGfxFile				= new String("path.gif");
	String diamondGfxFile			= new String("diamond.gif");


	String panelGfxFile			= new String("panel.gif");
	String lifeYesGfxFile		= new String("elixir.gif");
	String lifeNoGfxFile		= new String("life_no.gif");
	String bombYesGfxFile		= new String("passive_bomb.gif");
	String bombNoGfxFile		= new String("bomb_no.gif");
	String logoGfxFile			= new String("logo.gif");

	String menuBkGfxFile		= new String("menu_bg.gif");
	String menu1ActiveGfxFile	= new String("menu_01_up.gif");
	String menu1PassiveGfxFile	= new String("menu_01_down.gif");
	String menu2ActiveGfxFile	= new String("menu_02_up.gif");
	String menu2PassiveGfxFile	= new String("menu_02_down.gif");
	String menu3ActiveGfxFile	= new String("menu_03_up.gif");
	String menu3PassiveGfxFile	= new String("menu_03_down.gif");

	String highscoreBkGfxFile	= new String("menu_bg.gif");					//("highscore_bk.gif");

	boolean generateGame;		// true = nem kell kirajzolni, csak hatterbe; false = jatek kozben


	int labStat[][] = new int[LAB_X][LAB_Y];	// Falak es utak. 0 - fal, 1 - ut
	int labDin[][] = new int[LAB_X][LAB_Y];		// Ajandekok es gyemantok. 0 - semmi, 1 - gyemant, 2 - bonusz, 3 - elixir, 4 - passziv bomba, 5 - aktiv bomba
	int labMon[][] = new int[LAB_X][LAB_Y];		// 0 - semmi, 1 - okos szorny, 2 - buta szorny
	Point pacmanCoord;
	BufferedImage pacmanBk;
	Point monstersCoord[] = new Point[MONSTER_MAX];
	BufferedImage monstersBk[] = new BufferedImage[MONSTER_MAX];

	BufferedImage gameImage;

	Graphics gr;
	Graphics2D g;
	Graphics2D gameGr;

	public Gfx() {

		Image temp;
		Graphics2D g2;
		int width, height;

		setLayout(null);
     	addWindowListener(new Bezaras());
		addKeyListener( new MyKeyAdapter() );

		setBounds(0,0,SCREEN_X,SCREEN_Y);
		setVisible(true);
		gr=getGraphics();
		g = (Graphics2D) gr;

		gameImage = (BufferedImage) createImage(SCREEN_X,SCREEN_Y);
		gameGr = gameImage.createGraphics();

		pacmanGfx = loadImage(pacmanGfxFile);
		cleverMonsterGfx = loadImage(cleverMonsterGfxFile);
		stupidMonsterGfx = loadImage(stupidMonsterGfxFile);
		bonusGfx = loadImage(bonusGfxFile);
		elixirGfx = loadImage(elixirGfxFile);
		passiveBombGfx = loadImage(passiveBombGfxFile);
		activeBombGfx = loadImage(activeBombGfxFile);
		wallGfx = loadImage(wallGfxFile);
		pathGfx = loadImage(pathGfxFile);
		diamondGfx = loadImage(diamondGfxFile);

		panelGfx = loadImage(panelGfxFile);
		lifeYesGfx = loadImage(lifeYesGfxFile);
		lifeNoGfx = loadImage(lifeNoGfxFile);
		bombYesGfx = loadImage(bombYesGfxFile);
		bombNoGfx = loadImage(bombNoGfxFile);
		logoGfx = loadImage(logoGfxFile);

		menuBkGfx = loadImage(menuBkGfxFile);
		menu1ActiveGfx = loadImage(menu1ActiveGfxFile);
		menu1PassiveGfx = loadImage(menu1PassiveGfxFile);
		menu2ActiveGfx = loadImage(menu2ActiveGfxFile);
		menu2PassiveGfx = loadImage(menu2PassiveGfxFile);
		menu3ActiveGfx = loadImage(menu3ActiveGfxFile);
		menu3PassiveGfx = loadImage(menu3PassiveGfxFile);

		highscoreBkGfx = loadImage(highscoreBkGfxFile);


	}

	public BufferedImage loadImage(String fileName) {

		int width=0, height=0;
		Image temp = getToolkit().getImage(fileName);
		BufferedImage retImage;
		Graphics2D g2;

		try {
    	   	MediaTracker tracker = new MediaTracker (this);
          	tracker.addImage (temp, 0);
        	tracker.waitForID (0);
		} catch (Exception e) {};

		width = temp.getWidth(this);
		height = temp.getHeight(this);

		retImage = new BufferedImage(width,height,2);
		g2 = retImage.createGraphics();
		g2.drawImage(temp,0,0,this);
		g2.dispose();

		return retImage;

	}


	public void generateGameStart() {
		generateGame = true;
	}

	public void generateGameStop() {
		generateGame = false;
		gameDraw(0,0,SCREEN_X,SCREEN_Y);
	}




//	MENU RAJZOLO MUVELETEK

	public void menuPaint() {
		gameGr.drawImage(menuBkGfx,0,KEZ_Y,this);
		gameGr.drawImage(menu1PassiveGfx,MENU1_X,MENU1_Y,this);
		gameGr.drawImage(menu2PassiveGfx,MENU2_X,MENU2_Y,this);
		gameGr.drawImage(menu3PassiveGfx,MENU3_X,MENU3_Y,this);
		gameDraw(0,0,SCREEN_X,SCREEN_Y);
	}

	public void menuItemPaint(int act, int prev) {
		if (prev == act) return;
		switch(prev) {
			case 1:
				gameGr.drawImage(menu1PassiveGfx,MENU1_X,MENU1_Y,this);
				break;
			case 2:
				gameGr.drawImage(menu2PassiveGfx,MENU2_X,MENU2_Y,this);
				break;
			case 3:
				gameGr.drawImage(menu3PassiveGfx,MENU3_X,MENU3_Y,this);
				break;
		}

		switch(act) {
			case 1:
				gameGr.drawImage(menu1ActiveGfx,MENU1_X,MENU1_Y,this);
				break;
			case 2:
				gameGr.drawImage(menu2ActiveGfx,MENU2_X,MENU2_Y,this);
				break;
			case 3:
				gameGr.drawImage(menu3ActiveGfx,MENU3_X,MENU3_Y,this);
				break;
		}
		gameDraw(0,0,SCREEN_X,SCREEN_Y);

	}

//	HIGHSCORE RAJZOLO MUVELETEK
	public void highscorePaint() {
		gameGr.drawImage(highscoreBkGfx,0,0,this);
		gameDraw(0,0,SCREEN_X,SCREEN_Y);
	}


	public void namePaint(int id, String name, int score) {
		FontRenderContext frc = gameGr.getFontRenderContext ();
		Font betutipus = new Font ("Times New Roman", Font.BOLD, 25);

		gameGr.setColor(Color.black);
		String szoveg = new String(name);
		TextLayout megjelenito = new TextLayout (szoveg, betutipus, frc);
		megjelenito.draw (gameGr, HIGHS_X+4, HIGHS_Y+id*HIGHS_YH+4);

		gameGr.setColor(Color.red);
		szoveg = new String(name);
		megjelenito = new TextLayout (szoveg, betutipus, frc);
		megjelenito.draw (gameGr, HIGHS_X, HIGHS_Y+id*HIGHS_YH);

		gameGr.setColor(Color.black);
		szoveg = new String(Integer.toString(score));
		megjelenito = new TextLayout (szoveg, betutipus, frc);
		megjelenito.draw (gameGr, HIGHS_X+4+HIGHS_XW, HIGHS_Y+id*HIGHS_YH+4);

		gameGr.setColor(Color.red);
		szoveg = new String(Integer.toString(score));
		megjelenito = new TextLayout (szoveg, betutipus, frc);
		megjelenito.draw (gameGr, HIGHS_X+HIGHS_XW, HIGHS_Y+id*HIGHS_YH);

		gameDraw(0,0,SCREEN_X,SCREEN_Y);
	}

	boolean ok;
	public String getName() {

		String s;

		ok = false;
		g.setColor(Color.black);
		TextField t1 = new TextField("",10);
		t1.setBounds(FX,FY,FW,FH);
		add(t1);
		Button b1 = new Button("Ok");
		b1.setBounds(BX,BY,BW,BH);
		add(b1);
		gameGr.setColor(Color.black);
		gameGr.fillRect(225,150,250,250);
		gameDraw(0,0,SCREEN_X,SCREEN_Y);
		b1.setVisible(true);
		t1.setVisible(true);

		g.setColor(Color.red);
		FontRenderContext frc = g.getFontRenderContext ();
		Font betutipus = new Font ("Times New Roman", Font.BOLD, 25);

		g.setColor(Color.red);
		String szoveg = new String("Name:");
		TextLayout megjelenito = new TextLayout (szoveg, betutipus, frc);
		megjelenito.draw (g, TX, TY);

		b1.addActionListener(new OkListener());
		while (!ok);

		s = t1.getText();
		t1.setVisible(false);
		b1.setVisible(false);
		return s;

	}

		class OkListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				ok=true;
			}
		}




	// NEW GAME RAJZOLO MUVELETEK

	public void gamePaint() {
		gameDraw(0,0,SCREEN_X-KEZ_X,SCREEN_Y-KEZ_Y);
	}

	public void newGamePaint() {
		for (int i=0;i<LAB_Y;i++)
			for (int j=0;j<LAB_X;j++) {
				labDin[i][j] = 0;
				labMon[i][j] = 0;
			}
		gameGr.drawImage(panelGfx,SCREEN_X-KEZ_X,KEZ_Y,this);
		gameGr.drawImage(logoGfx,LOGO_X,LOGO_Y,this);

		FontRenderContext frc = gameGr.getFontRenderContext ();
		Font betutipus = new Font ("Times New Roman", Font.BOLD, 25);
		String szoveg = new String ("Score:");

		TextLayout megjelenito = new TextLayout (szoveg, betutipus, frc);
		gameGr.setColor (Color.blue);
		megjelenito.draw (gameGr, SCORE_LAB_X, SCORE_LAB_Y);

		szoveg = new String("Level:");
		megjelenito = new TextLayout (szoveg, betutipus, frc);
		megjelenito.draw (gameGr, LEVEL_LAB_X, LEVEL_LAB_Y);

		gameDraw(SCREEN_X-KEZ_X,KEZ_Y,SCREEN_X,SCREEN_Y);

		scorePaint(0);
		levelPaint(1);

	}

	public void scorePaint(int score) {
		gameGr.setColor(Color.white);
		gameGr.fillRect(SCORE_BOX_XK,SCORE_BOX_YK,SCORE_BOX_W,SCORE_BOX_H);

		FontRenderContext frc = gameGr.getFontRenderContext ();
		Font betutipus = new Font ("Times New Roman", Font.BOLD, 25);

		gameGr.setColor(Color.blue);
		String szoveg = new String(Integer.toString(score));
		TextLayout megjelenito = new TextLayout (szoveg, betutipus, frc);
		megjelenito.draw (gameGr, SCORE_NUM_X, SCORE_NUM_Y);

		gameDraw(SCORE_BOX_XK,SCORE_BOX_YK,SCORE_BOX_W,SCORE_BOX_H);
	}

	public void levelPaint(int level) {
		gameGr.setColor(Color.white);
		gameGr.fillRect(LEVEL_BOX_XK,LEVEL_BOX_YK,LEVEL_BOX_W,LEVEL_BOX_H);

		FontRenderContext frc = gameGr.getFontRenderContext ();
		Font betutipus = new Font ("Times New Roman", Font.BOLD, 20);

		gameGr.setColor(Color.blue);
		String szoveg = new String(Integer.toString(level));
		TextLayout megjelenito = new TextLayout (szoveg, betutipus, frc);
		megjelenito.draw (gameGr, LEVEL_NUM_X, LEVEL_NUM_Y);

		gameDraw(LEVEL_BOX_XK,LEVEL_BOX_YK,LEVEL_BOX_W,LEVEL_BOX_H);
	}

	// elet kiiro
	public void livesPaint (int act) {
		int i=0;
		if (act != 0)
			for (;i<act;i++)
				gameGr.drawImage(lifeYesGfx,LIVES_X+i*K_X,LIVES_Y,this);
		for (;i<LIVES_MAX;i++)
			gameGr.drawImage(lifeNoGfx,LIVES_X+i*K_X,LIVES_Y,this);

		gameDraw(LIVES_X,LIVES_Y,LIVES_X+LIVES_MAX*K_X,LIVES_Y);
	}

	// bomba kiiro
	public void bombsPaint (int act) {
		int i=0;
		if (act != 0)
			for (;i<act;i++)
				gameGr.drawImage(bombYesGfx,BOMBS_X+i*K_X,BOMBS_Y,this);
		for (;i<BOMB_MAX;i++)
			gameGr.drawImage(bombNoGfx,BOMBS_X+i*K_X,BOMBS_Y,this);

		gameDraw(BOMBS_X,BOMBS_Y,BOMBS_X+BOMB_MAX*K_X,BOMBS_Y);
	}

	// kep kezdokoordinatat ker
	public void restore(int x, int y) {
		Point lu = coordToLab(x,y);
		Point rd = coordToLab(x+K_X-1,y+K_Y-1);

		boolean two=true;
		if ((lu.getX() == rd.getX()) && (lu.getY() == rd.getY())) two=false;

		if (labStat[lu.getY()][lu.getX()] == 1)
			pathPaint(lu.getX(),lu.getY());
		else wallPaint(lu.getX(),lu.getY());

		switch (labDin[lu.getY()][lu.getX()]) {
			case 1: diamondPaint(lu.getX(),lu.getY());
					break;
			case 2:	bonusPaint(lu.getX(),lu.getY());
					break;
			case 3:	elixirPaint(lu.getX(),lu.getY());
					break;
			case 4:	passiveBombPaint(lu.getX(),lu.getY());
					break;
			case 5:	activeBombPaint(lu.getX(),lu.getY());
					break;
		}


		if (two) {
			if (labStat[rd.getY()][rd.getX()] == 1)
				pathPaint(rd.getX(),rd.getY());
			else wallPaint(rd.getX(),rd.getY());

			switch (labDin[rd.getY()][rd.getX()]) {
				case 1: diamondPaint(rd.getX(),rd.getY());
						break;
				case 2: bonusPaint(rd.getX(),rd.getY());
						break;
				case 3:	elixirPaint(rd.getX(),rd.getY());
						break;
				case 4: passiveBombPaint(rd.getX(),rd.getY());
						break;
				case 5: activeBombPaint(rd.getX(),rd.getY());
						break;
			}
		}



	}

	public void restoreAll() {
		if (pacmanCoord != null)
			restore(pacmanCoord.getX()-K_X/2,pacmanCoord.getY()-K_Y/2);
		for (int i=0;i<MONSTER_MAX;i++)
			if (monstersCoord[i] != null)
				restore(monstersCoord[i].getX()-K_X/2,monstersCoord[i].getY()-K_Y/2);

	}

	public void pacmanMeet() {
		pacmanBk = new BufferedImage(22,22,2);
		pacmanBk.createGraphics().drawImage(pathGfx,null,0,0);
	}

//	pacman (normal koordinatat var)
	public void pacmanCreate (int x, int y) {
		pacmanCoord = new Point(x,y);
		pacmanBk = new BufferedImage(22,22,2);
		pacmanPaint(x,y);
	}

	public void pacmanPaint (int x, int y) {
			restore(pacmanCoord.getX()-K_X/2,pacmanCoord.getY()-K_Y/2);

			gameGr.drawImage(pacmanGfx,x-K_X/2-KEZ_X,y-K_Y/2,this);
			pacmanCoord = new Point(x,y);
	}

	public void pacmanDelete () {
		pacmanCoord = null;
	}

	public void wallCreate (int x, int y) {
		labStat[y][x] = 0;
		wallPaint(x,y);
	}

	public void wallPaint (int x, int y) {
			Point p = labToCoord(x,y);
			gameGr.drawImage(wallGfx,p.getX()-KEZ_X,p.getY(),this);

	}

	public void wallDelete () {
	}

	public void pathCreate (int x, int y) {
		labStat[y][x] = 1;
		pathPaint(x,y);
	}

	public void pathPaint (int x, int y) {
			Point p = labToCoord(x,y);
			gameGr.drawImage(pathGfx,p.getX()-KEZ_X,p.getY(),this);

	}

	public void pathDelete () {
	}


//	diamond	(labirintus koordinatakat var)
	public void diamondCreate (int x, int y) {
		labDin[y][x] = 1;
		diamondPaint(x,y);
	}

	public void diamondPaint (int x, int y) {
			Point p = labToCoord(x,y);
			gameGr.drawImage(diamondGfx,p.getX()-KEZ_X,p.getY(),this);

	}

	public void diamondDelete (int x,int y) {
		labDin[y][x] = 0;
		if (labStat[y][x] == 1) pathPaint(x,y);
		else wallPaint(x,y);
		Point pc = coordToLab(pacmanCoord.getX(),pacmanCoord.getY());
		if ((pc.getX() == x) && (pc.getY() == y)) pacmanPaint(pacmanCoord.getX(),pacmanCoord.getY());

	}


//	Elixir (lab koord)
	public void elixirCreate (int x, int y) {
		labDin[y][x] = 3;
		elixirPaint(x,y);
	}

	public void elixirPaint (int x, int y) {
			Point p = labToCoord(x,y);
			gameGr.drawImage(elixirGfx,p.getX()-KEZ_X,p.getY(),this);

	}

	public void elixirDelete (int x,int y) {
		labDin[y][x] = 0;
		if (labStat[y][x] == 1) pathPaint(x,y);
		else wallPaint(x,y);
		Point pc = coordToLab(pacmanCoord.getX(),pacmanCoord.getY());
		if ((pc.getX() == x) && (pc.getY() == y)) pacmanPaint(pacmanCoord.getX(),pacmanCoord.getY());

	}

//	Bonus (lab koord)
	public void bonusCreate (int x, int y) {
		labDin[y][x] = 2;
		bonusPaint(x,y);
	}

	public void bonusPaint (int x, int y) {
			Point p = labToCoord(x,y);
			gameGr.drawImage(bonusGfx,p.getX()-KEZ_X,p.getY(),this);

	}

	public void bonusDelete (int x,int y) {
		labDin[y][x] = 0;
		if (labStat[y][x] == 1) pathPaint(x,y);
		else wallPaint(x,y);
		Point pc = coordToLab(pacmanCoord.getX(),pacmanCoord.getY());
		if ((pc.getX() == x) && (pc.getY() == y)) pacmanPaint(pacmanCoord.getX(),pacmanCoord.getY());

	}


	public void passiveBombCreate (int x, int y) {
		labDin[y][x] = 4;
		passiveBombPaint(x,y);
	}

	public void passiveBombPaint (int x, int y) {
			Point p = labToCoord(x,y);
			gameGr.drawImage(passiveBombGfx,p.getX()-KEZ_X,p.getY(),this);

	}

	public void passiveBombDelete (int x,int y) {
		labDin[y][x] = 0;
		if (labStat[y][x] == 1) pathPaint(x,y);
		else wallPaint(x,y);
		Point pc = coordToLab(pacmanCoord.getX(),pacmanCoord.getY());
		if ((pc.getX() == x) && (pc.getY() == y)) pacmanPaint(pacmanCoord.getX(),pacmanCoord.getY());

	}


	public void activeBombCreate (int x, int y) {
		labDin[y][x] = 5;
		activeBombPaint(x,y);
	}

	public void activeBombPaint (int x, int y) {
			Point p = labToCoord(x,y);
			gameGr.drawImage(activeBombGfx,p.getX()-KEZ_X,p.getY(),this);

	}

	public void activeBombDelete (int x,int y) {
		labDin[y][x] = 0;
		if (labStat[y][x] == 1) pathPaint(x,y);
		else wallPaint(x,y);
		Point pc = coordToLab(pacmanCoord.getX(),pacmanCoord.getY());
		if ((pc.getX() == x) && (pc.getY() == y)) pacmanPaint(pacmanCoord.getX(),pacmanCoord.getY());

	}


//	stupidMonster (normal koordinatat var)
	public void stupidMonsterCreate (int x, int y, int id) {
		Point p = coordToLab(x,y);
		labMon[p.getY()][p.getX()] = 1;
		monstersCoord[id] = new Point(x,y);


		stupidMonsterPaint(x,y,id);

	}

	public void stupidMonsterPaint (int x, int y, int id) {

			gameGr.drawImage(stupidMonsterGfx,x-K_X/2-KEZ_X,y-K_Y/2,this);
			monstersCoord[id] = new Point(x,y);

	}

	public void stupidMonsterDelete (int x, int y, int id) {
		Point p = coordToLab(x,y);
		labMon[p.getY()][p.getX()] = 0;
		monstersCoord[id] = null;
		restore(x-K_X/2,y-K_Y/2);
	}


//	cleverMonster (normal koordinatat var)
	public void cleverMonsterCreate (int x, int y, int id) {
		Point p = coordToLab(x,y);
		labMon[p.getY()][p.getX()] = 2;
		monstersCoord[id] = new Point(x,y);
		cleverMonsterPaint(x,y,id);
	}

	public void cleverMonsterPaint (int x, int y, int id) {
			gameGr.drawImage(cleverMonsterGfx,x-K_X/2-KEZ_X,y-K_Y/2,this);
			monstersCoord[id] = new Point(x,y);
	}

	public void cleverMonsterDelete (int x, int y, int id) {
		Point p = coordToLab(x,y);
		labMon[p.getY()][p.getX()] = 0;
		monstersCoord[id] = null;
		restore(x-K_X/2,y-K_Y/2);
	}



	public void gameDraw (int x1, int y1, int x2, int y2) {
		if (generateGame) return;
		g.setClip(x1,y1,x2,y2);
		g.drawImage(gameImage, null, 0, 0);
		g.setClip(0,0,SCREEN_X,SCREEN_Y);
	}



	class MyKeyAdapter extends KeyAdapter {
		public void keyPressed( KeyEvent e ) {
				int key = e.getKeyCode();
					if (key == KeyEvent.VK_LEFT) 	keyLeft();
					if (key == KeyEvent.VK_RIGHT) 	keyRight();
					if (key == KeyEvent.VK_UP) 		keyUpp();
					if (key == KeyEvent.VK_DOWN) 	keyDownn();
					if (key == KeyEvent.VK_SPACE)	keySpace();
					if (key == KeyEvent.VK_ENTER)	keyEnter();
					if (key == KeyEvent.VK_ESCAPE)	keyEsc();
		}
	}



	private Point coordToLab(int x, int y) {
		return new Point((int)Math.ceil((x-KEZ_X)/K_X),(int)Math.ceil((y-KEZ_Y)/K_Y));
	}

	private Point labToCoord(int x, int y) {
		return new Point(KEZ_X+x*K_X,KEZ_Y+y*K_Y);
	}

	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		if (gameImage == null) return;
		g2.setClip(0,0,SCREEN_X,SCREEN_Y);
		g2.drawImage(gameImage,null,0,0);
	}
}



class Point {
	int x;
	int y;

	public Point(int xb, int yb) {
		x = xb;
		y = yb;
	}

	public boolean compareTo(Point a) {
		return ((a.x == x) && (a.y == y));
	}

	public int getX() {
		return x;
	};
	public int getY() {
		return y;
	};

	public boolean meet (Point a) {
		int t1,t2;
		t1=Math.abs(a.x-x);
		t2=Math.abs(a.y-y);
		if (Math.sqrt(t1*t1+t2*t2) < K_X-2) return true;
		else return false;
	}
}

} // PacmanGame vege
