/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.projectgoth.leto.common.utils.enums.IEnumValueExtractor
 */
package com.projectgoth.fusion.payment;

import com.projectgoth.fusion.common.EnumUtils;
import com.projectgoth.leto.common.utils.enums.IEnumValueExtractor;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class PaymentMetaDetails
implements Serializable {
    public Integer id;
    public MetaType type;
    public String value;

    public PaymentMetaDetails(MetaType type) {
        this.type = type;
    }

    public PaymentMetaDetails(MetaType type, String value) {
        this.type = type;
        this.value = value;
    }

    public PaymentMetaDetails(ResultSet rs) throws SQLException {
        this.type = MetaType.valueOf(rs.getString("type"));
        this.id = rs.getInt("id");
        this.value = rs.getString("value");
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum MetaType implements EnumUtils.IEnumValueGetter<Integer>
    {
        FIRST_NAME(1),
        LAST_NAME(2),
        PAYPAL_ACCOUNT(3),
        AUTO_APPROVE(4),
        GC_CC_REFERENCE(5),
        GC_CC_STATUS(6),
        GC_CC_MERCHANTID(7),
        GC_CC_CARD_TYPE(8),
        GC_CC_FRAUD(9);

        private int code;
        private static HashMap<Integer, MetaType> lookupByCode;

        private MetaType(int code) {
            this.code = code;
        }

        public int code() {
            return this.code;
        }

        public Integer getEnumValue() {
            return this.code;
        }

        public static MetaType fromCode(int code) {
            return lookupByCode.get(code);
        }

        static {
            lookupByCode = new HashMap();
            EnumUtils.IEnumValueExtractor<Integer, MetaType> vendorCodeExtractor = new EnumUtils.IEnumValueExtractor<Integer, MetaType>(){

                public Integer getValue(MetaType enumConst) {
                    return enumConst.code;
                }
            };
            EnumUtils.populateLookUpMap(lookupByCode, MetaType.class, (IEnumValueExtractor)vendorCodeExtractor);
        }
    }
}

