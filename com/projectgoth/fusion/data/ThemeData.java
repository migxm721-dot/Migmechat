/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.data;

import com.projectgoth.fusion.data.ReferenceStoreItemData;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ThemeData
extends ReferenceStoreItemData
implements Serializable {
    public Integer id;
    public String name;
    public String description;
    public String location;
    public StatusEnum status;

    public ThemeData() {
    }

    public ThemeData(ResultSet rs) throws SQLException {
        this.id = (Integer)rs.getObject("id");
        this.name = rs.getString("name");
        this.description = rs.getString("description");
        this.location = rs.getString("location");
        Integer intVal = (Integer)rs.getObject("status");
        if (intVal != null) {
            this.status = StatusEnum.fromValue(intVal);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum StatusEnum {
        AVAILABLE(1),
        NOT_AVAILABLE(0);

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

