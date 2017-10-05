import java.util.*;

// Scheduler - kulon szal lesz, o osztja majd az eszt egy jatszman belul.
//
// Feladata a KeyListener billentyuzetfigyelo eletrehivasa,
// eljarasai az idopontokhoz kotott esemenyeket hivjak (pl. szornyek mozgasa, etc.)

public class Scheduler
  {
    public Jatszma parent_Jatszma;
    public Proto parent_Proto;
    public Grafika parent_Grafika;
    public KeyListener child_KeyListener;

// Konstruktor

    public Scheduler(Proto prot, Grafika graf, Jatszma jate)
      {
        parent_Proto=prot;
        parent_Jatszma=jate;
        parent_Grafika=graf;

        parent_Proto.printp("konstruktor     : Scheduler");
        Activate();
        parent_Proto.printn("konstruktor vege: Scheduler");
      }

// Elinditjuk a KeyListenert. 
// A Proto a KeyListeneren keresztul fut a kommunikacio a tesztelo es a program kozott.

    public void Activate()
      {
        parent_Proto.printp("meghivodik      : Scheduler:Activate");

		parent_Jatszma.child_Level.child_Labirintus.DisplayLab(); // eloszor kirajzoljuk
		child_KeyListener = new KeyListener(this,parent_Proto,parent_Grafika);
		child_KeyListener.start();

// Ide ter vissza, ha GameOver, vagy ha egy Level kesz.
		while (parent_Jatszma.GameOver!=true)  
        {
		  while ((parent_Jatszma.child_Level.LevelCompleted!=true) && (parent_Jatszma.GameOver!=true))
			  {}
		  if (parent_Jatszma.child_Level.LevelCompleted==true)
		  {
		  parent_Jatszma.child_Level.LevelCompleted=false;
		  parent_Jatszma.Level++;
		  parent_Jatszma.child_Level.GenerateLab();
		  }
		}		
		child_KeyListener.stop();
		parent_Proto.printn("meghivodik vege : Scheduler:Activate");
      }

// Szornyek lepnek
    public void Turn_Monster()            
        {
          parent_Proto.printp("meghivodik      : Scheduler:Turn_Monster");
          parent_Jatszma.child_Level.child_Labirintus.TurnMonst();
          parent_Proto.printn("meghivodik vege : Scheduler:Turn_Monster"); 
        }

// Uj ajandek jelenik meg - veletlenszeruen Elixir, Bomba vagy Bonus.

    void Turn_Surprise()        
        {
          parent_Proto.printp("meghivodik      : Scheduler:Turn_Surprise");

//          Random r = new Random();
//          int random_number = r.nextInt()%2+1;
//          parent_Jatszma.child_Level.child_Labirintus.AddSurprise(random_number);

          parent_Proto.printn("meghivodik vege : Scheduler:Turn_Surprise"); 
        }
}