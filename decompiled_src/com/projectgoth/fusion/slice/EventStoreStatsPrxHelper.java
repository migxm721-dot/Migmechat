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
import com.projectgoth.fusion.slice.EventStoreStatsPrx;
import com.projectgoth.fusion.slice._EventStoreStatsDelD;
import com.projectgoth.fusion.slice._EventStoreStatsDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class EventStoreStatsPrxHelper
extends ObjectPrxHelperBase
implements EventStoreStatsPrx {
    public static EventStoreStatsPrx checkedCast(ObjectPrx __obj) {
        EventStoreStatsPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (EventStoreStatsPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::EventStoreStats")) break block3;
                    EventStoreStatsPrxHelper __h = new EventStoreStatsPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static EventStoreStatsPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        EventStoreStatsPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (EventStoreStatsPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::EventStoreStats", __ctx)) break block3;
                    EventStoreStatsPrxHelper __h = new EventStoreStatsPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static EventStoreStatsPrx checkedCast(ObjectPrx __obj, String __facet) {
        EventStoreStatsPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::EventStoreStats")) {
                    EventStoreStatsPrxHelper __h = new EventStoreStatsPrxHelper();
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

    public static EventStoreStatsPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        EventStoreStatsPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::EventStoreStats", __ctx)) {
                    EventStoreStatsPrxHelper __h = new EventStoreStatsPrxHelper();
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

    public static EventStoreStatsPrx uncheckedCast(ObjectPrx __obj) {
        EventStoreStatsPrx __d = null;
        if (__obj != null) {
            try {
                __d = (EventStoreStatsPrx)__obj;
            }
            catch (ClassCastException ex) {
                EventStoreStatsPrxHelper __h = new EventStoreStatsPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static EventStoreStatsPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        EventStoreStatsPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            EventStoreStatsPrxHelper __h = new EventStoreStatsPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _EventStoreStatsDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _EventStoreStatsDelD();
    }

    public static void __write(BasicStream __os, EventStoreStatsPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static EventStoreStatsPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            EventStoreStatsPrxHelper result = new EventStoreStatsPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

