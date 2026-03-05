/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.app.dao.db.memcache;

import com.projectgoth.fusion.app.dao.DAOException;
import com.projectgoth.fusion.app.dao.base.MessageDAOChain;
import com.projectgoth.fusion.common.MemCachedClientWrapper;
import com.projectgoth.fusion.common.MemCachedKeySpaces;
import com.projectgoth.fusion.data.AlertMessageData;
import com.projectgoth.fusion.fdl.enums.AlertContentType;
import java.util.Date;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MemcacheMessageDAOChain
extends MessageDAOChain {
    @Override
    public List<AlertMessageData> getLatestAlertMessageList(int midletVersion, AlertMessageData.TypeEnum type, int countryId, Date minimumDate, AlertContentType alertContentType, int clientType) throws DAOException {
        String key = null;
        key = alertContentType == null ? clientType + "/" + midletVersion + "/" + (Object)((Object)type) + "/" + countryId : clientType + "/" + midletVersion + "/" + (Object)((Object)type) + "/" + countryId + "/" + alertContentType.value();
        List<AlertMessageData> alertMessages = (List<AlertMessageData>)MemCachedClientWrapper.get(MemCachedKeySpaces.CommonKeySpace.ALERT_MESSAGE, key);
        if (alertContentType == null) {
            alertMessages = super.getLatestAlertMessageList(midletVersion, type, countryId, minimumDate, alertContentType, clientType);
            MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.ALERT_MESSAGE, key, alertMessages);
        }
        return alertMessages;
    }
}

