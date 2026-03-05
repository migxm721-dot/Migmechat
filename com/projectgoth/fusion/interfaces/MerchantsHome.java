/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBHome
 */
package com.projectgoth.fusion.interfaces;

import com.projectgoth.fusion.interfaces.Merchants;
import java.rmi.RemoteException;
import javax.ejb.CreateException;
import javax.ejb.EJBHome;

public interface MerchantsHome
extends EJBHome {
    public static final String COMP_NAME = "java:comp/env/ejb/Merchants";
    public static final String JNDI_NAME = "ejb/Merchants";

    public Merchants create() throws CreateException, RemoteException;
}

