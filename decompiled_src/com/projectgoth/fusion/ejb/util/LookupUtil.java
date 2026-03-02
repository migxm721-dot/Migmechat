/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.ejb.util;

import com.projectgoth.fusion.common.ConfigUtils;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.log4j.Logger;

public class LookupUtil {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(LookupUtil.class));

    public static String getResourcePath(String resourceName) {
        String prefix = System.getProperty("config.jndi_resourceprefix", "java:/");
        return prefix + resourceName;
    }

    private static DataSource getDataSource(String path) {
        DataSource dataSource = null;
        try {
            InitialContext ctx = new InitialContext();
            dataSource = (DataSource)ctx.lookup(LookupUtil.getResourcePath(path));
            ctx.close();
        }
        catch (NamingException e) {
            log.error((Object)("unable to lookup datasource " + path), (Throwable)e);
        }
        return dataSource;
    }

    public static DataSource getFusionMasterDataSource() {
        return LookupUtil.getDataSource("jdbc/FusionDB");
    }

    public static DataSource getFusionSlaveDataSource() {
        return LookupUtil.getDataSource("jdbc/FusionDBSlave");
    }

    public static DataSource getRegistrationMasterDataSource() {
        return LookupUtil.getDataSource("jdbc/UserRegistrationDB");
    }

    public static DataSource getRegistrationSlaveDataSource() {
        return LookupUtil.getDataSource("jdbc/UserRegistrationDBSlave");
    }
}

