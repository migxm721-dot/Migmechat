/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  org.apache.log4j.Logger
 *  org.quartz.Job
 *  org.quartz.JobExecutionContext
 *  org.quartz.JobExecutionException
 *  redis.clients.jedis.Jedis
 *  redis.clients.jedis.exceptions.JedisException
 */
package com.projectgoth.fusion.jobscheduling.job;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.Redis;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.data.AccountEntryData;
import com.projectgoth.fusion.data.BasicMerchantTagDetailsData;
import com.projectgoth.fusion.data.CurrencyData;
import com.projectgoth.fusion.data.MerchantTagData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Account;
import com.projectgoth.fusion.interfaces.AccountHome;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import java.util.Calendar;
import java.util.Set;
import java.util.concurrent.Semaphore;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

public class MerchantTag
implements Job {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(MerchantTag.class));
    private static final Semaphore semaphore = new Semaphore(1);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        if (!semaphore.tryAcquire()) {
            log.warn((Object)"Another job is processing merchant tags. Exiting...");
            return;
        }
        Jedis jedisConn = null;
        String accountEntryId = null;
        block15: while (true) {
            try {
                try {}
                catch (Exception e) {
                    log.error((Object)"Unhandled exception while executing MerchantTag job: ", (Throwable)e);
                    Object var19_25 = null;
                    Redis.disconnect(jedisConn, log);
                    semaphore.release();
                    return;
                }
            }
            catch (Throwable throwable) {
                Object var19_26 = null;
                Redis.disconnect(jedisConn, log);
                semaphore.release();
                throw throwable;
            }
            while (true) {
                if (!SystemProperty.getBool("MerchantTaggingEnabled", true)) break block15;
                try {
                    Set idToProcess;
                    if (jedisConn == null) {
                        jedisConn = Redis.getQueuesMasterInstance();
                    }
                    if (!jedisConn.isConnected()) {
                        jedisConn.connect();
                    }
                    if ((idToProcess = jedisConn.zrange(Redis.KeySpaceQueuesInstance.TOP_MERCHANT_TAG.toString(), 0L, 0L)) != null && !idToProcess.isEmpty()) {
                        log.info((Object)("Retrieved set: " + idToProcess));
                        accountEntryId = (String)idToProcess.iterator().next();
                        Account accountBean = (Account)EJBHomeCache.getObject("ejb/Account", AccountHome.class);
                        User userBean = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
                        AccountEntryData fromUserEntry = null;
                        try {
                            fromUserEntry = accountBean.getAccountEntryFromSlave(Long.valueOf(accountEntryId));
                        }
                        catch (NumberFormatException nfe) {
                            log.error((Object)("Failed to parse account entry id: [" + accountEntryId + "]" + nfe.getMessage()));
                            jedisConn.zrem(Redis.KeySpaceQueuesInstance.TOP_MERCHANT_TAG.toString(), new String[]{accountEntryId});
                            continue;
                        }
                        if (fromUserEntry != null) {
                            AccountEntryData toUserEntry = null;
                            try {
                                toUserEntry = accountBean.getAccountEntryFromSlave(Long.valueOf(fromUserEntry.reference));
                            }
                            catch (NumberFormatException nfe) {
                                log.error((Object)("Failed to parse account entry id: [" + accountEntryId + "]" + nfe.getMessage()));
                                jedisConn.zrem(Redis.KeySpaceQueuesInstance.TOP_MERCHANT_TAG.toString(), new String[]{accountEntryId});
                                continue;
                            }
                            if (toUserEntry != null) {
                                BasicMerchantTagDetailsData tagData = accountBean.getMerchantTagFromUsername(null, toUserEntry.username, false);
                                int fromUserId = userBean.getUserID(fromUserEntry.username, null);
                                int toUserId = userBean.getUserID(toUserEntry.username, null);
                                double nettTransfer = 0.0;
                                if (tagData == null) {
                                    nettTransfer = accountBean.getNETTTransfer(null, fromUserEntry.username, toUserEntry.username, fromUserEntry.dateCreated, SystemProperty.getInt("MerchantMerchantTagInterval", 43200));
                                } else if (tagData != null && fromUserId == tagData.merchantUserID) {
                                    Calendar dt = Calendar.getInstance();
                                    int cutoff = (int)((dt.getTimeInMillis() - tagData.getLastSalesDateInMiliSeconds()) / 1000L) / 60;
                                    nettTransfer = accountBean.getNETTTransfer(null, fromUserEntry.username, toUserEntry.username, fromUserEntry.dateCreated, cutoff);
                                }
                                double minRequirementUSD = SystemProperty.getDouble("MinMerchantMerchantTagAmountUSD", 100.0);
                                double minRequirement = accountBean.convertCurrency(minRequirementUSD, "USD", CurrencyData.baseCurrency);
                                log.info((Object)("NETT transfer for merchant [" + fromUserEntry.username + "] from [" + fromUserEntry.dateCreated + "]: " + nettTransfer + " to:" + toUserEntry.username));
                                if (nettTransfer >= minRequirement) {
                                    log.info((Object)("Processing merchant tag: (" + toUserId + " --> " + fromUserId + ")"));
                                    accountBean.tagMerchant(null, toUserId, toUserEntry.username, fromUserId, fromUserEntry.username, tagData, fromUserEntry.dateCreated, null, MerchantTagData.StatusEnum.ACTIVE.value());
                                }
                                log.info((Object)("Removing redis key: " + accountEntryId));
                                jedisConn.zrem(Redis.KeySpaceQueuesInstance.TOP_MERCHANT_TAG.toString(), new String[]{accountEntryId});
                                continue block15;
                            }
                            log.warn((Object)("AccountEntry for reference [" + fromUserEntry.reference + "] not found. Not yet replicated to slave?"));
                            Thread.sleep(1000L);
                            continue block15;
                        }
                        log.warn((Object)("AccountEntry [" + accountEntryId + "] not found. Not yet replicated to slave?"));
                        Thread.sleep(1000L);
                        continue block15;
                    }
                    log.debug((Object)"Merchant Tag set is empty");
                    Thread.sleep(1000L);
                }
                catch (JedisException jde) {
                    log.error((Object)"Jedis connection error: ", (Throwable)jde);
                    Redis.disconnect(jedisConn, log);
                    try {
                        Thread.sleep(1000L);
                        continue block15;
                    }
                    catch (InterruptedException ie) {
                    }
                }
                catch (CreateException ce) {
                    log.error((Object)("Failed to instantiate bean: " + ce.getMessage()));
                    try {
                        Thread.sleep(1000L);
                        continue block15;
                    }
                    catch (InterruptedException ie) {}
                }
            }
            break;
        }
        log.info((Object)"Merchant tagging disabled... dying to live another day!");
        Object var19_24 = null;
        Redis.disconnect(jedisConn, log);
        semaphore.release();
    }
}

