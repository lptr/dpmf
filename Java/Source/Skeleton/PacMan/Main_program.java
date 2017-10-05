package PacMan;

import java.lang.reflect.*;
import java.lang.*;


/**
 * A játék fõprogramját megvalósító osztály.
 * Feladata a fõmenü megjelenítése
 *
 * @author Lóci
 */
public class Main_program extends SkeletonObject {

	/**
	 * Fõ inicializáló rész
	 *
	 */
	Main_program() {
       	Game game = new Game();
		Runtime.getRuntime().traceMethodCalls(true);
		Runtime.getRuntime().traceInstructions(true);
		long finalScore = game.StartGame();
		Println ("final score: " + finalScore);

		Ask ("press enter to exit.");

		// `Sequence Diagram` kirajzoló
//		for(int time=0;time<SkeletonObject.seqDiag.getSeqTime();time++) {
//			for(int item=0;item<SkeletonObject.objNumberCounter;item++) {
//				int val = SkeletonObject.seqDiag.getStateAt(item,time);
//				System.out.print(val==-1?"-":("" + val));
//			}
//			System.out.println(" " + SkeletonObject.seqDiag.getLabelAt(time));
//		}
		Field[] test = game.getClass().getFields();
		for(int i=0;i<test.length;i++)
			System.out.println(test[i] + ">>" + test[i].getName());

		Ask ("just once more, please.");
	 }

	/*
	 * A belépési pont. A java rendszer ezt a metódust hívja meg induláskor.
	 * Itt következik be a Main_program osztály példányosítása.
	 *
	 * @param   args  parancssori paraméterek (nem használt)
	 */
	public static void main (String args[]) {
		System.out.println ("in Main_program.main(), initialising...");
		
		new Main_program();
		System.exit(0);
	}
}
