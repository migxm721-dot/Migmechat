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
 *  IceInternal.BasicStream
 *  IceInternal.Incoming
 */
package com.projectgoth.fusion.slice.tests;

import Ice.Current;
import Ice.DispatchStatus;
import Ice.InputStream;
import Ice.MarshalException;
import Ice.Object;
import Ice.ObjectImpl;
import Ice.OperationMode;
import Ice.OperationNotExistException;
import Ice.OutputStream;
import IceInternal.BasicStream;
import IceInternal.Incoming;
import com.projectgoth.fusion.slice.tests.Printer;
import java.util.Arrays;

public abstract class _PrinterDisp
extends ObjectImpl
implements Printer {
    public static final String[] __ids = new String[]{"::Ice::Object", "::com::projectgoth::fusion::slice::tests::Printer"};
    private static final String[] __all = new String[]{"circular", "ice_id", "ice_ids", "ice_isA", "ice_ping", "printString"};

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

    public final void circular(String s, int level) {
        this.circular(s, level, null);
    }

    public final void printString(String s) {
        this.printString(s, null);
    }

    public static DispatchStatus ___printString(Printer __obj, Incoming __inS, Current __current) {
        _PrinterDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String s = __is.readString();
        __is.endReadEncaps();
        __obj.printString(s, __current);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___circular(Printer __obj, Incoming __inS, Current __current) {
        _PrinterDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String s = __is.readString();
        int level = __is.readInt();
        __is.endReadEncaps();
        __obj.circular(s, level, __current);
        return DispatchStatus.DispatchOK;
    }

    public DispatchStatus __dispatch(Incoming in, Current __current) {
        int pos = Arrays.binarySearch(__all, __current.operation);
        if (pos < 0) {
            throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
        }
        switch (pos) {
            case 0: {
                return _PrinterDisp.___circular(this, in, __current);
            }
            case 1: {
                return _PrinterDisp.___ice_id((Object)this, (Incoming)in, (Current)__current);
            }
            case 2: {
                return _PrinterDisp.___ice_ids((Object)this, (Incoming)in, (Current)__current);
            }
            case 3: {
                return _PrinterDisp.___ice_isA((Object)this, (Incoming)in, (Current)__current);
            }
            case 4: {
                return _PrinterDisp.___ice_ping((Object)this, (Incoming)in, (Current)__current);
            }
            case 5: {
                return _PrinterDisp.___printString(this, in, __current);
            }
        }
        assert (false);
        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
    }

    public void __write(BasicStream __os) {
        __os.writeTypeId(_PrinterDisp.ice_staticId());
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
        ex.reason = "type com::projectgoth::fusion::slice::tests::Printer was not generated with stream support";
        throw ex;
    }

    public void __read(InputStream __inS, boolean __rid) {
        MarshalException ex = new MarshalException();
        ex.reason = "type com::projectgoth::fusion::slice::tests::Printer was not generated with stream support";
        throw ex;
    }
}

