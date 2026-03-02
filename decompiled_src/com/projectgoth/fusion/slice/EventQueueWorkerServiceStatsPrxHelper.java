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
import com.projectgoth.fusion.slice.EventQueueWorkerServiceStatsPrx;
import com.projectgoth.fusion.slice._EventQueueWorkerServiceStatsDelD;
import com.projectgoth.fusion.slice._EventQueueWorkerServiceStatsDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class EventQueueWorkerServiceStatsPrxHelper
extends ObjectPrxHelperBase
implements EventQueueWorkerServiceStatsPrx {
    public static EventQueueWorkerServiceStatsPrx checkedCast(ObjectPrx __obj) {
        EventQueueWorkerServiceStatsPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (EventQueueWorkerServiceStatsPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::EventQueueWorkerServiceStats")) break block3;
                    EventQueueWorkerServiceStatsPrxHelper __h = new EventQueueWorkerServiceStatsPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static EventQueueWorkerServiceStatsPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        EventQueueWorkerServiceStatsPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (EventQueueWorkerServiceStatsPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::EventQueueWorkerServiceStats", __ctx)) break block3;
                    EventQueueWorkerServiceStatsPrxHelper __h = new EventQueueWorkerServiceStatsPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static EventQueueWorkerServiceStatsPrx checkedCast(ObjectPrx __obj, String __facet) {
        EventQueueWorkerServiceStatsPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::EventQueueWorkerServiceStats")) {
                    EventQueueWorkerServiceStatsPrxHelper __h = new EventQueueWorkerServiceStatsPrxHelper();
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

    public static EventQueueWorkerServiceStatsPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        EventQueueWorkerServiceStatsPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::EventQueueWorkerServiceStats", __ctx)) {
                    EventQueueWorkerServiceStatsPrxHelper __h = new EventQueueWorkerServiceStatsPrxHelper();
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

    public static EventQueueWorkerServiceStatsPrx uncheckedCast(ObjectPrx __obj) {
        EventQueueWorkerServiceStatsPrx __d = null;
        if (__obj != null) {
            try {
                __d = (EventQueueWorkerServiceStatsPrx)__obj;
            }
            catch (ClassCastException ex) {
                EventQueueWorkerServiceStatsPrxHelper __h = new EventQueueWorkerServiceStatsPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static EventQueueWorkerServiceStatsPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        EventQueueWorkerServiceStatsPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            EventQueueWorkerServiceStatsPrxHelper __h = new EventQueueWorkerServiceStatsPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _EventQueueWorkerServiceStatsDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _EventQueueWorkerServiceStatsDelD();
    }

    public static void __write(BasicStream __os, EventQueueWorkerServiceStatsPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static EventQueueWorkerServiceStatsPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            EventQueueWorkerServiceStatsPrxHelper result = new EventQueueWorkerServiceStatsPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

