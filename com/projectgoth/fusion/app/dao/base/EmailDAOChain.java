/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.app.dao.base;

import com.projectgoth.fusion.app.dao.DAOException;
import com.projectgoth.fusion.app.dao.base.DAOChain;

public class EmailDAOChain
implements DAOChain {
    private EmailDAOChain nextRead;
    private EmailDAOChain nextWrite;

    public void setNextRead(DAOChain nextRead) {
        this.nextRead = (EmailDAOChain)nextRead;
    }

    public void setNextWrite(DAOChain nextWrite) {
        this.nextWrite = (EmailDAOChain)nextWrite;
    }

    public boolean isBounceEmailAddress(String email) throws DAOException {
        if (this.nextRead != null) {
            return this.nextRead.isBounceEmailAddress(email);
        }
        throw new DAOException(String.format("Failed to check isBounceEmailAddress for email:%s", email));
    }
}

