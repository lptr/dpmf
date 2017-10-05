//Source file: C:/jdk1.3/bin/Game.java
import java.io.*;   // BufferedReader, InputStreamReader


/**
A jatek iranyitasat vegzi, az uj palyakat inditja.
*/
public class Game 
{
   public  Score_Field theScore_Field;
   public  Labyrinth theLabyrinth;
   public Game_Menu theGame_Menu;
   public Proto prot;
   public Pacman segedpac;
   public PacmanThread segedthread;
   public String leveltomb[]={"input.txt","input2.txt","input3.txt"};
 
   public Game(Proto p) 
   {
	prot=p;
   }
   
   /**
A jatek inditasa, New_level utan hivodik meg, es a palya vegeig, vagy pause hivasig folytatodik.
   @roseuid 3AB4CFEC0320
   */

   public void Play() 
   {
   }
   
   /**
A jatek futtatasaert felelos szal felfuggesztese, ilyenkor a program futasa megall.
   @roseuid 3AB4CFF40064
   */
   public void Pause() 
   {
   }
   
   /**
A felfuggesztett jatek folytatasa.
   @roseuid 3AB4D0000208
   */
   public void Resume() 
   {
   }
   
   /**
Egy uj jatekszint letrehozasa.
   @roseuid 3AB4D0040136
   */
	public void Quit() {
	for (int u=0;theLabyrinth.mt[u]!=null ;u++ )
	{
		theLabyrinth.mt[u].stop2();
	}
	for (int y=0;y<400 ;y++ )
	{
		prot.scrn[y]=0;
	}
	theLabyrinth.mat.stop2();
	theLabyrinth.pt.stop2();
	prot.theGame_Menu.theScore_Field.Set_diamonds(0);
	prot.theGame_Menu.theScore_Field.Set_bombs(0);
	prot.theGame_Menu.theScore_Field.Set_lives(3);
	prot.theGame_Menu.theScore_Field.Set_score(0);
	prot.theGame_Menu.theScore_Field.Set_level(1);
	}

	public void DiePacman(){
	for (int u=0;theLabyrinth.mt[u]!=null ;u++ )
	{
		theLabyrinth.mt[u].stop2();
	}
	for (int y=0;y<400 ;y++ )
	{
		prot.scrn[y]=0;
	}
	System.out.println("meghaltal!");
	theLabyrinth.GameOver();
	theLabyrinth.mat.stop2();
	theLabyrinth.pt.stop2();
	prot.theGame_Menu.theHighScore.CheckHigh(prot.theGame_Menu.theScore_Field.Get_score(),Get_Name());
	prot.theGame_Menu.theHighScore.SaveHighScore();
   }
	
	public String Get_Name()
	{
		return ("sss             ");
	}

	public void EndLevel() {
	prot.q("endlevel");
	for (int u=0;theLabyrinth.mt[u]!=null ;u++ )
	{
	prot.q(u+"-adik szorny megallt");
		theLabyrinth.mt[u].stop2();
	}
	theLabyrinth.mat.stop2();
	theLabyrinth.pt.stop2();
	prot.q("manager megallt");
	for (int y=0;y<400 ;y++ )
	{
		prot.scrn[y]=0;
	}
	prot.q("tabla torolve");
	try
	{
		prot.theGame_Menu.theScore_Field.Set_level(prot.theGame_Menu.theScore_Field.Get_level()+1);
		BufferedReader fis=(new BufferedReader(new FileReader(new File(leveltomb[1]))));
			New_Level(prot,fis);
			theLabyrinth.Load (fis);
			fis.close();
	}
		catch (IOException e){}
			theLabyrinth.Szalak();
	
	}

public void LoadLabyrinth(BufferedReader proj){
	  	try
		  {
			theLabyrinth.DiamondsNeeded+=3;
			String line;
		    char chars[] = new char[20];
			
            for (int y=0; y<20; y++)			// Labirintus krealas
						{
			                line=proj.readLine();
			                chars = line.toCharArray();
							for (int x=0; x<20; x++)
					          {
							  theLabyrinth.SetLevelMap(y*20+x,(int)chars[x]-48);
						      }
						}
		  }
		  	    catch (IOException e)
		  {
			System.out.println("Input error.");
			System.exit(0);
		  }

	}

   public void New_Level(Proto prot, BufferedReader proj) 
   {
	if (theLabyrinth!=null)
	{
	segedpac=theLabyrinth.thePacman;	
	//segedthread=theLabyrinth.pt;
	}	
	theLabyrinth = new Labyrinth(prot);
	if (segedpac!=null)
	{
	theLabyrinth.thePacman=segedpac;
//	theLabyrinth.pt=segedthread;
	}
	LoadLabyrinth(proj);
	theLabyrinth.Create_level(prot);
   }
   
   /**
Egy palya teljesitesekor a palyarol valo kilepest kezeli.
   @roseuid 3AB4D00B033E
   */
   public void End_level() 
   {
	theLabyrinth.Clear_level();
	
   }
   
   /**
Ha pacman meghal, vagy a jatekos kilep a jatekbol, akkor vegeter az aktualis jatek, a vezerles visszakerul a fomenuhoz.
   @roseuid 3AB937620208
   */
   public void End_game() 
   {
   }
}
