/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.projectgoth.fusion.restapi.data;

import com.projectgoth.fusion.data.CountryData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.data.UserProfileData;
import com.projectgoth.fusion.restapi.data.DataWithPrivacyData;
import com.projectgoth.fusion.restapi.data.SettingsEnums;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlRootElement(name="settings")
public class SettingsProfileDetailsData {
    private static final Pattern emailAddressRegex = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
    public DataWithPrivacyData<Integer, SettingsEnums.ShowHide> id;
    public DataWithPrivacyData<String, SettingsEnums.ShowHide> username;
    public DataWithPrivacyData<Boolean, SettingsEnums.ShowHide> isVerified;
    public DataWithPrivacyData<String, SettingsEnums.ShowHide> aboutMeVerified;
    public DataWithPrivacyData<Date, SettingsEnums.ShowHide> dateRegistered;
    public DataWithPrivacyData<String, SettingsEnums.ShowHide> country;
    public DataWithPrivacyData<String, SettingsEnums.ShowHide> gender;
    public DataWithPrivacyData<Integer, SettingsEnums.ShowHide> relationship;
    public DataWithPrivacyData<Date, SettingsEnums.Birthday> dateOfBirth;
    public DataWithPrivacyData<String, SettingsEnums.ShowHide> interests;
    public DataWithPrivacyData<String, SettingsEnums.ShowHide> aboutMe;
    public DataWithPrivacyData<FirstLastName, SettingsEnums.ShowHide> firstLastName;
    public DataWithPrivacyData<String, SettingsEnums.EveryoneFriendHide> mobilePhone;
    public DataWithPrivacyData<String, SettingsEnums.EveryoneFollowerFriendHide> externalEmail;
    public DataWithPrivacyData<String, SettingsEnums.ShowHide> mig33Email;
    public DataWithPrivacyData<String, SettingsEnums.ShowHide> city;
    public DataWithPrivacyData<String, SettingsEnums.ShowHide> state;
    public DataWithPrivacyData<String, SettingsEnums.ShowHide> schools;
    public DataWithPrivacyData<String, SettingsEnums.ShowHide> companies;
    public boolean externalEmailVerified = false;
    public static final SettingsEnums.Birthday PRIVACY_DEFAULT_BIRTHDAY = SettingsEnums.Birthday.HIDE;
    public static final SettingsEnums.ShowHide PRIVACY_DEFAULT_FIRSTLASTNAME = SettingsEnums.ShowHide.HIDE;
    public static final SettingsEnums.EveryoneFriendHide PRIVACY_DEFAULT_MOBILEPHONE = SettingsEnums.EveryoneFriendHide.HIDE;
    public static final SettingsEnums.EveryoneFollowerFriendHide PRIVACY_DEFAULT_EXTERNALEMAIL = SettingsEnums.EveryoneFollowerFriendHide.HIDE;

    public SettingsProfileDetailsData() {
    }

    public SettingsProfileDetailsData(UserData userData, UserProfileData upData, CountryData countryData, Map<String, Integer> privacySettings) {
        if (upData == null) {
            upData = new UserProfileData();
        }
        this.id = new DataWithPrivacyData<Integer, SettingsEnums.ShowHide>(userData.userID, SettingsEnums.ShowHide.SHOW);
        this.username = new DataWithPrivacyData<String, SettingsEnums.ShowHide>(userData.username, SettingsEnums.ShowHide.SHOW);
        this.isVerified = new DataWithPrivacyData<Boolean, SettingsEnums.ShowHide>(userData.accountVerified == UserData.AccountVerifiedEnum.VERIFIED_OK, SettingsEnums.ShowHide.SHOW);
        this.aboutMeVerified = new DataWithPrivacyData<String, SettingsEnums.ShowHide>(userData.aboutMeVerified, SettingsEnums.ShowHide.SHOW);
        this.dateRegistered = new DataWithPrivacyData<Date, SettingsEnums.ShowHide>(userData.dateRegistered, SettingsEnums.ShowHide.SHOW);
        this.country = new DataWithPrivacyData<String, SettingsEnums.ShowHide>(countryData.name, SettingsEnums.ShowHide.SHOW);
        this.gender = new DataWithPrivacyData<String, SettingsEnums.ShowHide>(upData.gender == null ? null : upData.gender.value(), SettingsEnums.ShowHide.SHOW);
        this.relationship = new DataWithPrivacyData<Object, SettingsEnums.ShowHide>((upData.relationshipStatus == null ? null : Integer.valueOf(upData.relationshipStatus.value())), SettingsEnums.ShowHide.SHOW);
        this.dateOfBirth = new DataWithPrivacyData<Date, SettingsEnums.Birthday>(upData.dateOfBirth, privacySettings.containsKey("DobPrivacy") ? SettingsEnums.Birthday.fromValue(privacySettings.get("DobPrivacy")) : PRIVACY_DEFAULT_BIRTHDAY);
        this.interests = new DataWithPrivacyData<String, SettingsEnums.ShowHide>(upData.hobbies, SettingsEnums.ShowHide.SHOW);
        this.aboutMe = new DataWithPrivacyData<String, SettingsEnums.ShowHide>(upData.aboutMe, SettingsEnums.ShowHide.SHOW);
        this.firstLastName = new DataWithPrivacyData<FirstLastName, SettingsEnums.ShowHide>(new FirstLastName(upData.firstName, upData.lastName), privacySettings.containsKey("FLNamePv") ? SettingsEnums.ShowHide.fromValue(privacySettings.get("FLNamePv")) : PRIVACY_DEFAULT_FIRSTLASTNAME);
        this.mobilePhone = new DataWithPrivacyData<String, SettingsEnums.EveryoneFriendHide>(userData.mobilePhone, privacySettings.containsKey("MobNumPrivacy") ? SettingsEnums.EveryoneFriendHide.fromValue(privacySettings.get("MobNumPrivacy")) : PRIVACY_DEFAULT_MOBILEPHONE);
        this.externalEmail = new DataWithPrivacyData<String, SettingsEnums.EveryoneFollowerFriendHide>(userData.emailAddress, privacySettings.containsKey("ExtEmPv") ? SettingsEnums.EveryoneFollowerFriendHide.fromValue(privacySettings.get("ExtEmPv")) : PRIVACY_DEFAULT_EXTERNALEMAIL);
        this.externalEmailVerified = userData.emailVerified;
        this.mig33Email = new DataWithPrivacyData<String, SettingsEnums.ShowHide>(this.getMig33EmailFromUserData(userData), SettingsEnums.ShowHide.SHOW);
        this.city = new DataWithPrivacyData<String, SettingsEnums.ShowHide>(upData.city, SettingsEnums.ShowHide.SHOW);
        this.state = new DataWithPrivacyData<String, SettingsEnums.ShowHide>(upData.state, SettingsEnums.ShowHide.SHOW);
        this.schools = new DataWithPrivacyData<String, SettingsEnums.ShowHide>(upData.schools, SettingsEnums.ShowHide.SHOW);
        this.companies = new DataWithPrivacyData<String, SettingsEnums.ShowHide>(upData.jobs, SettingsEnums.ShowHide.SHOW);
    }

    public String getMig33EmailFromUserData(UserData userData) {
        boolean isUsernameValid = true;
        if (isUsernameValid || userData.alias != null) {
            return String.format("%s@mig33.com", userData.alias == null ? userData.username : userData.alias);
        }
        return null;
    }

    public UserProfileData updateUserProfileData(UserProfileData upData) {
        upData.gender = this.gender.value == null ? null : UserProfileData.GenderEnum.fromValue((String)this.gender.value);
        upData.relationshipStatus = this.relationship.value == null ? null : UserProfileData.RelationshipStatusEnum.fromValue((Integer)this.relationship.value);
        upData.dateOfBirth = (Date)this.dateOfBirth.value;
        upData.hobbies = (String)this.interests.value;
        upData.aboutMe = (String)this.aboutMe.value;
        upData.firstName = ((FirstLastName)this.firstLastName.value).firstName;
        upData.lastName = ((FirstLastName)this.firstLastName.value).lastName;
        upData.city = (String)this.city.value;
        upData.state = (String)this.state.value;
        upData.schools = (String)this.schools.value;
        upData.jobs = (String)this.companies.value;
        return upData;
    }

    public boolean updateUserData(UserData userData) {
        boolean changed = false;
        if ((userData.emailAddress != null || this.externalEmail.value != null) && (userData.emailAddress == null || !userData.emailAddress.equals(this.externalEmail.value)) || this.externalEmail.value != null && null == userData.emailAddress) {
            userData.emailAddress = (String)this.externalEmail.value;
            changed = true;
        }
        return changed;
    }

    public Map<String, Integer> retrievePrivacy() {
        HashMap<String, Integer> values = new HashMap<String, Integer>();
        values.put("DobPrivacy", (this.dateOfBirth.privacy == null ? PRIVACY_DEFAULT_BIRTHDAY : (SettingsEnums.Birthday)((Object)this.dateOfBirth.privacy)).value());
        values.put("FLNamePv", (this.firstLastName.privacy == null ? PRIVACY_DEFAULT_FIRSTLASTNAME : (SettingsEnums.ShowHide)((Object)this.firstLastName.privacy)).value());
        values.put("MobNumPrivacy", (this.mobilePhone.privacy == null ? PRIVACY_DEFAULT_MOBILEPHONE : (SettingsEnums.EveryoneFriendHide)((Object)this.mobilePhone.privacy)).value());
        values.put("ExtEmPv", (this.externalEmail.privacy == null ? PRIVACY_DEFAULT_EXTERNALEMAIL : (SettingsEnums.EveryoneFollowerFriendHide)((Object)this.externalEmail.privacy)).value());
        return values;
    }

    public Map<String, String> retrieveAdditionalUserInfos() {
        HashMap<String, String> values = new HashMap<String, String>();
        return values;
    }

    public static class FirstLastName {
        public String firstName;
        public String lastName;

        public FirstLastName() {
        }

        public FirstLastName(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }
}

