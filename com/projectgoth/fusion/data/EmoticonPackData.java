/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.data;

import com.projectgoth.fusion.data.ReferenceStoreItemData;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class EmoticonPackData
extends ReferenceStoreItemData
implements Serializable {
    private static final long serialVersionUID = 3270071183111285012L;
    private Integer id;
    private TypeEnum type;
    private String name;
    private String description;
    private Double price;
    private Integer serviceID;
    private Integer groupID;
    private boolean groupVIPOnly;
    private Integer sortOrder;
    private boolean forSale;
    private StatusEnum status;
    private List<Integer> emoticonIDs = new ArrayList<Integer>();
    private ContentTypeEnum contentType;
    private Integer version;
    private String thumbnailFile;
    private String catalogImage;

    public String getCatalogImage() {
        return this.catalogImage;
    }

    public void setCatalogImage(String catalogImage) {
        this.catalogImage = catalogImage;
    }

    public String getThumbnailFile() {
        return this.thumbnailFile;
    }

    public void setThumbnailFile(String thumbnailURL) {
        this.thumbnailFile = thumbnailURL;
    }

    public Integer getVersion() {
        return this.version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public ContentTypeEnum getContentType() {
        return this.contentType;
    }

    public void setContentType(ContentTypeEnum contentType) {
        this.contentType = contentType;
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

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return this.price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public StatusEnum getStatus() {
        return this.status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public List<Integer> getEmoticonIDs() {
        return this.emoticonIDs;
    }

    public void addEmoticonID(Integer emoticonID) {
        this.emoticonIDs.add(emoticonID);
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

    public Integer getServiceID() {
        return this.serviceID;
    }

    public void setServiceID(Integer serviceID) {
        this.serviceID = serviceID;
    }

    public Integer getSortOrder() {
        return this.sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isForSale() {
        return this.forSale;
    }

    public void setForSale(boolean forSale) {
        this.forSale = forSale;
    }

    public TypeEnum getType() {
        return this.type;
    }

    public void setType(TypeEnum type) {
        this.type = type;
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
    public static enum ContentTypeEnum {
        EMOTICON(1),
        STICKER(2);

        private int value;

        private ContentTypeEnum(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static ContentTypeEnum fromValue(int value) {
            for (ContentTypeEnum e : ContentTypeEnum.values()) {
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
        FREE(1),
        PREMIUM_PURCHASE(2),
        PREMIUM_SUBSCRIPTION(3);

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

