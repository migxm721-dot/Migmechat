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
import com.projectgoth.fusion.slice.EventSystemAdminPrx;
import com.projectgoth.fusion.slice.EventSystemStats;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice._EventSystemAdminDel;
import com.projectgoth.fusion.slice._EventSystemAdminDelD;
import com.projectgoth.fusion.slice._EventSystemAdminDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class EventSystemAdminPrxHelper
extends ObjectPrxHelperBase
implements EventSystemAdminPrx {
    @Override
    public EventSystemStats getStats() throws FusionException {
        return this.getStats(null, false);
    }

    @Override
    public EventSystemStats getStats(Map<String, String> __ctx) throws FusionException {
        return this.getStats(__ctx, true);
    }

    private EventSystemStats getStats(Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getStats");
                __delBase = this.__getDelegate(false);
                _EventSystemAdminDel __del = (_EventSystemAdminDel)__delBase;
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

    public static EventSystemAdminPrx checkedCast(ObjectPrx __obj) {
        EventSystemAdminPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (EventSystemAdminPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::EventSystemAdmin")) break block3;
                    EventSystemAdminPrxHelper __h = new EventSystemAdminPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static EventSystemAdminPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        EventSystemAdminPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (EventSystemAdminPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::EventSystemAdmin", __ctx)) break block3;
                    EventSystemAdminPrxHelper __h = new EventSystemAdminPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static EventSystemAdminPrx checkedCast(ObjectPrx __obj, String __facet) {
        EventSystemAdminPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::EventSystemAdmin")) {
                    EventSystemAdminPrxHelper __h = new EventSystemAdminPrxHelper();
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

    public static EventSystemAdminPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        EventSystemAdminPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::EventSystemAdmin", __ctx)) {
                    EventSystemAdminPrxHelper __h = new EventSystemAdminPrxHelper();
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

    public static EventSystemAdminPrx uncheckedCast(ObjectPrx __obj) {
        EventSystemAdminPrx __d = null;
        if (__obj != null) {
            try {
                __d = (EventSystemAdminPrx)__obj;
            }
            catch (ClassCastException ex) {
                EventSystemAdminPrxHelper __h = new EventSystemAdminPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static EventSystemAdminPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        EventSystemAdminPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            EventSystemAdminPrxHelper __h = new EventSystemAdminPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _EventSystemAdminDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _EventSystemAdminDelD();
    }

    public static void __write(BasicStream __os, EventSystemAdminPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static EventSystemAdminPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            EventSystemAdminPrxHelper result = new EventSystemAdminPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

