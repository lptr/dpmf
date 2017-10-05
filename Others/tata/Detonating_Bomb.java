//Source file: C:/jdk1.3/bin/Detonating_Bomb.java
import java.util.Random;


public class Detonating_Bomb extends Games_Element 
{
   private int meret;
   private int zx;   
   public int birthtime;
   int n;
   int x;
   int y;
   public int uptime;
   public Pacman thePacman;
   public Proto theProto;
   public int tomb[] = new int [50];
   public int seged[] = new int[400];
   private int pozicio=0;
   Random random=new Random();

   public Detonating_Bomb(int x,int y,Proto prot,int time) 
   {
	PlaceX=x;
	meret=4;
	PlaceY=y;
	birthtime=time;
	uptime=5+random.nextInt(10);
	prot.theGame_Menu.theGame.theLabyrinth.c[20*x+y].Set_bonus(this);
	prot.scrn[20*x+y]=prot.scrn[20*x+y]+64;
   }
   
   /**
A bomba felrobban.
   @roseuid 3AB4D3CA033E
   */
	public int Get_Time() 
		{
		return birthtime;}
	public int UpTime() {return 10000;}
	public int RemainingTime() 
		{
		return uptime;}
	public void lehet(Games_Element g, Proto prot){
	if (prot.theGame_Menu.theGame.theLabyrinth.c[g.GetX()*20+g.GetY()].return_bonus()!=null)
	{
	prot.theGame_Menu.theGame.theLabyrinth.c[g.GetX()*20+g.GetY()].return_bonus().Delete(prot);
	}
	if (prot.theGame_Menu.theGame.theLabyrinth.c[g.GetX()*20+g.GetY()].return_figure()!=null)
	{
	prot.theGame_Menu.theGame.theLabyrinth.c[g.GetX()*20+g.GetY()].return_figure().Die(prot);
}
   }
	   public void Delete(Proto prot)
   {
	prot.scrn[PlaceX*20+PlaceY]-=64;
	prot.theGame_Menu.theGame.theLabyrinth.c[PlaceX*20+PlaceY].Set_bonus(null);
	prot.theGame_Menu.theGame.theLabyrinth.db[20*PlaceX+PlaceY] = null;
   }

	
	public void nemlehet(Games_Element g){};
	public void hatralevo(int i){};
	public void HolRobban(int X,int Y,Proto prot)
	{
	for (int j=0;j<50 ;j++ )
	{
		tomb[j]=0;
	}
	n=0;
	x=X;
	y=Y;
		for (;prot.theGame_Menu.theGame.theLabyrinth.c[20*x+y].Miez();x++ )
		{
			tomb[n]=x*20+y;	
			n++;
		}
	x=X;
	y=Y;
		for (;prot.theGame_Menu.theGame.theLabyrinth.c[20*x+y].Miez();x-- )
		{
			tomb[n]=x*20+y;	
			n++;
		}
	x=X;
	y=Y;
		for (;prot.theGame_Menu.theGame.theLabyrinth.c[20*x+y].Miez();y++ )
		{
			tomb[n]=x*20+y;	
			n++;
		}
	x=X;
	y=Y;
		for (;prot.theGame_Menu.theGame.theLabyrinth.c[20*x+y].Miez();y-- )
		{
			tomb[n]=x*20+y;	
			n++;
		}
	}
	public void Detonate(Proto prot) //robbanast atirni!!
   {
	prot.theGame_Menu.theGame.theLabyrinth.c[20*PlaceX+PlaceY].Robban(this,meret,prot);
	HolRobban(PlaceX,PlaceY,prot);
	n=0;
	while (tomb[n]>0)
	{
	prot.theGame_Menu.theGame.theLabyrinth.c[tomb[n]].Robban(this,meret,prot);
	n++;
	}
	}
}
