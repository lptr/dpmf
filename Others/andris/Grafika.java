/*
tracker.addImage(image,0); try { waitForID(0); } catch
         (InterruptedException e) {}
*/

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;

public class Grafika extends Canvas
{
	public Graphics gc;	 // grafikus objektum

/*	private final static int MERET=25;	 // Egy mezo merete
	private final int FONT_MERET=15;	// Betumeret
	private final static String FONT="Comic Sans MS";	 // Betutipus
	private static final int PLUSZ=40;	 // Kezdeti pozicio */
	private static final int x=700,y=500;	 //Meret
//	private int kep_x=15,kep_y=15;	// Kepmeret

	private BufferedImage kep,kep_lab;	
	private Image 
		    pacman_kep, szorny_kep, gyemant_kep, elixir_kep, bonusz_kep, ketyb_kep, bomba_kep, bonus_kep,
		    logo,
			starton, startoff, exiton, exitoff, scoreon, scoreoff,
			wall0,wall1,wall2,wall3,wall4,wall5,wall6,wall7,wall8,wall9,wall10,wall11,wall12,wall13,wall14,wall15,
			bombs,score,lives,a0,a1,a2,a3,a4,a5,a6,a7,a8,a9,
			high,hatter,hatter2;
			
	private static Canvas server; // Vaszon
    public Proto parent_Proto;
	MediaTracker tracker=new MediaTracker(this);

	//---------------------  KONSTRUKTOR          --------------------------------------


	public Grafika (Proto prot)	// Konstruktor
	{
			parent_Proto=prot;

			addKeyListener(new Bill());
			kep = new BufferedImage(x,y,BufferedImage.TYPE_INT_RGB);
			server=this;
			gc=kep.createGraphics();
	}

	//-----------------------------------------------              BETOLT                   -----------------------------------------------
	

	public Image a[]=new Image[10];

	public void betolt()
	{
		Toolkit t= Toolkit.getDefaultToolkit();

		try
		{

			logo=t.getImage("logo.gif");			tracker.addImage(logo,0); 
			starton=t.getImage("starton.gif");		tracker.addImage(starton,1);
			startoff=t.getImage("startoff.gif");	tracker.addImage(startoff,2);
			exiton=t.getImage("exiton.gif");		tracker.addImage(exiton,3);
			exitoff=t.getImage("exitoff.gif");		tracker.addImage(exitoff,4);
			scoreon=t.getImage("scoreon.gif");		tracker.addImage(scoreon,5);
			scoreoff=t.getImage("scoreoff.gif");	tracker.addImage(scoreoff,6);

			wall0=t.getImage("wall0.gif");			tracker.addImage(wall0,7);
			wall1=t.getImage("wall1.gif");			tracker.addImage(wall1,8);
			wall2=t.getImage("wall2.gif");			tracker.addImage(wall2,9);
			wall3=t.getImage("wall3.gif");			tracker.addImage(wall3,10);
			wall4=t.getImage("wall4.gif");			tracker.addImage(wall4,11);
			wall5=t.getImage("wall5.gif");			tracker.addImage(wall5,12);
			wall6=t.getImage("wall6.gif");			tracker.addImage(wall6,13);
			wall7=t.getImage("wall7.gif");			tracker.addImage(wall7,14);
			wall8=t.getImage("wall8.gif");			tracker.addImage(wall8,15);
			wall9=t.getImage("wall9.gif");			tracker.addImage(wall9,16);
			wall10=t.getImage("wall10.gif");		tracker.addImage(wall10,17);
			wall11=t.getImage("wall11.gif");		tracker.addImage(wall11,18);
			wall12=t.getImage("wall12.gif");		tracker.addImage(wall12,19);
			wall13=t.getImage("wall13.gif");		tracker.addImage(wall13,20);
			wall14=t.getImage("wall14.gif");		tracker.addImage(wall14,21);
			wall15=t.getImage("wall15.gif");		tracker.addImage(wall15,22);

			pacman_kep=t.getImage("pacman.gif");	tracker.addImage(pacman_kep,23);
			szorny_kep=t.getImage("szorny.gif");	tracker.addImage(szorny_kep,24);
			gyemant_kep=t.getImage("gyemant.gif");	tracker.addImage(gyemant_kep,25);
			elixir_kep=t.getImage("elixir.gif");	tracker.addImage(elixir_kep,26);
			ketyb_kep=t.getImage("ketybomba.gif");	tracker.addImage(ketyb_kep,27);
			bomba_kep=t.getImage("bomba.gif");		tracker.addImage(bomba_kep,28);
			bonus_kep=t.getImage("bonus.gif");		tracker.addImage(bonus_kep,29);

			bombs=t.getImage("bombs.gif");			tracker.addImage(bombs,30);
			score=t.getImage("score.gif");			tracker.addImage(score,31);
			lives=t.getImage("lives.gif");			tracker.addImage(lives,32);

			a[0]=t.getImage("0.gif");				tracker.addImage(a[0],33);
			a[1]=t.getImage("1.gif");				tracker.addImage(a[1],34);
			a[2]=t.getImage("2.gif");				tracker.addImage(a[2],35);
			a[3]=t.getImage("3.gif");				tracker.addImage(a[3],36);
			a[4]=t.getImage("4.gif");				tracker.addImage(a[4],37);
			a[5]=t.getImage("5.gif");				tracker.addImage(a[5],38);
			a[6]=t.getImage("6.gif");				tracker.addImage(a[6],39);
			a[7]=t.getImage("7.gif");				tracker.addImage(a[7],40);
			a[8]=t.getImage("8.gif");				tracker.addImage(a[8],41);
			a[9]=t.getImage("9.gif");				tracker.addImage(a[9],42);

			high=t.getImage("wall.gif");			tracker.addImage(high,43);

			hatter=t.getImage("hatter.gif");		tracker.addImage(hatter,44);
			hatter2=t.getImage("hatter2.gif");		tracker.addImage(hatter2,45);

			try { tracker.waitForAll(); } catch (InterruptedException e) {}

			}	catch (Exception e)	{;}

	}

//-----------------------------------------------                 MENU_RAJZ                    ------------------------------------------
public void show_High()
	{
	gc.clearRect(0,0,700,500);
	int startx=200, starty=100;
	gc.drawImage(high,startx,starty,this);	// logo megjelenitese
	repaint();
	while (Bill.megnyomtad!=true) {	}
	Bill.megnyomtad=false;
	menu_logo();
	}

public void menu_logo()
	{
	gc.clearRect(0,0,700,500);
	gc.drawImage(hatter,35,0,this);	// logo megjelenitese
	repaint();
	}

public void menu_rajz(int melyik_menupont)	// Menu kirajzolasa
	{
	    int startx=165, starty=160;

//		gc.clearRect(0,60,700,440);	
		switch (melyik_menupont) {	// Menupontok kiirasa
			case 0:	// Jatek menupont
				gc.drawImage(starton,startx+60,starty+80,this);
				gc.drawImage(scoreoff,startx+10,starty+120,this);
				gc.drawImage(exitoff,startx+40,starty+160,this);
				break;
		
			case 1:	// Legjobb eredmenyek menupont
				gc.drawImage(startoff,startx+60,starty+80,this);
				gc.drawImage(scoreon,startx+10,starty+120,this);
				gc.drawImage(exitoff,startx+40,starty+160,this);
				break;
			
			case 2:	// Kilepes menupont
				gc.drawImage(startoff,startx+60,starty+80,this);
				gc.drawImage(scoreoff,startx+10,starty+120,this);
				gc.drawImage(exiton,startx+40,starty+160,this);
				break;
		}
		repaint();
//		repaint(startx,starty+80,startx+300,starty+200);	// Ujrarajzolas
	}
//-----------------------------------------------                 Jatek_RAJZ                    ------------------------------------------
public void game_setup()
	{
	gc.clearRect(0,0,700,500);
	gc.drawImage(logo,0,0,this);	// logo megjelenitese
	gc.drawImage(score,0,100,this);
	gc.drawImage(bombs,8,140,this);
	gc.drawImage(lives,8,180,this);
	gc.drawImage(hatter2,20,240,this);
	repaint();
	}

public void display_score(int score, int bombs, int lives)
	{
	int d1=0,d2=0,d3=0;
	int startx=180;

// score
	d1=score / 100;
	d2=(score-d1*100) / 10;
	d3=(score-d1*100-d2*10);

	gc.drawImage(a[d1],startx,100,this);
	gc.drawImage(a[d2],startx+25,100,this);
	gc.drawImage(a[d3],startx+50,100,this);
	repaint(startx,100,75,40);

// bombs
	d1=bombs / 100;
	d2=(bombs-d1*100) / 10;
	d3=(bombs-d1*100-d2*10);

	gc.drawImage(a[d1],startx,140,this);
	gc.drawImage(a[d2],startx+25,140,this);
	gc.drawImage(a[d3],startx+50,140,this);
	repaint(startx,140,75,40);

// lives
	d1=lives / 100;
	d2=(lives-d1*100) / 10;
	d3=(lives-d1*100-d2*10);

	gc.drawImage(a[d1],startx,180,this);
	gc.drawImage(a[d2],startx+25,180,this);
	gc.drawImage(a[d3],startx+50,180,this);
	repaint(startx,180,75,40);

	}

public int labstartx=295;
public int labstarty=50;

public void paint_lab(int x, int y, int wallnumber, char item)
	{
	  switch (wallnumber)
	  {
	  case 0:	gc.drawImage(wall0,labstartx+x*40,labstarty+y*40,this); break;
  	  case 1:	gc.drawImage(wall1,labstartx+x*40,labstarty+y*40,this); break;
  	  case 2:	gc.drawImage(wall2,labstartx+x*40,labstarty+y*40,this); break;
  	  case 3:	gc.drawImage(wall3,labstartx+x*40,labstarty+y*40,this); break;
  	  case 4:	gc.drawImage(wall4,labstartx+x*40,labstarty+y*40,this); break;
  	  case 5:	gc.drawImage(wall5,labstartx+x*40,labstarty+y*40,this); break;
  	  case 6:	gc.drawImage(wall6,labstartx+x*40,labstarty+y*40,this); break;
  	  case 7:	gc.drawImage(wall7,labstartx+x*40,labstarty+y*40,this); break;
  	  case 8:	gc.drawImage(wall8,labstartx+x*40,labstarty+y*40,this); break;
  	  case 9:	gc.drawImage(wall9,labstartx+x*40,labstarty+y*40,this); break;
  	  case 10:	gc.drawImage(wall10,labstartx+x*40,labstarty+y*40,this); break;
  	  case 11:	gc.drawImage(wall11,labstartx+x*40,labstarty+y*40,this); break;
  	  case 12:	gc.drawImage(wall12,labstartx+x*40,labstarty+y*40,this); break;
  	  case 13:	gc.drawImage(wall13,labstartx+x*40,labstarty+y*40,this); break;
  	  case 14:	gc.drawImage(wall14,labstartx+x*40,labstarty+y*40,this); break;
  	  case 15:	gc.drawImage(wall15,labstartx+x*40,labstarty+y*40,this); break;
	  } 
	  switch (item)
	  {
	  case 'D': gc.drawImage(gyemant_kep,labstartx+x*40+7,labstarty+y*40+7,this); break;
	  case 'P': gc.drawImage(pacman_kep,labstartx+x*40+7,labstarty+y*40+7,this); break;
	  case 'b': gc.drawImage(bomba_kep,labstartx+x*40+7,labstarty+y*40+7,this); break;
	  case 'e': gc.drawImage(elixir_kep,labstartx+x*40+7,labstarty+y*40+7,this); break;
	  case 'M': gc.drawImage(szorny_kep,labstartx+x*40+7,labstarty+y*40+7,this); break;
	  case '+': gc.drawImage(bonus_kep,labstartx+x*40+7,labstarty+y*40+7,this); break;
	  case '0': gc.drawImage(ketyb_kep,labstartx+x*40+7,labstarty+y*40+7,this); break;
      }
	  repaint(labstartx+x*40,labstarty+y*40,40,40);
	}

//----------------------------------              PAINT               --------------------------------------------------------------

public void paint(Graphics g)
	{
    g.drawImage(kep,0,0,this); 
    }



public void update(Graphics g)
	{
    g.drawImage(kep,0,0,this); 
    }
}