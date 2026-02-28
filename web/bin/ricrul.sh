#!/bin/sh

# Assumptions:
# - All filenames does not contain spaces
# - bin/yaml.php has the latest index, to update run:
#   [ $(stat -f%m /var/tmp/controller_definition.json) > $(stat -f%m ~/mig33/web/sites/controller/) ] || echo bin/yaml.php --force

##### Configurations here #####

CAV=bin/yaml.php
ACK=ack

##### Code starts here #####

COMMAND="$1"
shift
[ $(uname) = "Linux" ] && sedRegExp="-r" || sedRegExp="-E"

COLOR_RED()   { echo "\033[31m"; }
COLOR_GREEN() { echo "\033[32m"; }
COLOR_YELLOW(){ printf "\033[33m"; }
COLOR_BLUE()  { echo "\033[34m"; }
COLOR_RESET() { printf "\033[0m";  }
SectionTitle(){ COLOR_YELLOW && echo $@ && COLOR_RESET; }

function error()
{
	if [ $# -gt 0 ]
	then
		echo "Error: $@"
		echo
	fi
	echo "Generates a list of entry URL for regression based on list of files given as an argument"
	echo
	echo "Usage: ${0##/*/} -h|help|?|--help"
	echo "Usage: ${0##/*/} -s|stat|stats|--stat|--stats [files...]"
	echo "Usage: ${0##/*/} -d|deleted|--deleted [files...]"
	echo "Usage: ${0##/*/} -m|modified|--modified [files...]"
	echo "Usage: ${0##/*/} -a|all|--all [files...]"
	[ $# -gt 0 ] && exit 1 || exit 0
}
function get_deleted_files()
{
	for i
	do
		[ -f $i ] || echo $i
	done
}
DELETED=($(get_deleted_files $@))
function get_modified_files()
{
	for i
	do
		[ -f $i ] && echo $i
	done
}
MODIFIED=($(get_modified_files $@))

function FilterModels()
{
	echo $@ |
	tr ' ' '\n' |
	grep sites/model |
	sed -E "s#sites/model/(.*).php#\1#"
}
function FilterValidators()
{
	echo $@ |
	tr ' ' '\n' |
	grep sites/validation |
	sed -E "s#sites/validation/(.*)_validator.php#\1#"
}
function FilterViews()
{
	echo $@ |
	tr ' ' '\n' |
	grep sites/view.*template |
	sed -E "s#sites/view/(.*)/(.*)/template/(.*)_template.php#\1/\3/\2#" |
	sort -u
}
function FilterDaos()
{
	echo $@ |
	tr ' ' '\n' |
	grep sites/dao |
	# sed -E "s#.*/(.*).php#\1#"
	xargs -n1 basename | cut -d'.' -f1 |
	sort -u
}
function FilterDecorators()
{
	echo $@ |
	tr ' ' '\n' |
	grep sites/decorator |
	# sed -E "s#.*/(.*).php#\1#"
	xargs -n1 basename | cut -d'.' -f1 |
	sort -u
}

function CheckStats()
{
	echo Type Deleted Modified
	for i in FilterDaos FilterModels FilterValidators FilterDecorators FilterViews
	do
		echo $i $($i ${DELETED[@]} | wc -l) $($i ${MODIFIED[@]} | wc -l)
	done
}

function CheckDeletedFiles()
{
	SectionTitle Checking DELETED models
	FilterModels ${DELETED[@]} | xargs -n1 $CAV --URL --model
	SectionTitle Checking DELETED validators
	FilterValidators ${DELETED[@]} | xargs -n1 $CAV --URL --validation
	SectionTitle Checking DELETED views
	FilterViews ${DELETED[@]}  | cut -d/ -f1,2 | xargs -n1 $CAV --view
	SectionTitle Checking DELETED daos
	FilterDaos ${DELETED[@]} | tr -d _ | xargs -n1 $ACK -clhi --noenv
	SectionTitle Checking DELETED decorators
	FilterDecorators ${DELETED[@]} | xargs -n1 $CAV --URL --decorator
	# domain
	# controller
	# modules
}

function CheckModifiedFiles()
{
	##### MODIFIED files #####
	# SectionTitle Checking MODIFIED models
	FilterModels ${MODIFIED[@]} | xargs -n1 $CAV --model | xargs $CAV --URL
	# SectionTitle Checking MODIFIED validators
	FilterValidators ${MODIFIED[@]} | xargs -n1 $CAV --validation | xargs $CAV --URL
	# SectionTitle Checking MODIFIED views
	for i in $(FilterViews ${MODIFIED[@]})
	do
		echo $i | cut -d/ -f1,2 | xargs $CAV --view | xargs $CAV --URL | grep ${i/*\/}
	done

	# TODO:
	# SectionTitle Checking MODIFIED daos
	# FilterDaos ${MODIFIED[@]} | tr -d _ | xargs -n1 $ACK -clhi --noenv
	# SectionTitle Checking MODIFIED decorators
	# FilterDecorators ${MODIFIED[@]} | xargs -n1 $CAV --decorator --URL
}

case $COMMAND in
	?|-h|--help|help) error;;
	-d|deleted|--deleted) CheckDeletedFiles;;
	-m|modified|--modified) CheckModifiedFiles | sort -u;;
	-a|all|--all) CheckDeletedFiles; CheckModifiedFiles | sort -u;;
	-s|stat|stats|--stat|--stats) CheckStats | column -t;;
	*) error Invalid Command;;
esac
