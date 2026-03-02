/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 *  Ice.TieBase
 */
package com.projectgoth.fusion.slice;

import Ice.Current;
import Ice.TieBase;
import com.projectgoth.fusion.slice.CollectedDataIce;
import com.projectgoth.fusion.slice.FusionExceptionWithRefCode;
import com.projectgoth.fusion.slice._RecommendationDataCollectionServiceDisp;
import com.projectgoth.fusion.slice._RecommendationDataCollectionServiceOperations;

public class _RecommendationDataCollectionServiceTie
extends _RecommendationDataCollectionServiceDisp
implements TieBase {
    private _RecommendationDataCollectionServiceOperations _ice_delegate;

    public _RecommendationDataCollectionServiceTie() {
    }

    public _RecommendationDataCollectionServiceTie(_RecommendationDataCollectionServiceOperations delegate) {
        this._ice_delegate = delegate;
    }

    public Object ice_delegate() {
        return this._ice_delegate;
    }

    public void ice_delegate(Object delegate) {
        this._ice_delegate = (_RecommendationDataCollectionServiceOperations)delegate;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        if (!(rhs instanceof _RecommendationDataCollectionServiceTie)) {
            return false;
        }
        return this._ice_delegate.equals(((_RecommendationDataCollectionServiceTie)rhs)._ice_delegate);
    }

    public int hashCode() {
        return this._ice_delegate.hashCode();
    }

    public void logData(CollectedDataIce dataIce, Current __current) throws FusionExceptionWithRefCode {
        this._ice_delegate.logData(dataIce, __current);
    }
}

