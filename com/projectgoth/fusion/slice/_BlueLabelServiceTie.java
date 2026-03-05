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
import com.projectgoth.fusion.slice.BlueLabelOneVoucher;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.WebServiceResponse;
import com.projectgoth.fusion.slice._BlueLabelServiceDisp;
import com.projectgoth.fusion.slice._BlueLabelServiceOperations;

public class _BlueLabelServiceTie
extends _BlueLabelServiceDisp
implements TieBase {
    private _BlueLabelServiceOperations _ice_delegate;

    public _BlueLabelServiceTie() {
    }

    public _BlueLabelServiceTie(_BlueLabelServiceOperations delegate) {
        this._ice_delegate = delegate;
    }

    public Object ice_delegate() {
        return this._ice_delegate;
    }

    public void ice_delegate(Object delegate) {
        this._ice_delegate = (_BlueLabelServiceOperations)delegate;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        if (!(rhs instanceof _BlueLabelServiceTie)) {
            return false;
        }
        return this._ice_delegate.equals(((_BlueLabelServiceTie)rhs)._ice_delegate);
    }

    public int hashCode() {
        return this._ice_delegate.hashCode();
    }

    public WebServiceResponse authenticate(String username, Current __current) throws FusionException {
        return this._ice_delegate.authenticate(username, __current);
    }

    public WebServiceResponse fullVoucherRedemption(String migUsername, String userTicket, BlueLabelOneVoucher voucher, Current __current) throws FusionException {
        return this._ice_delegate.fullVoucherRedemption(migUsername, userTicket, voucher, __current);
    }

    public WebServiceResponse getAccountStatus(String liveid, Current __current) throws FusionException {
        return this._ice_delegate.getAccountStatus(liveid, __current);
    }

    public WebServiceResponse registerAccount(String username, String password, int countryCode, String mobileNumber, int secretQuestionCode, String secretQuestionAnswer, String firstName, String lastName, String nickName, String dateOfBirth, String sex, String emailAddress, Current __current) throws FusionException {
        return this._ice_delegate.registerAccount(username, password, countryCode, mobileNumber, secretQuestionCode, secretQuestionAnswer, firstName, lastName, nickName, dateOfBirth, sex, emailAddress, __current);
    }
}

