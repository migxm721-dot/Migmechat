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
import com.projectgoth.fusion.slice.EmailAlertPrx;
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.slice._EmailAlertDel;
import com.projectgoth.fusion.slice._EmailAlertDelD;
import com.projectgoth.fusion.slice._EmailAlertDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class EmailAlertPrxHelper
extends ObjectPrxHelperBase
implements EmailAlertPrx {
    @Override
    public void requestUnreadEmailCount(String username, String password, UserPrx userProxy) {
        this.requestUnreadEmailCount(username, password, userProxy, null, false);
    }

    @Override
    public void requestUnreadEmailCount(String username, String password, UserPrx userProxy, Map<String, String> __ctx) {
        this.requestUnreadEmailCount(username, password, userProxy, __ctx, true);
    }

    private void requestUnreadEmailCount(String username, String password, UserPrx userProxy, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _EmailAlertDel __del = (_EmailAlertDel)__delBase;
                __del.requestUnreadEmailCount(username, password, userProxy, __ctx);
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

    public static EmailAlertPrx checkedCast(ObjectPrx __obj) {
        EmailAlertPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (EmailAlertPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::EmailAlert")) break block3;
                    EmailAlertPrxHelper __h = new EmailAlertPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static EmailAlertPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        EmailAlertPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (EmailAlertPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::EmailAlert", __ctx)) break block3;
                    EmailAlertPrxHelper __h = new EmailAlertPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static EmailAlertPrx checkedCast(ObjectPrx __obj, String __facet) {
        EmailAlertPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::EmailAlert")) {
                    EmailAlertPrxHelper __h = new EmailAlertPrxHelper();
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

    public static EmailAlertPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        EmailAlertPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::EmailAlert", __ctx)) {
                    EmailAlertPrxHelper __h = new EmailAlertPrxHelper();
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

    public static EmailAlertPrx uncheckedCast(ObjectPrx __obj) {
        EmailAlertPrx __d = null;
        if (__obj != null) {
            try {
                __d = (EmailAlertPrx)__obj;
            }
            catch (ClassCastException ex) {
                EmailAlertPrxHelper __h = new EmailAlertPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static EmailAlertPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        EmailAlertPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            EmailAlertPrxHelper __h = new EmailAlertPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _EmailAlertDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _EmailAlertDelD();
    }

    public static void __write(BasicStream __os, EmailAlertPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static EmailAlertPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            EmailAlertPrxHelper result = new EmailAlertPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

