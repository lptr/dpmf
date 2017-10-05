// Gyemant osztaly.

public class Gyemant
  {

// Szukseges valzotok

    public int PosX;
    public int PosY;

    public Proto parent_Proto;
    public Labirintus parent_Labirintus;

// Konstruktor

    public Gyemant(Proto prot, Labirintus labi, int px, int py, int sorszam)
    {
      parent_Proto=prot;
      parent_Labirintus=labi;
      
      PosX=px;
      PosY=py;

      parent_Proto.printp("konstruktor     : Gyemant");
      parent_Labirintus.Terkep[PosY*(parent_Labirintus.X*5)+PosX*5+3]=sorszam+12;

	  int data=parent_Labirintus.Terkep[PosY*(parent_Labirintus.X*5)+PosX*5];
	  parent_Labirintus.Terkep[PosY*(parent_Labirintus.X*5)+PosX*5]=data|16; // refresh

	  parent_Proto.printn("konstruktor vege: Gyemant");
    }

    public void Kill()
      {
        parent_Proto.printp("meghivodik      : Gyemant.Kill");
        parent_Labirintus.Terkep[PosY*(parent_Labirintus.X*5)+PosX*5+3]=0;

	    int data=parent_Labirintus.Terkep[PosY*(parent_Labirintus.X*5)+PosX*5];
	    parent_Labirintus.Terkep[PosY*(parent_Labirintus.X*5)+PosX*5]=data|16; // refresh

		parent_Proto.printn("meghivodik      : Gyemant.Kill");
      }
  }