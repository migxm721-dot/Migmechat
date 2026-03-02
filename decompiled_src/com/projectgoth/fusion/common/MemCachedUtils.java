/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.danga.MemCached.MemCachedClient
 *  com.danga.MemCached.SockIOPool
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.common;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.StringUtil;
import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;

public class MemCachedUtils {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(MemCachedUtils.class));
    private static final String PROPERTIES_FILENAME = "memcached.properties";
    public static final String NAMESPACE_SEPERATOR = "/";
    public static final String MEMCACHED_LOCK_PREFIX = "lock:";
    public static final int MEMCACHED_LOCK_TIME = 5000;
    private static Set<String> poolNames = new HashSet<String>();
    private static String fusionNamespace = "F";
    private static Properties properties;
    private static final Map<Instance, MemCachedClient> clients;

    private static void parsePoolNames() {
        String names = properties.getProperty("pools");
        StringTokenizer tokenizer = new StringTokenizer(names, ",");
        while (tokenizer.hasMoreTokens()) {
            poolNames.add(tokenizer.nextToken().trim());
        }
        log.info((Object)("memcached.properties has " + poolNames.size() + " indicated pool names"));
    }

    private static MemCachedClient configureClient(String poolName) {
        MemCachedClient memcachedClient;
        log.info((Object)("Configuring memcached client for pool [" + poolName + "]"));
        try {
            memcachedClient = new MemCachedClient(poolName);
            memcachedClient.setCompressEnable(true);
            memcachedClient.setCompressThreshold(16384L);
            if (poolName.equals("common") || poolName.equals("rateLimit")) {
                memcachedClient.setPrimitiveAsString(true);
            }
            if (poolName.equals("common") || poolName.equals("rateLimit") || poolName.equals("surgeMail")) {
                memcachedClient.setSanitizeKeys(false);
            }
        }
        catch (Exception e) {
            log.error((Object)"Failed to setup memcached client", (Throwable)e);
            return null;
        }
        return memcachedClient;
    }

    private static String[] getServersForPool(String poolName) {
        int numServers = Integer.parseInt(properties.getProperty(poolName + ".numServers"));
        String[] servers = new String[numServers];
        for (int i = 1; i <= numServers; ++i) {
            servers[i - 1] = properties.getProperty(poolName + ".server" + i);
        }
        return servers;
    }

    private static SockIOPool configurePool(String poolName) {
        log.info((Object)"reloading properties");
        MemCachedUtils.loadProperties();
        log.info((Object)("Creating pool [" + poolName + "]"));
        SockIOPool pool = SockIOPool.getInstance((String)poolName);
        pool.setServers(MemCachedUtils.getServersForPool(poolName));
        pool.setInitConn(Integer.parseInt(properties.getProperty("pool.initialConnections", "2")));
        pool.setMinConn(Integer.parseInt(properties.getProperty("pool.minConnections", "2")));
        pool.setMaxConn(Integer.parseInt(properties.getProperty("pool.maxConnections", "25")));
        pool.setMaxIdle((long)(1000 * Integer.parseInt(properties.getProperty("pool.maxIdleTime", "21600"))));
        pool.setMaintSleep((long)(1000 * Integer.parseInt(properties.getProperty("maintenanceThread.sleep", "30"))));
        pool.setNagle(false);
        pool.setSocketTO(1000 * Integer.parseInt(properties.getProperty("socket.readTimeout", "3")));
        pool.setSocketConnectTO(1000 * Integer.parseInt(properties.getProperty("socket.connectTimeout", "3")));
        pool.setHashingAlg(0);
        if (poolName.equals("common") || poolName.equals("surgeMail") || poolName.equals("rateLimit")) {
            pool.setHashingAlg(2);
        }
        pool.setFailover(StringUtil.toBooleanOrDefault(properties.getProperty("pool.failover"), false));
        pool.initialize();
        return pool;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static MemCachedClient getMemCachedClient(Instance pool) {
        Instance instance = pool;
        synchronized (instance) {
            MemCachedClient client = null;
            try {
                client = clients.get((Object)pool);
                if (client != null) {
                    return client;
                }
                log.info((Object)("configuring client [" + pool.toString() + "]"));
                MemCachedUtils.configurePool(pool.toString());
                client = MemCachedUtils.configureClient(pool.toString());
                clients.put(pool, client);
            }
            catch (Exception e) {
                log.fatal((Object)("Unable to get client:" + (Object)((Object)pool) + " from map"));
            }
            return client;
        }
    }

    public static String getFusionNamespace() {
        return fusionNamespace;
    }

    public static String getNamespace(String prefix) {
        return MemCachedUtils.getFusionNamespace() + NAMESPACE_SEPERATOR + prefix + NAMESPACE_SEPERATOR;
    }

    public static String getCacheKeyInNamespace(String prefix, String key) {
        return MemCachedUtils.getNamespace(prefix) + key;
    }

    public static boolean getLock(MemCachedClient memcache, String namespace, String key, int retries) {
        int count = 0;
        boolean gotLock = false;
        while (!(gotLock = memcache.add(MemCachedUtils.getCacheKeyInNamespace(namespace, MEMCACHED_LOCK_PREFIX + key), (Object)"locked", new Date(System.currentTimeMillis() + 5000L))) && count < retries) {
            try {
                ++count;
                Thread.sleep(1000L);
            }
            catch (InterruptedException e) {}
        }
        return gotLock;
    }

    public static boolean releaseLock(MemCachedClient memcache, String namespace, String key) {
        return memcache.delete(MemCachedUtils.getCacheKeyInNamespace(namespace, MEMCACHED_LOCK_PREFIX + key));
    }

    private static boolean loadProperties() {
        properties = new Properties();
        String propertiesLocation = ConfigUtils.getConfigDirectory() + PROPERTIES_FILENAME;
        try {
            FileInputStream inputStream = new FileInputStream(new File(propertiesLocation));
            properties.load(inputStream);
            if (properties.size() > 0) {
                return true;
            }
        }
        catch (Exception e) {
            log.fatal((Object)("Failed to load memcached.properties, it should be at [" + propertiesLocation + "], aborting."), (Throwable)e);
        }
        return false;
    }

    public static void main(String[] args) {
        MemCachedClient client1 = MemCachedUtils.getMemCachedClient(Instance.contactList);
        MemCachedClient client2 = MemCachedUtils.getMemCachedClient(Instance.broadcastList);
        assert (client1 != client2);
        System.out.println("client1:");
        System.out.println(client1.stats());
        System.out.println("client2:");
        System.out.println(client2.stats());
        client1.set("key1", (Object)"value");
        client2.set("key1", (Object)"othervalue");
        System.out.println(client1.get("key1"));
        System.out.println(client2.get("key1"));
        assert (!client1.get("key1").equals(client2.get("key2")));
        System.out.println("exited normally");
    }

    static {
        clients = new HashMap<Instance, MemCachedClient>();
        if (properties != null && properties.containsKey("disable")) {
            log.warn((Object)("MEMCACHED DISABLED BY 'disable' KEY IN " + ConfigUtils.getConfigDirectory() + PROPERTIES_FILENAME));
        }
        if (MemCachedUtils.loadProperties() && !properties.containsKey("disable")) {
            fusionNamespace = properties.getProperty("fusionNamespace", "F");
            MemCachedUtils.parsePoolNames();
            for (String poolName : poolNames) {
                clients.put(Instance.valueOf(poolName), null);
            }
            log.info((Object)("configured " + clients.size() + " dummy memcached clients"));
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Instance {
        contactList,
        broadcastList,
        profiles,
        userDisplayPictureAndStatus,
        captcha,
        common,
        rateLimit,
        bclPersisted,
        surgeMail,
        recentChatRooms,
        chatRoomSearch,
        authenticationService;

    }
}

