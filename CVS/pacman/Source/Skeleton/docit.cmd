@call p.cmd
rmdir /s /q Doc
mkdir Doc
javadoc -link http://java.sun.com/products/jdk/1.3/docs/api -private -sourcepath PacMan -locale hu_HU -encoding iso-8859-2 -charset iso-8859-2 -docencoding iso-8859-2 -d Doc -version -author -windowtitle "DPMF PacMan" PacMan\*.java
pause