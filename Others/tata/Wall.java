class Wall extends Games_Element 
{
   public Wall(int x,int y, Proto prot) 
   {
//	System.out.print(x*10+y+" ");
	PlaceX=x;
	PlaceY=y;
	prot.scrn[20*x+y]=128;
//	WallDraw dw=new WallDraw(x,y);
   }
	public boolean Miez() {return false;}

	public void Check(Figure f,int d)
	{

		f.Disallowed(d);
	}
		public void Robban(Detonating_Bomb db,int i)
		{
			db.nemlehet(this);
			db.hatralevo(i);

		}
   public void Delete() 
   {
   }
}
