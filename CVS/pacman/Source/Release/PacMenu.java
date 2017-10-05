package PacMan;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

class gameMenu extends JPanel implements KeyListener{
	mainWindow parent = null;

	private JLabel textWorldName; // Világ neve
	private JLabel textLevelName; // pálya neve
	private JLabel textWNdelimiter; // világ - pálya név elválasztó jel
	private JLabel textTime; // "Idõ" kiírás
	private JLabel textLives; // "Életek" kiírás
	private JLabel textBombs; // "Bombák" kiírás
	private JLabel valTime; // idõ értéke
	private JLabel valLives; // életek értéke
	private JLabel valBombs; // bombák értéke
	private GameCanvas gameView; // a pálya képe

	class GameCanvas extends JComponent {
		Image image;
		public void setImage(Image image) {
			this.image = image;
		}
		public void paint(Graphics g) {
			g.setColor(getBackground());
			if (image==null)
				g.fillRect(0, 0, getWidth(), getHeight());
			else
				g.drawImage(image, 0, 0, this);
		}
		public boolean isFocusTraversable() {
			return isEnabled();
		}
	};

//	private int Time = 0;
//	private int Bombs = 0;
//	private int Lives = 0;
//	private String Level = "level 1";
//	private String World = "WORLD";

	public void setWorld(String World) {
		textWorldName.setText(World);
	}

	public void setLevel(String Level) {
		textLevelName.setText(Level);
	}

	public void setTime(int Time) {
		valTime.setText("" + Time);
	}

	public void setLives(int Lives) {
		valLives.setText("" + Lives);
	}

	public void setBombs(int Bombs) {
		valBombs.setText("" + Bombs);
	}

	public void setGameView(Image gameImage) {
		gameView.setImage(gameImage);
	}




	public void keyPressed(KeyEvent e) {
	}
	public void keyReleased(KeyEvent e) {
	}
	public void keyTyped(KeyEvent e) {
//			System.out.println("keyTyped " + e);
			parent.setScreen(mainWindow.SCR_MAINMENU);
	}
	
	gameMenu(mainWindow parent) {
		this.parent = parent;

		setSize(640,480);
        setLayout(new BorderLayout());



		JPanel header = new JPanel();
			header.setLayout(new GridLayout(1,2));
			JPanel headerwl = new JPanel();
//				headerwl.setLayout(new BorderLayout());
				headerwl.add(textWorldName = new JLabel("World"));
				headerwl.add(textWNdelimiter = new JLabel(":"));
				headerwl.add(textLevelName = new JLabel("Level"));
			header.add(headerwl);
			JPanel headertime = new JPanel();
//				headertime.setLayout(new BorderLayout());
				headertime.add(textTime = new JLabel("Time : "));
				headertime.add(valTime = new JLabel("0"));
			header.add(headertime);
		add(header,BorderLayout.NORTH);
		JPanel footer = new JPanel();
			footer.setLayout(new GridLayout(1,2));
			JPanel footerlife = new JPanel();
//				footerlife.setLayout(new BorderLayout());
				footerlife.add(textLives = new JLabel("Lives : "));
				footerlife.add(valLives = new JLabel("0"));
			footer.add(footerlife);
			JPanel footerbomb = new JPanel();
//				footerbomb.setLayout(new BorderLayout());
				footerbomb.add(textBombs = new JLabel("Bombs : "));
				footerbomb.add(valBombs = new JLabel("0"));
			footer.add(footerbomb);
		add(footer,BorderLayout.SOUTH);
		JPanel center = new JPanel();
			center.add(gameView = new GameCanvas());
		add(center,BorderLayout.CENTER);
		gameView.addKeyListener(this);
     }

	 public Dimension getPreferredSize() {
		 return new Dimension(640,480);
	 }
};

class getHSnameFrame extends JDialog implements ActionListener{

	highscoreMenu parent;
	JTextField text;
	JButton button;
	int namepos;

	public String getName() {
		return text.getText();
	}

	public void actionPerformed(ActionEvent e) {
		dispatchEvent(new WindowEvent(this,WindowEvent.WINDOW_CLOSING));
//		hide();
	}

	getHSnameFrame(int namepos, highscoreMenu parent) {
		super(parent.parent, "Enter HighScore", true); // parent parentja a fõablak, megadjuk az ablak nevét, és hogy Modális, vagyis blokkolja a többi ablakot.. :)
		this.parent = parent;
		this.namepos = namepos;
		JPanel panel = new JPanel();
		panel.add(new JLabel("Enter name:"));
		panel.add(text = new JTextField("", 20));
		panel.add(button = new JButton("OK"));
		getContentPane().add(panel);
		button.addActionListener(this);
		text.addActionListener(this);
		pack();
//		show();
//		setVisible(true);
	}
};

class highscoreMenu extends JPanel implements ActionListener {
	mainWindow parent = null;
	int hsNum = 0;

	/**
	 * A highscore-t tároló file neve
	 */
	final String HighScoreFile = "/highscore.dat";

	/**
	 * A maximalisan tárolható emberek száma
	 */
	final int MaxStored = 10;

	/**
	 * A legjobbak neveinek eltárolására
	 */
	/*private*/ String Names[];

	/**
	 * A legjobbak pontszámainak eltárolásara
	 */
	private Integer Scores[];

	
	JButton prevButton;
	JButton nextButton;
	JLabel currentText;
	JTable hsTable; // nemhasználjuk még, majd az insert HSnél kellhet
	String currentGame;

	public String getName(int index) {
		return Names[index];
	}

	public Integer getScore(int index) {
		return Scores[index];
	}



	public void addHS(int World, int Score) {
		if (hsNum!=World) {
			currentText.setText(currentGame = (String)parent.gameList.get(hsNum));
			Load();
		}
		Insert(Score);
		repaint();
	}


	public void addHS(String World, int Score) {
		int i=0;
		while (!World.equals(parent.gameList.get(i)) && i<parent.gameList.size()) i++;
		if (World.equals(parent.gameList.get(i)))
			addHS(i,Score);
	}

	/**
	 * Eljaras egy uj eredmeny beszurasara. Ha az eredmeny nincs bent a legjobb
	 * 10-ben, nem csinal semmit, egyebkent berakja az uj eredmeny a helyere.
	 *
	 * @param NewName	A játékos neve.
	 * @param NewScore	A játékos pontszáma.
	 */
	public void Insert(int Score)
	{
		int i, j;
		boolean done = false;
		Integer NewScore = new Integer(Score);

		// Vegignezzuk az eddigi eredmenyeket...
		for (i = 0; i < MaxStored; i++)
		{
			// ...ha jobb az uj, mint egy regi...
			if (NewScore.compareTo(Scores[i])>0)
			{
				// ...lejjebbshifteljuk az eddigi tabla egy reszet...
				for (j = MaxStored - 1; j > i; j--)
				{
					Scores[j] = Scores[j-1];
					Names[j] = Names[j-1];
				}
				// ...es beszurjuk az uj eredmenyt
				Scores[i] = NewScore;
				getHSnameFrame a = new getHSnameFrame(i,this);
				a.show();
				Names[i] = a.getName();
				Save();
				break;
			}
		}
	}


	
	/**
	 * Eljárás a highscore-ban lévõ elemek inicializálására (üres név, 0 pont mindegyik elemre)
	 */

	public void Reset()
	{
        final Object[][] resetData = {
            {"VaVa", new Integer(10)},
            {"Viso", new Integer(9)},
            {"lptr", new Integer(8)},
            {"tRehak", new Integer(7)},
            {"akárki", new Integer(6)},
            {"bakker", new Integer(5)},
            {"anonymous", new Integer(4)},
            {"PacMan", new Integer(3)},
            {"DPMF", new Integer(2)},
            {"lúzer", new Integer(1)}
        };
		int i; 

		// Ciklus, végigmegy a tömbön és végiginicializálja
		for (i = 0; i < MaxStored; i++)
		{
			Names[i] = (String)resetData[i][0];
			Scores[i] = (Integer)resetData[i][1];
//			System.out.println("" + i + ":"+Names[i]+":"+Scores[i]);
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
			RandomAccessFile inputfile = new RandomAccessFile(currentGame+HighScoreFile,"r");

			int i;
			String s = new String();

			for (i = 0; i < MaxStored; i++)
			{
				s = inputfile.readLine();  // Nev beolvasasa
				Names [i] = s;
				s = inputfile.readLine();  // Pontszam beolvasasa
//				try {
					Scores [i] = Integer.valueOf(s);  // A beolvasott eredmény string számma konvertálása
//				} catch (NumberFormatException e)
//				{
					// Hiba volt beolvasáskor (pl. a fileban nem szerepelt semmi), ekkor 0 lesz az érték
//					Scores [i] = new Integer(0); 
//				}
			}
			inputfile.close(); // File bezárása

		} catch (/*IO*/Exception e) // bármi bugzik a file körül, akkor a standard HS adatokat nyomjuk be
		{
			Reset();
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
			RandomAccessFile outputfile = new RandomAccessFile(currentGame+HighScoreFile,"rw");

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
		// semmit sem teszünk ha a HighScore mentése közben erreur befigyel
		}

	}



    public void actionPerformed(ActionEvent e) {
		String arg = e.getActionCommand();

		if ("prev".equals(arg)) {
			hsNum = (hsNum==0?parent.gameList.size()-1:hsNum-1);
			currentText.setText(currentGame = (String)parent.gameList.get(hsNum));
			Load();
			repaint();
		} else if ("next".equals(arg)) {
			hsNum = (hsNum==parent.gameList.size()-1?0:hsNum+1);
			currentText.setText(currentGame = (String)parent.gameList.get(hsNum));
			Load();
			repaint();
		} else if ("EXIT".equals(arg)) {
			parent.setScreen(mainWindow.SCR_MAINMENU);
		}
	}

	highscoreMenu(mainWindow parent) {
		this.parent = parent;

		Names = new String[MaxStored];
		Scores = new Integer[MaxStored];
		currentGame = (String)parent.gameList.get(hsNum);
		Load();
		
		JButton b = null;
		setSize(640,480);
        setLayout(new BorderLayout());
		JPanel header = new JPanel();
			header.setLayout(new GridLayout(1,2));
	        header.add(b = new JButton("EXIT"));
			b.addActionListener(this);
			JPanel subheader = new JPanel();
			subheader.add(currentText = new JLabel(currentGame),BorderLayout.CENTER);
			header.add(subheader);
		add(header,BorderLayout.NORTH);
		JPanel footer = new JPanel();
			if (parent.gameList.size()>1) { // ha csak egy world van, akkor nem rakjuk ki a prev/next gombokat
				footer.setLayout(new GridLayout(1,2));
		        footer.add(prevButton = new JButton("prev"));
				prevButton.addActionListener(this);
		        footer.add(nextButton = new JButton("next"));
				nextButton.addActionListener(this);
			}
		add(footer,BorderLayout.SOUTH);
	        hsTable = new JTable(new MyTableModel(this));
			hsTable.setEnabled(false);
//			table.setShowGrid(false);
//			table.getColumnModel().getColumn(0).setPreferredWidth(50);
//			table.getColumnModel().getColumn(1).setPreferredWidth(50);
//			table.setPreferredSize(new Dimension(100,100));
//			table.setMaximumSize(new Dimension(100,100));
		add(hsTable,BorderLayout.CENTER);
     }

	 public Dimension getPreferredSize() {
		 return new Dimension(640,480);
	 }

    class MyTableModel extends AbstractTableModel {
        final String[] columnNames = {"Name", 
                                      "Score"};
		highscoreMenu parent;

		MyTableModel(highscoreMenu parent) {
			super();
			this.parent = parent;
		}

        public int getColumnCount() {
            return columnNames.length;
        }
        
        public int getRowCount() {
            return parent.MaxStored/*data.length*/;
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
			switch (col)
			{
			case 0: 
				return parent.getName(row);
			default:
				return parent.getScore(row);
			}
        }

        /*
         * JTable uses this method to determine the default renderer/
         * editor for each cell.  If we didn't implement this method,
         * then the last column would contain text ("true"/"false"),
         * rather than a check box.
         */
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
	};

};

class selectGameMenu extends JPanel implements ActionListener{
	mainWindow parent = null;

    public void actionPerformed(ActionEvent e) {
	String arg = e.getActionCommand();

	if ("EXIT".equals(arg))
		parent.setScreen(mainWindow.SCR_MAINMENU);
	else parent.setScreen(mainWindow.SCR_GAMEMENU);
	}
	
	selectGameMenu(mainWindow parent) {
		this.parent = parent;

		setSize(640,480);
        setLayout(new GridLayout(parent.gameList.size()+1,1));
		JButton b = null;

		for(int i=0;i<parent.gameList.size();i++) {
				add(b = new JButton((String)parent.gameList.get(i)));
				b.addActionListener(this);
		}
        add(b = new JButton("EXIT"));
		b.addActionListener(this);

     }

	 public Dimension getPreferredSize() {
		 return new Dimension(640,480);
	 }
};

class mainMenu extends JPanel implements ActionListener{
	mainWindow parent = null;

    public void actionPerformed(ActionEvent e) {
	String arg = e.getActionCommand();

	if ("Start game".equals(arg))
		parent.setScreen(mainWindow.SCR_SELECTGAMEMENU);
	else if ("Highscore".equals(arg)){
		parent.setScreen(mainWindow.SCR_HIGHSCOREMENU);
 SwingUtilities.invokeLater(new Runnable() {
     public void run() {
         parent.hsmenu.addHS(0,4200);
     }
 });
		System.out.println("End");}
	else if ("Exit".equals(arg))
		System.exit(0);
	}
	
	mainMenu(mainWindow parent) {
		this.parent = parent;

		setSize(640,480);
        setLayout(new GridLayout(3,1));
		JButton b = null;
		add(b = new JButton("Start game"));
		b.addActionListener(this);
        add(b = new JButton("Highscore"));
		b.addActionListener(this);
        add(b = new JButton("Exit"));
		b.addActionListener(this);
     }

	 public Dimension getPreferredSize() {
		 return new Dimension(640,480);
	 }
};

class mainWindow extends JFrame {
	final static public String SCR_MAINMENU = "main menu";
	final static public String SCR_GAMEMENU = "game menu";
	final static public String SCR_SELECTGAMEMENU = "select game menu";
	final static public String SCR_HIGHSCOREMENU = "highscore menu";

	final static public String gameDescriptorName = "game.description";

	public CardLayout myLayout;

	public Vector gameList;

	private Vector genGameList() {
		File gameDir = new File(".");
		File[] dirList = gameDir.listFiles();
		Vector gameList = new Vector();
		for(int i=0;i<dirList.length;i++)
			if (dirList[i].isDirectory())
				if ((new File(dirList[i],gameDescriptorName)).exists())
					gameList.add(dirList[i].getName());
		if (gameList.size()==0)	{
			System.err.println("NO GAMES FOUND ERROR!!");
			System.exit(-1);
		}
		return gameList;
	}

// http://java.sun.com/docs/books/tutorial/uiswing/layout/card.html
	public void setScreen(String name) {
		System.out.println("setScreen(\"" + name+"\")");
		myLayout.show(getContentPane(),name);

		synchronized (getTreeLock()) {
			int ncomponents = getComponentCount();
		    for (int i = 0 ; i < ncomponents ; i++) {
			Component comp = getComponent(i);
			if (comp.isVisible()) 
				comp.requestFocus();
				comp.transferFocus();
			}
		}

	}

	public highscoreMenu hsmenu;
	mainWindow() {
		gameList = genGameList();

		setSize(640,480);
		JPanel contentPane = new JPanel();
		myLayout = new CardLayout();
		contentPane.setLayout(myLayout);
		contentPane.add((JPanel)new gameMenu(this),SCR_GAMEMENU);
		contentPane.add((JPanel)new mainMenu(this),SCR_MAINMENU);
		contentPane.add((JPanel)new selectGameMenu(this),SCR_SELECTGAMEMENU);
		contentPane.add((JPanel)(hsmenu = new highscoreMenu(this)),SCR_HIGHSCOREMENU);
		setContentPane(contentPane);
		setScreen(SCR_MAINMENU);
		pack();
	}
};

class PacMenu extends Object {
	public static void main(String[] args) {
		mainWindow prog = new mainWindow();
		prog.setVisible(true);
	}
}
