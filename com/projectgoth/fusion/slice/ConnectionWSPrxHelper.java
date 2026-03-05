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
import com.projectgoth.fusion.slice.AMI_Connection_putMessageAsync;
import com.projectgoth.fusion.slice.ChatRoomDataIce;
import com.projectgoth.fusion.slice.ConnectionPrx;
import com.projectgoth.fusion.slice.ConnectionWSPrx;
import com.projectgoth.fusion.slice.ContactDataIce;
import com.projectgoth.fusion.slice.ContactGroupDataIce;
import com.projectgoth.fusion.slice.CurrencyDataIce;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.Message;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.MessageStatusEventIce;
import com.projectgoth.fusion.slice.SessionPrx;
import com.projectgoth.fusion.slice.UserEventIce;
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.slice._ConnectionWSDel;
import com.projectgoth.fusion.slice._ConnectionWSDelD;
import com.projectgoth.fusion.slice._ConnectionWSDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class ConnectionWSPrxHelper
extends ObjectPrxHelperBase
implements ConnectionWSPrx {
    @Override
    public void accountBalanceChanged(double balance, double fundedBalance, CurrencyDataIce currency) throws FusionException {
        this.accountBalanceChanged(balance, fundedBalance, currency, null, false);
    }

    @Override
    public void accountBalanceChanged(double balance, double fundedBalance, CurrencyDataIce currency, Map<String, String> __ctx) throws FusionException {
        this.accountBalanceChanged(balance, fundedBalance, currency, __ctx, true);
    }

    private void accountBalanceChanged(double balance, double fundedBalance, CurrencyDataIce currency, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("accountBalanceChanged");
                __delBase = this.__getDelegate(false);
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
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
    public void avatarChanged(String displayPicture, String statusMessage) throws FusionException {
        this.avatarChanged(displayPicture, statusMessage, null, false);
    }

    @Override
    public void avatarChanged(String displayPicture, String statusMessage, Map<String, String> __ctx) throws FusionException {
        this.avatarChanged(displayPicture, statusMessage, __ctx, true);
    }

    private void avatarChanged(String displayPicture, String statusMessage, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("avatarChanged");
                __delBase = this.__getDelegate(false);
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
                __del.avatarChanged(displayPicture, statusMessage, __ctx);
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
    public void contactAdded(ContactDataIce contact, int contactListVersion, boolean guaranteedIsNew) throws FusionException {
        this.contactAdded(contact, contactListVersion, guaranteedIsNew, null, false);
    }

    @Override
    public void contactAdded(ContactDataIce contact, int contactListVersion, boolean guaranteedIsNew, Map<String, String> __ctx) throws FusionException {
        this.contactAdded(contact, contactListVersion, guaranteedIsNew, __ctx, true);
    }

    private void contactAdded(ContactDataIce contact, int contactListVersion, boolean guaranteedIsNew, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("contactAdded");
                __delBase = this.__getDelegate(false);
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
                __del.contactAdded(contact, contactListVersion, guaranteedIsNew, __ctx);
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
    public void contactChangedDisplayPictureOneWay(int contactID, String displayPicture, long timeStamp) {
        this.contactChangedDisplayPictureOneWay(contactID, displayPicture, timeStamp, null, false);
    }

    @Override
    public void contactChangedDisplayPictureOneWay(int contactID, String displayPicture, long timeStamp, Map<String, String> __ctx) {
        this.contactChangedDisplayPictureOneWay(contactID, displayPicture, timeStamp, __ctx, true);
    }

    private void contactChangedDisplayPictureOneWay(int contactID, String displayPicture, long timeStamp, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
                __del.contactChangedDisplayPictureOneWay(contactID, displayPicture, timeStamp, __ctx);
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
    public void contactChangedPresenceOneWay(int contactID, int imType, int presence) {
        this.contactChangedPresenceOneWay(contactID, imType, presence, null, false);
    }

    @Override
    public void contactChangedPresenceOneWay(int contactID, int imType, int presence, Map<String, String> __ctx) {
        this.contactChangedPresenceOneWay(contactID, imType, presence, __ctx, true);
    }

    private void contactChangedPresenceOneWay(int contactID, int imType, int presence, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
                __del.contactChangedPresenceOneWay(contactID, imType, presence, __ctx);
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
    public void contactChangedStatusMessageOneWay(int contactID, String statusMessage, long timeStamp) {
        this.contactChangedStatusMessageOneWay(contactID, statusMessage, timeStamp, null, false);
    }

    @Override
    public void contactChangedStatusMessageOneWay(int contactID, String statusMessage, long timeStamp, Map<String, String> __ctx) {
        this.contactChangedStatusMessageOneWay(contactID, statusMessage, timeStamp, __ctx, true);
    }

    private void contactChangedStatusMessageOneWay(int contactID, String statusMessage, long timeStamp, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
                __del.contactChangedStatusMessageOneWay(contactID, statusMessage, timeStamp, __ctx);
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
    public void contactGroupAdded(ContactGroupDataIce contactGroup, int contactListVersion) throws FusionException {
        this.contactGroupAdded(contactGroup, contactListVersion, null, false);
    }

    @Override
    public void contactGroupAdded(ContactGroupDataIce contactGroup, int contactListVersion, Map<String, String> __ctx) throws FusionException {
        this.contactGroupAdded(contactGroup, contactListVersion, __ctx, true);
    }

    private void contactGroupAdded(ContactGroupDataIce contactGroup, int contactListVersion, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("contactGroupAdded");
                __delBase = this.__getDelegate(false);
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
                __del.contactGroupAdded(contactGroup, contactListVersion, __ctx);
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
    public void contactGroupRemoved(int contactGroupID, int contactListVersion) throws FusionException {
        this.contactGroupRemoved(contactGroupID, contactListVersion, null, false);
    }

    @Override
    public void contactGroupRemoved(int contactGroupID, int contactListVersion, Map<String, String> __ctx) throws FusionException {
        this.contactGroupRemoved(contactGroupID, contactListVersion, __ctx, true);
    }

    private void contactGroupRemoved(int contactGroupID, int contactListVersion, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("contactGroupRemoved");
                __delBase = this.__getDelegate(false);
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
                __del.contactGroupRemoved(contactGroupID, contactListVersion, __ctx);
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
    public void contactRemoved(int contactID, int contactListVersion) throws FusionException {
        this.contactRemoved(contactID, contactListVersion, null, false);
    }

    @Override
    public void contactRemoved(int contactID, int contactListVersion, Map<String, String> __ctx) throws FusionException {
        this.contactRemoved(contactID, contactListVersion, __ctx, true);
    }

    private void contactRemoved(int contactID, int contactListVersion, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("contactRemoved");
                __delBase = this.__getDelegate(false);
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
                __del.contactRemoved(contactID, contactListVersion, __ctx);
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
    public void contactRequest(String contactUsername, int outstandingRequests) throws FusionException {
        this.contactRequest(contactUsername, outstandingRequests, null, false);
    }

    @Override
    public void contactRequest(String contactUsername, int outstandingRequests, Map<String, String> __ctx) throws FusionException {
        this.contactRequest(contactUsername, outstandingRequests, __ctx, true);
    }

    private void contactRequest(String contactUsername, int outstandingRequests, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("contactRequest");
                __delBase = this.__getDelegate(false);
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
                __del.contactRequest(contactUsername, outstandingRequests, __ctx);
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
    public void contactRequestAccepted(ContactDataIce contact, int contactListVersion, int outstandingRequests) throws FusionException {
        this.contactRequestAccepted(contact, contactListVersion, outstandingRequests, null, false);
    }

    @Override
    public void contactRequestAccepted(ContactDataIce contact, int contactListVersion, int outstandingRequests, Map<String, String> __ctx) throws FusionException {
        this.contactRequestAccepted(contact, contactListVersion, outstandingRequests, __ctx, true);
    }

    private void contactRequestAccepted(ContactDataIce contact, int contactListVersion, int outstandingRequests, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("contactRequestAccepted");
                __delBase = this.__getDelegate(false);
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
                __del.contactRequestAccepted(contact, contactListVersion, outstandingRequests, __ctx);
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
    public void contactRequestRejected(String contactUsername, int outstandingRequests) throws FusionException {
        this.contactRequestRejected(contactUsername, outstandingRequests, null, false);
    }

    @Override
    public void contactRequestRejected(String contactUsername, int outstandingRequests, Map<String, String> __ctx) throws FusionException {
        this.contactRequestRejected(contactUsername, outstandingRequests, __ctx, true);
    }

    private void contactRequestRejected(String contactUsername, int outstandingRequests, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("contactRequestRejected");
                __delBase = this.__getDelegate(false);
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
                __del.contactRequestRejected(contactUsername, outstandingRequests, __ctx);
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
    public void disconnect(String reason) throws FusionException {
        this.disconnect(reason, null, false);
    }

    @Override
    public void disconnect(String reason, Map<String, String> __ctx) throws FusionException {
        this.disconnect(reason, __ctx, true);
    }

    private void disconnect(String reason, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("disconnect");
                __delBase = this.__getDelegate(false);
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
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
    public void emailNotification(int unreadEmailCount) throws FusionException {
        this.emailNotification(unreadEmailCount, null, false);
    }

    @Override
    public void emailNotification(int unreadEmailCount, Map<String, String> __ctx) throws FusionException {
        this.emailNotification(unreadEmailCount, __ctx, true);
    }

    private void emailNotification(int unreadEmailCount, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("emailNotification");
                __delBase = this.__getDelegate(false);
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
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
    public void emoticonsChanged(String[] hotKeys, String[] alternateKeys) throws FusionException {
        this.emoticonsChanged(hotKeys, alternateKeys, null, false);
    }

    @Override
    public void emoticonsChanged(String[] hotKeys, String[] alternateKeys, Map<String, String> __ctx) throws FusionException {
        this.emoticonsChanged(hotKeys, alternateKeys, __ctx, true);
    }

    private void emoticonsChanged(String[] hotKeys, String[] alternateKeys, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("emoticonsChanged");
                __delBase = this.__getDelegate(false);
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
                __del.emoticonsChanged(hotKeys, alternateKeys, __ctx);
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
    public short getClientVersion() {
        return this.getClientVersion(null, false);
    }

    @Override
    public short getClientVersion(Map<String, String> __ctx) {
        return this.getClientVersion(__ctx, true);
    }

    private short getClientVersion(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getClientVersion");
                __delBase = this.__getDelegate(false);
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
                return __del.getClientVersion(__ctx);
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
    public int getDeviceTypeAsInt() {
        return this.getDeviceTypeAsInt(null, false);
    }

    @Override
    public int getDeviceTypeAsInt(Map<String, String> __ctx) {
        return this.getDeviceTypeAsInt(__ctx, true);
    }

    private int getDeviceTypeAsInt(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getDeviceTypeAsInt");
                __delBase = this.__getDelegate(false);
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
                return __del.getDeviceTypeAsInt(__ctx);
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
    public String getMobileDevice() {
        return this.getMobileDevice(null, false);
    }

    @Override
    public String getMobileDevice(Map<String, String> __ctx) {
        return this.getMobileDevice(__ctx, true);
    }

    private String getMobileDevice(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getMobileDevice");
                __delBase = this.__getDelegate(false);
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
                return __del.getMobileDevice(__ctx);
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
    public ChatRoomDataIce[] getPopularChatRooms() throws FusionException {
        return this.getPopularChatRooms(null, false);
    }

    @Override
    public ChatRoomDataIce[] getPopularChatRooms(Map<String, String> __ctx) throws FusionException {
        return this.getPopularChatRooms(__ctx, true);
    }

    private ChatRoomDataIce[] getPopularChatRooms(Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getPopularChatRooms");
                __delBase = this.__getDelegate(false);
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
                return __del.getPopularChatRooms(__ctx);
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
    public String getRemoteIPAddress() {
        return this.getRemoteIPAddress(null, false);
    }

    @Override
    public String getRemoteIPAddress(Map<String, String> __ctx) {
        return this.getRemoteIPAddress(__ctx, true);
    }

    private String getRemoteIPAddress(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getRemoteIPAddress");
                __delBase = this.__getDelegate(false);
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
                return __del.getRemoteIPAddress(__ctx);
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
    public SessionPrx getSessionObject() {
        return this.getSessionObject(null, false);
    }

    @Override
    public SessionPrx getSessionObject(Map<String, String> __ctx) {
        return this.getSessionObject(__ctx, true);
    }

    private SessionPrx getSessionObject(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getSessionObject");
                __delBase = this.__getDelegate(false);
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
                return __del.getSessionObject(__ctx);
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
    public String getUserAgent() {
        return this.getUserAgent(null, false);
    }

    @Override
    public String getUserAgent(Map<String, String> __ctx) {
        return this.getUserAgent(__ctx, true);
    }

    private String getUserAgent(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getUserAgent");
                __delBase = this.__getDelegate(false);
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
                return __del.getUserAgent(__ctx);
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
    public UserPrx getUserObject() {
        return this.getUserObject(null, false);
    }

    @Override
    public UserPrx getUserObject(Map<String, String> __ctx) {
        return this.getUserObject(__ctx, true);
    }

    private UserPrx getUserObject(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getUserObject");
                __delBase = this.__getDelegate(false);
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
                return __del.getUserObject(__ctx);
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
    public String getUsername() {
        return this.getUsername(null, false);
    }

    @Override
    public String getUsername(Map<String, String> __ctx) {
        return this.getUsername(__ctx, true);
    }

    private String getUsername(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getUsername");
                __delBase = this.__getDelegate(false);
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
                return __del.getUsername(__ctx);
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
    public void logout() {
        this.logout(null, false);
    }

    @Override
    public void logout(Map<String, String> __ctx) {
        this.logout(__ctx, true);
    }

    private void logout(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
                __del.logout(__ctx);
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
    public void otherIMConferenceCreated(int imType, String conferenceID, String creator) throws FusionException {
        this.otherIMConferenceCreated(imType, conferenceID, creator, null, false);
    }

    @Override
    public void otherIMConferenceCreated(int imType, String conferenceID, String creator, Map<String, String> __ctx) throws FusionException {
        this.otherIMConferenceCreated(imType, conferenceID, creator, __ctx, true);
    }

    private void otherIMConferenceCreated(int imType, String conferenceID, String creator, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("otherIMConferenceCreated");
                __delBase = this.__getDelegate(false);
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
                __del.otherIMConferenceCreated(imType, conferenceID, creator, __ctx);
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
    public void otherIMLoggedIn(int imType) throws FusionException {
        this.otherIMLoggedIn(imType, null, false);
    }

    @Override
    public void otherIMLoggedIn(int imType, Map<String, String> __ctx) throws FusionException {
        this.otherIMLoggedIn(imType, __ctx, true);
    }

    private void otherIMLoggedIn(int imType, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("otherIMLoggedIn");
                __delBase = this.__getDelegate(false);
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
                __del.otherIMLoggedIn(imType, __ctx);
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
    public void otherIMLoggedOut(int imType, String reason) throws FusionException {
        this.otherIMLoggedOut(imType, reason, null, false);
    }

    @Override
    public void otherIMLoggedOut(int imType, String reason, Map<String, String> __ctx) throws FusionException {
        this.otherIMLoggedOut(imType, reason, __ctx, true);
    }

    private void otherIMLoggedOut(int imType, String reason, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("otherIMLoggedOut");
                __delBase = this.__getDelegate(false);
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
                __del.otherIMLoggedOut(imType, reason, __ctx);
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
    public void packetProcessed(byte[] result) {
        this.packetProcessed(result, null, false);
    }

    @Override
    public void packetProcessed(byte[] result, Map<String, String> __ctx) {
        this.packetProcessed(result, __ctx, true);
    }

    private void packetProcessed(byte[] result, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
                __del.packetProcessed(result, __ctx);
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
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
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
    public boolean processPacket(ConnectionPrx requestingConnection, byte[] packet) throws FusionException {
        return this.processPacket(requestingConnection, packet, null, false);
    }

    @Override
    public boolean processPacket(ConnectionPrx requestingConnection, byte[] packet, Map<String, String> __ctx) throws FusionException {
        return this.processPacket(requestingConnection, packet, __ctx, true);
    }

    private boolean processPacket(ConnectionPrx requestingConnection, byte[] packet, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("processPacket");
                __delBase = this.__getDelegate(false);
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
                return __del.processPacket(requestingConnection, packet, __ctx);
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
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
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
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
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
    public void putAlertMessageOneWay(String message, String title, short timeout) {
        this.putAlertMessageOneWay(message, title, timeout, null, false);
    }

    @Override
    public void putAlertMessageOneWay(String message, String title, short timeout, Map<String, String> __ctx) {
        this.putAlertMessageOneWay(message, title, timeout, __ctx, true);
    }

    private void putAlertMessageOneWay(String message, String title, short timeout, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
                __del.putAlertMessageOneWay(message, title, timeout, __ctx);
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
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
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
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
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
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
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
    public void putGenericPacket(byte[] packet) throws FusionException {
        this.putGenericPacket(packet, null, false);
    }

    @Override
    public void putGenericPacket(byte[] packet, Map<String, String> __ctx) throws FusionException {
        this.putGenericPacket(packet, __ctx, true);
    }

    private void putGenericPacket(byte[] packet, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("putGenericPacket");
                __delBase = this.__getDelegate(false);
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
                __del.putGenericPacket(packet, __ctx);
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
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
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
    public void putMessageAsync(MessageDataIce message) throws FusionException {
        this.putMessageAsync(message, null, false);
    }

    @Override
    public void putMessageAsync(MessageDataIce message, Map<String, String> __ctx) throws FusionException {
        this.putMessageAsync(message, __ctx, true);
    }

    private void putMessageAsync(MessageDataIce message, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("putMessageAsync");
                __delBase = this.__getDelegate(false);
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
                __del.putMessageAsync(message, __ctx);
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
    public boolean putMessageAsync_async(AMI_Connection_putMessageAsync __cb, MessageDataIce message) {
        return this.putMessageAsync_async(__cb, message, null, false);
    }

    @Override
    public boolean putMessageAsync_async(AMI_Connection_putMessageAsync __cb, MessageDataIce message, Map<String, String> __ctx) {
        return this.putMessageAsync_async(__cb, message, __ctx, true);
    }

    private boolean putMessageAsync_async(AMI_Connection_putMessageAsync __cb, MessageDataIce message, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        return __cb.__invoke(this, __cb, message, __ctx);
    }

    @Override
    public void putMessageOneWay(MessageDataIce message) {
        this.putMessageOneWay(message, null, false);
    }

    @Override
    public void putMessageOneWay(MessageDataIce message, Map<String, String> __ctx) {
        this.putMessageOneWay(message, __ctx, true);
    }

    private void putMessageOneWay(MessageDataIce message, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
                __del.putMessageOneWay(message, __ctx);
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
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
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
    public void putMessageStatusEvents(MessageStatusEventIce[] events, short requestTxnId) throws FusionException {
        this.putMessageStatusEvents(events, requestTxnId, null, false);
    }

    @Override
    public void putMessageStatusEvents(MessageStatusEventIce[] events, short requestTxnId, Map<String, String> __ctx) throws FusionException {
        this.putMessageStatusEvents(events, requestTxnId, __ctx, true);
    }

    private void putMessageStatusEvents(MessageStatusEventIce[] events, short requestTxnId, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("putMessageStatusEvents");
                __delBase = this.__getDelegate(false);
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
                __del.putMessageStatusEvents(events, requestTxnId, __ctx);
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
    public void putMessages(MessageDataIce[] messages) throws FusionException {
        this.putMessages(messages, null, false);
    }

    @Override
    public void putMessages(MessageDataIce[] messages, Map<String, String> __ctx) throws FusionException {
        this.putMessages(messages, __ctx, true);
    }

    private void putMessages(MessageDataIce[] messages, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("putMessages");
                __delBase = this.__getDelegate(false);
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
                __del.putMessages(messages, __ctx);
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
    public void putSerializedPacket(byte[] packet) throws FusionException {
        this.putSerializedPacket(packet, null, false);
    }

    @Override
    public void putSerializedPacket(byte[] packet, Map<String, String> __ctx) throws FusionException {
        this.putSerializedPacket(packet, __ctx, true);
    }

    private void putSerializedPacket(byte[] packet, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("putSerializedPacket");
                __delBase = this.__getDelegate(false);
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
                __del.putSerializedPacket(packet, __ctx);
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
    public void putSerializedPacketOneWay(byte[] packet) {
        this.putSerializedPacketOneWay(packet, null, false);
    }

    @Override
    public void putSerializedPacketOneWay(byte[] packet, Map<String, String> __ctx) {
        this.putSerializedPacketOneWay(packet, __ctx, true);
    }

    private void putSerializedPacketOneWay(byte[] packet, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
                __del.putSerializedPacketOneWay(packet, __ctx);
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
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
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
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
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
    public void silentlyDropIncomingPackets() {
        this.silentlyDropIncomingPackets(null, false);
    }

    @Override
    public void silentlyDropIncomingPackets(Map<String, String> __ctx) {
        this.silentlyDropIncomingPackets(__ctx, true);
    }

    private void silentlyDropIncomingPackets(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
                __del.silentlyDropIncomingPackets(__ctx);
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
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
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
    public void accessed() {
        this.accessed(null, false);
    }

    @Override
    public void accessed(Map<String, String> __ctx) {
        this.accessed(__ctx, true);
    }

    private void accessed(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
                __del.accessed(__ctx);
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
    public void addRemoteChildConnectionWS(String uuid, ConnectionWSPrx childConnectionWS) {
        this.addRemoteChildConnectionWS(uuid, childConnectionWS, null, false);
    }

    @Override
    public void addRemoteChildConnectionWS(String uuid, ConnectionWSPrx childConnectionWS, Map<String, String> __ctx) {
        this.addRemoteChildConnectionWS(uuid, childConnectionWS, __ctx, true);
    }

    private void addRemoteChildConnectionWS(String uuid, ConnectionWSPrx childConnectionWS, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
                __del.addRemoteChildConnectionWS(uuid, childConnectionWS, __ctx);
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
    public void removeRemoteChildConnectionWS(String uuid, ConnectionWSPrx childConnectionWS) {
        this.removeRemoteChildConnectionWS(uuid, childConnectionWS, null, false);
    }

    @Override
    public void removeRemoteChildConnectionWS(String uuid, ConnectionWSPrx childConnectionWS, Map<String, String> __ctx) {
        this.removeRemoteChildConnectionWS(uuid, childConnectionWS, __ctx, true);
    }

    private void removeRemoteChildConnectionWS(String uuid, ConnectionWSPrx childConnectionWS, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _ConnectionWSDel __del = (_ConnectionWSDel)__delBase;
                __del.removeRemoteChildConnectionWS(uuid, childConnectionWS, __ctx);
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

    public static ConnectionWSPrx checkedCast(ObjectPrx __obj) {
        ConnectionWSPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (ConnectionWSPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::ConnectionWS")) break block3;
                    ConnectionWSPrxHelper __h = new ConnectionWSPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static ConnectionWSPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        ConnectionWSPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (ConnectionWSPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::ConnectionWS", __ctx)) break block3;
                    ConnectionWSPrxHelper __h = new ConnectionWSPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static ConnectionWSPrx checkedCast(ObjectPrx __obj, String __facet) {
        ConnectionWSPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::ConnectionWS")) {
                    ConnectionWSPrxHelper __h = new ConnectionWSPrxHelper();
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

    public static ConnectionWSPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        ConnectionWSPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::ConnectionWS", __ctx)) {
                    ConnectionWSPrxHelper __h = new ConnectionWSPrxHelper();
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

    public static ConnectionWSPrx uncheckedCast(ObjectPrx __obj) {
        ConnectionWSPrx __d = null;
        if (__obj != null) {
            try {
                __d = (ConnectionWSPrx)__obj;
            }
            catch (ClassCastException ex) {
                ConnectionWSPrxHelper __h = new ConnectionWSPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static ConnectionWSPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        ConnectionWSPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            ConnectionWSPrxHelper __h = new ConnectionWSPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _ConnectionWSDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _ConnectionWSDelD();
    }

    public static void __write(BasicStream __os, ConnectionWSPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static ConnectionWSPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            ConnectionWSPrxHelper result = new ConnectionWSPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

