// Menu Handler - megjeleniti a kivalasztott menupontot. 
// Ez lehet segitseg, HST, credits.

public class Menu_Handler
  {
    public Proto parent_Proto;

    public Menu_Handler(Proto prot, String Choosen)
      {
        parent_Proto=prot;

        parent_Proto.printp("konstruktor     : Menu_Handler");
        Show(Choosen);
        parent_Proto.printn("konstruktor vege: Menu_Handler");
      }

    void Show(String Choosen)
      {
        parent_Proto.printp("meghivodik      : Menu_Handler:Show");
        parent_Proto.println("display         : "+Choosen);
        parent_Proto.printn("meghivodik vege : Menu_Handler:Show");
      }
  }