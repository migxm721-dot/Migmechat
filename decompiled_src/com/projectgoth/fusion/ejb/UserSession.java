/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.EJBException
 *  javax.ejb.SessionBean
 *  javax.ejb.SessionContext
 */
package com.projectgoth.fusion.ejb;

import com.projectgoth.fusion.ejb.UserBean;
import java.rmi.RemoteException;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

public class UserSession
extends UserBean
implements SessionBean {
    public void ejbActivate() throws EJBException, RemoteException {
        super.ejbActivate();
    }

    public void ejbPassivate() throws EJBException, RemoteException {
        super.ejbPassivate();
    }

    public void setSessionContext(SessionContext ctx) throws EJBException {
        super.setSessionContext(ctx);
    }

    public void unsetSessionContext() {
    }

    public void ejbRemove() throws EJBException, RemoteException {
        super.ejbRemove();
    }
}

