/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.json.JSONException
 */
package com.projectgoth.fusion.payment;

import com.projectgoth.fusion.common.EnumUtils;
import com.projectgoth.fusion.payment.PaymentData;
import java.util.HashMap;
import org.json.JSONException;

public abstract class PaymentIResponse {
    public PaymentData paymentData;
    public Integer code;
    public String response;

    public abstract String toJSON(ReturnType var1) throws JSONException;

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum ReturnType implements EnumUtils.IEnumValueGetter<Integer>
    {
        CREATE(1),
        GET_COMPACT_DETAILS(2),
        GET_FULL_DETAILS(3),
        UPDATE(4),
        APPROVE(5),
        REJECT(6);

        private Integer value;
        private static HashMap<Integer, ReturnType> lookupByValue;

        private ReturnType(int value) {
            this.value = value;
        }

        public static ReturnType fromValue(int value) {
            return lookupByValue.get(value);
        }

        public Integer getEnumValue() {
            return this.value;
        }

        static {
            lookupByValue = new HashMap();
            EnumUtils.populateLookUpMap(lookupByValue, ReturnType.class);
        }
    }
}

