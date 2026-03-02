/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.payment.mol;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.DateTimeUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.payment.PaymentAsynchStatusUpdate;
import com.projectgoth.fusion.payment.PaymentData;
import com.projectgoth.fusion.payment.mol.MOLPayment;
import com.projectgoth.fusion.payment.mol.MOLPaymentData;
import com.projectgoth.fusion.payment.mol.MOLQueryTransactionStatusResult;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.Semaphore;
import org.apache.log4j.Logger;

public class MOLAsynchronousUpdate
extends PaymentAsynchStatusUpdate {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(MOLAsynchronousUpdate.class));
    private static final Semaphore semaphore = new Semaphore(1);

    protected Logger getLogger() {
        return log;
    }

    protected Semaphore getSemaphore() {
        return semaphore;
    }

    public boolean isEnabled() {
        return SystemProperty.getBool(SystemPropertyEntities.Payments_MOL_ASYNCH_UPDATE.ENABLED);
    }

    protected int getVendorTypeFilter() {
        return PaymentData.TypeEnum.MOL.getEnumValue();
    }

    protected Integer getRecordCountsPerIteration() {
        return SystemProperty.getInt(SystemPropertyEntities.Payments_MOL_ASYNCH_UPDATE.MAX_REC_COUNT_PER_FETCH);
    }

    protected int getSleepMillisecPeriodAfterProcessingOneRecord() {
        return SystemProperty.getInt(SystemPropertyEntities.Payments_MOL_ASYNCH_UPDATE.SLEEP_MILLIS_AFTER_ONE_RECORD);
    }

    protected int getSleepMillisecPeriodAfterProcessingOneBatch() {
        return SystemProperty.getInt(SystemPropertyEntities.Payments_MOL_ASYNCH_UPDATE.SLEEP_MILLIS_AFTER_ONE_BATCH);
    }

    public static PaymentData.StatusEnum getPaymentStatus(Date currentTime, Date createdDateTime, long maxPendingLifeSpanSec, int resCode, String txStatus) {
        long elapsedInSec;
        long elapsedInSec2;
        String trimmedTxStatus;
        PaymentData.StatusEnum paymentStatus = PaymentData.StatusEnum.PENDING;
        MOLPayment.MOLResultCodeEnum resCodeEnum = MOLPayment.MOLResultCodeEnum.getMOLResult(resCode);
        paymentStatus = resCodeEnum == MOLPayment.MOLResultCodeEnum.TRANSACTION_SUCCESSFULLY_COMPLETED ? ((trimmedTxStatus = txStatus.trim()).equalsIgnoreCase("1") ? PaymentData.StatusEnum.APPROVED : ((elapsedInSec2 = DateTimeUtils.getElapsedTimeInSeconds(currentTime, createdDateTime)) > maxPendingLifeSpanSec ? PaymentData.StatusEnum.CANCELLED : PaymentData.StatusEnum.PENDING)) : (resCodeEnum == MOLPayment.MOLResultCodeEnum.USER_HAS_CHOSEN_TO_CANCEL_THE_TRANSACTION ? PaymentData.StatusEnum.CANCELLED : (resCodeEnum == MOLPayment.MOLResultCodeEnum.DUPLICATED_MERCHANT_REFERENCE_ID ? PaymentData.StatusEnum.REJECTED : (resCodeEnum == MOLPayment.MOLResultCodeEnum.INVALID_AMOUNT ? PaymentData.StatusEnum.REJECTED : ((elapsedInSec = DateTimeUtils.getElapsedTimeInSeconds(currentTime, createdDateTime)) > maxPendingLifeSpanSec ? (resCodeEnum == MOLPayment.MOLResultCodeEnum.ORDER_NOT_FOUND ? PaymentData.StatusEnum.INVALID : PaymentData.StatusEnum.TIMEOUT) : PaymentData.StatusEnum.PENDING))));
        return paymentStatus;
    }

    protected PaymentData retrieveStatusFromVendor(PaymentData paymentData) throws Exception {
        MOLPaymentData molPaymentData = (MOLPaymentData)paymentData;
        MOLQueryTransactionStatusResult qryTxResult = MOLPayment.getVendorTransactionStatus(molPaymentData.id.toString());
        long currentTimeMillis = System.currentTimeMillis();
        Timestamp currentTime = new Timestamp(currentTimeMillis);
        molPaymentData.assignUpdatedTime(currentTime);
        molPaymentData.vendorStatusUpdResCode = null;
        molPaymentData.asynchStatusUpdResCode = qryTxResult.resCode;
        molPaymentData.asynchStatusUpdStatusCode = qryTxResult.status;
        molPaymentData.vendorTransactionId = StringUtil.isBlank(qryTxResult.vendorTransactionId) ? "" : qryTxResult.vendorTransactionId;
        int resCodeInt = Integer.parseInt(qryTxResult.resCode.trim());
        long maxPendingLifeSpanSec = SystemProperty.getLong(SystemPropertyEntities.Payments_MOL_ASYNCH_UPDATE.MAX_PENDING_LIFE_SPAN_SEC);
        molPaymentData.status = MOLAsynchronousUpdate.getPaymentStatus(currentTime, molPaymentData.fetchCreatedTime(), maxPendingLifeSpanSec, resCodeInt, qryTxResult.status);
        log.info((Object)("ID:" + molPaymentData.id + " qryTxResult: " + qryTxResult + " Mapped to:" + molPaymentData.status));
        return molPaymentData;
    }
}

