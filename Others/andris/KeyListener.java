import java.io.*;
import java.util.*;
import java.lang.Math.*;

// Keylistener - ezen a szalon keresztul adhat majd be a felhasznalo
// billentyuzetrol erkezo parancsokat.
// pl. irany, pause, tuz (bomba lerak)

public class KeyListener extends Thread
  {

    boolean ExitStatus=false;
    
    public Proto parent_Proto;
    public Scheduler parent_Scheduler;
    public Grafika parent_Grafika;

// Konstruktor

    public KeyListener(Scheduler sche, Proto prot, Grafika graf)
      {
      parent_Scheduler=sche;
      parent_Proto=prot;
      parent_Grafika=graf;

// Egyszerre tortenhetnek a dolgok a Scheduler ill. Keylistener szalakon, ezert
// innen barmelyik esemenyt szimulalhatjuk.

      parent_Proto.printp("konstruktor     : KeyListener");
/*
      try
          {
            String line;
            int ForduloSzam=0;

            line=parent_Scheduler.parent_Jatszma.child_Level.child_Labirintus.parent_Project.readLine(); // Game

            parent_Proto.printlnfile("\n"+ForduloSzam+". fordulo utan");
            ForduloSzam++;

            parent_Scheduler.parent_Jatszma.child_Level.child_Labirintus.DisplayLab(); // 0. kor

            while ( (ExitStatus==false) &&
                  (parent_Scheduler.parent_Jatszma.child_Level.LevelCompleted==false) && 
                  (parent_Scheduler.parent_Jatszma.GameOver==false) )
            {

              boolean ExitRound=false;

              parent_Proto.printlnfile("\n"+ForduloSzam+". fordulo.");
                ForduloSzam++;

              while (ExitRound!=true)
                {
                line=parent_Scheduler.parent_Jatszma.child_Level.child_Labirintus.parent_Project.readLine();
                if (line.equals("200"))  { Keypressed_Direction(1); }    //    fel
                if (line.equals("201"))  { Keypressed_Direction(2); }    //    jobbra
                if (line.equals("202"))  { Keypressed_Direction(3); }    //    le
                if (line.equals("203"))  { Keypressed_Direction(4); }    //    balra
                if (line.equals("204"))  { Keypressed_BombOut(); }        //    bomba ki
                if (line.equals("p"))    { Keypressed_Pause(); }        //    pause

                if (line.equals(":")==true) {ExitRound=true;}    // Filevege?
                if (line.equals("End")==true) {ExitRound=true;ExitStatus=true;} // Vege a Game resznek
    
                if (ExitRound==true)
                  {
                    parent_Scheduler.parent_Jatszma.child_Level.child_Labirintus.TurnMonst();  // Mozduljanak a kor vegen a szornyek

                    int X=parent_Scheduler.parent_Jatszma.child_Level.child_Labirintus.X;
                    int Y=parent_Scheduler.parent_Jatszma.child_Level.child_Labirintus.Y;

                    // Bombak ketyegjenek
                    for (int y=0; y<Y; y++)
                    {
                      for (int x=0; x<X; x++)
                      {
                        int sudata=parent_Scheduler.parent_Jatszma.child_Level.child_Labirintus.Terkep[y*X*5+x*5+4];
                        if (sudata>=100)
                        {
                          if (sudata<200) { parent_Scheduler.parent_Jatszma.child_Level.child_Labirintus.Terkep[y*X*5+x*5+4]=0; }
                          else { parent_Scheduler.parent_Jatszma.child_Level.child_Labirintus.Terkep[y*X*5+x*5+4]-=100;}
                        }
                      }
                    }
                    

                    parent_Proto.printlnfile("Egyeb parancsok?");
                    BufferedReader E = new BufferedReader(new InputStreamReader(System.in));
                    try 
                        {
                        String bill=(E.readLine());
                        if (bill.equals("200"))  { Keypressed_Direction(1); }    //    fel
                        if (bill.equals("201"))  { Keypressed_Direction(2); }    //    jobbra
                        if (bill.equals("202"))  { Keypressed_Direction(3); }    //    le
                        if (bill.equals("203"))  { Keypressed_Direction(4); }    //    balra
                        if (bill.equals("204"))  { Keypressed_BombOut(); }        //    bomba ki
                        if (bill.equals("p"))    { Keypressed_Pause(); }        //    pause
                        }
                    catch (Exception e) {;}  

                    parent_Scheduler.parent_Jatszma.child_Level.child_Labirintus.DisplayLab();
                    parent_Scheduler.parent_Jatszma.Time++;
                    parent_Proto.printlnfile("Ido:   "+parent_Scheduler.parent_Jatszma.Time);
                    parent_Proto.printlnfile("Score: "+parent_Scheduler.parent_Jatszma.Score);
                  }
                }
            }
          }
      catch (IOException e) {parent_Proto.printlnfile("File error."); System.exit(0); }

        // Kiirjuk a labirintust

/*
        parent_Proto.println("\nKeylistener:");
        parent_Proto.println("1. fel");
        parent_Proto.println("2. jobbra");
        parent_Proto.println("3. le");
        parent_Proto.println("4. balra");
        parent_Proto.println("5. bomba ki");
        parent_Proto.println("6. pause");

        switch ( parent_Proto.KeyReader(1,7) )
        {
          case 1: { Keypressed_Direction(1); break; }
          case 2: { Keypressed_Direction(2); break; }
          case 3: { Keypressed_Direction(3); break; }
          case 4: { Keypressed_Direction(4); break; }
          case 5: { Keypressed_BombOut(); break; }
          case 6: { Keypressed_Pause(); break; }
          case 7: { ExitStatus=true; break; }
        }*/
      parent_Proto.printn("konstruktor vege: KeyListener");
      }

//-----------------------------------------------------------------------------------------------------
	public int framecounter=0;
	public int sucounter=100;
	public int bocounter=80;
	public int mocounter=2;
	public int rocounter=10;

	public void run() 
	{
		while (true)
			{      
			if (Bill.pause==true)
				{
				parent_Proto.mysetTitle("Paused.");
				}
			else
			{
			parent_Proto.mysetTitle("Pacman");
			// Labirintus megjelenites
            parent_Scheduler.parent_Jatszma.child_Level.child_Labirintus.DisplayLab();

 		    // Billentyuzet olvasasa
			if (Bill.megnyomtad==true)
		     {
			    Bill.megnyomtad=false;
				if (Bill.mozgas==3)  { Keypressed_Direction(1); }	 //	fel
                if (Bill.mozgas==0)  { Keypressed_Direction(2); }    //	jobbra
                if (Bill.mozgas==1)  { Keypressed_Direction(3); }    //	le
                if (Bill.mozgas==2)  { Keypressed_Direction(4); }    // balra
                if (Bill.space==true){ Bill.space=false;
									   Keypressed_BombOut(); }       // bomba ki
				if (Bill.kilepes==true){System.exit(0);}
			 }

			// 10 koronkent mozdulnak a szornyek is.
			if (--framecounter<0)
			{
			framecounter=16-parent_Scheduler.parent_Jatszma.Level*2;
            parent_Scheduler.parent_Jatszma.child_Level.child_Labirintus.TurnMonst();  // Mozduljanak a kor vegen a szornyek
			}

			// szornyek megjelenese
			if (--mocounter==0)
			{
			  mocounter=50;
			  int nom=parent_Scheduler.parent_Jatszma.child_Level.child_Labirintus.NumberOfMonsters;
			  if ((nom<10) && (nom<parent_Scheduler.parent_Jatszma.Level*2))
			  {
			    nom++;
			    parent_Scheduler.parent_Jatszma.child_Level.child_Labirintus.NumberOfMonsters=nom;
			    parent_Scheduler.parent_Jatszma.child_Level.child_Labirintus.GenerateMonst(nom+1,9,9,1-nom%3);
			  }
			}

			// elixirek megjelenese

			if (--sucounter==0)
			{
	        Random r = new Random();
	        int random_number = r.nextInt()%50;
			sucounter=random_number+200;

			  int noe=parent_Scheduler.parent_Jatszma.child_Level.child_Labirintus.NumberOfElixirs;
			  if (noe<3)
			  {
					  int b=Math.abs(r.nextInt()%9);
					  parent_Scheduler.parent_Jatszma.child_Level.child_Labirintus.AddSurprise(1,noe+25,b,b);
					  noe++;
				      parent_Scheduler.parent_Jatszma.child_Level.child_Labirintus.NumberOfElixirs=noe;
			  }
			}
			// bomba megjelenese
			if (--bocounter==0)
			{
	        Random r = new Random();
			int random_number = r.nextInt()%50;
			bocounter=random_number+200;

			  int nob=parent_Scheduler.parent_Jatszma.child_Level.child_Labirintus.NumberOfBombs;
			  if (nob<3)
			  {
					  int b=Math.abs(r.nextInt()%9);
					  parent_Scheduler.parent_Jatszma.child_Level.child_Labirintus.AddSurprise(2,nob+22,b,b);
					  nob++;
				      parent_Scheduler.parent_Jatszma.child_Level.child_Labirintus.NumberOfBombs=nob;
			  }
		
			
			}

            // Bombak ketyegjenek
			if (--rocounter==0)
			{
					rocounter=10;	
                    int X=parent_Scheduler.parent_Jatszma.child_Level.child_Labirintus.X;
                    int Y=parent_Scheduler.parent_Jatszma.child_Level.child_Labirintus.Y;
					for (int y=0; y<Y; y++)
                    {
                      for (int x=0; x<X; x++)
                      {
                        int sudata=parent_Scheduler.parent_Jatszma.child_Level.child_Labirintus.Terkep[y*X*5+x*5+4];
                        if (sudata>=100)
                        {
                          if (sudata<200)
						  {
							  parent_Scheduler.parent_Jatszma.child_Level.child_Labirintus.Terkep[y*X*5+x*5+4]=0; 
							  parent_Scheduler.parent_Jatszma.child_Level.child_Labirintus.Terkep[y*X*5+x*5]=parent_Scheduler.parent_Jatszma.child_Level.child_Labirintus.Terkep[y*X*5+x*5]|16;
						  }
                          else { parent_Scheduler.parent_Jatszma.child_Level.child_Labirintus.Terkep[y*X*5+x*5+4]-=100;}
                        }
                      }
                    }
			}

			}
			// next frame
			try { sleep(100); }
			catch(InterruptedException e) {return;}
			}
			
	}
//----------------------------------------------------------------------------------------------------


// Valamelyik iranyba szeretnenk mozgatni a pacmant.

      void Keypressed_Direction(int dir)
      {
      parent_Proto.printp("meghivodik      : KeyListener:Keypressed_Direction");
      parent_Scheduler.parent_Jatszma.child_Level.child_Labirintus.MovePacMan(dir);
      parent_Proto.printn("meghivodik vege : KeyListener:Keypressed_Direction");
      }

// Pause - elaltatjuk/felebresztjuk a szalakat. Majd.

      void Keypressed_Pause()
      {
      parent_Proto.printp("meghivodik      : KeyListener:Keypressed_Pause");
      parent_Proto.println("Szalak leallitas/ujrainditas");
      parent_Proto.printn("meghivodik vege : KeyListener:Keypressed_Pause");
      }

// Bombat rakunk le.

      void Keypressed_BombOut()
      {
      parent_Proto.printp("meghivodik      : KeyListener:Keypressed_BombOut");
      int c=parent_Scheduler.parent_Jatszma.child_Level.child_Labirintus.PacMan_Object.Bombs;
      if (c!=0)
          {
          parent_Scheduler.parent_Jatszma.child_Level.child_Labirintus.PacMan_Object.SetBomb();
          parent_Proto.printlnfile("Bombak: "+c+" -> "+(c-1));
          }
          else
          {
          parent_Proto.printlnfile("Bombak: 0");
          }

      parent_Proto.printn("meghivodik vege : KeyListener:Keypressed_BombOut");
      }

  }