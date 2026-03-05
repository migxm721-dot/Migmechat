/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.TieBase
 */
package com.projectgoth.fusion.slice;

import Ice.TieBase;
import com.projectgoth.fusion.slice._EventQueueWorkerDisp;
import com.projectgoth.fusion.slice._EventQueueWorkerOperations;

public class _EventQueueWorkerTie
extends _EventQueueWorkerDisp
implements TieBase {
    private _EventQueueWorkerOperations _ice_delegate;

    public _EventQueueWorkerTie() {
    }

    public _EventQueueWorkerTie(_EventQueueWorkerOperations delegate) {
        this._ice_delegate = delegate;
    }

    public Object ice_delegate() {
        return this._ice_delegate;
    }

    public void ice_delegate(Object delegate) {
        this._ice_delegate = (_EventQueueWorkerOperations)delegate;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        if (!(rhs instanceof _EventQueueWorkerTie)) {
            return false;
        }
        return this._ice_delegate.equals(((_EventQueueWorkerTie)rhs)._ice_delegate);
    }

    public int hashCode() {
        return this._ice_delegate.hashCode();
    }
}

