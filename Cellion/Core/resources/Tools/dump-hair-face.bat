@echo off
@title Hair Face Dump
Color 0A
set CLASSPATH=.;dist\*
echo CLASSPATH=.;dist\*
java -client -Dwzpath=wz tools.HairFaceDump
pause  