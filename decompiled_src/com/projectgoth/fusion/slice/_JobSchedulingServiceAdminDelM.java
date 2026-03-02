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
 *  IceInternal.Patcher
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
import IceInternal.Patcher;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.JobSchedulingServiceStats;
import com.projectgoth.fusion.slice.JobSchedulingServiceStatsHolder;
import com.projectgoth.fusion.slice._JobSchedulingServiceAdminDel;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class _JobSchedulingServiceAdminDelM
extends _ObjectDelM
implements _JobSchedulingServiceAdminDel {
    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public JobSchedulingServiceStats getStats(Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        JobSchedulingServiceStats jobSchedulingServiceStats;
        Outgoing __og = this.__handler.getOutgoing("getStats", OperationMode.Normal, __ctx);
        try {
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
                JobSchedulingServiceStatsHolder __ret = new JobSchedulingServiceStatsHolder();
                __is.readObject((Patcher)__ret.getPatcher());
                __is.readPendingObjects();
                __is.endReadEncaps();
                jobSchedulingServiceStats = __ret.value;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var8_10 = null;
        }
        catch (Throwable throwable) {
            Object var8_11 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return jobSchedulingServiceStats;
    }
}

