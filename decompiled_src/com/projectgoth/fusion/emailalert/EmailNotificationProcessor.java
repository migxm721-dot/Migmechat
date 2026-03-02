/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.LocalException
 */
package com.projectgoth.fusion.emailalert;

import Ice.LocalException;
import com.projectgoth.fusion.common.RMIExceptionHelper;
import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.emailalert.EmailAlert;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import com.projectgoth.fusion.slice.UserPrx;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.HashSet;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class EmailNotificationProcessor
implements Runnable {
    Socket socket;
    String from;
    String subject;
    boolean parsedFirstToLine = false;
    boolean parsedFirstFromLine = false;
    boolean parsedFirstSubjectLine = false;
    boolean finishedProcessing = false;

    public EmailNotificationProcessor(Socket socket) {
        this.socket = socket;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public void run() {
        block27: {
            block28: {
                block26: {
                    block25: {
                        out = null;
                        in = null;
                        subject = null;
                        from = null;
                        try {
                            try {
                                out = new PrintWriter(this.socket.getOutputStream(), true);
                                in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                                out.println("220 " + EmailAlert.hostName + " ESMTP");
                                EmailAlert.logger.debug((Object)(">" + this.socket.getInetAddress().getHostAddress() + ": 220 " + EmailAlert.hostName + " ESMTP"));
                                inputLine = in.readLine();
                                if (inputLine == null) {
                                    EmailAlert.logger.warn((Object)("Accepted connection from " + this.socket.getInetAddress().getHostAddress() + " but no data was received. Terminating connection"));
                                    out.close();
                                    in.close();
                                    this.socket.close();
                                    var10_7 = null;
                                    break block25;
                                }
                                EmailAlert.logger.debug((Object)("<" + this.socket.getInetAddress().getHostAddress() + ": " + inputLine));
                                if (!inputLine.startsWith("EHLO") && !inputLine.startsWith("HELO")) {
                                    EmailAlert.logger.warn((Object)("Accepted connection from " + this.socket.getInetAddress().getHostAddress() + " but didn't receive EHLO or HELO. Instead we received \"" + inputLine + "\". Terminating connection"));
                                    out.close();
                                    in.close();
                                    this.socket.close();
                                    break block26;
                                }
                                out.println("250 Hello");
                                EmailAlert.logger.debug((Object)(">" + this.socket.getInetAddress().getHostAddress() + ": 250 Hello"));
lbl30:
                                // 10 sources

                                while ((inputLine = in.readLine()) != null) {
                                    EmailAlert.logger.debug((Object)("<" + this.socket.getInetAddress().getHostAddress() + ": " + inputLine));
                                    if (inputLine.startsWith("MAIL") || inputLine.startsWith("RCPT") || inputLine.startsWith("RSET") || inputLine.equals(".")) {
                                        out.println("250 Ok.");
                                        EmailAlert.logger.debug((Object)(">" + this.socket.getInetAddress().getHostAddress() + ": 250 Ok."));
                                        continue;
                                    }
                                    if (inputLine.startsWith("DATA")) {
                                        out.println("354 Ok.");
                                        EmailAlert.logger.debug((Object)(">" + this.socket.getInetAddress().getHostAddress() + ": 354 Ok."));
                                        continue;
                                    }
                                    if (inputLine.startsWith("QUIT")) {
                                        out.println("221 Bye.");
                                        EmailAlert.logger.debug((Object)(">" + this.socket.getInetAddress().getHostAddress() + ": 221 Bye."));
                                        out.close();
                                        in.close();
                                        this.socket.close();
                                        break block27;
                                    }
                                    if (inputLine.startsWith("Subject: ")) {
                                        if (this.parsedFirstSubjectLine) {
                                            subject = inputLine.substring(9, inputLine.length());
                                            continue;
                                        }
                                        this.parsedFirstSubjectLine = true;
                                        continue;
                                    }
                                    if (inputLine.startsWith("From: ")) {
                                        if (this.parsedFirstFromLine) {
                                            from = inputLine.substring(6, inputLine.length());
                                            continue;
                                        }
                                        this.parsedFirstFromLine = true;
                                        continue;
                                    }
                                    if (!inputLine.startsWith("To: ")) continue;
                                    if (!this.parsedFirstToLine || this.finishedProcessing) {
                                        this.parsedFirstToLine = true;
                                        continue;
                                    }
                                    break block28;
                                }
                                break block27;
                            }
                            catch (Exception e) {
                                EmailAlert.logger.warn((Object)("Socket exception on port " + this.socket.getLocalPort() + ": " + e.getMessage()));
                                try {
                                    out.close();
                                    in.close();
                                    this.socket.close();
                                }
                                catch (Exception e2) {
                                }
                                var10_10 = null;
                                try {
                                    EmailAlert.logger.debug((Object)("Closing connection from " + this.socket.getInetAddress().getHostAddress()));
                                    out.close();
                                    in.close();
                                    this.socket.close();
                                    return;
                                }
                                catch (Exception e2) {
                                    return;
                                }
                            }
                        }
                        catch (Throwable var9_21) {
                            var10_11 = null;
                            ** try [egrp 3[TRYBLOCK] [9 : 944->996)] { 
lbl87:
                            // 1 sources

                            EmailAlert.logger.debug((Object)("Closing connection from " + this.socket.getInetAddress().getHostAddress()));
                            out.close();
                            in.close();
                            this.socket.close();
                            throw var9_21;
lbl92:
                            // 1 sources

                            catch (Exception e2) {
                                // empty catch block
                            }
                            throw var9_21;
                        }
                    }
                    ** try [egrp 3[TRYBLOCK] [9 : 944->996)] { 
lbl97:
                    // 1 sources

                    EmailAlert.logger.debug((Object)("Closing connection from " + this.socket.getInetAddress().getHostAddress()));
                    out.close();
                    in.close();
                    this.socket.close();
                    return;
lbl102:
                    // 1 sources

                    catch (Exception e2) {
                        // empty catch block
                    }
                    return;
                }
                var10_8 = null;
                ** try [egrp 3[TRYBLOCK] [9 : 944->996)] { 
lbl108:
                // 1 sources

                EmailAlert.logger.debug((Object)("Closing connection from " + this.socket.getInetAddress().getHostAddress()));
                out.close();
                in.close();
                this.socket.close();
                return;
lbl113:
                // 1 sources

                catch (Exception e2) {
                    // empty catch block
                }
                return;
            }
            do {
                if ((recipients = this.findEmailAliases(inputLine.toLowerCase())) == null) continue;
                for (String recipient : recipients) {
                    EmailAlert.receivedNotificationsCounter.add();
                    this.processNotification(recipient, from, subject);
                }
            } while ((inputLine = in.readLine()) != null && inputLine.startsWith("  "));
            this.finishedProcessing = true;
            ** GOTO lbl30
        }
        var10_9 = null;
        try {}
        catch (Exception e2) {
            return;
        }
        EmailAlert.logger.debug((Object)("Closing connection from " + this.socket.getInetAddress().getHostAddress()));
        out.close();
        in.close();
        this.socket.close();
    }

    private HashSet<String> findEmailAliases(String line) {
        int atIndex = line.indexOf("@" + EmailAlert.mailDomain);
        if (atIndex <= 0) {
            EmailAlert.logger.warn((Object)("Unable to process email notification as I couldn't find a recipient where I was supposed to. SMTP line: " + line));
            return null;
        }
        HashSet<String> recipients = new HashSet<String>();
        do {
            int startIndex;
            for (startIndex = atIndex - 1; startIndex >= 0 && line.charAt(startIndex) != '<' && line.charAt(startIndex) != '\'' && line.charAt(startIndex) != ' ' && line.charAt(startIndex) != '\"'; --startIndex) {
            }
            String alias = null;
            try {
                alias = line.substring(startIndex + 1, atIndex);
            }
            catch (Exception e) {
                // empty catch block
            }
            if (alias == null || alias.length() <= 0) continue;
            recipients.add(alias.toLowerCase());
        } while ((atIndex = line.indexOf("@" + EmailAlert.mailDomain, atIndex + 1)) > 0);
        return recipients;
    }

    private void processNotification(String recipient, String from, String subject) {
        if (EmailAlert.aliasesToIgnore.indexOf(recipient) >= 0) {
            return;
        }
        EmailAlert.logger.debug((Object)("Processing email notification. To=" + recipient + " From=" + from + " Subject=" + subject));
        User userEJB = null;
        String password = null;
        try {
            userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
        }
        catch (Exception e) {
            EmailAlert.logger.warn((Object)("Unable to create EJB to retrieve the password of the user " + recipient + ". Exception: " + e.getMessage()));
            return;
        }
        try {
            password = userEJB.processEmailNotification(recipient, from, subject, new AccountEntrySourceData(EmailAlert.class));
        }
        catch (RemoteException e) {
            EmailAlert.logger.warn((Object)("Unable to call UserEJB to process email notification. Exception: " + RMIExceptionHelper.getRootMessage(e)));
            return;
        }
        catch (Exception e) {
            EmailAlert.logger.warn((Object)("Unable to call UserEJB to process email notification. Exception: " + e.getMessage()));
            return;
        }
        UserPrx userPrx = null;
        try {
            userPrx = EmailAlert.icePrxFinder.findOnewayUserPrx(recipient);
        }
        catch (LocalException e) {
            EmailAlert.logger.warn((Object)("Unable to search for User proxy for the user " + recipient + ": " + e.getMessage()));
            return;
        }
        catch (Exception e) {
            EmailAlert.logger.warn((Object)("Unable to search for User proxy for the user " + recipient + ": " + e.getMessage()));
            return;
        }
        if (userPrx != null) {
            int unreadEmailCount;
            try {
                unreadEmailCount = EmailAlert.getUnreadEmailCountFromIMAP(recipient, password);
            }
            catch (Exception e) {
                EmailAlert.logger.warn((Object)("Unable to query IMAP server for unread email count for the user " + recipient + ": " + e.getMessage()));
                return;
            }
            try {
                userPrx.emailNotification(unreadEmailCount);
            }
            catch (LocalException e) {
                EmailAlert.logger.warn((Object)("Unable to notify User proxy for the user " + recipient + ": " + e.getMessage()));
            }
        }
        EmailAlert.processedNotificationsCounter.add();
    }
}

