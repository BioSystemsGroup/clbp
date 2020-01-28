#!/bin/sh

##
# Used to execute the clbp.  E.g.
# nohup ./bin/run.sh > keep-nohup.out 2>&1 &
#
# Time-stamp: <2020-01-27 17:50:07 gepr>
##

CLASSPATH=$CLASSPATH:./build/classes
CLASSPATH=$CLASSPATH:./lib/genson-1.3.jar
CLASSPATH=$CLASSPATH:./lib/logback-classic-0.9.28.jar
CLASSPATH=$CLASSPATH:./lib/logback-core-0.9.28.jar
#CLASSPATH=$CLASSPATH:./lib/mason-18-with-src.jar
CLASSPATH=$CLASSPATH:./lib/mason20-with-src.jar
CLASSPATH=$CLASSPATH:./lib/slf4j-api-1.6.1.jar
export CLASSPATH

time java -Duser.language=US -Duser.country=US clbp.Main $*
exit 0

