/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  redis.clients.jedis.Jedis
 */
package com.projectgoth.fusion.common;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.Redis;
import com.projectgoth.fusion.slice.FusionException;
import java.util.List;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class RedisQueue {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(RedisQueue.class));
    private static final String queueKeySpace = "Queue:";
    Jedis redisQueuesMasterInstance = null;

    public static RedisQueue getInstance(Jedis redisConnection) {
        try {
            RedisQueue rq = new RedisQueue(redisConnection);
            return rq;
        }
        catch (Exception e) {
            log.error((Object)("Unable to create RedisQueue instance: " + e.getMessage()), (Throwable)e);
            return null;
        }
    }

    public static RedisQueue getInstance() {
        try {
            RedisQueue rq = new RedisQueue();
            return rq;
        }
        catch (Exception e) {
            log.error((Object)("Unable to create RedisQueue instance: " + e.getMessage()), (Throwable)e);
            return null;
        }
    }

    private RedisQueue(Jedis redisConnection) throws Exception {
        if (redisConnection == null) {
            throw new Exception("Invalid Initialization - redisConnection is null");
        }
        this.redisQueuesMasterInstance = redisConnection;
    }

    private RedisQueue() throws Exception {
        this.redisQueuesMasterInstance = Redis.getQueuesMasterInstance();
    }

    public void disconnect() throws Exception {
        Redis.disconnect(this.redisQueuesMasterInstance, log);
    }

    public int push(String queueName, String value, boolean rightToLeft) throws Exception {
        if (rightToLeft) {
            Long resultLong = this.redisQueuesMasterInstance.rpush(queueKeySpace + queueName, new String[]{value});
            if (resultLong == null) {
                throw new FusionException("push-rpush returns null.");
            }
            return resultLong.intValue();
        }
        Long resultLong = this.redisQueuesMasterInstance.lpush(queueKeySpace + queueName, new String[]{value});
        if (resultLong == null) {
            throw new FusionException("push-lpush returns null.");
        }
        return resultLong.intValue();
    }

    public String pop(String queueName, boolean rightToLeft) throws Exception {
        if (rightToLeft) {
            return this.redisQueuesMasterInstance.lpop(queueKeySpace + queueName);
        }
        return this.redisQueuesMasterInstance.rpop(queueKeySpace + queueName);
    }

    public String rpoplpush(String sourceQueueName, String targetQueueName) throws Exception {
        return this.redisQueuesMasterInstance.rpoplpush(queueKeySpace + sourceQueueName, queueKeySpace + targetQueueName);
    }

    public String rpoplpushBlocking(String sourceQueueName, String targetQueueName, int retryInterval, int timeout) throws Exception {
        String value = this.redisQueuesMasterInstance.rpoplpush(queueKeySpace + sourceQueueName, queueKeySpace + targetQueueName);
        if (value == null) {
            long startTime = System.currentTimeMillis();
            while (value == null) {
                try {
                    Thread.sleep(retryInterval);
                }
                catch (InterruptedException ignored) {
                    // empty catch block
                }
                if ((value = this.redisQueuesMasterInstance.rpoplpush(queueKeySpace + sourceQueueName, queueKeySpace + targetQueueName)) != null || timeout <= 0 || System.currentTimeMillis() - startTime < (long)timeout) continue;
                return null;
            }
        }
        return value;
    }

    public int remove(String queueName, int count, String value) throws Exception {
        Long resultLong = this.redisQueuesMasterInstance.lrem(queueKeySpace + queueName, (long)count, value);
        if (resultLong == null) {
            throw new FusionException("remove-lrem returns null.");
        }
        return resultLong.intValue();
    }

    public String popBlocking(String queueName, int retryInterval, int timeout, boolean rightToLeft) throws Exception {
        String value = null;
        value = rightToLeft ? this.redisQueuesMasterInstance.lpop(queueKeySpace + queueName) : this.redisQueuesMasterInstance.rpop(queueKeySpace + queueName);
        if (value == null) {
            long startTime = System.currentTimeMillis();
            while (value == null) {
                try {
                    Thread.sleep(retryInterval);
                }
                catch (InterruptedException e) {
                    // empty catch block
                }
                if ((value = rightToLeft ? this.redisQueuesMasterInstance.lpop(queueKeySpace + queueName) : this.redisQueuesMasterInstance.rpop(queueKeySpace + queueName)) != null || timeout <= 0 || System.currentTimeMillis() - startTime < (long)timeout) continue;
                return null;
            }
        }
        return value;
    }

    public String peek(String queueName, boolean rightToLeft) throws Exception {
        if (rightToLeft) {
            return this.redisQueuesMasterInstance.lindex(queueKeySpace + queueName, 0L);
        }
        return this.redisQueuesMasterInstance.lindex(queueKeySpace + queueName, -1L);
    }

    public String peekBlocking(String queueName, int retryInterval, int timeout, boolean rightToLeft) throws Exception {
        String value = null;
        value = rightToLeft ? this.redisQueuesMasterInstance.lindex(queueKeySpace + queueName, 0L) : this.redisQueuesMasterInstance.lindex(queueKeySpace + queueName, -1L);
        if (value == null) {
            long startTime = System.currentTimeMillis();
            while (value == null) {
                try {
                    Thread.sleep(retryInterval);
                }
                catch (InterruptedException e) {
                    // empty catch block
                }
                if ((value = this.redisQueuesMasterInstance.lindex(queueKeySpace + queueName, 0L)) != null || timeout <= 0 || System.currentTimeMillis() - startTime < (long)timeout) continue;
                return null;
            }
        }
        return value;
    }

    public int size(String queueName) throws Exception {
        Long resultLong = this.redisQueuesMasterInstance.llen(queueKeySpace + queueName);
        if (resultLong == null) {
            throw new FusionException("size-llen returns null.");
        }
        return resultLong.intValue();
    }

    public List<String> peekAll(String queueName) throws Exception {
        return this.redisQueuesMasterInstance.lrange(queueKeySpace + queueName, 0L, -1L);
    }

    public int purgeQueue(String queueName) throws Exception {
        Long resultLong = this.redisQueuesMasterInstance.del(queueKeySpace + queueName);
        if (resultLong == null) {
            throw new FusionException("purgeQueue del returns null.");
        }
        return resultLong.intValue();
    }
}

