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
import com.projectgoth.fusion.slice.CallDataIce;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice._CallMakerDel;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class _CallMakerDelM
extends _ObjectDelM
implements _CallMakerDel {
    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public CallDataIce requestCallback(CallDataIce call, int maxDuration, int retries, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        CallDataIce callDataIce;
        Outgoing __og = this.__handler.getOutgoing("requestCallback", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                call.__write(__os);
                __os.writeInt(maxDuration);
                __os.writeInt(retries);
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
                CallDataIce __ret = new CallDataIce();
                __ret.__read(__is);
                __is.endReadEncaps();
                callDataIce = __ret;
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
        return callDataIce;
    }
}

