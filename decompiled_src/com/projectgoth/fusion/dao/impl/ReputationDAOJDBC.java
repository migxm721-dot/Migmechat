/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.springframework.dao.DataAccessException
 *  org.springframework.dao.EmptyResultDataAccessException
 *  org.springframework.jdbc.core.ResultSetExtractor
 *  org.springframework.jdbc.core.RowMapper
 */
package com.projectgoth.fusion.dao.impl;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.dao.ReputationDAO;
import com.projectgoth.fusion.dao.impl.MigJdbcDaoSupport;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ReputationDAOJDBC
extends MigJdbcDaoSupport
implements ReputationDAO {
    private Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ReputationDAOJDBC.class));

    @Override
    public SortedMap<Integer, Integer> readLevelTable() {
        TreeMap<Integer, Integer> map = new TreeMap<Integer, Integer>(Collections.reverseOrder());
        List levels = this.getMasterTemplate().query(this.getExternalizedQuery("ReputationDAO.readLevelTable"), (RowMapper)new ReputationLevelRowMapper());
        for (List level : levels) {
            map.put((Integer)level.get(0), (Integer)level.get(1));
        }
        return map;
    }

    @Override
    public int getUserScore(String username) {
        try {
            return this.getJdbcTemplate().queryForInt(this.getExternalizedQuery("ReputationDAO.getUserScore"), new Object[]{username});
        }
        catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    @Override
    public Map<Integer, Integer> getUserScores(int[] userIDs) {
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        try {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("getting scores for userids [" + StringUtil.asStringWithoutQuotes(userIDs) + "]"));
            }
            map = (Map)this.getJdbcTemplate().query(this.getExternalizedQuery("ReputationDAO.getUserScores") + "(" + StringUtil.asStringWithoutQuotes(userIDs) + ")", new ResultSetExtractor(){

                public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                    HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
                    while (rs.next()) {
                        map.put(rs.getInt(1), rs.getInt(2));
                    }
                    return map;
                }
            });
        }
        catch (EmptyResultDataAccessException e) {
            this.log.error((Object)"failed to get user scores", (Throwable)e);
        }
        for (int userID : userIDs) {
            if (map.containsKey(userID)) continue;
            map.put(userID, 0);
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("we received " + userIDs.length + " userIDs and sending back " + map.size() + " entries"));
        }
        return map;
    }

    @Override
    public int updateDailyScoreDistribution(Date runDate, SortedMap<Integer, Integer> scoreDistribution) {
        Object[] scoreValues = scoreDistribution.values().toArray();
        Object[] values = new Object[scoreValues.length + 1];
        values[0] = runDate;
        System.arraycopy(scoreValues, 0, values, 1, scoreValues.length);
        if (this.log.isDebugEnabled()) {
            for (Object object : values) {
                this.log.debug(object);
            }
        }
        return this.getMasterTemplate().update(this.getExternalizedQuery("ReputationDAO.updateDailyScoreDistribution"), values);
    }

    @Override
    public int updateDailyLevelDistribution(Date runDate, SortedMap<Integer, Integer> levelDistribution) {
        Object[] levelValues = levelDistribution.values().toArray();
        Object[] values = new Object[levelValues.length + 1];
        values[0] = runDate;
        System.arraycopy(levelValues, 0, values, 1, levelValues.length);
        StringBuffer buffer = new StringBuffer("(runDate,");
        for (int i = 0; i < levelValues.length; ++i) {
            if (i < levelValues.length - 1) {
                buffer.append("l").append(i + 1).append(",");
                continue;
            }
            buffer.append("l").append(i + 1);
        }
        buffer.append(")");
        String sql = this.getExternalizedQuery("ReputationDAO.updateDailyLevelDistribution") + buffer.toString() + " values " + this.preparedStatementParameters(values.length);
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)(values.length + " values"));
            for (Object object : values) {
                this.log.debug(object);
            }
            this.log.debug((Object)("sql = " + sql));
        }
        return this.getMasterTemplate().update(sql, values);
    }

    private static final class ReputationLevelRowMapper
    implements RowMapper {
        private ReputationLevelRowMapper() {
        }

        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            ArrayList<Integer> results = new ArrayList<Integer>();
            results.add(rs.getInt(1));
            results.add(rs.getInt(2));
            return results;
        }
    }
}

