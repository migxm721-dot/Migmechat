/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.data;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.fdl.enums.ChatroomCategoryRefreshType;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;

public class ChatroomCategoryData
implements Serializable {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ChatroomCategoryData.class));
    public Integer id;
    public String name;
    public Integer maxLevel;
    public StatusEnum status;
    public boolean itemsCanBeDeleted;
    public boolean initiallyCollapsed;
    public ChatroomCategoryRefreshType refreshMethod;
    public Integer orderIndex;
    public String refreshDisplayString;

    public ChatroomCategoryData(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.name = rs.getString("name");
        this.itemsCanBeDeleted = rs.getBoolean("itemscanbedeleted");
        this.initiallyCollapsed = rs.getBoolean("initiallycollapsed");
        this.maxLevel = rs.getInt("maxmiglevel");
        this.orderIndex = rs.getInt("orderindex");
        this.refreshDisplayString = rs.getString("refreshdisplaystring");
        Integer intval = (Integer)rs.getObject("status");
        if (intval != null) {
            this.status = StatusEnum.fromValue(intval);
        }
        if ((intval = (Integer)rs.getObject("refreshmethod")) != null) {
            this.refreshMethod = ChatroomCategoryRefreshType.fromValue((int)intval.byteValue());
        }
    }

    public byte intiallyCollapsedByteValue() {
        if (this.initiallyCollapsed) {
            return 1;
        }
        return 0;
    }

    public byte itemsCanBeDeletedByteValue() {
        if (this.itemsCanBeDeleted) {
            return 1;
        }
        return 0;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum StatusEnum {
        INACTIVE(0),
        ACTIVE(1);

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

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum SpecialCategoriesEnum {
        BOOKMARKED(1),
        RECENT(2),
        POPULAR(3),
        RECOMMENDED(8);

        private int value;

        private SpecialCategoriesEnum(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static SpecialCategoriesEnum fromValue(int value) {
            for (SpecialCategoriesEnum e : SpecialCategoriesEnum.values()) {
                if (e.value() != value) continue;
                return e;
            }
            return null;
        }
    }
}

