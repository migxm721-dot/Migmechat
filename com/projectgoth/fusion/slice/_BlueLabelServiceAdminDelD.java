/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 *  Ice.DispatchStatus
 *  Ice.Object
 *  Ice.OperationMode
 *  Ice.OperationNotExistException
 *  Ice.SystemException
 *  Ice.UserException
 *  Ice._ObjectDelD
 *  IceInternal.Direct
 *  IceInternal.LocalExceptionWrapper
 */
package com.projectgoth.fusion.slice;

import Ice.Current;
import Ice.DispatchStatus;
import Ice.Object;
import Ice.OperationMode;
import Ice.OperationNotExistException;
import Ice.SystemException;
import Ice.UserException;
import Ice._ObjectDelD;
import IceInternal.Direct;
import IceInternal.LocalExceptionWrapper;
import com.projectgoth.fusion.slice.BlueLabelServiceAdmin;
import com.projectgoth.fusion.slice.BlueLabelServiceStats;
import com.projectgoth.fusion.slice.BlueLabelServiceStatsHolder;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice._BlueLabelServiceAdminDel;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class _BlueLabelServiceAdminDelD
extends _ObjectDelD
implements _BlueLabelServiceAdminDel {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public BlueLabelServiceStats getStats(Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "getStats", OperationMode.Normal, __ctx);
        final BlueLabelServiceStatsHolder __result = new BlueLabelServiceStatsHolder();
        Direct __direct = null;
        try {
            BlueLabelServiceStats blueLabelServiceStats;
            __direct = new Direct(__current){

                public DispatchStatus run(Object __obj) {
                    BlueLabelServiceAdmin __servant = null;
                    try {
                        __servant = (BlueLabelServiceAdmin)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __result.value = __servant.getStats(__current);
                        return DispatchStatus.DispatchOK;
                    }
                    catch (UserException __ex) {
                        this.setUserException(__ex);
                        return DispatchStatus.DispatchUserException;
                    }
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                blueLabelServiceStats = __result.value;
                java.lang.Object var8_10 = null;
            }
            catch (Throwable throwable) {
                java.lang.Object var8_11 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return blueLabelServiceStats;
        }
        catch (FusionException __ex) {
            throw __ex;
        }
        catch (SystemException __ex) {
            throw __ex;
        }
        catch (Throwable __ex) {
            LocalExceptionWrapper.throwWrapper((Throwable)__ex);
            return __result.value;
        }
    }
}

