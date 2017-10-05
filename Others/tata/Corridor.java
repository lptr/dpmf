//Source file: C:/jdk1.3/bin/Corridor.java


public class Corridor extends Games_Element 
{
   
   /**
A mezon talalhato bonusz.
   */
   private Games_Element Bonus_type;
   
   /**
ki van kodolva, hogy a mezo mely oldalain van fal.
   */
   
   /**
A mezon talalhato figura.
   */
   private Figure Figure_type;
   public Proto theProto;
   
   public Corridor(int x,int y, Proto prot) 
   {
//	System.out.print(x*10+y+" ");
	PlaceX=x;
	PlaceY=y;
   }
   
   /**
Letorli a falat.
   @roseuid 3AB4D36A010E
   */
   public void Delete() 
   {
   }
   
   /**
   @roseuid 3AB92C430348
   */
	public void Check(Figure f,int d, Proto prot)
	{
		f.Allowed(d, prot);
	}

	public void Get_Type(Figure f, Proto prot)
	{
	if (Bonus_type!=null)
		{
			Bonus_type.Answer(f, prot);
		}
	
	}
   
   /**
A mezon talalhato bonusszal ter vissza.
   @roseuid 3ABCAAE40258
   */
   public Games_Element return_bonus() 
   {
	return Bonus_type;
   }

  
   /**
A mezon talalhato figuraval ter vissza.
   @roseuid 3ABCAAF2546
   */
   public Figure return_figure() 
   {
	return Figure_type;
   }
   
   /**
A mezon talalhato bonuszt allitja be.
   @roseuid 3ABD17C50244
   */
   public void Set_bonus(Games_Element g) 
   {
	Bonus_type=g;
   }
   
   /**
A mezon tartozkodo figurat allitja be.
   @roseuid 3ABD17CC01C2
   */
   public void Set_figure(Figure f) 
   {
	Figure_type=f;
   }
	public boolean Miez() {return true;}
public void Robban(Detonating_Bomb db,int i,Proto prot)
		{
			db.lehet(this,prot);
			db.hatralevo(i--);
		}
}
