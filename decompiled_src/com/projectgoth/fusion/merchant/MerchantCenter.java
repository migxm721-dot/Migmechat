/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  redis.clients.jedis.Jedis
 */
package com.projectgoth.fusion.merchant;

import com.projectgoth.fusion.common.Redis;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;

public class MerchantCenter {
    private static final Logger log = Logger.getLogger(MerchantCenter.class);
    private static MerchantCenter instance = null;

    private MerchantCenter() {
    }

    public static MerchantCenter getInstance() {
        if (instance == null) {
            instance = new MerchantCenter();
        }
        return instance;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void createTagEntry(long accountEntryId) {
        Jedis jedis = null;
        try {
            try {
                jedis = Redis.getQueuesMasterInstance();
                jedis.zadd(Redis.KeySpaceQueuesInstance.TOP_MERCHANT_TAG.toString(), (double)accountEntryId, String.valueOf(accountEntryId));
                log.debug((Object)("Creating redis entry for " + Redis.KeySpaceQueuesInstance.TOP_MERCHANT_TAG.toString() + ", value: " + String.valueOf(accountEntryId)));
            }
            catch (Exception e) {
                log.error((Object)("Failed to create merchant tag entry for accountentryId [" + accountEntryId + "], reason: "), (Throwable)e);
                Object var6_4 = null;
                Redis.disconnect(jedis, log);
                return;
            }
            Object var6_3 = null;
        }
        catch (Throwable throwable) {
            Object var6_5 = null;
            Redis.disconnect(jedis, log);
            throw throwable;
        }
        Redis.disconnect(jedis, log);
    }
}

