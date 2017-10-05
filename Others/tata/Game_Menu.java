//Source file: C:/jdk1.3/bin/Game_Menu.java
import java.io.*;   // BufferedReader, InputStreamReader


/**
A game_menu osztaly a jatek inditasaert, a kilepesert, es a beallitasokert felelos.
*/
public class Game_Menu 
{
   public HighScore theHighScore;
   public Proto theProto;  
	public Game theGame;
	public Score_Field theScore_Field;
	public Game_Menu() 
   {
   }
   
   /**
A jatekbol valo kilepes.
   @roseuid 3AB4D17D0028
   */
   public void Quit_game() 
   {
   }
   
   /**
A jatek inditasa, a Game objektumnak atadodik az iranyitas.
   @roseuid 3AB904DC0186
   */
   public void Start_game(Proto prot, BufferedReader proj) 
   {
	theScore_Field = new Score_Field(prot,this);
	theGame = new Game(prot);
		if (theHighScore==null)
		{
	theHighScore=new HighScore(prot);
	theHighScore.LoadHighScore();
	theHighScore.ShowHighScore();
		}
	theGame.New_Level(prot,proj);
   }
   
   /**
Egy menupont kivalasztasa, egyelore ez a highscore megtekintese lehet.
   @roseuid 3AB938D4003C
   */
   public void Enter_menu() 
   {
   }
}
