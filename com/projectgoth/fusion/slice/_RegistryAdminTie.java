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
import com.projectgoth.fusion.slice.RegistryStats;
import com.projectgoth.fusion.slice._RegistryAdminDisp;
import com.projectgoth.fusion.slice._RegistryAdminOperations;

public class _RegistryAdminTie
extends _RegistryAdminDisp
implements TieBase {
    private _RegistryAdminOperations _ice_delegate;

    public _RegistryAdminTie() {
    }

    public _RegistryAdminTie(_RegistryAdminOperations delegate) {
        this._ice_delegate = delegate;
    }

    public Object ice_delegate() {
        return this._ice_delegate;
    }

    public void ice_delegate(Object delegate) {
        this._ice_delegate = (_RegistryAdminOperations)delegate;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        if (!(rhs instanceof _RegistryAdminTie)) {
            return false;
        }
        return this._ice_delegate.equals(((_RegistryAdminTie)rhs)._ice_delegate);
    }

    public int hashCode() {
        return this._ice_delegate.hashCode();
    }

    public RegistryStats getStats(Current __current) throws FusionException {
        return this._ice_delegate.getStats(__current);
    }
}

