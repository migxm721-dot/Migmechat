/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.ejb;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

public class ConnectionHolder {
    private DataSource dataSource;
    private Connection conn;
    private boolean created;
    private boolean closed;

    public ConnectionHolder(DataSource dataSource, Connection conn) {
        this.dataSource = dataSource;
        this.conn = conn;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void close() throws SQLException {
        try {
            if (this.created && this.conn != null) {
                this.conn.close();
            }
            Object var2_1 = null;
            this.conn = null;
            this.closed = true;
        }
        catch (Throwable throwable) {
            Object var2_2 = null;
            this.conn = null;
            this.closed = true;
            throw throwable;
        }
    }

    public Connection getConnection() throws SQLException {
        if (this.closed) {
            throw new IllegalStateException();
        }
        if (this.conn == null) {
            this.conn = this.dataSource.getConnection();
            this.created = true;
        }
        return this.conn;
    }
}

