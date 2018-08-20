#!/bin/sh
export CLASSPATH=".:dist/*" 
java -server -Dlog4j.configuration=file:"./log4j.xml" -Dwzpath=wz -agentpath:/home/cloudrexion/jprofiler10.0.4/bin/linux-x64/libjprofilerti.so=port=8849 server.Start
