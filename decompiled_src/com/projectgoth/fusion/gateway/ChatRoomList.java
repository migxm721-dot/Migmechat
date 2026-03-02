/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.gateway;

import com.projectgoth.fusion.app.dao.DAOException;
import com.projectgoth.fusion.app.dao.DAOFactory;
import com.projectgoth.fusion.app.dao.data.UserObject;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.ChatRoomData;
import com.projectgoth.fusion.data.RecommendationItem;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Message;
import com.projectgoth.fusion.interfaces.MessageHome;
import com.projectgoth.fusion.recommendation.delivery.Enums;
import com.projectgoth.fusion.recommendation.delivery.RecommendationDeliveryUtils;
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.RegistryPrx;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ChatRoomList {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ChatRoomList.class));
    private static String[] defaultChatRoomNames = new String[0];
    private static String[] defaultGameChatRoomNames = new String[0];
    private static String[] defaultNewbiesChatRoomNames = new String[0];
    private static String[] chatRoomsToHide = new String[0];
    private static double hideProbability = 0.5;
    private static int categoryPageSize = 5;
    private static int initialPageSize = 5;
    private static int pageSize = 5;
    private static int maxChatRoomsReturned = 100;
    private static Map<Integer, CacheData> cache = new HashMap<Integer, CacheData>();
    private static int cacheInterval = 10;
    private ArrayListBuffer newbiesChatRooms = new ArrayListBuffer();
    private ArrayListBuffer favouriteChatRooms = new ArrayListBuffer();
    private ArrayListBuffer recentChatRooms = new ArrayListBuffer();
    private ArrayListBuffer gameChatRooms = new ArrayListBuffer();
    private ArrayListBuffer chatRooms = new ArrayListBuffer();
    private boolean chatRoomsLoaded;
    private int countryID;
    private static Semaphore semaphore;
    private static long chatroomListLastUpdated;
    private static Map<Integer, List<String>> chatroomNamesPerCategory;
    private static long CHATROOM_LIST_CACHE_TIME;
    private Map<Integer, ArrayListBuffer> nonStandardChatroomsPerUser = new HashMap<Integer, ArrayListBuffer>();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void loadChatroomInChatroomCategories() {
        if (chatroomListLastUpdated == 0L) {
            semaphore.acquireUninterruptibly();
        } else if (!semaphore.tryAcquire()) {
            return;
        }
        try {
            if (SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_CHATROOM_DAO)) {
                chatroomNamesPerCategory = DAOFactory.getInstance().getChatRoomDAO().getChatroomNamesPerCategory(true);
            } else {
                Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
                chatroomNamesPerCategory = messageEJB.getChatroomNamesPerCategory(true);
            }
            chatroomListLastUpdated = System.currentTimeMillis();
        }
        catch (Exception e) {
            log.warn((Object)"Unable to load chatroom categories", (Throwable)e);
        }
        finally {
            semaphore.release();
        }
    }

    public static List<String> getChatroomNamesInCategory(int chatroomCategoryId) throws Exception {
        if (System.currentTimeMillis() - chatroomListLastUpdated > CHATROOM_LIST_CACHE_TIME) {
            ChatRoomList.loadChatroomInChatroomCategories();
        }
        if (!chatroomNamesPerCategory.containsKey(chatroomCategoryId)) {
            log.error((Object)("Unable to retrieve chatroom name list for category [" + chatroomCategoryId + "]"));
            throw new Exception("Unable to retrieve chatrooms.");
        }
        return chatroomNamesPerCategory.get(chatroomCategoryId);
    }

    public Page getPopularChatRooms(RegistryPrx registryPrx, int countryID, boolean refresh) throws CreateException, RemoteException {
        if (refresh || this.chatRooms.itemsLeft() == 0) {
            if (SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_CHATROOM_DAO)) {
                try {
                    this.chatRooms.wrap(DAOFactory.getInstance().getChatRoomDAO().getChatRooms(countryID, null));
                }
                catch (DAOException e) {
                    log.error((Object)String.format("DAO: Failed to get Chatrooms from country:%s", countryID), (Throwable)e);
                    throw new RemoteException(e.getMessage());
                }
            } else {
                Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
                this.chatRooms.wrap(messageEJB.getChatRooms(countryID, null));
            }
            this.chatRooms.removeAll(this.favouriteChatRooms);
            this.chatRooms.removeAll(this.recentChatRooms);
            this.filterHiddenChatRooms(this.chatRooms);
        }
        return this.fillChatRoomSize(registryPrx, this.chatRooms.nextPage(categoryPageSize));
    }

    public Page getFavouriteChatRooms(RegistryPrx registryPrx, String username, boolean refresh) throws CreateException, RemoteException {
        if (refresh || this.favouriteChatRooms.itemsLeft() == 0) {
            if (SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_CHATROOM_DAO)) {
                try {
                    this.favouriteChatRooms.wrap(new UserObject(username).getFavouriteChatRooms());
                }
                catch (DAOException e) {
                    log.error((Object)String.format("DAO: Failed to get FavouriteChatRooms from user:%s", username), (Throwable)e);
                    throw new RemoteException(e.getMessage());
                }
            } else {
                Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
                this.favouriteChatRooms.wrap(messageEJB.getFavouriteChatRooms(username));
            }
        }
        return this.fillChatRoomSize(registryPrx, this.favouriteChatRooms.nextPage(categoryPageSize));
    }

    public Page getRecentChatRooms(RegistryPrx registryPrx, String username, boolean refresh) throws CreateException, RemoteException {
        if (refresh || this.recentChatRooms.size() == 0) {
            if (SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_CHATROOM_DAO)) {
                try {
                    this.recentChatRooms.wrap(new UserObject(username).getRecentChatRooms());
                }
                catch (DAOException e) {
                    log.error((Object)String.format("DAO: Failed to get RecentChatRooms from user:%s", username), (Throwable)e);
                    throw new RemoteException(e.getMessage());
                }
            } else {
                Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
                this.recentChatRooms.wrap(messageEJB.getRecentChatRooms(username));
            }
        }
        return this.fillChatRoomSize(registryPrx, this.recentChatRooms.nextPage(categoryPageSize));
    }

    public Page getRecommendedChatRooms(RegistryPrx registryPrx, int userId, boolean refresh) throws CreateException, RemoteException {
        if (refresh || this.chatRooms.itemsLeft() == 0) {
            ArrayList<ChatRoomData> recommendedChatRooms = new ArrayList<ChatRoomData>();
            int maxRecommendedChatRooms = SystemProperty.getInt(SystemPropertyEntities.Chatroom.MAX_RECOMMENDED_CHATROOMS);
            for (RecommendationItem item : RecommendationDeliveryUtils.getRecommendation(Enums.RecommendationTypeEnum.CHATROOMS, Enums.RecommendationTargetEnum.INDIVIDUAL, userId, null, maxRecommendedChatRooms, 0, false).getRecommendations()) {
                ChatRoomData room = (ChatRoomData)Enums.RecommendationTypeEnum.CHATROOMS.getEvaluator().evaluate(item);
                if (null == room) continue;
                recommendedChatRooms.add(room);
            }
            this.chatRooms.wrap(recommendedChatRooms);
            this.filterHiddenChatRooms(this.chatRooms);
        }
        return this.fillChatRoomSize(registryPrx, this.chatRooms.nextPage(categoryPageSize));
    }

    public Page getChatrooms(RegistryPrx registryPrx, boolean refresh, Integer categoryId) throws CreateException, RemoteException, Exception {
        if (!this.nonStandardChatroomsPerUser.containsKey(categoryId)) {
            this.nonStandardChatroomsPerUser.put(categoryId, new ArrayListBuffer());
        }
        if (refresh || this.nonStandardChatroomsPerUser.get(categoryId).size() == 0) {
            List<String> chatroomNames = ChatRoomList.getChatroomNamesInCategory(categoryId);
            ArrayListBuffer chatrooms = this.nonStandardChatroomsPerUser.get(categoryId);
            chatrooms.wrap(this.getChatRooms(registryPrx, chatroomNames.toArray(new String[chatroomNames.size()])));
            Collections.shuffle(chatrooms);
            this.nonStandardChatroomsPerUser.put(categoryId, chatrooms);
        }
        return this.fillChatRoomSize(registryPrx, this.nonStandardChatroomsPerUser.get(categoryId).nextPage(categoryPageSize));
    }

    public Page getGameChatRooms(RegistryPrx registryPrx, boolean refresh) throws CreateException, RemoteException {
        if (refresh || this.gameChatRooms.size() == 0) {
            this.gameChatRooms.wrap(this.getChatRooms(registryPrx, defaultGameChatRoomNames));
            this.filterHiddenChatRooms(this.chatRooms);
            Collections.shuffle(this.gameChatRooms);
            return this.gameChatRooms.nextPage(categoryPageSize);
        }
        return this.fillChatRoomSize(registryPrx, this.gameChatRooms.nextPage(categoryPageSize));
    }

    public Page getNewbiesRooms(RegistryPrx registryPrx, boolean refresh) throws CreateException, RemoteException {
        log.debug((Object)("getNewbiesRooms() REFRESH [" + refresh + "] chatroomsize [" + this.newbiesChatRooms.size() + "]"));
        if (refresh || this.newbiesChatRooms.size() == 0) {
            this.newbiesChatRooms.wrap(this.getChatRooms(registryPrx, defaultNewbiesChatRoomNames));
            Collections.shuffle(this.newbiesChatRooms);
            return this.newbiesChatRooms.nextPage(categoryPageSize);
        }
        return this.fillChatRoomSize(registryPrx, this.newbiesChatRooms.nextPage(categoryPageSize));
    }

    private void filterHiddenChatRooms(ArrayListBuffer chatrooms) {
        if (chatRoomsToHide.length == 0 || hideProbability == 0.0) {
            return;
        }
        Iterator li = chatrooms.iterator();
        block0: while (li.hasNext()) {
            ChatRoomData chatroomdata = (ChatRoomData)li.next();
            String chatroomname = chatroomdata.name.toLowerCase().replace(" ", "");
            double p = Math.random();
            for (int j = 0; j < chatRoomsToHide.length; ++j) {
                if (chatroomname.indexOf(chatRoomsToHide[j].toLowerCase()) <= 0) continue;
                if (!(p <= hideProbability)) continue block0;
                li.remove();
                continue block0;
            }
        }
    }

    private Page fillChatRoomSize(RegistryPrx registryPrx, Page page) {
        this.fillChatRoomSize(registryPrx, page.getPageList());
        return page;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private List<ChatRoomData> getChatRooms(int countryID, String searchString) throws CreateException, RemoteException {
        if (searchString == null && cacheInterval > 0) {
            CacheData cacheData;
            boolean refreshCache = false;
            Map<Integer, CacheData> map = cache;
            synchronized (map) {
                cacheData = cache.get(countryID);
                if (cacheData == null) {
                    refreshCache = true;
                } else if (cacheData.expiry < System.currentTimeMillis()) {
                    refreshCache = true;
                    cacheData.expiry += (long)cacheInterval;
                }
            }
            if (refreshCache) {
                cacheData = new CacheData();
                if (SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_CHATROOM_DAO)) {
                    try {
                        cacheData.chatRooms = DAOFactory.getInstance().getChatRoomDAO().getChatRooms(countryID, searchString);
                    }
                    catch (DAOException e) {
                        log.error((Object)String.format("Failed to get ChatRooms for country:%s, search:%s", countryID, searchString), (Throwable)e);
                        throw new RemoteException(e.getMessage());
                    }
                } else {
                    Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
                    cacheData.chatRooms = messageEJB.getChatRooms(countryID, searchString);
                }
                cacheData.expiry = System.currentTimeMillis() + (long)cacheInterval;
                map = cache;
                synchronized (map) {
                    cache.put(countryID, cacheData);
                }
            }
            return cacheData.chatRooms;
        }
        if (SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_CHATROOM_DAO)) {
            try {
                return DAOFactory.getInstance().getChatRoomDAO().getChatRooms(countryID, searchString);
            }
            catch (DAOException e) {
                log.error((Object)String.format("Failed to get ChatRooms for country:%s, search:%s", countryID, searchString), (Throwable)e);
                throw new RemoteException(e.getMessage());
            }
        }
        Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
        return messageEJB.getChatRooms(countryID, searchString);
    }

    private List<ChatRoomData> getChatRooms(RegistryPrx registryPrx, String[] chatRoomNames) throws RemoteException, CreateException {
        ChatRoomPrx[] chatRoomPrxs = new ChatRoomPrx[chatRoomNames.length];
        try {
            chatRoomPrxs = registryPrx.findChatRoomObjects(chatRoomNames);
        }
        catch (Exception e) {
            // empty catch block
        }
        ArrayList<ChatRoomData> chatRoomList = new ArrayList<ChatRoomData>();
        for (int i = 0; i < chatRoomNames.length; ++i) {
            ChatRoomPrx roomPrx = chatRoomPrxs[i];
            if (roomPrx == null) {
                ChatRoomData room = null;
                if (SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_CHATROOM_DAO)) {
                    try {
                        room = DAOFactory.getInstance().getChatRoomDAO().getChatRoom(chatRoomNames[i]);
                    }
                    catch (DAOException e) {
                        throw new RemoteException(e.getMessage());
                    }
                } else {
                    Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
                    room = messageEJB.getChatRoom(chatRoomNames[i]);
                }
                if (room == null) continue;
                chatRoomList.add(room);
                continue;
            }
            chatRoomList.add(new ChatRoomData(roomPrx.getRoomData()));
        }
        return chatRoomList;
    }

    private void fillChatRoomSize(RegistryPrx registryPrx, Collection<ChatRoomData> chatRooms) {
        ChatRoomPrx[] chatRoomPrxs;
        String[] chatRoomNames = new String[chatRooms.size()];
        int i = 0;
        for (ChatRoomData chatRoom : chatRooms) {
            chatRoomNames[i++] = chatRoom.name;
        }
        try {
            chatRoomPrxs = registryPrx.findChatRoomObjects(chatRoomNames);
        }
        catch (Exception e) {
            return;
        }
        i = 0;
        for (ChatRoomData chatRoom : chatRooms) {
            if (chatRoomPrxs[i] != null) {
                chatRoom.size = chatRoomPrxs[i].getNumParticipants();
            }
            ++i;
        }
    }

    public int pages() {
        return (int)Math.ceil((double)(this.chatRoomsLoaded ? this.chatRooms.size() : maxChatRoomsReturned) / (double)pageSize);
    }

    public List<ChatRoomData> getInitialList(RegistryPrx registryPrx, String username, String[] chatRoomNames) throws RemoteException, CreateException {
        int i;
        ArrayList<String> nameList = new ArrayList<String>();
        this.recentChatRooms.clear();
        if (SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_CHATROOM_DAO)) {
            try {
                this.recentChatRooms.addAll(new UserObject(username).getRecentChatRooms());
            }
            catch (DAOException e) {
                throw new RemoteException(e.getMessage());
            }
        } else {
            Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
            this.recentChatRooms.addAll(messageEJB.getRecentChatRooms(username));
        }
        for (i = 0; i < this.recentChatRooms.size() && i < initialPageSize; ++i) {
            nameList.add(((ChatRoomData)this.recentChatRooms.get((int)i)).name.toLowerCase());
        }
        for (i = 0; i < defaultChatRoomNames.length && nameList.size() < initialPageSize; ++i) {
            String name = defaultChatRoomNames[i].toLowerCase();
            if (nameList.contains(name)) continue;
            nameList.add(name);
        }
        return this.getChatRooms(registryPrx, nameList.toArray(new String[nameList.size()]));
    }

    public List<ChatRoomData> getList(RegistryPrx registryPrx, int countryID, String searchString) throws RemoteException, CreateException {
        this.countryID = countryID;
        if (searchString == null) {
            this.chatRooms.clear();
            for (int i = initialPageSize; i < this.recentChatRooms.size(); ++i) {
                this.chatRooms.add(this.recentChatRooms.get(i));
            }
            this.chatRoomsLoaded = false;
        } else {
            this.chatRooms.clear();
            this.chatRooms.addAll(this.getChatRooms(countryID, searchString));
            this.chatRoomsLoaded = true;
        }
        return this.getPage(registryPrx, 1);
    }

    public List<ChatRoomData> getPage(RegistryPrx registryPrx, int page) throws RemoteException, CreateException {
        int end;
        int start = (page - 1) * pageSize;
        if (!(this.chatRoomsLoaded || start <= this.chatRooms.size() && start + pageSize <= this.chatRooms.size())) {
            List<ChatRoomData> rooms = this.getChatRooms(this.countryID, null);
            for (int i = 0; i < rooms.size() && this.chatRooms.size() < maxChatRoomsReturned; ++i) {
                if (this.recentChatRooms.contains(rooms.get(i))) continue;
                this.chatRooms.add(rooms.get(i));
            }
            this.chatRoomsLoaded = true;
        }
        if (start >= (end = Math.min(start + pageSize, this.chatRooms.size()))) {
            return Collections.EMPTY_LIST;
        }
        List<ChatRoomData> pageList = this.chatRooms.subList(start, end);
        this.fillChatRoomSize(registryPrx, pageList);
        return pageList;
    }

    static {
        try {
            defaultChatRoomNames = SystemProperty.getArray("DefaultChatRooms", defaultChatRoomNames);
            defaultGameChatRoomNames = SystemProperty.getArray("DefaultGameChatRooms", defaultGameChatRoomNames);
            defaultNewbiesChatRoomNames = SystemProperty.getArray("DefaultNewbiesChatRooms", defaultNewbiesChatRoomNames);
            initialPageSize = SystemProperty.getInt("ChatRoomInitialPageSize", initialPageSize);
            pageSize = SystemProperty.getInt("ChatRoomPageSize", pageSize);
            maxChatRoomsReturned = SystemProperty.getInt("MaxChatRoomsReturned", maxChatRoomsReturned);
            cacheInterval = SystemProperty.getInt("ChatRoomCacheInterval", cacheInterval) * 1000;
            categoryPageSize = SystemProperty.getInt("CategoryPageSize", categoryPageSize);
            chatRoomsToHide = SystemProperty.getArray("ChatRoomsToHide", chatRoomsToHide);
            hideProbability = SystemProperty.getDouble("ChatroomHideProbability", hideProbability);
        }
        catch (Exception e) {
            log.warn((Object)"Failed to load chat room properties. Using default values");
        }
        semaphore = new Semaphore(1);
        chatroomNamesPerCategory = new ConcurrentHashMap<Integer, List<String>>();
        CHATROOM_LIST_CACHE_TIME = SystemProperty.getLong(SystemPropertyEntities.Chatroom.CHATROOM_LIST_CACHE_TIME_IN_MILLIS);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class Page {
        private List<ChatRoomData> fullList;
        private List<ChatRoomData> pageList;
        private int itemsLeft;

        public List<ChatRoomData> getFullList() {
            return this.fullList;
        }

        public List<ChatRoomData> getPageList() {
            return this.pageList;
        }

        public int itemsLeft() {
            return this.itemsLeft;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class ArrayListBuffer
    extends ArrayList<ChatRoomData> {
        private int index;

        private ArrayListBuffer() {
        }

        public void wrap(Collection<ChatRoomData> chatRoomList) {
            this.clear();
            this.addAll(chatRoomList);
            this.index = 0;
        }

        public int itemsLeft() {
            return Math.max(this.size() - this.index, 0);
        }

        public Page nextPage(int items) {
            int toIndex;
            int fromIndex = Math.min(this.index, this.size());
            this.index = toIndex = Math.min(fromIndex + items, this.size());
            Page page = new Page();
            page.fullList = this;
            page.pageList = fromIndex == toIndex ? Collections.EMPTY_LIST : this.subList(fromIndex, toIndex);
            page.itemsLeft = this.itemsLeft();
            return page;
        }
    }

    private static class CacheData {
        public long expiry;
        public List<ChatRoomData> chatRooms;

        private CacheData() {
        }
    }
}

