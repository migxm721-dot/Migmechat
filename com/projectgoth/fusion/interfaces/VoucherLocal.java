/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.EJBException
 *  javax.ejb.EJBLocalObject
 */
package com.projectgoth.fusion.interfaces;

import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.data.BlueLabelVoucherData;
import com.projectgoth.fusion.data.VoucherBatchData;
import com.projectgoth.fusion.data.VoucherData;
import com.projectgoth.fusion.ejb.FusionEJBException;
import com.sun.rowset.CachedRowSetImpl;
import java.util.List;
import javax.ejb.EJBException;
import javax.ejb.EJBLocalObject;

public interface VoucherLocal
extends EJBLocalObject {
    public int createVoucherBatch(String var1, String var2, String var3, int var4, String var5, boolean var6, AccountEntrySourceData var7) throws EJBException, FusionEJBException;

    public VoucherBatchData getVoucherBatch(int var1) throws FusionEJBException;

    public void cancelVoucherBatch(String var1, int var2, AccountEntrySourceData var3) throws EJBException;

    public void cancelVoucher(String var1, int var2, AccountEntrySourceData var3) throws EJBException;

    public BlueLabelVoucherData redeemBlueLabelVoucher(String var1, String var2, String var3) throws EJBException;

    public VoucherData redeemVoucher(String var1, String var2, AccountEntrySourceData var3) throws EJBException;

    public void voucherRechargeFailed(String var1) throws EJBException;

    public List retrieveVouchers(String var1, int var2, int var3) throws EJBException;

    public VoucherData searchForVoucher(String var1, String var2) throws EJBException;

    public VoucherData getVoucher(String var1) throws EJBException;

    public List retrieveVoucherBatches(String var1, int var2) throws EJBException;

    public VoucherData activateVoucher(String var1) throws EJBException;

    public CachedRowSetImpl affiliateOverview(String var1) throws EJBException;

    public CachedRowSetImpl recentActivities(String var1, int var2) throws EJBException;

    public int recentRedeem(String var1, int var2) throws EJBException;
}

