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
import com.projectgoth.fusion.slice.ChatRoomDataIce;
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.SessionPrx;
import com.projectgoth.fusion.slice.UserDataIce;
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.slice._ChatRoomDel;
import com.projectgoth.fusion.slice._ChatRoomDelD;
import com.projectgoth.fusion.slice._ChatRoomDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class ChatRoomPrxHelper
extends ObjectPrxHelperBase
implements ChatRoomPrx {
    @Override
    public void botKilled(String botInstanceID) throws FusionException {
        this.botKilled(botInstanceID, null, false);
    }

    @Override
    public void botKilled(String botInstanceID, Map<String, String> __ctx) throws FusionException {
        this.botKilled(botInstanceID, __ctx, true);
    }

    private void botKilled(String botInstanceID, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("botKilled");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.botKilled(botInstanceID, __ctx);
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
    public String[] getParticipants(String requestingUsername) {
        return this.getParticipants(requestingUsername, null, false);
    }

    @Override
    public String[] getParticipants(String requestingUsername, Map<String, String> __ctx) {
        return this.getParticipants(requestingUsername, __ctx, true);
    }

    private String[] getParticipants(String requestingUsername, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getParticipants");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                return __del.getParticipants(requestingUsername, __ctx);
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
    public boolean isParticipant(String username) throws FusionException {
        return this.isParticipant(username, null, false);
    }

    @Override
    public boolean isParticipant(String username, Map<String, String> __ctx) throws FusionException {
        return this.isParticipant(username, __ctx, true);
    }

    private boolean isParticipant(String username, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("isParticipant");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                return __del.isParticipant(username, __ctx);
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
    public void putBotMessage(String botInstanceID, String username, String message, String[] emoticonHotKeys, boolean displayPopUp) throws FusionException {
        this.putBotMessage(botInstanceID, username, message, emoticonHotKeys, displayPopUp, null, false);
    }

    @Override
    public void putBotMessage(String botInstanceID, String username, String message, String[] emoticonHotKeys, boolean displayPopUp, Map<String, String> __ctx) throws FusionException {
        this.putBotMessage(botInstanceID, username, message, emoticonHotKeys, displayPopUp, __ctx, true);
    }

    private void putBotMessage(String botInstanceID, String username, String message, String[] emoticonHotKeys, boolean displayPopUp, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("putBotMessage");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.putBotMessage(botInstanceID, username, message, emoticonHotKeys, displayPopUp, __ctx);
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
    public void putBotMessageToAllUsers(String botInstanceID, String message, String[] emoticonHotKeys, boolean displayPopUp) throws FusionException {
        this.putBotMessageToAllUsers(botInstanceID, message, emoticonHotKeys, displayPopUp, null, false);
    }

    @Override
    public void putBotMessageToAllUsers(String botInstanceID, String message, String[] emoticonHotKeys, boolean displayPopUp, Map<String, String> __ctx) throws FusionException {
        this.putBotMessageToAllUsers(botInstanceID, message, emoticonHotKeys, displayPopUp, __ctx, true);
    }

    private void putBotMessageToAllUsers(String botInstanceID, String message, String[] emoticonHotKeys, boolean displayPopUp, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("putBotMessageToAllUsers");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.putBotMessageToAllUsers(botInstanceID, message, emoticonHotKeys, displayPopUp, __ctx);
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
    public void putBotMessageToUsers(String botInstanceID, String[] usernames, String message, String[] emoticonHotKeys, boolean displayPopUp) throws FusionException {
        this.putBotMessageToUsers(botInstanceID, usernames, message, emoticonHotKeys, displayPopUp, null, false);
    }

    @Override
    public void putBotMessageToUsers(String botInstanceID, String[] usernames, String message, String[] emoticonHotKeys, boolean displayPopUp, Map<String, String> __ctx) throws FusionException {
        this.putBotMessageToUsers(botInstanceID, usernames, message, emoticonHotKeys, displayPopUp, __ctx, true);
    }

    private void putBotMessageToUsers(String botInstanceID, String[] usernames, String message, String[] emoticonHotKeys, boolean displayPopUp, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("putBotMessageToUsers");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.putBotMessageToUsers(botInstanceID, usernames, message, emoticonHotKeys, displayPopUp, __ctx);
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
    public void sendGamesHelpToUser(String username) throws FusionException {
        this.sendGamesHelpToUser(username, null, false);
    }

    @Override
    public void sendGamesHelpToUser(String username, Map<String, String> __ctx) throws FusionException {
        this.sendGamesHelpToUser(username, __ctx, true);
    }

    private void sendGamesHelpToUser(String username, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("sendGamesHelpToUser");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.sendGamesHelpToUser(username, __ctx);
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
    public void sendMessageToBots(String username, String message, long receivedTimestamp) throws FusionException {
        this.sendMessageToBots(username, message, receivedTimestamp, null, false);
    }

    @Override
    public void sendMessageToBots(String username, String message, long receivedTimestamp, Map<String, String> __ctx) throws FusionException {
        this.sendMessageToBots(username, message, receivedTimestamp, __ctx, true);
    }

    private void sendMessageToBots(String username, String message, long receivedTimestamp, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("sendMessageToBots");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.sendMessageToBots(username, message, receivedTimestamp, __ctx);
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
    public void startBot(String username, String botCommandName) throws FusionException {
        this.startBot(username, botCommandName, null, false);
    }

    @Override
    public void startBot(String username, String botCommandName, Map<String, String> __ctx) throws FusionException {
        this.startBot(username, botCommandName, __ctx, true);
    }

    private void startBot(String username, String botCommandName, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("startBot");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.startBot(username, botCommandName, __ctx);
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
    public void stopAllBots(String username, int timeout) throws FusionException {
        this.stopAllBots(username, timeout, null, false);
    }

    @Override
    public void stopAllBots(String username, int timeout, Map<String, String> __ctx) throws FusionException {
        this.stopAllBots(username, timeout, __ctx, true);
    }

    private void stopAllBots(String username, int timeout, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("stopAllBots");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.stopAllBots(username, timeout, __ctx);
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
    public void stopBot(String username, String botCommandName) throws FusionException {
        this.stopBot(username, botCommandName, null, false);
    }

    @Override
    public void stopBot(String username, String botCommandName, Map<String, String> __ctx) throws FusionException {
        this.stopBot(username, botCommandName, __ctx, true);
    }

    private void stopBot(String username, String botCommandName, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("stopBot");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.stopBot(username, botCommandName, __ctx);
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
    public void addGroupModerator(String instigator, String targetUser) throws FusionException {
        this.addGroupModerator(instigator, targetUser, null, false);
    }

    @Override
    public void addGroupModerator(String instigator, String targetUser, Map<String, String> __ctx) throws FusionException {
        this.addGroupModerator(instigator, targetUser, __ctx, true);
    }

    private void addGroupModerator(String instigator, String targetUser, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("addGroupModerator");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.addGroupModerator(instigator, targetUser, __ctx);
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
    public void addModerator(String username) {
        this.addModerator(username, null, false);
    }

    @Override
    public void addModerator(String username, Map<String, String> __ctx) {
        this.addModerator(username, __ctx, true);
    }

    private void addModerator(String username, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.addModerator(username, __ctx);
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
    public void addParticipant(UserPrx userProxy, UserDataIce userData, SessionPrx sessionProxy, String sessionID, String ipAddress, String mobileDevice, String userAgent, short clientVersion, int deviceType) throws FusionException {
        this.addParticipant(userProxy, userData, sessionProxy, sessionID, ipAddress, mobileDevice, userAgent, clientVersion, deviceType, null, false);
    }

    @Override
    public void addParticipant(UserPrx userProxy, UserDataIce userData, SessionPrx sessionProxy, String sessionID, String ipAddress, String mobileDevice, String userAgent, short clientVersion, int deviceType, Map<String, String> __ctx) throws FusionException {
        this.addParticipant(userProxy, userData, sessionProxy, sessionID, ipAddress, mobileDevice, userAgent, clientVersion, deviceType, __ctx, true);
    }

    private void addParticipant(UserPrx userProxy, UserDataIce userData, SessionPrx sessionProxy, String sessionID, String ipAddress, String mobileDevice, String userAgent, short clientVersion, int deviceType, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("addParticipant");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.addParticipant(userProxy, userData, sessionProxy, sessionID, ipAddress, mobileDevice, userAgent, clientVersion, deviceType, __ctx);
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
    public void addParticipantOld(UserPrx userProxy, UserDataIce userData, SessionPrx sessionProxy, String sessionID, String ipAddress, String mobileDevice, String userAgent) throws FusionException {
        this.addParticipantOld(userProxy, userData, sessionProxy, sessionID, ipAddress, mobileDevice, userAgent, null, false);
    }

    @Override
    public void addParticipantOld(UserPrx userProxy, UserDataIce userData, SessionPrx sessionProxy, String sessionID, String ipAddress, String mobileDevice, String userAgent, Map<String, String> __ctx) throws FusionException {
        this.addParticipantOld(userProxy, userData, sessionProxy, sessionID, ipAddress, mobileDevice, userAgent, __ctx, true);
    }

    private void addParticipantOld(UserPrx userProxy, UserDataIce userData, SessionPrx sessionProxy, String sessionID, String ipAddress, String mobileDevice, String userAgent, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("addParticipantOld");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.addParticipantOld(userProxy, userData, sessionProxy, sessionID, ipAddress, mobileDevice, userAgent, __ctx);
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
    public void adminAnnounce(String announceMessage, int waitTime) throws FusionException {
        this.adminAnnounce(announceMessage, waitTime, null, false);
    }

    @Override
    public void adminAnnounce(String announceMessage, int waitTime, Map<String, String> __ctx) throws FusionException {
        this.adminAnnounce(announceMessage, waitTime, __ctx, true);
    }

    private void adminAnnounce(String announceMessage, int waitTime, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("adminAnnounce");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.adminAnnounce(announceMessage, waitTime, __ctx);
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
    public void announceOff(String announcer) throws FusionException {
        this.announceOff(announcer, null, false);
    }

    @Override
    public void announceOff(String announcer, Map<String, String> __ctx) throws FusionException {
        this.announceOff(announcer, __ctx, true);
    }

    private void announceOff(String announcer, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("announceOff");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.announceOff(announcer, __ctx);
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
    public void announceOn(String announcer, String announceMessage, int waitTime) throws FusionException {
        this.announceOn(announcer, announceMessage, waitTime, null, false);
    }

    @Override
    public void announceOn(String announcer, String announceMessage, int waitTime, Map<String, String> __ctx) throws FusionException {
        this.announceOn(announcer, announceMessage, waitTime, __ctx, true);
    }

    private void announceOn(String announcer, String announceMessage, int waitTime, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("announceOn");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.announceOn(announcer, announceMessage, waitTime, __ctx);
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
    public void banGroupMembers(String[] banList, String instigator, int reasonCode) throws FusionException {
        this.banGroupMembers(banList, instigator, reasonCode, null, false);
    }

    @Override
    public void banGroupMembers(String[] banList, String instigator, int reasonCode, Map<String, String> __ctx) throws FusionException {
        this.banGroupMembers(banList, instigator, reasonCode, __ctx, true);
    }

    private void banGroupMembers(String[] banList, String instigator, int reasonCode, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("banGroupMembers");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.banGroupMembers(banList, instigator, reasonCode, __ctx);
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
    public void banIndexes(int[] indexes, String bannedBy, int reasonCode) throws FusionException {
        this.banIndexes(indexes, bannedBy, reasonCode, null, false);
    }

    @Override
    public void banIndexes(int[] indexes, String bannedBy, int reasonCode, Map<String, String> __ctx) throws FusionException {
        this.banIndexes(indexes, bannedBy, reasonCode, __ctx, true);
    }

    private void banIndexes(int[] indexes, String bannedBy, int reasonCode, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("banIndexes");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.banIndexes(indexes, bannedBy, reasonCode, __ctx);
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
    public void banMultiIds(String username) throws FusionException {
        this.banMultiIds(username, null, false);
    }

    @Override
    public void banMultiIds(String username, Map<String, String> __ctx) throws FusionException {
        this.banMultiIds(username, __ctx, true);
    }

    private void banMultiIds(String username, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("banMultiIds");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.banMultiIds(username, __ctx);
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
    public void banUser(String username) {
        this.banUser(username, null, false);
    }

    @Override
    public void banUser(String username, Map<String, String> __ctx) {
        this.banUser(username, __ctx, true);
    }

    private void banUser(String username, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.banUser(username, __ctx);
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
    public void broadcastMessage(String instigator, String message) throws FusionException {
        this.broadcastMessage(instigator, message, null, false);
    }

    @Override
    public void broadcastMessage(String instigator, String message, Map<String, String> __ctx) throws FusionException {
        this.broadcastMessage(instigator, message, __ctx, true);
    }

    private void broadcastMessage(String instigator, String message, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("broadcastMessage");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.broadcastMessage(instigator, message, __ctx);
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
    public void bumpUser(String instigator, String target) throws FusionException {
        this.bumpUser(instigator, target, null, false);
    }

    @Override
    public void bumpUser(String instigator, String target, Map<String, String> __ctx) throws FusionException {
        this.bumpUser(instigator, target, __ctx, true);
    }

    private void bumpUser(String instigator, String target, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("bumpUser");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.bumpUser(instigator, target, __ctx);
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
    public void changeOwner(String oldOwnerUsername, String newOwnerUsername) {
        this.changeOwner(oldOwnerUsername, newOwnerUsername, null, false);
    }

    @Override
    public void changeOwner(String oldOwnerUsername, String newOwnerUsername, Map<String, String> __ctx) {
        this.changeOwner(oldOwnerUsername, newOwnerUsername, __ctx, true);
    }

    private void changeOwner(String oldOwnerUsername, String newOwnerUsername, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.changeOwner(oldOwnerUsername, newOwnerUsername, __ctx);
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
    public void clearUserKick(String instigator, String target) throws FusionException {
        this.clearUserKick(instigator, target, null, false);
    }

    @Override
    public void clearUserKick(String instigator, String target, Map<String, String> __ctx) throws FusionException {
        this.clearUserKick(instigator, target, __ctx, true);
    }

    private void clearUserKick(String instigator, String target, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("clearUserKick");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.clearUserKick(instigator, target, __ctx);
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
    public void convertIntoGroupChatRoom(int groupID, String groupName) throws FusionException {
        this.convertIntoGroupChatRoom(groupID, groupName, null, false);
    }

    @Override
    public void convertIntoGroupChatRoom(int groupID, String groupName, Map<String, String> __ctx) throws FusionException {
        this.convertIntoGroupChatRoom(groupID, groupName, __ctx, true);
    }

    private void convertIntoGroupChatRoom(int groupID, String groupName, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("convertIntoGroupChatRoom");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.convertIntoGroupChatRoom(groupID, groupName, __ctx);
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
    public void convertIntoUserOwnedChatRoom() throws FusionException {
        this.convertIntoUserOwnedChatRoom(null, false);
    }

    @Override
    public void convertIntoUserOwnedChatRoom(Map<String, String> __ctx) throws FusionException {
        this.convertIntoUserOwnedChatRoom(__ctx, true);
    }

    private void convertIntoUserOwnedChatRoom(Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("convertIntoUserOwnedChatRoom");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.convertIntoUserOwnedChatRoom(__ctx);
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
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
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
    public String[] getAdministrators(String requestingUsername) {
        return this.getAdministrators(requestingUsername, null, false);
    }

    @Override
    public String[] getAdministrators(String requestingUsername, Map<String, String> __ctx) {
        return this.getAdministrators(requestingUsername, __ctx, true);
    }

    private String[] getAdministrators(String requestingUsername, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getAdministrators");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                return __del.getAdministrators(requestingUsername, __ctx);
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
    public String[] getAllParticipants(String requestingUsername) {
        return this.getAllParticipants(requestingUsername, null, false);
    }

    @Override
    public String[] getAllParticipants(String requestingUsername, Map<String, String> __ctx) {
        return this.getAllParticipants(requestingUsername, __ctx, true);
    }

    private String[] getAllParticipants(String requestingUsername, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getAllParticipants");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                return __del.getAllParticipants(requestingUsername, __ctx);
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
    public String[] getGroupModerators(String instigator) throws FusionException {
        return this.getGroupModerators(instigator, null, false);
    }

    @Override
    public String[] getGroupModerators(String instigator, Map<String, String> __ctx) throws FusionException {
        return this.getGroupModerators(instigator, __ctx, true);
    }

    private String[] getGroupModerators(String instigator, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getGroupModerators");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                return __del.getGroupModerators(instigator, __ctx);
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
    public int getMaximumMessageLength(String sender) {
        return this.getMaximumMessageLength(sender, null, false);
    }

    @Override
    public int getMaximumMessageLength(String sender, Map<String, String> __ctx) {
        return this.getMaximumMessageLength(sender, __ctx, true);
    }

    private int getMaximumMessageLength(String sender, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getMaximumMessageLength");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                return __del.getMaximumMessageLength(sender, __ctx);
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
    public int getNumParticipants() {
        return this.getNumParticipants(null, false);
    }

    @Override
    public int getNumParticipants(Map<String, String> __ctx) {
        return this.getNumParticipants(__ctx, true);
    }

    private int getNumParticipants(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getNumParticipants");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                return __del.getNumParticipants(__ctx);
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
    public ChatRoomDataIce getRoomData() {
        return this.getRoomData(null, false);
    }

    @Override
    public ChatRoomDataIce getRoomData(Map<String, String> __ctx) {
        return this.getRoomData(__ctx, true);
    }

    private ChatRoomDataIce getRoomData(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getRoomData");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                return __del.getRoomData(__ctx);
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
    public Map<String, String> getTheme() {
        return this.getTheme(null, false);
    }

    @Override
    public Map<String, String> getTheme(Map<String, String> __ctx) {
        return this.getTheme(__ctx, true);
    }

    private Map<String, String> getTheme(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getTheme");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                return __del.getTheme(__ctx);
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
    public void inviteUserToGroup(String invitee, String inviter) throws FusionException {
        this.inviteUserToGroup(invitee, inviter, null, false);
    }

    @Override
    public void inviteUserToGroup(String invitee, String inviter, Map<String, String> __ctx) throws FusionException {
        this.inviteUserToGroup(invitee, inviter, __ctx, true);
    }

    private void inviteUserToGroup(String invitee, String inviter, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("inviteUserToGroup");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.inviteUserToGroup(invitee, inviter, __ctx);
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
    public boolean isLocked() {
        return this.isLocked(null, false);
    }

    @Override
    public boolean isLocked(Map<String, String> __ctx) {
        return this.isLocked(__ctx, true);
    }

    private boolean isLocked(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("isLocked");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                return __del.isLocked(__ctx);
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
    public boolean isVisibleParticipant(String username) throws FusionException {
        return this.isVisibleParticipant(username, null, false);
    }

    @Override
    public boolean isVisibleParticipant(String username, Map<String, String> __ctx) throws FusionException {
        return this.isVisibleParticipant(username, __ctx, true);
    }

    private boolean isVisibleParticipant(String username, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("isVisibleParticipant");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                return __del.isVisibleParticipant(username, __ctx);
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
    public void kickIndexes(int[] indexes, String bannedBy) throws FusionException {
        this.kickIndexes(indexes, bannedBy, null, false);
    }

    @Override
    public void kickIndexes(int[] indexes, String bannedBy, Map<String, String> __ctx) throws FusionException {
        this.kickIndexes(indexes, bannedBy, __ctx, true);
    }

    private void kickIndexes(int[] indexes, String bannedBy, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("kickIndexes");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.kickIndexes(indexes, bannedBy, __ctx);
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
    public void listParticipants(String requestingUsername, int size, int startIndex) throws FusionException {
        this.listParticipants(requestingUsername, size, startIndex, null, false);
    }

    @Override
    public void listParticipants(String requestingUsername, int size, int startIndex, Map<String, String> __ctx) throws FusionException {
        this.listParticipants(requestingUsername, size, startIndex, __ctx, true);
    }

    private void listParticipants(String requestingUsername, int size, int startIndex, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("listParticipants");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.listParticipants(requestingUsername, size, startIndex, __ctx);
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
    public void lock(String locker) throws FusionException {
        this.lock(locker, null, false);
    }

    @Override
    public void lock(String locker, Map<String, String> __ctx) throws FusionException {
        this.lock(locker, __ctx, true);
    }

    private void lock(String locker, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("lock");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.lock(locker, __ctx);
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
    public void mute(String username, String target) throws FusionException {
        this.mute(username, target, null, false);
    }

    @Override
    public void mute(String username, String target, Map<String, String> __ctx) throws FusionException {
        this.mute(username, target, __ctx, true);
    }

    private void mute(String username, String target, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("mute");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.mute(username, target, __ctx);
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
    public void putMessage(MessageDataIce message, String sessionID) throws FusionException {
        this.putMessage(message, sessionID, null, false);
    }

    @Override
    public void putMessage(MessageDataIce message, String sessionID, Map<String, String> __ctx) throws FusionException {
        this.putMessage(message, sessionID, __ctx, true);
    }

    private void putMessage(MessageDataIce message, String sessionID, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("putMessage");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.putMessage(message, sessionID, __ctx);
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
    public void putSystemMessage(String messageText, String[] emoticonKeys) {
        this.putSystemMessage(messageText, emoticonKeys, null, false);
    }

    @Override
    public void putSystemMessage(String messageText, String[] emoticonKeys, Map<String, String> __ctx) {
        this.putSystemMessage(messageText, emoticonKeys, __ctx, true);
    }

    private void putSystemMessage(String messageText, String[] emoticonKeys, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.putSystemMessage(messageText, emoticonKeys, __ctx);
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
    public void putSystemMessageWithColour(String messageText, String[] emoticonKeys, int messageColour) {
        this.putSystemMessageWithColour(messageText, emoticonKeys, messageColour, null, false);
    }

    @Override
    public void putSystemMessageWithColour(String messageText, String[] emoticonKeys, int messageColour, Map<String, String> __ctx) {
        this.putSystemMessageWithColour(messageText, emoticonKeys, messageColour, __ctx, true);
    }

    private void putSystemMessageWithColour(String messageText, String[] emoticonKeys, int messageColour, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.putSystemMessageWithColour(messageText, emoticonKeys, messageColour, __ctx);
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
    public void removeGroupModerator(String instigator, String targetUser) throws FusionException {
        this.removeGroupModerator(instigator, targetUser, null, false);
    }

    @Override
    public void removeGroupModerator(String instigator, String targetUser, Map<String, String> __ctx) throws FusionException {
        this.removeGroupModerator(instigator, targetUser, __ctx, true);
    }

    private void removeGroupModerator(String instigator, String targetUser, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("removeGroupModerator");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.removeGroupModerator(instigator, targetUser, __ctx);
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
    public void removeModerator(String username) {
        this.removeModerator(username, null, false);
    }

    @Override
    public void removeModerator(String username, Map<String, String> __ctx) {
        this.removeModerator(username, __ctx, true);
    }

    private void removeModerator(String username, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.removeModerator(username, __ctx);
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
    public void removeParticipant(String username) throws FusionException {
        this.removeParticipant(username, null, false);
    }

    @Override
    public void removeParticipant(String username, Map<String, String> __ctx) throws FusionException {
        this.removeParticipant(username, __ctx, true);
    }

    private void removeParticipant(String username, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("removeParticipant");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.removeParticipant(username, __ctx);
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
    public void removeParticipantOneWay(String username, boolean removeFromUsersChatRoomList) {
        this.removeParticipantOneWay(username, removeFromUsersChatRoomList, null, false);
    }

    @Override
    public void removeParticipantOneWay(String username, boolean removeFromUsersChatRoomList, Map<String, String> __ctx) {
        this.removeParticipantOneWay(username, removeFromUsersChatRoomList, __ctx, true);
    }

    private void removeParticipantOneWay(String username, boolean removeFromUsersChatRoomList, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.removeParticipantOneWay(username, removeFromUsersChatRoomList, __ctx);
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
    public void setAdultOnly(boolean adultOnly) {
        this.setAdultOnly(adultOnly, null, false);
    }

    @Override
    public void setAdultOnly(boolean adultOnly, Map<String, String> __ctx) {
        this.setAdultOnly(adultOnly, __ctx, true);
    }

    private void setAdultOnly(boolean adultOnly, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.setAdultOnly(adultOnly, __ctx);
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
    public void setAllowKicking(boolean allowKicking) {
        this.setAllowKicking(allowKicking, null, false);
    }

    @Override
    public void setAllowKicking(boolean allowKicking, Map<String, String> __ctx) {
        this.setAllowKicking(allowKicking, __ctx, true);
    }

    private void setAllowKicking(boolean allowKicking, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.setAllowKicking(allowKicking, __ctx);
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
    public void setDescription(String description) {
        this.setDescription(description, null, false);
    }

    @Override
    public void setDescription(String description, Map<String, String> __ctx) {
        this.setDescription(description, __ctx, true);
    }

    private void setDescription(String description, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.setDescription(description, __ctx);
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
    public void setMaximumSize(int maximumSize) {
        this.setMaximumSize(maximumSize, null, false);
    }

    @Override
    public void setMaximumSize(int maximumSize, Map<String, String> __ctx) {
        this.setMaximumSize(maximumSize, __ctx, true);
    }

    private void setMaximumSize(int maximumSize, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.setMaximumSize(maximumSize, __ctx);
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
    public void setNumberOfFakeParticipants(String username, int number) {
        this.setNumberOfFakeParticipants(username, number, null, false);
    }

    @Override
    public void setNumberOfFakeParticipants(String username, int number, Map<String, String> __ctx) {
        this.setNumberOfFakeParticipants(username, number, __ctx, true);
    }

    private void setNumberOfFakeParticipants(String username, int number, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.setNumberOfFakeParticipants(username, number, __ctx);
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
    public void silence(String username, int timeout) throws FusionException {
        this.silence(username, timeout, null, false);
    }

    @Override
    public void silence(String username, int timeout, Map<String, String> __ctx) throws FusionException {
        this.silence(username, timeout, __ctx, true);
    }

    private void silence(String username, int timeout, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("silence");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.silence(username, timeout, __ctx);
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
    public void silenceUser(String instigator, String target, int timeout) throws FusionException {
        this.silenceUser(instigator, target, timeout, null, false);
    }

    @Override
    public void silenceUser(String instigator, String target, int timeout, Map<String, String> __ctx) throws FusionException {
        this.silenceUser(instigator, target, timeout, __ctx, true);
    }

    private void silenceUser(String instigator, String target, int timeout, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("silenceUser");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.silenceUser(instigator, target, timeout, __ctx);
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
    public void submitGiftAllTask(int giftId, String giftMessage, MessageDataIce message) throws FusionException {
        this.submitGiftAllTask(giftId, giftMessage, message, null, false);
    }

    @Override
    public void submitGiftAllTask(int giftId, String giftMessage, MessageDataIce message, Map<String, String> __ctx) throws FusionException {
        this.submitGiftAllTask(giftId, giftMessage, message, __ctx, true);
    }

    private void submitGiftAllTask(int giftId, String giftMessage, MessageDataIce message, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("submitGiftAllTask");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.submitGiftAllTask(giftId, giftMessage, message, __ctx);
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
    public void unbanGroupMember(String target, String instigator, int reasonCode) throws FusionException {
        this.unbanGroupMember(target, instigator, reasonCode, null, false);
    }

    @Override
    public void unbanGroupMember(String target, String instigator, int reasonCode, Map<String, String> __ctx) throws FusionException {
        this.unbanGroupMember(target, instigator, reasonCode, __ctx, true);
    }

    private void unbanGroupMember(String target, String instigator, int reasonCode, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("unbanGroupMember");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.unbanGroupMember(target, instigator, reasonCode, __ctx);
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
    public void unbanUser(String username) {
        this.unbanUser(username, null, false);
    }

    @Override
    public void unbanUser(String username, Map<String, String> __ctx) {
        this.unbanUser(username, __ctx, true);
    }

    private void unbanUser(String username, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.unbanUser(username, __ctx);
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
    public void unlock(String unlocker) throws FusionException {
        this.unlock(unlocker, null, false);
    }

    @Override
    public void unlock(String unlocker, Map<String, String> __ctx) throws FusionException {
        this.unlock(unlocker, __ctx, true);
    }

    private void unlock(String unlocker, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("unlock");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.unlock(unlocker, __ctx);
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
    public void unmute(String username, String target) throws FusionException {
        this.unmute(username, target, null, false);
    }

    @Override
    public void unmute(String username, String target, Map<String, String> __ctx) throws FusionException {
        this.unmute(username, target, __ctx, true);
    }

    private void unmute(String username, String target, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("unmute");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.unmute(username, target, __ctx);
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
    public void unsilence(String username) throws FusionException {
        this.unsilence(username, null, false);
    }

    @Override
    public void unsilence(String username, Map<String, String> __ctx) throws FusionException {
        this.unsilence(username, __ctx, true);
    }

    private void unsilence(String username, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("unsilence");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.unsilence(username, __ctx);
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
    public void unsilenceUser(String instigator, String target) throws FusionException {
        this.unsilenceUser(instigator, target, null, false);
    }

    @Override
    public void unsilenceUser(String instigator, String target, Map<String, String> __ctx) throws FusionException {
        this.unsilenceUser(instigator, target, __ctx, true);
    }

    private void unsilenceUser(String instigator, String target, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("unsilenceUser");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.unsilenceUser(instigator, target, __ctx);
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
    public void updateDescription(String instigator, String description) throws FusionException {
        this.updateDescription(instigator, description, null, false);
    }

    @Override
    public void updateDescription(String instigator, String description, Map<String, String> __ctx) throws FusionException {
        this.updateDescription(instigator, description, __ctx, true);
    }

    private void updateDescription(String instigator, String description, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("updateDescription");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.updateDescription(instigator, description, __ctx);
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
    public void updateExtraData(ChatRoomDataIce data) {
        this.updateExtraData(data, null, false);
    }

    @Override
    public void updateExtraData(ChatRoomDataIce data, Map<String, String> __ctx) {
        this.updateExtraData(data, __ctx, true);
    }

    private void updateExtraData(ChatRoomDataIce data, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.updateExtraData(data, __ctx);
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
    public void updateGroupModeratorStatus(String username, boolean promote) {
        this.updateGroupModeratorStatus(username, promote, null, false);
    }

    @Override
    public void updateGroupModeratorStatus(String username, boolean promote, Map<String, String> __ctx) {
        this.updateGroupModeratorStatus(username, promote, __ctx, true);
    }

    private void updateGroupModeratorStatus(String username, boolean promote, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.updateGroupModeratorStatus(username, promote, __ctx);
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
    public void voteToKickUser(String voter, String target) throws FusionException {
        this.voteToKickUser(voter, target, null, false);
    }

    @Override
    public void voteToKickUser(String voter, String target, Map<String, String> __ctx) throws FusionException {
        this.voteToKickUser(voter, target, __ctx, true);
    }

    private void voteToKickUser(String voter, String target, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("voteToKickUser");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.voteToKickUser(voter, target, __ctx);
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
    public void warnUser(String instigator, String target, String message) throws FusionException {
        this.warnUser(instigator, target, message, null, false);
    }

    @Override
    public void warnUser(String instigator, String target, String message, Map<String, String> __ctx) throws FusionException {
        this.warnUser(instigator, target, message, __ctx, true);
    }

    private void warnUser(String instigator, String target, String message, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("warnUser");
                __delBase = this.__getDelegate(false);
                _ChatRoomDel __del = (_ChatRoomDel)__delBase;
                __del.warnUser(instigator, target, message, __ctx);
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

    public static ChatRoomPrx checkedCast(ObjectPrx __obj) {
        ChatRoomPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (ChatRoomPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::ChatRoom")) break block3;
                    ChatRoomPrxHelper __h = new ChatRoomPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static ChatRoomPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        ChatRoomPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (ChatRoomPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::ChatRoom", __ctx)) break block3;
                    ChatRoomPrxHelper __h = new ChatRoomPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static ChatRoomPrx checkedCast(ObjectPrx __obj, String __facet) {
        ChatRoomPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::ChatRoom")) {
                    ChatRoomPrxHelper __h = new ChatRoomPrxHelper();
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

    public static ChatRoomPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        ChatRoomPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::ChatRoom", __ctx)) {
                    ChatRoomPrxHelper __h = new ChatRoomPrxHelper();
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

    public static ChatRoomPrx uncheckedCast(ObjectPrx __obj) {
        ChatRoomPrx __d = null;
        if (__obj != null) {
            try {
                __d = (ChatRoomPrx)__obj;
            }
            catch (ClassCastException ex) {
                ChatRoomPrxHelper __h = new ChatRoomPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static ChatRoomPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        ChatRoomPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            ChatRoomPrxHelper __h = new ChatRoomPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _ChatRoomDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _ChatRoomDelD();
    }

    public static void __write(BasicStream __os, ChatRoomPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static ChatRoomPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            ChatRoomPrxHelper result = new ChatRoomPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

