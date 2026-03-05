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
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.SMSSender;
import com.projectgoth.fusion.slice.SystemSMSDataIce;
import java.util.Arrays;

public abstract class _SMSSenderDisp
extends ObjectImpl
implements SMSSender {
    public static final String[] __ids = new String[]{"::Ice::Object", "::com::projectgoth::fusion::slice::SMSSender"};
    private static final String[] __all = new String[]{"ice_id", "ice_ids", "ice_isA", "ice_ping", "sendSMS", "sendSystemSMS"};

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

    public final void sendSMS(MessageDataIce message, long delay) throws FusionException {
        this.sendSMS(message, delay, null);
    }

    public final void sendSystemSMS(SystemSMSDataIce message, long delay) throws FusionException {
        this.sendSystemSMS(message, delay, null);
    }

    public static DispatchStatus ___sendSMS(SMSSender __obj, Incoming __inS, Current __current) {
        _SMSSenderDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        MessageDataIce message = new MessageDataIce();
        message.__read(__is);
        long delay = __is.readLong();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            __obj.sendSMS(message, delay, __current);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___sendSystemSMS(SMSSender __obj, Incoming __inS, Current __current) {
        _SMSSenderDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        SystemSMSDataIce message = new SystemSMSDataIce();
        message.__read(__is);
        long delay = __is.readLong();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            __obj.sendSystemSMS(message, delay, __current);
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
                return _SMSSenderDisp.___ice_id((Object)this, (Incoming)in, (Current)__current);
            }
            case 1: {
                return _SMSSenderDisp.___ice_ids((Object)this, (Incoming)in, (Current)__current);
            }
            case 2: {
                return _SMSSenderDisp.___ice_isA((Object)this, (Incoming)in, (Current)__current);
            }
            case 3: {
                return _SMSSenderDisp.___ice_ping((Object)this, (Incoming)in, (Current)__current);
            }
            case 4: {
                return _SMSSenderDisp.___sendSMS(this, in, __current);
            }
            case 5: {
                return _SMSSenderDisp.___sendSystemSMS(this, in, __current);
            }
        }
        assert (false);
        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
    }

    public void __write(BasicStream __os) {
        __os.writeTypeId(_SMSSenderDisp.ice_staticId());
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
        ex.reason = "type com::projectgoth::fusion::slice::SMSSender was not generated with stream support";
        throw ex;
    }

    public void __read(InputStream __inS, boolean __rid) {
        MarshalException ex = new MarshalException();
        ex.reason = "type com::projectgoth::fusion::slice::SMSSender was not generated with stream support";
        throw ex;
    }
}

