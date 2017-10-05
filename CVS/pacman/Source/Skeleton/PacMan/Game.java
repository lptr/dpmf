package PacMan;

import java.awt.*;

/**
 * Oszt�ly egy j�t�k lebonyol�t�sa. Akkor j�n l�tre, amikor a j�t�kos �j j�t�kot
 * ind�t a f�men�b�l, �s akkor sz�nik meg, amikor kil�p bel�le, �letei elfogynak,
 * vagy az utols� p�ly�t is teljes�tette.
 *
 * @author L�ci
 */
class Game extends SkeletonObject {

	/**
	 * Az aktu�lis p�ly�t tartalmaz� {@link Maze} objektum
	 */
	Maze currentMaze = null;

	/**
	 * Az aktu�lis szint sz�ma
	 */
	int levelnum = 1;

	/**
	 * A j�t�kos �ltal eddig el�rt pontsz�m
	 */
	int score = 0;

	/**
	 * A j�t�kos h�tralev� �leteinek sz�ma
	 */
	int lives = 0;

	/**
	 * A j�t�k fut�si �llapota. Lehet fut� (false) �s meg�ll�tott (true).
	 */
	boolean paused = false;
	
	/**
	 * Konstruktor. Az �g vil�gon semmit nem csin�l.
	 *
	 */
	Game() {
	}

	/**
	 * A j�t�k elind�t�sa.
	 *
	 * @see Maze#Maze
	 * @see Maze#Load
	 * @return  A j�t�kos �ltal el�rt pontsz�m
	 */
	long StartGame() {
		In ("StartGame", "game initialising..");
		
		currentMaze = new Maze(this, 1);

		Println ("starting gameloop..");
		GameLoop();

		Leave ("StartGame", "" + score);
		return score;
	}


	/**
	 * A j�t�k bels� ciklusa. Feladata minden l�p�sben megh�vni
	 * a {@link Maze#Step() currentMaze.Step()}-et.
	 */
	void GameLoop() {
		In ("GameLoop", "");
		
		// loopit!
		while (currentMaze.Step()) {
			Println ("[i] score: " + score);
			Println (GetName("GameLoop") + ": doing next step");
		}
		
		Leave ("GameLoop", "");
	}

	/**
	 * Pontsz�m n�vel�se. Callback a {@link ScoreBonus#PickUp} sz�m�ra.
	 *
	 * @param value	Pontsz�mn�vekm�ny
	 * @see	ScoreBonus
	 * @see Maze#IncreaseScore
	 */
	void IncreaseScore(long value) {
		In ("IncreaseScore", "" + value);
		score += value;
		Println ("[i] score increased to " + score + ".");
		Leave ("IncreaseScore", "" + score);
	}
}