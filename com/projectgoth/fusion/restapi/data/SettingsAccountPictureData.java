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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlRootElement(name="settings")
public class SettingsAccountPictureData {
    public SettingsEnums.DisplayPictureChoice displayPicture;
    public static final SettingsEnums.DisplayPictureChoice DEFAULT_DISPLAY_PICTURE = SettingsEnums.DisplayPictureChoice.AVATAR;

    public SettingsAccountPictureData() {
    }

    public SettingsAccountPictureData(int displayPictureSetting) {
        this.displayPicture = SettingsEnums.DisplayPictureChoice.fromValue(displayPictureSetting);
        if (this.displayPicture == null) {
            this.displayPicture = DEFAULT_DISPLAY_PICTURE;
        }
    }

    public int retrieveDisplayPictureSetting() {
        return (this.displayPicture == null ? DEFAULT_DISPLAY_PICTURE : this.displayPicture).value();
    }
}

