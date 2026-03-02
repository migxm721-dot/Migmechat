/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.DataAccessException
 *  org.springframework.jdbc.core.RowCallbackHandler
 *  org.springframework.jdbc.core.RowMapper
 */
package com.projectgoth.fusion.dao.impl;

import com.projectgoth.fusion.dao.BotDAO;
import com.projectgoth.fusion.dao.impl.MigJdbcDaoSupport;
import com.projectgoth.fusion.data.BotData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class BotDAOJDBC
extends MigJdbcDaoSupport
implements BotDAO {
    @Override
    public List<BotData> getBots() {
        try {
            return this.getJdbcTemplate().query(this.getExternalizedQuery("BotDAO.getBots"), new Object[0], (RowMapper)new BotDataRowMapper());
        }
        catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public Map<String, String> getBotConfig(long botID) {
        final HashMap<String, String> config = new HashMap<String, String>();
        this.getJdbcTemplate().query(this.getExternalizedQuery("BotDAO.getBotConfig"), new Object[]{botID}, new RowCallbackHandler(){

            public void processRow(ResultSet rs) throws SQLException {
                config.put(rs.getString("PropertyName"), rs.getString("PropertyValue"));
            }
        });
        return config;
    }

    @Override
    public Map<String, String> getBotCommands(long botID, String languageCode) {
        final HashMap<String, String> commands = new HashMap<String, String>();
        this.getJdbcTemplate().query(this.getExternalizedQuery("BotDAO.getBotCommandsForLanguage"), new Object[]{botID, languageCode}, new RowCallbackHandler(){

            public void processRow(ResultSet rs) throws SQLException {
                commands.put(rs.getString("CommandKey"), rs.getString("CommandValue"));
            }
        });
        return commands;
    }

    @Override
    public Map<String, String> getBotMessages(long botID, String languageCode) {
        final HashMap<String, String> messages = new HashMap<String, String>();
        this.getJdbcTemplate().query(this.getExternalizedQuery("BotDAO.getBotMessagesForLanguage"), new Object[]{botID, languageCode}, new RowCallbackHandler(){

            public void processRow(ResultSet rs) throws SQLException {
                messages.put(rs.getString("MessageKey"), rs.getString("MessageValue"));
            }
        });
        return messages;
    }

    private static final class BotDataRowMapper
    implements RowMapper {
        private BotDataRowMapper() {
        }

        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new BotData(rs);
        }
    }
}

