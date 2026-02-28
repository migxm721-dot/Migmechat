#!/bin/bash

BUILDJAR=t
BUILDWAR=
NODOWNLOAD=
while getopts "jwhd" flag
do
  case $flag in
    j) BUILDJAR=; ;;
    w) BUILDWAR=t;;
    d) NODOWNLOAD=-o;;
    h)  echo "Usage: $0 [-j] [-w] [-d]";
		echo "       -d   skip download metadata xml file from tools.projectgoth.com, if you know you did not change dependencies."
		echo "       -j   skip building jar file (skip 'mvn install')"
		echo "       -w   re-build war file in migbo-datasvc-web folder, default just to update jar file in war file"
		exit 0;;
  esac
done


DEPFILE=.dependency_dirs
if [ -f ${DEPFILE} ] ; then
	. ${DEPFILE}
else
	cat << EOF
Could not find the file '${DEPFILE}'.
Please create one. Sample content:

ASSEMBLYDIR=../datasvc-assembly
WEBDIR=../datasvc-web
JBOSS_ST_HOME=/usr/local/jboss7/standalone
EOF
	exit 1
fi

TGTWARFILE=migbo_datasvc.war
TGTJARFILE=migbo-datasvc-core.jar


# build and deploy jar project to local repo first
if [ ! "x$BUILDJAR" == "x" ] ; then
mvn ${NODOWNLOAD} install -DgeneratePom=true -DpomFile=pom.xml -DskipTests=true
RET=$?
if [ $RET -ne 0 ] ; then
	echo "Failed to build jar file ret=$RET. Please check error messages above!"
	exit 2
fi
else
	echo "=== SKIPPED building jar file ==="
fi
ORIDIR=`pwd`

# build war project, if specified
if [ ! "x$BUILDWAR" == "x" ] ; then
cd ${WEBDIR}
#mvn deploy -DskipTests=true -DaltDeploymentRepository=localid::default::file://${MVNDIR}
mvn ${NODOWNLOAD} package -DskipTests=true
RET=$?
if [ $RET -ne 0 ] ; then
	echo "Failed to build war file ret=$RET. Please check error messages above!"
	exit 2
fi
cd ${ORIDIR}
fi


SRCWARFILE=`ls -1t ${WEBDIR}/target/migbo-datasvc-web-*.war | head -1`
if [ -z $SRCWARFILE ] ; then
	echo "Unable to find war file for local deploy!"
	exit 2
fi

SRCJARFILE=`ls -1t target/migbo-datasvc-core-*.jar | head -1`
if [ -z $SRCJARFILE ] ; then
	echo "Unable to find jar file for local deploy!"
	exit 2
fi

# if did not build war file, update jar file into war file manually
if [ "x$BUILDWAR" == "x" ] ; then
	echo === Not re-building ${SRCWARFILE}, but updating jar in it directly ===
	# remove old jar file from war
	7z -tzip d ${SRCWARFILE} WEB-INF/lib/migbo-datasvc-core-*.jar
	RET=$?
	if [ $RET -ne 0 ] ; then
		echo "Failed to remove old jar file from war file"
		exit 3
	fi
	# add new jar file into war
	TMPLIBROOT=/tmp/migbo-build
	TMPLIBDIR=${TMPLIBROOT}/WEB-INF/lib/
	rm -rf ${TMPLIBDIR}
	mkdir -p ${TMPLIBDIR}
	cp ${SRCJARFILE} ${TMPLIBDIR}
	7z -tzip a ${SRCWARFILE} ${TMPLIBROOT}/WEB-INF
	RET=$?
	if [ $RET -ne 0 ] ; then
		echo "Failed to add new jar file into war file"
		exit 3
	fi
fi

SRCLIBDIR=`ls -1td ${WEBDIR}/target/migbo-datasvc-web-*/ | head -1`
if [ -z $SRCLIBDIR ] ; then
	echo "Unable to find war lib folder for local deploy!"
	exit 3
fi

echo ==============
echo SRCWARFILE=$SRCWARFILE
echo SRCJARFILE=$SRCJARFILE
echo SRCLIBDIR =$SRCLIBDIR
echo ==============
