#!/bin/bash
. deploy-common.sh

REMOTESVR=devdell
if [ $# -gt 0 ] ; then
        REMOTESVR=$1
fi

DATA_SVC_CORE_FILE=migbo-datasvc-core.jar
DATA_SVC_WAR_FILE=migbo_datasvc.war
LOCAL_DATA_SVC_DIR=target
LOCAL_JBOSS_DEPLOY_DIR=../migbo-datasvc-web/target

cp -rf ${SRCJARFILE} ${LOCAL_DATA_SVC_DIR}/${DATA_SVC_CORE_FILE}
cp -rf ${SRCWARFILE} ${LOCAL_JBOSS_DEPLOY_DIR}/${DATA_SVC_WAR_FILE}

REMOTE_DATA_SVC_DIR=/usr/migbo
REMOTE_JBOSS_DEPLOY_DIR=/usr/local/jboss7/standalone/deployments

echo "DATA_SVC_CORE_FILE=${DATA_SVC_CORE_FILE}"
echo "DATA_SVC_WAR_FILE=${DATA_SVC_WAR_FILE}"
echo "LOCAL_DATA_SVC_DIR=${LOCAL_DATA_SVC_DIR}"
echo "LOCAL_JBOSS_DEPLOY_DIR=${LOCAL_JBOSS_DEPLOY_DIR}"

REMOTE_STG_DIR=/tmp/migbo
ssh -t ${REMOTESVR} \
"
echo 'clean up remote staging folder ${REMOTE_STG_DIR}'
sudo rm -rfv ${REMOTE_STG_DIR}
echo 'recreating remote staging folder ${REMOTE_STG_DIR}'
sudo mkdir -p ${REMOTE_STG_DIR}

echo 'copy-back ${REMOTE_DATA_SVC_DIR}/lib for sync onto ${REMOTE_STG_DIR} staging folder'
sudo cp -rfv ${REMOTE_DATA_SVC_DIR}/lib ${REMOTE_STG_DIR}

UPLOADER_USERNAME=\`whoami\`
UPLOADER_GROUPNAME=\`groups | cut --delimiter=' '  -f 1\`
echo altering ownership of ${REMOTE_STG_DIR} to \${UPLOADER_USERNAME}:\${UPLOADER_GROUPNAME}
sudo chown -R \${UPLOADER_USERNAME}:\${UPLOADER_GROUPNAME} ${REMOTE_STG_DIR}
"


echo 'uploading files'
CMD="scp -C ${LOCAL_DATA_SVC_DIR}/${DATA_SVC_CORE_FILE} ${LOCAL_JBOSS_DEPLOY_DIR}/${DATA_SVC_WAR_FILE} ${REMOTESVR}:${REMOTE_STG_DIR}"
echo $CMD
$CMD

CMD="rsync -delete -vrlptDz --compress-level=9 -e ssh /usr/migbo/lib ${REMOTESVR}:${REMOTE_STG_DIR}"
echo $CMD
$CMD

ssh -t ${REMOTESVR} \
"
echo 're-creating logs folder on ${REMOTE_DATA_SVC_DIR}'
sudo mkdir -pv ${REMOTE_DATA_SVC_DIR}/logs


echo deploying ${DATA_SVC_CORE_FILE} to ${REMOTE_DATA_SVC_DIR}
sudo cp -fv ${REMOTE_STG_DIR}/${DATA_SVC_CORE_FILE} ${REMOTE_DATA_SVC_DIR}

echo deploying ${DATA_SVC_WAR_FILE} to ${REMOTE_JBOSS_DEPLOY_DIR}
sudo cp -fv ${REMOTE_STG_DIR}/${DATA_SVC_WAR_FILE} ${REMOTE_JBOSS_DEPLOY_DIR}

echo rsync staged lib file to lib deployment folder ${REMOTE_STG_DIR}/lib to ${REMOTE_DATA_SVC_DIR}
sudo rsync -delete -vrlptDz --compress-level=9  ${REMOTE_STG_DIR}/lib to ${REMOTE_DATA_SVC_DIR}

echo SSH: restarting jboss7
sudo /etc/init.d/jboss7 restart
echo SSH: restarting workers
sudo /etc/init.d/Worker0 restart
"
