// Bonus osztaly, a Surprise absztrakt osztaly kiegeszitoje.

public class Bonus extends Surprise
  {

// Ennyivel no a score.
    int Value=500;

    public Proto parent_Proto;

// Proto
    public Bonus(Proto prot)
      {
         parent_Proto=prot;
         parent_Proto.printp("konstruktor     : Bonus");
         parent_Proto.printn("konstruktor vege: Bonus");
      }

//Meghal
    public void Kill()
      {
         parent_Proto.printp("meghivodik      : Bonus:Kill");
         parent_Proto.printn("meghivodik vege : Bonus:Kill");
      }

  
  };