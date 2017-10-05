// Egy szint a jatekban. Letrehozza a labirintust.

public class Level
  {
    public Jatszma parent_Jatszma;
    public Labirintus child_Labirintus;
    public Proto parent_Proto;
    public Grafika parent_Grafika;
    boolean ExitStatus=false;

// Ezen keresztul jelezheti a szint, hogy kesz. (Elfogytak a gyemantok.)

    public boolean LevelCompleted=false;

    int Monster;
    int Talented;
    int Spd;

// Konstruktor

    public Level(Proto prot, Grafika graf, Jatszma jatsz, int Level)
      {
        parent_Proto=prot;
        parent_Jatszma=jatsz;
		parent_Grafika=graf;

        parent_Proto.printp("konstruktor     : Level");
        Monster=Level*2;
        Talented=Monster-1;
        Spd=1;
        parent_Proto.printn("konstruktor vege: Level");
      }

// Lebirintust generalunk a szukseges dolgokkal

    public void GenerateLab()
        {
          parent_Proto.printp("meghivodik      : Level:GenerateLab");
          child_Labirintus = new Labirintus(parent_Proto, parent_Grafika, this, Monster, Talented);
          parent_Proto.printn("meghivodik vege : Level:GenerateLab"); 
        }
  }