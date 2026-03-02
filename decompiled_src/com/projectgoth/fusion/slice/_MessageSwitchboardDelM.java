/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.LocalException
 *  Ice.OperationMode
 *  Ice.UnknownUserException
 *  Ice.UserException
 *  Ice._ObjectDelM
 *  IceInternal.BasicStream
 *  IceInternal.LocalExceptionWrapper
 *  IceInternal.Outgoing
 */
package com.projectgoth.fusion.slice;

import Ice.LocalException;
import Ice.OperationMode;
import Ice.UnknownUserException;
import Ice.UserException;
import Ice._ObjectDelM;
import IceInternal.BasicStream;
import IceInternal.LocalExceptionWrapper;
import IceInternal.Outgoing;
import com.projectgoth.fusion.slice.ChatDefinitionIce;
import com.projectgoth.fusion.slice.ChatDefinitionIceArrayHelper;
import com.projectgoth.fusion.slice.ConnectionPrx;
import com.projectgoth.fusion.slice.ConnectionPrxHelper;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GroupChatPrx;
import com.projectgoth.fusion.slice.GroupChatPrxHelper;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.RegistryPrx;
import com.projectgoth.fusion.slice.RegistryPrxHelper;
import com.projectgoth.fusion.slice.SessionPrx;
import com.projectgoth.fusion.slice.SessionPrxHelper;
import com.projectgoth.fusion.slice.StringArrayHelper;
import com.projectgoth.fusion.slice.UserDataIce;
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.slice.UserPrxHelper;
import com.projectgoth.fusion.slice._MessageSwitchboardDel;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class _MessageSwitchboardDelM
extends _ObjectDelM
implements _MessageSwitchboardDel {
    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public GroupChatPrx ensureGroupChatExists(SessionPrx currentSession, String groupChatID, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        GroupChatPrx groupChatPrx;
        Outgoing __og = this.__handler.getOutgoing("ensureGroupChatExists", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                SessionPrxHelper.__write(__os, currentSession);
                __os.writeString(groupChatID);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                BasicStream __is = __og.is();
                __is.startReadEncaps();
                GroupChatPrx __ret = GroupChatPrxHelper.__read(__is);
                __is.endReadEncaps();
                groupChatPrx = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var10_14 = null;
        }
        catch (Throwable throwable) {
            Object var10_15 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return groupChatPrx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void getAndPushMessages(String username, byte chatType, String suppliedChatID, long oldestMessageTimestamp, long newestMessageTimestamp, int limit, ConnectionPrx cxn, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("getAndPushMessages", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(username);
                __os.writeByte(chatType);
                __os.writeString(suppliedChatID);
                __os.writeLong(oldestMessageTimestamp);
                __os.writeLong(newestMessageTimestamp);
                __os.writeInt(limit);
                ConnectionPrxHelper.__write(__os, cxn);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                __og.is().skipEmptyEncaps();
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var15_16 = null;
        }
        catch (Throwable throwable) {
            Object var15_17 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void getAndPushMessages2(String username, byte chatType, String suppliedChatID, long oldestMessageTimestamp, long newestMessageTimestamp, int limit, ConnectionPrx cxn, int deviceType, short clientVersion, short fusionPktTransactionId, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("getAndPushMessages2", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(username);
                __os.writeByte(chatType);
                __os.writeString(suppliedChatID);
                __os.writeLong(oldestMessageTimestamp);
                __os.writeLong(newestMessageTimestamp);
                __os.writeInt(limit);
                ConnectionPrxHelper.__write(__os, cxn);
                __os.writeInt(deviceType);
                __os.writeShort(clientVersion);
                __os.writeShort(fusionPktTransactionId);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                __og.is().skipEmptyEncaps();
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var18_19 = null;
        }
        catch (Throwable throwable) {
            Object var18_20 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public ChatDefinitionIce[] getChats(int userID, int chatListVersion, int limit, byte chatType, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        ChatDefinitionIce[] chatDefinitionIceArray;
        Outgoing __og = this.__handler.getOutgoing("getChats", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeInt(userID);
                __os.writeInt(chatListVersion);
                __os.writeInt(limit);
                __os.writeByte(chatType);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                BasicStream __is = __og.is();
                __is.startReadEncaps();
                ChatDefinitionIce[] __ret = ChatDefinitionIceArrayHelper.read(__is);
                __is.endReadEncaps();
                chatDefinitionIceArray = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var12_16 = null;
        }
        catch (Throwable throwable) {
            Object var12_17 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return chatDefinitionIceArray;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public ChatDefinitionIce[] getChats2(int userID, int chatListVersion, int limit, byte chatType, ConnectionPrx cxn, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        ChatDefinitionIce[] chatDefinitionIceArray;
        Outgoing __og = this.__handler.getOutgoing("getChats2", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeInt(userID);
                __os.writeInt(chatListVersion);
                __os.writeInt(limit);
                __os.writeByte(chatType);
                ConnectionPrxHelper.__write(__os, cxn);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                BasicStream __is = __og.is();
                __is.startReadEncaps();
                ChatDefinitionIce[] __ret = ChatDefinitionIceArrayHelper.read(__is);
                __is.endReadEncaps();
                chatDefinitionIceArray = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var13_17 = null;
        }
        catch (Throwable throwable) {
            Object var13_18 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return chatDefinitionIceArray;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public boolean isUserChatSyncEnabled(ConnectionPrx cxn, String username, int userID, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        boolean bl;
        Outgoing __og = this.__handler.getOutgoing("isUserChatSyncEnabled", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                ConnectionPrxHelper.__write(__os, cxn);
                __os.writeString(username);
                __os.writeInt(userID);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                BasicStream __is = __og.is();
                __is.startReadEncaps();
                boolean __ret = __is.readBool();
                __is.endReadEncaps();
                bl = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var11_15 = null;
        }
        catch (Throwable throwable) {
            Object var11_16 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onCreateGroupChat(ChatDefinitionIce storedGroupChat, String creatorUsername, String privateChatPartnerUsername, GroupChatPrx groupChatRemote, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("onCreateGroupChat", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                storedGroupChat.__write(__os);
                __os.writeString(creatorUsername);
                __os.writeString(privateChatPartnerUsername);
                GroupChatPrxHelper.__write(__os, groupChatRemote);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                __og.is().skipEmptyEncaps();
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var10_13 = null;
        }
        catch (Throwable throwable) {
            Object var10_14 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onCreatePrivateChat(int userID, String username, String otherUser, int deviceType, short clientVersion, UserDataIce senderUserData, String recipientDisplayPicture, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("onCreatePrivateChat", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeInt(userID);
                __os.writeString(username);
                __os.writeString(otherUser);
                __os.writeInt(deviceType);
                __os.writeShort(clientVersion);
                senderUserData.__write(__os);
                __os.writeString(recipientDisplayPicture);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                __og.is().skipEmptyEncaps();
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var13_16 = null;
        }
        catch (Throwable throwable) {
            Object var13_17 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onGetChats(ConnectionPrx cxn, int userID, int chatListVersion, int limit, byte chatType, short transactionId, String parentUsername, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("onGetChats", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                ConnectionPrxHelper.__write(__os, cxn);
                __os.writeInt(userID);
                __os.writeInt(chatListVersion);
                __os.writeInt(limit);
                __os.writeByte(chatType);
                __os.writeShort(transactionId);
                __os.writeString(parentUsername);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                __og.is().skipEmptyEncaps();
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var13_16 = null;
        }
        catch (Throwable throwable) {
            Object var13_17 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onJoinChatRoom(String username, int userID, String chatRoomName, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("onJoinChatRoom", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(username);
                __os.writeInt(userID);
                __os.writeString(chatRoomName);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                __og.is().skipEmptyEncaps();
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var9_12 = null;
        }
        catch (Throwable throwable) {
            Object var9_13 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onJoinGroupChat(String username, int userID, String groupChatGUID, boolean debug, UserPrx userProxy, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("onJoinGroupChat", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(username);
                __os.writeInt(userID);
                __os.writeString(groupChatGUID);
                __os.writeBool(debug);
                UserPrxHelper.__write(__os, userProxy);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                __og.is().skipEmptyEncaps();
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var11_14 = null;
        }
        catch (Throwable throwable) {
            Object var11_15 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onLeaveChatRoom(String username, int userID, String chatRoomName, UserPrx userProxy, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("onLeaveChatRoom", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(username);
                __os.writeInt(userID);
                __os.writeString(chatRoomName);
                UserPrxHelper.__write(__os, userProxy);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                __og.is().skipEmptyEncaps();
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var10_13 = null;
        }
        catch (Throwable throwable) {
            Object var10_14 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onLeaveGroupChat(String username, int userID, String groupChatGUID, UserPrx userProxy, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("onLeaveGroupChat", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(username);
                __os.writeInt(userID);
                __os.writeString(groupChatGUID);
                UserPrxHelper.__write(__os, userProxy);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                __og.is().skipEmptyEncaps();
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var10_13 = null;
        }
        catch (Throwable throwable) {
            Object var10_14 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onLeavePrivateChat(int userID, String username, String otherUser, int deviceType, short clientVersion, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("onLeavePrivateChat", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeInt(userID);
                __os.writeString(username);
                __os.writeString(otherUser);
                __os.writeInt(deviceType);
                __os.writeShort(clientVersion);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                __og.is().skipEmptyEncaps();
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var11_14 = null;
        }
        catch (Throwable throwable) {
            Object var11_15 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onLogon(int userID, SessionPrx sess, short transactionID, String parentUsername, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("onLogon", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeInt(userID);
                SessionPrxHelper.__write(__os, sess);
                __os.writeShort(transactionID);
                __os.writeString(parentUsername);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                __og.is().skipEmptyEncaps();
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var10_13 = null;
        }
        catch (Throwable throwable) {
            Object var10_14 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onSendFusionMessageToChatRoom(SessionPrx currentSession, UserPrx parentUser, MessageDataIce messageData, String chatRoomName, int deviceType, short clientVersion, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("onSendFusionMessageToChatRoom", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                SessionPrxHelper.__write(__os, currentSession);
                UserPrxHelper.__write(__os, parentUser);
                messageData.__write(__os);
                __os.writeString(chatRoomName);
                __os.writeInt(deviceType);
                __os.writeShort(clientVersion);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                __og.is().skipEmptyEncaps();
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var12_15 = null;
        }
        catch (Throwable throwable) {
            Object var12_16 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onSendFusionMessageToGroupChat(SessionPrx currentSession, UserPrx parentUser, MessageDataIce messageData, String groupChatID, int deviceType, short clientVersion, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("onSendFusionMessageToGroupChat", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                SessionPrxHelper.__write(__os, currentSession);
                UserPrxHelper.__write(__os, parentUser);
                messageData.__write(__os);
                __os.writeString(groupChatID);
                __os.writeInt(deviceType);
                __os.writeShort(clientVersion);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                __og.is().skipEmptyEncaps();
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var12_15 = null;
        }
        catch (Throwable throwable) {
            Object var12_16 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public boolean onSendFusionMessageToIndividual(SessionPrx currentSession, UserPrx parentUser, MessageDataIce messageData, String destinationUsername, String[] uniqueUsersPrivateChattedWith, int deviceType, short clientVersion, UserDataIce senderUserData, String recipientDisplayPicture, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        boolean bl;
        Outgoing __og = this.__handler.getOutgoing("onSendFusionMessageToIndividual", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                SessionPrxHelper.__write(__os, currentSession);
                UserPrxHelper.__write(__os, parentUser);
                messageData.__write(__os);
                __os.writeString(destinationUsername);
                StringArrayHelper.write(__os, uniqueUsersPrivateChattedWith);
                __os.writeInt(deviceType);
                __os.writeShort(clientVersion);
                senderUserData.__write(__os);
                __os.writeString(recipientDisplayPicture);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                BasicStream __is = __og.is();
                __is.startReadEncaps();
                boolean __ret = __is.readBool();
                __is.endReadEncaps();
                bl = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var17_21 = null;
        }
        catch (Throwable throwable) {
            Object var17_22 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return bl;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public boolean onSendMessageToAllUsersInChat(SessionPrx currentSession, UserPrx parentUser, MessageDataIce messageData, UserDataIce senderUserData, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        boolean bl;
        Outgoing __og = this.__handler.getOutgoing("onSendMessageToAllUsersInChat", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                SessionPrxHelper.__write(__os, currentSession);
                UserPrxHelper.__write(__os, parentUser);
                messageData.__write(__os);
                senderUserData.__write(__os);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                BasicStream __is = __og.is();
                __is.startReadEncaps();
                boolean __ret = __is.readBool();
                __is.endReadEncaps();
                bl = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var12_16 = null;
        }
        catch (Throwable throwable) {
            Object var12_17 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setChatName(String parentUsername, String suppliedChatID, byte chatType, String chatName, RegistryPrx regy, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("setChatName", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(parentUsername);
                __os.writeString(suppliedChatID);
                __os.writeByte(chatType);
                __os.writeString(chatName);
                RegistryPrxHelper.__write(__os, regy);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                __og.is().skipEmptyEncaps();
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var11_14 = null;
        }
        catch (Throwable throwable) {
            Object var11_15 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }
}

