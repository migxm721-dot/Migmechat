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
import com.projectgoth.fusion.slice.BotHunterPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.SuspectGroupIce;
import com.projectgoth.fusion.slice._BotHunterDel;
import com.projectgoth.fusion.slice._BotHunterDelD;
import com.projectgoth.fusion.slice._BotHunterDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class BotHunterPrxHelper
extends ObjectPrxHelperBase
implements BotHunterPrx {
    @Override
    public SuspectGroupIce[] getLatestSuspects() throws FusionException {
        return this.getLatestSuspects(null, false);
    }

    @Override
    public SuspectGroupIce[] getLatestSuspects(Map<String, String> __ctx) throws FusionException {
        return this.getLatestSuspects(__ctx, true);
    }

    private SuspectGroupIce[] getLatestSuspects(Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getLatestSuspects");
                __delBase = this.__getDelegate(false);
                _BotHunterDel __del = (_BotHunterDel)__delBase;
                return __del.getLatestSuspects(__ctx);
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

    public static BotHunterPrx checkedCast(ObjectPrx __obj) {
        BotHunterPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (BotHunterPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::BotHunter")) break block3;
                    BotHunterPrxHelper __h = new BotHunterPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static BotHunterPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        BotHunterPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (BotHunterPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::BotHunter", __ctx)) break block3;
                    BotHunterPrxHelper __h = new BotHunterPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static BotHunterPrx checkedCast(ObjectPrx __obj, String __facet) {
        BotHunterPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::BotHunter")) {
                    BotHunterPrxHelper __h = new BotHunterPrxHelper();
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

    public static BotHunterPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        BotHunterPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::BotHunter", __ctx)) {
                    BotHunterPrxHelper __h = new BotHunterPrxHelper();
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

    public static BotHunterPrx uncheckedCast(ObjectPrx __obj) {
        BotHunterPrx __d = null;
        if (__obj != null) {
            try {
                __d = (BotHunterPrx)__obj;
            }
            catch (ClassCastException ex) {
                BotHunterPrxHelper __h = new BotHunterPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static BotHunterPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        BotHunterPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            BotHunterPrxHelper __h = new BotHunterPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _BotHunterDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _BotHunterDelD();
    }

    public static void __write(BasicStream __os, BotHunterPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static BotHunterPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            BotHunterPrxHelper result = new BotHunterPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

