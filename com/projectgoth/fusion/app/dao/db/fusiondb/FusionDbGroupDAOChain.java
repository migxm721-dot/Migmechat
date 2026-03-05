/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.app.dao.db.fusiondb;

import com.projectgoth.fusion.app.dao.DAOException;
import com.projectgoth.fusion.app.dao.base.GroupDAOChain;
import com.projectgoth.fusion.app.dao.db.fusiondb.DBUtils;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.GroupData;
import com.projectgoth.fusion.data.GroupMemberData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FusionDbGroupDAOChain
extends GroupDAOChain {
    private static final Logger log = Logger.getLogger(FusionDbGroupDAOChain.class);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Set<String> getModeratorUserNames(int groupId, boolean fromMasterDB) throws DAOException {
        TreeSet<String> treeSet;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = fromMasterDB ? DBUtils.getFusionWriteConnection() : DBUtils.getFusionReadConnection();
            ps = conn.prepareStatement("SELECT gm.username as username FROM groupmember gm WHERE gm.groupid = ?  AND gm.status = ?  AND gm.type = ?");
            ps.setInt(1, groupId);
            ps.setInt(2, GroupMemberData.StatusEnum.ACTIVE.value());
            ps.setInt(3, GroupMemberData.TypeEnum.MODERATOR.value());
            rs = ps.executeQuery();
            TreeSet<String> result = new TreeSet<String>();
            while (rs.next()) {
                result.add(rs.getString("username"));
            }
            treeSet = result;
            Object var9_10 = null;
        }
        catch (SQLException e) {
            Set<String> set;
            try {
                log.error((Object)String.format("Failed to get ModeratorUserNames for user:%s, fromMasterDB:%s", groupId, fromMasterDB));
                set = super.getModeratorUserNames(groupId, fromMasterDB);
                Object var9_11 = null;
            }
            catch (Throwable throwable) {
                Object var9_12 = null;
                DBUtils.closeResource(rs, ps, conn, log);
                throw throwable;
            }
            DBUtils.closeResource(rs, ps, conn, log);
            return set;
        }
        DBUtils.closeResource(rs, ps, conn, log);
        return treeSet;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public GroupData getGroup(int groupID) throws DAOException {
        GroupData groupData;
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block8: {
            GroupData groupData2;
            block7: {
                conn = null;
                ps = null;
                rs = null;
                try {
                    try {
                        conn = DBUtils.getFusionReadConnection();
                        ps = conn.prepareStatement("select groups.*, service.status vipservicestatus from groups LEFT OUTER JOIN service ON (groups.vipserviceid=service.id and service.status=1) where groups.id=? and groups.status=1");
                        ps.setInt(1, groupID);
                        rs = ps.executeQuery();
                        if (!rs.next()) {
                            log.warn((Object)String.format("FIXME: Failed to get group data for groupid:%s in fusion database", groupID));
                            if (!SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_CHAIN_ON_IF_SHOULD_NOT_REACH_HERE_HAPPENS)) {
                                throw new DAOException(String.format("Failed to find group data for groupid:%s in fusion database", groupID));
                            }
                            groupData2 = super.getGroup(groupID);
                            Object var8_8 = null;
                            break block7;
                        }
                        GroupData groupData3 = new GroupData(rs);
                        rs.close();
                        ps.close();
                        groupData = groupData3;
                        break block8;
                    }
                    catch (SQLException e) {
                        log.error((Object)String.format("Failed to get group data for groupid:%s", groupID), (Throwable)e);
                        GroupData groupData4 = super.getGroup(groupID);
                        Object var8_10 = null;
                        DBUtils.closeResource(rs, ps, conn, log);
                        return groupData4;
                    }
                }
                catch (Throwable throwable) {
                    Object var8_11 = null;
                    DBUtils.closeResource(rs, ps, conn, log);
                    throw throwable;
                }
            }
            DBUtils.closeResource(rs, ps, conn, log);
            return groupData2;
        }
        Object var8_9 = null;
        DBUtils.closeResource(rs, ps, conn, log);
        return groupData;
    }
}

