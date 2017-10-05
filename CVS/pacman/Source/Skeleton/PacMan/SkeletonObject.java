package PacMan;

import java.lang.*;
import java.io.*;
import java.util.*;
// ezeket az oszt�lyokat a seqCanvas haszn�lja..
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;  // az AffineTransform haszn�lja

/**
 * Ez az oszt�ly, a {@link seqDiagList}-ben t�rolt lista egy eleme. A list�t a szekvenciadiagram elo�ll�t�s�hoz haszn�ljuk.
 */
class seqDiagListItem extends Object {
	/**
	 * a bejegyz�shez tartoz� idopont
	 */
	private int time; 
	/**
	 * az objektum sorsz�ma
	 */
	private int objNum;
	/**
	 * az objektumhoz rendelt �rt�k. Azt reprezent�lja, hogy a sz�banforg� objektum az adott idopillanatban �ppen h�nyszor tal�lhat� meg a h�v�si l�ncban.<br>
	 *<dl>
	 *<dt>	0	<dd>�rt�k annyit tesz, az adott onjektum nem szerepel a h�v�si l�ncban
	 *<dt>	-1	<dd>az jelenti, hogy az objektum m�g nincs inicializ�lva, �rtelemszeruen a list�ba ilyen bejegyz�s nem ker�l.
	 *<dt>	n>0	<dd>az objektum n-szer tal�lhat� meg a h�v�si l�ncban.
	 *</dl>
	 */
	private int value; 
	/**
	 * jelenleg ezt az �rt�ket nem haszn�lja a program. Val�sz�n� hogy ez az elk�vetkezendoekben is �gy marad, feltehetoen t�r�lve lesz...
	 */
	private boolean init;

	/**
	 * Elem l�trehoz�sa. Miut�n egy elemet l�trehoztunk, annak �rt�ke nem m�dos�that�, de erre nincs is sz�ks�g, mivel a list�t tulajdonk�ppen loggol�sra haszn�ljuk.
	 */
	public seqDiagListItem(int t,int o,int v,boolean i) {
		time=t;
		objNum=o;
		value=v;
		init=i;
	}

	/**
	 * Megvizsg�lja, a param�terk�nt kapott azonos�t�hoz tartozik e a bejegyz�s
	 */
	public boolean isObj(int objNumber) {
		return (objNumber==objNum);
	}

	/**
	 * Megvizsg�lja, hogy a bejegyz�s legfeljebb a param�terk�nt �tadott idopontban keletkezett-e.
	 */
	public boolean isNotAfter(int time_) {
		return (time<=time_);
	}

	/**
	 * Visszat�r a bejegyzett �rt�kkel.
	 */
	public int getValue() {
		return value;
	}
}

/**
 * Szekvenciadiagram f�ggv�nyh�v�si list�j�nak alapeleme. Azt t�rolja, hogy a {@link seqDiagLabelListItem#time} idopontban bek�vetkezett {@link seqDiagLabelListItem#label} cimk�ju h�v�snak, melyik objektum volt a forr�sa �s melyik a c�l.
 */
class seqDiagLabelListItem extends Object {
	/**
	 * bejegyz�shez tartoz� idopont
	 */
	public int time;
	/**
	 * a f�ggv�nyh�v�shoz tartoz� cimke. A megh�vott f�ggv�ny nev�t �s adatait t�rolja
	 */
	public String label;
	/**
	 * A h�v� objektum azonos�t�ja
	 */
	public int from;
	/**
	 * A megh�vott f�ggv�nyt tartalmaz� objektum azonos�t�ja
	 */
	public int to;

	/**
	 *  Az elemeket ezzel kell l�trehozni, hiszen mivel log jellegu a lista, az elemek m�dos�t�sa nem lehets�ges...
	 */
	public seqDiagLabelListItem(int t, String l, int f, int t2) {
		time = t;
		label = l;
		from = f;
		to = t2;
	}
}		

/**
 * Szekvenciagram megjelen�t�s��rt felelos oszt�ly. Kialak�t�s�n�l a magas fok� flexibil�t�s volt a c�l, megjelen�s�t rengeteg param�terrel lehet szab�lyozni.<br>
 * <B>TODO:</B>Jelenleg ezek a param�terek szigor�an oszt�lyon bel�liek, csak itt �ll�that�ak be, k�sobbiekben ezen tal�n v�ltoztatni k�ne
 */
class seqCanvas extends Canvas implements KeyListener {
	private Image test;

	/**
	 * VaV�n�l van egy gy�ny�ru rajz r�la, majd felgrafik�zom g�pre, az megmutassa mi mit jelent...
	 */ 
	private int X, Y, headerY, timeX, cols, rows, tileX, tileY;
	private int seqTime, maxSeqTime;
	/**
	 * <B>TODO:</B>optmaliz�lni kell a list�ban val� keres�sen, most ak�r 200msec idofelesleg is keletkezhet emiatt a kirajzol�sban...
	 */
	private seqDiagList seqDiag;

	/**
	 * A param�terk�nt kapott szekv. diagram megjelen�t�s�hez inicializ�l�s.
	 *
	 * @param list a kirajzoland� szekvenciadiagram objektuma, innen k�rdezi le a megjelen�t�shez sz�ks�ges adatokat.
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
	 * Diagram idej�nek be�ll�t�sa. A szekvenciadiagram adatainak friss�t�se ut�n c�lszeru megh�vni, ilyenkor g�rgeti az adatokat, �s �jrarajzolja a k�pet.
	 */
	public void setTime(int seqTime) {
		maxSeqTime = this.seqTime = seqTime;
		repaint();
	}

	/**
	 * Egyszeru ny�l rajzol�sa. A f�ggv�ny egy v�zszintes nyilat k�pes rajzolni, jelenleg a {@link seqCanvas#renderNormalArrow} met�dus haszn�lja.
	 *
	 * @param g a szekvenciadiagram k�p�nek rajzt�bl�ja
	 * @param y a ny�l sora
	 * @param x1 a ny�l kiindul�si pontja
	 * @param x2 a ny�l v�ge, itt tal�lhat� a hegye
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
	 * Sz�veg kirajzol�sa dobozba. A String param�terben kapott sz�veget rajzolja ki, fekete bet�kkel, egy s�rga h�tteru t�glalapba.
	 *
	 * @param g a szekvenciadiagram k�p�nek rajzt�bl�ja
	 * @param label a ki�r�s sz�vege
	 * @param x a doboz x koordin�t�ja
	 * @param y a doboz y koordin�t�ja
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
	 * Visszacsatolt ny�l rajzol�sa. Akkor haszn�lja a rendszer, amikor egy oszt�lyon bel�li f�ggv�nyh�v�st kell �br�zolni
	 *
	 * @param g a szekvenciadiagram k�p�nek rajzt�bl�ja
	 * @param label a f�ggv�nyh�v�shoz kapcsol�d� cimke (<B>TODO:</B>a ny�lra kell helyezni)
	 * @param col a kirajzol�s oszlopa
	 * @param row a kirajzol�s sora
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
	 * Norm�l ny�l rajzol�sa. Akkor h�v�dik meg, amikor a f�ggv�nyh�v�s k�t k�l�nb�zo objektum k�z�tt k�vetkezik be.
	 *
	 * @param g a szekvenciadiagram k�p�nek rajzt�bl�ja
	 * @param label a f�ggv�nyh�v�shoz kapcsol�d� cimke (<B>TODO:</B>a ny�lra kell helyezni)
	 * @param row a kirajzol�s sora
	 * @param from a h�v� objektum oszlopa
	 * @param to a fogad� objektum oszlopa, itt tal�lhat� a ny�l hegye..
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
	 * Ny�l rajzol�s��rt felelos met�dus. a ny�l t�pus�t�l f�ggoen h�vja a {@link seqCanvas#renderHitchedArrow} vagy a {@link seqCanvas#renderNormalArrow} met�dusok valamelyik�t.
	 *
	 * @param g a szekvenciadiagram k�p�nek rajzt�bl�ja
	 * @param label a f�ggv�nyh�v�shoz kapcsol�d� cimke (<B>TODO:</B>a ny�lra kell helyezni)
	 * @param row a kirajzol�s sora
	 * @param from a h�v� objektum oszlopa
	 * @param to a fogad� objektum oszlopa, itt tal�lhat� a ny�l hegye..
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
	 * A kirajzolt diagram k�zponti r�sz�nek egy darabk�j�t jelen�ti meg.
	 *
	 * @param g  a szekvenciadiagram k�p�nek rajzt�bl�ja
	 * @param col kirajzol�s oszlopa
	 * @param row kirajzol�s sora
	 * @param type kirajzoland� elem t�pusa (<B>TODO:</B>m�g megv�ltoztatand�, c�lszeru legal�bb 5fajta �rt�k t�rol�sa, objneml�tezik,bel�p�s,h�v�sil�nctagja,kil�p�s,inakt�v)
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
	 * @param state a t�mbe egyel cs�sztatva van, mert k�l�nben negat�v indexek is beleker�ltek volna, amit a Java nem enged...
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
	 * A baloldalon tal�lhat� idos�v megjelen�t�se.
	 * 
	 * @param g a szekvenciadiagram k�p�nek rajzt�bl�ja
	 * @param startTime a legelso sor idej�t adja meg, az ezt k�veto sorokban az ido �rt�ke 1el no...
	 */
	private void drawTime(Graphics g, int startTime) {
		g.setColor(Color.black);
		for(int i=0; i<rows; i++)
			if (startTime+i>0)
				g.drawString("t: " + (startTime+i), 2,headerY + tileY*(i + 1) - 4);
		g.drawLine(timeX-1, headerY, timeX-1, Y);
	}

	/**
	 * Fejl�c kirajzol�sa.
	 *
	 * @param g a szekvenciadiagram k�p�nek rajzt�bl�ja
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
	 * A v�szon megjelen�t�se. Ez a met�dus felelos a szekvenciadiagram k�p�nek korrekt megjelen�t�s��rt. A k�p 3 fo komponense oszthat� fel.<br>
	 * A fejl�c, ennek kirajzol�s��rt a {@link #drawHeader} elj�r�s felelos, ez az ablakr�sz tartalmazza a list�zott objektumok neveit, az ablakon bel�l
	 * a (0, 0) - (X, headerY) ter�letet foglalja el<br>
	 * Az idos�v, ez az elem a diagram soraihzo tartalmazza az idopontokat, az ablakban a (0, headerY) - (timeX, Y) r�gi�t fedi le.<br>
	 * A diagram, ez a szkevenciadiagram k�pe, elo�ll�t�s��rt k�t fo f�ggv�ny felelos, a {@link #renderArrow}, illetve a {@link #renderTile}. A v�szon
	 * (timeX, headerY) - (X, Y) r�sz�t foglalja el.<br>
	 * <I>A fenti le�r�s egy kicst hamis, az X �s Y koordin�t�k itt val�j�ban timeX + cols*tileX, illetve headerY + rows*tileY �rt�keket jel�lik. Ez annyit jelen
	 * hogy az itt haszn�lt X, Y �rt�kek, �s a t�nyleges X, Y v�ltoz�k �rt�kbeni k�l�nbs�ge, m�g egy L alak�, szabadonmaradt ter�letet enged�lyez. Amennyiben
	 * a diagram kirajzol�s�n�l erre ig�ny van, itt k�nnyen elhelyezheto egy�n plusz adat, vagy a t�bl�zatot lez�r� grafikai elemek...</I>
	 *
	 * @param g a szekvenciadiagram k�p�nek rajzt�bl�ja
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
	 * A diagram g�rget�s�t megval�s�t� f�ggv�ny<br>
	 * <B>TODO:</B>valami�rt nem jut el ide az event... ???
	 */
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode()==KeyEvent.VK_UP && seqTime>0) {	// diagram felfel� l�ptet�se
			seqTime--;
			repaint();
		}
		if (e.getKeyCode()==KeyEvent.VK_DOWN && seqTime<maxSeqTime) {	// diagram l�ptet�se lefel�
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
 * Ez az objektum t�rolja a szekvenciadiagram adatait, hozz� kapcsol�dik a {@link seqCanvas}, ami a kirajzol�s�rt felelos objektum.
 * Miut�n inicializ�ltuk, az in illetve leave f�ggv�nyek megh�v�s�val szolg�ltathatjuk a diagram rajzol�s�hoz sz�ks�ges adatokat.
 */
class seqDiagList extends Object{
	/**
	 * 
	 */
	private int seqTime;
	/**
	 * Az egyes objektumokhoz taroz� h�v�si list�t t�rolja.
	 */
	LinkedList seqTimeLine;
	/**
	 * Minden idoegys�gben pontosan egy met�dush�v�s t�rt�nik, ezekrol a h�v�sokr�l t�rol adatot.
	 */
	LinkedList seqLabelList;
	/**
	 * {@link #seqLabelList} elo�ll�t�s�hoz sz�ks�ges seg�dv�ltoz�
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
//		if (value!=-1)   // ha ez elofordulna, hib�s a k�d
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
//		if (value!=-1) // elvileg m�r a 0 �rt�k sem lehets�ges...
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
 * Minden �ltalunk defini�lt oszt�lyt a SkeletonObjectb�l sz�rmaztattunk, �gy
 * ez az oszt�ly tulajdonk�ppen egy debuggol�/szerv�z keretet jelent a program
 * k�r�l.
 */ 
class SkeletonObject extends Object {
	/**
	 * [VaVa]
	 */
	public static seqDiagList seqDiag = new seqDiagList();

	/**
	 * Statikus sz�ml�l� az objektum sorsz�m gener�l�s�hoz. Minden �j objektum l�trej�ttekor
	 * n�vekszik eggyel.
	 */
	private static int objNumberCounter = 0;

	/**
	 * A beh�z�s m�rt�ke karakterben.
	 */
	protected static int margineWidth = 0;

	/**
	 * [VaVa]
	 */
	static LinkedList objList = new LinkedList();
	/**
	 * Az objektum sorsz�ma, l�trehoz�skor gener�l�dik. Egyedi azonos�t�.
	 */
	int objNumber;

	/**
	 * A konstruktor minden saj�t t�pus� oszt�ly p�ld�nyos�t�s�n�l lefut, �s �gy a
	 * p�ld�nyosod�sok alkalm�val folyamatosan n�vekvo objNumberCounter aktu�lis
	 * �rt�k�t az objNumber-hez rendelve, a l�trej�vo objektumot egy egyedi azonos�t�val
	 * l�tja el. Tov�bb� a p�l�dnyos�t�sr�l h�rt is ad a kimeneten.
	 */
	public SkeletonObject() {
		objNumber = objNumberCounter++;
		System.out.println (margine() + ">>> " + this + " created.");
		objList.add(this);
		seqDiag.add(objNumber);
	}
	
	/**
	 * H�rt ad az objektum megsemmis�l�s�r�l.
	 */
	public void finalize() throws Throwable {
		System.out.println (margine() + "<<< " + this + " has been overwritten.");
	}
	
	/**
	 * String-kontextusban az oszt�ly nev�t helyettes�ti be.
	 * A k�nyelmes kezelhet�s�g �rdek�ben, amikor b�rmely oszt�ly olyan kontextusban
	 * szerepel, ahol az �rt�k�re, mint string volna sz�ks�g, ez a f�ggv�ny fog lefutni. Hat�s�ra
	 * oszt�lyn�v[objektum sorsz�m] jelenik meg a kimeneten.<br> Pl.:
	 *    Maze[2]
	 *
	 * @return		az oszt�ly neve, illetve hogy a SkeletonObjectbol sz�rmaz� objektumok k�z�l h�nyadikk�nt p�ld�nyosodott
	 */
	public String toString() {
		return (getClass().getName() + "[" + objNumber + "]").substring(getClass().getName().indexOf('.') + 1);
	}
	
	/**
	 * Marg� gener�l�s�hoz haszn�lt f�ggv�ny. A {@link SkeletonObject#margineIn()} illetve a {@link SkeletonObject#margineOut()}
	 * met�dusok �ll�tanak a beh�z�son, jelezv�n a f�ggv�nyh�v�sok m�lys�g�t, �gy n�velve az
	 * �ttekinthetos�get.
	 * Ez a met�dus nem tesz m�st, mint az elozo k�t f�ggv�ny �ltal be�ll�tott sz�less�gu beh�z�st,
	 * vagyis {@link SkeletonObject#margineWidth} sz�m� space-t ad vissza.
	 *
	 * @return 		egy {@link SkeletonObject#margineWidth} sz�m� space-t tartalmaz� String.
	 */
	String margine() {
		String s = "";
		for (int i=0; i < margineWidth; i++)
			s = s + ' ';
		return s;	
	}
	
	/**
	 * N�veli a beh�z�s m�rt�k�t
	 */
	public void margineIn() {
		margineWidth += 2;
	}

	/**
	 * Cs�kkenti a beh�z�s m�rt�k�t
	 */
	public void margineOut() {
		margineWidth -= 2;
	}

	/**
	 * A szkeletonban gyakran sz�ks�ges a felhasz�l� fel� k�rd�st int�zni, a
	 * met�dus ezt a feladatot l�tja el, soremel�sig olvassa a beg�pelt karaktereket
	 * majd ezekkel visszat�r a h�v� f�lhez.
	 *
	 * @return A SYSTEM.IN-en �rkezo sort�r�ssel z�rt karaktersorozat.
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
	 * A set param�terben megadott halmazban l�v� szavakra pr�b�lja illeszteni a toFind
	 * tartalm�t, ez 3 k�rben folyik. Amennyiben a toFind=="" akkor a set legels� szav�val
	 * t�r vissza. Amennyiben ez nem siker�lt, ponton egyez�st keres a set szavaival, vagyis
	 * ha a set-ben megtal�lhat� a toFind sz�, akkor ezzel visszat�r. Legutols� k�rben
	 * azt felt�telezz�k hogy a toFind a set-ben tal�lhat� PONTOSAN EGY sz�nak a befejezetlen,
	 * f�lig beg�pelt p�ld�nya. Vagyis ha a toFinf pontosan egy sz� elej�re illeszthet� a
	 * set-ben, akkor a set-ben tal�lhat� teljes sz�val t�r vissza. Minden m�s esetben az �res
	 * string, vagyis "" a visszat�r�si �rt�k. A set-ben l�v� szavakat '|' karakterek v�lasztj�k
	 * el egym�st�l.<br>
	 * <B>TODO:</B>!!! tesztelni kell, hogy a hib�s helyekre elhelyezett '|' jelek mit eredm�nyeznek.
	 *
	 * @param set		a v�laszok lehets�ges list�ja, '|' jelekkel elv�lasztva
	 * @param toFind		a kieg�sz�tend� sz�veg
	 *
	 * @return		�res String-gel t�r vissza amennyiben a <code>toFind</code> semmi m�don nem illesztheto a <code>set</code> egyetlen elem�re sem
	*  <br>			a <code>set</code> egy eleme, amennyiben tal�lhat� illeszt�s
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

// buta r�gi verzi�ja a NotIn-nek, nemszeress�k, az �j verzi� a designos comleteAnswer n�ven fut... :)
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
	 * Feltesz egy k�rd�st a felhaszn�l�nak, majd megh�vja az Answer() met�dust,
	 * melynek feladata a v�lasz beolvas�sa, �s visszaad�sa. Ezt a v�laszt azut�n
	 * egyszeruen tov�bbpasszoljuk a h�v� f�lnek.
	 *
	 * @param q		a k�rd�s sz�vege
	 *
	 * @return		a system.in bementere �rkezo v�lasz
	 */
	String Ask(String q) {
		System.out.print (margine() + "[?] " + q + " ");
		return Answer();
	}

	/**
	 * Feltesz egy k�rd�st a felhasz�nl�nak, valamint ki�rja a lehets�ges
	 * v�laszok halmaz�t, amennyben a halmaz �res, b�rmilyen v�laszt elfogad,
	 * ha m�r legal�bb egy elemet is defini�lunk, addig v�r v�laszra,
	 * m�g az nem olyan, ami egy�rtelm�en k�thet� a lehets�ges v�laszok egyik�hez.
	 * A k�rd�st csak egyszer teszi fel...
	 *
	 * @param q		a k�rd�s sz�vege
	 * @param ans		a lehets�ges v�laszok pipe-al elv�lasztva (pl. "yes|no")
	 *
	 * @return		a lehets�ges v�laszok halmaz�nak egyik eleme
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
	 * Minden f�ggv�ny, elsok�nt ennek a met�dusnak a h�v�s�t tartalmazza, a met�dus
	 * egyfel�l ki�rja a kimenetre, hogy mely oszt�ly, milyen f�ggv�nye, mif�le
	 * param�terekkel h�v�dott meg, tov�bb� a beh�z�s m�lys�g�t n�veli egy szinttel.
	 *
	 * @param func		a f�ggv�ny neve
	 * @param m		egy�b �zenet - pl. a kapott param�terek ismertet�se
	 */
	void In(String func, String m) {
		Println("in " + GetName(func) + (m.equals("")?"":(" [" + m + "]")) + ".");
		margineIn();
		seqDiag.In(objNumber,func);
	}
	
	/**
	 * F�ggv�nynol/Met�dusb�l val� kil�p�s el�tti utols� l�p�sk�nt, az megh�vja ezt a
	 * met�dust, amely elosz�r visszavesz a beh�z�s m�lys�g�b�l egy szintet,
	 * majd ki�rja a f�ggv�nyb�l val� kil�p�st. Egy egyszer� leave is el�g volna ugyan,
	 * de az �ttekinthet�s�get ez a fajta jel�l�sm�d nagym�rt�kben n�veli.
	 *
	 * @param func		a f�ggv�ny neve
	 * @param m		egy�b �zenet - pl. a kapott a visszat�r�si �rt�k
	 */
	void Leave(String func, String m) {
		margineOut();
		Println("leave " + GetName(func) + (m.equals("")?"":(" [" + m + "]")) + ".");
		seqDiag.Leave(objNumber,"");
	}
	
	/**
	 * Ki�rja a param�terben kapott stringet, marg�val
	 *
	 * @param		s a ki�rni k�v�nt sz�veg
	 */
	void Print(String s) {
		System.out.print(margine() + s);
	}

	/**
	 * Ki�rja a param�terben kapott stringet, marg�val, illetve soremel�ssel.
	 *
	 * @param s		a ki�rni k�v�nt sz�veg
	 */
	void Println(String s) {
		System.out.println(margine() + s);
	}
	
	/**
	 * Amennyiben a programban valami hiba t�rt�nik, r�viden le�r�st ad r�la.
	 *
	 * @param s		a hiba egy�b adatait le�r� sz�veg
	 * @param e		a k�pz�d�tt kiv�tel
	 */
	void Error (String s, Exception e) {
		System.err.println ("\n" + margine() + "!!! Error in " + this + (s.equals("")?"":(", problem: " + s)) + ".");
		e.printStackTrace(System.err);
	}
	
	/**
	 * Az aktu�lis f�ggv�ny nev�b�l k�pez egy "oszt�lyn�v.f�ggv�nyn�v()" alak� sztringet.
	 *
	 * @param s		a visszaadand� f�ggv�ny neve
	 *
	 * @return		a {@link SkeletonObject#toString} �rt�ke �sszetapasztva az <code>s</code> param�terrel
	 */
	public String GetName (String s) {
		return this + "." + s + "()";
	}
}