// Elixir osztaly, a Surprise absztrakt osztaly kiegeszitoje.

public class Elixir extends Surprise
  {
    public Proto parent_Proto;
    public Labirintus parent_Labirintus;


// Konstruktor

    public Elixir(Proto prot, Labirintus labi, int px, int py, int sorszam)
      {
         parent_Proto=prot;
         parent_Labirintus=labi;

         parent_Proto.printp("konstruktor     : Elixir");

         PlcX=px;
         PlcY=py;

         parent_Labirintus.Terkep[PlcY*(parent_Labirintus.X*5)+PlcX*5+4]=sorszam;

		 int data=parent_Labirintus.Terkep[PlcY*(parent_Labirintus.X*5)+PlcX*5];
		 parent_Labirintus.Terkep[PlcY*(parent_Labirintus.X*5)+PlcX*5]=data|16; // refresh

         parent_Proto.printn("konstruktor vege: Elixir");
      }

// Meghal

    public void Kill()
      {
         parent_Proto.printp("meghivodik      : Elixir:Kill");
		 parent_Labirintus.Terkep[PlcY*(parent_Labirintus.X*5)+PlcX*5+4]=0;

		 int data=parent_Labirintus.Terkep[PlcY*(parent_Labirintus.X*5)+PlcX*5];
		 parent_Labirintus.Terkep[PlcY*(parent_Labirintus.X*5)+PlcX*5]=data|16; // refresh

		 parent_Proto.printn("meghivodik vege : Elixir:Kill");
      }

  
  };