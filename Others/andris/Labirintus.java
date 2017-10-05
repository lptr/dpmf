import java.io.*;
import java.util.*;
import java.lang.String.*;

// Labirintus.

public class Labirintus
  {
    public Level parent_Level;
    public Proto parent_Proto;
    public Grafika parent_Grafika;

// Beszedes valtozonevu valtozok

    public int NumberOfDiamands=0;
    public int NumberOfMonsters;
    public int NumberOfTalented;

	public int NumberOfElixirs=0;
	public int NumberOfBombs=0;

	public int Lives=3;

    public int X=0;
    public int Y=0;
    public int PacmanX=0;
    public int PacmanY=0;

    public BufferedReader parent_Project;

// Konstruktor

    public Labirintus(Proto prot, Grafika graf, Level leve, int _NumberOfMonsters, int _NumberOfTalented)
      {
        parent_Level=leve;
        parent_Proto=prot;
		parent_Grafika=graf;

        NumberOfMonsters=0; // _NumberOfMonsters;
        NumberOfTalented=_NumberOfTalented;

// Beszedjuk file-bol az adatokat...


// Elhelyezzuk a kezdeshez a dolgokat - gyemantok, szornyek, pacman - filebol olvasni :(

    try { 
        BufferedReader project=(new BufferedReader(new FileReader(new File("lab"+(parent_Level.parent_Jatszma.Level%3)+".txt"))));    
        GenerateLab(project);
        GeneratePacman(0,0);        

		// Gyemantok
		for (int ObjectNumber=12; ObjectNumber<22 ; ObjectNumber++ )
        {
        GenerateDiam(ObjectNumber-12,9-(ObjectNumber-12),ObjectNumber-12);
		NumberOfDiamands++;
		}


/*        try
          {
            String line;
            boolean exit=false;
            while (exit!=true)
            {
              line=project.readLine();

              if (line.substring(2,3).equals(","))
                {
                  int ObjectNumber=Integer.parseInt(line.substring(0,2));

                  if (ObjectNumber==1)                // PacMan
                    {
                      GeneratePacman(Integer.parseInt(line.substring(3,5)),Integer.parseInt(line.substring(6,8)));
                    }
                  if ((ObjectNumber>11) && (ObjectNumber<22))    // Gyemantok
                    {
                      GenerateDiam(ObjectNumber-12,Integer.parseInt(line.substring(3,5)),Integer.parseInt(line.substring(6,8)));
                      NumberOfDiamands++;
                    }
                  if ((ObjectNumber>1) && (ObjectNumber<12))    // Szornyek
                    {
                      GenerateMonst(ObjectNumber-2,Integer.parseInt(line.substring(3,5)),Integer.parseInt(line.substring(6,8)),Integer.parseInt(line.substring(9,11)));
                      NumberOfMonsters++;
                    }
                  if ((ObjectNumber>24) && (ObjectNumber<28))    // Elixir
                    {
                      AddSurprise(1,ObjectNumber,Integer.parseInt(line.substring(3,5)),Integer.parseInt(line.substring(6,8)));
                    }
                  if ((ObjectNumber>21) && (ObjectNumber<25))    // Bomba
                    {
                      if (line.substring(9,11).equals("00")) { AddSurprise(2,ObjectNumber,Integer.parseInt(line.substring(3,5)),Integer.parseInt(line.substring(6,8)));}
                      else { AddSurprise(4,ObjectNumber,Integer.parseInt(line.substring(3,5)),Integer.parseInt(line.substring(6,8)));}
                    }

                }
              else
                {
                  if (line==null) {exit=true;}    // Filevege?
                  if (line.equals("End")==true) {exit=true;} // Vege az init resznek
                }
            }
          }
        catch (IOException e) {parent_Proto.printlnfile("File error."); System.exit(0); }
        */
		}
    catch (IOException e) {parent_Proto.printlnfile("File error."); System.exit(0); }
    }

// Tombokbe foglaljuk, akiket lehet.

    public final Monsters Monsters_List[]=new Monsters[12];
    public final Gyemant Gyemant_List[]=new Gyemant[12];
    public final Surprise Surprise_List[]=new Surprise[12];
    public PacMan PacMan_Object;
    
    public int Terkep[]=new int[20*20*5];

    public int MonsterIndex[]=new int[15];

// Terkep krealas
    void GenerateLab(BufferedReader proj)
      {
        parent_Project=proj;

        parent_Proto.printp("meghivodik      : Labirintus:GenerateLab"); 

        try
          {
            String line;
            line=parent_Project.readLine();    // "Init"
            line=parent_Project.readLine();    // X
            X=Integer.parseInt(line);
            line=parent_Project.readLine();    // Y
            Y=Integer.parseInt(line);

            for (int y=0; y<Y; y++)            // Labirintus krealas
              {
                line=parent_Project.readLine();
                char [] chars = line.toCharArray();
                for (int x=0; x<X; x++)
                  {
                       Terkep[y*X*5+x*5]=(int)chars[x]-97+16;
                  }
              }
          }
        catch (IOException e)
          {
            parent_Proto.printlnfile("File error.");
            System.exit(0);
          }
        parent_Proto.printn("meghivodik vege : Labirintus:GenerateLab"); 
      }


// Terkep display
    void DisplayLab()
      {
        parent_Proto.printp("meghivodik      : Labirintus:DisplayLab"); 

		int data=0;
        int pmdata=0, modata=0, didata=0, sudata=0;
        char ch=' ';
		int item=0;

		for (int y=0; y<Y; y++)
        { for (int x=0; x<X; x++)
          {
			// Falak
			data=Terkep[y*X*5+x*5];

			// Objectek
            pmdata=Terkep[y*X*5+x*5+1];
            modata=Terkep[y*X*5+x*5+2];
            didata=Terkep[y*X*5+x*5+3];
            sudata=Terkep[y*X*5+x*5+4];

			ch=' ';
            if (didata!=0)    { ch='D'; }    // Gyemant
            if (sudata>=200)    { ch='0'; }
/*          if (sudata>=200)    { ch='1'; }
            if (sudata>=300)    { ch='2'; }
            if (sudata>=400)    { ch='3'; }
            if (sudata>=500)    { ch='4'; }
            if (sudata>=600)    { ch='5'; } // Aktiv bomba fazisai */
            if ((sudata>21) && (sudata<25))    { ch='b'; }    // Inaktiv bomba
            if ((sudata>24) && (sudata<28))    { ch='e'; }    // Elixir
            if ((sudata>27) && (sudata<31))    { ch='+'; }    // Bonusz
            if (pmdata!=0)    { ch='P'; }    // Pacman
			if (modata!=0)    { ch='M'; }    // Monster

			if ((data & 16)!=0)
			{
			data=data & 15;
			Terkep[y*X*5+x*5]=data;
			parent_Grafika.paint_lab(x,y,data,ch);
			}
			
		  
		  }
		}


// Innen megy a prototipus kiirasa
/*
        for (int y=0; y<Y; y++)
        {
          for (int x=0; x<X; x++)
          {
            data=Terkep[y*X*5+x*5];
			// Kitolto mezo
            if (((data & 1)!=0) || ((data & 8)!=0)) { ch='#'; }
            else {ch=' ';}
            parent_Proto.printfile(ch);
            // Felso mezo
            if ((data & 1)!=0) { ch='#'; }
            else {ch=' ';}
            parent_Proto.printfile(ch);
          }
          parent_Proto.printlnfile("#");

          for (int x=0; x<X; x++)
          {
            data=Terkep[y*X*5+x*5];
            // Balra mezo
            if ((data & 8)!=0) { ch='#'; }
            else {ch=' ';}
            parent_Proto.printfile(ch);

            // Object mezo
            pmdata=Terkep[y*X*5+x*5+1];
            modata=Terkep[y*X*5+x*5+2];
            didata=Terkep[y*X*5+x*5+3];
            sudata=Terkep[y*X*5+x*5+4];

            ch=' ';
            if (didata!=0)    { ch='D'; }    // Gyemant
            if (sudata>=200)    { ch='0'; }
            if (sudata>=200)    { ch='1'; }
            if (sudata>=300)    { ch='2'; }
            if (sudata>=400)    { ch='3'; }
            if (sudata>=500)    { ch='4'; }
            if (sudata>=600)    { ch='5'; } // Aktiv bomba fazisai
            if ((sudata>21) && (sudata<25))    { ch='b'; }    // Inaktiv bomba
            if ((sudata>24) && (sudata<28))    { ch='e'; }    // Elixir
            if ((sudata>27) && (sudata<31))    { ch='+'; }    // Bonusz
            if (pmdata!=0)    { ch='P'; }    // Pacman
            if (modata!=0)    { ch='M'; }    // Monster

            parent_Proto.printfile(ch);
          }
          parent_Proto.printlnfile("#");
        }

        // Zarosor.
        for (int x=0; x<=(X*2); x++)
          {
            parent_Proto.printfile('#');
          }
        parent_Proto.printlnfile("");
		*/
        parent_Proto.printn("meghivodik vege : Labirintus:DisplayLab"); 

      }


// Gyemantok...
    void GenerateDiam(int ObjectNumber, int x, int y)        
        {
          parent_Proto.printp("meghivodik      : Labirintus:GenerateDiam"); 
          Gyemant_List[ObjectNumber]=new Gyemant(parent_Proto, this, x, y, ObjectNumber);
          parent_Proto.printn("meghivodik vege : Labirintus:GenerateDiam"); 
        }

// Szornyek...
    void GenerateMonst(int sorszam, int x, int y, int isTalented)
        {
          parent_Proto.printp("meghivodik      : Labirintus:GenerateMonst"); 
          Monsters_List[sorszam] = new Monsters(parent_Proto, this, x, y, sorszam, isTalented);
          MonsterIndex[sorszam]=1;
          parent_Proto.printn("meghivodik vege : Labirintus:GenerateMonst"); 
        }

// Pacman...
    void GeneratePacman(int Px, int Py)
      {
          PacmanX=Px;
          PacmanY=Py;

          parent_Proto.printp("meghivodik      : Labirintus:GeneratePacMan"); 
          PacMan_Object = new PacMan(parent_Proto, this, PacmanX, PacmanY);
          parent_Proto.printn("meghivodik vege : Labirintus:GeneratePacMan"); 
      }

// Monster mozdul. Ekkor fellephet utkozes, vizsgaljuk. (Fejlesztes alatt)
    public void TurnMonst()        
        {
          parent_Proto.printp("meghivodik      : Labirintus:TurnMonst");
          for (int x=0; x<10; x++)
          {
            if ((MonsterIndex[x])!=0) {    Monsters_List[x].Move(x); }
          }

          CheckCollision();
          parent_Proto.printn("meghivodik vege : Labirintus:TurnMonst"); 
        }

// Uj ajandek jelenik meg, vagy a pacman lerakott egy bombat.
    public  void AddSurprise(int type, int sorszam, int xp, int yp)
        {
          parent_Proto.printp("meghivodik      : Labirintus:TurnSurprise");
          if (type==0) { Surprise_List[0] = new Bonus(parent_Proto); }
          if (type==1) { Surprise_List[sorszam-22] = new Elixir(parent_Proto, this, xp, yp, sorszam); }
          if (type==2) { Surprise_List[sorszam-22] = new Bomb(parent_Proto, this, xp, yp, sorszam-22, false); }
          if (type==4) { Surprise_List[sorszam-22] = new Bomb(parent_Proto, this, xp, yp, sorszam-22, true); }
          parent_Proto.printn("meghivodik vege : Labirintus:TurnSurprise");
        }

// Surprise eltuntetese. (Fejlesztes alatt)
    public  void KillSurprise(int number)        
        {
          parent_Proto.printp("meghivodik      : Labirintus:KillSurprise");
          Surprise_List[number-22].Kill();
          parent_Proto.printn("meghivodik vege : Labirintus:KillSurprise"); 
        }

// Monster eltuntetese. 
    public  void KillMonster(int x)
        {
          parent_Proto.printp("meghivodik      : Labirintus:KillMonster");
          Monsters_List[x].Kill();
          MonsterIndex[x]=0;
          parent_Level.parent_Jatszma.Score+=10;
          parent_Proto.printn("meghivodik vege : Labirintus:KillMonster"); 
        }

// Gyemant eltuntetese. (Fejlesztes alatt)
    public  void KillDiamand(int number)
        {
          parent_Proto.printp("meghivodik      : Labirintus:KillDiamand("+(number-12)+")");
          Gyemant_List[number-12].Kill();
          parent_Level.parent_Jatszma.Score+=8;
          parent_Proto.printn("meghivodik vege : Labirintus:KillDiamand("+(number-12)+")"); 
        }

// PacMan mozdul - letrejohetett utkozes - checkoljuk.
    public  void MovePacMan(int dir)
        {
          parent_Proto.printp("meghivodik      : Labirintus:MovePacman"); 
          PacMan_Object.Move(dir);
          CheckCollision();
          parent_Proto.printn("meghivodik vege : Labirintus:MovePacman"); 
        }

// Mi tortenhetett? Utkozes - valaki valakivel/valamivel.
// Ugyeljunk, hogy egy objektumot ne szuntessunk meg, ha meg nem is letezik.

    public  void CheckCollision()
      {

		   //Gyemant es Pacman utkozik - eggyel kevesebb gyemant. Ha elfogytak, uj szint!

           int code=Terkep[PacMan_Object.PosY*(X*5)+PacMan_Object.PosX*5+3];
           if (code!=0)
           {
             parent_Proto.printp("utkozik         : PacMan es Gyemant");
             parent_Proto.printlnfile("Utkozes: "+(code-12)+". Gyemant es PacMan");
             parent_Proto.printlnfile("Kikapcsol: "+(code-12)+". Gyemant");
             KillDiamand(code);
             parent_Proto.printlnfile("Gyemantok: "+NumberOfDiamands+" -> "+(NumberOfDiamands-1));
             NumberOfDiamands--;
             if (NumberOfDiamands==0)
               {
                 parent_Level.LevelCompleted=true;
               }
             parent_Proto.printn("utkozik vege    : PacMan es Gyemant"); 
           }

           // PacMan es Monster

           code=Terkep[PacMan_Object.PosY*(X*5)+PacMan_Object.PosX*5+2];
           if (code!=0)
           {
             parent_Proto.printp("utkozik         : PacMan es Monster");
             parent_Proto.printlnfile("Utkozes: "+(code-2)+". Szorny es PacMan");
             parent_Proto.printlnfile("Eletek: "+Lives+" -> "+(Lives-1));
             PacMan_Object.Kill();
             KillMonster(code-2);
					  Lives--;
					  if (Lives==0) { parent_Level.parent_Jatszma.GameOver=true; }
                      else          { GeneratePacman(0,0); }
             parent_Proto.printn("utkozik vege    : PacMan es Gyemant"); 
           }

           //Elixir meghal, jatekos elete eggyel tobb.

           code=Terkep[PacMan_Object.PosY*(X*5)+PacMan_Object.PosX*5+4];
           if ((code<28) && (code>24))
           {
             parent_Proto.printp("utkozik         : PacMan es Surprise:Elixir");
             parent_Proto.printlnfile("Utkozes: "+(code-25)+". Elixir es PacMan");
             parent_Proto.printlnfile("Kikapcsol: "+(code-25)+". Elixir");
             KillSurprise(code);
             parent_Proto.printlnfile("Eletek: "+Lives+" -> "+(++Lives));

             parent_Proto.printn("utkozik vege    : PacMan es Surprise:Elixir"); 
           }

           // Inaktiv bomba es PacMan
           code=Terkep[PacMan_Object.PosY*(X*5)+PacMan_Object.PosX*5+4];
           if ((code>21) && (code<25))
           {
             parent_Proto.printp("utkozik         : PacMan es Surprise:Bomba");
             parent_Proto.printlnfile("Utkozes: "+(code-22)+". Bomba es PacMan");
             parent_Proto.printlnfile("Kikapcsol: "+(code-22)+". Bomba");
             KillSurprise(code);
             parent_Proto.printlnfile("Bombak: "+PacMan_Object.Bombs+" -> "+(++PacMan_Object.Bombs));

             parent_Proto.printn("utkozik vege    : PacMan es Surprise:Bomba"); 
           }

           // Aktiv bomba es szorny
            for (int x=0; x<10; x++)
          {
            if ((MonsterIndex[x])!=0) 
                {
                  code=Terkep[Monsters_List[x].YPos*(X*5)+Monsters_List[x].XPos*5+4]; 
                  if (code>100)
                   {
                     parent_Proto.printp("utkozik         : Szorny es Surprise:Bomba_Aktiv");
                     parent_Proto.printlnfile("Utkozes: Bomba es Szorny");
                     parent_Proto.printlnfile("Kikapcsol: Mindketto");
                     if (code>500) {code-=500;}
                     if (code>400) {code-=400;}
                     if (code>300) {code-=300;}
                     if (code>200) {code-=200;}
                     if (code>100) {code-=100;}
                     KillSurprise(code);
                     KillMonster(x);
                     parent_Proto.printn("utkozik vege    : Szorny es Surprise:Bomba_Aktiv"); 
                   }

                }
          }
	  parent_Grafika.display_score(parent_Level.parent_Jatszma.Score,PacMan_Object.Bombs,Lives);
      }
  }

/*
             boolean ExitStatus=false;
             while ( (ExitStatus==false) &&
                     (parent_Level.LevelCompleted==false) && 
                     (parent_Level.parent_Jatszma.GameOver==false)
                   )
              {
                parent_Proto.println("\nUtkozesek szimulacio.");
                parent_Proto.println("1. Nem volt utkozes");
                parent_Proto.println("2. PacMan es Monster");
                parent_Proto.println("3. PacMan es Surprise:Bonus");
                parent_Proto.println("4. PacMan es Surprise:Elixir");
                parent_Proto.println("5. PacMan es Surprise:Bomb");
                parent_Proto.println("6. Monstr es Surprise:Bomb(aktiv)"); 
                parent_Proto.println("7. PacMan es Gyemant");


                
                  case 1: 
                      {
//Mehetunk tovabb
                      ExitStatus=true;
                      break;
                      }

                  case 2:
                      {
                      parent_Proto.printp("utkozik         : PacMan es Monster"); 
//Gameover, vagy pedig ujraindul a szint.
                      parent_Proto.println("lives           : "+Lives+"->"+(Lives-1));
                      Lives--;
                      if (Lives==0)
                      {
                      parent_Level.parent_Jatszma.GameOver=true;
                      break;
                      }
                      else
                      {
                      GenerateDiam();                      
                      GeneratePacman();
                      GenerateMonst();
                      }
                      parent_Proto.printn("utkozik vege    : PacMan es Monster"); 
                      break; 
                      }

                     case 3:
                      {
                      parent_Proto.printp("utkozik         : PacMan es Surprise:Bonus");
                      KillSurprise("Bonus");
                      parent_Proto.println("Score           : Score+Surprise");
                      parent_Proto.printn("utkozik vege    : PacMan es Surprise:Bonus"); 
                      break; 
                      }

//Elixir meghal, jatekos elete eggyel tobb.
                     case 4:
                      {
                      parent_Proto.printp("utkozik         : PacMan es Surprise:Elixir");
                      KillSurprise("Elixir");
                      parent_Proto.println("Eletek          : "+Lives+" -> "+(Lives++));
                      parent_Proto.printn("utkozik vege    : PacMan es Surprise:Elixir"); 
                      break; 
                      }

//Bomba meghal, Pacmannak eggyel tobb.
                     case 5:
                      {
                      parent_Proto.printp("utkozik         : PacMan es Surprise:Bomb");
                      KillSurprise("Bomb");
                      parent_Proto.println("Bombak          : "+PacMan_Object.Bombs+" -> "+(PacMan_Object.Bombs+1));
                      PacMan_Object.Bombs++;
                      parent_Proto.printn("utkozik vege    : PacMan es Surprise:Bomb"); 
                      break; 
                      }

//Bomba is, Monster is meghal.
                     case 6:
                      {
                      parent_Proto.printp("utkozik         : Monster es Bomba(aktiv)");
                      KillSurprise("Bomb");
                      KillMonster();
                      parent_Proto.printn("utkozik vege    : Monster es Bomba(aktiv)"); 
                      break; 
                      }

//Eggyel kevesebb gyemant. Ha elfogytak, uj szint!
                     case 7:
                      {
                      parent_Proto.printp("utkozik         : PacMan es Gyemant");
                      KillDiamand();
                      parent_Proto.println("Gyemantok       : "+NumberOfDiamands+" -> "+(NumberOfDiamands-1));
                      NumberOfDiamands--;
                      if (NumberOfDiamands==0)
                      {
                       parent_Level.LevelCompleted=true;
                      }
                      parent_Proto.printn("utkozik vege    : PacMan es Gyemant"); 
                      break; 
                      }

                }
              }
      }
  }*/