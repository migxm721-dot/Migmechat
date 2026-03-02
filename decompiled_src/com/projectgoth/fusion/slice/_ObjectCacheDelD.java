/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.CollocationOptimizationException
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

import Ice.CollocationOptimizationException;
import Ice.Current;
import Ice.DispatchStatus;
import Ice.OperationMode;
import Ice.OperationNotExistException;
import Ice.SystemException;
import Ice.UserException;
import Ice._ObjectDelD;
import IceInternal.Direct;
import IceInternal.LocalExceptionWrapper;
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.ChatRoomPrxHolder;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GroupChatArrayHolder;
import com.projectgoth.fusion.slice.GroupChatPrx;
import com.projectgoth.fusion.slice.GroupChatPrxHolder;
import com.projectgoth.fusion.slice.MessageSwitchboardPrx;
import com.projectgoth.fusion.slice.MessageSwitchboardPrxHolder;
import com.projectgoth.fusion.slice.ObjectCache;
import com.projectgoth.fusion.slice.ObjectExistsException;
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.slice.UserPrxHolder;
import com.projectgoth.fusion.slice._ObjectCacheDel;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class _ObjectCacheDelD
extends _ObjectDelD
implements _ObjectCacheDel {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public ChatRoomPrx createChatRoomObject(final String name, Map<String, String> __ctx) throws LocalExceptionWrapper, ObjectExistsException, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "createChatRoomObject", OperationMode.Normal, __ctx);
        final ChatRoomPrxHolder __result = new ChatRoomPrxHolder();
        Direct __direct = null;
        try {
            ChatRoomPrx chatRoomPrx;
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    ObjectCache __servant = null;
                    try {
                        __servant = (ObjectCache)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __result.value = __servant.createChatRoomObject(name, __current);
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
                chatRoomPrx = __result.value;
                Object var9_12 = null;
            }
            catch (Throwable throwable) {
                Object var9_13 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return chatRoomPrx;
        }
        catch (ObjectExistsException __ex) {
            throw __ex;
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
    public GroupChatPrx createGroupChatObject(final String id, final String creator, final String privateChatPartner, final String[] otherPartyList, Map<String, String> __ctx) throws LocalExceptionWrapper, ObjectExistsException, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "createGroupChatObject", OperationMode.Normal, __ctx);
        final GroupChatPrxHolder __result = new GroupChatPrxHolder();
        Direct __direct = null;
        try {
            GroupChatPrx groupChatPrx;
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    ObjectCache __servant = null;
                    try {
                        __servant = (ObjectCache)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __result.value = __servant.createGroupChatObject(id, creator, privateChatPartner, otherPartyList, __current);
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
                Object var12_15 = null;
            }
            catch (Throwable throwable) {
                Object var12_16 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return groupChatPrx;
        }
        catch (ObjectExistsException __ex) {
            throw __ex;
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

    @Override
    public UserPrx createUserObject(String username, Map<String, String> __ctx) throws LocalExceptionWrapper, ObjectExistsException, FusionException {
        throw new CollocationOptimizationException();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public UserPrx createUserObjectNonAsync(final String username, Map<String, String> __ctx) throws LocalExceptionWrapper, ObjectExistsException, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "createUserObjectNonAsync", OperationMode.Normal, __ctx);
        final UserPrxHolder __result = new UserPrxHolder();
        Direct __direct = null;
        try {
            UserPrx userPrx;
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    ObjectCache __servant = null;
                    try {
                        __servant = (ObjectCache)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __result.value = __servant.createUserObjectNonAsync(username, __current);
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
                userPrx = __result.value;
                Object var9_12 = null;
            }
            catch (Throwable throwable) {
                Object var9_13 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return userPrx;
        }
        catch (ObjectExistsException __ex) {
            throw __ex;
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
    public GroupChatPrx[] getAllGroupChats(Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "getAllGroupChats", OperationMode.Normal, __ctx);
        final GroupChatArrayHolder __result = new GroupChatArrayHolder();
        Direct __direct = null;
        try {
            GroupChatPrx[] groupChatPrxArray;
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    ObjectCache __servant = null;
                    try {
                        __servant = (ObjectCache)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __result.value = __servant.getAllGroupChats(__current);
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
                groupChatPrxArray = __result.value;
                Object var8_10 = null;
            }
            catch (Throwable throwable) {
                Object var8_11 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return groupChatPrxArray;
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
    public MessageSwitchboardPrx getMessageSwitchboard(Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "getMessageSwitchboard", OperationMode.Normal, __ctx);
        final MessageSwitchboardPrxHolder __result = new MessageSwitchboardPrxHolder();
        Direct __direct = null;
        try {
            MessageSwitchboardPrx messageSwitchboardPrx;
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    ObjectCache __servant = null;
                    try {
                        __servant = (ObjectCache)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __result.value = __servant.getMessageSwitchboard(__current);
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
                messageSwitchboardPrx = __result.value;
                Object var8_10 = null;
            }
            catch (Throwable throwable) {
                Object var8_11 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return messageSwitchboardPrx;
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
    public void purgeGroupChatObject(final String id, Map<String, String> __ctx) throws LocalExceptionWrapper {
        final Current __current = new Current();
        this.__initCurrent(__current, "purgeGroupChatObject", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    ObjectCache __servant = null;
                    try {
                        __servant = (ObjectCache)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __servant.purgeGroupChatObject(id, __current);
                    return DispatchStatus.DispatchOK;
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                Object var7_8 = null;
            }
            catch (Throwable throwable) {
                Object var7_9 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            {
            }
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
    public void purgeUserObject(final String username, Map<String, String> __ctx) throws LocalExceptionWrapper {
        final Current __current = new Current();
        this.__initCurrent(__current, "purgeUserObject", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    ObjectCache __servant = null;
                    try {
                        __servant = (ObjectCache)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __servant.purgeUserObject(username, __current);
                    return DispatchStatus.DispatchOK;
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                Object var7_8 = null;
            }
            catch (Throwable throwable) {
                Object var7_9 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            {
            }
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
    public void sendAlertMessageToAllUsers(final String message, final String title, final short timeout, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "sendAlertMessageToAllUsers", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    ObjectCache __servant = null;
                    try {
                        __servant = (ObjectCache)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.sendAlertMessageToAllUsers(message, title, timeout, __current);
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
}

