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
import com.projectgoth.fusion.slice.ReputationServicePrx;
import com.projectgoth.fusion.slice.ScoreAndLevel;
import com.projectgoth.fusion.slice._ReputationServiceDel;
import com.projectgoth.fusion.slice._ReputationServiceDelD;
import com.projectgoth.fusion.slice._ReputationServiceDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class ReputationServicePrxHelper
extends ObjectPrxHelperBase
implements ReputationServicePrx {
    @Override
    public void gatherAndProcess() throws FusionException {
        this.gatherAndProcess(null, false);
    }

    @Override
    public void gatherAndProcess(Map<String, String> __ctx) throws FusionException {
        this.gatherAndProcess(__ctx, true);
    }

    private void gatherAndProcess(Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("gatherAndProcess");
                __delBase = this.__getDelegate(false);
                _ReputationServiceDel __del = (_ReputationServiceDel)__delBase;
                __del.gatherAndProcess(__ctx);
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

    @Override
    public int getUserLevel(String username) throws FusionException {
        return this.getUserLevel(username, null, false);
    }

    @Override
    public int getUserLevel(String username, Map<String, String> __ctx) throws FusionException {
        return this.getUserLevel(username, __ctx, true);
    }

    private int getUserLevel(String username, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getUserLevel");
                __delBase = this.__getDelegate(false);
                _ReputationServiceDel __del = (_ReputationServiceDel)__delBase;
                return __del.getUserLevel(username, __ctx);
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

    @Override
    public ScoreAndLevel[] getUserScoreAndLevels(int[] userIDs) throws FusionException {
        return this.getUserScoreAndLevels(userIDs, null, false);
    }

    @Override
    public ScoreAndLevel[] getUserScoreAndLevels(int[] userIDs, Map<String, String> __ctx) throws FusionException {
        return this.getUserScoreAndLevels(userIDs, __ctx, true);
    }

    private ScoreAndLevel[] getUserScoreAndLevels(int[] userIDs, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getUserScoreAndLevels");
                __delBase = this.__getDelegate(false);
                _ReputationServiceDel __del = (_ReputationServiceDel)__delBase;
                return __del.getUserScoreAndLevels(userIDs, __ctx);
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

    @Override
    public void processPreviouslyDumpedData(String runDateString) throws FusionException {
        this.processPreviouslyDumpedData(runDateString, null, false);
    }

    @Override
    public void processPreviouslyDumpedData(String runDateString, Map<String, String> __ctx) throws FusionException {
        this.processPreviouslyDumpedData(runDateString, __ctx, true);
    }

    private void processPreviouslyDumpedData(String runDateString, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("processPreviouslyDumpedData");
                __delBase = this.__getDelegate(false);
                _ReputationServiceDel __del = (_ReputationServiceDel)__delBase;
                __del.processPreviouslyDumpedData(runDateString, __ctx);
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

    @Override
    public void processPreviouslySortedData(String runDateString) throws FusionException {
        this.processPreviouslySortedData(runDateString, null, false);
    }

    @Override
    public void processPreviouslySortedData(String runDateString, Map<String, String> __ctx) throws FusionException {
        this.processPreviouslySortedData(runDateString, __ctx, true);
    }

    private void processPreviouslySortedData(String runDateString, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("processPreviouslySortedData");
                __delBase = this.__getDelegate(false);
                _ReputationServiceDel __del = (_ReputationServiceDel)__delBase;
                __del.processPreviouslySortedData(runDateString, __ctx);
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

    @Override
    public void updateLastRunDate() throws FusionException {
        this.updateLastRunDate(null, false);
    }

    @Override
    public void updateLastRunDate(Map<String, String> __ctx) throws FusionException {
        this.updateLastRunDate(__ctx, true);
    }

    private void updateLastRunDate(Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("updateLastRunDate");
                __delBase = this.__getDelegate(false);
                _ReputationServiceDel __del = (_ReputationServiceDel)__delBase;
                __del.updateLastRunDate(__ctx);
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

    @Override
    public void updateScoreFromPreviouslyProcessedData(String runDateString) throws FusionException {
        this.updateScoreFromPreviouslyProcessedData(runDateString, null, false);
    }

    @Override
    public void updateScoreFromPreviouslyProcessedData(String runDateString, Map<String, String> __ctx) throws FusionException {
        this.updateScoreFromPreviouslyProcessedData(runDateString, __ctx, true);
    }

    private void updateScoreFromPreviouslyProcessedData(String runDateString, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("updateScoreFromPreviouslyProcessedData");
                __delBase = this.__getDelegate(false);
                _ReputationServiceDel __del = (_ReputationServiceDel)__delBase;
                __del.updateScoreFromPreviouslyProcessedData(runDateString, __ctx);
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

    public static ReputationServicePrx checkedCast(ObjectPrx __obj) {
        ReputationServicePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (ReputationServicePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::ReputationService")) break block3;
                    ReputationServicePrxHelper __h = new ReputationServicePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static ReputationServicePrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        ReputationServicePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (ReputationServicePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::ReputationService", __ctx)) break block3;
                    ReputationServicePrxHelper __h = new ReputationServicePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static ReputationServicePrx checkedCast(ObjectPrx __obj, String __facet) {
        ReputationServicePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::ReputationService")) {
                    ReputationServicePrxHelper __h = new ReputationServicePrxHelper();
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

    public static ReputationServicePrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        ReputationServicePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::ReputationService", __ctx)) {
                    ReputationServicePrxHelper __h = new ReputationServicePrxHelper();
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

    public static ReputationServicePrx uncheckedCast(ObjectPrx __obj) {
        ReputationServicePrx __d = null;
        if (__obj != null) {
            try {
                __d = (ReputationServicePrx)__obj;
            }
            catch (ClassCastException ex) {
                ReputationServicePrxHelper __h = new ReputationServicePrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static ReputationServicePrx uncheckedCast(ObjectPrx __obj, String __facet) {
        ReputationServicePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            ReputationServicePrxHelper __h = new ReputationServicePrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _ReputationServiceDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _ReputationServiceDelD();
    }

    public static void __write(BasicStream __os, ReputationServicePrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static ReputationServicePrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            ReputationServicePrxHelper result = new ReputationServicePrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

