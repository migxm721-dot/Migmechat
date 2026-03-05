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
import com.projectgoth.fusion.slice.UserNotificationPrx;
import com.projectgoth.fusion.slice._UserNotificationDelD;
import com.projectgoth.fusion.slice._UserNotificationDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class UserNotificationPrxHelper
extends ObjectPrxHelperBase
implements UserNotificationPrx {
    public static UserNotificationPrx checkedCast(ObjectPrx __obj) {
        UserNotificationPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (UserNotificationPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::UserNotification")) break block3;
                    UserNotificationPrxHelper __h = new UserNotificationPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static UserNotificationPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        UserNotificationPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (UserNotificationPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::UserNotification", __ctx)) break block3;
                    UserNotificationPrxHelper __h = new UserNotificationPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static UserNotificationPrx checkedCast(ObjectPrx __obj, String __facet) {
        UserNotificationPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::UserNotification")) {
                    UserNotificationPrxHelper __h = new UserNotificationPrxHelper();
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

    public static UserNotificationPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        UserNotificationPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::UserNotification", __ctx)) {
                    UserNotificationPrxHelper __h = new UserNotificationPrxHelper();
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

    public static UserNotificationPrx uncheckedCast(ObjectPrx __obj) {
        UserNotificationPrx __d = null;
        if (__obj != null) {
            try {
                __d = (UserNotificationPrx)__obj;
            }
            catch (ClassCastException ex) {
                UserNotificationPrxHelper __h = new UserNotificationPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static UserNotificationPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        UserNotificationPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            UserNotificationPrxHelper __h = new UserNotificationPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _UserNotificationDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _UserNotificationDelD();
    }

    public static void __write(BasicStream __os, UserNotificationPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static UserNotificationPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            UserNotificationPrxHelper result = new UserNotificationPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

