/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.FacetNotExistException
 *  Ice.LocalException
 *  Ice.ObjectPrx
 *  Ice.ObjectPrxHelperBase
 *  Ice._ObjectDel
 *  Ice._ObjectDelD
 *  Ice._ObjectDelM
 *  IceInternal.BasicStream
 *  IceInternal.LocalExceptionWrapper
 */
package com.projectgoth.fusion.slice;

import Ice.FacetNotExistException;
import Ice.LocalException;
import Ice.ObjectPrx;
import Ice.ObjectPrxHelperBase;
import Ice._ObjectDel;
import Ice._ObjectDelD;
import Ice._ObjectDelM;
import IceInternal.BasicStream;
import IceInternal.LocalExceptionWrapper;
import com.projectgoth.fusion.slice.ConnectionPrx;
import com.projectgoth.fusion.slice.EventPrivacySettingIce;
import com.projectgoth.fusion.slice.EventSystemPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.UserEventIce;
import com.projectgoth.fusion.slice._EventSystemDel;
import com.projectgoth.fusion.slice._EventSystemDelD;
import com.projectgoth.fusion.slice._EventSystemDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class EventSystemPrxHelper
extends ObjectPrxHelperBase
implements EventSystemPrx {
    @Override
    public void addedFriend(String username, String friend) throws FusionException {
        this.addedFriend(username, friend, null, false);
    }

    @Override
    public void addedFriend(String username, String friend, Map<String, String> __ctx) throws FusionException {
        this.addedFriend(username, friend, __ctx, true);
    }

    private void addedFriend(String username, String friend, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("addedFriend");
                __delBase = this.__getDelegate(false);
                _EventSystemDel __del = (_EventSystemDel)__delBase;
                __del.addedFriend(username, friend, __ctx);
                return;
            }
            catch (LocalExceptionWrapper __ex) {
                this.__handleExceptionWrapper(__delBase, __ex, null);
                continue;
            }
            catch (LocalException __ex) {
                __cnt = this.__handleException(__delBase, __ex, null, __cnt);
                continue;
            }
            break;
        }
    }

    @Override
    public void createdPublicChatroom(String username, String chatroomName) throws FusionException {
        this.createdPublicChatroom(username, chatroomName, null, false);
    }

    @Override
    public void createdPublicChatroom(String username, String chatroomName, Map<String, String> __ctx) throws FusionException {
        this.createdPublicChatroom(username, chatroomName, __ctx, true);
    }

    private void createdPublicChatroom(String username, String chatroomName, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("createdPublicChatroom");
                __delBase = this.__getDelegate(false);
                _EventSystemDel __del = (_EventSystemDel)__delBase;
                __del.createdPublicChatroom(username, chatroomName, __ctx);
                return;
            }
            catch (LocalExceptionWrapper __ex) {
                this.__handleExceptionWrapper(__delBase, __ex, null);
                continue;
            }
            catch (LocalException __ex) {
                __cnt = this.__handleException(__delBase, __ex, null, __cnt);
                continue;
            }
            break;
        }
    }

    @Override
    public void deleteUserEvents(String username) throws FusionException {
        this.deleteUserEvents(username, null, false);
    }

    @Override
    public void deleteUserEvents(String username, Map<String, String> __ctx) throws FusionException {
        this.deleteUserEvents(username, __ctx, true);
    }

    private void deleteUserEvents(String username, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("deleteUserEvents");
                __delBase = this.__getDelegate(false);
                _EventSystemDel __del = (_EventSystemDel)__delBase;
                __del.deleteUserEvents(username, __ctx);
                return;
            }
            catch (LocalExceptionWrapper __ex) {
                this.__handleExceptionWrapper(__delBase, __ex, null);
                continue;
            }
            catch (LocalException __ex) {
                __cnt = this.__handleException(__delBase, __ex, null, __cnt);
                continue;
            }
            break;
        }
    }

    @Override
    public void genericApplicationEvent(String username, String appId, String text, Map<String, String> customDeviceURL) throws FusionException {
        this.genericApplicationEvent(username, appId, text, customDeviceURL, null, false);
    }

    @Override
    public void genericApplicationEvent(String username, String appId, String text, Map<String, String> customDeviceURL, Map<String, String> __ctx) throws FusionException {
        this.genericApplicationEvent(username, appId, text, customDeviceURL, __ctx, true);
    }

    private void genericApplicationEvent(String username, String appId, String text, Map<String, String> customDeviceURL, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("genericApplicationEvent");
                __delBase = this.__getDelegate(false);
                _EventSystemDel __del = (_EventSystemDel)__delBase;
                __del.genericApplicationEvent(username, appId, text, customDeviceURL, __ctx);
                return;
            }
            catch (LocalExceptionWrapper __ex) {
                this.__handleExceptionWrapper(__delBase, __ex, null);
                continue;
            }
            catch (LocalException __ex) {
                __cnt = this.__handleException(__delBase, __ex, null, __cnt);
                continue;
            }
            break;
        }
    }

    @Override
    public EventPrivacySettingIce getPublishingPrivacyMask(String username) throws FusionException {
        return this.getPublishingPrivacyMask(username, null, false);
    }

    @Override
    public EventPrivacySettingIce getPublishingPrivacyMask(String username, Map<String, String> __ctx) throws FusionException {
        return this.getPublishingPrivacyMask(username, __ctx, true);
    }

    private EventPrivacySettingIce getPublishingPrivacyMask(String username, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getPublishingPrivacyMask");
                __delBase = this.__getDelegate(false);
                _EventSystemDel __del = (_EventSystemDel)__delBase;
                return __del.getPublishingPrivacyMask(username, __ctx);
            }
            catch (LocalExceptionWrapper __ex) {
                this.__handleExceptionWrapper(__delBase, __ex, null);
                continue;
            }
            catch (LocalException __ex) {
                __cnt = this.__handleException(__delBase, __ex, null, __cnt);
                continue;
            }
            break;
        }
    }

    @Override
    public EventPrivacySettingIce getReceivingPrivacyMask(String username) throws FusionException {
        return this.getReceivingPrivacyMask(username, null, false);
    }

    @Override
    public EventPrivacySettingIce getReceivingPrivacyMask(String username, Map<String, String> __ctx) throws FusionException {
        return this.getReceivingPrivacyMask(username, __ctx, true);
    }

    private EventPrivacySettingIce getReceivingPrivacyMask(String username, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getReceivingPrivacyMask");
                __delBase = this.__getDelegate(false);
                _EventSystemDel __del = (_EventSystemDel)__delBase;
                return __del.getReceivingPrivacyMask(username, __ctx);
            }
            catch (LocalExceptionWrapper __ex) {
                this.__handleExceptionWrapper(__delBase, __ex, null);
                continue;
            }
            catch (LocalException __ex) {
                __cnt = this.__handleException(__delBase, __ex, null, __cnt);
                continue;
            }
            break;
        }
    }

    @Override
    public UserEventIce[] getUserEventsForUser(String username) throws FusionException {
        return this.getUserEventsForUser(username, null, false);
    }

    @Override
    public UserEventIce[] getUserEventsForUser(String username, Map<String, String> __ctx) throws FusionException {
        return this.getUserEventsForUser(username, __ctx, true);
    }

    private UserEventIce[] getUserEventsForUser(String username, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getUserEventsForUser");
                __delBase = this.__getDelegate(false);
                _EventSystemDel __del = (_EventSystemDel)__delBase;
                return __del.getUserEventsForUser(username, __ctx);
            }
            catch (LocalExceptionWrapper __ex) {
                this.__handleExceptionWrapper(__delBase, __ex, null);
                continue;
            }
            catch (LocalException __ex) {
                __cnt = this.__handleException(__delBase, __ex, null, __cnt);
                continue;
            }
            break;
        }
    }

    @Override
    public UserEventIce[] getUserEventsGeneratedByUser(String username) throws FusionException {
        return this.getUserEventsGeneratedByUser(username, null, false);
    }

    @Override
    public UserEventIce[] getUserEventsGeneratedByUser(String username, Map<String, String> __ctx) throws FusionException {
        return this.getUserEventsGeneratedByUser(username, __ctx, true);
    }

    private UserEventIce[] getUserEventsGeneratedByUser(String username, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getUserEventsGeneratedByUser");
                __delBase = this.__getDelegate(false);
                _EventSystemDel __del = (_EventSystemDel)__delBase;
                return __del.getUserEventsGeneratedByUser(username, __ctx);
            }
            catch (LocalExceptionWrapper __ex) {
                this.__handleExceptionWrapper(__delBase, __ex, null);
                continue;
            }
            catch (LocalException __ex) {
                __cnt = this.__handleException(__delBase, __ex, null, __cnt);
                continue;
            }
            break;
        }
    }

    @Override
    public void giftShowerEvent(String username, String recipient, String giftName, int virtualGiftReceivedId, int totalRecipients) {
        this.giftShowerEvent(username, recipient, giftName, virtualGiftReceivedId, totalRecipients, null, false);
    }

    @Override
    public void giftShowerEvent(String username, String recipient, String giftName, int virtualGiftReceivedId, int totalRecipients, Map<String, String> __ctx) {
        this.giftShowerEvent(username, recipient, giftName, virtualGiftReceivedId, totalRecipients, __ctx, true);
    }

    private void giftShowerEvent(String username, String recipient, String giftName, int virtualGiftReceivedId, int totalRecipients, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _EventSystemDel __del = (_EventSystemDel)__delBase;
                __del.giftShowerEvent(username, recipient, giftName, virtualGiftReceivedId, totalRecipients, __ctx);
                return;
            }
            catch (LocalExceptionWrapper __ex) {
                this.__handleExceptionWrapper(__delBase, __ex, null);
                continue;
            }
            catch (LocalException __ex) {
                __cnt = this.__handleException(__delBase, __ex, null, __cnt);
                continue;
            }
            break;
        }
    }

    @Override
    public void groupAnnouncement(String username, int groupId, int groupAnnoucementId) throws FusionException {
        this.groupAnnouncement(username, groupId, groupAnnoucementId, null, false);
    }

    @Override
    public void groupAnnouncement(String username, int groupId, int groupAnnoucementId, Map<String, String> __ctx) throws FusionException {
        this.groupAnnouncement(username, groupId, groupAnnoucementId, __ctx, true);
    }

    private void groupAnnouncement(String username, int groupId, int groupAnnoucementId, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("groupAnnouncement");
                __delBase = this.__getDelegate(false);
                _EventSystemDel __del = (_EventSystemDel)__delBase;
                __del.groupAnnouncement(username, groupId, groupAnnoucementId, __ctx);
                return;
            }
            catch (LocalExceptionWrapper __ex) {
                this.__handleExceptionWrapper(__delBase, __ex, null);
                continue;
            }
            catch (LocalException __ex) {
                __cnt = this.__handleException(__delBase, __ex, null, __cnt);
                continue;
            }
            break;
        }
    }

    @Override
    public void groupDonation(String username, int groupId) throws FusionException {
        this.groupDonation(username, groupId, null, false);
    }

    @Override
    public void groupDonation(String username, int groupId, Map<String, String> __ctx) throws FusionException {
        this.groupDonation(username, groupId, __ctx, true);
    }

    private void groupDonation(String username, int groupId, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("groupDonation");
                __delBase = this.__getDelegate(false);
                _EventSystemDel __del = (_EventSystemDel)__delBase;
                __del.groupDonation(username, groupId, __ctx);
                return;
            }
            catch (LocalExceptionWrapper __ex) {
                this.__handleExceptionWrapper(__delBase, __ex, null);
                continue;
            }
            catch (LocalException __ex) {
                __cnt = this.__handleException(__delBase, __ex, null, __cnt);
                continue;
            }
            break;
        }
    }

    @Override
    public void groupJoined(String username, int groupId) throws FusionException {
        this.groupJoined(username, groupId, null, false);
    }

    @Override
    public void groupJoined(String username, int groupId, Map<String, String> __ctx) throws FusionException {
        this.groupJoined(username, groupId, __ctx, true);
    }

    private void groupJoined(String username, int groupId, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("groupJoined");
                __delBase = this.__getDelegate(false);
                _EventSystemDel __del = (_EventSystemDel)__delBase;
                __del.groupJoined(username, groupId, __ctx);
                return;
            }
            catch (LocalExceptionWrapper __ex) {
                this.__handleExceptionWrapper(__delBase, __ex, null);
                continue;
            }
            catch (LocalException __ex) {
                __cnt = this.__handleException(__delBase, __ex, null, __cnt);
                continue;
            }
            break;
        }
    }

    @Override
    public void madeGroupUserPost(String username, int userPostId, int groupId) throws FusionException {
        this.madeGroupUserPost(username, userPostId, groupId, null, false);
    }

    @Override
    public void madeGroupUserPost(String username, int userPostId, int groupId, Map<String, String> __ctx) throws FusionException {
        this.madeGroupUserPost(username, userPostId, groupId, __ctx, true);
    }

    private void madeGroupUserPost(String username, int userPostId, int groupId, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("madeGroupUserPost");
                __delBase = this.__getDelegate(false);
                _EventSystemDel __del = (_EventSystemDel)__delBase;
                __del.madeGroupUserPost(username, userPostId, groupId, __ctx);
                return;
            }
            catch (LocalExceptionWrapper __ex) {
                this.__handleExceptionWrapper(__delBase, __ex, null);
                continue;
            }
            catch (LocalException __ex) {
                __cnt = this.__handleException(__delBase, __ex, null, __cnt);
                continue;
            }
            break;
        }
    }

    @Override
    public void madePhotoPublic(String username, int scrapbookid, String title) throws FusionException {
        this.madePhotoPublic(username, scrapbookid, title, null, false);
    }

    @Override
    public void madePhotoPublic(String username, int scrapbookid, String title, Map<String, String> __ctx) throws FusionException {
        this.madePhotoPublic(username, scrapbookid, title, __ctx, true);
    }

    private void madePhotoPublic(String username, int scrapbookid, String title, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("madePhotoPublic");
                __delBase = this.__getDelegate(false);
                _EventSystemDel __del = (_EventSystemDel)__delBase;
                __del.madePhotoPublic(username, scrapbookid, title, __ctx);
                return;
            }
            catch (LocalExceptionWrapper __ex) {
                this.__handleExceptionWrapper(__delBase, __ex, null);
                continue;
            }
            catch (LocalException __ex) {
                __cnt = this.__handleException(__delBase, __ex, null, __cnt);
                continue;
            }
            break;
        }
    }

    @Override
    public void purchasedVirtualGoods(String username, byte itemType, int itemid, String itemName) throws FusionException {
        this.purchasedVirtualGoods(username, itemType, itemid, itemName, null, false);
    }

    @Override
    public void purchasedVirtualGoods(String username, byte itemType, int itemid, String itemName, Map<String, String> __ctx) throws FusionException {
        this.purchasedVirtualGoods(username, itemType, itemid, itemName, __ctx, true);
    }

    private void purchasedVirtualGoods(String username, byte itemType, int itemid, String itemName, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("purchasedVirtualGoods");
                __delBase = this.__getDelegate(false);
                _EventSystemDel __del = (_EventSystemDel)__delBase;
                __del.purchasedVirtualGoods(username, itemType, itemid, itemName, __ctx);
                return;
            }
            catch (LocalExceptionWrapper __ex) {
                this.__handleExceptionWrapper(__delBase, __ex, null);
                continue;
            }
            catch (LocalException __ex) {
                __cnt = this.__handleException(__delBase, __ex, null, __cnt);
                continue;
            }
            break;
        }
    }

    @Override
    public void setProfileStatus(String username, String status) throws FusionException {
        this.setProfileStatus(username, status, null, false);
    }

    @Override
    public void setProfileStatus(String username, String status, Map<String, String> __ctx) throws FusionException {
        this.setProfileStatus(username, status, __ctx, true);
    }

    private void setProfileStatus(String username, String status, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("setProfileStatus");
                __delBase = this.__getDelegate(false);
                _EventSystemDel __del = (_EventSystemDel)__delBase;
                __del.setProfileStatus(username, status, __ctx);
                return;
            }
            catch (LocalExceptionWrapper __ex) {
                this.__handleExceptionWrapper(__delBase, __ex, null);
                continue;
            }
            catch (LocalException __ex) {
                __cnt = this.__handleException(__delBase, __ex, null, __cnt);
                continue;
            }
            break;
        }
    }

    @Override
    public void setPublishingPrivacyMask(String username, EventPrivacySettingIce mask) throws FusionException {
        this.setPublishingPrivacyMask(username, mask, null, false);
    }

    @Override
    public void setPublishingPrivacyMask(String username, EventPrivacySettingIce mask, Map<String, String> __ctx) throws FusionException {
        this.setPublishingPrivacyMask(username, mask, __ctx, true);
    }

    private void setPublishingPrivacyMask(String username, EventPrivacySettingIce mask, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("setPublishingPrivacyMask");
                __delBase = this.__getDelegate(false);
                _EventSystemDel __del = (_EventSystemDel)__delBase;
                __del.setPublishingPrivacyMask(username, mask, __ctx);
                return;
            }
            catch (LocalExceptionWrapper __ex) {
                this.__handleExceptionWrapper(__delBase, __ex, null);
                continue;
            }
            catch (LocalException __ex) {
                __cnt = this.__handleException(__delBase, __ex, null, __cnt);
                continue;
            }
            break;
        }
    }

    @Override
    public void setReceivingPrivacyMask(String username, EventPrivacySettingIce mask) throws FusionException {
        this.setReceivingPrivacyMask(username, mask, null, false);
    }

    @Override
    public void setReceivingPrivacyMask(String username, EventPrivacySettingIce mask, Map<String, String> __ctx) throws FusionException {
        this.setReceivingPrivacyMask(username, mask, __ctx, true);
    }

    private void setReceivingPrivacyMask(String username, EventPrivacySettingIce mask, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("setReceivingPrivacyMask");
                __delBase = this.__getDelegate(false);
                _EventSystemDel __del = (_EventSystemDel)__delBase;
                __del.setReceivingPrivacyMask(username, mask, __ctx);
                return;
            }
            catch (LocalExceptionWrapper __ex) {
                this.__handleExceptionWrapper(__delBase, __ex, null);
                continue;
            }
            catch (LocalException __ex) {
                __cnt = this.__handleException(__delBase, __ex, null, __cnt);
                continue;
            }
            break;
        }
    }

    @Override
    public void streamEventsToLoggingInUser(String username, ConnectionPrx connectionProxy) throws FusionException {
        this.streamEventsToLoggingInUser(username, connectionProxy, null, false);
    }

    @Override
    public void streamEventsToLoggingInUser(String username, ConnectionPrx connectionProxy, Map<String, String> __ctx) throws FusionException {
        this.streamEventsToLoggingInUser(username, connectionProxy, __ctx, true);
    }

    private void streamEventsToLoggingInUser(String username, ConnectionPrx connectionProxy, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("streamEventsToLoggingInUser");
                __delBase = this.__getDelegate(false);
                _EventSystemDel __del = (_EventSystemDel)__delBase;
                __del.streamEventsToLoggingInUser(username, connectionProxy, __ctx);
                return;
            }
            catch (LocalExceptionWrapper __ex) {
                this.__handleExceptionWrapper(__delBase, __ex, null);
                continue;
            }
            catch (LocalException __ex) {
                __cnt = this.__handleException(__delBase, __ex, null, __cnt);
                continue;
            }
            break;
        }
    }

    @Override
    public void updateAllowList(String username, String[] watchers) throws FusionException {
        this.updateAllowList(username, watchers, null, false);
    }

    @Override
    public void updateAllowList(String username, String[] watchers, Map<String, String> __ctx) throws FusionException {
        this.updateAllowList(username, watchers, __ctx, true);
    }

    private void updateAllowList(String username, String[] watchers, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("updateAllowList");
                __delBase = this.__getDelegate(false);
                _EventSystemDel __del = (_EventSystemDel)__delBase;
                __del.updateAllowList(username, watchers, __ctx);
                return;
            }
            catch (LocalExceptionWrapper __ex) {
                this.__handleExceptionWrapper(__delBase, __ex, null);
                continue;
            }
            catch (LocalException __ex) {
                __cnt = this.__handleException(__delBase, __ex, null, __cnt);
                continue;
            }
            break;
        }
    }

    @Override
    public void updatedProfile(String username) throws FusionException {
        this.updatedProfile(username, null, false);
    }

    @Override
    public void updatedProfile(String username, Map<String, String> __ctx) throws FusionException {
        this.updatedProfile(username, __ctx, true);
    }

    private void updatedProfile(String username, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("updatedProfile");
                __delBase = this.__getDelegate(false);
                _EventSystemDel __del = (_EventSystemDel)__delBase;
                __del.updatedProfile(username, __ctx);
                return;
            }
            catch (LocalExceptionWrapper __ex) {
                this.__handleExceptionWrapper(__delBase, __ex, null);
                continue;
            }
            catch (LocalException __ex) {
                __cnt = this.__handleException(__delBase, __ex, null, __cnt);
                continue;
            }
            break;
        }
    }

    @Override
    public void userWallPost(String username, String wallOwnerUsername, String postContent, int userWallPostId) throws FusionException {
        this.userWallPost(username, wallOwnerUsername, postContent, userWallPostId, null, false);
    }

    @Override
    public void userWallPost(String username, String wallOwnerUsername, String postContent, int userWallPostId, Map<String, String> __ctx) throws FusionException {
        this.userWallPost(username, wallOwnerUsername, postContent, userWallPostId, __ctx, true);
    }

    private void userWallPost(String username, String wallOwnerUsername, String postContent, int userWallPostId, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("userWallPost");
                __delBase = this.__getDelegate(false);
                _EventSystemDel __del = (_EventSystemDel)__delBase;
                __del.userWallPost(username, wallOwnerUsername, postContent, userWallPostId, __ctx);
                return;
            }
            catch (LocalExceptionWrapper __ex) {
                this.__handleExceptionWrapper(__delBase, __ex, null);
                continue;
            }
            catch (LocalException __ex) {
                __cnt = this.__handleException(__delBase, __ex, null, __cnt);
                continue;
            }
            break;
        }
    }

    @Override
    public void virtualGift(String username, String recipient, String giftName, int virtualGiftReceivedId) {
        this.virtualGift(username, recipient, giftName, virtualGiftReceivedId, null, false);
    }

    @Override
    public void virtualGift(String username, String recipient, String giftName, int virtualGiftReceivedId, Map<String, String> __ctx) {
        this.virtualGift(username, recipient, giftName, virtualGiftReceivedId, __ctx, true);
    }

    private void virtualGift(String username, String recipient, String giftName, int virtualGiftReceivedId, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _EventSystemDel __del = (_EventSystemDel)__delBase;
                __del.virtualGift(username, recipient, giftName, virtualGiftReceivedId, __ctx);
                return;
            }
            catch (LocalExceptionWrapper __ex) {
                this.__handleExceptionWrapper(__delBase, __ex, null);
                continue;
            }
            catch (LocalException __ex) {
                __cnt = this.__handleException(__delBase, __ex, null, __cnt);
                continue;
            }
            break;
        }
    }

    public static EventSystemPrx checkedCast(ObjectPrx __obj) {
        EventSystemPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (EventSystemPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::EventSystem")) break block3;
                    EventSystemPrxHelper __h = new EventSystemPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static EventSystemPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        EventSystemPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (EventSystemPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::EventSystem", __ctx)) break block3;
                    EventSystemPrxHelper __h = new EventSystemPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static EventSystemPrx checkedCast(ObjectPrx __obj, String __facet) {
        EventSystemPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::EventSystem")) {
                    EventSystemPrxHelper __h = new EventSystemPrxHelper();
                    __h.__copyFrom(__bb);
                    __d = __h;
                }
            }
            catch (FacetNotExistException ex) {
                // empty catch block
            }
        }
        return __d;
    }

    public static EventSystemPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        EventSystemPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::EventSystem", __ctx)) {
                    EventSystemPrxHelper __h = new EventSystemPrxHelper();
                    __h.__copyFrom(__bb);
                    __d = __h;
                }
            }
            catch (FacetNotExistException ex) {
                // empty catch block
            }
        }
        return __d;
    }

    public static EventSystemPrx uncheckedCast(ObjectPrx __obj) {
        EventSystemPrx __d = null;
        if (__obj != null) {
            try {
                __d = (EventSystemPrx)__obj;
            }
            catch (ClassCastException ex) {
                EventSystemPrxHelper __h = new EventSystemPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static EventSystemPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        EventSystemPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            EventSystemPrxHelper __h = new EventSystemPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _EventSystemDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _EventSystemDelD();
    }

    public static void __write(BasicStream __os, EventSystemPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static EventSystemPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            EventSystemPrxHelper result = new EventSystemPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

