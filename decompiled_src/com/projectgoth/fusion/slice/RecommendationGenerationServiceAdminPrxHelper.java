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
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.RecommendationGenerationServiceAdminPrx;
import com.projectgoth.fusion.slice.RecommendationGenerationServiceStats;
import com.projectgoth.fusion.slice._RecommendationGenerationServiceAdminDel;
import com.projectgoth.fusion.slice._RecommendationGenerationServiceAdminDelD;
import com.projectgoth.fusion.slice._RecommendationGenerationServiceAdminDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class RecommendationGenerationServiceAdminPrxHelper
extends ObjectPrxHelperBase
implements RecommendationGenerationServiceAdminPrx {
    @Override
    public RecommendationGenerationServiceStats getStats() throws FusionException {
        return this.getStats(null, false);
    }

    @Override
    public RecommendationGenerationServiceStats getStats(Map<String, String> __ctx) throws FusionException {
        return this.getStats(__ctx, true);
    }

    private RecommendationGenerationServiceStats getStats(Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getStats");
                __delBase = this.__getDelegate(false);
                _RecommendationGenerationServiceAdminDel __del = (_RecommendationGenerationServiceAdminDel)__delBase;
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

    public static RecommendationGenerationServiceAdminPrx checkedCast(ObjectPrx __obj) {
        RecommendationGenerationServiceAdminPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (RecommendationGenerationServiceAdminPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::RecommendationGenerationServiceAdmin")) break block3;
                    RecommendationGenerationServiceAdminPrxHelper __h = new RecommendationGenerationServiceAdminPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static RecommendationGenerationServiceAdminPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        RecommendationGenerationServiceAdminPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (RecommendationGenerationServiceAdminPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::RecommendationGenerationServiceAdmin", __ctx)) break block3;
                    RecommendationGenerationServiceAdminPrxHelper __h = new RecommendationGenerationServiceAdminPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static RecommendationGenerationServiceAdminPrx checkedCast(ObjectPrx __obj, String __facet) {
        RecommendationGenerationServiceAdminPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::RecommendationGenerationServiceAdmin")) {
                    RecommendationGenerationServiceAdminPrxHelper __h = new RecommendationGenerationServiceAdminPrxHelper();
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

    public static RecommendationGenerationServiceAdminPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        RecommendationGenerationServiceAdminPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::RecommendationGenerationServiceAdmin", __ctx)) {
                    RecommendationGenerationServiceAdminPrxHelper __h = new RecommendationGenerationServiceAdminPrxHelper();
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

    public static RecommendationGenerationServiceAdminPrx uncheckedCast(ObjectPrx __obj) {
        RecommendationGenerationServiceAdminPrx __d = null;
        if (__obj != null) {
            try {
                __d = (RecommendationGenerationServiceAdminPrx)__obj;
            }
            catch (ClassCastException ex) {
                RecommendationGenerationServiceAdminPrxHelper __h = new RecommendationGenerationServiceAdminPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static RecommendationGenerationServiceAdminPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        RecommendationGenerationServiceAdminPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            RecommendationGenerationServiceAdminPrxHelper __h = new RecommendationGenerationServiceAdminPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _RecommendationGenerationServiceAdminDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _RecommendationGenerationServiceAdminDelD();
    }

    public static void __write(BasicStream __os, RecommendationGenerationServiceAdminPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static RecommendationGenerationServiceAdminPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            RecommendationGenerationServiceAdminPrxHelper result = new RecommendationGenerationServiceAdminPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

