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
import com.projectgoth.fusion.slice.BotHunterAdminPrx;
import com.projectgoth.fusion.slice.BotHunterStats;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice._BotHunterAdminDel;
import com.projectgoth.fusion.slice._BotHunterAdminDelD;
import com.projectgoth.fusion.slice._BotHunterAdminDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class BotHunterAdminPrxHelper
extends ObjectPrxHelperBase
implements BotHunterAdminPrx {
    @Override
    public BotHunterStats getStats() throws FusionException {
        return this.getStats(null, false);
    }

    @Override
    public BotHunterStats getStats(Map<String, String> __ctx) throws FusionException {
        return this.getStats(__ctx, true);
    }

    private BotHunterStats getStats(Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getStats");
                __delBase = this.__getDelegate(false);
                _BotHunterAdminDel __del = (_BotHunterAdminDel)__delBase;
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

    public static BotHunterAdminPrx checkedCast(ObjectPrx __obj) {
        BotHunterAdminPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (BotHunterAdminPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::BotHunterAdmin")) break block3;
                    BotHunterAdminPrxHelper __h = new BotHunterAdminPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static BotHunterAdminPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        BotHunterAdminPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (BotHunterAdminPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::BotHunterAdmin", __ctx)) break block3;
                    BotHunterAdminPrxHelper __h = new BotHunterAdminPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static BotHunterAdminPrx checkedCast(ObjectPrx __obj, String __facet) {
        BotHunterAdminPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::BotHunterAdmin")) {
                    BotHunterAdminPrxHelper __h = new BotHunterAdminPrxHelper();
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

    public static BotHunterAdminPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        BotHunterAdminPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::BotHunterAdmin", __ctx)) {
                    BotHunterAdminPrxHelper __h = new BotHunterAdminPrxHelper();
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

    public static BotHunterAdminPrx uncheckedCast(ObjectPrx __obj) {
        BotHunterAdminPrx __d = null;
        if (__obj != null) {
            try {
                __d = (BotHunterAdminPrx)__obj;
            }
            catch (ClassCastException ex) {
                BotHunterAdminPrxHelper __h = new BotHunterAdminPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static BotHunterAdminPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        BotHunterAdminPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            BotHunterAdminPrxHelper __h = new BotHunterAdminPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _BotHunterAdminDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _BotHunterAdminDelD();
    }

    public static void __write(BasicStream __os, BotHunterAdminPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static BotHunterAdminPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            BotHunterAdminPrxHelper result = new BotHunterAdminPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

