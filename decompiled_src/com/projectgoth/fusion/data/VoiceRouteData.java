/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.data;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class VoiceRouteData
implements Serializable,
Comparable<VoiceRouteData> {
    public Integer iddCode;
    public String areaCode;
    public Integer gatewayID;
    public Integer providerID;
    public Integer priority;
    public String dialCommand;

    public VoiceRouteData() {
    }

    public VoiceRouteData(ResultSet rs) throws SQLException {
        this.iddCode = rs.getInt("iddCode");
        this.areaCode = rs.getString("areaCode");
        this.gatewayID = rs.getInt("gatewayId");
        this.providerID = rs.getInt("providerId");
        this.priority = rs.getInt("priority");
        this.dialCommand = rs.getString("dialCommand");
    }

    @Override
    public int compareTo(VoiceRouteData o) {
        return this.priority.compareTo(o.priority);
    }
}

