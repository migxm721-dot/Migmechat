/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.objectcache;

import com.projectgoth.fusion.slice.RegistryPrx;
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.slice.UserPrxHelper;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ChatUserRegistry {
    private RegistryPrx registryPrx;

    public ChatUserRegistry(RegistryPrx registryPrx) {
        this.registryPrx = registryPrx;
    }

    private UserPrx[] getOneWayUserProxies(String[] userList) {
        UserPrx[] userProxies = this.registryPrx.findUserObjects(userList);
        if (userProxies == null || userProxies.length == 0) {
            return null;
        }
        for (int i = 0; i < userProxies.length; ++i) {
            userProxies[i] = UserPrxHelper.uncheckedCast(userProxies[i].ice_oneway());
            userProxies[i] = (UserPrx)userProxies[i].ice_connectionId("OneWayProxyGroup");
        }
        return userProxies;
    }

    public void contactChangedStatusMessage(String[] broadcastListArray, String username, String statusMessage, long timeStamp) {
        UserPrx[] userProxies = this.getOneWayUserProxies(broadcastListArray);
        if (userProxies == null) {
            return;
        }
        for (UserPrx userPrx : userProxies) {
            try {
                userPrx.contactChangedStatusMessageOneWay(username, statusMessage, timeStamp);
            }
            catch (Exception e) {
                // empty catch block
            }
        }
    }

    public void contactChangedDisplayPicture(String[] broadcastListArray, String username, String displayPicture, long timeStamp) {
        UserPrx[] userProxies = this.getOneWayUserProxies(broadcastListArray);
        if (userProxies == null) {
            return;
        }
        for (UserPrx userPrx : userProxies) {
            try {
                userPrx.contactChangedDisplayPictureOneWay(username, displayPicture, timeStamp);
            }
            catch (Exception e) {
                // empty catch block
            }
        }
    }

    public void contactChangedPresence(String[] watchers, int imTypeValue, String username, int presenceValue) {
        UserPrx[] userProxies = this.getOneWayUserProxies(watchers);
        if (userProxies == null) {
            return;
        }
        for (UserPrx userPrx : userProxies) {
            try {
                userPrx.contactChangedPresenceOneWay(imTypeValue, username, presenceValue);
            }
            catch (Exception e) {
                // empty catch block
            }
        }
    }

    public Map<String, UserPrx> findUserObjectsMap(String[] users) {
        return this.registryPrx.findUserObjectsMap(users);
    }
}

