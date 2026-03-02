/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.rewardsystem.processors;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.rewardsystem.RewardProgramParamName;
import com.projectgoth.fusion.rewardsystem.processors.RewardProgramProcessor;
import com.projectgoth.fusion.rewardsystem.triggers.GroupActivityTrigger;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;
import org.apache.log4j.Logger;

public class GroupActivityRewardProgramProcessor
extends RewardProgramProcessor {
    @RewardProgramParamName
    public static final String GROUPID_PARAM_KEY = "groupID";
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(GroupActivityRewardProgramProcessor.class));

    protected boolean processInternal(RewardProgramData programData, RewardProgramTrigger trigger) {
        if (!(trigger instanceof GroupActivityTrigger)) {
            return false;
        }
        GroupActivityTrigger bgwTrigger = (GroupActivityTrigger)trigger;
        String requiredGroupID = programData.getStringParam(GROUPID_PARAM_KEY, "");
        log.debug((Object)("userID[" + trigger.userData.userID + "]  groupID in Trigger[" + bgwTrigger.groupID + "] required[" + requiredGroupID + "]"));
        if (!StringUtil.isBlank(requiredGroupID)) {
            return requiredGroupID.equalsIgnoreCase(bgwTrigger.groupID);
        }
        return true;
    }
}

