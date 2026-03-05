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
import com.projectgoth.fusion.slice.UserWallPostUserEventIcePrx;
import com.projectgoth.fusion.slice._UserWallPostUserEventIceDelD;
import com.projectgoth.fusion.slice._UserWallPostUserEventIceDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class UserWallPostUserEventIcePrxHelper
extends ObjectPrxHelperBase
implements UserWallPostUserEventIcePrx {
    public static UserWallPostUserEventIcePrx checkedCast(ObjectPrx __obj) {
        UserWallPostUserEventIcePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (UserWallPostUserEventIcePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::UserWallPostUserEventIce")) break block3;
                    UserWallPostUserEventIcePrxHelper __h = new UserWallPostUserEventIcePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static UserWallPostUserEventIcePrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        UserWallPostUserEventIcePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (UserWallPostUserEventIcePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::UserWallPostUserEventIce", __ctx)) break block3;
                    UserWallPostUserEventIcePrxHelper __h = new UserWallPostUserEventIcePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static UserWallPostUserEventIcePrx checkedCast(ObjectPrx __obj, String __facet) {
        UserWallPostUserEventIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::UserWallPostUserEventIce")) {
                    UserWallPostUserEventIcePrxHelper __h = new UserWallPostUserEventIcePrxHelper();
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

    public static UserWallPostUserEventIcePrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        UserWallPostUserEventIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::UserWallPostUserEventIce", __ctx)) {
                    UserWallPostUserEventIcePrxHelper __h = new UserWallPostUserEventIcePrxHelper();
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

    public static UserWallPostUserEventIcePrx uncheckedCast(ObjectPrx __obj) {
        UserWallPostUserEventIcePrx __d = null;
        if (__obj != null) {
            try {
                __d = (UserWallPostUserEventIcePrx)__obj;
            }
            catch (ClassCastException ex) {
                UserWallPostUserEventIcePrxHelper __h = new UserWallPostUserEventIcePrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static UserWallPostUserEventIcePrx uncheckedCast(ObjectPrx __obj, String __facet) {
        UserWallPostUserEventIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            UserWallPostUserEventIcePrxHelper __h = new UserWallPostUserEventIcePrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _UserWallPostUserEventIceDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _UserWallPostUserEventIceDelD();
    }

    public static void __write(BasicStream __os, UserWallPostUserEventIcePrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static UserWallPostUserEventIcePrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            UserWallPostUserEventIcePrxHelper result = new UserWallPostUserEventIcePrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

