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
import com.projectgoth.fusion.slice.AddingMultipleFriendsUserEventIce;
import com.projectgoth.fusion.slice.AddingTwoFriendsUserEventIce;
import com.projectgoth.fusion.slice.CreatedChatroomUserEventIce;
import com.projectgoth.fusion.slice.EventPrivacySettingIce;
import com.projectgoth.fusion.slice.GenericApplicationUserEventIce;
import com.projectgoth.fusion.slice.GiftShowerUserEventIce;
import com.projectgoth.fusion.slice.GroupUserEventIce;
import com.projectgoth.fusion.slice.PhotoUploadedUserEventIce;
import com.projectgoth.fusion.slice.ProfileUpdatedUserEventIce;
import com.projectgoth.fusion.slice.PurchasedVirtualGoodsUserEventIce;
import com.projectgoth.fusion.slice.ShortTextStatusUserEventIce;
import com.projectgoth.fusion.slice.UserEventIce;
import com.projectgoth.fusion.slice.UserWallPostUserEventIce;
import com.projectgoth.fusion.slice.VirtualGiftUserEventIce;
import com.projectgoth.fusion.userevent.domain.AddingFriendUserEvent;
import com.projectgoth.fusion.userevent.domain.AddingMultipleFriendsUserEvent;
import com.projectgoth.fusion.userevent.domain.AddingTwoFriendsUserEvent;
import com.projectgoth.fusion.userevent.domain.CreatedChatroomUserEvent;
import com.projectgoth.fusion.userevent.domain.GenericApplicationUserEvent;
import com.projectgoth.fusion.userevent.domain.GiftShowerUserEvent;
import com.projectgoth.fusion.userevent.domain.GroupUserEvent;
import com.projectgoth.fusion.userevent.domain.PhotoUploadedUserEvent;
import com.projectgoth.fusion.userevent.domain.ProfileUpdatedUserEvent;
import com.projectgoth.fusion.userevent.domain.PurchasedVirtualGoodsUserEvent;
import com.projectgoth.fusion.userevent.domain.ShortTextStatusUserEvent;
import com.projectgoth.fusion.userevent.domain.UserEvent;
import com.projectgoth.fusion.userevent.domain.UserWallPostUserEvent;
import com.projectgoth.fusion.userevent.domain.VirtualGiftUserEvent;
import com.sleepycat.persist.model.Persistent;
import org.apache.log4j.Logger;

@Persistent(version=1)
public class EventPrivacySetting {
    private static final transient Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(EventPrivacySetting.class));
    private boolean statusUpdates = true;
    private boolean profileChanges = true;
    private boolean addFriends = false;
    private boolean photosPublished = true;
    private boolean contentPurchased = true;
    private boolean chatroomCreation = true;
    private boolean virtualGifting = true;

    public EventPrivacySetting() {
    }

    public EventPrivacySetting(boolean statusUpdates, boolean profileChanges, boolean addFriends, boolean photosPublished, boolean contentPurchased, boolean chatroomCreation, boolean virtualGifting) {
        this.statusUpdates = statusUpdates;
        this.profileChanges = profileChanges;
        this.addFriends = addFriends;
        this.photosPublished = photosPublished;
        this.contentPurchased = contentPurchased;
        this.chatroomCreation = chatroomCreation;
        this.virtualGifting = virtualGifting;
    }

    public void setStatusUpdates(boolean statusUpdates) {
        this.statusUpdates = statusUpdates;
    }

    public void setProfileChanges(boolean profileChanges) {
        this.profileChanges = profileChanges;
    }

    public void setAddFriends(boolean addFriends) {
        this.addFriends = addFriends;
    }

    public void setPhotosPublished(boolean photosPublished) {
        this.photosPublished = photosPublished;
    }

    public void setContentPurchased(boolean contentPurchased) {
        this.contentPurchased = contentPurchased;
    }

    public void setChatroomCreation(boolean chatroomCreation) {
        this.chatroomCreation = chatroomCreation;
    }

    public void setVirtualGifting(boolean virtualGifting) {
        this.virtualGifting = virtualGifting;
    }

    public boolean applyMask(UserEvent event) {
        if (event instanceof AddingFriendUserEvent) {
            return this.addFriends;
        }
        if (event instanceof AddingTwoFriendsUserEvent) {
            return this.addFriends;
        }
        if (event instanceof AddingMultipleFriendsUserEvent) {
            return this.addFriends;
        }
        if (event instanceof PurchasedVirtualGoodsUserEvent) {
            return this.contentPurchased;
        }
        if (event instanceof ShortTextStatusUserEvent) {
            return this.statusUpdates;
        }
        if (event instanceof ProfileUpdatedUserEvent) {
            return this.profileChanges;
        }
        if (event instanceof PhotoUploadedUserEvent) {
            return this.photosPublished;
        }
        if (event instanceof CreatedChatroomUserEvent) {
            return this.chatroomCreation;
        }
        if (event instanceof VirtualGiftUserEvent) {
            return this.virtualGifting;
        }
        if (event instanceof UserWallPostUserEvent) {
            return true;
        }
        if (event instanceof GroupUserEvent) {
            return true;
        }
        if (event instanceof GenericApplicationUserEvent) {
            return true;
        }
        if (event instanceof GiftShowerUserEvent) {
            return true;
        }
        log.warn((Object)("unknown event type [" + event + "]"));
        return false;
    }

    public boolean applyMask(UserEventIce event) {
        if (event instanceof AddingFriendUserEventIce) {
            return this.addFriends;
        }
        if (event instanceof AddingTwoFriendsUserEventIce) {
            return this.addFriends;
        }
        if (event instanceof AddingMultipleFriendsUserEventIce) {
            return this.addFriends;
        }
        if (event instanceof PurchasedVirtualGoodsUserEventIce) {
            return this.contentPurchased;
        }
        if (event instanceof ShortTextStatusUserEventIce) {
            return this.statusUpdates;
        }
        if (event instanceof ProfileUpdatedUserEventIce) {
            return this.profileChanges;
        }
        if (event instanceof PhotoUploadedUserEventIce) {
            return this.photosPublished;
        }
        if (event instanceof CreatedChatroomUserEventIce) {
            return this.chatroomCreation;
        }
        if (event instanceof VirtualGiftUserEventIce) {
            return this.virtualGifting;
        }
        if (event instanceof UserWallPostUserEventIce) {
            return true;
        }
        if (event instanceof GroupUserEventIce) {
            return true;
        }
        if (event instanceof GenericApplicationUserEventIce) {
            return true;
        }
        if (event instanceof GiftShowerUserEventIce) {
            return true;
        }
        log.warn((Object)("unknown event type [" + (Object)((Object)event) + "]"));
        return false;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("addFriends [").append(this.addFriends).append("] chatroomCreation [").append(this.chatroomCreation).append("] contentpurchased [").append(this.contentPurchased).append("] photosPublished [").append(this.photosPublished).append("] profileChanges [").append(this.profileChanges).append("] statusUpdates [").append(this.statusUpdates).append("]").append("] virtual gifts [").append(this.virtualGifting).append("]");
        return builder.toString();
    }

    public EventPrivacySettingIce toEventPrivacySettingIce() {
        return new EventPrivacySettingIce(this.statusUpdates, this.profileChanges, this.addFriends, this.photosPublished, this.contentPurchased, this.chatroomCreation, this.virtualGifting);
    }

    public static EventPrivacySetting fromEventPrivacySettingIce(EventPrivacySettingIce mask) {
        return new EventPrivacySetting(mask.statusUpdates, mask.profileChanges, mask.addFriends, mask.photosPublished, mask.contentPurchased, mask.chatroomCreation, mask.virtualGifting);
    }
}

