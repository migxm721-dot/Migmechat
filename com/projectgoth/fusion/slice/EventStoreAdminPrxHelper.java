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
import com.projectgoth.fusion.slice.EventStoreAdminPrx;
import com.projectgoth.fusion.slice.EventStoreStats;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice._EventStoreAdminDel;
import com.projectgoth.fusion.slice._EventStoreAdminDelD;
import com.projectgoth.fusion.slice._EventStoreAdminDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class EventStoreAdminPrxHelper
extends ObjectPrxHelperBase
implements EventStoreAdminPrx {
    @Override
    public EventStoreStats getStats() throws FusionException {
        return this.getStats(null, false);
    }

    @Override
    public EventStoreStats getStats(Map<String, String> __ctx) throws FusionException {
        return this.getStats(__ctx, true);
    }

    private EventStoreStats getStats(Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getStats");
                __delBase = this.__getDelegate(false);
                _EventStoreAdminDel __del = (_EventStoreAdminDel)__delBase;
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

    public static EventStoreAdminPrx checkedCast(ObjectPrx __obj) {
        EventStoreAdminPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (EventStoreAdminPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::EventStoreAdmin")) break block3;
                    EventStoreAdminPrxHelper __h = new EventStoreAdminPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static EventStoreAdminPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        EventStoreAdminPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (EventStoreAdminPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::EventStoreAdmin", __ctx)) break block3;
                    EventStoreAdminPrxHelper __h = new EventStoreAdminPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static EventStoreAdminPrx checkedCast(ObjectPrx __obj, String __facet) {
        EventStoreAdminPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::EventStoreAdmin")) {
                    EventStoreAdminPrxHelper __h = new EventStoreAdminPrxHelper();
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

    public static EventStoreAdminPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        EventStoreAdminPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::EventStoreAdmin", __ctx)) {
                    EventStoreAdminPrxHelper __h = new EventStoreAdminPrxHelper();
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

    public static EventStoreAdminPrx uncheckedCast(ObjectPrx __obj) {
        EventStoreAdminPrx __d = null;
        if (__obj != null) {
            try {
                __d = (EventStoreAdminPrx)__obj;
            }
            catch (ClassCastException ex) {
                EventStoreAdminPrxHelper __h = new EventStoreAdminPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static EventStoreAdminPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        EventStoreAdminPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            EventStoreAdminPrxHelper __h = new EventStoreAdminPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _EventStoreAdminDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _EventStoreAdminDelD();
    }

    public static void __write(BasicStream __os, EventStoreAdminPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static EventStoreAdminPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            EventStoreAdminPrxHelper result = new EventStoreAdminPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

