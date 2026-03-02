/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 *  Ice.TieBase
 */
package com.projectgoth.fusion.slice.tests;

import Ice.Current;
import Ice.TieBase;
import com.projectgoth.fusion.slice.tests._PrinterDisp;
import com.projectgoth.fusion.slice.tests._PrinterOperations;

public class _PrinterTie
extends _PrinterDisp
implements TieBase {
    private _PrinterOperations _ice_delegate;

    public _PrinterTie() {
    }

    public _PrinterTie(_PrinterOperations delegate) {
        this._ice_delegate = delegate;
    }

    public Object ice_delegate() {
        return this._ice_delegate;
    }

    public void ice_delegate(Object delegate) {
        this._ice_delegate = (_PrinterOperations)delegate;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        if (!(rhs instanceof _PrinterTie)) {
            return false;
        }
        return this._ice_delegate.equals(((_PrinterTie)rhs)._ice_delegate);
    }

    public int hashCode() {
        return this._ice_delegate.hashCode();
    }

    public void circular(String s, int level, Current __current) {
        this._ice_delegate.circular(s, level, __current);
    }

    public void printString(String s, Current __current) {
        this._ice_delegate.printString(s, __current);
    }
}

