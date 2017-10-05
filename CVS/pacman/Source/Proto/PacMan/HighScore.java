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
	 * A highscore-t t�rol� file neve
	 */
	final String HighScoreFile = "highscore.dat";

	/**
	 * A maximalisan t�rolhat� emberek sz�ma
	 */
	final int MaxStored = 10;

	/**
	 * A legjobbak neveinek elt�rol�s�ra
	 */
	private String Names[];

	/**
	 * A legjobbak pontsz�mainak elt�rol�sara
	 */
	private long Scores[];

	/**
	 * Konstruktor, inicializ�lja a HighScore-t�bl�t
	 */
	public HighScore() throws IOException
	{   
		Names = new String[MaxStored];
		Scores = new long[MaxStored];
		Reset();
		Load();
	}

	/**
	 * Elj�r�s a highscore-ban l�v� elemek inicializ�l�s�ra (�res n�v, 0 pont mindegyik elemre)
	 */
	public void Reset()
	{
		int i; 

		// Ciklus, v�gigmegy a t�mb�n �s v�giginicializ�lja
		for (i = 0; i < MaxStored; i++)
		{
			Names[i] = "";
			Scores[i] = 0;
		}
	}

	/**
	 * Elj�r�s a highscore t�bla file-b�l val� bet�lt�s�re
	 */
	public void Load()
	{
		try
		{
			// Megnyitjuk a file-t olvas�sra
			RandomAccessFile inputfile = new RandomAccessFile(HighScoreFile,"r");

			int i;
			String s = new String();

			for (i = 0; i < MaxStored; i++)
			{
				s = inputfile.readLine();  // Nev beolvasasa
				Names [i] = s;
				s = inputfile.readLine();  // Pontszam beolvasasa
				try {
					Scores [i] = Long.valueOf(s).longValue();  // A beolvasott eredm�ny string sz�mma konvert�l�sa
				} catch (NumberFormatException e)
				{
					// Hiba volt beolvas�skor (pl. a fileban nem szerepelt semmi), ekkor 0 lesz az �rt�k
					Scores [i] = 0; 
				}
			}
			inputfile.close(); // File bez�r�sa

		} catch (IOException e)
		{
			// Hiba t�rt�nt file olvas�s k�zben
			System.out.println("Hiba volt a highscore tabla olvasasa kozben!");
		}

	}

	/**
	 * Elj�r�s a highscore t�bla file-ba val� elment�s�re
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
	 * @param NewName	A j�t�kos neve.
	 * @param NewScore	A j�t�kos pontsz�ma.
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
	 * Elj�r�s a highscore t�bla ki�rat�s�ra
	 */
	public void paint()
	{
		// Vegigmegyunk az eg�sz t�mb�n �s ki�rjuk a k�perny�re az �rt�keket
		for (int i = 0; i < MaxStored; i++)
			if (Scores[i] > 0)
				System.out.println ("highscore " + Names[i] + " " + Scores[i]);
	}

	public void Parse (ourParser parser) throws Exception {

		/**
		 * Ki�rjuk a highscore t�bl�t
		 */
		paint();
	
		while (parser.getNextLine()==ourParser.TT_OK) {

			/**
			 * Ki�rjuk, hogy mit dolgozunk fel..
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
