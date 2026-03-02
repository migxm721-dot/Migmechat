/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.FacetNotExistException
 *  Ice.LocalException
 *  Ice.ObjectPrx
 *  Ice.ObjectPrxHelperBase
 *  Ice._ObjectDel
 *  Ice._ObjectDelD
 *  Ice._ObjectDelM
 *  IceInternal.BasicStream
 *  IceInternal.LocalExceptionWrapper
 */
package com.projectgoth.fusion.slice;

import Ice.FacetNotExistException;
import Ice.LocalException;
import Ice.ObjectPrx;
import Ice.ObjectPrxHelperBase;
import Ice._ObjectDel;
import Ice._ObjectDelD;
import Ice._ObjectDelM;
import IceInternal.BasicStream;
import IceInternal.LocalExceptionWrapper;
import com.projectgoth.fusion.slice.CollectedDataIce;
import com.projectgoth.fusion.slice.FusionExceptionWithRefCode;
import com.projectgoth.fusion.slice.RecommendationDataCollectionServicePrx;
import com.projectgoth.fusion.slice._RecommendationDataCollectionServiceDel;
import com.projectgoth.fusion.slice._RecommendationDataCollectionServiceDelD;
import com.projectgoth.fusion.slice._RecommendationDataCollectionServiceDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class RecommendationDataCollectionServicePrxHelper
extends ObjectPrxHelperBase
implements RecommendationDataCollectionServicePrx {
    @Override
    public void logData(CollectedDataIce dataIce) throws FusionExceptionWithRefCode {
        this.logData(dataIce, null, false);
    }

    @Override
    public void logData(CollectedDataIce dataIce, Map<String, String> __ctx) throws FusionExceptionWithRefCode {
        this.logData(dataIce, __ctx, true);
    }

    private void logData(CollectedDataIce dataIce, Map<String, String> __ctx, boolean __explicitCtx) throws FusionExceptionWithRefCode {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("logData");
                __delBase = this.__getDelegate(false);
                _RecommendationDataCollectionServiceDel __del = (_RecommendationDataCollectionServiceDel)__delBase;
                __del.logData(dataIce, __ctx);
                return;
            }
            catch (LocalExceptionWrapper __ex) {
                this.__handleExceptionWrapper(__delBase, __ex, null);
                continue;
            }
            catch (LocalException __ex) {
                __cnt = this.__handleException(__delBase, __ex, null, __cnt);
                continue;
            }
            break;
        }
    }

    public static RecommendationDataCollectionServicePrx checkedCast(ObjectPrx __obj) {
        RecommendationDataCollectionServicePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (RecommendationDataCollectionServicePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::RecommendationDataCollectionService")) break block3;
                    RecommendationDataCollectionServicePrxHelper __h = new RecommendationDataCollectionServicePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static RecommendationDataCollectionServicePrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        RecommendationDataCollectionServicePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (RecommendationDataCollectionServicePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::RecommendationDataCollectionService", __ctx)) break block3;
                    RecommendationDataCollectionServicePrxHelper __h = new RecommendationDataCollectionServicePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static RecommendationDataCollectionServicePrx checkedCast(ObjectPrx __obj, String __facet) {
        RecommendationDataCollectionServicePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::RecommendationDataCollectionService")) {
                    RecommendationDataCollectionServicePrxHelper __h = new RecommendationDataCollectionServicePrxHelper();
                    __h.__copyFrom(__bb);
                    __d = __h;
                }
            }
            catch (FacetNotExistException ex) {
                // empty catch block
            }
        }
        return __d;
    }

    public static RecommendationDataCollectionServicePrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        RecommendationDataCollectionServicePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::RecommendationDataCollectionService", __ctx)) {
                    RecommendationDataCollectionServicePrxHelper __h = new RecommendationDataCollectionServicePrxHelper();
                    __h.__copyFrom(__bb);
                    __d = __h;
                }
            }
            catch (FacetNotExistException ex) {
                // empty catch block
            }
        }
        return __d;
    }

    public static RecommendationDataCollectionServicePrx uncheckedCast(ObjectPrx __obj) {
        RecommendationDataCollectionServicePrx __d = null;
        if (__obj != null) {
            try {
                __d = (RecommendationDataCollectionServicePrx)__obj;
            }
            catch (ClassCastException ex) {
                RecommendationDataCollectionServicePrxHelper __h = new RecommendationDataCollectionServicePrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static RecommendationDataCollectionServicePrx uncheckedCast(ObjectPrx __obj, String __facet) {
        RecommendationDataCollectionServicePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            RecommendationDataCollectionServicePrxHelper __h = new RecommendationDataCollectionServicePrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _RecommendationDataCollectionServiceDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _RecommendationDataCollectionServiceDelD();
    }

    public static void __write(BasicStream __os, RecommendationDataCollectionServicePrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static RecommendationDataCollectionServicePrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            RecommendationDataCollectionServicePrxHelper result = new RecommendationDataCollectionServicePrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

