//Source file: C:/jdk1.3/bin/Monster.java
import java.util.Random;

/**
Szorny tipus
*/
public class Monster extends Figure 
{
   public boolean Intelligent;
   private boolean State;
   public Proto theProto;
   public int number;
   public int iq;
   Random random=new Random();
   
   public Monster(int x,int y, boolean i, int s, int d, Proto prot, int num)
   {
	super(x, y, s, d);
	theProto = prot;
	State=true;
	Intelligent=i;
	if (Intelligent)
	{
	iq=random.nextInt(100);
	}
	number=num;
	theLabyrinth.c[PlaceX*20+PlaceY].Set_figure(this);
	prot.scrn[20*x+y]=prot.scrn[20*x+y]+2;
   }
   
   /**
A szorny halalakor szemme valtozik.
   @roseuid 3AB928BB0370
   */
   public void Turntoeye() 
   {
	State=false;
   }
   
   /**
A szem a feszekbe visszaerve visszavaltozik szornye.
   @roseuid 3AB928C4033E
   */
   public void Turntomonster() 
   {
	State=true;
   }

   
	public void Ask(Figure f,Figure e,Proto prot)
	{
		if (f!=null)
		{
		f.Answer(e,prot);
		}
	}
	
	public void Answer(Figure e,Proto prot)
	{
		if ((e.Query()==1)&&(e.Harap()==0))
			{
			Eat(e,prot);
			}
			if ((e.Query()==1)&&(e.Harap()==1))
			{
			e.Eat(this,prot);
			}
			if (e.Query()==2)
		   {
			e.NotEat(this);
			}
	}
	public void Die(Proto prot)
	{
	prot.theGame_Menu.theGame.theLabyrinth.mt[number].stop2();
	prot.scrn[20*PlaceX+PlaceY]-= 2;
	prot.theGame_Menu.theGame.theLabyrinth.c[PlaceX*20+PlaceY].Set_figure(null);
	prot.theGame_Menu.theScore_Field.Set_score(prot.theGame_Menu.theScore_Field.Get_score()+30);
	} 	

	public void Eat(Figure e,Proto prot) 
	{
	e.Die(prot);
	}
	
	public void NotEat(Figure e) 
	{
	Stop();
	}

	public int Query() 
		
	{
	return 2;
	}


}