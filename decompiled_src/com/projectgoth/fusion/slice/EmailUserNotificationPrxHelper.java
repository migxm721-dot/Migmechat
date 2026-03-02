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
import com.projectgoth.fusion.slice.EmailUserNotificationPrx;
import com.projectgoth.fusion.slice._EmailUserNotificationDelD;
import com.projectgoth.fusion.slice._EmailUserNotificationDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class EmailUserNotificationPrxHelper
extends ObjectPrxHelperBase
implements EmailUserNotificationPrx {
    public static EmailUserNotificationPrx checkedCast(ObjectPrx __obj) {
        EmailUserNotificationPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (EmailUserNotificationPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::EmailUserNotification")) break block3;
                    EmailUserNotificationPrxHelper __h = new EmailUserNotificationPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static EmailUserNotificationPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        EmailUserNotificationPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (EmailUserNotificationPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::EmailUserNotification", __ctx)) break block3;
                    EmailUserNotificationPrxHelper __h = new EmailUserNotificationPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static EmailUserNotificationPrx checkedCast(ObjectPrx __obj, String __facet) {
        EmailUserNotificationPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::EmailUserNotification")) {
                    EmailUserNotificationPrxHelper __h = new EmailUserNotificationPrxHelper();
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

    public static EmailUserNotificationPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        EmailUserNotificationPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::EmailUserNotification", __ctx)) {
                    EmailUserNotificationPrxHelper __h = new EmailUserNotificationPrxHelper();
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

    public static EmailUserNotificationPrx uncheckedCast(ObjectPrx __obj) {
        EmailUserNotificationPrx __d = null;
        if (__obj != null) {
            try {
                __d = (EmailUserNotificationPrx)__obj;
            }
            catch (ClassCastException ex) {
                EmailUserNotificationPrxHelper __h = new EmailUserNotificationPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static EmailUserNotificationPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        EmailUserNotificationPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            EmailUserNotificationPrxHelper __h = new EmailUserNotificationPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _EmailUserNotificationDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _EmailUserNotificationDelD();
    }

    public static void __write(BasicStream __os, EmailUserNotificationPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static EmailUserNotificationPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            EmailUserNotificationPrxHelper result = new EmailUserNotificationPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

