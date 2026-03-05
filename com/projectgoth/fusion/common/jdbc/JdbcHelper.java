/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.common.jdbc;

import com.projectgoth.fusion.common.jdbc.ConnectionCreator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class JdbcHelper {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void execQuery(Connection conn, String sql, ParameterSetup setup, ResultSetHandler rsHandler) throws Exception {
        PreparedStatement ps = conn.prepareStatement(sql);
        try {
            setup.setParamValues(ps);
            ResultSet rs = ps.executeQuery();
            try {
                rsHandler.handle(rs);
                Object var7_6 = null;
            }
            catch (Throwable throwable) {
                Object var7_7 = null;
                rs.close();
                throw throwable;
            }
            rs.close();
            Object var9_9 = null;
        }
        catch (Throwable throwable) {
            Object var9_10 = null;
            ps.close();
            throw throwable;
        }
        ps.close();
    }

    public static void execQuery(Connection conn, String sql, QueryHandler queryHandler) throws Exception {
        JdbcHelper.execQuery(conn, sql, queryHandler, queryHandler);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void execQuery(ConnectionCreator connCreator, String sql, QueryHandler queryHandler) throws Exception {
        Connection conn = connCreator.create();
        try {
            JdbcHelper.execQuery(conn, sql, queryHandler, queryHandler);
            Object var5_4 = null;
        }
        catch (Throwable throwable) {
            Object var5_5 = null;
            conn.close();
            throw throwable;
        }
        conn.close();
    }

    public static abstract class QueryHandlerAdapter
    implements QueryHandler {
        public void setParamValues(PreparedStatement preparedStatement) throws Exception {
        }
    }

    public static interface QueryHandler
    extends ParameterSetup,
    ResultSetHandler {
    }

    public static interface ParameterSetup {
        public void setParamValues(PreparedStatement var1) throws Exception;
    }

    public static interface ResultSetHandler {
        public void handle(ResultSet var1) throws Exception;
    }
}

