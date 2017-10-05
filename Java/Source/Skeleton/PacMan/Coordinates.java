package PacMan;

/**
 * Egy koordin�tap�r t�rol�s�ra alkalmas oszt�ly
 */
public class Coordinates {
	public long x,y;

	/**
	 * Egy koordin�ta p�r l�trehoz�sa
	 *
	 * @author	L�ci
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