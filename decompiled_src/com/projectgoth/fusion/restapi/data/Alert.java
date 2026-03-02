/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.restapi.data;

import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Alert
implements Comparable<Alert> {
    String timestamp;
    Integer alertType;
    String alertKey;
    Map<String, String> alertData;

    public Alert(String timestamp, Integer alertType, String alertKey, Map<String, String> alertData) {
        this.timestamp = timestamp;
        this.alertType = alertType;
        this.alertKey = alertKey;
        this.alertData = alertData;
    }

    @Override
    public int compareTo(Alert a) {
        if (a == null) {
            return 1;
        }
        return a.timestamp.compareTo(this.timestamp);
    }

    public boolean equals(Object a) {
        if (a.getClass().equals(this.getClass())) {
            return this.equals((Alert)a);
        }
        return false;
    }

    public boolean equals(Alert a) {
        if (this.timestamp == null || this.alertType == null || this.alertKey == null) {
            return false;
        }
        return this.timestamp.equals(a.timestamp) && this.alertType.equals(a.alertType) && this.alertKey.equals(a.alertKey);
    }
}

