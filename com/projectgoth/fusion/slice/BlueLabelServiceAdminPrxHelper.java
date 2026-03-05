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
import com.projectgoth.fusion.slice.BlueLabelServiceAdminPrx;
import com.projectgoth.fusion.slice.BlueLabelServiceStats;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice._BlueLabelServiceAdminDel;
import com.projectgoth.fusion.slice._BlueLabelServiceAdminDelD;
import com.projectgoth.fusion.slice._BlueLabelServiceAdminDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class BlueLabelServiceAdminPrxHelper
extends ObjectPrxHelperBase
implements BlueLabelServiceAdminPrx {
    @Override
    public BlueLabelServiceStats getStats() throws FusionException {
        return this.getStats(null, false);
    }

    @Override
    public BlueLabelServiceStats getStats(Map<String, String> __ctx) throws FusionException {
        return this.getStats(__ctx, true);
    }

    private BlueLabelServiceStats getStats(Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getStats");
                __delBase = this.__getDelegate(false);
                _BlueLabelServiceAdminDel __del = (_BlueLabelServiceAdminDel)__delBase;
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

    public static BlueLabelServiceAdminPrx checkedCast(ObjectPrx __obj) {
        BlueLabelServiceAdminPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (BlueLabelServiceAdminPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::BlueLabelServiceAdmin")) break block3;
                    BlueLabelServiceAdminPrxHelper __h = new BlueLabelServiceAdminPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static BlueLabelServiceAdminPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        BlueLabelServiceAdminPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (BlueLabelServiceAdminPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::BlueLabelServiceAdmin", __ctx)) break block3;
                    BlueLabelServiceAdminPrxHelper __h = new BlueLabelServiceAdminPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static BlueLabelServiceAdminPrx checkedCast(ObjectPrx __obj, String __facet) {
        BlueLabelServiceAdminPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::BlueLabelServiceAdmin")) {
                    BlueLabelServiceAdminPrxHelper __h = new BlueLabelServiceAdminPrxHelper();
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

    public static BlueLabelServiceAdminPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        BlueLabelServiceAdminPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::BlueLabelServiceAdmin", __ctx)) {
                    BlueLabelServiceAdminPrxHelper __h = new BlueLabelServiceAdminPrxHelper();
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

    public static BlueLabelServiceAdminPrx uncheckedCast(ObjectPrx __obj) {
        BlueLabelServiceAdminPrx __d = null;
        if (__obj != null) {
            try {
                __d = (BlueLabelServiceAdminPrx)__obj;
            }
            catch (ClassCastException ex) {
                BlueLabelServiceAdminPrxHelper __h = new BlueLabelServiceAdminPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static BlueLabelServiceAdminPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        BlueLabelServiceAdminPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            BlueLabelServiceAdminPrxHelper __h = new BlueLabelServiceAdminPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _BlueLabelServiceAdminDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _BlueLabelServiceAdminDelD();
    }

    public static void __write(BasicStream __os, BlueLabelServiceAdminPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static BlueLabelServiceAdminPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            BlueLabelServiceAdminPrxHelper result = new BlueLabelServiceAdminPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

