/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v2.c3p0.ComboPooledDataSource
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.app.dao.db.fusiondb;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.projectgoth.fusion.app.dao.config.FusionBooleanConfigurationValue;
import com.projectgoth.fusion.app.dao.config.FusionConfigEnum;
import com.projectgoth.fusion.app.dao.config.FusionIntConfigurationValue;
import com.projectgoth.fusion.app.dao.config.FusionStringConfigurationValue;
import com.projectgoth.fusion.common.ConfigUtils;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.Logger;

public class DBUtils {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(DBUtils.class));

    public static Connection getFusionReadConnection() throws SQLException {
        return DataSourceEnum.FUSION_READ.dataSource.getConnection();
    }

    public static Connection getOlapReadConnection() throws SQLException {
        return DataSourceEnum.OLAP_READ.dataSource.getConnection();
    }

    public static Connection getFusionWriteConnection() throws SQLException {
        return DataSourceEnum.FUSION_WRITE.dataSource.getConnection();
    }

    public static void closeResource(ResultSet rs, Statement ps, Connection conn, Logger log) {
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e) {
            rs = null;
            log.warn((Object)"Failed to close ResultSet", (Throwable)e);
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e) {
            ps = null;
            log.warn((Object)"Failed to close (Prepared)Statement", (Throwable)e);
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e) {
            conn = null;
            log.warn((Object)"Failed to close Connection", (Throwable)e);
        }
    }

    private static void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(){

            public void run() {
                log.info((Object)"shutdown c3p0 connection pool");
                for (DataSourceEnum datasource : DataSourceEnum.values()) {
                    try {
                        datasource.dataSource.close();
                        datasource.dataSource = null;
                    }
                    catch (Throwable ex) {
                        log.error((Object)String.format("Failed to shutdown %s in shutdown hook, ignoring", new Object[]{datasource}), ex);
                    }
                }
            }
        });
    }

    static {
        DBUtils.registerShutdownHook();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum DataSourceEnum {
        OLAP_READ(FusionConfigEnum.OLAP_DB_READ),
        FUSION_READ(FusionConfigEnum.FUSION_DB_READ),
        FUSION_WRITE(FusionConfigEnum.FUSION_DB_WRITE);

        private String driverClass;
        private String user;
        private String password;
        private String jdbcUrl;
        private int initialPoolSize;
        private int minPoolSize;
        private int maxPoolSize;
        private int maxIdleTime;
        private int idleTestPeriod;
        private int timeout;
        private boolean validate;
        private String preferredTestQuery;
        private int maxConnectionAge;
        private boolean testConnectionOnCheckin;
        private boolean testConnectionOnCheckout;
        private int acquireRetryDelay;
        private int acquireRetryAttempts;
        private int acquireIncrement;
        private boolean breakAfterAcquireFailure;
        private ComboPooledDataSource dataSource;

        private DataSourceEnum(FusionConfigEnum configEnum) {
            this.driverClass = new FusionStringConfigurationValue(configEnum, "c3p0.driverClass", "com.mysql.jdbc.Driver").get();
            this.user = new FusionStringConfigurationValue(configEnum, "c3p0.user", "fusion").get();
            this.password = new FusionStringConfigurationValue(configEnum, "c3p0.password", "abalone5KG").get();
            this.jdbcUrl = new FusionStringConfigurationValue(configEnum, "c3p0.jdbcUrl", "jdbc:mysql://localhost:3306/fusion").get();
            this.initialPoolSize = new FusionIntConfigurationValue(configEnum, "c3p0.initialPoolSize", 3).get();
            this.minPoolSize = new FusionIntConfigurationValue(configEnum, "c3p0.minPoolSize", 3).get();
            this.maxPoolSize = new FusionIntConfigurationValue(configEnum, "c3p0.maxPoolSize", 10).get();
            this.maxIdleTime = new FusionIntConfigurationValue(configEnum, "c3p0.maxIdleTime", 20).get();
            this.idleTestPeriod = new FusionIntConfigurationValue(configEnum, "c3p0.idleTestPeriod", 3000).get();
            this.preferredTestQuery = new FusionStringConfigurationValue(configEnum, "c3p0.preferredTestQuery", "SELECT 1").get();
            this.maxConnectionAge = new FusionIntConfigurationValue(configEnum, "c3p0.maxConnectionAge", 3600).get();
            this.testConnectionOnCheckin = new FusionBooleanConfigurationValue(configEnum, "c3p0.testConnectionOnCheckin", true).get();
            this.testConnectionOnCheckout = new FusionBooleanConfigurationValue(configEnum, "c3p0.testConnectionOnCheckout", false).get();
            this.acquireRetryDelay = new FusionIntConfigurationValue(configEnum, "c3p0.acquireRetryDelay", 1000).get();
            this.acquireRetryAttempts = new FusionIntConfigurationValue(configEnum, "c3p0.acquireRetryAttempts", 30).get();
            this.acquireIncrement = new FusionIntConfigurationValue(configEnum, "c3p0.acquireIncrement", 1).get();
            this.breakAfterAcquireFailure = new FusionBooleanConfigurationValue(configEnum, "c3p0.breakAfterAcquireFailure", false).get();
            this.timeout = new FusionIntConfigurationValue(configEnum, "c3p0.timeout", 3500).get();
            this.validate = new FusionBooleanConfigurationValue(configEnum, "c3p0.validate", true).get();
            if (log.isDebugEnabled()) {
                log.debug((Object)("Config:  " + configEnum.getIdentifier()));
                log.debug((Object)(" URL:     " + this.jdbcUrl));
                log.debug((Object)(" Driver:  " + this.driverClass));
                log.debug((Object)(" User:    " + this.user));
                log.debug((Object)(" Password:\t" + this.password));
                log.debug((Object)(" InitialPoolSize\t" + this.initialPoolSize));
                log.debug((Object)(" MinPoolSize\t" + this.minPoolSize));
                log.debug((Object)(" MaxPoolSize\t" + this.maxPoolSize));
                log.debug((Object)(" MaxIdleTime: " + this.maxIdleTime));
                log.debug((Object)(" Timeout: " + this.timeout));
                log.debug((Object)(" Validate: " + this.validate));
                log.debug((Object)(" IdleTestPeriod: " + this.idleTestPeriod));
                log.debug((Object)(" PreferredTestQuery: " + this.preferredTestQuery));
                log.debug((Object)(" MaxConnectionAge: " + this.maxConnectionAge));
                log.debug((Object)(" testConnectionOnCheckin: " + this.testConnectionOnCheckin));
                log.debug((Object)(" testConnectionOnCheckout: " + this.testConnectionOnCheckout));
                log.debug((Object)(" acquireRetryDelay: " + this.acquireRetryDelay));
                log.debug((Object)(" acquireRetryAttempts: " + this.acquireRetryAttempts));
                log.debug((Object)(" acquireIncrement: " + this.acquireIncrement));
                log.debug((Object)(" breakAfterAcquireFailure: " + this.breakAfterAcquireFailure));
            }
            this.dataSource = new ComboPooledDataSource();
            try {
                this.dataSource.setDriverClass(this.driverClass);
                this.dataSource.setUser(this.user);
                this.dataSource.setPassword(this.password);
                this.dataSource.setJdbcUrl(this.jdbcUrl);
                this.dataSource.setInitialPoolSize(this.initialPoolSize);
                this.dataSource.setMinPoolSize(this.minPoolSize);
                this.dataSource.setMaxPoolSize(this.maxPoolSize);
                this.dataSource.setMaxIdleTime(this.maxIdleTime);
                this.dataSource.setIdleConnectionTestPeriod(this.idleTestPeriod);
                this.dataSource.setPreferredTestQuery(this.preferredTestQuery);
                this.dataSource.setMaxConnectionAge(this.maxConnectionAge);
                this.dataSource.setTestConnectionOnCheckin(this.testConnectionOnCheckin);
                this.dataSource.setTestConnectionOnCheckout(this.testConnectionOnCheckout);
                this.dataSource.setAcquireRetryDelay(this.acquireRetryDelay);
                this.dataSource.setAcquireRetryAttempts(this.acquireRetryAttempts);
                this.dataSource.setAcquireIncrement(this.acquireIncrement);
            }
            catch (Exception e) {
                log.error((Object)("Failed to Create datasource from properties file [" + configEnum.getIdentifier() + "]. Eerror:" + e), (Throwable)e);
            }
        }
    }
}

