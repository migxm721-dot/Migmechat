/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 *  Ice.DispatchStatus
 *  Ice.InputStream
 *  Ice.MarshalException
 *  Ice.Object
 *  Ice.ObjectImpl
 *  Ice.OperationMode
 *  Ice.OperationNotExistException
 *  Ice.OutputStream
 *  Ice.UserException
 *  IceInternal.BasicStream
 *  IceInternal.Incoming
 */
package com.projectgoth.fusion.slice;

import Ice.Current;
import Ice.DispatchStatus;
import Ice.InputStream;
import Ice.MarshalException;
import Ice.ObjectImpl;
import Ice.OperationMode;
import Ice.OperationNotExistException;
import Ice.OutputStream;
import Ice.UserException;
import IceInternal.BasicStream;
import IceInternal.Incoming;
import com.projectgoth.fusion.slice.AMD_Session_endSession;
import com.projectgoth.fusion.slice.AMD_Session_putMessage;
import com.projectgoth.fusion.slice.AMD_Session_sendMessage;
import com.projectgoth.fusion.slice.ByteArrayHelper;
import com.projectgoth.fusion.slice.ChatListIce;
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.ChatRoomPrxHelper;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GroupChatPrx;
import com.projectgoth.fusion.slice.GroupChatPrxHelper;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.MessageSwitchboardPrx;
import com.projectgoth.fusion.slice.MessageSwitchboardPrxHelper;
import com.projectgoth.fusion.slice.Session;
import com.projectgoth.fusion.slice.SessionMetricsIce;
import com.projectgoth.fusion.slice.StringArrayHelper;
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.slice.UserPrxHelper;
import com.projectgoth.fusion.slice._AMD_Session_endSession;
import com.projectgoth.fusion.slice._AMD_Session_putMessage;
import com.projectgoth.fusion.slice._AMD_Session_sendMessage;
import java.util.Arrays;

public abstract class _SessionDisp
extends ObjectImpl
implements Session {
    public static final String[] __ids = new String[]{"::Ice::Object", "::com::projectgoth::fusion::slice::Session"};
    private static final String[] __all = new String[]{"chatroomJoined", "endSession", "endSessionOneWay", "findGroupChatObject", "friendInvitedByPhoneNumber", "friendInvitedByUsername", "getChatListVersion", "getClientVersionIce", "getDeviceTypeAsInt", "getMessageSwitchboard", "getMobileDeviceIce", "getParentUsername", "getRemoteIPAddress", "getSessionID", "getSessionMetrics", "getUserAgentIce", "getUserProxy", "groupChatJoined", "groupChatJoinedMultiple", "ice_id", "ice_ids", "ice_isA", "ice_ping", "notifyUserJoinedChatRoomOneWay", "notifyUserJoinedGroupChat", "notifyUserLeftChatRoomOneWay", "notifyUserLeftGroupChat", "photoUploaded", "privateChattedWith", "profileEdited", "putAlertMessage", "putAlertMessageOneWay", "putMessage", "putMessageOneWay", "putSerializedPacket", "putSerializedPacketOneWay", "sendGroupChatParticipantArrays", "sendGroupChatParticipants", "sendMessage", "sendMessageBackToUserAsEmote", "setChatListVersion", "setCurrentChatListGroupChatSubset", "setLanguage", "setPresence", "silentlyDropIncomingPackets", "statusMessageSet", "themeUpdated", "touch"};

    protected void ice_copyStateFrom(Ice.Object __obj) throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public boolean ice_isA(String s) {
        return Arrays.binarySearch(__ids, s) >= 0;
    }

    public boolean ice_isA(String s, Current __current) {
        return Arrays.binarySearch(__ids, s) >= 0;
    }

    public String[] ice_ids() {
        return __ids;
    }

    public String[] ice_ids(Current __current) {
        return __ids;
    }

    public String ice_id() {
        return __ids[1];
    }

    public String ice_id(Current __current) {
        return __ids[1];
    }

    public static String ice_staticId() {
        return __ids[1];
    }

    public final void chatroomJoined(ChatRoomPrx roomProxy, String name) {
        this.chatroomJoined(roomProxy, name, null);
    }

    public final void endSession_async(AMD_Session_endSession __cb) throws FusionException {
        this.endSession_async(__cb, null);
    }

    public final void endSessionOneWay() {
        this.endSessionOneWay(null);
    }

    public final GroupChatPrx findGroupChatObject(String groupChatID) throws FusionException {
        return this.findGroupChatObject(groupChatID, null);
    }

    public final void friendInvitedByPhoneNumber() {
        this.friendInvitedByPhoneNumber(null);
    }

    public final void friendInvitedByUsername() {
        this.friendInvitedByUsername(null);
    }

    public final int getChatListVersion() throws FusionException {
        return this.getChatListVersion(null);
    }

    public final short getClientVersionIce() {
        return this.getClientVersionIce(null);
    }

    public final int getDeviceTypeAsInt() {
        return this.getDeviceTypeAsInt(null);
    }

    public final MessageSwitchboardPrx getMessageSwitchboard() throws FusionException {
        return this.getMessageSwitchboard(null);
    }

    public final String getMobileDeviceIce() {
        return this.getMobileDeviceIce(null);
    }

    public final String getParentUsername() throws FusionException {
        return this.getParentUsername(null);
    }

    public final String getRemoteIPAddress() {
        return this.getRemoteIPAddress(null);
    }

    public final String getSessionID() {
        return this.getSessionID(null);
    }

    public final SessionMetricsIce getSessionMetrics() {
        return this.getSessionMetrics(null);
    }

    public final String getUserAgentIce() {
        return this.getUserAgentIce(null);
    }

    public final UserPrx getUserProxy(String username) throws FusionException {
        return this.getUserProxy(username, null);
    }

    public final void groupChatJoined(String id) {
        this.groupChatJoined(id, null);
    }

    public final void groupChatJoinedMultiple(String id, int increment) {
        this.groupChatJoinedMultiple(id, increment, null);
    }

    public final void notifyUserJoinedChatRoomOneWay(String chatroomname, String username, boolean isAdministrator, boolean isMuted) {
        this.notifyUserJoinedChatRoomOneWay(chatroomname, username, isAdministrator, isMuted, null);
    }

    public final void notifyUserJoinedGroupChat(String groupChatId, String username, boolean isMuted) throws FusionException {
        this.notifyUserJoinedGroupChat(groupChatId, username, isMuted, null);
    }

    public final void notifyUserLeftChatRoomOneWay(String chatroomname, String username) {
        this.notifyUserLeftChatRoomOneWay(chatroomname, username, null);
    }

    public final void notifyUserLeftGroupChat(String groupChatId, String username) throws FusionException {
        this.notifyUserLeftGroupChat(groupChatId, username, null);
    }

    public final void photoUploaded() {
        this.photoUploaded(null);
    }

    public final boolean privateChattedWith(String username) {
        return this.privateChattedWith(username, null);
    }

    public final void profileEdited() {
        this.profileEdited(null);
    }

    public final void putAlertMessage(String message, String title, short timeout) throws FusionException {
        this.putAlertMessage(message, title, timeout, null);
    }

    public final void putAlertMessageOneWay(String message, String title, short timeout) {
        this.putAlertMessageOneWay(message, title, timeout, null);
    }

    public final void putMessage_async(AMD_Session_putMessage __cb, MessageDataIce message) throws FusionException {
        this.putMessage_async(__cb, message, null);
    }

    public final void putMessageOneWay(MessageDataIce message) {
        this.putMessageOneWay(message, null);
    }

    public final void putSerializedPacket(byte[] packet) throws FusionException {
        this.putSerializedPacket(packet, null);
    }

    public final void putSerializedPacketOneWay(byte[] packet) {
        this.putSerializedPacketOneWay(packet, null);
    }

    public final void sendGroupChatParticipantArrays(String groupChatId, byte imType, String[] participants, String[] mutedParticipants) throws FusionException {
        this.sendGroupChatParticipantArrays(groupChatId, imType, participants, mutedParticipants, null);
    }

    public final void sendGroupChatParticipants(String groupChatId, byte imType, String participants, String mutedParticipants) throws FusionException {
        this.sendGroupChatParticipants(groupChatId, imType, participants, mutedParticipants, null);
    }

    public final void sendMessage_async(AMD_Session_sendMessage __cb, MessageDataIce message) throws FusionException {
        this.sendMessage_async(__cb, message, null);
    }

    public final void sendMessageBackToUserAsEmote(MessageDataIce message) throws FusionException {
        this.sendMessageBackToUserAsEmote(message, null);
    }

    public final void setChatListVersion(int version) throws FusionException {
        this.setChatListVersion(version, null);
    }

    public final void setCurrentChatListGroupChatSubset(ChatListIce ccl) {
        this.setCurrentChatListGroupChatSubset(ccl, null);
    }

    public final void setLanguage(String language) {
        this.setLanguage(language, null);
    }

    public final void setPresence(int presence) throws FusionException {
        this.setPresence(presence, null);
    }

    public final void silentlyDropIncomingPackets() {
        this.silentlyDropIncomingPackets(null);
    }

    public final void statusMessageSet() {
        this.statusMessageSet(null);
    }

    public final void themeUpdated() {
        this.themeUpdated(null);
    }

    public final void touch() throws FusionException {
        this.touch(null);
    }

    public static DispatchStatus ___sendMessage(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        MessageDataIce message = new MessageDataIce();
        message.__read(__is);
        __is.endReadEncaps();
        _AMD_Session_sendMessage __cb = new _AMD_Session_sendMessage(__inS);
        try {
            __obj.sendMessage_async(__cb, message, __current);
        }
        catch (Exception ex) {
            __cb.ice_exception(ex);
        }
        return DispatchStatus.DispatchAsync;
    }

    public static DispatchStatus ___setPresence(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int presence = __is.readInt();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            __obj.setPresence(presence, __current);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___endSession(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        __inS.is().skipEmptyEncaps();
        _AMD_Session_endSession __cb = new _AMD_Session_endSession(__inS);
        try {
            __obj.endSession_async(__cb, __current);
        }
        catch (Exception ex) {
            __cb.ice_exception(ex);
        }
        return DispatchStatus.DispatchAsync;
    }

    public static DispatchStatus ___endSessionOneWay(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        __inS.is().skipEmptyEncaps();
        __obj.endSessionOneWay(__current);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___touch(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        __inS.is().skipEmptyEncaps();
        BasicStream __os = __inS.os();
        try {
            __obj.touch(__current);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___putMessage(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        MessageDataIce message = new MessageDataIce();
        message.__read(__is);
        __is.endReadEncaps();
        _AMD_Session_putMessage __cb = new _AMD_Session_putMessage(__inS);
        try {
            __obj.putMessage_async(__cb, message, __current);
        }
        catch (Exception ex) {
            __cb.ice_exception(ex);
        }
        return DispatchStatus.DispatchAsync;
    }

    public static DispatchStatus ___putMessageOneWay(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        MessageDataIce message = new MessageDataIce();
        message.__read(__is);
        __is.endReadEncaps();
        __obj.putMessageOneWay(message, __current);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___sendMessageBackToUserAsEmote(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        MessageDataIce message = new MessageDataIce();
        message.__read(__is);
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            __obj.sendMessageBackToUserAsEmote(message, __current);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___putAlertMessage(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String message = __is.readString();
        String title = __is.readString();
        short timeout = __is.readShort();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            __obj.putAlertMessage(message, title, timeout, __current);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___putAlertMessageOneWay(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String message = __is.readString();
        String title = __is.readString();
        short timeout = __is.readShort();
        __is.endReadEncaps();
        __obj.putAlertMessageOneWay(message, title, timeout, __current);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___getParentUsername(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        __inS.is().skipEmptyEncaps();
        BasicStream __os = __inS.os();
        try {
            String __ret = __obj.getParentUsername(__current);
            __os.writeString(__ret);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___getUserProxy(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String username = __is.readString();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            UserPrx __ret = __obj.getUserProxy(username, __current);
            UserPrxHelper.__write(__os, __ret);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___profileEdited(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        __inS.is().skipEmptyEncaps();
        __obj.profileEdited(__current);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___groupChatJoined(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id = __is.readString();
        __is.endReadEncaps();
        __obj.groupChatJoined(id, __current);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___groupChatJoinedMultiple(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id = __is.readString();
        int increment = __is.readInt();
        __is.endReadEncaps();
        __obj.groupChatJoinedMultiple(id, increment, __current);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___chatroomJoined(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        ChatRoomPrx roomProxy = ChatRoomPrxHelper.__read(__is);
        String name = __is.readString();
        __is.endReadEncaps();
        __obj.chatroomJoined(roomProxy, name, __current);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___statusMessageSet(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        __inS.is().skipEmptyEncaps();
        __obj.statusMessageSet(__current);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___photoUploaded(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        __inS.is().skipEmptyEncaps();
        __obj.photoUploaded(__current);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___friendInvitedByPhoneNumber(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        __inS.is().skipEmptyEncaps();
        __obj.friendInvitedByPhoneNumber(__current);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___friendInvitedByUsername(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        __inS.is().skipEmptyEncaps();
        __obj.friendInvitedByUsername(__current);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___themeUpdated(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        __inS.is().skipEmptyEncaps();
        __obj.themeUpdated(__current);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___silentlyDropIncomingPackets(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        __inS.is().skipEmptyEncaps();
        __obj.silentlyDropIncomingPackets(__current);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___getSessionID(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        __inS.is().skipEmptyEncaps();
        BasicStream __os = __inS.os();
        String __ret = __obj.getSessionID(__current);
        __os.writeString(__ret);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___getRemoteIPAddress(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        __inS.is().skipEmptyEncaps();
        BasicStream __os = __inS.os();
        String __ret = __obj.getRemoteIPAddress(__current);
        __os.writeString(__ret);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___getMobileDeviceIce(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        __inS.is().skipEmptyEncaps();
        BasicStream __os = __inS.os();
        String __ret = __obj.getMobileDeviceIce(__current);
        __os.writeString(__ret);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___getUserAgentIce(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        __inS.is().skipEmptyEncaps();
        BasicStream __os = __inS.os();
        String __ret = __obj.getUserAgentIce(__current);
        __os.writeString(__ret);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___getClientVersionIce(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        __inS.is().skipEmptyEncaps();
        BasicStream __os = __inS.os();
        short __ret = __obj.getClientVersionIce(__current);
        __os.writeShort(__ret);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___getDeviceTypeAsInt(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        __inS.is().skipEmptyEncaps();
        BasicStream __os = __inS.os();
        int __ret = __obj.getDeviceTypeAsInt(__current);
        __os.writeInt(__ret);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___setLanguage(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String language = __is.readString();
        __is.endReadEncaps();
        __obj.setLanguage(language, __current);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___notifyUserLeftChatRoomOneWay(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String chatroomname = __is.readString();
        String username = __is.readString();
        __is.endReadEncaps();
        __obj.notifyUserLeftChatRoomOneWay(chatroomname, username, __current);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___notifyUserJoinedChatRoomOneWay(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String chatroomname = __is.readString();
        String username = __is.readString();
        boolean isAdministrator = __is.readBool();
        boolean isMuted = __is.readBool();
        __is.endReadEncaps();
        __obj.notifyUserJoinedChatRoomOneWay(chatroomname, username, isAdministrator, isMuted, __current);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___notifyUserLeftGroupChat(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String groupChatId = __is.readString();
        String username = __is.readString();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            __obj.notifyUserLeftGroupChat(groupChatId, username, __current);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___notifyUserJoinedGroupChat(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String groupChatId = __is.readString();
        String username = __is.readString();
        boolean isMuted = __is.readBool();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            __obj.notifyUserJoinedGroupChat(groupChatId, username, isMuted, __current);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___sendGroupChatParticipants(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String groupChatId = __is.readString();
        byte imType = __is.readByte();
        String participants = __is.readString();
        String mutedParticipants = __is.readString();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            __obj.sendGroupChatParticipants(groupChatId, imType, participants, mutedParticipants, __current);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___sendGroupChatParticipantArrays(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String groupChatId = __is.readString();
        byte imType = __is.readByte();
        String[] participants = StringArrayHelper.read(__is);
        String[] mutedParticipants = StringArrayHelper.read(__is);
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            __obj.sendGroupChatParticipantArrays(groupChatId, imType, participants, mutedParticipants, __current);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___getChatListVersion(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        __inS.is().skipEmptyEncaps();
        BasicStream __os = __inS.os();
        try {
            int __ret = __obj.getChatListVersion(__current);
            __os.writeInt(__ret);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___setChatListVersion(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int version = __is.readInt();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            __obj.setChatListVersion(version, __current);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___putSerializedPacket(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        byte[] packet = ByteArrayHelper.read(__is);
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            __obj.putSerializedPacket(packet, __current);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___putSerializedPacketOneWay(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        byte[] packet = ByteArrayHelper.read(__is);
        __is.endReadEncaps();
        __obj.putSerializedPacketOneWay(packet, __current);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___findGroupChatObject(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String groupChatID = __is.readString();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            GroupChatPrx __ret = __obj.findGroupChatObject(groupChatID, __current);
            GroupChatPrxHelper.__write(__os, __ret);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___getMessageSwitchboard(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        __inS.is().skipEmptyEncaps();
        BasicStream __os = __inS.os();
        try {
            MessageSwitchboardPrx __ret = __obj.getMessageSwitchboard(__current);
            MessageSwitchboardPrxHelper.__write(__os, __ret);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___privateChattedWith(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String username = __is.readString();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        boolean __ret = __obj.privateChattedWith(username, __current);
        __os.writeBool(__ret);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___getSessionMetrics(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        __inS.is().skipEmptyEncaps();
        BasicStream __os = __inS.os();
        SessionMetricsIce __ret = __obj.getSessionMetrics(__current);
        __ret.__write(__os);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___setCurrentChatListGroupChatSubset(Session __obj, Incoming __inS, Current __current) {
        _SessionDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        ChatListIce ccl = new ChatListIce();
        ccl.__read(__is);
        __is.endReadEncaps();
        __obj.setCurrentChatListGroupChatSubset(ccl, __current);
        return DispatchStatus.DispatchOK;
    }

    public DispatchStatus __dispatch(Incoming in, Current __current) {
        int pos = Arrays.binarySearch(__all, __current.operation);
        if (pos < 0) {
            throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
        }
        switch (pos) {
            case 0: {
                return _SessionDisp.___chatroomJoined(this, in, __current);
            }
            case 1: {
                return _SessionDisp.___endSession(this, in, __current);
            }
            case 2: {
                return _SessionDisp.___endSessionOneWay(this, in, __current);
            }
            case 3: {
                return _SessionDisp.___findGroupChatObject(this, in, __current);
            }
            case 4: {
                return _SessionDisp.___friendInvitedByPhoneNumber(this, in, __current);
            }
            case 5: {
                return _SessionDisp.___friendInvitedByUsername(this, in, __current);
            }
            case 6: {
                return _SessionDisp.___getChatListVersion(this, in, __current);
            }
            case 7: {
                return _SessionDisp.___getClientVersionIce(this, in, __current);
            }
            case 8: {
                return _SessionDisp.___getDeviceTypeAsInt(this, in, __current);
            }
            case 9: {
                return _SessionDisp.___getMessageSwitchboard(this, in, __current);
            }
            case 10: {
                return _SessionDisp.___getMobileDeviceIce(this, in, __current);
            }
            case 11: {
                return _SessionDisp.___getParentUsername(this, in, __current);
            }
            case 12: {
                return _SessionDisp.___getRemoteIPAddress(this, in, __current);
            }
            case 13: {
                return _SessionDisp.___getSessionID(this, in, __current);
            }
            case 14: {
                return _SessionDisp.___getSessionMetrics(this, in, __current);
            }
            case 15: {
                return _SessionDisp.___getUserAgentIce(this, in, __current);
            }
            case 16: {
                return _SessionDisp.___getUserProxy(this, in, __current);
            }
            case 17: {
                return _SessionDisp.___groupChatJoined(this, in, __current);
            }
            case 18: {
                return _SessionDisp.___groupChatJoinedMultiple(this, in, __current);
            }
            case 19: {
                return _SessionDisp.___ice_id((Ice.Object)this, (Incoming)in, (Current)__current);
            }
            case 20: {
                return _SessionDisp.___ice_ids((Ice.Object)this, (Incoming)in, (Current)__current);
            }
            case 21: {
                return _SessionDisp.___ice_isA((Ice.Object)this, (Incoming)in, (Current)__current);
            }
            case 22: {
                return _SessionDisp.___ice_ping((Ice.Object)this, (Incoming)in, (Current)__current);
            }
            case 23: {
                return _SessionDisp.___notifyUserJoinedChatRoomOneWay(this, in, __current);
            }
            case 24: {
                return _SessionDisp.___notifyUserJoinedGroupChat(this, in, __current);
            }
            case 25: {
                return _SessionDisp.___notifyUserLeftChatRoomOneWay(this, in, __current);
            }
            case 26: {
                return _SessionDisp.___notifyUserLeftGroupChat(this, in, __current);
            }
            case 27: {
                return _SessionDisp.___photoUploaded(this, in, __current);
            }
            case 28: {
                return _SessionDisp.___privateChattedWith(this, in, __current);
            }
            case 29: {
                return _SessionDisp.___profileEdited(this, in, __current);
            }
            case 30: {
                return _SessionDisp.___putAlertMessage(this, in, __current);
            }
            case 31: {
                return _SessionDisp.___putAlertMessageOneWay(this, in, __current);
            }
            case 32: {
                return _SessionDisp.___putMessage(this, in, __current);
            }
            case 33: {
                return _SessionDisp.___putMessageOneWay(this, in, __current);
            }
            case 34: {
                return _SessionDisp.___putSerializedPacket(this, in, __current);
            }
            case 35: {
                return _SessionDisp.___putSerializedPacketOneWay(this, in, __current);
            }
            case 36: {
                return _SessionDisp.___sendGroupChatParticipantArrays(this, in, __current);
            }
            case 37: {
                return _SessionDisp.___sendGroupChatParticipants(this, in, __current);
            }
            case 38: {
                return _SessionDisp.___sendMessage(this, in, __current);
            }
            case 39: {
                return _SessionDisp.___sendMessageBackToUserAsEmote(this, in, __current);
            }
            case 40: {
                return _SessionDisp.___setChatListVersion(this, in, __current);
            }
            case 41: {
                return _SessionDisp.___setCurrentChatListGroupChatSubset(this, in, __current);
            }
            case 42: {
                return _SessionDisp.___setLanguage(this, in, __current);
            }
            case 43: {
                return _SessionDisp.___setPresence(this, in, __current);
            }
            case 44: {
                return _SessionDisp.___silentlyDropIncomingPackets(this, in, __current);
            }
            case 45: {
                return _SessionDisp.___statusMessageSet(this, in, __current);
            }
            case 46: {
                return _SessionDisp.___themeUpdated(this, in, __current);
            }
            case 47: {
                return _SessionDisp.___touch(this, in, __current);
            }
        }
        assert (false);
        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
    }

    public void __write(BasicStream __os) {
        __os.writeTypeId(_SessionDisp.ice_staticId());
        __os.startWriteSlice();
        __os.endWriteSlice();
        super.__write(__os);
    }

    public void __read(BasicStream __is, boolean __rid) {
        if (__rid) {
            __is.readTypeId();
        }
        __is.startReadSlice();
        __is.endReadSlice();
        super.__read(__is, true);
    }

    public void __write(OutputStream __outS) {
        MarshalException ex = new MarshalException();
        ex.reason = "type com::projectgoth::fusion::slice::Session was not generated with stream support";
        throw ex;
    }

    public void __read(InputStream __inS, boolean __rid) {
        MarshalException ex = new MarshalException();
        ex.reason = "type com::projectgoth::fusion::slice::Session was not generated with stream support";
        throw ex;
    }
}

