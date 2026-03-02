/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.rewardsystem.processors;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.rewardsystem.RewardProgramParamName;
import com.projectgoth.fusion.rewardsystem.processors.RewardProgramProcessor;
import com.projectgoth.fusion.rewardsystem.triggers.BotGameSpendingTrigger;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;
import org.apache.log4j.Logger;

public class BotGameSpendingRewardProgramProcessor
extends RewardProgramProcessor {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(BotGameSpendingRewardProgramProcessor.class));
    @RewardProgramParamName
    public static final String BOT_ID_PARAM_KEY = "botID";

    protected boolean processInternal(RewardProgramData programData, RewardProgramTrigger trigger) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("BotGameSpendingRewardProcessor triggered for program [" + programData.id + "] and trigger : " + trigger));
        }
        if (!(trigger instanceof BotGameSpendingTrigger)) {
            return false;
        }
        BotGameSpendingTrigger bgsTrigger = (BotGameSpendingTrigger)trigger;
        int botID = programData.getIntParam(BOT_ID_PARAM_KEY, -1);
        if (log.isDebugEnabled()) {
            log.debug((Object)("userID[" + trigger.userData.userID + "] botID in Trigger[" + bgsTrigger.botID + "] required[" + botID + "]"));
        }
        if (botID > 0) {
            return botID == bgsTrigger.botID;
        }
        return true;
    }
}

