/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.FacetNotExistException
 *  Ice.LocalException
 *  Ice.ObjectPrx
 *  Ice.ObjectPrxHelperBase
 *  Ice._ObjectDel
 *  Ice._ObjectDelD
 *  Ice._ObjectDelM
 *  IceInternal.BasicStream
 *  IceInternal.LocalExceptionWrapper
 */
package com.projectgoth.fusion.slice;

import Ice.FacetNotExistException;
import Ice.LocalException;
import Ice.ObjectPrx;
import Ice.ObjectPrxHelperBase;
import Ice._ObjectDel;
import Ice._ObjectDelD;
import Ice._ObjectDelM;
import IceInternal.BasicStream;
import IceInternal.LocalExceptionWrapper;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.ImageServerAdminPrx;
import com.projectgoth.fusion.slice.ImageServerStats;
import com.projectgoth.fusion.slice._ImageServerAdminDel;
import com.projectgoth.fusion.slice._ImageServerAdminDelD;
import com.projectgoth.fusion.slice._ImageServerAdminDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class ImageServerAdminPrxHelper
extends ObjectPrxHelperBase
implements ImageServerAdminPrx {
    @Override
    public ImageServerStats getStats() throws FusionException {
        return this.getStats(null, false);
    }

    @Override
    public ImageServerStats getStats(Map<String, String> __ctx) throws FusionException {
        return this.getStats(__ctx, true);
    }

    private ImageServerStats getStats(Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getStats");
                __delBase = this.__getDelegate(false);
                _ImageServerAdminDel __del = (_ImageServerAdminDel)__delBase;
                return __del.getStats(__ctx);
            }
            catch (LocalExceptionWrapper __ex) {
                this.__handleExceptionWrapper(__delBase, __ex, null);
                continue;
            }
            catch (LocalException __ex) {
                __cnt = this.__handleException(__delBase, __ex, null, __cnt);
                continue;
            }
            break;
        }
    }

    public static ImageServerAdminPrx checkedCast(ObjectPrx __obj) {
        ImageServerAdminPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (ImageServerAdminPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::ImageServerAdmin")) break block3;
                    ImageServerAdminPrxHelper __h = new ImageServerAdminPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static ImageServerAdminPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        ImageServerAdminPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (ImageServerAdminPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::ImageServerAdmin", __ctx)) break block3;
                    ImageServerAdminPrxHelper __h = new ImageServerAdminPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static ImageServerAdminPrx checkedCast(ObjectPrx __obj, String __facet) {
        ImageServerAdminPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::ImageServerAdmin")) {
                    ImageServerAdminPrxHelper __h = new ImageServerAdminPrxHelper();
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

    public static ImageServerAdminPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        ImageServerAdminPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::ImageServerAdmin", __ctx)) {
                    ImageServerAdminPrxHelper __h = new ImageServerAdminPrxHelper();
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

    public static ImageServerAdminPrx uncheckedCast(ObjectPrx __obj) {
        ImageServerAdminPrx __d = null;
        if (__obj != null) {
            try {
                __d = (ImageServerAdminPrx)__obj;
            }
            catch (ClassCastException ex) {
                ImageServerAdminPrxHelper __h = new ImageServerAdminPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static ImageServerAdminPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        ImageServerAdminPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            ImageServerAdminPrxHelper __h = new ImageServerAdminPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _ImageServerAdminDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _ImageServerAdminDelD();
    }

    public static void __write(BasicStream __os, ImageServerAdminPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static ImageServerAdminPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            ImageServerAdminPrxHelper result = new ImageServerAdminPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

