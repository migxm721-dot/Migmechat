/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.maintenance;

import com.projectgoth.fusion.common.DateTimeUtils;
import com.projectgoth.fusion.messagelogger.MessageToLog;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageLogHistogram {
    private static DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static long BRACKET_SIZE = DateTimeUtils.FIVE_MINUTES_IN_MS;
    private Long[] brackets = new Long[(int)DateTimeUtils.TWENTY_FOUR_HOURS_IN_MS / (int)BRACKET_SIZE];
    private ConcurrentMap<Long, AtomicInteger> allMessages = new ConcurrentHashMap<Long, AtomicInteger>();
    private ConcurrentMap<Long, AtomicInteger> privateChatMessages = new ConcurrentHashMap<Long, AtomicInteger>();
    private ConcurrentMap<Long, AtomicInteger> groupChatMessages = new ConcurrentHashMap<Long, AtomicInteger>();
    private ConcurrentMap<Long, AtomicInteger> chatroomMessages = new ConcurrentHashMap<Long, AtomicInteger>();
    private ConcurrentMap<Long, AtomicInteger> smsMessages = new ConcurrentHashMap<Long, AtomicInteger>();
    private ConcurrentMap<Long, AtomicInteger> msnSentMessages = new ConcurrentHashMap<Long, AtomicInteger>();
    private ConcurrentMap<Long, AtomicInteger> msnReceivedMessages = new ConcurrentHashMap<Long, AtomicInteger>();
    private ConcurrentMap<Long, AtomicInteger> yahooSentMessages = new ConcurrentHashMap<Long, AtomicInteger>();
    private ConcurrentMap<Long, AtomicInteger> yahooReceivedMessages = new ConcurrentHashMap<Long, AtomicInteger>();
    private ConcurrentMap<Long, AtomicInteger> aimSentMessages = new ConcurrentHashMap<Long, AtomicInteger>();
    private ConcurrentMap<Long, AtomicInteger> aimReceivedMessages = new ConcurrentHashMap<Long, AtomicInteger>();
    private ConcurrentMap<Long, AtomicInteger> gtalkSentMessages = new ConcurrentHashMap<Long, AtomicInteger>();
    private ConcurrentMap<Long, AtomicInteger> gtalkReceivedMessages = new ConcurrentHashMap<Long, AtomicInteger>();
    private Map<Integer, ConcurrentMap<Long, AtomicInteger>> mapOfMaps = new HashMap<Integer, ConcurrentMap<Long, AtomicInteger>>();

    public MessageLogHistogram() {
        this.initializeMaps();
    }

    private void initializeMaps() {
        Date midnightToday = DateTimeUtils.midnightToday();
        long timestamp = midnightToday.getTime();
        for (long i = 0L; i < DateTimeUtils.TWENTY_FOUR_HOURS_IN_MS / BRACKET_SIZE; ++i) {
            this.allMessages.put(timestamp += BRACKET_SIZE, new AtomicInteger(0));
            this.privateChatMessages.put(timestamp, new AtomicInteger(0));
            this.groupChatMessages.put(timestamp, new AtomicInteger(0));
            this.chatroomMessages.put(timestamp, new AtomicInteger(0));
            this.smsMessages.put(timestamp, new AtomicInteger(0));
            this.msnSentMessages.put(timestamp, new AtomicInteger(0));
            this.msnReceivedMessages.put(timestamp, new AtomicInteger(0));
            this.yahooSentMessages.put(timestamp, new AtomicInteger(0));
            this.yahooReceivedMessages.put(timestamp, new AtomicInteger(0));
            this.aimSentMessages.put(timestamp, new AtomicInteger(0));
            this.aimReceivedMessages.put(timestamp, new AtomicInteger(0));
            this.gtalkSentMessages.put(timestamp, new AtomicInteger(0));
            this.gtalkReceivedMessages.put(timestamp, new AtomicInteger(0));
            this.brackets[(int)i] = timestamp;
        }
        System.out.println("created " + this.brackets.length + " backets");
        this.mapOfMaps.put(MessageToLog.TypeEnum.PRIVATE.value(), this.privateChatMessages);
        this.mapOfMaps.put(MessageToLog.TypeEnum.GROUPCHAT.value(), this.groupChatMessages);
        this.mapOfMaps.put(MessageToLog.TypeEnum.CHATROOM.value(), this.chatroomMessages);
        this.mapOfMaps.put(MessageToLog.TypeEnum.SMS.value(), this.smsMessages);
        this.mapOfMaps.put(MessageToLog.TypeEnum.MSN_SENT.value(), this.msnSentMessages);
        this.mapOfMaps.put(MessageToLog.TypeEnum.MSN_RECEIVED.value(), this.msnReceivedMessages);
        this.mapOfMaps.put(MessageToLog.TypeEnum.YAHOO_SENT.value(), this.yahooSentMessages);
        this.mapOfMaps.put(MessageToLog.TypeEnum.YAHOO_RECEIVED.value(), this.yahooReceivedMessages);
        this.mapOfMaps.put(MessageToLog.TypeEnum.AIM_SENT.value(), this.aimSentMessages);
        this.mapOfMaps.put(MessageToLog.TypeEnum.AIM_RECEIVED.value(), this.aimReceivedMessages);
        this.mapOfMaps.put(MessageToLog.TypeEnum.GTALK_SENT.value(), this.gtalkSentMessages);
        this.mapOfMaps.put(MessageToLog.TypeEnum.GTALK_RECEIVED.value(), this.gtalkReceivedMessages);
        System.out.println("mapOfMaps has " + this.mapOfMaps.size() + " keys");
    }

    private int getSafeIndex(int insertionPoint) {
        if (insertionPoint == 0) {
            insertionPoint = -2;
        }
        if (insertionPoint > 0) {
            insertionPoint = -1 * (insertionPoint + 2);
        }
        return -1 * insertionPoint - 1;
    }

    public Date getBracketDate(long timestamp) {
        int point = Arrays.binarySearch((Object[])this.brackets, (Object)timestamp);
        return new Date(this.brackets[this.getSafeIndex(point)]);
    }

    public void parseFile(String filename) throws Exception {
        Pattern pattern = Pattern.compile("(\\d\\d\\d\\d\\-\\d\\d-\\d\\d \\d\\d\\:\\d\\d:\\d\\d),(\\d{1,2}),");
        Matcher matcher = pattern.matcher("");
        AtomicInteger counter = null;
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line = null;
        while ((line = reader.readLine()) != null) {
            matcher.reset(line);
            if (!matcher.find()) {
                System.out.println("no group match found for line [" + line + "]");
                continue;
            }
            String dateString = matcher.group(1);
            String typeString = matcher.group(2);
            Date date = DATE_FORMATTER.parse(dateString);
            int point = Arrays.binarySearch((Object[])this.brackets, (Object)date.getTime());
            Long key = this.brackets[this.getSafeIndex(point)];
            counter = (AtomicInteger)this.allMessages.get(key);
            counter.incrementAndGet();
            int type = Integer.parseInt(typeString);
            counter = (AtomicInteger)this.mapOfMaps.get(type).get(key);
            counter.incrementAndGet();
        }
        reader.close();
    }

    public void showHistogram() {
        long totalMessages = 0L;
        for (Long key : new TreeSet(this.allMessages.keySet())) {
            System.out.println(DATE_FORMATTER.format(new Date(key)) + ", " + this.allMessages.get(key));
            totalMessages += (long)((AtomicInteger)this.allMessages.get(key)).intValue();
        }
        System.out.println("total messages = " + totalMessages);
        for (MessageToLog.TypeEnum type : MessageToLog.TypeEnum.values()) {
            totalMessages = 0L;
            System.out.println(type.toString());
            ConcurrentMap<Long, AtomicInteger> map = this.mapOfMaps.get(type.value());
            for (Long key : new TreeSet(map.keySet())) {
                System.out.println(DATE_FORMATTER.format(new Date(key)) + ", " + map.get(key));
                totalMessages += (long)((AtomicInteger)map.get(key)).intValue();
            }
            System.out.println("total messages = " + totalMessages);
        }
    }

    public static void main(String[] args) throws Exception {
        MessageLogHistogram histo = new MessageLogHistogram();
        histo.parseFile(args[0]);
        histo.showHistogram();
    }
}

