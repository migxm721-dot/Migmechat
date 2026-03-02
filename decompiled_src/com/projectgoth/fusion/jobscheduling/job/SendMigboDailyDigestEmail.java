/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  org.apache.log4j.Logger
 *  org.quartz.Job
 *  org.quartz.JobExecutionContext
 *  org.quartz.JobExecutionException
 */
package com.projectgoth.fusion.jobscheduling.job;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.Enums;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.data.UserEmailAddressData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import com.projectgoth.fusion.restapi.util.MigboApiUtil;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class SendMigboDailyDigestEmail
implements Job {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(SendMigboDailyDigestEmail.class));
    private static final Semaphore semaphore = new Semaphore(1);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        boolean semaphoreAquired = false;
        try {
            try {
                long current;
                semaphoreAquired = semaphore.tryAcquire();
                if (!semaphoreAquired) {
                    log.warn((Object)"Another job is still triggering the Migbo Daily Digest Emails. Exiting...");
                    Object var34_3 = null;
                    if (!semaphoreAquired) return;
                    semaphore.release();
                    return;
                }
                log.info((Object)"Migbo Daily Digest Email Trigger [START]");
                User userBean = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
                MigboApiUtil apiUtil = MigboApiUtil.getInstance();
                int windowDurationInDays = SystemProperty.getInt("DailyDigestWindowInDays", 14);
                long ONEDAY_IN_MILLIS = 86400000L;
                long oneDayAgo = current - ONEDAY_IN_MILLIS;
                long windowBoundary = current - (long)windowDurationInDays * ONEDAY_IN_MILLIS;
                long totalProcessed = 0L;
                long totalSentOK = 0L;
                long totalSentFailed = 0L;
                long totalInvalidAddress = 0L;
                long totalInactive = 0L;
                long startTime = System.currentTimeMillis();
                for (current = System.currentTimeMillis(); current > windowBoundary; current -= ONEDAY_IN_MILLIS, oneDayAgo -= ONEDAY_IN_MILLIS) {
                    try {
                        Map users = userBean.getLastLoggedInUsersWithVerifiedEmail(UserEmailAddressData.UserEmailAddressTypeEnum.PRIMARY, new Timestamp(oneDayAgo), new Timestamp(current), null);
                        for (Map.Entry u : users.entrySet()) {
                            ++totalProcessed;
                            String username = (String)u.getKey();
                            Integer userid = (Integer)u.getValue();
                            try {
                                String pathPrefix = String.format("/user/%d/email/%d", userid, Enums.EmailTypeEnum.DAILY_DIGEST.value());
                                String postData = "{}";
                                if (apiUtil.postAndCheckOk(pathPrefix, postData)) {
                                    ++totalSentOK;
                                    log.info((Object)String.format("Successfully triggered daily digest email for [%s][%d] via migbo-datasvc [%s]", username, userid, pathPrefix));
                                    continue;
                                }
                                ++totalSentFailed;
                                log.warn((Object)String.format("Faled to trigger email for [%s][%d] via migbo-datasvc [%s]", username, userid, pathPrefix));
                            }
                            catch (Exception e) {
                                log.error((Object)String.format("Unable to send daily digest email to username[%s] userid[%d]: %s", username, userid, e.getMessage()), (Throwable)e);
                            }
                        }
                        continue;
                    }
                    catch (RemoteException re) {
                        log.error((Object)String.format("Unable to send email for users logged in between [%d] and [%d]: %s", oneDayAgo, current, re.getMessage()), (Throwable)re);
                    }
                }
                long timeTaken = System.currentTimeMillis() - startTime;
                log.info((Object)String.format("Processed [%d] users. Account Inactive [%d] Invalid address [%d]. Sent OK [%d]. Sent Failed [%d]", totalProcessed, totalInactive, totalInvalidAddress, totalSentOK, totalSentFailed));
                log.info((Object)String.format("Migbo Daily Digest Email Trigger [COMPLETE] Time taken: %d seconds", TimeUnit.MILLISECONDS.toSeconds(timeTaken)));
            }
            catch (CreateException ce) {
                log.error((Object)String.format("Unable to load UserEJB - createException caught : %s", ce.getMessage()), (Throwable)ce);
                Object var34_5 = null;
                if (!semaphoreAquired) return;
                semaphore.release();
                return;
            }
        }
        catch (Throwable throwable) {
            Object var34_6 = null;
            if (!semaphoreAquired) throw throwable;
            semaphore.release();
            throw throwable;
        }
        Object var34_4 = null;
        if (!semaphoreAquired) return;
        semaphore.release();
    }
}

