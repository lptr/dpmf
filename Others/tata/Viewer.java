import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class Viewer extends Thread
{
	Proto prot;
	private GridBagLayout gridbag = new GridBagLayout();
    private GridBagConstraints cons = new GridBagConstraints();
	ImageIcon kek= new ImageIcon("kek.jpg");
	ImageIcon fekete= new ImageIcon("fekete.jpg");
	ImageIcon pac= new ImageIcon("pac.jpg");
	ImageIcon mon= new ImageIcon("mon.jpg");
	ImageIcon bomb= new ImageIcon("bomb.jpg");
	ImageIcon bonus= new ImageIcon("bonus.jpg");
	ImageIcon life= new ImageIcon("life.jpg");
	ImageIcon diamond= new ImageIcon("diamond.jpg");
	ImageIcon dbomb= new ImageIcon("dbomb.jpg");
//		JTextArea highscore = new JTextArea(10,12);
//		JLabel hig = new JLabel("HighScores!");
		JLabel score = new JLabel("Pontszám: 0");
		JLabel elet = new JLabel("Életek száma: 3");
		JLabel bomba = new JLabel("Bombák száma: 1");
		JLabel dimleft = new JLabel("Gyémántok: 0");
		JLabel actuallevel= new JLabel("Szint: 1");
	public Viewer(Proto p) {prot=p;}
	public void run()
	{
		final JFrame	frame= new JFrame("camillo");
		frame.addWindowListener(new WindowAdapter() {
	    public void windowClosing(WindowEvent e) {
		frame.setVisible(false);
	    }
	});

		frame.addKeyListener(new KeyAdapter() {
	    public void keyPressed(KeyEvent e) {
		if(e.getKeyText(e.getKeyCode()).toLowerCase().compareTo("left") == 0) {
		prot.theGame_Menu.theGame.theLabyrinth.thePacman.nextmove=3;
		}		    
		if(e.getKeyText(e.getKeyCode()).toLowerCase().compareTo("up") == 0) {
		prot.theGame_Menu.theGame.theLabyrinth.thePacman.nextmove=0;
		}		    
		if(e.getKeyText(e.getKeyCode()).toLowerCase().compareTo("right") == 0) {
		prot.theGame_Menu.theGame.theLabyrinth.thePacman.nextmove=1;
		}		    
		if(e.getKeyText(e.getKeyCode()).toLowerCase().compareTo("down") == 0) {
		prot.theGame_Menu.theGame.theLabyrinth.thePacman.nextmove=2;
		}		    
		if(e.getKeyText(e.getKeyCode()).toLowerCase().compareTo("enter") == 0) {
		prot.theGame_Menu.theGame.theLabyrinth.thePacman.vanbomb=true;
		}		    
		if(e.getKeyText(e.getKeyCode()).toLowerCase().compareTo("f1") == 0) {
			prot.theGame_Menu.theGame.Quit();
			prot.Init();
		}		    
/*		if(e.getKeyText(e.getKeyCode()).toLowerCase().compareTo("p") == 0) {
		    PacMan_the_Game.clock.pause();
		}		    
		if(e.getKeyText(e.getKeyCode()).toLowerCase().compareTo("h") == 0) {
		    print();
		}		    */
	    }
	});

				frame.getContentPane().setLayout(gridbag);
				JPanel pane = new JPanel() { 				
				public void paintComponent(Graphics g) {
                super.paintComponent(g);
				for (int i = 0;i < 20; i++)
				{
				for (int j = 0;j < 20 ; j++)
				{
				switch (prot.scrn[20*i+j])
				{
				case 0: { kek.paintIcon(this,g,j*20,i*20); break; }  //semmi
				case 1: { pac.paintIcon(this,g,j*20,i*20); break; }  //pacman
				case 2: { mon.paintIcon(this,g,j*20,i*20); break; }  //monster
				case 3: { mon.paintIcon(this,g,j*20,i*20); break; }  //pacman,monster
				case 4: { diamond.paintIcon(this,g,j*20,i*20); break; }  //diamond
				case 5: { pac.paintIcon(this,g,j*20,i*20); break; }  //diamond,pacman
				case 6: { mon.paintIcon(this,g,j*20,i*20); break; }  //diamond,monster
				case 7: { mon.paintIcon(this,g,j*20,i*20); break; }  //diamond,pacman,monster
				case 8: { bonus.paintIcon(this,g,j*20,i*20); break; }  //score
				case 9: { pac.paintIcon(this,g,j*20,i*20); break; }  //score,pacman
				case 10: { mon.paintIcon(this,g,j*20,i*20); break; }  //score,monster
				case 11: { mon.paintIcon(this,g,j*20,i*20); break; }  //score,pacman,monster
				case 16: { life.paintIcon(this,g,j*20,i*20); break; }  //life
				case 17: { pac.paintIcon(this,g,j*20,i*20); break; }  //life,pacman
				case 18: { mon.paintIcon(this,g,j*20,i*20); break; }  //life,monster
				case 19: { mon.paintIcon(this,g,j*20,i*20); break; }  //life,pacman,monster
				case 32: { bomb.paintIcon(this,g,j*20,i*20); break; }  //bomb
				case 33: { pac.paintIcon(this,g,j*20,i*20); break; }  //bomb,pacman
				case 34: { mon.paintIcon(this,g,j*20,i*20); break; }  //bomb,monster
				case 35: { mon.paintIcon(this,g,j*20,i*20); break; }  //bomb,pacman,monster
				case 64: { dbomb.paintIcon(this,g,j*20,i*20); break; }  //dbomb
				case 65: { pac.paintIcon(this,g,j*20,i*20); break; }  //dbomb,pacman
				case 66: { mon.paintIcon(this,g,j*20,i*20); break; }  //dbomb,monster
				case 67: { mon.paintIcon(this,g,j*20,i*20); break; }  //dbomb,pacman,monster
				case 128: { fekete.paintIcon(this,g,j*20,i*20); break; } //fal
				}
			}
		}
	}

			
		};

	elet.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(10,10,10,10)));	
	elet.setPreferredSize(new Dimension(140,30));
	cons.gridx = 4;
	cons.gridy = 1;
	cons.gridheight =1;
	cons.anchor=GridBagConstraints.NORTHWEST;
	gridbag.setConstraints(elet, cons);
	frame.getContentPane().add(elet);

	bomba.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(10,10,10,10)));	
	bomba.setPreferredSize(new Dimension(140,30));
	cons.gridx = 4;
	cons.gridy = 2;
	cons.gridheight =1;
	cons.anchor=GridBagConstraints.NORTHWEST;
	gridbag.setConstraints(bomba, cons);
	frame.getContentPane().add(bomba);

/*	hig.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(10,10,10,10)));	
	hig.setPreferredSize(new Dimension(140,30));
	cons.gridx = 4;
	cons.gridy = 1;
	cons.gridheight =1;
	cons.anchor=GridBagConstraints.NORTHWEST;
	gridbag.setConstraints(hig, cons);
	frame.getContentPane().add(hig); */

	dimleft.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(10,10,10,10)));	
	dimleft.setPreferredSize(new Dimension(140,30));
	cons.gridx = 4;
	cons.gridy = 3;
	cons.gridheight =1;
	cons.anchor=GridBagConstraints.NORTHWEST;
	gridbag.setConstraints(dimleft, cons);
	frame.getContentPane().add(dimleft);

	actuallevel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(10,10,10,10)));	
	actuallevel.setPreferredSize(new Dimension(140,30));
	cons.gridx = 4;
	cons.gridy = 4;
	cons.gridheight =1;
	cons.anchor=GridBagConstraints.NORTHWEST;
	gridbag.setConstraints(actuallevel, cons);
	frame.getContentPane().add(actuallevel);

	score.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(10,10,10,10)));	
	score.setPreferredSize(new Dimension(140,30));
	cons.gridx = 4;
	cons.gridy = 5;
	cons.gridheight =1;
	cons.anchor=GridBagConstraints.NORTHWEST;
	gridbag.setConstraints(score, cons);
    frame.getContentPane().add(score);


/*	highscore.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(10,10,10,10)));	
	cons.gridx = 4;
	cons.gridy = 2;
	cons.gridheight =1;
	gridbag.setConstraints(highscore, cons);
	frame.getContentPane().add(highscore); */

	cons.gridx = 0;
	cons.gridy = 1;
	cons.gridwidth = 4;
	cons.gridheight = 4;
	gridbag.setConstraints(pane, cons);
			pane.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
			pane.setPreferredSize(new Dimension(400,400));
			frame.getContentPane().add(pane);
			frame.pack();
			frame.show();
		while (true)
		{
			frame.repaint();
		  try
			{
				sleep (100);
			}
			catch (InterruptedException e){	}
		}
	}
}
