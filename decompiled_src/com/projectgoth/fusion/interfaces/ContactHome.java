/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBHome
 */
package com.projectgoth.fusion.interfaces;

import com.projectgoth.fusion.interfaces.Contact;
import java.rmi.RemoteException;
import javax.ejb.CreateException;
import javax.ejb.EJBHome;

public interface ContactHome
extends EJBHome {
    public static final String COMP_NAME = "java:comp/env/ejb/Contact";
    public static final String JNDI_NAME = "ejb/Contact";

    public Contact create() throws CreateException, RemoteException;
}

