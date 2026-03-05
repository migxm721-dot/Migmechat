/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.projectgoth.leto.common.event.friending.FriendingRelationshipType
 *  com.projectgoth.leto.common.utils.enums.ValueToEnumMap
 */
package com.projectgoth.fusion.rewardsystem.triggers;

import com.projectgoth.fusion.common.EnumUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;
import com.projectgoth.leto.common.event.friending.FriendingRelationshipType;
import com.projectgoth.leto.common.utils.enums.ValueToEnumMap;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class RelationshipEventTrigger
extends RewardProgramTrigger {
    private UserData otherUserData;
    private RelationshipEventTypeEnum relationshipEvent;
    private static final String TMPLT_DATA_KEY_TRIGGER_OTHER_USER_DATA_ID = "trigger.otherUserData.userid";
    private static final String TMPLT_DATA_KEY_TRIGGER_OTHER_USER_DATA_USERNAME = "trigger.otherUserData.username";
    private static final String TMPLT_DATA_KEY_TRIGGER_OTHER_USER_DATA_DISPLAY_NAME = "trigger.otherUserData.displayName";
    private static final String TMPLT_DATA_KEY_TRIGGER_OTHER_USER_DATA_DISPLAY_PICTURE = "trigger.otherUserData.displayPicture";
    private static final String TMPLT_DATA_KEY_TRIGGER_OTHER_USER_DATA_AVATAR = "trigger.otherUserData.avatar";
    private static final String TMPLT_DATA_KEY_TRIGGER_OTHER_USER_DATA_FULLBODY_AVATAR = "trigger.otherUserData.fullbodyAvatar";
    private static final String TMPLT_DATA_KEY_TRIGGER_OTHER_USER_DATA_LANGUAGE = "trigger.otherUserData.language";
    private static final String TMPLT_DATA_KEY_TRIGGER_OTHER_USER_DATA_EMAILADDRESS = "trigger.otherUserData.emailAddress";
    private static final String TMPLT_DATA_KEY_TRIGGER_OTHER_USER_DATA_MOBILEPHONE = "trigger.otherUserData.mobilePhone";

    public RelationshipEventTrigger(RewardProgramData.TypeEnum programType, UserData thisUserData, UserData otherUserData, RelationshipEventTypeEnum relationshipEventType) {
        super(programType, thisUserData);
        this.quantityDelta = 1;
        this.amountDelta = 0.0;
        this.currency = "USD";
        this.otherUserData = otherUserData;
        this.relationshipEvent = relationshipEventType;
    }

    public UserData getOtherUserData() {
        return this.otherUserData;
    }

    public UserData getThisUserData() {
        return this.userData;
    }

    public RelationshipEventTypeEnum getRelationshipEvent() {
        return this.relationshipEvent;
    }

    @Override
    protected final void fillTemplateDataMap(Map<String, String> templateContextMap) {
        if (this.otherUserData != null) {
            if (this.otherUserData.userID != null) {
                templateContextMap.put(TMPLT_DATA_KEY_TRIGGER_OTHER_USER_DATA_ID, this.otherUserData.userID.toString());
            }
            templateContextMap.put(TMPLT_DATA_KEY_TRIGGER_OTHER_USER_DATA_USERNAME, this.otherUserData.username);
            templateContextMap.put(TMPLT_DATA_KEY_TRIGGER_OTHER_USER_DATA_DISPLAY_NAME, this.otherUserData.displayName);
            templateContextMap.put(TMPLT_DATA_KEY_TRIGGER_OTHER_USER_DATA_DISPLAY_PICTURE, this.otherUserData.displayPicture);
            templateContextMap.put(TMPLT_DATA_KEY_TRIGGER_OTHER_USER_DATA_AVATAR, this.otherUserData.avatar);
            templateContextMap.put(TMPLT_DATA_KEY_TRIGGER_OTHER_USER_DATA_FULLBODY_AVATAR, this.otherUserData.fullbodyAvatar);
            templateContextMap.put(TMPLT_DATA_KEY_TRIGGER_OTHER_USER_DATA_LANGUAGE, this.otherUserData.language);
            String emailAddress = StringUtil.isBlank(this.otherUserData.emailAddress) ? "N/A" : this.otherUserData.emailAddress;
            templateContextMap.put(TMPLT_DATA_KEY_TRIGGER_OTHER_USER_DATA_EMAILADDRESS, emailAddress);
            String mobilePhone = StringUtil.isBlank(this.otherUserData.mobilePhone) ? "N/A" : this.otherUserData.mobilePhone;
            templateContextMap.put(TMPLT_DATA_KEY_TRIGGER_OTHER_USER_DATA_MOBILEPHONE, mobilePhone);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum RelationshipEventTypeEnum implements EnumUtils.IEnumValueGetter<Integer>
    {
        NEW(FriendingRelationshipType.NEW),
        REMOVED(FriendingRelationshipType.REMOVED),
        MUTUALLY_FOLLOWING(FriendingRelationshipType.MUTUALLY_FOLLOWING);

        private final FriendingRelationshipType friendingRelationshipType;

        private RelationshipEventTypeEnum(FriendingRelationshipType friendingRelationshipType) {
            this.friendingRelationshipType = friendingRelationshipType;
        }

        public int getValue() {
            return this.getEnumValue();
        }

        public Integer getEnumValue() {
            return this.friendingRelationshipType.getEnumValue();
        }

        public FriendingRelationshipType toFriendingRelationshipType() {
            return this.friendingRelationshipType;
        }

        public static RelationshipEventTypeEnum fromValue(int value) {
            return (RelationshipEventTypeEnum)ValueToEnumMapInstance.INSTANCE.toEnum((Object)value);
        }

        private static final class ValueToEnumMapInstance {
            private static final ValueToEnumMap<Integer, RelationshipEventTypeEnum> INSTANCE = new ValueToEnumMap(RelationshipEventTypeEnum.class);

            private ValueToEnumMapInstance() {
            }
        }
    }
}

