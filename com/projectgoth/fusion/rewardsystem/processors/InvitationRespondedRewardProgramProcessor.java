/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.rewardsystem.processors;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.invitation.InvitationData;
import com.projectgoth.fusion.invitation.InvitationResponseData;
import com.projectgoth.fusion.rewardsystem.RewardProgramParamName;
import com.projectgoth.fusion.rewardsystem.processors.RewardProgramProcessor;
import com.projectgoth.fusion.rewardsystem.triggers.InvitationRespondedTrigger;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;
import java.util.Set;
import org.apache.log4j.Logger;

public class InvitationRespondedRewardProgramProcessor
extends RewardProgramProcessor {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(InvitationRespondedRewardProgramProcessor.class));
    @RewardProgramParamName
    public static final String USER_TYPE_PARAM_KEY = "userType";
    @RewardProgramParamName
    public static final String INVITATION_ID_PARAM_KEY = "invitationId";
    @RewardProgramParamName
    public static final String INVITATION_TYPE_PARAM_KEY = "invitationType";
    @RewardProgramParamName
    public static final String INVITATION_CHANNEL_PARAM_KEY = "invitationChannel";
    @RewardProgramParamName
    public static final String INVITATION_RESPONSE_TYPE_PARAM_KEY = "invitationResponseType";
    @RewardProgramParamName
    public static final String COUNTRY_IDS_KEY = "countryIDs";

    protected boolean processInternal(RewardProgramData programData, RewardProgramTrigger trigger) {
        if (!(trigger instanceof InvitationRespondedTrigger)) {
            return false;
        }
        InvitationRespondedTrigger irTrigger = (InvitationRespondedTrigger)trigger;
        InvitationData invitationData = irTrigger.getInvitationData();
        InvitationResponseData invitationResponseData = irTrigger.getInvitationResponseData();
        boolean passed = false;
        int userType = programData.getIntParam(USER_TYPE_PARAM_KEY, -1);
        boolean bl = passed = userType == -1 || UserType.matches(userType, irTrigger.isInviter());
        if (log.isDebugEnabled()) {
            log.debug((Object)("param user type: " + userType + ", value isInviter: " + irTrigger.isInviter() + ", passed = " + passed));
        }
        if (!passed) {
            return false;
        }
        int invitationId = programData.getIntParam(INVITATION_ID_PARAM_KEY, -1);
        boolean bl2 = passed = invitationId == -1 || invitationId == invitationData.id;
        if (log.isDebugEnabled()) {
            log.debug((Object)("param invitationId: " + invitationId + ", value invitationId: " + invitationData.id + ", passed = " + passed));
        }
        if (!passed) {
            return false;
        }
        int invitationType = programData.getIntParam(INVITATION_TYPE_PARAM_KEY, -1);
        boolean bl3 = passed = invitationType == -1 || invitationType == invitationData.type.getTypeCode();
        if (log.isDebugEnabled()) {
            log.debug((Object)("param invitationType: " + invitationType + ", value invitationType: " + invitationData.type + ", passed = " + passed));
        }
        if (!passed) {
            return false;
        }
        int invitationChannel = programData.getIntParam(INVITATION_CHANNEL_PARAM_KEY, -1);
        boolean bl4 = passed = invitationChannel == -1 || invitationChannel == invitationData.channel.getTypeCode();
        if (log.isDebugEnabled()) {
            log.debug((Object)("param invitationChannel: " + invitationChannel + ", value invitationChannel: " + invitationData.channel.getTypeCode() + ", passed = " + passed));
        }
        if (!passed) {
            return false;
        }
        int invitationResponseType = programData.getIntParam(INVITATION_RESPONSE_TYPE_PARAM_KEY, -1);
        boolean bl5 = passed = invitationResponseType == -1 || invitationResponseType == invitationResponseData.responseType.getTypeCode();
        if (log.isDebugEnabled()) {
            log.debug((Object)("param invitationResponseType: " + invitationResponseType + ", value invitationResponseType: " + invitationResponseData.responseType.getTypeCode() + ", passed = " + passed));
        }
        if (!passed) {
            return false;
        }
        passed = InvitationRespondedRewardProgramProcessor.countryMatches(programData, irTrigger.userData.countryID);
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

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum UserType {
        INVITER(1),
        INVITEE(2);

        private int value;

        private UserType(int value) {
            this.value = value;
        }

        public boolean matches(int paramValue) {
            return (paramValue & this.value) == this.value;
        }

        public static boolean matches(int paramValue, boolean isInviter) {
            return (isInviter ? INVITER : INVITEE).matches(paramValue);
        }

        public int getValue() {
            return this.value;
        }
    }
}

