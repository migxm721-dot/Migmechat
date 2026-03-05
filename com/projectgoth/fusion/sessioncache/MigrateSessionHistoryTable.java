/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v2.c3p0.PooledDataSource
 *  org.apache.log4j.Logger
 *  org.apache.log4j.PropertyConfigurator
 *  org.springframework.context.ApplicationContext
 */
package com.projectgoth.fusion.sessioncache;

import com.mchange.v2.c3p0.PooledDataSource;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.sessioncache.Main;
import com.projectgoth.fusion.sessioncache.SessionCacheApplicationContext;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.ApplicationContext;

public class MigrateSessionHistoryTable {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(Main.class));
    private static final int BATCH_SIZE = 100;
    private static final int CHUNK_SIZE = 2000;
    private static ApplicationContext context;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static void main(String[] args) {
        block21: {
            PropertyConfigurator.configureAndWatch((String)"log4j.properties");
            MigrateSessionHistoryTable.context = SessionCacheApplicationContext.getContext();
            masterDataSource = (PooledDataSource)MigrateSessionHistoryTable.context.getBean("dataSource");
            masterConnection = null;
            rowCount = 0L;
            try {
                masterConnection = masterDataSource.getConnection();
                statement = masterConnection.createStatement();
                resultSet = statement.executeQuery("select count(id) from sessionhistory");
                if (resultSet.next()) {
                    rowCount = resultSet.getLong(1);
                }
                MigrateSessionHistoryTable.log.info((Object)(rowCount + " sessions in the original sessionhistory table"));
                var8_8 = null;
                ** if (masterConnection == null) goto lbl-1000
            }
            catch (Throwable var7_13) {
                var8_9 = null;
                if (masterConnection == null) throw var7_13;
                try {
                    masterConnection.close();
                    masterConnection = null;
                    throw var7_13;
                }
                catch (SQLException e) {
                    MigrateSessionHistoryTable.log.error((Object)e);
                }
                throw var7_13;
            }
lbl-1000:
            // 1 sources

            {
                try {
                    masterConnection.close();
                    masterConnection = null;
                }
                catch (SQLException e) {
                    MigrateSessionHistoryTable.log.error((Object)e);
                }
            }
lbl-1000:
            // 2 sources

            {
                break block21;
                catch (SQLException e) {
                    MigrateSessionHistoryTable.log.error((Object)e);
                    var8_8 = null;
                    if (masterConnection != null) {
                        try {
                            masterConnection.close();
                            masterConnection = null;
                        }
                        catch (SQLException e) {
                            MigrateSessionHistoryTable.log.error((Object)e);
                        }
                    }
                }
            }
        }
        fromID = 0L;
        toID = 0L;
        masterConnection = masterDataSource.getConnection();
        statement = masterConnection.prepareStatement("select * from sessionhistory where id >= ? and id < ?");
        for (i = 0; i < 20; ++i) {
            statement.setLong(1, fromID);
            statement.setLong(2, toID += 100L);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                rowCount = resultSet.getLong(1);
            }
            fromID += 100L;
        }
        var13_17 = null;
        if (masterConnection == null) return;
        try {
            masterConnection.close();
            return;
        }
        catch (SQLException e) {
            MigrateSessionHistoryTable.log.error((Object)e);
        }
        return;
        {
            catch (SQLException e) {
                MigrateSessionHistoryTable.log.error((Object)e);
                var13_18 = null;
                if (masterConnection == null) return;
                try {
                    masterConnection.close();
                    return;
                }
                catch (SQLException e) {
                    MigrateSessionHistoryTable.log.error((Object)e);
                }
                return;
            }
        }
        catch (Throwable var12_23) {
            var13_19 = null;
            if (masterConnection == null) throw var12_23;
            try {
                masterConnection.close();
                throw var12_23;
            }
            catch (SQLException e) {
                MigrateSessionHistoryTable.log.error((Object)e);
            }
            throw var12_23;
        }
    }
}

