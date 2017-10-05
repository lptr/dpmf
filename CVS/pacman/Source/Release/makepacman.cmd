@rem $Id: makepacman.cmd,v 1.3 2001/05/31 02:21:45 lptr Exp $
@rem $Date: 2001/05/31 02:21:45 $
@rem $Author: lptr $

del /q PacMan\*.class
del /q Util\Parser\*.class
del /q ImageCache\*.class
javac -d . PacMan\*.java ..\Proto\Util\Parser\ourParser.java ImageCache\*.java
jar cfm PacMan.jar PacMan.manifest PacMan\*.class Util\Parser\*.class ImageCache\*.class data\*.ttf data\aboutbox.html data\pacmanicon.gif

rmdir /s /q Doc
mkdir Doc

rem javadoc -link http://java.sun.com/products/jdk/1.3/docs/api -use -private -locale hu_HU -encoding iso-8859-2 -charset iso-8859-2 -docencoding iso-8859-2 -d Doc -version -author -windowtitle "DPMF PacMan - v1.0" PacMan\*.java ImageCache\*.java ..\Proto\Util\Parser\*.java
