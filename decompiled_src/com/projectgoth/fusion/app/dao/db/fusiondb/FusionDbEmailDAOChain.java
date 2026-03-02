/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.app.dao.db.fusiondb;

import com.projectgoth.fusion.app.dao.DAOException;
import com.projectgoth.fusion.app.dao.base.EmailDAOChain;
import com.projectgoth.fusion.app.dao.db.fusiondb.DBUtils;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;

public class FusionDbEmailDAOChain
extends EmailDAOChain {
    private static final Logger log = Logger.getLogger(FusionDbEmailDAOChain.class);
    private static final String TRANSIENT = "Transient";

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isBounceEmailAddress(String email) throws DAOException {
        boolean bl;
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block6: {
            block7: {
                conn = null;
                ps = null;
                rs = null;
                conn = DBUtils.getFusionReadConnection();
                ps = conn.prepareStatement("SELECT bounceType FROM bouncedb WHERE emailaddress = ? limit 1");
                ps.setString(1, email);
                rs = ps.executeQuery();
                if (!rs.next()) break block6;
                if (!SystemProperty.getBool(SystemPropertyEntities.UserNotificationServiceSettings.ENABLED_SEND_TO_TRANSIENT_EMAIL) || !TRANSIENT.equalsIgnoreCase(rs.getString("bounceType"))) break block7;
                boolean bl2 = false;
                Object var8_9 = null;
                DBUtils.closeResource(rs, ps, conn, log);
                return bl2;
            }
            boolean bl3 = true;
            Object var8_10 = null;
            DBUtils.closeResource(rs, ps, conn, log);
            return bl3;
        }
        try {
            bl = false;
            Object var8_11 = null;
        }
        catch (SQLException e) {
            boolean bl4;
            try {
                log.error((Object)String.format("Failed to check isBounceEmailAddress for email:%s", email), (Throwable)e);
                bl4 = super.isBounceEmailAddress(email);
                Object var8_12 = null;
            }
            catch (Throwable throwable) {
                Object var8_13 = null;
                DBUtils.closeResource(rs, ps, conn, log);
                throw throwable;
            }
            DBUtils.closeResource(rs, ps, conn, log);
            return bl4;
        }
        DBUtils.closeResource(rs, ps, conn, log);
        return bl;
    }
}

