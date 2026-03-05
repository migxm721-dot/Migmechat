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
import com.projectgoth.fusion.slice.AddingFriendUserEventIcePrx;
import com.projectgoth.fusion.slice._AddingFriendUserEventIceDelD;
import com.projectgoth.fusion.slice._AddingFriendUserEventIceDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class AddingFriendUserEventIcePrxHelper
extends ObjectPrxHelperBase
implements AddingFriendUserEventIcePrx {
    public static AddingFriendUserEventIcePrx checkedCast(ObjectPrx __obj) {
        AddingFriendUserEventIcePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (AddingFriendUserEventIcePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::AddingFriendUserEventIce")) break block3;
                    AddingFriendUserEventIcePrxHelper __h = new AddingFriendUserEventIcePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static AddingFriendUserEventIcePrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        AddingFriendUserEventIcePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (AddingFriendUserEventIcePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::AddingFriendUserEventIce", __ctx)) break block3;
                    AddingFriendUserEventIcePrxHelper __h = new AddingFriendUserEventIcePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static AddingFriendUserEventIcePrx checkedCast(ObjectPrx __obj, String __facet) {
        AddingFriendUserEventIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::AddingFriendUserEventIce")) {
                    AddingFriendUserEventIcePrxHelper __h = new AddingFriendUserEventIcePrxHelper();
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

    public static AddingFriendUserEventIcePrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        AddingFriendUserEventIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::AddingFriendUserEventIce", __ctx)) {
                    AddingFriendUserEventIcePrxHelper __h = new AddingFriendUserEventIcePrxHelper();
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

    public static AddingFriendUserEventIcePrx uncheckedCast(ObjectPrx __obj) {
        AddingFriendUserEventIcePrx __d = null;
        if (__obj != null) {
            try {
                __d = (AddingFriendUserEventIcePrx)__obj;
            }
            catch (ClassCastException ex) {
                AddingFriendUserEventIcePrxHelper __h = new AddingFriendUserEventIcePrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static AddingFriendUserEventIcePrx uncheckedCast(ObjectPrx __obj, String __facet) {
        AddingFriendUserEventIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            AddingFriendUserEventIcePrxHelper __h = new AddingFriendUserEventIcePrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _AddingFriendUserEventIceDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _AddingFriendUserEventIceDelD();
    }

    public static void __write(BasicStream __os, AddingFriendUserEventIcePrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static AddingFriendUserEventIcePrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            AddingFriendUserEventIcePrxHelper result = new AddingFriendUserEventIcePrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

