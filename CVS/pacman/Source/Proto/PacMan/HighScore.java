// $Id: HighScore.java,v 1.5 2001/04/27 03:15:44 lptr Exp $
// $Date: 2001/04/27 03:15:44 $
// $Author: lptr $

package PacMan;

import java.io.*;
import java.lang.*;
import java.lang.Integer;
import Util.Parser.*;

/**
 * A HighScore tabla kezeleset megvalosito objektum
 */
public class HighScore extends ProtoObject implements Parsing{
	/**
	 * A highscore-t tároló file neve
	 */
	final String HighScoreFile = "highscore.dat";

	/**
	 * A maximalisan tárolható emberek száma
	 */
	final int MaxStored = 10;

	/**
	 * A legjobbak neveinek eltárolására
	 */
	private String Names[];

	/**
	 * A legjobbak pontszámainak eltárolásara
	 */
	private long Scores[];

	/**
	 * Konstruktor, inicializálja a HighScore-táblát
	 */
	public HighScore() throws IOException
	{   
		Names = new String[MaxStored];
		Scores = new long[MaxStored];
		Reset();
		Load();
	}

	/**
	 * Eljárás a highscore-ban lévõ elemek inicializálására (üres név, 0 pont mindegyik elemre)
	 */
	public void Reset()
	{
		int i; 

		// Ciklus, végigmegy a tömbön és végiginicializálja
		for (i = 0; i < MaxStored; i++)
		{
			Names[i] = "";
			Scores[i] = 0;
		}
	}

	/**
	 * Eljárás a highscore tábla file-ból való betöltésére
	 */
	public void Load()
	{
		try
		{
			// Megnyitjuk a file-t olvasásra
			RandomAccessFile inputfile = new RandomAccessFile(HighScoreFile,"r");

			int i;
			String s = new String();

			for (i = 0; i < MaxStored; i++)
			{
				s = inputfile.readLine();  // Nev beolvasasa
				Names [i] = s;
				s = inputfile.readLine();  // Pontszam beolvasasa
				try {
					Scores [i] = Long.valueOf(s).longValue();  // A beolvasott eredmény string számma konvertálása
				} catch (NumberFormatException e)
				{
					// Hiba volt beolvasáskor (pl. a fileban nem szerepelt semmi), ekkor 0 lesz az érték
					Scores [i] = 0; 
				}
			}
			inputfile.close(); // File bezárása

		} catch (IOException e)
		{
			// Hiba történt file olvasás közben
			System.out.println("Hiba volt a highscore tabla olvasasa kozben!");
		}

	}

	/**
	 * Eljárás a highscore tábla file-ba való elmentésére
	 */
	public void Save()
	{
		try
		{
			// Megnyitjuk a file-t irasra
			RandomAccessFile outputfile = new RandomAccessFile(HighScoreFile,"rw");

			int i;
			// Ciklusban kiirjuk a nev/pontszam parokat
			for (i = 0; i < MaxStored; i++)
			{
				outputfile.writeBytes (Names[i] + "\n");
				outputfile.writeBytes (String.valueOf (Scores[i]) + "\n");
			}
			outputfile.close(); // File bezarasa

		} catch (IOException e)
		{
			System.out.println("Hiba volt a highscore tabla olvasasa kozben!");
		}

	}

	/**
	 * Eljaras egy uj eredmeny beszurasara. Ha az eredmeny nincs bent a legjobb
	 * 10-ben, nem csinal semmit, egyebkent berakja az uj eredmeny a helyere.
	 *
	 * @param NewName	A játékos neve.
	 * @param NewScore	A játékos pontszáma.
	 */
	public void Insert(String NewName, long NewScore)
	{
		int i, j;
		boolean done = false;

		// Vegignezzuk az eddigi eredmenyeket...
		for (i = 0; i < MaxStored; i++)
		{
			// ...ha jobb az uj, mint egy regi...
			if (NewScore >= Scores[i])
			{
				// ...lejjebbshifteljuk az eddigi tabla egy reszet...
				for (j = MaxStored - 1; j > i; j--)
				{
					Scores[j] = Scores[j-1];
					Names[j] = Names[j-1];
				}
				// ...es beszurjuk az uj eredmenyt
				Scores[i] = NewScore;
				Names[i] = NewName;
				Save();
				break;
			}
		}
	}

	/**
	 * Eljárás a highscore tábla kiíratására
	 */
	public void paint()
	{
		// Vegigmegyunk az egész tömbön és kiírjuk a képernyõre az értékeket
		for (int i = 0; i < MaxStored; i++)
			if (Scores[i] > 0)
				System.out.println ("highscore " + Names[i] + " " + Scores[i]);
	}

	public void Parse (ourParser parser) throws Exception {

		/**
		 * Kiírjuk a highscore táblát
		 */
		paint();
	
		while (parser.getNextLine()==ourParser.TT_OK) {

			/**
			 * Kiírjuk, hogy mit dolgozunk fel..
			 */
			PrintParsed(parser.lineTokens);

			if (parser.matchLine("remark {%c}")) {
				System.out.println ("/* " + Concat(parser.lineTokens) + "*/");
				break;
			} else 
			if (parser.matchLine("exithighscore")) {
				System.out.println ("event exithighscore");
				break;
			} else {
				ThrowParseException(parser.lineTokens, parser.lineno());
			}
		}
	}
}
