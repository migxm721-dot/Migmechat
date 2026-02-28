#!/bin/bash

# migbo config:
#  MIGBO_BASE     migbo folder, default /usr/migbo
#  CONFIGDIR      migbo config folder relative to MIGBO_BASE, default config
#  LOGDIR         migbo logs folder, default MIGBO_BASE/logs
# clojure config:
#  CLOJURE_LIBDIR lib folder containing clojure jar files, default ~/lib/clj
#  CLOJURE_JARS   clojure jar files, used when CLOJURE_LIBDIR is not specified

MIGBO_BASE=${MIGBO_BASE:-/usr/migbo}

# set CONFIGDIR to a different folder if u want to use a different config folder
CONFIGDIR=${CONFIGDIR:-config}


DEFAULT_CLOJURE_LIBDIR=~/lib/clj
CLOJURE_LIBDIR_S=f
if [ ! -z $CLOJURE_LIBDIR ] ; then
	if [ ! -d $CLOJURE_LIBDIR ] ; then
		echo CLOJURE_LIBDIR $CLOJURE_LIBDIR specified is not a valid folder
		exit 1
	fi
	CLOJURE_LIBDIR_S=t
else
	# if CLOJURE_LIBDIR not specified, check CLOJURE_JARS first before resorting back to default
	if [ ! -z $CLOJURE_JARS ] ; then
		for f in ${CLOJURE_JARS/:/ } ; do
			if [ ! -f $f ] ; then
				echo $f specified in CLOJURE_JARS $CLOJURE_JARS , but not found
				exit 2
			fi
		done
	fi
fi

if [ $CLOJURE_LIBDIR_S = t ] || [ -z $CLOJURE_JARS ] ; then
	# lib dir not specified, using default lib dir
	DETECTED_CLOJURE_JARS=$(ls -1 ${CLOJURE_LIBDIR:-$DEFAULT_CLOJURE_LIBDIR}/*.jar 2> /dev/null | tr \\n \:)
	if [ ! -z $DETECTED_CLOJURE_JARS ] ; then
		DETECTED_CLOJURE_JARS=${DETECTED_CLOJURE_JARS:0:$((${#DETECTED_CLOJURE_JARS}-1))}
		if [ $CLOJURE_LIBDIR_S = t ] && [ ! -z $CLOJURE_JARS ] ; then
			echo "Warning: CLOJURE_JARS specified as $CLOJURE_JARS , but since CLOJURE_LIBDIR $CLOJURE_LIBDIR is specified, will use that instead"
			echo "         Using $DETECTED_CLOJURE_JARS"
		fi
		CLOJURE_JARS=$DETECTED_CLOJURE_JARS
	else
		if [ $CLOJURE_LIBDIR_S = t ] ; then
			echo "CLOJURE_LIBDIR is specified as $CLOJURE_LIBDIR , but no jar files found there"
			exit 3
		fi
		if [ -z $CLOJURE_JARS ] ; then
			echo "Could not find Clojure jar file(s)."
			echo "Please either specify CLOJURE_LIBDIR or CLOJURE_JARS env variable, or put the jar files in $DEFAULT_CLOJURE_LIBDIR"
			exit 4
		fi
	fi
fi

#echo $CLOJURE_JARS

DETECTED_MIGBO_JARS=$(ls -1 ${MIGBO_BASE}/migbo-datasvc-core.jar ${MIGBO_BASE}/lib/*.jar 2> /dev/null | tr \\n \:)
if [ ! -z $DETECTED_MIGBO_JARS ] ; then
	DETECTED_MIGBO_JARS=${DETECTED_MIGBO_JARS:0:$((${#DETECTED_MIGBO_JARS}-1))}
else
	echo "Error: Unable to find any jar files in MIGBO_BASE ${MIGBO_BASE}"
	exit 5
fi

# the real meat here, pass in -h for all options in clojure.main
CONFIG_DIR=${MIGBO_BASE}/$CONFIGDIR
LOG_DIR=${LOGDIR:-${MIGBO_BASE}/logs}
if [ ! -d $LOG_DIR ] || [ ! -w $LOG_DIR ] ; then
	if [ -z $LOGDIR ] ; then
		echo "default log directory $LOG_DIR is not a writable directory. please use LOGDIR to overwrite the default."
		exit 6
	else
		echo "LOGDIR is specified as $LOGDIR , but it's not a writable directory."
		exit 7
	fi
fi

export JAVA_ARGS="-Dconfig.dir=${CONFIG_DIR} -Dlog.dir=${LOG_DIR} -Djboss.server.log.dir=${LOG_DIR} -Dlog.filename=run-clj -Dnet.spy.log.LoggerImpl=net.spy.memcached.compat.log.Log4JLogger"
if [ `echo $CLOJURE_JARS | grep -c "jline"` -eq 1 ] ; then
	JLINE=jline.ConsoleRunner
fi
java $JAVA_ARGS -server -Xmx256m -classpath ${DETECTED_MIGBO_JARS}:${CLOJURE_JARS}:${MIGBO_BASE}:${CONFIG_DIR}:. $JLINE clojure.main $@

