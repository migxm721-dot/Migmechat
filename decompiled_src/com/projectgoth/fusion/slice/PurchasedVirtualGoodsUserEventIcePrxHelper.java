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
import com.projectgoth.fusion.slice.PurchasedVirtualGoodsUserEventIcePrx;
import com.projectgoth.fusion.slice._PurchasedVirtualGoodsUserEventIceDelD;
import com.projectgoth.fusion.slice._PurchasedVirtualGoodsUserEventIceDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class PurchasedVirtualGoodsUserEventIcePrxHelper
extends ObjectPrxHelperBase
implements PurchasedVirtualGoodsUserEventIcePrx {
    public static PurchasedVirtualGoodsUserEventIcePrx checkedCast(ObjectPrx __obj) {
        PurchasedVirtualGoodsUserEventIcePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (PurchasedVirtualGoodsUserEventIcePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::PurchasedVirtualGoodsUserEventIce")) break block3;
                    PurchasedVirtualGoodsUserEventIcePrxHelper __h = new PurchasedVirtualGoodsUserEventIcePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static PurchasedVirtualGoodsUserEventIcePrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        PurchasedVirtualGoodsUserEventIcePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (PurchasedVirtualGoodsUserEventIcePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::PurchasedVirtualGoodsUserEventIce", __ctx)) break block3;
                    PurchasedVirtualGoodsUserEventIcePrxHelper __h = new PurchasedVirtualGoodsUserEventIcePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static PurchasedVirtualGoodsUserEventIcePrx checkedCast(ObjectPrx __obj, String __facet) {
        PurchasedVirtualGoodsUserEventIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::PurchasedVirtualGoodsUserEventIce")) {
                    PurchasedVirtualGoodsUserEventIcePrxHelper __h = new PurchasedVirtualGoodsUserEventIcePrxHelper();
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

    public static PurchasedVirtualGoodsUserEventIcePrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        PurchasedVirtualGoodsUserEventIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::PurchasedVirtualGoodsUserEventIce", __ctx)) {
                    PurchasedVirtualGoodsUserEventIcePrxHelper __h = new PurchasedVirtualGoodsUserEventIcePrxHelper();
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

    public static PurchasedVirtualGoodsUserEventIcePrx uncheckedCast(ObjectPrx __obj) {
        PurchasedVirtualGoodsUserEventIcePrx __d = null;
        if (__obj != null) {
            try {
                __d = (PurchasedVirtualGoodsUserEventIcePrx)__obj;
            }
            catch (ClassCastException ex) {
                PurchasedVirtualGoodsUserEventIcePrxHelper __h = new PurchasedVirtualGoodsUserEventIcePrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static PurchasedVirtualGoodsUserEventIcePrx uncheckedCast(ObjectPrx __obj, String __facet) {
        PurchasedVirtualGoodsUserEventIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            PurchasedVirtualGoodsUserEventIcePrxHelper __h = new PurchasedVirtualGoodsUserEventIcePrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _PurchasedVirtualGoodsUserEventIceDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _PurchasedVirtualGoodsUserEventIceDelD();
    }

    public static void __write(BasicStream __os, PurchasedVirtualGoodsUserEventIcePrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static PurchasedVirtualGoodsUserEventIcePrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            PurchasedVirtualGoodsUserEventIcePrxHelper result = new PurchasedVirtualGoodsUserEventIcePrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

