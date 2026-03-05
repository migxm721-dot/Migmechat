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
import com.projectgoth.fusion.slice.EmailAlertStats;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice._EmailAlertAdminDisp;
import com.projectgoth.fusion.slice._EmailAlertAdminOperations;

public class _EmailAlertAdminTie
extends _EmailAlertAdminDisp
implements TieBase {
    private _EmailAlertAdminOperations _ice_delegate;

    public _EmailAlertAdminTie() {
    }

    public _EmailAlertAdminTie(_EmailAlertAdminOperations delegate) {
        this._ice_delegate = delegate;
    }

    public Object ice_delegate() {
        return this._ice_delegate;
    }

    public void ice_delegate(Object delegate) {
        this._ice_delegate = (_EmailAlertAdminOperations)delegate;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        if (!(rhs instanceof _EmailAlertAdminTie)) {
            return false;
        }
        return this._ice_delegate.equals(((_EmailAlertAdminTie)rhs)._ice_delegate);
    }

    public int hashCode() {
        return this._ice_delegate.hashCode();
    }

    public EmailAlertStats getStats(Current __current) throws FusionException {
        return this._ice_delegate.getStats(__current);
    }
}

