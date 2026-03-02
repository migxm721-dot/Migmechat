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
import com.projectgoth.fusion.slice.EventSystemStats;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice._EventSystemAdminDisp;
import com.projectgoth.fusion.slice._EventSystemAdminOperations;

public class _EventSystemAdminTie
extends _EventSystemAdminDisp
implements TieBase {
    private _EventSystemAdminOperations _ice_delegate;

    public _EventSystemAdminTie() {
    }

    public _EventSystemAdminTie(_EventSystemAdminOperations delegate) {
        this._ice_delegate = delegate;
    }

    public Object ice_delegate() {
        return this._ice_delegate;
    }

    public void ice_delegate(Object delegate) {
        this._ice_delegate = (_EventSystemAdminOperations)delegate;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        if (!(rhs instanceof _EventSystemAdminTie)) {
            return false;
        }
        return this._ice_delegate.equals(((_EventSystemAdminTie)rhs)._ice_delegate);
    }

    public int hashCode() {
        return this._ice_delegate.hashCode();
    }

    public EventSystemStats getStats(Current __current) throws FusionException {
        return this._ice_delegate.getStats(__current);
    }
}

