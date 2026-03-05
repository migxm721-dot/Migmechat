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
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GroupChatPrx;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.MessageSwitchboardPrx;
import com.projectgoth.fusion.slice.SessionMetricsIce;
import com.projectgoth.fusion.slice.SessionPrx;
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.slice._SessionDel;
import com.projectgoth.fusion.slice._SessionDelD;
import com.projectgoth.fusion.slice._SessionDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class SessionPrxHelper
extends ObjectPrxHelperBase
implements SessionPrx {
    @Override
    public void chatroomJoined(ChatRoomPrx roomProxy, String name) {
        this.chatroomJoined(roomProxy, name, null, false);
    }

    @Override
    public void chatroomJoined(ChatRoomPrx roomProxy, String name, Map<String, String> __ctx) {
        this.chatroomJoined(roomProxy, name, __ctx, true);
    }

    private void chatroomJoined(ChatRoomPrx roomProxy, String name, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _SessionDel __del = (_SessionDel)__delBase;
                __del.chatroomJoined(roomProxy, name, __ctx);
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
    public void endSession() throws FusionException {
        this.endSession(null, false);
    }

    @Override
    public void endSession(Map<String, String> __ctx) throws FusionException {
        this.endSession(__ctx, true);
    }

    private void endSession(Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("endSession");
                __delBase = this.__getDelegate(false);
                _SessionDel __del = (_SessionDel)__delBase;
                __del.endSession(__ctx);
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
    public void endSessionOneWay() {
        this.endSessionOneWay(null, false);
    }

    @Override
    public void endSessionOneWay(Map<String, String> __ctx) {
        this.endSessionOneWay(__ctx, true);
    }

    private void endSessionOneWay(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _SessionDel __del = (_SessionDel)__delBase;
                __del.endSessionOneWay(__ctx);
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
    public GroupChatPrx findGroupChatObject(String groupChatID) throws FusionException {
        return this.findGroupChatObject(groupChatID, null, false);
    }

    @Override
    public GroupChatPrx findGroupChatObject(String groupChatID, Map<String, String> __ctx) throws FusionException {
        return this.findGroupChatObject(groupChatID, __ctx, true);
    }

    private GroupChatPrx findGroupChatObject(String groupChatID, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("findGroupChatObject");
                __delBase = this.__getDelegate(false);
                _SessionDel __del = (_SessionDel)__delBase;
                return __del.findGroupChatObject(groupChatID, __ctx);
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
    public void friendInvitedByPhoneNumber() {
        this.friendInvitedByPhoneNumber(null, false);
    }

    @Override
    public void friendInvitedByPhoneNumber(Map<String, String> __ctx) {
        this.friendInvitedByPhoneNumber(__ctx, true);
    }

    private void friendInvitedByPhoneNumber(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _SessionDel __del = (_SessionDel)__delBase;
                __del.friendInvitedByPhoneNumber(__ctx);
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
    public void friendInvitedByUsername() {
        this.friendInvitedByUsername(null, false);
    }

    @Override
    public void friendInvitedByUsername(Map<String, String> __ctx) {
        this.friendInvitedByUsername(__ctx, true);
    }

    private void friendInvitedByUsername(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _SessionDel __del = (_SessionDel)__delBase;
                __del.friendInvitedByUsername(__ctx);
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
    public int getChatListVersion() throws FusionException {
        return this.getChatListVersion(null, false);
    }

    @Override
    public int getChatListVersion(Map<String, String> __ctx) throws FusionException {
        return this.getChatListVersion(__ctx, true);
    }

    private int getChatListVersion(Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getChatListVersion");
                __delBase = this.__getDelegate(false);
                _SessionDel __del = (_SessionDel)__delBase;
                return __del.getChatListVersion(__ctx);
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
    public short getClientVersionIce() {
        return this.getClientVersionIce(null, false);
    }

    @Override
    public short getClientVersionIce(Map<String, String> __ctx) {
        return this.getClientVersionIce(__ctx, true);
    }

    private short getClientVersionIce(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getClientVersionIce");
                __delBase = this.__getDelegate(false);
                _SessionDel __del = (_SessionDel)__delBase;
                return __del.getClientVersionIce(__ctx);
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
                _SessionDel __del = (_SessionDel)__delBase;
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
    public MessageSwitchboardPrx getMessageSwitchboard() throws FusionException {
        return this.getMessageSwitchboard(null, false);
    }

    @Override
    public MessageSwitchboardPrx getMessageSwitchboard(Map<String, String> __ctx) throws FusionException {
        return this.getMessageSwitchboard(__ctx, true);
    }

    private MessageSwitchboardPrx getMessageSwitchboard(Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getMessageSwitchboard");
                __delBase = this.__getDelegate(false);
                _SessionDel __del = (_SessionDel)__delBase;
                return __del.getMessageSwitchboard(__ctx);
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
    public String getMobileDeviceIce() {
        return this.getMobileDeviceIce(null, false);
    }

    @Override
    public String getMobileDeviceIce(Map<String, String> __ctx) {
        return this.getMobileDeviceIce(__ctx, true);
    }

    private String getMobileDeviceIce(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getMobileDeviceIce");
                __delBase = this.__getDelegate(false);
                _SessionDel __del = (_SessionDel)__delBase;
                return __del.getMobileDeviceIce(__ctx);
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
    public String getParentUsername() throws FusionException {
        return this.getParentUsername(null, false);
    }

    @Override
    public String getParentUsername(Map<String, String> __ctx) throws FusionException {
        return this.getParentUsername(__ctx, true);
    }

    private String getParentUsername(Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getParentUsername");
                __delBase = this.__getDelegate(false);
                _SessionDel __del = (_SessionDel)__delBase;
                return __del.getParentUsername(__ctx);
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
                _SessionDel __del = (_SessionDel)__delBase;
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
    public String getSessionID() {
        return this.getSessionID(null, false);
    }

    @Override
    public String getSessionID(Map<String, String> __ctx) {
        return this.getSessionID(__ctx, true);
    }

    private String getSessionID(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getSessionID");
                __delBase = this.__getDelegate(false);
                _SessionDel __del = (_SessionDel)__delBase;
                return __del.getSessionID(__ctx);
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
    public SessionMetricsIce getSessionMetrics() {
        return this.getSessionMetrics(null, false);
    }

    @Override
    public SessionMetricsIce getSessionMetrics(Map<String, String> __ctx) {
        return this.getSessionMetrics(__ctx, true);
    }

    private SessionMetricsIce getSessionMetrics(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getSessionMetrics");
                __delBase = this.__getDelegate(false);
                _SessionDel __del = (_SessionDel)__delBase;
                return __del.getSessionMetrics(__ctx);
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
    public String getUserAgentIce() {
        return this.getUserAgentIce(null, false);
    }

    @Override
    public String getUserAgentIce(Map<String, String> __ctx) {
        return this.getUserAgentIce(__ctx, true);
    }

    private String getUserAgentIce(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getUserAgentIce");
                __delBase = this.__getDelegate(false);
                _SessionDel __del = (_SessionDel)__delBase;
                return __del.getUserAgentIce(__ctx);
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
    public UserPrx getUserProxy(String username) throws FusionException {
        return this.getUserProxy(username, null, false);
    }

    @Override
    public UserPrx getUserProxy(String username, Map<String, String> __ctx) throws FusionException {
        return this.getUserProxy(username, __ctx, true);
    }

    private UserPrx getUserProxy(String username, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getUserProxy");
                __delBase = this.__getDelegate(false);
                _SessionDel __del = (_SessionDel)__delBase;
                return __del.getUserProxy(username, __ctx);
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
    public void groupChatJoined(String id) {
        this.groupChatJoined(id, null, false);
    }

    @Override
    public void groupChatJoined(String id, Map<String, String> __ctx) {
        this.groupChatJoined(id, __ctx, true);
    }

    private void groupChatJoined(String id, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _SessionDel __del = (_SessionDel)__delBase;
                __del.groupChatJoined(id, __ctx);
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
    public void groupChatJoinedMultiple(String id, int increment) {
        this.groupChatJoinedMultiple(id, increment, null, false);
    }

    @Override
    public void groupChatJoinedMultiple(String id, int increment, Map<String, String> __ctx) {
        this.groupChatJoinedMultiple(id, increment, __ctx, true);
    }

    private void groupChatJoinedMultiple(String id, int increment, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _SessionDel __del = (_SessionDel)__delBase;
                __del.groupChatJoinedMultiple(id, increment, __ctx);
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
    public void notifyUserJoinedChatRoomOneWay(String chatroomname, String username, boolean isAdministrator, boolean isMuted) {
        this.notifyUserJoinedChatRoomOneWay(chatroomname, username, isAdministrator, isMuted, null, false);
    }

    @Override
    public void notifyUserJoinedChatRoomOneWay(String chatroomname, String username, boolean isAdministrator, boolean isMuted, Map<String, String> __ctx) {
        this.notifyUserJoinedChatRoomOneWay(chatroomname, username, isAdministrator, isMuted, __ctx, true);
    }

    private void notifyUserJoinedChatRoomOneWay(String chatroomname, String username, boolean isAdministrator, boolean isMuted, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _SessionDel __del = (_SessionDel)__delBase;
                __del.notifyUserJoinedChatRoomOneWay(chatroomname, username, isAdministrator, isMuted, __ctx);
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
    public void notifyUserJoinedGroupChat(String groupChatId, String username, boolean isMuted) throws FusionException {
        this.notifyUserJoinedGroupChat(groupChatId, username, isMuted, null, false);
    }

    @Override
    public void notifyUserJoinedGroupChat(String groupChatId, String username, boolean isMuted, Map<String, String> __ctx) throws FusionException {
        this.notifyUserJoinedGroupChat(groupChatId, username, isMuted, __ctx, true);
    }

    private void notifyUserJoinedGroupChat(String groupChatId, String username, boolean isMuted, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("notifyUserJoinedGroupChat");
                __delBase = this.__getDelegate(false);
                _SessionDel __del = (_SessionDel)__delBase;
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
    public void notifyUserLeftChatRoomOneWay(String chatroomname, String username) {
        this.notifyUserLeftChatRoomOneWay(chatroomname, username, null, false);
    }

    @Override
    public void notifyUserLeftChatRoomOneWay(String chatroomname, String username, Map<String, String> __ctx) {
        this.notifyUserLeftChatRoomOneWay(chatroomname, username, __ctx, true);
    }

    private void notifyUserLeftChatRoomOneWay(String chatroomname, String username, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _SessionDel __del = (_SessionDel)__delBase;
                __del.notifyUserLeftChatRoomOneWay(chatroomname, username, __ctx);
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
    public void notifyUserLeftGroupChat(String groupChatId, String username) throws FusionException {
        this.notifyUserLeftGroupChat(groupChatId, username, null, false);
    }

    @Override
    public void notifyUserLeftGroupChat(String groupChatId, String username, Map<String, String> __ctx) throws FusionException {
        this.notifyUserLeftGroupChat(groupChatId, username, __ctx, true);
    }

    private void notifyUserLeftGroupChat(String groupChatId, String username, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("notifyUserLeftGroupChat");
                __delBase = this.__getDelegate(false);
                _SessionDel __del = (_SessionDel)__delBase;
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
    public void photoUploaded() {
        this.photoUploaded(null, false);
    }

    @Override
    public void photoUploaded(Map<String, String> __ctx) {
        this.photoUploaded(__ctx, true);
    }

    private void photoUploaded(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _SessionDel __del = (_SessionDel)__delBase;
                __del.photoUploaded(__ctx);
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
                _SessionDel __del = (_SessionDel)__delBase;
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
    public void profileEdited() {
        this.profileEdited(null, false);
    }

    @Override
    public void profileEdited(Map<String, String> __ctx) {
        this.profileEdited(__ctx, true);
    }

    private void profileEdited(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _SessionDel __del = (_SessionDel)__delBase;
                __del.profileEdited(__ctx);
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
                _SessionDel __del = (_SessionDel)__delBase;
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
                _SessionDel __del = (_SessionDel)__delBase;
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
                _SessionDel __del = (_SessionDel)__delBase;
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
                _SessionDel __del = (_SessionDel)__delBase;
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
                _SessionDel __del = (_SessionDel)__delBase;
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
                _SessionDel __del = (_SessionDel)__delBase;
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
    public void sendGroupChatParticipantArrays(String groupChatId, byte imType, String[] participants, String[] mutedParticipants) throws FusionException {
        this.sendGroupChatParticipantArrays(groupChatId, imType, participants, mutedParticipants, null, false);
    }

    @Override
    public void sendGroupChatParticipantArrays(String groupChatId, byte imType, String[] participants, String[] mutedParticipants, Map<String, String> __ctx) throws FusionException {
        this.sendGroupChatParticipantArrays(groupChatId, imType, participants, mutedParticipants, __ctx, true);
    }

    private void sendGroupChatParticipantArrays(String groupChatId, byte imType, String[] participants, String[] mutedParticipants, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("sendGroupChatParticipantArrays");
                __delBase = this.__getDelegate(false);
                _SessionDel __del = (_SessionDel)__delBase;
                __del.sendGroupChatParticipantArrays(groupChatId, imType, participants, mutedParticipants, __ctx);
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
    public void sendGroupChatParticipants(String groupChatId, byte imType, String participants, String mutedParticipants) throws FusionException {
        this.sendGroupChatParticipants(groupChatId, imType, participants, mutedParticipants, null, false);
    }

    @Override
    public void sendGroupChatParticipants(String groupChatId, byte imType, String participants, String mutedParticipants, Map<String, String> __ctx) throws FusionException {
        this.sendGroupChatParticipants(groupChatId, imType, participants, mutedParticipants, __ctx, true);
    }

    private void sendGroupChatParticipants(String groupChatId, byte imType, String participants, String mutedParticipants, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("sendGroupChatParticipants");
                __delBase = this.__getDelegate(false);
                _SessionDel __del = (_SessionDel)__delBase;
                __del.sendGroupChatParticipants(groupChatId, imType, participants, mutedParticipants, __ctx);
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
    public void sendMessage(MessageDataIce message) throws FusionException {
        this.sendMessage(message, null, false);
    }

    @Override
    public void sendMessage(MessageDataIce message, Map<String, String> __ctx) throws FusionException {
        this.sendMessage(message, __ctx, true);
    }

    private void sendMessage(MessageDataIce message, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("sendMessage");
                __delBase = this.__getDelegate(false);
                _SessionDel __del = (_SessionDel)__delBase;
                __del.sendMessage(message, __ctx);
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
    public void sendMessageBackToUserAsEmote(MessageDataIce message) throws FusionException {
        this.sendMessageBackToUserAsEmote(message, null, false);
    }

    @Override
    public void sendMessageBackToUserAsEmote(MessageDataIce message, Map<String, String> __ctx) throws FusionException {
        this.sendMessageBackToUserAsEmote(message, __ctx, true);
    }

    private void sendMessageBackToUserAsEmote(MessageDataIce message, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("sendMessageBackToUserAsEmote");
                __delBase = this.__getDelegate(false);
                _SessionDel __del = (_SessionDel)__delBase;
                __del.sendMessageBackToUserAsEmote(message, __ctx);
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
    public void setChatListVersion(int version) throws FusionException {
        this.setChatListVersion(version, null, false);
    }

    @Override
    public void setChatListVersion(int version, Map<String, String> __ctx) throws FusionException {
        this.setChatListVersion(version, __ctx, true);
    }

    private void setChatListVersion(int version, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("setChatListVersion");
                __delBase = this.__getDelegate(false);
                _SessionDel __del = (_SessionDel)__delBase;
                __del.setChatListVersion(version, __ctx);
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
                _SessionDel __del = (_SessionDel)__delBase;
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
    public void setLanguage(String language) {
        this.setLanguage(language, null, false);
    }

    @Override
    public void setLanguage(String language, Map<String, String> __ctx) {
        this.setLanguage(language, __ctx, true);
    }

    private void setLanguage(String language, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _SessionDel __del = (_SessionDel)__delBase;
                __del.setLanguage(language, __ctx);
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
    public void setPresence(int presence) throws FusionException {
        this.setPresence(presence, null, false);
    }

    @Override
    public void setPresence(int presence, Map<String, String> __ctx) throws FusionException {
        this.setPresence(presence, __ctx, true);
    }

    private void setPresence(int presence, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("setPresence");
                __delBase = this.__getDelegate(false);
                _SessionDel __del = (_SessionDel)__delBase;
                __del.setPresence(presence, __ctx);
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
                _SessionDel __del = (_SessionDel)__delBase;
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
    public void statusMessageSet() {
        this.statusMessageSet(null, false);
    }

    @Override
    public void statusMessageSet(Map<String, String> __ctx) {
        this.statusMessageSet(__ctx, true);
    }

    private void statusMessageSet(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _SessionDel __del = (_SessionDel)__delBase;
                __del.statusMessageSet(__ctx);
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
    public void themeUpdated() {
        this.themeUpdated(null, false);
    }

    @Override
    public void themeUpdated(Map<String, String> __ctx) {
        this.themeUpdated(__ctx, true);
    }

    private void themeUpdated(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _SessionDel __del = (_SessionDel)__delBase;
                __del.themeUpdated(__ctx);
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
    public void touch() throws FusionException {
        this.touch(null, false);
    }

    @Override
    public void touch(Map<String, String> __ctx) throws FusionException {
        this.touch(__ctx, true);
    }

    private void touch(Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("touch");
                __delBase = this.__getDelegate(false);
                _SessionDel __del = (_SessionDel)__delBase;
                __del.touch(__ctx);
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

    public static SessionPrx checkedCast(ObjectPrx __obj) {
        SessionPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (SessionPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::Session")) break block3;
                    SessionPrxHelper __h = new SessionPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static SessionPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        SessionPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (SessionPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::Session", __ctx)) break block3;
                    SessionPrxHelper __h = new SessionPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static SessionPrx checkedCast(ObjectPrx __obj, String __facet) {
        SessionPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::Session")) {
                    SessionPrxHelper __h = new SessionPrxHelper();
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

    public static SessionPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        SessionPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::Session", __ctx)) {
                    SessionPrxHelper __h = new SessionPrxHelper();
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

    public static SessionPrx uncheckedCast(ObjectPrx __obj) {
        SessionPrx __d = null;
        if (__obj != null) {
            try {
                __d = (SessionPrx)__obj;
            }
            catch (ClassCastException ex) {
                SessionPrxHelper __h = new SessionPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static SessionPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        SessionPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            SessionPrxHelper __h = new SessionPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _SessionDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _SessionDelD();
    }

    public static void __write(BasicStream __os, SessionPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static SessionPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            SessionPrxHelper result = new SessionPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

