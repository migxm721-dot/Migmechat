/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.springframework.jdbc.core.RowMapper
 */
package com.projectgoth.fusion.dao.impl;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.dao.FashionShowDAO;
import com.projectgoth.fusion.dao.impl.MigJdbcDaoSupport;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FashionShowDAOJDBC
extends MigJdbcDaoSupport
implements FashionShowDAO {
    private Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FashionShowDAOJDBC.class));

    @Override
    public List<String> getAvatarCandidatesForRedis(int days, int level, int avatarItemCount) {
        return this.getJdbcTemplate().query(this.getExternalizedQuery("FashionShowDAO.getAvatarCandidatesForRedis"), new Object[]{days, level, avatarItemCount}, (RowMapper)new AvatarCandidatesRowMapper());
    }

    private static final class AvatarCandidatesRowMapper
    implements RowMapper {
        private AvatarCandidatesRowMapper() {
        }

        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            String user = rs.getString("uname") + ":" + rs.getString("uid");
            return user;
        }
    }
}

