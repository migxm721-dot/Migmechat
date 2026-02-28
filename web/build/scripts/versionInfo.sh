#!/bin/sh

lastRCVersion=($(git branch -a |
	grep -E "remotes/REL_[0-9.]+_RC" |
	tail -1 |
	grep -oE "[0-9]+"
))

lastTagVersion=($(git branch -a |
	grep remotes/tags | grep '\.' |
	grep -vE "_RC|Fusion" | # explicit filtering to support fusion
	tail -1 |
	grep -oE "[0-9]+"
))

[ ${lastRCVersion[0]} -gt ${lastTagVersion[0]} ] &&
	majorVersion=${lastRCVersion[0]} ||
	majorVersion=${lastTagVersion[0]}

[ ${lastRCVersion[1]} -gt ${lastTagVersion[1]} ] &&
	minorVersion=${lastRCVersion[1]} ||
	minorVersion=${lastTagVersion[1]}

[ ${lastRCVersion[1]} -gt ${lastTagVersion[1]} ] &&
	miniVersion="" ||
	miniVersion=$((${lastTagVersion[2]}+1))

[ ${lastRCVersion[0]} -gt ${lastTagVersion[0]} ] &&
	minorVersion=${lastRCVersion[1]} &&
	miniVersion=""

echo $majorVersion $minorVersion $miniVersion | tr " " "."