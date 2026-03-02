/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.rewardsystem.processors;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.rewardsystem.RewardCentre;
import com.projectgoth.fusion.rewardsystem.processors.RewardProgramProcessor;
import com.projectgoth.fusion.rewardsystem.triggers.ConsecutiveLoginTrigger;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;
import org.apache.log4j.Logger;

public class ConsecutiveLoginRewardProgramProcessor
extends RewardProgramProcessor {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ConsecutiveLoginRewardProgramProcessor.class));
    private static final long MILLIS_IN_A_DAY = 86400000L;

    protected boolean processInternal(RewardProgramData programData, RewardProgramTrigger trigger) {
        if (!(trigger instanceof ConsecutiveLoginTrigger)) {
            return false;
        }
        ConsecutiveLoginTrigger clTrigger = (ConsecutiveLoginTrigger)trigger;
        if (clTrigger.lastLoginDate == null) {
            return true;
        }
        long lastLoginDay = clTrigger.lastLoginDate.getTime() / 86400000L;
        long currentDay = System.currentTimeMillis() / 86400000L;
        try {
            if (lastLoginDay > currentDay) {
                return false;
            }
            if (currentDay == lastLoginDay) {
                return false;
            }
            if (currentDay - lastLoginDay == 1L) {
                return true;
            }
            RewardCentre.getInstance().resetProgramScoreForUser(programData, trigger.userData.userID);
            return true;
        }
        catch (Exception e) {
            log.warn((Object)("Exception caught while trying to process consecutive login trigger for [" + trigger.userData.userID + "] programID[" + programData.id + "]"));
            return false;
        }
    }
}

