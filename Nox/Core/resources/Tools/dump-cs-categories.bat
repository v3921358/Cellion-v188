@echo off
@title Dump
set CLASSPATH=.;..\dist\*
java -server -Dwzpath=..\wz\ tools.export.CashShop
pause