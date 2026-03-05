/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.app.dao.base;

import com.projectgoth.fusion.app.dao.DAOException;
import com.projectgoth.fusion.app.dao.base.DAOChain;

public class GuardsetDAOChain
implements DAOChain {
    public static final short VERSION_NOT_EXIST = Short.MAX_VALUE;
    private GuardsetDAOChain nextRead;
    private GuardsetDAOChain nextWrite;

    public void setNextRead(DAOChain a) {
        this.nextRead = (GuardsetDAOChain)a;
    }

    public void setNextWrite(DAOChain a) {
        this.nextWrite = (GuardsetDAOChain)a;
    }

    public Short getMinimumClientVersionForAccess(int clientType, int guardCapability) throws DAOException {
        if (this.nextRead != null) {
            return this.nextRead.getMinimumClientVersionForAccess(clientType, guardCapability);
        }
        throw new DAOException("Unable to getMinimumClientVersionForAccess");
    }
}

