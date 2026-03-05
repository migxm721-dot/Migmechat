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
import com.projectgoth.fusion.reputation.util.LevelTable;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Properties;
import java.util.SortedMap;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.util.StopWatch;

public class BatchUpdateScore {
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
        block7: {
            if (args.length < 1) {
                System.err.println("Usage BatchUpdateScore <filename> <level>");
                System.exit(1);
            }
            DOMConfigurator.configureAndWatch((String)"log4j.xml");
            String filename = args[0];
            int levelToAssign = Integer.parseInt(args[1]);
            DirectoryHolder directoryHolder = DirectoryUtils.getDirectoryHolder();
            BufferedReader reader = new BufferedReader(new FileReader(directoryHolder.getDataDirectory() + filename));
            DataSource masterDataSource = BatchUpdateScore.getMasterDataSource();
            Connection masterConnection = null;
            sw = new StopWatch();
            String line = null;
            String username = null;
            log.info((Object)"partition");
            sw.start("starting partition");
            int lineNumber = 0;
            updated = 0;
            inserted = 0;
            try {
                masterConnection = masterDataSource.getConnection();
                PreparedStatement updateScoreStatement = masterConnection.prepareStatement("update score set score = ? where userid = (select id from userid where username = ?)");
                PreparedStatement insertScoreStatement = masterConnection.prepareStatement("insert into score (userid, score) values ((select id from userid where username = ?),?)");
                SortedMap<Integer, Integer> levelTable = LevelTable.readLevelTable(masterConnection);
                for (Integer key : levelTable.keySet()) {
                    log.info((Object)(key + " -> " + levelTable.get(key)));
                }
                int score = LevelTable.getScoreForLevel(levelToAssign, levelTable);
                while ((line = reader.readLine()) != null) {
                    ++lineNumber;
                    username = CSVUtils.getColumnFromLine(line, 0, ',');
                    updateScoreStatement.setInt(1, score);
                    updateScoreStatement.setString(2, username);
                    int rows = updateScoreStatement.executeUpdate();
                    if (rows == 0) {
                        insertScoreStatement.setString(1, username);
                        insertScoreStatement.setInt(2, score);
                        insertScoreStatement.executeUpdate();
                        ++inserted;
                        continue;
                    }
                    ++updated;
                }
                Object var19_20 = null;
                if (masterConnection == null) break block7;
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

