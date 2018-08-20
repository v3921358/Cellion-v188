@echo off
@title Nox: Rexion
Color 4F
set CLASSPATH=.;dist\*;
java -server -Dlog4j.configuration=file:"./log4j.xml" -Dwzpath=wz server.Start
pause