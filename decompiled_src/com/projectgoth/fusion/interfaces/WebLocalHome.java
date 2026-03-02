/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBLocalHome
 */
package com.projectgoth.fusion.interfaces;

import com.projectgoth.fusion.interfaces.WebLocal;
import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;

public interface WebLocalHome
extends EJBLocalHome {
    public static final String COMP_NAME = "java:comp/env/ejb/WebLocal";
    public static final String JNDI_NAME = "WebLocal";

    public WebLocal create() throws CreateException;
}

