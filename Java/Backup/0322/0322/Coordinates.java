package PacMan;

public class Coordinates {
	public long x,y;

	/**
	 * Egy koordin�ta-p�r l�trehoz�sa
	 *
	 * @param   _x  x koordin�ta
	 * @param   _y  y koordin�ta	
	 */
	Coordinates(long _x, long _y) {
		x = _x;
		y = _y;
	}

	public String toString() {
		return "(" + x + "," + y + ")";
	}
} 