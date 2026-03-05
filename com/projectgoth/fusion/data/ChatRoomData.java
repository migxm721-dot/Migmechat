/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.StringUtils
 */
package com.projectgoth.fusion.data;

import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.slice.ChatRoomDataIce;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.util.StringUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ChatRoomData
implements Serializable,
Comparable {
    public Integer id;
    public String name;
    public volatile String description;
    public volatile TypeEnum type;
    public volatile String creator;
    public Integer primaryCountryID;
    public Integer secondaryCountryID;
    public Integer locationID;
    public volatile Integer groupID;
    public Integer botID;
    public volatile Boolean adultOnly;
    public volatile Integer maximumSize;
    public volatile Boolean userOwned;
    public String newOwner;
    public volatile Boolean allowKicking;
    public Boolean allowUserKeywords;
    public Boolean allowBots;
    public String language;
    public Date dateCreated;
    public Date dateLastAccessed;
    public StatusEnum status;
    public Integer themeID;
    public String[] keywords;
    public Set<String> moderators;
    public Set<String> bannedUsers;
    public Map<String, String> theme;
    public Integer size = 0;
    private String lockUser;
    private String announcer = null;
    private String announceMessage = "";
    public Integer minMigLevel;
    public String rateLimitByIp;
    public Integer blockPeriodByIpInSeconds;

    public ChatRoomData() {
    }

    public ChatRoomData(ChatRoomData other) {
        this.id = other.id;
        this.name = other.name;
        this.description = other.description;
        this.type = other.type;
        this.creator = other.creator;
        this.primaryCountryID = other.primaryCountryID;
        this.secondaryCountryID = other.secondaryCountryID;
        this.locationID = other.locationID;
        this.groupID = other.groupID;
        this.botID = other.botID;
        this.adultOnly = other.adultOnly;
        this.maximumSize = other.maximumSize;
        this.userOwned = other.userOwned;
        this.newOwner = other.newOwner;
        this.allowKicking = other.allowKicking;
        this.allowUserKeywords = other.allowUserKeywords;
        this.allowBots = other.allowBots;
        this.language = other.language;
        this.dateCreated = other.dateCreated;
        this.dateLastAccessed = other.dateLastAccessed;
        this.status = other.status;
        this.themeID = other.themeID;
        this.keywords = other.keywords;
        if (SystemPropertyEntities.Temp.Cache.se351ChatRoomDataConcurrentCollectionsEnabled.getValue().booleanValue()) {
            this.moderators = Collections.newSetFromMap(new ConcurrentHashMap());
            this.moderators.addAll(other.moderators);
            this.bannedUsers = Collections.newSetFromMap(new ConcurrentHashMap());
            this.bannedUsers.addAll(other.bannedUsers);
            this.theme = other.theme == null ? null : new ConcurrentHashMap<String, String>(other.theme);
        } else {
            this.moderators = other.moderators == null ? null : new HashSet<String>(other.moderators);
            this.bannedUsers = other.bannedUsers == null ? null : new HashSet<String>(other.bannedUsers);
            this.theme = other.theme == null ? null : new HashMap<String, String>(other.theme);
        }
        this.size = other.size;
        this.lockUser = other.lockUser;
        this.announcer = other.announcer;
        this.announceMessage = other.announceMessage;
        this.minMigLevel = other.minMigLevel;
        this.rateLimitByIp = other.rateLimitByIp;
        this.blockPeriodByIpInSeconds = other.blockPeriodByIpInSeconds;
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof ChatRoomData)) {
            return false;
        }
        ChatRoomData c = (ChatRoomData)o;
        return this.name != null && this.name.equals(c.name);
    }

    public int compareTo(Object o) {
        ChatRoomData c = (ChatRoomData)o;
        if (this.groupID == null && c.groupID != null) {
            return 1;
        }
        if (this.groupID != null && c.groupID == null) {
            return -1;
        }
        if (this.name == null || this.size == null || this.maximumSize == null || this.dateLastAccessed == null) {
            return 1;
        }
        if (c.name == null || c.size == null || c.maximumSize == null | c.dateLastAccessed == null) {
            return -1;
        }
        if (this.name.equals(c.name)) {
            return 0;
        }
        if (this.isEmpty()) {
            if (c.isEmpty()) {
                return c.dateLastAccessed.compareTo(this.dateLastAccessed);
            }
            return 1;
        }
        if (this.isFull()) {
            if (c.isEmpty()) {
                return -1;
            }
            if (c.isFull()) {
                return c.dateLastAccessed.compareTo(this.dateLastAccessed);
            }
            return 1;
        }
        if (this.isLargelyEmpty()) {
            if (c.isFull() || c.isEmpty()) {
                return -1;
            }
            if (c.isLargelyEmpty()) {
                return c.dateLastAccessed.compareTo(this.dateLastAccessed);
            }
            return 1;
        }
        if (c.isLargelyEmpty() || c.isFull() || c.isEmpty()) {
            return -1;
        }
        return c.dateLastAccessed.compareTo(this.dateLastAccessed);
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public boolean isFull() {
        return this.size >= this.maximumSize;
    }

    public boolean isStadium() {
        return this.type == TypeEnum.STADIUM;
    }

    public boolean isUserOwned() {
        return this.type == TypeEnum.CHATROOM && this.userOwned != false;
    }

    public boolean isLargelyEmpty() {
        return this.size.doubleValue() / this.maximumSize.doubleValue() < 0.2;
    }

    public String getName() {
        return this.name;
    }

    public String getCreator() {
        return this.creator;
    }

    public boolean belongsToGroup() {
        return this.groupID != null && this.groupID > 0;
    }

    public boolean isModerator(String username) {
        return this.moderators != null && this.moderators.contains(username);
    }

    public boolean isOnBannedList(String username) {
        return this.bannedUsers != null && this.bannedUsers.contains(username);
    }

    public boolean isLocked() {
        return this.lockUser != null;
    }

    public String getLocker() {
        return this.lockUser;
    }

    public void lock(String locker) {
        this.lockUser = locker;
    }

    public String unlock() {
        String oldLocker = this.lockUser;
        this.lockUser = null;
        return oldLocker;
    }

    public boolean isAnnouncementOn() {
        return this.announcer != null;
    }

    public void setAnnouncementOn(String announcer, String announceMessage) {
        this.announcer = announcer;
        this.announceMessage = announceMessage;
    }

    public String setAnnouncementOff() {
        String oldAnnouncer = this.announcer;
        this.announcer = null;
        this.announceMessage = "";
        return oldAnnouncer;
    }

    public String getAnnouncer() {
        return this.announcer;
    }

    public String getAnnounceMessage() {
        return this.announceMessage;
    }

    public ChatRoomData(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.name = rs.getString("name");
        this.description = rs.getString("description");
        this.creator = rs.getString("creator");
        this.primaryCountryID = (Integer)rs.getObject("primaryCountryID");
        this.secondaryCountryID = (Integer)rs.getObject("secondaryCountryID");
        this.locationID = (Integer)rs.getObject("locationID");
        this.groupID = (Integer)rs.getObject("groupID");
        this.botID = (Integer)rs.getObject("botID");
        this.maximumSize = (Integer)rs.getObject("maximumSize");
        this.userOwned = rs.getBoolean("userOwned");
        this.newOwner = rs.getString("newOwner");
        this.allowKicking = rs.getBoolean("allowKicking");
        this.allowUserKeywords = rs.getBoolean("allowUserKeywords");
        this.allowBots = rs.getBoolean("allowBots");
        this.language = rs.getString("language");
        this.dateLastAccessed = rs.getTimestamp("dateLastAccessed");
        this.dateCreated = rs.getTimestamp("dateCreated");
        this.themeID = (Integer)rs.getObject("chatRoomThemeID");
        Integer intValue = (Integer)rs.getObject("type");
        if (intValue != null) {
            this.type = TypeEnum.fromValue(intValue);
        }
        if ((intValue = (Integer)rs.getObject("adultOnly")) != null) {
            this.adultOnly = intValue == 1;
        }
        if ((intValue = (Integer)rs.getObject("status")) != null) {
            this.status = StatusEnum.fromValue(intValue);
        }
        try {
            String keywordsCSV = rs.getString("keywords");
            if (StringUtils.hasLength((String)keywordsCSV)) {
                this.keywords = keywordsCSV.split(",");
            }
        }
        catch (SQLException sQLException) {
            // empty catch block
        }
    }

    public ChatRoomData(ChatRoomDataIce chatRoomIce) {
        this.id = chatRoomIce.id == Integer.MIN_VALUE ? null : Integer.valueOf(chatRoomIce.id);
        this.name = chatRoomIce.name.equals("\u0000") ? null : chatRoomIce.name;
        this.description = chatRoomIce.description.equals("\u0000") ? null : chatRoomIce.description;
        this.creator = chatRoomIce.creator.equals("\u0000") ? null : chatRoomIce.creator;
        this.type = chatRoomIce.type == Integer.MIN_VALUE ? null : TypeEnum.fromValue(chatRoomIce.type);
        this.primaryCountryID = chatRoomIce.primaryCountryID == Integer.MIN_VALUE ? null : Integer.valueOf(chatRoomIce.primaryCountryID);
        this.secondaryCountryID = chatRoomIce.secondaryCountryID == Integer.MIN_VALUE ? null : Integer.valueOf(chatRoomIce.secondaryCountryID);
        this.locationID = chatRoomIce.locationID == Integer.MIN_VALUE ? null : Integer.valueOf(chatRoomIce.locationID);
        this.groupID = chatRoomIce.groupID == Integer.MIN_VALUE ? null : Integer.valueOf(chatRoomIce.groupID);
        Integer n = this.botID = chatRoomIce.botID == Integer.MIN_VALUE ? null : Integer.valueOf(chatRoomIce.botID);
        this.adultOnly = chatRoomIce.adultOnly == Integer.MIN_VALUE ? null : Boolean.valueOf(chatRoomIce.adultOnly == 1);
        Integer n2 = this.maximumSize = chatRoomIce.maximumSize == Integer.MIN_VALUE ? null : Integer.valueOf(chatRoomIce.maximumSize);
        this.userOwned = chatRoomIce.userOwned == Integer.MIN_VALUE ? null : Boolean.valueOf(chatRoomIce.userOwned == 1);
        String string = this.newOwner = chatRoomIce.newOwner.equals("\u0000") ? null : chatRoomIce.newOwner;
        Boolean bl = chatRoomIce.allowKicking == Integer.MIN_VALUE ? null : (this.allowKicking = Boolean.valueOf(chatRoomIce.allowKicking == 1));
        Boolean bl2 = chatRoomIce.allowUserKeywords == Integer.MIN_VALUE ? null : (this.allowUserKeywords = Boolean.valueOf(chatRoomIce.allowUserKeywords == 1));
        this.allowBots = chatRoomIce.allowBots == Integer.MIN_VALUE ? null : Boolean.valueOf(chatRoomIce.allowBots == 1);
        this.dateCreated = chatRoomIce.dateCreated == Long.MIN_VALUE ? null : new Date(chatRoomIce.dateCreated);
        this.dateLastAccessed = chatRoomIce.dateLastAccessed == Long.MIN_VALUE ? null : new Date(chatRoomIce.dateLastAccessed);
        this.language = chatRoomIce.language.equals("\u0000") ? null : chatRoomIce.language;
        this.status = chatRoomIce.status == Integer.MIN_VALUE ? null : StatusEnum.fromValue(chatRoomIce.status);
        this.size = chatRoomIce.size == Integer.MIN_VALUE ? null : Integer.valueOf(chatRoomIce.size);
        this.keywords = chatRoomIce.keywords == null || chatRoomIce.keywords.length == 0 ? null : (String[])chatRoomIce.keywords.clone();
        this.themeID = chatRoomIce.themeID == Integer.MIN_VALUE ? null : Integer.valueOf(chatRoomIce.themeID);
        this.lockUser = chatRoomIce.lockUser.equals("\u0000") ? null : chatRoomIce.lockUser;
        this.announcer = chatRoomIce.announcer.equals("\u0000") ? null : chatRoomIce.announcer;
        this.announceMessage = chatRoomIce.announceMessage.equals("\u0000") ? null : chatRoomIce.announceMessage;
        this.minMigLevel = chatRoomIce.minMigLevel == Integer.MIN_VALUE ? null : Integer.valueOf(chatRoomIce.minMigLevel);
        this.rateLimitByIp = chatRoomIce.rateLimitByIp.equals("\u0000") ? null : chatRoomIce.rateLimitByIp;
        this.blockPeriodByIpInSeconds = chatRoomIce.blockPeriodByIpInSeconds == Integer.MIN_VALUE ? null : Integer.valueOf(chatRoomIce.blockPeriodByIpInSeconds);
    }

    public ChatRoomDataIce toIceObject() {
        ChatRoomDataIce chatRoomIce = new ChatRoomDataIce();
        chatRoomIce.id = this.id == null ? Integer.MIN_VALUE : this.id;
        chatRoomIce.name = this.name == null ? "\u0000" : this.name;
        chatRoomIce.description = this.description == null ? "\u0000" : this.description;
        chatRoomIce.type = this.type == null ? Integer.MIN_VALUE : this.type.value();
        chatRoomIce.creator = this.creator == null ? "\u0000" : this.creator;
        chatRoomIce.primaryCountryID = this.primaryCountryID == null ? Integer.MIN_VALUE : this.primaryCountryID;
        chatRoomIce.secondaryCountryID = this.secondaryCountryID == null ? Integer.MIN_VALUE : this.secondaryCountryID;
        chatRoomIce.locationID = this.locationID == null ? Integer.MIN_VALUE : this.locationID;
        chatRoomIce.groupID = this.groupID == null ? Integer.MIN_VALUE : this.groupID;
        int n = chatRoomIce.botID = this.botID == null ? Integer.MIN_VALUE : this.botID;
        chatRoomIce.adultOnly = this.adultOnly == null ? Integer.MIN_VALUE : (this.adultOnly != false ? 1 : 0);
        int n2 = chatRoomIce.maximumSize = this.maximumSize == null ? Integer.MIN_VALUE : this.maximumSize;
        chatRoomIce.userOwned = this.userOwned == null ? Integer.MIN_VALUE : (this.userOwned != false ? 1 : 0);
        String string = chatRoomIce.newOwner = this.newOwner == null ? "\u0000" : this.newOwner;
        int n3 = this.allowKicking == null ? Integer.MIN_VALUE : (chatRoomIce.allowKicking = this.allowKicking != false ? 1 : 0);
        int n4 = this.allowUserKeywords == null ? Integer.MIN_VALUE : (chatRoomIce.allowUserKeywords = this.allowUserKeywords != false ? 1 : 0);
        chatRoomIce.allowBots = this.allowBots == null ? Integer.MIN_VALUE : (this.allowBots != false ? 1 : 0);
        chatRoomIce.dateCreated = this.dateCreated == null ? Long.MIN_VALUE : this.dateCreated.getTime();
        chatRoomIce.dateLastAccessed = this.dateLastAccessed == null ? Long.MIN_VALUE : this.dateLastAccessed.getTime();
        chatRoomIce.language = this.language == null ? "\u0000" : this.language;
        chatRoomIce.status = this.status == null ? Integer.MIN_VALUE : this.status.value();
        chatRoomIce.size = this.size == null ? Integer.MIN_VALUE : this.size;
        chatRoomIce.keywords = this.keywords == null ? new String[]{} : (String[])this.keywords.clone();
        chatRoomIce.themeID = this.themeID == null ? Integer.MIN_VALUE : this.themeID;
        chatRoomIce.lockUser = this.lockUser == null ? "\u0000" : this.lockUser;
        chatRoomIce.announcer = this.announcer == null ? "\u0000" : this.announcer;
        chatRoomIce.announceMessage = this.announceMessage == null ? "\u0000" : this.announceMessage;
        chatRoomIce.minMigLevel = this.minMigLevel == null ? Integer.MIN_VALUE : this.minMigLevel;
        chatRoomIce.rateLimitByIp = this.rateLimitByIp == null ? "\u0000" : this.rateLimitByIp;
        chatRoomIce.blockPeriodByIpInSeconds = this.blockPeriodByIpInSeconds == null ? Integer.MIN_VALUE : this.blockPeriodByIpInSeconds;
        return chatRoomIce;
    }

    public void updateExtraData(ResultSet rs) throws SQLException {
        block5: while (rs.next()) {
            int typeInt = rs.getInt("type");
            String value = rs.getString("value");
            ExtraDataTypeEnum type = ExtraDataTypeEnum.fromValue(typeInt);
            if (type == null) {
                throw new SQLException(String.format("Unrecognized extra data type %d for chatroom %s [id=%d], value=%s, ignoring it...", typeInt, this.name, this.id, value));
            }
            switch (type) {
                case MIN_MIGLEVEL_TO_ENTER: {
                    this.minMigLevel = StringUtil.toIntOrDefault(value, -1);
                    if (this.minMigLevel != -1) continue block5;
                    this.minMigLevel = null;
                    continue block5;
                }
                case RATELIMIT_BY_IP: {
                    this.rateLimitByIp = value;
                    continue block5;
                }
                case BLOCK_PERIOD_BY_IP_IN_SECONDS: {
                    this.blockPeriodByIpInSeconds = StringUtil.toIntOrDefault(value, -1);
                    if (this.blockPeriodByIpInSeconds != -1) continue block5;
                    this.blockPeriodByIpInSeconds = null;
                    continue block5;
                }
            }
            throw new SQLException(String.format("Unsupported extra data type %s, value=%s for chatroom %s [id=%d], ignoring it...", new Object[]{type, value, this.name, this.id, value}));
        }
    }

    public void updateExtraData(ChatRoomData newData) {
        this.minMigLevel = newData.minMigLevel;
        this.rateLimitByIp = newData.rateLimitByIp;
        this.blockPeriodByIpInSeconds = newData.blockPeriodByIpInSeconds;
    }

    public Map<Integer, String> convertExtraDataToIntegerAndStringMap() {
        HashMap<Integer, String> m = new HashMap<Integer, String>();
        if (this.minMigLevel != null) {
            m.put(ExtraDataTypeEnum.MIN_MIGLEVEL_TO_ENTER.value, this.minMigLevel.toString());
        }
        if (this.rateLimitByIp != null) {
            m.put(ExtraDataTypeEnum.RATELIMIT_BY_IP.value, this.rateLimitByIp);
        }
        if (this.blockPeriodByIpInSeconds != null) {
            m.put(ExtraDataTypeEnum.BLOCK_PERIOD_BY_IP_IN_SECONDS.value, this.blockPeriodByIpInSeconds.toString());
        }
        return m;
    }

    public Map<Integer, String> convertExtraDataDifferenceToIntegerAndStringMap(ChatRoomData oldData) {
        HashMap<Integer, String> m = new HashMap<Integer, String>();
        if (this.minMigLevel != null && !this.minMigLevel.equals(oldData.minMigLevel) || this.minMigLevel == null && oldData.minMigLevel != null) {
            m.put(ExtraDataTypeEnum.MIN_MIGLEVEL_TO_ENTER.value, this.minMigLevel == null ? null : this.minMigLevel.toString());
        }
        if (this.rateLimitByIp != null && !this.rateLimitByIp.equals(oldData.rateLimitByIp) || this.rateLimitByIp == null && oldData.rateLimitByIp != null) {
            m.put(ExtraDataTypeEnum.RATELIMIT_BY_IP.value, this.rateLimitByIp);
        }
        if (this.blockPeriodByIpInSeconds != null && !this.blockPeriodByIpInSeconds.equals(oldData.blockPeriodByIpInSeconds) || this.blockPeriodByIpInSeconds == null && oldData.blockPeriodByIpInSeconds != null) {
            m.put(ExtraDataTypeEnum.BLOCK_PERIOD_BY_IP_IN_SECONDS.value, this.blockPeriodByIpInSeconds == null ? null : this.blockPeriodByIpInSeconds.toString());
        }
        return m;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static enum ExtraDataTypeEnum {
        MIN_MIGLEVEL_TO_ENTER(1),
        RATELIMIT_BY_IP(2),
        BLOCK_PERIOD_BY_IP_IN_SECONDS(3);

        private static final Map<Integer, ExtraDataTypeEnum> lookup;
        private int value;

        private ExtraDataTypeEnum(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static ExtraDataTypeEnum fromValue(int value) {
            return lookup.get(value);
        }

        static {
            lookup = SystemPropertyEntities.Temp.Cache.se351ChatRoomDataConcurrentCollectionsEnabled.getValue() != false ? new ConcurrentHashMap<Integer, ExtraDataTypeEnum>() : new HashMap<Integer, ExtraDataTypeEnum>();
            for (ExtraDataTypeEnum e : ExtraDataTypeEnum.values()) {
                lookup.put(e.value, e);
            }
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

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum TypeEnum {
        CHATROOM(1),
        STADIUM(2);

        private int value;

        private TypeEnum(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static TypeEnum fromValue(int value) {
            for (TypeEnum e : TypeEnum.values()) {
                if (e.value() != value) continue;
                return e;
            }
            return null;
        }
    }
}

