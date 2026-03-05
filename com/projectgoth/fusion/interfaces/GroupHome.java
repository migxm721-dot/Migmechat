/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBHome
 */
package com.projectgoth.fusion.interfaces;

import com.projectgoth.fusion.interfaces.Group;
import java.rmi.RemoteException;
import javax.ejb.CreateException;
import javax.ejb.EJBHome;

public interface GroupHome
extends EJBHome {
    public static final String COMP_NAME = "java:comp/env/ejb/Group";
    public static final String JNDI_NAME = "ejb/Group";

    public Group create() throws CreateException, RemoteException;
}

