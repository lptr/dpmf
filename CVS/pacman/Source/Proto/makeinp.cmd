@rem $Id: makeinp.cmd,v 1.4 2001/04/27 03:15:43 lptr Exp $
@rem $Date: 2001/04/27 03:15:43 $
@rem $Author: lptr $

@call p.cmd
javac Inputter\*.java Util\Parser\ourParser.java
jar cfm Inputter.jar Inputter\Inputter.manifest Inputter\*.class Util\Parser\*.class
