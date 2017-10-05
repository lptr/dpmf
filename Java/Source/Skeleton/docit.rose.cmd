@call p.cmd
rmdir /s /q Doc.Rose
mkdir Doc.Rose
javadoc -doclet JP.co.esm.caddies.doclets.RedDoclet -docletpath c:\project\dpmf\java -private PacMan\*.java
move *.red Doc.Rose
pause
