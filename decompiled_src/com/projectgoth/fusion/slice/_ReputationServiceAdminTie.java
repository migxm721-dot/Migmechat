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
import com.projectgoth.fusion.slice.ReputationServiceStats;
import com.projectgoth.fusion.slice._ReputationServiceAdminDisp;
import com.projectgoth.fusion.slice._ReputationServiceAdminOperations;

public class _ReputationServiceAdminTie
extends _ReputationServiceAdminDisp
implements TieBase {
    private _ReputationServiceAdminOperations _ice_delegate;

    public _ReputationServiceAdminTie() {
    }

    public _ReputationServiceAdminTie(_ReputationServiceAdminOperations delegate) {
        this._ice_delegate = delegate;
    }

    public Object ice_delegate() {
        return this._ice_delegate;
    }

    public void ice_delegate(Object delegate) {
        this._ice_delegate = (_ReputationServiceAdminOperations)delegate;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        if (!(rhs instanceof _ReputationServiceAdminTie)) {
            return false;
        }
        return this._ice_delegate.equals(((_ReputationServiceAdminTie)rhs)._ice_delegate);
    }

    public int hashCode() {
        return this._ice_delegate.hashCode();
    }

    public ReputationServiceStats getStats(Current __current) throws FusionException {
        return this._ice_delegate.getStats(__current);
    }
}

