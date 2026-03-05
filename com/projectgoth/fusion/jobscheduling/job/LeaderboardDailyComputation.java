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
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.leaderboard.Leaderboard;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class LeaderboardDailyComputation
implements Job {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(Leaderboard.class));
    private static final Semaphore semaphore = new Semaphore(1);
    private static final Set<Leaderboard.Type> TO_EXCLUDE = new HashSet<Leaderboard.Type>(Arrays.asList(Leaderboard.Type.MIG_LEVEL));

    public void execute(JobExecutionContext context) throws JobExecutionException {
        boolean semaphoreAquired = false;
        try {
            semaphoreAquired = semaphore.tryAcquire();
            if (!semaphoreAquired) {
                log.warn((Object)"Another job is still processing user referrals. Exiting...");
                return;
            }
        }
        catch (Exception e) {
            log.error((Object)"Unable to acquire semaphore");
            return;
        }
        if (!SystemProperty.getBool("EnableLeaderboards", false)) {
            log.warn((Object)"Leaderboards disabled through SystemProperty.EnableLeaderboards key");
            return;
        }
        try {
            log.info((Object)"Leaderboard update for MigLevel");
            Leaderboard.updateMigLevel();
            log.info((Object)"[COMPLETED] Leaderboard update for MigLevel");
        }
        catch (Exception e) {
            log.error((Object)("Unable to process job MigLevels: " + e.getLocalizedMessage()));
        }
        for (Leaderboard.Type t : Leaderboard.Type.values()) {
            if (TO_EXCLUDE.contains((Object)t)) continue;
            this.executeJob(t);
        }
        try {
            if (semaphoreAquired) {
                semaphore.release();
            }
        }
        catch (Exception e) {
            log.warn((Object)("Unable to release semaphore: " + e.getMessage()));
            throw new JobExecutionException((Throwable)e);
        }
    }

    private void executeJob(Leaderboard.Type leaderboardType) {
        String item = leaderboardType.toString() + Leaderboard.Period.DAILY.toString();
        try {
            log.info((Object)("Leaderboard reset for " + item));
            Leaderboard.reset(leaderboardType, Leaderboard.Period.DAILY, Leaderboard.Period.PREVIOUS_DAILY);
            log.info((Object)("[COMPLETED] Leaderboard reset for " + item));
        }
        catch (Exception e) {
            log.error((Object)("Unable to process job " + item + ": " + e.getLocalizedMessage()));
        }
    }
}

