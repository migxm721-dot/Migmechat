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
import com.projectgoth.fusion.slice.SMSUserNotificationPrx;
import com.projectgoth.fusion.slice._SMSUserNotificationDelD;
import com.projectgoth.fusion.slice._SMSUserNotificationDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class SMSUserNotificationPrxHelper
extends ObjectPrxHelperBase
implements SMSUserNotificationPrx {
    public static SMSUserNotificationPrx checkedCast(ObjectPrx __obj) {
        SMSUserNotificationPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (SMSUserNotificationPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::SMSUserNotification")) break block3;
                    SMSUserNotificationPrxHelper __h = new SMSUserNotificationPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static SMSUserNotificationPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        SMSUserNotificationPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (SMSUserNotificationPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::SMSUserNotification", __ctx)) break block3;
                    SMSUserNotificationPrxHelper __h = new SMSUserNotificationPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static SMSUserNotificationPrx checkedCast(ObjectPrx __obj, String __facet) {
        SMSUserNotificationPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::SMSUserNotification")) {
                    SMSUserNotificationPrxHelper __h = new SMSUserNotificationPrxHelper();
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

    public static SMSUserNotificationPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        SMSUserNotificationPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::SMSUserNotification", __ctx)) {
                    SMSUserNotificationPrxHelper __h = new SMSUserNotificationPrxHelper();
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

    public static SMSUserNotificationPrx uncheckedCast(ObjectPrx __obj) {
        SMSUserNotificationPrx __d = null;
        if (__obj != null) {
            try {
                __d = (SMSUserNotificationPrx)__obj;
            }
            catch (ClassCastException ex) {
                SMSUserNotificationPrxHelper __h = new SMSUserNotificationPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static SMSUserNotificationPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        SMSUserNotificationPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            SMSUserNotificationPrxHelper __h = new SMSUserNotificationPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _SMSUserNotificationDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _SMSUserNotificationDelD();
    }

    public static void __write(BasicStream __os, SMSUserNotificationPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static SMSUserNotificationPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            SMSUserNotificationPrxHelper result = new SMSUserNotificationPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

