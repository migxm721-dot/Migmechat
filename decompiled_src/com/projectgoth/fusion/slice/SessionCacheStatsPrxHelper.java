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
import com.projectgoth.fusion.slice.SessionCacheStatsPrx;
import com.projectgoth.fusion.slice._SessionCacheStatsDelD;
import com.projectgoth.fusion.slice._SessionCacheStatsDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class SessionCacheStatsPrxHelper
extends ObjectPrxHelperBase
implements SessionCacheStatsPrx {
    public static SessionCacheStatsPrx checkedCast(ObjectPrx __obj) {
        SessionCacheStatsPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (SessionCacheStatsPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::SessionCacheStats")) break block3;
                    SessionCacheStatsPrxHelper __h = new SessionCacheStatsPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static SessionCacheStatsPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        SessionCacheStatsPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (SessionCacheStatsPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::SessionCacheStats", __ctx)) break block3;
                    SessionCacheStatsPrxHelper __h = new SessionCacheStatsPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static SessionCacheStatsPrx checkedCast(ObjectPrx __obj, String __facet) {
        SessionCacheStatsPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::SessionCacheStats")) {
                    SessionCacheStatsPrxHelper __h = new SessionCacheStatsPrxHelper();
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

    public static SessionCacheStatsPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        SessionCacheStatsPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::SessionCacheStats", __ctx)) {
                    SessionCacheStatsPrxHelper __h = new SessionCacheStatsPrxHelper();
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

    public static SessionCacheStatsPrx uncheckedCast(ObjectPrx __obj) {
        SessionCacheStatsPrx __d = null;
        if (__obj != null) {
            try {
                __d = (SessionCacheStatsPrx)__obj;
            }
            catch (ClassCastException ex) {
                SessionCacheStatsPrxHelper __h = new SessionCacheStatsPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static SessionCacheStatsPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        SessionCacheStatsPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            SessionCacheStatsPrxHelper __h = new SessionCacheStatsPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _SessionCacheStatsDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _SessionCacheStatsDelD();
    }

    public static void __write(BasicStream __os, SessionCacheStatsPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static SessionCacheStatsPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            SessionCacheStatsPrxHelper result = new SessionCacheStatsPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

