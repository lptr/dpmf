package PacMan;

import java.awt.*;

/**
 * Osztály egy játék lebonyolítása. Akkor jön létre, amikor a játékos új játékot
 * indít a fõmenübõl, és akkor szûnik meg, amikor kilép belõle, életei elfogynak,
 * vagy az utolsó pályát is teljesítette.
 *
 * @author Lóci
 */
class Game extends SkeletonObject {

	/**
	 * Az aktuális pályát tartalmazó {@link Maze} objektum
	 */
	Maze currentMaze = null;

	/**
	 * Az aktuális szint száma
	 */
	int levelnum = 1;

	/**
	 * A játékos által eddig elért pontszám
	 */
	int score = 0;

	/**
	 * A játékos hátralevõ életeinek száma
	 */
	int lives = 0;

	/**
	 * A játék futási állapota. Lehet futó (false) és megállított (true).
	 */
	boolean paused = false;
	
	/**
	 * Konstruktor. Az ég világon semmit nem csinál.
	 *
	 */
	Game() {
	}

	/**
	 * A játék elindítása.
	 *
	 * @see Maze#Maze
	 * @see Maze#Load
	 * @return  A játékos által elért pontszám
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
	 * A játék belsõ ciklusa. Feladata minden lépésben meghívni
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
	 * Pontszám növelése. Callback a {@link ScoreBonus#PickUp} számára.
	 *
	 * @param value	Pontszámnövekmény
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