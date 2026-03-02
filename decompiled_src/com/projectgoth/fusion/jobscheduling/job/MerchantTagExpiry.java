/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.EJBException
 *  org.apache.log4j.Logger
 *  org.quartz.Job
 *  org.quartz.JobDataMap
 *  org.quartz.JobExecutionContext
 *  org.quartz.JobExecutionException
 */
package com.projectgoth.fusion.jobscheduling.job;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.data.MerchantTagData;
import com.projectgoth.fusion.data.MerchantTagUserData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Account;
import com.projectgoth.fusion.interfaces.AccountHome;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;
import javax.ejb.EJBException;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MerchantTagExpiry
implements Job {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(MerchantTagExpiry.class));
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
    private static final Semaphore semaphore = new Semaphore(1);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void execute(JobExecutionContext jobContext) throws JobExecutionException {
        if (!semaphore.tryAcquire()) {
            log.warn((Object)"Another job is still processing merchant tag expiration. Exiting...");
            return;
        }
        JobDataMap jobDataMap = jobContext.getMergedJobDataMap();
        int limit = jobDataMap.getInt("limit");
        long throttle = jobDataMap.getInt("throttle");
        int[] defaultTagTypes = new int[]{MerchantTagData.TypeEnum.NON_TOP_MERCHANT_TAG.value(), MerchantTagData.TypeEnum.TOP_MERCHANT_TAG.value()};
        int[] merchantTagTypesToExpire = SystemProperty.getIntArray("MerchantTagTypesToExpire", defaultTagTypes);
        List<Object> expiredTags = new ArrayList();
        try {
            try {
                Account accountBean = (Account)EJBHomeCache.getObject("ejb/Account", AccountHome.class);
                block10: for (int tagTypeIdx : merchantTagTypesToExpire) {
                    MerchantTagData.TypeEnum tagType = MerchantTagData.TypeEnum.fromValue(tagTypeIdx);
                    if (tagType == null) {
                        log.info((Object)("Undefined tag expiration type detected: " + tagTypeIdx));
                        continue;
                    }
                    int offset = 0;
                    boolean dataAvailable = true;
                    try {
                        while (dataAvailable) {
                            if (!SystemProperty.getBool("merchantTagExpirationEnabled", false)) {
                                log.warn((Object)"Merchant Tag Expiration job is disabled...");
                                continue block10;
                            }
                            log.info((Object)("Starting " + tagType.description() + " on " + dateFormat.format(new Date()) + " with limit: " + limit + " and throttle: " + throttle + " AND offset: " + offset));
                            expiredTags.clear();
                            expiredTags = this.getExpiredTags(tagType, accountBean, limit, offset);
                            if (expiredTags.size() == 0) {
                                log.info((Object)"No data available for this range... finished processing");
                                dataAvailable = false;
                                continue;
                            }
                            log.info((Object)("Started processing merchant tag expiry: limit[" + limit + "] offset[" + offset + "] found [" + expiredTags.size() + "]."));
                            offset = ((MerchantTagUserData)expiredTags.get((int)(expiredTags.size() - 1))).id;
                            try {
                                if (throttle > 0L) {
                                    try {
                                        Thread.sleep(throttle);
                                    }
                                    catch (InterruptedException ie) {
                                        // empty catch block
                                    }
                                }
                                accountBean.untagMerchant(null, expiredTags);
                                for (MerchantTagUserData merchantTagUserData : expiredTags) {
                                    log.info((Object)("Cleared tag for: merchant[" + merchantTagUserData.merchantUserName + "] to user[" + merchantTagUserData.userName + "]"));
                                }
                            }
                            catch (Exception e) {
                                for (MerchantTagUserData merchantTagUserData : expiredTags) {
                                    log.warn((Object)("Failed to untag merchant for user: " + merchantTagUserData.userName + ", reason:" + e.getLocalizedMessage()));
                                }
                            }
                        }
                    }
                    catch (Exception e) {
                        log.info((Object)("An error occurred while retrieving expiring " + tagType.description() + ": " + e.getMessage()));
                    }
                }
                Object var21_25 = null;
                semaphore.release();
            }
            catch (Exception e) {
                log.info((Object)("An error occurred while expiring merchant tags: " + e.getMessage()));
                Object var21_26 = null;
                semaphore.release();
            }
        }
        catch (Throwable throwable) {
            Object var21_27 = null;
            semaphore.release();
            throw throwable;
        }
    }

    private List<MerchantTagUserData> getExpiredTags(MerchantTagData.TypeEnum tagType, Account accountBean, int limit, int offset) throws Exception {
        try {
            if (tagType == MerchantTagData.TypeEnum.NON_TOP_MERCHANT_TAG) {
                return accountBean.getExpiredNonTopMerchantTags(limit, offset);
            }
            if (tagType == MerchantTagData.TypeEnum.TOP_MERCHANT_TAG) {
                return accountBean.getExpiredTopMerchantTags(limit, offset);
            }
            log.error((Object)("Not expiring tags in type: [" + (Object)((Object)tagType) + "]"));
            return null;
        }
        catch (EJBException e) {
            throw new Exception(e.getMessage());
        }
    }
}

