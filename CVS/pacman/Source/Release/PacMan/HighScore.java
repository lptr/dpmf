// $Id: HighScore.java,v 1.8 2001/05/31 11:56:11 vava Exp $
// $Date: 2001/05/31 11:56:11 $
// $Author: vava $

package PacMan;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

/**
 * @author: vava
 */

class GetHSNameFrame extends JDialog implements ActionListener{

	JTextField text;
	JButton button;
	int namepos;

	public String getName() {
		return text.getText();
	}

	public void actionPerformed(ActionEvent e) {
		dispatchEvent(new WindowEvent(this,WindowEvent.WINDOW_CLOSING));
	}

	GetHSNameFrame(int namepos, HighscoreMenu parent) {
		super(parent.parent, "Enter HighScore", true); // parent parentja a fõablak, megadjuk az ablak nevét, és hogy Modális, vagyis blokkolja a többi ablakot.. :)
		this.namepos = namepos;
		JPanel panel = new PMPanel();
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
}

class JHSLine extends JComponent {
	String name;
	String score;

	JHSLine() {
		name = "";
		score = "";
	}

	public void setName(String name) {
		this.name = name.toLowerCase();
	}

	public void setScore(Integer score) {
		this.score = "" + score;
	}

	public void setData(String name, Integer score) {
		setName(name);
		setScore(score);
		repaint();
	}

	public void paint(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(0,0,640,28);
		g.setColor(Color.yellow);
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setFont(SFBurlingtonFont.font.deriveFont((float)18));
		int nameWidth = (int)g2.getFontMetrics().stringWidth(name);
		g2.drawString(name,64+5,24);
		g2.setFont(SFBurlingtonFont.font.deriveFont((float)18));
		int scoreWidth = (int)g2.getFontMetrics().stringWidth(score);
		g2.drawString(score,64+507-scoreWidth,24);
	}
	
	public Dimension getPreferredSize() {
		return new Dimension (640,28);
	}
}

class HighscoreMenu extends PMPanel implements ActionListener {
	MainWindow parent = null;
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

	/**
	 * A megjelenítést végzõ sorok
	 */
	private JHSLine HSLine[];
	
	JButton prevButton;
	JButton nextButton;
	JLabel currentText;
	JTable hsTable; // nem használjuk még, majd az insert HS-nél kellhet
	String currentGame;

	public void refreshLines() {
		for (int i=0;i<10 ;i++ )
			HSLine[i].setData(Names[i],Scores[i]);
	}
	
	public String getName(int index) {
		return Names[index];
	}

	public Integer getScore(int index) {
		return Scores[index];
	}

	public void addHS(int World, int Score) {
		if (hsNum!=World) {
			currentText.setText(currentGame = (String)parent.gameList.get(World));
			hsNum = World;
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
				GetHSNameFrame a = new GetHSNameFrame(i,this);
				a.show();
				Names[i] = a.getName();
				Save();
				refreshLines();
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
            {"lptr", new Integer(10)},
            {"VaVa", new Integer(9)},
            {"Viso", new Integer(8)},
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
			RandomAccessFile inputfile = new RandomAccessFile(new File ("worlds", currentGame + ".highscores"),"r");

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

	refreshLines();
	}

	/**
	 * Eljárás a highscore tábla file-ba való elmentésére
	 */
	public void Save()
	{
		try
		{
			// Megnyitjuk a file-t irasra
			RandomAccessFile outputfile = new RandomAccessFile(new File ("worlds", currentGame + ".highscores"),"rw");

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
		} else if ("exit".equals(arg)) {
			parent.setScreen(MainWindow.SCR_MAINMENU);
		}
	}

	HighscoreMenu(MainWindow parent) {
		this.parent = parent;
		int i;

		Names = new String[MaxStored];
		Scores = new Integer[MaxStored];
		HSLine = new JHSLine[MaxStored];
		for (i=0; i<MaxStored; i++)
			HSLine[i] = new JHSLine();
		currentGame = (String)parent.gameList.get(hsNum);
		Load();
		
		JButton b = null;
		mySetSize(640,480);
		setBackground(Color.black);
		setForeground(Color.yellow);

        setLayout(new BorderLayout());
		JPanel header = new PMPanel(640,48);
			header.setLayout(new BorderLayout());
			header.add(currentText = new PMLabel(currentGame.toLowerCase(),SwingConstants.CENTER,36),BorderLayout.CENTER);
			currentText.setSize(640,48);
		add(header,BorderLayout.NORTH);
		JPanel footer = new PMPanel(640,48);
			if (parent.gameList.size()>1) { // ha csak egy world van, akkor nem rakjuk ki a prev/next gombokat
				footer.setLayout(new GridLayout(1,3));
		        footer.add(prevButton = new PMButton("prev"));
				prevButton.setSize(320,48);
				prevButton.addActionListener(this);
		        footer.add(b = new PMButton("exit",24));
				b.setSize(160,48);
				b.addActionListener(this);
		        footer.add(nextButton = new PMButton("next"));
				nextButton.setSize(320,48);
				nextButton.addActionListener(this);
			} else {
				footer.setLayout(new BorderLayout());
		        footer.add(b = new PMButton("exit",24),BorderLayout.CENTER);
				b.setSize(160,48);
				b.addActionListener(this);
			}
		add(footer,BorderLayout.SOUTH);
		JPanel center = new PMPanel(640,280);
			center.setLayout(new GridLayout(MaxStored,1));
			for (i=0;i<MaxStored ;i++ )
				center.add(HSLine[i]);
		add(center,BorderLayout.CENTER);
    }

	public Dimension getPreferredSize() {
		return new Dimension(640,480);
	}

}

