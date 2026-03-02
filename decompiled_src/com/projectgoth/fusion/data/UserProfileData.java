/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.data;

import com.projectgoth.fusion.data.UserData;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class UserProfileData
implements Serializable {
    public Integer id;
    public String username;
    public String firstName;
    public String lastName;
    public String homeTown;
    public String city;
    public String state;
    public Date dateOfBirth;
    public GenderEnum gender;
    public String jobs;
    public String schools;
    public String hobbies;
    public String likes;
    public String dislikes;
    public String aboutMe;
    public RelationshipStatusEnum relationshipStatus;
    public StatusEnum status;
    public Boolean anonymousViewing;
    public Integer numProfileViews;

    public UserProfileData() {
    }

    public UserProfileData(UserData userData) {
        this.id = userData.userID;
        this.username = userData.username;
        this.firstName = "";
        this.lastName = "";
        this.homeTown = "";
        this.city = "";
        this.state = "";
        this.dateOfBirth = userData.dateRegistered;
        this.gender = GenderEnum.FEMALE;
        this.jobs = "";
        this.schools = "";
        this.hobbies = "";
        this.likes = "";
        this.dislikes = "";
        this.aboutMe = "";
        this.relationshipStatus = RelationshipStatusEnum.COMPLICATED;
        this.status = StatusEnum.PUBLIC;
        this.anonymousViewing = false;
    }

    public UserProfileData(ResultSet rs) throws SQLException {
        Integer intVal;
        this.id = (Integer)rs.getObject("id");
        this.username = rs.getString("username");
        this.firstName = rs.getString("firstName");
        this.lastName = rs.getString("lastName");
        this.homeTown = rs.getString("homeTown");
        this.city = rs.getString("city");
        this.state = rs.getString("state");
        this.dateOfBirth = rs.getTimestamp("dateOfBirth");
        this.jobs = rs.getString("jobs");
        this.schools = rs.getString("schools");
        this.hobbies = rs.getString("hobbies");
        this.likes = rs.getString("likes");
        this.dislikes = rs.getString("dislikes");
        this.aboutMe = rs.getString("aboutMe");
        String strVal = rs.getString("gender");
        if (strVal != null) {
            this.gender = GenderEnum.fromValue(strVal);
        }
        if ((intVal = (Integer)rs.getObject("relationshipStatus")) != null) {
            this.relationshipStatus = RelationshipStatusEnum.fromValue(intVal);
        }
        if ((intVal = (Integer)rs.getObject("status")) != null) {
            this.status = StatusEnum.fromValue(intVal);
        }
    }

    public boolean isDifferent(UserProfileData oldProfileData) {
        if (this.username != null && !this.username.equals(oldProfileData.username)) {
            return true;
        }
        if (this.firstName != null && !this.firstName.equals(oldProfileData.firstName)) {
            return true;
        }
        if (this.lastName != null && !this.lastName.equals(oldProfileData.lastName)) {
            return true;
        }
        if (this.homeTown != null && !this.homeTown.equals(oldProfileData.homeTown)) {
            return true;
        }
        if (this.city != null && !this.city.equals(oldProfileData.city)) {
            return true;
        }
        if (this.state != null && !this.state.equals(oldProfileData.state)) {
            return true;
        }
        if (this.dateOfBirth != null && !this.dateOfBirth.equals(oldProfileData.dateOfBirth)) {
            return true;
        }
        if (this.gender != null && this.gender != oldProfileData.gender) {
            return true;
        }
        if (this.jobs != null && !this.jobs.equals(oldProfileData.jobs)) {
            return true;
        }
        if (this.schools != null && !this.schools.equals(oldProfileData.schools)) {
            return true;
        }
        if (this.hobbies != null && !this.hobbies.equals(oldProfileData.hobbies)) {
            return true;
        }
        if (this.likes != null && !this.likes.equals(oldProfileData.likes)) {
            return true;
        }
        if (this.dislikes != null && !this.dislikes.equals(oldProfileData.dislikes)) {
            return true;
        }
        if (this.aboutMe != null && !this.aboutMe.equals(oldProfileData.aboutMe)) {
            return true;
        }
        if (this.relationshipStatus != null && this.relationshipStatus != oldProfileData.relationshipStatus) {
            return true;
        }
        if (this.status != null && this.status != oldProfileData.status) {
            return true;
        }
        return this.anonymousViewing != null && !this.anonymousViewing.equals(oldProfileData.anonymousViewing);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum StatusEnum {
        PUBLIC(1),
        CONTACTS_ONLY(2),
        PRIVATE(3);

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
    public static enum RelationshipStatusEnum {
        SINGLE(1),
        IN_A_RELATIONSHIP(2),
        DOMESTIC_PARTNER(3),
        MARRIED(4),
        COMPLICATED(5);

        private int value;

        private RelationshipStatusEnum(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static RelationshipStatusEnum fromValue(int value) {
            for (RelationshipStatusEnum e : RelationshipStatusEnum.values()) {
                if (e.value() != value) continue;
                return e;
            }
            return null;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum GenderEnum {
        MALE("M"),
        FEMALE("F");

        private String value;

        private GenderEnum(String value) {
            this.value = value;
        }

        public String value() {
            return this.value;
        }

        public static GenderEnum fromValue(String value) {
            for (GenderEnum e : GenderEnum.values()) {
                if (!value.equals(e.value())) continue;
                return e;
            }
            return null;
        }
    }
}

