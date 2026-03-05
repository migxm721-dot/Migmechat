/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.maintenance;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.maintenance.AbstractDatabaseMaintenance;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.Logger;

public class PopulateUserID
extends AbstractDatabaseMaintenance {
    protected static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(PopulateUserID.class));

    public PopulateUserID() throws IOException {
        this.loadProperties();
        this.configureDataSources();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void go(int batchCount, int iterationDelaySeconds) throws SQLException, InterruptedException {
        long totalCount = 0L;
        int rowCount = 0;
        int reportCount = batchCount < 10000 ? 10000 : batchCount;
        Connection masterConnection = this.masterDataSource.getConnection();
        System.out.println("sleeping for " + iterationDelaySeconds + " between iterations");
        long start = System.currentTimeMillis();
        try {
            do {
                Statement statement = masterConnection.createStatement();
                rowCount = statement.executeUpdate("INSERT INTO userid (username) SELECT username FROM user WHERE NOT EXISTS (SELECT Id from UserID where UserID.username = user.username) LIMIT " + batchCount);
                statement.close();
                long end = System.currentTimeMillis();
                if ((totalCount += (long)rowCount) % (long)reportCount == 0L) {
                    System.out.println(totalCount + " done, last " + reportCount + " in " + (double)(end - start) / 1000.0 + " seconds (including sleep for 2+ iterations)");
                    start = System.currentTimeMillis();
                }
                Thread.sleep(iterationDelaySeconds * 1000);
            } while (rowCount > 0);
            Object var14_10 = null;
            if (masterConnection == null) return;
        }
        catch (Throwable throwable) {
            Object var14_11 = null;
            if (masterConnection == null) throw throwable;
            masterConnection.close();
            throw throwable;
        }
        masterConnection.close();
    }

    public static void main(String[] args) throws IOException {
        try {
            int batchCount = 1000;
            int iterationDelaySeconds = 125;
            if (args.length >= 1) {
                batchCount = Integer.parseInt(args[0]);
                iterationDelaySeconds = Integer.parseInt(args[1]);
            }
            PopulateUserID pop = new PopulateUserID();
            pop.go(batchCount, iterationDelaySeconds);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }
}

