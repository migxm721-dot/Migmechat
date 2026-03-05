/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice._ObjectDel
 *  IceInternal.LocalExceptionWrapper
 */
package com.projectgoth.fusion.slice;

import Ice._ObjectDel;
import IceInternal.LocalExceptionWrapper;
import com.projectgoth.fusion.slice.BlueLabelOneVoucher;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.WebServiceResponse;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface _BlueLabelServiceDel
extends _ObjectDel {
    public WebServiceResponse registerAccount(String var1, String var2, int var3, String var4, int var5, String var6, String var7, String var8, String var9, String var10, String var11, String var12, Map<String, String> var13) throws LocalExceptionWrapper, FusionException;

    public WebServiceResponse fullVoucherRedemption(String var1, String var2, BlueLabelOneVoucher var3, Map<String, String> var4) throws LocalExceptionWrapper, FusionException;

    public WebServiceResponse getAccountStatus(String var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public WebServiceResponse authenticate(String var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;
}

