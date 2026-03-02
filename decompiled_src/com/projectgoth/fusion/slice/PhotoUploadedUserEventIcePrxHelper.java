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
import com.projectgoth.fusion.slice.PhotoUploadedUserEventIcePrx;
import com.projectgoth.fusion.slice._PhotoUploadedUserEventIceDelD;
import com.projectgoth.fusion.slice._PhotoUploadedUserEventIceDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class PhotoUploadedUserEventIcePrxHelper
extends ObjectPrxHelperBase
implements PhotoUploadedUserEventIcePrx {
    public static PhotoUploadedUserEventIcePrx checkedCast(ObjectPrx __obj) {
        PhotoUploadedUserEventIcePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (PhotoUploadedUserEventIcePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::PhotoUploadedUserEventIce")) break block3;
                    PhotoUploadedUserEventIcePrxHelper __h = new PhotoUploadedUserEventIcePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static PhotoUploadedUserEventIcePrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        PhotoUploadedUserEventIcePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (PhotoUploadedUserEventIcePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::PhotoUploadedUserEventIce", __ctx)) break block3;
                    PhotoUploadedUserEventIcePrxHelper __h = new PhotoUploadedUserEventIcePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static PhotoUploadedUserEventIcePrx checkedCast(ObjectPrx __obj, String __facet) {
        PhotoUploadedUserEventIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::PhotoUploadedUserEventIce")) {
                    PhotoUploadedUserEventIcePrxHelper __h = new PhotoUploadedUserEventIcePrxHelper();
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

    public static PhotoUploadedUserEventIcePrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        PhotoUploadedUserEventIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::PhotoUploadedUserEventIce", __ctx)) {
                    PhotoUploadedUserEventIcePrxHelper __h = new PhotoUploadedUserEventIcePrxHelper();
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

    public static PhotoUploadedUserEventIcePrx uncheckedCast(ObjectPrx __obj) {
        PhotoUploadedUserEventIcePrx __d = null;
        if (__obj != null) {
            try {
                __d = (PhotoUploadedUserEventIcePrx)__obj;
            }
            catch (ClassCastException ex) {
                PhotoUploadedUserEventIcePrxHelper __h = new PhotoUploadedUserEventIcePrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static PhotoUploadedUserEventIcePrx uncheckedCast(ObjectPrx __obj, String __facet) {
        PhotoUploadedUserEventIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            PhotoUploadedUserEventIcePrxHelper __h = new PhotoUploadedUserEventIcePrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _PhotoUploadedUserEventIceDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _PhotoUploadedUserEventIceDelD();
    }

    public static void __write(BasicStream __os, PhotoUploadedUserEventIcePrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static PhotoUploadedUserEventIcePrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            PhotoUploadedUserEventIcePrxHelper result = new PhotoUploadedUserEventIcePrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

