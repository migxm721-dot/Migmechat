/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.app.dao.base;

import com.projectgoth.fusion.app.dao.DAOException;
import com.projectgoth.fusion.app.dao.base.DAOChain;
import com.projectgoth.fusion.data.GroupData;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class GroupDAOChain
implements DAOChain {
    private GroupDAOChain nextRead;
    private GroupDAOChain nextWrite;

    @Override
    public void setNextRead(DAOChain a) {
        this.nextRead = (GroupDAOChain)a;
    }

    @Override
    public void setNextWrite(DAOChain a) {
        this.nextWrite = (GroupDAOChain)a;
    }

    public Set<String> getModeratorUserNames(int groupId, boolean fromMasterDB) throws DAOException {
        if (this.nextRead != null) {
            return this.nextRead.getModeratorUserNames(groupId, fromMasterDB);
        }
        throw new DAOException(String.format("Unable to get ModeratorUserNames for groupid:%s, fromMasterDB:%s", groupId, fromMasterDB));
    }

    public GroupData getGroup(int groupID) throws DAOException {
        if (this.nextRead != null) {
            return this.nextRead.getGroup(groupID);
        }
        throw new DAOException(String.format("Unable to retrieve group data for group id:%s", groupID));
    }
}

