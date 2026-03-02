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
import com.projectgoth.fusion.rewardsystem.processors.ReferredUserRewardedBaseProcessor;
import com.projectgoth.fusion.rewardsystem.triggers.ReferredUserRewardedBaseTrigger;
import com.projectgoth.fusion.rewardsystem.triggers.ReferredUserRewardedWithMigLevelTrigger;
import org.apache.log4j.Logger;

public class ReferredUserRewardedWithMigLevelProcessor
extends ReferredUserRewardedBaseProcessor {
    @RewardProgramParamName
    public static final String REFERRED_USER_REWARDED_MIG_LEVEL_RANGE_PARAM_KEY = "refdUsrRwdMigLvlRange";
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ReferredUserRewardedWithMigLevelProcessor.class));

    protected boolean processInternal(RewardProgramData programData, ReferredUserRewardedBaseTrigger trigger) {
        if (!(trigger instanceof ReferredUserRewardedWithMigLevelTrigger)) {
            return false;
        }
        ReferredUserRewardedWithMigLevelTrigger rfdUserRewardedWithMigLevelTrigger = (ReferredUserRewardedWithMigLevelTrigger)trigger;
        if (programData.hasParameter(REFERRED_USER_REWARDED_MIG_LEVEL_RANGE_PARAM_KEY)) {
            String rewardedMigLevelRangeConstraint = programData.getStringParam(REFERRED_USER_REWARDED_MIG_LEVEL_RANGE_PARAM_KEY, "");
            int rewardedNewMigLevel = rfdUserRewardedWithMigLevelTrigger.getReferredUserNewMigLevel();
            if (log.isDebugEnabled()) {
                log.debug((Object)("rewardedMigLevelRangeConstraint:[" + rewardedMigLevelRangeConstraint + "].rewardedNewMigLevel:[" + rewardedNewMigLevel + "]"));
            }
            return ReferredUserRewardedWithMigLevelProcessor.checkRewardedNewMigLevel(rewardedMigLevelRangeConstraint, rewardedNewMigLevel);
        }
        return true;
    }

    private static boolean checkRewardedNewMigLevel(String rewardedMigLevelRangeConstraint, int rewardedNewMigLevel) {
        if (StringUtil.isBlank(rewardedMigLevelRangeConstraint)) {
            return true;
        }
        int separatorPos = rewardedMigLevelRangeConstraint.indexOf(44);
        if (separatorPos == -1) {
            int rewardedMigLevelConstraint = Integer.parseInt(rewardedMigLevelRangeConstraint);
            return rewardedMigLevelConstraint == rewardedNewMigLevel;
        }
        String minValStr = rewardedMigLevelRangeConstraint.substring(0, separatorPos);
        int maxValStart = separatorPos + 1;
        String maxValStr = maxValStart < rewardedMigLevelRangeConstraint.length() ? rewardedMigLevelRangeConstraint.substring(maxValStart) : "";
        return ReferredUserRewardedWithMigLevelProcessor.isAboveMin(minValStr, rewardedNewMigLevel) && ReferredUserRewardedWithMigLevelProcessor.isBelowMax(maxValStr, rewardedNewMigLevel);
    }

    private static boolean isAboveMin(String minValStr, int val) {
        if (!StringUtil.isBlank(minValStr)) {
            return Integer.parseInt(minValStr) <= val;
        }
        return true;
    }

    private static boolean isBelowMax(String maxValStr, int val) {
        if (!StringUtil.isBlank(maxValStr)) {
            return val <= Integer.parseInt(maxValStr);
        }
        return true;
    }
}

