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
import com.projectgoth.fusion.slice.ObjectCacheAdmin;
import com.projectgoth.fusion.slice.ObjectCacheStats;
import com.projectgoth.fusion.slice.StringArrayHelper;
import java.util.Arrays;

public abstract class _ObjectCacheAdminDisp
extends ObjectImpl
implements ObjectCacheAdmin {
    public static final String[] __ids = new String[]{"::Ice::Object", "::com::projectgoth::fusion::slice::ObjectCacheAdmin"};
    private static final String[] __all = new String[]{"getLoadWeightage", "getStats", "getUsernames", "ice_id", "ice_ids", "ice_isA", "ice_ping", "ping", "reloadEmotes", "setLoadWeightage"};

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

    public final int getLoadWeightage() {
        return this.getLoadWeightage(null);
    }

    public final ObjectCacheStats getStats() throws FusionException {
        return this.getStats(null);
    }

    public final String[] getUsernames() {
        return this.getUsernames(null);
    }

    public final int ping() {
        return this.ping(null);
    }

    public final void reloadEmotes() {
        this.reloadEmotes(null);
    }

    public final void setLoadWeightage(int weightage) {
        this.setLoadWeightage(weightage, null);
    }

    public static DispatchStatus ___ping(ObjectCacheAdmin __obj, Incoming __inS, Current __current) {
        _ObjectCacheAdminDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        __inS.is().skipEmptyEncaps();
        BasicStream __os = __inS.os();
        int __ret = __obj.ping(__current);
        __os.writeInt(__ret);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___getStats(ObjectCacheAdmin __obj, Incoming __inS, Current __current) {
        _ObjectCacheAdminDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        __inS.is().skipEmptyEncaps();
        BasicStream __os = __inS.os();
        try {
            ObjectCacheStats __ret = __obj.getStats(__current);
            __os.writeObject((Object)__ret);
            __os.writePendingObjects();
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___getUsernames(ObjectCacheAdmin __obj, Incoming __inS, Current __current) {
        _ObjectCacheAdminDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        __inS.is().skipEmptyEncaps();
        BasicStream __os = __inS.os();
        String[] __ret = __obj.getUsernames(__current);
        StringArrayHelper.write(__os, __ret);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___reloadEmotes(ObjectCacheAdmin __obj, Incoming __inS, Current __current) {
        _ObjectCacheAdminDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        __inS.is().skipEmptyEncaps();
        __obj.reloadEmotes(__current);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___setLoadWeightage(ObjectCacheAdmin __obj, Incoming __inS, Current __current) {
        _ObjectCacheAdminDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int weightage = __is.readInt();
        __is.endReadEncaps();
        __obj.setLoadWeightage(weightage, __current);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___getLoadWeightage(ObjectCacheAdmin __obj, Incoming __inS, Current __current) {
        _ObjectCacheAdminDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        __inS.is().skipEmptyEncaps();
        BasicStream __os = __inS.os();
        int __ret = __obj.getLoadWeightage(__current);
        __os.writeInt(__ret);
        return DispatchStatus.DispatchOK;
    }

    public DispatchStatus __dispatch(Incoming in, Current __current) {
        int pos = Arrays.binarySearch(__all, __current.operation);
        if (pos < 0) {
            throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
        }
        switch (pos) {
            case 0: {
                return _ObjectCacheAdminDisp.___getLoadWeightage(this, in, __current);
            }
            case 1: {
                return _ObjectCacheAdminDisp.___getStats(this, in, __current);
            }
            case 2: {
                return _ObjectCacheAdminDisp.___getUsernames(this, in, __current);
            }
            case 3: {
                return _ObjectCacheAdminDisp.___ice_id((Object)this, (Incoming)in, (Current)__current);
            }
            case 4: {
                return _ObjectCacheAdminDisp.___ice_ids((Object)this, (Incoming)in, (Current)__current);
            }
            case 5: {
                return _ObjectCacheAdminDisp.___ice_isA((Object)this, (Incoming)in, (Current)__current);
            }
            case 6: {
                return _ObjectCacheAdminDisp.___ice_ping((Object)this, (Incoming)in, (Current)__current);
            }
            case 7: {
                return _ObjectCacheAdminDisp.___ping(this, in, __current);
            }
            case 8: {
                return _ObjectCacheAdminDisp.___reloadEmotes(this, in, __current);
            }
            case 9: {
                return _ObjectCacheAdminDisp.___setLoadWeightage(this, in, __current);
            }
        }
        assert (false);
        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
    }

    public void __write(BasicStream __os) {
        __os.writeTypeId(_ObjectCacheAdminDisp.ice_staticId());
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
        ex.reason = "type com::projectgoth::fusion::slice::ObjectCacheAdmin was not generated with stream support";
        throw ex;
    }

    public void __read(InputStream __inS, boolean __rid) {
        MarshalException ex = new MarshalException();
        ex.reason = "type com::projectgoth::fusion::slice::ObjectCacheAdmin was not generated with stream support";
        throw ex;
    }
}

