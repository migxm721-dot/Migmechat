/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.invitation;

import com.projectgoth.fusion.common.EnumUtils;
import java.util.HashMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum InvitationStatusEnum implements EnumUtils.IEnumValueGetter<Integer>
{
    DISABLED(0),
    OPEN(1),
    CLOSED(2),
    EXPIRED(3),
    INVALID(4),
    UNKNOWN(5);

    private Integer typeCode;
    private static HashMap<Integer, InvitationStatusEnum> lookupByCode;

    private InvitationStatusEnum(int typeCode) {
        this.typeCode = typeCode;
    }

    public Integer getEnumValue() {
        return this.getTypeCode();
    }

    public int getTypeCode() {
        return this.typeCode;
    }

    public static InvitationStatusEnum fromTypeCode(int typeCode) {
        return lookupByCode.get(typeCode);
    }

    static {
        lookupByCode = new HashMap();
        EnumUtils.populateLookUpMap(lookupByCode, InvitationStatusEnum.class);
    }
}

