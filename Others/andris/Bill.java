import java.awt.*;
import java.awt.event.*;
import java.io.*;

class Bill extends KeyAdapter
{
	public static boolean beiratkozas=false;
	public static char kod='\t';
	public static int szammal=-1;
	public static int mozgas;	// Mozgas iranyat meghatarozo szam
	public static boolean kilepes,space,megnyomtad,pause;	// Specialis billenytuk
	public Bill(){	// Konstruktor
		super();
		mozgas=-1;	// Kezdeti ertekadas
		kilepes=false;
		space=false;
		megnyomtad=false;
		pause=false;
	}

	public void keyReleased(KeyEvent e){	// Gomb elendegesenek erzekelese
					/*switch (e.getKeyCode())
					{
					case KeyEvent.VK_LEFT: mozgas=-1;  break;
					case KeyEvent.VK_RIGHT: mozgas=-1; break;
					case KeyEvent.VK_UP: mozgas=-1; break;
					case KeyEvent.VK_DOWN: mozgas=-1; System.out.println("bill 23");break;
					case KeyEvent.VK_SPACE: space=false; break;
					case KeyEvent.VK_ESCAPE: kilepes=false; break;
					case KeyEvent.VK_ENTER:mozgas=-1;
					}*/

	}
	
	public void keyPressed(KeyEvent e){	// Gomb nyomasanak erzekelese
					megnyomtad=true;	// Megnyomtad a gombot
					switch (e.getKeyCode())	// Gombnak megfelelo ertekadas
					{
					case KeyEvent.VK_LEFT: mozgas=2;  break;
					case KeyEvent.VK_RIGHT: mozgas=0; break;
					case KeyEvent.VK_UP: mozgas=3; break;
					case KeyEvent.VK_DOWN:  mozgas=1;  break;
					case KeyEvent.VK_ESCAPE: kilepes=true; break;
					case KeyEvent.VK_SPACE: space=true; break;
					case KeyEvent.VK_ENTER: mozgas=50; break;
					case KeyEvent.VK_PAUSE: if (pause==true){pause=false;} else {pause=true;} break;
					}
					if (beiratkozas)
					{
						szammal=e.getKeyCode();
						kod=e.getKeyChar();
					}
					}
};
