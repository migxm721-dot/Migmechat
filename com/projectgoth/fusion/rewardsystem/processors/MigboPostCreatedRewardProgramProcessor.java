/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.rewardsystem.processors;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.Enums;
import com.projectgoth.fusion.common.MigboEnums;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.rewardsystem.RewardProgramParamName;
import com.projectgoth.fusion.rewardsystem.processors.RewardProgramProcessor;
import com.projectgoth.fusion.rewardsystem.triggers.MigboPostCreatedTrigger;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;
import org.apache.log4j.Logger;

public class MigboPostCreatedRewardProgramProcessor
extends RewardProgramProcessor {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(MigboPostCreatedRewardProgramProcessor.class));
    @RewardProgramParamName
    public static final String ORIGINALITY_PARAM_KEY = "originality";
    @RewardProgramParamName
    public static final String HASHTAG_PARAM_KEY = "hashtag";
    @RewardProgramParamName
    public static final String APPLICATION_PARAM_KEY = "application";
    @RewardProgramParamName
    public static final String TYPE_PARAM_KEY = "type";
    @RewardProgramParamName
    public static final String PARENT_POSTID_PARAM_KEY = "parentPostID";
    @RewardProgramParamName
    public static final String SHARE_THIRD_PARTY_KEY = "shareToThirdParty";

    protected boolean processInternal(RewardProgramData programData, RewardProgramTrigger trigger) {
        if (!(trigger instanceof MigboPostCreatedTrigger)) {
            return false;
        }
        MigboPostCreatedTrigger bgwTrigger = (MigboPostCreatedTrigger)trigger;
        MigboEnums.MigboPostOriginalityEnum originality = MigboEnums.MigboPostOriginalityEnum.fromType(programData.getIntParam(ORIGINALITY_PARAM_KEY, -1));
        String hashtag = programData.getStringParam(HASHTAG_PARAM_KEY, "");
        MigboEnums.PostApplicationEnum application = MigboEnums.PostApplicationEnum.fromValue(programData.getIntParam(APPLICATION_PARAM_KEY, 0));
        MigboEnums.MigboPostTypeEnum postType = MigboEnums.MigboPostTypeEnum.fromValue(programData.getIntParam(TYPE_PARAM_KEY, -1));
        Enums.ThirdPartyEnum shareToThirdParty = Enums.ThirdPartyEnum.fromValue(programData.getIntParam(SHARE_THIRD_PARTY_KEY, -1));
        String parentPostID = programData.getStringParam(PARENT_POSTID_PARAM_KEY, "");
        if (log.isDebugEnabled()) {
            log.debug((Object)("userID[" + trigger.userData.userID + "]  originality in Trigger[" + (Object)((Object)bgwTrigger.postOriginality) + "] required[" + (Object)((Object)originality) + "][" + programData.getIntParam(ORIGINALITY_PARAM_KEY, -1) + "]"));
        }
        boolean result = true;
        if (originality != null) {
            result &= originality == bgwTrigger.postOriginality;
        }
        if (!StringUtil.isBlank(hashtag)) {
            if (bgwTrigger.hashtags != null) {
                boolean containsRequiredHashtag = false;
                for (String s : bgwTrigger.hashtags) {
                    if (!s.equalsIgnoreCase(hashtag)) continue;
                    containsRequiredHashtag = true;
                    break;
                }
                result &= containsRequiredHashtag;
            } else {
                result = false;
            }
        }
        if (application != null) {
            result &= application == bgwTrigger.application;
        }
        if (postType != null) {
            result &= postType == bgwTrigger.postType;
        }
        if (shareToThirdParty != null) {
            result &= bgwTrigger.shareToThirdParty.contains((Object)shareToThirdParty);
        }
        if (!StringUtil.isBlank(parentPostID)) {
            result &= bgwTrigger.parentPostID != null && bgwTrigger.parentPostID.equals(parentPostID);
        }
        return result;
    }
}

