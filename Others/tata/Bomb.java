

/**
A palyan talalhato felveheto bomba.
*/
public class Bomb extends Games_Element 
{
   private int Uptime;
   public Labyrinth theLabyrinth;
   public Proto theProto;
   
   public Bomb(int x,int y, Proto prot,int uptime) 
   {
	PlaceX=x;
	PlaceY=y;
	Uptime=uptime+prot.theGame_Menu.theGame.theLabyrinth.GetBombUptime();
	theLabyrinth.c[20*x+y].Set_bonus(this);
	prot.scrn[20*x+y]=prot.scrn[20*x+y]+32;
   }
	public int UpTime ()
	{
	return Uptime;
	}
   /**
Letorli a bombat a palyarol.
   @roseuid 3AB4D4620028
   */
   public void Delete(Proto prot)
   {
	prot.scrn[PlaceX*20+PlaceY]-=32;
	prot.theGame_Menu.theGame.theLabyrinth.c[PlaceX*20+PlaceY].Set_bonus(null);
   }

	public void Answer (Figure f, Proto prot)
	{
		f.Pick_Bomb(prot);
	}


}
