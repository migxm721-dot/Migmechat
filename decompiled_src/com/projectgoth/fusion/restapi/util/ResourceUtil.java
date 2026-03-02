/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.restapi.util;

import com.projectgoth.fusion.clientsession.SSOLogin;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.ejb.EJBIcePrxFinder;
import com.projectgoth.fusion.restapi.data.FusionRestException;
import com.projectgoth.fusion.slice.ConnectionPrx;
import org.apache.log4j.Logger;

public class ResourceUtil {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ResourceUtil.class));

    public static String getSID(String sessionId) throws FusionRestException {
        String sID = null;
        sID = SSOLogin.isEncryptedSessionID(sessionId) ? SSOLogin.getSessionIDFromEncryptedSessionID(sessionId) : sessionId;
        if (sID == null) {
            log.error((Object)String.format("Failed to retreived session id.%s", sID));
            throw new FusionRestException(FusionRestException.RestException.MISSING_SESSION_ID, "Invalid session ID ");
        }
        return sID;
    }

    public static ConnectionPrx getConnectionProxy(String sessionId) throws FusionRestException {
        String sID = ResourceUtil.getSID(sessionId);
        ConnectionPrx prx = EJBIcePrxFinder.getConnectionProxy(sID);
        if (prx == null) {
            log.error((Object)String.format("Failed to retrieved connection from session id. %s", sID));
            throw new FusionRestException(FusionRestException.RestException.ERROR, "Invalid session ");
        }
        return prx;
    }
}

