/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.rewardsystem.processors;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.data.RegistrationContextData;
import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.rewardsystem.RewardProgramParamName;
import com.projectgoth.fusion.rewardsystem.processors.RewardProgramProcessor;
import com.projectgoth.fusion.rewardsystem.triggers.ExternalEmailVerifiedTrigger;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;
import org.apache.log4j.Logger;

public class ExternalEmailVerifiedRewardProgramProcessor
extends RewardProgramProcessor {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ExternalEmailVerifiedRewardProgramProcessor.class));
    @RewardProgramParamName
    public static final String MAX_USER_REGISTRATION_DATE_PARAM_KEY = "maxUserRegDate";
    @RewardProgramParamName
    public static final String MIN_USER_REGISTRATION_DATE_PARAM_KEY = "minUserRegDate";
    @RewardProgramParamName
    public static final String USER_REG_TYPE_REGEX_PARAM_KEY = "userRegTypeRegex";
    @RewardProgramParamName
    public static final String COUNTRY_IDS_PARAM_KEY = "countryIDs";
    @RewardProgramParamName
    public static final String COUNTRY_IDS_IS_WHITELIST_PARAM_KEY = "countryIDsIsWhitelist";
    @RewardProgramParamName
    public static final String VERIFIED_EMAIL_ADDRESS_REGEX_PARAM_KEY = "verifiedEmailAddrRegex";

    protected boolean processInternal(RewardProgramData programData, RewardProgramTrigger trigger) {
        boolean processInternalResult;
        if (!(trigger instanceof ExternalEmailVerifiedTrigger)) {
            return false;
        }
        ExternalEmailVerifiedTrigger externalEmailVerifiedTrigger = (ExternalEmailVerifiedTrigger)trigger;
        UserData userData = externalEmailVerifiedTrigger.userData;
        RegistrationContextData regContextData = externalEmailVerifiedTrigger.getRegContextData();
        String countryIDStr = userData.countryID == null ? "null" : userData.countryID.toString();
        boolean bl = processInternalResult = programData.matchesSetOfStringsConstraint(COUNTRY_IDS_PARAM_KEY, COUNTRY_IDS_IS_WHITELIST_PARAM_KEY, countryIDStr) && programData.matchesDateConstraint(MIN_USER_REGISTRATION_DATE_PARAM_KEY, true, userData.dateRegistered) && programData.matchesDateConstraint(MAX_USER_REGISTRATION_DATE_PARAM_KEY, false, userData.dateRegistered) && programData.matchesRegExKey(USER_REG_TYPE_REGEX_PARAM_KEY, regContextData.registrationType) && programData.matchesRegExKey(VERIFIED_EMAIL_ADDRESS_REGEX_PARAM_KEY, externalEmailVerifiedTrigger.getVerifiedEmailAddress());
        if (log.isDebugEnabled()) {
            log.debug((Object)(ExternalEmailVerifiedRewardProgramProcessor.class.getName() + " Reward Program ID:[" + programData.id + "] passed:[" + processInternalResult + "]"));
        }
        return processInternalResult;
    }
}

