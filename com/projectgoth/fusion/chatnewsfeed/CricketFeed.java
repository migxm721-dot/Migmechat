/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.axis.utils.StringUtils
 *  org.apache.log4j.Logger
 *  org.apache.log4j.xml.DOMConfigurator
 */
package com.projectgoth.fusion.chatnewsfeed;

import com.projectgoth.fusion.chatnewsfeed.ChatRoomNewsFeedApp;
import com.projectgoth.fusion.chatnewsfeed.cricket.data.Match;
import com.projectgoth.fusion.chatnewsfeed.cricket.data.MatchScorecard;
import com.projectgoth.fusion.chatnewsfeed.cricket.feed.CricketFeedConfigurer;
import com.projectgoth.fusion.chatnewsfeed.cricket.feed.MatchFeedHelper;
import com.projectgoth.fusion.chatnewsfeed.cricket.feed.MatchScoreFeedHelper;
import com.projectgoth.fusion.chatnewsfeed.util.FileUtil;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.data.GroupPostData;
import com.projectgoth.fusion.data.MessageData;
import java.io.File;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.axis.utils.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class CricketFeed
extends ChatRoomNewsFeedApp {
    protected static final String APP_NAME = "CricketFeed";
    protected static final String CONFIG_FILE = "CricketFeed.cfg";
    private static DocumentBuilderFactory xmlFactory;
    private MatchFeedHelper matchFeedHelper;
    private MatchScoreFeedHelper matchScoreFeedHelper;
    private Map<String, Match> matches = new HashMap<String, Match>();
    private Map<String, MatchScorecard> matchScorecards = new HashMap<String, MatchScorecard>();
    public static boolean filterBySeries;
    public static List<String> seriesNames;
    private static String username;
    private static String password;
    private static String localDir;
    private static String remoteDir;
    private static String calendarFileName;
    private static String scoreFileSuffix;
    public static String[] generalChatRoomPrefixes;
    public static String[] gameChatRoomPrefixes;
    public static int numberOfRooms;
    public static Map<String, String> teamMappings;
    public static Map<String, String> gamePrefixMappings;
    public static Map<String, String> teamScoreNameMappings;
    public static Map<String, String> seriesNameMappings;
    public static Map<String, Integer> groupModuleMappings;
    public static Map<String, String> groupUsernameMappings;
    public static boolean useMatchNumberForPrefix;

    public CricketFeed() {
        xmlFactory = DocumentBuilderFactory.newInstance();
        this.matchFeedHelper = new MatchFeedHelper();
        this.matchScoreFeedHelper = new MatchScoreFeedHelper();
        Authenticator.setDefault(new CustomAuthenticator());
    }

    public static DocumentBuilderFactory getXmlFactory() {
        return xmlFactory;
    }

    public static String getConfigFile() {
        return CONFIG_FILE;
    }

    public static String getLocalDir() {
        return localDir;
    }

    public static String getRemoteDir() {
        return remoteDir;
    }

    public static boolean isFilterBySeries() {
        return filterBySeries;
    }

    public static void setFilterBySeries(boolean filterBySeries) {
        CricketFeed.filterBySeries = filterBySeries;
    }

    public static List<String> getSeriesNames() {
        return seriesNames;
    }

    public static void setSeriesNames(List<String> seriesNames) {
        CricketFeed.seriesNames = seriesNames;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        CricketFeed.username = username;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        CricketFeed.password = password;
    }

    public static String getCalendarFileName() {
        return calendarFileName;
    }

    public static void setCalendarFileName(String calendarFileName) {
        CricketFeed.calendarFileName = calendarFileName;
    }

    public static String getScoreFileSuffix() {
        return scoreFileSuffix;
    }

    public static void setScoreFileSuffix(String scoreFileSuffix) {
        CricketFeed.scoreFileSuffix = scoreFileSuffix;
    }

    public static void setXmlFactory(DocumentBuilderFactory xmlFactory) {
        CricketFeed.xmlFactory = xmlFactory;
    }

    public static String[] getGeneralChatRoomPrefixes() {
        return generalChatRoomPrefixes;
    }

    public static void setGeneralChatRoomPrefixes(String[] generalChatRoomPrefixes) {
        CricketFeed.generalChatRoomPrefixes = generalChatRoomPrefixes;
    }

    public static String[] getGameChatRoomPrefixes() {
        return gameChatRoomPrefixes;
    }

    public static void setGameChatRoomPrefixes(String[] gameChatRoomPrefixes) {
        CricketFeed.gameChatRoomPrefixes = gameChatRoomPrefixes;
    }

    public static int getNumberOfRooms() {
        return numberOfRooms;
    }

    public static void setNumberOfRooms(int numberOfRooms) {
        CricketFeed.numberOfRooms = numberOfRooms;
    }

    public static Map<String, String> getTeamMappings() {
        return teamMappings;
    }

    public static void setTeamMappings(Map<String, String> teamMappings) {
        CricketFeed.teamMappings = teamMappings;
    }

    public static Map<String, String> getGamePrefixMappings() {
        return gamePrefixMappings;
    }

    public static void setGamePrefixMappings(Map<String, String> gamePrefixMappings) {
        CricketFeed.gamePrefixMappings = gamePrefixMappings;
    }

    public static Map<String, String> getTeamScoreNameMappings() {
        return teamScoreNameMappings;
    }

    public static void setTeamScoreNameMappings(Map<String, String> teamScoreNameMappings) {
        CricketFeed.teamScoreNameMappings = teamScoreNameMappings;
    }

    public static Map<String, String> getSeriesNameMappings() {
        return seriesNameMappings;
    }

    public static void setSeriesNameMappings(Map<String, String> seriesNameMappings) {
        CricketFeed.seriesNameMappings = seriesNameMappings;
    }

    public static Map<String, Integer> getGroupModuleMappings() {
        return groupModuleMappings;
    }

    public static void setGroupModuleMappings(Map<String, Integer> groupModuleMappings) {
        CricketFeed.groupModuleMappings = groupModuleMappings;
    }

    public static Map<String, String> getGroupUsernameMappings() {
        return groupUsernameMappings;
    }

    public static void setGroupUsernameMappings(Map<String, String> groupUsernameMappings) {
        CricketFeed.groupUsernameMappings = groupUsernameMappings;
    }

    public static boolean isUseMatchNumberForPrefix() {
        return useMatchNumberForPrefix;
    }

    public static void setUseMatchNumberForPrefix(boolean useMatchNumberForPrefix) {
        CricketFeed.useMatchNumberForPrefix = useMatchNumberForPrefix;
    }

    public static void setLocalDir(String localDir) {
        CricketFeed.localDir = localDir;
    }

    public static void setRemoteDir(String remoteDir) {
        CricketFeed.remoteDir = remoteDir;
    }

    public static void main(String[] args) {
        DOMConfigurator.configureAndWatch((String)ConfigUtils.getDefaultLog4jConfigFilename());
        logger.info((Object)"CricketFeed version @version@");
        logger.info((Object)"Copyright (c) 2005-2008 Project Goth Inc. All rights reserved");
        CricketFeed feedApp = new CricketFeed();
        if (args.length >= 1) {
            logger.info((Object)("Using custom configuration file: " + args[0]));
            feedApp.main(APP_NAME, args, args[0]);
        } else {
            feedApp.main(APP_NAME, args, CONFIG_FILE);
        }
    }

    @Override
    public int run(String[] arg0) {
        super.run(arg0);
        CricketFeedConfigurer configurer = new CricketFeedConfigurer(CricketFeed.communicator().getProperties(), this);
        configurer.configure();
        logger.info((Object)"Service started");
        GetMatchFeed matchFeed = new GetMatchFeed();
        GetMatchScoreFeed matchScoreFeed = new GetMatchScoreFeed();
        matchFeed.start();
        matchScoreFeed.start();
        CricketFeed.communicator().waitForShutdown();
        if (CricketFeed.interrupted()) {
            logger.fatal((Object)("CricketFeed " + hostName + ": terminating"));
        }
        return 0;
    }

    static {
        teamMappings = new HashMap<String, String>();
        gamePrefixMappings = new HashMap<String, String>();
        teamScoreNameMappings = new HashMap<String, String>();
        seriesNameMappings = new HashMap<String, String>();
        groupModuleMappings = new HashMap<String, Integer>();
        groupUsernameMappings = new HashMap<String, String>();
        logger = Logger.getLogger((String)ConfigUtils.getLoggerName(CricketFeed.class));
    }

    public class CustomAuthenticator
    extends Authenticator {
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password.toCharArray());
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    class GetMatchScoreFeed
    extends Thread {
        GetMatchScoreFeed() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        @Override
        public void run() {
            HashMap<String, Match> copyMatches = new HashMap<String, Match>();
            try {
                GetMatchScoreFeed.sleep(5000L);
            }
            catch (Exception e) {
                ChatRoomNewsFeedApp.logger.error((Object)"Exception running match score feed during initial wait period", (Throwable)e);
            }
            while (true) {
                Object var7_8;
                try {
                    while (true) {
                        try {
                            CricketFeed.this.matchScoreFeedHelper.initializeMatchesForFeed(copyMatches, CricketFeed.this.matches, CricketFeed.this.matchScorecards);
                            for (MatchScorecard scorecard : CricketFeed.this.matchScorecards.values()) {
                                Match match = scorecard.getMatch();
                                if (ChatRoomNewsFeedApp.logger.isDebugEnabled()) {
                                    ChatRoomNewsFeedApp.logger.debug((Object)("Match picked up: " + match));
                                }
                                if (match.isLive() || !StringUtils.isEmpty((String)match.getResult()) && !match.isResultReported()) {
                                    Document xmlDocument = this.loadScoreFeed(match);
                                    if (xmlDocument == null) continue;
                                    this.processScore(scorecard, match, xmlDocument);
                                    continue;
                                }
                                if (!match.isUpcoming() || !scorecard.isChangeChatRoomDescription()) continue;
                                this.setUpcomingMatchState(scorecard);
                            }
                            GetMatchScoreFeed.sleep(15000L);
                        }
                        catch (Exception e) {
                            ChatRoomNewsFeedApp.logger.error((Object)"Exception running match score feed", (Throwable)e);
                            var7_8 = null;
                            if (ChatRoomNewsFeedApp.logger.isDebugEnabled()) {
                                ChatRoomNewsFeedApp.logger.debug((Object)("Scorecards found: " + CricketFeed.this.matchScorecards.values()));
                            }
                            copyMatches.clear();
                            continue;
                        }
                        break;
                    }
                    var7_8 = null;
                }
                catch (Throwable throwable) {
                    var7_8 = null;
                    if (ChatRoomNewsFeedApp.logger.isDebugEnabled()) {
                        ChatRoomNewsFeedApp.logger.debug((Object)("Scorecards found: " + CricketFeed.this.matchScorecards.values()));
                    }
                    copyMatches.clear();
                    throw throwable;
                }
                if (ChatRoomNewsFeedApp.logger.isDebugEnabled()) {
                    ChatRoomNewsFeedApp.logger.debug((Object)("Scorecards found: " + CricketFeed.this.matchScorecards.values()));
                }
                copyMatches.clear();
            }
        }

        public Document loadScoreFeed(Match match) {
            Document xmlDocument = null;
            String fileName = match.getMatchIdentifier() + scoreFileSuffix;
            File file = new File(localDir + File.separatorChar + fileName);
            FileUtil.downloadFile(remoteDir + fileName, file);
            if (file.exists()) {
                try {
                    xmlDocument = FileUtil.getDocument(xmlFactory, file);
                }
                catch (Exception e) {
                    ChatRoomNewsFeedApp.logger.error((Object)("Match score file " + file.getAbsolutePath() + " could not be located. Skipping"));
                }
            }
            return xmlDocument;
        }

        private void processScore(MatchScorecard scorecard, Match match, Document xmlDocument) {
            Node inningsNode = CricketFeed.this.matchScoreFeedHelper.getActiveInnings(xmlDocument, "Innings", scorecard);
            if (inningsNode != null && inningsNode.getNodeType() == 1) {
                NamedNodeMap attributesMap = inningsNode.getAttributes();
                CricketFeed.this.matchScoreFeedHelper.getInningsAttributes(scorecard, attributesMap);
                CricketFeed.this.matchScoreFeedHelper.setMatchPreview(scorecard, match, xmlDocument);
                NodeList scoreNodeList = inningsNode.getChildNodes();
                Node scoreNode = CricketFeed.this.matchScoreFeedHelper.getLastNode(scoreNodeList);
                if (scoreNode != null) {
                    ArrayList<MessageData> messagesToSend = new ArrayList();
                    messagesToSend = CricketFeed.this.matchScoreFeedHelper.backFillScores(CricketFeed.this.matches, scorecard, scoreNodeList, scoreNode);
                    MessageData messageData = CricketFeed.this.matchScoreFeedHelper.getScore(scoreNode, scorecard, CricketFeed.this.matches);
                    if (scorecard.getCurrentNode() != -1 && scorecard.getCurrentNode() != scorecard.getLastReadNode()) {
                        this.sendLatestCommentary(scorecard, messagesToSend, messageData);
                        scorecard.setLastReadNode(scorecard.getCurrentNode());
                        scorecard.setLastCommentary(messageData.messageText);
                    } else if (!(match.isLive() || match.isResultReported() || StringUtils.isEmpty((String)match.getResult()))) {
                        this.sendMatchResult(scorecard, messagesToSend);
                    } else if (match.isLive() && scorecard.isChangeChatRoomDescription()) {
                        this.setLiveMatchState(scorecard);
                    }
                }
            }
        }

        private void sendMatchResult(MatchScorecard scorecard, List<MessageData> messagesToSend) {
            CricketFeed.this.chatRoomNames = CricketFeed.this.matchScoreFeedHelper.createRoomList(scorecard.getMatch(), generalChatRoomPrefixes, numberOfRooms);
            CricketFeed.this.matchScoreFeedHelper.generateMatchSummary(scorecard, true);
            CricketFeed.this.matchScoreFeedHelper.appendFinalResult(scorecard, CricketFeed.this.matches, messagesToSend);
            String roomDescription = null;
            if (scorecard.isChangeChatRoomDescription()) {
                ChatRoomNewsFeedApp.logger.info((Object)"Room description needs to be updated with result");
                roomDescription = CricketFeed.this.matchScoreFeedHelper.createChatRoomDescription(scorecard);
                if (ChatRoomNewsFeedApp.logger.isInfoEnabled()) {
                    ChatRoomNewsFeedApp.logger.info((Object)("Room description set: " + roomDescription));
                }
            }
            CricketFeed.this.matchScoreFeedHelper.appendInningsSummary(scorecard, messagesToSend);
            this.sendMessages(messagesToSend, roomDescription);
            this.postMatchResultToGroups(scorecard);
        }

        private void postMatchResultToGroups(MatchScorecard scorecard) {
            Match match = scorecard.getMatch();
            String teaser = CricketFeed.this.matchScoreFeedHelper.createMatchResultPost(scorecard);
            this.createGroupPost(teaser, "Cricket FanZone");
            this.createGroupPost(teaser, match.getTeamA());
            this.createGroupPost(teaser, match.getTeamB());
        }

        private void setUpcomingMatchState(MatchScorecard scorecard) {
            CricketFeed.this.chatRoomNames = CricketFeed.this.matchScoreFeedHelper.createRoomListForUpcomingEvent(scorecard.getMatch(), CricketFeed.this.matches, numberOfRooms);
            String roomDescription = CricketFeed.this.matchScoreFeedHelper.createChatRoomDescription(scorecard);
            this.updateMatchState(roomDescription);
        }

        private void setLiveMatchState(MatchScorecard scorecard) {
            CricketFeed.this.chatRoomNames = CricketFeed.this.matchScoreFeedHelper.createRoomList(scorecard.getMatch(), generalChatRoomPrefixes, numberOfRooms);
            String roomDescription = CricketFeed.this.matchScoreFeedHelper.createChatRoomDescription(scorecard);
            this.updateMatchState(roomDescription);
        }

        private void updateMatchState(String roomDescription) {
            try {
                if (CricketFeed.this.chatRoomNames != null && !StringUtils.isEmpty((String)roomDescription)) {
                    CricketFeed.this.updateChatRoomDescription(roomDescription);
                    if (ChatRoomNewsFeedApp.logger.isInfoEnabled()) {
                        ChatRoomNewsFeedApp.logger.info((Object)("Room description set: " + roomDescription));
                    }
                }
            }
            catch (Exception e) {
                ChatRoomNewsFeedApp.logger.error((Object)"Could not update description of chat rooms.", (Throwable)e);
            }
        }

        public void createGroupPost(String teaser, String groupName) {
            try {
                String username;
                Integer groupModuleId = groupModuleMappings.get(groupName);
                String groupOwner = groupUsernameMappings.get(groupName);
                String string = username = StringUtils.isEmpty((String)groupOwner) ? groupUsernameMappings.get("Cricket FanZone") : groupOwner;
                if (groupModuleId != null) {
                    CricketFeed.this.createGroupPost(username, groupModuleId, teaser, null, GroupPostData.StatusEnum.ACTIVE.value());
                    if (ChatRoomNewsFeedApp.logger.isDebugEnabled()) {
                        ChatRoomNewsFeedApp.logger.debug((Object)("Message '" + teaser + "' posted to group module " + groupModuleId));
                    }
                }
            }
            catch (Exception e) {
                ChatRoomNewsFeedApp.logger.warn((Object)("Error mapping group module for teams in match " + groupName));
            }
        }

        private boolean sendLatestCommentary(MatchScorecard scorecard, List<MessageData> messagesToSend, MessageData messageData) {
            CricketFeed.this.chatRoomNames = CricketFeed.this.matchScoreFeedHelper.createRoomList(scorecard.getMatch(), generalChatRoomPrefixes, numberOfRooms);
            boolean sentScores = false;
            if (messagesToSend != null) {
                if (scorecard.getLastReadNode() == 0) {
                    messagesToSend.add(0, CricketFeed.this.matchScoreFeedHelper.createMessageData(scorecard.getMatch().getPreview()));
                    if (ChatRoomNewsFeedApp.logger.isDebugEnabled()) {
                        ChatRoomNewsFeedApp.logger.debug((Object)("With Preview: " + scorecard.getMatch().getPreview()));
                    }
                }
                messagesToSend.add(messageData);
            }
            String roomDescription = null;
            if (scorecard.isChangeChatRoomDescription() || !scorecard.isLiveStatusUpdated()) {
                roomDescription = CricketFeed.this.matchScoreFeedHelper.createChatRoomDescription(scorecard);
                scorecard.setLiveStatusUpdated(true);
            }
            sentScores = this.sendMessages(messagesToSend, roomDescription);
            return sentScores;
        }

        private boolean sendMessages(List<MessageData> messages, String roomDescription) {
            try {
                if (CricketFeed.this.chatRoomNames != null) {
                    CricketFeed.this.sendMessagesToChatRooms(messages, roomDescription);
                    if (ChatRoomNewsFeedApp.logger.isInfoEnabled()) {
                        for (MessageData messageData : messages) {
                            ChatRoomNewsFeedApp.logger.info((Object)("Message sent: " + messageData.messageText));
                        }
                    }
                    return true;
                }
            }
            catch (Exception e) {
                ChatRoomNewsFeedApp.logger.error((Object)"Error occurred while sending messages to one or more chat rooms.", (Throwable)e);
            }
            return false;
        }
    }

    class GetMatchFeed
    extends Thread {
        GetMatchFeed() {
        }

        public void run() {
            while (true) {
                try {
                    while (true) {
                        Node node;
                        Document xmlDocument;
                        NodeList nodeList;
                        File file = new File(localDir + File.separatorChar + calendarFileName);
                        FileUtil.downloadFile(remoteDir + calendarFileName, file);
                        if (file.exists() && (nodeList = (xmlDocument = FileUtil.getDocument(xmlFactory, file)).getElementsByTagName("calendar")) != null && (node = nodeList.item(0)) != null) {
                            CricketFeed.this.matchFeedHelper.processMatches(node, xmlDocument, CricketFeed.this.matches, seriesNames);
                        }
                        if (ChatRoomNewsFeedApp.logger.isDebugEnabled()) {
                            ChatRoomNewsFeedApp.logger.debug((Object)"End of calendar download");
                        }
                        GetMatchFeed.sleep(60000L);
                    }
                }
                catch (Exception e) {
                    ChatRoomNewsFeedApp.logger.error((Object)"Exception running match feed", (Throwable)e);
                    continue;
                }
                break;
            }
        }
    }
}

