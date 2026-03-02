/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Communicator
 *  Ice.Current
 *  Ice.ObjectPrx
 *  net.sf.ehcache.CacheManager
 *  net.sf.ehcache.Ehcache
 *  net.sf.ehcache.Element
 *  net.sf.ehcache.constructs.blocking.CacheEntryFactory
 *  net.sf.ehcache.constructs.blocking.SelfPopulatingCache
 *  org.apache.log4j.Logger
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Required
 *  org.springframework.util.StringUtils
 */
package com.projectgoth.fusion.userevent.system;

import Ice.Communicator;
import Ice.Current;
import Ice.ObjectPrx;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.RequestAndRateLongCounter;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.dao.BroadcastListDAO;
import com.projectgoth.fusion.dao.DisplayPictureAndStatusMessageDAO;
import com.projectgoth.fusion.dao.GroupAnnouncementDAO;
import com.projectgoth.fusion.dao.GroupDAO;
import com.projectgoth.fusion.dao.GroupMembershipDAO;
import com.projectgoth.fusion.dao.UserPostDAO;
import com.projectgoth.fusion.dao.UserProfileStatusDAO;
import com.projectgoth.fusion.data.GroupAnnouncementData;
import com.projectgoth.fusion.data.GroupData;
import com.projectgoth.fusion.data.UserPostData;
import com.projectgoth.fusion.data.UserProfileData;
import com.projectgoth.fusion.slice.AddingFriendUserEventIce;
import com.projectgoth.fusion.slice.ConnectionPrx;
import com.projectgoth.fusion.slice.CreatedChatroomUserEventIce;
import com.projectgoth.fusion.slice.EventPrivacySettingIce;
import com.projectgoth.fusion.slice.EventStorePrx;
import com.projectgoth.fusion.slice.EventStorePrxHelper;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GenericApplicationUserEventIce;
import com.projectgoth.fusion.slice.GiftShowerUserEventIce;
import com.projectgoth.fusion.slice.GroupAnnouncementUserEventIce;
import com.projectgoth.fusion.slice.GroupDonationUserEventIce;
import com.projectgoth.fusion.slice.GroupJoinedUserEventIce;
import com.projectgoth.fusion.slice.GroupUserEventIce;
import com.projectgoth.fusion.slice.GroupUserPostUserEventIce;
import com.projectgoth.fusion.slice.PhotoUploadedUserEventIce;
import com.projectgoth.fusion.slice.ProfileUpdatedUserEventIce;
import com.projectgoth.fusion.slice.PurchasedVirtualGoodsUserEventIce;
import com.projectgoth.fusion.slice.RegistryPrx;
import com.projectgoth.fusion.slice.ShortTextStatusUserEventIce;
import com.projectgoth.fusion.slice.UserEventIce;
import com.projectgoth.fusion.slice.UserWallPostUserEventIce;
import com.projectgoth.fusion.slice.VirtualGiftUserEventIce;
import com.projectgoth.fusion.slice._EventSystemDisp;
import com.projectgoth.fusion.userevent.domain.EventPrivacySetting;
import com.projectgoth.fusion.userevent.domain.UserEventType;
import com.projectgoth.fusion.userevent.system.DistributionTask;
import com.projectgoth.fusion.userevent.system.GroupAnnouncementCacheEntryFactory;
import com.projectgoth.fusion.userevent.system.GroupCacheEntryFactory;
import com.projectgoth.fusion.userevent.system.GroupTopicForUserPostCacheEntryFactory;
import com.projectgoth.fusion.userevent.system.StoreGeneratorTask;
import com.projectgoth.fusion.userevent.system.StoreTask;
import com.projectgoth.fusion.userevent.system.StreamTask;
import com.projectgoth.fusion.userevent.system.domain.UsernameAndUserEvent;
import com.projectgoth.fusion.userevent.system.domain.UsernameAndUserEvents;
import com.projectgoth.fusion.userevent.system.loadbalancing.ConsistentHash;
import com.projectgoth.fusion.userevent.system.loadbalancing.FNVHashFunction;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class EventSystemI
extends _EventSystemDisp
implements InitializingBean {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(EventSystemI.class));
    private boolean slimVersion = true;
    public static final String KEY_VALUE_SEPERATOR = "**";
    private int virtualNodes = 200;
    private int storeQueueLimit = 10000;
    private int maxDistributionThreadPoolSize = 20;
    private int maxStoreThreadPoolSize = 20;
    private List<String> eventStores;
    private BroadcastListDAO broadcastListDAO;
    private DisplayPictureAndStatusMessageDAO displayPictureAndStatusMessageDAO;
    private UserProfileStatusDAO userProfileStatusDAO;
    private GroupDAO groupDAO;
    private GroupAnnouncementDAO groupAnnouncementDAO;
    private UserPostDAO userPostDAO;
    private GroupMembershipDAO groupMembershipDAO;
    private RegistryPrx registryProxy;
    private RequestAndRateLongCounter setProfileStatusCounter;
    private RequestAndRateLongCounter madePhotoPublicCounter;
    private RequestAndRateLongCounter createdPublicChatrooomStatusCounter;
    private RequestAndRateLongCounter addedFriendCounter;
    private RequestAndRateLongCounter updatedProfileCounter;
    private RequestAndRateLongCounter purchasedVirtualGoodsCounter;
    private RequestAndRateLongCounter virtualGiftCounter;
    private RequestAndRateLongCounter giftShowerEventCounter;
    private RequestAndRateLongCounter userWallPostCounter;
    private RequestAndRateLongCounter genericApplicationEventCounter;
    private RequestAndRateLongCounter groupDonationCounter;
    private RequestAndRateLongCounter groupJoinedCounter;
    private RequestAndRateLongCounter groupAnnouncementCounter;
    private RequestAndRateLongCounter groupUserPostCounter;
    private RequestAndRateLongCounter totalCounter;
    private RequestAndRateLongCounter droppedEventCounter;
    private RequestAndRateLongCounter streamedEventsCounter;
    private RequestAndRateLongCounter distributedEventsCounter;
    private ConsistentHash<String> consistentHash;
    private Map<String, EventStorePrx> eventStoreProxies;
    private Map<String, ExecutorService> eventStoreExecutors;
    private ExecutorService distributionService = (ThreadPoolExecutor)Executors.newFixedThreadPool(this.maxDistributionThreadPoolSize);
    private BufferedWriter userEventStatsLogger;
    private BufferedWriter generatorEventStatsLogger;
    private CacheManager cacheManager;
    private SelfPopulatingCache groupCache;
    private SelfPopulatingCache groupAnnouncementCache;
    private SelfPopulatingCache groupTopicForUserPostCache;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public EventSystemI() throws IOException {
        FileInputStream fis = new FileInputStream(new File(System.getProperty("config.dir", "/usr/fusion/etc/") + "ehcache.evt.xml").getAbsolutePath());
        try {
            this.cacheManager = new CacheManager((InputStream)fis);
            Object var3_2 = null;
        }
        catch (Throwable throwable) {
            Object var3_3 = null;
            fis.close();
            throw throwable;
        }
        fis.close();
    }

    public void afterPropertiesSet() throws Exception {
        this.addedFriendCounter = new RequestAndRateLongCounter(5);
        this.createdPublicChatrooomStatusCounter = new RequestAndRateLongCounter(5);
        this.madePhotoPublicCounter = new RequestAndRateLongCounter(5);
        this.purchasedVirtualGoodsCounter = new RequestAndRateLongCounter(5);
        this.setProfileStatusCounter = new RequestAndRateLongCounter(5);
        this.updatedProfileCounter = new RequestAndRateLongCounter(5);
        this.virtualGiftCounter = new RequestAndRateLongCounter(5);
        this.giftShowerEventCounter = new RequestAndRateLongCounter(5);
        this.userWallPostCounter = new RequestAndRateLongCounter(5);
        this.groupAnnouncementCounter = new RequestAndRateLongCounter(5);
        this.groupDonationCounter = new RequestAndRateLongCounter(5);
        this.groupJoinedCounter = new RequestAndRateLongCounter(5);
        this.groupUserPostCounter = new RequestAndRateLongCounter(5);
        this.genericApplicationEventCounter = new RequestAndRateLongCounter(5);
        this.totalCounter = new RequestAndRateLongCounter(1);
        this.droppedEventCounter = new RequestAndRateLongCounter(1);
        this.streamedEventsCounter = new RequestAndRateLongCounter(1);
        this.distributedEventsCounter = new RequestAndRateLongCounter(1);
        this.consistentHash = new ConsistentHash<String>(new FNVHashFunction(), this.virtualNodes, this.eventStores);
        if (this.slimVersion) {
            this.userEventStatsLogger = new BufferedWriter(new OutputStreamWriter((OutputStream)new FileOutputStream("userevents.log", true), "UTF-8"));
            this.generatorEventStatsLogger = new BufferedWriter(new OutputStreamWriter((OutputStream)new FileOutputStream("generatorevents.log", true), "UTF-8"));
        }
        this.groupCache = new SelfPopulatingCache((Ehcache)this.cacheManager.getCache("groupCache"), (CacheEntryFactory)new GroupCacheEntryFactory(this.groupDAO));
        this.groupAnnouncementCache = new SelfPopulatingCache((Ehcache)this.cacheManager.getCache("groupAnnouncementCache"), (CacheEntryFactory)new GroupAnnouncementCacheEntryFactory(this.groupAnnouncementDAO));
        this.groupTopicForUserPostCache = new SelfPopulatingCache((Ehcache)this.cacheManager.getCache("groupTopicForUserPostCache"), (CacheEntryFactory)new GroupTopicForUserPostCacheEntryFactory(this.userPostDAO));
    }

    public void setMaxDistributionThreadPoolSize(int maxDistributionThreadPoolSize) {
        this.maxDistributionThreadPoolSize = maxDistributionThreadPoolSize;
    }

    public void setMaxStoreThreadPoolSize(int maxStoreThreadPoolSize) {
        this.maxStoreThreadPoolSize = maxStoreThreadPoolSize;
    }

    public void setVirtualNodes(int virtualNodes) {
        this.virtualNodes = virtualNodes;
    }

    public void setStoreQueueLimit(int storeQueueLimit) {
        this.storeQueueLimit = storeQueueLimit;
    }

    @Required
    public void setEventStores(List<String> eventStores) {
        this.eventStores = eventStores;
    }

    @Required
    public void setBroadcastListDAO(BroadcastListDAO broadcastDAO) {
        this.broadcastListDAO = broadcastDAO;
    }

    public DisplayPictureAndStatusMessageDAO getDisplayPictureAndStatusMessageDAO() {
        return this.displayPictureAndStatusMessageDAO;
    }

    @Required
    public void setDisplayPictureAndStatusMessageDAO(DisplayPictureAndStatusMessageDAO displayPictureAndStatusMessageDAO) {
        this.displayPictureAndStatusMessageDAO = displayPictureAndStatusMessageDAO;
    }

    @Required
    public void setUserProfileStatusDAO(UserProfileStatusDAO userProfileStatusDAO) {
        this.userProfileStatusDAO = userProfileStatusDAO;
    }

    @Required
    public void setGroupDAO(GroupDAO groupDAO) {
        this.groupDAO = groupDAO;
    }

    @Required
    public void setGroupAnnouncementDAO(GroupAnnouncementDAO groupAnnouncementDAO) {
        this.groupAnnouncementDAO = groupAnnouncementDAO;
    }

    @Required
    public void setUserPostDAO(UserPostDAO userPostDAO) {
        this.userPostDAO = userPostDAO;
    }

    @Required
    public void setGroupMembershipDAO(GroupMembershipDAO groupMembershipDAO) {
        this.groupMembershipDAO = groupMembershipDAO;
    }

    public SelfPopulatingCache getGroupCache() {
        return this.groupCache;
    }

    public SelfPopulatingCache getGroupAnnouncementCache() {
        return this.groupAnnouncementCache;
    }

    public SelfPopulatingCache getGroupTopicForUserPostCache() {
        return this.groupTopicForUserPostCache;
    }

    public RegistryPrx getRegistryProxy() {
        return this.registryProxy;
    }

    public void setRegistryProxy(RegistryPrx registryProxy) {
        this.registryProxy = registryProxy;
    }

    public void setSlimVersion(boolean distributeEvents) {
        this.slimVersion = distributeEvents;
    }

    private EventStorePrx proxyForProxyString(String proxyString) {
        return this.eventStoreProxies.get(proxyString);
    }

    private Set<String> fusionBroadcastListForUsername(String username) {
        Set<String> broadcastList = this.broadcastListDAO.getBroadcastListForUser(username);
        return broadcastList;
    }

    private Set<String> fusionBroadcastListForGroup(int groupId) {
        return this.broadcastListDAO.getBroadcastListForGroup(groupId);
    }

    private Set<String> fusionBroadcastListForTopic(int userPostId) {
        return new HashSet<String>(this.groupMembershipDAO.getGroupMemberUsernamesForNewGroupUserPostNotificationViaEventSystem(userPostId));
    }

    private boolean profileIsPrivate(String username) {
        UserProfileData.StatusEnum status = this.userProfileStatusDAO.getUserProfileStatus(username);
        return status == UserProfileData.StatusEnum.PRIVATE;
    }

    private boolean isEventBeingGenerated(UserEventIce userEvent) {
        if (this.profileIsPrivate(userEvent.generatingUsername)) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("not generating events for private user [" + userEvent.generatingUsername + "]"));
            }
            return false;
        }
        EventPrivacySettingIce mask = null;
        try {
            mask = this.getPublishingPrivacyMask(userEvent.generatingUsername);
            if (!EventPrivacySetting.fromEventPrivacySettingIce(mask).applyMask(userEvent)) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("publishing mask [" + mask + "] for user [" + userEvent.generatingUsername + "] prohibits this event [" + (Object)((Object)userEvent) + "]"));
                }
                return false;
            }
        }
        catch (FusionException e) {
            log.error((Object)("failed to get publishing mask for user [" + userEvent.generatingUsername + "]"), (Throwable)((Object)e));
            return false;
        }
        return true;
    }

    private Set<String> defaultDistributionList(UserEventIce userEvent) {
        return this.fusionBroadcastListForUsername(userEvent.generatingUsername);
    }

    private void processGeneratorEvent(UserEventIce userEvent) {
        UsernameAndUserEvent generatorEvent = new UsernameAndUserEvent(userEvent.generatingUsername, userEvent);
        if (!this.slimVersion) {
            this.storeGeneratorEvent(generatorEvent);
        } else {
            this.logEvent(generatorEvent);
        }
    }

    private void storeAndDistributeEvents(Set<String> distributionList, UserEventIce userEvent) {
        for (String username : distributionList) {
            if (!this.slimVersion) {
                String proxyString = this.consistentHash.get(username);
                this.storeEvent(new UsernameAndUserEvent(username, userEvent), proxyString);
                this.distributeEvent(new UsernameAndUserEvent(username, (UserEventIce)((Object)userEvent.clone())), proxyString);
            } else {
                this.logEvent(new UsernameAndUserEvent(username, userEvent));
            }
            if (!log.isDebugEnabled()) continue;
            log.debug((Object)("Done processing event for user [" + username + "]"));
        }
    }

    private void defaultEventProcess(UserEventIce userEvent) {
        if (!this.isEventBeingGenerated(userEvent)) {
            return;
        }
        this.processGeneratorEvent(userEvent);
        this.storeAndDistributeEvents(this.defaultDistributionList(userEvent), userEvent);
    }

    private void storeGeneratorEvent(UsernameAndUserEvent event) {
        String proxyString = this.consistentHash.get(event.getUsername());
        if (log.isDebugEnabled()) {
            log.debug((Object)("Submitting generator event for generator [" + event.getUserEvent().generatingUsername + "] and user [" + event.getUsername() + "] to proxy [" + proxyString + "]"));
        }
        StoreGeneratorTask task = new StoreGeneratorTask(event, this.proxyForProxyString(proxyString));
        this.eventStoreExecutors.get(proxyString).execute(task);
    }

    private void storeEvent(UsernameAndUserEvent event, String proxyString) {
        StoreTask task = new StoreTask(event, this.proxyForProxyString(proxyString));
        if (log.isDebugEnabled()) {
            log.debug((Object)("Submitting event [" + (Object)((Object)event.getUserEvent()) + "] and user [" + event.getUsername() + "] to proxy [" + proxyString + "]"));
        }
        if (((ThreadPoolExecutor)this.eventStoreExecutors.get(proxyString)).getQueue().size() > this.storeQueueLimit) {
            this.droppedEventCounter.add();
            if (log.isDebugEnabled()) {
                log.debug((Object)("dropping event since we have exceeded the queue limit [" + this.storeQueueLimit + "]"));
            }
        } else {
            this.eventStoreExecutors.get(proxyString).execute(task);
        }
    }

    private void logEvent(UsernameAndUserEvent event) {
        StringBuilder builder = new StringBuilder(System.currentTimeMillis() + ",");
        builder.append(event.getUsername()).append(",").append((Object)event.getUserEvent()).append(",").append(event.getUserEvent().timestamp);
        if (StringUtils.hasLength((String)event.getUserEvent().generatingUsername)) {
            builder.append(",").append(event.getUserEvent().generatingUsername);
        }
        builder.append("\n");
        try {
            if (log.isDebugEnabled()) {
                log.debug((Object)("logging event to disk for user " + event.getUsername() + ""));
            }
            if (StringUtils.hasLength((String)event.getUserEvent().generatingUsername)) {
                this.userEventStatsLogger.write(builder.toString());
            } else {
                this.generatorEventStatsLogger.write(builder.toString());
            }
        }
        catch (IOException e) {
            log.error((Object)"failed to write", (Throwable)e);
        }
    }

    private void distributeEvent(UsernameAndUserEvent event, String proxyString) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("Submitting event [" + (Object)((Object)event.getUserEvent()) + "] to distribution service"));
        }
        DistributionTask task = new DistributionTask(event, this, this.proxyForProxyString(proxyString));
        this.distributionService.execute(task);
    }

    @Override
    public void streamEventsToLoggingInUser(String username, ConnectionPrx connectionProxy, Current __current) throws FusionException {
        if (log.isDebugEnabled()) {
            log.debug((Object)("going to stream for user [" + username + "]"));
        }
        if (this.slimVersion) {
            return;
        }
        EventStorePrx proxy = this.proxyForProxyString(this.consistentHash.get(username));
        if (proxy == null) {
            log.error((Object)("Failed to find an EventStore proxy for user [" + username + "], not streaming anything"));
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("found EventStore proxy [" + proxy + "] for username [" + username + "]"));
        }
        UserEventIce[] events = proxy.getUserEventsForUser(username);
        if (log.isDebugEnabled()) {
            log.debug((Object)("got " + (events == null ? 0 : events.length) + " events for user [" + username + "]"));
        }
        if (events != null && events.length > 0) {
            StreamTask streamTask = new StreamTask(new UsernameAndUserEvents(username, events), connectionProxy, this);
            this.distributionService.execute(streamTask);
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("sent stream for user [" + username + "]"));
        }
    }

    @Override
    public UserEventIce[] getUserEventsForUser(String username, Current __current) throws FusionException {
        UserEventIce[] events;
        EventStorePrx proxy = this.proxyForProxyString(this.consistentHash.get(username));
        for (UserEventIce event : events = proxy.getUserEventsForUser(username)) {
            this.assignRuntimeValues(event);
        }
        return events;
    }

    @Override
    public UserEventIce[] getUserEventsGeneratedByUser(String username, Current __current) throws FusionException {
        UserEventIce[] events;
        EventStorePrx proxy = this.proxyForProxyString(this.consistentHash.get(username));
        for (UserEventIce event : events = proxy.getUserEventsGeneratedByUser(username)) {
            this.assignRuntimeValues(event);
        }
        return events;
    }

    @Override
    public void updateAllowList(String username, String[] watchers, Current __current) throws FusionException {
        throw new FusionException("Not implemented");
    }

    public void assignRuntimeValues(UserEventIce event) {
        try {
            if (event instanceof GroupUserEventIce) {
                int groupId = ((GroupUserEventIce)event).groupId;
                Element element = this.getGroupCache().get((Serializable)Integer.valueOf(groupId));
                if (element != null) {
                    ((GroupUserEventIce)event).groupName = ((GroupData)element.getObjectValue()).name;
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("set group name to [" + ((GroupUserEventIce)event).groupName + "]"));
                    }
                } else {
                    log.error((Object)("failed to find group [" + groupId + "] details from group cache!"));
                }
                if (event instanceof GroupAnnouncementUserEventIce) {
                    int groupAnnouncementId = ((GroupAnnouncementUserEventIce)event).groupAnnouncementId;
                    element = this.getGroupAnnouncementCache().get((Serializable)Integer.valueOf(groupAnnouncementId));
                    if (element != null) {
                        ((GroupAnnouncementUserEventIce)event).groupAnnouncementTitle = ((GroupAnnouncementData)element.getObjectValue()).title;
                        if (log.isDebugEnabled()) {
                            log.debug((Object)("set group announcement title to [" + ((GroupAnnouncementUserEventIce)event).groupAnnouncementTitle + "]"));
                        }
                    } else {
                        log.warn((Object)("failed to find announcement [" + groupAnnouncementId + "] details from group announcement cache!"));
                    }
                }
                if (event instanceof GroupUserPostUserEventIce) {
                    int userPostId = ((GroupUserPostUserEventIce)event).userPostId;
                    element = this.getGroupTopicForUserPostCache().get((Serializable)Integer.valueOf(userPostId));
                    if (element != null) {
                        ((GroupUserPostUserEventIce)event).topicText = ((UserPostData)element.getObjectValue()).body;
                        ((GroupUserPostUserEventIce)event).topicId = ((UserPostData)element.getObjectValue()).id;
                        if (((GroupUserPostUserEventIce)event).topicText.length() > 35) {
                            ((GroupUserPostUserEventIce)event).topicText = ((GroupUserPostUserEventIce)event).topicText.substring(0, 34) + "...";
                        }
                        if (log.isDebugEnabled()) {
                            log.debug((Object)("set group topic text to [" + ((GroupUserPostUserEventIce)event).topicText + "]"));
                        }
                    } else {
                        log.warn((Object)("failed to find UserPostData [" + userPostId + "] details from GroupTopicForUserPostCache!"));
                    }
                }
            }
        }
        catch (Exception e) {
            log.error((Object)("failed to populate event [" + (Object)((Object)event) + "]"), (Throwable)e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addedFriend(String username, String friend, Current __current) throws FusionException {
        try {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Creating [" + UserEventType.ADDING_FRIEND.toString() + "] event for user [" + username + "] and friend [" + friend + "]"));
            }
            AddingFriendUserEventIce userEvent = new AddingFriendUserEventIce();
            userEvent.generatingUsername = username;
            userEvent.timestamp = System.currentTimeMillis();
            userEvent.friend1 = friend;
            this.defaultEventProcess(userEvent);
            Object var6_5 = null;
            this.addedFriendCounter.add();
            this.totalCounter.add();
        }
        catch (Throwable throwable) {
            Object var6_6 = null;
            this.addedFriendCounter.add();
            this.totalCounter.add();
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void createdPublicChatroom(String username, String chatroomName, Current __current) throws FusionException {
        try {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Creating [" + UserEventType.CREATE_PUBLIC_CHATROOM.toString() + "] event for user [" + username + "] and chatroom [" + chatroomName + "]"));
            }
            CreatedChatroomUserEventIce event = new CreatedChatroomUserEventIce();
            event.generatingUsername = username;
            event.timestamp = System.currentTimeMillis();
            event.chatroom = chatroomName;
            this.defaultEventProcess(event);
            Object var6_5 = null;
            this.createdPublicChatrooomStatusCounter.add();
            this.totalCounter.add();
        }
        catch (Throwable throwable) {
            Object var6_6 = null;
            this.createdPublicChatrooomStatusCounter.add();
            this.totalCounter.add();
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void madePhotoPublic(String username, int scrapbookid, String title, Current __current) throws FusionException {
        try {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Creating [" + UserEventType.PHOTO_UPLOAD_WITH_TITLE.toString() + "] event for user [" + username + "] and scrapbookid [" + scrapbookid + "] and title [" + title + "]"));
            }
            PhotoUploadedUserEventIce userEvent = new PhotoUploadedUserEventIce();
            userEvent.generatingUsername = username;
            userEvent.timestamp = System.currentTimeMillis();
            userEvent.scrapbookid = scrapbookid;
            userEvent.title = title;
            this.defaultEventProcess(userEvent);
            Object var7_6 = null;
            this.madePhotoPublicCounter.add();
            this.totalCounter.add();
        }
        catch (Throwable throwable) {
            Object var7_7 = null;
            this.madePhotoPublicCounter.add();
            this.totalCounter.add();
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void purchasedVirtualGoods(String username, byte itemType, int itemid, String itemName, Current __current) throws FusionException {
        try {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Creating [" + UserEventType.PURCHASED_GOODS.toString() + "] event for user [" + username + "] and itemid [" + itemid + "] and itemname [" + itemName + "]"));
            }
            PurchasedVirtualGoodsUserEventIce userEvent = new PurchasedVirtualGoodsUserEventIce();
            userEvent.generatingUsername = username;
            userEvent.timestamp = System.currentTimeMillis();
            userEvent.itemId = itemid;
            userEvent.itemName = itemName;
            userEvent.itemType = itemType;
            this.defaultEventProcess(userEvent);
            Object var8_7 = null;
            this.purchasedVirtualGoodsCounter.add();
            this.totalCounter.add();
        }
        catch (Throwable throwable) {
            Object var8_8 = null;
            this.purchasedVirtualGoodsCounter.add();
            this.totalCounter.add();
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setProfileStatus(String username, String status, Current __current) throws FusionException {
        try {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Creating [" + UserEventType.SHORT_TEXT_STATUS.toString() + "] event for user [" + username + "] and status [" + status + "]"));
            }
            ShortTextStatusUserEventIce userEvent = new ShortTextStatusUserEventIce();
            userEvent.generatingUsername = username;
            userEvent.timestamp = System.currentTimeMillis();
            userEvent.status = StringUtil.stripHTML(status);
            this.defaultEventProcess(userEvent);
            Object var6_5 = null;
            this.setProfileStatusCounter.add();
            this.totalCounter.add();
        }
        catch (Throwable throwable) {
            Object var6_6 = null;
            this.setProfileStatusCounter.add();
            this.totalCounter.add();
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void updatedProfile(String username, Current __current) throws FusionException {
        try {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Creating [" + UserEventType.UPDATING_PROFILE.toString() + "] event for user [" + username + "]"));
            }
            ProfileUpdatedUserEventIce userEvent = new ProfileUpdatedUserEventIce();
            userEvent.generatingUsername = username;
            userEvent.timestamp = System.currentTimeMillis();
            this.defaultEventProcess(userEvent);
            Object var5_4 = null;
            this.updatedProfileCounter.add();
            this.totalCounter.add();
        }
        catch (Throwable throwable) {
            Object var5_5 = null;
            this.updatedProfileCounter.add();
            this.totalCounter.add();
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public void virtualGift(String username, String recipient, String giftName, int virtualGiftReceivedId, Current __current) {
        try {
            try {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Creating [" + UserEventType.VIRTUAL_GIFT.toString() + "] event for user [" + username + "]"));
                }
                VirtualGiftUserEventIce userEvent = new VirtualGiftUserEventIce();
                userEvent.generatingUsername = username;
                userEvent.timestamp = System.currentTimeMillis();
                userEvent.recipient = recipient;
                userEvent.giftName = giftName;
                userEvent.virtualGiftReceivedId = virtualGiftReceivedId;
                if (!this.isEventBeingGenerated(userEvent)) {
                    Object var11_8 = null;
                    this.virtualGiftCounter.add();
                    this.totalCounter.add();
                    return;
                }
                Set<String> distributionList = this.defaultDistributionList(userEvent);
                distributionList.addAll(this.fusionBroadcastListForUsername(recipient));
                distributionList.remove(username);
                if (log.isDebugEnabled()) {
                    log.debug((Object)("distribution list for virtual gift event, generator [" + userEvent.generatingUsername + "] and recipient [" + recipient + "]"));
                    for (String watcher : distributionList) {
                        log.debug((Object)watcher);
                    }
                }
                this.storeAndDistributeEvents(distributionList, userEvent);
            }
            catch (Exception e) {
                log.error((Object)"Unable to trigger virtualGift event", (Throwable)e);
                Object var11_10 = null;
                this.virtualGiftCounter.add();
                this.totalCounter.add();
                return;
            }
        }
        catch (Throwable throwable) {
            Object var11_11 = null;
            this.virtualGiftCounter.add();
            this.totalCounter.add();
            throw throwable;
        }
        Object var11_9 = null;
        this.virtualGiftCounter.add();
        this.totalCounter.add();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public void giftShowerEvent(String username, String recipient, String giftName, int virtualGiftReceivedId, int totalRecipients, Current __current) {
        block8: {
            block7: {
                try {
                    try {
                        if (log.isDebugEnabled()) {
                            log.debug((Object)("Creating [" + UserEventType.GIFT_SHOWER_EVENT.toString() + "] event for user [" + username + "]"));
                        }
                        GiftShowerUserEventIce userEvent = new GiftShowerUserEventIce();
                        userEvent.generatingUsername = username;
                        userEvent.timestamp = System.currentTimeMillis();
                        userEvent.recipient = recipient;
                        userEvent.giftName = giftName;
                        userEvent.totalRecipients = totalRecipients;
                        userEvent.virtualGiftReceivedId = virtualGiftReceivedId;
                        if (!this.isEventBeingGenerated(userEvent)) {
                            Object var10_9 = null;
                            this.giftShowerEventCounter.add();
                            this.totalCounter.add();
                            break block7;
                        }
                        Set<String> distributionList = this.defaultDistributionList(userEvent);
                        distributionList.addAll(this.fusionBroadcastListForUsername(recipient));
                        distributionList.remove(username);
                        log.debug((Object)("Distributing [" + UserEventType.GIFT_SHOWER_EVENT.toString() + "] from [" + recipient + "] to " + distributionList.toString()));
                        this.storeAndDistributeEvents(distributionList, userEvent);
                        break block8;
                    }
                    catch (Exception e) {
                        log.error((Object)"Unable to trigger giftShowerEvent event", (Throwable)e);
                        Object var10_11 = null;
                        this.giftShowerEventCounter.add();
                        this.totalCounter.add();
                        log.debug((Object)("Total shower events: " + this.giftShowerEventCounter.getTotalRequests()));
                        return;
                    }
                }
                catch (Throwable throwable) {
                    Object var10_12 = null;
                    this.giftShowerEventCounter.add();
                    this.totalCounter.add();
                    log.debug((Object)("Total shower events: " + this.giftShowerEventCounter.getTotalRequests()));
                    throw throwable;
                }
            }
            log.debug((Object)("Total shower events: " + this.giftShowerEventCounter.getTotalRequests()));
            return;
        }
        Object var10_10 = null;
        this.giftShowerEventCounter.add();
        this.totalCounter.add();
        log.debug((Object)("Total shower events: " + this.giftShowerEventCounter.getTotalRequests()));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void userWallPost(String username, String wallOwnerUsername, String postContent, int userWallPostId, Current __current) throws FusionException {
        try {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Creating [" + UserEventType.USER_WALL_POST.toString() + "] event for user [" + username + "]"));
            }
            UserWallPostUserEventIce userEvent = new UserWallPostUserEventIce();
            userEvent.generatingUsername = username;
            userEvent.timestamp = System.currentTimeMillis();
            userEvent.wallOwnerUsername = wallOwnerUsername;
            userEvent.postPrefix = postContent.substring(0, 10) + "...";
            userEvent.userWallPostId = userWallPostId;
            if (!this.isEventBeingGenerated(userEvent)) {
                Object var11_7 = null;
                this.userWallPostCounter.add();
                this.totalCounter.add();
                return;
            }
            Set<String> distributionList = this.fusionBroadcastListForUsername(wallOwnerUsername);
            distributionList.add(wallOwnerUsername);
            distributionList.remove(username);
            if (log.isDebugEnabled()) {
                log.debug((Object)("distribution list for user wall post event, generator [" + userEvent.generatingUsername + "] and wall owner [" + userEvent.wallOwnerUsername + "]"));
                for (String watcher : distributionList) {
                    log.debug((Object)watcher);
                }
            }
            this.storeAndDistributeEvents(distributionList, userEvent);
        }
        catch (Throwable throwable) {
            Object var11_9 = null;
            this.userWallPostCounter.add();
            this.totalCounter.add();
            throw throwable;
        }
        Object var11_8 = null;
        this.userWallPostCounter.add();
        this.totalCounter.add();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void groupAnnouncement(String username, int groupId, int groupAnnoucementId, Current __current) throws FusionException {
        try {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Creating [" + UserEventType.GROUP_ANNOUNCEMENT.toString() + "] event for user [" + username + "]"));
            }
            GroupAnnouncementUserEventIce groupEvent = new GroupAnnouncementUserEventIce();
            groupEvent.generatingUsername = username;
            groupEvent.timestamp = System.currentTimeMillis();
            groupEvent.groupId = groupId;
            groupEvent.groupAnnouncementId = groupAnnoucementId;
            Set<String> distributionList = this.fusionBroadcastListForGroup(groupId);
            if (log.isDebugEnabled()) {
                log.debug((Object)("distribution list for group announcement event, group [" + groupEvent.groupId + "]"));
                for (String watcher : distributionList) {
                    log.debug((Object)watcher);
                }
            }
            this.storeAndDistributeEvents(distributionList, groupEvent);
            Object var10_9 = null;
            this.groupAnnouncementCounter.add();
            this.totalCounter.add();
        }
        catch (Throwable throwable) {
            Object var10_10 = null;
            this.groupAnnouncementCounter.add();
            this.totalCounter.add();
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void groupDonation(String username, int groupId, Current __current) throws FusionException {
        try {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Creating [" + UserEventType.GROUP_DONATION.toString() + "] event for user [" + username + "]"));
            }
            GroupDonationUserEventIce groupEvent = new GroupDonationUserEventIce();
            groupEvent.generatingUsername = username;
            groupEvent.timestamp = System.currentTimeMillis();
            groupEvent.groupId = groupId;
            this.defaultEventProcess(groupEvent);
            Object var6_5 = null;
            this.groupDonationCounter.add();
            this.totalCounter.add();
        }
        catch (Throwable throwable) {
            Object var6_6 = null;
            this.groupDonationCounter.add();
            this.totalCounter.add();
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void groupJoined(String username, int groupId, Current __current) throws FusionException {
        try {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Creating [" + UserEventType.GROUP_JOINED.toString() + "] event for user [" + username + "]"));
            }
            GroupJoinedUserEventIce groupEvent = new GroupJoinedUserEventIce();
            groupEvent.generatingUsername = username;
            groupEvent.timestamp = System.currentTimeMillis();
            groupEvent.groupId = groupId;
            this.defaultEventProcess(groupEvent);
            Object var6_5 = null;
            this.groupJoinedCounter.add();
            this.totalCounter.add();
        }
        catch (Throwable throwable) {
            Object var6_6 = null;
            this.groupJoinedCounter.add();
            this.totalCounter.add();
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void madeGroupUserPost(String username, int userPostId, int groupId, Current __current) throws FusionException {
        try {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Creating [" + UserEventType.GROUP_USER_POST.toString() + "] event for user [" + username + "]"));
            }
            GroupUserPostUserEventIce groupEvent = new GroupUserPostUserEventIce();
            groupEvent.generatingUsername = username;
            groupEvent.timestamp = System.currentTimeMillis();
            groupEvent.groupId = groupId;
            groupEvent.userPostId = userPostId;
            Set<String> distributionList = this.fusionBroadcastListForTopic(userPostId);
            if (log.isDebugEnabled()) {
                log.debug((Object)("distribution list for group userpost event, userpost [" + groupEvent.userPostId + "] group [" + groupEvent.groupId + "]"));
                for (String watcher : distributionList) {
                    log.debug((Object)watcher);
                }
            }
            this.storeAndDistributeEvents(distributionList, groupEvent);
            Object var10_9 = null;
            this.groupUserPostCounter.add();
            this.totalCounter.add();
        }
        catch (Throwable throwable) {
            Object var10_10 = null;
            this.groupUserPostCounter.add();
            this.totalCounter.add();
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void genericApplicationEvent(String username, String appID, String text, Map<String, String> customDeviceURLs, Current __current) throws FusionException {
        try {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Creating [" + UserEventType.GENERIC_APP_EVENT.toString() + "] event for user [" + username + "]"));
            }
            if (StringUtil.isBlank(username) || StringUtil.isBlank(text) || customDeviceURLs == null) {
                log.error((Object)("genericApplicationEvent called with username (" + username + ") text(" + text + ") customDeviceURLs(" + customDeviceURLs + ")"));
                Object var8_6 = null;
                this.genericApplicationEventCounter.add();
                this.totalCounter.add();
                return;
            }
            if (text.length() > 200) {
                text = text.substring(0, 200);
            }
            GenericApplicationUserEventIce userEvent = new GenericApplicationUserEventIce();
            userEvent.generatingUsername = username;
            userEvent.timestamp = System.currentTimeMillis();
            userEvent.text = StringUtil.stripHTML(text);
            userEvent.urls = customDeviceURLs;
            this.defaultEventProcess(userEvent);
        }
        catch (Throwable throwable) {
            Object var8_8 = null;
            this.genericApplicationEventCounter.add();
            this.totalCounter.add();
            throw throwable;
        }
        Object var8_7 = null;
        this.genericApplicationEventCounter.add();
        this.totalCounter.add();
    }

    @Override
    public EventPrivacySettingIce getPublishingPrivacyMask(String username, Current __current) throws FusionException {
        EventStorePrx proxy = this.proxyForProxyString(this.consistentHash.get(username));
        return proxy.getPublishingPrivacyMask(username);
    }

    @Override
    public EventPrivacySettingIce getReceivingPrivacyMask(String username, Current __current) throws FusionException {
        EventStorePrx proxy = this.proxyForProxyString(this.consistentHash.get(username));
        return proxy.getReceivingPrivacyMask(username);
    }

    @Override
    public void setPublishingPrivacyMask(String username, EventPrivacySettingIce mask, Current __current) throws FusionException {
        EventStorePrx proxy = this.proxyForProxyString(this.consistentHash.get(username));
        proxy.setPublishingPrivacyMask(username, mask);
    }

    @Override
    public void setReceivingPrivacyMask(String username, EventPrivacySettingIce mask, Current __current) throws FusionException {
        EventStorePrx proxy = this.proxyForProxyString(this.consistentHash.get(username));
        proxy.setReceivingPrivacyMask(username, mask);
    }

    @Override
    public void deleteUserEvents(String username, Current __current) throws FusionException {
        String proxyString = this.consistentHash.get(username);
        EventStorePrx eventStoreProxy = this.proxyForProxyString(proxyString);
        eventStoreProxy.deleteUserEvents(username);
    }

    private EventStorePrx narrowEventStoreProxy(Communicator communicator, String name) {
        EventStorePrx proxy;
        try {
            ObjectPrx basePrx = communicator.stringToProxy(name);
            if (basePrx == null) {
                log.error((Object)("failed to create proxy from string [" + name + "]"));
                return null;
            }
            proxy = EventStorePrxHelper.checkedCast(basePrx);
            if (proxy == null) {
                log.error((Object)("failed to cast EventStore proxy from base proxy from string [" + name + "]"));
                return null;
            }
            proxy.ice_ping();
        }
        catch (Exception e) {
            log.warn((Object)("failed to contact proxy [" + name + "]"), (Throwable)e);
            return null;
        }
        return proxy;
    }

    public void createProxies(Communicator communicator) {
        log.info((Object)"Creating proxies...");
        try {
            this.eventStoreProxies = new HashMap<String, EventStorePrx>(this.eventStores.size());
            this.eventStoreExecutors = new HashMap<String, ExecutorService>(this.eventStores.size());
            for (String eventStoreProxyString : this.eventStores) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Considering proxy string [" + eventStoreProxyString + "]"));
                }
                EventStorePrx proxy = this.narrowEventStoreProxy(communicator, eventStoreProxyString);
                while (proxy == null) {
                    proxy = this.narrowEventStoreProxy(communicator, eventStoreProxyString);
                    log.warn((Object)("Waiting for proxy [" + eventStoreProxyString + "] to become available, EventSystem is not creating events until the proxy is up!"));
                }
                this.eventStoreProxies.put(eventStoreProxyString, proxy);
                this.eventStoreExecutors.put(eventStoreProxyString, Executors.newFixedThreadPool(this.maxStoreThreadPoolSize));
                log.info((Object)("Added proxy [" + proxy + "]"));
            }
        }
        catch (Exception e) {
            log.error((Object)"Failed to create proxies", (Throwable)e);
        }
        log.info((Object)"Done creating proxies...");
    }

    public void shutdown() {
        log.info((Object)("Shutting down " + this.getClass().getCanonicalName()));
        if (this.slimVersion) {
            try {
                this.userEventStatsLogger.close();
                this.generatorEventStatsLogger.close();
            }
            catch (IOException e) {
                log.error((Object)"failed to close stats logger", (Throwable)e);
            }
        }
    }

    public RequestAndRateLongCounter getSetProfileStatusCounter() {
        return this.setProfileStatusCounter;
    }

    public RequestAndRateLongCounter getMadePhotoPublicCounter() {
        return this.madePhotoPublicCounter;
    }

    public RequestAndRateLongCounter getCreatedPublicChatrooomStatusCounter() {
        return this.createdPublicChatrooomStatusCounter;
    }

    public RequestAndRateLongCounter getAddedFriendCounter() {
        return this.addedFriendCounter;
    }

    public RequestAndRateLongCounter getUpdatedProfileCounter() {
        return this.updatedProfileCounter;
    }

    public RequestAndRateLongCounter getPurchasedVirtualGoodsCounter() {
        return this.purchasedVirtualGoodsCounter;
    }

    public RequestAndRateLongCounter getVirtualGiftCounter() {
        return this.virtualGiftCounter;
    }

    public RequestAndRateLongCounter getUserWallPostCounter() {
        return this.userWallPostCounter;
    }

    public RequestAndRateLongCounter getTotalCounter() {
        return this.totalCounter;
    }

    public RequestAndRateLongCounter getDroppedEventCounter() {
        return this.droppedEventCounter;
    }

    public RequestAndRateLongCounter getStreamedEventsCounter() {
        return this.streamedEventsCounter;
    }

    public RequestAndRateLongCounter getDistributedEventsCounter() {
        return this.distributedEventsCounter;
    }

    public RequestAndRateLongCounter getGenericApplicationEventCounter() {
        return this.genericApplicationEventCounter;
    }

    public RequestAndRateLongCounter getGiftShowerEventCounter() {
        return this.giftShowerEventCounter;
    }
}

