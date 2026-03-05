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
import com.projectgoth.fusion.slice.FusionExceptionWithRefCode;
import com.projectgoth.fusion.slice.RecommendationDataCollectionServiceStats;
import com.projectgoth.fusion.slice._RecommendationDataCollectionServiceAdminDisp;
import com.projectgoth.fusion.slice._RecommendationDataCollectionServiceAdminOperations;

public class _RecommendationDataCollectionServiceAdminTie
extends _RecommendationDataCollectionServiceAdminDisp
implements TieBase {
    private _RecommendationDataCollectionServiceAdminOperations _ice_delegate;

    public _RecommendationDataCollectionServiceAdminTie() {
    }

    public _RecommendationDataCollectionServiceAdminTie(_RecommendationDataCollectionServiceAdminOperations delegate) {
        this._ice_delegate = delegate;
    }

    public Object ice_delegate() {
        return this._ice_delegate;
    }

    public void ice_delegate(Object delegate) {
        this._ice_delegate = (_RecommendationDataCollectionServiceAdminOperations)delegate;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        if (!(rhs instanceof _RecommendationDataCollectionServiceAdminTie)) {
            return false;
        }
        return this._ice_delegate.equals(((_RecommendationDataCollectionServiceAdminTie)rhs)._ice_delegate);
    }

    public int hashCode() {
        return this._ice_delegate.hashCode();
    }

    public RecommendationDataCollectionServiceStats getStats(Current __current) throws FusionExceptionWithRefCode {
        return this._ice_delegate.getStats(__current);
    }
}

