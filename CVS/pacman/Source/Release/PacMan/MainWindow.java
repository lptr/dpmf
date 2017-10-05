// $Id: MainWindow.java,v 1.17 2001/05/31 11:49:24 lptr Exp $
// $Date: 2001/05/31 11:49:24 $
// $Author: lptr $

package PacMan;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.jar.*;
import java.net.*;


/**
 * @author: lptr
 */

/**
 * @author: vava
 */

class PacFont extends Object {
	public static Font font;
	PacFont() {
		try { 
			font = Font.createFont(Font.TRUETYPE_FONT,getClass().getResourceAsStream("/data/pacfont.ttf"));
//			JarFile a = new JarFile("PacMan.jar");
//			font=Font.createFont(Font.TRUETYPE_FONT,a.getInputStream(a.getEntry("data/pacfont.ttf")));
		}
		catch(Exception e) {
			System.err.println("Missing or corrupt font file!!");
			e.printStackTrace();
			System.exit(-2);
		}
	}
}

class SFBurlingtonFont extends Object {
	public static Font font;
	SFBurlingtonFont() {
		try { 
			font=Font.createFont(Font.TRUETYPE_FONT,getClass().getResourceAsStream("/data/pacnum.ttf"));
//			font=Font.createFont(Font.TRUETYPE_FONT,new FileInputStream(new File("pacnum.ttf")));
//			JarFile a = new JarFile("PacMan.jar");
//			font=Font.createFont(Font.TRUETYPE_FONT,a.getInputStream(a.getEntry("data/pacnum.ttf")));
		}
		catch(Exception e) {
			System.err.println("Missing or corrupt font file!!");
			e.printStackTrace();
			System.exit(-2);
		}
	}
}

class PMButton extends JButton {
	private Dimension Size = null;

	PMButton(String text) {
		this(text,36);
	}
	PMButton(String text,int fontsize) {
		super(text);
		setBackground(Color.black);
		setForeground(Color.yellow);
		setBorderPainted(false);
		setFocusPainted(false);
		setFont(PacFont.font.deriveFont((float)fontsize));
	}
	public void paint(Graphics g) {
		((Graphics2D)(g)).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		super.paint(g);
	}
	public void mySetSize(int X, int Y) {
		Size = new Dimension(X,Y);
		super.setSize(X,Y);
	}
	public Dimension getMaximumSize() {
		if (Size==null)
			return super.getMaximumSize();
		else
			return Size;
	}
	public Dimension getMinimumSize() {
		if (Size==null)
			return super.getMinimumSize();
		else
			return Size;
	}
	public Dimension getPreferredSize() {
		if (Size==null)
			return super.getPreferredSize();
		else
			return Size;
	}
}

class PMPanel extends JPanel {
	private Dimension Size = null;
	
	PMPanel() {
		super();
		setBackground(Color.black);
		setForeground(Color.yellow);
	}
	PMPanel(int X, int Y) {
		this();
		mySetSize(X,Y);
	}
	public void mySetSize(int X, int Y) {
		Size = new Dimension(X,Y);
		super.setSize(X,Y);
	}
	public Dimension getMaximumSize() {
		if (Size==null)
			return super.getMaximumSize();
		else
			return Size;
	}
	public Dimension getMinimumSize() {
		if (Size==null)
			return super.getMinimumSize();
		else
			return Size;
	}
	public Dimension getPreferredSize() {
		if (Size==null)
			return super.getPreferredSize();
		else
			return Size;
	}
}

class PMLabel extends JLabel {
	private Dimension Size = null;

	PMLabel(String text) {
		this(text,SwingConstants.CENTER,12);
	}

	PMLabel(String text,int pos) {
		this(text,pos,12);
	}

	PMLabel(String text,int pos,int fontsize) {
		super(text,pos);
		setBackground(Color.black);
		setForeground(Color.yellow);
		setFont(PacFont.font.deriveFont((float)fontsize));
	}
	public void paint(Graphics g) {
		((Graphics2D)(g)).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(Color.black);
		super.paint(g);
	}
	public void setText(String text) {
		super.setText(text.toLowerCase());
	}

	public void mySetSize(int X, int Y) {
		Size = new Dimension(X,Y);
		super.setSize(X,Y);
	}
	public Dimension getMaximumSize() {
		if (Size==null)
			return super.getMaximumSize();
		else
			return Size;
	}
	public Dimension getMinimumSize() {
		if (Size==null)
			return super.getMinimumSize();
		else
			return Size;
	}
	public Dimension getPreferredSize() {
		if (Size==null)
			return super.getPreferredSize();
		else
			return Size;
	}
}

class SFLabel extends JLabel {
	private Dimension Size = null;

	SFLabel(String text) {
		this(text,SwingConstants.LEADING,24);
	}

	SFLabel(String text,int pos) {
		this(text,pos,24);
	}

	SFLabel(String text,int pos,int fontsize) {
		super(text,pos);
		setSize(100,30);
		setBackground(Color.black);
		setForeground(Color.yellow);
		setFont(SFBurlingtonFont.font.deriveFont((float)fontsize));
	}
	public void paint(Graphics g) {
		((Graphics2D)(g)).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(Color.black);
		super.paint(g);
	}
	public void mySetSize(int X, int Y) {
		Size = new Dimension(X,Y);
		super.setSize(X,Y);
	}
	public Dimension getMaximumSize() {
		if (Size==null)
			return super.getMaximumSize();
		else
			return Size;
	}
	public Dimension getMinimumSize() {
		if (Size==null)
			return super.getMinimumSize();
		else
			return Size;
	}
	public Dimension getPreferredSize() {
		if (Size==null)
			return super.getPreferredSize();
		else
			return Size;
	}
}

class GameMenu extends JPanel {
	final MainWindow parent;

	GameMenu(MainWindow parent) {
		this.parent = parent;
		setBackground(Color.black);
		setForeground(Color.yellow);
	}
}

class SelectGameMenu extends JPanel implements ActionListener{
	final MainWindow parent;

    public void actionPerformed(ActionEvent e) {
//		String arg = e.getActionCommand();

//		if ("EXIT".equals(arg))
			parent.setScreen(MainWindow.SCR_MAINMENU);
	}
	
	class gameActionListener extends Object implements ActionListener {
		int gameNum;
		gameActionListener(int gameNum) {
			this.gameNum = gameNum;
		}
	    public void actionPerformed(ActionEvent e) {
			parent.setScreen(MainWindow.SCR_GAMEMENU);
			try {
				parent.game = new Game(parent.gamemenu, parent, gameNum);
			} catch (Exception ee) {
				ee.printStackTrace();
				parent.setScreen(MainWindow.SCR_SELECTGAMEMENU);
			}
		}
	}
	
	SelectGameMenu(MainWindow parent) {
		this.parent = parent;

		setBackground(Color.black);
		setForeground(Color.yellow);
		setSize(640,480);
        setLayout(new GridLayout(parent.gameList.size()+1,1));
		JButton b = null;

		for(int i=0; i < parent.gameList.size(); i++) {
			add(b = new PMButton(((String)parent.gameList.get(i)).toLowerCase()));
			b.addActionListener(new gameActionListener(i));

		}
		add(b = new PMButton("exit"));
		b.addActionListener(this);
	}

	public Dimension getPreferredSize() {
		return new Dimension(640,480);
	}
}

class MainMenu extends JPanel implements ActionListener{
	MainWindow parent = null;

    public void actionPerformed(ActionEvent e) {
		String arg = e.getActionCommand();

		if ("start game".equals(arg)) {
			parent.setScreen(MainWindow.SCR_SELECTGAMEMENU);

		}
		else
		if ("highscore".equals(arg)) {
			parent.setScreen(MainWindow.SCR_HIGHSCOREMENU);
		}
		else
		if ("ABOUT".equals(arg)) {
			doAboutBox();
		}
		else
		if ("exit".equals(arg))
			System.exit(0);
	}
	
	MainMenu(MainWindow parent) {
		this.parent = parent;

		setBackground(Color.black);
		setForeground(Color.yellow);
		setSize(640,480);
        setLayout(new GridLayout(4,1));
		JButton b = null;
		add(b = new PMButton("start game"));
		b.addActionListener(this);
        add(b = new PMButton("highscore"));
		b.addActionListener(this);
        add(b = new PMButton("ABOUT"));
        b.addActionListener(this);
        add(b = new PMButton("exit"));
		b.addActionListener(this);
    }

	public Dimension getPreferredSize() {
		return new Dimension(640,480);
	}

	private void doAboutBox() {
		try {
			DataInputStream is = new DataInputStream (getClass().getResourceAsStream("/data/aboutbox.html"));
			URL imgurl = getClass().getResource("/data/pacmanicon.gif");
			byte[] temp = new byte[is.available()];
			is.readFully(temp);
			String html = new String(temp).replace('\n', ' ').replace('\r', ' ');
			html =
				html.substring(0, html.indexOf("pacmanicon.gif")) + 
				imgurl + 
				html.substring(html.indexOf("pacmanicon.gif") + "pacmanicon.gif".length());
			JOptionPane.showMessageDialog(
				parent,
				html,
				"DPMF PacMan",
				JOptionPane.PLAIN_MESSAGE);
		} catch (IOException e) {
		}
	}
}

class MainWindow extends JFrame {
	String currentPacManRevision = "$Revision: 1.17 $";

	final static public String SCR_MAINMENU = "main menu";
	final static public String SCR_GAMEMENU = "game menu";
	final static public String SCR_SELECTGAMEMENU = "select game menu";
	final static public String SCR_HIGHSCOREMENU = "highscore menu";

	final static public String gameDescriptorName = "game.description";

	public CardLayout myLayout;

	public LinkedList gameList;
	public HighscoreMenu hsmenu;
	public GameMenu gamemenu;
	public Game game;

	private LinkedList genGameList() {
		File gameDir = new File("worlds");
		String[] dirList = gameDir.list();
		gameList = new LinkedList();
		if (dirList != null) {
			for(int i=0;i<dirList.length;i++)
				if (dirList[i].endsWith(".world"))
					gameList.add(dirList[i].substring(0, dirList[i].lastIndexOf(".")));
		}
 		if (gameList.size()==0)	{
			System.err.println("No PacMan worlds found in the directory \"worlds\".\nYou need to put them there, man!");
			System.exit(-1);
		}
		return gameList;
	}

	// http://java.sun.com/docs/books/tutorial/uiswing/layout/card.html
	public void setScreen(String name) {
//		System.out.println("setScreen(\"" + name+"\")");
		myLayout.show(getContentPane(), name);

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

	public Dimension getMaximumSize() {
		return new Dimension(640,480);
	}
	public Dimension getMinimumSize() {
		return new Dimension(640,480);
	}
	public Dimension getPreferredSize() {
		return new Dimension(640,480);
	}
	
	MainWindow() {
		gameList = genGameList();

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		setBackground(Color.black);
		setForeground(Color.yellow);
		setSize(640,480);
		JPanel contentPane = new PMPanel();
		myLayout = new CardLayout();
		contentPane.setLayout(myLayout);
		contentPane.add((JPanel)(gamemenu = new GameMenu(this)),SCR_GAMEMENU);
		contentPane.add((JPanel)new MainMenu(this),SCR_MAINMENU);
		contentPane.add((JPanel)new SelectGameMenu(this),SCR_SELECTGAMEMENU);
		contentPane.add((JPanel)(hsmenu = new HighscoreMenu(this)),SCR_HIGHSCOREMENU);
		setContentPane(contentPane);
		setScreen(SCR_MAINMENU);

		setTitle ("DPMF PacMan - " + currentPacManRevision);

		pack();
		setVisible(true);
	}

	public static void main(String[] args) {
		new PacFont();
		new SFBurlingtonFont();
		new MainWindow();
	}
}
