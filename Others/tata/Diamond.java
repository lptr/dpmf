//Source file: C:/jdk1.3/bin/Diamond.java


public class Diamond extends Games_Element 
{
   public Proto theProto;
   public Labyrinth theLabyrinth;
	 
   public Diamond(int x, int y, Proto prot) 
   {
	PlaceX=x;
	PlaceY=y;
	theLabyrinth.c[20*x+y].Set_bonus(this);
	prot.scrn[20*x+y]=prot.scrn[20*x+y]+4;
   }
   	public int UpTime ()
	{
	return 1000;
	}

   /**
Letorli a palyarol a gyemantot.
   @roseuid 3AB4D38D032A
   */
   public void Delete(Proto prot)
   {
	prot.scrn[PlaceX*20+PlaceY]-=4;
	prot.theGame_Menu.theGame.theLabyrinth.c[PlaceX*20+PlaceY].Set_bonus(null);
   }
	public void Answer (Figure f, Proto prot)
	{
		f.Pick_Diamond(prot);
	}

}
