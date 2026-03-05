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
import com.projectgoth.fusion.fashionshow.AvatarCandidates;
import java.util.concurrent.Semaphore;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class AvatarCandidatePooling
implements Job {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(AvatarCandidatePooling.class));
    private static final Semaphore semaphore = new Semaphore(1);

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void execute(JobExecutionContext context) throws JobExecutionException {
        boolean semaphoreAquired = false;
        try {
            try {
                semaphoreAquired = semaphore.tryAcquire();
                if (!semaphoreAquired) {
                    log.warn((Object)"Another job is still processing user referrals. Exiting...");
                    Object var5_3 = null;
                    if (!semaphoreAquired) return;
                    semaphore.release();
                    return;
                }
                log.info((Object)"Populating avatar candidates");
                AvatarCandidates.populateAvatarCandidates();
                log.info((Object)"[COMPLETED] Populating avatar candidates");
            }
            catch (Exception e) {
                log.error((Object)("Unable to populate avatar candidate pool: " + e.getMessage()), (Throwable)e);
                throw new JobExecutionException((Throwable)e);
            }
        }
        catch (Throwable throwable) {
            Object var5_5 = null;
            if (!semaphoreAquired) throw throwable;
            semaphore.release();
            throw throwable;
        }
        Object var5_4 = null;
        if (!semaphoreAquired) return;
        semaphore.release();
    }
}

