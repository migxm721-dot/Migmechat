/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Appender
 *  org.apache.log4j.ConsoleAppender
 *  org.apache.log4j.Layout
 *  org.apache.log4j.Level
 *  org.apache.log4j.Logger
 *  org.apache.log4j.PatternLayout
 *  org.apache.log4j.Priority
 */
package com.projectgoth.fusion.maintenance;

import com.projectgoth.fusion.data.ChatRoomData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Message;
import com.projectgoth.fusion.interfaces.MessageHome;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;

public class CreateChatRooms {
    private static final Logger log = Logger.getLogger(CreateChatRooms.class);

    public static void main(String[] args) {
        try {
            CreateChatRooms.mainInner(args);
        }
        catch (Exception e) {
            System.err.println("Exception " + e);
            e.printStackTrace();
        }
    }

    private static void mainInner(String[] args) throws Exception {
        System.out.println("Usage: CreateChatRooms <creatorUsername> <creatorRepuScore> <startIndex> <endIndex> <chatroom prefix> <maxChatRoomSize> <sleepMillis>");
        System.out.println("Note: owner's reputation score will be permanently updated to the supplied value  (owner will not be rewarded)");
        if (args.length != 7) {
            System.err.println("Wrong number of args");
            System.exit(1);
        }
        CreateChatRooms.initializeLog4jForConsoleApp();
        int a = 0;
        String creatorUsername = args[a++];
        int creatorRepuScore = Integer.parseInt(args[a++]);
        int startIndex = Integer.parseInt(args[a++]);
        int endIndex = Integer.parseInt(args[a++]);
        String chatRoomPrefix = args[a++];
        int maxChatRoomSize = Integer.parseInt(args[a++]);
        long sleep = Long.parseLong(args[a++]);
        User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
        Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
        int creatorId = userEJB.getUserID(creatorUsername, null);
        userEJB.updateReputationScore(creatorId, creatorRepuScore, true);
        for (int i = startIndex; i <= endIndex; ++i) {
            String roomName = chatRoomPrefix + i;
            log.info((Object)("Creating chatroom " + roomName));
            ChatRoomData chatRoom = new ChatRoomData();
            chatRoom.creator = creatorUsername;
            chatRoom.name = roomName;
            chatRoom.maximumSize = maxChatRoomSize;
            messageEJB.createChatRoom(chatRoom, null);
            try {
                Thread.sleep(sleep);
                continue;
            }
            catch (Exception e) {
                // empty catch block
            }
        }
    }

    private static void initializeLog4jForConsoleApp() {
        ConsoleAppender console = new ConsoleAppender();
        String PATTERN = "%d [%p|%C{1}] %m%n";
        console.setLayout((Layout)new PatternLayout(PATTERN));
        console.setThreshold((Priority)Level.DEBUG);
        console.activateOptions();
        Logger.getRootLogger().addAppender((Appender)console);
        Logger.getRootLogger().setLevel(Level.INFO);
    }
}

