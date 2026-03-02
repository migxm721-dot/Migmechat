/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBHome
 */
package com.projectgoth.fusion.interfaces;

import com.projectgoth.fusion.interfaces.Account;
import java.rmi.RemoteException;
import javax.ejb.CreateException;
import javax.ejb.EJBHome;

public interface AccountHome
extends EJBHome {
    public static final String COMP_NAME = "java:comp/env/ejb/Account";
    public static final String JNDI_NAME = "ejb/Account";

    public Account create() throws CreateException, RemoteException;
}

