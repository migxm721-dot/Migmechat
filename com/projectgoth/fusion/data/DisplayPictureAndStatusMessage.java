/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.danga.MemCached.MemCachedClient
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.data;

import com.danga.MemCached.MemCachedClient;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.MemCachedClientWrapper;
import com.projectgoth.fusion.common.MemCachedUtils;
import com.projectgoth.fusion.data.ContactData;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DisplayPictureAndStatusMessage
implements Serializable {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(DisplayPictureAndStatusMessage.class));
    private String displayPicture;
    private String statusMessage;
    private Date statusTimestamp;

    public DisplayPictureAndStatusMessage() {
    }

    public DisplayPictureAndStatusMessage(ContactData contact) {
        this.displayPicture = contact.displayPicture;
        this.statusMessage = contact.statusMessage;
        this.statusTimestamp = contact.statusTimeStamp;
    }

    public String getDisplayPicture() {
        return this.displayPicture;
    }

    public void setDisplayPicture(String displayPicture) {
        this.displayPicture = displayPicture;
    }

    public String getStatusMessage() {
        return this.statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public Date getStatusTimestamp() {
        return this.statusTimestamp;
    }

    public void setStatusTimestamp(Date statusTimestamp) {
        this.statusTimestamp = statusTimestamp;
    }

    public static String getKey(String username) {
        return username;
    }

    public static DisplayPictureAndStatusMessage getDisplayPictureAndStatusMessage(MemCachedClient instance, String username) {
        try {
            Object obj = instance.get(DisplayPictureAndStatusMessage.getKey(username));
            if (obj instanceof DisplayPictureAndStatusMessage) {
                return (DisplayPictureAndStatusMessage)obj;
            }
            return null;
        }
        catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug((Object)"Failed to get object since serialVersionUID is diff, ignoring and return null instead", (Throwable)e);
            }
            return null;
        }
    }

    public static void setDisplayPictureAndStatusMessage(MemCachedClient instance, String username, DisplayPictureAndStatusMessage displayPictureAndStatusMessage) {
        instance.set(DisplayPictureAndStatusMessage.getKey(username), (Object)displayPictureAndStatusMessage);
    }

    public static void setDisplayPictureAndStatusMessage(MemCachedClient instance, ContactData contact) {
        DisplayPictureAndStatusMessage avatar = new DisplayPictureAndStatusMessage(contact);
        DisplayPictureAndStatusMessage.setDisplayPictureAndStatusMessage(instance, contact.fusionUsername, avatar);
    }

    public static void setMultiDisplayPictureAndStatusMessage(MemCachedClient instance, Collection<ContactData> contacts) {
        if (instance == null) {
            instance = MemCachedClientWrapper.getMemCachedClient(MemCachedUtils.Instance.userDisplayPictureAndStatus);
        }
        for (ContactData contact : contacts) {
            DisplayPictureAndStatusMessage.setDisplayPictureAndStatusMessage(instance, contact);
        }
    }

    public static boolean deleteDisplayPictureAndStatusMessage(MemCachedClient instance, String username) {
        return instance.delete(username);
    }
}

