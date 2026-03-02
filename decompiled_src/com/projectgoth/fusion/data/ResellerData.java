/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.data;

import java.io.Serializable;

public class ResellerData
implements Serializable {
    public Integer id;
    public Integer countryID;
    public String state;
    public String city;
    public String name;
    public String address;
    public String phoneNumber;
    public String phoneNumberToDisplay;
    public String phoneNumber2;
    public String phoneNumber2ToDisplay;
    public StatusEnum status;

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

