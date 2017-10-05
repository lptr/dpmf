// Maga a jatek - innen hivodik meg az inditokepernyo, beallitasok, help, credits, es maga a jatek.

public class Jatek
  {
    public Proto parent_Proto;
    public Grafika parent_Grafika;

    boolean ExitStatus=false;
	public int access=0;

// Konstruktor, itt kommunikalhat a felhasznalo a programmal.

	public int hol_all=0;
	public int melyik_menupont=0;

    public Jatek(Proto prot, Grafika graf)
    {
      parent_Proto=prot;
      parent_Grafika=graf;
      parent_Proto.printp("konstruktor     : Jatek");
/*      
      while (ExitStatus==false)
      {
        parent_Proto.println("\nJatek valasztasi lehetosegek.");
        parent_Proto.println("1. Jatek:Initialize           -> Jatszma");
        parent_Proto.println("2. Jatek:Menu_Handler(HST)    -> Menu_Handler");
        parent_Proto.println("3. Jatek:Menu_Handler(Help)   -> Menu_Handler");
        parent_Proto.println("4. Jatek:Menu_Handler(Credits)-> Menu_Handler");
        parent_Proto.println("5. Jatek:Config_Handler       -> Config_Handler");
        parent_Proto.println("6. Jatek:Exit                 -> exit");

// A beolvasott szamnak megfelelo fuggveny hivodik meg.

        switch ( parent_Proto.KeyReader(1,6) )
        {
          case 1: { Initialize();  break; }
          case 2: { Menu_Handler("HST"); break; }
          case 3: { Menu_Handler("Help"); break; }
          case 4: { Menu_Handler("Credits"); break; }
          case 5: { Config_Handler();break;    }
          case 6: { Exit(); break; }
        }
      }
*/

	  while (true)
	  {
		
		parent_Grafika.menu_logo();
		parent_Grafika.menu_rajz(0);

     	do
	    {
  	       if (Bill.megnyomtad==true)	 // Billentyuzet olvasasa
		     {	    
				Bill.megnyomtad=false;
				melyik_menupont=mozgat(Bill.mozgas);	 // Lepes a menuben
			 }
	    }
	    while (Bill.mozgas!=50);
		
		parent_Grafika.menu_rajz(hol_all);
		switch (melyik_menupont)
		{
		case 0: Initialize(); break;
		case 1: parent_Grafika.show_High(); break;
		case 2: System.exit(0); break;
		}
	  }
    }

public int mozgat(int merre)
	  {
	     switch (merre)
			{
			case 1: hol_all=(hol_all+1) %3; break;		// Lefele lepes
			case 3: hol_all=(hol_all+2) % 3; break;		// Felfele lepes
			default: {;}
			}	
		 parent_Grafika.menu_rajz(hol_all);
		 return(hol_all);
	  }

//	  Initialize();
//      Exit();

//      parent_Proto.printn("konstruktor vege: Jatek");
   

// Kilepunk a jatekbol.

    void Exit()
      {  
        parent_Proto.printp("meghivodik      : Exit");
        ExitStatus=true;
        parent_Proto.printn("meghivodik vege : Exit");

      }

// Induljon a kovetkezo objektum : Jatszma!

    void Initialize ()
      {
        parent_Proto.printp("meghivodik      : Jatek:Initialize");
        Jatszma Jatszma_Object = new Jatszma(parent_Proto, parent_Grafika);
		parent_Proto.printn("meghivodik vege : Jatek:Initialize");
      }


// A Menu_Handler kiirja az altalunk kivalasztott segitseget, highscoretablat, vagy creditset.

    void Menu_Handler (String Choosen)
      {
        parent_Proto.printp("meghivodik      : Jatek:Menu_Handler("+Choosen+")");
        Menu_Handler Menu_Handler_Object = new Menu_Handler(parent_Proto, Choosen);
        parent_Proto.printn("meghivodik vege : Jatek:Menu_Handler("+Choosen+")");
      }  

  }