/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.projectgoth.leto.common.event.gifting.VGReceivedUserEvent
 *  com.projectgoth.leto.common.user.UserDetails
 */
package com.projectgoth.fusion.rewardsystem.triggers;

import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;
import com.projectgoth.leto.common.event.gifting.VGReceivedUserEvent;
import com.projectgoth.leto.common.user.UserDetails;
import java.text.SimpleDateFormat;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class VGReceivedTrigger
extends RewardProgramTrigger
implements VGReceivedUserEvent {
    public static final String TMPLT_DATA_KEY_VG_RECEIVED_TRIGGER_VIRTUAL_GIFT_ID = "VGReceivedTrigger.virtualGiftID";
    public static final String TMPLT_DATA_KEY_VG_RECEIVED_TRIGGER_VIRTUAL_GIFT_RECEIVED_ID = "VGReceivedTrigger.virtualGiftReceivedID";
    public static final String TMPLT_DATA_KEY_VG_RECEIVED_TRIGGER_VIRTUAL_GIFT_IMAGE = "VGReceivedTrigger.image";
    public static final String TMPLT_DATA_KEY_VG_RECEIVED_TRIGGER_VIRTUAL_MESSAGE = "VGReceivedTrigger.message";
    public static final String TMPLT_DATA_KEY_VG_RECEIVED_TRIGGER_VIRTUAL_MESSAGE_STYLE_HIDDEN = "VGReceivedTrigger.messageStyleHidden";
    public static final String TMPLT_DATA_KEY_VG_RECEIVED_TRIGGER_SENDER_USER_NAME = "VGReceivedTrigger.senderUserName";
    public static final String TMPLT_DATA_KEY_VG_RECEIVED_TRIGGER_SENDER_USER_ID = "VGReceivedTrigger.senderUserID";
    public static final String TMPLT_DATA_KEY_VG_RECEIVED_TRIGGER_SENDER_DISPLAY_NAME = "VGReceivedTrigger.senderDisplayName";
    public static final String TMPLT_DATA_KEY_VG_RECEIVED_TRIGGER_SENDER_DISPLAY_PICTURE = "VGReceivedTrigger.senderDisplayPicture";
    public static final String TMPLT_DATA_KEY_VG_RECEIVED_TRIGGER_SENDER_AVATAR = "VGReceivedTrigger.senderAvatar";
    public static final String TMPLT_DATA_KEY_VG_RECEIVED_TRIGGER_SENDER_FULLBODY_AVATAR = "VGReceivedTrigger.senderFullbodyAvatar";
    public static final String TMPLT_DATA_KEY_VG_RECEIVED_TRIGGER_SENDER_LANGUAGE = "VGReceivedTrigger.senderLanguage";
    public static final String TMPLT_DATA_KEY_VG_RECEIVED_TRIGGER_SENDER_EMAILADDRESS = "VGReceivedTrigger.senderEmailAddress";
    public static final String TMPLT_DATA_KEY_VG_RECEIVED_TRIGGER_SENDER_MOBILEPHONE = "VGReceivedTrigger.senderMobilePhone";
    public static final String TMPLT_DATA_KEY_VG_RECEIVED_TRIGGER_VIRTUAL_GIFT_NAME = "VGReceivedTrigger.virtualGiftName";
    public static final String TMPLT_DATA_KEY_VG_RECEIVED_TRIGGER_VIRTUAL_TIMESTAMP = "VGReceivedTrigger.timestamp";
    public static final String TMPLT_DATA_KEY_VG_RECEIVED_TRIGGER_VIRTUAL_SHOW_MESSAGE = "VGReceivedTrigger.showMessage";
    private static ThreadLocal<SimpleDateFormat> dataFormatHolder = new ThreadLocal<SimpleDateFormat>(){

        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(SystemProperty.get(SystemPropertyEntities.GiftSettings.GIFT_RECEIVE_TIMESTAMP_FORMAT));
        }
    };
    public int virtualGiftID;
    public UserData senderUserData;
    private final int virtualGiftReceivedID;
    public boolean fromSenderInventory;
    public String virtualGiftName;
    public String message;
    public String image;

    public VGReceivedTrigger(UserData userData, int virtualGiftReceivedID) {
        super(RewardProgramData.TypeEnum.VIRTUAL_GIFT_RECEIVED, userData);
        this.virtualGiftReceivedID = virtualGiftReceivedID;
    }

    public int getVirtualGiftReceivedId() {
        return this.virtualGiftReceivedID;
    }

    @Override
    protected void fillTemplateDataMap(Map<String, String> templateContextMap) {
        if (this.senderUserData != null) {
            if (this.senderUserData.userID != null) {
                templateContextMap.put(TMPLT_DATA_KEY_VG_RECEIVED_TRIGGER_SENDER_USER_ID, this.senderUserData.userID.toString());
            }
            templateContextMap.put(TMPLT_DATA_KEY_VG_RECEIVED_TRIGGER_SENDER_USER_NAME, this.senderUserData.username);
            templateContextMap.put(TMPLT_DATA_KEY_VG_RECEIVED_TRIGGER_SENDER_DISPLAY_NAME, this.senderUserData.displayName);
            templateContextMap.put(TMPLT_DATA_KEY_VG_RECEIVED_TRIGGER_SENDER_DISPLAY_PICTURE, this.senderUserData.displayPicture);
            templateContextMap.put(TMPLT_DATA_KEY_VG_RECEIVED_TRIGGER_SENDER_AVATAR, this.senderUserData.avatar);
            templateContextMap.put(TMPLT_DATA_KEY_VG_RECEIVED_TRIGGER_SENDER_FULLBODY_AVATAR, this.senderUserData.fullbodyAvatar);
            templateContextMap.put(TMPLT_DATA_KEY_VG_RECEIVED_TRIGGER_SENDER_LANGUAGE, this.senderUserData.language);
            String emailAddress = StringUtil.isBlank(this.senderUserData.emailAddress) ? "N/A" : this.senderUserData.emailAddress;
            templateContextMap.put(TMPLT_DATA_KEY_VG_RECEIVED_TRIGGER_SENDER_EMAILADDRESS, emailAddress);
            String mobilePhone = StringUtil.isBlank(this.senderUserData.mobilePhone) ? "N/A" : this.senderUserData.mobilePhone;
            templateContextMap.put(TMPLT_DATA_KEY_VG_RECEIVED_TRIGGER_SENDER_MOBILEPHONE, mobilePhone);
        }
        templateContextMap.put(TMPLT_DATA_KEY_VG_RECEIVED_TRIGGER_VIRTUAL_GIFT_ID, String.valueOf(this.virtualGiftID));
        templateContextMap.put(TMPLT_DATA_KEY_VG_RECEIVED_TRIGGER_VIRTUAL_GIFT_NAME, this.virtualGiftName);
        templateContextMap.put(TMPLT_DATA_KEY_VG_RECEIVED_TRIGGER_VIRTUAL_GIFT_IMAGE, this.image);
        templateContextMap.put(TMPLT_DATA_KEY_VG_RECEIVED_TRIGGER_VIRTUAL_MESSAGE_STYLE_HIDDEN, StringUtil.isBlank(this.message) ? "display:none !important;" : "");
        templateContextMap.put(TMPLT_DATA_KEY_VG_RECEIVED_TRIGGER_VIRTUAL_GIFT_RECEIVED_ID, String.valueOf(this.virtualGiftReceivedID));
        if (StringUtil.isBlank(this.message)) {
            templateContextMap.put(TMPLT_DATA_KEY_VG_RECEIVED_TRIGGER_VIRTUAL_SHOW_MESSAGE, "background:transparent;");
            templateContextMap.put(TMPLT_DATA_KEY_VG_RECEIVED_TRIGGER_VIRTUAL_TIMESTAMP, "");
            templateContextMap.put(TMPLT_DATA_KEY_VG_RECEIVED_TRIGGER_VIRTUAL_MESSAGE, "");
        } else {
            templateContextMap.put(TMPLT_DATA_KEY_VG_RECEIVED_TRIGGER_VIRTUAL_SHOW_MESSAGE, "");
            templateContextMap.put(TMPLT_DATA_KEY_VG_RECEIVED_TRIGGER_VIRTUAL_TIMESTAMP, dataFormatHolder.get().format(System.currentTimeMillis()));
            templateContextMap.put(TMPLT_DATA_KEY_VG_RECEIVED_TRIGGER_VIRTUAL_MESSAGE, this.message);
        }
    }

    public int getVirtualGiftID() {
        return this.virtualGiftID;
    }

    public UserDetails getSenderUser() {
        return this.senderUserData;
    }

    public boolean isFromSenderInventory() {
        return this.fromSenderInventory;
    }

    public String getVirtualGiftName() {
        return this.virtualGiftName;
    }

    public String getMessage() {
        return this.message;
    }

    public String getImage() {
        return this.message;
    }
}

