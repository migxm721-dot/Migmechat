/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.EJBObject
 */
package com.projectgoth.fusion.interfaces;

import com.projectgoth.fusion.ejb.FusionEJBException;
import java.rmi.RemoteException;
import javax.ejb.EJBObject;

public interface Guardset
extends EJBObject {
    public Short getMinimumClientVersionForAccess(int var1, int var2) throws FusionEJBException, RemoteException;
}

