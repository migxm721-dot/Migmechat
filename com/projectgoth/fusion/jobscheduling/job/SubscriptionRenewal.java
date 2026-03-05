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
import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.data.ServiceData;
import com.projectgoth.fusion.data.SubscriptionData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Account;
import com.projectgoth.fusion.interfaces.AccountHome;
import com.projectgoth.fusion.jobscheduling.JobSchedulingService;
import com.projectgoth.fusion.voiceengine.VoiceEngine;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class SubscriptionRenewal
implements Job {
    private static final int CHUNK_SIZE = 100;
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(SubscriptionRenewal.class));
    private static final Semaphore semaphore = new Semaphore(1);
    private Map<Integer, ServiceData> services = new HashMap<Integer, ServiceData>();

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
                    log.warn((Object)"Another job is still processing subscriptions. Exiting...");
                    Object var5_3 = null;
                    if (!semaphoreAquired) return;
                    semaphore.release();
                    return;
                }
                log.info((Object)"Triggering SubscriptionRenewal");
                this.processExpiringSubscriptions();
                this.processExpiredSubscriptions();
                this.processPendingSubscriptions();
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

    private ServiceData getService(Account accountEJB, int serviceID) throws RemoteException {
        ServiceData serviceData = this.services.get(serviceID);
        if (serviceData == null && (serviceData = accountEJB.getService(serviceID)) != null) {
            this.services.put(serviceData.id, serviceData);
        }
        return serviceData;
    }

    private void processExpiringSubscriptions() throws CreateException, RemoteException {
        int fromID = 0;
        int processed = 0;
        int notified = 0;
        int failed = 0;
        Account accountEJB = (Account)EJBHomeCache.getObject("ejb/Account", AccountHome.class);
        List subscriptions = accountEJB.getExpiringSubscriptions(0, 100);
        while (subscriptions.size() > 0) {
            for (SubscriptionData subscription : subscriptions) {
                try {
                    if (!Boolean.TRUE.equals(subscription.expiryReminderSent)) {
                        ServiceData serviceData = this.getService(accountEJB, subscription.serviceID);
                        if (serviceData == null) {
                            throw new Exception("Subscription " + subscription.id + " has unknown service ID " + subscription.serviceID);
                        }
                        if (serviceData.expiryReminderSMS != null) {
                            accountEJB.sendSubscriptionExpiryReminderSMS(subscription.username, subscription.id, serviceData.expiryReminderSMS, new AccountEntrySourceData(JobSchedulingService.class));
                            ++notified;
                        }
                    }
                }
                catch (Exception e) {
                    log.warn((Object)("Unable to process expired subscription " + subscription.id), (Throwable)e);
                    ++failed;
                }
                fromID = subscription.id + 1;
            }
            processed += subscriptions.size();
            subscriptions = accountEJB.getExpiringSubscriptions(fromID, 100);
        }
        log.info((Object)("Successfully processed " + processed + " expiring subscription(s), " + notified + " notification(s) sent, " + failed + " failed"));
    }

    private void processExpiredSubscriptions() throws CreateException, RemoteException, UnknownHostException {
        int fromID = 0;
        int processed = 0;
        int failed = 0;
        AccountEntrySourceData accountEntrySource = new AccountEntrySourceData(VoiceEngine.class);
        Account accountEJB = (Account)EJBHomeCache.getObject("ejb/Account", AccountHome.class);
        List subscriptions = accountEJB.getExpiredSubscriptions(0, 100);
        while (subscriptions.size() > 0) {
            for (SubscriptionData subscription : subscriptions) {
                try {
                    accountEJB.expireSubscription(subscription.username, subscription.id);
                    accountEJB.subscribeService(subscription.username, subscription.serviceID, accountEntrySource);
                    ++processed;
                }
                catch (RemoteException e) {
                    log.warn((Object)("Unable to process expired subscription " + subscription.id), (Throwable)e);
                    ++failed;
                }
                fromID = subscription.id + 1;
            }
            subscriptions = accountEJB.getExpiredSubscriptions(fromID, 100);
        }
        log.info((Object)("Successfully processed " + processed + " expired subscription(s), " + failed + " failed"));
    }

    private void processPendingSubscriptions() throws CreateException, RemoteException {
    }
}

