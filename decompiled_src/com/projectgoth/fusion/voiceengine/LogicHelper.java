/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.voiceengine;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.data.AccountBalanceData;
import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.data.CallData;
import com.projectgoth.fusion.data.CurrencyData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.data.VoucherData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Account;
import com.projectgoth.fusion.interfaces.AccountHome;
import com.projectgoth.fusion.interfaces.Message;
import com.projectgoth.fusion.interfaces.MessageHome;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import com.projectgoth.fusion.interfaces.Voice;
import com.projectgoth.fusion.interfaces.VoiceHome;
import com.projectgoth.fusion.interfaces.Voucher;
import com.projectgoth.fusion.interfaces.VoucherHome;
import com.projectgoth.fusion.voiceengine.CallMakerI;
import com.projectgoth.fusion.voiceengine.CallRequest;
import com.projectgoth.fusion.voiceengine.ExtendedControl;
import com.projectgoth.fusion.voiceengine.FastAGICommand;
import com.projectgoth.fusion.voiceengine.FastAGIServer;
import com.projectgoth.fusion.voiceengine.FastAGIWorker;
import com.projectgoth.fusion.voiceengine.VoiceEngine;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;

public class LogicHelper {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(LogicHelper.class));
    protected FastAGIWorker worker = null;
    protected FastAGIServer server = null;
    protected FastAGICommand command = null;
    protected ExtendedControl control = null;
    protected CallMakerI callMaker = null;

    public LogicHelper(FastAGIWorker worker, FastAGIServer server, FastAGICommand command, ExtendedControl control, CallMakerI callMaker) {
        this.worker = worker;
        this.server = server;
        this.command = command;
        this.control = control;
        this.callMaker = callMaker;
    }

    public FastAGIWorker getWorker() {
        return this.worker;
    }

    public FastAGICommand getCommand() {
        return this.command;
    }

    public CallMakerI getCallMaker() {
        return this.callMaker;
    }

    public CallData getPendingCallThrough(String username, boolean logWarnings) throws CreateException, RemoteException, IOException {
        Voice voiceEJB = (Voice)EJBHomeCache.getObject("ejb/Voice", VoiceHome.class);
        List pendingCalls = voiceEJB.getCallEntries(username, CallData.StatusEnum.PENDING.value());
        CallData pendingCallThrough = null;
        if (pendingCalls != null) {
            for (CallData callData : pendingCalls) {
                if (!callData.isCallThrough()) continue;
                CallData expiredCall = null;
                if (System.currentTimeMillis() - callData.dateCreated.getTime() > (long)this.server.getCallThroughValidPeriod()) {
                    expiredCall = callData;
                } else if (pendingCallThrough == null) {
                    pendingCallThrough = callData;
                } else if (callData.id > pendingCallThrough.id) {
                    expiredCall = pendingCallThrough;
                    pendingCallThrough = callData;
                } else {
                    expiredCall = callData;
                }
                if (expiredCall == null) continue;
            }
        }
        if (logWarnings && pendingCallThrough == null) {
            StringBuilder builder = new StringBuilder("No pending call-through found for ").append(username).append(". Calls examined: ");
            if (pendingCalls != null) {
                for (CallData callData : pendingCalls) {
                    builder.append(callData.id).append(" ");
                }
            }
            log.debug((Object)builder);
        }
        return pendingCallThrough;
    }

    public CallRequest getPendingMidletRequest(UserData userData, String didNumber) throws CreateException, RemoteException, IOException {
        log.info((Object)("Getting pending midlet request for user '" + userData.username + "'"));
        CallData callData = this.getPendingCallThrough(userData.username, true);
        if (callData == null) {
            log.warn((Object)("No pending request found for " + userData.username));
            return null;
        }
        callData.didNumber = didNumber;
        return new CallRequest(callData, null, null, 0L, 0.0, 0L, 0L, 0);
    }

    public CallRequest createDirectCallThroughRequest(UserData userData, String callerId, String didNumber) {
        log.info((Object)("Creating direct call through request for user '" + userData.username + "'"));
        CallData callData = new CallData();
        callData.username = userData.username;
        callData.type = CallData.TypeEnum.DIRECT_CALL_THROUGH;
        callData.didNumber = didNumber;
        callData.sourceType = CallData.SourceDestinationTypeEnum.PSTN_PHONE;
        callData.source = callerId == null || callerId.length() < 1 ? null : callerId;
        return new CallRequest(callData, null, null, 0L, 0.0, 0L, 0L, 0);
    }

    public CallRequest createMissedCallCallbackRequest(UserData userData, String callerId, Integer sourceProvider) {
        log.info((Object)("Creating missed call callback request for user '" + userData.username + "'"));
        CallData callData = new CallData();
        callData.username = userData.username;
        callData.type = CallData.TypeEnum.MISSED_CALL_CALLBACK;
        callData.sourceType = CallData.SourceDestinationTypeEnum.PSTN_PHONE;
        callData.source = callerId;
        callData.sourceProvider = sourceProvider;
        callData.destinationType = CallData.SourceDestinationTypeEnum.PSTN_PHONE;
        return new CallRequest(callData, null, null, 0L, 0.0, 0L, 0L, 0);
    }

    public CallRequest evaluateCallRequest(CallRequest request) throws CreateException, RemoteException, IOException {
        CallData callData = request.getCallData();
        log.info((Object)("Evaluating call request for user '" + callData.username + "'"));
        Voice voiceEJB = (Voice)EJBHomeCache.getObject("ejb/Voice", VoiceHome.class);
        callData = voiceEJB.evaluatePhoneCall(callData);
        log.info((Object)"Filling in call request data from call data");
        request = this.callMaker.fillInCallRequest(callData, Integer.parseInt(this.command.getParameter("gw_id")));
        if (request != null) {
            log.info((Object)("Call data for '" + callData.destination + "', user '" + callData.username + "'': " + "callerid='" + callData.source + "', " + "dialcommand='" + request.getDialCommand() + ", limitdur='" + request.getLimitDuration() + "', " + "limitrate='" + request.getLimitRate() + "', warningdur='" + request.getLimitTimeoutWarning() + "', " + "repeatdur='" + request.getLimitTimeoutRepeat() + "', callid='" + request.getCallId() + "'"));
        } else {
            log.warn((Object)"Unable to fill in call request");
        }
        return request;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public CallRequest initiateCallRequest(CallRequest request) throws CreateException, RemoteException, IOException {
        CallRequest callRequest;
        boolean ok;
        CallData callData;
        block15: {
            CallRequest callRequest2;
            block14: {
                CallRequest callRequest3;
                block13: {
                    callData = request.getCallData();
                    ok = false;
                    try {
                        try {
                            String channel = this.command.getChannel();
                            if (channel == null || channel.length() == 0) {
                                log.warn((Object)"No channel data, so can't process missed call callback request");
                                callRequest3 = null;
                                Object var11_9 = null;
                                if (ok) return callRequest3;
                                if (callData == null) return callRequest3;
                                if (callData.status != CallData.StatusEnum.IN_PROGRESS) return callRequest3;
                                break block13;
                            }
                            log.info((Object)("Initiating call request for user '" + callData.username + "'"));
                            Voice voiceEJB = (Voice)EJBHomeCache.getObject("ejb/Voice", VoiceHome.class);
                            if (callData.id == null) {
                                log.info((Object)("Persisting call request for user '" + callData.username + "'"));
                                callData = voiceEJB.initiatePhoneCall(callData);
                            }
                            log.info((Object)"Setting the proper callerId to link call completion");
                            this.control.setAsteriskCallerId(callData.source);
                            this.control.setAsteriskCallerIdName(callData.source);
                            String astCallerId = this.control.getAsteriskCallerId();
                            String astCallerIdName = this.control.getAsteriskCallerIdName();
                            String astCallerDid = this.control.getAsteriskCallerDid();
                            log.info((Object)("Asterisk CallerDID is set to '" + astCallerDid + "'"));
                            log.info((Object)("Asterisk CallerID is set to '" + astCallerId + "'"));
                            log.info((Object)("Asterisk CallerIDName is set to '" + astCallerIdName + "'"));
                            this.control.setCDRUserField("" + callData.id);
                            this.control.addSIPHeader("X-CallRef: " + callData.id);
                            this.control.addSIPHeader("X-CallLeg: A");
                            this.control.addSIPHeader("X-CallType: 2");
                            this.control.resetCDR();
                            try {
                                callData.sourceChannel = channel;
                                callData = this.callMaker.requestCall(callData, Integer.parseInt(this.command.getParameter("gw_id")));
                            }
                            catch (Exception e) {
                                log.warn((Object)("Error int callMaker.requestCall(), " + e.getMessage()));
                                callData.status = CallData.StatusEnum.FAILED;
                                callData.failReason = e.getMessage();
                            }
                            voiceEJB.updateCallDetail(callData);
                            if (callData.status == CallData.StatusEnum.FAILED) {
                                log.warn((Object)"Error initiating a call request");
                                callRequest2 = null;
                                break block14;
                            }
                            log.info((Object)"Filling in call request data from call data");
                            request = this.callMaker.fillInCallRequest(callData, Integer.parseInt(this.command.getParameter("gw_id")));
                            if (request != null) {
                                log.info((Object)("Call data for '" + callData.destination + "', user '" + callData.username + "'': " + "callerid='" + callData.source + "', " + "dialcommand='" + request.getDialCommand() + ", limitdur='" + request.getLimitDuration() + "', " + "limitrate='" + request.getLimitRate() + "', warningdur='" + request.getLimitTimeoutWarning() + "', " + "repeatdur='" + request.getLimitTimeoutRepeat() + "', callid='" + request.getCallId() + "'"));
                            } else {
                                log.warn((Object)"Unable to fill in call request");
                            }
                            ok = true;
                            callRequest = request;
                            break block15;
                        }
                        catch (CreateException e) {
                            log.warn((Object)("Error initiating a call for user '" + callData.username + "', (ex1); " + e.getMessage()));
                            throw e;
                        }
                        catch (RemoteException e) {
                            log.warn((Object)("Error initiating a call for user '" + callData.username + "', (ex2); " + e.getMessage()));
                            throw e;
                        }
                    }
                    catch (Throwable throwable) {
                        Object var11_12 = null;
                        if (ok) throw throwable;
                        if (callData == null) throw throwable;
                        if (callData.status != CallData.StatusEnum.IN_PROGRESS) throw throwable;
                        log.warn((Object)("Error initiating a call for user '" + callData.username + "', so cancelling"));
                        this.callMaker.cancelCall(callData);
                        throw throwable;
                    }
                }
                log.warn((Object)("Error initiating a call for user '" + callData.username + "', so cancelling"));
                this.callMaker.cancelCall(callData);
                return callRequest3;
            }
            Object var11_10 = null;
            if (ok) return callRequest2;
            if (callData == null) return callRequest2;
            if (callData.status != CallData.StatusEnum.IN_PROGRESS) return callRequest2;
            log.warn((Object)("Error initiating a call for user '" + callData.username + "', so cancelling"));
            this.callMaker.cancelCall(callData);
            return callRequest2;
        }
        Object var11_11 = null;
        if (ok) return callRequest;
        if (callData == null) return callRequest;
        if (callData.status != CallData.StatusEnum.IN_PROGRESS) return callRequest;
        log.warn((Object)("Error initiating a call for user '" + callData.username + "', so cancelling"));
        this.callMaker.cancelCall(callData);
        return callRequest;
    }

    public CallRequest updateCallRequest(CallRequest request) throws CreateException, RemoteException, IOException {
        CallData callData = request.getCallData();
        log.info((Object)("Updating call request for user '" + callData.username + "'"));
        Voice voiceEJB = (Voice)EJBHomeCache.getObject("ejb/Voice", VoiceHome.class);
        voiceEJB.updateCallDetail(callData);
        try {
            this.callMaker.cancelCall(callData);
            this.callMaker.requestCall(callData, Integer.parseInt(this.command.getParameter("gw_id")));
        }
        catch (Exception e) {
            log.warn((Object)"Unable to re-register call with CallMaker");
            return null;
        }
        return request;
    }

    public void processBillingStart(UserData userData, String destination) {
        if (userData == null) {
            log.warn((Object)"No channel data, so can't process billing start");
            return;
        }
        if (destination == null || destination.length() < 1) {
            log.warn((Object)"No destination, so can't process billing start request");
            return;
        }
        log.info((Object)("Processing billing start for '" + userData.username + "'"));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void processBillingEnd(UserData userData, String destination) throws CreateException, RemoteException, IOException {
        boolean ok;
        CallData callData;
        block12: {
            block11: {
                block10: {
                    block9: {
                        callData = null;
                        ok = false;
                        try {
                            try {
                                String channel = this.command.getChannel();
                                if (channel == null || channel.length() == 0) {
                                    log.warn((Object)"No channel data, so can't process billing end request");
                                    Object var7_8 = null;
                                    if (ok) return;
                                    if (callData == null) return;
                                    if (callData.status != CallData.StatusEnum.IN_PROGRESS) return;
                                    break block9;
                                }
                                if (userData == null) {
                                    log.warn((Object)"No user data, so can't process billing end request");
                                    break block10;
                                }
                                if (destination == null || destination.length() < 1) {
                                    log.warn((Object)"No destination, so can't process billing end request");
                                    break block11;
                                }
                                log.info((Object)("Processing billing end to '" + destination + "', user '" + userData.username + "'"));
                                callData = this.getPendingCallThrough(userData.username, false);
                                if (callData != null && destination.equals(callData.destination)) {
                                    this.callMaker.callCompleted(callData, false);
                                    return;
                                }
                                log.info((Object)("No call data or destination doesn't match to '" + destination + "', user '" + userData.username + "'"));
                                break block12;
                            }
                            catch (CreateException e) {
                                log.warn((Object)("Ex1- Error with billing end to '" + destination + "', user '" + userData.username + "'; " + e.getMessage()));
                                throw e;
                            }
                            catch (RemoteException e) {
                                log.warn((Object)("Ex2- Error with billing end to '" + destination + "', user '" + userData.username + "'; " + e.getMessage()));
                                Object var7_13 = null;
                                if (ok) return;
                                if (callData == null) return;
                                if (callData.status != CallData.StatusEnum.IN_PROGRESS) return;
                                log.warn((Object)("Error completing billing end to '" + destination + "', user '" + userData.username + "', so cancelling"));
                                this.callMaker.cancelCall(callData);
                                return;
                            }
                        }
                        catch (Throwable throwable) {
                            Object var7_14 = null;
                            if (ok) throw throwable;
                            if (callData == null) throw throwable;
                            if (callData.status != CallData.StatusEnum.IN_PROGRESS) throw throwable;
                            log.warn((Object)("Error completing billing end to '" + destination + "', user '" + userData.username + "', so cancelling"));
                            this.callMaker.cancelCall(callData);
                            throw throwable;
                        }
                    }
                    log.warn((Object)("Error completing billing end to '" + destination + "', user '" + userData.username + "', so cancelling"));
                    this.callMaker.cancelCall(callData);
                    return;
                }
                Object var7_9 = null;
                if (ok) return;
                if (callData == null) return;
                if (callData.status != CallData.StatusEnum.IN_PROGRESS) return;
                log.warn((Object)("Error completing billing end to '" + destination + "', user '" + userData.username + "', so cancelling"));
                this.callMaker.cancelCall(callData);
                return;
            }
            Object var7_10 = null;
            if (ok) return;
            if (callData == null) return;
            if (callData.status != CallData.StatusEnum.IN_PROGRESS) return;
            log.warn((Object)("Error completing billing end to '" + destination + "', user '" + userData.username + "', so cancelling"));
            this.callMaker.cancelCall(callData);
            return;
        }
        Object var7_11 = null;
        if (ok) return;
        if (callData == null) return;
        if (callData.status != CallData.StatusEnum.IN_PROGRESS) return;
        log.warn((Object)("Error completing billing end to '" + destination + "', user '" + userData.username + "', so cancelling"));
        this.callMaker.cancelCall(callData);
    }

    public void processBillingCancel(UserData userData, String destination) throws CreateException, RemoteException, IOException {
        CallData callData = null;
        try {
            String channel = this.command.getChannel();
            if (channel == null || channel.length() == 0) {
                log.warn((Object)"No channel data, so can't process billing cancel request");
                return;
            }
            if (userData == null) {
                log.warn((Object)"No user data, so can't process billing cancel request");
                return;
            }
            if (destination == null || destination.length() < 1) {
                log.warn((Object)"No destination, so can't process billing cancel request");
                return;
            }
            log.info((Object)("Processing billing cancel to '" + destination + "', user '" + userData.username + "'"));
            callData = this.getPendingCallThrough(userData.username, false);
            if (callData == null || !destination.equals(callData.destination)) {
                log.info((Object)("No call data or destination doesn't match to '" + destination + "', user '" + userData.username + "'"));
                return;
            }
            this.callMaker.cancelCall(callData);
            Voice voiceEJB = (Voice)EJBHomeCache.getObject("ejb/Voice", VoiceHome.class);
            callData.status = CallData.StatusEnum.FAILED;
            if (callData.failReason == null || callData.failReason.length() < 1) {
                callData.failReason = "Call Cancelled";
            }
            voiceEJB.updateCallDetail(callData);
        }
        catch (CreateException e) {
            log.warn((Object)("Ex1- Error with billing cancel to '" + destination + "', user '" + userData.username + "'; " + e.getMessage()));
            throw e;
        }
        catch (RemoteException e) {
            log.warn((Object)("Ex2- Error with billing cancel to '" + destination + "', user '" + userData.username + "'; " + e.getMessage()));
        }
    }

    public AccountBalanceData getBalance(UserData userData) throws CreateException, RemoteException, IOException {
        AccountBalanceData userBalance = new AccountBalanceData();
        userBalance.currency = new CurrencyData();
        userBalance.currency.code = "usd";
        userBalance.balance = 0.0;
        userBalance.fundedBalance = 0.0;
        try {
            if (userData == null) {
                log.warn((Object)"No user data, so can't process get balance request");
                return userBalance;
            }
            log.info((Object)("Retrieving balance for username '" + userData.username + "'"));
            Account accountEJB = (Account)EJBHomeCache.getObject("ejb/Account", AccountHome.class);
            AccountBalanceData realUserBalance = accountEJB.getAccountBalance(userData.username);
            if (realUserBalance != null) {
                log.info((Object)("User '" + userData.username + "' has a balance of '" + realUserBalance.balance + "', funded '" + realUserBalance.fundedBalance + "', in currency '" + (realUserBalance.currency == null ? "" : realUserBalance.currency.code) + "'"));
            } else {
                log.warn((Object)"Could not get user's balance");
            }
            return realUserBalance;
        }
        catch (CreateException e) {
            log.warn((Object)("Error with get balance with user '" + userData.username + "' (ex1); " + e.getMessage()));
            throw e;
        }
        catch (RemoteException e) {
            log.warn((Object)("Error with get balance with user '" + userData.username + "' (ex2); " + e.getMessage()));
            return userBalance;
        }
    }

    public long getLimitDuration(UserData userData, CallRequest callRequest) throws CreateException, RemoteException, IOException {
        try {
            if (userData == null) {
                log.warn((Object)"No user data, so can't process get limit duration request");
                return 0L;
            }
            if (callRequest == null) {
                log.warn((Object)"No call request data, so can't process get limit duration request");
                return 0L;
            }
            log.info((Object)("Retrieving limit duration for username '" + userData.username + "'"));
            log.info((Object)("Retrieving pending call-through for " + userData.username));
            CallData callData = this.getPendingCallThrough(userData.username, true);
            if (callData == null) {
                log.info((Object)("No pending call-through found for " + userData.username));
            } else {
                log.info((Object)("Pending call-through " + callData.id + " found for " + userData.username + ". Destination " + callData.destination));
            }
            return callRequest.limitDuration;
        }
        catch (CreateException e) {
            log.warn((Object)("Ex1- Error with get limit duration with user '" + userData.username + "'; " + e.getMessage()));
            throw e;
        }
        catch (RemoteException e) {
            log.warn((Object)("Ex2- Error with get limit duration with user '" + userData.username + "'; " + e.getMessage()));
            return 0L;
        }
    }

    public UserData loadUserFromUsername(String username) throws CreateException, RemoteException, IOException {
        try {
            if (username == null || username.length() < 1) {
                log.warn((Object)"No user name, so can't load user from suername");
                return null;
            }
            log.info((Object)("Loading user from username '" + username + "'"));
            User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
            UserData userData = userEJB.loadUser(username, false, false);
            if (userData == null || !userData.username.equals(username)) {
                log.warn((Object)("Could not load the user from username '" + username + "'"));
                userData = null;
            }
            return userData;
        }
        catch (CreateException e) {
            log.warn((Object)("Ex1- Could not load the user from username '" + username + "'; " + e.getMessage()));
            throw e;
        }
        catch (RemoteException e) {
            log.warn((Object)("Ex2- Could not load the user from username '" + username + "'; " + e.getMessage()));
            return null;
        }
    }

    public UserData loadUserFromMobilePhone(String mobilePhone) throws CreateException, RemoteException, IOException {
        try {
            if (mobilePhone == null || mobilePhone.length() < 1) {
                log.warn((Object)"No mobile phone, so can't load user from mobile phone");
                return null;
            }
            log.info((Object)("Loading user from mobile phone '" + mobilePhone + "'"));
            User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
            UserData userData = userEJB.loadUserFromMobilePhone(mobilePhone);
            if (userData == null || !userData.mobilePhone.equals(mobilePhone)) {
                log.warn((Object)("Could not load the user from mobile phone '" + mobilePhone + "'"));
                userData = null;
            }
            return userData;
        }
        catch (CreateException e) {
            log.warn((Object)("Ex1- Could not load the user from mobile phone '" + mobilePhone + "'; " + e.getMessage()));
            throw e;
        }
        catch (RemoteException e) {
            log.warn((Object)("Ex2- Could not load the user from mobile phone '" + mobilePhone + "'; " + e.getMessage()));
            return null;
        }
    }

    public UserData loadUserFromVoucherNumber(String voucherNumber) throws CreateException, RemoteException, IOException {
        try {
            if (voucherNumber == null || voucherNumber.length() < 1) {
                log.warn((Object)"No voucher number, so can't load user from voucher number");
                return null;
            }
            log.info((Object)("Loading user from voucher number '" + voucherNumber + "'"));
            User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
            UserData userData = userEJB.loadUserFromVoucherNumber(voucherNumber);
            if (userData == null) {
                log.warn((Object)("Could not load the user from voucher number '" + voucherNumber + "' with normal method, trying another method"));
                String username = UserData.TypeEnum.MIG33_PREPAID_CARD.toString().toLowerCase() + "_" + voucherNumber;
                userData = userEJB.loadUser(username, false, false);
                if (userData == null || !userData.username.equals(username)) {
                    log.warn((Object)("Could not load the user from voucher number '" + voucherNumber + "'"));
                    userData = null;
                }
            }
            return userData;
        }
        catch (CreateException e) {
            log.warn((Object)("Ex1- Could not load the user from voucher number '" + voucherNumber + "'; " + e.getMessage()));
            throw e;
        }
        catch (RemoteException e) {
            log.warn((Object)("Ex2- Could not load the user from voucher number '" + voucherNumber + "'; " + e.getMessage()));
            return null;
        }
    }

    public UserData createUserFromVoucher(String voucherNumber, String fromDID) throws CreateException, RemoteException, IOException {
        try {
            if (voucherNumber == null || voucherNumber.length() < 1) {
                log.warn((Object)"No voucher number, so can't create user from voucher number");
                return null;
            }
            log.info((Object)("Creating user from voucherNumber '" + voucherNumber + "'"));
            User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
            UserData userData = userEJB.createPrepaidCardUser(fromDID, voucherNumber, new AccountEntrySourceData(VoiceEngine.class));
            if (userData == null) {
                log.warn((Object)("Could not create user from voucher number '" + voucherNumber + "'"));
            }
            return userData;
        }
        catch (CreateException e) {
            log.warn((Object)("Ex1- Could not create user from voucher number '" + voucherNumber + "'; " + e.getMessage()));
            throw e;
        }
        catch (RemoteException e) {
            log.warn((Object)("Ex2- Could not create user from voucher number '" + voucherNumber + "'; " + e.getMessage()));
            return null;
        }
    }

    public UserData updateUserMobilePhone(UserData userData, String mobilePhone) throws CreateException, RemoteException, IOException {
        try {
            if (userData == null) {
                log.warn((Object)"No user data, so can't update user's mobile phone");
                return userData;
            }
            log.info((Object)("Updating user '" + userData.username + "' mobile phone to '" + mobilePhone + "'"));
            User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
            if (mobilePhone != null && mobilePhone.length() < 1) {
                mobilePhone = null;
            }
            userEJB.changeMobilePhone(userData.username, mobilePhone, new AccountEntrySourceData(VoiceEngine.class));
            userData.mobilePhone = mobilePhone;
            if (mobilePhone == null) {
                if (userData == null || userData.mobilePhone != null) {
                    log.warn((Object)("Could not update user mobile phone to '" + mobilePhone + "'"));
                }
            } else if (userData == null || userData.mobilePhone == null || !userData.mobilePhone.equals(mobilePhone)) {
                log.warn((Object)("Could not update user mobile phone to '" + mobilePhone + "'"));
            }
            return userData;
        }
        catch (CreateException e) {
            log.warn((Object)("Ex1- Could not update user mobile phone to '" + mobilePhone + "'; " + e.getMessage()));
            throw e;
        }
        catch (RemoteException e) {
            log.warn((Object)("Ex2- Could not update user mobile phone to '" + mobilePhone + "'; " + e.getMessage()));
            return userData;
        }
    }

    public VoucherData getVoucher(String voucherNumber) throws CreateException, RemoteException, IOException {
        try {
            if (voucherNumber == null || voucherNumber.length() < 1) {
                log.warn((Object)"No voucher number, so can't get voucher from voucher number");
                return null;
            }
            log.info((Object)("Get voucher from voucherNumber '" + voucherNumber + "'"));
            Voucher voucherEJB = (Voucher)EJBHomeCache.getObject("ejb/Voucher", VoucherHome.class);
            VoucherData voucherData = voucherEJB.getVoucher(voucherNumber);
            if (voucherData == null) {
                log.warn((Object)("Could not get voucher number '" + voucherNumber + "'"));
            }
            return voucherData;
        }
        catch (CreateException e) {
            log.warn((Object)("Ex1- Could not get voucher number '" + voucherNumber + "'; " + e.getMessage()));
            throw e;
        }
        catch (RemoteException e) {
            log.warn((Object)("Ex2- Could not get voucher number '" + voucherNumber + "'; " + e.getMessage()));
            return null;
        }
    }

    public VoucherData redeemVoucher(String username, String voucherNumber) throws CreateException, RemoteException, IOException {
        try {
            if (username == null || username.length() < 1) {
                log.warn((Object)"No username, so can't get voucher with username");
                return null;
            }
            if (voucherNumber == null || voucherNumber.length() < 1) {
                log.warn((Object)"No voucher number, so can't get voucher with voucher number");
                return null;
            }
            log.info((Object)("Get voucher for username '" + username + "' with voucherNumber '" + voucherNumber + "'"));
            Voucher voucherEJB = (Voucher)EJBHomeCache.getObject("ejb/Voucher", VoucherHome.class);
            VoucherData voucherData = voucherEJB.redeemVoucher(username, voucherNumber, new AccountEntrySourceData(VoiceEngine.class));
            if (voucherData == null) {
                log.warn((Object)("Could not redeem voucher number '" + voucherNumber + "'"));
            }
            return voucherData;
        }
        catch (CreateException e) {
            log.warn((Object)("Ex1- Could not redeem voucher number '" + voucherNumber + "'; " + e.getMessage()));
            throw e;
        }
        catch (RemoteException e) {
            log.warn((Object)("Ex2- Could not redeem voucher number '" + voucherNumber + "'; " + e.getMessage()));
            return null;
        }
    }

    public String stripNonNumeric(String phoneNumber) {
        String newPhoneNumber = "";
        if (phoneNumber == null || phoneNumber.length() < 1) {
            log.warn((Object)"Phone number is null or invalid; can't strip non-numeric");
            return null;
        }
        for (int i = 0; i < phoneNumber.length(); ++i) {
            char ch = phoneNumber.charAt(i);
            if (ch < '0' || ch > '9') continue;
            newPhoneNumber = newPhoneNumber + ch;
        }
        log.info((Object)("Stripped phone number: original='" + phoneNumber + "', cleaned='" + newPhoneNumber + "'"));
        return newPhoneNumber;
    }

    public String cleanPhoneNumber(String phoneNumber) throws CreateException, RemoteException, IOException {
        Message messageEJB;
        String newPhoneNumber = phoneNumber;
        if ((newPhoneNumber = this.stripNonNumeric(newPhoneNumber)) == null || newPhoneNumber.length() < 1) {
            log.warn((Object)"Phone number is null or invalid; can't clean number");
            return null;
        }
        try {
            messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
            newPhoneNumber = messageEJB.cleanAndValidatePhoneNumber(newPhoneNumber, true);
        }
        catch (Exception e) {
            // empty catch block
        }
        if (newPhoneNumber.length() > 3 && newPhoneNumber.substring(0, 4).equals("0011")) {
            newPhoneNumber = newPhoneNumber.substring(4);
        } else if (newPhoneNumber.length() > 2 && newPhoneNumber.substring(0, 3).equals("000")) {
            newPhoneNumber = newPhoneNumber.substring(3);
        } else if (newPhoneNumber.length() > 2 && newPhoneNumber.substring(0, 3).equals("011")) {
            newPhoneNumber = newPhoneNumber.substring(3);
        } else if (newPhoneNumber.length() > 1 && newPhoneNumber.substring(0, 2).equals("00")) {
            newPhoneNumber = newPhoneNumber.substring(2);
        } else if (newPhoneNumber.length() > 0 && newPhoneNumber.substring(0, 1).equals("0")) {
            newPhoneNumber = newPhoneNumber.substring(1);
        }
        try {
            messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
            newPhoneNumber = messageEJB.cleanAndValidatePhoneNumber(newPhoneNumber, true);
        }
        catch (Exception exception) {
            // empty catch block
        }
        log.info((Object)("Cleaning phone number: original='" + phoneNumber + "', cleaned='" + newPhoneNumber + "'"));
        return newPhoneNumber;
    }

    public String fixCallerCountryCode(String phoneNumber, String didNumber) throws CreateException, RemoteException, IOException {
        String newPhoneNumber = phoneNumber;
        String newDIDNumber = didNumber;
        newPhoneNumber = this.stripNonNumeric(newPhoneNumber);
        newDIDNumber = this.stripNonNumeric(newDIDNumber);
        if (newPhoneNumber == null || newPhoneNumber.length() < 1) {
            log.warn((Object)"Phone number is null or invalid; can't fix phone number");
            return null;
        }
        if (newDIDNumber == null || newDIDNumber.length() < 1) {
            log.warn((Object)"DID number is null or invalid; can't fix phone number");
            return null;
        }
        try {
            newDIDNumber = this.cleanPhoneNumber(newDIDNumber);
            Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
            int phoneIDD = -1;
            int didIDD = -1;
            try {
                phoneIDD = messageEJB.getIDDCode(newPhoneNumber);
            }
            catch (Exception e) {
                // empty catch block
            }
            try {
                didIDD = messageEJB.getIDDCode(newDIDNumber);
            }
            catch (Exception e) {
                // empty catch block
            }
            if (newPhoneNumber.length() > 3 && newPhoneNumber.substring(0, 4).equals("0000")) {
                newPhoneNumber = newPhoneNumber.substring(4);
            } else if (newPhoneNumber.length() > 2 && newPhoneNumber.substring(0, 3).equals("000")) {
                newPhoneNumber = newPhoneNumber.substring(3);
            } else if (newPhoneNumber.length() > 1 && newPhoneNumber.substring(0, 2).equals("00")) {
                newPhoneNumber = newPhoneNumber.substring(2);
            } else if (newPhoneNumber.length() > 1 && !newPhoneNumber.substring(0, 1).equals("0")) {
                if (didIDD > 0 && phoneIDD != didIDD) {
                    newPhoneNumber = new String("" + didIDD) + newPhoneNumber;
                }
            } else if (newPhoneNumber.substring(0, 1).equals("0")) {
                newPhoneNumber = "" + didIDD + newPhoneNumber.substring(1);
            }
        }
        catch (CreateException e) {
            log.warn((Object)("Error prefixing number (ex1); " + e.getMessage()));
        }
        catch (Exception e) {
            log.warn((Object)("Error prefixing number (ex2); " + e.getMessage()));
        }
        log.info((Object)("Fixing country code: original='" + phoneNumber + "', fixed='" + newPhoneNumber + "' (DID is '" + newDIDNumber + "')"));
        return newPhoneNumber;
    }

    public String fixValidateNumber(String phoneNumber) throws CreateException, RemoteException, IOException {
        String newPhoneNumber = phoneNumber;
        if ((newPhoneNumber = this.stripNonNumeric(newPhoneNumber)) == null || newPhoneNumber.length() < 1) {
            log.warn((Object)"Phone number is null or invalid; can't fix phone number");
            return null;
        }
        try {
            Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
            try {
                newPhoneNumber = messageEJB.cleanAndValidatePhoneNumber(newPhoneNumber, true);
            }
            catch (Exception e) {
                newPhoneNumber = null;
            }
        }
        catch (CreateException e) {
            log.warn((Object)("Error fixvalidate number (ex1); " + e.getMessage()));
            newPhoneNumber = null;
        }
        catch (Exception e) {
            log.warn((Object)("Error fixvalidate number (ex2); " + e.getMessage()));
            newPhoneNumber = null;
        }
        if (newPhoneNumber == null || newPhoneNumber.length() < 1) {
            newPhoneNumber = null;
        }
        log.info((Object)("Validate and fix number: original='" + phoneNumber + "', fixed='" + newPhoneNumber + "'"));
        return newPhoneNumber;
    }
}

