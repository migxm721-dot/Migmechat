/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.rewardsystem.processors;

import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.rewardsystem.RewardProgramParamName;
import com.projectgoth.fusion.rewardsystem.processors.RewardProgramProcessor;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;
import com.projectgoth.fusion.rewardsystem.triggers.UserRewardedBaseTrigger;
import java.util.Set;

public abstract class UserRewardedBaseProcessor
extends RewardProgramProcessor {
    @RewardProgramParamName
    public static final String QUALIFIED_REWARDPROGRAM_IDS_PARAM_KEY = "qlfydRwdPgmIDs";
    @RewardProgramParamName
    public static final String QUALIFIED_REWARDPROGRAM_TYPES_PARAM_KEY = "qlfydRwdPgmTypes";
    @RewardProgramParamName
    public static final String QUALIFIED_REWARDPROGRAM_PARAMS_PARAM_KEY = "qlfydRwdPgmPrms";
    @RewardProgramParamName
    public static final String QUALIFIED_REWARDPROGRAM_IDS_IS_WHITELIST_PARAM_KEY = "qlfydRwdPgmIDsIsWhiteLst";
    @RewardProgramParamName
    public static final String QUALIFIED_REWARDPROGRAM_TYPES_IS_WHITELIST_PARAM_KEY = "qlfydRwdPgmTypesIsWhiteLst";
    @RewardProgramParamName
    public static final String QUALIFIED_REWARDPROGRAM_PARAMS_IS_WHITELIST_PARAM_KEY = "qlfydRwdPgmPrmsIsWhiteLst";

    protected final boolean processInternal(RewardProgramData programData, RewardProgramTrigger trigger) throws Exception {
        if (!(trigger instanceof UserRewardedBaseTrigger)) {
            return false;
        }
        UserRewardedBaseTrigger usrRewardedTrigger = (UserRewardedBaseTrigger)trigger;
        if (UserRewardedBaseProcessor.passedFulfilledUserRewardProgramConstraints(programData, usrRewardedTrigger.getQualifiedUserRewardProgram())) {
            return this.processInternal(programData, usrRewardedTrigger);
        }
        return false;
    }

    private static boolean passedQualifiedRewardProgramIDsConstraints(RewardProgramData programData, RewardProgramData qualifiedRewardProgram) {
        if (programData.hasParameter(QUALIFIED_REWARDPROGRAM_IDS_PARAM_KEY)) {
            String rewardProgramIDStr = qualifiedRewardProgram.id.toString();
            return programData.matchesSetOfStringsConstraint(QUALIFIED_REWARDPROGRAM_IDS_PARAM_KEY, QUALIFIED_REWARDPROGRAM_IDS_IS_WHITELIST_PARAM_KEY, rewardProgramIDStr);
        }
        return true;
    }

    private static boolean passedQualifiedRewardProgramTypesConstraints(RewardProgramData programData, RewardProgramData qualifiedRewardProgram) {
        if (programData.hasParameter(QUALIFIED_REWARDPROGRAM_TYPES_PARAM_KEY)) {
            String rewardProgramTypeStr = String.valueOf(qualifiedRewardProgram.type.value());
            return programData.matchesSetOfStringsConstraint(QUALIFIED_REWARDPROGRAM_TYPES_PARAM_KEY, QUALIFIED_REWARDPROGRAM_TYPES_IS_WHITELIST_PARAM_KEY, rewardProgramTypeStr);
        }
        return true;
    }

    private static boolean passedQualifiedRewardProgramParamsConstraints(RewardProgramData programData, RewardProgramData qualifiedRewardProgram) {
        if (programData.hasParameter(QUALIFIED_REWARDPROGRAM_PARAMS_PARAM_KEY)) {
            boolean isWhiteList = programData.getBoolParam(QUALIFIED_REWARDPROGRAM_PARAMS_IS_WHITELIST_PARAM_KEY, true);
            Set<String> inclusionExclusionReferredProgramParams = programData.getStringSetParam(QUALIFIED_REWARDPROGRAM_PARAMS_PARAM_KEY);
            boolean found = false;
            for (String param : inclusionExclusionReferredProgramParams) {
                if (!qualifiedRewardProgram.hasParameter(param)) continue;
                found = true;
                return true;
            }
            return isWhiteList ? found : false == found;
        }
        return true;
    }

    private static boolean passedFulfilledUserRewardProgramConstraints(RewardProgramData programData, RewardProgramData qualifiedRewardProgram) {
        return UserRewardedBaseProcessor.passedQualifiedRewardProgramIDsConstraints(programData, qualifiedRewardProgram) && UserRewardedBaseProcessor.passedQualifiedRewardProgramTypesConstraints(programData, qualifiedRewardProgram) && UserRewardedBaseProcessor.passedQualifiedRewardProgramParamsConstraints(programData, qualifiedRewardProgram);
    }

    protected abstract boolean processInternal(RewardProgramData var1, UserRewardedBaseTrigger var2) throws Exception;
}

