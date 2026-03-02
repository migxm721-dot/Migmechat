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
import com.projectgoth.fusion.slice.ReputationServiceAdminPrx;
import com.projectgoth.fusion.slice.ReputationServiceStats;
import com.projectgoth.fusion.slice._ReputationServiceAdminDel;
import com.projectgoth.fusion.slice._ReputationServiceAdminDelD;
import com.projectgoth.fusion.slice._ReputationServiceAdminDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class ReputationServiceAdminPrxHelper
extends ObjectPrxHelperBase
implements ReputationServiceAdminPrx {
    @Override
    public ReputationServiceStats getStats() throws FusionException {
        return this.getStats(null, false);
    }

    @Override
    public ReputationServiceStats getStats(Map<String, String> __ctx) throws FusionException {
        return this.getStats(__ctx, true);
    }

    private ReputationServiceStats getStats(Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getStats");
                __delBase = this.__getDelegate(false);
                _ReputationServiceAdminDel __del = (_ReputationServiceAdminDel)__delBase;
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

    public static ReputationServiceAdminPrx checkedCast(ObjectPrx __obj) {
        ReputationServiceAdminPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (ReputationServiceAdminPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::ReputationServiceAdmin")) break block3;
                    ReputationServiceAdminPrxHelper __h = new ReputationServiceAdminPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static ReputationServiceAdminPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        ReputationServiceAdminPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (ReputationServiceAdminPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::ReputationServiceAdmin", __ctx)) break block3;
                    ReputationServiceAdminPrxHelper __h = new ReputationServiceAdminPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static ReputationServiceAdminPrx checkedCast(ObjectPrx __obj, String __facet) {
        ReputationServiceAdminPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::ReputationServiceAdmin")) {
                    ReputationServiceAdminPrxHelper __h = new ReputationServiceAdminPrxHelper();
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

    public static ReputationServiceAdminPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        ReputationServiceAdminPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::ReputationServiceAdmin", __ctx)) {
                    ReputationServiceAdminPrxHelper __h = new ReputationServiceAdminPrxHelper();
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

    public static ReputationServiceAdminPrx uncheckedCast(ObjectPrx __obj) {
        ReputationServiceAdminPrx __d = null;
        if (__obj != null) {
            try {
                __d = (ReputationServiceAdminPrx)__obj;
            }
            catch (ClassCastException ex) {
                ReputationServiceAdminPrxHelper __h = new ReputationServiceAdminPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static ReputationServiceAdminPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        ReputationServiceAdminPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            ReputationServiceAdminPrxHelper __h = new ReputationServiceAdminPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _ReputationServiceAdminDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _ReputationServiceAdminDelD();
    }

    public static void __write(BasicStream __os, ReputationServiceAdminPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static ReputationServiceAdminPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            ReputationServiceAdminPrxHelper result = new ReputationServiceAdminPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

