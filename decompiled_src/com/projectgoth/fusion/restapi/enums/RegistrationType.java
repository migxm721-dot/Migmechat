/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.projectgoth.leto.common.event.authenticated.RegistrationType
 *  com.projectgoth.leto.common.utils.enums.IEnumValueGetter
 *  com.projectgoth.leto.common.utils.enums.ValueToEnumMap
 */
package com.projectgoth.fusion.restapi.enums;

import com.projectgoth.leto.common.utils.enums.IEnumValueGetter;
import com.projectgoth.leto.common.utils.enums.ValueToEnumMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum RegistrationType implements IEnumValueGetter<String>
{
    MOBILE_REGISTRATION(com.projectgoth.leto.common.event.authenticated.RegistrationType.MOBILE_REGISTRATION),
    EMAIL_LEGACY(com.projectgoth.leto.common.event.authenticated.RegistrationType.EMAIL_LEGACY),
    EMAIL_REGISTRATION_PATH1(com.projectgoth.leto.common.event.authenticated.RegistrationType.EMAIL_REGISTRATION_PATH1),
    EMAIL_REGISTRATION_PATH2(com.projectgoth.leto.common.event.authenticated.RegistrationType.EMAIL_REGISTRATION_PATH2),
    FACEBOOK_CONNECT(com.projectgoth.leto.common.event.authenticated.RegistrationType.FACEBOOK_CONNECT);

    private com.projectgoth.leto.common.event.authenticated.RegistrationType value;

    private RegistrationType(com.projectgoth.leto.common.event.authenticated.RegistrationType value) {
        this.value = value;
    }

    public String value() {
        return this.value.getEnumValue();
    }

    public static RegistrationType fromValue(String value) {
        return (RegistrationType)ValueToEnumMapInstance.INSTANCE.toEnum((Object)value);
    }

    public com.projectgoth.leto.common.event.authenticated.RegistrationType toRegistrationTypeEnum() {
        return this.value;
    }

    public String getEnumValue() {
        return this.value();
    }

    private static final class ValueToEnumMapInstance {
        private static final ValueToEnumMap<String, RegistrationType> INSTANCE = new ValueToEnumMap(RegistrationType.class);

        private ValueToEnumMapInstance() {
        }
    }
}

