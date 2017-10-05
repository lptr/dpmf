//Source file: C:/jdk1.3/bin/Labyrinth.java
import java.util.Random;
import java.io.*;   // BufferedReader, InputStreamReader

/**
A labirintus osztaly tarolja egy palya terkepet, o tarolja az utkozesek lekerdezesehez szukseges adatokat.
*/
public class Labyrinth 
{
   
   /**
A palya alaprajzanak terkepe.
   */
   public MonsterThread mt[] = new MonsterThread[20];
   public PacmanThread pt;
   public ManagerThread mat;
   public Monster Monsters[] = new Monster[20];
   public Pacman thePacman;
   public Bonus theBonus;
   public Bomb theBomb;
   public Life theLife;
   private int StartX=18;
   private int StartY=18;
   public Diamond theDiamond;
Random random=new Random();
  public int gameover[] = { 
1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
1,0,1,1,1,0,0,0,1,0,0,1,0,0,1,0,1,1,1,1,
1,1,0,0,0,0,0,1,0,1,0,1,1,1,1,0,1,0,0,1,
1,1,0,1,1,1,0,1,0,1,0,1,0,0,1,0,1,1,0,1,
1,1,0,0,0,1,1,0,1,0,1,1,0,0,1,0,1,0,0,1,
1,0,1,1,1,0,1,0,0,0,1,1,0,0,1,0,1,1,1,1,
1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
1,0,0,1,1,1,0,1,0,0,0,0,0,1,0,0,0,0,0,1,
1,0,1,0,0,0,1,0,1,0,0,0,1,0,0,0,0,0,0,1,
1,0,1,0,0,0,1,0,1,0,0,0,1,0,0,0,0,0,0,1,
1,0,1,0,0,0,1,0,0,1,0,1,0,0,0,0,0,0,0,1,
1,0,0,1,1,1,0,0,0,0,1,0,0,0,0,0,0,0,0,1,
1,0,0,0,0,0,0,0,0,1,1,1,1,0,1,1,1,0,0,1,
1,0,0,0,0,0,0,0,0,1,0,0,0,0,1,0,1,0,0,1,
1,0,0,0,0,0,0,0,0,1,1,1,0,0,1,1,1,0,0,1,
1,0,0,0,0,0,0,0,0,1,0,0,0,0,1,1,0,0,0,1,
1,0,0,0,0,0,0,0,0,1,1,1,1,0,1,0,1,0,0,1,
1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1
   };
   public int Level_Map[] = { 
1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1
   };
	public int valDiamond=50,valBonus=50,valBomb=50,valLife=10;   
	public int upBonus=2000,upBomb=3000,upLife=2000;   
   public static Detonating_Bomb db[] = new Detonating_Bomb[400];
   public static Games_Element c[] = new Games_Element[400];
   public Game theGame;
   public Figure theFigure[];
   public Games_Element theGames_Element[];
   public Proto theProto;
   public int DiamondsNeeded=0;
   public boolean bool;
	public int returnDiamondsNeeded() {
		return DiamondsNeeded;}
   public Labyrinth(Proto p) 
   {
	theProto=p;
   }
   
   /**
Egy uj palya letrehozasa, a levelmap valtozo feltoltese, a szukseges palyaelemek letrehozasa.
   @roseuid 3AB90AC80370
   */
   public void Create_level(Proto prot) 
   {
	for (int x=0;x<400 ;x++ )
	{
		if (Level_Map[x]==0)
		{
		c[x]=new Corridor((x/20),(x%20), prot);
		}
		if (Level_Map[x]==1)
		{
		c[x]=new Wall((x/20),(x%20), prot);
		}
	}
   }
   public void SetLevelMap(int pozicio,int ertek)
	{
	Level_Map[pozicio]=ertek;
	}


	public int GetBombUptime()
	{
	return random.nextInt(upBomb);
	}
	public int GetBonusUptime()
	{
	return random.nextInt(upBonus);
	}
	public int GetLifeUptime()
	{
	return random.nextInt(upLife);
	}
	public void GameOver()
	{
		for (int h=0;h<400 ;h++ )
		{
			theProto.scrn[h]=gameover[h];
		}
	}
   public void Set_bomb(int x, int y, Proto prot, int time ) 
   {
	db[20*x+y] = new Detonating_Bomb (x,y,prot,time);
	c[20*x+y].Set_bonus(db[20*x+y]);
   }

   public void Generate(int size,Proto prot)
	{
		if (random.nextInt(1000)<valBomb)
		{
			valBomb=(int)(0.8*valBomb);
			int x=random.nextInt(size);
			int y=random.nextInt(size);
			if ((prot.theGame_Menu.theGame.theLabyrinth.c[x*size+y].return_bonus()==null)&&(prot.theGame_Menu.theGame.theLabyrinth.Level_Map[x*size+y]==0))
			{
				Bomb bomb = new Bomb (x,y,prot,prot.Get_Time());
			}
		}	
		if (random.nextInt(1000)<valDiamond)
		{
			int x=random.nextInt(size);
			int y=random.nextInt(size);
			if ((prot.theGame_Menu.theGame.theLabyrinth.c[x*size+y].return_bonus()==null)&&(prot.theGame_Menu.theGame.theLabyrinth.Level_Map[x*size+y]==0))
			{
				Diamond diamond = new Diamond (x,y,prot);
			}
		}	
		if (random.nextInt(1000)<valBonus)
		{
			int x=random.nextInt(size);
			int y=random.nextInt(size);
			if ((prot.theGame_Menu.theGame.theLabyrinth.c[x*size+y].return_bonus()==null)&&(prot.theGame_Menu.theGame.theLabyrinth.Level_Map[x*size+y]==0))
			{
				Bonus bonus = new Bonus (x,y,prot,prot.Get_Time());
			}
		}	
		if (random.nextInt(1000)<valLife)
		{
			valLife=(int)(0.8*valLife);
			int x=random.nextInt(size);
			int y=random.nextInt(size);
			if ((prot.theGame_Menu.theGame.theLabyrinth.c[x*size+y].return_bonus()==null)&&(prot.theGame_Menu.theGame.theLabyrinth.Level_Map[x*size+y]==0))
			{
				Life life = new Life (x,y,prot,prot.Get_Time());
			}
		}	

	}
   
   /**
Letorli a palyat, altalaban szint teljesitesekor hivodik meg.
   @roseuid 3AB9334C019A
   */
   public void Clear_level() 
   {
		
   }
   	public void Load(BufferedReader proj){
	  	try
		  {
			String line;
			int num=0;
		    char chars[] = new char[400];
			
            line=proj.readLine(); // #
            line=proj.readLine(); // pacmanx, pacmany
            chars = line.toCharArray();
			if (thePacman!=null)
			{
	//			theProto.scrn[thePacman.GetY()*20+thePacman.GetX()]-=1;
				c[thePacman.GetX()*20+thePacman.GetY()].Set_figure(null);
				thePacman.SetX((int)chars[0]-48);
				thePacman.SetY((int)chars[2]-48);
				c[thePacman.GetX()*20+thePacman.GetY()].Set_figure(thePacman);
				theProto.scrn[thePacman.GetX()*20+thePacman.GetY()]+=1;
			}
			if (thePacman==null)
			{
			thePacman = new Pacman((int)chars[0]-48,(int)chars[2]-48,1,0,theProto);
			}
			line=proj.readLine(); // #
			line=proj.readLine(); // elso monster
            chars = line.toCharArray();
			while (chars[0] != '#')
			{
/*				for (int q = 0; q < (line.length()+1)/2-2 ; q++)
				{
					mozgas[q]=(int)chars[(q+2)*2]-49;
				}*/
				if ((int)chars[4]-48==1)
				{
					bool=true;
				}
				if ((int)chars[4]-48==0)
				{
					bool=false;
				}
				Monsters[num] = new Monster((int)chars[0]-48,(int)chars[2]-48,bool,(int)chars[6]-48,0,theProto,num);
	            line=proj.readLine(); // kov. monster v. ujsor
	            chars = line.toCharArray();
				num++;
/*				for (int w = 0;w < 100 ; w++)
				{
					mozgas[w]=4;
					}*/
			}
			num=0;
			line=proj.readLine(); // elso monster
            chars = line.toCharArray();
			while (chars[0] != '#')
			{
				Diamond diamond = new Diamond ((int)chars[0]-48,(int)chars[2]-48,theProto);
	            line=proj.readLine(); // kov. gyemant v. ujsor
	            chars = line.toCharArray();
			}
			line=proj.readLine(); // elso monster
            chars = line.toCharArray();
			while (chars[0] != '#')
			{
				Bonus bonus = new Bonus ((int)chars[0]-48,(int)chars[2]-48,theProto,0);
	            line=proj.readLine(); // kov. bonusz v. ujsor
	            chars = line.toCharArray();
			}
			line=proj.readLine(); // elso monster
            chars = line.toCharArray();
			while (chars[0] != '#')
			{
				Life life = new Life ((int)chars[0]-48,(int)chars[2]-48,theProto,0);
	            line=proj.readLine(); // kov. elet v. ujsor
	            chars = line.toCharArray();
			}
			line=proj.readLine(); // elso monster
            chars = line.toCharArray();
			while (chars[0] != '#')
			{
				Bomb bomb = new Bomb ((int)chars[0]-48,(int)chars[2]-48,theProto,0);
				line=proj.readLine(); // kov. bomba v. ujsor
	            chars = line.toCharArray();
			}
			proj.close();
		  }
	    catch (IOException e)
		  {
			System.out.println("Input error.");
			System.exit(0);
		  }
 
	}

	public void Szalak(){
			for (int u=0;Monsters[u]!=null ;u++ )
			{
				MonsterThread t=new MonsterThread("monster",Monsters,u,theProto);
				mt[u]=t;
				t.start();
			}
				if (pt==null)
				{
				PacmanThread z=new PacmanThread("pac",thePacman,theProto);
				pt=z;
				z.start();
				}
				ManagerThread m=new ManagerThread(theProto);
				mat=m;
				m.start();
	}
	public int GetStartX()
	{
	return StartX;
	}
	public int GetStartY()
	{
	return StartY;
	}


}
