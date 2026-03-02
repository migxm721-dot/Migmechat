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
public class PartitionButterflySubLevels {
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
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage PopulateInitialSubLevels <filename>");
            System.exit(1);
        }
        DOMConfigurator.configureAndWatch((String)"log4j.xml");
        String filename = args[0];
        DirectoryHolder directoryHolder = DirectoryUtils.getDirectoryHolder();
        BufferedReader reader = new BufferedReader(new FileReader(directoryHolder.getDataDirectory() + filename));
        BufferedWriter writer = new BufferedWriter(new FileWriter(directoryHolder.getDataDirectory() + "partitioned." + filename));
        DataSource masterDataSource = PartitionButterflySubLevels.getMasterDataSource();
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
            SortedMap<Integer, Integer> levelTable = PartitionButterflySubLevels.readLevelTable(masterConnection);
            for (Integer key : levelTable.keySet()) {
                log.info((Object)(key + " -> " + levelTable.get(key)));
            }
            log.info((Object)PartitionButterflySubLevels.getScoreForLevel(6, levelTable));
            TreeMap<Integer, Integer> brackets = new TreeMap<Integer, Integer>();
            brackets.put((int)(0.4 * (double)numberOfUsers), PartitionButterflySubLevels.getScoreForLevel(6, levelTable));
            brackets.put((int)(0.65 * (double)numberOfUsers), PartitionButterflySubLevels.getScoreForLevel(7, levelTable));
            brackets.put((int)(0.8500000000000001 * (double)numberOfUsers), PartitionButterflySubLevels.getScoreForLevel(8, levelTable));
            brackets.put((int)(0.9500000000000001 * (double)numberOfUsers), PartitionButterflySubLevels.getScoreForLevel(9, levelTable));
            brackets.put((int)(1.0 * (double)numberOfUsers), PartitionButterflySubLevels.getScoreForLevel(10, levelTable));
            log.info((Object)"brackets");
            for (Integer key : brackets.keySet()) {
                log.info((Object)(key + " -> " + brackets.get(key)));
            }
            log.info((Object)"partition");
            sw.start("starting partition");
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                username = CSVUtils.getColumnFromLine(line, 0, ',');
                writer.write(username);
                writer.write(",");
                writer.write(Integer.toString(PartitionButterflySubLevels.getPartitionForLine(++lineNumber, brackets)));
                writer.write(",");
                writer.newLine();
            }
            sw.stop();
            log.info((Object)"finished with score dump...");
        }
        finally {
            if (masterConnection != null) {
                masterConnection.close();
            }
            writer.close();
        }
    }
}

