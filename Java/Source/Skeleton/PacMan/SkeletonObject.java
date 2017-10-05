package PacMan;

import java.lang.*;
import java.io.*;
import java.util.*;
// ezeket az osztályokat a seqCanvas használja..
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;  // az AffineTransform használja

/**
 * Ez az osztály, a {@link seqDiagList}-ben tárolt lista egy eleme. A listát a szekvenciadiagram eloállításához használjuk.
 */
class seqDiagListItem extends Object {
	/**
	 * a bejegyzéshez tartozó idopont
	 */
	private int time; 
	/**
	 * az objektum sorszáma
	 */
	private int objNum;
	/**
	 * az objektumhoz rendelt érték. Azt reprezentálja, hogy a szóbanforgó objektum az adott idopillanatban éppen hányszor található meg a hívási láncban.<br>
	 *<dl>
	 *<dt>	0	<dd>érték annyit tesz, az adott onjektum nem szerepel a hívási láncban
	 *<dt>	-1	<dd>az jelenti, hogy az objektum még nincs inicializálva, értelemszeruen a listába ilyen bejegyzés nem kerül.
	 *<dt>	n>0	<dd>az objektum n-szer található meg a hívási láncban.
	 *</dl>
	 */
	private int value; 
	/**
	 * jelenleg ezt az értéket nem használja a program. Valószínü hogy ez az elkövetkezendoekben is így marad, feltehetoen törölve lesz...
	 */
	private boolean init;

	/**
	 * Elem létrehozása. Miután egy elemet létrehoztunk, annak értéke nem módosítható, de erre nincs is szükség, mivel a listát tulajdonképpen loggolásra használjuk.
	 */
	public seqDiagListItem(int t,int o,int v,boolean i) {
		time=t;
		objNum=o;
		value=v;
		init=i;
	}

	/**
	 * Megvizsgálja, a paraméterként kapott azonosítóhoz tartozik e a bejegyzés
	 */
	public boolean isObj(int objNumber) {
		return (objNumber==objNum);
	}

	/**
	 * Megvizsgálja, hogy a bejegyzés legfeljebb a paraméterként átadott idopontban keletkezett-e.
	 */
	public boolean isNotAfter(int time_) {
		return (time<=time_);
	}

	/**
	 * Visszatér a bejegyzett értékkel.
	 */
	public int getValue() {
		return value;
	}
}

/**
 * Szekvenciadiagram függvényhívási listájának alapeleme. Azt tárolja, hogy a {@link seqDiagLabelListItem#time} idopontban bekövetkezett {@link seqDiagLabelListItem#label} cimkéju hívásnak, melyik objektum volt a forrása és melyik a cél.
 */
class seqDiagLabelListItem extends Object {
	/**
	 * bejegyzéshez tartozó idopont
	 */
	public int time;
	/**
	 * a függvényhíváshoz tartozó cimke. A meghívott függvény nevét és adatait tárolja
	 */
	public String label;
	/**
	 * A hívó objektum azonosítója
	 */
	public int from;
	/**
	 * A meghívott függvényt tartalmazó objektum azonosítója
	 */
	public int to;

	/**
	 *  Az elemeket ezzel kell létrehozni, hiszen mivel log jellegu a lista, az elemek módosítása nem lehetséges...
	 */
	public seqDiagLabelListItem(int t, String l, int f, int t2) {
		time = t;
		label = l;
		from = f;
		to = t2;
	}
}		

/**
 * Szekvenciagram megjelenítéséért felelos osztály. Kialakításánál a magas fokú flexibilítás volt a cél, megjelenését rengeteg paraméterrel lehet szabályozni.<br>
 * <B>TODO:</B>Jelenleg ezek a paraméterek szigorúan osztályon belüliek, csak itt állíthatóak be, késobbiekben ezen talán változtatni kéne
 */
class seqCanvas extends Canvas implements KeyListener {
	private Image test;

	/**
	 * VaVánál van egy gyönyöru rajz róla, majd felgrafikázom gépre, az megmutassa mi mit jelent...
	 */ 
	private int X, Y, headerY, timeX, cols, rows, tileX, tileY;
	private int seqTime, maxSeqTime;
	/**
	 * <B>TODO:</B>optmalizálni kell a listában való keresésen, most akár 200msec idofelesleg is keletkezhet emiatt a kirajzolásban...
	 */
	private seqDiagList seqDiag;

	/**
	 * A paraméterként kapott szekv. diagram megjelenítéséhez inicializálás.
	 *
	 * @param list a kirajzolandó szekvenciadiagram objektuma, innen kérdezi le a megjelenítéshez szükséges adatokat.
	 */
	public seqCanvas(seqDiagList list) {
		test = Toolkit.getDefaultToolkit().getImage("back.gif");

		X = 600;
		Y = 400;
		tileX = 40;
		tileY = 20;
		timeX = 40;
		headerY = 60;
		cols = 12;
		rows = 17;
		seqDiag = list;
		seqTime=0;

		Frame f = new Frame("Sequence Diagram");
		Panel p = new Panel();
		f.add (p);
		p.add(this);
		setSize(X, Y);
		f.setSize(640, 440);
		f.pack();
		f.setVisible(true);
		p.addKeyListener(this);
	}

	/**
	 * Diagram idejének beállítása. A szekvenciadiagram adatainak frissítése után célszeru meghívni, ilyenkor görgeti az adatokat, és újrarajzolja a képet.
	 */
	public void setTime(int seqTime) {
		maxSeqTime = this.seqTime = seqTime;
		repaint();
	}

	/**
	 * Egyszeru nyíl rajzolása. A függvény egy vízszintes nyilat képes rajzolni, jelenleg a {@link seqCanvas#renderNormalArrow} metódus használja.
	 *
	 * @param g a szekvenciadiagram képének rajztáblája
	 * @param y a nyíl sora
	 * @param x1 a nyíl kiindulási pontja
	 * @param x2 a nyíl vége, itt található a hegye
	 */
	private void paintArrow(Graphics g, int y, int x1, int x2) {
		if (x1<x2) {
			g.drawLine(x1+5,y,x2-5,y);
			g.drawLine(x2-5,y,x2-10,y-5);
			g.drawLine(x2-5,y,x2-10,y+5);
		} else {
			g.drawLine(x1-5,y,x2+5,y);
			g.drawLine(x2+5,y,x2+10,y-5);
			g.drawLine(x2+5,y,x2+10,y+5);
		}
	}

	/**
	 * Szöveg kirajzolása dobozba. A String paraméterben kapott szöveget rajzolja ki, fekete betükkel, egy sárga hátteru téglalapba.
	 *
	 * @param g a szekvenciadiagram képének rajztáblája
	 * @param label a kiírás szövege
	 * @param x a doboz x koordinátája
	 * @param y a doboz y koordinátája
	 */
	private void renderBoxedText(Graphics g, String label, int x, int y) {
		int fontWidth = (int)g.getFontMetrics().getStringBounds(label,g).getWidth();
		int fontHeight = (int)g.getFontMetrics().getStringBounds(label,g).getHeight()-2;

		g.setColor(Color.yellow);
		g.fillRect(x-1, y-1,fontWidth+3,fontHeight+2);
		g.setColor(Color.black);
		g.drawRect(x-1, y-1,fontWidth+3,fontHeight+2);
		g.drawString(label, x+1, y+fontHeight-2);
	}

	/**
	 * Visszacsatolt nyíl rajzolása. Akkor használja a rendszer, amikor egy osztályon belüli függvényhívást kell ábrázolni
	 *
	 * @param g a szekvenciadiagram képének rajztáblája
	 * @param label a függvényhíváshoz kapcsolódó cimke (<B>TODO:</B>a nyílra kell helyezni)
	 * @param col a kirajzolás oszlopa
	 * @param row a kirajzolás sora
	 */
	private void renderHitchedArrow(Graphics g, String label, int col, int row) {
		int x = timeX + tileX * col + (int)(tileX*0.65);
		int y = headerY + tileY * row;
		int fontHeight = (int)g.getFontMetrics().getStringBounds(label,g).getHeight()-2;
		g.setColor(Color.black);
		g.drawLine(x,y,x+10,y);
		g.drawLine(x+10,y,x+10,y+10);
		g.drawLine(x+10,y+10,x,y+10);
		g.drawLine(x,y+10,x+5,y+15);
		g.drawLine(x,y+10,x+5,y+5);

		renderBoxedText(g, label, x+13, y - fontHeight - 4);
	}

	/**
	 * Normál nyíl rajzolása. Akkor hívódik meg, amikor a függvényhívás két különbözo objektum között következik be.
	 *
	 * @param g a szekvenciadiagram képének rajztáblája
	 * @param label a függvényhíváshoz kapcsolódó cimke (<B>TODO:</B>a nyílra kell helyezni)
	 * @param row a kirajzolás sora
	 * @param from a hívó objektum oszlopa
	 * @param to a fogadó objektum oszlopa, itt található a nyíl hegye..
	 */
	private void renderNormalArrow(Graphics g, String label, int row, int from, int to) {
		int fromX = timeX + tileX * from + (int)(tileX*0.5);
		int toX = timeX + tileX * to + (int)(tileX*0.5);
		int Y = headerY + tileY * row;
		int fontWidth = (int)g.getFontMetrics().getStringBounds(label,g).getWidth();
		int fontHeight = (int)g.getFontMetrics().getStringBounds(label,g).getHeight() - 2;
		g.setColor(Color.black);
		paintArrow(g, Y , fromX, toX);
		renderBoxedText(g, label, (int)((fromX + toX - fontWidth)*0.5), Y - fontHeight - 4);
//		g.drawString(label, (int)((fromX + toX)*0.5),Y-1);
	}

	/**
	 * Nyíl rajzolásáért felelos metódus. a nyíl típusától függoen hívja a {@link seqCanvas#renderHitchedArrow} vagy a {@link seqCanvas#renderNormalArrow} metódusok valamelyikét.
	 *
	 * @param g a szekvenciadiagram képének rajztáblája
	 * @param label a függvényhíváshoz kapcsolódó cimke (<B>TODO:</B>a nyílra kell helyezni)
	 * @param row a kirajzolás sora
	 * @param from a hívó objektum oszlopa
	 * @param to a fogadó objektum oszlopa, itt található a nyíl hegye..
	 */
	private void renderArrow(Graphics g, String label, int row, int from, int to) {
		if (!label.equals("")) {
			if (from==to)
				renderHitchedArrow(g, label, from, row);
			else
				renderNormalArrow(g, label, row, from, to);
		}
		
	}

	/**
	 * A kirajzolt diagram központi részének egy darabkáját jeleníti meg.
	 *
	 * @param g  a szekvenciadiagram képének rajztáblája
	 * @param col kirajzolás oszlopa
	 * @param row kirajzolás sora
	 * @param type kirajzolandó elem típusa (<B>TODO:</B>még megváltoztatandó, célszeru legalább 5fajta érték tárolása, objnemlétezik,belépés,hívásilánctagja,kilépés,inaktív)
	 */
	private void renderTile(Graphics g, int col, int row, int type) { // type :  -1 = dont draw anything,   0 = inactive,   1 = active
		if (type>=0 && col>=0 && col<cols && row>=0 && row<rows)
		{
			int width=(int)(tileX*(type==0?0.05:0.3));
			g.setColor(Color.blue);
			g.fillRect((int)(timeX+tileX*col + 0.5*(tileX-width)), headerY+tileY*row, width, tileY);
		}
	}

//	private static int TILE_UNINITIALIZED = 0;
//	private static int TILE_INACTIVE= 1;
//	private static int TILE_BEGIN = 2;
//	private static int TILE_ACTIVE = 3;
//	private static int TILE_END = 4;
//	private static int TILE_ONELEN= 5;

	private static int TILE_UNINITIALIZED = -1;
	private static int TILE_INACTIVE= 0;
	private static int TILE_BEGIN = 1;
	private static int TILE_ACTIVE = 1;
	private static int TILE_END = 1;
	private static int TILE_ONELEN= 1;

	/**
	 *
	 * @param state a tömbe egyel csúsztatva van, mert különben negatív indexek is belekerültek volna, amit a Java nem enged...
	 */

	private void renderTileColumn(Graphics g, int col, int[] state) {
		int type  = TILE_UNINITIALIZED;
		for(int row=0; row<rows; row++) {
			switch (state[row+1]) {
				case -1 :
					type = TILE_UNINITIALIZED;
					break;
				case 0 :
					type = TILE_INACTIVE;
					break;
				default :
					switch ((state[row]>0?2:0)+(state[row+2]>0?1:0)) {
						case 3 :
							type = TILE_ACTIVE;
							break;
						case 2 :
							type = TILE_END;
							break;
						case 1 :
							type = TILE_BEGIN;
							break;
						case 0 :
							type = TILE_ONELEN;
							break;
					}
					break;
			}
			renderTile(g, col, row, type);
		}
	}

	/**
	 * A baloldalon található idosáv megjelenítése.
	 * 
	 * @param g a szekvenciadiagram képének rajztáblája
	 * @param startTime a legelso sor idejét adja meg, az ezt követo sorokban az ido értéke 1el no...
	 */
	private void drawTime(Graphics g, int startTime) {
		g.setColor(Color.black);
		for(int i=0; i<rows; i++)
			if (startTime+i>0)
				g.drawString("t: " + (startTime+i), 2,headerY + tileY*(i + 1) - 4);
		g.drawLine(timeX-1, headerY, timeX-1, Y);
	}

	/**
	 * Fejléc kirajzolása.
	 *
	 * @param g a szekvenciadiagram képének rajztáblája
	 */
	private void drawHeader(Graphics g) {

		Graphics2D g2 = (Graphics2D)g;
		Font old = g.getFont();
		Font ferde = new Font(null, Font.PLAIN, 12).deriveFont(AffineTransform.getRotateInstance(-0.15*Math.PI));
		g.setFont(ferde);
		g.setColor(Color.black);
		for(int col=0;col<cols && col<SkeletonObject.objList.size();col++)
			g.drawString("" + (SkeletonObject)(SkeletonObject.objList.get(col)), timeX + (int)(tileX*(col+0.5)), headerY-4);
		g.drawLine(0, headerY, X, headerY);
		g.setFont(old);
	}

	public void paint(Graphics g) {
		update(g);
	}

	/**
	 * A vászon megjelenítése. Ez a metódus felelos a szekvenciadiagram képének korrekt megjelenítéséért. A kép 3 fo komponense osztható fel.<br>
	 * A fejléc, ennek kirajzolásáért a {@link #drawHeader} eljárás felelos, ez az ablakrész tartalmazza a listázott objektumok neveit, az ablakon belül
	 * a (0, 0) - (X, headerY) területet foglalja el<br>
	 * Az idosáv, ez az elem a diagram soraihzo tartalmazza az idopontokat, az ablakban a (0, headerY) - (timeX, Y) régiót fedi le.<br>
	 * A diagram, ez a szkevenciadiagram képe, eloállításáért két fo függvény felelos, a {@link #renderArrow}, illetve a {@link #renderTile}. A vászon
	 * (timeX, headerY) - (X, Y) részét foglalja el.<br>
	 * <I>A fenti leírás egy kicst hamis, az X és Y koordináták itt valójában timeX + cols*tileX, illetve headerY + rows*tileY értékeket jelölik. Ez annyit jelen
	 * hogy az itt használt X, Y értékek, és a tényleges X, Y változók értékbeni különbsége, még egy L alakú, szabadonmaradt területet engedélyez. Amennyiben
	 * a diagram kirajzolásánál erre igény van, itt könnyen elhelyezheto egyén plusz adat, vagy a táblázatot lezáró grafikai elemek...</I>
	 *
	 * @param g a szekvenciadiagram képének rajztáblája
	 */
	public void update(Graphics g) {

		long start = System.currentTimeMillis();
		BufferedImage img = (BufferedImage)createImage(X ,Y);
		Graphics g2 = img.createGraphics();
		for(int x=0;x<600;x+=128)
			for(int y=0;y<400;y+=128)
				g2.drawImage(test, x, y, this);
//		g2.setColor(Color.white);
//		g2.clearRect (0, 0, X, Y);

		((Graphics2D)(g2)).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g2.setClip(timeX, headerY+1, cols*tileX, rows*tileY);
//		for(int time=0;time<rows;time++)
		for(int item=0;item<cols;item++)
			renderTileColumn(g2, item, seqDiag.getStateArray(item,seqTime-rows-1,rows+2));
		g2.setFont(new Font(null, Font.PLAIN, 10));
		for(int time=0;time<rows;time++)
			if (time+seqTime-rows>0) {
				seqDiagLabelListItem temp = seqDiag.getLabelObjAt(time+seqTime-rows);
				renderArrow(g2, temp.label, time, temp.from, temp.to);
			}
		g2.setClip(0, 0, X, headerY+1);
		drawHeader(g2);
		g2.setClip(0, headerY+1, timeX, Y - headerY+1);
		drawTime(g2, seqTime-rows);
		g2.setClip(0, 0, X, Y);
		g2.setColor(Color.black);
		g2.drawLine(0, Y-1, X, Y-1);
		g.drawImage(img,0,0,this);
		start = System.currentTimeMillis()-start;
		g.drawString("" + start, X- (int)g.getFontMetrics().getStringBounds("" + start,g).getWidth(), Y-3);
	}

	/**
	 * A diagram görgetését megvalósító függvény<br>
	 * <B>TODO:</B>valamiért nem jut el ide az event... ???
	 */
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode()==KeyEvent.VK_UP && seqTime>0) {	// diagram felfelé léptetése
			seqTime--;
			repaint();
		}
		if (e.getKeyCode()==KeyEvent.VK_DOWN && seqTime<maxSeqTime) {	// diagram léptetése lefelé
			seqTime++;
			repaint();
		}
	}
	public void keyTyped(KeyEvent e) {
	}
	public void keyReleased(KeyEvent e) {
	}

}


/**
 * Ez az objektum tárolja a szekvenciadiagram adatait, hozzá kapcsolódik a {@link seqCanvas}, ami a kirajzolásért felelos objektum.
 * Miután inicializáltuk, az in illetve leave függvények meghívásával szolgáltathatjuk a diagram rajzolásához szükséges adatokat.
 */
class seqDiagList extends Object{
	/**
	 * 
	 */
	private int seqTime;
	/**
	 * Az egyes objektumokhoz tarozó hívási listát tárolja.
	 */
	LinkedList seqTimeLine;
	/**
	 * Minden idoegységben pontosan egy metódushívás történik, ezekrol a hívásokról tárol adatot.
	 */
	LinkedList seqLabelList;
	/**
	 * {@link #seqLabelList} eloállításához szükséges segédváltozó
	 */
	Stack seqLabelStack;
//	Frame f ;
	seqCanvas c; //just

	public seqDiagList() {
		seqTimeLine = new LinkedList();
		seqLabelList = new LinkedList();
		seqLabelStack = new Stack();
		seqTime=1;
		c = new seqCanvas(this);
	}

	public void add(int objNumber) {
		seqTimeLine.add((Object)(new seqDiagListItem(seqTime,objNumber,0,true)));
	}

	private void add(int seqtime, int objNumber,int value, boolean init) {
		seqTimeLine.add((Object)(new seqDiagListItem(seqtime,objNumber,value,init)));
	}

	public int getSeqTime() {
		return seqTime;
	}

	public int getState(int objNumber) {
		return getStateAt(objNumber,seqTime);
	}
		
	public String getLabelAt(int time) {
		int index = seqLabelList.size()-1;

		while (index>=0)
		{
			if  (((seqDiagLabelListItem)(seqLabelList.get(index))).time==time) break;
			index--;
		}
		if (index>=0) return ((seqDiagLabelListItem)(seqLabelList.get(index))).label;
		else return "";
	}

	public seqDiagLabelListItem getLabelObjAt(int time) {
		int index = seqLabelList.size()-1;

		while (index>=0)
		{
			if  (((seqDiagLabelListItem)(seqLabelList.get(index))).time==time) break;
			index--;
		}
		if (index>=0) return ((seqDiagLabelListItem)(seqLabelList.get(index)));
		else return new seqDiagLabelListItem(0,"",0,0);
	}

	public int[] getStateArray(int objNumber, int timeStart, int elements) {
		int[] temp = new int[elements];
		for(int time = 0; time<elements; time++)
			temp[time] = getStateAt(objNumber, time+timeStart);
		return temp;
	}

	public int getStateAt(int objNumber,int time) {
		int index = seqTimeLine.size()-1;

		while (index>=0)
		{
			if (((seqDiagListItem)(seqTimeLine.get(index))).isNotAfter(time)) break;
			index--;
		}
		while (index>=0)
		{
			if (((seqDiagListItem)(seqTimeLine.get(index))).isObj(objNumber)) break;
			index--;
		}
		if (index>=0) return ((seqDiagListItem)(seqTimeLine.get(index))).getValue();
		else return -1; // given object was not member this time
	}
		
	public void In(int objNumber,String label) {
		int value = getState(objNumber);
//		if (value!=-1)   // ha ez elofordulna, hibás a kód
			add(seqTime,objNumber,value+1,false);
		if (seqLabelStack.empty())
			seqLabelList.add((Object)(new seqDiagLabelListItem(seqTime, label,-1,objNumber)));
		else
			seqLabelList.add((Object)(new seqDiagLabelListItem(seqTime, label,((Integer)(seqLabelStack.peek())).intValue(),objNumber)));
		seqLabelStack.push((Object)(new Integer(objNumber)));
		seqTime++;
		c.setTime(seqTime);
	}
		
	public void Leave(int objNumber,String label) {
		int value = getState(objNumber);
//		if (value!=-1) // elvileg már a 0 érték sem lehetséges...
			add(seqTime,objNumber,value-1,false);
		seqLabelStack.pop();
		if (seqLabelStack.empty())
			seqLabelList.add((Object)(new seqDiagLabelListItem(seqTime, label,objNumber,-1)));
		else {
			seqLabelList.add((Object)(new seqDiagLabelListItem(seqTime, label,objNumber,((Integer)(seqLabelStack.peek())).intValue())));
			if  (((Integer)(seqLabelStack.peek())).intValue()!=objNumber) {
				seqTime++;
				c.setTime(seqTime);
			}
		}
	}
}

/**
 * Minden általunk definiált osztályt a SkeletonObjectbõl származtattunk, így
 * ez az osztály tulajdonképpen egy debuggoló/szervíz keretet jelent a program
 * kürül.
 */ 
class SkeletonObject extends Object {
	/**
	 * [VaVa]
	 */
	public static seqDiagList seqDiag = new seqDiagList();

	/**
	 * Statikus számláló az objektum sorszám generálásához. Minden új objektum létrejöttekor
	 * növekszik eggyel.
	 */
	private static int objNumberCounter = 0;

	/**
	 * A behúzás mértéke karakterben.
	 */
	protected static int margineWidth = 0;

	/**
	 * [VaVa]
	 */
	static LinkedList objList = new LinkedList();
	/**
	 * Az objektum sorszáma, létrehozáskor generálódik. Egyedi azonosító.
	 */
	int objNumber;

	/**
	 * A konstruktor minden saját típusú osztály példányosításánál lefut, és így a
	 * példányosodások alkalmával folyamatosan növekvo objNumberCounter aktuális
	 * értékét az objNumber-hez rendelve, a létrejövo objektumot egy egyedi azonosítóval
	 * látja el. Továbbá a péládnyosításról hírt is ad a kimeneten.
	 */
	public SkeletonObject() {
		objNumber = objNumberCounter++;
		System.out.println (margine() + ">>> " + this + " created.");
		objList.add(this);
		seqDiag.add(objNumber);
	}
	
	/**
	 * Hírt ad az objektum megsemmisülésérõl.
	 */
	public void finalize() throws Throwable {
		System.out.println (margine() + "<<< " + this + " has been overwritten.");
	}
	
	/**
	 * String-kontextusban az osztály nevét helyettesíti be.
	 * A kényelmes kezelhetõség érdekében, amikor bármely osztály olyan kontextusban
	 * szerepel, ahol az értékére, mint string volna szükség, ez a függvény fog lefutni. Hatására
	 * osztálynév[objektum sorszám] jelenik meg a kimeneten.<br> Pl.:
	 *    Maze[2]
	 *
	 * @return		az osztály neve, illetve hogy a SkeletonObjectbol származó objektumok közül hányadikként példányosodott
	 */
	public String toString() {
		return (getClass().getName() + "[" + objNumber + "]").substring(getClass().getName().indexOf('.') + 1);
	}
	
	/**
	 * Margó generálásához használt függvény. A {@link SkeletonObject#margineIn()} illetve a {@link SkeletonObject#margineOut()}
	 * metódusok állítanak a behúzáson, jelezvén a függvényhívások mélységét, így növelve az
	 * áttekinthetoséget.
	 * Ez a metódus nem tesz mást, mint az elozo két függvény által beállított szélességu behúzást,
	 * vagyis {@link SkeletonObject#margineWidth} számú space-t ad vissza.
	 *
	 * @return 		egy {@link SkeletonObject#margineWidth} számú space-t tartalmazó String.
	 */
	String margine() {
		String s = "";
		for (int i=0; i < margineWidth; i++)
			s = s + ' ';
		return s;	
	}
	
	/**
	 * Növeli a behúzás mértékét
	 */
	public void margineIn() {
		margineWidth += 2;
	}

	/**
	 * Csökkenti a behúzás mértékét
	 */
	public void margineOut() {
		margineWidth -= 2;
	}

	/**
	 * A szkeletonban gyakran szükséges a felhaszáló felé kérdést intézni, a
	 * metódus ezt a feladatot látja el, soremelésig olvassa a begépelt karaktereket
	 * majd ezekkel visszatér a hívó félhez.
	 *
	 * @return A SYSTEM.IN-en érkezo sortöréssel zárt karaktersorozat.
	 */
	String Answer() {
		char ch = 0;
		
		String s = "";
		
		while (true) {
			try {
				ch = (char)System.in.read();
			} catch (IOException e) {
			}
			
			if (ch == '\r')
				continue;

			if (ch == '\n')
				return s;
			s = s + ch;
		}
	}

	/**
	 * A set paraméterben megadott halmazban lévõ szavakra próbálja illeszteni a toFind
	 * tartalmát, ez 3 körben folyik. Amennyiben a toFind=="" akkor a set legelsõ szavával
	 * tér vissza. Amennyiben ez nem sikerült, ponton egyezést keres a set szavaival, vagyis
	 * ha a set-ben megtalálható a toFind szó, akkor ezzel visszatér. Legutolsó körben
	 * azt feltételezzük hogy a toFind a set-ben található PONTOSAN EGY szónak a befejezetlen,
	 * félig begépelt példánya. Vagyis ha a toFinf pontosan egy szó elejére illeszthetõ a
	 * set-ben, akkor a set-ben található teljes szóval tér vissza. Minden más esetben az üres
	 * string, vagyis "" a visszatérési érték. A set-ben lévõ szavakat '|' karakterek választják
	 * el egymástól.<br>
	 * <B>TODO:</B>!!! tesztelni kell, hogy a hibás helyekre elhelyezett '|' jelek mit eredményeznek.
	 *
	 * @param set		a válaszok lehetséges listája, '|' jelekkel elválasztva
	 * @param toFind		a kiegészítendõ szöveg
	 *
	 * @return		üres String-gel tér vissza amennyiben a <code>toFind</code> semmi módon nem illesztheto a <code>set</code> egyetlen elemére sem
	*  <br>			a <code>set</code> egy eleme, amennyiben található illesztés
	 */ 
	String completeAnswer(String set, String toFind) {
		if (toFind.length()==0) {
			if (set.indexOf('|')==-1) return set;
			else return set.substring(0,set.indexOf('|'));
		}
		int temp=0,next;
		do {
			next=set.indexOf('|',temp); if (next==-1) next=set.length();
			if (set.regionMatches(true,temp,toFind,0,next-temp) && toFind.length()==next-temp) return toFind;
			temp=next+1;
		} while (next<set.length());
		temp=0; int hit=0; String match="";
		do {
			next=set.indexOf('|',temp); if (next==-1) next=set.length();
			if (set.regionMatches(true,temp,toFind,0,toFind.length())) {
				hit++; match=set.substring(temp,next);
			}
			temp=next+1;
		} while (next<set.length());
		if (hit==1) return match; else return "";
	}

// buta régi verziója a NotIn-nek, nemszeressük, az új verzió a designos comleteAnswer néven fut... :)
//	boolean NotIn(String set, String toFind) {
//		int temp=0,next;
//		do {
//			next=set.indexOf('|',temp); if (next==-1) next=set.length();
//			Println("Notintest" + (next-temp) );
//			if (set.regionMatches(true,temp,toFind,0,next-temp)) return false;
//			temp=next+1;
//		} while (next<set.length());
//		return true;
//	}
	
	/**
	 * Feltesz egy kérdést a felhasználónak, majd meghívja az Answer() metódust,
	 * melynek feladata a válasz beolvasása, és visszaadása. Ezt a választ azután
	 * egyszeruen továbbpasszoljuk a hívó félnek.
	 *
	 * @param q		a kérdés szövege
	 *
	 * @return		a system.in bementere érkezo válasz
	 */
	String Ask(String q) {
		System.out.print (margine() + "[?] " + q + " ");
		return Answer();
	}

	/**
	 * Feltesz egy kérdést a felhaszánlónak, valamint kiírja a lehetséges
	 * válaszok halmazát, amennyben a halmaz üres, bármilyen választ elfogad,
	 * ha már legalább egy elemet is definiálunk, addig vár válaszra,
	 * míg az nem olyan, ami egyértelmûen köthetõ a lehetséges válaszok egyikéhez.
	 * A kérdést csak egyszer teszi fel...
	 *
	 * @param q		a kérdés szövege
	 * @param ans		a lehetséges válaszok pipe-al elválasztva (pl. "yes|no")
	 *
	 * @return		a lehetséges válaszok halmazának egyik eleme
	 */
	String Ask(String q, String ans) {
		String answer;
		do {
			System.out.print (margine() + "[?] " + q + " [" + ans + "]");
			if (ans.equals("")) {
				answer = Answer();
			} else {
				answer = completeAnswer(ans, Answer());
			}
		} while (!ans.equals("") && answer.equals(""));

		return answer;
	}
	
	/**
	 * Minden függvény, elsoként ennek a metódusnak a hívását tartalmazza, a metódus
	 * egyfelõl kiírja a kimenetre, hogy mely osztály, milyen függvénye, miféle
	 * paraméterekkel hívódott meg, továbbá a behúzás mélységét növeli egy szinttel.
	 *
	 * @param func		a függvény neve
	 * @param m		egyéb üzenet - pl. a kapott paraméterek ismertetése
	 */
	void In(String func, String m) {
		Println("in " + GetName(func) + (m.equals("")?"":(" [" + m + "]")) + ".");
		margineIn();
		seqDiag.In(objNumber,func);
	}
	
	/**
	 * Függvénynol/Metódusból való kilépés elötti utolsó lépésként, az meghívja ezt a
	 * metódust, amely eloször visszavesz a behúzás mélységébõl egy szintet,
	 * majd kiírja a függvénybõl való kilépést. Egy egyszerû leave is elég volna ugyan,
	 * de az áttekinthetõséget ez a fajta jelölésmód nagymértékben növeli.
	 *
	 * @param func		a függvény neve
	 * @param m		egyéb üzenet - pl. a kapott a visszatérési érték
	 */
	void Leave(String func, String m) {
		margineOut();
		Println("leave " + GetName(func) + (m.equals("")?"":(" [" + m + "]")) + ".");
		seqDiag.Leave(objNumber,"");
	}
	
	/**
	 * Kiírja a paraméterben kapott stringet, margóval
	 *
	 * @param		s a kiírni kívánt szöveg
	 */
	void Print(String s) {
		System.out.print(margine() + s);
	}

	/**
	 * Kiírja a paraméterben kapott stringet, margóval, illetve soremeléssel.
	 *
	 * @param s		a kiírni kívánt szöveg
	 */
	void Println(String s) {
		System.out.println(margine() + s);
	}
	
	/**
	 * Amennyiben a programban valami hiba történik, röviden leírást ad róla.
	 *
	 * @param s		a hiba egyéb adatait leíró szöveg
	 * @param e		a képzõdött kivétel
	 */
	void Error (String s, Exception e) {
		System.err.println ("\n" + margine() + "!!! Error in " + this + (s.equals("")?"":(", problem: " + s)) + ".");
		e.printStackTrace(System.err);
	}
	
	/**
	 * Az aktuális függvény nevébõl képez egy "osztálynév.függvénynév()" alakú sztringet.
	 *
	 * @param s		a visszaadandó függvény neve
	 *
	 * @return		a {@link SkeletonObject#toString} értéke összetapasztva az <code>s</code> paraméterrel
	 */
	public String GetName (String s) {
		return this + "." + s + "()";
	}
}