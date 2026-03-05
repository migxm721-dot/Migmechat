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
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.SMSSenderPrx;
import com.projectgoth.fusion.slice.SystemSMSDataIce;
import com.projectgoth.fusion.slice._SMSSenderDel;
import com.projectgoth.fusion.slice._SMSSenderDelD;
import com.projectgoth.fusion.slice._SMSSenderDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class SMSSenderPrxHelper
extends ObjectPrxHelperBase
implements SMSSenderPrx {
    @Override
    public void sendSMS(MessageDataIce message, long delay) throws FusionException {
        this.sendSMS(message, delay, null, false);
    }

    @Override
    public void sendSMS(MessageDataIce message, long delay, Map<String, String> __ctx) throws FusionException {
        this.sendSMS(message, delay, __ctx, true);
    }

    private void sendSMS(MessageDataIce message, long delay, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("sendSMS");
                __delBase = this.__getDelegate(false);
                _SMSSenderDel __del = (_SMSSenderDel)__delBase;
                __del.sendSMS(message, delay, __ctx);
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

    @Override
    public void sendSystemSMS(SystemSMSDataIce message, long delay) throws FusionException {
        this.sendSystemSMS(message, delay, null, false);
    }

    @Override
    public void sendSystemSMS(SystemSMSDataIce message, long delay, Map<String, String> __ctx) throws FusionException {
        this.sendSystemSMS(message, delay, __ctx, true);
    }

    private void sendSystemSMS(SystemSMSDataIce message, long delay, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("sendSystemSMS");
                __delBase = this.__getDelegate(false);
                _SMSSenderDel __del = (_SMSSenderDel)__delBase;
                __del.sendSystemSMS(message, delay, __ctx);
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

    public static SMSSenderPrx checkedCast(ObjectPrx __obj) {
        SMSSenderPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (SMSSenderPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::SMSSender")) break block3;
                    SMSSenderPrxHelper __h = new SMSSenderPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static SMSSenderPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        SMSSenderPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (SMSSenderPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::SMSSender", __ctx)) break block3;
                    SMSSenderPrxHelper __h = new SMSSenderPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static SMSSenderPrx checkedCast(ObjectPrx __obj, String __facet) {
        SMSSenderPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::SMSSender")) {
                    SMSSenderPrxHelper __h = new SMSSenderPrxHelper();
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

    public static SMSSenderPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        SMSSenderPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::SMSSender", __ctx)) {
                    SMSSenderPrxHelper __h = new SMSSenderPrxHelper();
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

    public static SMSSenderPrx uncheckedCast(ObjectPrx __obj) {
        SMSSenderPrx __d = null;
        if (__obj != null) {
            try {
                __d = (SMSSenderPrx)__obj;
            }
            catch (ClassCastException ex) {
                SMSSenderPrxHelper __h = new SMSSenderPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static SMSSenderPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        SMSSenderPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            SMSSenderPrxHelper __h = new SMSSenderPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _SMSSenderDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _SMSSenderDelD();
    }

    public static void __write(BasicStream __os, SMSSenderPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static SMSSenderPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            SMSSenderPrxHelper result = new SMSSenderPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

