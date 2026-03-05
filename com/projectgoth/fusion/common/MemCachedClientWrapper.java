/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.danga.MemCached.MemCachedClient
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.common;

import com.danga.MemCached.MemCachedClient;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.MemCachedDistributedLock;
import com.projectgoth.fusion.common.MemCachedKeySpaces;
import com.projectgoth.fusion.common.MemCachedKeyUtils;
import com.projectgoth.fusion.common.MemCachedUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MemCachedClientWrapper {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(MemCachedClientWrapper.class));
    public static int MAX_KEY_LENGTH = 250;
    private static final Map<MemCachedUtils.Instance, MemCachedClient> clientMap = new HashMap<MemCachedUtils.Instance, MemCachedClient>();

    public static MemCachedClient getMemCachedClient(MemCachedUtils.Instance instance) {
        return clientMap.get((Object)instance);
    }

    public static boolean add(MemCachedKeySpaces.MemCachedKeySpaceInterface keySpace, String key, Object value) {
        return clientMap.get((Object)keySpace.getMemCachedInstance()).add(MemCachedKeyUtils.getFullKeyFromStrings(keySpace.getName(), key), value, keySpace.getExpiryDate());
    }

    public static boolean add(MemCachedKeySpaces.MemCachedKeySpaceInterface keySpace, String key, Object value, long ttl) {
        return clientMap.get((Object)keySpace.getMemCachedInstance()).add(MemCachedKeyUtils.getFullKeyFromStrings(keySpace.getName(), key), value, ttl > 0L ? new Date(System.currentTimeMillis() + ttl) : keySpace.getExpiryDate());
    }

    public static boolean set(MemCachedKeySpaces.MemCachedKeySpaceInterface keySpace, String key, Object value) {
        return clientMap.get((Object)keySpace.getMemCachedInstance()).set(MemCachedKeyUtils.getFullKeyFromStrings(keySpace.getName(), key), value, keySpace.getExpiryDate());
    }

    public static boolean set(MemCachedKeySpaces.MemCachedKeySpaceInterface keySpace, String key, Object value, long ttl) {
        return clientMap.get((Object)keySpace.getMemCachedInstance()).set(MemCachedKeyUtils.getFullKeyFromStrings(keySpace.getName(), key), value, ttl > 0L ? new Date(System.currentTimeMillis() + ttl) : keySpace.getExpiryDate());
    }

    public static boolean delete(MemCachedKeySpaces.MemCachedKeySpaceInterface keySpace, String key) {
        return clientMap.get((Object)keySpace.getMemCachedInstance()).delete(MemCachedKeyUtils.getFullKeyFromStrings(keySpace.getName(), key));
    }

    public static long incr(MemCachedKeySpaces.MemCachedKeySpaceInterface keySpace, String key) {
        return MemCachedClientWrapper.incr(keySpace, key, 1L);
    }

    public static long incr(MemCachedKeySpaces.MemCachedKeySpaceInterface keySpace, String key, long incrValue) {
        return clientMap.get((Object)keySpace.getMemCachedInstance()).incr(MemCachedKeyUtils.getFullKeyFromStrings(keySpace.getName(), key), incrValue);
    }

    public static long decr(MemCachedKeySpaces.MemCachedKeySpaceInterface keySpace, String key) {
        return MemCachedClientWrapper.decr(keySpace, key, 1L);
    }

    public static long decr(MemCachedKeySpaces.MemCachedKeySpaceInterface keySpace, String key, long incrValue) {
        return clientMap.get((Object)keySpace.getMemCachedInstance()).decr(MemCachedKeyUtils.getFullKeyFromStrings(keySpace.getName(), key), incrValue);
    }

    public static long addOrIncr(MemCachedKeySpaces.MemCachedKeySpaceInterface keySpace, String key) {
        return MemCachedClientWrapper.addOrIncr(keySpace, key, keySpace.getCacheTime());
    }

    public static long addOrIncr(MemCachedKeySpaces.MemCachedKeySpaceInterface keySpace, String key, long ttl) {
        return MemCachedClientWrapper.addOrIncr(keySpace, key, 1L, ttl);
    }

    public static long addOrIncr(MemCachedKeySpaces.MemCachedKeySpaceInterface keySpace, String key, long incrValue, long ttl) {
        boolean ret = MemCachedClientWrapper.add(keySpace, key, incrValue, ttl);
        if (ret) {
            return incrValue;
        }
        return MemCachedClientWrapper.incr(keySpace, key, incrValue);
    }

    public static Object get(MemCachedKeySpaces.MemCachedKeySpaceInterface keySpace, String key) {
        String fullKey = MemCachedKeyUtils.getFullKeyFromStrings(keySpace.getName(), key);
        if (fullKey.length() > MAX_KEY_LENGTH) {
            return null;
        }
        return clientMap.get((Object)keySpace.getMemCachedInstance()).get(fullKey);
    }

    public static long getCounter(MemCachedKeySpaces.MemCachedKeySpaceInterface keySpace, String key) {
        String fullKey = MemCachedKeyUtils.getFullKeyFromStrings(keySpace.getName(), key);
        if (fullKey.length() > MAX_KEY_LENGTH) {
            return -1L;
        }
        return clientMap.get((Object)keySpace.getMemCachedInstance()).getCounter(fullKey);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <TContext> long getCounter(MemCachedKeySpaces.MemCachedKeySpaceInterface keySpace, String key, InitialCounterValueProvider<TContext> initialCounterValueProvider, TContext contextData) {
        String fullKey = MemCachedKeyUtils.getFullKeyFromStrings(keySpace.getName(), key);
        if (fullKey.length() > MAX_KEY_LENGTH) {
            return -1L;
        }
        long currentCounter = MemCachedClientWrapper.getCounter(keySpace, key);
        if (currentCounter >= 0L) {
            return currentCounter;
        }
        long initialCounter = -1L;
        MemCachedDistributedLock.LockInstance lockInstance = MemCachedDistributedLock.getDistributedLock(initialCounterValueProvider.getInitialValueLockKeySpace(keySpace, key, contextData), initialCounterValueProvider.getInitialValueLockKeyName(keySpace, key, contextData), initialCounterValueProvider.getInitialValueLockWaitTime(keySpace, key, contextData), -1L);
        if (lockInstance != null) {
            try {
                currentCounter = MemCachedClientWrapper.getCounter(keySpace, key);
                if (currentCounter >= 0L) {
                    initialCounter = currentCounter;
                } else {
                    initialCounter = initialCounterValueProvider.getInitialValue(keySpace, key, contextData);
                    if (initialCounter >= 0L) {
                        MemCachedClientWrapper.set(keySpace, key, initialCounter, initialCounterValueProvider.getInitialValueTimeToLive(keySpace, key, contextData));
                    }
                }
                Object var11_8 = null;
                lockInstance.release(false);
            }
            catch (Throwable throwable) {
                Object var11_9 = null;
                lockInstance.release(false);
                throw throwable;
            }
        }
        return initialCounter;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <TContext> long incr(MemCachedKeySpaces.MemCachedKeySpaceInterface keySpace, String key, InitialCounterValueProvider<TContext> initialCounterValueProvider, long incrValue, TContext contextData) {
        String fullKey = MemCachedKeyUtils.getFullKeyFromStrings(keySpace.getName(), key);
        if (fullKey.length() > MAX_KEY_LENGTH) {
            return -1L;
        }
        long newValue = MemCachedClientWrapper.incr(keySpace, key, incrValue);
        if (newValue >= 0L) {
            return newValue;
        }
        newValue = -1L;
        MemCachedDistributedLock.LockInstance lockInstance = MemCachedDistributedLock.getDistributedLock(initialCounterValueProvider.getInitialValueLockKeySpace(keySpace, key, contextData), initialCounterValueProvider.getInitialValueLockKeyName(keySpace, key, contextData), initialCounterValueProvider.getInitialValueLockWaitTime(keySpace, key, contextData), -1L);
        if (lockInstance != null) {
            try {
                long initialCounter;
                newValue = MemCachedClientWrapper.incr(keySpace, key, incrValue);
                if (newValue < 0L && (initialCounter = initialCounterValueProvider.getInitialValue(keySpace, key, contextData)) >= 0L) {
                    MemCachedClientWrapper.add(keySpace, key, initialCounter, initialCounterValueProvider.getInitialValueTimeToLive(keySpace, key, contextData));
                    newValue = MemCachedClientWrapper.incr(keySpace, key, incrValue);
                }
                Object var13_9 = null;
                lockInstance.release(false);
            }
            catch (Throwable throwable) {
                Object var13_10 = null;
                lockInstance.release(false);
                throw throwable;
            }
        }
        return newValue;
    }

    public static String getString(MemCachedKeySpaces.MemCachedKeySpaceInterface keySpace, String key) {
        return MemCachedClientWrapper.toString(MemCachedClientWrapper.get(keySpace, key));
    }

    public static Integer getInt(MemCachedKeySpaces.MemCachedKeySpaceInterface keySpace, String key) {
        return MemCachedClientWrapper.toInt(MemCachedClientWrapper.get(keySpace, key));
    }

    public static Long getLong(MemCachedKeySpaces.MemCachedKeySpaceInterface keySpace, String key) {
        return MemCachedClientWrapper.toLong(MemCachedClientWrapper.get(keySpace, key));
    }

    public static Double getDouble(MemCachedKeySpaces.MemCachedKeySpaceInterface keySpace, String key) {
        return MemCachedClientWrapper.toDouble(MemCachedClientWrapper.get(keySpace, key));
    }

    public static Map<String, Object> getMulti(MemCachedKeySpaces.MemCachedKeySpaceInterface keySpace, String[] keys) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        for (Map.Entry e : clientMap.get((Object)keySpace.getMemCachedInstance()).getMulti(MemCachedKeyUtils.getFullKeysForKeySpace(keySpace, keys)).entrySet()) {
            map.put(((String)e.getKey()).substring(keySpace.getName().length() + 1), e.getValue());
        }
        return map;
    }

    public static boolean deletePaged(MemCachedKeySpaces.MemCachedKeySpaceInterface keySpace, String key) {
        Integer numPages = MemCachedClientWrapper.getInt(keySpace, MemCachedKeyUtils.getFullKeyFromStrings(key, "PAGES"));
        if (null == numPages) {
            return false;
        }
        boolean b = true;
        Integer i = 0;
        while (i < numPages) {
            b &= MemCachedClientWrapper.delete(keySpace, MemCachedKeyUtils.getFullKeyFromStrings(key, "PAGE", i.toString()));
            i = i + 1;
        }
        return b &= MemCachedClientWrapper.delete(keySpace, MemCachedKeyUtils.getFullKeyFromStrings(key, "PAGES"));
    }

    public static Object getPaged(MemCachedKeySpaces.MemCachedKeySpaceInterface keySpace, String key) {
        Integer numPages = MemCachedClientWrapper.getInt(keySpace, MemCachedKeyUtils.getFullKeyFromStrings(key, "PAGES"));
        if (null == numPages) {
            return null;
        }
        ArrayList<String> keys = new ArrayList<String>();
        Integer i = 0;
        while (i < numPages) {
            keys.add(MemCachedKeyUtils.getFullKeyFromStrings(key, "PAGE", i.toString()));
            i = i + 1;
        }
        Map multiMap = clientMap.get((Object)keySpace.getMemCachedInstance()).getMulti(MemCachedKeyUtils.getFullKeysForKeySpace(keySpace, keys.toArray(new String[0])));
        String firstKey = MemCachedKeyUtils.getFullKeyFromStrings(keySpace.getName(), key, "PAGE", "0");
        Object first = multiMap.get(firstKey);
        if (null == first) {
            return null;
        }
        multiMap.remove(firstKey);
        Integer i2 = 1;
        while (i2 < numPages) {
            block9: {
                Object page = multiMap.get(MemCachedKeyUtils.getFullKeyFromStrings(keySpace.getName(), key, "PAGE", i2.toString()));
                if (null == page) {
                    return null;
                }
                try {
                    if (first instanceof Collection) {
                        ((Collection)first).addAll((Collection)page);
                        break block9;
                    }
                    if (first instanceof Map) {
                        ((Map)first).putAll((Map)page);
                        break block9;
                    }
                    throw new ClassCastException();
                }
                catch (Exception e) {
                    log.error((Object)"unable to retrive paged list", (Throwable)e);
                    return null;
                }
            }
            i2 = i2 + 1;
        }
        return first;
    }

    public static boolean setPaged(MemCachedKeySpaces.MemCachedKeySpaceInterface keySpace, String key, Object items) {
        return MemCachedClientWrapper.setPaged(keySpace, key, items, null);
    }

    public static boolean setPaged(MemCachedKeySpaces.MemCachedKeySpaceInterface keySpace, String key, Object items, Integer pageSize) {
        Iterator<Object> iter;
        int pages;
        int numItems;
        Integer finalPageSize;
        boolean b = true;
        if (null == items) {
            return false;
        }
        Integer n = finalPageSize = pageSize == null ? keySpace.getPageSize() : pageSize;
        if (null == finalPageSize) {
            throw new IllegalArgumentException("Pagesize for " + keySpace + " cannot be null");
        }
        boolean isCollection = false;
        if (items instanceof Collection) {
            numItems = ((Collection)items).size();
            pages = numItems / finalPageSize + 1;
            iter = ((Collection)items).iterator();
            isCollection = true;
        } else if (items instanceof Map) {
            numItems = ((Map)items).size();
            pages = numItems / finalPageSize + 1;
            iter = ((Map)items).entrySet().iterator();
        } else {
            throw new IllegalArgumentException("Only Collections & Maps can be paged - you passed in " + items.getClass());
        }
        int idx = 0;
        Integer i = 0;
        while (i < pages) {
            int upperBound = i * finalPageSize + finalPageSize;
            upperBound = upperBound > numItems ? numItems : upperBound;
            try {
                Object sub = items.getClass().newInstance();
                while (idx < upperBound) {
                    if (isCollection) {
                        ((Collection)sub).add(iter.next());
                    } else {
                        Map.Entry e = (Map.Entry)iter.next();
                        ((Map)sub).put(e.getKey(), e.getValue());
                    }
                    ++idx;
                }
                b &= MemCachedClientWrapper.set(keySpace, MemCachedKeyUtils.getFullKeyFromStrings(key, "PAGE", i.toString()), sub);
            }
            catch (Exception e) {
                return false;
            }
            i = i + 1;
        }
        return b ? MemCachedClientWrapper.set(keySpace, MemCachedKeyUtils.getFullKeyFromStrings(key, "PAGES"), pages) : b;
    }

    public static Map<String, String> getMultiString(MemCachedKeySpaces.MemCachedKeySpaceInterface keySpace, String[] keys) {
        HashMap<String, String> map = new HashMap<String, String>();
        for (Map.Entry e : clientMap.get((Object)keySpace.getMemCachedInstance()).getMulti(MemCachedKeyUtils.getFullKeysForKeySpace(keySpace, keys)).entrySet()) {
            map.put(((String)e.getKey()).substring(keySpace.getName().length() + 1), MemCachedClientWrapper.toString(e.getValue()));
        }
        return map;
    }

    public static Map<String, Integer> getMultiInt(MemCachedKeySpaces.MemCachedKeySpaceInterface keySpace, String[] keys) {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        for (Map.Entry e : clientMap.get((Object)keySpace.getMemCachedInstance()).getMulti(MemCachedKeyUtils.getFullKeysForKeySpace(keySpace, keys)).entrySet()) {
            map.put(((String)e.getKey()).substring(keySpace.getName().length() + 1), MemCachedClientWrapper.toInt(e.getValue()));
        }
        return map;
    }

    public static Map<String, Long> getMultiLong(MemCachedKeySpaces.MemCachedKeySpaceInterface keySpace, String[] keys) {
        HashMap<String, Long> map = new HashMap<String, Long>();
        for (Map.Entry e : clientMap.get((Object)keySpace.getMemCachedInstance()).getMulti(MemCachedKeyUtils.getFullKeysForKeySpace(keySpace, keys)).entrySet()) {
            map.put(((String)e.getKey()).substring(keySpace.getName().length() + 1), MemCachedClientWrapper.toLong(e.getValue()));
        }
        return map;
    }

    public static Map<String, Double> getMultiDouble(MemCachedKeySpaces.MemCachedKeySpaceInterface keySpace, String[] keys) {
        HashMap<String, Double> map = new HashMap<String, Double>();
        for (Map.Entry e : clientMap.get((Object)keySpace.getMemCachedInstance()).getMulti(MemCachedKeyUtils.getFullKeysForKeySpace(keySpace, keys)).entrySet()) {
            map.put(((String)e.getKey()).substring(keySpace.getName().length() + 1), MemCachedClientWrapper.toDouble(e.getValue()));
        }
        return map;
    }

    private static String toString(Object obj) {
        return obj == null ? null : obj.toString();
    }

    private static Integer toInt(Object obj) {
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

    private static Long toLong(Object obj) {
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

    private static Double toDouble(Object obj) {
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
        for (MemCachedUtils.Instance instance : MemCachedUtils.Instance.values()) {
            log.info((Object)("Setting up MemCachedClientWrapper for instance: " + (Object)((Object)instance)));
            clientMap.put(instance, MemCachedUtils.getMemCachedClient(instance));
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface InitialCounterValueProvider<TContext> {
        public MemCachedKeySpaces.MemCachedKeySpaceInterface getInitialValueLockKeySpace(MemCachedKeySpaces.MemCachedKeySpaceInterface var1, String var2, TContext var3);

        public String getInitialValueLockKeyName(MemCachedKeySpaces.MemCachedKeySpaceInterface var1, String var2, TContext var3);

        public long getInitialValue(MemCachedKeySpaces.MemCachedKeySpaceInterface var1, String var2, TContext var3);

        public long getInitialValueLockWaitTime(MemCachedKeySpaces.MemCachedKeySpaceInterface var1, String var2, TContext var3);

        public long getInitialValueTimeToLive(MemCachedKeySpaces.MemCachedKeySpaceInterface var1, String var2, TContext var3);
    }
}

