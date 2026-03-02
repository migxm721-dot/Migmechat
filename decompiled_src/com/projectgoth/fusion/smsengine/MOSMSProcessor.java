/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.smsengine;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.data.SystemSMSData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.data.VoucherData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Message;
import com.projectgoth.fusion.interfaces.MessageHome;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import com.projectgoth.fusion.interfaces.Voucher;
import com.projectgoth.fusion.interfaces.VoucherHome;
import com.projectgoth.fusion.smsengine.SMPPGateway;
import com.projectgoth.fusion.smsengine.SMPPID;
import com.projectgoth.fusion.smsengine.SMPPPacket;
import com.projectgoth.fusion.smsengine.SMSControl;
import com.projectgoth.fusion.smsengine.SMSEngine;
import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;

public class MOSMSProcessor {
    public static final int BANGLALINK_GATEWAY_ID = 23;
    public static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(MOSMSProcessor.class));
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public void process(SMPPGateway gateway, int sequence, String source, String destination, String message) {
        this.executor.execute(new Task(gateway, sequence, source, destination, message));
    }

    private class Task
    implements Runnable {
        private SMPPGateway gateway;
        private int sequence;
        private String source;
        private String destination;
        private String message;

        public Task(SMPPGateway gateway, int sequence, String source, String destination, String message) {
            this.gateway = gateway;
            this.sequence = sequence;
            this.source = source;
            this.destination = destination;
            this.message = message.trim().toLowerCase();
        }

        public void run() {
            try {
                if (this.gateway.gatewayData.id == 23) {
                    String username = this.getUsernameFromMobilePhone(this.source);
                    if ("mig33".equals(this.message)) {
                        this.sendBanglalinkURLDownloadSMS(username);
                    }
                    this.sendBanglainkVoucherSMS(username);
                }
                this.sendDeliverSMResponse();
            }
            catch (Exception e) {
                log.warn((Object)("Gateway " + this.gateway.getID() + ". Failed to process MO SMS"), (Throwable)e);
            }
        }

        private String getUsernameFromMobilePhone(String mobilePhone) throws CreateException, RemoteException {
            User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
            UserData userData = userEJB.loadUserFromMobilePhone(mobilePhone);
            return userData == null ? null : userData.username;
        }

        private void sendBanglalinkURLDownloadSMS(String username) throws CreateException, RemoteException, NoSuchFieldException, UnknownHostException {
            if (!SMSControl.isSendEnabledForSubtype(SystemSMSData.SubTypeEnum.BANGLALINK_URL_DOWNLOAD, username)) {
                return;
            }
            SystemSMSData systemSMSData = new SystemSMSData();
            systemSMSData.username = username;
            systemSMSData.type = SystemSMSData.TypeEnum.STANDARD;
            systemSMSData.subType = SystemSMSData.SubTypeEnum.BANGLALINK_URL_DOWNLOAD;
            systemSMSData.source = this.destination;
            systemSMSData.destination = this.source;
            systemSMSData.messageText = SystemProperty.get("BanglalinkURLDownloadSMS");
            Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
            messageEJB.sendSystemSMS(systemSMSData, new AccountEntrySourceData(SMSEngine.class));
            log.info((Object)("Gateway " + this.gateway.getID() + ". Download URL sent to " + this.source + ". " + systemSMSData.messageText));
        }

        private void sendBanglainkVoucherSMS(String username) throws CreateException, RemoteException, NoSuchFieldException, UnknownHostException {
            if (!SMSControl.isSendEnabledForSubtype(SystemSMSData.SubTypeEnum.BANGLALINK_VOUCHER, username)) {
                return;
            }
            Voucher voucherEJB = (Voucher)EJBHomeCache.getObject("ejb/Voucher", VoucherHome.class);
            VoucherData voucherData = voucherEJB.activateVoucher("blvoucher");
            if (voucherData == null) {
                log.warn((Object)"Failed to process MO SMS - No voucher available");
                return;
            }
            SystemSMSData systemSMSData = new SystemSMSData();
            systemSMSData.username = username;
            systemSMSData.type = SystemSMSData.TypeEnum.STANDARD;
            systemSMSData.subType = SystemSMSData.SubTypeEnum.BANGLALINK_VOUCHER;
            systemSMSData.source = this.destination;
            systemSMSData.destination = this.source;
            systemSMSData.messageText = SystemProperty.get("BanglalinkVoucherSMS").replaceAll("%n", voucherData.number);
            Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
            messageEJB.sendSystemSMS(systemSMSData, new AccountEntrySourceData(SMSEngine.class));
            log.info((Object)("Gateway " + this.gateway.getID() + ". Voucher sent to " + this.source + ". " + systemSMSData.messageText));
        }

        private void sendDeliverSMResponse() throws IOException {
            SMPPPacket deliverResponse = new SMPPPacket(SMPPID.DELIVER_SM_RESP);
            deliverResponse.setSequence(this.sequence);
            deliverResponse.put("");
            this.gateway.sendSMPPPacket(deliverResponse, false);
        }
    }
}

