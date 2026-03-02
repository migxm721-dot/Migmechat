/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.mail.internet.AddressException
 *  javax.mail.internet.InternetAddress
 */
package com.projectgoth.fusion.uns.domain;

import com.projectgoth.fusion.uns.domain.Note;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class EmailNote
extends Note {
    private String sender;
    private String senderPassword;
    private Set<String> recipients = new HashSet<String>();
    private String subject;
    private List<EmailPart> emailExtraParts = new ArrayList<EmailPart>();
    private String mimeType;

    public EmailNote(String text, String subject) {
        this(text, subject, null);
    }

    public EmailNote(String text, String subject, String mimeType) {
        super(text);
        this.subject = subject;
        this.mimeType = mimeType;
    }

    public EmailNote(String sender, String senderPassword, String text, String subject) {
        super(text);
        this.sender = sender;
        this.senderPassword = senderPassword;
        this.subject = subject;
    }

    public Set<String> getRecipients() {
        return this.recipients;
    }

    public void addRecipient(String recipient) {
        this.recipients.add(recipient);
    }

    public String getSender() {
        return this.sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSenderPassword() {
        return this.senderPassword;
    }

    public void setSenderPassword(String senderPassword) {
        this.senderPassword = senderPassword;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public boolean hasRecipients() {
        return !this.recipients.isEmpty();
    }

    public InternetAddress getSenderInternetAddress() throws AddressException {
        return new InternetAddress(this.sender);
    }

    public InternetAddress[] getRecipientInternetAddresses() throws AddressException {
        InternetAddress[] recipientAddresses = new InternetAddress[this.recipients.size()];
        int i = 0;
        for (String recipient : this.recipients) {
            recipientAddresses[i++] = new InternetAddress(recipient);
        }
        return recipientAddresses;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public void addExtraPart(EmailPart part) {
        this.emailExtraParts.add(part);
    }

    public EmailPart get(int index) {
        return this.emailExtraParts.get(index);
    }

    public int extraPartCount() {
        return this.emailExtraParts.size();
    }

    public static class EmailPart {
        public String message;
        public String mimeType;

        public EmailPart(String message, String mimeType) {
            this.message = message;
            this.mimeType = mimeType;
        }

        public String toString() {
            return "MIMEType=[" + this.mimeType + "];Message=[" + this.message + "]";
        }
    }
}

