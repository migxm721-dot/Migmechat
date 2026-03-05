/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.BooleanHolder
 *  Ice.Current
 *  Ice.DispatchStatus
 *  Ice.Object
 *  Ice.OperationMode
 *  Ice.OperationNotExistException
 *  Ice.SystemException
 *  Ice.UserException
 *  Ice._ObjectDelD
 *  IceInternal.Direct
 *  IceInternal.LocalExceptionWrapper
 */
package com.projectgoth.fusion.slice;

import Ice.BooleanHolder;
import Ice.Current;
import Ice.DispatchStatus;
import Ice.OperationMode;
import Ice.OperationNotExistException;
import Ice.SystemException;
import Ice.UserException;
import Ice._ObjectDelD;
import IceInternal.Direct;
import IceInternal.LocalExceptionWrapper;
import com.projectgoth.fusion.slice.ChatDefinitionIce;
import com.projectgoth.fusion.slice.ChatDefinitionIceArrayHolder;
import com.projectgoth.fusion.slice.ConnectionPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GroupChatPrx;
import com.projectgoth.fusion.slice.GroupChatPrxHolder;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.MessageSwitchboard;
import com.projectgoth.fusion.slice.RegistryPrx;
import com.projectgoth.fusion.slice.SessionPrx;
import com.projectgoth.fusion.slice.UserDataIce;
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.slice._MessageSwitchboardDel;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class _MessageSwitchboardDelD
extends _ObjectDelD
implements _MessageSwitchboardDel {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public GroupChatPrx ensureGroupChatExists(final SessionPrx currentSession, final String groupChatID, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "ensureGroupChatExists", OperationMode.Normal, __ctx);
        final GroupChatPrxHolder __result = new GroupChatPrxHolder();
        Direct __direct = null;
        try {
            GroupChatPrx groupChatPrx;
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    MessageSwitchboard __servant = null;
                    try {
                        __servant = (MessageSwitchboard)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __result.value = __servant.ensureGroupChatExists(currentSession, groupChatID, __current);
                        return DispatchStatus.DispatchOK;
                    }
                    catch (UserException __ex) {
                        this.setUserException(__ex);
                        return DispatchStatus.DispatchUserException;
                    }
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                groupChatPrx = __result.value;
                Object var10_12 = null;
            }
            catch (Throwable throwable) {
                Object var10_13 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return groupChatPrx;
        }
        catch (FusionException __ex) {
            throw __ex;
        }
        catch (SystemException __ex) {
            throw __ex;
        }
        catch (Throwable __ex) {
            LocalExceptionWrapper.throwWrapper((Throwable)__ex);
            return __result.value;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void getAndPushMessages(final String username, final byte chatType, final String suppliedChatID, final long oldestMessageTimestamp, final long newestMessageTimestamp, final int limit, final ConnectionPrx cxn, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "getAndPushMessages", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    MessageSwitchboard __servant = null;
                    try {
                        __servant = (MessageSwitchboard)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.getAndPushMessages(username, chatType, suppliedChatID, oldestMessageTimestamp, newestMessageTimestamp, limit, cxn, __current);
                        return DispatchStatus.DispatchOK;
                    }
                    catch (UserException __ex) {
                        this.setUserException(__ex);
                        return DispatchStatus.DispatchUserException;
                    }
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                Object var15_15 = null;
            }
            catch (Throwable throwable) {
                Object var15_16 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            {
            }
        }
        catch (FusionException __ex) {
            throw __ex;
        }
        catch (SystemException __ex) {
            throw __ex;
        }
        catch (Throwable __ex) {
            LocalExceptionWrapper.throwWrapper((Throwable)__ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void getAndPushMessages2(final String username, final byte chatType, final String suppliedChatID, final long oldestMessageTimestamp, final long newestMessageTimestamp, final int limit, final ConnectionPrx cxn, final int deviceType, final short clientVersion, final short fusionPktTransactionId, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "getAndPushMessages2", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    MessageSwitchboard __servant = null;
                    try {
                        __servant = (MessageSwitchboard)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.getAndPushMessages2(username, chatType, suppliedChatID, oldestMessageTimestamp, newestMessageTimestamp, limit, cxn, deviceType, clientVersion, fusionPktTransactionId, __current);
                        return DispatchStatus.DispatchOK;
                    }
                    catch (UserException __ex) {
                        this.setUserException(__ex);
                        return DispatchStatus.DispatchUserException;
                    }
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                Object var18_18 = null;
            }
            catch (Throwable throwable) {
                Object var18_19 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            {
            }
        }
        catch (FusionException __ex) {
            throw __ex;
        }
        catch (SystemException __ex) {
            throw __ex;
        }
        catch (Throwable __ex) {
            LocalExceptionWrapper.throwWrapper((Throwable)__ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public ChatDefinitionIce[] getChats(final int userID, final int chatListVersion, final int limit, final byte chatType, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "getChats", OperationMode.Normal, __ctx);
        final ChatDefinitionIceArrayHolder __result = new ChatDefinitionIceArrayHolder();
        Direct __direct = null;
        try {
            ChatDefinitionIce[] chatDefinitionIceArray;
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    MessageSwitchboard __servant = null;
                    try {
                        __servant = (MessageSwitchboard)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __result.value = __servant.getChats(userID, chatListVersion, limit, chatType, __current);
                        return DispatchStatus.DispatchOK;
                    }
                    catch (UserException __ex) {
                        this.setUserException(__ex);
                        return DispatchStatus.DispatchUserException;
                    }
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                chatDefinitionIceArray = __result.value;
                Object var12_14 = null;
            }
            catch (Throwable throwable) {
                Object var12_15 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return chatDefinitionIceArray;
        }
        catch (FusionException __ex) {
            throw __ex;
        }
        catch (SystemException __ex) {
            throw __ex;
        }
        catch (Throwable __ex) {
            LocalExceptionWrapper.throwWrapper((Throwable)__ex);
            return __result.value;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public ChatDefinitionIce[] getChats2(final int userID, final int chatListVersion, final int limit, final byte chatType, final ConnectionPrx cxn, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "getChats2", OperationMode.Normal, __ctx);
        final ChatDefinitionIceArrayHolder __result = new ChatDefinitionIceArrayHolder();
        Direct __direct = null;
        try {
            ChatDefinitionIce[] chatDefinitionIceArray;
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    MessageSwitchboard __servant = null;
                    try {
                        __servant = (MessageSwitchboard)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __result.value = __servant.getChats2(userID, chatListVersion, limit, chatType, cxn, __current);
                        return DispatchStatus.DispatchOK;
                    }
                    catch (UserException __ex) {
                        this.setUserException(__ex);
                        return DispatchStatus.DispatchUserException;
                    }
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                chatDefinitionIceArray = __result.value;
                Object var13_15 = null;
            }
            catch (Throwable throwable) {
                Object var13_16 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return chatDefinitionIceArray;
        }
        catch (FusionException __ex) {
            throw __ex;
        }
        catch (SystemException __ex) {
            throw __ex;
        }
        catch (Throwable __ex) {
            LocalExceptionWrapper.throwWrapper((Throwable)__ex);
            return __result.value;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public boolean isUserChatSyncEnabled(final ConnectionPrx cxn, final String username, final int userID, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "isUserChatSyncEnabled", OperationMode.Normal, __ctx);
        final BooleanHolder __result = new BooleanHolder();
        Direct __direct = null;
        try {
            boolean bl;
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    MessageSwitchboard __servant = null;
                    try {
                        __servant = (MessageSwitchboard)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __result.value = __servant.isUserChatSyncEnabled(cxn, username, userID, __current);
                        return DispatchStatus.DispatchOK;
                    }
                    catch (UserException __ex) {
                        this.setUserException(__ex);
                        return DispatchStatus.DispatchUserException;
                    }
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                bl = __result.value;
                Object var11_13 = null;
            }
            catch (Throwable throwable) {
                Object var11_14 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return bl;
        }
        catch (FusionException __ex) {
            throw __ex;
        }
        catch (SystemException __ex) {
            throw __ex;
        }
        catch (Throwable __ex) {
            LocalExceptionWrapper.throwWrapper((Throwable)__ex);
            return __result.value;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onCreateGroupChat(final ChatDefinitionIce storedGroupChat, final String creatorUsername, final String privateChatPartnerUsername, final GroupChatPrx groupChatRemote, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "onCreateGroupChat", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    MessageSwitchboard __servant = null;
                    try {
                        __servant = (MessageSwitchboard)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.onCreateGroupChat(storedGroupChat, creatorUsername, privateChatPartnerUsername, groupChatRemote, __current);
                        return DispatchStatus.DispatchOK;
                    }
                    catch (UserException __ex) {
                        this.setUserException(__ex);
                        return DispatchStatus.DispatchUserException;
                    }
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                Object var10_12 = null;
            }
            catch (Throwable throwable) {
                Object var10_13 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            {
            }
        }
        catch (FusionException __ex) {
            throw __ex;
        }
        catch (SystemException __ex) {
            throw __ex;
        }
        catch (Throwable __ex) {
            LocalExceptionWrapper.throwWrapper((Throwable)__ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onCreatePrivateChat(final int userID, final String username, final String otherUser, final int deviceType, final short clientVersion, final UserDataIce senderUserData, final String recipientDisplayPicture, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "onCreatePrivateChat", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    MessageSwitchboard __servant = null;
                    try {
                        __servant = (MessageSwitchboard)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.onCreatePrivateChat(userID, username, otherUser, deviceType, clientVersion, senderUserData, recipientDisplayPicture, __current);
                        return DispatchStatus.DispatchOK;
                    }
                    catch (UserException __ex) {
                        this.setUserException(__ex);
                        return DispatchStatus.DispatchUserException;
                    }
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                Object var13_15 = null;
            }
            catch (Throwable throwable) {
                Object var13_16 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            {
            }
        }
        catch (FusionException __ex) {
            throw __ex;
        }
        catch (SystemException __ex) {
            throw __ex;
        }
        catch (Throwable __ex) {
            LocalExceptionWrapper.throwWrapper((Throwable)__ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onGetChats(final ConnectionPrx cxn, final int userID, final int chatListVersion, final int limit, final byte chatType, final short transactionId, final String parentUsername, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "onGetChats", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    MessageSwitchboard __servant = null;
                    try {
                        __servant = (MessageSwitchboard)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.onGetChats(cxn, userID, chatListVersion, limit, chatType, transactionId, parentUsername, __current);
                        return DispatchStatus.DispatchOK;
                    }
                    catch (UserException __ex) {
                        this.setUserException(__ex);
                        return DispatchStatus.DispatchUserException;
                    }
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                Object var13_15 = null;
            }
            catch (Throwable throwable) {
                Object var13_16 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            {
            }
        }
        catch (FusionException __ex) {
            throw __ex;
        }
        catch (SystemException __ex) {
            throw __ex;
        }
        catch (Throwable __ex) {
            LocalExceptionWrapper.throwWrapper((Throwable)__ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onJoinChatRoom(final String username, final int userID, final String chatRoomName, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "onJoinChatRoom", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    MessageSwitchboard __servant = null;
                    try {
                        __servant = (MessageSwitchboard)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.onJoinChatRoom(username, userID, chatRoomName, __current);
                        return DispatchStatus.DispatchOK;
                    }
                    catch (UserException __ex) {
                        this.setUserException(__ex);
                        return DispatchStatus.DispatchUserException;
                    }
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                Object var9_11 = null;
            }
            catch (Throwable throwable) {
                Object var9_12 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            {
            }
        }
        catch (FusionException __ex) {
            throw __ex;
        }
        catch (SystemException __ex) {
            throw __ex;
        }
        catch (Throwable __ex) {
            LocalExceptionWrapper.throwWrapper((Throwable)__ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onJoinGroupChat(final String username, final int userID, final String groupChatGUID, final boolean debug, final UserPrx userProxy, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "onJoinGroupChat", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    MessageSwitchboard __servant = null;
                    try {
                        __servant = (MessageSwitchboard)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.onJoinGroupChat(username, userID, groupChatGUID, debug, userProxy, __current);
                        return DispatchStatus.DispatchOK;
                    }
                    catch (UserException __ex) {
                        this.setUserException(__ex);
                        return DispatchStatus.DispatchUserException;
                    }
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                Object var11_13 = null;
            }
            catch (Throwable throwable) {
                Object var11_14 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            {
            }
        }
        catch (FusionException __ex) {
            throw __ex;
        }
        catch (SystemException __ex) {
            throw __ex;
        }
        catch (Throwable __ex) {
            LocalExceptionWrapper.throwWrapper((Throwable)__ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onLeaveChatRoom(final String username, final int userID, final String chatRoomName, final UserPrx userProxy, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "onLeaveChatRoom", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    MessageSwitchboard __servant = null;
                    try {
                        __servant = (MessageSwitchboard)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.onLeaveChatRoom(username, userID, chatRoomName, userProxy, __current);
                        return DispatchStatus.DispatchOK;
                    }
                    catch (UserException __ex) {
                        this.setUserException(__ex);
                        return DispatchStatus.DispatchUserException;
                    }
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                Object var10_12 = null;
            }
            catch (Throwable throwable) {
                Object var10_13 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            {
            }
        }
        catch (FusionException __ex) {
            throw __ex;
        }
        catch (SystemException __ex) {
            throw __ex;
        }
        catch (Throwable __ex) {
            LocalExceptionWrapper.throwWrapper((Throwable)__ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onLeaveGroupChat(final String username, final int userID, final String groupChatGUID, final UserPrx userProxy, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "onLeaveGroupChat", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    MessageSwitchboard __servant = null;
                    try {
                        __servant = (MessageSwitchboard)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.onLeaveGroupChat(username, userID, groupChatGUID, userProxy, __current);
                        return DispatchStatus.DispatchOK;
                    }
                    catch (UserException __ex) {
                        this.setUserException(__ex);
                        return DispatchStatus.DispatchUserException;
                    }
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                Object var10_12 = null;
            }
            catch (Throwable throwable) {
                Object var10_13 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            {
            }
        }
        catch (FusionException __ex) {
            throw __ex;
        }
        catch (SystemException __ex) {
            throw __ex;
        }
        catch (Throwable __ex) {
            LocalExceptionWrapper.throwWrapper((Throwable)__ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onLeavePrivateChat(final int userID, final String username, final String otherUser, final int deviceType, final short clientVersion, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "onLeavePrivateChat", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    MessageSwitchboard __servant = null;
                    try {
                        __servant = (MessageSwitchboard)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.onLeavePrivateChat(userID, username, otherUser, deviceType, clientVersion, __current);
                        return DispatchStatus.DispatchOK;
                    }
                    catch (UserException __ex) {
                        this.setUserException(__ex);
                        return DispatchStatus.DispatchUserException;
                    }
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                Object var11_13 = null;
            }
            catch (Throwable throwable) {
                Object var11_14 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            {
            }
        }
        catch (FusionException __ex) {
            throw __ex;
        }
        catch (SystemException __ex) {
            throw __ex;
        }
        catch (Throwable __ex) {
            LocalExceptionWrapper.throwWrapper((Throwable)__ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onLogon(final int userID, final SessionPrx sess, final short transactionID, final String parentUsername, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "onLogon", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    MessageSwitchboard __servant = null;
                    try {
                        __servant = (MessageSwitchboard)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.onLogon(userID, sess, transactionID, parentUsername, __current);
                        return DispatchStatus.DispatchOK;
                    }
                    catch (UserException __ex) {
                        this.setUserException(__ex);
                        return DispatchStatus.DispatchUserException;
                    }
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                Object var10_12 = null;
            }
            catch (Throwable throwable) {
                Object var10_13 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            {
            }
        }
        catch (FusionException __ex) {
            throw __ex;
        }
        catch (SystemException __ex) {
            throw __ex;
        }
        catch (Throwable __ex) {
            LocalExceptionWrapper.throwWrapper((Throwable)__ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onSendFusionMessageToChatRoom(final SessionPrx currentSession, final UserPrx parentUser, final MessageDataIce messageData, final String chatRoomName, final int deviceType, final short clientVersion, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "onSendFusionMessageToChatRoom", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    MessageSwitchboard __servant = null;
                    try {
                        __servant = (MessageSwitchboard)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.onSendFusionMessageToChatRoom(currentSession, parentUser, messageData, chatRoomName, deviceType, clientVersion, __current);
                        return DispatchStatus.DispatchOK;
                    }
                    catch (UserException __ex) {
                        this.setUserException(__ex);
                        return DispatchStatus.DispatchUserException;
                    }
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                Object var12_14 = null;
            }
            catch (Throwable throwable) {
                Object var12_15 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            {
            }
        }
        catch (FusionException __ex) {
            throw __ex;
        }
        catch (SystemException __ex) {
            throw __ex;
        }
        catch (Throwable __ex) {
            LocalExceptionWrapper.throwWrapper((Throwable)__ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onSendFusionMessageToGroupChat(final SessionPrx currentSession, final UserPrx parentUser, final MessageDataIce messageData, final String groupChatID, final int deviceType, final short clientVersion, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "onSendFusionMessageToGroupChat", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    MessageSwitchboard __servant = null;
                    try {
                        __servant = (MessageSwitchboard)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.onSendFusionMessageToGroupChat(currentSession, parentUser, messageData, groupChatID, deviceType, clientVersion, __current);
                        return DispatchStatus.DispatchOK;
                    }
                    catch (UserException __ex) {
                        this.setUserException(__ex);
                        return DispatchStatus.DispatchUserException;
                    }
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                Object var12_14 = null;
            }
            catch (Throwable throwable) {
                Object var12_15 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            {
            }
        }
        catch (FusionException __ex) {
            throw __ex;
        }
        catch (SystemException __ex) {
            throw __ex;
        }
        catch (Throwable __ex) {
            LocalExceptionWrapper.throwWrapper((Throwable)__ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public boolean onSendFusionMessageToIndividual(final SessionPrx currentSession, final UserPrx parentUser, final MessageDataIce messageData, final String destinationUsername, final String[] uniqueUsersPrivateChattedWith, final int deviceType, final short clientVersion, final UserDataIce senderUserData, final String recipientDisplayPicture, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "onSendFusionMessageToIndividual", OperationMode.Normal, __ctx);
        final BooleanHolder __result = new BooleanHolder();
        Direct __direct = null;
        try {
            boolean bl;
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    MessageSwitchboard __servant = null;
                    try {
                        __servant = (MessageSwitchboard)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __result.value = __servant.onSendFusionMessageToIndividual(currentSession, parentUser, messageData, destinationUsername, uniqueUsersPrivateChattedWith, deviceType, clientVersion, senderUserData, recipientDisplayPicture, __current);
                        return DispatchStatus.DispatchOK;
                    }
                    catch (UserException __ex) {
                        this.setUserException(__ex);
                        return DispatchStatus.DispatchUserException;
                    }
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                bl = __result.value;
                Object var17_19 = null;
            }
            catch (Throwable throwable) {
                Object var17_20 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return bl;
        }
        catch (FusionException __ex) {
            throw __ex;
        }
        catch (SystemException __ex) {
            throw __ex;
        }
        catch (Throwable __ex) {
            LocalExceptionWrapper.throwWrapper((Throwable)__ex);
            return __result.value;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public boolean onSendMessageToAllUsersInChat(final SessionPrx currentSession, final UserPrx parentUser, final MessageDataIce messageData, final UserDataIce senderUserData, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "onSendMessageToAllUsersInChat", OperationMode.Normal, __ctx);
        final BooleanHolder __result = new BooleanHolder();
        Direct __direct = null;
        try {
            boolean bl;
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    MessageSwitchboard __servant = null;
                    try {
                        __servant = (MessageSwitchboard)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __result.value = __servant.onSendMessageToAllUsersInChat(currentSession, parentUser, messageData, senderUserData, __current);
                        return DispatchStatus.DispatchOK;
                    }
                    catch (UserException __ex) {
                        this.setUserException(__ex);
                        return DispatchStatus.DispatchUserException;
                    }
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                bl = __result.value;
                Object var12_14 = null;
            }
            catch (Throwable throwable) {
                Object var12_15 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return bl;
        }
        catch (FusionException __ex) {
            throw __ex;
        }
        catch (SystemException __ex) {
            throw __ex;
        }
        catch (Throwable __ex) {
            LocalExceptionWrapper.throwWrapper((Throwable)__ex);
            return __result.value;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setChatName(final String parentUsername, final String suppliedChatID, final byte chatType, final String chatName, final RegistryPrx regy, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "setChatName", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    MessageSwitchboard __servant = null;
                    try {
                        __servant = (MessageSwitchboard)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.setChatName(parentUsername, suppliedChatID, chatType, chatName, regy, __current);
                        return DispatchStatus.DispatchOK;
                    }
                    catch (UserException __ex) {
                        this.setUserException(__ex);
                        return DispatchStatus.DispatchUserException;
                    }
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                Object var11_13 = null;
            }
            catch (Throwable throwable) {
                Object var11_14 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            {
            }
        }
        catch (FusionException __ex) {
            throw __ex;
        }
        catch (SystemException __ex) {
            throw __ex;
        }
        catch (Throwable __ex) {
            LocalExceptionWrapper.throwWrapper((Throwable)__ex);
        }
    }
}

