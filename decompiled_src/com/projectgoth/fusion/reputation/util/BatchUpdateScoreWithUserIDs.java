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
import org.springframework.util.StopWatch;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class BatchUpdateScoreWithUserIDs {
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static void main(String[] args) throws Exception {
        int inserted;
        int updated;
        StopWatch sw;
        block8: {
            if (args.length < 1) {
                System.err.println("Usage BatchUpdateScore <filename> <level>");
                System.exit(1);
            }
            DOMConfigurator.configureAndWatch((String)"log4j.xml");
            String filename = args[0];
            int levelToAssign = Integer.parseInt(args[1]);
            DirectoryHolder directoryHolder = DirectoryUtils.getDirectoryHolder();
            BufferedReader reader = new BufferedReader(new FileReader(directoryHolder.getDataDirectory() + filename));
            DataSource masterDataSource = BatchUpdateScoreWithUserIDs.getMasterDataSource();
            Connection masterConnection = null;
            sw = new StopWatch();
            String line = null;
            log.info((Object)"partition");
            sw.start("starting partition");
            int lineNumber = 0;
            updated = 0;
            inserted = 0;
            try {
                masterConnection = masterDataSource.getConnection();
                PreparedStatement updateScoreStatement = masterConnection.prepareStatement("update score set score = ? where userid = ?");
                PreparedStatement insertScoreStatement = masterConnection.prepareStatement("insert into score (userid, score) values (?,?)");
                SortedMap<Integer, Integer> levelTable = BatchUpdateScoreWithUserIDs.readLevelTable(masterConnection);
                for (Integer key : levelTable.keySet()) {
                    log.info((Object)(key + " -> " + levelTable.get(key)));
                }
                int score = BatchUpdateScoreWithUserIDs.getScoreForLevel(levelToAssign, levelTable) - 510;
                System.out.println("setting score to " + score);
                while ((line = reader.readLine()) != null) {
                    int userid = Integer.parseInt(CSVUtils.getColumnFromLine(line, 0, ','));
                    if (++lineNumber % 5000 == 0) {
                        System.out.println(lineNumber);
                    }
                    updateScoreStatement.setInt(1, score);
                    updateScoreStatement.setInt(2, userid);
                    int rows = updateScoreStatement.executeUpdate();
                    if (rows == 0) {
                        insertScoreStatement.setInt(1, userid);
                        insertScoreStatement.setInt(2, score);
                        insertScoreStatement.executeUpdate();
                        ++inserted;
                        continue;
                    }
                    ++updated;
                }
                Object var19_20 = null;
                if (masterConnection == null) break block8;
            }
            catch (Throwable throwable) {
                Object var19_21 = null;
                if (masterConnection != null) {
                    masterConnection.close();
                }
                throw throwable;
            }
            masterConnection.close();
        }
        sw.stop();
        log.info((Object)("finished with batch, updated [" + updated + "] inserted [" + inserted + "]"));
    }
}

