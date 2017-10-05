@rem $Id: makeout.cmd,v 1.4 2001/04/27 03:15:43 lptr Exp $
@rem $Date: 2001/04/27 03:15:43 $
@rem $Author: lptr $

@call p.cmd
javac Outputter\*.java Util\Parser\ourParser.java
jar cfm Outputter.jar Outputter\Outputter.manifest Outputter\*.class Util\Parser\*.class

