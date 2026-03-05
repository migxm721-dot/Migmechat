/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.userevent.domain;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.slice.AddingFriendUserEventIce;
import com.projectgoth.fusion.slice.AddingMultipleFriendsUserEventIce;
import com.projectgoth.fusion.slice.AddingTwoFriendsUserEventIce;
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
import com.projectgoth.fusion.userevent.domain.AddingFriendUserEvent;
import com.projectgoth.fusion.userevent.domain.AddingMultipleFriendsUserEvent;
import com.projectgoth.fusion.userevent.domain.AddingTwoFriendsUserEvent;
import com.projectgoth.fusion.userevent.domain.CreatedChatroomUserEvent;
import com.projectgoth.fusion.userevent.domain.GenericApplicationUserEvent;
import com.projectgoth.fusion.userevent.domain.GiftShowerUserEvent;
import com.projectgoth.fusion.userevent.domain.GroupAnnouncementUserEvent;
import com.projectgoth.fusion.userevent.domain.GroupDonationUserEvent;
import com.projectgoth.fusion.userevent.domain.GroupJoinedUserEvent;
import com.projectgoth.fusion.userevent.domain.GroupUserPostUserEvent;
import com.projectgoth.fusion.userevent.domain.PhotoUploadedUserEvent;
import com.projectgoth.fusion.userevent.domain.ProfileUpdatedUserEvent;
import com.projectgoth.fusion.userevent.domain.PurchasedVirtualGoodsUserEvent;
import com.projectgoth.fusion.userevent.domain.ShortTextStatusUserEvent;
import com.projectgoth.fusion.userevent.domain.UserEvent;
import com.projectgoth.fusion.userevent.domain.UserWallPostUserEvent;
import com.projectgoth.fusion.userevent.domain.VirtualGiftUserEvent;
import org.apache.log4j.Logger;

public abstract class UserEventFactory {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(UserEventFactory.class));

    public static UserEvent getUserEvent(UserEventIce event) {
        if (event instanceof AddingFriendUserEventIce) {
            return new AddingFriendUserEvent((AddingFriendUserEventIce)event);
        }
        if (event instanceof AddingTwoFriendsUserEventIce) {
            return new AddingTwoFriendsUserEvent((AddingTwoFriendsUserEventIce)event);
        }
        if (event instanceof AddingMultipleFriendsUserEventIce) {
            return new AddingMultipleFriendsUserEvent((AddingMultipleFriendsUserEventIce)event);
        }
        if (event instanceof PurchasedVirtualGoodsUserEventIce) {
            return new PurchasedVirtualGoodsUserEvent((PurchasedVirtualGoodsUserEventIce)event);
        }
        if (event instanceof ShortTextStatusUserEventIce) {
            return new ShortTextStatusUserEvent((ShortTextStatusUserEventIce)event);
        }
        if (event instanceof ProfileUpdatedUserEventIce) {
            return new ProfileUpdatedUserEvent((ProfileUpdatedUserEventIce)event);
        }
        if (event instanceof PhotoUploadedUserEventIce) {
            return new PhotoUploadedUserEvent((PhotoUploadedUserEventIce)event);
        }
        if (event instanceof CreatedChatroomUserEventIce) {
            return new CreatedChatroomUserEvent((CreatedChatroomUserEventIce)event);
        }
        if (event instanceof VirtualGiftUserEventIce) {
            return new VirtualGiftUserEvent((VirtualGiftUserEventIce)event);
        }
        if (event instanceof UserWallPostUserEventIce) {
            return new UserWallPostUserEvent((UserWallPostUserEventIce)event);
        }
        if (event instanceof GroupDonationUserEventIce) {
            return new GroupDonationUserEvent((GroupDonationUserEventIce)event);
        }
        if (event instanceof GroupJoinedUserEventIce) {
            return new GroupJoinedUserEvent((GroupJoinedUserEventIce)event);
        }
        if (event instanceof GroupAnnouncementUserEventIce) {
            return new GroupAnnouncementUserEvent((GroupAnnouncementUserEventIce)event);
        }
        if (event instanceof GroupUserPostUserEventIce) {
            return new GroupUserPostUserEvent((GroupUserPostUserEventIce)event);
        }
        if (event instanceof GenericApplicationUserEventIce) {
            return new GenericApplicationUserEvent((GenericApplicationUserEventIce)event);
        }
        if (event instanceof GiftShowerUserEventIce) {
            return new GiftShowerUserEvent((GiftShowerUserEventIce)event);
        }
        log.warn((Object)("unknown event type " + event.ice_id()));
        return new UserEvent(event);
    }
}

