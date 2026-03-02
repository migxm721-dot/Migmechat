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
import com.projectgoth.fusion.slice.ImageServerStatsPrx;
import com.projectgoth.fusion.slice._ImageServerStatsDelD;
import com.projectgoth.fusion.slice._ImageServerStatsDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class ImageServerStatsPrxHelper
extends ObjectPrxHelperBase
implements ImageServerStatsPrx {
    public static ImageServerStatsPrx checkedCast(ObjectPrx __obj) {
        ImageServerStatsPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (ImageServerStatsPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::ImageServerStats")) break block3;
                    ImageServerStatsPrxHelper __h = new ImageServerStatsPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static ImageServerStatsPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        ImageServerStatsPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (ImageServerStatsPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::ImageServerStats", __ctx)) break block3;
                    ImageServerStatsPrxHelper __h = new ImageServerStatsPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static ImageServerStatsPrx checkedCast(ObjectPrx __obj, String __facet) {
        ImageServerStatsPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::ImageServerStats")) {
                    ImageServerStatsPrxHelper __h = new ImageServerStatsPrxHelper();
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

    public static ImageServerStatsPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        ImageServerStatsPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::ImageServerStats", __ctx)) {
                    ImageServerStatsPrxHelper __h = new ImageServerStatsPrxHelper();
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

    public static ImageServerStatsPrx uncheckedCast(ObjectPrx __obj) {
        ImageServerStatsPrx __d = null;
        if (__obj != null) {
            try {
                __d = (ImageServerStatsPrx)__obj;
            }
            catch (ClassCastException ex) {
                ImageServerStatsPrxHelper __h = new ImageServerStatsPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static ImageServerStatsPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        ImageServerStatsPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            ImageServerStatsPrxHelper __h = new ImageServerStatsPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _ImageServerStatsDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _ImageServerStatsDelD();
    }

    public static void __write(BasicStream __os, ImageServerStatsPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static ImageServerStatsPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            ImageServerStatsPrxHelper result = new ImageServerStatsPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

