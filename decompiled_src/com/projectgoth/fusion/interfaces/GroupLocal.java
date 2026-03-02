/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.EJBException
 *  javax.ejb.EJBLocalObject
 */
package com.projectgoth.fusion.interfaces;

import com.projectgoth.fusion.common.EJBExceptionWithErrorCause;
import java.util.List;
import java.util.Set;
import javax.ejb.EJBException;
import javax.ejb.EJBLocalObject;

public interface GroupLocal
extends EJBLocalObject {
    public void giveGroupMemberModeratorRights(String var1, int var2, String var3) throws EJBExceptionWithErrorCause, EJBException;

    public void removeGroupMemberModeratorRights(String var1, int var2, String var3) throws EJBExceptionWithErrorCause, EJBException;

    public int getModeratorCount(int var1, boolean var2);

    public Set getModeratorUserNames(int var1, boolean var2);

    public List getGroupMembers(int var1) throws EJBException;
}

