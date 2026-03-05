/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Communicator
 *  Ice.ObjectPrx
 *  Ice.Util
 *  org.apache.log4j.xml.DOMConfigurator
 */
package com.projectgoth.fusion.maintenance;

import Ice.Communicator;
import Ice.ObjectPrx;
import Ice.Util;
import com.projectgoth.fusion.maintenance.AbstractDatabaseMaintenance;
import com.projectgoth.fusion.slice.AuthenticationServicePrx;
import com.projectgoth.fusion.slice.AuthenticationServicePrxHelper;
import com.projectgoth.fusion.slice.FusionException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import org.apache.log4j.xml.DOMConfigurator;

public class MigrateCredentials
extends AbstractDatabaseMaintenance {
    private static Communicator iceCommunicator = Util.initialize((String[])new String[0]);
    private String hostname;
    private AuthenticationServicePrx authenticationServiceProxy;

    public MigrateCredentials(String hostname) throws IOException {
        this.loadProperties();
        this.configureDataSources();
        this.hostname = hostname;
    }

    public synchronized AuthenticationServicePrx findAuthenticationServiceProxy() {
        try {
            if (this.authenticationServiceProxy == null) {
                ObjectPrx basePrx = iceCommunicator.stringToProxy("AuthenticationService:tcp -h " + this.hostname + " -p 23500");
                if (basePrx == null) {
                    throw new Exception("communicator().stringToProxy() returned null");
                }
                this.authenticationServiceProxy = AuthenticationServicePrxHelper.checkedCast(basePrx);
                if (this.authenticationServiceProxy == null) {
                    throw new Exception("AuthenticationServicePrxHelper.checkedCast() returned null");
                }
            }
        }
        catch (Exception e) {
            log.warn((Object)("failed to locate authentication service at endpoint(s) " + this.hostname + "]"), (Throwable)e);
            this.authenticationServiceProxy = null;
        }
        return this.authenticationServiceProxy;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void process(int delay) throws SQLException, InterruptedException {
        Connection connection = null;
        try {
            System.out.println(new Date() + " getting connection...");
            connection = this.slaveDataSource.getConnection();
            int resultsProcessed = 0;
            int lastProcessedUserId = 0;
            do {
                System.out.println(new Date() + " executing query...");
                PreparedStatement ps = connection.prepareStatement("select uid.id,uid.username from userid uid left outer join credential c on uid.id = c.userid and c.passwordtype = 1 where uid.id > ? and c.userid is null limit 1000");
                ps.setInt(1, lastProcessedUserId);
                ResultSet rs = ps.executeQuery();
                resultsProcessed = 0;
                System.out.println(new Date() + " iterating through results...");
                while (rs.next()) {
                    int userID = rs.getInt(1);
                    String username = rs.getString(2);
                    System.out.println("user id [" + userID + "] and username [" + username + "]");
                    try {
                        this.findAuthenticationServiceProxy().migrateUserCredentials(userID);
                        ++resultsProcessed;
                        lastProcessedUserId = userID;
                        Thread.sleep(delay);
                    }
                    catch (FusionException e) {
                        log.error((Object)("failed to migrate user [" + userID + "]"), (Throwable)((Object)e));
                    }
                }
            } while (resultsProcessed > 0);
            Object var11_10 = null;
            if (connection == null) return;
        }
        catch (Throwable throwable) {
            Object var11_11 = null;
            if (connection == null) throw throwable;
            connection.close();
            throw throwable;
        }
        connection.close();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static void main(String[] args) {
        System.setProperty("log.filename", "migratecredentials");
        DOMConfigurator.configureAndWatch((String)"log4j.xml");
        try {
            try {
                MigrateCredentials mg = new MigrateCredentials(args[0]);
                mg.process(Integer.parseInt(args[1]));
            }
            catch (Throwable t) {
                log.error((Object)"failed to migrate credentials", t);
                Object var3_4 = null;
                System.exit(0);
                return;
            }
            Object var3_3 = null;
        }
        catch (Throwable throwable) {
            Object var3_5 = null;
            System.exit(0);
            throw throwable;
        }
        System.exit(0);
    }
}

