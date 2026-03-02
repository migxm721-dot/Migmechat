/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.EJBException
 *  javax.ejb.EJBLocalObject
 */
package com.projectgoth.fusion.interfaces;

import com.projectgoth.fusion.merchant.MerchantPointsLogData;
import java.util.List;
import javax.ejb.EJBException;
import javax.ejb.EJBLocalObject;

public interface MerchantsLocal
extends EJBLocalObject {
    public MerchantPointsLogData insertMerchantPoints(MerchantPointsLogData var1);

    public List getMerchantsByCountry(int var1, int var2, int var3, int var4, boolean var5) throws EJBException;

    public List getMerchantsByCountry(int var1, String var2, int var3, int var4, boolean var5) throws EJBException;
}

