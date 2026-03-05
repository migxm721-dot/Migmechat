/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Application
 *  Ice.Object
 *  Ice.ObjectAdapter
 *  Ice.Properties
 *  Ice.Util
 *  org.apache.log4j.Logger
 *  org.springframework.beans.factory.annotation.Required
 */
package com.projectgoth.fusion.jobscheduling;

import Ice.Application;
import Ice.Object;
import Ice.ObjectAdapter;
import Ice.Properties;
import Ice.Util;
import com.projectgoth.fusion.common.IcePrxFinder;
import com.projectgoth.fusion.jobscheduling.JobSchedulingServiceAdminI;
import com.projectgoth.fusion.jobscheduling.JobSchedulingServiceI;
import com.projectgoth.fusion.slice.UserNotificationServicePrx;
import com.projectgoth.fusion.stats.IceStats;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

public class JobSchedulingService
extends Application {
    private static Logger log = Logger.getLogger(JobSchedulingService.class);
    public static ObjectAdapter JobSchedulingServiceAdapter = null;
    public static Properties properties = null;
    public static long startTime = System.currentTimeMillis();
    private String hostName;
    private JobSchedulingServiceI jobSchedulingServiceServant;
    private IcePrxFinder icePrxFinder;

    @Required
    public void setJobSchedulingServiceServant(JobSchedulingServiceI jobSchedulingServiceServant) {
        this.jobSchedulingServiceServant = jobSchedulingServiceServant;
    }

    private void configureServant(Properties properties) throws Exception {
        this.icePrxFinder = new IcePrxFinder(JobSchedulingService.communicator(), properties);
        UserNotificationServicePrx unsProxy = this.icePrxFinder.waitForUserNotificationServiceProxy();
        if (unsProxy == null) {
            log.fatal((java.lang.Object)"Aborting, weren't able to find a UNS proxy..");
            System.exit(1);
        }
        this.jobSchedulingServiceServant.setIcePrxFinder(this.icePrxFinder);
        this.jobSchedulingServiceServant.setUserNotificationServiceProxy(unsProxy);
        this.jobSchedulingServiceServant.initializeScheduler();
        this.jobSchedulingServiceServant.scheduleDailyGatherAndProcessJob(properties.getPropertyWithDefault("DailyGatherAndProcessCronExpression", "0 0 2 ? * *"));
        int interval = properties.getPropertyAsIntWithDefault("SubscriptionRenewalInterval", 900000);
        if (interval > 0) {
            this.jobSchedulingServiceServant.scheduleSubscriptionRenewal(System.currentTimeMillis(), interval);
        }
        if ((interval = properties.getPropertyAsIntWithDefault("UserReferralNotificationInterval", 900000)) > 0) {
            this.jobSchedulingServiceServant.scheduleUserReferralNotification(System.currentTimeMillis(), interval);
        }
        this.jobSchedulingServiceServant.scheduleLeaderboardWeeklyComputation(properties.getPropertyWithDefault("LeaderboardWeeklyUpdateCronExpression", "0 0 4 ? * 7"));
        this.jobSchedulingServiceServant.schedulePaintWarsWeeklyRaffles(properties.getPropertyWithDefault("PaintWarsWeeklyRafflesUpdateCronExpression", "0 0 4 ? * 7"));
        this.jobSchedulingServiceServant.scheduleLeaderboardDailyComputation(properties.getPropertyWithDefault("LeaderboardDailyUpdateCronExpression", "0 0 4 * * ?"));
        this.jobSchedulingServiceServant.scheduleMerchantTrail(properties.getPropertyWithDefault("MerchantTrailCronExpression", "0 0 2 ? * *"));
        this.jobSchedulingServiceServant.scheduleMerchantTagExpiry(properties.getPropertyWithDefault("MerchantTagExpiry.cronExpression", "0 0 2 ? * *"), properties.getPropertyAsIntWithDefault("MerchantTagExpiry.limit", 1000), properties.getPropertyAsIntWithDefault("MerchantTagExpiry.throttle", 10));
        this.jobSchedulingServiceServant.scheduleAvatarCandidatePooling(properties.getPropertyWithDefault("AvatarCandidatePoolingCronExpression", "0 0 2 * * ?"));
        this.jobSchedulingServiceServant.scheduleMigboRSSFeedGeneration(properties.getPropertyWithDefault("MigboRSSFeedGenerationCronExpression", "0 0 * * * ?"));
        this.jobSchedulingServiceServant.scheduleMigboEmailDailyDigestSending(properties.getPropertyWithDefault("MigboDailyDigestEmailCronExpression", "0 0 8 * * ?"));
        this.jobSchedulingServiceServant.scheduleMigboEmailDailyDigestSendingToUsersBeingFollowed(properties.getPropertyWithDefault("MigboDailyDigestEmailToUsersBeingFollowedCronExpression", "0 0 8 * * ?"));
        this.jobSchedulingServiceServant.scheduleVasComputation(properties.getPropertyWithDefault("VasUpdateCronExpression", "0 0 5 * * ?"));
        interval = properties.getPropertyAsIntWithDefault("TopMerchantTagging", 900000);
        if (interval > 0) {
            this.jobSchedulingServiceServant.scheduleTopMerchantTagging(System.currentTimeMillis(), interval);
        }
        if ((interval = properties.getPropertyAsIntWithDefault("MOLAsynchronousUpdateInterval", 300000)) > 0) {
            this.jobSchedulingServiceServant.scheduleMOLAsynchronousUpdate(System.currentTimeMillis(), interval);
        }
        if ((interval = properties.getPropertyAsIntWithDefault("MIMOPayAsynchronousUpdateInterval", 300000)) > 0) {
            this.jobSchedulingServiceServant.scheduleMIMOPayAsynchronousUpdate(System.currentTimeMillis(), interval);
        }
        if ((interval = properties.getPropertyAsIntWithDefault("PaypalAsynchronousUpdateInterval", 300000)) > 0) {
            this.jobSchedulingServiceServant.schedulePaypalAsynchronousUpdate(System.currentTimeMillis(), interval);
        }
        if ((interval = properties.getPropertyAsIntWithDefault("RecommendationGenerationServiceInterval", 300000)) > 0) {
            this.jobSchedulingServiceServant.scheduleRecommendationGeneration(System.currentTimeMillis(), interval);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public int run(String[] arg0) {
        properties = JobSchedulingService.communicator().getProperties();
        try {
            try {
                this.configureServant(properties);
                log.info((java.lang.Object)("Configured endpoint [" + properties.getProperty("JobSchedulingServiceAdapter.Endpoints") + "]"));
                try {
                    this.hostName = InetAddress.getLocalHost().getHostName().toUpperCase();
                }
                catch (UnknownHostException e) {
                    this.hostName = "UNKNOWN";
                }
                log.debug((java.lang.Object)"Initialising JobSchedulingService interface");
                JobSchedulingServiceAdapter = JobSchedulingService.communicator().createObjectAdapter("JobSchedulingServiceAdapter");
                JobSchedulingServiceAdapter.add((Object)this.jobSchedulingServiceServant, Util.stringToIdentity((String)"JobSchedulingService"));
                IceStats.getInstance().setIceObjects(JobSchedulingService.communicator(), JobSchedulingServiceAdapter, null);
                log.debug((java.lang.Object)"Initialising JobSchedulingServiceAdmin interface");
                ObjectAdapter jobSchedulingServiceAdminAdapter = JobSchedulingService.communicator().createObjectAdapter("JobSchedulingServiceAdminAdapter");
                JobSchedulingServiceAdminI jobSchedulingServiceAdmin = new JobSchedulingServiceAdminI(this.jobSchedulingServiceServant);
                jobSchedulingServiceAdminAdapter.add((Object)jobSchedulingServiceAdmin, Util.stringToIdentity((String)"JobSchedulingServiceAdmin"));
                jobSchedulingServiceAdminAdapter.activate();
                JobSchedulingServiceAdapter.activate();
                log.info((java.lang.Object)"Service started");
                JobSchedulingService.communicator().waitForShutdown();
            }
            catch (Exception e) {
                log.error((java.lang.Object)"failed to configure servant", (Throwable)e);
                java.lang.Object var5_7 = null;
                if (!JobSchedulingService.interrupted()) return 0;
                log.fatal((java.lang.Object)("JobSchedulingService " + this.hostName + ": terminating"));
                this.jobSchedulingServiceServant.shutdown();
                return 0;
            }
            java.lang.Object var5_6 = null;
        }
        catch (Throwable throwable) {
            java.lang.Object var5_8 = null;
            if (!JobSchedulingService.interrupted()) throw throwable;
            log.fatal((java.lang.Object)("JobSchedulingService " + this.hostName + ": terminating"));
            this.jobSchedulingServiceServant.shutdown();
            throw throwable;
        }
        if (!JobSchedulingService.interrupted()) return 0;
        log.fatal((java.lang.Object)("JobSchedulingService " + this.hostName + ": terminating"));
        this.jobSchedulingServiceServant.shutdown();
        return 0;
    }
}

