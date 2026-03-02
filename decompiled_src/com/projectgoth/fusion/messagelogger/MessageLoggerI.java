/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.messagelogger;

import Ice.Current;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.RMIExceptionHelper;
import com.projectgoth.fusion.common.RequestCounter;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Message;
import com.projectgoth.fusion.interfaces.MessageHome;
import com.projectgoth.fusion.messagelogger.MessageLogger;
import com.projectgoth.fusion.messagelogger.MessageToLog;
import com.projectgoth.fusion.slice._MessageLoggerDisp;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.Logger;

public class MessageLoggerI
extends _MessageLoggerDisp
implements Runnable {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(MessageLoggerI.class));
    public LinkedList<MessageToLog> messagesToLog = new LinkedList();
    public int numMessagesQueued = 0;
    public int maxMessagesQueued = 0;
    private Thread logToDiskThread;
    private StatsSaver statsSaver;
    public RequestCounter receivedMessagesCounter;
    public RequestCounter loggedMessagesCounter;
    private String basePath;
    private BufferedWriter writer = null;
    private boolean disableWritingToFile = false;
    private Date lastMessageDateCreated = null;
    SimpleDateFormat dateFormatterFullTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat dateFormatterDateOnly = new SimpleDateFormat("yyyy-MM-dd");
    HashMap<Integer, MessageStats> messageStatsPerCountry = new HashMap();

    public MessageLoggerI() {
        this.basePath = MessageLogger.properties.getPropertyWithDefault("BasePath", ".");
        this.receivedMessagesCounter = new RequestCounter(MessageLogger.properties.getPropertyAsIntWithDefault("RequestCounterInterval", 10));
        this.loggedMessagesCounter = new RequestCounter(MessageLogger.properties.getPropertyAsIntWithDefault("RequestCounterInterval", 10));
        this.logToDiskThread = new Thread(this);
        this.logToDiskThread.start();
        long saveStatsToDBInterval = MessageLogger.properties.getPropertyAsIntWithDefault("SaveStatsToDBInterval", 600) * 1000;
        log.info((Object)("Message stats will be written to the DB every " + saveStatsToDBInterval / 1000L + " seconds"));
        boolean bl = this.disableWritingToFile = MessageLogger.properties.getPropertyAsIntWithDefault("DisableWritingToFile", 0) == 1;
        if (this.disableWritingToFile) {
            log.info((Object)"Messages will NOT be written to file (DisableWritingToFile is set to 1 in the config file)");
        }
        this.statsSaver = new StatsSaver();
        Timer timer = new Timer(true);
        timer.schedule((TimerTask)this.statsSaver, saveStatsToDBInterval, saveStatsToDBInterval);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void logMessage(int type, int sourceCountryID, String source, String destination, int numRecipients, String messageText, Current __current) {
        MessageToLog.TypeEnum messageType = MessageToLog.TypeEnum.fromValue(type);
        if (messageType == null) {
            log.warn((Object)("Received unknown message type: " + type));
            return;
        }
        LinkedList<MessageToLog> linkedList = this.messagesToLog;
        synchronized (linkedList) {
            this.messagesToLog.add(new MessageToLog(new Date(), sourceCountryID, messageType, source, destination, numRecipients, messageText));
            ++this.numMessagesQueued;
            if (this.numMessagesQueued > this.maxMessagesQueued) {
                this.maxMessagesQueued = this.numMessagesQueued;
            }
        }
        this.receivedMessagesCounter.add();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void run() {
        while (true) {
            MessageToLog messageToLog;
            LinkedList<MessageToLog> linkedList = this.messagesToLog;
            synchronized (linkedList) {
                messageToLog = this.messagesToLog.peek();
            }
            if (messageToLog == null) {
                try {
                    Thread.sleep(200L);
                }
                catch (InterruptedException e) {}
                continue;
            }
            try {
                this.writeMessage(messageToLog);
            }
            catch (Exception e) {
                try {
                    log.warn((Object)("Unable to write message. Exception: " + e.getMessage()));
                    Thread.sleep(10000L);
                }
                catch (InterruptedException e2) {}
                continue;
            }
            linkedList = this.messagesToLog;
            synchronized (linkedList) {
                this.messagesToLog.poll();
                --this.numMessagesQueued;
            }
        }
    }

    private synchronized void writeMessage(MessageToLog messageToLog) throws Exception {
        MessageStats countryStats;
        if (!this.datesAreInTheSameHour(this.lastMessageDateCreated, messageToLog.dateCreated)) {
            this.writeMessageStatsToDB();
            if (this.writer != null) {
                this.writer.close();
            }
            if (!this.disableWritingToFile) {
                this.writer = this.openNewFile(messageToLog.dateCreated);
            }
        }
        if ((countryStats = this.messageStatsPerCountry.get(messageToLog.sourceCountryID)) == null) {
            countryStats = new MessageStats(messageToLog.sourceCountryID);
            this.messageStatsPerCountry.put(messageToLog.sourceCountryID, countryStats);
        }
        switch (messageToLog.type) {
            case PRIVATE: {
                ++countryStats.numPrivate;
                break;
            }
            case GROUPCHAT: {
                ++countryStats.numGroupChatSent;
                countryStats.numGroupChatReceived += messageToLog.numRecipients;
                break;
            }
            case CHATROOM: {
                ++countryStats.numChatRoomSent;
                countryStats.numChatRoomReceived += messageToLog.numRecipients;
                break;
            }
            case SMS: {
                ++countryStats.numSMS;
                break;
            }
            case MSN_SENT: {
                ++countryStats.numMSNSent;
                break;
            }
            case MSN_RECEIVED: {
                ++countryStats.numMSNReceived;
                break;
            }
            case YAHOO_SENT: {
                ++countryStats.numYahooSent;
                break;
            }
            case YAHOO_RECEIVED: {
                ++countryStats.numYahooReceived;
                break;
            }
            case AIM_SENT: {
                ++countryStats.numAIMSent;
                break;
            }
            case AIM_RECEIVED: {
                ++countryStats.numAIMReceived;
                break;
            }
            case GTALK_SENT: {
                ++countryStats.numGTalkSent;
                break;
            }
            case GTALK_RECEIVED: {
                ++countryStats.numGTalkReceived;
                break;
            }
            case FACEBOOK_SENT: {
                ++countryStats.numFacebookSent;
                break;
            }
            case FACEBOOK_RECEIVED: {
                ++countryStats.numFacebookReceived;
                break;
            }
            default: {
                log.warn((Object)("Received unrecognised message type: " + (Object)((Object)messageToLog.type)));
            }
        }
        if (!this.disableWritingToFile) {
            this.writer.write(this.convertMessageToString(messageToLog));
            this.writer.newLine();
            this.loggedMessagesCounter.add();
        }
        this.lastMessageDateCreated = messageToLog.dateCreated;
    }

    private boolean datesAreInTheSameHour(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        if (date1.getYear() != date2.getYear()) {
            return false;
        }
        if (date1.getMonth() != date2.getMonth()) {
            return false;
        }
        if (date1.getDate() != date2.getDate()) {
            return false;
        }
        return date1.getHours() == date2.getHours();
    }

    private BufferedWriter openNewFile(Date date) throws Exception {
        String hour;
        String day;
        String year = Integer.toString(date.getYear() + 1900);
        String month = Integer.toString(date.getMonth() + 1);
        if (month.length() < 2) {
            month = "0" + month;
        }
        if ((day = Integer.toString(date.getDate())).length() < 2) {
            day = "0" + day;
        }
        if ((hour = Integer.toString(date.getHours())).length() < 2) {
            hour = "0" + hour;
        }
        File logFilePath = new File(this.basePath + File.separator + year + File.separator + month + File.separator + day);
        File logFile = new File(logFilePath.getPath() + File.separator + year + month + day + "T" + hour + ".log");
        logFilePath.mkdirs();
        logFile.createNewFile();
        return new BufferedWriter(new OutputStreamWriter((OutputStream)new FileOutputStream(logFile, true), "UTF-8"));
    }

    private String convertMessageToString(MessageToLog messageToLog) {
        StringBuilder sb = new StringBuilder(messageToLog.messageText.length() + messageToLog.source.length() + messageToLog.destination.length() + 40);
        sb.append(this.dateFormatterFullTime.format(messageToLog.dateCreated));
        sb.append(',');
        sb.append(messageToLog.type.value());
        sb.append(',');
        sb.append(messageToLog.sourceCountryID);
        sb.append(",\"");
        sb.append(messageToLog.source);
        sb.append("\",\"");
        sb.append(messageToLog.destination);
        sb.append("\",");
        sb.append(messageToLog.numRecipients);
        sb.append(",\"");
        sb.append(messageToLog.messageText);
        sb.append('\"');
        return sb.toString();
    }

    public void terminating() {
        if (this.writer != null) {
            try {
                this.writer.close();
            }
            catch (IOException e) {
                log.warn((Object)("Unable to close file: " + e.getMessage()));
            }
        }
        if (this.lastMessageDateCreated != null) {
            try {
                this.writeMessageStatsToDB();
            }
            catch (Exception e) {
                log.warn((Object)("Unable to save remaining message stats to DB: " + e.getMessage()));
            }
        }
    }

    private synchronized void writeMessageStatsToDB() throws Exception {
        if (this.lastMessageDateCreated == null) {
            return;
        }
        Message messageEJB = null;
        try {
            messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
        }
        catch (Exception e) {
            throw new Exception("Unable to create EJB to log message stats for " + this.dateFormatterDateOnly.format(this.lastMessageDateCreated) + " to the DB. Exception: " + e.getMessage());
        }
        Iterator<MessageStats> i = this.messageStatsPerCountry.values().iterator();
        while (i.hasNext()) {
            MessageStats countryStat = i.next();
            try {
                messageEJB.logMessageStats(this.lastMessageDateCreated, countryStat.countryID, countryStat.numPrivate, countryStat.numGroupChatSent, countryStat.numGroupChatReceived, countryStat.numChatRoomSent, countryStat.numChatRoomReceived, countryStat.numSMS, countryStat.numMSNSent, countryStat.numMSNReceived, countryStat.numYahooSent, countryStat.numYahooReceived, countryStat.numAIMSent, countryStat.numAIMReceived, countryStat.numGTalkSent, countryStat.numGTalkReceived, countryStat.numFacebookSent, countryStat.numFacebookReceived);
                i.remove();
            }
            catch (RemoteException e) {
                throw new Exception("Unable to log message stats for " + this.dateFormatterDateOnly.format(this.lastMessageDateCreated) + " for the country ID " + countryStat.countryID + " to the DB. Exception: " + RMIExceptionHelper.getRootMessage(e));
            }
            catch (Exception e) {
                throw new Exception("Unable to log message stats for " + this.dateFormatterDateOnly.format(this.lastMessageDateCreated) + " for the country ID " + countryStat.countryID + " to the DB. Exception: " + e.getMessage());
            }
        }
    }

    private class MessageStats {
        public int countryID;
        public int numPrivate = 0;
        public int numGroupChatSent = 0;
        public int numGroupChatReceived = 0;
        public int numChatRoomSent = 0;
        public int numChatRoomReceived = 0;
        public int numSMS = 0;
        public int numMSNSent = 0;
        public int numMSNReceived = 0;
        public int numYahooSent = 0;
        public int numYahooReceived = 0;
        public int numAIMSent = 0;
        public int numAIMReceived = 0;
        public int numGTalkSent = 0;
        public int numGTalkReceived = 0;
        public int numFacebookSent = 0;
        public int numFacebookReceived = 0;

        public MessageStats(int countryID) {
            this.countryID = countryID;
        }
    }

    private class StatsSaver
    extends TimerTask {
        private StatsSaver() {
        }

        public void run() {
            try {
                MessageLoggerI.this.writeMessageStatsToDB();
            }
            catch (Exception e) {
                log.warn((Object)("Unable to save stats to DB: " + e.getMessage()));
            }
        }
    }
}

