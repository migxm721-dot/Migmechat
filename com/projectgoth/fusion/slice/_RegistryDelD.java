/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.CollocationOptimizationException
 *  Ice.Current
 *  Ice.DispatchStatus
 *  Ice.IntHolder
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
import Ice.IntHolder;
import Ice.OperationMode;
import Ice.OperationNotExistException;
import Ice.SystemException;
import Ice.UserException;
import Ice._ObjectDelD;
import IceInternal.Direct;
import IceInternal.LocalExceptionWrapper;
import com.projectgoth.fusion.slice.BotServiceAdminPrx;
import com.projectgoth.fusion.slice.BotServicePrx;
import com.projectgoth.fusion.slice.BotServicePrxHolder;
import com.projectgoth.fusion.slice.ChatRoomProxyArrayHolder;
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.ChatRoomPrxHolder;
import com.projectgoth.fusion.slice.ConnectionPrx;
import com.projectgoth.fusion.slice.ConnectionPrxHolder;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GroupChatPrx;
import com.projectgoth.fusion.slice.GroupChatPrxHolder;
import com.projectgoth.fusion.slice.MessageSwitchboardAdminPrx;
import com.projectgoth.fusion.slice.MessageSwitchboardPrx;
import com.projectgoth.fusion.slice.ObjectCacheAdminPrx;
import com.projectgoth.fusion.slice.ObjectCachePrx;
import com.projectgoth.fusion.slice.ObjectCachePrxHolder;
import com.projectgoth.fusion.slice.ObjectCacheStats;
import com.projectgoth.fusion.slice.ObjectExistsException;
import com.projectgoth.fusion.slice.ObjectNotFoundException;
import com.projectgoth.fusion.slice.Registry;
import com.projectgoth.fusion.slice.UserProxyArrayHolder;
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.slice.UserPrxHolder;
import com.projectgoth.fusion.slice.UsernameToProxyMapHolder;
import com.projectgoth.fusion.slice._RegistryDel;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class _RegistryDelD
extends _ObjectDelD
implements _RegistryDel {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void deregisterBotService(final String hostName, Map<String, String> __ctx) throws LocalExceptionWrapper {
        final Current __current = new Current();
        this.__initCurrent(__current, "deregisterBotService", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    Registry __servant = null;
                    try {
                        __servant = (Registry)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __servant.deregisterBotService(hostName, __current);
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
    public void deregisterChatRoomObject(final String name, Map<String, String> __ctx) throws LocalExceptionWrapper {
        final Current __current = new Current();
        this.__initCurrent(__current, "deregisterChatRoomObject", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    Registry __servant = null;
                    try {
                        __servant = (Registry)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __servant.deregisterChatRoomObject(name, __current);
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
    public void deregisterConnectionObject(final String sessionID, Map<String, String> __ctx) throws LocalExceptionWrapper {
        final Current __current = new Current();
        this.__initCurrent(__current, "deregisterConnectionObject", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    Registry __servant = null;
                    try {
                        __servant = (Registry)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __servant.deregisterConnectionObject(sessionID, __current);
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
    public void deregisterGroupChatObject(final String id, Map<String, String> __ctx) throws LocalExceptionWrapper {
        final Current __current = new Current();
        this.__initCurrent(__current, "deregisterGroupChatObject", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    Registry __servant = null;
                    try {
                        __servant = (Registry)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __servant.deregisterGroupChatObject(id, __current);
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
    public void deregisterMessageSwitchboard(final String hostName, Map<String, String> __ctx) throws LocalExceptionWrapper {
        final Current __current = new Current();
        this.__initCurrent(__current, "deregisterMessageSwitchboard", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    Registry __servant = null;
                    try {
                        __servant = (Registry)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __servant.deregisterMessageSwitchboard(hostName, __current);
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
    public void deregisterObjectCache(final String hostName, Map<String, String> __ctx) throws LocalExceptionWrapper {
        final Current __current = new Current();
        this.__initCurrent(__current, "deregisterObjectCache", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    Registry __servant = null;
                    try {
                        __servant = (Registry)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __servant.deregisterObjectCache(hostName, __current);
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
    public void deregisterUserObject(final String username, final String objectCacheHostname, Map<String, String> __ctx) throws LocalExceptionWrapper {
        final Current __current = new Current();
        this.__initCurrent(__current, "deregisterUserObject", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    Registry __servant = null;
                    try {
                        __servant = (Registry)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __servant.deregisterUserObject(username, objectCacheHostname, __current);
                    return DispatchStatus.DispatchOK;
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                Object var8_9 = null;
            }
            catch (Throwable throwable) {
                Object var8_10 = null;
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
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public ChatRoomPrx findChatRoomObject(final String name, Map<String, String> __ctx) throws LocalExceptionWrapper, ObjectNotFoundException {
        final Current __current = new Current();
        this.__initCurrent(__current, "findChatRoomObject", OperationMode.Normal, __ctx);
        final ChatRoomPrxHolder __result = new ChatRoomPrxHolder();
        Direct __direct = null;
        try {
            ChatRoomPrx chatRoomPrx;
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    Registry __servant = null;
                    try {
                        __servant = (Registry)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __result.value = __servant.findChatRoomObject(name, __current);
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
                Object var9_11 = null;
            }
            catch (Throwable throwable) {
                Object var9_12 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return chatRoomPrx;
        }
        catch (ObjectNotFoundException __ex) {
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
    public ChatRoomPrx[] findChatRoomObjects(final String[] chatRoomNames, Map<String, String> __ctx) throws LocalExceptionWrapper {
        final Current __current = new Current();
        this.__initCurrent(__current, "findChatRoomObjects", OperationMode.Normal, __ctx);
        final ChatRoomProxyArrayHolder __result = new ChatRoomProxyArrayHolder();
        Direct __direct = null;
        try {
            ChatRoomPrx[] chatRoomPrxArray;
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    Registry __servant = null;
                    try {
                        __servant = (Registry)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __result.value = __servant.findChatRoomObjects(chatRoomNames, __current);
                    return DispatchStatus.DispatchOK;
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                chatRoomPrxArray = __result.value;
                Object var9_10 = null;
            }
            catch (Throwable throwable) {
                Object var9_11 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return chatRoomPrxArray;
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
    public ConnectionPrx findConnectionObject(final String sessionID, Map<String, String> __ctx) throws LocalExceptionWrapper, ObjectNotFoundException {
        final Current __current = new Current();
        this.__initCurrent(__current, "findConnectionObject", OperationMode.Normal, __ctx);
        final ConnectionPrxHolder __result = new ConnectionPrxHolder();
        Direct __direct = null;
        try {
            ConnectionPrx connectionPrx;
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    Registry __servant = null;
                    try {
                        __servant = (Registry)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __result.value = __servant.findConnectionObject(sessionID, __current);
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
                connectionPrx = __result.value;
                Object var9_11 = null;
            }
            catch (Throwable throwable) {
                Object var9_12 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return connectionPrx;
        }
        catch (ObjectNotFoundException __ex) {
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
    public GroupChatPrx findGroupChatObject(final String id, Map<String, String> __ctx) throws LocalExceptionWrapper, ObjectNotFoundException {
        final Current __current = new Current();
        this.__initCurrent(__current, "findGroupChatObject", OperationMode.Normal, __ctx);
        final GroupChatPrxHolder __result = new GroupChatPrxHolder();
        Direct __direct = null;
        try {
            GroupChatPrx groupChatPrx;
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    Registry __servant = null;
                    try {
                        __servant = (Registry)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __result.value = __servant.findGroupChatObject(id, __current);
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
                Object var9_11 = null;
            }
            catch (Throwable throwable) {
                Object var9_12 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return groupChatPrx;
        }
        catch (ObjectNotFoundException __ex) {
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
    public UserPrx findUserObject(final String username, Map<String, String> __ctx) throws LocalExceptionWrapper, ObjectNotFoundException {
        final Current __current = new Current();
        this.__initCurrent(__current, "findUserObject", OperationMode.Normal, __ctx);
        final UserPrxHolder __result = new UserPrxHolder();
        Direct __direct = null;
        try {
            UserPrx userPrx;
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    Registry __servant = null;
                    try {
                        __servant = (Registry)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __result.value = __servant.findUserObject(username, __current);
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
                Object var9_11 = null;
            }
            catch (Throwable throwable) {
                Object var9_12 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return userPrx;
        }
        catch (ObjectNotFoundException __ex) {
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
    public UserPrx[] findUserObjects(final String[] usernames, Map<String, String> __ctx) throws LocalExceptionWrapper {
        final Current __current = new Current();
        this.__initCurrent(__current, "findUserObjects", OperationMode.Normal, __ctx);
        final UserProxyArrayHolder __result = new UserProxyArrayHolder();
        Direct __direct = null;
        try {
            UserPrx[] userPrxArray;
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    Registry __servant = null;
                    try {
                        __servant = (Registry)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __result.value = __servant.findUserObjects(usernames, __current);
                    return DispatchStatus.DispatchOK;
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                userPrxArray = __result.value;
                Object var9_10 = null;
            }
            catch (Throwable throwable) {
                Object var9_11 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return userPrxArray;
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
    public Map<String, UserPrx> findUserObjectsMap(final String[] usernames, Map<String, String> __ctx) throws LocalExceptionWrapper {
        final Current __current = new Current();
        this.__initCurrent(__current, "findUserObjectsMap", OperationMode.Normal, __ctx);
        final UsernameToProxyMapHolder __result = new UsernameToProxyMapHolder();
        Direct __direct = null;
        try {
            Map<String, UserPrx> map;
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    Registry __servant = null;
                    try {
                        __servant = (Registry)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __result.value = __servant.findUserObjectsMap(usernames, __current);
                    return DispatchStatus.DispatchOK;
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                map = __result.value;
                Object var9_10 = null;
            }
            catch (Throwable throwable) {
                Object var9_11 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return map;
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
    public BotServicePrx getLowestLoadedBotService(Map<String, String> __ctx) throws LocalExceptionWrapper, ObjectNotFoundException {
        final Current __current = new Current();
        this.__initCurrent(__current, "getLowestLoadedBotService", OperationMode.Normal, __ctx);
        final BotServicePrxHolder __result = new BotServicePrxHolder();
        Direct __direct = null;
        try {
            BotServicePrx botServicePrx;
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    Registry __servant = null;
                    try {
                        __servant = (Registry)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __result.value = __servant.getLowestLoadedBotService(__current);
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
                botServicePrx = __result.value;
                Object var8_10 = null;
            }
            catch (Throwable throwable) {
                Object var8_11 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return botServicePrx;
        }
        catch (ObjectNotFoundException __ex) {
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
    public ObjectCachePrx getLowestLoadedObjectCache(Map<String, String> __ctx) throws LocalExceptionWrapper, ObjectNotFoundException {
        final Current __current = new Current();
        this.__initCurrent(__current, "getLowestLoadedObjectCache", OperationMode.Normal, __ctx);
        final ObjectCachePrxHolder __result = new ObjectCachePrxHolder();
        Direct __direct = null;
        try {
            ObjectCachePrx objectCachePrx;
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    Registry __servant = null;
                    try {
                        __servant = (Registry)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __result.value = __servant.getLowestLoadedObjectCache(__current);
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
                objectCachePrx = __result.value;
                Object var8_10 = null;
            }
            catch (Throwable throwable) {
                Object var8_11 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return objectCachePrx;
        }
        catch (ObjectNotFoundException __ex) {
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
    public MessageSwitchboardPrx getMessageSwitchboard(Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        throw new CollocationOptimizationException();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public int getUserCount(Map<String, String> __ctx) throws LocalExceptionWrapper {
        final Current __current = new Current();
        this.__initCurrent(__current, "getUserCount", OperationMode.Normal, __ctx);
        final IntHolder __result = new IntHolder();
        Direct __direct = null;
        try {
            int n;
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    Registry __servant = null;
                    try {
                        __servant = (Registry)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __result.value = __servant.getUserCount(__current);
                    return DispatchStatus.DispatchOK;
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                n = __result.value;
                Object var8_9 = null;
            }
            catch (Throwable throwable) {
                Object var8_10 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return n;
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
    public int newGatewayID(Map<String, String> __ctx) throws LocalExceptionWrapper {
        final Current __current = new Current();
        this.__initCurrent(__current, "newGatewayID", OperationMode.Normal, __ctx);
        final IntHolder __result = new IntHolder();
        Direct __direct = null;
        try {
            int n;
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    Registry __servant = null;
                    try {
                        __servant = (Registry)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __result.value = __servant.newGatewayID(__current);
                    return DispatchStatus.DispatchOK;
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                n = __result.value;
                Object var8_9 = null;
            }
            catch (Throwable throwable) {
                Object var8_10 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return n;
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
    public void registerBotService(final String hostName, final int load, final BotServicePrx serviceProxy, final BotServiceAdminPrx adminProxy, Map<String, String> __ctx) throws LocalExceptionWrapper {
        final Current __current = new Current();
        this.__initCurrent(__current, "registerBotService", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    Registry __servant = null;
                    try {
                        __servant = (Registry)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __servant.registerBotService(hostName, load, serviceProxy, adminProxy, __current);
                    return DispatchStatus.DispatchOK;
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                Object var10_11 = null;
            }
            catch (Throwable throwable) {
                Object var10_12 = null;
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
    public void registerChatRoomObject(final String name, final ChatRoomPrx chatRoomProxy, Map<String, String> __ctx) throws LocalExceptionWrapper, ObjectExistsException {
        final Current __current = new Current();
        this.__initCurrent(__current, "registerChatRoomObject", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    Registry __servant = null;
                    try {
                        __servant = (Registry)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.registerChatRoomObject(name, chatRoomProxy, __current);
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
                Object var8_10 = null;
            }
            catch (Throwable throwable) {
                Object var8_11 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            {
            }
        }
        catch (ObjectExistsException __ex) {
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
    public void registerConnectionObject(final String sessionID, final ConnectionPrx connectionProxy, Map<String, String> __ctx) throws LocalExceptionWrapper, ObjectExistsException {
        final Current __current = new Current();
        this.__initCurrent(__current, "registerConnectionObject", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    Registry __servant = null;
                    try {
                        __servant = (Registry)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.registerConnectionObject(sessionID, connectionProxy, __current);
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
                Object var8_10 = null;
            }
            catch (Throwable throwable) {
                Object var8_11 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            {
            }
        }
        catch (ObjectExistsException __ex) {
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
    public void registerGroupChatObject(final String id, final GroupChatPrx groupChatProxy, Map<String, String> __ctx) throws LocalExceptionWrapper {
        final Current __current = new Current();
        this.__initCurrent(__current, "registerGroupChatObject", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    Registry __servant = null;
                    try {
                        __servant = (Registry)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __servant.registerGroupChatObject(id, groupChatProxy, __current);
                    return DispatchStatus.DispatchOK;
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                Object var8_9 = null;
            }
            catch (Throwable throwable) {
                Object var8_10 = null;
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
    public void registerMessageSwitchboard(final String hostName, final MessageSwitchboardPrx msbProxy, final MessageSwitchboardAdminPrx adminProxy, Map<String, String> __ctx) throws LocalExceptionWrapper {
        final Current __current = new Current();
        this.__initCurrent(__current, "registerMessageSwitchboard", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    Registry __servant = null;
                    try {
                        __servant = (Registry)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __servant.registerMessageSwitchboard(hostName, msbProxy, adminProxy, __current);
                    return DispatchStatus.DispatchOK;
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                Object var9_10 = null;
            }
            catch (Throwable throwable) {
                Object var9_11 = null;
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
    public void registerObjectCache(final String hostName, final ObjectCachePrx cacheProxy, final ObjectCacheAdminPrx adminProxy, Map<String, String> __ctx) throws LocalExceptionWrapper {
        final Current __current = new Current();
        this.__initCurrent(__current, "registerObjectCache", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    Registry __servant = null;
                    try {
                        __servant = (Registry)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __servant.registerObjectCache(hostName, cacheProxy, adminProxy, __current);
                    return DispatchStatus.DispatchOK;
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                Object var9_10 = null;
            }
            catch (Throwable throwable) {
                Object var9_11 = null;
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
    public void registerObjectCacheStats(final String objectCacheHostName, final ObjectCacheStats stats, Map<String, String> __ctx) throws LocalExceptionWrapper, ObjectNotFoundException {
        final Current __current = new Current();
        this.__initCurrent(__current, "registerObjectCacheStats", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    Registry __servant = null;
                    try {
                        __servant = (Registry)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.registerObjectCacheStats(objectCacheHostName, stats, __current);
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
                Object var8_10 = null;
            }
            catch (Throwable throwable) {
                Object var8_11 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            {
            }
        }
        catch (ObjectNotFoundException __ex) {
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
    public void registerUserObject(final String username, final UserPrx userProxy, final String objectCacheHostname, Map<String, String> __ctx) throws LocalExceptionWrapper, ObjectExistsException {
        final Current __current = new Current();
        this.__initCurrent(__current, "registerUserObject", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    Registry __servant = null;
                    try {
                        __servant = (Registry)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.registerUserObject(username, userProxy, objectCacheHostname, __current);
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
        catch (ObjectExistsException __ex) {
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
    public void sendAlertMessageToAllUsers(final String message, final String title, final short timeout, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "sendAlertMessageToAllUsers", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    Registry __servant = null;
                    try {
                        __servant = (Registry)__obj;
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

