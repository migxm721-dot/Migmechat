#!/bin/bash

# include the common deploy related preparation
. deploy-common.sh

JBOSS_ST_HOME=${JBOSS_ST_HOME:-/usr/local/jboss7/standalone}
MIGBODIR=${MIGBODIR:-/usr/migbo}

# Fix jboss subdirectories being owned by root due to
# jboss being started with sudo in the past
sudo chown -R vagrant:vagrant ${JBOSS_ST_HOME}

if [ "$1" = "full" ]
then
	echo "Re-Starting jboss..."
	/etc/init.d/jboss7 restart
	echo "Packaging..."
	mvn clean -DskipTests=true package
	echo "Deploying..."
	mvn jboss-as:deploy

	sleep 10

fi

# hot deploy war file to local jboss
cp -v -p ${SRCWARFILE} ${JBOSS_ST_HOME}/deployments/${TGTWARFILE}
touch ${JBOSS_ST_HOME}/deployments/${TGTWARFILE}.dodeploy

# deploy jar file to local worker
cp -v -p ${SRCJARFILE} ${MIGBODIR}/${TGTJARFILE}

# deploy lib files to local worker
mkdir -p ${MIGBODIR}/lib/
echo Sync-ing lib files ...
sudo rsync -thriu --exclude=migbo-datasvc* ${SRCLIBDIR}/WEB-INF/lib/*.jar ${MIGBODIR}/lib/

# restart server and worker
LOGFILE=${JBOSS_ST_HOME}/log/server.log

echo -n "waiting for jboss 7 to hot deploy "
sleep 1
COUNT=10
while [ ! -f ${JBOSS_ST_HOME}/deployments/${TGTWARFILE}.deployed -o -f ${JBOSS_ST_HOME}/deployments/${TGTWARFILE}.dodeploy ] ; do
    #tail -1 $LOGFILE
	echo -n "."
    sleep 1
	COUNT=$(($COUNT-1))
done
echo
if [ $COUNT -eq 0 ] ; then
	echo jboss 7 has not hot deployed the application after 10 seconds...
	echo please check the log file $LOGFILE manually
fi

echo "restarting migbo worker ..."
sudo /etc/init.d/Worker0 restart
echo "Done!"
