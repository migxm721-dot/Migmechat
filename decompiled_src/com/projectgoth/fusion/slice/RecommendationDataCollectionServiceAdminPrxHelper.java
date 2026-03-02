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
import com.projectgoth.fusion.slice.FusionExceptionWithRefCode;
import com.projectgoth.fusion.slice.RecommendationDataCollectionServiceAdminPrx;
import com.projectgoth.fusion.slice.RecommendationDataCollectionServiceStats;
import com.projectgoth.fusion.slice._RecommendationDataCollectionServiceAdminDel;
import com.projectgoth.fusion.slice._RecommendationDataCollectionServiceAdminDelD;
import com.projectgoth.fusion.slice._RecommendationDataCollectionServiceAdminDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class RecommendationDataCollectionServiceAdminPrxHelper
extends ObjectPrxHelperBase
implements RecommendationDataCollectionServiceAdminPrx {
    @Override
    public RecommendationDataCollectionServiceStats getStats() throws FusionExceptionWithRefCode {
        return this.getStats(null, false);
    }

    @Override
    public RecommendationDataCollectionServiceStats getStats(Map<String, String> __ctx) throws FusionExceptionWithRefCode {
        return this.getStats(__ctx, true);
    }

    private RecommendationDataCollectionServiceStats getStats(Map<String, String> __ctx, boolean __explicitCtx) throws FusionExceptionWithRefCode {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getStats");
                __delBase = this.__getDelegate(false);
                _RecommendationDataCollectionServiceAdminDel __del = (_RecommendationDataCollectionServiceAdminDel)__delBase;
                return __del.getStats(__ctx);
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

    public static RecommendationDataCollectionServiceAdminPrx checkedCast(ObjectPrx __obj) {
        RecommendationDataCollectionServiceAdminPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (RecommendationDataCollectionServiceAdminPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::RecommendationDataCollectionServiceAdmin")) break block3;
                    RecommendationDataCollectionServiceAdminPrxHelper __h = new RecommendationDataCollectionServiceAdminPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static RecommendationDataCollectionServiceAdminPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        RecommendationDataCollectionServiceAdminPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (RecommendationDataCollectionServiceAdminPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::RecommendationDataCollectionServiceAdmin", __ctx)) break block3;
                    RecommendationDataCollectionServiceAdminPrxHelper __h = new RecommendationDataCollectionServiceAdminPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static RecommendationDataCollectionServiceAdminPrx checkedCast(ObjectPrx __obj, String __facet) {
        RecommendationDataCollectionServiceAdminPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::RecommendationDataCollectionServiceAdmin")) {
                    RecommendationDataCollectionServiceAdminPrxHelper __h = new RecommendationDataCollectionServiceAdminPrxHelper();
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

    public static RecommendationDataCollectionServiceAdminPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        RecommendationDataCollectionServiceAdminPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::RecommendationDataCollectionServiceAdmin", __ctx)) {
                    RecommendationDataCollectionServiceAdminPrxHelper __h = new RecommendationDataCollectionServiceAdminPrxHelper();
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

    public static RecommendationDataCollectionServiceAdminPrx uncheckedCast(ObjectPrx __obj) {
        RecommendationDataCollectionServiceAdminPrx __d = null;
        if (__obj != null) {
            try {
                __d = (RecommendationDataCollectionServiceAdminPrx)__obj;
            }
            catch (ClassCastException ex) {
                RecommendationDataCollectionServiceAdminPrxHelper __h = new RecommendationDataCollectionServiceAdminPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static RecommendationDataCollectionServiceAdminPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        RecommendationDataCollectionServiceAdminPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            RecommendationDataCollectionServiceAdminPrxHelper __h = new RecommendationDataCollectionServiceAdminPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _RecommendationDataCollectionServiceAdminDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _RecommendationDataCollectionServiceAdminDelD();
    }

    public static void __write(BasicStream __os, RecommendationDataCollectionServiceAdminPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static RecommendationDataCollectionServiceAdminPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            RecommendationDataCollectionServiceAdminPrxHelper result = new RecommendationDataCollectionServiceAdminPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

