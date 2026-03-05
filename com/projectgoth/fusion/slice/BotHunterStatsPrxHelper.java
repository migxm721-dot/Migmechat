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
import com.projectgoth.fusion.slice.BotHunterStatsPrx;
import com.projectgoth.fusion.slice._BotHunterStatsDelD;
import com.projectgoth.fusion.slice._BotHunterStatsDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class BotHunterStatsPrxHelper
extends ObjectPrxHelperBase
implements BotHunterStatsPrx {
    public static BotHunterStatsPrx checkedCast(ObjectPrx __obj) {
        BotHunterStatsPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (BotHunterStatsPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::BotHunterStats")) break block3;
                    BotHunterStatsPrxHelper __h = new BotHunterStatsPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static BotHunterStatsPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        BotHunterStatsPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (BotHunterStatsPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::BotHunterStats", __ctx)) break block3;
                    BotHunterStatsPrxHelper __h = new BotHunterStatsPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static BotHunterStatsPrx checkedCast(ObjectPrx __obj, String __facet) {
        BotHunterStatsPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::BotHunterStats")) {
                    BotHunterStatsPrxHelper __h = new BotHunterStatsPrxHelper();
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

    public static BotHunterStatsPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        BotHunterStatsPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::BotHunterStats", __ctx)) {
                    BotHunterStatsPrxHelper __h = new BotHunterStatsPrxHelper();
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

    public static BotHunterStatsPrx uncheckedCast(ObjectPrx __obj) {
        BotHunterStatsPrx __d = null;
        if (__obj != null) {
            try {
                __d = (BotHunterStatsPrx)__obj;
            }
            catch (ClassCastException ex) {
                BotHunterStatsPrxHelper __h = new BotHunterStatsPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static BotHunterStatsPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        BotHunterStatsPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            BotHunterStatsPrxHelper __h = new BotHunterStatsPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _BotHunterStatsDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _BotHunterStatsDelD();
    }

    public static void __write(BasicStream __os, BotHunterStatsPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static BotHunterStatsPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            BotHunterStatsPrxHelper result = new BotHunterStatsPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

