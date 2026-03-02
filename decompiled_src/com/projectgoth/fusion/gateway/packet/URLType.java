/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.gateway.packet;

import java.util.HashMap;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
enum URLType {
    SCRAPBOOK(1, "ScrapbookURL", true),
    USER_PROFILE(2, "UserProfileURL", true),
    MY_PROFILE(3, "MyProfileURL", true),
    MY_ACCOUNT(4, "MyAccountURL"),
    SEARCH_PROFILE(5, "SearchProfileURL"),
    SEND_FROM_SCRAPBOOK(6, "SendFromScrapbookURL", true),
    BLOCK_LIST(7, "BlockListURL"),
    STORE(8, "mig33StoreURL"),
    BUY_CREDIT(9, "BuyCreditURL"),
    BUZZ(10, "BuzzURL"),
    LOOKOUT(11, "LookOutURL"),
    EDIT_PROFILE(12, "EditProfileURL"),
    ANONYMOUS_CALLING(13, "AnonymousCallSettingsURL"),
    PRIVACY_SETTINGS(14, "PrivacySettingsURL"),
    BUY_EMOTICON(15, "BuyEmoticonsURL"),
    THEME(16, "ThemeURL"),
    VIRTUAL_GIFT(17, "VirtualGiftURL"),
    MIGBO_DATASVC_API(18, "MigboDataSvcURL", true),
    SSO_API(19, "SSODataSvcURL", true),
    MIGBO_UPLOAD_API(20, "MigboUploadURL", true),
    MIGBO_IMAGES_URL(21, "MigboImagesURL", true),
    MIGBO_ALERTS(22, "MigboAlertsURL", true),
    DISCOVER(23, "DiscoverURL", true);

    private int value;
    private String systemProperty;
    private boolean isViewSensitive = false;
    private static final Map<Integer, URLType> lookup;

    private URLType(int value) {
        this.value = value;
    }

    private URLType(int value, String systemProperty) {
        this.value = value;
        this.systemProperty = systemProperty;
    }

    private URLType(int value, String systemProperty, boolean isViewSensitive) {
        this.value = value;
        this.systemProperty = systemProperty;
        this.isViewSensitive = isViewSensitive;
    }

    public static URLType fromValue(int value) {
        return lookup.get(value);
    }

    public String getSystemProperty() {
        return this.systemProperty;
    }

    public boolean getIsViewSensitive() {
        return this.isViewSensitive;
    }

    public int value() {
        return this.value;
    }

    static {
        lookup = new HashMap<Integer, URLType>();
        for (URLType c : URLType.values()) {
            lookup.put(c.value, c);
        }
    }
}

