// Proto modellhez szukseges eljarasok:
//
//        konstruktor : amibol maga a Jatek fut
//        KeyReader : amivel a billentyuzetrol olvasunk be
//        printn,printp,println : altalanos kijelzofuggvenyek
//        main : az indito.

//--------------------------------------------------------------------------
import java.io.*;
import java.util.*;
import java.lang.String.*;
import java.awt.*;
import java.awt.event.*;
//--------------------------------------------------------------------------
public class Proto extends Frame
{
// Konstruktor. 

    public BufferedWriter output;
	public int version = 5;
	public Grafika child_Grafika;

    public Proto()
      {

  	  System.out.println("\nC64 Forever.\n");

	  System.out.println("Iranyitas kurzorgombokkal.");
	  System.out.println("Space - bomba ki.");
	  System.out.println("Pause - pause.");
	  System.out.println("Escape - kilepes.");

	  Grafika child_Grafika=new Grafika(this);					// rajzolasert felelos objektum
      add(child_Grafika);
	  child_Grafika.betolt();
	  pack();

	  mysetTitle("Pacman");			// Ablak cime
	  addWindowListener(new WindowAdapter()
		  {	// Ablak letrehozasa
			public void windowClosing(WindowEvent e)
				{ System.exit(-1);}
		  });
      setResizable(false);					// Nem lehet atmeretezni az ablakot
	  setSize(700,500);						// Meret
	  setBackground(new Color(0,0,0));		// Szin
	  show();
	  toFront();

/*	Menu menu= new Menu(rajz);													//a tartalmazott objektumok megpeldanyositasa
	High_Score_Table highscoretable=new High_Score_Table(rajz);
	boolean kilep=false;
	Date ido;
	long utoljara;
*/
    Jatek child_Jatek = new Jatek(this, child_Grafika); 

    }

/*  System.out.println("\nMelyik verziot kivanja tesztelni?\n");
	System.out.println("1. Pacman mozogni probal.");
	System.out.println("2. Pacman felvesz 2 gyemantot.");
	System.out.println("3. Buta szorny mozog 10 kort.");
	System.out.println("4. Okos szorny mozog elkapja Pacman-t.");
	System.out.println("5. Pacman felszed ezt-azt.");
	System.out.println("6. Pacman es a bombak.");
	System.out.println("7. Az okos szorny es az aktiv bomba.\n");

    version=KeyReader(1,7);

	try { 
        output=(new BufferedWriter(new FileWriter(new File("output"+version+".txt"))));    

		Jatek child_Jatek = new Jatek(this);

		output.close();

	    }
   catch (IOException e) {System.out.println("File error."); System.exit(0); }

//      System.out.println("konstruktor vege: Proto"); 
    } */

public void mysetTitle(String title)
	{
	setTitle(title);	
	}
//--------------------------------------------------------------------------
// Kiiro eljarasok az output szebbitese erdekeben.
// Vegigoroklodnek az egesz programon.

    public String whitespace="";

// a kiirando eggyel bentebb kerul
    public void printp(String xout)
    {
      whitespace+=" ";
//      System.out.println(whitespace+xout);
    }

// a kovetkezo kovetkezo eggyel kintebb kerul
    public void printn(String xout)
    {
//      System.out.println(whitespace+xout);
//      whitespace=whitespace.substring(0,whitespace.length()-1);
    }

// spontan kijelzes
    public void println(String xout)
    {
  //    System.out.println(whitespace+xout);
    }

// iras file-ba string

	public void printlnfile(String xout)
	{
//	  System.out.println(xout);

/*	  try { output.write(xout+'\n');}
	  catch (IOException e) {System.out.println("File error."); System.exit(0); }
*/
	}

// iras file-ba char

	public void printfile(char xout)
	{
//	  System.out.print(xout);
/*	  try { output.write(xout);}
	  catch (IOException e) {System.out.println("File error."); System.exit(0); }
*/
	}

//--------------------------------------------------------------------------

// Keyreader eljaras
// Hibaturo billentyuzetrol beolvaso
// Bemeno parameterkent megkapja a beolvasando szam also es felso korlatjat,
// kimeno parameterkent visszaadja a beolvasott szamot.

    public int KeyReader(int tol, int ig)
    {
         int bill=-1;
         boolean okay=false;

         System.out.println(tol+"-tol "+ig+"-ig kerek egy egesz szamot.");

         BufferedReader Y = new BufferedReader(new InputStreamReader(System.in));
         while (okay==false)
           { okay=true;
             try { bill=Integer.parseInt(Y.readLine());}
             catch (Exception e) {okay=false;}  
             if ((bill<tol) || (bill>ig)) {okay=false;}
             if (okay==false) {System.out.println("Rossz valasz, "+tol+"-tol "+ig+"-ig kerek egy szamot.");}
           }
          return bill;
     }

//--------------------------------------------------------------------------
/*
    public void HighStuff()
	  {
		HighScore_Handler hst=new HighScore_Handler();
		if (hst.felkerulhet(child_Jatszma.Score))
			{
	        nevBeAblak= new Frame("Game Over");
	        nevBeAblak.setFont(new Font("Helvetica Bold", Font.BOLD, 18));
	        nevBeAblak.setBackground(Color.gray);
	        Panel p1=new Panel();
	        p1.setLayout(new BorderLayout());
	        p1.add("North", new Label("Gratulálok, felkerült a High-Score táblára"));
	        nevField=new TextField();
	        p1.add("Center", nevField);
	        Button ok= new Button("OK");
	        class OkButtonListener implements ActionListener
				{
				boolean lenyomva;
				public void actionPerformed( ActionEvent e )
					{
			        if (!lenyomva)
						{ 
						nevBeAblak.show(false);
						hst.frissit(nevField.getText(), elertPontszam);
						hst.kiir();
						lenyomva=true;
				        }
					}
		        }
	        ok.addActionListener(new OkButtonListener() );
	        p1.add("South", ok);
	        nevBeAblak.add(p1);
	        nevBeAblak.setResizable(false);	
	        nevBeAblak.pack();
	        nevBeAblak.show();
			}
		else hst.kiir();
     }*/
//-------------------------------------------------------------------------------------------------

  public static void main(String[] args)
    {
       Proto Proto_Object = new Proto();
    }
}