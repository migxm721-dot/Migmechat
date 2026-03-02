/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.quartz.Job
 *  org.quartz.JobDataMap
 *  org.quartz.JobExecutionContext
 *  org.quartz.JobExecutionException
 */
package com.projectgoth.fusion.jobscheduling.job;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.IcePrxFinder;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.slice.RecommendationGenerationServicePrx;
import java.util.concurrent.Semaphore;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class RecommendationGenerationServiceJob
implements Job {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(RecommendationGenerationServiceJob.class));
    private static final Semaphore semaphore = new Semaphore(1);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void execute(JobExecutionContext context) throws JobExecutionException {
        if (!semaphore.tryAcquire()) {
            log.warn((Object)"Another job is processing this recommendation generation transformation. Exiting...");
            return;
        }
        try {
            try {
                if (!SystemProperty.getBool(SystemPropertyEntities.RecommendationServiceSettings.GENERATION_SERVICE_VIA_QUARTZ_SCHEDULER_ENABLED)) {
                    log.info((Object)"Not running RecommendationGenerationServiceJob via quartz scheduler since disabled in system property");
                    Object var7_2 = null;
                    semaphore.release();
                    return;
                }
                log.info((Object)"triggering RecommendationGenerationServiceJob via quartz scheduler");
                IcePrxFinder icePrxFinder = (IcePrxFinder)context.getScheduler().getContext().get((Object)"icePrxFinder");
                RecommendationGenerationServicePrx rgsProxy = icePrxFinder.waitForRecommendationGenerationServiceProxy();
                JobDataMap jobDataMap = context.getMergedJobDataMap();
                int transID = jobDataMap.getInt("transformationID");
                rgsProxy.runTransformation(transID);
            }
            catch (Exception e) {
                log.error((Object)"failed to recommendationGenerationServiceProxy.invokeJob()", (Throwable)e);
                Object var7_4 = null;
                semaphore.release();
                return;
            }
        }
        catch (Throwable throwable) {
            Object var7_5 = null;
            semaphore.release();
            throw throwable;
        }
        Object var7_3 = null;
        semaphore.release();
    }
}

