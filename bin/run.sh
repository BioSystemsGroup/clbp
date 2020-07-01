
#!/bin/sh

##
# Used to execute the clbp.  E.g.
# nohup ./bin/run.sh > keep-nohup.out 2>&1 &
#
# Time-stamp: <2020-06-26 12:02:58 gepr>
##

CLASSPATH=$CLASSPATH:./build/classes
CLASSPATH=$CLASSPATH:./lib/genson-1.6.jar
CLASSPATH=$CLASSPATH:./lib/logback-classic-0.9.28.jar
CLASSPATH=$CLASSPATH:./lib/logback-core-0.9.28.jar
#CLASSPATH=$CLASSPATH:./lib/mason-18-with-src.jar
CLASSPATH=$CLASSPATH:./lib/mason20-with-src.jar
CLASSPATH=$CLASSPATH:./lib/slf4j-api-1.6.1.jar
CLASSPATH=$CLASSPATH:./lib/commons-io-2.6.jar
export CLASSPATH

#time java -Duser.language=US -Duser.country=US clbp.Main $*
#time /usr/local/graalvm-ce-java11-20.1.0/bin/java clbp.Main $*
nice -19 /usr/local/graalvm-ce-java11-20.1.0/bin/java clbp.Main $*
exit 0

