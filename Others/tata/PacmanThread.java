class PacmanThread extends Thread 
{
	Pacman p;
	Proto prot;
	boolean futas=true;
	public PacmanThread(String s,Pacman thePacman,Proto proto) {
		super(s);
		p=thePacman;
		prot=proto;
		}
	public void stop2() {futas=false;}
	public void run()
	{
		while (futas)
		{
			p.Start();
			p.Set_directions(p.nextmove,prot);
			p.Check_Bonus(p,prot);
			if (p.vanbomb)
			{
				p.Put_Bomb(prot,prot.Get_Time());
				p.vanbomb=false;
			}
			try
			{
			sleep (p.Speed*100);
			}
		catch (InterruptedException e){	}
		}
	}
}
