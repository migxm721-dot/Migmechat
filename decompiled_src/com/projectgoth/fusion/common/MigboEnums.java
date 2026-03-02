/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.projectgoth.leto.common.event.post.PostContentType
 *  com.projectgoth.leto.common.event.post.PostOriginality
 *  com.projectgoth.leto.common.event.post.PostingApplicationType
 *  com.projectgoth.leto.common.utils.enums.IEnumValueGetter
 *  com.projectgoth.leto.common.utils.enums.ValueToEnumMap
 */
package com.projectgoth.fusion.common;

import com.projectgoth.fusion.fdl.enums.ClientType;
import com.projectgoth.fusion.restapi.data.SSOEnums;
import com.projectgoth.leto.common.event.post.PostContentType;
import com.projectgoth.leto.common.event.post.PostOriginality;
import com.projectgoth.leto.common.event.post.PostingApplicationType;
import com.projectgoth.leto.common.utils.enums.IEnumValueGetter;
import com.projectgoth.leto.common.utils.enums.ValueToEnumMap;

public class MigboEnums {

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum MigboFollowingEventTypeEnum {
        NEW_FOLLOWING(1),
        REMOVE_FOLLOWING(2),
        MUTUAL_FOLLOWING(3);

        private int type;

        private MigboFollowingEventTypeEnum(int type) {
            this.type = type;
        }

        public int value() {
            return this.type;
        }

        public static boolean isValid(int type) {
            return MigboFollowingEventTypeEnum.fromType(type) != null;
        }

        public static MigboFollowingEventTypeEnum fromType(int type) {
            for (MigboFollowingEventTypeEnum e : MigboFollowingEventTypeEnum.values()) {
                if (e.type != type) continue;
                return e;
            }
            return null;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum MigboPostTypeEnum implements IEnumValueGetter<Integer>
    {
        TEXT(PostContentType.TEXT),
        LINK(PostContentType.LINK),
        PHOTO(PostContentType.PHOTO),
        VIDEO(PostContentType.VIDEO),
        RSSFEED(PostContentType.RSSFEED),
        ACTIVITY(PostContentType.ACTIVITY),
        GAME_EVENT(PostContentType.GAME_EVENT);

        private final PostContentType postContentType;

        private MigboPostTypeEnum(PostContentType postContentType) {
            this.postContentType = postContentType;
        }

        public int getType() {
            return this.postContentType.getEnumValue();
        }

        public static boolean isValid(int type) {
            return MigboPostTypeEnum.fromValue(type) != null;
        }

        public static MigboPostTypeEnum fromValue(int type) {
            return (MigboPostTypeEnum)ValueToEnumMapInstance.INSTANCE.toEnum((Object)type);
        }

        public PostContentType toPostContentType() {
            return this.postContentType;
        }

        public Integer getEnumValue() {
            return this.postContentType.getEnumValue();
        }

        private static final class ValueToEnumMapInstance {
            public static final ValueToEnumMap<Integer, MigboPostTypeEnum> INSTANCE = new ValueToEnumMap(MigboPostTypeEnum.class);

            private ValueToEnumMapInstance() {
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum MigboPostOriginalityEnum implements IEnumValueGetter<Integer>
    {
        ORIGINAL(PostOriginality.ORIGINAL),
        REPLY(PostOriginality.REPLY),
        RESHARE(PostOriginality.RESHARE);

        private final PostOriginality postOriginality;

        private MigboPostOriginalityEnum(PostOriginality postOriginality) {
            this.postOriginality = postOriginality;
        }

        public int getType() {
            return this.postOriginality.getEnumValue();
        }

        public static boolean isValid(int type) {
            return MigboPostOriginalityEnum.fromType(type) != null;
        }

        public static MigboPostOriginalityEnum fromType(int type) {
            return (MigboPostOriginalityEnum)ValueToEnumMapInstance.INSTANCE.toEnum((Object)type);
        }

        public PostOriginality toPostOriginality() {
            return this.postOriginality;
        }

        public Integer getEnumValue() {
            return this.postOriginality.getEnumValue();
        }

        private static final class ValueToEnumMapInstance {
            public static final ValueToEnumMap<Integer, MigboPostOriginalityEnum> INSTANCE = new ValueToEnumMap(MigboPostOriginalityEnum.class);

            private ValueToEnumMapInstance() {
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum PostApplicationEnum implements IEnumValueGetter<Integer>
    {
        WEB(PostingApplicationType.WEB),
        WAP(PostingApplicationType.WAP),
        J2ME(PostingApplicationType.J2ME),
        ANDROID(PostingApplicationType.ANDROID),
        SYSTEM(PostingApplicationType.SYSTEM),
        BLACKBERRY(PostingApplicationType.BLACKBERRY),
        BLAAST(PostingApplicationType.BLAAST),
        MRE(PostingApplicationType.MRE),
        IOS(PostingApplicationType.IOS);

        private final PostingApplicationType postingApplicationType;

        private PostApplicationEnum(PostingApplicationType postingApplicationType) {
            this.postingApplicationType = postingApplicationType;
        }

        public int value() {
            return this.postingApplicationType.getEnumValue();
        }

        public static PostApplicationEnum fromValue(int v) {
            return (PostApplicationEnum)ValueToEnumMapInstance.INSTANCE.toEnum((Object)v);
        }

        public static PostApplicationEnum fromDeviceType(ClientType deviceType) {
            if (deviceType == null) {
                return null;
            }
            switch (deviceType) {
                case AJAX1: 
                case AJAX2: 
                case MIGBO: 
                case MERCHANT_CENTER: 
                case VAS: {
                    return WEB;
                }
                case ANDROID: {
                    return ANDROID;
                }
                case WAP: {
                    return WAP;
                }
                case BLACKBERRY: {
                    return BLACKBERRY;
                }
                case BLAAST: {
                    return BLAAST;
                }
                case MRE: {
                    return MRE;
                }
                case IOS: {
                    return IOS;
                }
            }
            return J2ME;
        }

        public static PostApplicationEnum fromSSOView(SSOEnums.View ssoView) {
            if (ssoView == null) {
                return null;
            }
            switch (ssoView) {
                case MIG33_AJAX: 
                case MIG33_AJAXV2: 
                case MIGBO_WEB: {
                    return WEB;
                }
                case MIG33_WAP: 
                case MIGBO_WAP: {
                    return WAP;
                }
                case MIG33_TOUCH: 
                case MIGBO_TOUCH: {
                    return ANDROID;
                }
                case MIG33_MIDLET: 
                case MIGBO_MIDLET: 
                case MIG33_WINDOWS_MOBILE: 
                case MIGBO_WINDOWS_MOBILE: {
                    return J2ME;
                }
                case MIG33_BLACKBERRY: 
                case MIGBO_BLACKBERRY: {
                    return BLACKBERRY;
                }
                case MIG33_BLAAST: 
                case MIGBO_BLAAST: {
                    return BLAAST;
                }
                case MIG33_MRE: 
                case MIGBO_MRE: {
                    return MRE;
                }
                case MIG33_IOS: 
                case MIGBO_IOS: {
                    return IOS;
                }
            }
            return null;
        }

        public PostingApplicationType toPostingApplicationType() {
            return this.postingApplicationType;
        }

        public Integer getEnumValue() {
            return this.postingApplicationType.getEnumValue();
        }

        private static final class ValueToEnumMapInstance {
            public static final ValueToEnumMap<Integer, PostApplicationEnum> INSTANCE = new ValueToEnumMap(PostApplicationEnum.class);

            private ValueToEnumMapInstance() {
            }
        }
    }
}

