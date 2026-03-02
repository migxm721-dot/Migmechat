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
import com.projectgoth.fusion.data.CallData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.data.VoucherData;
import com.projectgoth.fusion.voiceengine.CallMakerI;
import com.projectgoth.fusion.voiceengine.CallRequest;
import com.projectgoth.fusion.voiceengine.ExtendedControl;
import com.projectgoth.fusion.voiceengine.FastAGICommand;
import com.projectgoth.fusion.voiceengine.FastAGIWorker;
import com.projectgoth.fusion.voiceengine.LogicHelper;
import java.io.IOException;
import java.rmi.RemoteException;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;

public class CallingCard {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(CallingCard.class));
    protected static final int INPUT_TIME_SEC = 7;
    protected static final int OPT_TIME_SEC = 3;
    protected static final int VOUCHER_MIN_LEN = 4;
    protected static final int VOUCHER_MAX_LEN = 32;
    protected static final int PHONE_MIN_LEN = 5;
    protected static final int PHONE_MAX_LEN = 32;
    protected static final int RETRIES_MAX = 3;
    protected static final int RETRIES_HELP = 2;
    protected static final String DEF_ASTERISK_LANG = "mig33/en/allison";
    protected static final String DEF_ASTERISK_CURR = "";
    protected static final int RES_DONE = 1;
    protected static final int RES_CONTINUE = 0;
    protected static final int RES_ERROR = -1;
    protected static final int RES_TIMEOUT = -2;
    protected static final int RES_HANGUP = -2;
    protected LogicHelper helper = null;
    protected FastAGIWorker worker = null;
    protected FastAGICommand command = null;
    protected CallMakerI callMaker = null;
    protected ExtendedControl control = null;
    protected UserData userData = null;
    protected VoucherData voucherData = null;
    protected String callerId = null;
    protected String callerIdName = null;
    protected String callerDid = null;

    public CallingCard(LogicHelper helper, ExtendedControl control) {
        this.helper = helper;
        this.control = control;
        if (this.helper != null) {
            this.worker = helper.getWorker();
            this.command = helper.getCommand();
            this.callMaker = helper.getCallMaker();
        }
    }

    protected boolean isPhoneLenValid(String phone) {
        return phone != null && phone.length() >= 5 && phone.length() <= 32;
    }

    protected boolean isCallerIdLenValid(String phone) {
        return phone != null && phone.length() >= 5 && phone.length() <= 32;
    }

    protected boolean isVoucherLenValid(String pin) {
        return pin != null && pin.length() >= 4 && pin.length() <= 32;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public boolean processPlaceCall(CallRequest request) throws CreateException, RemoteException, IOException {
        boolean bl;
        boolean ok;
        block21: {
            boolean bl2;
            block20: {
                boolean bl3;
                block18: {
                    block19: {
                        ok = false;
                        if (this.userData == null || request == null || request.getDestination() == null) {
                            log.info((Object)"User is not registered or missing phone number");
                            return false;
                        }
                        try {
                            try {
                                CallData.TypeEnum callType = request.getCallData().type;
                                switch (callType) {
                                    case MIDLET_CALL_THROUGH: 
                                    case DIRECT_CALL_THROUGH: {
                                        request = this.helper.initiateCallRequest(request);
                                        break;
                                    }
                                    case MISSED_CALL_CALLBACK: {
                                        request = this.helper.updateCallRequest(request);
                                        break;
                                    }
                                    default: {
                                        log.warn((Object)("Invalid call type " + callType.toString()));
                                        bl3 = false;
                                        Object var15_9 = null;
                                        if (ok) break block18;
                                        break block19;
                                    }
                                }
                                if (request == null || request.getDialCommand() == null || request.getDialCommand().length() < 1 || request.getLimitDuration() < 1L) {
                                    bl2 = false;
                                    break block20;
                                }
                                log.info((Object)("Placing call to '" + request.getDestination() + "' by username '" + this.userData.username + "'"));
                                String dialCommand = request.getDialCommand();
                                int dialDuration = 40;
                                long limitDuration = request.getLimitDuration();
                                long limitTimeoutWarning = request.getLimitTimeoutWarning();
                                long limitTimeoutRepeat = request.getLimitTimeoutRepeat();
                                this.helper.processBillingStart(this.userData, request.getDestination());
                                int status = this.control.dialOptions(dialCommand, dialDuration, "goL(" + limitDuration + ":" + limitTimeoutWarning + ":" + limitTimeoutRepeat + ")");
                                if (status == 5 || status == 0) {
                                    log.info((Object)("Call was answered (code = 'answer(" + status + ")')"));
                                    ok = true;
                                } else {
                                    log.info((Object)("Call was NOT answered (code = 'unknown(" + status + ")')"));
                                    ok = false;
                                }
                                bl = ok;
                                break block21;
                            }
                            catch (CreateException e) {
                                throw e;
                            }
                            catch (RemoteException remoteException) {
                                Object var15_12 = null;
                                if (!ok) {
                                    log.info((Object)"Cancelling call and billing");
                                    if (null == request) return false;
                                    this.helper.processBillingCancel(this.userData, request.getDestination());
                                    return false;
                                }
                                log.info((Object)"Ending the call and billing");
                                if (null == request) return false;
                                this.helper.processBillingEnd(this.userData, request.getDestination());
                                return false;
                            }
                        }
                        catch (Throwable throwable) {
                            Object var15_13 = null;
                            if (!ok) {
                                log.info((Object)"Cancelling call and billing");
                                if (null == request) throw throwable;
                                this.helper.processBillingCancel(this.userData, request.getDestination());
                                throw throwable;
                            }
                            log.info((Object)"Ending the call and billing");
                            if (null == request) throw throwable;
                            this.helper.processBillingEnd(this.userData, request.getDestination());
                            throw throwable;
                        }
                    }
                    log.info((Object)"Cancelling call and billing");
                    if (null == request) return bl3;
                    this.helper.processBillingCancel(this.userData, request.getDestination());
                    return bl3;
                }
                log.info((Object)"Ending the call and billing");
                if (null == request) return bl3;
                this.helper.processBillingEnd(this.userData, request.getDestination());
                return bl3;
            }
            Object var15_10 = null;
            if (!ok) {
                log.info((Object)"Cancelling call and billing");
                if (null == request) return bl2;
                this.helper.processBillingCancel(this.userData, request.getDestination());
                return bl2;
            }
            log.info((Object)"Ending the call and billing");
            if (null == request) return bl2;
            this.helper.processBillingEnd(this.userData, request.getDestination());
            return bl2;
        }
        Object var15_11 = null;
        if (!ok) {
            log.info((Object)"Cancelling call and billing");
            if (null == request) return bl;
            this.helper.processBillingCancel(this.userData, request.getDestination());
            return bl;
        }
        log.info((Object)"Ending the call and billing");
        if (null == request) return bl;
        this.helper.processBillingEnd(this.userData, request.getDestination());
        return bl;
    }

    public boolean playCongestion() {
        log.info((Object)"Playing congestion tone");
        this.control.playCongestion(1);
        return true;
    }

    public int handleAuthMenu() throws CreateException, RemoteException, IOException {
        int res = 0;
        String input = null;
        int count = 0;
        log.info((Object)"Handling menu 'authMenu'");
        if (this.userData != null) {
            log.info((Object)"User must NOT be registered to continue");
            return 0;
        }
        try {
            while (this.control.isConnected()) {
                input = this.control.readInput("ct0012", 32, 7);
                if (input != null && input.length() > 0) {
                    log.info((Object)("User entered input '" + input + "'"));
                    if (this.isVoucherLenValid(input)) {
                        this.voucherData = this.helper.getVoucher(input);
                        if (this.voucherData != null && this.voucherData.status == VoucherData.StatusEnum.REDEEMED) {
                            log.info((Object)("Success valid voucher number '" + input + "'"));
                            log.info((Object)"Loading user from voucher number (voucher already redeemed)");
                            this.userData = this.helper.loadUserFromVoucherNumber(this.voucherData.number);
                            if (this.userData != null) {
                                res = 1;
                                break;
                            }
                            log.info((Object)"Could not load user from voucher number");
                        } else {
                            if (this.voucherData != null && this.voucherData.status == VoucherData.StatusEnum.ACTIVE) {
                                log.info((Object)("Success valid voucher number '" + input + "'"));
                                res = 1;
                                break;
                            }
                            log.info((Object)("Could not find voucher number '" + input + "'"));
                        }
                    } else {
                        log.info((Object)("Voucher number '" + input + "' appears invalid"));
                    }
                    log.info((Object)"User entered an unavailable option or invalid data");
                    this.control.playback("ct0007");
                    if (count < 2) {
                        this.control.playback("ct0024");
                    }
                } else {
                    log.info((Object)"User timed out when entering digit");
                }
                log.info((Object)("User has '" + (3 - count - 1) + "' retry before max retries"));
                if (++count >= 3) {
                    res = -2;
                    break;
                }
                if (count < 2) continue;
                this.control.playback(DEF_ASTERISK_CURR);
            }
            if (res == 1 || res == 0) {
                if (this.voucherData != null && this.voucherData.status == VoucherData.StatusEnum.REDEEMED) {
                    log.info((Object)"Taking user to main menu (voucher already redeemed)");
                } else if (this.voucherData != null && this.voucherData.status == VoucherData.StatusEnum.ACTIVE) {
                    log.info((Object)"Taking user to registration (voucher not redeemed)");
                    res = this.handleRegisterMenu();
                    res = res == 1 || res == 0 ? 1 : -1;
                } else {
                    log.info((Object)"Voucher number is not valid or user has exited");
                }
            }
            return this.control.isConnected() ? res : -2;
        }
        catch (CreateException e) {
            throw e;
        }
        catch (RemoteException e) {
            return -1;
        }
    }

    public int handleRegisterMenu() throws CreateException, RemoteException, IOException {
        int res = 0;
        String digit = null;
        int count = 0;
        log.info((Object)"Handling menu 'registerMenu'");
        if (this.userData != null || this.voucherData == null) {
            log.info((Object)"User must NOT be registered with a valid voucher number to continue");
            return 0;
        }
        try {
            while (this.control.isConnected()) {
                digit = this.control.readInput("ct0022", 1, 3);
                if (digit != null && digit.length() > 0) {
                    log.info((Object)("User entered input '" + digit + "'"));
                    if (digit.equals("1")) {
                        this.userData = this.helper.createUserFromVoucher(this.voucherData.number, this.callerDid);
                        if (this.userData != null) {
                            log.info((Object)("Created user from voucher number '" + this.voucherData.number + "'"));
                            UserData userDataNew = this.helper.updateUserMobilePhone(this.userData, this.callerId);
                            if (!this.isCallerIdLenValid(this.callerId)) {
                                log.info((Object)("Updated user mobile phone to '" + this.callerId + "' (PIN-less)"));
                                log.info((Object)"Caller id is required to continue");
                                this.control.playback("ct0009");
                                res = 0;
                                break;
                            }
                            if (userDataNew != null) {
                                log.info((Object)("Success updating mobile phone to '" + this.callerId + "' (PIN-less)"));
                                this.userData = userDataNew;
                                this.control.playback("ct0016");
                                res = 1;
                                break;
                            }
                            log.info((Object)("Failed updating mobile phone to '" + this.callerId + "' (PIN-less)"));
                        } else {
                            log.info((Object)("Could not create user from voucher number '" + this.voucherData.number + "'"));
                        }
                        this.control.playback("ct0015");
                        res = -1;
                        break;
                    }
                    if (digit.equals("2")) {
                        this.userData = this.helper.createUserFromVoucher(this.voucherData.number, this.callerDid);
                        if (this.userData != null) {
                            log.info((Object)("Created user from voucher number '" + this.voucherData.number + "'"));
                            UserData userDataNew = this.helper.updateUserMobilePhone(this.userData, DEF_ASTERISK_CURR);
                            if (userDataNew != null) {
                                log.info((Object)"Success updating mobile phone to '' (PIN-full)");
                                this.userData = userDataNew;
                                this.control.playback("ct0017");
                                res = 1;
                                break;
                            }
                            log.info((Object)"Failed updating mobile phone to '' (PIN-full)");
                        } else {
                            log.info((Object)("Could not create user from voucher number '" + this.voucherData.number + "'"));
                        }
                        this.control.playback("ct0015");
                        res = -1;
                        break;
                    }
                    log.info((Object)"User entered an unavailable option or invalid data");
                    this.control.playback("ct0023");
                    if (count < 2) {
                        this.control.playback("ct0024");
                    }
                } else {
                    log.info((Object)"User timed out when entering digit");
                }
                log.info((Object)("User has '" + (3 - count - 1) + "' retry before max retries"));
                if (++count >= 3) {
                    res = -2;
                    break;
                }
                if (count < 2) continue;
                this.control.playback(DEF_ASTERISK_CURR);
            }
            return this.control.isConnected() ? res : -2;
        }
        catch (CreateException e) {
            throw e;
        }
        catch (RemoteException e) {
            return -1;
        }
    }

    public int handleMainMenu(CallRequest request) throws CreateException, RemoteException, IOException {
        int res = 0;
        String input = null;
        int count = 0;
        log.info((Object)"Handling menu 'mainMenu'");
        if (this.userData == null) {
            log.info((Object)"User must be registered to continue");
            this.control.playback("ct0019");
            return 0;
        }
        try {
            if (request.callData.type != CallData.TypeEnum.MISSED_CALL_CALLBACK) {
                AccountBalanceData balanceData = this.helper.getBalance(this.userData);
                this.control.sayBalance(balanceData.balance, balanceData.currency == null ? DEF_ASTERISK_CURR : balanceData.currency.code);
            }
            while (this.control.isConnected()) {
                input = this.control.readInputExit("ct0011", "*", 32, 7);
                if (input != null && input.length() > 0) {
                    log.info((Object)("User entered input '" + input + "'"));
                    if (input.equals("*")) {
                        if (request.callData.type == CallData.TypeEnum.MISSED_CALL_CALLBACK) {
                            res = 0;
                            count = 0;
                            continue;
                        }
                        res = this.handleAccountMenu();
                        if (res == 1 || res == 0) {
                            res = 0;
                            count = 0;
                            continue;
                        }
                        res = -1;
                        break;
                    }
                    if (this.isPhoneLenValid(input)) {
                        request.setDestination(this.helper.cleanPhoneNumber(input));
                        res = this.handleCallMenu(request);
                        if (res == 1) {
                            res = 1;
                            break;
                        }
                        if (res == 0) {
                            res = 0;
                            count = 0;
                            continue;
                        }
                        res = -1;
                        break;
                    }
                    log.info((Object)"User entered an unavailable option or invalid data");
                    this.control.playback("ct0026");
                    if (count < 2) {
                        this.control.playback("ct0024");
                    }
                } else {
                    log.info((Object)"User timed out when entering digit");
                }
                log.info((Object)("User has '" + (3 - count - 1) + "' retry before max retries"));
                if (++count >= 3) {
                    res = -2;
                    break;
                }
                if (count < 2) continue;
                this.control.playback(DEF_ASTERISK_CURR);
            }
            return this.control.isConnected() ? res : -2;
        }
        catch (CreateException e) {
            throw e;
        }
        catch (RemoteException e) {
            return -1;
        }
    }

    public int handleAccountMenu() throws CreateException, RemoteException, IOException {
        int res = 0;
        String digit = null;
        int count = 0;
        log.info((Object)"Handling menu 'accountMenu'");
        try {
            while (this.control.isConnected()) {
                digit = this.control.readInput("ct0003", 1, 3);
                if (digit != null && digit.length() > 0) {
                    log.info((Object)("User entered input '" + digit + "'"));
                    if (digit.equals("1")) {
                        if (this.userData == null) {
                            log.info((Object)"User must be registered to continue");
                            this.control.playback("ct0019");
                            res = 0;
                            break;
                        }
                        res = this.handleRechargeMenu();
                        if (res == 1) {
                            AccountBalanceData balanceData = this.helper.getBalance(this.userData);
                            this.control.sayBalance(balanceData.balance, balanceData.currency == null ? DEF_ASTERISK_CURR : balanceData.currency.code);
                            res = 0;
                            break;
                        }
                        if (res == 0) {
                            res = 0;
                            break;
                        }
                        res = -1;
                        break;
                    }
                    if (digit.equals("2")) {
                        if (this.userData == null) {
                            log.info((Object)"User must be registered to continue");
                            this.control.playback("ct0019");
                            res = 0;
                            break;
                        }
                        if (!this.isCallerIdLenValid(this.callerId)) {
                            log.info((Object)"Caller id is required to continue");
                            this.control.playback("ct0009");
                            res = 0;
                            break;
                        }
                        res = this.handlePINToggleMenu();
                        if (res == 1 || res == 0) {
                            res = 0;
                            break;
                        }
                        res = -1;
                        break;
                    }
                    if (digit.equals("3")) {
                        if (this.userData == null) {
                            log.info((Object)"User must be registered to continue");
                            this.control.playback("ct0019");
                            res = 0;
                            break;
                        }
                        AccountBalanceData balanceData = this.helper.getBalance(this.userData);
                        this.control.sayBalance(balanceData.balance, balanceData.currency == null ? DEF_ASTERISK_CURR : balanceData.currency.code);
                        res = 0;
                        break;
                    }
                    if (digit.equals("4")) {
                        this.control.playback("ct0029");
                        res = 0;
                        break;
                    }
                    if (digit.equals("0")) {
                        res = 0;
                        break;
                    }
                    log.info((Object)"User entered an unavailable option or invalid data");
                    this.control.playback("ct0023");
                    if (count < 2) {
                        this.control.playback("ct0024");
                    }
                } else {
                    log.info((Object)"User timed out when entering digit");
                }
                log.info((Object)("User has '" + (3 - count - 1) + "' retry before max retries"));
                if (++count >= 3) {
                    res = -2;
                    break;
                }
                if (count < 2) continue;
                this.control.playback(DEF_ASTERISK_CURR);
            }
            return this.control.isConnected() ? res : -2;
        }
        catch (CreateException e) {
            throw e;
        }
        catch (RemoteException e) {
            return -1;
        }
    }

    public int handleRechargeMenu() throws CreateException, RemoteException, IOException {
        int res = 0;
        String input = null;
        int count = 0;
        log.info((Object)"Handling menu 'rechargeMenu'");
        if (this.userData == null) {
            log.info((Object)"User must be registered to continue");
            this.control.playback("ct0019");
            return 0;
        }
        try {
            while (this.control.isConnected()) {
                input = this.control.readInputExit("ct0005", "0", 32, 7);
                if (input != null && input.length() > 0) {
                    log.info((Object)("User entered input '" + input + "'"));
                    if (input.equals("0")) {
                        res = 0;
                        break;
                    }
                    if (this.isVoucherLenValid(input)) {
                        this.voucherData = this.helper.getVoucher(input);
                        if (this.voucherData != null) {
                            VoucherData voucherDataNew = this.helper.redeemVoucher(this.userData.username, input);
                            if (voucherDataNew != null) {
                                log.info((Object)("Redeemed voucher number '" + input + "'"));
                                this.control.playback("ct0025");
                                res = 1;
                                break;
                            }
                            log.info((Object)("Could not redeem voucher number '" + input + "'"));
                            this.control.playback("ct0008");
                            res = 1;
                            break;
                        }
                        log.info((Object)("Could not find voucher number '" + input + "'"));
                    } else {
                        log.info((Object)("Voucher number '" + input + "' appears invalid"));
                    }
                    log.info((Object)"User entered an unavailable option or invalid data");
                    this.control.playback("ct0007");
                    if (count < 2) {
                        this.control.playback("ct0024");
                    }
                } else {
                    log.info((Object)"User timed out when entering digit");
                }
                log.info((Object)("User has '" + (3 - count - 1) + "' retry before max retries"));
                if (++count >= 3) {
                    res = -2;
                    break;
                }
                if (count < 2) continue;
                this.control.playback(DEF_ASTERISK_CURR);
            }
            return this.control.isConnected() ? res : -2;
        }
        catch (CreateException e) {
            throw e;
        }
        catch (RemoteException e) {
            return -1;
        }
    }

    public int handlePINToggleMenu() throws CreateException, RemoteException, IOException {
        int res = 0;
        String digit = null;
        int count = 0;
        log.info((Object)"Handling menu 'pinToggleMenu'");
        if (this.userData == null) {
            log.info((Object)"User must be registered to continue");
            this.control.playback("ct0019");
            return 0;
        }
        if (!this.isCallerIdLenValid(this.callerId)) {
            log.info((Object)"Caller id is required to continue");
            this.control.playback("ct0009");
            return 0;
        }
        try {
            if (this.userData.mobilePhone == null || this.userData.mobilePhone.length() < 1) {
                log.info((Object)"User does not have mobile phone, taking second route");
                res = this.handlePINToggleSubMenu();
                if (res == 1 || res == 0) {
                    res = 0;
                    return res;
                }
                res = -1;
                return res;
            }
            log.info((Object)"User has mobile phone, taking first route");
            while (this.control.isConnected()) {
                digit = this.control.readInput("ct0010", 1, 3);
                if (digit != null && digit.length() > 0) {
                    log.info((Object)("User entered input '" + digit + "'"));
                    if (digit.equals("1")) {
                        res = this.handlePINToggleSubMenu();
                        if (res == 1 || res == 0) {
                            res = 0;
                            break;
                        }
                        res = -1;
                        break;
                    }
                    if (digit.equals("2")) {
                        if (this.userData.type != UserData.TypeEnum.MIG33_PREPAID_CARD) {
                            log.info((Object)("User '" + this.userData.username + "' is not a prepaid card caller; can't unregister number"));
                            this.control.playback("ct0038");
                            res = 0;
                            break;
                        }
                        UserData userDataNew = this.helper.updateUserMobilePhone(this.userData, DEF_ASTERISK_CURR);
                        if (userDataNew != null) {
                            log.info((Object)"Updated user mobile phone to '' (PIN-full)");
                            this.userData = userDataNew;
                            this.control.playback("ct0017");
                            res = 0;
                            break;
                        }
                        log.info((Object)"Could not update user mobile phone to '' (PIN-full)");
                        this.control.playback("ct0015");
                        res = 0;
                        break;
                    }
                    if (digit.equals("0")) {
                        res = 0;
                        break;
                    }
                    log.info((Object)"User entered an unavailable option or invalid data");
                    this.control.playback("ct0023");
                    if (count < 2) {
                        this.control.playback("ct0024");
                    }
                } else {
                    log.info((Object)"User timed out when entering digit");
                }
                log.info((Object)("User has '" + (3 - count - 1) + "' retry before max retries"));
                if (++count >= 3) {
                    res = -2;
                    break;
                }
                if (count < 2) continue;
                this.control.playback(DEF_ASTERISK_CURR);
            }
            return this.control.isConnected() ? res : -2;
        }
        catch (CreateException e) {
            throw e;
        }
        catch (RemoteException e) {
            return -1;
        }
    }

    public int handlePINToggleSubMenu() throws CreateException, RemoteException, IOException {
        int res = 0;
        String digit = null;
        int count = 0;
        log.info((Object)"Handling menu 'pinToggleSubMenu'");
        if (this.userData == null) {
            log.info((Object)"User must be registered to continue");
            this.control.playback("ct0019");
            return 0;
        }
        if (!this.isCallerIdLenValid(this.callerId)) {
            log.info((Object)"Caller id is required to continue");
            this.control.playback("ct0009");
            return 0;
        }
        try {
            if (this.userData.mobilePhone == null || this.userData.mobilePhone.length() < 1) {
                this.control.playback("ct0018");
                this.control.sayDigits(this.callerId);
            } else {
                this.control.playback("ct0004");
                this.control.sayDigits(this.callerId);
            }
            while (this.control.isConnected()) {
                digit = this.control.readInput("ct0032", 1, 3);
                if (digit != null && digit.length() > 0) {
                    log.info((Object)("User entered input '" + digit + "'"));
                    if (digit.equals("1")) {
                        UserData userDataNew = this.helper.updateUserMobilePhone(this.userData, this.callerId);
                        if (!this.isCallerIdLenValid(this.callerId)) {
                            log.info((Object)("Updated user mobile phone to '" + this.callerId + "' (PIN-less)"));
                            log.info((Object)"Caller id is required to continue");
                            this.control.playback("ct0009");
                            res = 0;
                            break;
                        }
                        if (userDataNew != null) {
                            log.info((Object)("Updated user mobile phone to '" + this.callerId + "' (PIN-less)"));
                            this.userData = userDataNew;
                            this.control.playback("ct0016");
                            res = 0;
                            break;
                        }
                        log.info((Object)("Could not update user mobile phone to '" + this.callerId + "' (PIN-less)"));
                        this.control.playback("ct0015");
                        res = 0;
                        break;
                    }
                    if (digit.equals("0")) {
                        res = 0;
                        break;
                    }
                    log.info((Object)"User entered an unavailable option or invalid data");
                    this.control.playback("ct0023");
                    if (count < 2) {
                        this.control.playback("ct0024");
                    }
                } else {
                    log.info((Object)"User timed out when entering digit");
                }
                log.info((Object)("User has '" + (3 - count - 1) + "' retry before max retries"));
                if (++count >= 3) {
                    res = -2;
                    break;
                }
                if (count < 2) continue;
                this.control.playback(DEF_ASTERISK_CURR);
            }
            return this.control.isConnected() ? res : -2;
        }
        catch (CreateException e) {
            throw e;
        }
        catch (RemoteException e) {
            return -1;
        }
    }

    public int handleCallMenu(CallRequest request) throws CreateException, RemoteException, IOException {
        int res = 0;
        String digit = null;
        int count = 0;
        log.info((Object)"Handling menu 'callMenu'");
        if (this.userData == null) {
            log.info((Object)"User must be registered to continue");
            this.control.playback("ct0019");
            return 0;
        }
        try {
            request = this.helper.evaluateCallRequest(request);
            if (request == null) {
                this.control.playback("ct0030");
                res = -1;
                return this.control.isConnected() ? res : -2;
            }
            log.info((Object)("User's call request: dialcommand='" + request.getDialCommand() + "', " + "limitdur='" + request.getLimitDuration() + "', " + "limitrate='" + request.getLimitRate() + "', " + "limitwarn='" + request.getLimitTimeoutWarning() + "', " + "limitrep='" + request.getLimitTimeoutRepeat() + "'"));
            if (request.limitDuration < 1L) {
                log.info((Object)"User's balance/limit is too low or call restricted (balance too low)");
                this.control.playback("ct0031");
                return -1;
            }
            if (request.getCallData().type == CallData.TypeEnum.MIDLET_CALL_THROUGH || request.getCallData().type == CallData.TypeEnum.MISSED_CALL_CALLBACK) {
                this.control.playback("ct0034");
                if (this.processPlaceCall(request)) {
                    log.info((Object)("Success placing the call to destination '" + request.getDestination() + "'"));
                    res = 1;
                    return this.control.isConnected() ? res : -2;
                }
                log.info((Object)("Failure placing the call to destination '" + request.getDestination() + "'"));
                this.control.playback("ct0030");
                res = -1;
                return this.control.isConnected() ? res : -2;
            }
            this.control.sayLimit(request.limitDuration / 1000L);
            while (this.control.isConnected()) {
                digit = this.control.readInput("ct0014", 1, 3);
                if (digit != null && digit.length() > 0) {
                    log.info((Object)("User entered input '" + digit + "'"));
                    if (digit.equals("0")) {
                        res = 0;
                        break;
                    }
                    if (digit.equals("1")) {
                        this.control.playback("ct0034");
                        if (this.processPlaceCall(request)) {
                            log.info((Object)("Success placing the call to destination '" + request.getDestination() + "'"));
                            res = 1;
                            break;
                        }
                        log.info((Object)("Failure placing the call to destination '" + request.getDestination() + "'"));
                        this.control.playback("ct0030");
                        res = -1;
                        break;
                    }
                    log.info((Object)"User entered an unavailable option or invalid data");
                    this.control.playback("ct0023");
                    if (count < 2) {
                        this.control.playback("ct0024");
                    }
                } else {
                    log.info((Object)"User timed out when entering digit");
                }
                log.info((Object)("User has '" + (3 - count - 1) + "' retry before max retries"));
                if (++count >= 3) {
                    res = -2;
                    break;
                }
                if (count < 2) continue;
                this.control.playback(DEF_ASTERISK_CURR);
            }
            return this.control.isConnected() ? res : -2;
        }
        catch (CreateException e) {
            throw e;
        }
        catch (RemoteException e) {
            return -1;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void execute() throws CreateException, RemoteException, IOException {
        int res = 0;
        try {
            block28: {
                try {
                    block35: {
                        block36: {
                            block34: {
                                block32: {
                                    block33: {
                                        block31: {
                                            block29: {
                                                block30: {
                                                    block27: {
                                                        log.info((Object)"Starting calling card system");
                                                        this.control.answer();
                                                        this.control.wait(1);
                                                        this.callerId = this.control.getAsteriskCallerId();
                                                        this.callerIdName = this.control.getAsteriskCallerIdName();
                                                        this.callerDid = this.control.getAsteriskCallerDid();
                                                        if ("115409019".equals(this.callerDid)) {
                                                            this.callerDid = "27115409019";
                                                        }
                                                        this.control.setAsteriskLanguage(DEF_ASTERISK_LANG);
                                                        this.control.setAsteriskCurrency(DEF_ASTERISK_CURR);
                                                        if (this.callerId == null || this.callerId.length() < 1 || this.callerId.equals("0")) {
                                                            this.callerId = null;
                                                        }
                                                        if (this.callerDid == null || this.callerDid.length() < 1) {
                                                            this.callerDid = null;
                                                        }
                                                        String oldCallerId = this.callerId;
                                                        String oldCallerIdName = this.callerIdName;
                                                        String oldCallerDid = this.callerDid;
                                                        try {
                                                            this.callerDid = this.helper.cleanPhoneNumber(this.callerDid);
                                                            if (this.callerId != null && this.callerId.length() > 0) {
                                                                this.callerId = this.helper.fixCallerCountryCode(this.callerId, this.callerDid);
                                                                this.callerId = this.helper.fixValidateNumber(this.callerId);
                                                            }
                                                            this.callerIdName = this.callerId;
                                                            this.control.setAsteriskCallerId(this.callerId == null ? DEF_ASTERISK_CURR : this.callerId);
                                                            this.control.setAsteriskCallerIdName(this.callerIdName == null ? DEF_ASTERISK_CURR : this.callerIdName);
                                                            this.control.setAsteriskCallerDid(this.callerDid == null ? DEF_ASTERISK_CURR : this.callerDid);
                                                        }
                                                        catch (CreateException e) {
                                                            throw e;
                                                        }
                                                        catch (RemoteException e) {
                                                        }
                                                        catch (IOException e) {
                                                            // empty catch block
                                                        }
                                                        String astCallerId = this.control.getAsteriskCallerId();
                                                        String astCallerIdName = this.control.getAsteriskCallerIdName();
                                                        String astCallerDid = this.control.getAsteriskCallerDid();
                                                        log.info((Object)("CallerDID was '" + oldCallerDid + "', but is now fixed to '" + this.callerDid + "'"));
                                                        log.info((Object)("CallerID was '" + oldCallerId + "', but is now fixed to '" + this.callerId + "'"));
                                                        log.info((Object)("CallerIDName was '" + oldCallerIdName + "', but is now fixed to '" + this.callerIdName + "'"));
                                                        log.info((Object)("Asterisk CallerDID is set to '" + astCallerDid + "'"));
                                                        log.info((Object)("Asterisk CallerID is set to '" + astCallerId + "'"));
                                                        log.info((Object)("Asterisk CallerIDName is set to '" + astCallerIdName + "'"));
                                                        try {
                                                            if (this.callerId == null || this.callerId.length() <= 0) break block27;
                                                            log.info((Object)("User is calling from CID '" + this.callerId + "' on DID '" + this.callerDid + "'"));
                                                            this.userData = this.helper.loadUserFromMobilePhone(this.callerId);
                                                            if (this.userData != null) {
                                                                log.info((Object)("Found username '" + this.userData.username + "' with that caller id"));
                                                            }
                                                        }
                                                        catch (CreateException e) {
                                                            throw e;
                                                        }
                                                        catch (RemoteException e) {
                                                        }
                                                        catch (IOException e) {
                                                            // empty catch block
                                                        }
                                                    }
                                                    if (!this.control.isConnected()) break block28;
                                                    res = 0;
                                                    if (this.userData == null) break block30;
                                                    boolean isAniCallback = "1".equals(this.command.getParameter("ani_callback"));
                                                    if (isAniCallback) {
                                                        log.info((Object)("Missed call callback from registered user '" + this.userData.username + "'"));
                                                        CallRequest request = this.helper.createMissedCallCallbackRequest(this.userData, this.callerId, this.command.getParameterAsInt("leg_a_carrier_id"));
                                                        request = this.helper.initiateCallRequest(request);
                                                        this.control.playback("ct0020");
                                                        res = this.handleMainMenu(request);
                                                        break block29;
                                                    } else {
                                                        CallRequest request = null;
                                                        try {
                                                            request = this.helper.getPendingMidletRequest(this.userData, this.callerDid);
                                                        }
                                                        catch (CreateException e) {
                                                            throw e;
                                                        }
                                                        catch (RemoteException e) {
                                                        }
                                                        catch (IOException e) {
                                                            // empty catch block
                                                        }
                                                        if (request == null) {
                                                            log.info((Object)("Found registered user '" + this.userData.username + "'"));
                                                            this.control.playback("ct0020");
                                                            request = this.helper.createDirectCallThroughRequest(this.userData, this.callerId, this.callerDid);
                                                            res = this.handleMainMenu(request);
                                                            break block29;
                                                        } else {
                                                            log.info((Object)("Found midlet request for '" + this.userData.username + "'"));
                                                            res = this.handleCallMenu(request);
                                                            res = 1;
                                                        }
                                                    }
                                                    break block29;
                                                }
                                                log.info((Object)"Could not find registered user");
                                                this.control.playback("ct0020");
                                            }
                                            if (res != 0) break block31;
                                            res = 0;
                                            res = this.handleAuthMenu();
                                            if (res == 1) break block32;
                                            break block33;
                                        }
                                        this.control.playback("ct0006", 4);
                                        break block28;
                                    }
                                    if (res != 0) break block34;
                                }
                                res = 0;
                                if (this.userData == null) break block35;
                                break block36;
                            }
                            this.control.playback("ct0006", 4);
                            break block28;
                        }
                        CallRequest request = this.helper.createDirectCallThroughRequest(this.userData, this.callerId, this.callerDid);
                        res = this.handleMainMenu(request);
                    }
                    this.control.playback("ct0006", 4);
                }
                catch (CreateException e) {
                    log.warn((Object)("Error running the calling card system (ex1); " + e.getMessage()));
                    Object var12_26 = null;
                    this.control.wait(1);
                    this.control.hangup();
                    log.info((Object)"Completed the calling card system");
                    return;
                }
                catch (RemoteException e) {
                    log.warn((Object)("Error running the calling card system (ex2); " + e.getMessage()));
                    Object var12_27 = null;
                    this.control.wait(1);
                    this.control.hangup();
                    log.info((Object)"Completed the calling card system");
                    return;
                }
                catch (IOException e) {
                    log.warn((Object)("Error running the calling card system (ex3); " + e.getMessage()));
                    Object var12_28 = null;
                    this.control.wait(1);
                    this.control.hangup();
                    log.info((Object)"Completed the calling card system");
                    return;
                }
            }
            Object var12_25 = null;
            this.control.wait(1);
            this.control.hangup();
            log.info((Object)"Completed the calling card system");
            return;
        }
        catch (Throwable throwable) {
            Object var12_29 = null;
            this.control.wait(1);
            this.control.hangup();
            log.info((Object)"Completed the calling card system");
            throw throwable;
        }
    }
}

