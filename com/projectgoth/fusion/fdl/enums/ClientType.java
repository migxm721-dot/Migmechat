/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.fdl.enums;

import java.util.HashMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum ClientType {
    MIDP1(1),
    MIDP2(2),
    DESKTOP(3),
    SYMBIAN(4),
    AJAX1(5),
    TOOLBAR(6),
    WINDOWS_MOBILE(7),
    ANDROID(8),
    WAP(9),
    MIGBO(10),
    VAS(11),
    MERCHANT_CENTER(12),
    BLACKBERRY(13),
    BLAAST(14),
    MRE(15),
    AJAX2(16),
    IOS(17);

    private byte value;
    private static final HashMap<Byte, ClientType> LOOKUP;

    private ClientType(byte value) {
        this.value = value;
    }

    public byte value() {
        return this.value;
    }

    public static ClientType fromValue(int value) {
        return LOOKUP.get((byte)value);
    }

    public static ClientType fromValue(Byte value) {
        return LOOKUP.get(value);
    }

    public static final boolean isAjax(ClientType deviceType) {
        return deviceType == AJAX1 || deviceType == AJAX2;
    }

    public static final boolean isMobileClientV2(ClientType deviceType) {
        return deviceType == ANDROID || deviceType == BLACKBERRY || deviceType == MRE || deviceType == IOS;
    }

    public static final boolean isMobileClientV2AndNewVersion(ClientType deviceType, short version) {
        return deviceType == ANDROID && version >= 200 || deviceType == BLACKBERRY && version >= 500 || deviceType == MRE && version >= 100 || deviceType == IOS && version >= 100;
    }

    public static final boolean canShowMidletTabs(ClientType deviceType) {
        if (deviceType == null) {
            return false;
        }
        switch (deviceType) {
            case MIDP1: 
            case MIDP2: 
            case SYMBIAN: 
            case BLACKBERRY: {
                return true;
            }
        }
        return false;
    }

    public static final boolean isMobileClientV2AndNewVersionOrAjax(ClientType deviceType, short version) {
        return ClientType.isMobileClientV2AndNewVersion(deviceType, version) || ClientType.isAjax(deviceType);
    }

    static {
        LOOKUP = new HashMap();
        for (ClientType clientType : ClientType.values()) {
            LOOKUP.put(clientType.value, clientType);
        }
    }
}

