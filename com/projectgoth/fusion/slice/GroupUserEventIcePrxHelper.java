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
import com.projectgoth.fusion.slice.GroupUserEventIcePrx;
import com.projectgoth.fusion.slice._GroupUserEventIceDelD;
import com.projectgoth.fusion.slice._GroupUserEventIceDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class GroupUserEventIcePrxHelper
extends ObjectPrxHelperBase
implements GroupUserEventIcePrx {
    public static GroupUserEventIcePrx checkedCast(ObjectPrx __obj) {
        GroupUserEventIcePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (GroupUserEventIcePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::GroupUserEventIce")) break block3;
                    GroupUserEventIcePrxHelper __h = new GroupUserEventIcePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static GroupUserEventIcePrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        GroupUserEventIcePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (GroupUserEventIcePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::GroupUserEventIce", __ctx)) break block3;
                    GroupUserEventIcePrxHelper __h = new GroupUserEventIcePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static GroupUserEventIcePrx checkedCast(ObjectPrx __obj, String __facet) {
        GroupUserEventIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::GroupUserEventIce")) {
                    GroupUserEventIcePrxHelper __h = new GroupUserEventIcePrxHelper();
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

    public static GroupUserEventIcePrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        GroupUserEventIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::GroupUserEventIce", __ctx)) {
                    GroupUserEventIcePrxHelper __h = new GroupUserEventIcePrxHelper();
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

    public static GroupUserEventIcePrx uncheckedCast(ObjectPrx __obj) {
        GroupUserEventIcePrx __d = null;
        if (__obj != null) {
            try {
                __d = (GroupUserEventIcePrx)__obj;
            }
            catch (ClassCastException ex) {
                GroupUserEventIcePrxHelper __h = new GroupUserEventIcePrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static GroupUserEventIcePrx uncheckedCast(ObjectPrx __obj, String __facet) {
        GroupUserEventIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            GroupUserEventIcePrxHelper __h = new GroupUserEventIcePrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _GroupUserEventIceDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _GroupUserEventIceDelD();
    }

    public static void __write(BasicStream __os, GroupUserEventIcePrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static GroupUserEventIcePrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            GroupUserEventIcePrxHelper result = new GroupUserEventIcePrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

