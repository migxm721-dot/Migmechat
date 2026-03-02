/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.jdbc.core.RowMapper
 */
package com.projectgoth.fusion.dao.impl;

import com.projectgoth.fusion.dao.GroupMembershipDAO;
import com.projectgoth.fusion.dao.impl.MigJdbcDaoSupport;
import com.projectgoth.fusion.domain.UsernameAndMobileNumber;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class GroupMembershipDAOJDBC
extends MigJdbcDaoSupport
implements GroupMembershipDAO {
    @Override
    public List<String> getGroupMemberUsernamesForEmailNotification(int groupId) {
        return this.getJdbcTemplate().query(this.getExternalizedQuery("GroupMembershipDAO.getGroupMemberUsernamesForEmailNotification"), new Object[]{groupId}, (RowMapper)new MigJdbcDaoSupport.StringRowMapper("username"));
    }

    @Override
    public List<String> getGroupMemberUsernamesForEventNotification(int groupId) {
        return this.getJdbcTemplate().query(this.getExternalizedQuery("GroupMembershipDAO.getGroupMemberUsernamesForEventNotification"), new Object[]{groupId}, (RowMapper)new MigJdbcDaoSupport.StringRowMapper("username"));
    }

    @Override
    public List<UsernameAndMobileNumber> getGroupMemberUsernamesAndMobileNumbersForSMSNotification(int groupId) {
        return this.getJdbcTemplate().query(this.getExternalizedQuery("GroupMembershipDAO.getGroupMemberUsernamesAndMobileNumbersForSMSNotification"), new Object[]{groupId}, (RowMapper)new UsernameAndMobileNumberRowMapper());
    }

    @Override
    public List<UsernameAndMobileNumber> getGroupMemberUsernamesAndMobileNumbersForGroupEventSMSNotification(int groupId) {
        return this.getJdbcTemplate().query(this.getExternalizedQuery("GroupMembershipDAO.getGroupMemberUsernamesAndMobileNumbersForGroupEventSMSNotification"), new Object[]{groupId}, (RowMapper)new UsernameAndMobileNumberRowMapper());
    }

    @Override
    public List<String> getGroupMemberUsernamesForGroupEventNotification(int groupId) {
        return this.getJdbcTemplate().query(this.getExternalizedQuery("GroupMembershipDAO.getGroupMemberUsernamesForGroupEventNotification"), new Object[]{groupId}, (RowMapper)new MigJdbcDaoSupport.StringRowMapper("username"));
    }

    @Override
    public List<String> getGroupMemberUsernamesForNewGroupUserPostNotificationViaEmail(int userPostId) {
        return this.getJdbcTemplate().query(this.getExternalizedQuery("GroupMembershipDAO.getGroupMemberUsernamesForNewGroupUserPostNotificationViaEmail"), new Object[]{userPostId}, (RowMapper)new MigJdbcDaoSupport.StringRowMapper("username"));
    }

    @Override
    public List<String> getGroupMemberUsernamesForNewGroupUserPostNotificationViaEventSystem(int userPostId) {
        return this.getJdbcTemplate().query(this.getExternalizedQuery("GroupMembershipDAO.getGroupMemberUsernamesForNewGroupUserPostNotificationViaEventSystem"), new Object[]{userPostId}, (RowMapper)new MigJdbcDaoSupport.StringRowMapper("username"));
    }

    private static final class UsernameAndMobileNumberRowMapper
    implements RowMapper {
        private UsernameAndMobileNumberRowMapper() {
        }

        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            UsernameAndMobileNumber uam = new UsernameAndMobileNumber(rs.getString("username"), rs.getString("mobilephone"));
            return uam;
        }
    }
}

