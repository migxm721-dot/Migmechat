/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.objectcache;

import com.projectgoth.fusion.cache.MemCacheOrEJB;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MultiIdFinder {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(MultiIdFinder.class));

    public static Set<String> getMultiIds(HashMap<String, List<String>> ipToUsernames, String chatroomName) {
        User userEJB = null;
        try {
            userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
        }
        catch (CreateException e) {
            log.error((Object)("Unable to create User EJB: " + (Object)((Object)e)));
            return null;
        }
        HashSet<String> banList = new HashSet<String>();
        for (Map.Entry<String, List<String>> entry : ipToUsernames.entrySet()) {
            String ip = entry.getKey();
            List<String> usernames = entry.getValue();
            if (usernames == null || usernames.size() < 2) continue;
            MultiIdFinder.addUsersToBanListThatMeetCriteria(banList, MultiIdCriteriaTypeEnum.MIGLEVEL, usernames, ip, chatroomName, userEJB);
            MultiIdFinder.addUsersToBanListThatMeetCriteria(banList, MultiIdCriteriaTypeEnum.NUM_FRIENDS, usernames, ip, chatroomName, userEJB);
            MultiIdFinder.addUsersToBanListThatMeetCriteria(banList, MultiIdCriteriaTypeEnum.NUM_GIFTS, usernames, ip, chatroomName, userEJB);
        }
        return banList;
    }

    private static void addUsersToBanListThatMeetCriteria(Set<String> banList, MultiIdCriteriaTypeEnum type, List<String> usernames, String ip, String chatroomName, User userEJB) {
        HashMap<Integer, List<String>> criteriaValueToUsernames = new HashMap<Integer, List<String>>();
        block11: for (String username : usernames) {
            if (banList.contains(username)) continue;
            int criteriaValue = -1;
            switch (type) {
                case MIGLEVEL: {
                    try {
                        criteriaValue = MemCacheOrEJB.getUserReputationLevel(username);
                    }
                    catch (Exception e) {
                        log.error((Object)("Unable to check mig level: " + e));
                    }
                    break;
                }
                case NUM_FRIENDS: {
                    try {
                        Set bcl = userEJB.loadBroadcastList(username, null);
                        criteriaValue = bcl.size();
                        if (criteriaValue > 0) break;
                        continue block11;
                    }
                    catch (Exception e) {
                        log.error((Object)("Unable to get broadcast list: " + e));
                        break;
                    }
                }
                case NUM_GIFTS: {
                    try {
                        int userId = userEJB.getUserID(username, null);
                        if (userId <= 0 || (criteriaValue = userEJB.getGiftsReceivedCount(userId)) > 0) break;
                        continue block11;
                    }
                    catch (Exception e) {
                        log.error((Object)("Unable to get gifts received count: " + e));
                        break;
                    }
                }
                default: {
                    log.error((Object)("Unknown MultiIdCriteriaTypeEnum [" + (Object)((Object)type) + "]"));
                    return;
                }
            }
            if (criteriaValue == -1) continue;
            if (!criteriaValueToUsernames.containsKey(criteriaValue)) {
                ArrayList<String> list = new ArrayList<String>();
                list.add(username);
                criteriaValueToUsernames.put(criteriaValue, list);
                continue;
            }
            criteriaValueToUsernames.get(criteriaValue).add(username);
        }
        Set<String> criteriaBanList = MultiIdFinder.getUsersWithIdenticalCriteria(criteriaValueToUsernames);
        for (String username : criteriaBanList) {
            log.info((Object)("Adding User [" + username + "] with IP [" + ip + "] to Chatroom [" + chatroomName + "] ban list for containing the same IP and [" + (Object)((Object)type) + "] as another chatroom user"));
        }
        banList.addAll(criteriaBanList);
    }

    private static Set<String> getUsersWithIdenticalCriteria(HashMap<Integer, List<String>> map) {
        HashSet<String> usernames = new HashSet<String>();
        Iterator<Map.Entry<Integer, List<String>>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            List<String> list = iter.next().getValue();
            if (list.size() <= 1) continue;
            for (String item : list) {
                usernames.add(item);
            }
        }
        return usernames;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum MultiIdCriteriaTypeEnum {
        MIGLEVEL(1),
        NUM_FRIENDS(2),
        NUM_GIFTS(3);

        private int value;

        private MultiIdCriteriaTypeEnum(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static MultiIdCriteriaTypeEnum fromValue(int value) {
            for (MultiIdCriteriaTypeEnum e : MultiIdCriteriaTypeEnum.values()) {
                if (e.value() != value) continue;
                return e;
            }
            return null;
        }
    }
}

