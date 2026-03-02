/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBLocalHome
 */
package com.projectgoth.fusion.interfaces;

import com.projectgoth.fusion.interfaces.MerchantsLocal;
import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;

public interface MerchantsLocalHome
extends EJBLocalHome {
    public static final String COMP_NAME = "java:comp/env/ejb/MerchantsLocal";
    public static final String JNDI_NAME = "MerchantsLocal";

    public MerchantsLocal create() throws CreateException;
}

