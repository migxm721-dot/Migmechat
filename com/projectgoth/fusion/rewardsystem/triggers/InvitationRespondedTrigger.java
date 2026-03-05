/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.projectgoth.leto.common.event.CampaignParticipation
 *  com.projectgoth.leto.common.event.invites.InvitationRespondedEvent
 *  com.projectgoth.leto.common.event.invites.InviteActivity
 *  com.projectgoth.leto.common.event.invites.InviteChannel
 *  com.projectgoth.leto.common.event.invites.InviteeResponse
 *  com.projectgoth.leto.common.user.UserDetails
 */
package com.projectgoth.fusion.rewardsystem.triggers;

import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.invitation.InvitationData;
import com.projectgoth.fusion.invitation.InvitationResponseData;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTriggerWithParticipatedCampaigns;
import com.projectgoth.leto.common.event.CampaignParticipation;
import com.projectgoth.leto.common.event.invites.InvitationRespondedEvent;
import com.projectgoth.leto.common.event.invites.InviteActivity;
import com.projectgoth.leto.common.event.invites.InviteChannel;
import com.projectgoth.leto.common.event.invites.InviteeResponse;
import com.projectgoth.leto.common.user.UserDetails;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class InvitationRespondedTrigger
extends RewardProgramTriggerWithParticipatedCampaigns
implements InvitationRespondedEvent {
    public static final String TMPLT_DATA_KEY_INVITATION_RESPONDED_TRIGGER_COUNTER_PARTY_LANGUAGE = "InvitationRespondedTrigger.counterPartyUserData.language";
    public static final String TMPLT_DATA_KEY_INVITATION_RESPONDED_TRIGGER_COUNTER_PARTY_FULL_BODY_AVATAR = "InvitationRespondedTrigger.counterPartyUserData.fullbodyAvatar";
    public static final String TMPLT_DATA_KEY_INVITATION_RESPONDED_TRIGGER_COUNTER_PARTY_AVATAR = "InvitationRespondedTrigger.counterPartyUserData.avatar";
    public static final String TMPLT_DATA_KEY_INVITATION_RESPONDED_TRIGGER_COUNTER_PARTY_DISPLAY_PICTURE = "InvitationRespondedTrigger.counterPartyUserData.displayPicture";
    public static final String TMPLT_DATA_KEY_INVITATION_RESPONDED_TRIGGER_COUNTER_PARTY_DISPLAY_NAME = "InvitationRespondedTrigger.counterPartyUserData.displayName";
    public static final String TMPLT_DATA_KEY_INVITATION_RESPONDED_TRIGGER_COUNTER_PARTY_USER_NAME = "InvitationRespondedTrigger.counterPartyUserData.username";
    public static final String TMPLT_DATA_KEY_INVITATION_RESPONDED_TRIGGER_COUNTER_PARTY_USER_ID = "InvitationRespondedTrigger.counterPartyUserData.id";
    public static final String TMPLT_DATA_KEY_INVITATION_RESPONDED_TRIGGER_COUNTER_PARTY_EMAILADDRESS = "InvitationRespondedTrigger.counterPartyUserData.emailAddress";
    public static final String TMPLT_DATA_KEY_INVITATION_RESPONDED_TRIGGER_COUNTER_PARTY_MOBILEADDRESS = "InvitationRespondedTrigger.counterPartyUserData.mobileAddress";
    private final boolean isInviter;
    private final InvitationData invitationData;
    private final InvitationResponseData invitationResponseData;
    private final UserData counterPartyUserData;
    private final Map<Integer, CampaignParticipation> participatedCampaigns = new HashMap<Integer, CampaignParticipation>();

    public InvitationRespondedTrigger(UserData userData, boolean isInviter, InvitationData invitationData, InvitationResponseData invitationResponseData, UserData counterPartyUserData) {
        super(RewardProgramData.TypeEnum.INVITATION_RESPONDED, userData);
        this.isInviter = isInviter;
        this.invitationData = invitationData;
        this.invitationResponseData = invitationResponseData;
        this.counterPartyUserData = counterPartyUserData;
        this.quantityDelta = 1;
        this.amountDelta = 0.0;
    }

    public UserData getCounterPartyUserData() {
        return this.counterPartyUserData;
    }

    public boolean isInviter() {
        return this.isInviter;
    }

    public InvitationData getInvitationData() {
        return this.invitationData;
    }

    public InvitationResponseData getInvitationResponseData() {
        return this.invitationResponseData;
    }

    @Override
    protected void fillTemplateDataMap(Map<String, String> templateContextMap) {
        if (this.counterPartyUserData.userID != null) {
            templateContextMap.put(TMPLT_DATA_KEY_INVITATION_RESPONDED_TRIGGER_COUNTER_PARTY_USER_ID, this.counterPartyUserData.userID.toString());
        }
        templateContextMap.put(TMPLT_DATA_KEY_INVITATION_RESPONDED_TRIGGER_COUNTER_PARTY_USER_NAME, this.counterPartyUserData.username);
        templateContextMap.put(TMPLT_DATA_KEY_INVITATION_RESPONDED_TRIGGER_COUNTER_PARTY_DISPLAY_NAME, this.counterPartyUserData.displayName);
        templateContextMap.put(TMPLT_DATA_KEY_INVITATION_RESPONDED_TRIGGER_COUNTER_PARTY_DISPLAY_PICTURE, this.counterPartyUserData.displayPicture);
        templateContextMap.put(TMPLT_DATA_KEY_INVITATION_RESPONDED_TRIGGER_COUNTER_PARTY_AVATAR, this.counterPartyUserData.avatar);
        templateContextMap.put(TMPLT_DATA_KEY_INVITATION_RESPONDED_TRIGGER_COUNTER_PARTY_FULL_BODY_AVATAR, this.counterPartyUserData.fullbodyAvatar);
        templateContextMap.put(TMPLT_DATA_KEY_INVITATION_RESPONDED_TRIGGER_COUNTER_PARTY_LANGUAGE, this.counterPartyUserData.language);
        String emailAddress = StringUtil.isBlank(this.counterPartyUserData.emailAddress) ? "N/A" : this.counterPartyUserData.emailAddress;
        templateContextMap.put(TMPLT_DATA_KEY_INVITATION_RESPONDED_TRIGGER_COUNTER_PARTY_EMAILADDRESS, emailAddress);
        String mobilePhone = StringUtil.isBlank(this.counterPartyUserData.mobilePhone) ? "N/A" : this.counterPartyUserData.mobilePhone;
        templateContextMap.put(TMPLT_DATA_KEY_INVITATION_RESPONDED_TRIGGER_COUNTER_PARTY_MOBILEADDRESS, mobilePhone);
    }

    public boolean isSubjectUserAsInviter() {
        return this.isInviter();
    }

    public UserDetails getCounterPartyUser() {
        return this.getCounterPartyUserData();
    }

    public InviteActivity getActivityType() {
        InvitationData invitationData = this.getInvitationData();
        return invitationData != null ? invitationData.type.toInviteActivityType() : null;
    }

    public InviteChannel getChannelType() {
        InvitationData invitationData = this.getInvitationData();
        return invitationData != null ? invitationData.channel.toInviteChannelType() : null;
    }

    public Date getInvitationCreateTime() {
        InvitationData invitationData = this.getInvitationData();
        return invitationData != null ? invitationData.createdTime : null;
    }

    public Date getLastInviteeResponseTime() {
        InvitationResponseData invitationResponseData = this.getInvitationResponseData();
        return invitationResponseData != null ? invitationResponseData.responseTime : null;
    }

    public InviteeResponse getLastInviteeResponseType() {
        InvitationResponseData invitationResponseData = this.getInvitationResponseData();
        return invitationResponseData != null ? invitationResponseData.responseType.toInviteeResponseType() : null;
    }

    public String getDestinationRef() {
        InvitationData invitationData = this.getInvitationData();
        return invitationData != null ? invitationData.destination : null;
    }
}

