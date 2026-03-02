/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 *  Ice.LocalException
 *  Ice.ObjectPrx
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.registry;

import Ice.Current;
import Ice.LocalException;
import Ice.ObjectPrx;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.registry.ObjectCacheRef;
import com.projectgoth.fusion.registry.Registry;
import com.projectgoth.fusion.registry.RegistryContext;
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
import com.projectgoth.fusion.slice.RegistryNodePrx;
import com.projectgoth.fusion.slice.RegistryNodePrxHelper;
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.slice._RegistryNodeDisp;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.log4j.Logger;

public class RegistryNodeI
extends _RegistryNodeDisp {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(RegistryNodeI.class));
    public ConcurrentHashMap<String, RegistryNodePrx> otherRegistries = new ConcurrentHashMap();
    private RegistryContext applicationContext;

    public RegistryNodeI(RegistryContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void convergeWithCluster() throws Exception {
        int numOtherRegistries = this.applicationContext.getProperties().getPropertyAsInt("NumOtherRegistries");
        boolean connectedToFirstNode = false;
        log.info((Object)"Converging with cluster");
        for (int i = 1; i <= numOtherRegistries; ++i) {
            String otherRegistryHostName = this.applicationContext.getProperties().getProperty("OtherRegistry" + i).toUpperCase();
            String registryStringifiedProxy = "RegistryNode:tcp -h " + otherRegistryHostName + " -p 8000";
            if (registryStringifiedProxy.length() <= 0) continue;
            RegistryNodePrx otherRegistryNodePrx = null;
            log.info((Object)("Connecting to the Registry on " + otherRegistryHostName));
            ObjectPrx basePrx = Registry.communicator().stringToProxy(registryStringifiedProxy);
            try {
                otherRegistryNodePrx = RegistryNodePrxHelper.checkedCast(basePrx);
            }
            catch (LocalException e) {
                log.warn((Object)("Connection to the Registry on " + otherRegistryHostName + " failed: " + e.toString()));
            }
            if (otherRegistryNodePrx == null) continue;
            if (!connectedToFirstNode) {
                log.info((Object)("Replicating from the Registry on " + otherRegistryHostName));
                try {
                    otherRegistryHostName = otherRegistryNodePrx.registerNewNode(this.applicationContext.getThisNodePrx(), this.applicationContext.getHostName(), true);
                }
                catch (FusionException fe) {
                    log.fatal((Object)("Registry " + this.applicationContext.getHostName() + ": Replication from Registry on " + otherRegistryHostName + " failed. Exception: " + fe.message));
                    throw new Exception();
                }
                connectedToFirstNode = true;
                log.info((Object)("Replication from " + otherRegistryHostName + " complete"));
            } else {
                try {
                    otherRegistryHostName = otherRegistryNodePrx.registerNewNode(this.applicationContext.getThisNodePrx(), this.applicationContext.getHostName(), false);
                }
                catch (FusionException fe) {
                    // empty catch block
                }
            }
            this.otherRegistries.put(otherRegistryHostName, otherRegistryNodePrx);
        }
        if (this.otherRegistries.size() == 1) {
            log.info((Object)"Converged with 1 other Registry");
        } else {
            log.info((Object)("Converged with " + this.otherRegistries.size() + " other Registries"));
        }
    }

    public void registerObjectCacheStats(String objectCacheUniqueID, ObjectCacheStats stats, Current __current) throws ObjectNotFoundException {
        this.applicationContext.getRegistry().registerObjectCacheStats(objectCacheUniqueID, stats);
    }

    public void registerUserObject(String username, UserPrx userProxy, String objectCacheHostname, Current __current) throws ObjectExistsException {
        this.applicationContext.getRegistry().registerUserObject(username, userProxy, objectCacheHostname, null);
    }

    public void deregisterUserObject(String username, String objectCacheHostname, Current __current) {
        this.applicationContext.getRegistry().deregisterUserObject(username, objectCacheHostname, null);
    }

    public void registerObjectCache(String hostName, ObjectCachePrx cacheProxy, ObjectCacheAdminPrx adminProxy, Current __current) {
        this.applicationContext.getRegistry().registerObjectCache(hostName, cacheProxy, adminProxy, null);
    }

    public void deregisterObjectCache(String hostName, Current __current) {
        this.applicationContext.getRegistry().deregisterObjectCache(hostName, null);
    }

    public void registerBotService(String hostName, int load, BotServicePrx serviceProxy, BotServiceAdminPrx adminProxy, Current __current) {
        this.applicationContext.getRegistry().registerBotService(hostName, load, serviceProxy, adminProxy, null);
    }

    public void deregisterBotService(String hostName, Current __current) {
        this.applicationContext.getRegistry().deregisterBotService(hostName, null);
    }

    public String registerNewNode(RegistryNodePrx otherRegistryNodeProxy, String otherRegistryHostName, boolean replicate, Current __current) throws FusionException {
        log.info((Object)("Converging with new registry node " + otherRegistryHostName));
        if (replicate) {
            log.info((Object)("Replicating " + this.applicationContext.getRegistry().objectCacheRefs.size() + " Object Cache references"));
            for (ObjectCacheRef objectCacheRef : this.applicationContext.getRegistry().objectCacheRefs.values()) {
                otherRegistryNodeProxy.registerObjectCache(objectCacheRef.getHostName(), objectCacheRef.getCacheProxy(), objectCacheRef.getAdminProxy());
            }
            ConcurrentMap<String, UserPrx> userProxies = this.applicationContext.getRegistry().getUserProxies();
            log.info((Object)("Replicating " + userProxies.size() + " User object proxies"));
            for (Map.Entry user : userProxies.entrySet()) {
                try {
                    otherRegistryNodeProxy.registerUserObject((String)user.getKey(), (UserPrx)user.getValue(), null);
                }
                catch (ObjectExistsException e) {
                    log.warn((Object)("Replication failed. Caught ObjectExistsException when registering the User object '" + (String)user.getKey() + "'"));
                    FusionException fe = new FusionException();
                    fe.message = this.applicationContext.getHostName() + ": Caught ObjectExistsException when registering the User object '" + (String)user.getKey() + "'";
                    throw fe;
                }
            }
            ConcurrentMap<String, ConnectionPrx> connectionProxies = this.applicationContext.getRegistry().getConnectionProxies();
            log.info((Object)("Replicating " + connectionProxies.size() + " Connection object proxies"));
            for (Map.Entry session : connectionProxies.entrySet()) {
                try {
                    otherRegistryNodeProxy.registerConnectionObject((String)session.getKey(), (ConnectionPrx)session.getValue());
                }
                catch (ObjectExistsException e) {
                    log.warn((Object)("Replication failed. Caught ObjectExistsException when registering the Connection object '" + (String)session.getKey() + "'"));
                    FusionException fe = new FusionException();
                    fe.message = this.applicationContext.getHostName() + ": Caught ObjectExistsException when registering the Connection object '" + (String)session.getKey() + "'";
                    throw fe;
                }
            }
            ConcurrentMap<String, ChatRoomPrx> chatRoomProxies = this.applicationContext.getRegistry().getChatRoomProxies();
            log.info((Object)("Replicating " + chatRoomProxies.size() + " ChatRoom object proxies"));
            for (Map.Entry room : chatRoomProxies.entrySet()) {
                try {
                    otherRegistryNodeProxy.registerChatRoomObject((String)room.getKey(), (ChatRoomPrx)room.getValue());
                }
                catch (Exception e) {
                    log.warn((Object)("Replication failed. Caught Exception when registering the ChatRoom object '" + (String)room.getKey() + "': " + e.toString()));
                    FusionException fe = new FusionException();
                    fe.message = this.applicationContext.getHostName() + ": Caught Exception when registering the ChatRoom object '" + (String)room.getKey() + "': " + e.toString();
                    throw fe;
                }
            }
            log.info((Object)("Replication to " + otherRegistryHostName + " complete"));
        }
        this.otherRegistries.put(otherRegistryHostName, otherRegistryNodeProxy);
        return this.applicationContext.getHostName();
    }

    public void registerObjectCacheStatsWithOtherRegistries(String objectCacheUniqueID, ObjectCacheStats stats) throws ObjectNotFoundException {
        if (this.otherRegistries.size() == 0) {
            return;
        }
        Iterator<Map.Entry<String, RegistryNodePrx>> i = this.otherRegistries.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, RegistryNodePrx> node = i.next();
            try {
                node.getValue().registerObjectCacheStats(objectCacheUniqueID, stats);
            }
            catch (LocalException e) {
                log.warn((Object)("Have lost connection to the Registry on " + node.getKey()));
                i.remove();
            }
        }
    }

    public void registerUserObjectWithOtherRegistries(String username, UserPrx userProxy, String objectCacheHostname) {
        if (this.otherRegistries.size() == 0) {
            return;
        }
        Iterator<Map.Entry<String, RegistryNodePrx>> i = this.otherRegistries.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, RegistryNodePrx> node = i.next();
            try {
                node.getValue().registerUserObject(username, userProxy, objectCacheHostname);
            }
            catch (ObjectExistsException e) {
                log.error((Object)("Registry " + this.applicationContext.getHostName() + ": Caught ObjectExistsException when registering User object '" + username + "' with the Registry on " + node.getKey() + ". THE REGISTRIES ARE NO LONGER IN A CONSISTENT STATE."));
            }
            catch (LocalException e) {
                log.warn((Object)("Have lost connection to the Registry on " + node.getKey()));
                i.remove();
            }
        }
    }

    public void deregisterUserObjectFromOtherRegistries(String username, String objectCacheHostname) {
        if (this.otherRegistries.size() == 0) {
            return;
        }
        Iterator<Map.Entry<String, RegistryNodePrx>> i = this.otherRegistries.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, RegistryNodePrx> node = i.next();
            try {
                node.getValue().deregisterUserObject(username, objectCacheHostname);
            }
            catch (LocalException e) {
                log.warn((Object)("Have lost connection to the Registry on " + node.getKey()));
                i.remove();
            }
        }
    }

    public void registerConnectionObjectWithOtherRegistries(String sessionID, ConnectionPrx connectionProxy) {
        if (this.otherRegistries.size() == 0) {
            return;
        }
        Iterator<Map.Entry<String, RegistryNodePrx>> i = this.otherRegistries.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, RegistryNodePrx> node = i.next();
            try {
                node.getValue().registerConnectionObject(sessionID, connectionProxy);
            }
            catch (ObjectExistsException e) {
                log.error((Object)("Registry " + this.applicationContext.getHostName() + ": Caught ObjectExistsException when registering the Connection object '" + sessionID + "' with the Registry on " + node.getKey() + ". THE REGISTRIES ARE NO LONGER IN A CONSISTENT STATE."));
            }
            catch (LocalException e) {
                log.warn((Object)("Have lost connection to the Registry on " + node.getKey()));
                i.remove();
            }
        }
    }

    public void deregisterConnectionObjectFromOtherRegistries(String sessionID) {
        if (this.otherRegistries.size() == 0) {
            return;
        }
        Iterator<Map.Entry<String, RegistryNodePrx>> i = this.otherRegistries.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, RegistryNodePrx> node = i.next();
            try {
                node.getValue().deregisterConnectionObject(sessionID);
            }
            catch (LocalException e) {
                log.warn((Object)("Have lost connection to the Registry on " + node.getKey()));
                i.remove();
            }
        }
    }

    public void registerObjectCacheWithOtherRegistries(String hostName, ObjectCachePrx cacheProxy, ObjectCacheAdminPrx adminProxy) {
        if (this.otherRegistries.size() == 0) {
            return;
        }
        Iterator<Map.Entry<String, RegistryNodePrx>> i = this.otherRegistries.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, RegistryNodePrx> node = i.next();
            try {
                node.getValue().registerObjectCache(hostName, cacheProxy, adminProxy);
            }
            catch (LocalException e) {
                log.warn((Object)("Have lost connection to the Registry on " + node.getKey()));
                i.remove();
            }
        }
    }

    public void deregisterObjectCacheFromOtherRegistries(String hostName) {
        if (this.otherRegistries.size() == 0) {
            return;
        }
        Iterator<Map.Entry<String, RegistryNodePrx>> i = this.otherRegistries.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, RegistryNodePrx> node = i.next();
            try {
                node.getValue().deregisterObjectCache(hostName);
            }
            catch (LocalException e) {
                log.warn((Object)("Have lost connection to the Registry on " + node.getKey()));
                i.remove();
            }
        }
    }

    public void registerBotServiceWithOtherRegistries(String hostName, int load, BotServicePrx serviceProxy, BotServiceAdminPrx adminProxy) {
        if (this.otherRegistries.size() == 0) {
            return;
        }
        Iterator<Map.Entry<String, RegistryNodePrx>> i = this.otherRegistries.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, RegistryNodePrx> node = i.next();
            try {
                node.getValue().registerBotService(hostName, load, serviceProxy, adminProxy);
            }
            catch (LocalException e) {
                log.warn((Object)("Have lost connection to the Registry on " + node.getKey()));
                i.remove();
            }
        }
    }

    public void deregisterBotServiceFromOtherRegistries(String hostName) {
        if (this.otherRegistries.size() == 0) {
            return;
        }
        Iterator<Map.Entry<String, RegistryNodePrx>> i = this.otherRegistries.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, RegistryNodePrx> node = i.next();
            try {
                node.getValue().deregisterBotService(hostName);
            }
            catch (LocalException e) {
                log.warn((Object)("Have lost connection to the Registry on " + node.getKey()));
                i.remove();
            }
        }
    }

    public void registerConnectionObject(String sessionID, ConnectionPrx connectionProxy, Current __current) throws ObjectExistsException {
        this.applicationContext.getRegistry().registerConnectionObject(sessionID, connectionProxy, null);
    }

    public void deregisterConnectionObject(String sessionID, Current __current) {
        this.applicationContext.getRegistry().deregisterConnectionObject(sessionID, null);
    }

    public void registerChatRoomObjectWithOtherRegistries(String name, ChatRoomPrx chatRoomProxy) {
        if (this.otherRegistries.size() == 0) {
            return;
        }
        Iterator<Map.Entry<String, RegistryNodePrx>> i = this.otherRegistries.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, RegistryNodePrx> node = i.next();
            try {
                node.getValue().registerChatRoomObject(name, chatRoomProxy);
            }
            catch (ObjectExistsException e) {
                log.error((Object)("Registry " + this.applicationContext.getHostName() + ": Caught ObjectExistsException when registering the ChatRoom object '" + name + "' with the Registry on " + node.getKey() + ". THE REGISTRIES ARE NO LONGER IN A CONSISTENT STATE."));
            }
            catch (LocalException e) {
                log.warn((Object)("Have lost connection to the Registry on " + node.getKey()));
                i.remove();
            }
        }
    }

    public void deregisterChatRoomObjectFromOtherRegistries(String name) {
        if (this.otherRegistries.size() == 0) {
            return;
        }
        Iterator<Map.Entry<String, RegistryNodePrx>> i = this.otherRegistries.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, RegistryNodePrx> node = i.next();
            try {
                node.getValue().deregisterChatRoomObject(name);
            }
            catch (LocalException e) {
                log.warn((Object)("Have lost connection to the Registry on " + node.getKey()));
                i.remove();
            }
        }
    }

    public void registerGroupChatObjectWithOtherRegistries(String id, GroupChatPrx groupChatProxy) {
        if (this.otherRegistries.size() == 0) {
            return;
        }
        Iterator<Map.Entry<String, RegistryNodePrx>> i = this.otherRegistries.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, RegistryNodePrx> node = i.next();
            try {
                node.getValue().registerGroupChatObject(id, groupChatProxy);
            }
            catch (LocalException e) {
                log.warn((Object)("Have lost connection to the Registry on " + node.getKey()));
                i.remove();
            }
        }
    }

    public void deregisterGroupChatObjectFromOtherRegistries(String id) {
        if (this.otherRegistries.size() == 0) {
            return;
        }
        Iterator<Map.Entry<String, RegistryNodePrx>> i = this.otherRegistries.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, RegistryNodePrx> node = i.next();
            try {
                node.getValue().deregisterGroupChatObject(id);
            }
            catch (LocalException e) {
                log.warn((Object)("Have lost connection to the Registry on " + node.getKey()));
                i.remove();
            }
        }
    }

    public void registerChatRoomObject(String name, ChatRoomPrx chatRoomProxy, Current __current) throws ObjectExistsException {
        this.applicationContext.getRegistry().registerChatRoomObject(name, chatRoomProxy, null);
    }

    public void deregisterChatRoomObject(String name, Current __current) {
        this.applicationContext.getRegistry().deregisterChatRoomObject(name, null);
    }

    public void registerGroupChatObject(String id, GroupChatPrx groupChatProxy, Current __current) {
        this.applicationContext.getRegistry().registerGroupChatObject(id, groupChatProxy, null);
    }

    public void deregisterGroupChatObject(String id, Current __current) {
        this.applicationContext.getRegistry().deregisterGroupChatObject(id, null);
    }

    public void registerMessageSwitchboardWithOtherRegistries(String hostName, MessageSwitchboardPrx msbProxy, MessageSwitchboardAdminPrx adminProxy) {
        if (this.otherRegistries.size() == 0) {
            return;
        }
        Iterator<Map.Entry<String, RegistryNodePrx>> i = this.otherRegistries.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, RegistryNodePrx> node = i.next();
            try {
                node.getValue().registerMessageSwitchboard(hostName, msbProxy, adminProxy);
            }
            catch (LocalException e) {
                log.warn((Object)("Have lost connection to the Registry on " + node.getKey()));
                i.remove();
            }
        }
    }

    public void deregisterMessageSwitchboardFromOtherRegistries(String hostName) {
        if (this.otherRegistries.size() == 0) {
            return;
        }
        Iterator<Map.Entry<String, RegistryNodePrx>> i = this.otherRegistries.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, RegistryNodePrx> node = i.next();
            try {
                node.getValue().deregisterMessageSwitchboard(hostName);
            }
            catch (LocalException e) {
                log.warn((Object)("Have lost connection to the Registry on " + node.getKey()));
                i.remove();
            }
        }
    }

    public void registerMessageSwitchboard(String hostName, MessageSwitchboardPrx msbProxy, MessageSwitchboardAdminPrx adminProxy, Current __current) {
        this.applicationContext.getRegistry().registerMessageSwitchboard(hostName, msbProxy, adminProxy, null);
    }

    public void deregisterMessageSwitchboard(String hostName, Current __current) {
        this.applicationContext.getRegistry().deregisterMessageSwitchboard(hostName, null);
    }
}

