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
package com.projectgoth.fusion.payment;

import com.projectgoth.fusion.common.ErrorCause;
import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Account;
import com.projectgoth.fusion.interfaces.AccountHome;
import com.projectgoth.fusion.payment.PaymentData;
import com.projectgoth.fusion.payment.PaymentException;
import java.util.List;
import java.util.concurrent.Semaphore;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class PaymentAsynchStatusUpdate
implements Job {
    private final String jobName = this.getClass().getName();
    private static final int MAX_DELAY_MULTIPLIER = 15;
    private static final int SUSPEND_ON_ERROR_DELAY_MS = 2000;

    protected PaymentAsynchStatusUpdate() {
    }

    protected abstract Logger getLogger();

    protected abstract Semaphore getSemaphore();

    public String getJobName() {
        return this.jobName;
    }

    public abstract boolean isEnabled();

    protected abstract int getVendorTypeFilter();

    protected abstract Integer getRecordCountsPerIteration();

    protected abstract int getSleepMillisecPeriodAfterProcessingOneRecord();

    protected abstract int getSleepMillisecPeriodAfterProcessingOneBatch();

    protected abstract PaymentData retrieveStatusFromVendor(PaymentData var1) throws Exception;

    protected List<PaymentData> getPaymentTransactions(Account accountEJB, int vendorType, Integer recordCountsPerIteration) throws Exception {
        return accountEJB.getPendingPaymentTransactionsForRequery(vendorType, recordCountsPerIteration);
    }

    protected void updateStatus(Account accountEJB, PaymentData paymentData) throws Exception {
        Logger log = this.getLogger();
        AccountEntrySourceData accountEntrySourceData = new AccountEntrySourceData(this.getClass());
        try {
            accountEJB.updatePayment(paymentData, accountEntrySourceData);
        }
        catch (PaymentException ex) {
            ErrorCause errorCause = ex.getErrorCause();
            if (errorCause == ErrorCause.PaymentErrorReasonType.DB_UPDATE_PAYMENT_FAILED) {
                log.warn((Object)("Unable to update paymentData.id=[" + paymentData.id + "] username = [" + paymentData.username + "].PaymentData state may have changed."));
            }
            log.error((Object)("Error while trying to update status for paymentData.id=" + paymentData.id + " username = " + paymentData.username), (Throwable)((Object)ex));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Logger log = this.getLogger();
        if (!this.getSemaphore().tryAcquire()) {
            log.warn((Object)("Another job is processing . " + this.getJobName() + "  Exiting..."));
            return;
        }
        log.info((Object)(this.getJobName() + " started... "));
        try {
            block22: {
                try {
                    if (this.isEnabled()) {
                        Account accountEJB = (Account)EJBHomeCache.getObject("ejb/Account", AccountHome.class);
                        int vendorType = this.getVendorTypeFilter();
                        Integer recordCountsPerIteration = this.getRecordCountsPerIteration();
                        if (recordCountsPerIteration != null && recordCountsPerIteration < 1) {
                            recordCountsPerIteration = null;
                        }
                        if (log.isDebugEnabled()) {
                            log.debug((Object)("Retrieving pending payment transactions for " + vendorType + " max rows " + recordCountsPerIteration));
                        }
                        List<PaymentData> paymentDataList = null;
                        try {
                            paymentDataList = this.getPaymentTransactions(accountEJB, vendorType, recordCountsPerIteration);
                        }
                        catch (Exception ex) {
                            log.error((Object)("getPaymentTransactions( vendorType:" + vendorType + ",recordCountsPerIteration " + recordCountsPerIteration + ") failed"), (Throwable)ex);
                            throw new JobExecutionException("Error in retrieving payment transactions", (Throwable)ex);
                        }
                        if (log.isDebugEnabled()) {
                            log.debug((Object)("Found " + paymentDataList.size() + " records "));
                        }
                        if (paymentDataList != null) {
                            for (PaymentData paymentData : paymentDataList) {
                                try {
                                    PaymentData newPaymentStatus = this.retrieveStatusFromVendor(paymentData);
                                    if (newPaymentStatus != null) {
                                        this.updateStatus(accountEJB, newPaymentStatus);
                                    } else {
                                        log.error((Object)("retrieveStatusFromVendor(paymentData.id=" + paymentData.id + ") implementation is returning null!"));
                                    }
                                }
                                catch (Exception ex) {
                                    log.error((Object)("Error while trying to update status for paymentData.id=" + paymentData.id + " username = " + paymentData.username), (Throwable)ex);
                                }
                                try {
                                    int delayAfterOneRecord = this.getSleepMillisecPeriodAfterProcessingOneRecord();
                                    Thread.sleep(delayAfterOneRecord);
                                }
                                catch (InterruptedException ie) {}
                            }
                            break block22;
                        }
                        log.warn((Object)"List of PaymentData is null");
                        break block22;
                    }
                    log.info((Object)(this.getJobName() + " disabled. Bailing out \\m/."));
                }
                catch (CreateException e) {
                    log.error((Object)"Error in retrieving beans...", (Throwable)e);
                    Object var11_18 = null;
                    this.getSemaphore().release();
                    log.info((Object)(this.getJobName() + " terminated."));
                    return;
                }
                catch (JobExecutionException e) {
                    log.error((Object)("Unable to continue with job " + this.getJobName()), (Throwable)e);
                    Object var11_19 = null;
                    this.getSemaphore().release();
                    log.info((Object)(this.getJobName() + " terminated."));
                    return;
                }
                catch (Exception e) {
                    log.error((Object)("Unhandled exception while executing " + this.getJobName() + ": "), (Throwable)e);
                    Object var11_20 = null;
                    this.getSemaphore().release();
                    log.info((Object)(this.getJobName() + " terminated."));
                    return;
                }
            }
            Object var11_17 = null;
            this.getSemaphore().release();
            log.info((Object)(this.getJobName() + " terminated."));
            return;
        }
        catch (Throwable throwable) {
            Object var11_21 = null;
            this.getSemaphore().release();
            log.info((Object)(this.getJobName() + " terminated."));
            throw throwable;
        }
    }
}

