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
import com.projectgoth.fusion.dao.LeaderboardDAO;
import com.projectgoth.fusion.jobscheduling.job.UserReferralNotification;
import java.util.concurrent.Semaphore;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class LeaderboardComputation
implements Job {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(UserReferralNotification.class));
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
                LeaderboardDAO leaderboardDAO = (LeaderboardDAO)context.getScheduler().getContext().get((Object)"leaderboardDAO");
                leaderboardDAO.generateLeaderboard(1);
                leaderboardDAO.generateLeaderboard(2);
                leaderboardDAO.generateLeaderboard(3);
                leaderboardDAO.generateLeaderboard(4);
                leaderboardDAO.generateLeaderboard(5);
                leaderboardDAO.generateLeaderboard(6);
                leaderboardDAO.generateLeaderboard(7);
                leaderboardDAO.generateLeaderboard(8);
                leaderboardDAO.generateLeaderboard(9);
                leaderboardDAO.generateLeaderboard(10);
                leaderboardDAO.generateLeaderboard(11);
            }
            catch (Exception e) {
                log.error((Object)"xxxxx", (Throwable)e);
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

