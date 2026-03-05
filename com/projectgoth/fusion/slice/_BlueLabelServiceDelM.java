/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.LocalException
 *  Ice.OperationMode
 *  Ice.UnknownUserException
 *  Ice.UserException
 *  Ice._ObjectDelM
 *  IceInternal.BasicStream
 *  IceInternal.LocalExceptionWrapper
 *  IceInternal.Outgoing
 */
package com.projectgoth.fusion.slice;

import Ice.LocalException;
import Ice.OperationMode;
import Ice.UnknownUserException;
import Ice.UserException;
import Ice._ObjectDelM;
import IceInternal.BasicStream;
import IceInternal.LocalExceptionWrapper;
import IceInternal.Outgoing;
import com.projectgoth.fusion.slice.BlueLabelOneVoucher;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.WebServiceResponse;
import com.projectgoth.fusion.slice._BlueLabelServiceDel;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class _BlueLabelServiceDelM
extends _ObjectDelM
implements _BlueLabelServiceDel {
    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public WebServiceResponse authenticate(String username, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        WebServiceResponse webServiceResponse;
        Outgoing __og = this.__handler.getOutgoing("authenticate", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(username);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                BasicStream __is = __og.is();
                __is.startReadEncaps();
                WebServiceResponse __ret = new WebServiceResponse();
                __ret.__read(__is);
                __is.endReadEncaps();
                webServiceResponse = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var9_13 = null;
        }
        catch (Throwable throwable) {
            Object var9_14 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return webServiceResponse;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public WebServiceResponse fullVoucherRedemption(String migUsername, String userTicket, BlueLabelOneVoucher voucher, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        WebServiceResponse webServiceResponse;
        Outgoing __og = this.__handler.getOutgoing("fullVoucherRedemption", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(migUsername);
                __os.writeString(userTicket);
                voucher.__write(__os);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                BasicStream __is = __og.is();
                __is.startReadEncaps();
                WebServiceResponse __ret = new WebServiceResponse();
                __ret.__read(__is);
                __is.endReadEncaps();
                webServiceResponse = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var11_15 = null;
        }
        catch (Throwable throwable) {
            Object var11_16 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return webServiceResponse;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public WebServiceResponse getAccountStatus(String liveid, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        WebServiceResponse webServiceResponse;
        Outgoing __og = this.__handler.getOutgoing("getAccountStatus", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(liveid);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                BasicStream __is = __og.is();
                __is.startReadEncaps();
                WebServiceResponse __ret = new WebServiceResponse();
                __ret.__read(__is);
                __is.endReadEncaps();
                webServiceResponse = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var9_13 = null;
        }
        catch (Throwable throwable) {
            Object var9_14 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return webServiceResponse;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public WebServiceResponse registerAccount(String username, String password, int countryCode, String mobileNumber, int secretQuestionCode, String secretQuestionAnswer, String firstName, String lastName, String nickName, String dateOfBirth, String sex, String emailAddress, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        WebServiceResponse webServiceResponse;
        Outgoing __og = this.__handler.getOutgoing("registerAccount", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(username);
                __os.writeString(password);
                __os.writeInt(countryCode);
                __os.writeString(mobileNumber);
                __os.writeInt(secretQuestionCode);
                __os.writeString(secretQuestionAnswer);
                __os.writeString(firstName);
                __os.writeString(lastName);
                __os.writeString(nickName);
                __os.writeString(dateOfBirth);
                __os.writeString(sex);
                __os.writeString(emailAddress);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                BasicStream __is = __og.is();
                __is.startReadEncaps();
                WebServiceResponse __ret = new WebServiceResponse();
                __ret.__read(__is);
                __is.endReadEncaps();
                webServiceResponse = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var20_24 = null;
        }
        catch (Throwable throwable) {
            Object var20_25 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return webServiceResponse;
    }
}

