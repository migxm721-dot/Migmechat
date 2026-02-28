#!/bin/bash
isQA=`git status -b | grep "On branch QA" | wc -l`
if [ $isQA == "1" ]
then
	mvn -B release:prepare
	mvn release:perform
else
        echo "not on QA branch!";
fi;
