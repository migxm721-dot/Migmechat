/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.data;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExternalDownloadLinkData
implements Serializable {
    public int id;
    public String url;
    public int hitRate;
    public StatusEnum status;
    public int startRange;
    public int endRange;
    public String version;

    public ExternalDownloadLinkData(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.url = rs.getString("url");
        this.hitRate = rs.getInt("hitrate");
        this.status = StatusEnum.fromValue(rs.getInt("status"));
        this.version = rs.getString("version");
    }

    public void setRange(int startRange) {
        this.startRange = startRange;
        this.endRange = startRange + this.hitRate;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum StatusEnum {
        AVAILABLE(1),
        UNAVAILABLE(0);

        private int value;

        private StatusEnum(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static StatusEnum fromValue(int value) {
            for (StatusEnum e : StatusEnum.values()) {
                if (e.value() != value) continue;
                return e;
            }
            return null;
        }
    }
}

