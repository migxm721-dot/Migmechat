/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.springframework.jdbc.core.RowMapper
 */
package com.projectgoth.fusion.dao.impl;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.MemCachedClientWrapper;
import com.projectgoth.fusion.common.MemCachedKeySpaces;
import com.projectgoth.fusion.dao.UserProfileStatusDAO;
import com.projectgoth.fusion.dao.impl.MigJdbcDaoSupport;
import com.projectgoth.fusion.data.UserProfileData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;

public class UserProfileStatusDAOJDBC
extends MigJdbcDaoSupport
implements UserProfileStatusDAO {
    private Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(UserProfileStatusDAOJDBC.class));

    public UserProfileData.StatusEnum getUserProfileStatus(String username) {
        try {
            UserProfileData.StatusEnum status = (UserProfileData.StatusEnum)((Object)MemCachedClientWrapper.get(MemCachedKeySpaces.CommonKeySpace.USER_PROFILE_STATUS, username));
            if (status != null) {
                this.log.debug((Object)"found a UserProfileStatus in memcached");
                return status;
            }
            status = this.loadUserProfileStatusFromDB(username);
            if (status != null) {
                this.log.debug((Object)"found a UserProfileStatus in the DB");
                MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.USER_PROFILE_STATUS, username, (Object)status);
                return status;
            }
        }
        catch (Exception ex) {
            this.log.error((Object)"failed to load the UserProfileStatus", (Throwable)ex);
        }
        return UserProfileData.StatusEnum.PUBLIC;
    }

    private UserProfileData.StatusEnum loadUserProfileStatusFromDB(String username) {
        List status = this.getJdbcTemplate().query(this.getExternalizedQuery("UserProfileStatusDAO.getUserProfileStatus"), new Object[]{username}, (RowMapper)new UserProfileStatusRowMapper());
        if (!status.isEmpty()) {
            return (UserProfileData.StatusEnum)((Object)status.get(0));
        }
        return null;
    }

    public static final class UserProfileStatusRowMapper
    implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            return UserProfileData.StatusEnum.fromValue(rs.getInt(1));
        }
    }
}

