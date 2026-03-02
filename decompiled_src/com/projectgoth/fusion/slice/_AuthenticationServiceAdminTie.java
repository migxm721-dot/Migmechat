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
import com.projectgoth.fusion.slice.AuthenticationServiceStats;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice._AuthenticationServiceAdminDisp;
import com.projectgoth.fusion.slice._AuthenticationServiceAdminOperations;

public class _AuthenticationServiceAdminTie
extends _AuthenticationServiceAdminDisp
implements TieBase {
    private _AuthenticationServiceAdminOperations _ice_delegate;

    public _AuthenticationServiceAdminTie() {
    }

    public _AuthenticationServiceAdminTie(_AuthenticationServiceAdminOperations delegate) {
        this._ice_delegate = delegate;
    }

    public Object ice_delegate() {
        return this._ice_delegate;
    }

    public void ice_delegate(Object delegate) {
        this._ice_delegate = (_AuthenticationServiceAdminOperations)delegate;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        if (!(rhs instanceof _AuthenticationServiceAdminTie)) {
            return false;
        }
        return this._ice_delegate.equals(((_AuthenticationServiceAdminTie)rhs)._ice_delegate);
    }

    public int hashCode() {
        return this._ice_delegate.hashCode();
    }

    public AuthenticationServiceStats getStats(Current __current) throws FusionException {
        return this._ice_delegate.getStats(__current);
    }
}

