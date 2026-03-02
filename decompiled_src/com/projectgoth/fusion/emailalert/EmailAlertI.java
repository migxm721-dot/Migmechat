/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 */
package com.projectgoth.fusion.emailalert;

import Ice.Current;
import com.projectgoth.fusion.emailalert.EmailAlert;
import com.projectgoth.fusion.emailalert.UnreadCountConnectionNotifier;
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.slice._EmailAlertDisp;

public class EmailAlertI
extends _EmailAlertDisp {
    public void requestUnreadEmailCount(String username, String password, UserPrx userProxy, Current __current) {
        EmailAlert.receivedGatewayQueriesCounter.add();
        if (EmailAlert.gatewayQueriesPool.getQueue().size() >= 100000) {
            EmailAlert.discardedGatewayQueriesCounter.add();
            return;
        }
        EmailAlert.logger.debug((Object)("Processing Gateway request for unread email count for '" + username + "'"));
        EmailAlert.gatewayQueriesPool.execute(new UnreadCountConnectionNotifier(username, password, userProxy));
    }
}

