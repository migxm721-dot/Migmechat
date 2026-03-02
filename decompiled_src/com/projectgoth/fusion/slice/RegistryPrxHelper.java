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
import com.projectgoth.fusion.slice.BotServiceAdminPrx;
import com.projectgoth.fusion.slice.BotServicePrx;
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.ConnectionPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GroupChatPrx;
import com.projectgoth.fusion.slice.MessageSwitchboardAdminPrx;
import com.projectgoth.fusion.slice.MessageSwitchboardPrx;
import com.projectgoth.fusion.slice.ObjectCacheAdminPrx;
import com.projectgoth.fusion.slice.ObjectCachePrx;
import com.projectgoth.fusion.slice.ObjectCacheStats;
import com.projectgoth.fusion.slice.ObjectExistsException;
import com.projectgoth.fusion.slice.ObjectNotFoundException;
import com.projectgoth.fusion.slice.RegistryPrx;
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.slice._RegistryDel;
import com.projectgoth.fusion.slice._RegistryDelD;
import com.projectgoth.fusion.slice._RegistryDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class RegistryPrxHelper
extends ObjectPrxHelperBase
implements RegistryPrx {
    @Override
    public void deregisterBotService(String hostName) {
        this.deregisterBotService(hostName, null, false);
    }

    @Override
    public void deregisterBotService(String hostName, Map<String, String> __ctx) {
        this.deregisterBotService(hostName, __ctx, true);
    }

    private void deregisterBotService(String hostName, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _RegistryDel __del = (_RegistryDel)__delBase;
                __del.deregisterBotService(hostName, __ctx);
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
    public void deregisterChatRoomObject(String name) {
        this.deregisterChatRoomObject(name, null, false);
    }

    @Override
    public void deregisterChatRoomObject(String name, Map<String, String> __ctx) {
        this.deregisterChatRoomObject(name, __ctx, true);
    }

    private void deregisterChatRoomObject(String name, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _RegistryDel __del = (_RegistryDel)__delBase;
                __del.deregisterChatRoomObject(name, __ctx);
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
    public void deregisterConnectionObject(String sessionID) {
        this.deregisterConnectionObject(sessionID, null, false);
    }

    @Override
    public void deregisterConnectionObject(String sessionID, Map<String, String> __ctx) {
        this.deregisterConnectionObject(sessionID, __ctx, true);
    }

    private void deregisterConnectionObject(String sessionID, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _RegistryDel __del = (_RegistryDel)__delBase;
                __del.deregisterConnectionObject(sessionID, __ctx);
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
    public void deregisterGroupChatObject(String id) {
        this.deregisterGroupChatObject(id, null, false);
    }

    @Override
    public void deregisterGroupChatObject(String id, Map<String, String> __ctx) {
        this.deregisterGroupChatObject(id, __ctx, true);
    }

    private void deregisterGroupChatObject(String id, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _RegistryDel __del = (_RegistryDel)__delBase;
                __del.deregisterGroupChatObject(id, __ctx);
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
    public void deregisterMessageSwitchboard(String hostName) {
        this.deregisterMessageSwitchboard(hostName, null, false);
    }

    @Override
    public void deregisterMessageSwitchboard(String hostName, Map<String, String> __ctx) {
        this.deregisterMessageSwitchboard(hostName, __ctx, true);
    }

    private void deregisterMessageSwitchboard(String hostName, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _RegistryDel __del = (_RegistryDel)__delBase;
                __del.deregisterMessageSwitchboard(hostName, __ctx);
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
    public void deregisterObjectCache(String hostName) {
        this.deregisterObjectCache(hostName, null, false);
    }

    @Override
    public void deregisterObjectCache(String hostName, Map<String, String> __ctx) {
        this.deregisterObjectCache(hostName, __ctx, true);
    }

    private void deregisterObjectCache(String hostName, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _RegistryDel __del = (_RegistryDel)__delBase;
                __del.deregisterObjectCache(hostName, __ctx);
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
    public void deregisterUserObject(String username, String objectCacheHostname) {
        this.deregisterUserObject(username, objectCacheHostname, null, false);
    }

    @Override
    public void deregisterUserObject(String username, String objectCacheHostname, Map<String, String> __ctx) {
        this.deregisterUserObject(username, objectCacheHostname, __ctx, true);
    }

    private void deregisterUserObject(String username, String objectCacheHostname, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _RegistryDel __del = (_RegistryDel)__delBase;
                __del.deregisterUserObject(username, objectCacheHostname, __ctx);
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
    public ChatRoomPrx findChatRoomObject(String name) throws ObjectNotFoundException {
        return this.findChatRoomObject(name, null, false);
    }

    @Override
    public ChatRoomPrx findChatRoomObject(String name, Map<String, String> __ctx) throws ObjectNotFoundException {
        return this.findChatRoomObject(name, __ctx, true);
    }

    private ChatRoomPrx findChatRoomObject(String name, Map<String, String> __ctx, boolean __explicitCtx) throws ObjectNotFoundException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("findChatRoomObject");
                __delBase = this.__getDelegate(false);
                _RegistryDel __del = (_RegistryDel)__delBase;
                return __del.findChatRoomObject(name, __ctx);
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
    public ChatRoomPrx[] findChatRoomObjects(String[] chatRoomNames) {
        return this.findChatRoomObjects(chatRoomNames, null, false);
    }

    @Override
    public ChatRoomPrx[] findChatRoomObjects(String[] chatRoomNames, Map<String, String> __ctx) {
        return this.findChatRoomObjects(chatRoomNames, __ctx, true);
    }

    private ChatRoomPrx[] findChatRoomObjects(String[] chatRoomNames, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("findChatRoomObjects");
                __delBase = this.__getDelegate(false);
                _RegistryDel __del = (_RegistryDel)__delBase;
                return __del.findChatRoomObjects(chatRoomNames, __ctx);
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
    public ConnectionPrx findConnectionObject(String sessionID) throws ObjectNotFoundException {
        return this.findConnectionObject(sessionID, null, false);
    }

    @Override
    public ConnectionPrx findConnectionObject(String sessionID, Map<String, String> __ctx) throws ObjectNotFoundException {
        return this.findConnectionObject(sessionID, __ctx, true);
    }

    private ConnectionPrx findConnectionObject(String sessionID, Map<String, String> __ctx, boolean __explicitCtx) throws ObjectNotFoundException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("findConnectionObject");
                __delBase = this.__getDelegate(false);
                _RegistryDel __del = (_RegistryDel)__delBase;
                return __del.findConnectionObject(sessionID, __ctx);
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
    public GroupChatPrx findGroupChatObject(String id) throws ObjectNotFoundException {
        return this.findGroupChatObject(id, null, false);
    }

    @Override
    public GroupChatPrx findGroupChatObject(String id, Map<String, String> __ctx) throws ObjectNotFoundException {
        return this.findGroupChatObject(id, __ctx, true);
    }

    private GroupChatPrx findGroupChatObject(String id, Map<String, String> __ctx, boolean __explicitCtx) throws ObjectNotFoundException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("findGroupChatObject");
                __delBase = this.__getDelegate(false);
                _RegistryDel __del = (_RegistryDel)__delBase;
                return __del.findGroupChatObject(id, __ctx);
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
    public UserPrx findUserObject(String username) throws ObjectNotFoundException {
        return this.findUserObject(username, null, false);
    }

    @Override
    public UserPrx findUserObject(String username, Map<String, String> __ctx) throws ObjectNotFoundException {
        return this.findUserObject(username, __ctx, true);
    }

    private UserPrx findUserObject(String username, Map<String, String> __ctx, boolean __explicitCtx) throws ObjectNotFoundException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("findUserObject");
                __delBase = this.__getDelegate(false);
                _RegistryDel __del = (_RegistryDel)__delBase;
                return __del.findUserObject(username, __ctx);
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
    public UserPrx[] findUserObjects(String[] usernames) {
        return this.findUserObjects(usernames, null, false);
    }

    @Override
    public UserPrx[] findUserObjects(String[] usernames, Map<String, String> __ctx) {
        return this.findUserObjects(usernames, __ctx, true);
    }

    private UserPrx[] findUserObjects(String[] usernames, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("findUserObjects");
                __delBase = this.__getDelegate(false);
                _RegistryDel __del = (_RegistryDel)__delBase;
                return __del.findUserObjects(usernames, __ctx);
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
    public Map<String, UserPrx> findUserObjectsMap(String[] usernames) {
        return this.findUserObjectsMap(usernames, null, false);
    }

    @Override
    public Map<String, UserPrx> findUserObjectsMap(String[] usernames, Map<String, String> __ctx) {
        return this.findUserObjectsMap(usernames, __ctx, true);
    }

    private Map<String, UserPrx> findUserObjectsMap(String[] usernames, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("findUserObjectsMap");
                __delBase = this.__getDelegate(false);
                _RegistryDel __del = (_RegistryDel)__delBase;
                return __del.findUserObjectsMap(usernames, __ctx);
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
    public BotServicePrx getLowestLoadedBotService() throws ObjectNotFoundException {
        return this.getLowestLoadedBotService(null, false);
    }

    @Override
    public BotServicePrx getLowestLoadedBotService(Map<String, String> __ctx) throws ObjectNotFoundException {
        return this.getLowestLoadedBotService(__ctx, true);
    }

    private BotServicePrx getLowestLoadedBotService(Map<String, String> __ctx, boolean __explicitCtx) throws ObjectNotFoundException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getLowestLoadedBotService");
                __delBase = this.__getDelegate(false);
                _RegistryDel __del = (_RegistryDel)__delBase;
                return __del.getLowestLoadedBotService(__ctx);
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
    public ObjectCachePrx getLowestLoadedObjectCache() throws ObjectNotFoundException {
        return this.getLowestLoadedObjectCache(null, false);
    }

    @Override
    public ObjectCachePrx getLowestLoadedObjectCache(Map<String, String> __ctx) throws ObjectNotFoundException {
        return this.getLowestLoadedObjectCache(__ctx, true);
    }

    private ObjectCachePrx getLowestLoadedObjectCache(Map<String, String> __ctx, boolean __explicitCtx) throws ObjectNotFoundException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getLowestLoadedObjectCache");
                __delBase = this.__getDelegate(false);
                _RegistryDel __del = (_RegistryDel)__delBase;
                return __del.getLowestLoadedObjectCache(__ctx);
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
    public MessageSwitchboardPrx getMessageSwitchboard() throws FusionException {
        return this.getMessageSwitchboard(null, false);
    }

    @Override
    public MessageSwitchboardPrx getMessageSwitchboard(Map<String, String> __ctx) throws FusionException {
        return this.getMessageSwitchboard(__ctx, true);
    }

    private MessageSwitchboardPrx getMessageSwitchboard(Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getMessageSwitchboard");
                __delBase = this.__getDelegate(false);
                _RegistryDel __del = (_RegistryDel)__delBase;
                return __del.getMessageSwitchboard(__ctx);
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
    public int getUserCount() {
        return this.getUserCount(null, false);
    }

    @Override
    public int getUserCount(Map<String, String> __ctx) {
        return this.getUserCount(__ctx, true);
    }

    private int getUserCount(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getUserCount");
                __delBase = this.__getDelegate(false);
                _RegistryDel __del = (_RegistryDel)__delBase;
                return __del.getUserCount(__ctx);
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
    public int newGatewayID() {
        return this.newGatewayID(null, false);
    }

    @Override
    public int newGatewayID(Map<String, String> __ctx) {
        return this.newGatewayID(__ctx, true);
    }

    private int newGatewayID(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("newGatewayID");
                __delBase = this.__getDelegate(false);
                _RegistryDel __del = (_RegistryDel)__delBase;
                return __del.newGatewayID(__ctx);
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
    public void registerBotService(String hostName, int load, BotServicePrx serviceProxy, BotServiceAdminPrx adminProxy) {
        this.registerBotService(hostName, load, serviceProxy, adminProxy, null, false);
    }

    @Override
    public void registerBotService(String hostName, int load, BotServicePrx serviceProxy, BotServiceAdminPrx adminProxy, Map<String, String> __ctx) {
        this.registerBotService(hostName, load, serviceProxy, adminProxy, __ctx, true);
    }

    private void registerBotService(String hostName, int load, BotServicePrx serviceProxy, BotServiceAdminPrx adminProxy, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _RegistryDel __del = (_RegistryDel)__delBase;
                __del.registerBotService(hostName, load, serviceProxy, adminProxy, __ctx);
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
    public void registerChatRoomObject(String name, ChatRoomPrx chatRoomProxy) throws ObjectExistsException {
        this.registerChatRoomObject(name, chatRoomProxy, null, false);
    }

    @Override
    public void registerChatRoomObject(String name, ChatRoomPrx chatRoomProxy, Map<String, String> __ctx) throws ObjectExistsException {
        this.registerChatRoomObject(name, chatRoomProxy, __ctx, true);
    }

    private void registerChatRoomObject(String name, ChatRoomPrx chatRoomProxy, Map<String, String> __ctx, boolean __explicitCtx) throws ObjectExistsException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("registerChatRoomObject");
                __delBase = this.__getDelegate(false);
                _RegistryDel __del = (_RegistryDel)__delBase;
                __del.registerChatRoomObject(name, chatRoomProxy, __ctx);
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
    public void registerConnectionObject(String sessionID, ConnectionPrx connectionProxy) throws ObjectExistsException {
        this.registerConnectionObject(sessionID, connectionProxy, null, false);
    }

    @Override
    public void registerConnectionObject(String sessionID, ConnectionPrx connectionProxy, Map<String, String> __ctx) throws ObjectExistsException {
        this.registerConnectionObject(sessionID, connectionProxy, __ctx, true);
    }

    private void registerConnectionObject(String sessionID, ConnectionPrx connectionProxy, Map<String, String> __ctx, boolean __explicitCtx) throws ObjectExistsException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("registerConnectionObject");
                __delBase = this.__getDelegate(false);
                _RegistryDel __del = (_RegistryDel)__delBase;
                __del.registerConnectionObject(sessionID, connectionProxy, __ctx);
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
    public void registerGroupChatObject(String id, GroupChatPrx groupChatProxy) {
        this.registerGroupChatObject(id, groupChatProxy, null, false);
    }

    @Override
    public void registerGroupChatObject(String id, GroupChatPrx groupChatProxy, Map<String, String> __ctx) {
        this.registerGroupChatObject(id, groupChatProxy, __ctx, true);
    }

    private void registerGroupChatObject(String id, GroupChatPrx groupChatProxy, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _RegistryDel __del = (_RegistryDel)__delBase;
                __del.registerGroupChatObject(id, groupChatProxy, __ctx);
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
    public void registerMessageSwitchboard(String hostName, MessageSwitchboardPrx msbProxy, MessageSwitchboardAdminPrx adminProxy) {
        this.registerMessageSwitchboard(hostName, msbProxy, adminProxy, null, false);
    }

    @Override
    public void registerMessageSwitchboard(String hostName, MessageSwitchboardPrx msbProxy, MessageSwitchboardAdminPrx adminProxy, Map<String, String> __ctx) {
        this.registerMessageSwitchboard(hostName, msbProxy, adminProxy, __ctx, true);
    }

    private void registerMessageSwitchboard(String hostName, MessageSwitchboardPrx msbProxy, MessageSwitchboardAdminPrx adminProxy, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _RegistryDel __del = (_RegistryDel)__delBase;
                __del.registerMessageSwitchboard(hostName, msbProxy, adminProxy, __ctx);
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
    public void registerObjectCache(String hostName, ObjectCachePrx cacheProxy, ObjectCacheAdminPrx adminProxy) {
        this.registerObjectCache(hostName, cacheProxy, adminProxy, null, false);
    }

    @Override
    public void registerObjectCache(String hostName, ObjectCachePrx cacheProxy, ObjectCacheAdminPrx adminProxy, Map<String, String> __ctx) {
        this.registerObjectCache(hostName, cacheProxy, adminProxy, __ctx, true);
    }

    private void registerObjectCache(String hostName, ObjectCachePrx cacheProxy, ObjectCacheAdminPrx adminProxy, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _RegistryDel __del = (_RegistryDel)__delBase;
                __del.registerObjectCache(hostName, cacheProxy, adminProxy, __ctx);
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
    public void registerObjectCacheStats(String objectCacheHostName, ObjectCacheStats stats) throws ObjectNotFoundException {
        this.registerObjectCacheStats(objectCacheHostName, stats, null, false);
    }

    @Override
    public void registerObjectCacheStats(String objectCacheHostName, ObjectCacheStats stats, Map<String, String> __ctx) throws ObjectNotFoundException {
        this.registerObjectCacheStats(objectCacheHostName, stats, __ctx, true);
    }

    private void registerObjectCacheStats(String objectCacheHostName, ObjectCacheStats stats, Map<String, String> __ctx, boolean __explicitCtx) throws ObjectNotFoundException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("registerObjectCacheStats");
                __delBase = this.__getDelegate(false);
                _RegistryDel __del = (_RegistryDel)__delBase;
                __del.registerObjectCacheStats(objectCacheHostName, stats, __ctx);
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
    public void registerUserObject(String username, UserPrx userProxy, String objectCacheHostname) throws ObjectExistsException {
        this.registerUserObject(username, userProxy, objectCacheHostname, null, false);
    }

    @Override
    public void registerUserObject(String username, UserPrx userProxy, String objectCacheHostname, Map<String, String> __ctx) throws ObjectExistsException {
        this.registerUserObject(username, userProxy, objectCacheHostname, __ctx, true);
    }

    private void registerUserObject(String username, UserPrx userProxy, String objectCacheHostname, Map<String, String> __ctx, boolean __explicitCtx) throws ObjectExistsException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("registerUserObject");
                __delBase = this.__getDelegate(false);
                _RegistryDel __del = (_RegistryDel)__delBase;
                __del.registerUserObject(username, userProxy, objectCacheHostname, __ctx);
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
    public void sendAlertMessageToAllUsers(String message, String title, short timeout) throws FusionException {
        this.sendAlertMessageToAllUsers(message, title, timeout, null, false);
    }

    @Override
    public void sendAlertMessageToAllUsers(String message, String title, short timeout, Map<String, String> __ctx) throws FusionException {
        this.sendAlertMessageToAllUsers(message, title, timeout, __ctx, true);
    }

    private void sendAlertMessageToAllUsers(String message, String title, short timeout, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("sendAlertMessageToAllUsers");
                __delBase = this.__getDelegate(false);
                _RegistryDel __del = (_RegistryDel)__delBase;
                __del.sendAlertMessageToAllUsers(message, title, timeout, __ctx);
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

    public static RegistryPrx checkedCast(ObjectPrx __obj) {
        RegistryPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (RegistryPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::Registry")) break block3;
                    RegistryPrxHelper __h = new RegistryPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static RegistryPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        RegistryPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (RegistryPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::Registry", __ctx)) break block3;
                    RegistryPrxHelper __h = new RegistryPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static RegistryPrx checkedCast(ObjectPrx __obj, String __facet) {
        RegistryPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::Registry")) {
                    RegistryPrxHelper __h = new RegistryPrxHelper();
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

    public static RegistryPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        RegistryPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::Registry", __ctx)) {
                    RegistryPrxHelper __h = new RegistryPrxHelper();
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

    public static RegistryPrx uncheckedCast(ObjectPrx __obj) {
        RegistryPrx __d = null;
        if (__obj != null) {
            try {
                __d = (RegistryPrx)__obj;
            }
            catch (ClassCastException ex) {
                RegistryPrxHelper __h = new RegistryPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static RegistryPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        RegistryPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            RegistryPrxHelper __h = new RegistryPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _RegistryDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _RegistryDelD();
    }

    public static void __write(BasicStream __os, RegistryPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static RegistryPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            RegistryPrxHelper result = new RegistryPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

