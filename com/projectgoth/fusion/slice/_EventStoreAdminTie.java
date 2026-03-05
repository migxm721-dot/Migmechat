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
import com.projectgoth.fusion.slice.EventStoreStats;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice._EventStoreAdminDisp;
import com.projectgoth.fusion.slice._EventStoreAdminOperations;

public class _EventStoreAdminTie
extends _EventStoreAdminDisp
implements TieBase {
    private _EventStoreAdminOperations _ice_delegate;

    public _EventStoreAdminTie() {
    }

    public _EventStoreAdminTie(_EventStoreAdminOperations delegate) {
        this._ice_delegate = delegate;
    }

    public Object ice_delegate() {
        return this._ice_delegate;
    }

    public void ice_delegate(Object delegate) {
        this._ice_delegate = (_EventStoreAdminOperations)delegate;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        if (!(rhs instanceof _EventStoreAdminTie)) {
            return false;
        }
        return this._ice_delegate.equals(((_EventStoreAdminTie)rhs)._ice_delegate);
    }

    public int hashCode() {
        return this._ice_delegate.hashCode();
    }

    public EventStoreStats getStats(Current __current) throws FusionException {
        return this._ice_delegate.getStats(__current);
    }
}

