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
import com.projectgoth.fusion.slice.CollectedRewardProgramTriggerSummaryDataIcePrx;
import com.projectgoth.fusion.slice._CollectedRewardProgramTriggerSummaryDataIceDelD;
import com.projectgoth.fusion.slice._CollectedRewardProgramTriggerSummaryDataIceDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class CollectedRewardProgramTriggerSummaryDataIcePrxHelper
extends ObjectPrxHelperBase
implements CollectedRewardProgramTriggerSummaryDataIcePrx {
    public static CollectedRewardProgramTriggerSummaryDataIcePrx checkedCast(ObjectPrx __obj) {
        CollectedRewardProgramTriggerSummaryDataIcePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (CollectedRewardProgramTriggerSummaryDataIcePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::CollectedRewardProgramTriggerSummaryDataIce")) break block3;
                    CollectedRewardProgramTriggerSummaryDataIcePrxHelper __h = new CollectedRewardProgramTriggerSummaryDataIcePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static CollectedRewardProgramTriggerSummaryDataIcePrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        CollectedRewardProgramTriggerSummaryDataIcePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (CollectedRewardProgramTriggerSummaryDataIcePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::CollectedRewardProgramTriggerSummaryDataIce", __ctx)) break block3;
                    CollectedRewardProgramTriggerSummaryDataIcePrxHelper __h = new CollectedRewardProgramTriggerSummaryDataIcePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static CollectedRewardProgramTriggerSummaryDataIcePrx checkedCast(ObjectPrx __obj, String __facet) {
        CollectedRewardProgramTriggerSummaryDataIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::CollectedRewardProgramTriggerSummaryDataIce")) {
                    CollectedRewardProgramTriggerSummaryDataIcePrxHelper __h = new CollectedRewardProgramTriggerSummaryDataIcePrxHelper();
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

    public static CollectedRewardProgramTriggerSummaryDataIcePrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        CollectedRewardProgramTriggerSummaryDataIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::CollectedRewardProgramTriggerSummaryDataIce", __ctx)) {
                    CollectedRewardProgramTriggerSummaryDataIcePrxHelper __h = new CollectedRewardProgramTriggerSummaryDataIcePrxHelper();
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

    public static CollectedRewardProgramTriggerSummaryDataIcePrx uncheckedCast(ObjectPrx __obj) {
        CollectedRewardProgramTriggerSummaryDataIcePrx __d = null;
        if (__obj != null) {
            try {
                __d = (CollectedRewardProgramTriggerSummaryDataIcePrx)__obj;
            }
            catch (ClassCastException ex) {
                CollectedRewardProgramTriggerSummaryDataIcePrxHelper __h = new CollectedRewardProgramTriggerSummaryDataIcePrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static CollectedRewardProgramTriggerSummaryDataIcePrx uncheckedCast(ObjectPrx __obj, String __facet) {
        CollectedRewardProgramTriggerSummaryDataIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            CollectedRewardProgramTriggerSummaryDataIcePrxHelper __h = new CollectedRewardProgramTriggerSummaryDataIcePrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _CollectedRewardProgramTriggerSummaryDataIceDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _CollectedRewardProgramTriggerSummaryDataIceDelD();
    }

    public static void __write(BasicStream __os, CollectedRewardProgramTriggerSummaryDataIcePrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static CollectedRewardProgramTriggerSummaryDataIcePrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            CollectedRewardProgramTriggerSummaryDataIcePrxHelper result = new CollectedRewardProgramTriggerSummaryDataIcePrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

