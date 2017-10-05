import java.util.Random;

class MonsterThread extends Thread 
{
	int seged[]=new int[400];
	int ut[]=new int[400];
	Monster m;
	int dir;
	int u;
	int s=0;
	boolean futas=true;
	int n=0;
	int actual=0;
	int irany;
	int szorny=0;
	int pacman=0;
	int rendez[]=new int[4];
	int endez[]=new int[4];
	Monster[] Monsters;
	Proto prot; 
	Random random=new Random();
	public MonsterThread(String s,Monster[] M,int number,Proto proto) {
		super(s);
		u=number;
		m=M[u];
		Monsters=M;
		prot=proto;
	}
	public void stop2() {futas=false;}

	public void run()
	{
		while (futas)
		{
			if (Monsters[u].Intelligent==false)
			{
			dir = random.nextInt(4);
			Monsters[u].Start();
			Monsters[u].Set_directions(dir,prot);
			}
			if (Monsters[u].Intelligent==true)
			{
		for (int i=0;i<400 ;i++ )
		{
			seged[i]=0;
		}
		for (int i=0;i<4 ;i++ )
		{
			rendez[i]=0;
		}
		for (int i=0;i<400 ;i++ )
		{
			ut[i]=0;
		}
			szorny=Monsters[u].GetX()*20+Monsters[u].GetY();
			seged[szorny]=1;
			pacman=prot.theGame_Menu.theGame.theLabyrinth.thePacman.GetX()*20+prot.theGame_Menu.theGame.theLabyrinth.thePacman.GetY();
			while (seged[pacman]==0)
			{
			for (int g=0;g<400 ;g++ )
			{
			if (seged[g]>0)
			{
				if (g+1<399)
				{
				if ((prot.theGame_Menu.theGame.theLabyrinth.Level_Map[g+1]==0)&&(seged[g+1]==0))
				{
					seged[g+1]=seged[g]+1;
				}
				}
				if (g-20>0)
				{
				if ((prot.theGame_Menu.theGame.theLabyrinth.Level_Map[g-20]==0)&&(seged[g-20]==0))
				{
					seged[g-20]=seged[g]+1;
				}
				}
				if (g+20<399)
				{
				if ((prot.theGame_Menu.theGame.theLabyrinth.Level_Map[g+20]==0)&&(seged[g+20]==0))
				{
					seged[g+20]=seged[g]+1;
				}
				}
				if (g-1>0)
				{
				if ((prot.theGame_Menu.theGame.theLabyrinth.Level_Map[g-1]==0)&&(seged[g-1]==0))
				{
					seged[g-1]=seged[g]+1;
				}
				}
	
			}
			}
			}
		for (int y=0;y<400 ;y++ )
				{
					if (seged[y]==0)
					{
						seged[y]=1000;
					}
				}
			actual=pacman;
			n=1;
			while (ut[n-1]!=szorny)
			{
				rendez[0]=seged[actual+1];
				rendez[1]=seged[actual-1];
				rendez[2]=seged[actual+20];
				rendez[3]=seged[actual-20];
				endez[0]=seged[actual+1];
				endez[1]=seged[actual-1];
				endez[2]=seged[actual+20];
				endez[3]=seged[actual-20];
			for (int g=0;g<4 ;g++ )
			{
				for (int f=0;f<4;f++ )
				{
					if (rendez[g]<rendez[f])
					{
						s=rendez[g];
						rendez[g]=rendez[f];
						rendez[f]=s;
					}					
				}
			}	
	if (rendez[0]==endez[0])
	{
			ut[n]=actual+1;
	}				
	if (rendez[0]==endez[1])
	{
			ut[n]=actual-1;
	}				
	if (rendez[0]==endez[2])
	{
			ut[n]=actual+20;
	}				
	if (rendez[0]==endez[3])
	{
			ut[n]=actual-20;
	}				
				actual=ut[n];
				n++;
				}
			switch (ut[n-2]-szorny)
			{
			case -20: {irany=0;break;}
			case  20: {irany=2;break;}
			case  -1: {irany=3;break;}
			case  1: {irany=1;break;}
			
			}
			Monsters[u].Start();
			Monsters[u].Set_directions(irany,prot);
			}
			try
			{
			sleep (Monsters[u].Speed*100);
			}
		catch (InterruptedException e){	}
		}
	}
public void mutat(){
for (int x=0;x<20 ;x++ )
{
for (int y=0;y<20 ;y++ )
{
	if (seged[20*x+y]<10)
	{
	System.out.print((char)(seged[20*x+y]+48));
	System.out.print(' ');
	}
	if (seged[20*x+y]>10)
	{
	System.out.print((char)((seged[20*x+y]/10)+48));
	System.out.print((char)((seged[20*x+y]%10)+48));
	}
}
	System.out.print('\n');
}
}
}
