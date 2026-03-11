#!/bin/bash

# Set environment variables
FUSION_HOME=/home/ubuntu/fusion
LOG_DIR=$FUSION_HOME/logs

# Create logs directory if not exists
mkdir -p $LOG_DIR

# Set classpath
CLASSPATH=$FUSION_HOME/lib/*:$FUSION_HOME/etc

# Java options
JAVA_OPTS="-Dlog4j.configuration=file:$FUSION_HOME/etc/log4j.properties"
JAVA_OPTS="$JAVA_OPTS -Dlog.dir=$LOG_DIR"
JAVA_OPTS="$JAVA_OPTS -Xmx512m -Xms256m"

# Run gateway
cd $FUSION_HOME
java $JAVA_OPTS -cp "$CLASSPATH" gateway.Gateway

exit $?
