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
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;
import com.projectgoth.fusion.rewardsystem.triggers.ThirdPartyAppInternalInvitationTrigger;
import org.apache.log4j.Logger;

public class ThirdPartyAppInternalInvitationRewardProgramProcessor
extends RewardProgramProcessor {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ThirdPartyAppInternalInvitationRewardProgramProcessor.class));
    @RewardProgramParamName
    public static final String APP_NAME_PARAM_KEY = "appName";

    protected boolean processInternal(RewardProgramData programData, RewardProgramTrigger trigger) {
        if (!(trigger instanceof ThirdPartyAppInternalInvitationTrigger)) {
            return false;
        }
        ThirdPartyAppInternalInvitationTrigger tpapTrigger = (ThirdPartyAppInternalInvitationTrigger)trigger;
        String appName = programData.getStringParam(APP_NAME_PARAM_KEY, "");
        log.debug((Object)("userID[" + trigger.userData.userID + "] appName in Trigger[" + tpapTrigger.applicationName + "] required[" + appName + "]"));
        if (!StringUtil.isBlank(appName)) {
            return appName.equalsIgnoreCase(tpapTrigger.applicationName);
        }
        return true;
    }
}

