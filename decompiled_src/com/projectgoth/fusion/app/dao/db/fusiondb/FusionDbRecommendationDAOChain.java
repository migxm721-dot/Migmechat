/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.app.dao.db.fusiondb;

import com.projectgoth.fusion.app.dao.DAOException;
import com.projectgoth.fusion.app.dao.base.RecommendationDAOChain;
import com.projectgoth.fusion.app.dao.db.fusiondb.DBUtils;
import com.projectgoth.fusion.recommendation.RecommendationTransform;
import com.projectgoth.fusion.recommendation.RecommendationTransformParam;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FusionDbRecommendationDAOChain
extends RecommendationDAOChain {
    private static final Logger log = Logger.getLogger(FusionDbRecommendationDAOChain.class);

    @Override
    public Map<String, RecommendationTransformParam> getRecommendationTransformParamsByName(int transformID) throws DAOException {
        List<RecommendationTransformParam> params = this.getRecommendationTransformParams(transformID);
        HashMap<String, RecommendationTransformParam> map = new HashMap<String, RecommendationTransformParam>();
        for (RecommendationTransformParam p : params) {
            map.put(p.getName(), p);
        }
        return map;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<RecommendationTransformParam> getRecommendationTransformParams(int transformID) throws DAOException {
        ArrayList<RecommendationTransformParam> arrayList;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtils.getFusionReadConnection();
            String sql = "select * from recommendationtransformparam where transformid = ?";
            ps = conn.prepareStatement("select * from recommendationtransformparam where transformid = ?");
            ps.setInt(1, transformID);
            rs = ps.executeQuery();
            ArrayList<RecommendationTransformParam> results = new ArrayList<RecommendationTransformParam>();
            while (rs.next()) {
                RecommendationTransformParam param = new RecommendationTransformParam(rs.getString("name"), rs.getString("value"));
                results.add(param);
            }
            arrayList = results;
            Object var9_10 = null;
        }
        catch (Exception e) {
            List<RecommendationTransformParam> list;
            try {
                log.error((Object)("Unable to getRecommendationTransformParams with transformID=" + transformID + " e=" + e), (Throwable)e);
                list = super.getRecommendationTransformParams(transformID);
                Object var9_11 = null;
            }
            catch (Throwable throwable) {
                Object var9_12 = null;
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
    public RecommendationTransform getRecommendationTransform(int transformID) throws DAOException {
        RecommendationTransform recommendationTransform;
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block5: {
            RecommendationTransform rt;
            conn = null;
            ps = null;
            rs = null;
            conn = DBUtils.getFusionReadConnection();
            String sql = "select * from recommendationtransform where id=?";
            ps = conn.prepareStatement("select * from recommendationtransform where id=?");
            ps.setInt(1, transformID);
            rs = ps.executeQuery();
            if (!rs.next()) break block5;
            RecommendationTransform recommendationTransform2 = rt = new RecommendationTransform(rs);
            Object var9_11 = null;
            DBUtils.closeResource(rs, ps, conn, log);
            return recommendationTransform2;
        }
        try {
            recommendationTransform = null;
            Object var9_12 = null;
        }
        catch (Exception e) {
            RecommendationTransform recommendationTransform3;
            try {
                log.error((Object)("Unable to getRecommendationTransform with transformID=" + transformID + " e=" + e), (Throwable)e);
                recommendationTransform3 = super.getRecommendationTransform(transformID);
                Object var9_13 = null;
            }
            catch (Throwable throwable) {
                Object var9_14 = null;
                DBUtils.closeResource(rs, ps, conn, log);
                throw throwable;
            }
            DBUtils.closeResource(rs, ps, conn, log);
            return recommendationTransform3;
        }
        DBUtils.closeResource(rs, ps, conn, log);
        return recommendationTransform;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<RecommendationTransform> getActiveRecommendationTransforms() throws DAOException {
        ArrayList<RecommendationTransform> arrayList;
        if (log.isDebugEnabled()) {
            log.info((Object)("getRecommendationTransforms on FusionDbRecommendationDAOChain=" + this.hashCode()));
        }
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtils.getFusionReadConnection();
            String sql = "select * from recommendationtransform where status = 1";
            ps = conn.prepareStatement("select * from recommendationtransform where status = 1");
            rs = ps.executeQuery();
            ArrayList<RecommendationTransform> results = new ArrayList<RecommendationTransform>();
            while (rs.next()) {
                RecommendationTransform rt = new RecommendationTransform(rs);
                results.add(rt);
            }
            arrayList = results;
            Object var8_9 = null;
        }
        catch (Exception e) {
            List<RecommendationTransform> list;
            try {
                log.error((Object)"Unable to getRecommendationTransforms", (Throwable)e);
                list = super.getActiveRecommendationTransforms();
                Object var8_10 = null;
            }
            catch (Throwable throwable) {
                Object var8_11 = null;
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

