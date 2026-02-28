#!/bin/sh

COLOR_RED()   { echo -en "\033[31m"; }
COLOR_GREEN() { echo -en "\033[32m"; }
COLOR_YELLOW(){ echo -en "\033[33m"; }
COLOR_BLUE()  { echo -en "\033[34m"; }
COLOR_RESET() { echo -en "\033[0m";  }

# Check file for PHP syntax errors
# takes a list of filenames
function php_lint()
{
	for i
	do
		# check if file exists
		# check if file ends with ".php"
		if [ -f "$i" ] && [ "${i: -4}" == ".php" ]
		then
			php -l "$i" > /dev/null
			[ $? -gt 0 ] && error_count[${#error_count[@]}]="$i"
		fi
	done

	# syntax check fails, force exit
	[ ${#error_count[@]} -gt 0 ] &&
		COLOR_RED &&
		echo "Error: ${#error_count[@]} PHP syntax error found" &&
		echo "${error_count[@]}" | tr " " '\n' &&
		COLOR_RESET &&
		exit 255
}

cd ../
php_lint $(git diff --name-only trunk)