@echo off
del PacMan\*.class
del PacMan.jar

call compile.cmd
call pack.cmd
call docit.cmd
