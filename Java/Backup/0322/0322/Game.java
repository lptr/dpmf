package PacMan;

import java.awt.*;

class Game extends SkeletonObject {

	Maze currentMaze = null;
	int score = 0;
	int lives = 0;
	int levelnum = 1;
	boolean paused = false;
	

	/**
	 * Game oszt�ly
	 * 
	 * Feladata egy j�t�k lebonyol�t�sa
	 *
	 */
	Game() {
	}
	

	/**
	 * A j�t�k elind�t�sa
	 *
	 * @return  a j�t�kos �ltal el�rt pontsz�m   
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
	 * GameLoop - A j�t�k bels� ciklusa. Feladata minden l�p�sben megh�vni
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