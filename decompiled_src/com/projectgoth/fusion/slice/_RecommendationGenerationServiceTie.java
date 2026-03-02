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
import com.projectgoth.fusion.slice._RecommendationGenerationServiceDisp;
import com.projectgoth.fusion.slice._RecommendationGenerationServiceOperations;

public class _RecommendationGenerationServiceTie
extends _RecommendationGenerationServiceDisp
implements TieBase {
    private _RecommendationGenerationServiceOperations _ice_delegate;

    public _RecommendationGenerationServiceTie() {
    }

    public _RecommendationGenerationServiceTie(_RecommendationGenerationServiceOperations delegate) {
        this._ice_delegate = delegate;
    }

    public Object ice_delegate() {
        return this._ice_delegate;
    }

    public void ice_delegate(Object delegate) {
        this._ice_delegate = (_RecommendationGenerationServiceOperations)delegate;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        if (!(rhs instanceof _RecommendationGenerationServiceTie)) {
            return false;
        }
        return this._ice_delegate.equals(((_RecommendationGenerationServiceTie)rhs)._ice_delegate);
    }

    public int hashCode() {
        return this._ice_delegate.hashCode();
    }

    public void runTransformation(int transformationID, Current __current) {
        this._ice_delegate.runTransformation(transformationID, __current);
    }
}

