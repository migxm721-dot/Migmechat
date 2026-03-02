/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBHome
 */
package com.projectgoth.fusion.interfaces;

import com.projectgoth.fusion.interfaces.User;
import java.rmi.RemoteException;
import javax.ejb.CreateException;
import javax.ejb.EJBHome;

public interface UserHome
extends EJBHome {
    public static final String COMP_NAME = "java:comp/env/ejb/User";
    public static final String JNDI_NAME = "ejb/User";

    public User create() throws CreateException, RemoteException;
}

