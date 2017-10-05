class MazeData extends Object {

//	ItemList items	// a p�ly�n elhelyezett stuffokat ez a lista fogja t�rolni...

	MazeEdit main;
	
	private int sizeX;
	private int sizeY;
	private int time;
	private int probab;
	private FieldItem[] mazeData;

	MazeData(MazeEdit main) {
		this.main = main;
		sizeX = 0;
		sizeY = 0;
		time = 0;
		probab = 0;
		resize(3,3);
	}

	private void resize(int x, int y) {
		FieldItem[] data;
		data = new FieldItem[x*y];
		for (int i=0; i<x; i++)
			for (int j=0; j<y; j++)
				if (i>=sizeX || j>=sizeY)
					data[j*x+i]=new FieldItem();
				else
					data[j*x+i]=getFieldItem(i,j);
		mazeData = data;
		sizeX = x;
		sizeY = y;
	}
	
	public int getSizeX() {
		return sizeX;
	}
	public void setSizeX(int x) {
		resize(x,sizeY);
		main.mazedraw.repaint();
	}

	public int getSizeY() {
		return sizeY;
	}
	public void setSizeY(int y) {
		resize(sizeX,y);
		main.mazedraw.repaint();
	}

	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}

	public int getProbab() {
		return probab;
	}
	public void setProbab(int probab) {
		this.probab = probab;
	}
	
	public FieldItem getFieldItem(int pos) {
		if (pos>=0 && pos<sizeX*sizeY)
			return mazeData[pos];
		else
			return null;
	}

	public FieldItem getFieldItem(int x, int y) {
		if (x>=0 && x<sizeX && y>=0 && y<sizeY)
			return mazeData[y*sizeX+x];
		else
			return null;
	}

}

//!!!!!
//!!!!!
//!!!!! WARNING!! These values are DIRECTLY used in FieldItem because of speed-up reasons!!! Modification of constants may cause ArrayIndexOutOfBounds Exception, or errorneous output
//!!!!!
//!!!!!
class Coordinates extends Object {
	public final static int DIR_UP = 0;
	public final static int DIR_RIGHT = 1;
	public final static int DIR_DOWN = 2;
	public final static int DIR_LEFT = 3;
};

/**
 * A labirintus egy mez�j�nek adatait t�rolja.
 * a konstansok haszn�lata cs�nya. c�lszer� lenne majd bugfixelni..
 *
 * @author VaVa
 */
class FieldItem {
	private boolean[] wall;
	private boolean blocked;

// mez� falainak inicializ�l�sa, alap�rtelmez�s szerint sehol sincs fal
	FieldItem() {
		wall = new boolean[4];
		for (int i=0; i<=3; i++)
			wall[i]=false;
	}

// mez� falainak inicializ�l�sa Stringb�l. amennyiben a string r�videbb a marad�k helyen nem lesz fal. Ahol 0 van a stringben
// ott neml lesz fal, ahol b�rmi m�s, ott lesz.
	FieldItem(String s) {
		this();
//		wall = new boolean[4];
//		for (int i=0; i<=3 && i<s.length(); i++)
//			wall[i]=s.charAt(i)=='0'?false:true;
	}

// blokkolts�gjelz� t�rl�se
	public void clearBlocked() {
		blocked = false;
	}

// lek�rdezz�k �s be�ll�tjuk egyid�ben a blokkolts�gjelz�t.
	public boolean getSetBlocked() {
		boolean temp = blocked;
		blocked = true;
		return temp;
	}

// megadja adott ir�nyban van-e fal.
	boolean isWall(int direction) {
		return wall[direction]; // lehal j�l ha nem norm�lis param�tert kap... ehhe
	}

// be�ll�tja a falat
	void setWall(int direction) {
		wall[direction] = true;
//		checkBlocked();
	}

// t�rli a falat adott ir�nyban.
	void clearWall(int direction) {
		wall[direction] = false;
//		checkBlocked();
	}

// �tkapcsolja a falat.
	void switchWall(int direction) {
		wall[direction] = !wall[direction];
//		checkBlocked();
	}

	/**
	 * Lek�rdezi/Be�ll�tja hogy blokkolva van-e a fal.
	 */
	public void checkBlocked() {
		blocked = 
			wall[Coordinates.DIR_UP] && 
			wall[Coordinates.DIR_DOWN] && 
			wall[Coordinates.DIR_LEFT] && 
			wall[Coordinates.DIR_RIGHT];
	}
	public boolean getBlocked() {
		return blocked;
	}
	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

	/**
	 * A falat le�r� string
	 *
	 * @return	A falat le�r� String.
	 */
	public String toString() {
		return	(wall[Coordinates.DIR_UP]?"1":"0") + 
				(wall[Coordinates.DIR_RIGHT]?"1":"0") + 
				(wall[Coordinates.DIR_DOWN]?"1":"0") + 
				(wall[Coordinates.DIR_LEFT]?"1":"0");
	}

	public String Show(int line) {
		switch (line) {
		case 0 :
			return
				(isWall(Coordinates.DIR_LEFT) || isWall(Coordinates.DIR_UP)?"+":" ") +
			    (isWall(Coordinates.DIR_UP)?"-":" ") +
				(isWall(Coordinates.DIR_RIGHT) || isWall(Coordinates.DIR_UP)?"+":" ");
		case 1 :
			return
				(isWall(Coordinates.DIR_LEFT)?"|":" ") +
			    " " +
				(isWall(Coordinates.DIR_RIGHT)?"|":" ");
		case 2 :
			return
				(isWall(Coordinates.DIR_LEFT) || isWall(Coordinates.DIR_DOWN)?"+":" ") +
			    (isWall(Coordinates.DIR_DOWN)?"-":" ") +
				(isWall(Coordinates.DIR_RIGHT) || isWall(Coordinates.DIR_DOWN)?"+":" ");
		}
		return "";
	}
}
