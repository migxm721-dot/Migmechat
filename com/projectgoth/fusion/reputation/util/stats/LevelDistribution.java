/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v2.c3p0.ComboPooledDataSource
 *  org.apache.log4j.Logger
 *  org.apache.log4j.xml.DOMConfigurator
 */
package com.projectgoth.fusion.reputation.util.stats;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.reputation.ReputationServiceI;
import com.projectgoth.fusion.reputation.UpdateScoreTable;
import com.projectgoth.fusion.reputation.util.LevelTable;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class LevelDistribution {
    protected static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(UpdateScoreTable.class));
    private DataSource olapDataSource;
    private SortedMap<Integer, Integer> levelTable;

    public LevelDistribution(DataSource olapDataSource, SortedMap<Integer, Integer> levelTable) {
        this.olapDataSource = olapDataSource;
        this.levelTable = levelTable;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public SortedMap<Integer, Integer> getDistribution() throws SQLException {
        TreeMap<Integer, Integer> treeMap;
        block4: {
            Connection olapConnection = null;
            try {
                olapConnection = this.olapDataSource.getConnection();
                PreparedStatement selectScoreLevelStatement = olapConnection.prepareStatement("select count(*) from score where score >= ? and score < ?");
                TreeMap<Integer, Integer> distribution = new TreeMap<Integer, Integer>();
                for (Integer level : this.levelTable.values()) {
                    int bottomBracket = LevelTable.getScoreForLevel(level, this.levelTable);
                    int topBracket = LevelTable.getScoreForLevel(level + 1, this.levelTable);
                    if (topBracket == bottomBracket) continue;
                    selectScoreLevelStatement.setInt(1, bottomBracket);
                    selectScoreLevelStatement.setInt(2, topBracket);
                    ResultSet rs = selectScoreLevelStatement.executeQuery();
                    if (!rs.next()) continue;
                    distribution.put(level, rs.getInt(1));
                }
                log.info((Object)"level distribution: ");
                for (Integer level : distribution.keySet()) {
                    log.info((Object)(level + "," + distribution.get(level)));
                }
                treeMap = distribution;
                Object var10_9 = null;
                if (olapConnection == null) break block4;
            }
            catch (Throwable throwable) {
                block5: {
                    Object var10_10 = null;
                    if (olapConnection == null) break block5;
                    olapConnection.close();
                }
                throw throwable;
            }
            olapConnection.close();
        }
        return treeMap;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            String outFilename = "score.distribution." + ReputationServiceI.FILE_DATE_FORMAT.format(new Date()) + ".csv";
            System.err.println("Usage ScoreDistribution <filename>, using default filename [" + outFilename + "]");
        } else {
            String outFilename = args[0];
        }
        DOMConfigurator.configureAndWatch((String)"log4j.xml");
    }

    public static DataSource getOLAPDataSource() throws Exception {
        Properties databaseProperties = new Properties();
        databaseProperties.load(new FileInputStream(System.getProperty("config.dir") + "database.properties"));
        ComboPooledDataSource datasource = new ComboPooledDataSource();
        log.info((Object)"rep jdbc url: jdbc:mysql://olap01:3306/fusion");
        datasource.setJdbcUrl("jdbc:mysql://olap01:3306/fusion");
        datasource.setUser(databaseProperties.getProperty("database.username"));
        datasource.setPassword(databaseProperties.getProperty("database.password"));
        datasource.setDriverClass(databaseProperties.getProperty("database.driver"));
        datasource.setMinPoolSize(1);
        datasource.setAcquireIncrement(1);
        datasource.setAcquireRetryAttempts(2);
        datasource.setMaxPoolSize(1);
        return datasource;
    }
}

