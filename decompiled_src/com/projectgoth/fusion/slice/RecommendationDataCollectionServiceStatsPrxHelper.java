/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.FacetNotExistException
 *  Ice.ObjectPrx
 *  Ice.ObjectPrxHelperBase
 *  Ice._ObjectDelD
 *  Ice._ObjectDelM
 *  IceInternal.BasicStream
 */
package com.projectgoth.fusion.slice;

import Ice.FacetNotExistException;
import Ice.ObjectPrx;
import Ice.ObjectPrxHelperBase;
import Ice._ObjectDelD;
import Ice._ObjectDelM;
import IceInternal.BasicStream;
import com.projectgoth.fusion.slice.RecommendationDataCollectionServiceStatsPrx;
import com.projectgoth.fusion.slice._RecommendationDataCollectionServiceStatsDelD;
import com.projectgoth.fusion.slice._RecommendationDataCollectionServiceStatsDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class RecommendationDataCollectionServiceStatsPrxHelper
extends ObjectPrxHelperBase
implements RecommendationDataCollectionServiceStatsPrx {
    public static RecommendationDataCollectionServiceStatsPrx checkedCast(ObjectPrx __obj) {
        RecommendationDataCollectionServiceStatsPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (RecommendationDataCollectionServiceStatsPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::RecommendationDataCollectionServiceStats")) break block3;
                    RecommendationDataCollectionServiceStatsPrxHelper __h = new RecommendationDataCollectionServiceStatsPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static RecommendationDataCollectionServiceStatsPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        RecommendationDataCollectionServiceStatsPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (RecommendationDataCollectionServiceStatsPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::RecommendationDataCollectionServiceStats", __ctx)) break block3;
                    RecommendationDataCollectionServiceStatsPrxHelper __h = new RecommendationDataCollectionServiceStatsPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static RecommendationDataCollectionServiceStatsPrx checkedCast(ObjectPrx __obj, String __facet) {
        RecommendationDataCollectionServiceStatsPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::RecommendationDataCollectionServiceStats")) {
                    RecommendationDataCollectionServiceStatsPrxHelper __h = new RecommendationDataCollectionServiceStatsPrxHelper();
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

    public static RecommendationDataCollectionServiceStatsPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        RecommendationDataCollectionServiceStatsPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::RecommendationDataCollectionServiceStats", __ctx)) {
                    RecommendationDataCollectionServiceStatsPrxHelper __h = new RecommendationDataCollectionServiceStatsPrxHelper();
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

    public static RecommendationDataCollectionServiceStatsPrx uncheckedCast(ObjectPrx __obj) {
        RecommendationDataCollectionServiceStatsPrx __d = null;
        if (__obj != null) {
            try {
                __d = (RecommendationDataCollectionServiceStatsPrx)__obj;
            }
            catch (ClassCastException ex) {
                RecommendationDataCollectionServiceStatsPrxHelper __h = new RecommendationDataCollectionServiceStatsPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static RecommendationDataCollectionServiceStatsPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        RecommendationDataCollectionServiceStatsPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            RecommendationDataCollectionServiceStatsPrxHelper __h = new RecommendationDataCollectionServiceStatsPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _RecommendationDataCollectionServiceStatsDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _RecommendationDataCollectionServiceStatsDelD();
    }

    public static void __write(BasicStream __os, RecommendationDataCollectionServiceStatsPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static RecommendationDataCollectionServiceStatsPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            RecommendationDataCollectionServiceStatsPrxHelper result = new RecommendationDataCollectionServiceStatsPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

