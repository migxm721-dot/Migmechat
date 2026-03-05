/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.LocalException
 *  Ice.Object
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
import Ice.Object;
import Ice.OperationMode;
import Ice.UnknownUserException;
import Ice.UserException;
import Ice._ObjectDelM;
import IceInternal.BasicStream;
import IceInternal.LocalExceptionWrapper;
import IceInternal.Outgoing;
import com.projectgoth.fusion.slice.CollectedDataIce;
import com.projectgoth.fusion.slice.FusionExceptionWithRefCode;
import com.projectgoth.fusion.slice._RecommendationDataCollectionServiceDel;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class _RecommendationDataCollectionServiceDelM
extends _ObjectDelM
implements _RecommendationDataCollectionServiceDel {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void logData(CollectedDataIce dataIce, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionExceptionWithRefCode {
        Outgoing __og = this.__handler.getOutgoing("logData", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeObject((Object)dataIce);
                __os.writePendingObjects();
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
                    catch (FusionExceptionWithRefCode __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                __og.is().skipEmptyEncaps();
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            java.lang.Object var7_10 = null;
        }
        catch (Throwable throwable) {
            java.lang.Object var7_11 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }
}

