/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBException
 *  javax.ejb.SessionBean
 *  javax.ejb.SessionContext
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.ejb;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.EJBExceptionWithErrorCause;
import com.projectgoth.fusion.common.ErrorCause;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.GroupData;
import com.projectgoth.fusion.data.GroupMemberData;
import com.projectgoth.fusion.data.ReputationLevelData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.ejb.EJBIcePrxFinder;
import com.projectgoth.fusion.ejb.util.LookupUtil;
import com.projectgoth.fusion.interfaces.MessageLocal;
import com.projectgoth.fusion.interfaces.MessageLocalHome;
import com.projectgoth.fusion.interfaces.UserLocal;
import com.projectgoth.fusion.interfaces.UserLocalHome;
import com.projectgoth.fusion.slice.ChatRoomPrx;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.sql.DataSource;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class GroupBean
implements SessionBean {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(GroupBean.class));
    private DataSource dataSourceMaster;
    private DataSource dataSourceSlave;
    private DataSource userRegistrationMaster;
    private DataSource userRegistrationSlave;
    private SessionContext context;

    public void ejbActivate() throws EJBException, RemoteException {
    }

    public void ejbPassivate() throws EJBException, RemoteException {
    }

    public void ejbRemove() throws EJBException, RemoteException {
    }

    public void setSessionContext(SessionContext newContext) throws EJBException, RemoteException {
        this.context = newContext;
    }

    public void ejbCreate() throws CreateException {
        try {
            this.dataSourceMaster = LookupUtil.getFusionMasterDataSource();
            this.dataSourceSlave = LookupUtil.getFusionSlaveDataSource();
            this.userRegistrationMaster = LookupUtil.getRegistrationMasterDataSource();
            this.userRegistrationSlave = LookupUtil.getRegistrationSlaveDataSource();
            SystemProperty.ejbInit(this.dataSourceSlave);
        }
        catch (Throwable e) {
            CreateException crex = new CreateException("Unable to create User EJB: " + e.getMessage());
            crex.initCause(e);
            throw crex;
        }
    }

    public void giveGroupMemberModeratorRights(String groupCreatorUserName, int groupID, String groupMemberUserName) throws EJBExceptionWithErrorCause, EJBException {
        try {
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            GroupData groupData = userEJB.getGroup(groupID);
            if (groupData == null) {
                throw new EJBExceptionWithErrorCause(ErrorCause.GroupErrorType.INVALID_GROUP, "group");
            }
            if (!groupData.createdBy.equalsIgnoreCase(groupCreatorUserName)) {
                throw new EJBExceptionWithErrorCause(ErrorCause.GroupErrorType.INCORRECT_GROUP_CREATOR, groupCreatorUserName, groupData.name);
            }
            UserData groupMemberUserData = userEJB.loadUser(groupMemberUserName, false, false);
            if (groupMemberUserData == null) {
                throw new EJBExceptionWithErrorCause(ErrorCause.GroupErrorType.INVALID_USER, "group member " + groupMemberUserName);
            }
            ReputationLevelData groupMemberReputation = userEJB.getReputationLevelByUserid(groupMemberUserData.userID, true);
            if (groupMemberReputation == null) {
                throw new EJBExceptionWithErrorCause(ErrorCause.GroupErrorType.FAIL_LOADING_REPUTATION_DATA, " group member " + groupMemberUserName);
            }
            int moderatorMinUserLevel = SystemProperty.getInt(SystemPropertyEntities.Group.MODERATOR_MIN_USER_LEVEL);
            if (groupMemberReputation.level < moderatorMinUserLevel) {
                throw new EJBExceptionWithErrorCause(ErrorCause.GroupErrorType.INCORRECT_TARGET_USER_LEVEL, groupMemberUserName, moderatorMinUserLevel);
            }
            UserData groupCreatorUserData = userEJB.loadUser(groupCreatorUserName, false, false);
            if (groupCreatorUserData == null) {
                throw new EJBExceptionWithErrorCause(ErrorCause.GroupErrorType.INVALID_USER, "group creator " + groupCreatorUserData);
            }
            ReputationLevelData groupCreatorReputation = userEJB.getReputationLevelByUserid(groupCreatorUserData.userID, true);
            if (groupCreatorReputation == null) {
                throw new EJBExceptionWithErrorCause(ErrorCause.GroupErrorType.FAIL_LOADING_REPUTATION_DATA, " group creator " + groupCreatorUserName);
            }
            int maxNumModerator = groupCreatorReputation.numGroupModerators;
            if (this.retrieveModeratorCount(groupID, false) >= maxNumModerator) {
                throw new EJBExceptionWithErrorCause(ErrorCause.GroupErrorType.MAX_MODERATOR_COUNT_REACHED, groupCreatorUserName, maxNumModerator, groupData.name);
            }
            this.updateGroupMemberModeratorStatus(groupData, groupMemberUserName, true);
        }
        catch (EJBException ex) {
            throw ex;
        }
        catch (SQLException ex) {
            throw new EJBExceptionWithErrorCause(ex, ErrorCause.GroupErrorType.INTERNAL_ERROR, "Database access error.");
        }
        catch (Exception ex) {
            throw new EJBExceptionWithErrorCause(ex, ErrorCause.GroupErrorType.INTERNAL_ERROR, "Failed to give moderator rights." + ex.getMessage());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void updateGroupMemberModeratorStatus(GroupData groupData, String groupMemberUserName, boolean promote) throws SQLException, CreateException {
        Connection conn = this.dataSourceMaster.getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(" UPDATE groupmember, groups  SET groupmember.type = ?  WHERE groupmember.groupid = groups.id  AND groupmember.status = ?  AND groupmember.username = ?  AND groups.id = ?  AND groups.createdby = ?");
            try {
                ps.setInt(1, promote ? GroupMemberData.TypeEnum.MODERATOR.value() : GroupMemberData.TypeEnum.REGULAR.value());
                ps.setInt(2, GroupMemberData.StatusEnum.ACTIVE.value());
                ps.setString(3, groupMemberUserName);
                ps.setInt(4, groupData.id);
                ps.setString(5, groupData.createdBy);
                if (ps.executeUpdate() != 1) {
                    throw new EJBExceptionWithErrorCause(ErrorCause.GroupErrorType.MANAGE_RIGHTS_ERROR, promote ? "Failed to grant moderator rights to '" + groupMemberUserName + "' on group '" + groupData.name + "'" : "Failed to revoke moderator rights from '" + groupMemberUserName + "' on group '" + groupData.name + "'");
                }
                Object var7_7 = null;
            }
            catch (Throwable throwable) {
                Object var7_8 = null;
                ps.close();
                throw throwable;
            }
            ps.close();
            Object var9_10 = null;
        }
        catch (Throwable throwable) {
            Object var9_11 = null;
            conn.close();
            throw throwable;
        }
        conn.close();
        try {
            this.updateChatroomParticipantModeratorStatus(groupData.id, groupMemberUserName, promote);
        }
        catch (Exception ex) {
            log.error((Object)("Unable to update chatroom participant moderator status for group " + groupData.id + " member:" + groupMemberUserName + " promote:" + promote + "."), (Throwable)ex);
        }
    }

    public void removeGroupMemberModeratorRights(String groupCreatorUserName, int groupID, String groupMemberUserName) throws EJBExceptionWithErrorCause, EJBException {
        try {
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            GroupData groupData = userEJB.getGroup(groupID);
            if (groupData == null) {
                throw new EJBExceptionWithErrorCause(ErrorCause.GroupErrorType.INVALID_GROUP, "group");
            }
            if (!groupData.createdBy.equalsIgnoreCase(groupCreatorUserName)) {
                throw new EJBExceptionWithErrorCause(ErrorCause.GroupErrorType.INCORRECT_GROUP_CREATOR, groupCreatorUserName, groupData.name);
            }
            UserData groupMemberUserData = userEJB.loadUser(groupMemberUserName, false, false);
            if (groupMemberUserData == null) {
                throw new EJBExceptionWithErrorCause(ErrorCause.GroupErrorType.INVALID_USER, "group member " + groupMemberUserName);
            }
            UserData groupCreatorUserData = userEJB.loadUser(groupCreatorUserName, false, false);
            if (groupCreatorUserData == null) {
                throw new EJBExceptionWithErrorCause(ErrorCause.GroupErrorType.INVALID_USER, "group creator " + groupCreatorUserData);
            }
            if (groupCreatorUserData.userID.equals(groupMemberUserData.userID)) {
                throw new EJBExceptionWithErrorCause(ErrorCause.GroupErrorType.SELF_DEMOTE, new Object[0]);
            }
            this.updateGroupMemberModeratorStatus(groupData, groupMemberUserName, false);
        }
        catch (EJBException ex) {
            throw ex;
        }
        catch (SQLException ex) {
            throw new EJBExceptionWithErrorCause(ex, ErrorCause.GroupErrorType.INTERNAL_ERROR, "Database access error.");
        }
        catch (Exception ex) {
            throw new EJBExceptionWithErrorCause(ex, ErrorCause.GroupErrorType.INTERNAL_ERROR, "Failed to give moderator rights." + ex.getMessage());
        }
    }

    public int getModeratorCount(int groupId, boolean fromMasterDB) {
        if (!SystemProperty.getBool(SystemPropertyEntities.Default.VIEW_GROUP_MODERATORS_ENABLED)) {
            return 0;
        }
        try {
            return this.retrieveModeratorCount(groupId, fromMasterDB);
        }
        catch (SQLException sqlEx) {
            throw new EJBExceptionWithErrorCause(sqlEx, ErrorCause.GroupErrorType.INTERNAL_ERROR, sqlEx.getMessage());
        }
    }

    /*
     * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private int retrieveModeratorCount(int groupId, boolean fromMasterDB) throws SQLException {
        int n;
        Connection conn = fromMasterDB ? this.dataSourceMaster.getConnection() : this.dataSourceSlave.getConnection();
        try {
            ResultSet rs;
            PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) as modCount FROM groupmember  WHERE groupid = ?  AND status = ?  AND type = ?");
            try {
                ps.setInt(1, groupId);
                ps.setInt(2, GroupMemberData.StatusEnum.ACTIVE.value());
                ps.setInt(3, GroupMemberData.TypeEnum.MODERATOR.value());
                rs = ps.executeQuery();
                try {
                    if (!rs.next()) throw new EJBExceptionWithErrorCause(ErrorCause.GroupErrorType.INTERNAL_ERROR, "Failed to retrieve result for groupid " + groupId);
                    n = rs.getInt("modCount");
                    Object var8_7 = null;
                }
                catch (Throwable throwable) {
                    Object var8_8 = null;
                    rs.close();
                    throw throwable;
                }
            }
            catch (Throwable throwable) {
                Object var10_10 = null;
                ps.close();
                throw throwable;
            }
            rs.close();
            Object var10_9 = null;
            ps.close();
            Object var12_11 = null;
        }
        catch (Throwable throwable) {
            Object var12_12 = null;
            conn.close();
            throw throwable;
        }
        conn.close();
        return n;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Set<String> getModeratorUserNames(int groupId, boolean fromMasterDB) {
        if (!SystemProperty.getBool(SystemPropertyEntities.Default.VIEW_GROUP_MODERATORS_ENABLED)) {
            return new HashSet<String>(0);
        }
        TreeSet<String> result = new TreeSet<String>();
        try {
            Connection conn = fromMasterDB ? this.dataSourceMaster.getConnection() : this.dataSourceSlave.getConnection();
            try {
                PreparedStatement ps = conn.prepareStatement("SELECT gm.username as username FROM groupmember gm WHERE gm.groupid = ?  AND gm.status = ?  AND gm.type = ?");
                try {
                    ps.setInt(1, groupId);
                    ps.setInt(2, GroupMemberData.StatusEnum.ACTIVE.value());
                    ps.setInt(3, GroupMemberData.TypeEnum.MODERATOR.value());
                    ResultSet rs = ps.executeQuery();
                    try {
                        while (rs.next()) {
                            result.add(rs.getString("username"));
                        }
                        Object var8_8 = null;
                    }
                    catch (Throwable throwable) {
                        Object var8_9 = null;
                        rs.close();
                        throw throwable;
                    }
                    rs.close();
                    Object var10_11 = null;
                }
                catch (Throwable throwable) {
                    Object var10_12 = null;
                    ps.close();
                    throw throwable;
                }
                ps.close();
                Object var12_14 = null;
            }
            catch (Throwable throwable) {
                Object var12_15 = null;
                conn.close();
                throw throwable;
            }
            conn.close();
            {
            }
        }
        catch (SQLException sqlEx) {
            throw new EJBExceptionWithErrorCause(sqlEx, ErrorCause.GroupErrorType.INTERNAL_ERROR, sqlEx.getMessage());
        }
        return result;
    }

    private int updateChatroomParticipantModeratorStatus(int groupid, String username, boolean promote) throws CreateException {
        MessageLocal messageEJB = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
        String[] chatrooms = messageEJB.getGroupChatRooms(groupid);
        ChatRoomPrx[] chatRoomProxies = null;
        if (chatrooms != null && chatrooms.length > 0) {
            chatRoomProxies = EJBIcePrxFinder.findChatRoomProxies(chatrooms);
        }
        if (log.isDebugEnabled()) {
            if (chatRoomProxies != null) {
                log.debug((Object)("For group:" + groupid + " ,user[" + username + "] is in " + chatRoomProxies.length + " chatrooms"));
            } else {
                log.debug((Object)("For group:" + groupid + " ,user[" + username + "] :active chatroomproxies is null"));
            }
        }
        int chatRoomCount = 0;
        if (chatRoomProxies != null) {
            for (ChatRoomPrx chatroomPrx : chatRoomProxies) {
                if (chatroomPrx == null) continue;
                try {
                    chatroomPrx.updateGroupModeratorStatus(username, promote);
                    ++chatRoomCount;
                }
                catch (Exception ex) {
                    log.error((Object)("Unable to update group moderator status for username " + username + " under group " + groupid + " promote:" + promote + "."), (Throwable)ex);
                }
            }
        }
        return chatRoomCount;
    }

    /*
     * Loose catch block
     */
    public List<Integer> getGroupMembers(int groupId) throws EJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Integer> members = new ArrayList<Integer>();
        conn = this.dataSourceSlave.getConnection();
        ps = conn.prepareStatement("SELECT userid.id FROM userid JOIN groupmember USING(username) WHERE groupmember.groupid=? AND status=1");
        ps.setInt(1, groupId);
        rs = ps.executeQuery();
        while (rs.next()) {
            int userId = rs.getInt("id");
            members.add(userId);
        }
        ArrayList<Integer> arrayList = members;
        Object var8_9 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return arrayList;
        catch (SQLException e) {
            try {
                throw new EJBException("SQLException", (Exception)e);
            }
            catch (Throwable throwable) {
                Object var8_10 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }
}

