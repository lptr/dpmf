//Source file: C:/jdk1.3/bin/Pacman.java


/**
Pacman osztaly, a jatekos iranyitotta.
*/
public class Pacman extends Figure 
{
   
   /**
Itt taroljuk hogy pacman serthetetlen-e, vagy nem.
   */
   private int State;
   
   /**
A pacman mezojen levo bonus tipusa.
   */
   private Games_Element Bonus_type;
   private int Bonus_kod;
   public Proto theProto;
   public int nextmove;
   public boolean vanbomb;
   public Corridor c[];
   public Game theGame;
   public Score_Field theScore_Field;
   public Pacman(int x,int y,int s,int d, Proto prot)
   {
	super(x, y, s, d);
	theProto=prot;
	State=0;
	prot.scrn[20*x+y]=prot.scrn[20*x+y]+1;
	theLabyrinth.c[PlaceX*20+PlaceY].Set_figure(this);
   }
   
   /**
Pacman bonuszt vesz fel.
   @roseuid 3AB4D272030C
   */
public void Pick_Diamond(Proto prot)	
		{
		theLabyrinth.c[20*PlaceX+PlaceY].return_bonus().Delete(prot);
		theScore_Field.Set_diamonds(theScore_Field.Get_diamonds()+1);
		}

public void Pick_Bonus(Proto prot)	
		{
		theScore_Field.Set_score(theScore_Field.Get_score()+20);
		theLabyrinth.c[20*PlaceX+PlaceY].return_bonus().Delete(prot);
		}
public void Pick_Bomb(Proto prot)	
		{
		if (theScore_Field.Get_bombs()<5)
			{
			theScore_Field.Set_bombs(theScore_Field.Get_bombs()+1);
		theLabyrinth.c[20*PlaceX+PlaceY].return_bonus().Delete(prot);
			}
		}
public void Pick_Life(Proto prot)	
		{
		if (theScore_Field.Get_lives()<5)
			{
		theLabyrinth.c[20*PlaceX+PlaceY].return_bonus().Delete(prot);
			theScore_Field.Set_lives(theScore_Field.Get_lives()+1);
			}
		
   }
   public void Check_Bonus(Figure f,Proto prot)
   {
    prot.theGame_Menu.theGame.theLabyrinth.c[PlaceX*20+PlaceY].Get_Type(f, prot);
   }

   /**
Pacman bombat rak le.
   @roseuid 3AB4D28103AC
   */
   public void Put_Bomb(Proto prot,int time) 
   {
		{
			if ((theLabyrinth.c[PlaceX*20+PlaceY].return_bonus()==null)&&(theScore_Field.Get_bombs()>0))
			{
			theScore_Field.Set_bombs(theScore_Field.Get_bombs()-1);
			prot.theGame_Menu.theGame.theLabyrinth.Set_bomb(PlaceX,PlaceY,prot,time );
			}
		};
   }
   
   /**
Halala utan Pacman ot masodpercig serthetetlenne valik.
   @roseuid 3AB4D29002C6
   */
   public void Go_Invincible() 
   {
	State=1;
   }
   
   /**
Az ot masodperc leteltevel visszater sertheto allapotba pacman.
   @roseuid 3AB4D2980014
   */
   public void Go_normal() 
   {
	State=0;
   }
   
   /**
Pacman halalakor hivodik meg.
   @roseuid 3AB4D29F00A0
   */
/*   public void Kill_Pacman() 
   {
	theScore_Field.Set_lives(theScore_Field.Get_lives()-1);
	if (theScore_Field.Get_lives()<0) 
	{
	theGame.End_game();
	}
	Go_Invincible();
	SetX(0);
	SetY(0);
   }*/
/*   
Falnak utkozve Pacman megall.
   @roseuid 3AB92E2001E0
   */
   public void Stop() 
   {
	Speed=0;
   }
   
   /**
Utkozes utan pacman mozgatasra ujbol elindul.
   @roseuid 3AB92E2603CA
   */
   public void Start() 
   {
	Speed=1;
   }

   public void Move(int d, Proto prot) 
   {
	prot.scrn[20*PlaceX+PlaceY]-= 1;
	theLabyrinth.c[PlaceX*20+PlaceY].Set_figure(null);
	switch (d)
	{
	case 0: {PlaceX--; 	 break;}
	case 1: {PlaceY++; 	 break;}
	case 2: {PlaceX++; 	 break;}
	case 3: {PlaceY--; 	 break;}
	}
	prot.scrn[20*PlaceX+PlaceY]+= 1;
	theLabyrinth.c[PlaceX*20+PlaceY].Set_figure(this);
   }
	public int Harap()
	{
	return State;
	}

	public void Answer(Figure f,Proto prot)
		{
		if (State==0)
			{
			f.Eat(this,prot);
			}
			else	
			{
			Eat(f,prot);
			}
	
		}

	public void Ask(Figure f,Figure e,Proto prot)
	{
		if (f!=null)
		{
			f.Answer(e,prot);
		}
	}

	public void Eat(Figure e) 
	{
	e.Turntoeye();
	};
	public void NotEat(Figure e) 
	{
	e.Stop();
	};
	
	public void Die(Proto prot)
	{
	System.out.println("aa");
	prot.scrn[20*PlaceX+PlaceY]-= 1;
	prot.theGame_Menu.theGame.theLabyrinth.c[PlaceX*20+PlaceY].Set_figure(null);
	prot.theGame_Menu.theGame.theLabyrinth.thePacman.SetX(prot.theGame_Menu.theGame.theLabyrinth.GetStartX());
	prot.theGame_Menu.theGame.theLabyrinth.thePacman.SetY(prot.theGame_Menu.theGame.theLabyrinth.GetStartY());
	prot.theGame_Menu.theScore_Field.Set_lives(prot.theGame_Menu.theScore_Field.Get_lives()-1);
	Stop();
	prot.scrn[20*prot.theGame_Menu.theGame.theLabyrinth.GetStartX()+prot.theGame_Menu.theGame.theLabyrinth.GetStartY()]+= 1;
	prot.theGame_Menu.theGame.theLabyrinth.c[prot.theGame_Menu.theGame.theLabyrinth.GetStartX()*20+prot.theGame_Menu.theGame.theLabyrinth.GetStartY()].Set_figure(this);
	} 

	public int Query() 
		
	{
	return 1;
	}

}
