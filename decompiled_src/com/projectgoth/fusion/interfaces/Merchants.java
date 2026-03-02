/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.EJBObject
 */
package com.projectgoth.fusion.interfaces;

import com.projectgoth.fusion.merchant.MerchantPointsLogData;
import java.rmi.RemoteException;
import java.util.List;
import javax.ejb.EJBObject;

public interface Merchants
extends EJBObject {
    public MerchantPointsLogData insertMerchantPoints(MerchantPointsLogData var1) throws RemoteException;

    public List getMerchantsByCountry(int var1, int var2, int var3, int var4, boolean var5) throws RemoteException;

    public List getMerchantsByCountry(int var1, String var2, int var3, int var4, boolean var5) throws RemoteException;
}

