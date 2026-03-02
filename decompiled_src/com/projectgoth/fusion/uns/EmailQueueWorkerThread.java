/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.mail.Address
 *  javax.mail.BodyPart
 *  javax.mail.Flags$Flag
 *  javax.mail.Folder
 *  javax.mail.Message
 *  javax.mail.Message$RecipientType
 *  javax.mail.Multipart
 *  javax.mail.Session
 *  javax.mail.Store
 *  javax.mail.Transport
 *  javax.mail.internet.InternetAddress
 *  javax.mail.internet.MimeBodyPart
 *  javax.mail.internet.MimeMessage
 *  javax.mail.internet.MimeMessage$RecipientType
 *  javax.mail.internet.MimeMultipart
 *  org.apache.log4j.Logger
 *  org.springframework.mail.MailSender
 *  org.springframework.mail.SimpleMailMessage
 */
package com.projectgoth.fusion.uns;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.uns.domain.EmailNote;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.log4j.Logger;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class EmailQueueWorkerThread
extends Thread {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(EmailQueueWorkerThread.class));
    private static final Logger emailLogger = Logger.getLogger((String)"EmailQueueWorker");
    private static final String emailLoggerMsg = "|%s|%s|%s|%s";
    private BlockingQueue<EmailNote> queue;
    private MailSender mailSender;
    private String mailServer;
    private boolean dryRun;
    private AtomicLong emailsSent;

    public EmailQueueWorkerThread(BlockingQueue<EmailNote> queue, MailSender mailSender, String mailServer, boolean dryRun, AtomicLong emailsSent) {
        this.queue = queue;
        this.mailSender = mailSender;
        this.mailServer = mailServer;
        this.dryRun = dryRun;
        this.emailsSent = emailsSent;
        System.setProperty("mail.smtp.host", mailServer);
        System.setProperty("mail.smtp.timeout", "20000");
        System.setProperty("mail.smtp.connectiontimeout", "20000");
        System.setProperty("mail.imap.timeout", "20000");
        System.setProperty("mail.imap.connectiontimeout", "20000");
    }

    @Override
    public void run() {
        while (true) {
            try {
                while (true) {
                    if (SystemProperty.getBool(SystemPropertyEntities.UserNotificationServiceSettings.NEW_EMAIL_SENDING_LOGIC_ENABLED)) {
                        this.newEmailSendingLogic();
                        continue;
                    }
                    this.oldEmailSendingLogic();
                }
            }
            catch (InterruptedException e) {
                continue;
            }
            catch (Exception e) {
                log.error((Object)"failed to send email ", (Throwable)e);
                continue;
            }
            break;
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void oldEmailSendingLogic() throws Exception {
        EmailNote note = this.queue.take();
        if (note.getSender() == null) {
            SimpleMailMessage message = new SimpleMailMessage();
            if (note.getRecipients().size() > 1) {
                message.setTo("\"migme\" <mig@mig.me>");
                message.setBcc(note.getRecipients().toArray(new String[0]));
            } else {
                message.setTo(note.getRecipients().toArray(new String[0]));
            }
            message.setSubject(note.getSubject());
            message.setText(note.getText());
            message.setFrom(SystemProperty.get(SystemPropertyEntities.Default.FROM_EMAIL_ADDRESS));
            if (log.isDebugEnabled()) {
                log.debug((Object)("sending email subject [" + message.getSubject() + "] to [" + StringUtil.asString(message.getTo()) + "] bcc [" + StringUtil.asString(message.getBcc()) + "] body:\n" + message.getText()));
            }
            if (this.dryRun) return;
            try {
                if (SystemProperty.getBool(SystemPropertyEntities.UserNotificationServiceSettings.LOG_BEFORE_SENDING_EMAIL_TO_SMTP_ENABLED)) {
                    emailLogger.info((Object)String.format(emailLoggerMsg, System.currentTimeMillis(), note.getID(), 0, "NULL"));
                }
                this.mailSender.send(message);
                this.emailsSent.incrementAndGet();
                if (!SystemProperty.getBool(SystemPropertyEntities.UserNotificationServiceSettings.LOG_AFTER_SENDING_EMAIL_TO_SMTP_ENABLED)) return;
                emailLogger.info((Object)String.format(emailLoggerMsg, System.currentTimeMillis(), note.getID(), 1, "NULL"));
                return;
            }
            catch (Exception e) {
                emailLogger.info((Object)String.format(emailLoggerMsg, System.currentTimeMillis(), note.getID(), -1, StringUtil.customizeStringForLogging(e.getMessage(), SystemProperty.getInt(SystemPropertyEntities.UserNotificationServiceSettings.MAX_EMAIL_SENDING_ERROR_MSG_LENGTH, 1000))));
                throw new Exception("Error sending " + note.getID() + ".Exception:" + e, e);
            }
        }
        Session session = Session.getInstance((Properties)System.getProperties(), null);
        MimeMessage message = new MimeMessage(session);
        message.setFrom((Address)note.getSenderInternetAddress());
        message.setRecipients(MimeMessage.RecipientType.TO, (Address[])note.getRecipientInternetAddresses());
        message.setSubject(note.getSubject());
        message.setText(note.getText());
        if (!this.dryRun) {
            try {
                if (SystemProperty.getBool(SystemPropertyEntities.UserNotificationServiceSettings.LOG_BEFORE_SENDING_EMAIL_TO_SMTP_ENABLED)) {
                    emailLogger.info((Object)String.format(emailLoggerMsg, System.currentTimeMillis(), note.getID(), 0, "NULL"));
                }
                Transport.send((Message)message);
                this.emailsSent.incrementAndGet();
                if (SystemProperty.getBool(SystemPropertyEntities.UserNotificationServiceSettings.LOG_AFTER_SENDING_EMAIL_TO_SMTP_ENABLED)) {
                    emailLogger.info((Object)String.format(emailLoggerMsg, System.currentTimeMillis(), note.getID(), 1, "NULL"));
                }
            }
            catch (Exception e) {
                emailLogger.info((Object)String.format(emailLoggerMsg, System.currentTimeMillis(), note.getID(), -1, StringUtil.customizeStringForLogging(e.getMessage(), SystemProperty.getInt(SystemPropertyEntities.UserNotificationServiceSettings.MAX_EMAIL_SENDING_ERROR_MSG_LENGTH, 1000))));
                throw new Exception("Error sending " + note.getID() + ".Exception:" + e, e);
            }
        }
        Store store = session.getStore("imap");
        store.connect(this.mailServer, note.getSender(), note.getSenderPassword());
        Folder folder = store.getFolder("Sent Items");
        if (!folder.exists()) {
            folder.create(1);
        }
        folder.open(2);
        message.setFlag(Flags.Flag.SEEN, true);
        message.setSentDate(new Date());
        message.saveChanges();
        if (!this.dryRun) {
            folder.appendMessages(new Message[]{message});
        }
        folder.close(false);
        store.close();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void newEmailSendingLogic() throws Exception {
        InternetAddress[] recipients;
        boolean isSystemNotification;
        EmailNote note = this.queue.take();
        boolean bl = isSystemNotification = note.getSender() == null;
        if (isSystemNotification && note.getMimeType() == null) {
            SimpleMailMessage message = new SimpleMailMessage();
            if (note.getRecipients().size() > 1) {
                message.setTo("\"migme\" <mig@mig.me>");
                message.setBcc(note.getRecipients().toArray(new String[0]));
            } else {
                message.setTo(note.getRecipients().toArray(new String[0]));
            }
            message.setSubject(note.getSubject());
            message.setText(note.getText());
            message.setFrom(SystemProperty.get(SystemPropertyEntities.Default.FROM_EMAIL_ADDRESS));
            if (log.isDebugEnabled()) {
                log.debug((Object)("newEmailSendingLogic():sending email subject [" + message.getSubject() + "] to [" + StringUtil.asString(message.getTo()) + "] bcc [" + StringUtil.asString(message.getBcc()) + "] body:\n" + message.getText()));
            }
            if (this.dryRun) return;
            try {
                if (SystemProperty.getBool(SystemPropertyEntities.UserNotificationServiceSettings.LOG_BEFORE_SENDING_EMAIL_TO_SMTP_ENABLED)) {
                    emailLogger.info((Object)String.format(emailLoggerMsg, System.currentTimeMillis(), note.getID(), 0, "NULL"));
                }
                this.mailSender.send(message);
                this.emailsSent.incrementAndGet();
                if (!SystemProperty.getBool(SystemPropertyEntities.UserNotificationServiceSettings.LOG_AFTER_SENDING_EMAIL_TO_SMTP_ENABLED)) return;
                emailLogger.info((Object)String.format(emailLoggerMsg, System.currentTimeMillis(), note.getID(), 1, "NULL"));
                return;
            }
            catch (Exception e) {
                emailLogger.info((Object)String.format(emailLoggerMsg, System.currentTimeMillis(), note.getID(), -1, StringUtil.customizeStringForLogging(e.getMessage(), SystemProperty.getInt(SystemPropertyEntities.UserNotificationServiceSettings.MAX_EMAIL_SENDING_ERROR_MSG_LENGTH, 1000))));
                throw new Exception("Error sending " + note.getID() + ".Exception:" + e, e);
            }
        }
        Session session = Session.getInstance((Properties)System.getProperties(), null);
        MimeMessage message = new MimeMessage(session);
        if (isSystemNotification) {
            message.setFrom((Address)new InternetAddress(SystemProperty.get(SystemPropertyEntities.Default.FROM_EMAIL_ADDRESS)));
            recipients = note.getRecipientInternetAddresses();
            if (recipients != null && recipients.length > 1) {
                message.setRecipients(MimeMessage.RecipientType.TO, "noreply@mig.me");
                message.setRecipients(MimeMessage.RecipientType.BCC, (Address[])recipients);
            } else {
                message.setRecipients(MimeMessage.RecipientType.TO, (Address[])recipients);
            }
        } else {
            message.setFrom((Address)note.getSenderInternetAddress());
            message.setRecipients(MimeMessage.RecipientType.TO, (Address[])note.getRecipientInternetAddresses());
        }
        message.setSubject(note.getSubject());
        if (note.extraPartCount() == 0) {
            if (note.getMimeType() == null) {
                message.setText(note.getText());
            } else {
                message.setContent((Object)note.getText(), note.getMimeType());
            }
        } else {
            MimeMultipart multiPart = new MimeMultipart("alternative");
            MimeBodyPart mainPart = new MimeBodyPart();
            if (StringUtil.isBlank(note.getMimeType())) {
                mainPart.setText(note.getText());
            } else {
                mainPart.setContent((Object)note.getText(), note.getMimeType());
            }
            multiPart.addBodyPart((BodyPart)mainPart);
            for (int i = 0; i < note.extraPartCount(); ++i) {
                EmailNote.EmailPart extraEmailPart = note.get(i);
                MimeBodyPart extraMimePart = new MimeBodyPart();
                if (StringUtil.isBlank(extraEmailPart.mimeType)) {
                    extraMimePart.setText(extraEmailPart.message);
                } else {
                    extraMimePart.setContent((Object)extraEmailPart.message, extraEmailPart.mimeType);
                }
                multiPart.addBodyPart((BodyPart)extraMimePart);
            }
            message.setContent((Multipart)multiPart);
        }
        if (log.isDebugEnabled()) {
            try {
                recipients = message.getRecipients(Message.RecipientType.TO);
                String contentType = message.getContentType();
                Object content = message.getContent();
                String recipientListToString = recipients == null ? (String)null : Arrays.asList(recipients).toString();
                log.debug((Object)("newEmailSendingLogic():sending email subject [" + message.getSubject() + "] recipients:[" + recipientListToString + "] contentType:[" + contentType + "] body:\n" + content));
            }
            catch (Exception ex) {
                log.info((Object)("Unable to do a log debug for message[" + message + "]"), (Throwable)ex);
            }
        }
        if (!this.dryRun) {
            try {
                if (SystemProperty.getBool(SystemPropertyEntities.UserNotificationServiceSettings.LOG_BEFORE_SENDING_EMAIL_TO_SMTP_ENABLED)) {
                    emailLogger.info((Object)String.format(emailLoggerMsg, System.currentTimeMillis(), note.getID(), 0, "NULL"));
                }
                Transport.send((Message)message);
                this.emailsSent.incrementAndGet();
                if (SystemProperty.getBool(SystemPropertyEntities.UserNotificationServiceSettings.LOG_AFTER_SENDING_EMAIL_TO_SMTP_ENABLED)) {
                    emailLogger.info((Object)String.format(emailLoggerMsg, System.currentTimeMillis(), note.getID(), 1, "NULL"));
                }
            }
            catch (Exception e) {
                emailLogger.info((Object)String.format(emailLoggerMsg, System.currentTimeMillis(), note.getID(), -1, StringUtil.customizeStringForLogging(e.getMessage(), SystemProperty.getInt(SystemPropertyEntities.UserNotificationServiceSettings.MAX_EMAIL_SENDING_ERROR_MSG_LENGTH, 1000))));
                throw new Exception("Error sending " + note.getID() + ".Exception:" + e, e);
            }
        }
        if (isSystemNotification) return;
        Store store = session.getStore("imap");
        store.connect(this.mailServer, note.getSender(), note.getSenderPassword());
        Folder folder = store.getFolder("Sent Items");
        if (!folder.exists()) {
            folder.create(1);
        }
        folder.open(2);
        message.setFlag(Flags.Flag.SEEN, true);
        message.setSentDate(new Date());
        message.saveChanges();
        if (!this.dryRun) {
            folder.appendMessages(new Message[]{message});
        }
        folder.close(false);
        store.close();
    }
}

