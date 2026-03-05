/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBHome
 */
package com.projectgoth.fusion.interfaces;

import com.projectgoth.fusion.interfaces.Rewards;
import java.rmi.RemoteException;
import javax.ejb.CreateException;
import javax.ejb.EJBHome;

public interface RewardsHome
extends EJBHome {
    public static final String COMP_NAME = "java:comp/env/ejb/Rewards";
    public static final String JNDI_NAME = "ejb/Rewards";

    public Rewards create() throws CreateException, RemoteException;
}

