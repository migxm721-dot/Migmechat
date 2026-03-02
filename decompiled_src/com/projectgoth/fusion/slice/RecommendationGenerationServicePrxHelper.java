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
import com.projectgoth.fusion.slice.RecommendationGenerationServicePrx;
import com.projectgoth.fusion.slice._RecommendationGenerationServiceDel;
import com.projectgoth.fusion.slice._RecommendationGenerationServiceDelD;
import com.projectgoth.fusion.slice._RecommendationGenerationServiceDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class RecommendationGenerationServicePrxHelper
extends ObjectPrxHelperBase
implements RecommendationGenerationServicePrx {
    @Override
    public void runTransformation(int transformationID) {
        this.runTransformation(transformationID, null, false);
    }

    @Override
    public void runTransformation(int transformationID, Map<String, String> __ctx) {
        this.runTransformation(transformationID, __ctx, true);
    }

    private void runTransformation(int transformationID, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _RecommendationGenerationServiceDel __del = (_RecommendationGenerationServiceDel)__delBase;
                __del.runTransformation(transformationID, __ctx);
                return;
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

    public static RecommendationGenerationServicePrx checkedCast(ObjectPrx __obj) {
        RecommendationGenerationServicePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (RecommendationGenerationServicePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::RecommendationGenerationService")) break block3;
                    RecommendationGenerationServicePrxHelper __h = new RecommendationGenerationServicePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static RecommendationGenerationServicePrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        RecommendationGenerationServicePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (RecommendationGenerationServicePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::RecommendationGenerationService", __ctx)) break block3;
                    RecommendationGenerationServicePrxHelper __h = new RecommendationGenerationServicePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static RecommendationGenerationServicePrx checkedCast(ObjectPrx __obj, String __facet) {
        RecommendationGenerationServicePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::RecommendationGenerationService")) {
                    RecommendationGenerationServicePrxHelper __h = new RecommendationGenerationServicePrxHelper();
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

    public static RecommendationGenerationServicePrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        RecommendationGenerationServicePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::RecommendationGenerationService", __ctx)) {
                    RecommendationGenerationServicePrxHelper __h = new RecommendationGenerationServicePrxHelper();
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

    public static RecommendationGenerationServicePrx uncheckedCast(ObjectPrx __obj) {
        RecommendationGenerationServicePrx __d = null;
        if (__obj != null) {
            try {
                __d = (RecommendationGenerationServicePrx)__obj;
            }
            catch (ClassCastException ex) {
                RecommendationGenerationServicePrxHelper __h = new RecommendationGenerationServicePrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static RecommendationGenerationServicePrx uncheckedCast(ObjectPrx __obj, String __facet) {
        RecommendationGenerationServicePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            RecommendationGenerationServicePrxHelper __h = new RecommendationGenerationServicePrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _RecommendationGenerationServiceDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _RecommendationGenerationServiceDelD();
    }

    public static void __write(BasicStream __os, RecommendationGenerationServicePrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static RecommendationGenerationServicePrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            RecommendationGenerationServicePrxHelper result = new RecommendationGenerationServicePrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

