/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.jdbc.core.RowMapper
 */
package com.projectgoth.fusion.dao.impl;

import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.dao.UserDAO;
import com.projectgoth.fusion.dao.impl.MigJdbcDaoSupport;
import com.projectgoth.fusion.domain.CountryLogins;
import com.projectgoth.fusion.userevent.system.domain.AllowListEntry;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class UserDAOJDBC
extends MigJdbcDaoSupport
implements UserDAO {
    @Override
    public List<CountryLogins> findRecentLoginsGroupedByCountry(Date since, Date until, int period) {
        return this.getJdbcTemplate().query(this.getExternalizedQuery("UserDAO.findRecentLoginsGroupedByCountry"), new Object[]{since, period, until}, (RowMapper)new CountryLoginsRowMapper());
    }

    @Override
    public List<AllowListEntry> getAllowListForUser(String username) {
        return this.getJdbcTemplate().query(this.getExternalizedQuery("UserDAO.getAllowListForUser"), new Object[]{username}, (RowMapper)new AllowListEntryRowMapper());
    }

    @Override
    public String getMobileNumberForUser(String username) {
        return (String)this.getJdbcTemplate().queryForObject(this.getExternalizedQuery("UserDAO.getMobileNumberForUser"), new Object[]{username}, String.class);
    }

    @Override
    public boolean isVerfiedEmailAddress(String recipient) {
        int result = this.getJdbcTemplate().queryForInt(this.getExternalizedQuery("UserDAO.isVerfiedEmailAddress"), new Object[]{recipient});
        return result != 0;
    }

    @Override
    public boolean isBounceEmailAddress(String recipient) {
        if (!SystemProperty.getBool(SystemPropertyEntities.UserNotificationServiceSettings.CHECK_EMAIL_BOUNCEDB_ENABLED)) {
            return false;
        }
        int result = this.getJdbcTemplate().queryForInt(this.getExternalizedQuery("UserDAO.isBounceEmailAddress"), new Object[]{recipient});
        return result != 0;
    }

    private static final class AllowListEntryRowMapper
    implements RowMapper {
        private AllowListEntryRowMapper() {
        }

        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            AllowListEntry countryLogins = new AllowListEntry(rs.getString("AllowUsername"), rs.getBoolean("PendingApproval"));
            return countryLogins;
        }
    }

    private static final class CountryLoginsRowMapper
    implements RowMapper {
        private CountryLoginsRowMapper() {
        }

        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            CountryLogins countryLogins = new CountryLogins(rs.getInt("country"), rs.getInt("sessions"), rs.getBoolean("mobileverified"));
            return countryLogins;
        }
    }
}

