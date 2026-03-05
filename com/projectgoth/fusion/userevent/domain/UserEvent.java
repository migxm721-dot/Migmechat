/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sleepycat.persist.model.Persistent
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.userevent.domain;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.slice.AddingFriendUserEventIce;
import com.projectgoth.fusion.slice.CreatedChatroomUserEventIce;
import com.projectgoth.fusion.slice.GenericApplicationUserEventIce;
import com.projectgoth.fusion.slice.GiftShowerUserEventIce;
import com.projectgoth.fusion.slice.GroupAnnouncementUserEventIce;
import com.projectgoth.fusion.slice.GroupDonationUserEventIce;
import com.projectgoth.fusion.slice.GroupJoinedUserEventIce;
import com.projectgoth.fusion.slice.GroupUserPostUserEventIce;
import com.projectgoth.fusion.slice.PhotoUploadedUserEventIce;
import com.projectgoth.fusion.slice.ProfileUpdatedUserEventIce;
import com.projectgoth.fusion.slice.PurchasedVirtualGoodsUserEventIce;
import com.projectgoth.fusion.slice.ShortTextStatusUserEventIce;
import com.projectgoth.fusion.slice.UserEventIce;
import com.projectgoth.fusion.slice.UserWallPostUserEventIce;
import com.projectgoth.fusion.slice.VirtualGiftUserEventIce;
import com.projectgoth.fusion.userevent.domain.UserEventType;
import com.sleepycat.persist.model.Persistent;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Persistent
public class UserEvent {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(UserEvent.class));
    private long timestamp;
    private String generatingUsername;
    private String text;

    public UserEvent() {
    }

    public UserEvent(UserEvent event) {
        this.timestamp = event.getTimestamp();
        this.generatingUsername = event.getGeneratingUsername();
        this.text = event.getText();
    }

    public UserEvent(UserEventIce event) {
        this.timestamp = event.timestamp;
        this.generatingUsername = event.generatingUsername;
        this.text = event.text;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getGeneratingUsername() {
        return this.generatingUsername;
    }

    public void setGeneratingUsername(String generatingUsername) {
        this.generatingUsername = generatingUsername;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public UserEventIce toIceEvent() {
        if (log.isDebugEnabled()) {
            log.debug((Object)("creating " + this.getClass().getName()));
        }
        UserEventIce iceEvent = new UserEventIce();
        iceEvent.timestamp = this.timestamp;
        iceEvent.generatingUsername = this.generatingUsername;
        iceEvent.text = this.text;
        return iceEvent;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("timestamp [").append(new Date(this.timestamp)).append("] ");
        buffer.append("gen user [").append(this.generatingUsername).append("] ");
        buffer.append("text [").append(this.text).append("]");
        return buffer.toString();
    }

    public static String toString(UserEventIce event) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("timestamp [").append(event.timestamp).append("] ");
        buffer.append("gen user [").append(event.generatingUsername).append("] ");
        buffer.append("text [").append(event.text).append("]");
        return buffer.toString();
    }

    public static Map<String, String> findSubstitutionParameters(UserEventIce event) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("user1", event.generatingUsername);
        return map;
    }

    public static UserEventType getEventType(UserEventIce event) {
        if (event instanceof AddingFriendUserEventIce) {
            return UserEventType.ADDING_FRIEND;
        }
        if (event instanceof PurchasedVirtualGoodsUserEventIce) {
            return UserEventType.PURCHASED_GOODS;
        }
        if (event instanceof ShortTextStatusUserEventIce) {
            return UserEventType.SHORT_TEXT_STATUS;
        }
        if (event instanceof ProfileUpdatedUserEventIce) {
            return UserEventType.UPDATING_PROFILE;
        }
        if (event instanceof PhotoUploadedUserEventIce) {
            return UserEventType.PHOTO_UPLOAD_WITH_TITLE;
        }
        if (event instanceof CreatedChatroomUserEventIce) {
            return UserEventType.CREATE_PUBLIC_CHATROOM;
        }
        if (event instanceof CreatedChatroomUserEventIce) {
            return UserEventType.CREATE_PUBLIC_CHATROOM;
        }
        if (event instanceof VirtualGiftUserEventIce) {
            return UserEventType.VIRTUAL_GIFT;
        }
        if (event instanceof UserWallPostUserEventIce) {
            return UserEventType.USER_WALL_POST;
        }
        if (event instanceof GroupDonationUserEventIce) {
            return UserEventType.GROUP_DONATION;
        }
        if (event instanceof GroupJoinedUserEventIce) {
            return UserEventType.GROUP_JOINED;
        }
        if (event instanceof GroupAnnouncementUserEventIce) {
            return UserEventType.GROUP_ANNOUNCEMENT;
        }
        if (event instanceof GroupUserPostUserEventIce) {
            return UserEventType.GROUP_USER_POST;
        }
        if (event instanceof GenericApplicationUserEventIce) {
            return UserEventType.GENERIC_APP_EVENT;
        }
        if (event instanceof GiftShowerUserEventIce) {
            return UserEventType.GIFT_SHOWER_EVENT;
        }
        log.warn((Object)("unknown event type " + event.ice_id()));
        return null;
    }
}

