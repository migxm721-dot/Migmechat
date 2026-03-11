#!/bin/bash

## .........................
## Start Bot Hunter
## .........................

echo STARTING BOT HUNTER

FUSION_BASE=/usr/fusion

export CP=$FUSION_BASE/Fusion.jar:$FUSION_BASE/jpcap.jar:$FUSION_BASE/jbossall-client.jar:
export CP=$CP:$FUSION_BASE/commons-cli-1.0.jar:$FUSION_BASE/Ice-3.3.1.jar:$FUSION_BASE/log4j-1.2.9.jar

### sudo java -cp $CP com.projectgoth.fusion.bothunter.BotHunter "$@"

sudo java -cp $CP com.projectgoth.fusion.bothunter.BotHunter -AnalyseRatios -AnalyseSequence -PacketSource eth2 -AnalysisThreadCount 5 -PcapFilter 'port 9119 and dst 192.168.1.110' 


