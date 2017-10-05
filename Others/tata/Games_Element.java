//Source file: C:/jdk1.3/bin/Games_Element.java


public abstract class Games_Element 
{
   
   /**
Az X koordinata.
   */
   protected int PlaceX;
   
   /**
Az Y koordinata.
   */
   protected int PlaceY;
	public Monster alap;
   public int kod; //diamond=1, bomb=2, life=3, bonus=4, detonatingbomb=5
//   public Labyrinth theLabyrinth;
   public Proto theProto;
   
   public void Delete(Proto prot) 
   {
	PlaceX=250;
	PlaceY=250;
   }
   
   /**
Az elem  X koordinatajaval ter vissza
   @roseuid 3ABCA6120136
   */
   public int GetX() 
   {
	return PlaceX;
   }
   
   /**
Az elem Y koordinatajaval ter vissza
   @roseuid 3ABCA61700C8
   */
   public int GetY() 
   {
	return PlaceY;
   }
   
   /**
Az elem X koordinatajat allitja be.
   @roseuid 3ABCA61E030C
   */
   public void SetX(int x) 
   {
	PlaceX=x;
   }
   
   /**
Az elem Y koordinatajat allitja be.
   @roseuid 3ABCA6250096
   */
   public void SetY(int y) 
   {
	PlaceY=y;
   }
	public void Answer (Figure f, Proto prot){}
	public void Set_bonus(Games_Element g){}
	public void Set_figure(Figure f){}
	public Games_Element return_bonus(){return this;}
	public Figure return_figure(){return alap;}
	public void Get_Type(Figure f, Proto prot){}
	public void Check (Figure f,int d, Proto prot) {}	
	public void Robban (Detonating_Bomb db,int i,Proto prot) {}
	public void lehet(Games_Element g)	{}
	public void nemlehet(Games_Element g)	{}
	public void hatralevo(int i)	{}
	public void Detonate(Proto prot) {}
	public int UpTime () {return 0;}
	public boolean Miez() {return true;}
   }
