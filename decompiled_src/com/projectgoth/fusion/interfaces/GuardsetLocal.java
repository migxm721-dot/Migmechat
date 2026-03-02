/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.EJBLocalObject
 */
package com.projectgoth.fusion.interfaces;

import com.projectgoth.fusion.ejb.FusionEJBException;
import javax.ejb.EJBLocalObject;

public interface GuardsetLocal
extends EJBLocalObject {
    public Short getMinimumClientVersionForAccess(int var1, int var2) throws FusionEJBException;
}

