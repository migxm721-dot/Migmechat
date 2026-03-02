/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.app.dao.db.ejb;

import com.projectgoth.fusion.app.dao.DAOException;
import com.projectgoth.fusion.app.dao.base.GroupDAOChain;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.GroupData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Group;
import com.projectgoth.fusion.interfaces.GroupHome;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import java.util.Set;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class EJBGroupDAOChain
extends GroupDAOChain {
    private static final Logger log = Logger.getLogger(EJBGroupDAOChain.class);

    @Override
    public Set<String> getModeratorUserNames(int groupId, boolean fromMasterDB) throws DAOException {
        if (!SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_EJB_NODE_GETMODERATORUSERNAMES)) {
            return super.getModeratorUserNames(groupId, fromMasterDB);
        }
        try {
            Group groupEJB = (Group)EJBHomeCache.getObject("ejb/Group", GroupHome.class);
            return groupEJB.getModeratorUserNames(groupId, fromMasterDB);
        }
        catch (Exception e) {
            log.warn((Object)String.format("Failed to get ModeratorUserNames for group:%s, fromMasterDB:%s", groupId, fromMasterDB), (Throwable)e);
            return super.getModeratorUserNames(groupId, fromMasterDB);
        }
    }

    @Override
    public GroupData getGroup(int groupID) throws DAOException {
        if (!SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_EJB_NODE_GETGROUPDATA)) {
            return super.getGroup(groupID);
        }
        try {
            User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
            return userEJB.getGroup(groupID);
        }
        catch (Exception e) {
            log.warn((Object)String.format("Failed to get GroupData for groupid:%s", groupID), (Throwable)e);
            return super.getGroup(groupID);
        }
    }
}

