@echo off
@title Dump
set CLASSPATH=.;dist\*
java -server -Dwzpath=wz\ -DCashShopParse=cs\ tools.export.CashShopParser
@pause