/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.ext.Provider
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.restapi.resource;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.EJBExceptionWithErrorCause;
import com.projectgoth.fusion.common.ErrorCause;
import com.projectgoth.fusion.common.HashObjectUtils;
import com.projectgoth.fusion.data.AccountEntryData;
import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.data.CashReceiptData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.AccountLocal;
import com.projectgoth.fusion.interfaces.AccountLocalHome;
import com.projectgoth.fusion.interfaces.MISLocal;
import com.projectgoth.fusion.interfaces.MISLocalHome;
import com.projectgoth.fusion.recommendation.collector.DataCollectorUtils;
import com.projectgoth.fusion.restapi.data.DataHolder;
import com.projectgoth.fusion.restapi.data.FusionRestException;
import com.projectgoth.fusion.restapi.data.ReverseCreditData;
import com.projectgoth.fusion.restapi.data.ThirdPartyPaymentData;
import java.util.Hashtable;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.ext.Provider;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Provider
@Path(value="/account")
public class AccountResource {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(AccountResource.class));

    @POST
    @Path(value="/reverse/transfer_credit")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public DataHolder<Boolean> reverseTransferCredit(DataHolder<ReverseCreditData> postRequest) throws FusionRestException {
        String reqId = AccountResource.generateRequestID();
        ReverseCreditData data = (ReverseCreditData)postRequest.data;
        log.info((Object)("[" + reqId + "];received reverseTransferCredit;data=[" + data + "]"));
        try {
            AccountLocal accountLocal = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
            accountLocal.reverseTransferCredit(data.accountEntryID, data.misUserName, new AccountEntrySourceData(AccountResource.class));
        }
        catch (Exception e) {
            log.error((Object)String.format("[" + reqId + "] Failed to reverse transfer credit, param:%s, due to:%s", data, e), (Throwable)e);
            throw new FusionRestException(101, "[" + reqId + "] Internal Error, Failed to reverse transfer credit, please try again later.");
        }
        return new DataHolder<Boolean>(true);
    }

    @POST
    @Path(value="/reverse/ttcredit")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public DataHolder<Boolean> reverseTTCredit(DataHolder<ReverseCreditData> postRequest) throws FusionRestException {
        ReverseCreditData data = (ReverseCreditData)postRequest.data;
        try {
            AccountLocal accountLocal = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
            accountLocal.reverseTTCredit(data.accountEntryID, data.misUserName, new AccountEntrySourceData(AccountResource.class));
        }
        catch (Exception e) {
            log.error((Object)String.format("Failed to reverse transfer credit, param:%s, due to:%s", data, e), (Throwable)e);
            throw new FusionRestException(101, "Internal Error, Failed to reverse transfer credit, please try again later");
        }
        return new DataHolder<Boolean>(true);
    }

    private static String generateRequestID() {
        return DataCollectorUtils.newErrorID();
    }

    @POST
    @Path(value="/cashreceipt/match")
    @Consumes(value={"application/json"})
    public void matchCashReceipt(DataHolder<CashReceiptData> cashReceiptDataHolder) throws FusionRestException {
        String reqId = AccountResource.generateRequestID();
        CashReceiptData cashReceiptData = (CashReceiptData)cashReceiptDataHolder.data;
        log.info((Object)("[" + reqId + "];received matchCashReceipt;data=[" + cashReceiptData + "]"));
        Integer id = cashReceiptData.id;
        if (id == null) {
            String msg = "[" + reqId + "] Cash receipt id must not be null";
            log.warn((Object)msg);
            throw new FusionRestException(FusionRestException.RestException.MANDATORY_FIELD_MISSING.getCode(), msg);
        }
        try {
            MISLocal misBean = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            misBean.matchCashReceipt(cashReceiptData, new AccountEntrySourceData(AccountResource.class));
        }
        catch (Exception e) {
            log.error((Object)String.format("[" + reqId + "] Failed to match  cash receipt, id:%s, due to:%s", id, e), (Throwable)e);
            throw new FusionRestException(101, "[" + reqId + "] Internal Error, Failed to match  cash receipt id [" + id + "].Exception:" + e.getMessage());
        }
    }

    @POST
    @Path(value="/cashreceipt/create")
    @Consumes(value={"application/json"})
    public DataHolder<CashReceiptData> createCashReceipt(DataHolder<CashReceiptData> cashReceiptDataHolder) throws FusionRestException {
        String reqId = AccountResource.generateRequestID();
        CashReceiptData cashReceiptData = (CashReceiptData)cashReceiptDataHolder.data;
        log.info((Object)("[" + reqId + "];received createCashReceipt;data=[" + cashReceiptData + "]"));
        try {
            MISLocal misBean = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            CashReceiptData result = misBean.createCashReceipt(cashReceiptData, new AccountEntrySourceData(AccountResource.class));
            log.info((Object)("[" + reqId + "];createCashReceipt inserted new id=[" + cashReceiptData.id + "]"));
            return new DataHolder<CashReceiptData>(result);
        }
        catch (Exception e) {
            log.error((Object)String.format("[" + reqId + "] Failed to create  cash receipt, due to:%s", e), (Throwable)e);
            throw new FusionRestException(101, "[" + reqId + "] Internal Error, Failed to create  cash receipt:" + e.getMessage());
        }
    }

    @POST
    @Path(value="{username}/paythirdparty")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public DataHolder<AccountEntryData> thirdPartyAPIDebit(@PathParam(value="username") String username, DataHolder<ThirdPartyPaymentData> paymentDataHolder) throws FusionRestException {
        ThirdPartyPaymentData paymentData = (ThirdPartyPaymentData)paymentDataHolder.data;
        try {
            AccountLocal accountEJB = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
            AccountEntryData accountEntryData = accountEJB.thirdPartyAPIDebit(username, paymentData.reference, paymentData.description, paymentData.amount, paymentData.currency, new AccountEntrySourceData(paymentData.ipAddress, paymentData.sessionId, paymentData.mobileDevice, paymentData.userAgent));
            log.info((Object)("[" + paymentData.reference + "];thirdPartyAPIDebit inserted new id=[" + accountEntryData.id + "]"));
            return new DataHolder<AccountEntryData>(accountEntryData);
        }
        catch (Exception e) {
            FusionRestException.RestException errorCode = FusionRestException.RestException.ERROR;
            if (e instanceof EJBExceptionWithErrorCause && ((EJBExceptionWithErrorCause)((Object)e)).getErrorCause() == ErrorCause.TransferCreditErrorReasonType.INSUFFICIENT_CREDIT) {
                errorCode = FusionRestException.RestException.INSUFFICIENT_CREDIT;
            }
            log.error((Object)String.format("[" + paymentData.reference + "] Failed to do third party payment for user(%s), due to:%s", username, e), (Throwable)e);
            throw new FusionRestException(errorCode, "[" + paymentData.reference + "] Internal Error, Failed to do third party payment:" + e.getMessage());
        }
    }

    @GET
    @Path(value="getaccountentry")
    @Produces(value={"application/json"})
    public DataHolder<Hashtable> getAccountEntry(@QueryParam(value="id") long id) throws FusionRestException {
        try {
            AccountLocal accountBean = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
            return new DataHolder<Hashtable>(HashObjectUtils.dataObjectToHashtable(accountBean.getAccountEntry(id)));
        }
        catch (Exception e) {
            log.error((Object)String.format("Error in getaccountentry id:%s", id), (Throwable)e);
            throw new FusionRestException(101, "Unable to getaccountentry");
        }
    }
}

