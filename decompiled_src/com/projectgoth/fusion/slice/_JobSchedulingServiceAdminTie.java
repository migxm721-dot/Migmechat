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
import com.projectgoth.fusion.slice.JobSchedulingServiceStats;
import com.projectgoth.fusion.slice._JobSchedulingServiceAdminDisp;
import com.projectgoth.fusion.slice._JobSchedulingServiceAdminOperations;

public class _JobSchedulingServiceAdminTie
extends _JobSchedulingServiceAdminDisp
implements TieBase {
    private _JobSchedulingServiceAdminOperations _ice_delegate;

    public _JobSchedulingServiceAdminTie() {
    }

    public _JobSchedulingServiceAdminTie(_JobSchedulingServiceAdminOperations delegate) {
        this._ice_delegate = delegate;
    }

    public Object ice_delegate() {
        return this._ice_delegate;
    }

    public void ice_delegate(Object delegate) {
        this._ice_delegate = (_JobSchedulingServiceAdminOperations)delegate;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        if (!(rhs instanceof _JobSchedulingServiceAdminTie)) {
            return false;
        }
        return this._ice_delegate.equals(((_JobSchedulingServiceAdminTie)rhs)._ice_delegate);
    }

    public int hashCode() {
        return this._ice_delegate.hashCode();
    }

    public JobSchedulingServiceStats getStats(Current __current) throws FusionException {
        return this._ice_delegate.getStats(__current);
    }
}

