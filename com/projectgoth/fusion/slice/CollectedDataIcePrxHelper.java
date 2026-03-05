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
import com.projectgoth.fusion.slice.CollectedDataIcePrx;
import com.projectgoth.fusion.slice._CollectedDataIceDelD;
import com.projectgoth.fusion.slice._CollectedDataIceDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class CollectedDataIcePrxHelper
extends ObjectPrxHelperBase
implements CollectedDataIcePrx {
    public static CollectedDataIcePrx checkedCast(ObjectPrx __obj) {
        CollectedDataIcePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (CollectedDataIcePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::CollectedDataIce")) break block3;
                    CollectedDataIcePrxHelper __h = new CollectedDataIcePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static CollectedDataIcePrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        CollectedDataIcePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (CollectedDataIcePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::CollectedDataIce", __ctx)) break block3;
                    CollectedDataIcePrxHelper __h = new CollectedDataIcePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static CollectedDataIcePrx checkedCast(ObjectPrx __obj, String __facet) {
        CollectedDataIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::CollectedDataIce")) {
                    CollectedDataIcePrxHelper __h = new CollectedDataIcePrxHelper();
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

    public static CollectedDataIcePrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        CollectedDataIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::CollectedDataIce", __ctx)) {
                    CollectedDataIcePrxHelper __h = new CollectedDataIcePrxHelper();
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

    public static CollectedDataIcePrx uncheckedCast(ObjectPrx __obj) {
        CollectedDataIcePrx __d = null;
        if (__obj != null) {
            try {
                __d = (CollectedDataIcePrx)__obj;
            }
            catch (ClassCastException ex) {
                CollectedDataIcePrxHelper __h = new CollectedDataIcePrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static CollectedDataIcePrx uncheckedCast(ObjectPrx __obj, String __facet) {
        CollectedDataIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            CollectedDataIcePrxHelper __h = new CollectedDataIcePrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _CollectedDataIceDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _CollectedDataIceDelD();
    }

    public static void __write(BasicStream __os, CollectedDataIcePrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static CollectedDataIcePrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            CollectedDataIcePrxHelper result = new CollectedDataIcePrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

