// Bomba, a Surprise absztrakt osztaly kiegeszitoje

public class Bomb extends Surprise
  {    
    public Proto parent_Proto;
    public Labirintus parent_Labirintus;

    boolean isActive;

// Konstruktor

    public Bomb(Proto prot, Labirintus labi, int px, int py, int sorszam, boolean ia)

    {
        parent_Proto=prot;
        parent_Labirintus=labi;

        isActive=ia;
        PlcX=px;
        PlcY=py;

// vagy aktiv, vagy nem.

        if (isActive==false)
        {
        parent_Proto.printp("konstruktor     : Bomb");
        parent_Labirintus.Terkep[PlcY*(parent_Labirintus.X*5)+PlcX*5+4]=sorszam+22;

		int data=parent_Labirintus.Terkep[PlcY*(parent_Labirintus.X*5)+PlcX*5];
		parent_Labirintus.Terkep[PlcY*(parent_Labirintus.X*5)+PlcX*5]=data|16;	// refresh

        parent_Proto.printn("konstruktor vege: Bomb");
        }
        else
        {
        parent_Proto.printp("konstruktor     : Bomb_active");
        parent_Labirintus.Terkep[PlcY*(parent_Labirintus.X*5)+PlcX*5+4]=sorszam+600+22;

		int data=parent_Labirintus.Terkep[PlcY*(parent_Labirintus.X*5)+PlcX*5];
		parent_Labirintus.Terkep[PlcY*(parent_Labirintus.X*5)+PlcX*5]=data|16;	// refresh

		parent_Proto.printn("konstruktor vege: Bomb_active");
        }
      }

// vagy meghal.

    public void Kill()
      {
        parent_Proto.printp("meghivodik      : Bomb:Kill");
        parent_Labirintus.Terkep[PlcY*(parent_Labirintus.X*5)+PlcX*5+4]=0;

		int data=parent_Labirintus.Terkep[PlcY*(parent_Labirintus.X*5)+PlcX*5];
		parent_Labirintus.Terkep[PlcY*(parent_Labirintus.X*5)+PlcX*5]=data|16;	// refresh

		parent_Proto.printn("meghivodik vege : Bomb:Kill");
      }
  };