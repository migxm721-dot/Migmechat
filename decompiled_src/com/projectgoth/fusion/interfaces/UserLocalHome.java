/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBLocalHome
 */
package com.projectgoth.fusion.interfaces;

import com.projectgoth.fusion.interfaces.UserLocal;
import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;

public interface UserLocalHome
extends EJBLocalHome {
    public static final String COMP_NAME = "java:comp/env/ejb/UserLocal";
    public static final String JNDI_NAME = "UserLocal";

    public UserLocal create() throws CreateException;
}

