//Source file: C:/jdk1.3/bin/Bonus.java


/**
A palyan talalhato pontbonusz.
*/
public class Bonus extends Games_Element 
{
   private int Uptime;
   public Labyrinth theLabyrinth;
   public Proto theProto;
   
   public Bonus(int x,int y, Proto prot,int uptime) 
   {
	PlaceX=x;
	PlaceY=y;
	Uptime=uptime+prot.theGame_Menu.theGame.theLabyrinth.GetBonusUptime();
	theLabyrinth.c[20*x+y].Set_bonus(this);
	prot.scrn[20*x+y]=prot.scrn[20*x+y]+8;
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
	prot.scrn[PlaceX*20+PlaceY]-=8;
	prot.theGame_Menu.theGame.theLabyrinth.c[PlaceX*20+PlaceY].Set_bonus(null);
   }
	public void Answer (Figure f, Proto prot)
	{
		f.Pick_Bonus(prot);
	}
}
