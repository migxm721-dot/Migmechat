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
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.SessionCacheAdminPrx;
import com.projectgoth.fusion.slice.SessionCacheStats;
import com.projectgoth.fusion.slice._SessionCacheAdminDel;
import com.projectgoth.fusion.slice._SessionCacheAdminDelD;
import com.projectgoth.fusion.slice._SessionCacheAdminDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class SessionCacheAdminPrxHelper
extends ObjectPrxHelperBase
implements SessionCacheAdminPrx {
    @Override
    public SessionCacheStats getStats() throws FusionException {
        return this.getStats(null, false);
    }

    @Override
    public SessionCacheStats getStats(Map<String, String> __ctx) throws FusionException {
        return this.getStats(__ctx, true);
    }

    private SessionCacheStats getStats(Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getStats");
                __delBase = this.__getDelegate(false);
                _SessionCacheAdminDel __del = (_SessionCacheAdminDel)__delBase;
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

    public static SessionCacheAdminPrx checkedCast(ObjectPrx __obj) {
        SessionCacheAdminPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (SessionCacheAdminPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::SessionCacheAdmin")) break block3;
                    SessionCacheAdminPrxHelper __h = new SessionCacheAdminPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static SessionCacheAdminPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        SessionCacheAdminPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (SessionCacheAdminPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::SessionCacheAdmin", __ctx)) break block3;
                    SessionCacheAdminPrxHelper __h = new SessionCacheAdminPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static SessionCacheAdminPrx checkedCast(ObjectPrx __obj, String __facet) {
        SessionCacheAdminPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::SessionCacheAdmin")) {
                    SessionCacheAdminPrxHelper __h = new SessionCacheAdminPrxHelper();
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

    public static SessionCacheAdminPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        SessionCacheAdminPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::SessionCacheAdmin", __ctx)) {
                    SessionCacheAdminPrxHelper __h = new SessionCacheAdminPrxHelper();
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

    public static SessionCacheAdminPrx uncheckedCast(ObjectPrx __obj) {
        SessionCacheAdminPrx __d = null;
        if (__obj != null) {
            try {
                __d = (SessionCacheAdminPrx)__obj;
            }
            catch (ClassCastException ex) {
                SessionCacheAdminPrxHelper __h = new SessionCacheAdminPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static SessionCacheAdminPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        SessionCacheAdminPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            SessionCacheAdminPrxHelper __h = new SessionCacheAdminPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _SessionCacheAdminDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _SessionCacheAdminDelD();
    }

    public static void __write(BasicStream __os, SessionCacheAdminPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static SessionCacheAdminPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            SessionCacheAdminPrxHelper result = new SessionCacheAdminPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

