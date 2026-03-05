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
import com.projectgoth.fusion.slice.ChatListIce;
import com.projectgoth.fusion.slice.ConnectionPrx;
import com.projectgoth.fusion.slice.ContactDataIce;
import com.projectgoth.fusion.slice.ContactGroupDataIce;
import com.projectgoth.fusion.slice.ContactList;
import com.projectgoth.fusion.slice.Credential;
import com.projectgoth.fusion.slice.CurrencyDataIce;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.Message;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.MessageStatusEventIce;
import com.projectgoth.fusion.slice.PresenceAndCapabilityIce;
import com.projectgoth.fusion.slice.SessionPrx;
import com.projectgoth.fusion.slice.UserDataIce;
import com.projectgoth.fusion.slice.UserErrorResponse;
import com.projectgoth.fusion.slice.UserEventIce;
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.slice._UserDel;
import com.projectgoth.fusion.slice._UserDelD;
import com.projectgoth.fusion.slice._UserDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class UserPrxHelper
extends ObjectPrxHelperBase
implements UserPrx {
    @Override
    public ContactDataIce acceptContactRequest(ContactDataIce contact, UserPrx contactProxy, int inviterContactListVersion, int inviteeContactListVersion) {
        return this.acceptContactRequest(contact, contactProxy, inviterContactListVersion, inviteeContactListVersion, null, false);
    }

    @Override
    public ContactDataIce acceptContactRequest(ContactDataIce contact, UserPrx contactProxy, int inviterContactListVersion, int inviteeContactListVersion, Map<String, String> __ctx) {
        return this.acceptContactRequest(contact, contactProxy, inviterContactListVersion, inviteeContactListVersion, __ctx, true);
    }

    private ContactDataIce acceptContactRequest(ContactDataIce contact, UserPrx contactProxy, int inviterContactListVersion, int inviteeContactListVersion, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("acceptContactRequest");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                return __del.acceptContactRequest(contact, contactProxy, inviterContactListVersion, inviteeContactListVersion, __ctx);
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
    public void accountBalanceChanged(double balance, double fundedBalance, CurrencyDataIce currency) {
        this.accountBalanceChanged(balance, fundedBalance, currency, null, false);
    }

    @Override
    public void accountBalanceChanged(double balance, double fundedBalance, CurrencyDataIce currency, Map<String, String> __ctx) {
        this.accountBalanceChanged(balance, fundedBalance, currency, __ctx, true);
    }

    private void accountBalanceChanged(double balance, double fundedBalance, CurrencyDataIce currency, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.accountBalanceChanged(balance, fundedBalance, currency, __ctx);
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
    public void addContact(ContactDataIce contact, int contactListVersion) {
        this.addContact(contact, contactListVersion, null, false);
    }

    @Override
    public void addContact(ContactDataIce contact, int contactListVersion, Map<String, String> __ctx) {
        this.addContact(contact, contactListVersion, __ctx, true);
    }

    private void addContact(ContactDataIce contact, int contactListVersion, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.addContact(contact, contactListVersion, __ctx);
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
    public void addPendingContact(String username) {
        this.addPendingContact(username, null, false);
    }

    @Override
    public void addPendingContact(String username, Map<String, String> __ctx) {
        this.addPendingContact(username, __ctx, true);
    }

    private void addPendingContact(String username, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.addPendingContact(username, __ctx);
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
    public void addToContactAndBroadcastLists(ContactDataIce contact, int contactListVersion) {
        this.addToContactAndBroadcastLists(contact, contactListVersion, null, false);
    }

    @Override
    public void addToContactAndBroadcastLists(ContactDataIce contact, int contactListVersion, Map<String, String> __ctx) {
        this.addToContactAndBroadcastLists(contact, contactListVersion, __ctx, true);
    }

    private void addToContactAndBroadcastLists(ContactDataIce contact, int contactListVersion, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.addToContactAndBroadcastLists(contact, contactListVersion, __ctx);
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
    public void addToCurrentChatroomList(String chatroom) throws FusionException {
        this.addToCurrentChatroomList(chatroom, null, false);
    }

    @Override
    public void addToCurrentChatroomList(String chatroom, Map<String, String> __ctx) throws FusionException {
        this.addToCurrentChatroomList(chatroom, __ctx, true);
    }

    private void addToCurrentChatroomList(String chatroom, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("addToCurrentChatroomList");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.addToCurrentChatroomList(chatroom, __ctx);
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
    public void anonymousCallSettingChanged(int setting) {
        this.anonymousCallSettingChanged(setting, null, false);
    }

    @Override
    public void anonymousCallSettingChanged(int setting, Map<String, String> __ctx) {
        this.anonymousCallSettingChanged(setting, __ctx, true);
    }

    private void anonymousCallSettingChanged(int setting, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.anonymousCallSettingChanged(setting, __ctx);
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
    public void blockUser(String username, int contactListVersion) {
        this.blockUser(username, contactListVersion, null, false);
    }

    @Override
    public void blockUser(String username, int contactListVersion, Map<String, String> __ctx) {
        this.blockUser(username, contactListVersion, __ctx, true);
    }

    private void blockUser(String username, int contactListVersion, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.blockUser(username, contactListVersion, __ctx);
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
    public void contactChangedDisplayPictureOneWay(String source, String displayPicture, long timeStamp) {
        this.contactChangedDisplayPictureOneWay(source, displayPicture, timeStamp, null, false);
    }

    @Override
    public void contactChangedDisplayPictureOneWay(String source, String displayPicture, long timeStamp, Map<String, String> __ctx) {
        this.contactChangedDisplayPictureOneWay(source, displayPicture, timeStamp, __ctx, true);
    }

    private void contactChangedDisplayPictureOneWay(String source, String displayPicture, long timeStamp, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.contactChangedDisplayPictureOneWay(source, displayPicture, timeStamp, __ctx);
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
    public void contactChangedPresenceOneWay(int imType, String source, int presence) {
        this.contactChangedPresenceOneWay(imType, source, presence, null, false);
    }

    @Override
    public void contactChangedPresenceOneWay(int imType, String source, int presence, Map<String, String> __ctx) {
        this.contactChangedPresenceOneWay(imType, source, presence, __ctx, true);
    }

    private void contactChangedPresenceOneWay(int imType, String source, int presence, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.contactChangedPresenceOneWay(imType, source, presence, __ctx);
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
    public void contactChangedStatusMessageOneWay(String source, String statusMessage, long timeStamp) {
        this.contactChangedStatusMessageOneWay(source, statusMessage, timeStamp, null, false);
    }

    @Override
    public void contactChangedStatusMessageOneWay(String source, String statusMessage, long timeStamp, Map<String, String> __ctx) {
        this.contactChangedStatusMessageOneWay(source, statusMessage, timeStamp, __ctx, true);
    }

    private void contactChangedStatusMessageOneWay(String source, String statusMessage, long timeStamp, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.contactChangedStatusMessageOneWay(source, statusMessage, timeStamp, __ctx);
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
    public void contactDetailChanged(ContactDataIce contact, int contactListVersion) {
        this.contactDetailChanged(contact, contactListVersion, null, false);
    }

    @Override
    public void contactDetailChanged(ContactDataIce contact, int contactListVersion, Map<String, String> __ctx) {
        this.contactDetailChanged(contact, contactListVersion, __ctx, true);
    }

    private void contactDetailChanged(ContactDataIce contact, int contactListVersion, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.contactDetailChanged(contact, contactListVersion, __ctx);
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
    public void contactGroupDeleted(int contactGroupID, int contactListVersion) {
        this.contactGroupDeleted(contactGroupID, contactListVersion, null, false);
    }

    @Override
    public void contactGroupDeleted(int contactGroupID, int contactListVersion, Map<String, String> __ctx) {
        this.contactGroupDeleted(contactGroupID, contactListVersion, __ctx, true);
    }

    private void contactGroupDeleted(int contactGroupID, int contactListVersion, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.contactGroupDeleted(contactGroupID, contactListVersion, __ctx);
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
    public void contactGroupDetailChanged(ContactGroupDataIce contactGroup, int contactListVersion) {
        this.contactGroupDetailChanged(contactGroup, contactListVersion, null, false);
    }

    @Override
    public void contactGroupDetailChanged(ContactGroupDataIce contactGroup, int contactListVersion, Map<String, String> __ctx) {
        this.contactGroupDetailChanged(contactGroup, contactListVersion, __ctx, true);
    }

    private void contactGroupDetailChanged(ContactGroupDataIce contactGroup, int contactListVersion, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.contactGroupDetailChanged(contactGroup, contactListVersion, __ctx);
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
    public ContactDataIce contactRequestWasAccepted(ContactDataIce contact, String statusMessage, String displayPicture, int overallFusionPresence, int contactListVersion) {
        return this.contactRequestWasAccepted(contact, statusMessage, displayPicture, overallFusionPresence, contactListVersion, null, false);
    }

    @Override
    public ContactDataIce contactRequestWasAccepted(ContactDataIce contact, String statusMessage, String displayPicture, int overallFusionPresence, int contactListVersion, Map<String, String> __ctx) {
        return this.contactRequestWasAccepted(contact, statusMessage, displayPicture, overallFusionPresence, contactListVersion, __ctx, true);
    }

    private ContactDataIce contactRequestWasAccepted(ContactDataIce contact, String statusMessage, String displayPicture, int overallFusionPresence, int contactListVersion, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("contactRequestWasAccepted");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                return __del.contactRequestWasAccepted(contact, statusMessage, displayPicture, overallFusionPresence, contactListVersion, __ctx);
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
    public void contactRequestWasRejected(String contactRequestUsername, int contactListVersion) {
        this.contactRequestWasRejected(contactRequestUsername, contactListVersion, null, false);
    }

    @Override
    public void contactRequestWasRejected(String contactRequestUsername, int contactListVersion, Map<String, String> __ctx) {
        this.contactRequestWasRejected(contactRequestUsername, contactListVersion, __ctx, true);
    }

    private void contactRequestWasRejected(String contactRequestUsername, int contactListVersion, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.contactRequestWasRejected(contactRequestUsername, contactListVersion, __ctx);
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
    public PresenceAndCapabilityIce contactUpdated(ContactDataIce contact, String oldusername, boolean acceptedContactRequest, boolean changedFusionContact, UserPrx newContactUserProxy, int contactListVersion) throws FusionException {
        return this.contactUpdated(contact, oldusername, acceptedContactRequest, changedFusionContact, newContactUserProxy, contactListVersion, null, false);
    }

    @Override
    public PresenceAndCapabilityIce contactUpdated(ContactDataIce contact, String oldusername, boolean acceptedContactRequest, boolean changedFusionContact, UserPrx newContactUserProxy, int contactListVersion, Map<String, String> __ctx) throws FusionException {
        return this.contactUpdated(contact, oldusername, acceptedContactRequest, changedFusionContact, newContactUserProxy, contactListVersion, __ctx, true);
    }

    private PresenceAndCapabilityIce contactUpdated(ContactDataIce contact, String oldusername, boolean acceptedContactRequest, boolean changedFusionContact, UserPrx newContactUserProxy, int contactListVersion, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("contactUpdated");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                return __del.contactUpdated(contact, oldusername, acceptedContactRequest, changedFusionContact, newContactUserProxy, contactListVersion, __ctx);
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
    public SessionPrx createSession(String sessionID, int presence, int deviceType, int connectionType, int imType, int port, int remotePort, String IP, String mobileDevice, String userAgent, short clientVersion, String language, ConnectionPrx connectionProxy) throws FusionException {
        return this.createSession(sessionID, presence, deviceType, connectionType, imType, port, remotePort, IP, mobileDevice, userAgent, clientVersion, language, connectionProxy, null, false);
    }

    @Override
    public SessionPrx createSession(String sessionID, int presence, int deviceType, int connectionType, int imType, int port, int remotePort, String IP, String mobileDevice, String userAgent, short clientVersion, String language, ConnectionPrx connectionProxy, Map<String, String> __ctx) throws FusionException {
        return this.createSession(sessionID, presence, deviceType, connectionType, imType, port, remotePort, IP, mobileDevice, userAgent, clientVersion, language, connectionProxy, __ctx, true);
    }

    private SessionPrx createSession(String sessionID, int presence, int deviceType, int connectionType, int imType, int port, int remotePort, String IP, String mobileDevice, String userAgent, short clientVersion, String language, ConnectionPrx connectionProxy, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("createSession");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                return __del.createSession(sessionID, presence, deviceType, connectionType, imType, port, remotePort, IP, mobileDevice, userAgent, clientVersion, language, connectionProxy, __ctx);
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
    public void disconnect(String reason) {
        this.disconnect(reason, null, false);
    }

    @Override
    public void disconnect(String reason, Map<String, String> __ctx) {
        this.disconnect(reason, __ctx, true);
    }

    private void disconnect(String reason, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.disconnect(reason, __ctx);
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
    public void disconnectFlooder(String reason) {
        this.disconnectFlooder(reason, null, false);
    }

    @Override
    public void disconnectFlooder(String reason, Map<String, String> __ctx) {
        this.disconnectFlooder(reason, __ctx, true);
    }

    private void disconnectFlooder(String reason, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.disconnectFlooder(reason, __ctx);
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
    public void emailNotification(int unreadEmailCount) {
        this.emailNotification(unreadEmailCount, null, false);
    }

    @Override
    public void emailNotification(int unreadEmailCount, Map<String, String> __ctx) {
        this.emailNotification(unreadEmailCount, __ctx, true);
    }

    private void emailNotification(int unreadEmailCount, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.emailNotification(unreadEmailCount, __ctx);
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
    public void emoticonPackActivated(int emoticonPackId) {
        this.emoticonPackActivated(emoticonPackId, null, false);
    }

    @Override
    public void emoticonPackActivated(int emoticonPackId, Map<String, String> __ctx) {
        this.emoticonPackActivated(emoticonPackId, __ctx, true);
    }

    private void emoticonPackActivated(int emoticonPackId, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.emoticonPackActivated(emoticonPackId, __ctx);
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
    public void enteringGroupChat(boolean isCreator) throws FusionException {
        this.enteringGroupChat(isCreator, null, false);
    }

    @Override
    public void enteringGroupChat(boolean isCreator, Map<String, String> __ctx) throws FusionException {
        this.enteringGroupChat(isCreator, __ctx, true);
    }

    private void enteringGroupChat(boolean isCreator, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("enteringGroupChat");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.enteringGroupChat(isCreator, __ctx);
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
    public int executeEmoteCommandWithState(String emoteCommand, MessageDataIce message, SessionPrx sessionProxy) throws FusionException {
        return this.executeEmoteCommandWithState(emoteCommand, message, sessionProxy, null, false);
    }

    @Override
    public int executeEmoteCommandWithState(String emoteCommand, MessageDataIce message, SessionPrx sessionProxy, Map<String, String> __ctx) throws FusionException {
        return this.executeEmoteCommandWithState(emoteCommand, message, sessionProxy, __ctx, true);
    }

    private int executeEmoteCommandWithState(String emoteCommand, MessageDataIce message, SessionPrx sessionProxy, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("executeEmoteCommandWithState");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                return __del.executeEmoteCommandWithState(emoteCommand, message, sessionProxy, __ctx);
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
    public SessionPrx findSession(String sid) throws FusionException {
        return this.findSession(sid, null, false);
    }

    @Override
    public SessionPrx findSession(String sid, Map<String, String> __ctx) throws FusionException {
        return this.findSession(sid, __ctx, true);
    }

    private SessionPrx findSession(String sid, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("findSession");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                return __del.findSession(sid, __ctx);
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
    public String[] getBlockList() {
        return this.getBlockList(null, false);
    }

    @Override
    public String[] getBlockList(Map<String, String> __ctx) {
        return this.getBlockList(__ctx, true);
    }

    private String[] getBlockList(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getBlockList");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                return __del.getBlockList(__ctx);
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
    public String[] getBlockListFromUsernames(String[] usernames) {
        return this.getBlockListFromUsernames(usernames, null, false);
    }

    @Override
    public String[] getBlockListFromUsernames(String[] usernames, Map<String, String> __ctx) {
        return this.getBlockListFromUsernames(usernames, __ctx, true);
    }

    private String[] getBlockListFromUsernames(String[] usernames, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getBlockListFromUsernames");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                return __del.getBlockListFromUsernames(usernames, __ctx);
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
    public String[] getBroadcastList() {
        return this.getBroadcastList(null, false);
    }

    @Override
    public String[] getBroadcastList(Map<String, String> __ctx) {
        return this.getBroadcastList(__ctx, true);
    }

    private String[] getBroadcastList(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getBroadcastList");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                return __del.getBroadcastList(__ctx);
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
    public int[] getConnectedOtherIMs() {
        return this.getConnectedOtherIMs(null, false);
    }

    @Override
    public int[] getConnectedOtherIMs(Map<String, String> __ctx) {
        return this.getConnectedOtherIMs(__ctx, true);
    }

    private int[] getConnectedOtherIMs(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getConnectedOtherIMs");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                return __del.getConnectedOtherIMs(__ctx);
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
    public ContactList getContactList() {
        return this.getContactList(null, false);
    }

    @Override
    public ContactList getContactList(Map<String, String> __ctx) {
        return this.getContactList(__ctx, true);
    }

    private ContactList getContactList(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getContactList");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                return __del.getContactList(__ctx);
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
    public int getContactListVersion() {
        return this.getContactListVersion(null, false);
    }

    @Override
    public int getContactListVersion(Map<String, String> __ctx) {
        return this.getContactListVersion(__ctx, true);
    }

    private int getContactListVersion(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getContactListVersion");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                return __del.getContactListVersion(__ctx);
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
    public ContactDataIce[] getContacts() {
        return this.getContacts(null, false);
    }

    @Override
    public ContactDataIce[] getContacts(Map<String, String> __ctx) {
        return this.getContacts(__ctx, true);
    }

    private ContactDataIce[] getContacts(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getContacts");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                return __del.getContacts(__ctx);
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
    public String[] getCurrentChatrooms() {
        return this.getCurrentChatrooms(null, false);
    }

    @Override
    public String[] getCurrentChatrooms(Map<String, String> __ctx) {
        return this.getCurrentChatrooms(__ctx, true);
    }

    private String[] getCurrentChatrooms(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getCurrentChatrooms");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                return __del.getCurrentChatrooms(__ctx);
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
    public String[] getEmoticonAlternateKeys() {
        return this.getEmoticonAlternateKeys(null, false);
    }

    @Override
    public String[] getEmoticonAlternateKeys(Map<String, String> __ctx) {
        return this.getEmoticonAlternateKeys(__ctx, true);
    }

    private String[] getEmoticonAlternateKeys(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getEmoticonAlternateKeys");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                return __del.getEmoticonAlternateKeys(__ctx);
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
    public String[] getEmoticonHotKeys() {
        return this.getEmoticonHotKeys(null, false);
    }

    @Override
    public String[] getEmoticonHotKeys(Map<String, String> __ctx) {
        return this.getEmoticonHotKeys(__ctx, true);
    }

    private String[] getEmoticonHotKeys(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getEmoticonHotKeys");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                return __del.getEmoticonHotKeys(__ctx);
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
    public int getOnlineContactsCount() {
        return this.getOnlineContactsCount(null, false);
    }

    @Override
    public int getOnlineContactsCount(Map<String, String> __ctx) {
        return this.getOnlineContactsCount(__ctx, true);
    }

    private int getOnlineContactsCount(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getOnlineContactsCount");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                return __del.getOnlineContactsCount(__ctx);
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
    public String[] getOtherIMConferenceParticipants(int imType, String otherIMConferenceID) {
        return this.getOtherIMConferenceParticipants(imType, otherIMConferenceID, null, false);
    }

    @Override
    public String[] getOtherIMConferenceParticipants(int imType, String otherIMConferenceID, Map<String, String> __ctx) {
        return this.getOtherIMConferenceParticipants(imType, otherIMConferenceID, __ctx, true);
    }

    private String[] getOtherIMConferenceParticipants(int imType, String otherIMConferenceID, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getOtherIMConferenceParticipants");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                return __del.getOtherIMConferenceParticipants(imType, otherIMConferenceID, __ctx);
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
    public ContactDataIce[] getOtherIMContacts() {
        return this.getOtherIMContacts(null, false);
    }

    @Override
    public ContactDataIce[] getOtherIMContacts(Map<String, String> __ctx) {
        return this.getOtherIMContacts(__ctx, true);
    }

    private ContactDataIce[] getOtherIMContacts(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getOtherIMContacts");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                return __del.getOtherIMContacts(__ctx);
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
    public Credential[] getOtherIMCredentials() {
        return this.getOtherIMCredentials(null, false);
    }

    @Override
    public Credential[] getOtherIMCredentials(Map<String, String> __ctx) {
        return this.getOtherIMCredentials(__ctx, true);
    }

    private Credential[] getOtherIMCredentials(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getOtherIMCredentials");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                return __del.getOtherIMCredentials(__ctx);
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
    public int getOverallFusionPresence(String requestingUsername) {
        return this.getOverallFusionPresence(requestingUsername, null, false);
    }

    @Override
    public int getOverallFusionPresence(String requestingUsername, Map<String, String> __ctx) {
        return this.getOverallFusionPresence(requestingUsername, __ctx, true);
    }

    private int getOverallFusionPresence(String requestingUsername, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getOverallFusionPresence");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                return __del.getOverallFusionPresence(requestingUsername, __ctx);
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
    public int getReputationDataLevel() {
        return this.getReputationDataLevel(null, false);
    }

    @Override
    public int getReputationDataLevel(Map<String, String> __ctx) {
        return this.getReputationDataLevel(__ctx, true);
    }

    private int getReputationDataLevel(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getReputationDataLevel");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                return __del.getReputationDataLevel(__ctx);
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
    public SessionPrx[] getSessions() {
        return this.getSessions(null, false);
    }

    @Override
    public SessionPrx[] getSessions(Map<String, String> __ctx) {
        return this.getSessions(__ctx, true);
    }

    private SessionPrx[] getSessions(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getSessions");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                return __del.getSessions(__ctx);
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
    public int getUnreadEmailCount() {
        return this.getUnreadEmailCount(null, false);
    }

    @Override
    public int getUnreadEmailCount(Map<String, String> __ctx) {
        return this.getUnreadEmailCount(__ctx, true);
    }

    private int getUnreadEmailCount(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getUnreadEmailCount");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                return __del.getUnreadEmailCount(__ctx);
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
    public UserDataIce getUserData() {
        return this.getUserData(null, false);
    }

    @Override
    public UserDataIce getUserData(Map<String, String> __ctx) {
        return this.getUserData(__ctx, true);
    }

    private UserDataIce getUserData(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getUserData");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                return __del.getUserData(__ctx);
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
    public boolean isOnBlockList(String contactUsername) {
        return this.isOnBlockList(contactUsername, null, false);
    }

    @Override
    public boolean isOnBlockList(String contactUsername, Map<String, String> __ctx) {
        return this.isOnBlockList(contactUsername, __ctx, true);
    }

    private boolean isOnBlockList(String contactUsername, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("isOnBlockList");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                return __del.isOnBlockList(contactUsername, __ctx);
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
    public boolean isOnContactList(String contactUsername) {
        return this.isOnContactList(contactUsername, null, false);
    }

    @Override
    public boolean isOnContactList(String contactUsername, Map<String, String> __ctx) {
        return this.isOnContactList(contactUsername, __ctx, true);
    }

    private boolean isOnContactList(String contactUsername, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("isOnContactList");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                return __del.isOnContactList(contactUsername, __ctx);
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
    public void leavingGroupChat() {
        this.leavingGroupChat(null, false);
    }

    @Override
    public void leavingGroupChat(Map<String, String> __ctx) {
        this.leavingGroupChat(__ctx, true);
    }

    private void leavingGroupChat(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.leavingGroupChat(__ctx);
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
    public void messageSettingChanged(int setting) {
        this.messageSettingChanged(setting, null, false);
    }

    @Override
    public void messageSettingChanged(int setting, Map<String, String> __ctx) {
        this.messageSettingChanged(setting, __ctx, true);
    }

    private void messageSettingChanged(int setting, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.messageSettingChanged(setting, __ctx);
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
    public void newUserContactUpdated(String usernameThatWasModified, boolean acceptedContactRequest) {
        this.newUserContactUpdated(usernameThatWasModified, acceptedContactRequest, null, false);
    }

    @Override
    public void newUserContactUpdated(String usernameThatWasModified, boolean acceptedContactRequest, Map<String, String> __ctx) {
        this.newUserContactUpdated(usernameThatWasModified, acceptedContactRequest, __ctx, true);
    }

    private void newUserContactUpdated(String usernameThatWasModified, boolean acceptedContactRequest, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.newUserContactUpdated(usernameThatWasModified, acceptedContactRequest, __ctx);
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
    public void notifySessionsOfNewContact(ContactDataIce newContact, int contactListVersion, boolean guaranteedIsNew) {
        this.notifySessionsOfNewContact(newContact, contactListVersion, guaranteedIsNew, null, false);
    }

    @Override
    public void notifySessionsOfNewContact(ContactDataIce newContact, int contactListVersion, boolean guaranteedIsNew, Map<String, String> __ctx) {
        this.notifySessionsOfNewContact(newContact, contactListVersion, guaranteedIsNew, __ctx, true);
    }

    private void notifySessionsOfNewContact(ContactDataIce newContact, int contactListVersion, boolean guaranteedIsNew, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.notifySessionsOfNewContact(newContact, contactListVersion, guaranteedIsNew, __ctx);
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
    public void notifyUserJoinedGroupChat(String groupChatId, String username, boolean isMuted) {
        this.notifyUserJoinedGroupChat(groupChatId, username, isMuted, null, false);
    }

    @Override
    public void notifyUserJoinedGroupChat(String groupChatId, String username, boolean isMuted, Map<String, String> __ctx) {
        this.notifyUserJoinedGroupChat(groupChatId, username, isMuted, __ctx, true);
    }

    private void notifyUserJoinedGroupChat(String groupChatId, String username, boolean isMuted, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.notifyUserJoinedGroupChat(groupChatId, username, isMuted, __ctx);
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
    public void notifyUserLeftGroupChat(String groupChatId, String username) {
        this.notifyUserLeftGroupChat(groupChatId, username, null, false);
    }

    @Override
    public void notifyUserLeftGroupChat(String groupChatId, String username, Map<String, String> __ctx) {
        this.notifyUserLeftGroupChat(groupChatId, username, __ctx, true);
    }

    private void notifyUserLeftGroupChat(String groupChatId, String username, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.notifyUserLeftGroupChat(groupChatId, username, __ctx);
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
    public void oldUserContactUpdated(String usernameThatWasModified) throws FusionException {
        this.oldUserContactUpdated(usernameThatWasModified, null, false);
    }

    @Override
    public void oldUserContactUpdated(String usernameThatWasModified, Map<String, String> __ctx) throws FusionException {
        this.oldUserContactUpdated(usernameThatWasModified, __ctx, true);
    }

    private void oldUserContactUpdated(String usernameThatWasModified, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("oldUserContactUpdated");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.oldUserContactUpdated(usernameThatWasModified, __ctx);
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
    public void otherIMAddContact(int imType, String otherIMUsername) throws FusionException {
        this.otherIMAddContact(imType, otherIMUsername, null, false);
    }

    @Override
    public void otherIMAddContact(int imType, String otherIMUsername, Map<String, String> __ctx) throws FusionException {
        this.otherIMAddContact(imType, otherIMUsername, __ctx, true);
    }

    private void otherIMAddContact(int imType, String otherIMUsername, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("otherIMAddContact");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.otherIMAddContact(imType, otherIMUsername, __ctx);
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
    public String otherIMInviteToConference(int imType, String otherIMConferenceID, String otherIMUsername) throws FusionException {
        return this.otherIMInviteToConference(imType, otherIMConferenceID, otherIMUsername, null, false);
    }

    @Override
    public String otherIMInviteToConference(int imType, String otherIMConferenceID, String otherIMUsername, Map<String, String> __ctx) throws FusionException {
        return this.otherIMInviteToConference(imType, otherIMConferenceID, otherIMUsername, __ctx, true);
    }

    private String otherIMInviteToConference(int imType, String otherIMConferenceID, String otherIMUsername, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("otherIMInviteToConference");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                return __del.otherIMInviteToConference(imType, otherIMConferenceID, otherIMUsername, __ctx);
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
    public void otherIMLeaveConference(int imType, String otherIMConferenceID) {
        this.otherIMLeaveConference(imType, otherIMConferenceID, null, false);
    }

    @Override
    public void otherIMLeaveConference(int imType, String otherIMConferenceID, Map<String, String> __ctx) {
        this.otherIMLeaveConference(imType, otherIMConferenceID, __ctx, true);
    }

    private void otherIMLeaveConference(int imType, String otherIMConferenceID, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.otherIMLeaveConference(imType, otherIMConferenceID, __ctx);
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
    public void otherIMLogin(int imType, int presence, boolean showOfflineContacts) throws FusionException {
        this.otherIMLogin(imType, presence, showOfflineContacts, null, false);
    }

    @Override
    public void otherIMLogin(int imType, int presence, boolean showOfflineContacts, Map<String, String> __ctx) throws FusionException {
        this.otherIMLogin(imType, presence, showOfflineContacts, __ctx, true);
    }

    private void otherIMLogin(int imType, int presence, boolean showOfflineContacts, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("otherIMLogin");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.otherIMLogin(imType, presence, showOfflineContacts, __ctx);
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
    public void otherIMLogout(int imType) {
        this.otherIMLogout(imType, null, false);
    }

    @Override
    public void otherIMLogout(int imType, Map<String, String> __ctx) {
        this.otherIMLogout(imType, __ctx, true);
    }

    private void otherIMLogout(int imType, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.otherIMLogout(imType, __ctx);
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
    public void otherIMRemoveContact(int contactId) throws FusionException {
        this.otherIMRemoveContact(contactId, null, false);
    }

    @Override
    public void otherIMRemoveContact(int contactId, Map<String, String> __ctx) throws FusionException {
        this.otherIMRemoveContact(contactId, __ctx, true);
    }

    private void otherIMRemoveContact(int contactId, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("otherIMRemoveContact");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.otherIMRemoveContact(contactId, __ctx);
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
    public void otherIMRemoved(int imType) {
        this.otherIMRemoved(imType, null, false);
    }

    @Override
    public void otherIMRemoved(int imType, Map<String, String> __ctx) {
        this.otherIMRemoved(imType, __ctx, true);
    }

    private void otherIMRemoved(int imType, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.otherIMRemoved(imType, __ctx);
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
    public void otherIMSendMessage(int imType, String otherIMUsername, String message) throws FusionException {
        this.otherIMSendMessage(imType, otherIMUsername, message, null, false);
    }

    @Override
    public void otherIMSendMessage(int imType, String otherIMUsername, String message, Map<String, String> __ctx) throws FusionException {
        this.otherIMSendMessage(imType, otherIMUsername, message, __ctx, true);
    }

    private void otherIMSendMessage(int imType, String otherIMUsername, String message, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("otherIMSendMessage");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.otherIMSendMessage(imType, otherIMUsername, message, __ctx);
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
    public void privateChatNowAGroupChat(String groupChatID, String creator) throws FusionException {
        this.privateChatNowAGroupChat(groupChatID, creator, null, false);
    }

    @Override
    public void privateChatNowAGroupChat(String groupChatID, String creator, Map<String, String> __ctx) throws FusionException {
        this.privateChatNowAGroupChat(groupChatID, creator, __ctx, true);
    }

    private void privateChatNowAGroupChat(String groupChatID, String creator, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("privateChatNowAGroupChat");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.privateChatNowAGroupChat(groupChatID, creator, __ctx);
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
    public boolean privateChattedWith(String username) {
        return this.privateChattedWith(username, null, false);
    }

    @Override
    public boolean privateChattedWith(String username, Map<String, String> __ctx) {
        return this.privateChattedWith(username, __ctx, true);
    }

    private boolean privateChattedWith(String username, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("privateChattedWith");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                return __del.privateChattedWith(username, __ctx);
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
    public void pushNotification(Message msg) throws FusionException {
        this.pushNotification(msg, null, false);
    }

    @Override
    public void pushNotification(Message msg, Map<String, String> __ctx) throws FusionException {
        this.pushNotification(msg, __ctx, true);
    }

    private void pushNotification(Message msg, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("pushNotification");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.pushNotification(msg, __ctx);
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
    public void putAlertMessage(String message, String title, short timeout) throws FusionException {
        this.putAlertMessage(message, title, timeout, null, false);
    }

    @Override
    public void putAlertMessage(String message, String title, short timeout, Map<String, String> __ctx) throws FusionException {
        this.putAlertMessage(message, title, timeout, __ctx, true);
    }

    private void putAlertMessage(String message, String title, short timeout, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("putAlertMessage");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.putAlertMessage(message, title, timeout, __ctx);
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
    public void putAnonymousCallNotification(String requestingUsername, String requestingMobilePhone) throws FusionException {
        this.putAnonymousCallNotification(requestingUsername, requestingMobilePhone, null, false);
    }

    @Override
    public void putAnonymousCallNotification(String requestingUsername, String requestingMobilePhone, Map<String, String> __ctx) throws FusionException {
        this.putAnonymousCallNotification(requestingUsername, requestingMobilePhone, __ctx, true);
    }

    private void putAnonymousCallNotification(String requestingUsername, String requestingMobilePhone, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("putAnonymousCallNotification");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.putAnonymousCallNotification(requestingUsername, requestingMobilePhone, __ctx);
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
    public void putEvent(UserEventIce event) throws FusionException {
        this.putEvent(event, null, false);
    }

    @Override
    public void putEvent(UserEventIce event, Map<String, String> __ctx) throws FusionException {
        this.putEvent(event, __ctx, true);
    }

    private void putEvent(UserEventIce event, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("putEvent");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.putEvent(event, __ctx);
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
    public void putFileReceived(MessageDataIce message) throws FusionException {
        this.putFileReceived(message, null, false);
    }

    @Override
    public void putFileReceived(MessageDataIce message, Map<String, String> __ctx) throws FusionException {
        this.putFileReceived(message, __ctx, true);
    }

    private void putFileReceived(MessageDataIce message, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("putFileReceived");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.putFileReceived(message, __ctx);
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
    public void putMessage(MessageDataIce message) throws FusionException {
        this.putMessage(message, null, false);
    }

    @Override
    public void putMessage(MessageDataIce message, Map<String, String> __ctx) throws FusionException {
        this.putMessage(message, __ctx, true);
    }

    private void putMessage(MessageDataIce message, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("putMessage");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.putMessage(message, __ctx);
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
    public void putMessageStatusEvent(MessageStatusEventIce mseIce) throws FusionException {
        this.putMessageStatusEvent(mseIce, null, false);
    }

    @Override
    public void putMessageStatusEvent(MessageStatusEventIce mseIce, Map<String, String> __ctx) throws FusionException {
        this.putMessageStatusEvent(mseIce, __ctx, true);
    }

    private void putMessageStatusEvent(MessageStatusEventIce mseIce, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("putMessageStatusEvent");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.putMessageStatusEvent(mseIce, __ctx);
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
    public void putServerQuestion(String message, String url) throws FusionException {
        this.putServerQuestion(message, url, null, false);
    }

    @Override
    public void putServerQuestion(String message, String url, Map<String, String> __ctx) throws FusionException {
        this.putServerQuestion(message, url, __ctx, true);
    }

    private void putServerQuestion(String message, String url, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("putServerQuestion");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.putServerQuestion(message, url, __ctx);
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
    public void putWebCallNotification(String source, String destination, int gateway, String gatewayName, int protocol) throws FusionException {
        this.putWebCallNotification(source, destination, gateway, gatewayName, protocol, null, false);
    }

    @Override
    public void putWebCallNotification(String source, String destination, int gateway, String gatewayName, int protocol, Map<String, String> __ctx) throws FusionException {
        this.putWebCallNotification(source, destination, gateway, gatewayName, protocol, __ctx, true);
    }

    private void putWebCallNotification(String source, String destination, int gateway, String gatewayName, int protocol, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("putWebCallNotification");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.putWebCallNotification(source, destination, gateway, gatewayName, protocol, __ctx);
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
    public void rejectContactRequest(String inviterUsername) {
        this.rejectContactRequest(inviterUsername, null, false);
    }

    @Override
    public void rejectContactRequest(String inviterUsername, Map<String, String> __ctx) {
        this.rejectContactRequest(inviterUsername, __ctx, true);
    }

    private void rejectContactRequest(String inviterUsername, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.rejectContactRequest(inviterUsername, __ctx);
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
    public void removeContact(int contactid, int contactListVersion) {
        this.removeContact(contactid, contactListVersion, null, false);
    }

    @Override
    public void removeContact(int contactid, int contactListVersion, Map<String, String> __ctx) {
        this.removeContact(contactid, contactListVersion, __ctx, true);
    }

    private void removeContact(int contactid, int contactListVersion, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.removeContact(contactid, contactListVersion, __ctx);
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
    public void removeFromCurrentChatroomList(String chatroom) {
        this.removeFromCurrentChatroomList(chatroom, null, false);
    }

    @Override
    public void removeFromCurrentChatroomList(String chatroom, Map<String, String> __ctx) {
        this.removeFromCurrentChatroomList(chatroom, __ctx, true);
    }

    private void removeFromCurrentChatroomList(String chatroom, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.removeFromCurrentChatroomList(chatroom, __ctx);
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
    public void setCurrentChatListGroupChatSubset(ChatListIce ccl) {
        this.setCurrentChatListGroupChatSubset(ccl, null, false);
    }

    @Override
    public void setCurrentChatListGroupChatSubset(ChatListIce ccl, Map<String, String> __ctx) {
        this.setCurrentChatListGroupChatSubset(ccl, __ctx, true);
    }

    private void setCurrentChatListGroupChatSubset(ChatListIce ccl, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.setCurrentChatListGroupChatSubset(ccl, __ctx);
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
    public void stopBroadcastingTo(String username) {
        this.stopBroadcastingTo(username, null, false);
    }

    @Override
    public void stopBroadcastingTo(String username, Map<String, String> __ctx) {
        this.stopBroadcastingTo(username, __ctx, true);
    }

    private void stopBroadcastingTo(String username, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.stopBroadcastingTo(username, __ctx);
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
    public boolean supportsBinaryMessage() {
        return this.supportsBinaryMessage(null, false);
    }

    @Override
    public boolean supportsBinaryMessage(Map<String, String> __ctx) {
        return this.supportsBinaryMessage(__ctx, true);
    }

    private boolean supportsBinaryMessage(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("supportsBinaryMessage");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                return __del.supportsBinaryMessage(__ctx);
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
    public void themeChanged(String themeLocation) throws FusionException {
        this.themeChanged(themeLocation, null, false);
    }

    @Override
    public void themeChanged(String themeLocation, Map<String, String> __ctx) throws FusionException {
        this.themeChanged(themeLocation, __ctx, true);
    }

    private void themeChanged(String themeLocation, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("themeChanged");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.themeChanged(themeLocation, __ctx);
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
    public void unblockUser(String username) {
        this.unblockUser(username, null, false);
    }

    @Override
    public void unblockUser(String username, Map<String, String> __ctx) {
        this.unblockUser(username, __ctx, true);
    }

    private void unblockUser(String username, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.unblockUser(username, __ctx);
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
    public UserErrorResponse userCanContactMe(String username, MessageDataIce message) {
        return this.userCanContactMe(username, message, null, false);
    }

    @Override
    public UserErrorResponse userCanContactMe(String username, MessageDataIce message, Map<String, String> __ctx) {
        return this.userCanContactMe(username, message, __ctx, true);
    }

    private UserErrorResponse userCanContactMe(String username, MessageDataIce message, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("userCanContactMe");
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                return __del.userCanContactMe(username, message, __ctx);
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
    public void userDetailChanged(UserDataIce user) {
        this.userDetailChanged(user, null, false);
    }

    @Override
    public void userDetailChanged(UserDataIce user, Map<String, String> __ctx) {
        this.userDetailChanged(user, __ctx, true);
    }

    private void userDetailChanged(UserDataIce user, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.userDetailChanged(user, __ctx);
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
    public void userDisplayPictureChanged(String displayPicture, long timeStamp) {
        this.userDisplayPictureChanged(displayPicture, timeStamp, null, false);
    }

    @Override
    public void userDisplayPictureChanged(String displayPicture, long timeStamp, Map<String, String> __ctx) {
        this.userDisplayPictureChanged(displayPicture, timeStamp, __ctx, true);
    }

    private void userDisplayPictureChanged(String displayPicture, long timeStamp, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.userDisplayPictureChanged(displayPicture, timeStamp, __ctx);
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
    public void userReputationChanged() {
        this.userReputationChanged(null, false);
    }

    @Override
    public void userReputationChanged(Map<String, String> __ctx) {
        this.userReputationChanged(__ctx, true);
    }

    private void userReputationChanged(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.userReputationChanged(__ctx);
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
    public void userStatusMessageChanged(String statusMessage, long timeStamp) {
        this.userStatusMessageChanged(statusMessage, timeStamp, null, false);
    }

    @Override
    public void userStatusMessageChanged(String statusMessage, long timeStamp, Map<String, String> __ctx) {
        this.userStatusMessageChanged(statusMessage, timeStamp, __ctx, true);
    }

    private void userStatusMessageChanged(String statusMessage, long timeStamp, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _UserDel __del = (_UserDel)__delBase;
                __del.userStatusMessageChanged(statusMessage, timeStamp, __ctx);
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

    public static UserPrx checkedCast(ObjectPrx __obj) {
        UserPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (UserPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::User")) break block3;
                    UserPrxHelper __h = new UserPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static UserPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        UserPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (UserPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::User", __ctx)) break block3;
                    UserPrxHelper __h = new UserPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static UserPrx checkedCast(ObjectPrx __obj, String __facet) {
        UserPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::User")) {
                    UserPrxHelper __h = new UserPrxHelper();
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

    public static UserPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        UserPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::User", __ctx)) {
                    UserPrxHelper __h = new UserPrxHelper();
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

    public static UserPrx uncheckedCast(ObjectPrx __obj) {
        UserPrx __d = null;
        if (__obj != null) {
            try {
                __d = (UserPrx)__obj;
            }
            catch (ClassCastException ex) {
                UserPrxHelper __h = new UserPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static UserPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        UserPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            UserPrxHelper __h = new UserPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _UserDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _UserDelD();
    }

    public static void __write(BasicStream __os, UserPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static UserPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            UserPrxHelper result = new UserPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

