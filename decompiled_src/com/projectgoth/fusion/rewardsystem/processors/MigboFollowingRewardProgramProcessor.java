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
import com.projectgoth.fusion.rewardsystem.triggers.MigboFollowingEventTrigger;
import com.projectgoth.fusion.rewardsystem.triggers.RelationshipEventTrigger;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;
import org.apache.log4j.Logger;

public class MigboFollowingRewardProgramProcessor
extends RewardProgramProcessor {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(MigboFollowingRewardProgramProcessor.class));
    @RewardProgramParamName
    public static final String FOLLOWED_USERID_PARAM_KEY = "followedUserID";
    @RewardProgramParamName
    public static final String EVENT_TYPE_PARAM_KEY = "eventType";
    @RewardProgramParamName
    public static final String IS_AUTO_FOLLOW = "isAutoFollow";
    @RewardProgramParamName
    public static final String OTHER_USER_VERIFIED_ACCOUNT_STATUS = "otherUsrVfydAccStats";
    @RewardProgramParamName
    public static final String OTHER_USER_VERIFIED_ACCOUNT_ENTITY_TYPE = "otherUsrVfydAccEntityType";

    protected boolean processInternal(RewardProgramData programData, RewardProgramTrigger trigger) {
        if (!(trigger instanceof MigboFollowingEventTrigger)) {
            return false;
        }
        MigboFollowingEventTrigger mfeTrigger = (MigboFollowingEventTrigger)trigger;
        RelationshipEventTrigger.RelationshipEventTypeEnum expectedEventType = RelationshipEventTrigger.RelationshipEventTypeEnum.fromValue(programData.getIntParam(EVENT_TYPE_PARAM_KEY, -1));
        int expectedFollowedUserID = programData.getIntParam(FOLLOWED_USERID_PARAM_KEY, -1);
        if (log.isDebugEnabled()) {
            log.debug((Object)("expected relationshipEventType [" + expectedEventType + "] expected followedUserID[" + expectedFollowedUserID + "] triggeredRelationshipEventType[" + mfeTrigger.getRelationshipEvent() + "] triggeredFollowedUserID[" + mfeTrigger.getFollowedUser() + "]"));
        }
        boolean result = true;
        if (expectedEventType != null) {
            result &= expectedEventType == mfeTrigger.getRelationshipEvent();
        }
        if (expectedFollowedUserID != -1) {
            result &= expectedFollowedUserID == mfeTrigger.getFollowedUser().userID;
        }
        if (programData.hasParameter(IS_AUTO_FOLLOW)) {
            boolean expectedAutoFollowFlag = programData.getBoolParam(IS_AUTO_FOLLOW, false);
            result &= expectedAutoFollowFlag == mfeTrigger.isAutoFollow();
        }
        return result &= programData.matchesVerifiedAccountStatusConstraint(OTHER_USER_VERIFIED_ACCOUNT_STATUS, mfeTrigger.getFollowedUser().accountVerified) && programData.matchesVerifiedAccountTypeConstraint(OTHER_USER_VERIFIED_ACCOUNT_ENTITY_TYPE, mfeTrigger.getFollowedUser().accountType);
    }
}

