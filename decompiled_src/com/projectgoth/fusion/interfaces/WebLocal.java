/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.EJBException
 *  javax.ejb.EJBLocalObject
 */
package com.projectgoth.fusion.interfaces;

import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.data.UserPostData;
import java.util.Vector;
import javax.ejb.EJBException;
import javax.ejb.EJBLocalObject;

public interface WebLocal
extends EJBLocalObject {
    public Vector getUsersWhoViewed(String var1) throws EJBException;

    public String cancelVoucherBatch(String var1, int var2, String var3, String var4, String var5, String var6) throws EJBException;

    public Vector getHandsetVendors() throws EJBException;

    public Vector getHandsetVendorPrefixes() throws EJBException;

    public boolean processILoopAPICall(String var1, String var2, String var3, String var4, String var5, String var6, String var7) throws EJBException;

    public boolean processOplayoAPICall(int var1, int var2, int var3, int var4, int var5) throws EJBException;

    public Vector getCountriesWithMerchants() throws EJBException;

    public boolean joinGroupWithoutValidation(UserData var1, int var2, int var3, String var4, String var5, String var6, String var7, boolean var8, boolean var9, boolean var10, boolean var11, boolean var12, boolean var13) throws EJBException;

    public String inviteUserToGroup(String var1, String var2, int var3) throws EJBException;

    public UserPostData createGroupUserPostTransaction(int var1, UserPostData var2) throws Exception;
}

