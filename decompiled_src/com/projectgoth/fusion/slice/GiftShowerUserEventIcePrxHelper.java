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
import com.projectgoth.fusion.slice.GiftShowerUserEventIcePrx;
import com.projectgoth.fusion.slice._GiftShowerUserEventIceDelD;
import com.projectgoth.fusion.slice._GiftShowerUserEventIceDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class GiftShowerUserEventIcePrxHelper
extends ObjectPrxHelperBase
implements GiftShowerUserEventIcePrx {
    public static GiftShowerUserEventIcePrx checkedCast(ObjectPrx __obj) {
        GiftShowerUserEventIcePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (GiftShowerUserEventIcePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::GiftShowerUserEventIce")) break block3;
                    GiftShowerUserEventIcePrxHelper __h = new GiftShowerUserEventIcePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static GiftShowerUserEventIcePrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        GiftShowerUserEventIcePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (GiftShowerUserEventIcePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::GiftShowerUserEventIce", __ctx)) break block3;
                    GiftShowerUserEventIcePrxHelper __h = new GiftShowerUserEventIcePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static GiftShowerUserEventIcePrx checkedCast(ObjectPrx __obj, String __facet) {
        GiftShowerUserEventIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::GiftShowerUserEventIce")) {
                    GiftShowerUserEventIcePrxHelper __h = new GiftShowerUserEventIcePrxHelper();
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

    public static GiftShowerUserEventIcePrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        GiftShowerUserEventIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::GiftShowerUserEventIce", __ctx)) {
                    GiftShowerUserEventIcePrxHelper __h = new GiftShowerUserEventIcePrxHelper();
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

    public static GiftShowerUserEventIcePrx uncheckedCast(ObjectPrx __obj) {
        GiftShowerUserEventIcePrx __d = null;
        if (__obj != null) {
            try {
                __d = (GiftShowerUserEventIcePrx)__obj;
            }
            catch (ClassCastException ex) {
                GiftShowerUserEventIcePrxHelper __h = new GiftShowerUserEventIcePrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static GiftShowerUserEventIcePrx uncheckedCast(ObjectPrx __obj, String __facet) {
        GiftShowerUserEventIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            GiftShowerUserEventIcePrxHelper __h = new GiftShowerUserEventIcePrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _GiftShowerUserEventIceDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _GiftShowerUserEventIceDelD();
    }

    public static void __write(BasicStream __os, GiftShowerUserEventIcePrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static GiftShowerUserEventIcePrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            GiftShowerUserEventIcePrxHelper result = new GiftShowerUserEventIcePrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

