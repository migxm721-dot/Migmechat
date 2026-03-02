/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mysql.jdbc.exceptions.MySQLSyntaxErrorException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.maintenance;

import com.mysql.jdbc.exceptions.MySQLSyntaxErrorException;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.maintenance.AbstractDatabaseMaintenance;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import org.apache.log4j.Logger;

public class PopulateUserIDFromFile
extends AbstractDatabaseMaintenance {
    protected static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(PopulateUserIDFromFile.class));

    public PopulateUserIDFromFile() throws IOException {
        this.loadProperties();
        this.configureDataSources();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void go(String filename, int batchCount, int iterationDelayMilliSeconds) throws SQLException, InterruptedException, IOException {
        long totalCount = 0L;
        int readCount = 0;
        int reportCount = batchCount < 10000 ? 10000 : batchCount;
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line = null;
        ArrayList<String> usernames = new ArrayList<String>(batchCount + 1);
        StringBuilder sql = new StringBuilder();
        Connection masterConnection = this.masterDataSource.getConnection();
        System.out.println("sleeping for " + iterationDelayMilliSeconds + " between iterations");
        long start = System.currentTimeMillis();
        try {
            do {
                readCount = 0;
                usernames.clear();
                sql.append("INSERT INTO userid (username) VALUES ");
                while (readCount++ < batchCount && reader != null && (line = reader.readLine()) != null) {
                    usernames.add(line.replace("'", "''"));
                }
                if (usernames.size() > 0) {
                    for (String username : usernames) {
                        sql.append("('").append(username).append("'),");
                    }
                    sql.delete(sql.length() - 1, sql.length());
                    try {
                        Statement statement = masterConnection.createStatement();
                        statement.executeUpdate(sql.toString());
                        statement.close();
                        long end = System.currentTimeMillis();
                        if ((totalCount += (long)batchCount) % (long)reportCount == 0L) {
                            System.out.println(totalCount + " done, last " + reportCount + " in " + (double)(end - start) / 1000.0 + " seconds (including sleep for 2+ iterations)");
                            start = System.currentTimeMillis();
                        }
                    }
                    catch (MySQLSyntaxErrorException e) {
                        System.out.println("skipping users, error with " + sql);
                    }
                }
                sql.delete(0, sql.length());
                Thread.sleep(iterationDelayMilliSeconds);
            } while (line != null);
            Object var21_18 = null;
            if (masterConnection == null) return;
        }
        catch (Throwable throwable) {
            Object var21_19 = null;
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
            if (args.length < 3) {
                System.err.println("Usage PUID <filename> <batchcount> <iteration delay in ms>");
                System.exit(1);
            }
            batchCount = Integer.parseInt(args[1]);
            iterationDelaySeconds = Integer.parseInt(args[2]);
            PopulateUserIDFromFile pop = new PopulateUserIDFromFile();
            pop.go(args[0], batchCount, iterationDelaySeconds);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }
}

