package PacMan;

public class Main_program extends SkeletonObject {


	/**
	 * Fõ inicializáló rész
	 *
	 */
    Main_program() {
        Game game = new Game();
		long finalScore = game.StartGame();
		Println ("final score: " + finalScore);
    }


	/**
	 * A belépési pont
	 *
	 * @param   args  parancssori paraméterek
	 */
	public static void main (String args[]) {
		System.out.println ("in Main_program.main(), initialising...");
		
		new Main_program();
	}
}
