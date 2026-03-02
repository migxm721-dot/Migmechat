/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBHome
 */
package com.projectgoth.fusion.interfaces;

import com.projectgoth.fusion.interfaces.Content;
import java.rmi.RemoteException;
import javax.ejb.CreateException;
import javax.ejb.EJBHome;

public interface ContentHome
extends EJBHome {
    public static final String COMP_NAME = "java:comp/env/ejb/Content";
    public static final String JNDI_NAME = "ejb/Content";

    public Content create() throws CreateException, RemoteException;
}

