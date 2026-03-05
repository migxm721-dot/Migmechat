/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.projectgoth.fusion.restapi.data;

import com.projectgoth.fusion.restapi.data.SettingsEnums;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlRootElement(name="settings")
public class SettingsAccountCommunicationData {
    public SettingsEnums.EveryoneFollowerFriend chat;
    public SettingsEnums.OnOff buzz;
    public SettingsEnums.OnOff lookout;
    public SettingsEnums.EveryoneFollowerFriendHide footprints;
    public SettingsEnums.EveryoneOrFollowerAndFriend feed;
    public static final SettingsEnums.EveryoneFollowerFriend PRIVACY_DEFAULT_CHAT = SettingsEnums.EveryoneFollowerFriend.EVERYONE;
    public static final SettingsEnums.OnOff PRIVACY_DEFAULT_BUZZ = SettingsEnums.OnOff.ON;
    public static final SettingsEnums.OnOff PRIVACY_DEFAULT_LOOKOUT = SettingsEnums.OnOff.ON;
    public static final SettingsEnums.EveryoneFollowerFriendHide PRIVACY_DEFAULT_FOOTPRINTS = SettingsEnums.EveryoneFollowerFriendHide.HIDE;
    public static final SettingsEnums.EveryoneOrFollowerAndFriend PRIVACY_DEFAULT_FEED = SettingsEnums.EveryoneOrFollowerAndFriend.EVERYONE;

    public SettingsAccountCommunicationData() {
    }

    public SettingsAccountCommunicationData(Map<String, Integer> privacySettings) {
        this.chat = privacySettings.containsKey("ChatPv") ? SettingsEnums.EveryoneFollowerFriend.fromValue(privacySettings.get("ChatPv")) : PRIVACY_DEFAULT_CHAT;
        this.buzz = privacySettings.containsKey("BuzzPv") ? SettingsEnums.OnOff.fromValue(privacySettings.get("BuzzPv")) : PRIVACY_DEFAULT_BUZZ;
        this.lookout = privacySettings.containsKey("LOPv") ? SettingsEnums.OnOff.fromValue(privacySettings.get("LOPv")) : PRIVACY_DEFAULT_LOOKOUT;
        this.footprints = privacySettings.containsKey("FPPv") ? SettingsEnums.EveryoneFollowerFriendHide.fromValue(privacySettings.get("FPPv")) : PRIVACY_DEFAULT_FOOTPRINTS;
        this.feed = privacySettings.containsKey("FeedPv") ? SettingsEnums.EveryoneOrFollowerAndFriend.fromValue(privacySettings.get("FeedPv")) : PRIVACY_DEFAULT_FEED;
    }

    public Map<String, Integer> retrievePrivacy() {
        HashMap<String, Integer> values = new HashMap<String, Integer>();
        values.put("ChatPv", (this.chat == null ? PRIVACY_DEFAULT_CHAT : this.chat).value());
        values.put("BuzzPv", (this.buzz == null ? PRIVACY_DEFAULT_BUZZ : this.buzz).value());
        values.put("LOPv", (this.lookout == null ? PRIVACY_DEFAULT_LOOKOUT : this.lookout).value());
        values.put("FPPv", (this.footprints == null ? PRIVACY_DEFAULT_FOOTPRINTS : this.footprints).value());
        values.put("FeedPv", (this.feed == null ? PRIVACY_DEFAULT_FEED : this.feed).value());
        return values;
    }
}

