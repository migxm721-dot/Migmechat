/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBException
 */
package com.projectgoth.fusion.rewardsystem.processors;

import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.data.UserReputationScoreAndLevelData;
import com.projectgoth.fusion.rewardsystem.RewardCentre;
import com.projectgoth.fusion.rewardsystem.RewardProgramParamName;
import com.projectgoth.fusion.rewardsystem.processors.RewardProgramProcessor;
import com.projectgoth.fusion.rewardsystem.triggers.ReferredUserRewardedBaseTrigger;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;
import java.rmi.RemoteException;
import java.util.Set;
import javax.ejb.CreateException;
import javax.ejb.EJBException;

public abstract class ReferredUserRewardedBaseProcessor
extends RewardProgramProcessor {
    @RewardProgramParamName
    public static final String MAX_REFERRED_USER_MIG_LEVEL_PARAM_KEY = "maxRefdUsrMigLvl";
    @RewardProgramParamName
    public static final String MIN_REFERRED_USER_MIG_LEVEL_PARAM_KEY = "minRefdUsrMigLvl";
    @RewardProgramParamName
    public static final String REFERRED_USER_TYPES_PARAM_KEY = "refdUsrTypes";
    @RewardProgramParamName
    public static final String REFERRED_USER_TYPES_IS_WHITELIST_PARAM_KEY = "refdUsrTypesIsWhitelist";
    @RewardProgramParamName
    public static final String MAX_REFERRED_USER_REGISTRATION_DATE_PARAM_KEY = "maxRefdUsrRegDate";
    @RewardProgramParamName
    public static final String MIN_REFERRED_USER_REGISTRATION_DATE_PARAM_KEY = "minRefdUsrRegDate";
    @RewardProgramParamName
    public static final String REFERRED_USER_COUNTRY_IDS_PARAM_KEY = "refdUsrCtryIDs";
    @RewardProgramParamName
    public static final String REFERRED_USER_COUNTRY_IDS_IS_WHITELIST_PARAM_KEY = "refdUsrCtryIDsIsWhiteLst";
    @RewardProgramParamName
    public static final String REFERRED_USER_REWARDPROGRAM_IDS_PARAM_KEY = "refdUsrRwdPgmIDs";
    @RewardProgramParamName
    public static final String REFERRED_USER_REWARDPROGRAM_TYPES_PARAM_KEY = "refdUsrRwdPgmTypes";
    @RewardProgramParamName
    public static final String REFERRED_USER_REWARDPROGRAM_PARAMS_PARAM_KEY = "refdUsrRwdPgmPrms";
    @RewardProgramParamName
    public static final String REFERRED_USER_REWARDPROGRAM_IDS_IS_WHITELIST_PARAM_KEY = "refdUsrRwdPgmIDsIsWhiteLst";
    @RewardProgramParamName
    public static final String REFERRED_USER_REWARDPROGRAM_TYPES_IS_WHITELIST_PARAM_KEY = "refdUsrRwdPgmTypesIsWhiteLst";
    @RewardProgramParamName
    public static final String REFERRED_USER_REWARDPROGRAM_PARAMS_IS_WHITELIST_PARAM_KEY = "refdUsrRwdPgmPrmsIsWhiteLst";

    protected final boolean processInternal(RewardProgramData programData, RewardProgramTrigger trigger) throws Exception {
        if (!(trigger instanceof ReferredUserRewardedBaseTrigger)) {
            return false;
        }
        ReferredUserRewardedBaseTrigger rfdUsrRewardedTrigger = (ReferredUserRewardedBaseTrigger)trigger;
        if (ReferredUserRewardedBaseProcessor.passedCommonFilter(programData, rfdUsrRewardedTrigger)) {
            return this.processInternal(programData, rfdUsrRewardedTrigger);
        }
        return false;
    }

    private static boolean passedCommonFilter(RewardProgramData programData, ReferredUserRewardedBaseTrigger trigger) throws EJBException, RemoteException, CreateException {
        return ReferredUserRewardedBaseProcessor.passedReferredUserRegDateConstraint(programData, trigger.getReferredUserData()) && ReferredUserRewardedBaseProcessor.passedReferredUserCountryIDConstraint(programData, trigger.getReferredUserData()) && ReferredUserRewardedBaseProcessor.passedReferredUserRewardProgramConstraints(programData, trigger.getReferredUserRewardProgram()) && ReferredUserRewardedBaseProcessor.passedReferredUserMigLevels(programData, trigger.getReferredUserData());
    }

    private static boolean passedReferredUserCountryIDConstraint(RewardProgramData programData, UserData referredUserData) {
        String countryIDStr = referredUserData.countryID == null ? "null" : referredUserData.countryID.toString();
        return programData.matchesSetOfStringsConstraint(REFERRED_USER_COUNTRY_IDS_PARAM_KEY, REFERRED_USER_COUNTRY_IDS_IS_WHITELIST_PARAM_KEY, countryIDStr);
    }

    private static boolean passedReferredUserRegDateConstraint(RewardProgramData programData, UserData referredUserData) {
        return programData.matchesDateConstraint(MIN_REFERRED_USER_REGISTRATION_DATE_PARAM_KEY, true, referredUserData.dateRegistered) && programData.matchesDateConstraint(MAX_REFERRED_USER_REGISTRATION_DATE_PARAM_KEY, false, referredUserData.dateRegistered);
    }

    private static boolean passedReferredUserMigLevels(RewardProgramData programData, UserData referredUserData) throws EJBException, RemoteException, CreateException {
        if (programData.hasParameter(MIN_REFERRED_USER_MIG_LEVEL_PARAM_KEY) || programData.hasParameter(MAX_REFERRED_USER_MIG_LEVEL_PARAM_KEY)) {
            int minMigLevel = programData.getIntParam(MIN_REFERRED_USER_MIG_LEVEL_PARAM_KEY, -1);
            int maxMigLevel = programData.getIntParam(MAX_REFERRED_USER_MIG_LEVEL_PARAM_KEY, -1);
            if (minMigLevel != -1 || maxMigLevel != -1) {
                UserReputationScoreAndLevelData reputation = RewardCentre.getInstance().getUserReputationScoreAndLevelData(referredUserData);
                int referredUserMigLevel = reputation.level;
                return !(minMigLevel != -1 && minMigLevel > referredUserMigLevel || maxMigLevel != -1 && referredUserMigLevel > maxMigLevel);
            }
        }
        return true;
    }

    private static boolean passedReferredUserRewardProgramIDsConstraints(RewardProgramData programData, RewardProgramData referredRewardProgram) {
        if (programData.hasParameter(REFERRED_USER_REWARDPROGRAM_IDS_PARAM_KEY)) {
            String rewardProgramIDStr = referredRewardProgram.id.toString();
            return programData.matchesSetOfStringsConstraint(REFERRED_USER_REWARDPROGRAM_IDS_PARAM_KEY, REFERRED_USER_REWARDPROGRAM_IDS_IS_WHITELIST_PARAM_KEY, rewardProgramIDStr);
        }
        return true;
    }

    private static boolean passedReferredUserRewardProgramTypesConstraints(RewardProgramData programData, RewardProgramData referredRewardProgram) {
        if (programData.hasParameter(REFERRED_USER_REWARDPROGRAM_TYPES_PARAM_KEY)) {
            String rewardProgramTypeStr = String.valueOf(referredRewardProgram.type.value());
            return programData.matchesSetOfStringsConstraint(REFERRED_USER_REWARDPROGRAM_TYPES_PARAM_KEY, REFERRED_USER_REWARDPROGRAM_TYPES_IS_WHITELIST_PARAM_KEY, rewardProgramTypeStr);
        }
        return true;
    }

    private static boolean passedReferredUserRewardProgramParamsConstraints(RewardProgramData programData, RewardProgramData referredRewardProgram) {
        if (programData.hasParameter(REFERRED_USER_REWARDPROGRAM_PARAMS_PARAM_KEY)) {
            boolean isWhiteList = programData.getBoolParam(REFERRED_USER_REWARDPROGRAM_PARAMS_IS_WHITELIST_PARAM_KEY, true);
            Set<String> inclusionExclusionReferredProgramParams = programData.getStringSetParam(REFERRED_USER_REWARDPROGRAM_PARAMS_PARAM_KEY);
            boolean found = false;
            for (String param : inclusionExclusionReferredProgramParams) {
                if (!referredRewardProgram.hasParameter(param)) continue;
                found = true;
                return true;
            }
            return isWhiteList ? found : false == found;
        }
        return true;
    }

    private static boolean passedReferredUserRewardProgramConstraints(RewardProgramData programData, RewardProgramData referredRewardProgram) {
        return ReferredUserRewardedBaseProcessor.passedReferredUserRewardProgramIDsConstraints(programData, referredRewardProgram) && ReferredUserRewardedBaseProcessor.passedReferredUserRewardProgramTypesConstraints(programData, referredRewardProgram) && ReferredUserRewardedBaseProcessor.passedReferredUserRewardProgramParamsConstraints(programData, referredRewardProgram);
    }

    protected abstract boolean processInternal(RewardProgramData var1, ReferredUserRewardedBaseTrigger var2) throws Exception;
}

