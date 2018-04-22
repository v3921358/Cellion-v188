@echo off
@title Nox: Rexion
Color 5F
set CLASSPATH=.;dist\*;
java -XX:+UseG1GC -server -Xrunjdwp:transport=dt_socket,address=9000,server=y,suspend=n -Dlog4j.configuration=file:"./log4j.xml" -Dwzpath=wz server.Start
pause