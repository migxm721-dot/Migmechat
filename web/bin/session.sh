#!/bin/sh

function error()
{
	echo "Usage: ${0##/*/} [<host> [<username> [<password>]]]"
	exit
}

case $1 in
	help|--help|-h|?) error;;
	edit|-e) vim $0 && exit;;
esac

login_url=${1:-"https://login.devlab.projectgoth.com"}
username=${2:-"chernjie"}
password=${3:-"ten20304050"}
useragent=${4:-"Chrome Test"}

curl -ks $login_url/datasvc/sso/login -XPOST -d"username=$username&password=$password" --user-agent "$useragent" | json
exit

result=$(curl -ks $login_url/datasvc/sso/login -XPOST -d"username=$username&password=$password" --user-agent "$useragent")

# Test if result is in JSON format or if json tool exists
echo "$result" | json > /dev/null
[ $? -gt 0 ] && echo "$result" && exit

printf "\033[31m" &&
	[ "$(echo "$result" | json data.captcha)" != "null" ] &&
	echo "$result" | json data.message &&
#	printf "data:image/png;base64," &&
#	echo "$result" | json data.captcha &&
	printf "\033[0m" &&
	exit
printf "\033[32m" &&
	[ -n "$(echo "$result" | json data.sessionId)" ] &&
	echo "$result" | json data.sessionId && 
	printf "\033[0m" &&
	exit
printf "\033[31m" &&
	[ -n "$(echo "$result" | json error.errno)" ] &&
	echo "$result" | json error.message &&
	printf "\033[0m" &&
	exit

