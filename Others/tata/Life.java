//Source file: C:/jdk1.3/bin/Life.java


/**
A palyan talalhato pontbonusz.
*/
public class Life extends Games_Element 
{
   private int Uptime;
   public Proto theProto;
   public Labyrinth theLabyrinth;
   
   public Life(int x, int y, Proto prot,int uptime) 
   {
	PlaceX=x;
	PlaceY=y;
	Uptime=uptime+prot.theGame_Menu.theGame.theLabyrinth.GetLifeUptime();
	theLabyrinth.c[20*x+y].Set_bonus(this);
	prot.scrn[20*x+y]=prot.scrn[20*x+y]+16;
   }

	public int UpTime ()
	{
	return Uptime;
	}

   /**
Letorli a bonuszt.
   @roseuid 3AB4D427006E
   */
   public void Delete(Proto prot)
   {
	prot.scrn[PlaceX*20+PlaceY]-=16;
	prot.theGame_Menu.theGame.theLabyrinth.c[PlaceX*20+PlaceY].Set_bonus(null);
   }

	public void Answer (Figure f, Proto prot)
	{
		f.Pick_Life(prot);
	}

}
