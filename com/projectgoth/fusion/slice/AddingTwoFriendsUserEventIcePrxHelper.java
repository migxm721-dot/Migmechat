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
import com.projectgoth.fusion.slice.AddingTwoFriendsUserEventIcePrx;
import com.projectgoth.fusion.slice._AddingTwoFriendsUserEventIceDelD;
import com.projectgoth.fusion.slice._AddingTwoFriendsUserEventIceDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class AddingTwoFriendsUserEventIcePrxHelper
extends ObjectPrxHelperBase
implements AddingTwoFriendsUserEventIcePrx {
    public static AddingTwoFriendsUserEventIcePrx checkedCast(ObjectPrx __obj) {
        AddingTwoFriendsUserEventIcePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (AddingTwoFriendsUserEventIcePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::AddingTwoFriendsUserEventIce")) break block3;
                    AddingTwoFriendsUserEventIcePrxHelper __h = new AddingTwoFriendsUserEventIcePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static AddingTwoFriendsUserEventIcePrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        AddingTwoFriendsUserEventIcePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (AddingTwoFriendsUserEventIcePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::AddingTwoFriendsUserEventIce", __ctx)) break block3;
                    AddingTwoFriendsUserEventIcePrxHelper __h = new AddingTwoFriendsUserEventIcePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static AddingTwoFriendsUserEventIcePrx checkedCast(ObjectPrx __obj, String __facet) {
        AddingTwoFriendsUserEventIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::AddingTwoFriendsUserEventIce")) {
                    AddingTwoFriendsUserEventIcePrxHelper __h = new AddingTwoFriendsUserEventIcePrxHelper();
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

    public static AddingTwoFriendsUserEventIcePrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        AddingTwoFriendsUserEventIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::AddingTwoFriendsUserEventIce", __ctx)) {
                    AddingTwoFriendsUserEventIcePrxHelper __h = new AddingTwoFriendsUserEventIcePrxHelper();
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

    public static AddingTwoFriendsUserEventIcePrx uncheckedCast(ObjectPrx __obj) {
        AddingTwoFriendsUserEventIcePrx __d = null;
        if (__obj != null) {
            try {
                __d = (AddingTwoFriendsUserEventIcePrx)__obj;
            }
            catch (ClassCastException ex) {
                AddingTwoFriendsUserEventIcePrxHelper __h = new AddingTwoFriendsUserEventIcePrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static AddingTwoFriendsUserEventIcePrx uncheckedCast(ObjectPrx __obj, String __facet) {
        AddingTwoFriendsUserEventIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            AddingTwoFriendsUserEventIcePrxHelper __h = new AddingTwoFriendsUserEventIcePrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _AddingTwoFriendsUserEventIceDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _AddingTwoFriendsUserEventIceDelD();
    }

    public static void __write(BasicStream __os, AddingTwoFriendsUserEventIcePrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static AddingTwoFriendsUserEventIcePrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            AddingTwoFriendsUserEventIcePrxHelper result = new AddingTwoFriendsUserEventIcePrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

