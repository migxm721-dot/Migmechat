/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.EJBException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.clientsession;

import com.projectgoth.fusion.clientsession.SSOLogoutClientContext;
import com.projectgoth.fusion.clientsession.SSOLogoutSessionInfo;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.ejb.EJBIcePrxFinder;
import com.projectgoth.fusion.slice.ConnectionPrx;
import javax.ejb.EJBException;
import org.apache.log4j.Logger;

public class SimpleSSOLogoutClientContext
implements SSOLogoutClientContext {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(SimpleSSOLogoutClientContext.class));

    public void postLogout(SSOLogoutSessionInfo sessionInfo) {
        try {
            ConnectionPrx connectionPrx = EJBIcePrxFinder.findConnectionPrx(sessionInfo.sessionID);
            if (connectionPrx != null) {
                connectionPrx.logout();
            }
        }
        catch (EJBException e) {
        }
        catch (Exception e) {
            log.error((Object)String.format("Unable to logout fusion session %s", sessionInfo.sessionID), (Throwable)e);
        }
    }
}

