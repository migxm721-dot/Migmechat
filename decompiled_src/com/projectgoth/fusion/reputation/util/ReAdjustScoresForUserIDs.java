/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v2.c3p0.ComboPooledDataSource
 *  org.apache.log4j.Logger
 *  org.apache.log4j.xml.DOMConfigurator
 */
package com.projectgoth.fusion.reputation.util;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.reputation.UpdateScoreTable;
import com.projectgoth.fusion.reputation.util.CSVUtils;
import com.projectgoth.fusion.reputation.util.DirectoryHolder;
import com.projectgoth.fusion.reputation.util.DirectoryUtils;
import com.projectgoth.fusion.reputation.util.LevelTable;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ReAdjustScoresForUserIDs {
    protected static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(UpdateScoreTable.class));

    public static DataSource getMasterDataSource() throws Exception {
        Properties databaseProperties = new Properties();
        databaseProperties.load(new FileInputStream(System.getProperty("config.dir") + "database.properties"));
        ComboPooledDataSource datasource = new ComboPooledDataSource();
        log.info((Object)("rep jdbc url: " + databaseProperties.getProperty("database.jdbcUrl")));
        datasource.setJdbcUrl(databaseProperties.getProperty("database.jdbcUrl"));
        datasource.setUser(databaseProperties.getProperty("database.username"));
        datasource.setPassword(databaseProperties.getProperty("database.password"));
        datasource.setDriverClass(databaseProperties.getProperty("database.driver"));
        datasource.setMinPoolSize(1);
        datasource.setAcquireIncrement(1);
        datasource.setAcquireRetryAttempts(2);
        datasource.setMaxPoolSize(1);
        return datasource;
    }

    public static SortedMap<Integer, Integer> readLevelTable(Connection connection, String tableName) throws SQLException {
        TreeMap<Integer, Integer> map = new TreeMap<Integer, Integer>(Collections.reverseOrder());
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("select score,level from " + tableName + " order by score desc");
        while (rs.next()) {
            map.put(rs.getInt(1), rs.getInt(2));
        }
        return map;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage ReAdjustScoresForUserIDs <filename>");
            System.exit(1);
        }
        DOMConfigurator.configureAndWatch((String)"log4j.xml");
        String filename = args[0];
        DirectoryHolder directoryHolder = DirectoryUtils.getDirectoryHolder();
        BufferedReader reader = new BufferedReader(new FileReader(directoryHolder.getDataDirectory() + filename));
        DataSource masterDataSource = ReAdjustScoresForUserIDs.getMasterDataSource();
        Connection masterConnection = null;
        String line = null;
        int lineNumber = 0;
        int updated = 0;
        try {
            masterConnection = masterDataSource.getConnection();
            PreparedStatement updateScoreStatement = masterConnection.prepareStatement("update score set score = ? where userid = ?");
            SortedMap<Integer, Integer> levelTable = ReAdjustScoresForUserIDs.readLevelTable(masterConnection, "ReputationScoreToLevel");
            SortedMap<Integer, Integer> oldLevelTable = ReAdjustScoresForUserIDs.readLevelTable(masterConnection, "ReputationScoreToLevel_orig");
            System.out.println("current level table:");
            for (Integer key : levelTable.keySet()) {
                System.out.println(key + " -> " + levelTable.get(key));
            }
            System.out.println("old level table:");
            for (Integer key : oldLevelTable.keySet()) {
                System.out.println(key + " -> " + oldLevelTable.get(key));
            }
            while ((line = reader.readLine()) != null && lineNumber < 1) {
                int userid = Integer.parseInt(CSVUtils.getColumnFromLine(line, 0, ','));
                int score = Integer.parseInt(CSVUtils.getColumnFromLine(line, 1, ','));
                if (++lineNumber % 5000 == 0) {
                    System.out.println(lineNumber);
                }
                int newScore = LevelTable.getScoreForLevel(LevelTable.getLevelForScore(score, oldLevelTable) + 1, levelTable) - 201;
                System.out.println("user id [" + userid + "] old score [" + score + "] old score level [" + LevelTable.getLevelForScore(score, oldLevelTable) + "] new score [" + newScore + "] and new score level [" + LevelTable.getLevelForScore(newScore, levelTable));
                updateScoreStatement.setInt(1, newScore);
                updateScoreStatement.setInt(2, userid);
                int rows = updateScoreStatement.executeUpdate();
                if (rows <= 0) continue;
                ++updated;
            }
            Object var17_18 = null;
            if (masterConnection == null) return;
        }
        catch (Throwable throwable) {
            Object var17_19 = null;
            if (masterConnection == null) throw throwable;
            masterConnection.close();
            throw throwable;
        }
        masterConnection.close();
    }
}

