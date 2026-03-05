/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.data;

import com.projectgoth.fusion.common.ContentUtils;
import com.projectgoth.fusion.common.Numerics;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.ReferenceStoreItemData;
import com.projectgoth.fusion.data.StoreCategoryData;
import com.projectgoth.fusion.data.StoreRatingSummaryData;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class VirtualGiftData
extends ReferenceStoreItemData
implements Serializable {
    private Integer id;
    private String name;
    private String hotKey;
    private Double price;
    private String currency;
    private Integer numAvailable;
    private Integer numSold;
    private Integer sortOrder;
    private Integer groupID;
    private boolean groupVIPOnly;
    private StatusEnum status;
    private String giftAllMessage;
    private StoreCategoryData storeCategory = null;
    private StoreRatingSummaryData storeRatingSummary = null;
    private int migLevelMin = -1;
    private int storeItemId;
    private final Map<String, SortedMap<Short, String>> imageLocations = new HashMap<String, SortedMap<Short, String>>();

    public int getStoreitemId() {
        return this.storeItemId;
    }

    public void setStoreitemId(int storeItemId) {
        this.storeItemId = storeItemId;
    }

    public void setImageLocation(String imageFormatType, short resolution, String location) {
        String normalizedImageFormatType = ContentUtils.normalizeImageFormatType(imageFormatType);
        if (location == null) {
            SortedMap<Short, String> resolutionToLocation = this.imageLocations.get(normalizedImageFormatType);
            if (resolutionToLocation != null) {
                resolutionToLocation.remove(resolution);
                if (resolutionToLocation.size() == 0) {
                    this.imageLocations.remove(normalizedImageFormatType);
                }
            }
        } else {
            SortedMap<Short, String> resolutionToLocation = this.imageLocations.get(normalizedImageFormatType);
            if (resolutionToLocation == null) {
                resolutionToLocation = new TreeMap<Short, String>();
                this.imageLocations.put(normalizedImageFormatType, resolutionToLocation);
            }
            resolutionToLocation.put(resolution, location);
        }
    }

    public String getImageLocation(String imageFormatType, short resolution) {
        String normalizedImageFormatType = ContentUtils.normalizeImageFormatType(imageFormatType);
        Map resolutionToLocation = this.imageLocations.get(normalizedImageFormatType);
        if (resolutionToLocation != null) {
            return (String)resolutionToLocation.get(resolution);
        }
        return null;
    }

    public String getImageLocation(ImageFormatType imageFormatType, short resolution) {
        return this.getImageLocation(imageFormatType.getCode(), resolution);
    }

    public void setImageLocation(ImageFormatType imageFormatType, short resolution, String location) {
        this.setImageLocation(imageFormatType.getCode(), resolution, location);
    }

    public SortedMap<Short, String> getAvailableImageLocations(String imageFormatType) {
        return this.imageLocations.get(ContentUtils.normalizeImageFormatType(imageFormatType));
    }

    public SortedMap<Short, String> getAvailableImageLocations(ImageFormatType imageFormatType) {
        return this.getAvailableImageLocations(imageFormatType.getCode());
    }

    public String toString() {
        return this.id.toString();
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHotKey() {
        return this.hotKey;
    }

    public void setHotKey(String hotKey) {
        this.hotKey = hotKey;
    }

    public Double getRoundedPrice() {
        return this.price == null ? null : Double.valueOf(-1.0 * Numerics.floor(-1.0 * this.price, 2));
    }

    public Double getPrice() {
        return this.price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCurrency() {
        return this.currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getNumAvailable() {
        return this.numAvailable;
    }

    public void setNumAvailable(Integer numAvailable) {
        this.numAvailable = numAvailable;
    }

    public Integer getNumSold() {
        return this.numSold;
    }

    public void setNumSold(Integer numSold) {
        this.numSold = numSold;
    }

    public Integer getSortOrder() {
        return this.sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getLocation12x12GIF() {
        return this.getImageLocation(ImageFormatType.GIF, (short)12);
    }

    public void setLocation12x12GIF(String location12x12GIF) {
        this.setImageLocation(ImageFormatType.GIF, (short)12, location12x12GIF);
    }

    public String getLocation12x12PNG() {
        return this.getImageLocation(ImageFormatType.PNG, (short)12);
    }

    public void setLocation12x12PNG(String location12x12PNG) {
        this.setImageLocation(ImageFormatType.PNG, (short)12, location12x12PNG);
    }

    public String getLocation14x14GIF() {
        return this.getImageLocation(ImageFormatType.GIF, (short)14);
    }

    public void setLocation14x14GIF(String location14x14GIF) {
        this.setImageLocation(ImageFormatType.GIF, (short)14, location14x14GIF);
    }

    public String getLocation14x14PNG() {
        return this.getImageLocation(ImageFormatType.PNG, (short)14);
    }

    public void setLocation14x14PNG(String location14x14PNG) {
        this.setImageLocation(ImageFormatType.PNG, (short)14, location14x14PNG);
    }

    public String getLocation16x16GIF() {
        return this.getImageLocation(ImageFormatType.GIF, (short)16);
    }

    public void setLocation16x16GIF(String location16x16GIF) {
        this.setImageLocation(ImageFormatType.GIF, (short)16, location16x16GIF);
    }

    public String getLocation16x16PNG() {
        return this.getImageLocation(ImageFormatType.PNG, (short)16);
    }

    public void setLocation16x16PNG(String location16x16PNG) {
        this.setImageLocation(ImageFormatType.PNG, (short)16, location16x16PNG);
    }

    public String getLocation64x64PNG() {
        return this.getImageLocation(ImageFormatType.PNG, (short)64);
    }

    public void setLocation64x64PNG(String location64x64PNG) {
        this.setImageLocation(ImageFormatType.PNG, (short)64, location64x64PNG);
    }

    public StatusEnum getStatus() {
        return this.status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public Integer getGroupID() {
        return this.groupID;
    }

    public void setGroupID(Integer groupID) {
        this.groupID = groupID;
    }

    public boolean isGroupVIPOnly() {
        return this.groupVIPOnly;
    }

    public void setGroupVIPOnly(boolean groupVIPOnly) {
        this.groupVIPOnly = groupVIPOnly;
    }

    public void setStoreCategory(StoreCategoryData cat) {
        this.storeCategory = cat;
    }

    public StoreCategoryData getStoreCategory() {
        return this.storeCategory;
    }

    public void setStoreRatingSummary(StoreRatingSummaryData summary) {
        this.storeRatingSummary = summary;
    }

    public StoreRatingSummaryData getStoreRatingSummary() {
        return this.storeRatingSummary;
    }

    public int getMigLevelMin() {
        return this.migLevelMin;
    }

    public void setMigLevelMin(int minLevel) {
        this.migLevelMin = minLevel;
    }

    public String getGiftAllMessage() {
        return this.giftAllMessage;
    }

    public VirtualGiftData() {
    }

    public VirtualGiftData(ResultSet rs) throws SQLException {
        this.id = rs.getInt("ID");
        this.name = rs.getString("Name");
        this.hotKey = rs.getString("HotKey");
        this.price = rs.getDouble("Price");
        this.currency = rs.getString("Currency");
        this.numAvailable = rs.getInt("NumAvailable");
        this.numSold = rs.getInt("NumSold");
        this.sortOrder = rs.getInt("SortOrder");
        this.groupID = rs.getInt("GroupID");
        this.groupVIPOnly = rs.getBoolean("GroupVIPOnly");
        this.status = StatusEnum.fromValue(rs.getInt("Status"));
        try {
            this.giftAllMessage = rs.getString("GiftAllMessage");
        }
        catch (SQLException e) {
            // empty catch block
        }
        try {
            this.migLevelMin = rs.getInt("migLevelMin");
        }
        catch (SQLException e) {
            // empty catch block
        }
        try {
            this.storeItemId = rs.getInt("storeitemid");
        }
        catch (SQLException e) {
            // empty catch block
        }
        for (Map.Entry<String, short[]> imageTypeResolution : ContentUtils.getSupportedVirtualGiftResolutions()) {
            short[] resolutions;
            String imgFormat = imageTypeResolution.getKey();
            for (short resolution : resolutions = imageTypeResolution.getValue()) {
                String resultSetColumn = this.getResultSetColumn(imgFormat, resolution);
                try {
                    String location = rs.getString(resultSetColumn);
                    if (StringUtil.isBlank(location)) {
                        this.setImageLocation(imgFormat, resolution, null);
                        continue;
                    }
                    this.setImageLocation(imgFormat, resolution, location);
                }
                catch (SQLException ex) {
                    this.setImageLocation(imgFormat, resolution, null);
                }
            }
        }
    }

    private String getResultSetColumn(String imageFormatType, int resolution) {
        return new StringBuilder(30).append("Location").append(resolution).append('x').append(resolution).append(ContentUtils.normalizeImageFormatType(imageFormatType)).toString();
    }

    public String getGiftUrl(ImageFormatType imageFormatType, short resolution) {
        String path = this.getImageLocation(imageFormatType, resolution);
        if (StringUtil.isBlank(path)) {
            return "";
        }
        return String.format("%s/%s", SystemProperty.get(SystemPropertyEntities.Default.MIG33_WEB_BASE_URL), path);
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
    public static enum GiftingType {
        GIFT(0),
        GIFT_SHOWER(1);

        private int value;

        private GiftingType(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static GiftingType fromValue(int value) {
            for (GiftingType e : GiftingType.values()) {
                if (e.value() != value) continue;
                return e;
            }
            return null;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum ImageFormatType {
        PNG("PNG"),
        GIF("GIF");

        private String code;

        private ImageFormatType(String code) {
            this.code = code;
        }

        public String getCode() {
            return this.code;
        }
    }
}

