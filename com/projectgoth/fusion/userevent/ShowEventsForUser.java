/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Communicator
 *  Ice.ObjectPrx
 *  Ice.Util
 *  org.apache.log4j.xml.DOMConfigurator
 */
package com.projectgoth.fusion.userevent;

import Ice.Communicator;
import Ice.ObjectPrx;
import Ice.Util;
import com.projectgoth.fusion.fdl.enums.ClientType;
import com.projectgoth.fusion.slice.EventSystemPrx;
import com.projectgoth.fusion.slice.EventSystemPrxHelper;
import com.projectgoth.fusion.slice.GroupUserPostUserEventIce;
import com.projectgoth.fusion.slice.ShortTextStatusUserEventIce;
import com.projectgoth.fusion.slice.UserEventIce;
import com.projectgoth.fusion.userevent.EventTextTranslator;
import org.apache.log4j.xml.DOMConfigurator;

public class ShowEventsForUser {
    private static Communicator iceCommunicator;
    private static EventSystemPrx eventSystemPrx;
    private static EventTextTranslator translator;

    public static EventSystemPrx getEventSystem(String hostname) throws Exception {
        if (eventSystemPrx == null) {
            if (iceCommunicator == null) {
                throw new Exception("Ice communicator has not been initialized");
            }
            ObjectPrx base = iceCommunicator.stringToProxy("EventSystem: tcp -h " + hostname + " -p 21500 -t 5000");
            eventSystemPrx = EventSystemPrxHelper.checkedCast(base);
            if (eventSystemPrx == null) {
                throw new Exception("Invalid EventSystem proxy");
            }
        }
        return eventSystemPrx;
    }

    public static void printEvent(UserEventIce event, String receivingUsername) {
        if (event instanceof ShortTextStatusUserEventIce) {
            System.out.println((Object)((Object)event) + " generating user [" + event.generatingUsername + "] display picture [" + event.generatingUserDisplayPicture + "] timestamp [" + event.timestamp + "] status [" + ((ShortTextStatusUserEventIce)event).status + " ] text [" + translator.translate(event, ClientType.MIDP2, receivingUsername) + "]");
        } else if (event instanceof GroupUserPostUserEventIce) {
            System.out.println((Object)((Object)event) + " generating user [" + event.generatingUsername + "] display picture [" + event.generatingUserDisplayPicture + "] timestamp [" + event.timestamp + "] status [" + ((GroupUserPostUserEventIce)event).topicText + " ] text [" + translator.translate(event, ClientType.MIDP2, receivingUsername) + "]");
        } else {
            System.out.println((Object)((Object)event) + " generating user [" + event.generatingUsername + "] display picture [" + event.generatingUserDisplayPicture + "] timestamp [" + event.timestamp + "] text [" + translator.translate(event, ClientType.MIDP2, receivingUsername) + "]");
        }
    }

    public static void main(String[] args) throws Exception {
        UserEventIce[] events;
        DOMConfigurator.configureAndWatch((String)"log4j.xml");
        if (args.length < 2) {
            System.err.println("Usage: ShowEventsForUser <event system hostname> <username>");
            System.exit(1);
        }
        String hostname = args[0];
        String username = args[1];
        for (UserEventIce event : events = ShowEventsForUser.getEventSystem(hostname).getUserEventsForUser(username)) {
            ShowEventsForUser.printEvent(event, username);
        }
        iceCommunicator.shutdown();
        System.exit(0);
    }

    static {
        translator = new EventTextTranslator();
        iceCommunicator = Util.initialize((String[])new String[0]);
    }
}

