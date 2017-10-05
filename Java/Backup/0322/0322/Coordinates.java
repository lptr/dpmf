package PacMan;

public class Coordinates {
	public long x,y;

	/**
	 * Egy koordináta-pár létrehozása
	 *
	 * @param   _x  x koordináta
	 * @param   _y  y koordináta	
	 */
	Coordinates(long _x, long _y) {
		x = _x;
		y = _y;
	}

	public String toString() {
		return "(" + x + "," + y + ")";
	}
} 