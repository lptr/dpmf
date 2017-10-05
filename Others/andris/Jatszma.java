// Jatszma
//
// Innen hozzuk letre a jatekot, inditjuk majd el a szalakat.

public class Jatszma
  {
    public Proto parent_Proto;
    public Grafika parent_Grafika;
    public Scheduler child_Scheduler;
    public Level child_Level;

    boolean ExitStatus=false;

// Ezen a valtozon keresztul jelenik meg, ha elfogy a PacMan osszes elete.

    public boolean GameOver=false;

    String PlayerName;

// Hanyadik palya, ebbol szamoljuk a szornyek szamat, sebesseget es intelligenciajat
    public int Level=0;

// A jatekos pontszama, a  Highscoretable-nal lesz jelentos.
    public int Score=0;
    public int Time=0;

// Konstruktor

    public Jatszma(Proto prot, Grafika graf)
      {
      parent_Proto=prot;
      parent_Grafika=graf;

      parent_Proto.printp("konstruktor     : Jatszma");
      UjLevel();
      parent_Proto.printn("konstruktor vege: Jatszma");
      }

// Kovetkezo szint legeneralasa. Letrehozzuk, elinditjuk, a vegen kilepunk.

    public void UjLevel()
      {
      parent_Proto.printp("meghivodik      : Jatszma:UjLevel");
      Level++;;
      GenerateLevel(Level);
      Start();
      GameOver();
      parent_Proto.printp("meghivodik vege : Jatszma:UjLevel");
      }

// Level keszul.

    public void GenerateLevel(int Level)
        {
        parent_Proto.printp("meghivodik      : Jatszma:GenerateLevel"); 

		parent_Grafika.game_setup();

		child_Level = new Level(parent_Proto, parent_Grafika, this, Level);
        child_Level.GenerateLab();
        parent_Proto.printn("meghivodik vege : Jatszma:GenerateLevel"); 
        }

// Elinditjuk a Schedulert, ami a jatekot vezerli.

    public void Start()
        {
        parent_Proto.printp("meghivodik      : Jatszma:Start");
        child_Scheduler = new Scheduler(parent_Proto, parent_Grafika, this);    
        parent_Proto.printn("meghivodik vege : Jatszma:Start");
        }

// Ha vege, lepjunk ki.

    public void GameOver()
        {
          parent_Proto.printp("meghivodik      : Jatszma:GameOver");
          ExitStatus=true;
          parent_Proto.printn("meghivodik vege : Jatszma:GameOver");
        }

  }