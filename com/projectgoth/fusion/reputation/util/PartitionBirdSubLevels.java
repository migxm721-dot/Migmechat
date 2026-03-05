/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v2.c3p0.ComboPooledDataSource
 *  org.apache.log4j.Logger
 *  org.apache.log4j.xml.DOMConfigurator
 *  org.springframework.util.StopWatch
 */
package com.projectgoth.fusion.reputation.util;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.reputation.UpdateScoreTable;
import com.projectgoth.fusion.reputation.util.CSVUtils;
import com.projectgoth.fusion.reputation.util.DirectoryHolder;
import com.projectgoth.fusion.reputation.util.DirectoryUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
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
import org.springframework.util.StopWatch;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class PartitionBirdSubLevels {
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

    public static SortedMap<Integer, Integer> readLevelTable(Connection connection) throws SQLException {
        TreeMap<Integer, Integer> map = new TreeMap<Integer, Integer>(Collections.reverseOrder());
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("select score,level from ReputationScoreToLevel order by score desc");
        while (rs.next()) {
            map.put(rs.getInt(1), rs.getInt(2));
        }
        return map;
    }

    public static int getUserLevel(int score, SortedMap<Integer, Integer> levelTable) {
        for (Integer key : levelTable.keySet()) {
            if (score < key) continue;
            return (Integer)levelTable.get(key);
        }
        return 1;
    }

    public static int getScoreForLevel(int level, SortedMap<Integer, Integer> levelTable) {
        for (Integer key : levelTable.keySet()) {
            if (level < (Integer)levelTable.get(key)) continue;
            return key;
        }
        return 0;
    }

    public static int getPartitionForLine(int lineNumber, SortedMap<Integer, Integer> brackets) {
        for (Integer key : brackets.keySet()) {
            if (lineNumber > key) continue;
            return (Integer)brackets.get(key);
        }
        return (Integer)brackets.get(brackets.firstKey());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static void main(String[] args) throws Exception {
        BufferedWriter writer;
        block10: {
            if (args.length < 1) {
                System.err.println("Usage PopulateInitialSubLevels <filename>");
                System.exit(1);
            }
            DOMConfigurator.configureAndWatch((String)"log4j.xml");
            String filename = args[0];
            DirectoryHolder directoryHolder = DirectoryUtils.getDirectoryHolder();
            BufferedReader reader = new BufferedReader(new FileReader(directoryHolder.getDataDirectory() + filename));
            writer = new BufferedWriter(new FileWriter(directoryHolder.getDataDirectory() + "partitioned." + filename));
            DataSource masterDataSource = PartitionBirdSubLevels.getMasterDataSource();
            Connection masterConnection = null;
            StopWatch sw = new StopWatch();
            int numberOfUsers = 0;
            String line = null;
            while ((line = reader.readLine()) != null) {
                ++numberOfUsers;
            }
            reader.close();
            reader = new BufferedReader(new FileReader(directoryHolder.getDataDirectory() + filename));
            try {
                String username = null;
                masterConnection = masterDataSource.getConnection();
                SortedMap<Integer, Integer> levelTable = PartitionBirdSubLevels.readLevelTable(masterConnection);
                for (Integer key : levelTable.keySet()) {
                    log.info((Object)(key + " -> " + levelTable.get(key)));
                }
                log.info((Object)PartitionBirdSubLevels.getScoreForLevel(6, levelTable));
                TreeMap<Integer, Integer> brackets = new TreeMap<Integer, Integer>();
                brackets.put((int)(0.4 * (double)numberOfUsers), PartitionBirdSubLevels.getScoreForLevel(11, levelTable));
                brackets.put((int)(0.65 * (double)numberOfUsers), PartitionBirdSubLevels.getScoreForLevel(12, levelTable));
                brackets.put((int)(0.8500000000000001 * (double)numberOfUsers), PartitionBirdSubLevels.getScoreForLevel(13, levelTable));
                brackets.put((int)(0.9500000000000001 * (double)numberOfUsers), PartitionBirdSubLevels.getScoreForLevel(14, levelTable));
                brackets.put((int)(1.0 * (double)numberOfUsers), PartitionBirdSubLevels.getScoreForLevel(15, levelTable));
                log.info((Object)"brackets");
                for (Integer key : brackets.keySet()) {
                    log.info((Object)(key + " -> " + brackets.get(key)));
                }
                PreparedStatement updateScoreStatement = masterConnection.prepareStatement("update score set score = ? where userid = ?");
                PreparedStatement insertScoreStatement = masterConnection.prepareStatement("insert into score (userid, score) values (?,?)");
                log.info((Object)"partition");
                sw.start("starting partition");
                int lineNumber = 0;
                int userid = 0;
                int score = 0;
                int updated = 0;
                int inserted = 0;
                while ((line = reader.readLine()) != null) {
                    username = CSVUtils.getColumnFromLine(line, 0, ',');
                    userid = Integer.parseInt(username);
                    score = PartitionBirdSubLevels.getPartitionForLine(++lineNumber, brackets);
                    writer.write(username);
                    writer.write(",");
                    writer.write(Integer.toString(PartitionBirdSubLevels.getPartitionForLine(lineNumber, brackets)));
                    writer.write(",");
                    writer.newLine();
                    try {
                        insertScoreStatement.setInt(1, userid);
                        insertScoreStatement.setInt(2, score);
                        insertScoreStatement.executeUpdate();
                        ++inserted;
                    }
                    catch (SQLException e) {
                        ++updated;
                        updateScoreStatement.setInt(1, score);
                        updateScoreStatement.setInt(2, Integer.parseInt(username));
                        int rows = updateScoreStatement.executeUpdate();
                        if (rows != 0) continue;
                        log.warn((Object)("failed to insert score " + PartitionBirdSubLevels.getPartitionForLine(lineNumber, brackets) + " for user " + userid));
                    }
                }
                sw.stop();
                log.info((Object)("finished with populating the birds, updated [" + updated + "] inserted [" + inserted + "]"));
                Object var23_22 = null;
                if (masterConnection == null) break block10;
            }
            catch (Throwable throwable) {
                Object var23_23 = null;
                if (masterConnection != null) {
                    masterConnection.close();
                }
                writer.close();
                throw throwable;
            }
            masterConnection.close();
        }
        writer.close();
    }
}

