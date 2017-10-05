package PacMan;

import java.awt.*;

class Game extends SkeletonObject {

	Maze currentMaze = null;
	int score = 0;
	int lives = 0;
	int levelnum = 1;
	boolean paused = false;
	
	Game() {
	}
	
	long StartGame() {
		In ("StartGame", "game initialising..");
		
		currentMaze = new Maze(this, 1);

		Println ("starting gameloop..");
		GameLoop();

		Leave ("StartGame", "");		
		return 100;
	}

	void GameLoop() {
		In ("GameLoop", "");
		
		// loopit!
		while (currentMaze.Step()) {
			Println (GetName("GameLoop") + ": doing next step");
		}
		
		Leave ("GameLoop", "");
	}
	
}