/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 *  Ice.TieBase
 */
package com.projectgoth.fusion.slice;

import Ice.Current;
import Ice.TieBase;
import com.projectgoth.fusion.slice.ConnectionPrx;
import com.projectgoth.fusion.slice.EventPrivacySettingIce;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.UserEventIce;
import com.projectgoth.fusion.slice._EventSystemDisp;
import com.projectgoth.fusion.slice._EventSystemOperations;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class _EventSystemTie
extends _EventSystemDisp
implements TieBase {
    private _EventSystemOperations _ice_delegate;

    public _EventSystemTie() {
    }

    public _EventSystemTie(_EventSystemOperations delegate) {
        this._ice_delegate = delegate;
    }

    public Object ice_delegate() {
        return this._ice_delegate;
    }

    public void ice_delegate(Object delegate) {
        this._ice_delegate = (_EventSystemOperations)delegate;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        if (!(rhs instanceof _EventSystemTie)) {
            return false;
        }
        return this._ice_delegate.equals(((_EventSystemTie)rhs)._ice_delegate);
    }

    public int hashCode() {
        return this._ice_delegate.hashCode();
    }

    @Override
    public void addedFriend(String username, String friend, Current __current) throws FusionException {
        this._ice_delegate.addedFriend(username, friend, __current);
    }

    @Override
    public void createdPublicChatroom(String username, String chatroomName, Current __current) throws FusionException {
        this._ice_delegate.createdPublicChatroom(username, chatroomName, __current);
    }

    @Override
    public void deleteUserEvents(String username, Current __current) throws FusionException {
        this._ice_delegate.deleteUserEvents(username, __current);
    }

    @Override
    public void genericApplicationEvent(String username, String appId, String text, Map<String, String> customDeviceURL, Current __current) throws FusionException {
        this._ice_delegate.genericApplicationEvent(username, appId, text, customDeviceURL, __current);
    }

    @Override
    public EventPrivacySettingIce getPublishingPrivacyMask(String username, Current __current) throws FusionException {
        return this._ice_delegate.getPublishingPrivacyMask(username, __current);
    }

    @Override
    public EventPrivacySettingIce getReceivingPrivacyMask(String username, Current __current) throws FusionException {
        return this._ice_delegate.getReceivingPrivacyMask(username, __current);
    }

    @Override
    public UserEventIce[] getUserEventsForUser(String username, Current __current) throws FusionException {
        return this._ice_delegate.getUserEventsForUser(username, __current);
    }

    @Override
    public UserEventIce[] getUserEventsGeneratedByUser(String username, Current __current) throws FusionException {
        return this._ice_delegate.getUserEventsGeneratedByUser(username, __current);
    }

    @Override
    public void giftShowerEvent(String username, String recipient, String giftName, int virtualGiftReceivedId, int totalRecipients, Current __current) {
        this._ice_delegate.giftShowerEvent(username, recipient, giftName, virtualGiftReceivedId, totalRecipients, __current);
    }

    @Override
    public void groupAnnouncement(String username, int groupId, int groupAnnoucementId, Current __current) throws FusionException {
        this._ice_delegate.groupAnnouncement(username, groupId, groupAnnoucementId, __current);
    }

    @Override
    public void groupDonation(String username, int groupId, Current __current) throws FusionException {
        this._ice_delegate.groupDonation(username, groupId, __current);
    }

    @Override
    public void groupJoined(String username, int groupId, Current __current) throws FusionException {
        this._ice_delegate.groupJoined(username, groupId, __current);
    }

    @Override
    public void madeGroupUserPost(String username, int userPostId, int groupId, Current __current) throws FusionException {
        this._ice_delegate.madeGroupUserPost(username, userPostId, groupId, __current);
    }

    @Override
    public void madePhotoPublic(String username, int scrapbookid, String title, Current __current) throws FusionException {
        this._ice_delegate.madePhotoPublic(username, scrapbookid, title, __current);
    }

    @Override
    public void purchasedVirtualGoods(String username, byte itemType, int itemid, String itemName, Current __current) throws FusionException {
        this._ice_delegate.purchasedVirtualGoods(username, itemType, itemid, itemName, __current);
    }

    @Override
    public void setProfileStatus(String username, String status, Current __current) throws FusionException {
        this._ice_delegate.setProfileStatus(username, status, __current);
    }

    @Override
    public void setPublishingPrivacyMask(String username, EventPrivacySettingIce mask, Current __current) throws FusionException {
        this._ice_delegate.setPublishingPrivacyMask(username, mask, __current);
    }

    @Override
    public void setReceivingPrivacyMask(String username, EventPrivacySettingIce mask, Current __current) throws FusionException {
        this._ice_delegate.setReceivingPrivacyMask(username, mask, __current);
    }

    @Override
    public void streamEventsToLoggingInUser(String username, ConnectionPrx connectionProxy, Current __current) throws FusionException {
        this._ice_delegate.streamEventsToLoggingInUser(username, connectionProxy, __current);
    }

    @Override
    public void updateAllowList(String username, String[] watchers, Current __current) throws FusionException {
        this._ice_delegate.updateAllowList(username, watchers, __current);
    }

    @Override
    public void updatedProfile(String username, Current __current) throws FusionException {
        this._ice_delegate.updatedProfile(username, __current);
    }

    @Override
    public void userWallPost(String username, String wallOwnerUsername, String postContent, int userWallPostId, Current __current) throws FusionException {
        this._ice_delegate.userWallPost(username, wallOwnerUsername, postContent, userWallPostId, __current);
    }

    @Override
    public void virtualGift(String username, String recipient, String giftName, int virtualGiftReceivedId, Current __current) {
        this._ice_delegate.virtualGift(username, recipient, giftName, virtualGiftReceivedId, __current);
    }
}

