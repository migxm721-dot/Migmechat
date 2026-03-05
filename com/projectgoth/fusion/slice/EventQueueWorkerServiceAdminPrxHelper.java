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
import com.projectgoth.fusion.slice.EventQueueWorkerServiceAdminPrx;
import com.projectgoth.fusion.slice.EventQueueWorkerServiceStats;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice._EventQueueWorkerServiceAdminDel;
import com.projectgoth.fusion.slice._EventQueueWorkerServiceAdminDelD;
import com.projectgoth.fusion.slice._EventQueueWorkerServiceAdminDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class EventQueueWorkerServiceAdminPrxHelper
extends ObjectPrxHelperBase
implements EventQueueWorkerServiceAdminPrx {
    @Override
    public EventQueueWorkerServiceStats getStats() throws FusionException {
        return this.getStats(null, false);
    }

    @Override
    public EventQueueWorkerServiceStats getStats(Map<String, String> __ctx) throws FusionException {
        return this.getStats(__ctx, true);
    }

    private EventQueueWorkerServiceStats getStats(Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getStats");
                __delBase = this.__getDelegate(false);
                _EventQueueWorkerServiceAdminDel __del = (_EventQueueWorkerServiceAdminDel)__delBase;
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

    public static EventQueueWorkerServiceAdminPrx checkedCast(ObjectPrx __obj) {
        EventQueueWorkerServiceAdminPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (EventQueueWorkerServiceAdminPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::EventQueueWorkerServiceAdmin")) break block3;
                    EventQueueWorkerServiceAdminPrxHelper __h = new EventQueueWorkerServiceAdminPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static EventQueueWorkerServiceAdminPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        EventQueueWorkerServiceAdminPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (EventQueueWorkerServiceAdminPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::EventQueueWorkerServiceAdmin", __ctx)) break block3;
                    EventQueueWorkerServiceAdminPrxHelper __h = new EventQueueWorkerServiceAdminPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static EventQueueWorkerServiceAdminPrx checkedCast(ObjectPrx __obj, String __facet) {
        EventQueueWorkerServiceAdminPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::EventQueueWorkerServiceAdmin")) {
                    EventQueueWorkerServiceAdminPrxHelper __h = new EventQueueWorkerServiceAdminPrxHelper();
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

    public static EventQueueWorkerServiceAdminPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        EventQueueWorkerServiceAdminPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::EventQueueWorkerServiceAdmin", __ctx)) {
                    EventQueueWorkerServiceAdminPrxHelper __h = new EventQueueWorkerServiceAdminPrxHelper();
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

    public static EventQueueWorkerServiceAdminPrx uncheckedCast(ObjectPrx __obj) {
        EventQueueWorkerServiceAdminPrx __d = null;
        if (__obj != null) {
            try {
                __d = (EventQueueWorkerServiceAdminPrx)__obj;
            }
            catch (ClassCastException ex) {
                EventQueueWorkerServiceAdminPrxHelper __h = new EventQueueWorkerServiceAdminPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static EventQueueWorkerServiceAdminPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        EventQueueWorkerServiceAdminPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            EventQueueWorkerServiceAdminPrxHelper __h = new EventQueueWorkerServiceAdminPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _EventQueueWorkerServiceAdminDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _EventQueueWorkerServiceAdminDelD();
    }

    public static void __write(BasicStream __os, EventQueueWorkerServiceAdminPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static EventQueueWorkerServiceAdminPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            EventQueueWorkerServiceAdminPrxHelper result = new EventQueueWorkerServiceAdminPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

