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
import com.projectgoth.fusion.slice.BlueLabelServiceStats;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice._BlueLabelServiceAdminDisp;
import com.projectgoth.fusion.slice._BlueLabelServiceAdminOperations;

public class _BlueLabelServiceAdminTie
extends _BlueLabelServiceAdminDisp
implements TieBase {
    private _BlueLabelServiceAdminOperations _ice_delegate;

    public _BlueLabelServiceAdminTie() {
    }

    public _BlueLabelServiceAdminTie(_BlueLabelServiceAdminOperations delegate) {
        this._ice_delegate = delegate;
    }

    public Object ice_delegate() {
        return this._ice_delegate;
    }

    public void ice_delegate(Object delegate) {
        this._ice_delegate = (_BlueLabelServiceAdminOperations)delegate;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        if (!(rhs instanceof _BlueLabelServiceAdminTie)) {
            return false;
        }
        return this._ice_delegate.equals(((_BlueLabelServiceAdminTie)rhs)._ice_delegate);
    }

    public int hashCode() {
        return this._ice_delegate.hashCode();
    }

    public BlueLabelServiceStats getStats(Current __current) throws FusionException {
        return this._ice_delegate.getStats(__current);
    }
}

