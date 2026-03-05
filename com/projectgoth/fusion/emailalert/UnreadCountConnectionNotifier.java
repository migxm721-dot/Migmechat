/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.LocalException
 */
package com.projectgoth.fusion.emailalert;

import Ice.LocalException;
import com.projectgoth.fusion.emailalert.EmailAlert;
import com.projectgoth.fusion.slice.UserPrx;
import java.util.Date;

public class UnreadCountConnectionNotifier
implements Runnable {
    String username;
    String password;
    UserPrx userProxy;
    Date dateRequestReceived;

    public UnreadCountConnectionNotifier(String username, String password, UserPrx userProxy) {
        this.username = username;
        this.password = password;
        this.userProxy = userProxy;
        this.dateRequestReceived = new Date();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void run() {
        try {
            try {
                Date now = new Date();
                if (now.getTime() - this.dateRequestReceived.getTime() > 1200000L) {
                    EmailAlert.discardedGatewayQueriesCounter.add();
                    Object var4_4 = null;
                    EmailAlert.processedGatewayQueriesCounter.add();
                    return;
                }
                int unreadEmailCount = EmailAlert.getUnreadEmailCountFromIMAP(this.username, this.password);
                EmailAlert.logger.debug((Object)("Notifying Gateway that unread email count for '" + this.username + "' is " + unreadEmailCount));
                this.userProxy.emailNotification(unreadEmailCount);
            }
            catch (LocalException e) {
                Object var4_6 = null;
                EmailAlert.processedGatewayQueriesCounter.add();
                return;
            }
            catch (Exception exception) {
                Object var4_7 = null;
                EmailAlert.processedGatewayQueriesCounter.add();
                return;
            }
        }
        catch (Throwable throwable) {
            Object var4_8 = null;
            EmailAlert.processedGatewayQueriesCounter.add();
            throw throwable;
        }
        Object var4_5 = null;
        EmailAlert.processedGatewayQueriesCounter.add();
    }
}

