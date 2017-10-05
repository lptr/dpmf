import java.util.*;

// Monsters osztaly.

public class Monsters
  {

    public Proto parent_Proto;
    public Labirintus parent_Labirintus;

    public int XPos;
    public int YPos;
    public int isTalented;

// Konstruktor.

    public Monsters(Proto prot, Labirintus labi, int xp, int yp, int sorszam, int it)
      {
        parent_Proto=prot;
        parent_Labirintus=labi;

        XPos=xp;
        YPos=yp;
        isTalented=it;
        parent_Proto.printp("konstruktor     : Monsters");
        parent_Labirintus.Terkep[YPos*(parent_Labirintus.X*5)+XPos*5+2]=sorszam+2;

		int data=parent_Labirintus.Terkep[YPos*(parent_Labirintus.X*5)+XPos*5];
		parent_Labirintus.Terkep[YPos*(parent_Labirintus.X*5)+XPos*5]=data|16;

        parent_Proto.printn("konstruktor vege: Monsters");
      }

// Jellemzo valtozok. 


// Mozog

    public void Move(int sorszam)
        {
          parent_Proto.printp("meghivodik      : Monsters:Move");
          if (isTalented==0)
            {      
              Random random=new Random();
              int dir=0;
              boolean moved=false;

			  int data=parent_Labirintus.Terkep[YPos*(parent_Labirintus.X*5)+XPos*5];
			  parent_Labirintus.Terkep[YPos*(parent_Labirintus.X*5)+XPos*5]=data|16;	//refresh

              parent_Labirintus.Terkep[YPos*(parent_Labirintus.X*5)+XPos*5+2]=0; // Toroljuk
			  
              while (moved==false)
              {
                dir=random.nextInt(4);
                switch (dir)
                {
                case 0 : { if ( ((data & 1)==0) && (parent_Labirintus.Terkep[(YPos-1)*(parent_Labirintus.X*5)+XPos*5+2]==0) )  { YPos--; moved=true;} break; }
                case 1 : { if ( ((data & 2)==0) && (parent_Labirintus.Terkep[YPos*(parent_Labirintus.X*5)+(XPos+1)*5+2]==0) )  { XPos++; moved=true;} break; }
                case 2 : { if ( ((data & 4)==0) && (parent_Labirintus.Terkep[(YPos+1)*(parent_Labirintus.X*5)+XPos*5+2]==0) )  { YPos++; moved=true;} break; }
                case 3 : { if ( ((data & 8)==0) && (parent_Labirintus.Terkep[YPos*(parent_Labirintus.X*5)+(XPos-1)*5+2]==0) )  { XPos--; moved=true;} break; }                
                }
              }
              parent_Labirintus.Terkep[YPos*(parent_Labirintus.X*5)+XPos*5+2]=sorszam+2; // Kitesszuk az ujat

			  data=parent_Labirintus.Terkep[YPos*(parent_Labirintus.X*5)+XPos*5];
			  parent_Labirintus.Terkep[YPos*(parent_Labirintus.X*5)+XPos*5]=data|16;	// refresh
            }
          else 
            {
              int Calc[]=new int[20*20];
              boolean found=false;
              int data=0;

              int X=parent_Labirintus.X;
              int Y=parent_Labirintus.Y;

              Calc[YPos*parent_Labirintus.X+XPos]=1;
              Calc[parent_Labirintus.PacMan_Object.PosY*X+parent_Labirintus.PacMan_Object.PosX]=100;

              while (found==false)
              {
                 for (int y=0; y<Y ;y++ )
                  {
                     for (int x=0; x<X ;x++ )
                       {
                          if ((Calc[y*X+x]!=0) && (Calc[y*X+x]!=100)) // ha egy elem !0, a tole elerheto elemeket megszamozzuk
                          {
                            data=parent_Labirintus.Terkep[y*(X*5)+x*5];
                            if ((data & 1)==0)
                            {
                            if (Calc[(y-1)*X+x]==0) { Calc[(y-1)*X+x]=Calc[y*X+x]+1; }
                            if (Calc[(y-1)*X+x]==100) {found=true;}
                            }    

                            if ((data & 2)==0)
                            {
                             if (Calc[y*X+(x+1)]==0) { Calc[y*X+(x+1)]=Calc[y*X+x]+1; }
                            if (Calc[y*X+(x+1)]==100) {found=true;}
                            }

                            if ((data & 4)==0)
                            {
                            if (Calc[(y+1)*X+x]==0) { Calc[(y+1)*X+x]=Calc[y*X+x]+1; }
                            if (Calc[(y+1)*X+x]==100) {found=true;}
                            }

                            if ((data & 8)==0)
                            {
                            if (Calc[y*X+(x-1)]==0) { Calc[y*X+(x-1)]=Calc[y*X+x]+1; }
                            if (Calc[y*X+(x-1)]==100) {found=true;}
                            }
                          }


                       }
                  }

              }
/*
                 // Display
                 for (int yd=0; yd<parent_Labirintus.Y ;yd++ )
                 {  
                     for (int xd=0; xd<parent_Labirintus.X ; xd++)
                        {
                         parent_Proto.printfile(Calc[yd*parent_Labirintus.X+xd]);
                        }
                     parent_Proto.printlnfile();
                 }
*/
                 // Visszafele vezeto legrovidebb ut keresese

                 int xb=parent_Labirintus.PacMan_Object.PosX;
                 int yb=parent_Labirintus.PacMan_Object.PosY;

                 int xbnew=0, ybnew=0;
                 found=false;
                 int max=100;
                 int dir=4;
                 while (found==false)
                 {
                    data=parent_Labirintus.Terkep[yb*(X*5)+xb*5];
                    if ((data & 1)==0) { if ((Calc[(yb-1)*X+xb]<max) && (Calc[(yb-1)*X+xb]!=0)) { max=Calc[(yb-1)*X+xb];xbnew=xb;ybnew=yb-1; dir=0;} }    
                    if ((data & 2)==0) { if ((Calc[yb*X+(xb+1)]<max) && (Calc[yb*X+(xb+1)]!=0)) { max=Calc[yb*X+(xb+1)];xbnew=xb+1;ybnew=yb; dir=1;} }
                    if ((data & 4)==0) { if ((Calc[(yb+1)*X+xb]<max) && (Calc[(yb+1)*X+xb]!=0)) { max=Calc[(yb+1)*X+xb];xbnew=xb;ybnew=yb+1; dir=2;} }
                    if ((data & 8)==0) { if ((Calc[yb*X+(xb-1)]<max) && (Calc[yb*X+(xb-1)]!=0)) { max=Calc[yb*X+(xb-1)];xbnew=xb-1;ybnew=yb; dir=3;} }
                    xb=xbnew; yb=ybnew;
                    if (Calc[yb*X+xb]==1) 
                        {
                        found=true;
                        parent_Labirintus.Terkep[YPos*(parent_Labirintus.X*5)+XPos*5+2]=0; // Toroljuk

						data=parent_Labirintus.Terkep[YPos*(parent_Labirintus.X*5)+XPos*5];
						parent_Labirintus.Terkep[YPos*(parent_Labirintus.X*5)+XPos*5]=data|16; // refresh

						switch (dir)
                          {  // Pont az ellenkezo iranyban mozgunk
                            case 0 : { if (parent_Labirintus.Terkep[(YPos+1)*(parent_Labirintus.X*5)+XPos*5+2]==0) {YPos++;} break; }
                            case 1 : { if (parent_Labirintus.Terkep[YPos*(parent_Labirintus.X*5)+(XPos-1)*5+2]==0) {XPos--;} break; }
                            case 2 : { if (parent_Labirintus.Terkep[(YPos-1)*(parent_Labirintus.X*5)+XPos*5+2]==0) {YPos--;} break; }
                            case 3 : { if (parent_Labirintus.Terkep[YPos*(parent_Labirintus.X*5)+(XPos+1)*5+2]==0) {XPos++;} break; }                
                          }
                        parent_Labirintus.Terkep[YPos*(parent_Labirintus.X*5)+XPos*5+2]=sorszam+2; // Kitesszuk az ujat                        
						
						data=parent_Labirintus.Terkep[YPos*(parent_Labirintus.X*5)+XPos*5];
						parent_Labirintus.Terkep[YPos*(parent_Labirintus.X*5)+XPos*5]=data|16; // refresh
                        }
                 }

            }

          parent_Proto.printn("meghivodik vege : Monsters:Move");
        }

// Meghal

    public void Kill()
      {
         parent_Proto.printp("meghivodik      : Monsters:Kill");

		 int data=parent_Labirintus.Terkep[YPos*(parent_Labirintus.X*5)+XPos*5];
		 parent_Labirintus.Terkep[YPos*(parent_Labirintus.X*5)+XPos*5]=data | 16;		// refresh

		 parent_Labirintus.Terkep[YPos*(parent_Labirintus.X*5)+XPos*5+2]=0;

         parent_Proto.printn("meghivodik vege : Monsters:Kill");
      }
  }