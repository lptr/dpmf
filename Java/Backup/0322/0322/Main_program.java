package PacMan;

public class Main_program extends SkeletonObject {


	/**
	 * F� inicializ�l� r�sz
	 *
	 */
    Main_program() {
        Game game = new Game();
		long finalScore = game.StartGame();
		Println ("final score: " + finalScore);
    }


	/**
	 * A bel�p�si pont
	 *
	 * @param   args  parancssori param�terek
	 */
	public static void main (String args[]) {
		System.out.println ("in Main_program.main(), initialising...");
		
		new Main_program();
	}
}
