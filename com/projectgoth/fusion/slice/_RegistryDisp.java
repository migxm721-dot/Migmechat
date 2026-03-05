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
import com.projectgoth.fusion.slice.AMD_Registry_getMessageSwitchboard;
import com.projectgoth.fusion.slice.BotServiceAdminPrx;
import com.projectgoth.fusion.slice.BotServiceAdminPrxHelper;
import com.projectgoth.fusion.slice.BotServicePrx;
import com.projectgoth.fusion.slice.BotServicePrxHelper;
import com.projectgoth.fusion.slice.ChatRoomProxyArrayHelper;
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
import com.projectgoth.fusion.slice.Registry;
import com.projectgoth.fusion.slice.StringArrayHelper;
import com.projectgoth.fusion.slice.UserProxyArrayHelper;
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.slice.UserPrxHelper;
import com.projectgoth.fusion.slice.UsernameToProxyMapHelper;
import com.projectgoth.fusion.slice._AMD_Registry_getMessageSwitchboard;
import java.util.Arrays;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class _RegistryDisp
extends ObjectImpl
implements Registry {
    public static final String[] __ids = new String[]{"::Ice::Object", "::com::projectgoth::fusion::slice::Registry"};
    private static final String[] __all = new String[]{"deregisterBotService", "deregisterChatRoomObject", "deregisterConnectionObject", "deregisterGroupChatObject", "deregisterMessageSwitchboard", "deregisterObjectCache", "deregisterUserObject", "findChatRoomObject", "findChatRoomObjects", "findConnectionObject", "findGroupChatObject", "findUserObject", "findUserObjects", "findUserObjectsMap", "getLowestLoadedBotService", "getLowestLoadedObjectCache", "getMessageSwitchboard", "getUserCount", "ice_id", "ice_ids", "ice_isA", "ice_ping", "newGatewayID", "registerBotService", "registerChatRoomObject", "registerConnectionObject", "registerGroupChatObject", "registerMessageSwitchboard", "registerObjectCache", "registerObjectCacheStats", "registerUserObject", "sendAlertMessageToAllUsers"};

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

    @Override
    public final void deregisterBotService(String hostName) {
        this.deregisterBotService(hostName, null);
    }

    @Override
    public final void deregisterChatRoomObject(String name) {
        this.deregisterChatRoomObject(name, null);
    }

    @Override
    public final void deregisterConnectionObject(String sessionID) {
        this.deregisterConnectionObject(sessionID, null);
    }

    @Override
    public final void deregisterGroupChatObject(String id) {
        this.deregisterGroupChatObject(id, null);
    }

    @Override
    public final void deregisterMessageSwitchboard(String hostName) {
        this.deregisterMessageSwitchboard(hostName, null);
    }

    @Override
    public final void deregisterObjectCache(String hostName) {
        this.deregisterObjectCache(hostName, null);
    }

    @Override
    public final void deregisterUserObject(String username, String objectCacheHostname) {
        this.deregisterUserObject(username, objectCacheHostname, null);
    }

    @Override
    public final ChatRoomPrx findChatRoomObject(String name) throws ObjectNotFoundException {
        return this.findChatRoomObject(name, null);
    }

    @Override
    public final ChatRoomPrx[] findChatRoomObjects(String[] chatRoomNames) {
        return this.findChatRoomObjects(chatRoomNames, null);
    }

    @Override
    public final ConnectionPrx findConnectionObject(String sessionID) throws ObjectNotFoundException {
        return this.findConnectionObject(sessionID, null);
    }

    @Override
    public final GroupChatPrx findGroupChatObject(String id) throws ObjectNotFoundException {
        return this.findGroupChatObject(id, null);
    }

    @Override
    public final UserPrx findUserObject(String username) throws ObjectNotFoundException {
        return this.findUserObject(username, null);
    }

    @Override
    public final UserPrx[] findUserObjects(String[] usernames) {
        return this.findUserObjects(usernames, null);
    }

    @Override
    public final Map<String, UserPrx> findUserObjectsMap(String[] usernames) {
        return this.findUserObjectsMap(usernames, null);
    }

    @Override
    public final BotServicePrx getLowestLoadedBotService() throws ObjectNotFoundException {
        return this.getLowestLoadedBotService(null);
    }

    @Override
    public final ObjectCachePrx getLowestLoadedObjectCache() throws ObjectNotFoundException {
        return this.getLowestLoadedObjectCache(null);
    }

    @Override
    public final void getMessageSwitchboard_async(AMD_Registry_getMessageSwitchboard __cb) throws FusionException {
        this.getMessageSwitchboard_async(__cb, null);
    }

    @Override
    public final int getUserCount() {
        return this.getUserCount(null);
    }

    @Override
    public final int newGatewayID() {
        return this.newGatewayID(null);
    }

    @Override
    public final void registerBotService(String hostName, int load, BotServicePrx serviceProxy, BotServiceAdminPrx adminProxy) {
        this.registerBotService(hostName, load, serviceProxy, adminProxy, null);
    }

    @Override
    public final void registerChatRoomObject(String name, ChatRoomPrx chatRoomProxy) throws ObjectExistsException {
        this.registerChatRoomObject(name, chatRoomProxy, null);
    }

    @Override
    public final void registerConnectionObject(String sessionID, ConnectionPrx connectionProxy) throws ObjectExistsException {
        this.registerConnectionObject(sessionID, connectionProxy, null);
    }

    @Override
    public final void registerGroupChatObject(String id, GroupChatPrx groupChatProxy) {
        this.registerGroupChatObject(id, groupChatProxy, null);
    }

    @Override
    public final void registerMessageSwitchboard(String hostName, MessageSwitchboardPrx msbProxy, MessageSwitchboardAdminPrx adminProxy) {
        this.registerMessageSwitchboard(hostName, msbProxy, adminProxy, null);
    }

    @Override
    public final void registerObjectCache(String hostName, ObjectCachePrx cacheProxy, ObjectCacheAdminPrx adminProxy) {
        this.registerObjectCache(hostName, cacheProxy, adminProxy, null);
    }

    @Override
    public final void registerObjectCacheStats(String objectCacheHostName, ObjectCacheStats stats) throws ObjectNotFoundException {
        this.registerObjectCacheStats(objectCacheHostName, stats, null);
    }

    @Override
    public final void registerUserObject(String username, UserPrx userProxy, String objectCacheHostname) throws ObjectExistsException {
        this.registerUserObject(username, userProxy, objectCacheHostname, null);
    }

    @Override
    public final void sendAlertMessageToAllUsers(String message, String title, short timeout) throws FusionException {
        this.sendAlertMessageToAllUsers(message, title, timeout, null);
    }

    public static DispatchStatus ___findUserObject(Registry __obj, Incoming __inS, Current __current) {
        _RegistryDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String username = __is.readString();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            UserPrx __ret = __obj.findUserObject(username, __current);
            UserPrxHelper.__write(__os, __ret);
            return DispatchStatus.DispatchOK;
        }
        catch (ObjectNotFoundException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___findUserObjects(Registry __obj, Incoming __inS, Current __current) {
        _RegistryDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String[] usernames = StringArrayHelper.read(__is);
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        UserPrx[] __ret = __obj.findUserObjects(usernames, __current);
        UserProxyArrayHelper.write(__os, __ret);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___findUserObjectsMap(Registry __obj, Incoming __inS, Current __current) {
        _RegistryDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String[] usernames = StringArrayHelper.read(__is);
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        Map __ret = __obj.findUserObjectsMap(usernames, __current);
        UsernameToProxyMapHelper.write(__os, __ret);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___registerUserObject(Registry __obj, Incoming __inS, Current __current) {
        _RegistryDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
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

    public static DispatchStatus ___deregisterUserObject(Registry __obj, Incoming __inS, Current __current) {
        _RegistryDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String username = __is.readString();
        String objectCacheHostname = __is.readString();
        __is.endReadEncaps();
        __obj.deregisterUserObject(username, objectCacheHostname, __current);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___findConnectionObject(Registry __obj, Incoming __inS, Current __current) {
        _RegistryDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String sessionID = __is.readString();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            ConnectionPrx __ret = __obj.findConnectionObject(sessionID, __current);
            ConnectionPrxHelper.__write(__os, __ret);
            return DispatchStatus.DispatchOK;
        }
        catch (ObjectNotFoundException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___registerConnectionObject(Registry __obj, Incoming __inS, Current __current) {
        _RegistryDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
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

    public static DispatchStatus ___deregisterConnectionObject(Registry __obj, Incoming __inS, Current __current) {
        _RegistryDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String sessionID = __is.readString();
        __is.endReadEncaps();
        __obj.deregisterConnectionObject(sessionID, __current);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___findChatRoomObject(Registry __obj, Incoming __inS, Current __current) {
        _RegistryDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String name = __is.readString();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            ChatRoomPrx __ret = __obj.findChatRoomObject(name, __current);
            ChatRoomPrxHelper.__write(__os, __ret);
            return DispatchStatus.DispatchOK;
        }
        catch (ObjectNotFoundException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___findChatRoomObjects(Registry __obj, Incoming __inS, Current __current) {
        _RegistryDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String[] chatRoomNames = StringArrayHelper.read(__is);
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        ChatRoomPrx[] __ret = __obj.findChatRoomObjects(chatRoomNames, __current);
        ChatRoomProxyArrayHelper.write(__os, __ret);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___registerChatRoomObject(Registry __obj, Incoming __inS, Current __current) {
        _RegistryDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
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

    public static DispatchStatus ___deregisterChatRoomObject(Registry __obj, Incoming __inS, Current __current) {
        _RegistryDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String name = __is.readString();
        __is.endReadEncaps();
        __obj.deregisterChatRoomObject(name, __current);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___findGroupChatObject(Registry __obj, Incoming __inS, Current __current) {
        _RegistryDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id = __is.readString();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            GroupChatPrx __ret = __obj.findGroupChatObject(id, __current);
            GroupChatPrxHelper.__write(__os, __ret);
            return DispatchStatus.DispatchOK;
        }
        catch (ObjectNotFoundException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___registerGroupChatObject(Registry __obj, Incoming __inS, Current __current) {
        _RegistryDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id = __is.readString();
        GroupChatPrx groupChatProxy = GroupChatPrxHelper.__read(__is);
        __is.endReadEncaps();
        __obj.registerGroupChatObject(id, groupChatProxy, __current);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___deregisterGroupChatObject(Registry __obj, Incoming __inS, Current __current) {
        _RegistryDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id = __is.readString();
        __is.endReadEncaps();
        __obj.deregisterGroupChatObject(id, __current);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___getLowestLoadedObjectCache(Registry __obj, Incoming __inS, Current __current) {
        _RegistryDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        __inS.is().skipEmptyEncaps();
        BasicStream __os = __inS.os();
        try {
            ObjectCachePrx __ret = __obj.getLowestLoadedObjectCache(__current);
            ObjectCachePrxHelper.__write(__os, __ret);
            return DispatchStatus.DispatchOK;
        }
        catch (ObjectNotFoundException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___registerObjectCache(Registry __obj, Incoming __inS, Current __current) {
        _RegistryDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String hostName = __is.readString();
        ObjectCachePrx cacheProxy = ObjectCachePrxHelper.__read(__is);
        ObjectCacheAdminPrx adminProxy = ObjectCacheAdminPrxHelper.__read(__is);
        __is.endReadEncaps();
        __obj.registerObjectCache(hostName, cacheProxy, adminProxy, __current);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___deregisterObjectCache(Registry __obj, Incoming __inS, Current __current) {
        _RegistryDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String hostName = __is.readString();
        __is.endReadEncaps();
        __obj.deregisterObjectCache(hostName, __current);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___getLowestLoadedBotService(Registry __obj, Incoming __inS, Current __current) {
        _RegistryDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        __inS.is().skipEmptyEncaps();
        BasicStream __os = __inS.os();
        try {
            BotServicePrx __ret = __obj.getLowestLoadedBotService(__current);
            BotServicePrxHelper.__write(__os, __ret);
            return DispatchStatus.DispatchOK;
        }
        catch (ObjectNotFoundException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___registerBotService(Registry __obj, Incoming __inS, Current __current) {
        _RegistryDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
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

    public static DispatchStatus ___deregisterBotService(Registry __obj, Incoming __inS, Current __current) {
        _RegistryDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String hostName = __is.readString();
        __is.endReadEncaps();
        __obj.deregisterBotService(hostName, __current);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___sendAlertMessageToAllUsers(Registry __obj, Incoming __inS, Current __current) {
        _RegistryDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String message = __is.readString();
        String title = __is.readString();
        short timeout = __is.readShort();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            __obj.sendAlertMessageToAllUsers(message, title, timeout, __current);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___newGatewayID(Registry __obj, Incoming __inS, Current __current) {
        _RegistryDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        __inS.is().skipEmptyEncaps();
        BasicStream __os = __inS.os();
        int __ret = __obj.newGatewayID(__current);
        __os.writeInt(__ret);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___registerObjectCacheStats(Registry __obj, Incoming __inS, Current __current) {
        _RegistryDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
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

    public static DispatchStatus ___getUserCount(Registry __obj, Incoming __inS, Current __current) {
        _RegistryDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        __inS.is().skipEmptyEncaps();
        BasicStream __os = __inS.os();
        int __ret = __obj.getUserCount(__current);
        __os.writeInt(__ret);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___registerMessageSwitchboard(Registry __obj, Incoming __inS, Current __current) {
        _RegistryDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String hostName = __is.readString();
        MessageSwitchboardPrx msbProxy = MessageSwitchboardPrxHelper.__read(__is);
        MessageSwitchboardAdminPrx adminProxy = MessageSwitchboardAdminPrxHelper.__read(__is);
        __is.endReadEncaps();
        __obj.registerMessageSwitchboard(hostName, msbProxy, adminProxy, __current);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___deregisterMessageSwitchboard(Registry __obj, Incoming __inS, Current __current) {
        _RegistryDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String hostName = __is.readString();
        __is.endReadEncaps();
        __obj.deregisterMessageSwitchboard(hostName, __current);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___getMessageSwitchboard(Registry __obj, Incoming __inS, Current __current) {
        _RegistryDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        __inS.is().skipEmptyEncaps();
        _AMD_Registry_getMessageSwitchboard __cb = new _AMD_Registry_getMessageSwitchboard(__inS);
        try {
            __obj.getMessageSwitchboard_async(__cb, __current);
        }
        catch (Exception ex) {
            __cb.ice_exception(ex);
        }
        return DispatchStatus.DispatchAsync;
    }

    public DispatchStatus __dispatch(Incoming in, Current __current) {
        int pos = Arrays.binarySearch(__all, __current.operation);
        if (pos < 0) {
            throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
        }
        switch (pos) {
            case 0: {
                return _RegistryDisp.___deregisterBotService(this, in, __current);
            }
            case 1: {
                return _RegistryDisp.___deregisterChatRoomObject(this, in, __current);
            }
            case 2: {
                return _RegistryDisp.___deregisterConnectionObject(this, in, __current);
            }
            case 3: {
                return _RegistryDisp.___deregisterGroupChatObject(this, in, __current);
            }
            case 4: {
                return _RegistryDisp.___deregisterMessageSwitchboard(this, in, __current);
            }
            case 5: {
                return _RegistryDisp.___deregisterObjectCache(this, in, __current);
            }
            case 6: {
                return _RegistryDisp.___deregisterUserObject(this, in, __current);
            }
            case 7: {
                return _RegistryDisp.___findChatRoomObject(this, in, __current);
            }
            case 8: {
                return _RegistryDisp.___findChatRoomObjects(this, in, __current);
            }
            case 9: {
                return _RegistryDisp.___findConnectionObject(this, in, __current);
            }
            case 10: {
                return _RegistryDisp.___findGroupChatObject(this, in, __current);
            }
            case 11: {
                return _RegistryDisp.___findUserObject(this, in, __current);
            }
            case 12: {
                return _RegistryDisp.___findUserObjects(this, in, __current);
            }
            case 13: {
                return _RegistryDisp.___findUserObjectsMap(this, in, __current);
            }
            case 14: {
                return _RegistryDisp.___getLowestLoadedBotService(this, in, __current);
            }
            case 15: {
                return _RegistryDisp.___getLowestLoadedObjectCache(this, in, __current);
            }
            case 16: {
                return _RegistryDisp.___getMessageSwitchboard(this, in, __current);
            }
            case 17: {
                return _RegistryDisp.___getUserCount(this, in, __current);
            }
            case 18: {
                return _RegistryDisp.___ice_id((Ice.Object)this, (Incoming)in, (Current)__current);
            }
            case 19: {
                return _RegistryDisp.___ice_ids((Ice.Object)this, (Incoming)in, (Current)__current);
            }
            case 20: {
                return _RegistryDisp.___ice_isA((Ice.Object)this, (Incoming)in, (Current)__current);
            }
            case 21: {
                return _RegistryDisp.___ice_ping((Ice.Object)this, (Incoming)in, (Current)__current);
            }
            case 22: {
                return _RegistryDisp.___newGatewayID(this, in, __current);
            }
            case 23: {
                return _RegistryDisp.___registerBotService(this, in, __current);
            }
            case 24: {
                return _RegistryDisp.___registerChatRoomObject(this, in, __current);
            }
            case 25: {
                return _RegistryDisp.___registerConnectionObject(this, in, __current);
            }
            case 26: {
                return _RegistryDisp.___registerGroupChatObject(this, in, __current);
            }
            case 27: {
                return _RegistryDisp.___registerMessageSwitchboard(this, in, __current);
            }
            case 28: {
                return _RegistryDisp.___registerObjectCache(this, in, __current);
            }
            case 29: {
                return _RegistryDisp.___registerObjectCacheStats(this, in, __current);
            }
            case 30: {
                return _RegistryDisp.___registerUserObject(this, in, __current);
            }
            case 31: {
                return _RegistryDisp.___sendAlertMessageToAllUsers(this, in, __current);
            }
        }
        assert (false);
        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
    }

    public void __write(BasicStream __os) {
        __os.writeTypeId(_RegistryDisp.ice_staticId());
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
        ex.reason = "type com::projectgoth::fusion::slice::Registry was not generated with stream support";
        throw ex;
    }

    public void __read(InputStream __inS, boolean __rid) {
        MarshalException ex = new MarshalException();
        ex.reason = "type com::projectgoth::fusion::slice::Registry was not generated with stream support";
        throw ex;
    }
}

