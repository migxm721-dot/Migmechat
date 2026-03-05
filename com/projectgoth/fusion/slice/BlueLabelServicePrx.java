/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.ObjectPrx
 */
package com.projectgoth.fusion.slice;

import Ice.ObjectPrx;
import com.projectgoth.fusion.slice.BlueLabelOneVoucher;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.WebServiceResponse;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface BlueLabelServicePrx
extends ObjectPrx {
    public WebServiceResponse registerAccount(String var1, String var2, int var3, String var4, int var5, String var6, String var7, String var8, String var9, String var10, String var11, String var12) throws FusionException;

    public WebServiceResponse registerAccount(String var1, String var2, int var3, String var4, int var5, String var6, String var7, String var8, String var9, String var10, String var11, String var12, Map<String, String> var13) throws FusionException;

    public WebServiceResponse fullVoucherRedemption(String var1, String var2, BlueLabelOneVoucher var3) throws FusionException;

    public WebServiceResponse fullVoucherRedemption(String var1, String var2, BlueLabelOneVoucher var3, Map<String, String> var4) throws FusionException;

    public WebServiceResponse getAccountStatus(String var1) throws FusionException;

    public WebServiceResponse getAccountStatus(String var1, Map<String, String> var2) throws FusionException;

    public WebServiceResponse authenticate(String var1) throws FusionException;

    public WebServiceResponse authenticate(String var1, Map<String, String> var2) throws FusionException;
}

