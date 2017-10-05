//Source file: C:/jdk1.3/bin/Figure.java


public abstract class Figure 
{
   protected int Speed;
   protected int Seged;
   protected int Direction;
   public Proto theProto;
   /**
A hely X koordinataja.
   */
   protected int PlaceX;
   
   /**
A hely Y koordinataja.
   */
   protected int PlaceY;
   public Corridor c[];
   public Labyrinth theLabyrinth;
   protected int d;
   public Figure(int x, int y, int s, int d) 
   {
	PlaceX=x;
	PlaceY=y;
	Speed=s;
	Seged=s;
	Direction=d;
   } 
   
   /**
   @roseuid 3AB4D20F038E
   */
   public void Kill_figure() 
   {
		
   }
   
   /**
   @roseuid 3AB90DCB01EA
   */
   public void Set_directions(int d,Proto prot) 
   {
	if (d==4) //allj egyhelyben
	{
		return;
	}
	if (d==0)
	{
		Ask(theLabyrinth.c[PlaceX*20+PlaceY-20].return_figure(),this,prot);
		if (Speed!=0)
		{
		theLabyrinth.c[PlaceX*20+PlaceY-20].Check(this,d, prot);
		}
	}
	if (d==1)
	{
		Ask(theLabyrinth.c[PlaceX*20+PlaceY+1].return_figure(),this,prot);
		if (Speed!=0)
		{
		theLabyrinth.c[PlaceX*20+PlaceY+1].Check(this,d, prot);
		}
	}
	if (d==2)
	{
		Ask(theLabyrinth.c[PlaceX*20+PlaceY+20].return_figure(),this,prot);
		if (Speed!=0)
		{
		theLabyrinth.c[PlaceX*20+PlaceY+20].Check(this,d, prot);
		}
	}
	if (d==3)
	{
		Ask(theLabyrinth.c[PlaceX*20+PlaceY-1].return_figure(),this,prot);
		if (Speed!=0)
		{
		theLabyrinth.c[PlaceX*20+PlaceY-1].Check(this,d, prot);
		}
	}
   }

	public void Allowed(int d, Proto prot)
	{
	Move(d, prot);
	}

	public void Disallowed(int d)
	{
	Stop();
	}

   public void Move(int d, Proto prot) 
   {
	this.Start();
	if (this.Query() == 2) { prot.scrn[20*PlaceX+PlaceY]-= 2;}
	theLabyrinth.c[PlaceX*20+PlaceY].Set_figure(null);
	switch (d)
	{
	case 0: {PlaceX--; 	 break;}
	case 1: {PlaceY++; 	 break;}
	case 2: {PlaceX++; 	 break;}
	case 3: {PlaceY--; 	 break;}
	}
	if (this.Query() == 2) { prot.scrn[20*PlaceX+PlaceY]+= 2;}
	theLabyrinth.c[PlaceX*20+PlaceY].Set_figure(this);
   }

   /**
Az X koordinatat adja vissza.
   @roseuid 3ABCA69D003C
   */
   public int GetX() 
   {
	return PlaceX;	   

	}
   
   /**
A figura X koordinatajat allitja be.
   @roseuid 3ABCA6A2017C
   */
   public void SetX(int x) 
   {
	PlaceX=x;
   }
   
   /**
Az Y koordinatat adja vissza.
   @roseuid 3ABCA6A80064
   */
   public int GetY() 
   {
	return PlaceY;
   }

   public void Stop() 
   {
	Seged=Speed;
	Speed=0;
   }   

   public void Start() 
   {
	Speed=Seged;
   }

  
   /**
A figura Y koordinatajat allitja be.
   @roseuid 3ABCA6AD024E
   */
   public void SetY(int y) 
   {
	PlaceY=y;
   }

	public void Pick_Diamond(Proto prot){}
	public void Pick_Bonus(Proto prot){}
	public void Pick_Bomb(Proto prot){}
	public void Pick_Life(Proto prot){}
	public void Eat(Figure e,Proto prot){}
	public void NotEat(Figure e){}
	public void Ask (Figure f,Figure e,Proto prot) {}
	public void Answer (Figure f,Proto prot) {}
	public void Turntoeye() {}
	public void Die (Proto prot) {}
	public int Query() {return 0;}
	public int Harap() {return 0;}
}
