package PacMan;

import java.lang.reflect.*;
import java.lang.*;


/**
 * A j�t�k f�programj�t megval�s�t� oszt�ly.
 * Feladata a f�men� megjelen�t�se
 *
 * @author L�ci
 */
public class Main_program extends SkeletonObject {

	/**
	 * F� inicializ�l� r�sz
	 *
	 */
	Main_program() {
       	Game game = new Game();
		Runtime.getRuntime().traceMethodCalls(true);
		Runtime.getRuntime().traceInstructions(true);
		long finalScore = game.StartGame();
		Println ("final score: " + finalScore);

		Ask ("press enter to exit.");

		// `Sequence Diagram` kirajzol�
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
	 * A bel�p�si pont. A java rendszer ezt a met�dust h�vja meg indul�skor.
	 * Itt k�vetkezik be a Main_program oszt�ly p�ld�nyos�t�sa.
	 *
	 * @param   args  parancssori param�terek (nem haszn�lt)
	 */
	public static void main (String args[]) {
		System.out.println ("in Main_program.main(), initialising...");
		
		new Main_program();
		System.exit(0);
	}
}
