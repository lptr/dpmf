@rem $Id: pack.cmd,v 1.4 2001/04/27 03:15:43 lptr Exp $
@rem $Date: 2001/04/27 03:15:43 $
@rem $Author: lptr $

@call p.cmd
jar cfm PacMan.jar PacMan\PacMan.manifest PacMan\*.class Util\Parser\*.class
