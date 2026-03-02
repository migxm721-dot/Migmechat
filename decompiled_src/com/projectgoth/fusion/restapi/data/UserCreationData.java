/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.restapi.data;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.CountryData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.MISLocal;
import com.projectgoth.fusion.interfaces.MISLocalHome;
import java.lang.reflect.Field;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.log4j.Logger;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlRootElement(name="create")
public class UserCreationData {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(UserCreationData.class));
    public String registrationType;
    public String username;
    public String password;
    public String emailAddress;
    public String mobilePhone;
    public Integer countryID;
    public String countryISOCode;
    public Long dateOfBirth;
    public String registrationIPAddress;
    public String referrerUsername;
    public String campaign;
    public Integer type;
    public String registrationToken;
    public String registrationDevice;
    public String userAgent;
    public String fbid;
    public String accessToken;
    public String invitationToken;

    public void initializeToDefaultValues() {
        Field[] fields = this.getClass().getFields();
        try {
            for (Field field : fields) {
                String value;
                if (!field.getType().toString().equals("class java.lang.String") || (value = (String)field.get(this)) != null && value.length() != 0) continue;
                field.set(this, "");
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (this.countryID == null) {
            this.countryID = 0;
        }
        if (this.dateOfBirth == null) {
            this.dateOfBirth = 0L;
        }
        if (this.type == null) {
            this.type = UserData.TypeEnum.MIG33.value();
        }
    }

    public UserData getUserData() {
        if (SystemProperty.getBool(SystemPropertyEntities.Temp.WW384_FALL_BACK_ON_ISO_COUNTRY_CODE_ENABLED)) {
            if (log.isDebugEnabled()) {
                log.debug((Object)String.format("UserCreationData: countryID: %d, ISO country code: %s", this.countryID, this.countryISOCode));
            }
            if (!(null != this.countryID && 0 != this.countryID || StringUtil.isBlank(this.countryISOCode))) {
                CountryData cd = null;
                try {
                    MISLocal misEJB = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
                    cd = misEJB.getCountryByISOCode(this.countryISOCode);
                }
                catch (Exception exception) {
                    // empty catch block
                }
                if (null != cd) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)String.format("Got the country data based on ISO country code: %s, country ID: %d", this.countryISOCode, cd.id));
                    }
                    this.countryID = cd.id;
                }
            }
        }
        UserData userData = new UserData();
        userData.username = this.username;
        userData.password = this.password;
        userData.emailAddress = this.emailAddress;
        userData.mobilePhone = this.mobilePhone;
        userData.countryID = this.countryID;
        userData.registrationIPAddress = this.registrationIPAddress;
        userData.registrationDevice = this.registrationDevice;
        if (SystemPropertyEntities.Temp.Cache.SE511_ENABLED.getValue().booleanValue()) {
            userData.userAgent = this.userAgent;
        }
        userData.type = UserData.TypeEnum.fromValue(this.type);
        return userData;
    }
}

