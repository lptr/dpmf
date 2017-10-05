@rem $Id: makeedit.cmd,v 1.2 2001/04/27 03:15:43 lptr Exp $
@rem $Date: 2001/04/27 03:15:43 $
@rem $Author: lptr $

@call p.cmd
javac Util\MazeEdit\*.java Util\Parser\ourParser.java
jar cfm MazeEdit.jar Util\MazeEdit\MazeEdit.manifest Util\MazeEdit\*.class Util\Parser\*.class
