/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.pool2.impl.GenericObjectPoolConfig
 *  org.apache.log4j.Logger
 *  redis.clients.jedis.Jedis
 *  redis.clients.jedis.JedisPool
 *  redis.clients.jedis.JedisPoolConfig
 */
package com.projectgoth.fusion.common;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.jedis.FusionJedisPool;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisConnectionManager {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(RedisConnectionManager.class));
    private static final ConcurrentHashMap<String, FusionJedisPool> pools = new ConcurrentHashMap();
    private final JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
    private final ExecutorService ex = Executors.newSingleThreadExecutor();
    private AtomicInteger configVersion = new AtomicInteger(0);

    private RedisConnectionManager() {
        this.ex.execute(new Runnable(){

            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    RedisConnectionManager.this.refreshConfig();
                    try {
                        Thread.sleep(1000 * SystemProperty.getInt(SystemPropertyEntities.JedisPoolConfig.PROPS_REFRESH_INTERVAL_SECS));
                    }
                    catch (InterruptedException e) {
                        log.info((Object)"JedisPool PropsRefreshThread interrupted and shutting down");
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                log.info((Object)"JedisPool PropsRefreshThread exiting");
            }
        });
    }

    public Jedis getConnection(String host, int port) {
        if (SystemPropertyEntities.JedisPoolConfig.Cache.enabled.getValue().booleanValue()) {
            String key = host + ":" + String.valueOf(port);
            int timeout = SystemPropertyEntities.JedisPoolConfig.Cache.poolSocketTimeout.getValue();
            FusionJedisPool jedisPool = pools.get(key);
            if (jedisPool == null) {
                log.info((Object)("adding key in pool cache: " + key + " Current number of JedisPool created: " + pools.size() + pools));
                jedisPool = pools.putIfAbsent(key, new FusionJedisPool((GenericObjectPoolConfig)this.jedisPoolConfig, host, port, timeout));
                if (jedisPool == null) {
                    jedisPool = pools.get(key);
                }
            }
            jedisPool.ensureConfigUpToDate(this.configVersion.get(), (GenericObjectPoolConfig)this.jedisPoolConfig);
            Jedis jedis = jedisPool.getResource();
            if (jedis.getClient().getTimeout() != timeout) {
                log.info((Object)"jedisPool timeout refreshed.");
                jedis.getClient().setTimeout(timeout);
                jedis.getClient().rollbackTimeout();
            }
            return jedis;
        }
        Jedis jedis = new Jedis(host, port, SystemPropertyEntities.JedisPoolConfig.Cache.nonPoolSocketTimeout.getValue().intValue());
        jedis.connect();
        return jedis;
    }

    public void closeConnection(Jedis jedis) {
        if (SystemProperty.getBool(SystemPropertyEntities.JedisPoolConfig.JEDIS_POOL_ENABLED)) {
            String key = jedis.getClient().getHost() + ":" + String.valueOf(jedis.getClient().getPort());
            JedisPool jedisPool = pools.get(key);
            if (jedisPool != null) {
                jedisPool.returnResource(jedis);
            }
        } else {
            jedis.disconnect();
        }
    }

    private void refreshConfig() {
        boolean blockWhenExhausted = SystemProperty.getBool(SystemPropertyEntities.JedisPoolConfig.BLOCK_WHEN_EXHAUSTED);
        int maxTotal = SystemProperty.getInt(SystemPropertyEntities.JedisPoolConfig.MAX_TOTAL);
        int maxIdle = SystemProperty.getInt(SystemPropertyEntities.JedisPoolConfig.MAX_IDLE);
        int minIdle = SystemProperty.getInt(SystemPropertyEntities.JedisPoolConfig.MIN_IDLE);
        boolean testOnBorrow = SystemProperty.getBool(SystemPropertyEntities.JedisPoolConfig.TEST_ON_BORROW);
        boolean testOnReturn = SystemProperty.getBool(SystemPropertyEntities.JedisPoolConfig.TEST_ON_RETURN);
        boolean testWhileIdle = SystemProperty.getBool(SystemPropertyEntities.JedisPoolConfig.TEST_WHILE_IDLE);
        int numTestsPerEvictionRun = SystemProperty.getInt(SystemPropertyEntities.JedisPoolConfig.NUM_TESTS_PER_EVICTION_RUN);
        long timeBetweenEvictionRunsMillis = SystemProperty.getLong(SystemPropertyEntities.JedisPoolConfig.TIME_BETWEEN_EVICTION_RUN_MILLIS);
        long minEvictableIdleTime = SystemProperty.getLong(SystemPropertyEntities.JedisPoolConfig.MIN_EVICTABLE_IDLE_TIME_MILLIS);
        long maxWait = SystemProperty.getLong(SystemPropertyEntities.JedisPoolConfig.MAX_WAIT);
        boolean propertyChanged = false;
        if (this.jedisPoolConfig.getBlockWhenExhausted() != blockWhenExhausted) {
            this.jedisPoolConfig.setBlockWhenExhausted(blockWhenExhausted);
            propertyChanged = true;
        }
        if (this.jedisPoolConfig.getMaxTotal() != maxTotal) {
            this.jedisPoolConfig.setMaxTotal(maxTotal);
            propertyChanged = true;
        }
        if (this.jedisPoolConfig.getMaxIdle() != maxIdle) {
            this.jedisPoolConfig.setMaxIdle(maxIdle);
            propertyChanged = true;
        }
        if (this.jedisPoolConfig.getMinIdle() != minIdle) {
            this.jedisPoolConfig.setMinIdle(minIdle);
            propertyChanged = true;
        }
        if (this.jedisPoolConfig.getTestOnBorrow() != testOnBorrow) {
            this.jedisPoolConfig.setTestOnBorrow(testOnBorrow);
            propertyChanged = true;
        }
        if (this.jedisPoolConfig.getTestOnReturn() != testOnReturn) {
            this.jedisPoolConfig.setTestOnReturn(testOnReturn);
            propertyChanged = true;
        }
        if (this.jedisPoolConfig.getTestWhileIdle() != testWhileIdle) {
            this.jedisPoolConfig.setTestWhileIdle(testWhileIdle);
            propertyChanged = true;
        }
        if (this.jedisPoolConfig.getNumTestsPerEvictionRun() != numTestsPerEvictionRun) {
            this.jedisPoolConfig.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
            propertyChanged = true;
        }
        if (this.jedisPoolConfig.getTimeBetweenEvictionRunsMillis() != timeBetweenEvictionRunsMillis) {
            this.jedisPoolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
            propertyChanged = true;
        }
        if (this.jedisPoolConfig.getMinEvictableIdleTimeMillis() != minEvictableIdleTime) {
            this.jedisPoolConfig.setMinEvictableIdleTimeMillis(minEvictableIdleTime);
            propertyChanged = true;
        }
        if (this.jedisPoolConfig.getMaxWaitMillis() != maxWait) {
            this.jedisPoolConfig.setMaxWaitMillis(maxWait);
            propertyChanged = true;
        }
        if (propertyChanged) {
            this.configVersion.incrementAndGet();
        }
        if (SystemProperty.getBool(SystemPropertyEntities.JedisPoolConfig.PROPS_LOG_ENABLED)) {
            StringBuilder sb = new StringBuilder();
            sb.append("\n maxTotal=").append(this.jedisPoolConfig.getMaxTotal());
            sb.append("\n maxIdle=").append(this.jedisPoolConfig.getMaxIdle());
            sb.append("\n minIdle=").append(this.jedisPoolConfig.getMinIdle());
            sb.append("\n testOnBorrow=").append(this.jedisPoolConfig.getTestOnBorrow());
            sb.append("\n testOnReturn=").append(this.jedisPoolConfig.getTestOnReturn());
            sb.append("\n testWhileIdle=").append(this.jedisPoolConfig.getTestWhileIdle());
            sb.append("\n numTestsPerEvictionRun=").append(this.jedisPoolConfig.getNumTestsPerEvictionRun());
            sb.append("\n timeBetweenEvictionRunMillis=").append(this.jedisPoolConfig.getTimeBetweenEvictionRunsMillis());
            sb.append("\n MinEvictableIdleTimeMillis=").append(this.jedisPoolConfig.getMinEvictableIdleTimeMillis());
            sb.append("\n maxWait=").append(this.jedisPoolConfig.getMaxWaitMillis());
            log.info((Object)("Refreshed jedis pool config: " + sb.toString()));
        }
    }

    public static RedisConnectionManager getInstance() {
        return RedisConnectionManagerHolder.INSTANCE;
    }

    public void shutdown() {
        this.ex.shutdown();
        for (JedisPool jedisPool : pools.values()) {
            jedisPool.destroy();
        }
    }

    private static class RedisConnectionManagerHolder {
        public static final RedisConnectionManager INSTANCE = new RedisConnectionManager();

        private RedisConnectionManagerHolder() {
        }
    }
}

