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
import com.projectgoth.fusion.slice.ChatDefinitionIce;
import com.projectgoth.fusion.slice.ConnectionPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GroupChatPrx;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.MessageSwitchboardPrx;
import com.projectgoth.fusion.slice.RegistryPrx;
import com.projectgoth.fusion.slice.SessionPrx;
import com.projectgoth.fusion.slice.UserDataIce;
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.slice._MessageSwitchboardDel;
import com.projectgoth.fusion.slice._MessageSwitchboardDelD;
import com.projectgoth.fusion.slice._MessageSwitchboardDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class MessageSwitchboardPrxHelper
extends ObjectPrxHelperBase
implements MessageSwitchboardPrx {
    @Override
    public GroupChatPrx ensureGroupChatExists(SessionPrx currentSession, String groupChatID) throws FusionException {
        return this.ensureGroupChatExists(currentSession, groupChatID, null, false);
    }

    @Override
    public GroupChatPrx ensureGroupChatExists(SessionPrx currentSession, String groupChatID, Map<String, String> __ctx) throws FusionException {
        return this.ensureGroupChatExists(currentSession, groupChatID, __ctx, true);
    }

    private GroupChatPrx ensureGroupChatExists(SessionPrx currentSession, String groupChatID, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("ensureGroupChatExists");
                __delBase = this.__getDelegate(false);
                _MessageSwitchboardDel __del = (_MessageSwitchboardDel)__delBase;
                return __del.ensureGroupChatExists(currentSession, groupChatID, __ctx);
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
    public void getAndPushMessages(String username, byte chatType, String suppliedChatID, long oldestMessageTimestamp, long newestMessageTimestamp, int limit, ConnectionPrx cxn) throws FusionException {
        this.getAndPushMessages(username, chatType, suppliedChatID, oldestMessageTimestamp, newestMessageTimestamp, limit, cxn, null, false);
    }

    @Override
    public void getAndPushMessages(String username, byte chatType, String suppliedChatID, long oldestMessageTimestamp, long newestMessageTimestamp, int limit, ConnectionPrx cxn, Map<String, String> __ctx) throws FusionException {
        this.getAndPushMessages(username, chatType, suppliedChatID, oldestMessageTimestamp, newestMessageTimestamp, limit, cxn, __ctx, true);
    }

    private void getAndPushMessages(String username, byte chatType, String suppliedChatID, long oldestMessageTimestamp, long newestMessageTimestamp, int limit, ConnectionPrx cxn, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getAndPushMessages");
                __delBase = this.__getDelegate(false);
                _MessageSwitchboardDel __del = (_MessageSwitchboardDel)__delBase;
                __del.getAndPushMessages(username, chatType, suppliedChatID, oldestMessageTimestamp, newestMessageTimestamp, limit, cxn, __ctx);
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
    public void getAndPushMessages2(String username, byte chatType, String suppliedChatID, long oldestMessageTimestamp, long newestMessageTimestamp, int limit, ConnectionPrx cxn, int deviceType, short clientVersion, short fusionPktTransactionId) throws FusionException {
        this.getAndPushMessages2(username, chatType, suppliedChatID, oldestMessageTimestamp, newestMessageTimestamp, limit, cxn, deviceType, clientVersion, fusionPktTransactionId, null, false);
    }

    @Override
    public void getAndPushMessages2(String username, byte chatType, String suppliedChatID, long oldestMessageTimestamp, long newestMessageTimestamp, int limit, ConnectionPrx cxn, int deviceType, short clientVersion, short fusionPktTransactionId, Map<String, String> __ctx) throws FusionException {
        this.getAndPushMessages2(username, chatType, suppliedChatID, oldestMessageTimestamp, newestMessageTimestamp, limit, cxn, deviceType, clientVersion, fusionPktTransactionId, __ctx, true);
    }

    private void getAndPushMessages2(String username, byte chatType, String suppliedChatID, long oldestMessageTimestamp, long newestMessageTimestamp, int limit, ConnectionPrx cxn, int deviceType, short clientVersion, short fusionPktTransactionId, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getAndPushMessages2");
                __delBase = this.__getDelegate(false);
                _MessageSwitchboardDel __del = (_MessageSwitchboardDel)__delBase;
                __del.getAndPushMessages2(username, chatType, suppliedChatID, oldestMessageTimestamp, newestMessageTimestamp, limit, cxn, deviceType, clientVersion, fusionPktTransactionId, __ctx);
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
    public ChatDefinitionIce[] getChats(int userID, int chatListVersion, int limit, byte chatType) throws FusionException {
        return this.getChats(userID, chatListVersion, limit, chatType, null, false);
    }

    @Override
    public ChatDefinitionIce[] getChats(int userID, int chatListVersion, int limit, byte chatType, Map<String, String> __ctx) throws FusionException {
        return this.getChats(userID, chatListVersion, limit, chatType, __ctx, true);
    }

    private ChatDefinitionIce[] getChats(int userID, int chatListVersion, int limit, byte chatType, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getChats");
                __delBase = this.__getDelegate(false);
                _MessageSwitchboardDel __del = (_MessageSwitchboardDel)__delBase;
                return __del.getChats(userID, chatListVersion, limit, chatType, __ctx);
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
    public ChatDefinitionIce[] getChats2(int userID, int chatListVersion, int limit, byte chatType, ConnectionPrx cxn) throws FusionException {
        return this.getChats2(userID, chatListVersion, limit, chatType, cxn, null, false);
    }

    @Override
    public ChatDefinitionIce[] getChats2(int userID, int chatListVersion, int limit, byte chatType, ConnectionPrx cxn, Map<String, String> __ctx) throws FusionException {
        return this.getChats2(userID, chatListVersion, limit, chatType, cxn, __ctx, true);
    }

    private ChatDefinitionIce[] getChats2(int userID, int chatListVersion, int limit, byte chatType, ConnectionPrx cxn, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getChats2");
                __delBase = this.__getDelegate(false);
                _MessageSwitchboardDel __del = (_MessageSwitchboardDel)__delBase;
                return __del.getChats2(userID, chatListVersion, limit, chatType, cxn, __ctx);
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
    public boolean isUserChatSyncEnabled(ConnectionPrx cxn, String username, int userID) throws FusionException {
        return this.isUserChatSyncEnabled(cxn, username, userID, null, false);
    }

    @Override
    public boolean isUserChatSyncEnabled(ConnectionPrx cxn, String username, int userID, Map<String, String> __ctx) throws FusionException {
        return this.isUserChatSyncEnabled(cxn, username, userID, __ctx, true);
    }

    private boolean isUserChatSyncEnabled(ConnectionPrx cxn, String username, int userID, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("isUserChatSyncEnabled");
                __delBase = this.__getDelegate(false);
                _MessageSwitchboardDel __del = (_MessageSwitchboardDel)__delBase;
                return __del.isUserChatSyncEnabled(cxn, username, userID, __ctx);
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
    public void onCreateGroupChat(ChatDefinitionIce storedGroupChat, String creatorUsername, String privateChatPartnerUsername, GroupChatPrx groupChatRemote) throws FusionException {
        this.onCreateGroupChat(storedGroupChat, creatorUsername, privateChatPartnerUsername, groupChatRemote, null, false);
    }

    @Override
    public void onCreateGroupChat(ChatDefinitionIce storedGroupChat, String creatorUsername, String privateChatPartnerUsername, GroupChatPrx groupChatRemote, Map<String, String> __ctx) throws FusionException {
        this.onCreateGroupChat(storedGroupChat, creatorUsername, privateChatPartnerUsername, groupChatRemote, __ctx, true);
    }

    private void onCreateGroupChat(ChatDefinitionIce storedGroupChat, String creatorUsername, String privateChatPartnerUsername, GroupChatPrx groupChatRemote, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("onCreateGroupChat");
                __delBase = this.__getDelegate(false);
                _MessageSwitchboardDel __del = (_MessageSwitchboardDel)__delBase;
                __del.onCreateGroupChat(storedGroupChat, creatorUsername, privateChatPartnerUsername, groupChatRemote, __ctx);
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
    public void onCreatePrivateChat(int userID, String username, String otherUser, int deviceType, short clientVersion, UserDataIce senderUserData, String recipientDisplayPicture) throws FusionException {
        this.onCreatePrivateChat(userID, username, otherUser, deviceType, clientVersion, senderUserData, recipientDisplayPicture, null, false);
    }

    @Override
    public void onCreatePrivateChat(int userID, String username, String otherUser, int deviceType, short clientVersion, UserDataIce senderUserData, String recipientDisplayPicture, Map<String, String> __ctx) throws FusionException {
        this.onCreatePrivateChat(userID, username, otherUser, deviceType, clientVersion, senderUserData, recipientDisplayPicture, __ctx, true);
    }

    private void onCreatePrivateChat(int userID, String username, String otherUser, int deviceType, short clientVersion, UserDataIce senderUserData, String recipientDisplayPicture, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("onCreatePrivateChat");
                __delBase = this.__getDelegate(false);
                _MessageSwitchboardDel __del = (_MessageSwitchboardDel)__delBase;
                __del.onCreatePrivateChat(userID, username, otherUser, deviceType, clientVersion, senderUserData, recipientDisplayPicture, __ctx);
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
    public void onGetChats(ConnectionPrx cxn, int userID, int chatListVersion, int limit, byte chatType, short transactionId, String parentUsername) throws FusionException {
        this.onGetChats(cxn, userID, chatListVersion, limit, chatType, transactionId, parentUsername, null, false);
    }

    @Override
    public void onGetChats(ConnectionPrx cxn, int userID, int chatListVersion, int limit, byte chatType, short transactionId, String parentUsername, Map<String, String> __ctx) throws FusionException {
        this.onGetChats(cxn, userID, chatListVersion, limit, chatType, transactionId, parentUsername, __ctx, true);
    }

    private void onGetChats(ConnectionPrx cxn, int userID, int chatListVersion, int limit, byte chatType, short transactionId, String parentUsername, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("onGetChats");
                __delBase = this.__getDelegate(false);
                _MessageSwitchboardDel __del = (_MessageSwitchboardDel)__delBase;
                __del.onGetChats(cxn, userID, chatListVersion, limit, chatType, transactionId, parentUsername, __ctx);
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
    public void onJoinChatRoom(String username, int userID, String chatRoomName) throws FusionException {
        this.onJoinChatRoom(username, userID, chatRoomName, null, false);
    }

    @Override
    public void onJoinChatRoom(String username, int userID, String chatRoomName, Map<String, String> __ctx) throws FusionException {
        this.onJoinChatRoom(username, userID, chatRoomName, __ctx, true);
    }

    private void onJoinChatRoom(String username, int userID, String chatRoomName, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("onJoinChatRoom");
                __delBase = this.__getDelegate(false);
                _MessageSwitchboardDel __del = (_MessageSwitchboardDel)__delBase;
                __del.onJoinChatRoom(username, userID, chatRoomName, __ctx);
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
    public void onJoinGroupChat(String username, int userID, String groupChatGUID, boolean debug, UserPrx userProxy) throws FusionException {
        this.onJoinGroupChat(username, userID, groupChatGUID, debug, userProxy, null, false);
    }

    @Override
    public void onJoinGroupChat(String username, int userID, String groupChatGUID, boolean debug, UserPrx userProxy, Map<String, String> __ctx) throws FusionException {
        this.onJoinGroupChat(username, userID, groupChatGUID, debug, userProxy, __ctx, true);
    }

    private void onJoinGroupChat(String username, int userID, String groupChatGUID, boolean debug, UserPrx userProxy, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("onJoinGroupChat");
                __delBase = this.__getDelegate(false);
                _MessageSwitchboardDel __del = (_MessageSwitchboardDel)__delBase;
                __del.onJoinGroupChat(username, userID, groupChatGUID, debug, userProxy, __ctx);
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
    public void onLeaveChatRoom(String username, int userID, String chatRoomName, UserPrx userProxy) throws FusionException {
        this.onLeaveChatRoom(username, userID, chatRoomName, userProxy, null, false);
    }

    @Override
    public void onLeaveChatRoom(String username, int userID, String chatRoomName, UserPrx userProxy, Map<String, String> __ctx) throws FusionException {
        this.onLeaveChatRoom(username, userID, chatRoomName, userProxy, __ctx, true);
    }

    private void onLeaveChatRoom(String username, int userID, String chatRoomName, UserPrx userProxy, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("onLeaveChatRoom");
                __delBase = this.__getDelegate(false);
                _MessageSwitchboardDel __del = (_MessageSwitchboardDel)__delBase;
                __del.onLeaveChatRoom(username, userID, chatRoomName, userProxy, __ctx);
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
    public void onLeaveGroupChat(String username, int userID, String groupChatGUID, UserPrx userProxy) throws FusionException {
        this.onLeaveGroupChat(username, userID, groupChatGUID, userProxy, null, false);
    }

    @Override
    public void onLeaveGroupChat(String username, int userID, String groupChatGUID, UserPrx userProxy, Map<String, String> __ctx) throws FusionException {
        this.onLeaveGroupChat(username, userID, groupChatGUID, userProxy, __ctx, true);
    }

    private void onLeaveGroupChat(String username, int userID, String groupChatGUID, UserPrx userProxy, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("onLeaveGroupChat");
                __delBase = this.__getDelegate(false);
                _MessageSwitchboardDel __del = (_MessageSwitchboardDel)__delBase;
                __del.onLeaveGroupChat(username, userID, groupChatGUID, userProxy, __ctx);
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
    public void onLeavePrivateChat(int userID, String username, String otherUser, int deviceType, short clientVersion) throws FusionException {
        this.onLeavePrivateChat(userID, username, otherUser, deviceType, clientVersion, null, false);
    }

    @Override
    public void onLeavePrivateChat(int userID, String username, String otherUser, int deviceType, short clientVersion, Map<String, String> __ctx) throws FusionException {
        this.onLeavePrivateChat(userID, username, otherUser, deviceType, clientVersion, __ctx, true);
    }

    private void onLeavePrivateChat(int userID, String username, String otherUser, int deviceType, short clientVersion, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("onLeavePrivateChat");
                __delBase = this.__getDelegate(false);
                _MessageSwitchboardDel __del = (_MessageSwitchboardDel)__delBase;
                __del.onLeavePrivateChat(userID, username, otherUser, deviceType, clientVersion, __ctx);
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
    public void onLogon(int userID, SessionPrx sess, short transactionID, String parentUsername) throws FusionException {
        this.onLogon(userID, sess, transactionID, parentUsername, null, false);
    }

    @Override
    public void onLogon(int userID, SessionPrx sess, short transactionID, String parentUsername, Map<String, String> __ctx) throws FusionException {
        this.onLogon(userID, sess, transactionID, parentUsername, __ctx, true);
    }

    private void onLogon(int userID, SessionPrx sess, short transactionID, String parentUsername, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("onLogon");
                __delBase = this.__getDelegate(false);
                _MessageSwitchboardDel __del = (_MessageSwitchboardDel)__delBase;
                __del.onLogon(userID, sess, transactionID, parentUsername, __ctx);
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
    public void onSendFusionMessageToChatRoom(SessionPrx currentSession, UserPrx parentUser, MessageDataIce messageData, String chatRoomName, int deviceType, short clientVersion) throws FusionException {
        this.onSendFusionMessageToChatRoom(currentSession, parentUser, messageData, chatRoomName, deviceType, clientVersion, null, false);
    }

    @Override
    public void onSendFusionMessageToChatRoom(SessionPrx currentSession, UserPrx parentUser, MessageDataIce messageData, String chatRoomName, int deviceType, short clientVersion, Map<String, String> __ctx) throws FusionException {
        this.onSendFusionMessageToChatRoom(currentSession, parentUser, messageData, chatRoomName, deviceType, clientVersion, __ctx, true);
    }

    private void onSendFusionMessageToChatRoom(SessionPrx currentSession, UserPrx parentUser, MessageDataIce messageData, String chatRoomName, int deviceType, short clientVersion, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("onSendFusionMessageToChatRoom");
                __delBase = this.__getDelegate(false);
                _MessageSwitchboardDel __del = (_MessageSwitchboardDel)__delBase;
                __del.onSendFusionMessageToChatRoom(currentSession, parentUser, messageData, chatRoomName, deviceType, clientVersion, __ctx);
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
    public void onSendFusionMessageToGroupChat(SessionPrx currentSession, UserPrx parentUser, MessageDataIce messageData, String groupChatID, int deviceType, short clientVersion) throws FusionException {
        this.onSendFusionMessageToGroupChat(currentSession, parentUser, messageData, groupChatID, deviceType, clientVersion, null, false);
    }

    @Override
    public void onSendFusionMessageToGroupChat(SessionPrx currentSession, UserPrx parentUser, MessageDataIce messageData, String groupChatID, int deviceType, short clientVersion, Map<String, String> __ctx) throws FusionException {
        this.onSendFusionMessageToGroupChat(currentSession, parentUser, messageData, groupChatID, deviceType, clientVersion, __ctx, true);
    }

    private void onSendFusionMessageToGroupChat(SessionPrx currentSession, UserPrx parentUser, MessageDataIce messageData, String groupChatID, int deviceType, short clientVersion, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("onSendFusionMessageToGroupChat");
                __delBase = this.__getDelegate(false);
                _MessageSwitchboardDel __del = (_MessageSwitchboardDel)__delBase;
                __del.onSendFusionMessageToGroupChat(currentSession, parentUser, messageData, groupChatID, deviceType, clientVersion, __ctx);
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
    public boolean onSendFusionMessageToIndividual(SessionPrx currentSession, UserPrx parentUser, MessageDataIce messageData, String destinationUsername, String[] uniqueUsersPrivateChattedWith, int deviceType, short clientVersion, UserDataIce senderUserData, String recipientDisplayPicture) throws FusionException {
        return this.onSendFusionMessageToIndividual(currentSession, parentUser, messageData, destinationUsername, uniqueUsersPrivateChattedWith, deviceType, clientVersion, senderUserData, recipientDisplayPicture, null, false);
    }

    @Override
    public boolean onSendFusionMessageToIndividual(SessionPrx currentSession, UserPrx parentUser, MessageDataIce messageData, String destinationUsername, String[] uniqueUsersPrivateChattedWith, int deviceType, short clientVersion, UserDataIce senderUserData, String recipientDisplayPicture, Map<String, String> __ctx) throws FusionException {
        return this.onSendFusionMessageToIndividual(currentSession, parentUser, messageData, destinationUsername, uniqueUsersPrivateChattedWith, deviceType, clientVersion, senderUserData, recipientDisplayPicture, __ctx, true);
    }

    private boolean onSendFusionMessageToIndividual(SessionPrx currentSession, UserPrx parentUser, MessageDataIce messageData, String destinationUsername, String[] uniqueUsersPrivateChattedWith, int deviceType, short clientVersion, UserDataIce senderUserData, String recipientDisplayPicture, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("onSendFusionMessageToIndividual");
                __delBase = this.__getDelegate(false);
                _MessageSwitchboardDel __del = (_MessageSwitchboardDel)__delBase;
                return __del.onSendFusionMessageToIndividual(currentSession, parentUser, messageData, destinationUsername, uniqueUsersPrivateChattedWith, deviceType, clientVersion, senderUserData, recipientDisplayPicture, __ctx);
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
    public boolean onSendMessageToAllUsersInChat(SessionPrx currentSession, UserPrx parentUser, MessageDataIce messageData, UserDataIce senderUserData) throws FusionException {
        return this.onSendMessageToAllUsersInChat(currentSession, parentUser, messageData, senderUserData, null, false);
    }

    @Override
    public boolean onSendMessageToAllUsersInChat(SessionPrx currentSession, UserPrx parentUser, MessageDataIce messageData, UserDataIce senderUserData, Map<String, String> __ctx) throws FusionException {
        return this.onSendMessageToAllUsersInChat(currentSession, parentUser, messageData, senderUserData, __ctx, true);
    }

    private boolean onSendMessageToAllUsersInChat(SessionPrx currentSession, UserPrx parentUser, MessageDataIce messageData, UserDataIce senderUserData, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("onSendMessageToAllUsersInChat");
                __delBase = this.__getDelegate(false);
                _MessageSwitchboardDel __del = (_MessageSwitchboardDel)__delBase;
                return __del.onSendMessageToAllUsersInChat(currentSession, parentUser, messageData, senderUserData, __ctx);
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
    public void setChatName(String parentUsername, String suppliedChatID, byte chatType, String chatName, RegistryPrx regy) throws FusionException {
        this.setChatName(parentUsername, suppliedChatID, chatType, chatName, regy, null, false);
    }

    @Override
    public void setChatName(String parentUsername, String suppliedChatID, byte chatType, String chatName, RegistryPrx regy, Map<String, String> __ctx) throws FusionException {
        this.setChatName(parentUsername, suppliedChatID, chatType, chatName, regy, __ctx, true);
    }

    private void setChatName(String parentUsername, String suppliedChatID, byte chatType, String chatName, RegistryPrx regy, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("setChatName");
                __delBase = this.__getDelegate(false);
                _MessageSwitchboardDel __del = (_MessageSwitchboardDel)__delBase;
                __del.setChatName(parentUsername, suppliedChatID, chatType, chatName, regy, __ctx);
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

    public static MessageSwitchboardPrx checkedCast(ObjectPrx __obj) {
        MessageSwitchboardPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (MessageSwitchboardPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::MessageSwitchboard")) break block3;
                    MessageSwitchboardPrxHelper __h = new MessageSwitchboardPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static MessageSwitchboardPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        MessageSwitchboardPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (MessageSwitchboardPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::MessageSwitchboard", __ctx)) break block3;
                    MessageSwitchboardPrxHelper __h = new MessageSwitchboardPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static MessageSwitchboardPrx checkedCast(ObjectPrx __obj, String __facet) {
        MessageSwitchboardPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::MessageSwitchboard")) {
                    MessageSwitchboardPrxHelper __h = new MessageSwitchboardPrxHelper();
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

    public static MessageSwitchboardPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        MessageSwitchboardPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::MessageSwitchboard", __ctx)) {
                    MessageSwitchboardPrxHelper __h = new MessageSwitchboardPrxHelper();
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

    public static MessageSwitchboardPrx uncheckedCast(ObjectPrx __obj) {
        MessageSwitchboardPrx __d = null;
        if (__obj != null) {
            try {
                __d = (MessageSwitchboardPrx)__obj;
            }
            catch (ClassCastException ex) {
                MessageSwitchboardPrxHelper __h = new MessageSwitchboardPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static MessageSwitchboardPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        MessageSwitchboardPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            MessageSwitchboardPrxHelper __h = new MessageSwitchboardPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _MessageSwitchboardDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _MessageSwitchboardDelD();
    }

    public static void __write(BasicStream __os, MessageSwitchboardPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static MessageSwitchboardPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            MessageSwitchboardPrxHelper result = new MessageSwitchboardPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

