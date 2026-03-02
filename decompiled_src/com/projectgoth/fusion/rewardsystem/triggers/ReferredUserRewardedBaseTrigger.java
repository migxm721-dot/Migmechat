/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.projectgoth.leto.common.event.referreduserrewarded.ReferredUserRewardedEvent
 *  com.projectgoth.leto.common.user.UserDetails
 */
package com.projectgoth.fusion.rewardsystem.triggers;

import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;
import com.projectgoth.leto.common.event.referreduserrewarded.ReferredUserRewardedEvent;
import com.projectgoth.leto.common.user.UserDetails;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class ReferredUserRewardedBaseTrigger
extends RewardProgramTrigger
implements ReferredUserRewardedEvent {
    public static final String TMPLT_DATA_KEY_TRIGGER_REFERRED_USER_REWARDED_USER_ID = "trigger.referredUserRewarded.userid";
    public static final String TMPLT_DATA_KEY_TRIGGER_REFERRED_USER_REWARDED_USER_NAME = "trigger.referredUserRewarded.username";
    public static final String TMPLT_DATA_KEY_TRIGGER_REFERRED_USER_REWARDED_DISPLAY_NAME = "trigger.referredUserRewarded.displayName";
    public static final String TMPLT_DATA_KEY_TRIGGER_REFERRED_USER_REWARDED_DISPLAY_PICTURE = "trigger.referredUserRewarded.displayPicture";
    public static final String TMPLT_DATA_KEY_TRIGGER_REFERRED_USER_REWARDED_AVATAR = "trigger.referredUserRewarded.avatar";
    public static final String TMPLT_DATA_KEY_TRIGGER_REFERRED_USER_REWARDED_FULLBODY_AVATAR = "trigger.referredUserRewarded.fullbodyAvatar";
    public static final String TMPLT_DATA_KEY_TRIGGER_REFERRED_USER_REWARDED_LANGUAGE = "trigger.referredUserRewarded.language";
    public static final String TMPLT_DATA_KEY_TRIGGER_REFERRED_USER_REWARDED_MOBILEPHONE = "trigger.referredUserRewarded.mobilePhone";
    public static final String TMPLT_DATA_KEY_TRIGGER_REFERRED_USER_REWARDED_EMAILADDRESS = "trigger.referredUserRewarded.emailAddress";
    private final UserData referredUserData;
    private final RewardProgramData referredUserRewardProgram;
    private final Date rewardedTime;

    protected ReferredUserRewardedBaseTrigger(RewardProgramData.TypeEnum programType, UserData referrerUserData, UserData referredUserData, RewardProgramData referredUserRewardProgram) {
        super(programType, referrerUserData);
        this.referredUserData = referredUserData;
        this.referredUserRewardProgram = referredUserRewardProgram;
        this.rewardedTime = new Timestamp(System.currentTimeMillis());
    }

    public UserData getReferrerUserData() {
        return this.userData;
    }

    public UserData getReferredUserData() {
        return this.referredUserData;
    }

    public RewardProgramData getReferredUserRewardProgram() {
        return this.referredUserRewardProgram;
    }

    @Override
    protected final void fillTemplateDataMap(Map<String, String> templateContextMap) {
        if (this.referredUserData != null) {
            if (this.referredUserData.userID != null) {
                templateContextMap.put(TMPLT_DATA_KEY_TRIGGER_REFERRED_USER_REWARDED_USER_ID, this.referredUserData.userID.toString());
            }
            templateContextMap.put(TMPLT_DATA_KEY_TRIGGER_REFERRED_USER_REWARDED_USER_NAME, this.referredUserData.username);
            templateContextMap.put(TMPLT_DATA_KEY_TRIGGER_REFERRED_USER_REWARDED_DISPLAY_NAME, this.referredUserData.displayName);
            templateContextMap.put(TMPLT_DATA_KEY_TRIGGER_REFERRED_USER_REWARDED_DISPLAY_PICTURE, this.referredUserData.displayPicture);
            templateContextMap.put(TMPLT_DATA_KEY_TRIGGER_REFERRED_USER_REWARDED_AVATAR, this.referredUserData.avatar);
            templateContextMap.put(TMPLT_DATA_KEY_TRIGGER_REFERRED_USER_REWARDED_FULLBODY_AVATAR, this.referredUserData.fullbodyAvatar);
            templateContextMap.put(TMPLT_DATA_KEY_TRIGGER_REFERRED_USER_REWARDED_LANGUAGE, this.referredUserData.language);
            String emailAddress = StringUtil.isBlank(this.referredUserData.emailAddress) ? "N/A" : this.referredUserData.emailAddress;
            templateContextMap.put(TMPLT_DATA_KEY_TRIGGER_REFERRED_USER_REWARDED_EMAILADDRESS, emailAddress);
            String mobilePhone = StringUtil.isBlank(this.referredUserData.mobilePhone) ? "N/A" : this.referredUserData.mobilePhone;
            templateContextMap.put(TMPLT_DATA_KEY_TRIGGER_REFERRED_USER_REWARDED_MOBILEPHONE, mobilePhone);
        }
        this.fillTemplateDataMapForReferredUserRewardedTrigger(templateContextMap);
    }

    protected final void fillTemplateDataMapForReferredUserRewardedTrigger(Map<String, String> templateContextMap) {
    }

    public UserDetails getReferredUser() {
        return this.getReferredUserData();
    }

    public Date getRewardedTime() {
        return this.rewardedTime;
    }

    public long getRewardedProgramID() {
        return this.referredUserRewardProgram != null ? (long)this.referredUserRewardProgram.id.intValue() : -1L;
    }

    public int getRewardedProgramTriggerType() {
        return this.referredUserRewardProgram != null ? this.referredUserRewardProgram.type.getId() : -1;
    }
}

