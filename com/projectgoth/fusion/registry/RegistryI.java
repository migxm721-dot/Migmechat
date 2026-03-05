/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 *  Ice.LocalException
 *  Ice.UserException
 *  org.apache.log4j.Logger
 *  org.springframework.util.StringUtils
 */
package com.projectgoth.fusion.registry;

import Ice.Current;
import Ice.LocalException;
import Ice.UserException;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.RequestCounter;
import com.projectgoth.fusion.common.ServiceStatsFactory;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.registry.BotServiceRef;
import com.projectgoth.fusion.registry.MessageSwitchboardRef;
import com.projectgoth.fusion.registry.ObjectCacheLoadBalancer;
import com.projectgoth.fusion.registry.ObjectCacheRef;
import com.projectgoth.fusion.registry.ProxyMap;
import com.projectgoth.fusion.registry.Registry;
import com.projectgoth.fusion.registry.RegistryContext;
import com.projectgoth.fusion.registry.RegistryThreadPoolExecutor;
import com.projectgoth.fusion.slice.AMD_Registry_getMessageSwitchboard;
import com.projectgoth.fusion.slice.AMI_ObjectCache_getMessageSwitchboard;
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
import com.projectgoth.fusion.slice.ObjectCachePrxHelper;
import com.projectgoth.fusion.slice.ObjectCacheStats;
import com.projectgoth.fusion.slice.ObjectExistsException;
import com.projectgoth.fusion.slice.ObjectNotFoundException;
import com.projectgoth.fusion.slice.RegistryStats;
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.slice._RegistryDisp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class RegistryI
extends _RegistryDisp {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(RegistryI.class));
    private int iceInvalidProxyPurgerTimeout;
    private int iceObjectCachePingerTimeout;
    public ConcurrentHashMap<String, ObjectCacheRef> objectCacheRefs;
    public ConcurrentHashMap<String, BotServiceRef> botServiceRefs;
    public ConcurrentHashMap<String, MessageSwitchboardRef> messageSwitchboardRefs;
    private InvalidProxyPurger purger;
    public RequestCounter requestCounter;
    private AtomicInteger gatewayID;
    private RegistryContext applicationContext;
    private final UserMap userMap = new UserMap();
    private final ConnectionMap connectionMap = new ConnectionMap();
    private final RoomMap roomMap = new RoomMap();
    private final GroupMap groupMap = new GroupMap();

    public RegistryI(RegistryContext appContext) {
        this.applicationContext = appContext;
        this.objectCacheRefs = new ConcurrentHashMap();
        this.botServiceRefs = new ConcurrentHashMap();
        this.messageSwitchboardRefs = new ConcurrentHashMap();
        this.gatewayID = new AtomicInteger();
        this.purger = new InvalidProxyPurger();
        this.purger.start();
        Timer timer = new Timer(true);
        int objectCachePingInterval = this.applicationContext.getProperties().getPropertyAsIntWithDefault("ObjectCachePingInterval", 1) * 1000;
        this.iceInvalidProxyPurgerTimeout = this.applicationContext.getProperties().getPropertyAsIntWithDefault("IceInvalidProxyPurgerTimeout", 35000);
        this.iceObjectCachePingerTimeout = this.applicationContext.getProperties().getPropertyAsIntWithDefault("IceObjectCachePingerTimeout", 500);
        ObjectCachePinger pinger = new ObjectCachePinger();
        timer.schedule((TimerTask)pinger, objectCachePingInterval, (long)objectCachePingInterval);
        int botServicePingInterval = this.applicationContext.getProperties().getPropertyAsIntWithDefault("BotServicePingInterval", 1) * 1000;
        BotServicePinger botServicePinger = new BotServicePinger();
        timer.schedule((TimerTask)botServicePinger, botServicePingInterval, (long)botServicePingInterval);
        this.requestCounter = new RequestCounter(this.applicationContext.getProperties().getPropertyAsIntWithDefault("RequestCounterInterval", 10));
    }

    @Override
    public UserPrx findUserObject(String username, Current __current) throws ObjectNotFoundException {
        this.requestCounter.add();
        UserPrx userProxy = (UserPrx)this.userMap.find(username);
        if (userProxy == null) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Unable to return a reference to the User object '" + username + "'"));
            }
            throw new ObjectNotFoundException();
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("Returning a reference to the User object '" + username + "'"));
        }
        return userProxy;
    }

    @Override
    public void registerObjectCacheStats(String objectCacheUniqueID, ObjectCacheStats stats, Current __current) throws ObjectNotFoundException {
        this.requestCounter.add();
        if (this.objectCacheRefs.containsKey(objectCacheUniqueID)) {
            this.objectCacheRefs.get(objectCacheUniqueID).setStats(stats);
            if (log.isDebugEnabled()) {
                log.debug((Object)("received statistics from " + objectCacheUniqueID));
            }
            if (__current != null) {
                this.applicationContext.getRegistryNode().registerObjectCacheStatsWithOtherRegistries(objectCacheUniqueID, stats);
            }
        } else {
            throw new ObjectNotFoundException(objectCacheUniqueID + " is not currently registered");
        }
    }

    @Override
    public void registerUserObject(String username, UserPrx userProxy, String objectCacheUniqueID, Current __current) throws ObjectExistsException {
        this.requestCounter.add();
        if (log.isDebugEnabled()) {
            log.debug((Object)("Registering the User object '" + username + "'"));
        }
        if (userProxy == null) {
            log.warn((Object)("Registering the User object '" + username + "' failed: null"));
            return;
        }
        UserPrx existingProxy = (UserPrx)this.userMap.find(username);
        if (existingProxy != null) {
            log.warn((Object)("The User object '" + username + "' is already registered"));
        }
        this.userMap.register(username, userProxy);
        if (__current != null) {
            this.applicationContext.getRegistryNode().registerUserObjectWithOtherRegistries(username, userProxy, objectCacheUniqueID);
        }
    }

    @Override
    public void deregisterUserObject(String username, String objectCacheUniqueID, Current __current) {
        UserPrx oldValue;
        this.requestCounter.add();
        if (log.isDebugEnabled()) {
            log.debug((Object)("Deregistering the User object '" + username + "'"));
        }
        if ((oldValue = (UserPrx)this.userMap.deregister(username)) == null && log.isDebugEnabled()) {
            log.debug((Object)("The User object '" + username + "' was not found to deregister"));
        }
        if (__current != null) {
            this.applicationContext.getRegistryNode().deregisterUserObjectFromOtherRegistries(username, objectCacheUniqueID);
        }
    }

    @Override
    public UserPrx[] findUserObjects(String[] usernames, Current __current) {
        this.requestCounter.add();
        LinkedList<UserPrx> userProxiesToReturn = new LinkedList<UserPrx>();
        for (String username : usernames) {
            UserPrx userProxy = (UserPrx)this.userMap.find(username);
            if (userProxy == null) continue;
            userProxiesToReturn.add(userProxy);
        }
        return userProxiesToReturn.toArray(new UserPrx[userProxiesToReturn.size()]);
    }

    @Override
    public Map<String, UserPrx> findUserObjectsMap(String[] usernames, Current __current) {
        this.requestCounter.add();
        HashMap<String, UserPrx> result = new HashMap<String, UserPrx>();
        for (String username : usernames) {
            UserPrx userProxy = (UserPrx)this.userMap.find(username);
            if (userProxy == null) continue;
            result.put(username, userProxy);
        }
        return result;
    }

    private int findNumberOfOnlineRegisteredObjectCaches() {
        int count = 0;
        for (ObjectCacheRef objectCacheRef : this.objectCacheRefs.values()) {
            if (!objectCacheRef.isOnline()) continue;
            ++count;
        }
        return count;
    }

    @Override
    public ObjectCachePrx getLowestLoadedObjectCache(Current __current) throws ObjectNotFoundException {
        ObjectCacheRef lowestLoadedObjectCacheRef;
        this.requestCounter.add();
        if (log.isDebugEnabled()) {
            log.debug((Object)("number of online object caches: " + this.findNumberOfOnlineRegisteredObjectCaches()));
        }
        if ((lowestLoadedObjectCacheRef = ObjectCacheLoadBalancer.getLowestLoaded(this.objectCacheRefs.values())) == null) {
            log.warn((Object)"Failed to find a lowest loaded object cache server");
            throw new ObjectNotFoundException();
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("returning lowest loaded object cache: " + lowestLoadedObjectCacheRef.getCacheProxy()));
        }
        return lowestLoadedObjectCacheRef.getCacheProxy();
    }

    @Override
    public void registerObjectCache(String uniqueID, ObjectCachePrx cacheProxy, ObjectCacheAdminPrx adminProxy, Current __current) {
        this.requestCounter.add();
        if (this.objectCacheRefs.get(uniqueID) == null) {
            log.info((Object)("New ObjectCache [" + cacheProxy + "] registered"));
        } else {
            log.info((Object)("Connection to [" + cacheProxy + "] restored"));
        }
        try {
            cacheProxy.ice_ping();
        }
        catch (LocalException e) {
            log.error((Object)("Failed to ping ObjectCache [" + cacheProxy + "] that's attempting to register, ignoring request"));
            throw e;
        }
        this.objectCacheRefs.put(uniqueID, new ObjectCacheRef(uniqueID, cacheProxy, adminProxy));
        if (__current != null) {
            this.applicationContext.getRegistryNode().registerObjectCacheWithOtherRegistries(uniqueID, cacheProxy, adminProxy);
        }
    }

    @Override
    public void deregisterObjectCache(String uniqueID, Current __current) {
        this.requestCounter.add();
        this.objectCacheRefs.remove(uniqueID);
        log.info((Object)("Object Cache " + uniqueID + " was deregistered"));
        if (__current != null) {
            this.applicationContext.getRegistryNode().deregisterObjectCacheFromOtherRegistries(uniqueID);
        }
    }

    @Override
    public ConnectionPrx findConnectionObject(String sessionID, Current __current) throws ObjectNotFoundException {
        this.requestCounter.add();
        ConnectionPrx connectionProxy = (ConnectionPrx)this.connectionMap.find(sessionID);
        if (connectionProxy == null) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Unable to return a reference to the Connection object '" + sessionID + "'"));
            }
            throw new ObjectNotFoundException();
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("Returning a reference to the Connection object '" + sessionID + "'"));
        }
        return connectionProxy;
    }

    @Override
    public void registerConnectionObject(String sessionID, ConnectionPrx connectionProxy, Current __current) throws ObjectExistsException {
        ConnectionPrx existingProxy;
        this.requestCounter.add();
        if (log.isDebugEnabled()) {
            log.debug((Object)("Registering the Connection object '" + sessionID + "'"));
        }
        if ((existingProxy = (ConnectionPrx)this.connectionMap.find(sessionID)) != null) {
            log.error((Object)("Registry " + this.applicationContext.getHostName() + ": Received request to register the Connection object '" + sessionID + "' that is already registered"));
            throw new ObjectExistsException();
        }
        this.connectionMap.register(sessionID, connectionProxy);
        if (__current != null) {
            this.applicationContext.getRegistryNode().registerConnectionObjectWithOtherRegistries(sessionID, connectionProxy);
        }
    }

    @Override
    public void deregisterConnectionObject(String sessionID, Current __current) {
        ConnectionPrx oldValue;
        this.requestCounter.add();
        if (log.isDebugEnabled()) {
            log.debug((Object)("Deregistering the Connection object '" + sessionID + "'"));
        }
        if ((oldValue = (ConnectionPrx)this.connectionMap.deregister(sessionID)) == null && log.isDebugEnabled()) {
            log.debug((Object)("The Connection object '" + sessionID + "' was not found to deregister"));
        }
        if (__current != null) {
            this.applicationContext.getRegistryNode().deregisterConnectionObjectFromOtherRegistries(sessionID);
        }
    }

    @Override
    public ChatRoomPrx findChatRoomObject(String name, Current __current) throws ObjectNotFoundException {
        this.requestCounter.add();
        if (log.isDebugEnabled()) {
            log.debug((Object)("finding chatroom [" + name + "]"));
        }
        if (!StringUtils.hasLength((String)name)) {
            throw new ObjectNotFoundException();
        }
        ChatRoomPrx chatRoomProxy = (ChatRoomPrx)this.roomMap.find(name = name.toLowerCase());
        if (chatRoomProxy == null) {
            throw new ObjectNotFoundException();
        }
        return chatRoomProxy;
    }

    @Override
    public ChatRoomPrx[] findChatRoomObjects(String[] chatRoomNames, Current __current) {
        this.requestCounter.add();
        if (log.isDebugEnabled()) {
            log.debug((Object)("finding chatrooms [" + StringUtil.asString(chatRoomNames) + "]"));
        }
        LinkedList<ChatRoomPrx> chatRoomProxiesToReturn = new LinkedList<ChatRoomPrx>();
        for (String chatRoomName : chatRoomNames) {
            ChatRoomPrx chatRoomProxy = (ChatRoomPrx)this.roomMap.find(chatRoomName.toLowerCase());
            chatRoomProxiesToReturn.add(chatRoomProxy);
        }
        return chatRoomProxiesToReturn.toArray(new ChatRoomPrx[chatRoomProxiesToReturn.size()]);
    }

    @Override
    public void registerChatRoomObject(String name, ChatRoomPrx chatRoomProxy, Current __current) throws ObjectExistsException {
        ChatRoomPrx existingProxy;
        this.requestCounter.add();
        name = name.toLowerCase();
        if (log.isDebugEnabled()) {
            log.debug((Object)("Registering the ChatRoom object '" + name + "'"));
        }
        if ((existingProxy = (ChatRoomPrx)this.roomMap.find(name)) != null) {
            log.warn((Object)("Registry " + this.applicationContext.getHostName() + ": Received request to register the Chat Room object '" + name + "' that is already registered"));
            throw new ObjectExistsException();
        }
        this.roomMap.register(name, chatRoomProxy);
        if (__current != null) {
            this.applicationContext.getRegistryNode().registerChatRoomObjectWithOtherRegistries(name, chatRoomProxy);
        }
    }

    @Override
    public void deregisterChatRoomObject(String name, Current __current) {
        ChatRoomPrx oldValue;
        this.requestCounter.add();
        name = name.toLowerCase();
        if (log.isDebugEnabled()) {
            log.debug((Object)("Deregistering the ChatRoom object '" + name + "'"));
        }
        if ((oldValue = (ChatRoomPrx)this.roomMap.deregister(name)) == null && log.isDebugEnabled()) {
            log.debug((Object)("The ChatRoom object '" + name + "' was not found to deregister"));
        }
        if (__current != null) {
            this.applicationContext.getRegistryNode().deregisterChatRoomObjectFromOtherRegistries(name);
        }
    }

    @Override
    public GroupChatPrx findGroupChatObject(String id, Current __current) throws ObjectNotFoundException {
        this.requestCounter.add();
        GroupChatPrx groupChatProxy = (GroupChatPrx)this.groupMap.find(id);
        if (groupChatProxy == null) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Unable to return a reference to the GroupChat object '" + id + "'"));
            }
            throw new ObjectNotFoundException();
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("Returning a reference to the GroupChat object '" + id + "'"));
        }
        return groupChatProxy;
    }

    @Override
    public void registerGroupChatObject(String id, GroupChatPrx groupChatProxy, Current __current) {
        GroupChatPrx existingProxy;
        this.requestCounter.add();
        if (log.isDebugEnabled()) {
            log.debug((Object)("Registering the GroupChat object '" + id + "'"));
        }
        if ((existingProxy = (GroupChatPrx)this.groupMap.find(id)) != null) {
            log.warn((Object)("The GroupChat object '" + id + "' is already registered. The old reference will be discarded."));
        }
        this.groupMap.register(id, groupChatProxy);
        if (__current != null) {
            this.applicationContext.getRegistryNode().registerGroupChatObjectWithOtherRegistries(id, groupChatProxy);
        }
    }

    @Override
    public void deregisterGroupChatObject(String id, Current __current) {
        GroupChatPrx oldValue;
        this.requestCounter.add();
        if (log.isDebugEnabled()) {
            log.debug((Object)("Deregistering the GroupChat object '" + id + "'"));
        }
        if ((oldValue = (GroupChatPrx)this.groupMap.deregister(id)) == null && log.isDebugEnabled()) {
            log.debug((Object)("The GroupChat object '" + id + "' was not found to deregister"));
        }
        if (__current != null) {
            this.applicationContext.getRegistryNode().deregisterGroupChatObjectFromOtherRegistries(id);
        }
    }

    @Override
    public BotServicePrx getLowestLoadedBotService(Current __current) throws ObjectNotFoundException {
        this.requestCounter.add();
        BotServiceRef lowestLoadedBotServiceRef = null;
        for (BotServiceRef botServiceRef : this.botServiceRefs.values()) {
            if (!botServiceRef.isOnline() || lowestLoadedBotServiceRef != null && botServiceRef.getLoad() >= lowestLoadedBotServiceRef.getLoad()) continue;
            lowestLoadedBotServiceRef = botServiceRef;
        }
        if (lowestLoadedBotServiceRef == null) {
            log.warn((Object)"Failed to find a lowest loaded BotService server");
            throw new ObjectNotFoundException();
        }
        return lowestLoadedBotServiceRef.getServiceProxy();
    }

    @Override
    public void registerBotService(String hostName, int load, BotServicePrx serviceProxy, BotServiceAdminPrx adminProxy, Current __current) {
        this.requestCounter.add();
        try {
            serviceProxy.ice_ping();
        }
        catch (LocalException e) {
            log.error((Object)("Failed to ping BotService [" + serviceProxy + "] that's attempting to register, ignoring request"));
            throw e;
        }
        BotServiceRef oldValue = this.botServiceRefs.put(hostName, new BotServiceRef(hostName, load, serviceProxy, adminProxy));
        if (oldValue == null) {
            log.info((Object)("New BotService [" + serviceProxy + "] registered"));
        } else {
            log.info((Object)("Connection to BotService [" + serviceProxy + "] restored"));
        }
        if (__current != null) {
            this.applicationContext.getRegistryNode().registerBotServiceWithOtherRegistries(hostName, load, serviceProxy, adminProxy);
        }
    }

    @Override
    public void deregisterBotService(String hostName, Current __current) {
        this.requestCounter.add();
        this.objectCacheRefs.remove(hostName);
        log.info((Object)("Bot Service " + hostName + " was deregistered"));
        if (__current != null) {
            this.applicationContext.getRegistryNode().deregisterBotServiceFromOtherRegistries(hostName);
        }
    }

    @Override
    public void sendAlertMessageToAllUsers(String message, String title, short timeout, Current __current) throws FusionException {
        for (ObjectCacheRef ref : this.objectCacheRefs.values()) {
            try {
                ObjectCachePrx objectCacheProxy = ObjectCachePrxHelper.uncheckedCast(ref.getCacheProxy().ice_timeout(this.iceInvalidProxyPurgerTimeout));
                objectCacheProxy.sendAlertMessageToAllUsers(message, title, timeout);
            }
            catch (LocalException e) {}
        }
    }

    @Override
    public int newGatewayID(Current __current) {
        return this.gatewayID.incrementAndGet();
    }

    @Override
    public int getUserCount(Current __current) {
        return this.userMap.size();
    }

    public RegistryStats getStats(RegistryStats stats) {
        if (stats == null) {
            stats = ServiceStatsFactory.getRegistryStats(this.applicationContext.getHostName(), Registry.startTime);
        }
        stats.numUserProxies = this.userMap.size();
        stats.maxUserProxies = this.userMap.getMaxProxies();
        stats.numConnectionProxies = this.connectionMap.size();
        stats.maxConnectionProxies = this.connectionMap.getMaxProxies();
        stats.numChatRoomProxies = this.roomMap.size();
        stats.maxChatRoomProxies = this.roomMap.getMaxProxies();
        stats.numGroupChatProxies = this.groupMap.size();
        stats.maxGroupChatProxies = this.groupMap.getMaxProxies();
        stats.objectCaches = "";
        Iterator<ObjectCacheRef> i = this.objectCacheRefs.values().iterator();
        while (i.hasNext()) {
            ObjectCacheRef ref = i.next();
            stats.objectCaches = stats.objectCaches + ref.getHostName();
            stats.objectCaches = ref.isOnline() ? stats.objectCaches + " (Load: " + ObjectCacheLoadBalancer.getLoad(ref.getStats()) + ")" : stats.objectCaches + " (OFFLINE)";
            if (!i.hasNext()) continue;
            stats.objectCaches = stats.objectCaches + "; ";
        }
        return stats;
    }

    public ConcurrentMap<String, UserPrx> getUserProxies() {
        return this.userMap.getProxies();
    }

    public ConcurrentMap<String, ConnectionPrx> getConnectionProxies() {
        return this.connectionMap.getProxies();
    }

    public ConcurrentMap<String, ChatRoomPrx> getChatRoomProxies() {
        return this.roomMap.getProxies();
    }

    @Override
    public void registerMessageSwitchboard(String uniqueID, MessageSwitchboardPrx msbProxy, MessageSwitchboardAdminPrx adminProxy, Current __current) {
        this.requestCounter.add();
        if (this.messageSwitchboardRefs.get(uniqueID) == null) {
            log.info((Object)("New MessageSwitchboard [" + msbProxy + "] registered"));
        } else {
            log.info((Object)("Connection to [" + msbProxy + "] restored"));
        }
        try {
            msbProxy.ice_ping();
        }
        catch (LocalException e) {
            log.error((Object)("Failed to ping MessageSwitchboard [" + msbProxy + "] that's attempting to register, ignoring request"));
            throw e;
        }
        this.messageSwitchboardRefs.put(uniqueID, new MessageSwitchboardRef(uniqueID, msbProxy, adminProxy));
        if (__current != null) {
            this.applicationContext.getRegistryNode().registerMessageSwitchboardWithOtherRegistries(uniqueID, msbProxy, adminProxy);
        }
    }

    @Override
    public void deregisterMessageSwitchboard(String uniqueID, Current __current) {
        this.requestCounter.add();
        this.messageSwitchboardRefs.remove(uniqueID);
        log.info((Object)("MessageSwitchboard " + uniqueID + " was deregistered"));
        if (__current != null) {
            this.applicationContext.getRegistryNode().deregisterMessageSwitchboardFromOtherRegistries(uniqueID);
        }
    }

    @Override
    public void getMessageSwitchboard_async(final AMD_Registry_getMessageSwitchboard cb, Current __current) throws FusionException {
        if (SystemProperty.getBool(SystemPropertyEntities.ChatSyncSettings.STANDALONE_MESSAGE_SWITCHBOARD_ENABLED)) {
            int random = new Random(System.currentTimeMillis()).nextInt(this.messageSwitchboardRefs.size());
            Collection<MessageSwitchboardRef> values = this.messageSwitchboardRefs.values();
            MessageSwitchboardRef[] arr = values.toArray(new MessageSwitchboardRef[values.size()]);
            cb.ice_response(arr[random].getProxy());
        } else if (SystemProperty.getBool(SystemPropertyEntities.RegistrySettings.USE_AMD_AMI_GET_MESSAGE_SWITCHBOARD)) {
            try {
                RegistryThreadPoolExecutor.schedule(new Runnable(){

                    public void run() {
                        try {
                            RegistryI.this.getLowestLoadedObjectCache().getMessageSwitchboard_async(new AMI_ObjectCache_getMessageSwitchboard(){

                                public void ice_response(MessageSwitchboardPrx result) {
                                    cb.ice_response(result);
                                }

                                public void ice_exception(UserException ex) {
                                    cb.ice_exception((Exception)ex);
                                }

                                public void ice_exception(LocalException ex) {
                                    cb.ice_exception((Exception)((Object)ex));
                                }
                            });
                        }
                        catch (FusionException ex) {
                            cb.ice_exception((Exception)((Object)ex));
                        }
                    }
                });
            }
            catch (Exception e) {
                log.error((Object)("Exception while scheduling getMessageSwitchboard task: " + e.getMessage()), (Throwable)e);
                cb.ice_exception(e);
            }
        } else {
            cb.ice_response(this.getLowestLoadedObjectCache().getMessageSwitchboard());
        }
    }

    private class BotServicePinger
    extends TimerTask {
        private BotServicePinger() {
        }

        public void run() {
            for (BotServiceRef botServiceRef : RegistryI.this.botServiceRefs.values()) {
                try {
                    int load = botServiceRef.getAdminProxy().ping();
                    botServiceRef.setLoad(load);
                    if (botServiceRef.isOnline()) continue;
                    log.info((Object)("Connection to BotService [" + botServiceRef.getServiceProxy() + "] restored"));
                    botServiceRef.setOnline(true);
                }
                catch (LocalException e) {
                    if (!botServiceRef.isOnline()) continue;
                    log.warn((Object)("Have lost connection to BotService [" + botServiceRef.getServiceProxy() + "]"), (Throwable)e);
                    botServiceRef.setOnline(false);
                }
            }
        }
    }

    private class ObjectCachePinger
    extends TimerTask {
        private ObjectCachePinger() {
        }

        public void run() {
            for (ObjectCacheRef objectCacheRef : RegistryI.this.objectCacheRefs.values()) {
                try {
                    objectCacheRef.getAdminProxy().ice_timeout(RegistryI.this.iceObjectCachePingerTimeout).ice_ping();
                    if (objectCacheRef.isOnline()) continue;
                    log.info((Object)("Connection to [" + objectCacheRef.getCacheProxy() + "] restored"));
                    objectCacheRef.setOnline(true);
                }
                catch (LocalException e) {
                    if (objectCacheRef.isOnline()) {
                        log.warn((Object)("Have lost connection to [" + objectCacheRef.getCacheProxy() + "]"), (Throwable)e);
                        objectCacheRef.setOnline(false);
                        continue;
                    }
                    if (!log.isDebugEnabled()) continue;
                    log.debug((Object)("object cache [" + objectCacheRef.getAdminProxy() + "] is still offline"));
                }
            }
        }
    }

    private class InvalidProxyPurger
    extends Thread {
        public InvalidProxyPurger() {
            this.setName("InvalidProxyPurger");
        }

        public void run() {
            long purgeInterval = RegistryI.this.applicationContext.getProperties().getPropertyAsIntWithDefault("InvalidProxyPurgeInterval", 300) * 1000;
            while (true) {
                try {
                    while (true) {
                        try {
                            InvalidProxyPurger.sleep(purgeInterval);
                        }
                        catch (InterruptedException ignored) {
                            // empty catch block
                        }
                        if (log.isDebugEnabled()) {
                            log.debug((Object)"Purging invalid object references...");
                        }
                        RegistryI.this.userMap.purgeExpired();
                        RegistryI.this.connectionMap.purgeExpired();
                        RegistryI.this.roomMap.purgeExpired();
                        RegistryI.this.groupMap.purgeExpired();
                        if (!log.isDebugEnabled()) continue;
                        log.debug((Object)"Purge complete");
                    }
                }
                catch (Exception e) {
                    log.error((Object)"InvalidProxyPurger thread FAILED", (Throwable)e);
                    continue;
                }
                break;
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private class GroupMap
    extends ProxyMap<GroupChatPrx> {
        private GroupMap() {
        }

        @Override
        public void purgeExpired(Set<Map.Entry<String, GroupChatPrx>> entries) {
            for (Map.Entry<String, GroupChatPrx> group : entries) {
                String name = group.getKey();
                try {
                    GroupChatPrx prx = group.getValue();
                    if (prx == null) continue;
                    prx.ice_timeout(RegistryI.this.iceInvalidProxyPurgerTimeout).ice_ping();
                }
                catch (LocalException e) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("Purging the invalid ChatGroup object proxy '" + name + "'"));
                    }
                    this.remove(name);
                    RegistryI.this.applicationContext.getRegistryNode().deregisterGroupChatObjectFromOtherRegistries(name);
                }
                catch (Exception e) {
                    log.warn((Object)("Exception purging the invalid ChatGroup object proxy '" + name + "'"), (Throwable)e);
                    this.remove(name);
                    RegistryI.this.applicationContext.getRegistryNode().deregisterGroupChatObjectFromOtherRegistries(name);
                }
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private class RoomMap
    extends ProxyMap<ChatRoomPrx> {
        private RoomMap() {
        }

        @Override
        public void purgeExpired(Set<Map.Entry<String, ChatRoomPrx>> entries) {
            for (Map.Entry<String, ChatRoomPrx> room : entries) {
                String name = room.getKey();
                try {
                    ChatRoomPrx prx = room.getValue();
                    if (prx == null) continue;
                    prx.ice_timeout(RegistryI.this.iceInvalidProxyPurgerTimeout).ice_ping();
                }
                catch (LocalException e) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("Purging the invalid ChatRoom object proxy '" + name + "'"));
                    }
                    this.remove(name);
                    RegistryI.this.applicationContext.getRegistryNode().deregisterChatRoomObjectFromOtherRegistries(name);
                }
                catch (Exception e) {
                    log.warn((Object)("Exception purging the invalid ChatRoom object proxy '" + name + "'"), (Throwable)e);
                    this.remove(name);
                    RegistryI.this.applicationContext.getRegistryNode().deregisterChatRoomObjectFromOtherRegistries(name);
                }
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private class ConnectionMap
    extends ProxyMap<ConnectionPrx> {
        private ConnectionMap() {
        }

        @Override
        public void purgeExpired(Set<Map.Entry<String, ConnectionPrx>> entries) {
            for (Map.Entry<String, ConnectionPrx> session : entries) {
                String sessionID = session.getKey();
                try {
                    ConnectionPrx prx = session.getValue();
                    if (prx == null) continue;
                    prx.ice_timeout(RegistryI.this.iceInvalidProxyPurgerTimeout).ice_ping();
                }
                catch (LocalException e) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("Purging the invalid Connection object proxy '" + sessionID + "'"));
                    }
                    this.remove(sessionID);
                    RegistryI.this.applicationContext.getRegistryNode().deregisterConnectionObjectFromOtherRegistries(sessionID);
                }
                catch (Exception e) {
                    log.warn((Object)("Exception purging the invalid Connection object proxy '" + sessionID + "'"), (Throwable)e);
                    this.remove(sessionID);
                    RegistryI.this.applicationContext.getRegistryNode().deregisterConnectionObjectFromOtherRegistries(sessionID);
                }
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private class UserMap
    extends ProxyMap<UserPrx> {
        private UserMap() {
        }

        @Override
        public void purgeExpired(Set<Map.Entry<String, UserPrx>> entries) {
            for (Map.Entry<String, UserPrx> user : entries) {
                String username = user.getKey();
                try {
                    UserPrx prx = user.getValue();
                    if (prx == null) continue;
                    prx.ice_timeout(RegistryI.this.iceInvalidProxyPurgerTimeout).ice_ping();
                }
                catch (LocalException e) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("Purging the invalid User object proxy '" + username + "'"));
                    }
                    this.remove(username);
                    RegistryI.this.applicationContext.getRegistryNode().deregisterUserObjectFromOtherRegistries(username, null);
                }
                catch (Exception e) {
                    log.warn((Object)("Exception purging the invalid User object proxy '" + username + "'"), (Throwable)e);
                    this.remove(username);
                    RegistryI.this.applicationContext.getRegistryNode().deregisterUserObjectFromOtherRegistries(username, null);
                }
            }
        }
    }
}

