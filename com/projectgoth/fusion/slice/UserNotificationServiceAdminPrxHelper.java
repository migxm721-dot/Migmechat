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
import com.projectgoth.fusion.slice.UserNotificationServiceAdminPrx;
import com.projectgoth.fusion.slice.UserNotificationServiceStats;
import com.projectgoth.fusion.slice._UserNotificationServiceAdminDel;
import com.projectgoth.fusion.slice._UserNotificationServiceAdminDelD;
import com.projectgoth.fusion.slice._UserNotificationServiceAdminDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class UserNotificationServiceAdminPrxHelper
extends ObjectPrxHelperBase
implements UserNotificationServiceAdminPrx {
    @Override
    public UserNotificationServiceStats getStats() throws FusionException {
        return this.getStats(null, false);
    }

    @Override
    public UserNotificationServiceStats getStats(Map<String, String> __ctx) throws FusionException {
        return this.getStats(__ctx, true);
    }

    private UserNotificationServiceStats getStats(Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getStats");
                __delBase = this.__getDelegate(false);
                _UserNotificationServiceAdminDel __del = (_UserNotificationServiceAdminDel)__delBase;
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

    public static UserNotificationServiceAdminPrx checkedCast(ObjectPrx __obj) {
        UserNotificationServiceAdminPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (UserNotificationServiceAdminPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::UserNotificationServiceAdmin")) break block3;
                    UserNotificationServiceAdminPrxHelper __h = new UserNotificationServiceAdminPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static UserNotificationServiceAdminPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        UserNotificationServiceAdminPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (UserNotificationServiceAdminPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::UserNotificationServiceAdmin", __ctx)) break block3;
                    UserNotificationServiceAdminPrxHelper __h = new UserNotificationServiceAdminPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static UserNotificationServiceAdminPrx checkedCast(ObjectPrx __obj, String __facet) {
        UserNotificationServiceAdminPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::UserNotificationServiceAdmin")) {
                    UserNotificationServiceAdminPrxHelper __h = new UserNotificationServiceAdminPrxHelper();
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

    public static UserNotificationServiceAdminPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        UserNotificationServiceAdminPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::UserNotificationServiceAdmin", __ctx)) {
                    UserNotificationServiceAdminPrxHelper __h = new UserNotificationServiceAdminPrxHelper();
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

    public static UserNotificationServiceAdminPrx uncheckedCast(ObjectPrx __obj) {
        UserNotificationServiceAdminPrx __d = null;
        if (__obj != null) {
            try {
                __d = (UserNotificationServiceAdminPrx)__obj;
            }
            catch (ClassCastException ex) {
                UserNotificationServiceAdminPrxHelper __h = new UserNotificationServiceAdminPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static UserNotificationServiceAdminPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        UserNotificationServiceAdminPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            UserNotificationServiceAdminPrxHelper __h = new UserNotificationServiceAdminPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _UserNotificationServiceAdminDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _UserNotificationServiceAdminDelD();
    }

    public static void __write(BasicStream __os, UserNotificationServiceAdminPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static UserNotificationServiceAdminPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            UserNotificationServiceAdminPrxHelper result = new UserNotificationServiceAdminPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

