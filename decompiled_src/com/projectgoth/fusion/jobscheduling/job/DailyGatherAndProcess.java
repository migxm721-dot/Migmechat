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
import com.projectgoth.fusion.common.IcePrxFinder;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.slice.ReputationServicePrx;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class DailyGatherAndProcess
implements Job {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(DailyGatherAndProcess.class));

    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            if (!SystemProperty.getBool(SystemPropertyEntities.JobSchedulerSettings.REPUTATION_DAILY_GATHER_AND_PROCESS_ENABLED)) {
                log.info((Object)"Not running DailyGatherAndProcess job since disabled in system property");
                return;
            }
            log.info((Object)"triggering DailyGatherAndProcess");
            ReputationServicePrx reputationServiceProxy = (ReputationServicePrx)context.getScheduler().getContext().get((Object)"reputationServiceProxy");
            if (reputationServiceProxy == null) {
                IcePrxFinder icePrxFinder = (IcePrxFinder)context.getScheduler().getContext().get((Object)"icePrxFinder");
                reputationServiceProxy = icePrxFinder.waitForReputationServiceProxy();
            }
            reputationServiceProxy.gatherAndProcess();
        }
        catch (Exception e) {
            log.error((Object)"failed to reputationServiceProxy.gatherAndProcess()", (Throwable)e);
        }
    }
}

