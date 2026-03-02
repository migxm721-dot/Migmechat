/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBLocalHome
 */
package com.projectgoth.fusion.interfaces;

import com.projectgoth.fusion.interfaces.MISLocal;
import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;

public interface MISLocalHome
extends EJBLocalHome {
    public static final String COMP_NAME = "java:comp/env/ejb/MISLocal";
    public static final String JNDI_NAME = "MISLocal";

    public MISLocal create() throws CreateException;
}

