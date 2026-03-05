/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.data;

import java.io.Serializable;
import java.util.Date;

public class PremiumSMSPaymentData
implements Serializable {
    public Integer id;
    public String username;
    public Integer systemSMSID;
    public Date dateCreated;
    public Double amount;
    public Double fee;
    public String responseCode;
    public StatusEnum status;

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum StatusEnum {
        PENDING(0),
        SUCCEEDED(1),
        FAILED(2);

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

