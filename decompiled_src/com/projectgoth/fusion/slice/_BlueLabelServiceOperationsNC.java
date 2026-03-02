/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.slice;

import com.projectgoth.fusion.slice.BlueLabelOneVoucher;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.WebServiceResponse;

public interface _BlueLabelServiceOperationsNC {
    public WebServiceResponse registerAccount(String var1, String var2, int var3, String var4, int var5, String var6, String var7, String var8, String var9, String var10, String var11, String var12) throws FusionException;

    public WebServiceResponse fullVoucherRedemption(String var1, String var2, BlueLabelOneVoucher var3) throws FusionException;

    public WebServiceResponse getAccountStatus(String var1) throws FusionException;

    public WebServiceResponse authenticate(String var1) throws FusionException;
}

