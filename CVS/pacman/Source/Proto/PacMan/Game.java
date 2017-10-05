// $Id: Game.java,v 1.12 2001/04/27 19:22:43 lptr Exp $
// $Date: 2001/04/27 19:22:43 $
// $Author: lptr $

package PacMan;

import java.io.*;
import java.util.*;
import Util.Parser.*;

public class Game extends ProtoObject implements Parsing {

	String dirname;
	Maze currentMaze;
	File gameDir;
	LinkedList levels;
	String playerName = "";

	int score;
	int lives;

	LinkedList output = new LinkedList();

	Game(String dirname, ourParser parser) throws Exception {
		gameDir = new File(dirname);
		levels = new LinkedList();
		String[] temp = gameDir.list();

		if (temp == null)
			throw new IOException("couldn't find directiory: \"" + dirname + "\"");
		for (int i=0; i < temp.length; i++) {
			if (temp[i].endsWith(".lev"))
				levels.addLast(temp[i]);
		}

		score = 0;
		lives = 3;

		try {
			while (levels.size() > 0) {
				currentMaze = new Maze(this, new File (gameDir, (String)levels.removeFirst()).getPath());
				try {
					Parse(parser);
				} catch (NextLevelException e) {
				}
			}
		} catch (EndGameException e) {
		}

		Output("event exitgame");
		DumpOutput();
	}

	void Output(String text) {
		output.addLast((Object)text);
	}

	void DumpOutput() {
		while (output.size() > 0) {
			System.out.println(output.removeFirst());
		}
	}

	public void IncreaseScore(long count) {
		score += count;
	}

	void ShowInfo() {
		Output("game " + score + " " + lives);
		Output("maze " + currentMaze.time);
		currentMaze.OutputItems();
	}

	double ParseDouble (String s, double dvalue) {
		if (s.equals("*"))
			return dvalue;
		else
			return Double.parseDouble(s);
	}

	long ParseLong (String s, long dvalue) {
		if (s.equals("*"))
			return dvalue;
		else
			return Long.parseLong(s);
	}

	int ParseInt (String s, int dvalue) {
		if (s.equals("*"))
			return dvalue;
		else
			return Integer.parseInt(s);
	}

	String ParseString (String s, String dvalue) {
		if (s.equals("*"))
			return dvalue;
		else
			return s;
	}

	public void Parse (ourParser parser) throws Exception {
		DumpOutput();
		while (parser.getNextLine() == ourParser.TT_OK) {
			PrintParsed(parser.lineTokens);

			// megjegyzés

			if (parser.matchLine("remark {%c}")) {
				Output ("/* " + Concat(parser.lineTokens) + "*/");
			} else 

			// kilépés

			if (parser.matchLine("exitgame %c")) {
				playerName = parser.lineTokens[1];
				throw new EndGameException();
			} else

			// normál játék-parancsok

			if (parser.matchLine("setdirection (up|right|down|left)")) {
				currentMaze.pacman.SetDirection(parser.lineTokens[1]);
			} else
			if (parser.matchLine("putbomb")) {
				currentMaze.PutBomb();
			} else
			if (parser.matchLine("step")) {
				try {
					currentMaze.Step();
				} catch (LifeLostException e) {
				} finally {
					ShowInfo();
					Output("step");
					DumpOutput();
				}
			} else 

			// debug

			if (parser.matchLine("show maze")) {
				currentMaze.ShowMaze();
				DumpOutput();
			} else
			if (parser.matchLine("show info")) {
				Output ("/*");
				ShowInfo();
				Output ("*/");
				DumpOutput();
			} else

			// hacking

			if (parser.matchLine("hack nextlevel")) {
				DumpOutput();
				throw new NextLevelException();
			} else
			if (parser.matchLine("hack load %c")) {
				DumpOutput();
				levels.addFirst(parser.lineTokens[2]);
				throw new NextLevelException();
			} else

			// hack values

			if (parser.matchLine("hack score %n")) {
				score = ParseInt(parser.lineTokens[2], score);
			} else
			if (parser.matchLine("hack lives %n")) {
				lives = ParseInt(parser.lineTokens[2], lives);
			} else
			if (parser.matchLine("hack time %n")) {
				currentMaze.time = ParseLong(parser.lineTokens[2], currentMaze.time);
			} else

			// object removal hack

			if (parser.matchLine("hack (monster|bomb|bonus|crystal) %n remove")) {
				boolean found = true;
				int no = Integer.parseInt(parser.lineTokens[2]);
				MazeItem m = currentMaze.GetItem(no);

				if (m == null)
					found = false;
				else {
					if (parser.lineTokens[1].equalsIgnoreCase("monster") && m instanceof Monster)
						currentMaze.monsters.remove(m);
					else
					if (parser.lineTokens[1].equalsIgnoreCase("bomb") && m instanceof Bomb)
						currentMaze.bombs.remove(m);
					else
					if (parser.lineTokens[1].equalsIgnoreCase("bonus") && m instanceof Bonus)
						currentMaze.bonuses.remove(m);
					else
					if (m instanceof Crystal)
						currentMaze.crystals.remove(m);
					else
						found = false;
				}

				if (!found)
					Output ("error " + parser.lineTokens[1] + " " + no + " does not exist");
			} else

			// pacman hack handlink

			if (parser.matchLine("hack pacman set (%n|*) (%n|*) (up|right|down|left|stop|*) (%n|*)")) {
				currentMaze.pacman.pos.x = ParseDouble(parser.lineTokens[3], currentMaze.pacman.pos.x);
				currentMaze.pacman.pos.y = ParseDouble(parser.lineTokens[4], currentMaze.pacman.pos.y);
				currentMaze.pacman.SetDirection (ParseString (parser.lineTokens[5], Coordinates.DIRS[currentMaze.pacman.direction]));
				currentMaze.pacman.hackBombs(ParseInt(parser.lineTokens[6], currentMaze.pacman.bombs));
			} else

			// monster hacking

			if (parser.matchLine("hack monster (auto|manual)")) {
				currentMaze.debugAutoMove = parser.lineTokens[2].equalsIgnoreCase("auto");
			} else
			if (parser.matchLine("hack monster create (clever|dumb) %n %n %n")) {
				Coordinates c = new Coordinates (
					Double.parseDouble (parser.lineTokens[4]),
					Double.parseDouble (parser.lineTokens[5]));
				double speed = Double.parseDouble (parser.lineTokens[6]);

				if (parser.lineTokens[3].equalsIgnoreCase("clever"))
					currentMaze.monsters.add(new CleverMonster(currentMaze, c, speed));
				else
					currentMaze.monsters.add(new DumbMonster(currentMaze, c, speed));
			} else
			if (parser.matchLine("hack monster %n set (%n|*) (%n|*) (up|right|down|left|stop|*)")) {
				int no = Integer.parseInt(parser.lineTokens[2]);
				Monster m = currentMaze.GetMonster(no);
				if (m == null)
					Output ("error monster " + no + " does not exist");
				else {
					m.pos.x = ParseDouble(parser.lineTokens[4], m.pos.x);
					m.pos.y = ParseDouble(parser.lineTokens[5], m.pos.y);
					m.SetDirection (ParseString (parser.lineTokens[6], Coordinates.DIRS[m.direction]));
				}
			} else
			if (parser.matchLine("hack monster %n kill %n")) {
				int no = Integer.parseInt(parser.lineTokens[2]);
				Monster m = currentMaze.GetMonster(no);
				if (m == null)
					Output ("error monster " + no + " does not exist");
				else
					m.Deactivate(Long.parseLong(parser.lineTokens[4]));
			} else
			if (parser.matchLine("hack monster %n reborn")) {
				int no = Integer.parseInt(parser.lineTokens[2]);
				Monster m = currentMaze.GetMonster(no);
				if (m == null)
					Output ("error monster " + no + " does not exist");
				else {
					m.timeToRebirth = 0;
					m.Reborn();
				}
			} else

			// hack bomb handling

			if (parser.matchLine("hack bomb create %n %n %n")) {
				currentMaze.bombs.add(
					new Bomb(
						currentMaze, 
						new Coordinates (
							Double.parseDouble (parser.lineTokens[3]),
							Double.parseDouble (parser.lineTokens[4])),
						Long.parseLong (parser.lineTokens[5])));
			} else
			if (parser.matchLine("hack bomb %n (activate|deactivate)")) {
				int no = Integer.parseInt(parser.lineTokens[2]);
				Bomb b = currentMaze.GetBomb(no);
				if (b == null)
					Output ("error bomb " + no + " does not exist");
				else {
					if (parser.lineTokens[3].equalsIgnoreCase("activate"))
						b.Activate();
					else
						b.active = false;
				}
			} else
			if (parser.matchLine("hack bomb %n set (%n|*) (%n|*) (%n|*)")) {
				int no = Integer.parseInt(parser.lineTokens[2]);
				Bomb b = currentMaze.GetBomb(no);
				if (b == null)
					Output ("error bomb " + no + " does not exist");
				else {
					b.pos.x = ParseDouble(parser.lineTokens[4], b.pos.x);
					b.pos.y = ParseDouble(parser.lineTokens[5], b.pos.y);
					b.timeToLive = ParseLong(parser.lineTokens[6], b.timeToLive);
				}
			} else

			// hack bonus handling

			if (parser.matchLine("hack bonus (auto|manual)")) {
				currentMaze.debugAutoBonuses = parser.lineTokens[2].equalsIgnoreCase("auto");
			} else
			if (parser.matchLine("hack bonus create (time|score|bomb) %n %n %n %n")) {
				Coordinates c = new Coordinates (
					Double.parseDouble (parser.lineTokens[4]),
					Double.parseDouble (parser.lineTokens[5]));
				long time = Long.parseLong(parser.lineTokens[6]);
				long value = Long.parseLong(parser.lineTokens[7]);

				Bonus b;
				if (parser.lineTokens[3].equalsIgnoreCase("time"))
					b = new TimeBonus (currentMaze, c, time, value);
				else
				if (parser.lineTokens[3].equalsIgnoreCase("score"))
					b = new ScoreBonus (currentMaze, c, time, value);
				else
					b = new BombBonus (currentMaze, c, time, (int)value);

				currentMaze.bonuses.add(b);
			} else
			if (parser.matchLine("hack bonus %n set (%n|*) (%n|*) (%n|*) (%n|*)")) {
				int no = Integer.parseInt(parser.lineTokens[2]);
				Bonus b = currentMaze.GetBonus(no);
				if (b == null)
					Output ("error bonus " + no + " does not exist");
				else {
					b.pos.x = ParseDouble(parser.lineTokens[4], b.pos.x);
					b.pos.y = ParseDouble(parser.lineTokens[5], b.pos.y);
					b.timeToLive = ParseLong(parser.lineTokens[6], b.timeToLive);
					b.hackValue(ParseLong(parser.lineTokens[7], b.hackGetValue()));
				}
			} else

			// hack crystal

			if (parser.matchLine("hack crystal create %n %n")) {
				currentMaze.crystals.add(
					new Crystal(
						currentMaze, 
						new Coordinates (
							Double.parseDouble (parser.lineTokens[3]),
							Double.parseDouble (parser.lineTokens[4]))));
			} else
			if (parser.matchLine("hack crystal %n set (%n|*) (%n|*)")) {
				int no = Integer.parseInt(parser.lineTokens[2]);
				Crystal c = currentMaze.GetCrystal(no);
				if (c == null)
					Output ("error crystal " + no + " does not exist");
				else {
					c.pos.x = ParseDouble(parser.lineTokens[4], c.pos.x);
					c.pos.y = ParseDouble(parser.lineTokens[5], c.pos.y);
				}
			} else


			// hack wall
			
			if (parser.matchLine("hack wall (on|off) %n %n (up|right|down|left) (single|both)")) {
				boolean set = parser.lineTokens[2].equalsIgnoreCase("on");

				Coordinates p = new Coordinates (
					Integer.parseInt (parser.lineTokens[3]),
					Integer.parseInt (parser.lineTokens[4]));

				int dir = Coordinates.ConvertDirection(parser.lineTokens[5]);

				boolean both = parser.lineTokens[6].equalsIgnoreCase("both");

				if (p.x < 0 || p.x > currentMaze.mazeWidth ||
					p.y < 0 || p.y > currentMaze.mazeHeight)
					Output ("error invalid wall addressed");
				else {
					FieldItem f = currentMaze.getFieldAt(p);
					FieldItem f2 = null;
					if (both) {
						Coordinates p2 = p.GetNeighbour(dir);
						if (p2.x < 0 || p2.x > currentMaze.mazeWidth ||
							p2.y < 0 || p2.y > currentMaze.mazeHeight)
							both = false;
						else
							f2 = currentMaze.getFieldAt(p2);
					}

					if (set) {
						f.setWall(dir);
						if (both)
							f2.setWall(Coordinates.OppositeDirection(dir));
					} else {
						f.clearWall(dir);
						if (both)
							f2.clearWall(Coordinates.OppositeDirection(dir));
					}
				}
			} else {

				Output("warning parse error at " + parser.lineno() + ": " + Concat(parser.lineTokens));
				DumpOutput();
			}
		}
		DumpOutput();
		ThrowParseException("unexpected end of file", parser.lineno());
	}

	public String GetPlayerName() {
		return playerName;
	}

	public long GetScore() {
		return score;
	}
}