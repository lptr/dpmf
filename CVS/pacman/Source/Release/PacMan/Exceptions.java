// $Id: Exceptions.java,v 1.1 2001/05/14 15:11:34 lptr Exp $
// $Date: 2001/05/14 15:11:34 $
// $Author: lptr $

package PacMan;

class NextLevelException extends java.lang.Exception {
}

class LifeLostException extends java.lang.Exception {
}

class EndGameException extends java.lang.Exception {
}

class BadDataException extends java.lang.Exception {
	BadDataException (String s, int lineno) {
		super (s + " at line " + lineno);
	}

	BadDataException (String s) {
		super (s);
	}
}