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
import com.projectgoth.fusion.slice.CallDataIce;
import com.projectgoth.fusion.slice.CallMakerPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice._CallMakerDel;
import com.projectgoth.fusion.slice._CallMakerDelD;
import com.projectgoth.fusion.slice._CallMakerDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class CallMakerPrxHelper
extends ObjectPrxHelperBase
implements CallMakerPrx {
    @Override
    public CallDataIce requestCallback(CallDataIce call, int maxDuration, int retries) throws FusionException {
        return this.requestCallback(call, maxDuration, retries, null, false);
    }

    @Override
    public CallDataIce requestCallback(CallDataIce call, int maxDuration, int retries, Map<String, String> __ctx) throws FusionException {
        return this.requestCallback(call, maxDuration, retries, __ctx, true);
    }

    private CallDataIce requestCallback(CallDataIce call, int maxDuration, int retries, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("requestCallback");
                __delBase = this.__getDelegate(false);
                _CallMakerDel __del = (_CallMakerDel)__delBase;
                return __del.requestCallback(call, maxDuration, retries, __ctx);
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

    public static CallMakerPrx checkedCast(ObjectPrx __obj) {
        CallMakerPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (CallMakerPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::CallMaker")) break block3;
                    CallMakerPrxHelper __h = new CallMakerPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static CallMakerPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        CallMakerPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (CallMakerPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::CallMaker", __ctx)) break block3;
                    CallMakerPrxHelper __h = new CallMakerPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static CallMakerPrx checkedCast(ObjectPrx __obj, String __facet) {
        CallMakerPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::CallMaker")) {
                    CallMakerPrxHelper __h = new CallMakerPrxHelper();
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

    public static CallMakerPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        CallMakerPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::CallMaker", __ctx)) {
                    CallMakerPrxHelper __h = new CallMakerPrxHelper();
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

    public static CallMakerPrx uncheckedCast(ObjectPrx __obj) {
        CallMakerPrx __d = null;
        if (__obj != null) {
            try {
                __d = (CallMakerPrx)__obj;
            }
            catch (ClassCastException ex) {
                CallMakerPrxHelper __h = new CallMakerPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static CallMakerPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        CallMakerPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            CallMakerPrxHelper __h = new CallMakerPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _CallMakerDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _CallMakerDelD();
    }

    public static void __write(BasicStream __os, CallMakerPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static CallMakerPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            CallMakerPrxHelper result = new CallMakerPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

