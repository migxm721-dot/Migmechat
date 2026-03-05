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
import com.projectgoth.fusion.common.MemCachedClientWrapper;
import com.projectgoth.fusion.common.MemCachedKeySpaces;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.AccountEntryData;
import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.data.UserReferralData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Account;
import com.projectgoth.fusion.interfaces.AccountHome;
import com.projectgoth.fusion.interfaces.Message;
import com.projectgoth.fusion.interfaces.MessageHome;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import com.projectgoth.fusion.jobscheduling.JobSchedulingService;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class UserReferralNotification
implements Job {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(UserReferralNotification.class));
    private static final Semaphore semaphore = new Semaphore(1);
    private static int fromActivationID = 0;

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
                log.info((Object)("Triggering UserReferralNotification. From activation ID = " + fromActivationID));
                this.processUserReferralNotifications();
            }
            catch (Exception e) {
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

    private void processUserReferralNotifications() throws CreateException, RemoteException {
        int processed = 0;
        int credited = 0;
        int failedCounter = 0;
        HashMap<UserReferralData, String> failedReferrals = new HashMap<UserReferralData, String>();
        Account accountEJB = (Account)EJBHomeCache.getObject("ejb/Account", AccountHome.class);
        User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
        List unpaidUserReferrals = accountEJB.getUnpaidUserReferrals(fromActivationID);
        Collections.sort(unpaidUserReferrals, new ActivationIDComparator());
        if (unpaidUserReferrals.size() > 0) {
            log.info((Object)("Processing " + unpaidUserReferrals.size() + " unpaid user referrals from activation ID " + ((UserReferralData)unpaidUserReferrals.get((int)0)).activationID + " to " + ((UserReferralData)unpaidUserReferrals.get((int)(unpaidUserReferrals.size() - 1))).activationID));
        }
        for (UserReferralData userReferralData : unpaidUserReferrals) {
            try {
                AccountEntryData accountEntryData = null;
                if (!userReferralData.username.equalsIgnoreCase(userReferralData.referredUsername)) {
                    accountEntryData = accountEJB.creditReferrer(userReferralData, new AccountEntrySourceData(JobSchedulingService.class));
                    if (SystemProperty.getBool(SystemPropertyEntities.Default.RESET_REFERRAL_SUCCESS_RATIO_ON_CREDIT_AWARD)) {
                        try {
                            userEJB.getUserReferralSuccessRate(userReferralData.username, true);
                        }
                        catch (Exception ee) {
                            MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.USER_REFERRAL_SUCCESS_RATE, userReferralData.username);
                            log.warn((Object)("Unable to reset user referral success rate [" + userReferralData.username + "] : " + ee.getMessage()), (Throwable)ee);
                        }
                    }
                } else {
                    log.warn((Object)("Self referral: username[" + userReferralData.username + "], referrer name[" + userReferralData.referrerName + "]... skipping credit"));
                }
                if (accountEntryData != null) {
                    ++credited;
                }
                if (failedReferrals.size() == 0) {
                    fromActivationID = userReferralData.activationID + 1;
                }
                ++processed;
                failedCounter = 0;
            }
            catch (Exception e) {
                ++failedCounter;
                log.error((Object)("Unable to process user referral ID " + userReferralData.id + ", Referrer [" + userReferralData.username + "]. Reason: " + e.getLocalizedMessage()));
                if (failedReferrals.size() == 0) {
                    fromActivationID = userReferralData.activationID;
                }
                failedReferrals.put(userReferralData, e.getLocalizedMessage());
                if (failedCounter != 3) continue;
                break;
            }
        }
        log.info((Object)("Successfully processed " + processed + " user referral notification(s), " + credited + " credited, " + failedReferrals.size() + " failed"));
        if (failedReferrals.size() > 0) {
            try {
                Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
                StringBuffer content = new StringBuffer();
                content.append("Processing the following user referrals failed: ");
                content.append(System.getProperty("line.separator"));
                content.append(System.getProperty("line.separator"));
                for (UserReferralData userReferralData : failedReferrals.keySet()) {
                    content.append("Referral ID: [").append(userReferralData.id).append("] Referrer: [").append(userReferralData.username).append("] Referred username: [").append(userReferralData.referredUsername).append("] Caused by: [").append((String)failedReferrals.get(userReferralData)).append("]").append(System.getProperty("line.separator"));
                }
                messageEJB.sendEmailFromNoReply(SystemProperty.get("TechOpsEmail", "techops@mig33global.com"), "Failed user referrals", content.toString());
                messageEJB.sendEmailFromNoReply(SystemProperty.get("KLSupportTeamEmail", "KLSupportTeam@mig33global.com"), "Failed user referrals", content.toString());
            }
            catch (CreateException ce) {
                log.error((Object)("Unable to load MessageBean to send email message... Reason: " + ce.getLocalizedMessage()));
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class ActivationIDComparator
    implements Comparator<UserReferralData> {
        private ActivationIDComparator() {
        }

        @Override
        public int compare(UserReferralData o1, UserReferralData o2) {
            return o1.activationID.compareTo(o2.activationID);
        }
    }
}

