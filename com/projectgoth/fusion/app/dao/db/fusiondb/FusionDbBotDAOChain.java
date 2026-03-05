/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.app.dao.db.fusiondb;

import com.projectgoth.fusion.app.dao.DAOException;
import com.projectgoth.fusion.app.dao.base.BotDAOChain;
import com.projectgoth.fusion.app.dao.db.fusiondb.DBUtils;
import com.projectgoth.fusion.data.BotData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;

public class FusionDbBotDAOChain
extends BotDAOChain {
    private static final Logger log = Logger.getLogger(FusionDbBotDAOChain.class);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public BotData getBot(int botID) throws DAOException {
        BotData botData;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtils.getFusionReadConnection();
            ps = conn.prepareStatement("select * from bot where id = ? and status = 1");
            ps.setInt(1, botID);
            rs = ps.executeQuery();
            botData = rs.next() ? new BotData(rs) : null;
            Object var8_7 = null;
        }
        catch (SQLException e) {
            BotData botData2;
            try {
                log.error((Object)String.format("Failed to get BotData for bot:%s", botID), (Throwable)e);
                botData2 = super.getBot(botID);
                Object var8_8 = null;
            }
            catch (Throwable throwable) {
                Object var8_9 = null;
                DBUtils.closeResource(rs, ps, conn, log);
                throw throwable;
            }
            DBUtils.closeResource(rs, ps, conn, log);
            return botData2;
        }
        DBUtils.closeResource(rs, ps, conn, log);
        return botData;
    }
}

