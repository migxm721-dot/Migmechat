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
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.ChatRoomPrxHelper;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GroupChatArrayHelper;
import com.projectgoth.fusion.slice.GroupChatPrx;
import com.projectgoth.fusion.slice.GroupChatPrxHelper;
import com.projectgoth.fusion.slice.MessageSwitchboardPrx;
import com.projectgoth.fusion.slice.MessageSwitchboardPrxHelper;
import com.projectgoth.fusion.slice.ObjectExistsException;
import com.projectgoth.fusion.slice.StringArrayHelper;
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.slice.UserPrxHelper;
import com.projectgoth.fusion.slice._ObjectCacheDel;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class _ObjectCacheDelM
extends _ObjectDelM
implements _ObjectCacheDel {
    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public ChatRoomPrx createChatRoomObject(String name, Map<String, String> __ctx) throws LocalExceptionWrapper, ObjectExistsException, FusionException {
        ChatRoomPrx chatRoomPrx;
        Outgoing __og = this.__handler.getOutgoing("createChatRoomObject", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(name);
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
                    catch (ObjectExistsException __ex) {
                        throw __ex;
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
                ChatRoomPrx __ret = ChatRoomPrxHelper.__read(__is);
                __is.endReadEncaps();
                chatRoomPrx = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var9_14 = null;
        }
        catch (Throwable throwable) {
            Object var9_15 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return chatRoomPrx;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public GroupChatPrx createGroupChatObject(String id, String creator, String privateChatPartner, String[] otherPartyList, Map<String, String> __ctx) throws LocalExceptionWrapper, ObjectExistsException, FusionException {
        GroupChatPrx groupChatPrx;
        Outgoing __og = this.__handler.getOutgoing("createGroupChatObject", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(id);
                __os.writeString(creator);
                __os.writeString(privateChatPartner);
                StringArrayHelper.write(__os, otherPartyList);
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
                    catch (ObjectExistsException __ex) {
                        throw __ex;
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
            Object var12_17 = null;
        }
        catch (Throwable throwable) {
            Object var12_18 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return groupChatPrx;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public UserPrx createUserObject(String username, Map<String, String> __ctx) throws LocalExceptionWrapper, ObjectExistsException, FusionException {
        UserPrx userPrx;
        Outgoing __og = this.__handler.getOutgoing("createUserObject", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(username);
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
                    catch (ObjectExistsException __ex) {
                        throw __ex;
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
                UserPrx __ret = UserPrxHelper.__read(__is);
                __is.endReadEncaps();
                userPrx = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var9_14 = null;
        }
        catch (Throwable throwable) {
            Object var9_15 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return userPrx;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public UserPrx createUserObjectNonAsync(String username, Map<String, String> __ctx) throws LocalExceptionWrapper, ObjectExistsException, FusionException {
        UserPrx userPrx;
        Outgoing __og = this.__handler.getOutgoing("createUserObjectNonAsync", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(username);
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
                    catch (ObjectExistsException __ex) {
                        throw __ex;
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
                UserPrx __ret = UserPrxHelper.__read(__is);
                __is.endReadEncaps();
                userPrx = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var9_14 = null;
        }
        catch (Throwable throwable) {
            Object var9_15 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return userPrx;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public GroupChatPrx[] getAllGroupChats(Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        GroupChatPrx[] groupChatPrxArray;
        Outgoing __og = this.__handler.getOutgoing("getAllGroupChats", OperationMode.Normal, __ctx);
        try {
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
                GroupChatPrx[] __ret = GroupChatArrayHelper.read(__is);
                __is.endReadEncaps();
                groupChatPrxArray = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var8_10 = null;
        }
        catch (Throwable throwable) {
            Object var8_11 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return groupChatPrxArray;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public MessageSwitchboardPrx getMessageSwitchboard(Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        MessageSwitchboardPrx messageSwitchboardPrx;
        Outgoing __og = this.__handler.getOutgoing("getMessageSwitchboard", OperationMode.Normal, __ctx);
        try {
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
                MessageSwitchboardPrx __ret = MessageSwitchboardPrxHelper.__read(__is);
                __is.endReadEncaps();
                messageSwitchboardPrx = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var8_10 = null;
        }
        catch (Throwable throwable) {
            Object var8_11 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return messageSwitchboardPrx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void purgeGroupChatObject(String id, Map<String, String> __ctx) throws LocalExceptionWrapper {
        Outgoing __og = this.__handler.getOutgoing("purgeGroupChatObject", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(id);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            if (!__og.is().isEmpty()) {
                try {
                    if (!__ok) {
                        try {
                            __og.throwUserException();
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
            }
            Object var7_9 = null;
        }
        catch (Throwable throwable) {
            Object var7_10 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void purgeUserObject(String username, Map<String, String> __ctx) throws LocalExceptionWrapper {
        Outgoing __og = this.__handler.getOutgoing("purgeUserObject", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(username);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            if (!__og.is().isEmpty()) {
                try {
                    if (!__ok) {
                        try {
                            __og.throwUserException();
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
            }
            Object var7_9 = null;
        }
        catch (Throwable throwable) {
            Object var7_10 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void sendAlertMessageToAllUsers(String message, String title, short timeout, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("sendAlertMessageToAllUsers", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(message);
                __os.writeString(title);
                __os.writeShort(timeout);
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
}

