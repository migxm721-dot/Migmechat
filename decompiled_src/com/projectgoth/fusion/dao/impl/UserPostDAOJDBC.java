/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.jdbc.core.RowMapper
 */
package com.projectgoth.fusion.dao.impl;

import com.projectgoth.fusion.dao.UserPostDAO;
import com.projectgoth.fusion.dao.impl.MigJdbcDaoSupport;
import com.projectgoth.fusion.data.UserPostData;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class UserPostDAOJDBC
extends MigJdbcDaoSupport
implements UserPostDAO {
    public UserPostData getUserPost(int postId) {
        return (UserPostData)this.getJdbcTemplate().queryForObject(this.getExternalizedQuery("UserPostDAO.getUserPost"), new Object[]{postId}, (RowMapper)new UserPostRowMapper());
    }

    public UserPostData getTopicForUserPost(int postId) {
        return (UserPostData)this.getJdbcTemplate().queryForObject(this.getExternalizedQuery("UserPostDAO.getTopicForUserPost"), new Object[]{postId}, (RowMapper)new UserPostRowMapper());
    }

    private static final class UserPostRowMapper
    implements RowMapper {
        private UserPostRowMapper() {
        }

        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            UserPostData userPost = new UserPostData(rs);
            return userPost;
        }
    }
}

