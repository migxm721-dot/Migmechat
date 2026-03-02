/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBLocalHome
 */
package com.projectgoth.fusion.interfaces;

import com.projectgoth.fusion.interfaces.AccountLocal;
import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;

public interface AccountLocalHome
extends EJBLocalHome {
    public static final String COMP_NAME = "java:comp/env/ejb/AccountLocal";
    public static final String JNDI_NAME = "AccountLocal";

    public AccountLocal create() throws CreateException;
}

