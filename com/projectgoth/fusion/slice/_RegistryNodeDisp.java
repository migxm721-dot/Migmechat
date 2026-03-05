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
 *  IceInternal.Patcher
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
import IceInternal.Patcher;
import com.projectgoth.fusion.slice.BotServiceAdminPrx;
import com.projectgoth.fusion.slice.BotServiceAdminPrxHelper;
import com.projectgoth.fusion.slice.BotServicePrx;
import com.projectgoth.fusion.slice.BotServicePrxHelper;
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.ChatRoomPrxHelper;
import com.projectgoth.fusion.slice.ConnectionPrx;
import com.projectgoth.fusion.slice.ConnectionPrxHelper;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GroupChatPrx;
import com.projectgoth.fusion.slice.GroupChatPrxHelper;
import com.projectgoth.fusion.slice.MessageSwitchboardAdminPrx;
import com.projectgoth.fusion.slice.MessageSwitchboardAdminPrxHelper;
import com.projectgoth.fusion.slice.MessageSwitchboardPrx;
import com.projectgoth.fusion.slice.MessageSwitchboardPrxHelper;
import com.projectgoth.fusion.slice.ObjectCacheAdminPrx;
import com.projectgoth.fusion.slice.ObjectCacheAdminPrxHelper;
import com.projectgoth.fusion.slice.ObjectCachePrx;
import com.projectgoth.fusion.slice.ObjectCachePrxHelper;
import com.projectgoth.fusion.slice.ObjectCacheStats;
import com.projectgoth.fusion.slice.ObjectCacheStatsHolder;
import com.projectgoth.fusion.slice.ObjectExistsException;
import com.projectgoth.fusion.slice.ObjectNotFoundException;
import com.projectgoth.fusion.slice.RegistryNode;
import com.projectgoth.fusion.slice.RegistryNodePrx;
import com.projectgoth.fusion.slice.RegistryNodePrxHelper;
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.slice.UserPrxHelper;
import java.util.Arrays;

public abstract class _RegistryNodeDisp
extends ObjectImpl
implements RegistryNode {
    public static final String[] __ids = new String[]{"::Ice::Object", "::com::projectgoth::fusion::slice::RegistryNode"};
    private static final String[] __all = new String[]{"deregisterBotService", "deregisterChatRoomObject", "deregisterConnectionObject", "deregisterGroupChatObject", "deregisterMessageSwitchboard", "deregisterObjectCache", "deregisterUserObject", "ice_id", "ice_ids", "ice_isA", "ice_ping", "registerBotService", "registerChatRoomObject", "registerConnectionObject", "registerGroupChatObject", "registerMessageSwitchboard", "registerNewNode", "registerObjectCache", "registerObjectCacheStats", "registerUserObject"};

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

    public final void deregisterBotService(String hostName) {
        this.deregisterBotService(hostName, null);
    }

    public final void deregisterChatRoomObject(String name) {
        this.deregisterChatRoomObject(name, null);
    }

    public final void deregisterConnectionObject(String sessionID) {
        this.deregisterConnectionObject(sessionID, null);
    }

    public final void deregisterGroupChatObject(String id) {
        this.deregisterGroupChatObject(id, null);
    }

    public final void deregisterMessageSwitchboard(String hostName) {
        this.deregisterMessageSwitchboard(hostName, null);
    }

    public final void deregisterObjectCache(String hostName) {
        this.deregisterObjectCache(hostName, null);
    }

    public final void deregisterUserObject(String username, String objectCacheHostname) {
        this.deregisterUserObject(username, objectCacheHostname, null);
    }

    public final void registerBotService(String hostName, int load, BotServicePrx serviceProxy, BotServiceAdminPrx adminProxy) {
        this.registerBotService(hostName, load, serviceProxy, adminProxy, null);
    }

    public final void registerChatRoomObject(String name, ChatRoomPrx chatRoomProxy) throws ObjectExistsException {
        this.registerChatRoomObject(name, chatRoomProxy, null);
    }

    public final void registerConnectionObject(String sessionID, ConnectionPrx connectionProxy) throws ObjectExistsException {
        this.registerConnectionObject(sessionID, connectionProxy, null);
    }

    public final void registerGroupChatObject(String id, GroupChatPrx groupChatProxy) {
        this.registerGroupChatObject(id, groupChatProxy, null);
    }

    public final void registerMessageSwitchboard(String hostName, MessageSwitchboardPrx cacheProxy, MessageSwitchboardAdminPrx adminProxy) {
        this.registerMessageSwitchboard(hostName, cacheProxy, adminProxy, null);
    }

    public final String registerNewNode(RegistryNodePrx newNodeProxy, String hostName, boolean replicate) throws FusionException {
        return this.registerNewNode(newNodeProxy, hostName, replicate, null);
    }

    public final void registerObjectCache(String hostName, ObjectCachePrx cacheProxy, ObjectCacheAdminPrx adminProxy) {
        this.registerObjectCache(hostName, cacheProxy, adminProxy, null);
    }

    public final void registerObjectCacheStats(String objectCacheHostName, ObjectCacheStats stats) throws ObjectNotFoundException {
        this.registerObjectCacheStats(objectCacheHostName, stats, null);
    }

    public final void registerUserObject(String username, UserPrx userProxy, String objectCacheHostname) throws ObjectExistsException {
        this.registerUserObject(username, userProxy, objectCacheHostname, null);
    }

    public static DispatchStatus ___registerUserObject(RegistryNode __obj, Incoming __inS, Current __current) {
        _RegistryNodeDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String username = __is.readString();
        UserPrx userProxy = UserPrxHelper.__read(__is);
        String objectCacheHostname = __is.readString();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            __obj.registerUserObject(username, userProxy, objectCacheHostname, __current);
            return DispatchStatus.DispatchOK;
        }
        catch (ObjectExistsException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___deregisterUserObject(RegistryNode __obj, Incoming __inS, Current __current) {
        _RegistryNodeDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String username = __is.readString();
        String objectCacheHostname = __is.readString();
        __is.endReadEncaps();
        __obj.deregisterUserObject(username, objectCacheHostname, __current);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___registerConnectionObject(RegistryNode __obj, Incoming __inS, Current __current) {
        _RegistryNodeDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String sessionID = __is.readString();
        ConnectionPrx connectionProxy = ConnectionPrxHelper.__read(__is);
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            __obj.registerConnectionObject(sessionID, connectionProxy, __current);
            return DispatchStatus.DispatchOK;
        }
        catch (ObjectExistsException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___deregisterConnectionObject(RegistryNode __obj, Incoming __inS, Current __current) {
        _RegistryNodeDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String sessionID = __is.readString();
        __is.endReadEncaps();
        __obj.deregisterConnectionObject(sessionID, __current);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___registerChatRoomObject(RegistryNode __obj, Incoming __inS, Current __current) {
        _RegistryNodeDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String name = __is.readString();
        ChatRoomPrx chatRoomProxy = ChatRoomPrxHelper.__read(__is);
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            __obj.registerChatRoomObject(name, chatRoomProxy, __current);
            return DispatchStatus.DispatchOK;
        }
        catch (ObjectExistsException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___deregisterChatRoomObject(RegistryNode __obj, Incoming __inS, Current __current) {
        _RegistryNodeDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String name = __is.readString();
        __is.endReadEncaps();
        __obj.deregisterChatRoomObject(name, __current);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___registerGroupChatObject(RegistryNode __obj, Incoming __inS, Current __current) {
        _RegistryNodeDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id = __is.readString();
        GroupChatPrx groupChatProxy = GroupChatPrxHelper.__read(__is);
        __is.endReadEncaps();
        __obj.registerGroupChatObject(id, groupChatProxy, __current);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___deregisterGroupChatObject(RegistryNode __obj, Incoming __inS, Current __current) {
        _RegistryNodeDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id = __is.readString();
        __is.endReadEncaps();
        __obj.deregisterGroupChatObject(id, __current);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___registerObjectCache(RegistryNode __obj, Incoming __inS, Current __current) {
        _RegistryNodeDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String hostName = __is.readString();
        ObjectCachePrx cacheProxy = ObjectCachePrxHelper.__read(__is);
        ObjectCacheAdminPrx adminProxy = ObjectCacheAdminPrxHelper.__read(__is);
        __is.endReadEncaps();
        __obj.registerObjectCache(hostName, cacheProxy, adminProxy, __current);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___deregisterObjectCache(RegistryNode __obj, Incoming __inS, Current __current) {
        _RegistryNodeDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String hostName = __is.readString();
        __is.endReadEncaps();
        __obj.deregisterObjectCache(hostName, __current);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___registerBotService(RegistryNode __obj, Incoming __inS, Current __current) {
        _RegistryNodeDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String hostName = __is.readString();
        int load = __is.readInt();
        BotServicePrx serviceProxy = BotServicePrxHelper.__read(__is);
        BotServiceAdminPrx adminProxy = BotServiceAdminPrxHelper.__read(__is);
        __is.endReadEncaps();
        __obj.registerBotService(hostName, load, serviceProxy, adminProxy, __current);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___deregisterBotService(RegistryNode __obj, Incoming __inS, Current __current) {
        _RegistryNodeDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String hostName = __is.readString();
        __is.endReadEncaps();
        __obj.deregisterBotService(hostName, __current);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___registerNewNode(RegistryNode __obj, Incoming __inS, Current __current) {
        _RegistryNodeDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        RegistryNodePrx newNodeProxy = RegistryNodePrxHelper.__read(__is);
        String hostName = __is.readString();
        boolean replicate = __is.readBool();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            String __ret = __obj.registerNewNode(newNodeProxy, hostName, replicate, __current);
            __os.writeString(__ret);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___registerObjectCacheStats(RegistryNode __obj, Incoming __inS, Current __current) {
        _RegistryNodeDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String objectCacheHostName = __is.readString();
        ObjectCacheStatsHolder stats = new ObjectCacheStatsHolder();
        __is.readObject((Patcher)stats.getPatcher());
        __is.readPendingObjects();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            __obj.registerObjectCacheStats(objectCacheHostName, stats.value, __current);
            return DispatchStatus.DispatchOK;
        }
        catch (ObjectNotFoundException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___registerMessageSwitchboard(RegistryNode __obj, Incoming __inS, Current __current) {
        _RegistryNodeDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String hostName = __is.readString();
        MessageSwitchboardPrx cacheProxy = MessageSwitchboardPrxHelper.__read(__is);
        MessageSwitchboardAdminPrx adminProxy = MessageSwitchboardAdminPrxHelper.__read(__is);
        __is.endReadEncaps();
        __obj.registerMessageSwitchboard(hostName, cacheProxy, adminProxy, __current);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___deregisterMessageSwitchboard(RegistryNode __obj, Incoming __inS, Current __current) {
        _RegistryNodeDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String hostName = __is.readString();
        __is.endReadEncaps();
        __obj.deregisterMessageSwitchboard(hostName, __current);
        return DispatchStatus.DispatchOK;
    }

    public DispatchStatus __dispatch(Incoming in, Current __current) {
        int pos = Arrays.binarySearch(__all, __current.operation);
        if (pos < 0) {
            throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
        }
        switch (pos) {
            case 0: {
                return _RegistryNodeDisp.___deregisterBotService(this, in, __current);
            }
            case 1: {
                return _RegistryNodeDisp.___deregisterChatRoomObject(this, in, __current);
            }
            case 2: {
                return _RegistryNodeDisp.___deregisterConnectionObject(this, in, __current);
            }
            case 3: {
                return _RegistryNodeDisp.___deregisterGroupChatObject(this, in, __current);
            }
            case 4: {
                return _RegistryNodeDisp.___deregisterMessageSwitchboard(this, in, __current);
            }
            case 5: {
                return _RegistryNodeDisp.___deregisterObjectCache(this, in, __current);
            }
            case 6: {
                return _RegistryNodeDisp.___deregisterUserObject(this, in, __current);
            }
            case 7: {
                return _RegistryNodeDisp.___ice_id((Ice.Object)this, (Incoming)in, (Current)__current);
            }
            case 8: {
                return _RegistryNodeDisp.___ice_ids((Ice.Object)this, (Incoming)in, (Current)__current);
            }
            case 9: {
                return _RegistryNodeDisp.___ice_isA((Ice.Object)this, (Incoming)in, (Current)__current);
            }
            case 10: {
                return _RegistryNodeDisp.___ice_ping((Ice.Object)this, (Incoming)in, (Current)__current);
            }
            case 11: {
                return _RegistryNodeDisp.___registerBotService(this, in, __current);
            }
            case 12: {
                return _RegistryNodeDisp.___registerChatRoomObject(this, in, __current);
            }
            case 13: {
                return _RegistryNodeDisp.___registerConnectionObject(this, in, __current);
            }
            case 14: {
                return _RegistryNodeDisp.___registerGroupChatObject(this, in, __current);
            }
            case 15: {
                return _RegistryNodeDisp.___registerMessageSwitchboard(this, in, __current);
            }
            case 16: {
                return _RegistryNodeDisp.___registerNewNode(this, in, __current);
            }
            case 17: {
                return _RegistryNodeDisp.___registerObjectCache(this, in, __current);
            }
            case 18: {
                return _RegistryNodeDisp.___registerObjectCacheStats(this, in, __current);
            }
            case 19: {
                return _RegistryNodeDisp.___registerUserObject(this, in, __current);
            }
        }
        assert (false);
        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
    }

    public void __write(BasicStream __os) {
        __os.writeTypeId(_RegistryNodeDisp.ice_staticId());
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
        ex.reason = "type com::projectgoth::fusion::slice::RegistryNode was not generated with stream support";
        throw ex;
    }

    public void __read(InputStream __inS, boolean __rid) {
        MarshalException ex = new MarshalException();
        ex.reason = "type com::projectgoth::fusion::slice::RegistryNode was not generated with stream support";
        throw ex;
    }
}

