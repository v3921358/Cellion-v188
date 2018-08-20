#!/bin/sh
export CLASSPATH=".:dist/*" 
java -XX:+AggressiveOpts -server -Dlog4j.configuration=file:"./log4j.xml" -Dwzpath=wz server.Start