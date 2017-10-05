@rem $Id: runall.cmd,v 1.3 2001/04/27 03:15:43 lptr Exp $
@rem $Date: 2001/04/27 03:15:43 $
@rem $Author: lptr $

java -jar Inputter.jar | java -jar PacMan.jar | java -jar Outputter.jar > pacman.rpl
