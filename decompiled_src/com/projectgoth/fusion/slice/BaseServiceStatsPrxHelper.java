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
import com.projectgoth.fusion.slice.BaseServiceStatsPrx;
import com.projectgoth.fusion.slice._BaseServiceStatsDelD;
import com.projectgoth.fusion.slice._BaseServiceStatsDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class BaseServiceStatsPrxHelper
extends ObjectPrxHelperBase
implements BaseServiceStatsPrx {
    public static BaseServiceStatsPrx checkedCast(ObjectPrx __obj) {
        BaseServiceStatsPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (BaseServiceStatsPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::BaseServiceStats")) break block3;
                    BaseServiceStatsPrxHelper __h = new BaseServiceStatsPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static BaseServiceStatsPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        BaseServiceStatsPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (BaseServiceStatsPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::BaseServiceStats", __ctx)) break block3;
                    BaseServiceStatsPrxHelper __h = new BaseServiceStatsPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static BaseServiceStatsPrx checkedCast(ObjectPrx __obj, String __facet) {
        BaseServiceStatsPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::BaseServiceStats")) {
                    BaseServiceStatsPrxHelper __h = new BaseServiceStatsPrxHelper();
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

    public static BaseServiceStatsPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        BaseServiceStatsPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::BaseServiceStats", __ctx)) {
                    BaseServiceStatsPrxHelper __h = new BaseServiceStatsPrxHelper();
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

    public static BaseServiceStatsPrx uncheckedCast(ObjectPrx __obj) {
        BaseServiceStatsPrx __d = null;
        if (__obj != null) {
            try {
                __d = (BaseServiceStatsPrx)__obj;
            }
            catch (ClassCastException ex) {
                BaseServiceStatsPrxHelper __h = new BaseServiceStatsPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static BaseServiceStatsPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        BaseServiceStatsPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            BaseServiceStatsPrxHelper __h = new BaseServiceStatsPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _BaseServiceStatsDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _BaseServiceStatsDelD();
    }

    public static void __write(BasicStream __os, BaseServiceStatsPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static BaseServiceStatsPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            BaseServiceStatsPrxHelper result = new BaseServiceStatsPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

