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

import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.data.UserProfileData;
import com.projectgoth.fusion.data.UserRegistrationContextData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import com.projectgoth.fusion.restapi.enums.RegistrationType;
import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;

public class RegisterAccounts {
    public static final String MOBILE_PATH = "mobile";
    public static final String EMAIL_PATH1 = "email1";
    public static final String EMAIL_PATH2 = "email2";

    public static void main(String[] args) {
        try {
            RegisterAccounts.mainInner(args);
        }
        catch (Exception e) {
            System.err.println("Exception " + e);
            e.printStackTrace();
        }
    }

    private static void mainInner(String[] args) throws Exception {
        RegistrationType type;
        System.out.println("Usage: RegisterAccounts <path> <startIndex> <endIndex> <username prefix> <password> <starting mobile number> <ip address> <campaignName> <verified=true/false> <sleepBetweenUsersMillis>");
        System.out.println("where path=mobile/email1/email2");
        if (args.length != 10) {
            System.err.println("Wrong number of args");
            System.exit(1);
        }
        RegisterAccounts.initializeLog4jForConsoleApp();
        int a = 0;
        String path = args[a++].toLowerCase();
        int startIndex = Integer.parseInt(args[a++]);
        int endIndex = Integer.parseInt(args[a++]);
        String usernamePrefix = args[a++];
        String password = args[a++];
        Long startingMobileNumber = Long.parseLong(args[a++]);
        String regIP = args[a++];
        String campaignName = args[a++];
        boolean verified = Boolean.parseBoolean(args[a++].toLowerCase());
        long sleep = Long.parseLong(args[a++]);
        if (path.equals(MOBILE_PATH)) {
            type = RegistrationType.MOBILE_REGISTRATION;
        } else if (path.equals(EMAIL_PATH1)) {
            type = RegistrationType.EMAIL_REGISTRATION_PATH1;
        } else if (path.equals(EMAIL_PATH2)) {
            type = RegistrationType.EMAIL_REGISTRATION_PATH2;
        } else {
            type = null;
            System.err.println("Invalid path param");
            System.exit(1);
        }
        User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
        for (int i = startIndex; i <= endIndex; ++i) {
            String username = usernamePrefix + i;
            UserData userData = new UserData();
            userData.type = UserData.TypeEnum.MIG33;
            userData.username = username;
            userData.password = password;
            userData.mobilePhone = "+1" + Long.toString(startingMobileNumber + (long)i);
            userData.displayName = username;
            userData.emailAddress = username + i + "@testing.mig33.com";
            userData.registrationIPAddress = regIP;
            UserProfileData userProfileData = new UserProfileData();
            userProfileData.username = userData.username;
            userProfileData.firstName = userData.username + ".first";
            userProfileData.lastName = userData.username + ".last";
            UserRegistrationContextData regContextData = new UserRegistrationContextData(campaignName, verified, type);
            userEJB.createUser(userData, userProfileData, false, regContextData, new AccountEntrySourceData(RegisterAccounts.class), false, false, false);
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

