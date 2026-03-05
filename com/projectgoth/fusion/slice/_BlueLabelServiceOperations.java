/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 */
package com.projectgoth.fusion.slice;

import Ice.Current;
import com.projectgoth.fusion.slice.BlueLabelOneVoucher;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.WebServiceResponse;

public interface _BlueLabelServiceOperations {
    public WebServiceResponse registerAccount(String var1, String var2, int var3, String var4, int var5, String var6, String var7, String var8, String var9, String var10, String var11, String var12, Current var13) throws FusionException;

    public WebServiceResponse fullVoucherRedemption(String var1, String var2, BlueLabelOneVoucher var3, Current var4) throws FusionException;

    public WebServiceResponse getAccountStatus(String var1, Current var2) throws FusionException;

    public WebServiceResponse authenticate(String var1, Current var2) throws FusionException;
}

