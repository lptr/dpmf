@rem $Id: makepacman.cmd,v 1.4 2001/05/08 20:36:54 lptr Exp $
@rem $Date: 2001/05/08 20:36:54 $
@rem $Author: lptr $

@call p.cmd
javac PacMan\*.java Util\Parser\ourParser.java
jar cfm PacMan.jar PacMan\PacMan.manifest PacMan\*.class Util\Parser\*.class
