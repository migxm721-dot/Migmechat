/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.EJBObject
 */
package com.projectgoth.fusion.interfaces;

import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.data.BlueLabelVoucherData;
import com.projectgoth.fusion.data.VoucherBatchData;
import com.projectgoth.fusion.data.VoucherData;
import com.projectgoth.fusion.ejb.FusionEJBException;
import com.sun.rowset.CachedRowSetImpl;
import java.rmi.RemoteException;
import java.util.List;
import javax.ejb.EJBObject;

public interface Voucher
extends EJBObject {
    public int createVoucherBatch(String var1, String var2, String var3, int var4, String var5, boolean var6, AccountEntrySourceData var7) throws FusionEJBException, RemoteException;

    public VoucherBatchData getVoucherBatch(int var1) throws FusionEJBException, RemoteException;

    public void cancelVoucherBatch(String var1, int var2, AccountEntrySourceData var3) throws RemoteException;

    public void cancelVoucher(String var1, int var2, AccountEntrySourceData var3) throws RemoteException;

    public BlueLabelVoucherData redeemBlueLabelVoucher(String var1, String var2, String var3) throws RemoteException;

    public VoucherData redeemVoucher(String var1, String var2, AccountEntrySourceData var3) throws RemoteException;

    public void voucherRechargeFailed(String var1) throws RemoteException;

    public List retrieveVouchers(String var1, int var2, int var3) throws RemoteException;

    public VoucherData searchForVoucher(String var1, String var2) throws RemoteException;

    public VoucherData getVoucher(String var1) throws RemoteException;

    public List retrieveVoucherBatches(String var1, int var2) throws RemoteException;

    public VoucherData activateVoucher(String var1) throws RemoteException;

    public CachedRowSetImpl affiliateOverview(String var1) throws RemoteException;

    public CachedRowSetImpl recentActivities(String var1, int var2) throws RemoteException;

    public int recentRedeem(String var1, int var2) throws RemoteException;
}

