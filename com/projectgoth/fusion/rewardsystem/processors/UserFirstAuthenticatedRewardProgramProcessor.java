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
import com.projectgoth.fusion.rewardsystem.triggers.UserFirstAuthenticatedTrigger;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.apache.log4j.Logger;

public class UserFirstAuthenticatedRewardProgramProcessor
extends RewardProgramProcessor {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(UserFirstAuthenticatedRewardProgramProcessor.class));
    @RewardProgramParamName
    public static final String CAMPAIGN_REGEX_PARAM_KEY = "campaignRegex";
    @RewardProgramParamName
    public static final String REG_TYPE_REGEX_KEY = "regTypeRegex";
    @RewardProgramParamName
    public static final String COUNTRY_IDS_KEY = "countryIDs";
    @RewardProgramParamName
    public static final String REG_IP_ADDRESS_REGEX_KEY = "regIpAddrRegex";
    @RewardProgramParamName
    public static final String REG_EMAIL_ADDRESS_REGEX_KEY = "regEmailAddrRegex";
    @RewardProgramParamName
    public static final String REG_USER_AGENT_REGEX_KEY = "regUserAgentRegex";

    protected boolean processInternal(RewardProgramData programData, RewardProgramTrigger trigger) {
        boolean processInternalResult;
        if (!(trigger instanceof UserFirstAuthenticatedTrigger)) {
            return false;
        }
        UserFirstAuthenticatedTrigger ufaTrigger = (UserFirstAuthenticatedTrigger)trigger;
        boolean bl = processInternalResult = UserFirstAuthenticatedRewardProgramProcessor.countryMatches(programData, ufaTrigger.userData.countryID) && UserFirstAuthenticatedRewardProgramProcessor.matchesRegExKey(programData, REG_TYPE_REGEX_KEY, ufaTrigger.regContextData.registrationType) && UserFirstAuthenticatedRewardProgramProcessor.matchesRegExKey(programData, CAMPAIGN_REGEX_PARAM_KEY, ufaTrigger.regContextData.campaign) && UserFirstAuthenticatedRewardProgramProcessor.matchesRegExKey(programData, REG_IP_ADDRESS_REGEX_KEY, ufaTrigger.regContextData.ipAddress) && UserFirstAuthenticatedRewardProgramProcessor.matchesRegExKey(programData, REG_EMAIL_ADDRESS_REGEX_KEY, ufaTrigger.regContextData.email) && UserFirstAuthenticatedRewardProgramProcessor.matchesRegExKey(programData, REG_USER_AGENT_REGEX_KEY, ufaTrigger.regContextData.userAgent);
        if (log.isDebugEnabled()) {
            log.debug((Object)(UserFirstAuthenticatedRewardProgramProcessor.class.getName() + " Reward Program ID:[" + programData.id + "] passed:[" + processInternalResult + "]"));
        }
        return processInternalResult;
    }

    private static boolean matchesRegExKey(RewardProgramData programData, String regexKey, String input) throws PatternSyntaxException {
        String regex = programData.getStringParam(regexKey, null);
        if (log.isDebugEnabled()) {
            log.debug((Object)String.format("RewardProgramID:[%s] regexkey:[%s] regex:[%s] input:[%s]", programData.id, regexKey, regex, input));
        }
        boolean passed = false;
        if (regex == null) {
            passed = true;
        } else if (StringUtil.isBlank(regex)) {
            passed = input == null ? false : StringUtil.isBlank(input);
        } else if (input == null) {
            passed = false;
        } else {
            try {
                passed = Pattern.matches(regex, input);
            }
            catch (PatternSyntaxException e) {
                log.error((Object)String.format("Incorrect regex RewardProgramID:[%s] regexkey:[%s] regex:[%s] input:[%s]", programData.id, regexKey, regex, input), (Throwable)e);
                passed = false;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)String.format("RewardProgramID:[%s] regexkey:[%s] regex:[%s] input:[%s] passed:[%s]", programData.id, regexKey, regex, input, passed));
        }
        return passed;
    }

    private static boolean countryMatches(RewardProgramData programData, Integer countryId) {
        if (programData.hasParameter(COUNTRY_IDS_KEY)) {
            String countryIdStr = countryId == null ? "null" : countryId.toString();
            Set<String> eligibleCountries = programData.getStringSetParam(COUNTRY_IDS_KEY);
            if (log.isDebugEnabled()) {
                log.debug((Object)String.format("RewardProgramID:[%s] key:[%s] countries:[%s] countryIdStr[%s]", programData.id, COUNTRY_IDS_KEY, eligibleCountries, countryIdStr));
            }
            boolean passed = eligibleCountries.contains(countryIdStr);
            if (log.isDebugEnabled()) {
                log.debug((Object)String.format("RewardProgramID:[%s] key:[%s] countries:[%s] countryIdStr[%s] passed:[%s]", programData.id, COUNTRY_IDS_KEY, eligibleCountries, countryIdStr, passed));
            }
            return passed;
        }
        return true;
    }
}

