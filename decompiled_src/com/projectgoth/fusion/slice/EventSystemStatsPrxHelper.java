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
import com.projectgoth.fusion.slice.EventSystemStatsPrx;
import com.projectgoth.fusion.slice._EventSystemStatsDelD;
import com.projectgoth.fusion.slice._EventSystemStatsDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class EventSystemStatsPrxHelper
extends ObjectPrxHelperBase
implements EventSystemStatsPrx {
    public static EventSystemStatsPrx checkedCast(ObjectPrx __obj) {
        EventSystemStatsPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (EventSystemStatsPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::EventSystemStats")) break block3;
                    EventSystemStatsPrxHelper __h = new EventSystemStatsPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static EventSystemStatsPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        EventSystemStatsPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (EventSystemStatsPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::EventSystemStats", __ctx)) break block3;
                    EventSystemStatsPrxHelper __h = new EventSystemStatsPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static EventSystemStatsPrx checkedCast(ObjectPrx __obj, String __facet) {
        EventSystemStatsPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::EventSystemStats")) {
                    EventSystemStatsPrxHelper __h = new EventSystemStatsPrxHelper();
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

    public static EventSystemStatsPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        EventSystemStatsPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::EventSystemStats", __ctx)) {
                    EventSystemStatsPrxHelper __h = new EventSystemStatsPrxHelper();
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

    public static EventSystemStatsPrx uncheckedCast(ObjectPrx __obj) {
        EventSystemStatsPrx __d = null;
        if (__obj != null) {
            try {
                __d = (EventSystemStatsPrx)__obj;
            }
            catch (ClassCastException ex) {
                EventSystemStatsPrxHelper __h = new EventSystemStatsPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static EventSystemStatsPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        EventSystemStatsPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            EventSystemStatsPrxHelper __h = new EventSystemStatsPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _EventSystemStatsDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _EventSystemStatsDelD();
    }

    public static void __write(BasicStream __os, EventSystemStatsPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static EventSystemStatsPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            EventSystemStatsPrxHelper result = new EventSystemStatsPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

