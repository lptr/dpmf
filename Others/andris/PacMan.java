// PacMan osztaly.

public class PacMan
  {

// Szukseges valzotok

    public int PosX;
    public int PosY;
    public int Bombs;

    public Proto parent_Proto;
    public Labirintus parent_Labirintus;

// Konstruktor

    public PacMan(Proto prot, Labirintus labi, int _PosX, int _PosY)
    {
      parent_Proto=prot;
      parent_Labirintus=labi;
      
      parent_Proto.printp("konstruktor     : PacMan");

      PosX=_PosX;
      PosY=_PosY;
      
      parent_Labirintus.Terkep[PosY*(parent_Labirintus.X*5)+PosX*5+1]=1;
      
	  int data=parent_Labirintus.Terkep[PosY*(parent_Labirintus.X*5)+PosX*5];	// refresh
	  parent_Labirintus.Terkep[PosY*(parent_Labirintus.X*5)+PosX*5]=data|16;

      parent_Proto.printn("konstruktor vege: PacMan");
    }


// PacMan letesz egy bombat

    public  void SetBomb()
    {
        parent_Proto.printp("meghivodik      : PacMan:SetBomb");
        Bombs--;
        parent_Labirintus.AddSurprise(4,24,PosX,PosY);
        parent_Proto.printn("meghivodik vege : PacMan:SetBomb"); 
    }

// PacMan mozdul

    public  void Move(int dir)
      {
        parent_Proto.printp("meghivodik      : PacMan:Move"); 


        parent_Labirintus.Terkep[PosY*(parent_Labirintus.X*5)+PosX*5+1]=0;

        int data=parent_Labirintus.Terkep[PosY*(parent_Labirintus.X*5)+PosX*5];
		parent_Labirintus.Terkep[PosY*(parent_Labirintus.X*5)+PosX*5]=data|16;	// refresh

        switch (dir)
        {
        case 1:    // fel
            {
            parent_Proto.printlnfile("Parancs: PacMan fel");
            if ((data & 1)==0) 
                {
                parent_Proto.printlnfile("PacMan mozdult: "+PosX+","+PosY+" -> "+PosX+","+(--PosY));
                }
            else {parent_Proto.printlnfile("PacMan nem tudott mozdulni.");}
            break;
            }
        case 2:    // jobbra
            {
            parent_Proto.printlnfile("Parancs: PacMan jobbra");
            if ((data & 2)==0)
                {
                parent_Proto.printlnfile("PacMan mozdult: "+PosX+","+PosY+" -> "+(++PosX)+","+PosY);
                }
            else {parent_Proto.printlnfile("PacMan nem tudott mozdulni.");}
            break;
            }
        case 3:    // le
            {
            parent_Proto.printlnfile("Parancs: PacMan le");
            if ((data & 4)==0)
                {
                parent_Proto.printlnfile("PacMan mozdult: "+PosX+","+PosY+" -> "+PosX+","+(++PosY));
                }
            else {parent_Proto.printlnfile("PacMan nem tudott mozdulni.");}
            break;
            }
        case 4:    // balra
            {
            parent_Proto.printlnfile("Parancs: PacMan balra");
            if ((data & 8)==0)
                {
                parent_Proto.printlnfile("PacMan mozdult: "+PosX+","+PosY+" -> "+(--PosX)+","+PosY);
                }
            else {parent_Proto.printlnfile("PacMan nem tudott mozdulni.");}
            break;
            }
        } 
        
        parent_Labirintus.Terkep[PosY*(parent_Labirintus.X*5)+PosX*5+1]=1;

		data=parent_Labirintus.Terkep[PosY*(parent_Labirintus.X*5)+PosX*5];
		parent_Labirintus.Terkep[PosY*(parent_Labirintus.X*5)+PosX*5]=data|16;	//refresh

        parent_Proto.printn("meghivodik vege : PacMan:Move");  
      }

    public void Kill()
      {
        parent_Proto.printp("meghivodik      : Gyemant.Kill");
        parent_Labirintus.Terkep[PosY*(parent_Labirintus.X*5)+PosX*5+1]=0;

		int data=parent_Labirintus.Terkep[PosY*(parent_Labirintus.X*5)+PosX*5];
		parent_Labirintus.Terkep[PosY*(parent_Labirintus.X*5)+PosX*5]=data|16;	// refresh

        parent_Proto.printn("meghivodik      : Gyemant.Kill");
      }
  }