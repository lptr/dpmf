@rem $Id: docit.cmd,v 1.5 2001/04/27 03:15:43 lptr Exp $
@rem $Date: 2001/04/27 03:15:43 $
@rem $Author: lptr $

@call p.cmd
echo  documenting...
javadoc -link http://java.sun.com/products/jdk/1.3/docs/api -use -private -locale hu_HU -encoding iso-8859-2 -charset iso-8859-2 -docencoding iso-8859-2 -d Doc -version -author -windowtitle "DPMF PacMan Prototype" PacMan\*.java Inputter\*.java Outputter\*.java Util\Parser\*.java Util\MazeEdit\*.java
