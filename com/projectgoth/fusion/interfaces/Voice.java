/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.EJBObject
 */
package com.projectgoth.fusion.interfaces;

import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.data.CallData;
import java.rmi.RemoteException;
import java.util.List;
import javax.ejb.EJBObject;

public interface Voice
extends EJBObject {
    public String getDIDNumber(int var1) throws RemoteException;

    public String getFullDIDNumber(int var1) throws RemoteException;

    public CallData evaluatePhoneCall(CallData var1) throws RemoteException;

    public CallData initiatePhoneCall(CallData var1) throws RemoteException;

    public CallData getCallEntryWithCost(int var1) throws RemoteException;

    public List getCallEntries(String var1) throws RemoteException;

    public List getCallEntriesWithCost(String var1) throws RemoteException;

    public List getCallEntries(String var1, Integer var2) throws RemoteException;

    public void updateCallDetail(CallData var1) throws RemoteException;

    public void chargeCall(CallData var1, AccountEntrySourceData var2) throws RemoteException;

    public List getVoiceGateways() throws RemoteException;

    public List getVoiceGateways(int var1) throws RemoteException;
}

