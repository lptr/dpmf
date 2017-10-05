class ManagerThread extends Thread 
{
	Proto prot;
	boolean futas=true;
	public ManagerThread(Proto p){prot=p;}
	public void stop2() {futas=false;}
	public void run() {
	while (futas)
	{
		prot.theGame_Menu.theGame.theLabyrinth.Generate(20,prot);	

			for (int i=0;i<400 ;i++ )
		{
			if (prot.theGame_Menu.theGame.theLabyrinth.db[i]!=null)
			{
			if ((prot.Get_Time()-prot.theGame_Menu.theGame.theLabyrinth.db[i].Get_Time())>prot.theGame_Menu.theGame.theLabyrinth.db[i].RemainingTime())
			{
				prot.theGame_Menu.theGame.theLabyrinth.db[i].Detonate(prot);
			}
			}
		}
		for (int i=0;i<400 ;i++ )
		{
			if (prot.theGame_Menu.theGame.theLabyrinth.c[i].return_bonus()!=null)
			{
			if ((prot.Get_Time()-prot.theGame_Menu.theGame.theLabyrinth.c[i].return_bonus().UpTime())>0)
			{
				prot.theGame_Menu.theGame.theLabyrinth.c[i].return_bonus().Delete(prot);
			}
			}
		}
	prot.Set_Time(prot.Get_Time()+1);
	try
	{
		sleep (200);
	}
	catch (InterruptedException e){	}
		}
	}
}
