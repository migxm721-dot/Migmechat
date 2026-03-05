/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 *  Ice.TieBase
 */
package com.projectgoth.fusion.slice;

import Ice.Current;
import Ice.TieBase;
import com.projectgoth.fusion.slice.AMD_Registry_getMessageSwitchboard;
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
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.slice._RegistryDisp;
import com.projectgoth.fusion.slice._RegistryOperations;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class _RegistryTie
extends _RegistryDisp
implements TieBase {
    private _RegistryOperations _ice_delegate;

    public _RegistryTie() {
    }

    public _RegistryTie(_RegistryOperations delegate) {
        this._ice_delegate = delegate;
    }

    public Object ice_delegate() {
        return this._ice_delegate;
    }

    public void ice_delegate(Object delegate) {
        this._ice_delegate = (_RegistryOperations)delegate;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        if (!(rhs instanceof _RegistryTie)) {
            return false;
        }
        return this._ice_delegate.equals(((_RegistryTie)rhs)._ice_delegate);
    }

    public int hashCode() {
        return this._ice_delegate.hashCode();
    }

    @Override
    public void deregisterBotService(String hostName, Current __current) {
        this._ice_delegate.deregisterBotService(hostName, __current);
    }

    @Override
    public void deregisterChatRoomObject(String name, Current __current) {
        this._ice_delegate.deregisterChatRoomObject(name, __current);
    }

    @Override
    public void deregisterConnectionObject(String sessionID, Current __current) {
        this._ice_delegate.deregisterConnectionObject(sessionID, __current);
    }

    @Override
    public void deregisterGroupChatObject(String id, Current __current) {
        this._ice_delegate.deregisterGroupChatObject(id, __current);
    }

    @Override
    public void deregisterMessageSwitchboard(String hostName, Current __current) {
        this._ice_delegate.deregisterMessageSwitchboard(hostName, __current);
    }

    @Override
    public void deregisterObjectCache(String hostName, Current __current) {
        this._ice_delegate.deregisterObjectCache(hostName, __current);
    }

    @Override
    public void deregisterUserObject(String username, String objectCacheHostname, Current __current) {
        this._ice_delegate.deregisterUserObject(username, objectCacheHostname, __current);
    }

    @Override
    public ChatRoomPrx findChatRoomObject(String name, Current __current) throws ObjectNotFoundException {
        return this._ice_delegate.findChatRoomObject(name, __current);
    }

    @Override
    public ChatRoomPrx[] findChatRoomObjects(String[] chatRoomNames, Current __current) {
        return this._ice_delegate.findChatRoomObjects(chatRoomNames, __current);
    }

    @Override
    public ConnectionPrx findConnectionObject(String sessionID, Current __current) throws ObjectNotFoundException {
        return this._ice_delegate.findConnectionObject(sessionID, __current);
    }

    @Override
    public GroupChatPrx findGroupChatObject(String id, Current __current) throws ObjectNotFoundException {
        return this._ice_delegate.findGroupChatObject(id, __current);
    }

    @Override
    public UserPrx findUserObject(String username, Current __current) throws ObjectNotFoundException {
        return this._ice_delegate.findUserObject(username, __current);
    }

    @Override
    public UserPrx[] findUserObjects(String[] usernames, Current __current) {
        return this._ice_delegate.findUserObjects(usernames, __current);
    }

    @Override
    public Map<String, UserPrx> findUserObjectsMap(String[] usernames, Current __current) {
        return this._ice_delegate.findUserObjectsMap(usernames, __current);
    }

    @Override
    public BotServicePrx getLowestLoadedBotService(Current __current) throws ObjectNotFoundException {
        return this._ice_delegate.getLowestLoadedBotService(__current);
    }

    @Override
    public ObjectCachePrx getLowestLoadedObjectCache(Current __current) throws ObjectNotFoundException {
        return this._ice_delegate.getLowestLoadedObjectCache(__current);
    }

    @Override
    public void getMessageSwitchboard_async(AMD_Registry_getMessageSwitchboard __cb, Current __current) throws FusionException {
        this._ice_delegate.getMessageSwitchboard_async(__cb, __current);
    }

    @Override
    public int getUserCount(Current __current) {
        return this._ice_delegate.getUserCount(__current);
    }

    @Override
    public int newGatewayID(Current __current) {
        return this._ice_delegate.newGatewayID(__current);
    }

    @Override
    public void registerBotService(String hostName, int load, BotServicePrx serviceProxy, BotServiceAdminPrx adminProxy, Current __current) {
        this._ice_delegate.registerBotService(hostName, load, serviceProxy, adminProxy, __current);
    }

    @Override
    public void registerChatRoomObject(String name, ChatRoomPrx chatRoomProxy, Current __current) throws ObjectExistsException {
        this._ice_delegate.registerChatRoomObject(name, chatRoomProxy, __current);
    }

    @Override
    public void registerConnectionObject(String sessionID, ConnectionPrx connectionProxy, Current __current) throws ObjectExistsException {
        this._ice_delegate.registerConnectionObject(sessionID, connectionProxy, __current);
    }

    @Override
    public void registerGroupChatObject(String id, GroupChatPrx groupChatProxy, Current __current) {
        this._ice_delegate.registerGroupChatObject(id, groupChatProxy, __current);
    }

    @Override
    public void registerMessageSwitchboard(String hostName, MessageSwitchboardPrx msbProxy, MessageSwitchboardAdminPrx adminProxy, Current __current) {
        this._ice_delegate.registerMessageSwitchboard(hostName, msbProxy, adminProxy, __current);
    }

    @Override
    public void registerObjectCache(String hostName, ObjectCachePrx cacheProxy, ObjectCacheAdminPrx adminProxy, Current __current) {
        this._ice_delegate.registerObjectCache(hostName, cacheProxy, adminProxy, __current);
    }

    @Override
    public void registerObjectCacheStats(String objectCacheHostName, ObjectCacheStats stats, Current __current) throws ObjectNotFoundException {
        this._ice_delegate.registerObjectCacheStats(objectCacheHostName, stats, __current);
    }

    @Override
    public void registerUserObject(String username, UserPrx userProxy, String objectCacheHostname, Current __current) throws ObjectExistsException {
        this._ice_delegate.registerUserObject(username, userProxy, objectCacheHostname, __current);
    }

    @Override
    public void sendAlertMessageToAllUsers(String message, String title, short timeout, Current __current) throws FusionException {
        this._ice_delegate.sendAlertMessageToAllUsers(message, title, timeout, __current);
    }
}

