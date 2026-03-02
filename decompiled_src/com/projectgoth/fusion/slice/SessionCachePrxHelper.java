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
import com.projectgoth.fusion.slice.SessionCachePrx;
import com.projectgoth.fusion.slice.SessionIce;
import com.projectgoth.fusion.slice.SessionMetricsIce;
import com.projectgoth.fusion.slice._SessionCacheDel;
import com.projectgoth.fusion.slice._SessionCacheDelD;
import com.projectgoth.fusion.slice._SessionCacheDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class SessionCachePrxHelper
extends ObjectPrxHelperBase
implements SessionCachePrx {
    @Override
    public void logSession(SessionIce session, SessionMetricsIce sessionMetrics) {
        this.logSession(session, sessionMetrics, null, false);
    }

    @Override
    public void logSession(SessionIce session, SessionMetricsIce sessionMetrics, Map<String, String> __ctx) {
        this.logSession(session, sessionMetrics, __ctx, true);
    }

    private void logSession(SessionIce session, SessionMetricsIce sessionMetrics, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _SessionCacheDel __del = (_SessionCacheDel)__delBase;
                __del.logSession(session, sessionMetrics, __ctx);
                return;
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

    public static SessionCachePrx checkedCast(ObjectPrx __obj) {
        SessionCachePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (SessionCachePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::SessionCache")) break block3;
                    SessionCachePrxHelper __h = new SessionCachePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static SessionCachePrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        SessionCachePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (SessionCachePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::SessionCache", __ctx)) break block3;
                    SessionCachePrxHelper __h = new SessionCachePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static SessionCachePrx checkedCast(ObjectPrx __obj, String __facet) {
        SessionCachePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::SessionCache")) {
                    SessionCachePrxHelper __h = new SessionCachePrxHelper();
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

    public static SessionCachePrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        SessionCachePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::SessionCache", __ctx)) {
                    SessionCachePrxHelper __h = new SessionCachePrxHelper();
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

    public static SessionCachePrx uncheckedCast(ObjectPrx __obj) {
        SessionCachePrx __d = null;
        if (__obj != null) {
            try {
                __d = (SessionCachePrx)__obj;
            }
            catch (ClassCastException ex) {
                SessionCachePrxHelper __h = new SessionCachePrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static SessionCachePrx uncheckedCast(ObjectPrx __obj, String __facet) {
        SessionCachePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            SessionCachePrxHelper __h = new SessionCachePrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _SessionCacheDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _SessionCacheDelD();
    }

    public static void __write(BasicStream __os, SessionCachePrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static SessionCachePrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            SessionCachePrxHelper result = new SessionCachePrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

