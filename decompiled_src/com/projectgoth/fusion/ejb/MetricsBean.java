/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBException
 *  javax.ejb.SessionBean
 *  javax.ejb.SessionContext
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.ejb;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.ejb.EJBIcePrxFinder;
import com.projectgoth.fusion.ejb.util.LookupUtil;
import com.projectgoth.fusion.recommendation.collector.CollectedDataTypeEnum;
import com.projectgoth.fusion.rewardsystem.instrumentation.RewardProgramTriggerSampleCategory;
import com.projectgoth.fusion.rewardsystem.instrumentation.SampleCategory;
import com.projectgoth.fusion.rewardsystem.instrumentation.SampleSummary;
import com.projectgoth.fusion.slice.CollectedRewardProgramTriggerSummaryDataIce;
import com.projectgoth.fusion.slice.RecommendationDataCollectionServicePrx;
import java.rmi.RemoteException;
import java.security.SecureRandom;
import java.util.Collection;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.sql.DataSource;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MetricsBean
implements SessionBean {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(MetricsBean.class));
    private DataSource dataSourceMaster;
    private DataSource dataSourceSlave;
    private DataSource userRegistrationMaster;
    private DataSource userRegistrationSlave;
    private SecureRandom randomGen;
    private SessionContext context;

    public void setSessionContext(SessionContext newContext) throws EJBException {
        this.context = newContext;
    }

    public void ejbRemove() throws EJBException, RemoteException {
    }

    public void ejbActivate() throws EJBException, RemoteException {
    }

    public void ejbPassivate() throws EJBException, RemoteException {
    }

    public void ejbCreate() throws CreateException {
        try {
            this.dataSourceMaster = LookupUtil.getFusionMasterDataSource();
            this.dataSourceSlave = LookupUtil.getFusionSlaveDataSource();
            this.userRegistrationMaster = LookupUtil.getRegistrationMasterDataSource();
            this.userRegistrationSlave = LookupUtil.getRegistrationSlaveDataSource();
            this.randomGen = new SecureRandom();
            SystemProperty.ejbInit(this.dataSourceSlave);
        }
        catch (Exception e) {
            log.error((Object)"Unable to create Metrics EJB", (Throwable)e);
            throw new CreateException("Unable to create Metrics EJB: " + e.getMessage());
        }
    }

    public boolean logMetricsSampleSummaries(String host, String instance, Collection<SampleSummary> sampleSummaries) {
        boolean result = true;
        for (SampleSummary sampleSummary : sampleSummaries) {
            RecommendationDataCollectionServicePrx prx = EJBIcePrxFinder.getRecommendationDataCollectionServicePrx();
            SampleCategory sampleCategory = sampleSummary.getSampleCategory();
            if (sampleCategory instanceof RewardProgramTriggerSampleCategory) {
                RewardProgramTriggerSampleCategory triggerSampleCategory = (RewardProgramTriggerSampleCategory)sampleCategory;
                CollectedRewardProgramTriggerSummaryDataIce triggerSummaryDataIce = new CollectedRewardProgramTriggerSummaryDataIce();
                triggerSummaryDataIce.createTimestamp = System.currentTimeMillis();
                triggerSummaryDataIce.id = sampleSummary.getSummaryId();
                triggerSummaryDataIce.dataType = CollectedDataTypeEnum.REWARD_PROGRAM_TRIGGER_SUMMARY.getCode();
                triggerSummaryDataIce.dequeuedCount = sampleSummary.getCountTimeSpentProcessingAfterDequeue();
                triggerSummaryDataIce.droppedCount = sampleSummary.getDroppedCount();
                triggerSummaryDataIce.failedCount = sampleSummary.getFailedCount();
                triggerSummaryDataIce.host = host;
                triggerSummaryDataIce.instance = instance;
                triggerSummaryDataIce.maxProcessingTimeAfterDequeue = sampleSummary.getMaxTimeSpentProcessingAfterDequeue();
                triggerSummaryDataIce.maxReceivedTimestamp = sampleSummary.getMaxReceivedTimestamp();
                triggerSummaryDataIce.maxTimeSpentInQueue = sampleSummary.getMaxTimeSpentInQueue();
                triggerSummaryDataIce.meanProcessingTimeAfterDequeue = sampleSummary.getMeanTimeSpentProcessingAfterDequeue();
                triggerSummaryDataIce.meanTimeSpentInQueue = sampleSummary.getMeanTimeSpentInQueue();
                triggerSummaryDataIce.minProcessingTimeAfterDequeue = sampleSummary.getMinTimeSpentProcessingAfterDequeue();
                triggerSummaryDataIce.minReceivedTimestamp = sampleSummary.getMinReceivedTimestamp();
                triggerSummaryDataIce.minTimeSpentInQueue = sampleSummary.getMinTimeSpentInQueue();
                triggerSummaryDataIce.programType = triggerSampleCategory.intValue();
                triggerSummaryDataIce.receivedCount = sampleSummary.getReceivedCount();
                triggerSummaryDataIce.successfulCount = sampleSummary.getSuccessfulCount();
                triggerSummaryDataIce.varianceProcessingTimeAfterDequeue = sampleSummary.getVarianceTimeSpentProcessingAfterDequeue();
                triggerSummaryDataIce.varianceTimeSpentInQueue = sampleSummary.getVarianceTimeSpentInQueue();
                try {
                    prx.logData(triggerSummaryDataIce);
                }
                catch (Exception ex) {
                    result = false;
                    log.error((Object)("Error logging data. Exception:" + ex), (Throwable)ex);
                }
                continue;
            }
            log.warn((Object)("Ignoring unsupported sample category:[" + sampleCategory + "].Sample Summary:[" + sampleSummary + "]"));
        }
        return result;
    }
}

