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
import com.projectgoth.fusion.slice.ReputationServiceStatsPrx;
import com.projectgoth.fusion.slice._ReputationServiceStatsDelD;
import com.projectgoth.fusion.slice._ReputationServiceStatsDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class ReputationServiceStatsPrxHelper
extends ObjectPrxHelperBase
implements ReputationServiceStatsPrx {
    public static ReputationServiceStatsPrx checkedCast(ObjectPrx __obj) {
        ReputationServiceStatsPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (ReputationServiceStatsPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::ReputationServiceStats")) break block3;
                    ReputationServiceStatsPrxHelper __h = new ReputationServiceStatsPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static ReputationServiceStatsPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        ReputationServiceStatsPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (ReputationServiceStatsPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::ReputationServiceStats", __ctx)) break block3;
                    ReputationServiceStatsPrxHelper __h = new ReputationServiceStatsPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static ReputationServiceStatsPrx checkedCast(ObjectPrx __obj, String __facet) {
        ReputationServiceStatsPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::ReputationServiceStats")) {
                    ReputationServiceStatsPrxHelper __h = new ReputationServiceStatsPrxHelper();
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

    public static ReputationServiceStatsPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        ReputationServiceStatsPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::ReputationServiceStats", __ctx)) {
                    ReputationServiceStatsPrxHelper __h = new ReputationServiceStatsPrxHelper();
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

    public static ReputationServiceStatsPrx uncheckedCast(ObjectPrx __obj) {
        ReputationServiceStatsPrx __d = null;
        if (__obj != null) {
            try {
                __d = (ReputationServiceStatsPrx)__obj;
            }
            catch (ClassCastException ex) {
                ReputationServiceStatsPrxHelper __h = new ReputationServiceStatsPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static ReputationServiceStatsPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        ReputationServiceStatsPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            ReputationServiceStatsPrxHelper __h = new ReputationServiceStatsPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _ReputationServiceStatsDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _ReputationServiceStatsDelD();
    }

    public static void __write(BasicStream __os, ReputationServiceStatsPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static ReputationServiceStatsPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            ReputationServiceStatsPrxHelper result = new ReputationServiceStatsPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

