/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Properties
 *  org.apache.axis.utils.StringUtils
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.chatnewsfeed.cricket.feed;

import Ice.Properties;
import com.projectgoth.fusion.chatnewsfeed.CricketFeed;
import com.projectgoth.fusion.chatnewsfeed.util.FileUtil;
import com.projectgoth.fusion.common.ConfigUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.axis.utils.StringUtils;
import org.apache.log4j.Logger;

public class CricketFeedConfigurer {
    private static final Logger logger = Logger.getLogger((String)ConfigUtils.getLoggerName(CricketFeed.class));
    CricketFeed cricketFeed;
    Properties properties;

    public CricketFeedConfigurer(Properties properties, CricketFeed cricketFeed) {
        this.cricketFeed = cricketFeed;
        this.properties = properties;
    }

    public void configure() {
        this.setupFeedParameters();
        this.setChatRoomProperties();
        this.setSeriesProperties();
        this.setTeamNameMappings();
        this.cleanupRemoteServer();
        this.cleanupLocalDirectory();
    }

    private void setChatRoomProperties() {
        CricketFeed.numberOfRooms = this.properties.getPropertyAsIntWithDefault("NumberOfRooms", 25);
        CricketFeed.generalChatRoomPrefixes = this.properties.getProperty("GeneralChatRoomPrefixes").split("[;]");
        CricketFeed.gameChatRoomPrefixes = this.properties.getProperty("GameChatRoomPrefixes").split("[;]");
    }

    private void setupFeedParameters() {
        String configFile = CricketFeed.getConfigFile();
        String username = this.properties.getProperty("UserName");
        String password = this.properties.getProperty("Pass");
        if (StringUtils.isEmpty((String)username) || StringUtils.isEmpty((String)password)) {
            throw new RuntimeException("Login credentials missing! Check " + configFile);
        }
        CricketFeed.setUsername(this.properties.getProperty("UserName"));
        CricketFeed.setPassword(this.properties.getProperty("Pass"));
        String localDir = this.properties.getProperty("LocalDir");
        String remoteDir = this.properties.getProperty("RemoteDir");
        if (StringUtils.isEmpty((String)localDir) || StringUtils.isEmpty((String)remoteDir)) {
            throw new RuntimeException("Local/remote directory path missing! Check " + configFile);
        }
        CricketFeed.setLocalDir(localDir);
        CricketFeed.setRemoteDir(remoteDir);
        String calendarFileName = this.properties.getProperty("CalendarFileName");
        if (StringUtils.isEmpty((String)calendarFileName)) {
            throw new RuntimeException("Feed file name missing! Check " + configFile);
        }
        CricketFeed.setCalendarFileName(calendarFileName);
        String scoreFileSuffix = this.properties.getProperty("ScoreFileSuffix");
        if (StringUtils.isEmpty((String)scoreFileSuffix)) {
            throw new RuntimeException("Score file suffix missing! Check " + configFile);
        }
        CricketFeed.setScoreFileSuffix(scoreFileSuffix);
    }

    private void setSeriesProperties() {
        String useMatchNumber;
        String seriesMappingsProperty;
        boolean filterBySeries;
        String filterBySeriesValue = this.properties.getProperty("FilterBySeries");
        boolean bl = filterBySeries = !StringUtils.isEmpty((String)filterBySeriesValue) && filterBySeriesValue.equalsIgnoreCase("true");
        if (filterBySeries) {
            try {
                List<String> seriesNames = Arrays.asList(this.properties.getProperty("SeriesNames").split("[;]"));
                for (String seriesName : seriesNames) {
                    seriesName = seriesName.trim();
                }
                CricketFeed.setSeriesNames(seriesNames);
            }
            catch (Exception e) {
                logger.error((Object)"No series filters provided. Reading all matches...");
                filterBySeries = false;
            }
        }
        if (!StringUtils.isEmpty((String)(seriesMappingsProperty = this.properties.getProperty("SeriesNameMappings")))) {
            String[] seriesNameMappingsArray = seriesMappingsProperty.split("[;]");
            for (int i = 0; i < seriesNameMappingsArray.length; ++i) {
                String[] seriesNameMapping = seriesNameMappingsArray[i].split(":");
                CricketFeed.seriesNameMappings.put(seriesNameMapping[0].trim(), seriesNameMapping[1].trim());
            }
        }
        CricketFeed.useMatchNumberForPrefix = !StringUtils.isEmpty((String)(useMatchNumber = this.properties.getProperty("UseMatchNumberForPrefix"))) && useMatchNumber.equalsIgnoreCase("true");
    }

    private void setTeamNameMappings() {
        String teamScoreNameProperty;
        String gamePrefixConfigProperty;
        String teamNameConfigProperty = this.properties.getProperty("TeamPrefixMappings");
        if (!StringUtils.isEmpty((String)teamNameConfigProperty)) {
            String[] teamNameMappings = teamNameConfigProperty.split("[;]");
            for (int i = 0; i < teamNameMappings.length; ++i) {
                String[] teamNameMapping = teamNameMappings[i].split(":");
                CricketFeed.teamMappings.put(teamNameMapping[0].trim(), teamNameMapping[1].trim());
            }
        }
        if (!StringUtils.isEmpty((String)(gamePrefixConfigProperty = this.properties.getProperty("GamePrefixMappings")))) {
            String[] gamePrefixConfig = gamePrefixConfigProperty.split("[;]");
            for (int i = 0; i < gamePrefixConfig.length; ++i) {
                String[] gamePrefixMapping = gamePrefixConfig[i].split(":");
                CricketFeed.gamePrefixMappings.put(gamePrefixMapping[0].trim(), gamePrefixMapping[1].trim());
            }
        }
        if (!StringUtils.isEmpty((String)(teamScoreNameProperty = this.properties.getProperty("TeamScoreNameMappings")))) {
            String[] teamScoreNamesConfig = teamScoreNameProperty.split("[;]");
            for (int i = 0; i < teamScoreNamesConfig.length; ++i) {
                String[] teamNameMapping = teamScoreNamesConfig[i].split(":");
                CricketFeed.teamScoreNameMappings.put(teamNameMapping[0].trim(), teamNameMapping[1].trim());
            }
        }
        this.mapTeamGroupModules();
    }

    private void mapTeamGroupModules() {
        String groupUsernameProperty;
        String teamGroupModuleProperty = this.properties.getProperty("GroupModules");
        if (!StringUtils.isEmpty((String)teamGroupModuleProperty)) {
            String[] teamGroupModulesConfig = teamGroupModuleProperty.split("[;]");
            for (int i = 0; i < teamGroupModulesConfig.length; ++i) {
                String[] teamModuleMapping = teamGroupModulesConfig[i].split(":");
                if (StringUtils.isEmpty((String)teamModuleMapping[1])) continue;
                try {
                    int moduleId = Integer.parseInt(teamModuleMapping[1].trim());
                    CricketFeed.groupModuleMappings.put(teamModuleMapping[0].trim(), moduleId);
                    continue;
                }
                catch (Exception e) {
                    logger.warn((Object)("Invalid group module mapping for " + teamModuleMapping[0]), (Throwable)e);
                }
            }
        }
        if (!StringUtils.isEmpty((String)(groupUsernameProperty = this.properties.getProperty("GroupUsernames")))) {
            String[] groupUsernamesConfig = groupUsernameProperty.split("[;]");
            for (int i = 0; i < groupUsernamesConfig.length; ++i) {
                String[] groupUserNameMapping = groupUsernamesConfig[i].split(":");
                CricketFeed.groupUsernameMappings.put(groupUserNameMapping[0].trim(), groupUserNameMapping[1].trim());
            }
        }
    }

    private void cleanupRemoteServer() {
        boolean cleanup;
        String turnOnCleanup = this.properties.getProperty("TurnOnRemoteCleanup");
        boolean bl = cleanup = !StringUtils.isEmpty((String)turnOnCleanup) && turnOnCleanup.equalsIgnoreCase("true");
        if (cleanup) {
            logger.info((Object)"Remote server cleanup is turned on");
            String ftpUser = this.properties.getProperty("FTPUser");
            String ftpPass = this.properties.getProperty("FTPPass");
            String ftpDir = this.properties.getProperty("FTPDir");
            String remoteServer = this.properties.getProperty("RemoteServer");
            String remoteServerTimeZone = this.properties.getProperty("RemoteServerTimeZone");
            this.checkMissingFTPProperty(ftpUser, "FTPUser");
            this.checkMissingFTPProperty(ftpPass, "FTPPass");
            this.checkMissingFTPProperty(ftpDir, "FTPDir");
            this.checkMissingFTPProperty(remoteServer, "RemoteServer");
            this.checkMissingFTPProperty(remoteServerTimeZone, "RemoteServerTimeZone");
            ArrayList<String> fileTypes = new ArrayList<String>();
            fileTypes.add(".xml");
            ArrayList<String> filesToExclude = new ArrayList<String>();
            filesToExclude.add(CricketFeed.getCalendarFileName());
            FileUtil.deleteOldRemoteFiles(remoteServer, remoteServerTimeZone, ftpDir, ftpUser, ftpPass, fileTypes, filesToExclude);
        } else {
            logger.info((Object)"Remote server cleanup is turned off");
        }
    }

    private void checkMissingFTPProperty(String name, String propertyName) {
        if (StringUtils.isEmpty((String)name)) {
            logger.error((Object)("Missing FTP property: '" + propertyName + "'. cannot perform server cleanup. Check " + CricketFeed.getConfigFile()));
        }
    }

    private void cleanupLocalDirectory() {
        ArrayList<String> fileTypes = new ArrayList<String>();
        fileTypes.add(".xml");
        ArrayList<String> filesToExclude = new ArrayList<String>();
        filesToExclude.add(CricketFeed.getCalendarFileName());
        FileUtil.deleteOldLocalFiles(CricketFeed.getLocalDir(), fileTypes, filesToExclude);
    }
}

