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
import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.Properties;
import org.apache.log4j.Logger;

public class Membase {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(Membase.class));
    private static final String KEY_SEPERATOR = "/";
    private static final String URL_ENCODED_SPACE = "%20";
    private static final String PROPERTIES_FILENAME = "membase.properties";
    private static Properties properties;
    private static MemCachedClient membaseClient;

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
            log.fatal((Object)("Failed to load membase.properties, it should be at [" + propertiesLocation + "], aborting."), (Throwable)e);
        }
        return false;
    }

    public static boolean add(KeySpace keySpace, String key, Object value) {
        if (keySpace.expires()) {
            return membaseClient.add(keySpace.getFullKey(key), value, keySpace.getExpiryDate());
        }
        return membaseClient.add(keySpace.getFullKey(key), value);
    }

    public static boolean set(KeySpace keySpace, String key, Object value) {
        if (keySpace.expires()) {
            return membaseClient.set(keySpace.getFullKey(key), value, keySpace.getExpiryDate());
        }
        return membaseClient.set(keySpace.getFullKey(key), value);
    }

    public static boolean delete(KeySpace keySpace, String key) {
        return membaseClient.delete(keySpace.getFullKey(key));
    }

    public static long incr(KeySpace keySpace, String key) {
        return membaseClient.incr(keySpace.getFullKey(key), 1L);
    }

    public static long addOrIncr(KeySpace keySpace, String key) {
        return membaseClient.addOrIncr(keySpace.getFullKey(key), 1L);
    }

    public static Object get(KeySpace keySpace, String key) {
        return membaseClient.get(keySpace.getFullKey(key));
    }

    public static long getCounter(KeySpace keySpace, String key) {
        return membaseClient.getCounter(keySpace.getFullKey(key));
    }

    public static String getString(KeySpace keySpace, String key) {
        Object obj = Membase.get(keySpace, key);
        return obj == null ? null : obj.toString();
    }

    public static Integer getInt(KeySpace keySpace, String key) {
        Object obj = Membase.get(keySpace, key);
        if (obj == null || obj instanceof Integer) {
            return (Integer)obj;
        }
        try {
            return Integer.valueOf(obj.toString());
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    public static Long getLong(KeySpace keySpace, String key) {
        Object obj = Membase.get(keySpace, key);
        if (obj == null || obj instanceof Long) {
            return (Long)obj;
        }
        try {
            return Long.valueOf(obj.toString());
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    public static Double getDouble(KeySpace keySpace, String key) {
        Object obj = Membase.get(keySpace, key);
        if (obj == null || obj instanceof Double) {
            return (Double)obj;
        }
        try {
            return Double.valueOf(obj.toString());
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    static {
        membaseClient = new MemCachedClient();
        log.info((Object)"Initializing Membase connection...");
        if (Membase.loadProperties()) {
            try {
                int numServers = Integer.parseInt(properties.getProperty("numServers"));
                String[] servers = new String[numServers];
                for (int i = 1; i <= numServers; ++i) {
                    servers[i - 1] = properties.getProperty("server" + i);
                    log.info((Object)("Adding Membase server " + servers[i - 1]));
                }
                SockIOPool pool = SockIOPool.getInstance();
                pool.setServers(servers);
                pool.setInitConn(Integer.parseInt(properties.getProperty("pool.initialConnections", "2")));
                pool.setMinConn(Integer.parseInt(properties.getProperty("pool.minConnections", "2")));
                pool.setMaxConn(Integer.parseInt(properties.getProperty("pool.maxConnections", "25")));
                pool.setMaxIdle((long)(1000 * Integer.parseInt(properties.getProperty("pool.maxIdleTime", Integer.toString(21600)))));
                pool.setMaintSleep((long)Integer.parseInt(properties.getProperty("maintenanceThread.sleep", "30")));
                pool.setNagle(false);
                pool.setSocketTO(1000 * Integer.parseInt(properties.getProperty("socket.readTimeout", "3")));
                pool.setSocketConnectTO(1000 * Integer.parseInt(properties.getProperty("socket.connectTimeout", "3")));
                pool.setHashingAlg(2);
                membaseClient.setCompressEnable(true);
                membaseClient.setCompressThreshold(16384L);
                membaseClient.setPrimitiveAsString(true);
                membaseClient.setSanitizeKeys(false);
                pool.initialize();
            }
            catch (Exception e) {
                log.fatal((Object)"Unable to initialize Membase", (Throwable)e);
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum KeySpace {
        RECENT_CHATROOM_LIST("RCL", 180L, TimeUnit.DAYS);

        private String name;
        private long cacheTime = 0L;

        private KeySpace(String name) {
            this.name = name;
        }

        private KeySpace(String name, long cacheTime, TimeUnit cacheTimeUnit) {
            this.name = name;
            if (cacheTime <= 0L) {
                return;
            }
            switch (cacheTimeUnit) {
                case MILLISECONDS: {
                    this.cacheTime = cacheTime;
                    break;
                }
                case SECONDS: {
                    this.cacheTime = cacheTime * 1000L;
                    break;
                }
                case MINUTES: {
                    this.cacheTime = cacheTime * 60000L;
                    break;
                }
                case HOURS: {
                    this.cacheTime = cacheTime * 3600000L;
                    break;
                }
                case DAYS: {
                    this.cacheTime = cacheTime * 86400000L;
                }
            }
        }

        public String getFullKey(String key) {
            return this.name + Membase.KEY_SEPERATOR + key.replaceAll(" ", Membase.URL_ENCODED_SPACE);
        }

        public boolean expires() {
            return this.cacheTime > 0L;
        }

        public Date getExpiryDate() {
            return new Date(System.currentTimeMillis() + this.cacheTime);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static enum TimeUnit {
        MILLISECONDS,
        SECONDS,
        MINUTES,
        HOURS,
        DAYS;

    }
}

