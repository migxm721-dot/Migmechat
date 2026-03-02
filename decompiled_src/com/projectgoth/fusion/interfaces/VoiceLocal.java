/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.EJBException
 *  javax.ejb.EJBLocalObject
 */
package com.projectgoth.fusion.interfaces;

import com.projectgoth.fusion.data.CallData;
import java.util.List;
import javax.ejb.EJBException;
import javax.ejb.EJBLocalObject;

public interface VoiceLocal
extends EJBLocalObject {
    public String getDIDNumber(int var1) throws EJBException;

    public String getFullDIDNumber(int var1) throws EJBException;

    public CallData evaluatePhoneCall(CallData var1) throws EJBException;

    public CallData initiatePhoneCall(CallData var1) throws EJBException;

    public CallData getCallEntryWithCost(int var1) throws EJBException;

    public List getCallEntries(String var1) throws EJBException;

    public List getCallEntriesWithCost(String var1) throws EJBException;

    public List getCallEntries(String var1, Integer var2) throws EJBException;
}

