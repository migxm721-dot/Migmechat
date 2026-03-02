/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v2.c3p0.ComboPooledDataSource
 *  javax.ejb.CreateException
 *  javax.ejb.EJBException
 *  net.sf.ehcache.constructs.blocking.SelfPopulatingCache
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.reputation;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.projectgoth.fusion.common.ChatRoomUtils;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.Enums;
import com.projectgoth.fusion.common.IcePrxFinder;
import com.projectgoth.fusion.common.MemCachedClientWrapper;
import com.projectgoth.fusion.common.MemCachedKeySpaces;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.data.ReputationLevelData;
import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import com.projectgoth.fusion.reputation.file.ScoreSummary;
import com.projectgoth.fusion.reputation.util.DirectoryHolder;
import com.projectgoth.fusion.reputation.util.LevelTable;
import com.projectgoth.fusion.rewardsystem.RewardCentre;
import com.projectgoth.fusion.rewardsystem.triggers.ReputationLevelIncreaseTrigger;
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.Message;
import com.projectgoth.fusion.slice.ObjectNotFoundException;
import com.projectgoth.fusion.slice.UserNotificationServicePrx;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Properties;
import java.util.SortedMap;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.sql.DataSource;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;
import org.apache.log4j.Logger;

public class UpdateScoreTable {
    protected static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(UpdateScoreTable.class));
    private DataSource masterDataSource;
    private DirectoryHolder directoryHolder;
    private SelfPopulatingCache scoreCache;
    private IcePrxFinder icePrxFinder;

    public UpdateScoreTable(DataSource masterDataSource, DirectoryHolder directoryHolder, SelfPopulatingCache scoreCache, IcePrxFinder icePrxFinder) {
        this.masterDataSource = masterDataSource;
        this.directoryHolder = directoryHolder;
        this.scoreCache = scoreCache;
        this.icePrxFinder = icePrxFinder;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public int process(String runDateString) throws Exception {
        connection = null;
        start = System.currentTimeMillis();
        errorLines = 0;
        lines = 0;
        try {
            try {
                UpdateScoreTable.log.info((Object)("processing score file for time marker [" + runDateString + "] and updating score table"));
                connection = this.masterDataSource.getConnection();
                selectPreparedStatement = connection.prepareStatement("select uid.id, s.score from userid uid left outer join score s on (uid.id = s.userid) where uid.username = ?");
                updatePreparedStatement = connection.prepareStatement("update score s set s.score = ? where s.userid = ?");
                insertPreparedStatement = connection.prepareStatement("insert into score (userid,score) values (?,?)");
                levelDataTable = LevelTable.readLevelDataTable(connection);
                rs = null;
                reader = new BufferedReader(new FileReader(this.directoryHolder.getDataDirectory() + this.directoryHolder.getScoreFilename(runDateString)));
                while ((line = reader.readLine()) != null) {
                    lineParts = StringUtil.split(line, ',');
                    score = Integer.parseInt(lineParts.get(ScoreSummary.TOTAL_SCORE_INDEX));
                    username = lineParts.get(0);
                    try {
                        block25: {
                            selectPreparedStatement.setString(1, username);
                            rs = selectPreparedStatement.executeQuery();
                            if (rs.next()) break block25;
                            UpdateScoreTable.log.error((Object)("failed to update score for user [" + username + "], invalid username"));
                            var24_25 = null;
                            ** GOTO lbl84
                        }
                        hasScore = rs.getObject("score") != null;
                        userID = rs.getInt("id");
                        oldScore = rs.getInt("score");
                        newScore = oldScore + score;
                        if (hasScore) {
                            updatePreparedStatement.setInt(1, newScore);
                            updatePreparedStatement.setInt(2, userID);
                            rows = updatePreparedStatement.executeUpdate();
                            if (rows != 1) {
                                UpdateScoreTable.log.error((Object)("failed to update score for user [" + username + "], rows effected [" + rows + "]"));
                            } else {
                                MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.REPUTATION_SCORE, userID + "");
                            }
                        } else {
                            insertPreparedStatement.setInt(1, userID);
                            insertPreparedStatement.setInt(2, newScore);
                            rows = insertPreparedStatement.executeUpdate();
                            if (rows != 1) {
                                UpdateScoreTable.log.error((Object)("failed to update score for user [" + username + "], rows effected [" + rows + "]"));
                            } else {
                                MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.REPUTATION_SCORE, userID + "");
                            }
                        }
                        if (this.scoreCache != null) {
                            this.scoreCache.remove((Serializable)username);
                        }
                        oldLevelData = LevelTable.getLevelDataForScore(oldScore, levelDataTable);
                        newLevelData = LevelTable.getLevelDataForScore(newScore, levelDataTable);
                        if (oldLevelData.level.intValue() != newLevelData.level.intValue()) {
                            this.onLevelChanged(userID, username, oldLevelData, newLevelData, connection);
                        }
                        if (lines++ % 1000 != 0) ** GOTO lbl91
                        UpdateScoreTable.log.debug((Object)(lines + " lines done"));
                        ** GOTO lbl91
                    }
                    catch (SQLException e) {
                        ++errorLines;
                        UpdateScoreTable.log.error((Object)("failed to update line [" + line + "] number [" + lines + "]"), (Throwable)e);
                        var24_25 = null;
                        try {
                            if (rs == null) continue;
                            rs.close();
                        }
                        catch (SQLException e) {
                            rs = null;
                        }
                        continue;
                    }
                    {
                        catch (Throwable var23_27) {
                            var24_25 = null;
                            ** try [egrp 4[TRYBLOCK] [6 : 698->713)] { 
lbl78:
                            // 1 sources

                            if (rs == null) throw var23_27;
                            rs.close();
                            throw var23_27;
lbl81:
                            // 1 sources

                            catch (SQLException e) {
                                rs = null;
                            }
                            throw var23_27;
                        }
lbl84:
                        // 1 sources

                        ** try [egrp 4[TRYBLOCK] [6 : 698->713)] { 
lbl85:
                        // 1 sources

                        if (rs == null) continue;
                        rs.close();
lbl88:
                        // 1 sources

                        catch (SQLException e) {
                            rs = null;
                        }
                        continue;
lbl91:
                        // 2 sources

                        var24_25 = null;
                        try {}
                        catch (SQLException e) {}
                        rs = null;
                        continue;
                        if (rs == null) continue;
                        rs.close();
                    }
                }
                var27_28 = null;
            }
            catch (Exception e) {
                UpdateScoreTable.log.error((Object)"failed to update score table", (Throwable)e);
                throw e;
            }
        }
        catch (Throwable var26_30) {
            var27_29 = null;
            UpdateScoreTable.log.info((Object)("finished processing score file for time marker [" + runDateString + "] and updating score table, it took " + (System.currentTimeMillis() - start) / 1000L + " seconds"));
            if (connection == null) throw var26_30;
            connection.close();
            throw var26_30;
        }
        UpdateScoreTable.log.info((Object)("finished processing score file for time marker [" + runDateString + "] and updating score table, it took " + (System.currentTimeMillis() - start) / 1000L + " seconds"));
        if (connection == null) return errorLines;
        connection.close();
        return errorLines;
    }

    public void onLevelChanged(int userID, String username, ReputationLevelData oldLevelData, ReputationLevelData newLevelData, Connection connection) {
        if (newLevelData.chatRoomSize != oldLevelData.chatRoomSize) {
            this.updateUserOwnedRoomSize(username, newLevelData.chatRoomSize, connection);
        }
        try {
            User userBean = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
            UserData userData = userBean.loadUser(username, false, false);
            ReputationLevelIncreaseTrigger trigger = new ReputationLevelIncreaseTrigger(userData, newLevelData.level, new Timestamp(System.currentTimeMillis()), -1, RewardProgramData.TypeEnum.MANUAL.getId());
            trigger.amountDelta = 0.0;
            RewardCentre.getInstance().sendTrigger(trigger);
        }
        catch (CreateException e) {
            log.error((Object)("Unabled to create userbean. exception : " + e.getMessage()));
        }
        catch (EJBException e) {
            log.error((Object)("Unable to get user info for " + username + ". exception: " + e.getMessage()));
        }
        catch (Exception e) {
            log.error((Object)("Unknown error while notifying reward system. exception:" + e.getMessage()));
        }
        try {
            UserNotificationServicePrx unsProxy = this.icePrxFinder.getUserNotificationServiceProxy();
            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("miglevel", newLevelData.level.toString());
            unsProxy.notifyFusionUser(new Message(newLevelData.level.toString(), userID, username, Enums.NotificationTypeEnum.MIGLEVEL_INCREASE_ALERT.getType(), System.currentTimeMillis(), parameters));
        }
        catch (Exception e) {
            log.error((Object)"Unknown error while contacting UNS to generate miglevel increase alert", (Throwable)e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    private void updateUserOwnedRoomSize(String username, int chatRoomSize, Connection connection) {
        block31: {
            PreparedStatement psGetChatRooms = null;
            PreparedStatement psUpdateChatRoomSize = null;
            ResultSet rs = null;
            psGetChatRooms = connection.prepareStatement("select name from chatroom where creator = ? and type = 1 and userowned = 1");
            psUpdateChatRoomSize = connection.prepareStatement("update chatroom set maximumsize = ? where name = ?");
            psGetChatRooms.setString(1, username);
            rs = psGetChatRooms.executeQuery();
            while (rs.next()) {
                String chatRoomName = null;
                try {
                    ChatRoomPrx chatRoomPrx;
                    chatRoomName = rs.getString("name");
                    psUpdateChatRoomSize.setInt(1, chatRoomSize);
                    psUpdateChatRoomSize.setString(2, chatRoomName);
                    psUpdateChatRoomSize.executeUpdate();
                    ChatRoomUtils.invalidateChatRoomCache(chatRoomName);
                    if (this.icePrxFinder == null || (chatRoomPrx = this.icePrxFinder.findChatRoomPrx(chatRoomName)) == null) continue;
                    chatRoomPrx.setMaximumSize(chatRoomSize);
                }
                catch (ObjectNotFoundException e) {
                }
                catch (Exception e) {
                    log.error((Object)("failed to update chat room size for user [" + username + "], room [" + chatRoomName + "]"), (Throwable)e);
                }
            }
            Object var10_12 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e2) {
                rs = null;
            }
            try {
                if (psUpdateChatRoomSize != null) {
                    psUpdateChatRoomSize.close();
                }
            }
            catch (SQLException e2) {
                psUpdateChatRoomSize = null;
            }
            try {
                if (psGetChatRooms != null) {
                    psGetChatRooms.close();
                }
                break block31;
            }
            catch (SQLException e2) {
                psGetChatRooms = null;
            }
            break block31;
            {
                catch (Exception e) {
                    log.error((Object)("failed to update chat room size for user [" + username + "]"), (Throwable)e);
                    Object var10_13 = null;
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                    }
                    catch (SQLException e2) {
                        rs = null;
                    }
                    try {
                        if (psUpdateChatRoomSize != null) {
                            psUpdateChatRoomSize.close();
                        }
                    }
                    catch (SQLException e2) {
                        psUpdateChatRoomSize = null;
                    }
                    try {
                        if (psGetChatRooms != null) {
                            psGetChatRooms.close();
                        }
                        break block31;
                    }
                    catch (SQLException e2) {
                        psGetChatRooms = null;
                    }
                }
            }
            catch (Throwable throwable) {
                Object var10_14 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (psUpdateChatRoomSize != null) {
                        psUpdateChatRoomSize.close();
                    }
                }
                catch (SQLException e2) {
                    psUpdateChatRoomSize = null;
                }
                try {
                    if (psGetChatRooms != null) {
                        psGetChatRooms.close();
                    }
                }
                catch (SQLException e2) {
                    psGetChatRooms = null;
                }
                throw throwable;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Properties databaseProperties = new Properties();
        databaseProperties.load(new FileInputStream(System.getProperty("config.dir") + "database.properties"));
        ComboPooledDataSource datasource = new ComboPooledDataSource();
        log.info((Object)("rep jdbc url: " + databaseProperties.getProperty("database.jdbcUrl")));
        datasource.setJdbcUrl(databaseProperties.getProperty("database.jdbcUrl"));
        datasource.setUser(databaseProperties.getProperty("database.username"));
        datasource.setPassword(databaseProperties.getProperty("database.password"));
        datasource.setDriverClass(databaseProperties.getProperty("database.driver"));
        datasource.setMinPoolSize(1);
        datasource.setAcquireIncrement(1);
        datasource.setAcquireRetryAttempts(2);
        datasource.setMaxPoolSize(1);
        UpdateScoreTable update = new UpdateScoreTable((DataSource)datasource, new DirectoryHolder("/reputation/", "/reputation/scratch/", "/reputation/dump/"), null, null);
        SortedMap<Integer, ReputationLevelData> levelDataTable = LevelTable.readLevelDataTable(datasource.getConnection());
        ReputationLevelData oldLevelData = LevelTable.getLevelDataForScore(((ReputationLevelData)levelDataTable.get((Object)Integer.valueOf((int)Integer.parseInt((String)args[2])))).score, levelDataTable);
        ReputationLevelData newLevelData = LevelTable.getLevelDataForScore(((ReputationLevelData)levelDataTable.get((Object)Integer.valueOf((int)Integer.parseInt((String)args[3])))).score, levelDataTable);
        update.onLevelChanged(Integer.parseInt(args[0]), args[1], oldLevelData, newLevelData, datasource.getConnection());
    }
}

