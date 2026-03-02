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
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.RecommendationGenerationServiceStats;
import com.projectgoth.fusion.slice._RecommendationGenerationServiceAdminDisp;
import com.projectgoth.fusion.slice._RecommendationGenerationServiceAdminOperations;

public class _RecommendationGenerationServiceAdminTie
extends _RecommendationGenerationServiceAdminDisp
implements TieBase {
    private _RecommendationGenerationServiceAdminOperations _ice_delegate;

    public _RecommendationGenerationServiceAdminTie() {
    }

    public _RecommendationGenerationServiceAdminTie(_RecommendationGenerationServiceAdminOperations delegate) {
        this._ice_delegate = delegate;
    }

    public Object ice_delegate() {
        return this._ice_delegate;
    }

    public void ice_delegate(Object delegate) {
        this._ice_delegate = (_RecommendationGenerationServiceAdminOperations)delegate;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        if (!(rhs instanceof _RecommendationGenerationServiceAdminTie)) {
            return false;
        }
        return this._ice_delegate.equals(((_RecommendationGenerationServiceAdminTie)rhs)._ice_delegate);
    }

    public int hashCode() {
        return this._ice_delegate.hashCode();
    }

    public RecommendationGenerationServiceStats getStats(Current __current) throws FusionException {
        return this._ice_delegate.getStats(__current);
    }
}

