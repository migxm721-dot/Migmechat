#!/bin/bash

# The following command can only be used if Tomcat has the deployment manager enabled.
# (cd web/web/;mvn tomcat7:deploy -Dtarget=MIAB)

REMOTESVR=miab
WARFILENAME=auth-web.war
WARFILEPATH=web/web/target/${WARFILENAME}
REMOTE_DEPLOYMENT_DIR=/usr/local/tomcat7/webapps/ROOT
REMOTE_WARFILEPATH=${REMOTE_DEPLOYMENT_DIR}.war

echo ""
echo "Removing ${REMOTE_DEPLOYMENT_DIR} ..."
ssh ${REMOTESVR} "rm -fR ${REMOTE_DEPLOYMENT_DIR}"
echo ""
echo "Copying ${WARFILEPATH} to ${REMOTESVR}:${REMOTE_WARFILEPATH} ..."
scp ${WARFILEPATH} ${REMOTESVR}:${REMOTE_WARFILEPATH}
echo ""
echo "Done!"
