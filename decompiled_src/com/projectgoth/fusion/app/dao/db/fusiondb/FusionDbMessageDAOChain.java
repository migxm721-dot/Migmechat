/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.app.dao.db.fusiondb;

import com.projectgoth.fusion.app.dao.DAOException;
import com.projectgoth.fusion.app.dao.base.MessageDAOChain;
import com.projectgoth.fusion.app.dao.db.fusiondb.DBUtils;
import com.projectgoth.fusion.data.AlertMessageData;
import com.projectgoth.fusion.fdl.enums.AlertContentType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FusionDbMessageDAOChain
extends MessageDAOChain {
    private static final Logger log = Logger.getLogger(FusionDbMessageDAOChain.class);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Map<Integer, String> loadHelpTexts() throws DAOException {
        HashMap<Integer, String> hashMap;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtils.getFusionReadConnection();
            ps = conn.prepareStatement("select * from clienttext");
            rs = ps.executeQuery();
            HashMap<Integer, String> helpTexts = new HashMap<Integer, String>();
            while (rs.next()) {
                int type = rs.getInt("Type");
                if (type != 1) continue;
                helpTexts.put(rs.getInt("ID"), rs.getString("Text"));
            }
            hashMap = helpTexts;
            Object var7_9 = null;
        }
        catch (SQLException e) {
            Map<Integer, String> map;
            try {
                log.error((Object)"Failed to loadHelpTexts", (Throwable)e);
                map = super.loadHelpTexts();
                Object var7_10 = null;
            }
            catch (Throwable throwable) {
                Object var7_11 = null;
                DBUtils.closeResource(rs, ps, conn, log);
                throw throwable;
            }
            DBUtils.closeResource(rs, ps, conn, log);
            return map;
        }
        DBUtils.closeResource(rs, ps, conn, log);
        return hashMap;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Map<Integer, String> loadInfoTexts() throws DAOException {
        HashMap<Integer, String> hashMap;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtils.getFusionReadConnection();
            ps = conn.prepareStatement("select * from clienttext");
            rs = ps.executeQuery();
            HashMap<Integer, String> infoTexts = new HashMap<Integer, String>();
            while (rs.next()) {
                int type = rs.getInt("Type");
                if (type != 2) continue;
                infoTexts.put(rs.getInt("ID"), rs.getString("Text"));
            }
            hashMap = infoTexts;
            Object var7_9 = null;
        }
        catch (SQLException e) {
            Map<Integer, String> map;
            try {
                log.error((Object)"Failed to loadInfoTexts", (Throwable)e);
                map = super.loadInfoTexts();
                Object var7_10 = null;
            }
            catch (Throwable throwable) {
                Object var7_11 = null;
                DBUtils.closeResource(rs, ps, conn, log);
                throw throwable;
            }
            DBUtils.closeResource(rs, ps, conn, log);
            return map;
        }
        DBUtils.closeResource(rs, ps, conn, log);
        return hashMap;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<AlertMessageData> getLatestAlertMessageList(int midletVersion, AlertMessageData.TypeEnum type, int countryId, Date minimumDate, AlertContentType alertContentType, int clientType) throws DAOException {
        ArrayList<AlertMessageData> arrayList;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtils.getFusionReadConnection();
            String sql = "select * from alertmessage where MinMidletVersion <= ? and MaxMidletVersion >= ? and Type = ? and (CountryID = ? or CountryID is null) and StartDate <= now() and ExpiryDate > now() and Status = ? and clientType = ?";
            if (alertContentType != null) {
                sql = sql + " and ContentType = ?";
            }
            sql = sql + " order by CountryID";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, midletVersion);
            ps.setInt(2, midletVersion);
            ps.setInt(3, type.value());
            ps.setInt(4, countryId);
            ps.setInt(5, AlertMessageData.StatusEnum.ACTIVE.value());
            ps.setInt(6, clientType);
            if (alertContentType != null) {
                ps.setInt(7, alertContentType.value());
            }
            rs = ps.executeQuery();
            ArrayList<AlertMessageData> alertMessages = new ArrayList<AlertMessageData>();
            while (rs.next()) {
                alertMessages.add(new AlertMessageData(rs));
            }
            arrayList = alertMessages;
            Object var14_15 = null;
        }
        catch (SQLException e) {
            List<AlertMessageData> list;
            try {
                log.error((Object)String.format("Unable to get LatestAlertMessage List for midletVersion:%s, type:%s, country:%s, date:%s, contentype:%s, clientType:%s ", new Object[]{midletVersion, type, countryId, minimumDate, alertContentType, clientType}), (Throwable)e);
                list = super.getLatestAlertMessageList(midletVersion, type, countryId, minimumDate, alertContentType, clientType);
                Object var14_16 = null;
            }
            catch (Throwable throwable) {
                Object var14_17 = null;
                DBUtils.closeResource(rs, ps, conn, log);
                throw throwable;
            }
            DBUtils.closeResource(rs, ps, conn, log);
            return list;
        }
        DBUtils.closeResource(rs, ps, conn, log);
        return arrayList;
    }
}

