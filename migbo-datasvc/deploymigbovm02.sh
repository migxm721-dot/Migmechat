#!/bin/bash

# include the common deploy related preparation
. deploy-common.sh

REMOTESVR=migbo-vm02-root
#REMOTEDIR=/usr/local/jboss/server/default
#DEPLOYDIR=deploy
REMOTEDIR=/usr/local/jboss7/standalone
DEPLOYDIR=deployments


# deploy to sin02
echo "deploying to ${REMOTESVR}"
if [ "x$BUILDWAR" == "x" ] ; then
	echo " copying jar file ${SRCJARFILE} to ${REMOTESVR} ..."
scp -p ${SRCJARFILE} root@${REMOTESVR}:/tmp/
else
	echo " copying war file ${SRCWARFILE} to ${REMOTESVR} ..."
scp -p ${SRCWARFILE} root@${REMOTESVR}:/tmp/${TGTWARFILE}
fi

if [ $? -ne 0 ] ; then exit 2; fi;

SRCJARFILEBASE=`basename ${SRCJARFILE}`

TMPWARDIR=/tmp/migbo-datasvc-war
TMPMIGBODIR=/tmp/migbo-dir
ssh root@${REMOTESVR} \
"
if [ \"x$BUILDWAR\" == \"x\" ] ; then
	echo Updating war file on ${REMOTESVR} ...
	# remove old jar file from war
	7za -tzip d /tmp/${TGTWARFILE} WEB-INF/lib/migbo-datasvc-core-*.jar
	# add new jar file into war
	TMPLIBROOT=/tmp/migbo-build
	TMPLIBDIR=${TMPLIBROOT}/WEB-INF/lib/
	rm -rf ${TMPLIBDIR}
	mkdir -p ${TMPLIBDIR}
	cp -v /tmp/${SRCJARFILEBASE} ${TMPLIBDIR}
	7za -tzip a /tmp/${TGTWARFILE} ${TMPLIBROOT}/WEB-INF
	echo Updated war file on ${REMOTESVR}
fi

echo Copying worker files to migbo-vm03
rm -rf ${TMPWARDIR} ${TMPMIGBODIR}
mkdir -p ${TMPWARDIR}
mkdir -p ${TMPMIGBODIR}
cd ${TMPWARDIR}
unzip -x ../migbo_datasvc.war
cp -v -p ${TMPWARDIR}/WEB-INF/lib/${SRCJARFILEBASE} ${TMPMIGBODIR}/${TGTJARFILE}
cp -v -p -r ${TMPWARDIR}/WEB-INF/lib ${TMPMIGBODIR}/
rm -v ${TMPMIGBODIR}/lib/${SRCJARFILEBASE}

ssh migbo-vm03 'rm -rf ${TMPMIGBODIR}'
scp -p -r ${TMPMIGBODIR} migbo-vm03:/tmp/

echo Deploying war file
cp -v /tmp/${TGTWARFILE} ${REMOTEDIR}/${DEPLOYDIR} ; 
touch ${REMOTEDIR}/${DEPLOYDIR}/${TGTWARFILE}.dodeploy
echo files deployed on ${REMOTESVR};
sleep 5;
if [ -f ${REMOTEDIR}/${DEPLOYDIR}/${TGTWARFILE}.deployed ] ; then
	echo ' war file deployed on jboss7'
else
	echo ' war file NOT YET deployed on jboss7, log:'
	tail -4 ${REMOTEDIR}/log/server.log
fi

# deploy worker to migbo-vm03
echo Deploying on migbo-vm03
ssh migbo-vm03 '
rm -rf /usr/migbo/lib
cp -v -r ${TMPMIGBODIR}/* /usr/migbo
nohup /etc/init.d/Worker0 restart > /tmp/Worker0
sleep 2
tail /tmp/Worker0
echo Probably done starting up Worker0
'
echo Done on migbo-vm03
"
if [ $? -ne 0 ] ; then echo failed to run deploy on ${REMOTESVR}; exit 2; fi;
echo ===== done, deployed to jboss on ${REMOTESVR} =====
