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

import com.projectgoth.fusion.data.ContactData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Contact;
import com.projectgoth.fusion.interfaces.ContactHome;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import java.rmi.RemoteException;
import java.util.Random;
import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;

public class FriendUsers {
    private static final Logger log = Logger.getLogger(FriendUsers.class);
    private static final Random ran = new Random();

    public static void main(String[] args) {
        try {
            FriendUsers.mainInner(args);
        }
        catch (Exception e) {
            System.err.println("Exception " + e);
            e.printStackTrace();
        }
    }

    private static void mainInner(String[] args) throws Exception {
        System.out.println("Usage: FriendUsers <startIndex> <endIndex> <username prefix> <friendsPerUser> <sleepMillis>");
        if (args.length != 5) {
            System.err.println("Wrong number of args");
            System.exit(1);
        }
        FriendUsers.initializeLog4jForConsoleApp();
        int a = 0;
        int startIndex = Integer.parseInt(args[a++]);
        int endIndex = Integer.parseInt(args[a++]);
        String usernamePrefix = args[a++];
        int friendsPerUser = Integer.parseInt(args[a++]);
        long sleep = Long.parseLong(args[a++]);
        Contact contactEJB = (Contact)EJBHomeCache.getObject("ejb/Contact", ContactHome.class);
        User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
        for (int i = startIndex; i <= endIndex; ++i) {
            String usernameA = usernamePrefix + i;
            log.info((Object)("Making friends for " + usernameA));
            for (int j = 0; j < friendsPerUser; ++j) {
                int friendIndex = startIndex + ran.nextInt(endIndex - startIndex);
                String usernameB = usernamePrefix + friendIndex;
                log.info((Object)("Friending " + usernameA + " and " + usernameB));
                int userA_id = userEJB.getUserID(usernameA, null);
                int userB_id = userEJB.getUserID(usernameB, null);
                try {
                    ContactData friendContact = new ContactData();
                    friendContact.username = usernameA;
                    friendContact.fusionUsername = usernameB;
                    friendContact.displayOnPhone = true;
                    contactEJB.addPendingFusionContact(userA_id, friendContact);
                    ContactData acceptContact = new ContactData();
                    acceptContact.username = usernameB;
                    acceptContact.fusionUsername = usernameA;
                    acceptContact.displayOnPhone = true;
                    contactEJB.acceptContactRequest(userB_id, acceptContact, true);
                }
                catch (RemoteException rem) {
                    log.error((Object)("Failed to friend users: " + rem));
                }
                try {
                    Thread.sleep(sleep);
                    continue;
                }
                catch (Exception e) {
                    // empty catch block
                }
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

