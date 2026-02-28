#!/bin/sh

SOURCE_TREE_BASE=../..
COMMON_BASE=$SOURCE_TREE_BASE/common
FUSION_BASE=..

CLASSPATH=$FUSION_BASE/eclipse_bin:$FUSION_BASE

for jar in `find $COMMON_BASE/ -type f | grep -v Ice-3 | grep -iv swt | grep -i jar$` ; do
        #echo "adding " $jar
        CLASSPATH=$CLASSPATH:$jar
done

CLASSPATH=$CLASSPATH:$COMMON_BASE/Ice-3.3.1-mac/lib/Ice.jar:$COMMON_BASE/swt/swt-macosx-3.3.1.1.jar

#echo "CLASSPATH = $CLASSPATH"

#echo java $JAVA_ARGS -server -Xmx1536m -Dlog.dir=logs/ -Dconfig.dir=$FUSION_BASE/config -classpath $CLASSPATH $@
ulimit -n 8192; java $JAVA_ARGS -server -Xmx256m -Dlog.dir=$FUSION_BASE/logs/ -Dconfig.dir=$FUSION_BASE/etc/ -classpath $CLASSPATH $@
