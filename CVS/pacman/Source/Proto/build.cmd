@rem $Id: build.cmd,v 1.9 2001/05/08 20:36:54 lptr Exp $
@rem $Date: 2001/05/08 20:36:54 $
@rem $Author: lptr $

@echo off
echo cleanup...
call cleanup.cmd > nul

echo compiling...
echo  pacman...
call makepacman.cmd

echo  inputter...
call makeinp.cmd

echo  outputter...
call makeout.cmd

echo  editor...
call makeedit.cmd

echo documenting...
call docit.cmd
