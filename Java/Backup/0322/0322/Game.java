package PacMan;

import java.awt.*;

class Game extends SkeletonObject {

	Maze currentMaze = null;
	int score = 0;
	int lives = 0;
	int levelnum = 1;
	boolean paused = false;
	

	/**
	 * Game osztály
	 * 
	 * Feladata egy játék lebonyolítása
	 *
	 */
	Game() {
	}
	

	/**
	 * A játék elindítása
	 *
	 * @return  a játékos által elért pontszám   
	 */
	long StartGame() {
		In ("StartGame", "game initialising..");
		
		currentMaze = new Maze(this, 1);

		Println ("starting gameloop..");
		GameLoop();

		Leave ("StartGame", "");		
		return 100;
	}


	/**
	 * GameLoop - A játék belsõ ciklusa. Feladata minden lépésben meghívni
	 * a currentMaze.Step()-et.
	 */
	void GameLoop() {
		In ("GameLoop", "");
		
		// loopit!
		while (currentMaze.Step()) {
			Println (GetName("GameLoop") + ": doing next step");
		}
		
		Leave ("GameLoop", "");
	}
	
}