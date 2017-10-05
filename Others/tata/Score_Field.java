//Source file: C:/jdk1.3/bin/Score_Field.java
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class Score_Field 
{
   private static int Score;
   private static int Lives;
   private static  int Diamonds;
   private static  int Bombs;
   private static  int Level;
   public static Proto prot;
   public static Game_Menu gm;
   
   public Score_Field(Proto p,Game_Menu g) 
   {
	Score=0;
	Lives=3;
	Diamonds=0;
	Bombs=0;
	Level=1;
	prot=p;
	gm=g;
	}
   
   /**
   @roseuid 3AB90B900104
   */
   public static  void Set_score(int s) 
   {
	Score=s;
	prot.vi.score.setText("Pontszám: "+s);
   }
   
   /**
   @roseuid 3AB90B99028A
   */
   public  static void Set_lives(int l) 
   {
	Lives=l;
	prot.vi.elet.setText("Életek száma: "+l);
		if (l==0)
	{
		prot.theGame_Menu.theGame.DiePacman();
	}
   }
   
   /**
   @roseuid 3AB90BA00320
   */
   public static void Set_diamonds(int d) 
   {
	Diamonds=d;
	Set_score(Score+10);
	prot.vi.dimleft.setText("Gyémántok: "+d);
		if (prot.theGame_Menu.theGame.theLabyrinth.returnDiamondsNeeded()==d)
		{
		prot.theGame_Menu.theGame.EndLevel();
		}

   }
   
   /**
   @roseuid 3AB90BA90244
   */
   public static  void Set_bombs(int b) 
   {
	Bombs=b;
	prot.vi.bomba.setText("Bombák száma: "+b);
   }
   
   /**
   @roseuid 3AB90BB101EA
   */
   public static  void Set_level(int l) 
   {
	Level=l;
	Set_score(Score+l*50);
	prot.vi.actuallevel.setText("Szint: "+l);
   }
   
   /**
   @roseuid 3AB90BC1032A
   */
//   public void Set_name(String s) 
//   {
//	Name=s;
//   }
   
   /**
   @roseuid 3AB91A170334
   */
   public static  int Get_score() 
   {
	return Score;
   }
   
   /**
   @roseuid 3AB91A200294
   */
   public static  int Get_lives() 
   {
	return Lives;   
}
   
   /**
   @roseuid 3AB91A280348
   */
   public static  int Get_bombs() 
   {
	return Bombs;
   }
   
   /**
   @roseuid 3AB91A2F028A
   */
   public static  int Get_level() 
   {
	return Level;
   }
   
   /**
   @roseuid 3AB934EC0212
   */
   public static int Get_diamonds() 
   {
	return Diamonds;
   }
   
}
