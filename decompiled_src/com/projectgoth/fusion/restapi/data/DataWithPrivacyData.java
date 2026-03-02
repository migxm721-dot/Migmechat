/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.restapi.data;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DataWithPrivacyData<ValueType, PrivacyType> {
    public ValueType value;
    public PrivacyType privacy;

    public DataWithPrivacyData() {
    }

    DataWithPrivacyData(ValueType thevalue, PrivacyType theprivacy) {
        this.value = thevalue;
        this.privacy = theprivacy;
    }
}

