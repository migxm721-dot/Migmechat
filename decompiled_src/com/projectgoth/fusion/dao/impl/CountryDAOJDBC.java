/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.DataAccessException
 *  org.springframework.jdbc.core.RowMapper
 */
package com.projectgoth.fusion.dao.impl;

import com.projectgoth.fusion.dao.CountryDAO;
import com.projectgoth.fusion.dao.impl.MigJdbcDaoSupport;
import com.projectgoth.fusion.data.CountryData;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;

public class CountryDAOJDBC
extends MigJdbcDaoSupport
implements CountryDAO {
    public CountryData getCountryForUser(String username) {
        try {
            return (CountryData)this.getJdbcTemplate().queryForObject(this.getExternalizedQuery("CountryDAO.getCountryForUser"), new Object[]{username}, (RowMapper)new CountryDataRowMapper());
        }
        catch (DataAccessException e) {
            return null;
        }
    }

    private static final class CountryDataRowMapper
    implements RowMapper {
        private CountryDataRowMapper() {
        }

        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new CountryData(rs);
        }
    }
}

