// $Id: Main_program.java,v 1.6 2001/04/27 03:15:44 lptr Exp $
// $Date: 2001/04/27 03:15:44 $
// $Author: lptr $

package PacMan;

import java.lang.*;
import java.io.*;
import Util.Parser.*;


/**
 * Ez az interf�sz azzal a tulajdons�ggal ruh�z fel egy objektuomt,
 * hogy tud bemenetet parsolni. */
interface Parsing {
	void Parse(ourParser parser) throws Exception;
}

class ProtoObject {
	String Concat (String[] tokens) {
		String line = "";

		for (int i = 0; i < tokens.length; i++) {
			line += tokens[i] + " ";
		}

		return line;
	}

	void Output(String s) {
		System.out.println (s);
	}

	void DumpOutput() {
	}

	void PrintParsed(String[] tokens) {
		Output ("/* [ " + getClass().getName() + "] command executed: " + Concat(tokens) + "*/");
	}

	void ThrowParseException(String line, int lineno) throws Exception {
		Output ("error parse " + lineno + " " + line);
		DumpOutput();
		throw new Exception("parse error at line " + lineno + ": " + line);
	}

	void ThrowParseException(String[] tokens, int lineno) throws Exception {
		ThrowParseException(Concat(tokens), lineno);
	}
}

/**
 * F�program
 *
 * @author L�ci
 */
public class Main_program extends ProtoObject implements Parsing {

	public void Parse (ourParser parser) throws Exception {

		HighScore highScore = new HighScore();

		/**
		 * Kirakjuk a men�-t, mert a f�men�ben vagyunk, elv�gre..
		 */
		Output ("showmenu");
	
		while (parser.getNextLine()==ourParser.TT_OK) {

			/**
			 * Ki�rjuk, hogy mit dolgozunk fel..
			 */

			PrintParsed(parser.lineTokens);

			if (parser.matchLine("remark {%c}")) {
				Output ("/* " + Concat(parser.lineTokens) + "*/");
				break;
			} else 
			if (parser.matchLine("startgame %c")) {
				Game game = new Game(parser.lineTokens[1], parser);
				highScore.Insert(game.GetPlayerName(), game.GetScore());
			} else 
			if (parser.matchLine("highscore")) {
				highScore.Parse(parser);
			} else 
			if (parser.matchLine("exit")) {
				Output ("event exit");
				break;
			} else {
				Output("warning parse error at " + parser.lineno() + ": " + Concat(parser.lineTokens));
			}

			/**
			 * Kirakjuk a men�-t, mert a f�men�ben vagyunk megint, elv�gre..
			 */
			System.out.println ("showmenu");
		}
	}

	Main_program() throws Exception {
		Parse (new ourParser (System.in));
	}

	/*
	 * A bel�p�si pont. A java rendszer ezt a met�dust h�vja meg indul�skor.
	 * Itt k�vetkezik be a Main_program oszt�ly p�ld�nyos�t�sa.
	 *
	 * @param   args  parancssori param�terek (nem haszn�lt)
	 */
	public static void main (String args[]) throws Exception {
		new Main_program();
		System.exit(0);
	}
}