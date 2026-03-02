/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.StringUtils
 */
package com.projectgoth.fusion.data;

import com.projectgoth.fusion.data.ServiceData;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import org.springframework.util.StringUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class GroupData
implements Serializable,
Comparable<GroupData> {
    public Integer id;
    public TypeEnum type;
    public Integer countryID;
    public String name;
    public String description;
    public String about;
    public Date dateCreated;
    public String createdBy;
    public String picture;
    public String emailAddress;
    public String referralSMS;
    public Boolean allowNonMembersToJoinRooms;
    public Integer groupCategoryID;
    public Integer vipServiceID;
    public Integer numOfMembers;
    public StatusEnum status;
    public Boolean pendingInvitation;
    public String categoryName;
    public Boolean supportsVIPs;

    public GroupData() {
    }

    public GroupData(ResultSet rs) throws SQLException {
        this.id = (Integer)rs.getObject("id");
        this.type = TypeEnum.fromValue(rs.getByte("type"));
        this.countryID = (Integer)rs.getObject("countryID");
        this.name = rs.getString("name");
        this.description = rs.getString("description");
        this.about = rs.getString("about");
        this.dateCreated = rs.getTimestamp("dateCreated");
        this.createdBy = rs.getString("createdBy");
        this.picture = rs.getString("picture");
        this.emailAddress = rs.getString("emailAddress");
        this.referralSMS = rs.getString("referralSMS");
        this.allowNonMembersToJoinRooms = (Boolean)rs.getObject("AllowNonMembersToJoinRooms");
        this.groupCategoryID = (Integer)rs.getObject("groupCategoryID");
        this.vipServiceID = (Integer)rs.getObject("vipServiceID");
        this.numOfMembers = (Integer)rs.getObject("NumMembers");
        Integer intVal = (Integer)rs.getObject("status");
        if (intVal != null) {
            this.status = StatusEnum.fromValue(intVal);
        }
        try {
            intVal = (Integer)rs.getObject("vipservicestatus");
            this.supportsVIPs = intVal != null && intVal.intValue() == ServiceData.StatusEnum.ACTIVE.value();
        }
        catch (SQLException e) {
            // empty catch block
        }
        try {
            String categoryName = rs.getString("categoryname");
            if (StringUtils.hasLength((String)categoryName)) {
                this.categoryName = categoryName;
            }
        }
        catch (SQLException sQLException) {
            // empty catch block
        }
    }

    @Override
    public int compareTo(GroupData groupData) {
        if (groupData == null) {
            return 1;
        }
        return this.id.compareTo(groupData.id);
    }

    public boolean isOpenGroup() {
        return this.type == TypeEnum.OPEN;
    }

    public boolean isClosedGroup() {
        return this.type == TypeEnum.CLOSED;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum TypeEnum {
        OPEN(0),
        CLOSED(1),
        UNLISTED(2);

        private byte value;

        private TypeEnum(byte value) {
            this.value = value;
        }

        public byte value() {
            return this.value;
        }

        public static TypeEnum fromValue(byte value) {
            for (TypeEnum e : TypeEnum.values()) {
                if (e.value() != value) continue;
                return e;
            }
            return null;
        }
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
}

