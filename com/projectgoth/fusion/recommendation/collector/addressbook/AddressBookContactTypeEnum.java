/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.recommendation.collector.addressbook;

import com.projectgoth.fusion.common.EnumUtils;
import java.util.HashMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum AddressBookContactTypeEnum implements EnumUtils.IEnumValueGetter<Byte>
{
    MOBILENUMBER(1),
    EMAILADDRESS(2);

    private final byte code;

    private AddressBookContactTypeEnum(byte code) {
        this.code = code;
    }

    public static AddressBookContactTypeEnum fromCode(byte code) {
        return SingletonHolder.lookupByCode.get(code);
    }

    public byte getCode() {
        return this.code;
    }

    public Byte getEnumValue() {
        return this.getCode();
    }

    private static class SingletonHolder {
        public static final HashMap<Byte, AddressBookContactTypeEnum> lookupByCode = (HashMap)EnumUtils.buildLookUpMap(new HashMap(), AddressBookContactTypeEnum.class);

        private SingletonHolder() {
        }
    }
}

