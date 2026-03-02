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
import com.projectgoth.fusion.slice.BotServiceStatsPrx;
import com.projectgoth.fusion.slice._BotServiceStatsDelD;
import com.projectgoth.fusion.slice._BotServiceStatsDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class BotServiceStatsPrxHelper
extends ObjectPrxHelperBase
implements BotServiceStatsPrx {
    public static BotServiceStatsPrx checkedCast(ObjectPrx __obj) {
        BotServiceStatsPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (BotServiceStatsPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::BotServiceStats")) break block3;
                    BotServiceStatsPrxHelper __h = new BotServiceStatsPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static BotServiceStatsPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        BotServiceStatsPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (BotServiceStatsPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::BotServiceStats", __ctx)) break block3;
                    BotServiceStatsPrxHelper __h = new BotServiceStatsPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static BotServiceStatsPrx checkedCast(ObjectPrx __obj, String __facet) {
        BotServiceStatsPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::BotServiceStats")) {
                    BotServiceStatsPrxHelper __h = new BotServiceStatsPrxHelper();
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

    public static BotServiceStatsPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        BotServiceStatsPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::BotServiceStats", __ctx)) {
                    BotServiceStatsPrxHelper __h = new BotServiceStatsPrxHelper();
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

    public static BotServiceStatsPrx uncheckedCast(ObjectPrx __obj) {
        BotServiceStatsPrx __d = null;
        if (__obj != null) {
            try {
                __d = (BotServiceStatsPrx)__obj;
            }
            catch (ClassCastException ex) {
                BotServiceStatsPrxHelper __h = new BotServiceStatsPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static BotServiceStatsPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        BotServiceStatsPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            BotServiceStatsPrxHelper __h = new BotServiceStatsPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _BotServiceStatsDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _BotServiceStatsDelD();
    }

    public static void __write(BasicStream __os, BotServiceStatsPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static BotServiceStatsPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            BotServiceStatsPrxHelper result = new BotServiceStatsPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

