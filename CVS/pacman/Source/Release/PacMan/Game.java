// $Id: Game.java,v 1.18 2001/05/31 02:21:45 lptr Exp $
// $Date: 2001/05/31 02:21:45 $
// $Author: lptr $

package PacMan;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.awt.image.*;
import java.awt.event.*;
import ImageCache.*;
import java.util.zip.*;

class GameCanvas extends JComponent {
	BufferedImage image;
	int Counter = 0;

	public void paint(Graphics g) {
		if (image!=null)
			g.drawImage(image, 0, 0, this);
	}

	public boolean isFocusTraversable() {
		return isEnabled();
	}

	public GameCanvas(BufferedImage image) {
		this.image = image;
	}

	public BufferedImage swapImages(BufferedImage other) {
		BufferedImage temp = image;
		image = other;
		repaint();
		return temp;
	}

	public Dimension getPreferredSize() {
		return new Dimension (image.getWidth(), image.getHeight());
	}
}

class FrameCounter {
	int frames;

	FrameCounter() {
		frames = 0;
	}

	synchronized void increase() {
		frames++;
	}

	synchronized int decrease() {
		if (frames > 0)
			frames--;
		return frames;

	}

	int count() {
		return frames;
	}

	synchronized void reset() {
		frames = 0;
	}

	public String toString() {
		return Integer.toString(frames);
	}
}

class Game extends Thread implements KeyListener {

	/* compontents for the UI */

	JPanel parent;
	JPanel GamePanel;
	MainWindow wMain;

	private GameCanvas canvas;
	private JLabel textWorldName; // Világ neve
	private JLabel textLevelName; // pálya neve
	private JLabel textWNdelimiter; // világ - pálya név elválasztó jel
	private JLabel valTime; // idõ értéke
	private JLabel valScore; // idõ értéke
	private JLabel valLives; // életek értéke
	private JLabel valBombs; // bombák értéke
	private JComponent gameView; // a pálya képe

	/**
	 * Az tartja nyilván, hogy hány képkockányival van lemaradva a mozgás. A szinkronizáció
	 * miatt van szükség külön objektumra.
	 */
	FrameCounter framesLeft;

	/**
	 * Puffer a kirajzoláshoz. Ide képzõdik a kép, a {@link GameCanvas#paint(Graphics g)}
	 * metódus ezt rakja ki, ha van rá ideje.
	 */
	BufferedImage frameBuffer;
	BufferedImage frame;
	private Graphics2D bufferGraphics;

	/* data */

	ImageCache imageCache;
	ZipFile worldData;

	int worldNum;
	String worldName;
	Maze currentMaze;
	LinkedList levels;

	/* gameflow */

	javax.swing.Timer timer;
	
	int score;
	int lives;

	final int GFX_X = 576;
	final int GFX_Y = 384;

	/*
		A játék aktuális állapotát reprezentálja.	
	 */
	private int state;
	/*
		Ezt a változót arra fogjuk használni, hogyha egy állapotban pause-t kér a user, akkor ezt jelezze. ugyanis a
		GAMEPAUSED állapotba csak a PLAYIMG állapotból kerülhetünk (vagy olyan esetben, amikor épp arra történne váltás...)
	 */
	private boolean pauseit;
	/*
		A world indulásakor, be kell tölteni a hozzá szükséges adatokat. Amíg ez nem történt meg, ebben az
		állapotban tartózkodunk..
	 */
	static final int STATE_LOADING_WORLD = 0;
	/*
		Egy pálya indulásakor be kell tölteni annak adatait, a töltés alatt ebben az állapotban tartózkodunk
	 */
	static final int STATE_STARTING_LEVEL = 1;
	static final int STATE_LOADING_LEVEL = 10;
	/*
		Játék közben ez az aktív állapot
	 */
	static final int STATE_PLAYING = 2;
	/*
		Level sikeres vége esetén ide kerülünk.
	 */
	static final int STATE_ENDING_LEVEL = 3;
	/*
		World sikeres vége esetén ide kerülünk
	 */
	static final int STATE_ENDING_WORLD = 4;
	/*
		Amennyiben a játék véget ér az életek elvesztése miatt, akkor ez az állapot aktív
	 */
	static final int STATE_GAMEOVER = 5;
	/*
		ha életet vesztünk, mert letelt az idõ, vagy hasonló, akkor ide kerülünk
	 */
	static final int STATE_RESTARTING_LEVEL = 6;
	/*
		a játékból történõ hírtelen kilépés esetén ebbe lépünk. pl ESC
	 */
	static final int STATE_TERMINATING = 7;
	/*
		Game pauseálása.
	 */
	static final int STATE_GAMEPAUSED = 8;



	/* keyboard handling */

	public void keyPressed(KeyEvent e) {
		switch (state) {
			case STATE_PLAYING :
				switch (e.getKeyCode()) {
					case KeyEvent.VK_LEFT :
						currentMaze.SetDirection(Coordinates.DIR_LEFT);
						break;
					case KeyEvent.VK_RIGHT :
						currentMaze.SetDirection(Coordinates.DIR_RIGHT);
						break;
					case KeyEvent.VK_UP :
						currentMaze.SetDirection(Coordinates.DIR_UP);
						break;
					case KeyEvent.VK_DOWN :
						currentMaze.SetDirection(Coordinates.DIR_DOWN);
						break;
					case KeyEvent.VK_SPACE :
						currentMaze.PutBomb();
						break;
					case KeyEvent.VK_ESCAPE :
						state = STATE_TERMINATING;
						break;
					case KeyEvent.VK_P :
						state = STATE_GAMEPAUSED;
						break;
				}
				break;
			case STATE_GAMEPAUSED:
				switch (e.getKeyCode())	{
					case KeyEvent.VK_P :
						clearBuffer();
						framesLeft.reset();
						state = STATE_PLAYING;
						break;
				}
				break;
		}
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE && state!=STATE_ENDING_WORLD)
			state = STATE_TERMINATING;
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	public Game(JPanel parent, MainWindow wMain, int worldNum) throws Exception {
		this.parent = parent;
		this.wMain = wMain;
		this.worldNum = worldNum;

		worldName = (String)wMain.gameList.get(worldNum);
		levels = new LinkedList();

		worldData = new ZipFile(new File("worlds", worldName + ".world"));
		for (Enumeration e = worldData.entries() ; e.hasMoreElements() ;) {
			ZipEntry file = (ZipEntry)e.nextElement();
//			System.out.println ("zip element: " + file.getName());
			if (file.getName().endsWith(".lev")) {
				levels.addLast(file);
			}
		}

		score = 0;
		lives = 3;

		/* build user interface */

//		System.out.println("i was called, i was called!!! (\"" + worldName +"\")");

		parent.setLayout(new FlowLayout());
		GamePanel = new PMPanel();
		GamePanel.setSize(640, 480);
		GamePanel.setLayout(new BorderLayout());

		JPanel header = new PMPanel(640,32);
			header.setLayout(new GridLayout(1,2));
			JPanel headerwl = new PMPanel(400,32);
				headerwl.add(textWorldName = new PMLabel("world", SwingConstants.RIGHT));
				textWorldName.setVerticalTextPosition(SwingConstants.BOTTOM);
				((PMLabel)textWorldName).mySetSize(150,32);
				PMLabel fasz;
				headerwl.add(fasz = new PMLabel(":", SwingConstants.CENTER));
//				fasz.mySetSize(20,32);
				headerwl.add(textLevelName = new PMLabel("Level", SwingConstants.LEFT));
//				((PMLabel)textLevelName).mySetSize(170,32);
			header.add(headerwl);
			JPanel headertime = new PMPanel(240,32);
				headertime.add(new PMLabel("Time: "));
				headertime.add(valTime = new SFLabel("0", SwingConstants.CENTER));
				((SFLabel)valTime).mySetSize(64,32);
			header.add(headertime);
		GamePanel.add(header, BorderLayout.NORTH);

		JPanel footer = new PMPanel(640,32);
			footer.setLayout(new GridLayout(1,3));
			JPanel footerlife = new PMPanel(213,32);
				footerlife.add(new PMLabel("Lives: "));
				footerlife.add(valLives = new SFLabel("0", SwingConstants.CENTER));
				((SFLabel)valLives).mySetSize(64,32);
			footer.add(footerlife);
			JPanel footerscore = new PMPanel(214,32);
				footerscore.add(new PMLabel("Score: "));
				footerscore.add(valScore = new SFLabel("0", SwingConstants.CENTER));
				((SFLabel)valScore).mySetSize(64,32);
			footer.add(footerscore);
			JPanel footerbomb = new PMPanel(213,32);
				footerbomb.add(new PMLabel("Bombs: "));
				footerbomb.add(valBombs = new SFLabel("0", SwingConstants.CENTER));
				((SFLabel)valBombs).mySetSize(64,32);
			footer.add(footerbomb);
		GamePanel.add(footer, BorderLayout.SOUTH);

		frameBuffer = new BufferedImage(GFX_X, GFX_Y, BufferedImage.TYPE_INT_RGB);
		frame = new BufferedImage(GFX_X, GFX_Y, BufferedImage.TYPE_INT_RGB);

		canvas = new GameCanvas(frame);
		canvas.addKeyListener(this);
		GamePanel.add(canvas, BorderLayout.CENTER);

		textLevelName.setText(worldName); // a világ neve

		parent.add(GamePanel);

		canvas.requestFocus();
		canvas.transferFocus();

		textWorldName.setText(worldName.toLowerCase());
		textLevelName.setText("Loading");

		parent.validate();
//		parent.repaint();

		/* load images */
		imageCache = new ImageCache(worldData, canvas);
//		while (!imageCache.getState()) {
//			sleep(50);
//		}

		/* get the timer */
		framesLeft = new FrameCounter();
		timer = new javax.swing.Timer(40, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				synchronized (framesLeft) {
					framesLeft.increase();
					interrupt();
				}
			}
		});
		
		start();
	}

	private void writeMessage(String msg) {
		Graphics fbuff = frameBuffer.createGraphics();
		fbuff.setColor(Color.black);
		fbuff.fillRect(0,0,GFX_X,GFX_Y);
		fbuff.setColor(Color.white);
		fbuff.drawString(msg,GFX_X/2-50,GFX_Y/2);
		frameBuffer = canvas.swapImages(frameBuffer);
	}

	private void clearBuffer() {
		{Graphics fbuff = frameBuffer.createGraphics();
		fbuff.setColor(Color.black);
		fbuff.fillRect(0,0,GFX_X,GFX_Y);
		frameBuffer = canvas.swapImages(frameBuffer);}
		{Graphics fbuff = frameBuffer.createGraphics();
		fbuff.setColor(Color.black);
		fbuff.fillRect(0,0,GFX_X,GFX_Y);
		frameBuffer = canvas.swapImages(frameBuffer);}
	}
	
	public void run() {
		boolean showmustgoon = true;
		framesLeft.reset();
		state = STATE_LOADING_WORLD;
		timer.start();
		while (showmustgoon) {
//			System.out.print(state);
			switch (state) {

				case STATE_LOADING_WORLD: {
					final String[] msg = {"Loading","Loading .","Loading . .","Loading . . ."};
					int i = (framesLeft.count() / 5) % 4;
					writeMessage(msg[i]);
				}
					if (imageCache.getState() && framesLeft.count()>20) {
						state = STATE_STARTING_LEVEL;
						framesLeft.reset();
					}
					break;
				case STATE_STARTING_LEVEL:
					try {
						ZipEntry levelEntry = (ZipEntry)levels.removeFirst();
						String levelName = levelEntry.getName();
						String levelText;
						try {
							levelText = levelName.substring(3,levelName.length()-4).toLowerCase();
						} catch (IndexOutOfBoundsException e) { 
							levelText = "unknown";
						}
						textLevelName.setText(levelText); // pálya neve
						try {
							System.out.println ("bg: " + levelName.substring(0, levelName.lastIndexOf(".")) + ".png");
							imageCache.loadBG(worldData, worldData.getEntry(
								levelName.substring(0, levelName.lastIndexOf(".")) + ".png"));
							currentMaze = new Maze(
								this, 
								worldData.getInputStream(levelEntry),
								imageCache,
								bufferGraphics);
						} catch (IOException e) {
							System.out.println("could not load level " + levelName);
							showmustgoon = false;
							break;
						}
						state = STATE_LOADING_LEVEL;
						framesLeft.reset();
					} catch (BadDataException e) {
						state = STATE_TERMINATING;
						framesLeft.reset();
					}
					break;

				case STATE_LOADING_LEVEL:{
					final String[] msg = {"Loading level","Loading level .","Loading level . .","Loading level . . ."};
					int i = (framesLeft.count() / 5) % 4;
					writeMessage(msg[i]);
				}
					if (imageCache.getState()) {
						clearBuffer();
						state = STATE_PLAYING;
						framesLeft.reset();

					}
					break;

				case STATE_PLAYING:
					try {
						if (framesLeft.count() > 0) {
							int count;
							synchronized (framesLeft) {
								count = framesLeft.count();
								framesLeft.reset();
							}
							try {
								for (; count > 0; count--)
									currentMaze.Step();
								} catch (LifeLostException e) {
									/* PACMAM lehalt */
									lives--;
									if (lives > 0)
										state = STATE_RESTARTING_LEVEL;
									else
										state = STATE_GAMEOVER;
								} catch (EndGameException e) {
									/* TIME's UP */
									lives--;
									if (lives > 0)
										state = STATE_RESTARTING_LEVEL;
									else
										state = STATE_GAMEOVER;
								}
							{
								int min = (int)(currentMaze.time / 25 / 60);
								int sec = ((int)(currentMaze.time / 25)) % 60;
								valTime.setText("" + min + ":" + (sec<10?"0":"") + sec);
							}
							valLives.setText("" + lives);
							valScore.setText("" + score);
							if (currentMaze.pacman!=null)
								valBombs.setText("" + currentMaze.pacman.bombs);
							else
								valBombs.setText("0");
							Graphics2D gfx = frameBuffer.createGraphics();
							gfx.setClip(0,0,currentMaze.mazeWidth*Maze.CELLSIZE,currentMaze.mazeHeight*Maze.CELLSIZE);
							gfx.translate((GFX_X-currentMaze.mazeWidth*Maze.CELLSIZE)/2,(GFX_Y-currentMaze.mazeHeight*Maze.CELLSIZE)/2);
							currentMaze.paint(gfx, imageCache);
							frameBuffer = canvas.swapImages(frameBuffer);
						}
					} catch (NextLevelException e) {
						state = STATE_ENDING_LEVEL;
						framesLeft.reset();
					}
					break;
			
				case STATE_RESTARTING_LEVEL:
						{Graphics fbuff = frameBuffer.createGraphics();
						fbuff.setColor(Color.black);
						fbuff.fillRect(0,0,GFX_X,GFX_Y);
						frameBuffer = canvas.swapImages(frameBuffer);}
						{Graphics fbuff = frameBuffer.createGraphics();
						fbuff.setColor(Color.black);
						fbuff.fillRect(0,0,GFX_X,GFX_Y);
						frameBuffer = canvas.swapImages(frameBuffer);}
						currentMaze.RestartMaze();
						state = STATE_PLAYING;
						framesLeft.reset();
					break;
				
				case STATE_ENDING_LEVEL:
					if (currentMaze.time>0) {
						currentMaze.time-=100;
						if (currentMaze.time<0)
							currentMaze.time=0;
						if (framesLeft.count() % 2 == 0)
							score+=5;
						int min = (int)(currentMaze.time / 25 / 60);
						int sec = ((int)(currentMaze.time / 25)) % 60;
						valTime.setText("" + min + ":" + (sec<10?"0":"") + sec);
						valScore.setText("" + score);
					}
					else {
						if (levels.size()==0) {
							state = STATE_ENDING_WORLD;
							framesLeft.reset();
						} else 
							state = STATE_STARTING_LEVEL;
					}		
					break;

				case STATE_ENDING_WORLD:
					writeMessage("Congratulations! You WIN!!");
					if (framesLeft.count()>25)
						showmustgoon = false;
					break;

				case STATE_GAMEPAUSED:
					writeMessage("GAME PAUSED");
					break;

				case STATE_GAMEOVER:
					writeMessage("GAME OVER");
					if (framesLeft.count()>25)
						showmustgoon = false;
					break;

				case STATE_TERMINATING:
					showmustgoon = false;
					break;

			}
			try {
				sleep(999); /* tkp a végtelenségig should we wait :) */
			} catch (InterruptedException e) {
			}
		}
		timer.stop();
		canvas.removeKeyListener(this);
		if (state!=STATE_TERMINATING) {
			wMain.setScreen(MainWindow.SCR_HIGHSCOREMENU);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					wMain.hsmenu.addHS(worldNum,score);
				}
			});
		} else
			wMain.setScreen(MainWindow.SCR_MAINMENU);
		parent.remove(GamePanel);
	}
			
/*		}
		try {
			try {
				while (levels.size() > 0) {
					currentMaze = new Maze(this, new File (gameDir, (String)levels.removeFirst()).getPath(), imageCache, bufferGraphics);
					framesLeft.reset();
					timer.restart();
			
					try {
						GameLoop();
					} catch (NextLevelException e) {
					} finally {
						timer.stop();
					}
				}
			} catch (EndGameException e) {
				wMain.setScreen(MainWindow.SCR_MAINMENU);
			}
		} catch (InterruptedException e) {
			System.out.println ("hey man, i was interrupted! damn.. that was stupid.");
		} catch (BadDataException e)	{
			System.out.println ("oh, my god! something nasty has happened:\n" + e);
		}

		canvas.removeKeyListener(this);
		parent.remove(GamePanel);
	}*/

/*	public String GetPlayerName() {
		return playerName;
	}*/

	public void IncreaseScore(long count) {
		score += count;
	}

	public long GetScore() {
		return score;
	}
}