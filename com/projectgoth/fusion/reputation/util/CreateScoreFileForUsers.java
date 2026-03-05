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
import com.projectgoth.fusion.reputation.file.SortBigFile;
import com.projectgoth.fusion.reputation.file.comparator.SingleIndexIntegerFileEntryComparator;
import com.projectgoth.fusion.reputation.file.comparator.SingleIndexIntegerStringListComparator;
import com.projectgoth.fusion.reputation.util.CSVUtils;
import com.projectgoth.fusion.reputation.util.DirectoryHolder;
import com.projectgoth.fusion.reputation.util.DirectoryUtils;
import com.projectgoth.fusion.reputation.util.FileLocation;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.util.StopWatch;

public class CreateScoreFileForUsers {
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
        StopWatch sw;
        BufferedWriter writer;
        DirectoryHolder directoryHolder;
        String filename;
        block8: {
            if (args.length < 1) {
                System.err.println("Usage InitialLevel <filename>");
                System.exit(1);
            }
            DOMConfigurator.configureAndWatch((String)"log4j.xml");
            filename = args[0];
            directoryHolder = DirectoryUtils.getDirectoryHolder();
            BufferedReader reader = new BufferedReader(new FileReader(directoryHolder.getDataDirectory() + filename));
            writer = new BufferedWriter(new FileWriter(directoryHolder.getDataDirectory() + "scored." + filename));
            DataSource masterDataSource = CreateScoreFileForUsers.getMasterDataSource();
            Connection masterConnection = null;
            sw = new StopWatch();
            try {
                String line = null;
                String username = null;
                masterConnection = masterDataSource.getConnection();
                PreparedStatement getOldScoreStatement = masterConnection.prepareStatement("select uid.id, score from score_orig s right outer join userid uid on s.userid = uid.id where username in (?,?,?,?,?,?,?,?,?,?)");
                log.info((Object)"starting score dump...");
                sw.start("score dump");
                ArrayList<String> names = new ArrayList<String>();
                while ((line = reader.readLine()) != null) {
                    username = CSVUtils.getColumnFromLine(line, 0, '\t');
                    names.add(username);
                    if (names.size() < 10) continue;
                    int index = 1;
                    for (String name : names) {
                        getOldScoreStatement.setString(index++, name);
                    }
                    ResultSet rs = getOldScoreStatement.executeQuery();
                    index = 0;
                    while (rs.next()) {
                        writer.write(Integer.toString(rs.getInt(1)));
                        writer.write(",");
                        writer.write(Integer.toString(rs.getInt(2)));
                        writer.newLine();
                    }
                    rs.close();
                    names.clear();
                }
                getOldScoreStatement = masterConnection.prepareStatement("select uid.id, score from score_orig s right outer join userid uid on s.userid = uid.id where username = ?");
                for (String name : names) {
                    getOldScoreStatement.setString(1, name);
                    ResultSet rs = getOldScoreStatement.executeQuery();
                    if (!rs.next()) continue;
                    writer.write(Integer.toString(rs.getInt(1)));
                    writer.write(",");
                    writer.write(Integer.toString(rs.getInt(2)));
                    writer.newLine();
                }
                sw.stop();
                log.info((Object)"finished with score dump...");
                Object var16_16 = null;
                if (masterConnection == null) break block8;
            }
            catch (Throwable throwable) {
                Object var16_17 = null;
                if (masterConnection != null) {
                    masterConnection.close();
                }
                throw throwable;
            }
            masterConnection.close();
        }
        writer.flush();
        writer.close();
        sw.start("sorting score dump");
        SortBigFile sorter = new SortBigFile(directoryHolder);
        sorter.go(new FileLocation(directoryHolder.getDataDirectory(), "scored." + filename), new FileLocation(directoryHolder.getDataDirectory(), "scored." + filename), new SingleIndexIntegerFileEntryComparator(1), new SingleIndexIntegerStringListComparator(1), 2, ',');
        sw.stop();
        log.info((Object)sw.prettyPrint());
    }
}

