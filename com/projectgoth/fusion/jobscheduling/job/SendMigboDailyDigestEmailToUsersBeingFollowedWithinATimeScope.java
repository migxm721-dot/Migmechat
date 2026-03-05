/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.quartz.Job
 *  org.quartz.JobExecutionContext
 *  org.quartz.JobExecutionException
 */
package com.projectgoth.fusion.jobscheduling.job;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.MiniblogDailyDigestHelper;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import java.util.concurrent.Semaphore;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class SendMigboDailyDigestEmailToUsersBeingFollowedWithinATimeScope
implements Job {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(SendMigboDailyDigestEmailToUsersBeingFollowedWithinATimeScope.class));
    private static final Semaphore semaphore = new Semaphore(1);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        if (!SystemProperty.getBool(SystemPropertyEntities.JobSchedulerSettings.DAILY_DIGEST_FOR_USERS_BEING_FOLLOWED_ENABLED)) {
            log.info((Object)"Daily digest is disable");
            return;
        }
        boolean semaphoreAquired = false;
        try {
            semaphoreAquired = semaphore.tryAcquire();
            if (!semaphoreAquired) {
                log.warn((Object)"Another job is still triggering the Migbo Daily Digest Emails To Users Being Followed Within A Cerain Time Scope. Exiting...");
                Object var6_3 = null;
                if (semaphoreAquired) {
                    semaphore.release();
                }
                return;
            }
            log.info((Object)"Migbo Daily Digest Email To Users Being Followed Within A Cerain Time Scope Trigger [START]");
            long total = MiniblogDailyDigestHelper.sendRecentlyFollowedDailyDigest(System.currentTimeMillis());
            MiniblogDailyDigestHelper.truncateSortedSetAndHashForDailyDigest();
            log.info((Object)("Migbo Daily Digest Email To Users Being Followed Within A Cerain Time Scope Trigger [END], total users:" + total));
        }
        catch (Throwable throwable) {
            Object var6_5 = null;
            if (semaphoreAquired) {
                semaphore.release();
            }
            throw throwable;
        }
        Object var6_4 = null;
        if (semaphoreAquired) {
            semaphore.release();
        }
    }
}

