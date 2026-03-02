/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.danga.MemCached.MemCachedClient
 *  org.apache.log4j.Logger
 *  org.springframework.jdbc.core.RowMapper
 */
package com.projectgoth.fusion.dao.impl;

import com.danga.MemCached.MemCachedClient;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.MemCachedUtils;
import com.projectgoth.fusion.dao.DisplayPictureAndStatusMessageDAO;
import com.projectgoth.fusion.dao.impl.MigJdbcDaoSupport;
import com.projectgoth.fusion.data.DisplayPictureAndStatusMessage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;

public class DisplayPictureAndStatusMessageDAOJDBC
extends MigJdbcDaoSupport
implements DisplayPictureAndStatusMessageDAO {
    private Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(DisplayPictureAndStatusMessageDAOJDBC.class));
    private static MemCachedClient displayPictureAndStatusMessageMemcache = MemCachedUtils.getMemCachedClient(MemCachedUtils.Instance.userDisplayPictureAndStatus);

    public DisplayPictureAndStatusMessage getDisplayPictureAndStatusMessage(String username) {
        try {
            DisplayPictureAndStatusMessage displayPictureAndStatusMessage = DisplayPictureAndStatusMessage.getDisplayPictureAndStatusMessage(displayPictureAndStatusMessageMemcache, username);
            if (displayPictureAndStatusMessage != null) {
                this.log.debug((Object)"found a displayPictureAndStatusMessage in memcached");
                return displayPictureAndStatusMessage;
            }
            displayPictureAndStatusMessage = this.loadDisplayPictureAndStatusMessageFromDB(username);
            if (displayPictureAndStatusMessage != null) {
                this.log.debug((Object)"found a displayPictureAndStatusMessage in the DB");
                DisplayPictureAndStatusMessage.setDisplayPictureAndStatusMessage(displayPictureAndStatusMessageMemcache, username, displayPictureAndStatusMessage);
                return displayPictureAndStatusMessage;
            }
        }
        catch (Exception ex) {
            this.log.error((Object)"failed to load the DisplayPictureAndStatusMessage", (Throwable)ex);
        }
        return null;
    }

    public String getGroupDisplayPicture(int groupId) {
        Object result = this.getJdbcTemplate().queryForObject(this.getExternalizedQuery("DisplayPictureAndStatusMessageDAO.getGroupDisplayPicture"), new Object[]{groupId}, String.class);
        if (result != null) {
            return (String)result;
        }
        return null;
    }

    private DisplayPictureAndStatusMessage loadDisplayPictureAndStatusMessageFromDB(String username) {
        List displayPictureAndStatusMessage = this.getJdbcTemplate().query(this.getExternalizedQuery("DisplayPictureAndStatusMessageDAO.getDisplayPictureAndStatusMessage"), new Object[]{username}, (RowMapper)new DisplayPictureAndStatusMessageRowMapper());
        if (!displayPictureAndStatusMessage.isEmpty()) {
            return (DisplayPictureAndStatusMessage)displayPictureAndStatusMessage.get(0);
        }
        return null;
    }

    private static final class DisplayPictureAndStatusMessageRowMapper
    implements RowMapper {
        private DisplayPictureAndStatusMessageRowMapper() {
        }

        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            DisplayPictureAndStatusMessage displayPictureAndStatusMessage = new DisplayPictureAndStatusMessage();
            displayPictureAndStatusMessage.setDisplayPicture(rs.getString("displayPicture"));
            displayPictureAndStatusMessage.setStatusMessage(rs.getString("statusMessage"));
            try {
                displayPictureAndStatusMessage.setStatusTimestamp(rs.getTimestamp("statusTimeStamp"));
            }
            catch (Exception ex) {
                displayPictureAndStatusMessage.setStatusTimestamp(new Date(0L));
            }
            return displayPictureAndStatusMessage;
        }
    }
}

