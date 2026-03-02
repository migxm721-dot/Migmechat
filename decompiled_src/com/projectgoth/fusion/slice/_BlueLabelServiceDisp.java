/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 *  Ice.DispatchStatus
 *  Ice.InputStream
 *  Ice.MarshalException
 *  Ice.Object
 *  Ice.ObjectImpl
 *  Ice.OperationMode
 *  Ice.OperationNotExistException
 *  Ice.OutputStream
 *  Ice.UserException
 *  IceInternal.BasicStream
 *  IceInternal.Incoming
 */
package com.projectgoth.fusion.slice;

import Ice.Current;
import Ice.DispatchStatus;
import Ice.InputStream;
import Ice.MarshalException;
import Ice.Object;
import Ice.ObjectImpl;
import Ice.OperationMode;
import Ice.OperationNotExistException;
import Ice.OutputStream;
import Ice.UserException;
import IceInternal.BasicStream;
import IceInternal.Incoming;
import com.projectgoth.fusion.slice.BlueLabelOneVoucher;
import com.projectgoth.fusion.slice.BlueLabelService;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.WebServiceResponse;
import java.util.Arrays;

public abstract class _BlueLabelServiceDisp
extends ObjectImpl
implements BlueLabelService {
    public static final String[] __ids = new String[]{"::Ice::Object", "::com::projectgoth::fusion::slice::BlueLabelService"};
    private static final String[] __all = new String[]{"authenticate", "fullVoucherRedemption", "getAccountStatus", "ice_id", "ice_ids", "ice_isA", "ice_ping", "registerAccount"};

    protected void ice_copyStateFrom(Object __obj) throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public boolean ice_isA(String s) {
        return Arrays.binarySearch(__ids, s) >= 0;
    }

    public boolean ice_isA(String s, Current __current) {
        return Arrays.binarySearch(__ids, s) >= 0;
    }

    public String[] ice_ids() {
        return __ids;
    }

    public String[] ice_ids(Current __current) {
        return __ids;
    }

    public String ice_id() {
        return __ids[1];
    }

    public String ice_id(Current __current) {
        return __ids[1];
    }

    public static String ice_staticId() {
        return __ids[1];
    }

    public final WebServiceResponse authenticate(String username) throws FusionException {
        return this.authenticate(username, null);
    }

    public final WebServiceResponse fullVoucherRedemption(String migUsername, String userTicket, BlueLabelOneVoucher voucher) throws FusionException {
        return this.fullVoucherRedemption(migUsername, userTicket, voucher, null);
    }

    public final WebServiceResponse getAccountStatus(String liveid) throws FusionException {
        return this.getAccountStatus(liveid, null);
    }

    public final WebServiceResponse registerAccount(String username, String password, int countryCode, String mobileNumber, int secretQuestionCode, String secretQuestionAnswer, String firstName, String lastName, String nickName, String dateOfBirth, String sex, String emailAddress) throws FusionException {
        return this.registerAccount(username, password, countryCode, mobileNumber, secretQuestionCode, secretQuestionAnswer, firstName, lastName, nickName, dateOfBirth, sex, emailAddress, null);
    }

    public static DispatchStatus ___registerAccount(BlueLabelService __obj, Incoming __inS, Current __current) {
        _BlueLabelServiceDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String username = __is.readString();
        String password = __is.readString();
        int countryCode = __is.readInt();
        String mobileNumber = __is.readString();
        int secretQuestionCode = __is.readInt();
        String secretQuestionAnswer = __is.readString();
        String firstName = __is.readString();
        String lastName = __is.readString();
        String nickName = __is.readString();
        String dateOfBirth = __is.readString();
        String sex = __is.readString();
        String emailAddress = __is.readString();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            WebServiceResponse __ret = __obj.registerAccount(username, password, countryCode, mobileNumber, secretQuestionCode, secretQuestionAnswer, firstName, lastName, nickName, dateOfBirth, sex, emailAddress, __current);
            __ret.__write(__os);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___fullVoucherRedemption(BlueLabelService __obj, Incoming __inS, Current __current) {
        _BlueLabelServiceDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String migUsername = __is.readString();
        String userTicket = __is.readString();
        BlueLabelOneVoucher voucher = new BlueLabelOneVoucher();
        voucher.__read(__is);
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            WebServiceResponse __ret = __obj.fullVoucherRedemption(migUsername, userTicket, voucher, __current);
            __ret.__write(__os);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___getAccountStatus(BlueLabelService __obj, Incoming __inS, Current __current) {
        _BlueLabelServiceDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String liveid = __is.readString();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            WebServiceResponse __ret = __obj.getAccountStatus(liveid, __current);
            __ret.__write(__os);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___authenticate(BlueLabelService __obj, Incoming __inS, Current __current) {
        _BlueLabelServiceDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String username = __is.readString();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            WebServiceResponse __ret = __obj.authenticate(username, __current);
            __ret.__write(__os);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public DispatchStatus __dispatch(Incoming in, Current __current) {
        int pos = Arrays.binarySearch(__all, __current.operation);
        if (pos < 0) {
            throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
        }
        switch (pos) {
            case 0: {
                return _BlueLabelServiceDisp.___authenticate(this, in, __current);
            }
            case 1: {
                return _BlueLabelServiceDisp.___fullVoucherRedemption(this, in, __current);
            }
            case 2: {
                return _BlueLabelServiceDisp.___getAccountStatus(this, in, __current);
            }
            case 3: {
                return _BlueLabelServiceDisp.___ice_id((Object)this, (Incoming)in, (Current)__current);
            }
            case 4: {
                return _BlueLabelServiceDisp.___ice_ids((Object)this, (Incoming)in, (Current)__current);
            }
            case 5: {
                return _BlueLabelServiceDisp.___ice_isA((Object)this, (Incoming)in, (Current)__current);
            }
            case 6: {
                return _BlueLabelServiceDisp.___ice_ping((Object)this, (Incoming)in, (Current)__current);
            }
            case 7: {
                return _BlueLabelServiceDisp.___registerAccount(this, in, __current);
            }
        }
        assert (false);
        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
    }

    public void __write(BasicStream __os) {
        __os.writeTypeId(_BlueLabelServiceDisp.ice_staticId());
        __os.startWriteSlice();
        __os.endWriteSlice();
        super.__write(__os);
    }

    public void __read(BasicStream __is, boolean __rid) {
        if (__rid) {
            __is.readTypeId();
        }
        __is.startReadSlice();
        __is.endReadSlice();
        super.__read(__is, true);
    }

    public void __write(OutputStream __outS) {
        MarshalException ex = new MarshalException();
        ex.reason = "type com::projectgoth::fusion::slice::BlueLabelService was not generated with stream support";
        throw ex;
    }

    public void __read(InputStream __inS, boolean __rid) {
        MarshalException ex = new MarshalException();
        ex.reason = "type com::projectgoth::fusion::slice::BlueLabelService was not generated with stream support";
        throw ex;
    }
}

