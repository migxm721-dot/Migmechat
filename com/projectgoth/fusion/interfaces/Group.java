/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.EJBObject
 */
package com.projectgoth.fusion.interfaces;

import com.projectgoth.fusion.common.EJBExceptionWithErrorCause;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;
import javax.ejb.EJBObject;

public interface Group
extends EJBObject {
    public void giveGroupMemberModeratorRights(String var1, int var2, String var3) throws EJBExceptionWithErrorCause, RemoteException;

    public void removeGroupMemberModeratorRights(String var1, int var2, String var3) throws EJBExceptionWithErrorCause, RemoteException;

    public int getModeratorCount(int var1, boolean var2) throws RemoteException;

    public Set getModeratorUserNames(int var1, boolean var2) throws RemoteException;

    public List getGroupMembers(int var1) throws RemoteException;
}

