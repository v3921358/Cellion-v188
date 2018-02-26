@echo off
@title Nox: Rexion
set CLASSPATH=.;dist\*;
java -server -Xmx2048m -Dlog4j.configuration=file:"./log4j.xml" -Dwzpath=wz\ -Xrunjdwp:transport=dt_socket,address=9002,server=y,suspend=n server.Start
pause