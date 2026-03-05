/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.app.dao.db.fusiondb;

import com.projectgoth.fusion.app.dao.DAOException;
import com.projectgoth.fusion.app.dao.base.CampaignDataDAOChain;
import com.projectgoth.fusion.app.dao.db.fusiondb.DBUtils;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.CampaignData;
import com.projectgoth.fusion.data.CampaignParticipantData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FusionDbCampaignDataDAOChain
extends CampaignDataDAOChain {
    private static final Logger log = Logger.getLogger(FusionDbCampaignDataDAOChain.class);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CampaignData getCampaignData(int campaignid) throws DAOException {
        CampaignData campaignData;
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block5: {
            conn = null;
            ps = null;
            rs = null;
            conn = DBUtils.getFusionReadConnection();
            ps = conn.prepareStatement("SELECT * FROM campaign WHERE id = ?");
            ps.setInt(1, campaignid);
            rs = ps.executeQuery();
            if (!rs.next()) break block5;
            CampaignData campaignData2 = new CampaignData(rs);
            Object var8_8 = null;
            DBUtils.closeResource(rs, ps, conn, log);
            return campaignData2;
        }
        try {
            campaignData = null;
            Object var8_9 = null;
        }
        catch (SQLException e) {
            CampaignData campaignData3;
            try {
                log.error((Object)String.format("Failed to check getCampaignData for campaignid:%s", campaignid), (Throwable)e);
                campaignData3 = super.getCampaignData(campaignid);
                Object var8_10 = null;
            }
            catch (Throwable throwable) {
                Object var8_11 = null;
                DBUtils.closeResource(rs, ps, conn, log);
                throw throwable;
            }
            DBUtils.closeResource(rs, ps, conn, log);
            return campaignData3;
        }
        DBUtils.closeResource(rs, ps, conn, log);
        return campaignData;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CampaignParticipantData getCampaignParticipantData(int userid, int campaignid) throws DAOException {
        CampaignParticipantData campaignParticipantData;
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block5: {
            conn = null;
            ps = null;
            rs = null;
            conn = DBUtils.getFusionReadConnection();
            ps = conn.prepareStatement("SELECT * FROM campaignparticipant WHERE campaignid = ? and userid =?");
            ps.setInt(1, campaignid);
            ps.setInt(2, userid);
            rs = ps.executeQuery();
            if (!rs.next()) break block5;
            CampaignParticipantData campaignParticipantData2 = new CampaignParticipantData(rs);
            Object var9_9 = null;
            DBUtils.closeResource(rs, ps, conn, log);
            return campaignParticipantData2;
        }
        try {
            campaignParticipantData = null;
            Object var9_10 = null;
        }
        catch (SQLException e) {
            CampaignParticipantData campaignParticipantData3;
            try {
                log.error((Object)String.format("Failed to check getCampaignUserData for campaignid:%s userid:%s", campaignid, userid), (Throwable)e);
                campaignParticipantData3 = super.getCampaignParticipantData(campaignid, userid);
                Object var9_11 = null;
            }
            catch (Throwable throwable) {
                Object var9_12 = null;
                DBUtils.closeResource(rs, ps, conn, log);
                throw throwable;
            }
            DBUtils.closeResource(rs, ps, conn, log);
            return campaignParticipantData3;
        }
        DBUtils.closeResource(rs, ps, conn, log);
        return campaignParticipantData;
    }

    @Override
    public List<CampaignParticipantData> getActiveCampaignParticipantDataByType(int userid) throws DAOException {
        int[] type = SystemProperty.getIntArray(SystemPropertyEntities.Campaign.CAMPAIGN_TYPE_WHITELIST);
        ArrayList<CampaignParticipantData> results = new ArrayList<CampaignParticipantData>();
        for (int i = 0; i < type.length; ++i) {
            results.addAll(this.getActiveCampaignParticipantDataByType(userid, type[i]));
        }
        return results;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CampaignParticipantData> getActiveCampaignParticipantDataByType(int userid, int type) throws DAOException {
        ArrayList<CampaignParticipantData> arrayList;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<CampaignParticipantData> results = new ArrayList<CampaignParticipantData>();
        try {
            conn = DBUtils.getFusionReadConnection();
            ps = conn.prepareStatement("SELECT cp.* FROM campaignparticipant cp join campaign c on c.id= cp.campaignid WHERE c.type=? and cp.userid =? and c.status=1 and c.startdate<now() and c.enddate>now()");
            ps.setInt(1, type);
            ps.setInt(2, userid);
            rs = ps.executeQuery();
            while (rs.next()) {
                results.add(new CampaignParticipantData(rs));
            }
            arrayList = results;
            Object var10_9 = null;
        }
        catch (SQLException e) {
            List<CampaignParticipantData> list;
            try {
                log.error((Object)String.format("Failed to check getCampaignUserData for campaignid:%s userid:%s", type, userid), (Throwable)e);
                list = super.getActiveCampaignParticipantDataByType(type, userid);
                Object var10_10 = null;
            }
            catch (Throwable throwable) {
                Object var10_11 = null;
                DBUtils.closeResource(rs, ps, conn, log);
                throw throwable;
            }
            DBUtils.closeResource(rs, ps, conn, log);
            return list;
        }
        DBUtils.closeResource(rs, ps, conn, log);
        return arrayList;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CampaignParticipantData getCampaignParticipantDataByMobilePhone(String mobilePhone, int campaignId) throws DAOException {
        CampaignParticipantData campaignParticipantData;
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block5: {
            conn = null;
            ps = null;
            rs = null;
            conn = DBUtils.getFusionReadConnection();
            ps = conn.prepareStatement("SELECT * FROM campaignparticipant WHERE campaignid = ? and mobilephone =?");
            ps.setInt(1, campaignId);
            ps.setString(2, mobilePhone);
            rs = ps.executeQuery();
            if (!rs.next()) break block5;
            CampaignParticipantData campaignParticipantData2 = new CampaignParticipantData(rs);
            Object var9_9 = null;
            DBUtils.closeResource(rs, ps, conn, log);
            return campaignParticipantData2;
        }
        try {
            campaignParticipantData = null;
            Object var9_10 = null;
        }
        catch (SQLException e) {
            CampaignParticipantData campaignParticipantData3;
            try {
                log.error((Object)String.format("Failed to check getCampaignUserData for campaignid:%s mobilePhone:%s", campaignId, mobilePhone), (Throwable)e);
                campaignParticipantData3 = super.getCampaignParticipantDataByMobilePhone(mobilePhone, campaignId);
                Object var9_11 = null;
            }
            catch (Throwable throwable) {
                Object var9_12 = null;
                DBUtils.closeResource(rs, ps, conn, log);
                throw throwable;
            }
            DBUtils.closeResource(rs, ps, conn, log);
            return campaignParticipantData3;
        }
        DBUtils.closeResource(rs, ps, conn, log);
        return campaignParticipantData;
    }

    @Override
    public CampaignParticipantData joinCampaign(CampaignParticipantData campaignParticipantData) throws DAOException {
        if (SystemPropertyEntities.Temp.Cache.se604UserAgentTrackingEnabled.getValue().booleanValue()) {
            return this.insertCampaignParticipantData(campaignParticipantData);
        }
        return this.joinCampaignPreSE604(campaignParticipantData);
    }

    /*
     * Exception decompiling
     */
    private CampaignParticipantData insertCampaignParticipantData(CampaignParticipantData campaignParticipantData) throws DAOException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 3 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    private CampaignParticipantData joinCampaignPreSE604(CampaignParticipantData campaignParticpantData) throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            try {
                conn = DBUtils.getFusionWriteConnection();
                ps = conn.prepareStatement("INSERT  INTO campaignparticipant (campaignid,userid, datecreated, mobilephone,emailaddress,reference) values (?, ?, now(),?,?,?)");
                ps.setInt(1, campaignParticpantData.getCampaignId());
                ps.setInt(2, campaignParticpantData.getUserId());
                ps.setString(3, campaignParticpantData.getMobilePhone());
                ps.setString(4, campaignParticpantData.getEmailAddress());
                ps.setString(5, campaignParticpantData.getReference());
                ps.executeUpdate();
            }
            catch (SQLException e) {
                log.error((Object)String.format("Failed to check joinCampaign for campaignid:%s userid:%s", campaignParticpantData.getCampaignId(), campaignParticpantData.getUserId()), (Throwable)e);
                throw new DAOException(String.format("Failed to join campaign for campaignid:%s userid:%s", campaignParticpantData.getCampaignId(), campaignParticpantData.getUserId()), e);
            }
            Object var7_5 = null;
        }
        catch (Throwable throwable) {
            Object var7_6 = null;
            DBUtils.closeResource(rs, ps, conn, log);
            throw throwable;
        }
        DBUtils.closeResource(rs, ps, conn, log);
        return campaignParticpantData;
    }
}

