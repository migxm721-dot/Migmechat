/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.projectgoth.leto.common.utils.enums.IEnumValueExtractor
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.projectgoth.fusion.payment;

import com.projectgoth.fusion.common.DateTimeUtils;
import com.projectgoth.fusion.common.EnumUtils;
import com.projectgoth.fusion.common.JSONUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.payment.PaymentMetaDetails;
import com.projectgoth.leto.common.utils.enums.IEnumValueExtractor;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class PaymentData
implements Serializable {
    public static final byte[] EMPTY_EXTRA_FIELDS = new JSONObject().toString().getBytes();
    public Integer id;
    public String vendorTransactionId;
    public int userId;
    public StatusEnum status;
    private Date createdTS;
    private Date updatedTS;
    public TypeEnum vendorType;
    public double amount;
    public String currency;
    public byte[] description;
    public String username;
    public String exchangeRate;
    protected List<PaymentMetaDetails> details = new ArrayList<PaymentMetaDetails>();
    public AccountEntrySourceData accountEntrySource;

    public PaymentData(TypeEnum vendorType) {
        this.vendorType = vendorType;
    }

    protected PaymentData(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.assignCreatedTime(rs.getTimestamp("datecreated"));
        this.vendorTransactionId = rs.getString("vendortransactionid");
        this.userId = rs.getInt("userid");
        this.status = StatusEnum.fromValue(rs.getInt("status"));
        this.assignUpdatedTime(rs.getTimestamp("dateupdated"));
        this.vendorType = TypeEnum.fromValue(rs.getInt("type"));
        this.amount = rs.getDouble("amount");
        this.currency = rs.getString("currency");
        try {
            this.username = rs.getString("username");
        }
        catch (SQLException sQLException) {
            // empty catch block
        }
        this.deserializeExtraFields(rs);
    }

    protected PaymentData(JSONObject jsonObject) throws JSONException, ParseException {
        this.currency = JSONUtils.getString(jsonObject, "currency");
        this.vendorType = null;
        Integer vendorTypeCode = JSONUtils.getInteger(jsonObject, "type");
        this.vendorType = vendorTypeCode != null ? TypeEnum.fromValue(vendorTypeCode) : null;
        this.amount = JSONUtils.getDouble(jsonObject, "amount", 0.0);
        this.userId = JSONUtils.getInteger(jsonObject, "userId", -1);
        Integer statusCode = JSONUtils.getInteger(jsonObject, "status");
        this.status = statusCode != null ? StatusEnum.fromValue(statusCode) : null;
        this.id = JSONUtils.getInteger(jsonObject, "id");
        String dateCreatedStr = JSONUtils.getString(jsonObject, "dateCreated");
        if (dateCreatedStr != null) {
            this.assignCreatedTime(DateTimeUtils.getPaymentTransactionTimeFromString(dateCreatedStr));
        } else {
            this.assignCreatedTime(null);
        }
        this.vendorTransactionId = JSONUtils.getString(jsonObject, "vendorTransactionId");
        String dateUpdatedStr = JSONUtils.getString(jsonObject, "dateUpdated");
        if (dateUpdatedStr != null) {
            this.assignUpdatedTime(DateTimeUtils.getPaymentTransactionTimeFromString(dateUpdatedStr));
        } else {
            this.assignUpdatedTime(null);
        }
        this.username = JSONUtils.getString(jsonObject, "username");
        JSONObject aesJsonObj = JSONUtils.getJSONObject(jsonObject, "accountEntrySource");
        this.accountEntrySource = aesJsonObj != null ? new AccountEntrySourceData(aesJsonObj) : null;
        this.deserializeExtraFields(jsonObject);
    }

    public List<PaymentMetaDetails> getDetails() {
        return new ArrayList<PaymentMetaDetails>();
    }

    public void setDetails(List<PaymentMetaDetails> meta) {
    }

    public Date fetchCreatedTime() {
        return this.createdTS;
    }

    public Date fetchUpdatedTime() {
        return this.updatedTS;
    }

    public void assignCreatedTime(Date ts) {
        this.createdTS = ts;
    }

    public void assignUpdatedTime(Date ts) {
        this.updatedTS = ts;
    }

    public String getDateCreated() {
        return DateTimeUtils.getStringForPaymentTransactionTime(this.createdTS);
    }

    public void setDateCreated(String str) throws ParseException {
        this.createdTS = str == null ? null : DateTimeUtils.getPaymentTransactionTimeFromString(str);
    }

    public String getDateUpdated() {
        return DateTimeUtils.getStringForPaymentTransactionTime(this.updatedTS);
    }

    public void setDateUpdated(String str) throws ParseException {
        this.updatedTS = str == null ? null : DateTimeUtils.getPaymentTransactionTimeFromString(str);
    }

    public JSONObject serializeExtraFieldsToJSON() throws JSONException {
        return new JSONObject();
    }

    protected void deserializeExtraFields(ResultSet rs) throws SQLException {
    }

    protected void deserializeExtraFields(JSONObject additionDetails) throws JSONException {
    }

    public boolean enableStrictAmountCheck() {
        return false;
    }

    public StatusEnum getInitialStatus() {
        return StatusEnum.PENDING;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum ViewEnum implements EnumUtils.IEnumValueGetter<String>
    {
        MIG33_AJAX("ajax"),
        WAP("wap"),
        MIDLET("midlet"),
        MTK_MRE("mre"),
        TOUCH("touch"),
        BLACKBERRY("blackberry"),
        MIG33_CORPORATE("corporate"),
        IOS("ios");

        private String value;
        private static HashMap<String, ViewEnum> lookupByValue;

        private ViewEnum(String valueStr) {
            this.value = StringUtil.trimmedLowerCase(valueStr);
        }

        public String value() {
            return this.value;
        }

        public static ViewEnum fromName(String name) {
            if (name == null) {
                return null;
            }
            return lookupByValue.get(StringUtil.trimmedLowerCase(name));
        }

        public String getEnumValue() {
            return StringUtil.trimmedLowerCase(this.value);
        }

        static {
            lookupByValue = new HashMap();
            EnumUtils.populateLookUpMap(lookupByValue, ViewEnum.class);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum StatusEnum implements EnumUtils.IEnumValueGetter<Integer>
    {
        PENDING(0),
        APPROVED(1),
        REJECTED(2),
        CANCELLED(3),
        TIMEOUT(4),
        INVALID(5),
        VENDOR_FAILED(6),
        ONHOLD(7),
        CHARGE_BACK(8);

        private Integer value;
        private static HashMap<Integer, StatusEnum> lookupByValue;

        private StatusEnum(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static StatusEnum fromValue(int value) {
            return lookupByValue.get(value);
        }

        public Integer getEnumValue() {
            return this.value;
        }

        static {
            lookupByValue = new HashMap();
            EnumUtils.populateLookUpMap(lookupByValue, StatusEnum.class);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum TypeEnum implements EnumUtils.IEnumValueGetter<Integer>
    {
        MOL(1, "MoneyOnline", "mol"),
        PAYPAL(2, "Paypal", "paypal"),
        MIMOPAY(3, "MIMOPAY", "mimopay"),
        CREDIT_CARD(4, "CreditCard", "creditcard");

        private Integer value;
        private String displayName;
        private String code;
        private static HashMap<Integer, TypeEnum> lookupByValue;
        private static HashMap<String, TypeEnum> lookupByCode;

        private TypeEnum(int value, String name, String code) {
            this.value = value;
            this.displayName = name;
            this.code = StringUtil.trimmedLowerCase(code);
        }

        public int value() {
            return this.value;
        }

        public String displayName() {
            return this.displayName;
        }

        public String code() {
            return this.code;
        }

        public static TypeEnum fromValue(int value) {
            return lookupByValue.get(value);
        }

        public static TypeEnum fromCode(String code) {
            if (code == null) {
                return null;
            }
            return lookupByCode.get(StringUtil.trimmedLowerCase(code));
        }

        public Integer getEnumValue() {
            return this.value;
        }

        static {
            lookupByValue = new HashMap();
            lookupByCode = new HashMap();
            EnumUtils.IEnumValueExtractor<String, TypeEnum> vendorCodeExtractor = new EnumUtils.IEnumValueExtractor<String, TypeEnum>(){

                public String getValue(TypeEnum enumConst) {
                    return enumConst.code;
                }
            };
            EnumUtils.populateLookUpMap(lookupByValue, TypeEnum.class);
            EnumUtils.populateLookUpMap(lookupByCode, TypeEnum.class, (IEnumValueExtractor)vendorCodeExtractor);
        }
    }
}

