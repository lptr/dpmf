@rem $Id: makepacman.cmd,v 1.3 2001/04/27 13:42:41 lptr Exp $
@rem $Date: 2001/04/27 13:42:41 $
@rem $Author: lptr $

@call p.cmd
javac PacMan\*.java Util\Parser\ourParser.java
jar cfm PacMan.jar PacMan\PacMan.manifest PacMan\*.class Util\Parser\*.class
