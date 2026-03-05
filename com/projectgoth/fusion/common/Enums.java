/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.projectgoth.leto.common.event.post.IThirdPartySiteType
 *  com.projectgoth.leto.common.event.post.ThirdPartySiteType
 *  com.projectgoth.leto.common.utils.enums.ValueToEnumMap
 */
package com.projectgoth.fusion.common;

import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.fdl.enums.ImType;
import com.projectgoth.fusion.fdl.enums.MessageType;
import com.projectgoth.fusion.restapi.data.FusionRestException;
import com.projectgoth.leto.common.event.post.IThirdPartySiteType;
import com.projectgoth.leto.common.event.post.ThirdPartySiteType;
import com.projectgoth.leto.common.utils.enums.ValueToEnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Enums {

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum ForgotPasswordEnum {
        VIA_EMAIL(1),
        VIA_SECURITY_QUESTION(2),
        VIA_SMS(3);

        private int value;

        private ForgotPasswordEnum(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static ForgotPasswordEnum fromValue(int value) {
            for (ForgotPasswordEnum e : ForgotPasswordEnum.values()) {
                if (e.value() != value) continue;
                return e;
            }
            return null;
        }

        public static boolean isForgotPasswordTypeEnabled(ForgotPasswordEnum type) {
            switch (type) {
                case VIA_SMS: {
                    return SystemProperty.getBool(SystemPropertyEntities.ForgotPassword.ENABLED_FORGOT_PASSWORD_VIA_SMS);
                }
                case VIA_EMAIL: {
                    return SystemProperty.getBool(SystemPropertyEntities.ForgotPassword.ENABLED_FORGOT_PASSWORD_VIA_EMAIL);
                }
                case VIA_SECURITY_QUESTION: {
                    return SystemProperty.getBool(SystemPropertyEntities.ForgotPassword.ENABLED_FORGOT_PASSWORD_VIA_SECURITY_QUESTION);
                }
            }
            return false;
        }

        public static String getRatelimitPattern(ForgotPasswordEnum type) {
            switch (type) {
                case VIA_SMS: {
                    return SystemProperty.get(SystemPropertyEntities.ForgotPassword.ATTEMPT_RATE_LIMIT_VIA_SMS);
                }
                case VIA_EMAIL: {
                    return SystemProperty.get(SystemPropertyEntities.ForgotPassword.ATTEMPT_RATE_LIMIT_VIA_EMAIL);
                }
                case VIA_SECURITY_QUESTION: {
                    return SystemProperty.get(SystemPropertyEntities.ForgotPassword.ATTEMPT_RATE_LIMIT_VIA_SQ);
                }
            }
            return null;
        }

        public static FusionRestException.RestException getRatelimitRestException(ForgotPasswordEnum type) {
            switch (type) {
                case VIA_SMS: {
                    return FusionRestException.RestException.FORGOT_PASSWORD_VIA_SMS_RATE_LIMIT;
                }
                case VIA_EMAIL: {
                    return FusionRestException.RestException.FORGOT_PASSWORD_VIA_EMAIL_RATE_LIMIT;
                }
                case VIA_SECURITY_QUESTION: {
                    return FusionRestException.RestException.FORGOT_PASSWORD_VIA_SECURITY_QUESTION_RATE_LIMIT;
                }
            }
            return FusionRestException.RestException.FORGOT_PASSWORD_OVERALL_RATE_LIMIT;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Mig33UserActionMisLogEnum {
        CHANGE_EMAIL_ADDRESS(1, "MIG33_USER", "UPDATE", "Updated email address from %s to %s."),
        CHANGE_PASSWORD(2, "MIG33_USER", "UPDATE", "Updated password."),
        ADD_EMAIL_ADDRESS(3, "MIG33_USER", "ADD", "Added email address %s"),
        DELETE_EMAIL_ADDRESS(4, "MIG33_USER", "DELETE", "Delete email address %s"),
        VERIFY_EMAIL_ADDRESS(5, "MIG33_USER", "VERIFY", "Verify email address %s"),
        SUSPEND_USER(6, "MIG33_USER", "SUSPEND_USER", "Suspended user,duration(hours) %s"),
        FORGOT_PASSWORD(7, "MIG33_USER", "FORGOT_PASSWORD", "Forgot password via %s");

        private int code;
        private String section;
        private String action;
        private String descriptionPattern;

        private Mig33UserActionMisLogEnum(int code, String section, String action, String descriptionPattern) {
            this.code = code;
            this.section = section;
            this.action = action;
            this.descriptionPattern = descriptionPattern;
        }

        public String getSection() {
            return this.section;
        }

        public String getAction() {
            return this.action;
        }

        public String getDescriptionPattern() {
            return this.descriptionPattern;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum MessageStatusEventTypeEnum {
        COMPOSING(1),
        RECEIVED(2),
        READ(3);

        public static int MAX_VALUE;
        private int value;

        private MessageStatusEventTypeEnum(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static MessageStatusEventTypeEnum fromValue(int value) {
            for (MessageStatusEventTypeEnum e : MessageStatusEventTypeEnum.values()) {
                if (e.value() != value) continue;
                return e;
            }
            return null;
        }

        static {
            MAX_VALUE = READ.value();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum EmailTypeEnum {
        REFERRAL(1),
        DAILY_DIGEST(2),
        DAILY_DIGEST_FOR_USERS_BEING_FOLLOWED(7),
        REFERRAL_ACK(10);

        private static final Map<Integer, EmailTypeEnum> lookup;
        int value;

        private EmailTypeEnum(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static EmailTypeEnum fromValue(int v) {
            return lookup.get(v);
        }

        static {
            lookup = new HashMap<Integer, EmailTypeEnum>();
            for (EmailTypeEnum e : EmailTypeEnum.values()) {
                lookup.put(e.value, e);
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum EventTypeEnum {
        TEST_EVENT(0, "Dummy Test Event"),
        FRIEND_ADDED(1, "Friend Added"),
        VIRTUAL_GIFT_PURCHASED(2, "VG Purchased"),
        USERDATA_UPDATED(3, "User Data Updated"),
        GAME_EVENT(4, "Game Event"),
        THIRD_PARTY_SITE_CREDENTIAL_UPDATED(5, "Third Party Site Credential Updated"),
        STATUS_UPDATE_EVENT(6, "Status Update Event");

        public int value;
        public String description;
        private static Set<EventTypeEnum> ALL_TYPES;

        private EventTypeEnum(int value, String description) {
            this.value = value;
            this.description = description;
        }

        public static EventTypeEnum fromValue(int value) {
            for (EventTypeEnum e : EventTypeEnum.values()) {
                if (e.value != value) continue;
                return e;
            }
            return null;
        }

        public static Set<EventTypeEnum> getAllTypes() {
            if (ALL_TYPES == null) {
                ALL_TYPES = new HashSet<EventTypeEnum>();
                ALL_TYPES.add(TEST_EVENT);
                ALL_TYPES.add(FRIEND_ADDED);
                ALL_TYPES.add(VIRTUAL_GIFT_PURCHASED);
                ALL_TYPES.add(USERDATA_UPDATED);
                ALL_TYPES.add(GAME_EVENT);
                ALL_TYPES.add(THIRD_PARTY_SITE_CREDENTIAL_UPDATED);
                ALL_TYPES.add(STATUS_UPDATE_EVENT);
            }
            return ALL_TYPES;
        }

        public static boolean isValid(int v) {
            return EventTypeEnum.fromValue(v) != null;
        }

        public static String getDescription(int v) {
            return EventTypeEnum.toString(v);
        }

        public static String toString(int v) {
            return EventTypeEnum.isValid(v) ? EventTypeEnum.fromValue((int)v).description : "Invalid Event Type [" + v + "]";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum MerchantTagStatEnum {
        FAILED(0, "failed"),
        CREATED(1, "created"),
        TRANSFERED(2, "transfered"),
        EXPIRED(3, "expired");

        private int code;
        private String description;

        private MerchantTagStatEnum(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public static MerchantTagStatEnum fromCode(int code) {
            for (MerchantTagStatEnum value : MerchantTagStatEnum.values()) {
                if (value.code != code) continue;
                return value;
            }
            return null;
        }

        public static String getDescription(int code) {
            return MerchantTagStatEnum.fromCode(code) != null ? MerchantTagStatEnum.fromCode((int)code).description : "";
        }

        public static boolean isValid(int code) {
            return MerchantTagStatEnum.fromCode(code) != null;
        }

        public static String toString(int code) {
            return MerchantTagStatEnum.fromCode(code) != null ? MerchantTagStatEnum.fromCode((int)code).description : "";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum GroupUnbanReasonEnum {
        chance1(1, "giving user his first chance"),
        chance2(2, "giving user his last chance");

        private int code;
        private String description;

        private GroupUnbanReasonEnum(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public static String stringifyValues() {
            StringBuffer str = new StringBuffer();
            boolean first = true;
            str.append("[ ");
            for (GroupUnbanReasonEnum value : GroupUnbanReasonEnum.values()) {
                if (!first) {
                    str.append(", ");
                } else {
                    first = false;
                }
                str.append(value.code);
                str.append(": ");
                str.append(value.description);
            }
            str.append(" ]");
            return str.toString();
        }

        public static GroupUnbanReasonEnum fromCode(int code) {
            for (GroupUnbanReasonEnum value : GroupUnbanReasonEnum.values()) {
                if (value.code != code) continue;
                return value;
            }
            return null;
        }

        public static String getDescription(int code) {
            return GroupUnbanReasonEnum.fromCode(code) != null ? GroupUnbanReasonEnum.fromCode((int)code).description : "";
        }

        public static boolean isValid(int code) {
            return GroupUnbanReasonEnum.fromCode(code) != null;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum GroupBanReasonEnum {
        SPAM(1, "spamming in the chatroom"),
        FLOOD(2, "flooding in the chatroom"),
        ABUSE(3, "abusing"),
        HACK(4, "hacking"),
        IMPOSTER(5, "imposter");

        private int code;
        private String description;

        private GroupBanReasonEnum(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public static String stringifyValues() {
            StringBuffer str = new StringBuffer();
            boolean first = true;
            str.append("[ ");
            for (GroupBanReasonEnum value : GroupBanReasonEnum.values()) {
                if (!first) {
                    str.append(", ");
                } else {
                    first = false;
                }
                str.append(value.code);
                str.append(": ");
                str.append(value.description);
            }
            str.append(" ]");
            return str.toString();
        }

        public static GroupBanReasonEnum fromCode(int code) {
            for (GroupBanReasonEnum value : GroupBanReasonEnum.values()) {
                if (value.code != code) continue;
                return value;
            }
            return null;
        }

        public static String getDescription(int code) {
            return GroupBanReasonEnum.fromCode(code) != null ? GroupBanReasonEnum.fromCode((int)code).description : "";
        }

        public static boolean isValid(int code) {
            return GroupBanReasonEnum.fromCode(code) != null;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum NotificationTypeEnum {
        TOTAL_COUNT(0),
        FRIEND_INVITE(1),
        GROUP_INVITE(2),
        VIRTUALGIFT_ALERT(3),
        GAME_INVITE(4),
        NEW_BADGE_ALERT(5),
        NEW_FOLLOWER_ALERT(6),
        FOLLOWING_REQUEST(7),
        PRIVATE_MESSAGE(8),
        GROUPCHAT_MESSAGE(9),
        MIGLEVEL_INCREASE_ALERT(10),
        REPLY_TO_MIGBO_POST_ALERT(11),
        INCOMING_CREDIT_TRANSFER_ALERT(12),
        MERCHANT_STATUS_CHANGE_ALERT(13),
        REPLY_TO_MIGBO_WATCHED_POST_ALERT(14),
        REPLY_TO_MIGBO_PARTICIPATED_POST_ALERT(15),
        MORE_OFFLINE_MESSAGES_AT_MIGBO(16),
        MUTUAL_FOLLOWING_ALERT(17),
        SYS_ALERT(18),
        GAME_HELP(19),
        MENTIONED_IN_MIGBO_POST_ALERT(21);

        private int type;
        public static final EnumSet<NotificationTypeEnum> COLLAPSE_SET;
        public static final EnumSet<NotificationTypeEnum> ACCUMULATED_SET;
        public static final EnumSet<NotificationTypeEnum> MIGBO_SET;
        public static final EnumSet<NotificationTypeEnum> PERSISTENT_SET;
        public static final EnumSet<NotificationTypeEnum> MIGCORE_SET;
        public static final EnumSet<NotificationTypeEnum> MIGBO_NON_PERSISTENT_SET;

        private NotificationTypeEnum(int type) {
            this.type = type;
        }

        public int getType() {
            return this.type;
        }

        private static EnumSet<NotificationTypeEnum> getMigboNonPersistentSet() {
            EnumSet<NotificationTypeEnum> nonPersistentSet = EnumSet.copyOf(MIGBO_SET);
            nonPersistentSet.removeAll(PERSISTENT_SET);
            return nonPersistentSet;
        }

        public static boolean isForCollapse(int type) {
            NotificationTypeEnum e = NotificationTypeEnum.fromType(type);
            return COLLAPSE_SET.contains((Object)e);
        }

        public static boolean isForMigBo(int type) {
            NotificationTypeEnum e = NotificationTypeEnum.fromType(type);
            return MIGBO_SET.contains((Object)e);
        }

        public static boolean isForPersistent(int type) {
            NotificationTypeEnum e = NotificationTypeEnum.fromType(type);
            return PERSISTENT_SET.contains((Object)e);
        }

        public static boolean isForMigCore(int type) {
            NotificationTypeEnum e = NotificationTypeEnum.fromType(type);
            return MIGCORE_SET.contains((Object)e);
        }

        public static boolean isValid(int type) {
            return NotificationTypeEnum.fromType(type) != null;
        }

        public static NotificationTypeEnum fromType(int type) {
            for (NotificationTypeEnum e : NotificationTypeEnum.values()) {
                if (e.type != type) continue;
                return e;
            }
            return null;
        }

        static {
            COLLAPSE_SET = EnumSet.of(GAME_INVITE);
            ACCUMULATED_SET = EnumSet.of(NEW_FOLLOWER_ALERT, VIRTUALGIFT_ALERT);
            MIGBO_SET = EnumSet.of(NEW_BADGE_ALERT, new NotificationTypeEnum[]{MUTUAL_FOLLOWING_ALERT, NEW_FOLLOWER_ALERT, FOLLOWING_REQUEST, FRIEND_INVITE, GROUP_INVITE, VIRTUALGIFT_ALERT, GAME_INVITE, MIGLEVEL_INCREASE_ALERT, REPLY_TO_MIGBO_POST_ALERT, INCOMING_CREDIT_TRANSFER_ALERT, MERCHANT_STATUS_CHANGE_ALERT, REPLY_TO_MIGBO_WATCHED_POST_ALERT, REPLY_TO_MIGBO_PARTICIPATED_POST_ALERT, SYS_ALERT, MENTIONED_IN_MIGBO_POST_ALERT});
            PERSISTENT_SET = EnumSet.of(FOLLOWING_REQUEST, FRIEND_INVITE, GROUP_INVITE, GAME_INVITE);
            MIGCORE_SET = EnumSet.of(FRIEND_INVITE, GROUP_INVITE, VIRTUALGIFT_ALERT, GAME_INVITE);
            MIGBO_NON_PERSISTENT_SET = NotificationTypeEnum.getMigboNonPersistentSet();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum IMEnum {
        FUSION(ImType.FUSION, MessageType.FUSION, "migme", true),
        MSN(ImType.MSN, MessageType.MSN, "MSN", true),
        AIM(ImType.AIM, MessageType.AIM, "AIM", false),
        YAHOO(ImType.YAHOO, MessageType.YAHOO, "Yahoo!", true),
        GTALK(ImType.GTALK, MessageType.GTALK, "GTalk", false),
        FACEBOOK(ImType.FACEBOOK, MessageType.FACEBOOK, "Facebook", false);

        private ImType imType;
        private MessageType messageType;
        private String name;
        private boolean supportsGroupChat;

        private IMEnum(ImType imType, MessageType messageType, String name, boolean supportsGroupChat) {
            this.imType = imType;
            this.messageType = messageType;
            this.name = name;
            this.supportsGroupChat = supportsGroupChat;
        }

        public ImType getImType() {
            return this.imType;
        }

        public MessageType getMessageType() {
            return this.messageType;
        }

        public String getName() {
            return this.name;
        }

        public boolean supportsGroupChat() {
            return this.supportsGroupChat;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum PaymentEnum {
        ALL(0),
        CREDIT_CARD(1),
        TELEGRAPHIC_TRANSFER(2),
        BANK_TRANSFER(3),
        WESTERN_UNION(4),
        VOUCHER(5);

        private int value;

        private PaymentEnum(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static PaymentEnum fromValue(int value) {
            for (PaymentEnum e : PaymentEnum.values()) {
                if (e.value() != value) continue;
                return e;
            }
            return null;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum UserProfileKeywordEnum {
        SCHOOLS(1),
        HOBBIES(2),
        LIKES(3),
        DISLIKES(4),
        JOBS(5);

        private int value;

        private UserProfileKeywordEnum(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static UserProfileKeywordEnum fromValue(int value) {
            for (UserProfileKeywordEnum e : UserProfileKeywordEnum.values()) {
                if (e.value() != value) continue;
                return e;
            }
            return null;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum ThirdPartyEnum implements IThirdPartySiteType<ThirdPartyEnum>
    {
        FACEBOOK(ThirdPartySiteType.FACEBOOK),
        TWITTER(ThirdPartySiteType.TWITTER);

        private ThirdPartySiteType thirdPartySites;

        private ThirdPartyEnum(ThirdPartySiteType value) {
            this.thirdPartySites = value;
        }

        public int value() {
            return this.getEnumValue();
        }

        public static ThirdPartyEnum fromValue(int value) {
            return (ThirdPartyEnum)ValueToEnumMapInstances.INSTANCE.toEnum((Object)value);
        }

        public Integer getEnumValue() {
            return this.thirdPartySites.getEnumValue();
        }

        private static final class ValueToEnumMapInstances {
            private static final ValueToEnumMap<Integer, ThirdPartyEnum> INSTANCE = new ValueToEnumMap(ThirdPartyEnum.class);

            private ValueToEnumMapInstances() {
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @Deprecated
    public static enum DeviceModeEnum {
        AWAKE(0),
        SLEEP(1);

        private int value;

        private DeviceModeEnum(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static DeviceModeEnum fromValue(int value) {
            for (DeviceModeEnum e : DeviceModeEnum.values()) {
                if (e.value() != value) continue;
                return e;
            }
            return null;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum ConnectionEnum {
        TCP(1),
        HTTP(2);

        private int value;

        private ConnectionEnum(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static ConnectionEnum fromValue(int value) {
            for (ConnectionEnum e : ConnectionEnum.values()) {
                if (e.value() != value) continue;
                return e;
            }
            return null;
        }
    }
}

