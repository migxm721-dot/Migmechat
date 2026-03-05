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
import com.projectgoth.fusion.slice.CollectedAddressBookDataIcePrx;
import com.projectgoth.fusion.slice._CollectedAddressBookDataIceDelD;
import com.projectgoth.fusion.slice._CollectedAddressBookDataIceDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class CollectedAddressBookDataIcePrxHelper
extends ObjectPrxHelperBase
implements CollectedAddressBookDataIcePrx {
    public static CollectedAddressBookDataIcePrx checkedCast(ObjectPrx __obj) {
        CollectedAddressBookDataIcePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (CollectedAddressBookDataIcePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::CollectedAddressBookDataIce")) break block3;
                    CollectedAddressBookDataIcePrxHelper __h = new CollectedAddressBookDataIcePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static CollectedAddressBookDataIcePrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        CollectedAddressBookDataIcePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (CollectedAddressBookDataIcePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::CollectedAddressBookDataIce", __ctx)) break block3;
                    CollectedAddressBookDataIcePrxHelper __h = new CollectedAddressBookDataIcePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static CollectedAddressBookDataIcePrx checkedCast(ObjectPrx __obj, String __facet) {
        CollectedAddressBookDataIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::CollectedAddressBookDataIce")) {
                    CollectedAddressBookDataIcePrxHelper __h = new CollectedAddressBookDataIcePrxHelper();
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

    public static CollectedAddressBookDataIcePrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        CollectedAddressBookDataIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::CollectedAddressBookDataIce", __ctx)) {
                    CollectedAddressBookDataIcePrxHelper __h = new CollectedAddressBookDataIcePrxHelper();
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

    public static CollectedAddressBookDataIcePrx uncheckedCast(ObjectPrx __obj) {
        CollectedAddressBookDataIcePrx __d = null;
        if (__obj != null) {
            try {
                __d = (CollectedAddressBookDataIcePrx)__obj;
            }
            catch (ClassCastException ex) {
                CollectedAddressBookDataIcePrxHelper __h = new CollectedAddressBookDataIcePrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static CollectedAddressBookDataIcePrx uncheckedCast(ObjectPrx __obj, String __facet) {
        CollectedAddressBookDataIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            CollectedAddressBookDataIcePrxHelper __h = new CollectedAddressBookDataIcePrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _CollectedAddressBookDataIceDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _CollectedAddressBookDataIceDelD();
    }

    public static void __write(BasicStream __os, CollectedAddressBookDataIcePrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static CollectedAddressBookDataIcePrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            CollectedAddressBookDataIcePrxHelper result = new CollectedAddressBookDataIcePrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

