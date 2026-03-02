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
import com.projectgoth.fusion.slice.AddingMultipleFriendsUserEventIcePrx;
import com.projectgoth.fusion.slice._AddingMultipleFriendsUserEventIceDelD;
import com.projectgoth.fusion.slice._AddingMultipleFriendsUserEventIceDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class AddingMultipleFriendsUserEventIcePrxHelper
extends ObjectPrxHelperBase
implements AddingMultipleFriendsUserEventIcePrx {
    public static AddingMultipleFriendsUserEventIcePrx checkedCast(ObjectPrx __obj) {
        AddingMultipleFriendsUserEventIcePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (AddingMultipleFriendsUserEventIcePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::AddingMultipleFriendsUserEventIce")) break block3;
                    AddingMultipleFriendsUserEventIcePrxHelper __h = new AddingMultipleFriendsUserEventIcePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static AddingMultipleFriendsUserEventIcePrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        AddingMultipleFriendsUserEventIcePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (AddingMultipleFriendsUserEventIcePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::AddingMultipleFriendsUserEventIce", __ctx)) break block3;
                    AddingMultipleFriendsUserEventIcePrxHelper __h = new AddingMultipleFriendsUserEventIcePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static AddingMultipleFriendsUserEventIcePrx checkedCast(ObjectPrx __obj, String __facet) {
        AddingMultipleFriendsUserEventIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::AddingMultipleFriendsUserEventIce")) {
                    AddingMultipleFriendsUserEventIcePrxHelper __h = new AddingMultipleFriendsUserEventIcePrxHelper();
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

    public static AddingMultipleFriendsUserEventIcePrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        AddingMultipleFriendsUserEventIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::AddingMultipleFriendsUserEventIce", __ctx)) {
                    AddingMultipleFriendsUserEventIcePrxHelper __h = new AddingMultipleFriendsUserEventIcePrxHelper();
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

    public static AddingMultipleFriendsUserEventIcePrx uncheckedCast(ObjectPrx __obj) {
        AddingMultipleFriendsUserEventIcePrx __d = null;
        if (__obj != null) {
            try {
                __d = (AddingMultipleFriendsUserEventIcePrx)__obj;
            }
            catch (ClassCastException ex) {
                AddingMultipleFriendsUserEventIcePrxHelper __h = new AddingMultipleFriendsUserEventIcePrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static AddingMultipleFriendsUserEventIcePrx uncheckedCast(ObjectPrx __obj, String __facet) {
        AddingMultipleFriendsUserEventIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            AddingMultipleFriendsUserEventIcePrxHelper __h = new AddingMultipleFriendsUserEventIcePrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _AddingMultipleFriendsUserEventIceDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _AddingMultipleFriendsUserEventIceDelD();
    }

    public static void __write(BasicStream __os, AddingMultipleFriendsUserEventIcePrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static AddingMultipleFriendsUserEventIcePrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            AddingMultipleFriendsUserEventIcePrxHelper result = new AddingMultipleFriendsUserEventIcePrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

