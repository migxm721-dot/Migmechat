#!/bin/bash

# The following command can only be used if Tomcat has the deployment manager enabled.
# (cd web/web/;mvn tomcat7:deploy -Dtarget=QAlab)

REMOTESVR=qalab_oauth
WARFILENAME=auth-web.war
WARFILEPATH=web/web/target/${WARFILENAME}
REMOTE_TEMP_DIR=/tmp/auth
REMOTE_TEMP_PATH=${REMOTE_TEMP_DIR}/${WARFILENAME}
REMOTE_DEPLOYMENT_DIR=/usr/local/tomcat7/webapps/ROOT
REMOTE_WARFILEPATH=${REMOTE_DEPLOYMENT_DIR}.war
CONFIGFILEPATH=service/config
REMOTE_CONFIGPATH=/usr/auth/config
AESKEYSFILEPATH=service/aeskeys
REMOTE_AESKEYSPATH=/usr/auth/aeskeys

ssh ${REMOTESVR} "mkdir -p ${REMOTE_TEMP_DIR}"
echo ""

echo "Copying ${WARFILEPATH} to ${REMOTESVR}:${REMOTE_TEMP_PATH} ..."
scp ${WARFILEPATH} ${REMOTESVR}:${REMOTE_TEMP_PATH}
echo ""

echo "Redeploying war file ..."
ssh -t ${REMOTESVR} " \
	sudo rm -fR ${REMOTE_DEPLOYMENT_DIR}; \
	sudo cp ${REMOTE_TEMP_PATH} ${REMOTE_WARFILEPATH}; \
	rm -fR ${REMOTE_TEMP_PATH} "
echo ""

read -p "Do you want to update the config files? [y/n] " yn
case $yn in
	[Yy]* ) echo "Copying ${CONFIGFILEPATH} to ${REMOTESVR}:${REMOTE_CONFIGPATH} ..."
            scp -r ${CONFIGFILEPATH} ${REMOTESVR}:${REMOTE_CONFIGPATH}
            ;;
	* ) echo "Skipped updating config files";;
esac
echo ""

read -p "Do you want to update the AES keys? [y/n] " yn
case $yn in
	[Yy]* ) echo "Copying ${AESKEYSFILEPATH} to ${REMOTESVR}:${REMOTE_AESKEYSPATH} ..."
            scp -r ${AESKEYSFILEPATH} ${REMOTESVR}:${REMOTE_AESKEYSPATH}
            ;;
	* ) echo "Skipped updating AES keys";;
esac
echo ""

echo "Done!"
