/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.data;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ContentData
implements Serializable {
    public Integer id;
    public Integer contentCategoryID;
    public Integer contentProviderID;
    public TypeEnum type;
    public String name;
    public String artist;
    public Integer countryID;
    public Double price;
    public String currency;
    public Double wholesaleCost;
    public String wholesaleCostCurrency;
    public String preview;
    public Integer previewWidth;
    public Integer previewHeight;
    public String thumbnail;
    public String providerID;
    public Integer groupID;
    public boolean groupVIPOnly;
    public StatusEnum status;
    public Double baseCurrencyPrice;
    public Double baseCurrencyWholesaleCost;
    public String contentProviderName;
    public String orderURL;

    public ContentData() {
    }

    public ContentData(Connection conn, int id) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("select content.id, content.contentcategoryid, content.contentproviderid, contentprovider.name contentprovidername, contentprovider.orderurl orderurl, content.type, content.name, content.artist, content.countryid, content.price, content.price/pricecurrency.exchangerate baseprice, content.currency, content.wholesalecost, content.wholesalecost/costcurrency.exchangerate basewholesalecost, content.wholesalecostcurrency, content.preview, content.previewwidth, content.previewheight, content.providerid, content.groupid, content.groupviponly, content.status from content, contentprovider, currency pricecurrency, currency costcurrency where content.contentproviderid=contentprovider.id and content.currency=pricecurrency.code and content.wholesalecostcurrency = costcurrency.code and content.id = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new Exception("Content not found");
            }
            this.readContentFromResultSet(rs);
        }
        catch (SQLException e) {
            throw new Exception("Unable to load content from DB: " + e.getMessage());
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
        }
    }

    public ContentData(ResultSet rs) throws Exception {
        this.readContentFromResultSet(rs);
    }

    public void readContentFromResultSet(ResultSet rs) throws Exception {
        this.id = rs.getInt("id");
        this.contentCategoryID = rs.getInt("contentcategoryid");
        this.contentProviderID = rs.getInt("contentproviderid");
        this.contentProviderName = rs.getString("contentprovidername");
        this.orderURL = rs.getString("orderurl");
        this.type = TypeEnum.fromValue(rs.getInt("type"));
        this.name = rs.getString("name");
        this.artist = rs.getString("artist");
        this.countryID = rs.getInt("countryid");
        this.price = rs.getDouble("price");
        this.baseCurrencyPrice = rs.getDouble("baseprice");
        this.currency = rs.getString("currency");
        this.wholesaleCost = rs.getDouble("wholesalecost");
        this.baseCurrencyWholesaleCost = rs.getDouble("basewholesalecost");
        this.wholesaleCostCurrency = rs.getString("wholesalecostcurrency");
        this.preview = rs.getString("preview");
        this.previewWidth = rs.getInt("previewwidth");
        this.previewHeight = rs.getInt("previewheight");
        this.providerID = rs.getString("providerid");
        this.groupID = rs.getInt("GroupID");
        this.groupVIPOnly = rs.getBoolean("GroupVIPOnly");
        this.status = StatusEnum.fromValue(rs.getInt("status"));
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

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum TypeEnum {
        RINGTONE(1),
        WALLPAPER(2),
        VIDEO(3),
        APPLICATION(4);

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

