/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 */
package com.projectgoth.fusion.restapi.data;

import javax.xml.bind.annotation.XmlElement;

public class SettingsEnums {

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum DisplayPictureChoice {
        AVATAR(1),
        PROFILE_PICTURE(2);

        private int value;

        private DisplayPictureChoice(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static DisplayPictureChoice fromValue(int value) {
            for (DisplayPictureChoice e : DisplayPictureChoice.values()) {
                if (e.value() != value) continue;
                return e;
            }
            return null;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Birthday {
        HIDE(0),
        SHOW_FULL(1),
        SHOW_WITHOUT_YEAR(2);

        private int value;

        private Birthday(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static Birthday fromValue(int value) {
            for (Birthday e : Birthday.values()) {
                if (e.value() != value) continue;
                return e;
            }
            return null;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum EveryoneFriendHide {
        HIDE(0),
        EVERYONE(1),
        FRIEND_ONLY(2);

        private int value;

        private EveryoneFriendHide(int value) {
            this.value = value;
        }

        @XmlElement
        public int value() {
            return this.value;
        }

        public static EveryoneFriendHide fromValue(int value) {
            for (EveryoneFriendHide e : EveryoneFriendHide.values()) {
                if (e.value() != value) continue;
                return e;
            }
            return null;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum EveryoneOrFollowerAndFriend {
        EVERYONE(1),
        FRIEND_OR_FOLLOWER(2);

        private int value;

        private EveryoneOrFollowerAndFriend(int value) {
            this.value = value;
        }

        @XmlElement
        public int value() {
            return this.value;
        }

        public static EveryoneOrFollowerAndFriend fromValue(int value) {
            for (EveryoneOrFollowerAndFriend e : EveryoneOrFollowerAndFriend.values()) {
                if (e.value() != value) continue;
                return e;
            }
            return null;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum EveryoneFollowerFriend {
        EVERYONE(1),
        FRIEND_ONLY(2),
        FOLLOWER_ONLY(3);

        private int value;

        private EveryoneFollowerFriend(int value) {
            this.value = value;
        }

        @XmlElement
        public int value() {
            return this.value;
        }

        public static EveryoneFollowerFriend fromValue(int value) {
            for (EveryoneFollowerFriend e : EveryoneFollowerFriend.values()) {
                if (e.value() != value) continue;
                return e;
            }
            return null;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum EveryoneFollowerFriendHide {
        HIDE(0),
        EVERYONE(1),
        FRIEND_ONLY(2),
        FOLLOWER_ONLY(3);

        private int value;

        private EveryoneFollowerFriendHide(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static EveryoneFollowerFriendHide fromValue(int value) {
            for (EveryoneFollowerFriendHide e : EveryoneFollowerFriendHide.values()) {
                if (e.value() != value) continue;
                return e;
            }
            return null;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum OnOff {
        ON(1),
        OFF(0);

        private int value;

        private OnOff(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static OnOff fromValue(int value) {
            for (OnOff e : OnOff.values()) {
                if (e.value() != value) continue;
                return e;
            }
            return null;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum ShowHide {
        SHOW(1),
        HIDE(0);

        private int value;

        private ShowHide(int value) {
            this.value = value;
        }

        @XmlElement
        public int value() {
            return this.value;
        }

        public static ShowHide fromValue(int value) {
            for (ShowHide e : ShowHide.values()) {
                if (e.value() != value) continue;
                return e;
            }
            return null;
        }
    }
}

