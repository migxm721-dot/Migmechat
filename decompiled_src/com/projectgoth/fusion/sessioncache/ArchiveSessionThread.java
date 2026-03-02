/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.sessioncache;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.dao.SessionArchiveDAO;
import com.projectgoth.fusion.data.UserReputationScoreAndLevelData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import com.projectgoth.fusion.sessioncache.SessionArchiveDetail;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.ReputationServicePrx;
import com.projectgoth.fusion.slice.ScoreAndLevel;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ArchiveSessionThread
extends Thread {
    private Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ArchiveSessionThread.class));
    private static final int MAX_BATCH_RETRY_COUNT = 10;
    private BlockingQueue<SessionArchiveDetail> sessions;
    private SessionArchiveDAO sessionArchiveDAO;
    private int batchSize = 20;
    private final int maxWaitTimeInSeconds;
    private boolean running = true;
    private ReputationServicePrx reputationServicePrx;
    private AtomicInteger abortedBatches;
    private int maxQueueSize = 0;

    public ArchiveSessionThread(SessionArchiveDAO sessionArchiveDAO, BlockingQueue<SessionArchiveDetail> sessions, int batchSize, int maxWaitTimeInSeconds, ReputationServicePrx reputationServicePrx, AtomicInteger abortedBatches, int maxQueueSize) {
        this.sessions = sessions;
        this.sessionArchiveDAO = sessionArchiveDAO;
        this.batchSize = batchSize;
        this.maxWaitTimeInSeconds = maxWaitTimeInSeconds;
        this.reputationServicePrx = reputationServicePrx;
        this.abortedBatches = abortedBatches;
        this.maxQueueSize = maxQueueSize;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    void populateMigLevelForList(List<SessionArchiveDetail> list) {
        block17: {
            try {
                if (SystemProperty.getBool(SystemPropertyEntities.SessionCacheSettings.USE_REPUTATION_SERVICE_FOR_SCORE_RETRIEVAL)) {
                    int[] userIDs = new int[list.size()];
                    int index = 0;
                    for (SessionArchiveDetail listDetail : list) {
                        userIDs[index++] = listDetail.getUserID();
                    }
                    if (this.log.isDebugEnabled()) {
                        this.log.debug((Object)("sending " + userIDs.length + " user ids to reputation service"));
                    }
                    ScoreAndLevel[] scoresAndLevels = this.reputationServicePrx.getUserScoreAndLevels(userIDs);
                    if (this.log.isDebugEnabled()) {
                        if (scoresAndLevels != null) {
                            this.log.debug((Object)("found " + scoresAndLevels.length + " ScoresAndLevel from reputationServicePrx"));
                        } else {
                            this.log.debug((Object)"received null ScoresAndLevel from reputationServicePrx");
                        }
                    }
                    if (scoresAndLevels != null && scoresAndLevels.length == list.size()) {
                        index = 0;
                        for (SessionArchiveDetail listDetail : list) {
                            listDetail.setMigLevel(scoresAndLevels[index].level);
                            listDetail.setMigLevelScore(scoresAndLevels[index].score);
                            ++index;
                        }
                        break block17;
                    }
                    throw new FusionException("Unable to retrieve reputation score and levels from reputationService");
                }
                try {
                    TreeSet<Integer> userIDSet = new TreeSet<Integer>();
                    for (SessionArchiveDetail listDetail : list) {
                        userIDSet.add(listDetail.getUserID());
                    }
                    User userBean = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
                    Map map = userBean.getReputationScoreAndLevelForUsers(new LinkedList(userIDSet), null);
                    Iterator<SessionArchiveDetail> i$ = list.iterator();
                    while (i$.hasNext()) {
                        SessionArchiveDetail listDetail;
                        UserReputationScoreAndLevelData scoreData = (UserReputationScoreAndLevelData)map.get((listDetail = i$.next()).getUserID());
                        listDetail.setMigLevel(scoreData == null ? 1 : scoreData.level);
                        listDetail.setMigLevelScore(scoreData == null ? 0 : scoreData.score);
                    }
                }
                catch (CreateException e) {
                    throw new FusionException("Unable to create userBean for invocation of getReputationScoreAndLevelForUsers(): " + e.getMessage());
                }
                catch (RemoteException e) {
                    throw new FusionException("Unable to invoke getReputationScoreAndLevelForUsers(): " + e.getMessage());
                }
                catch (EJBException e) {
                    throw new FusionException("EJBException caught while invoking getReputationScoreAndLevelForUsers(): " + e.getMessage());
                }
            }
            catch (FusionException e) {
                this.log.error((Object)"failed to find mig levels for batch, setting all to level 1 and score 0", (Throwable)((Object)e));
                for (SessionArchiveDetail listDetail : list) {
                    listDetail.setMigLevel(1);
                    listDetail.setMigLevelScore(0);
                }
            }
        }
    }

    private boolean addDetailToList(List<SessionArchiveDetail> list, SessionArchiveDetail detail) {
        ArrayList<String> errors = null;
        if (detail.getRemoteAddress() != null && detail.getRemoteAddress().length() > 45) {
            if (errors == null) {
                errors = new ArrayList<String>();
            }
            String truncatedValue = detail.getRemoteAddress().substring(0, 45);
            errors.add(String.format("remoteAddress longer than limit %d, value '%s' truncated to '%s'", 45, detail.getRemoteAddress(), truncatedValue));
            detail.setRemoteAddress(truncatedValue);
        } else if (detail.getMobileDevice() != null && detail.getMobileDevice().length() > 500) {
            if (errors == null) {
                errors = new ArrayList();
            }
            String truncatedValue = detail.getMobileDevice().substring(0, 500);
            errors.add(String.format("mobileDevice longer than limit %d, value '%s' truncated to '%s'", 500, detail.getMobileDevice(), truncatedValue));
            detail.setMobileDevice(truncatedValue);
        }
        detail.setLanguage(detail.getLanguage().replaceAll("[\n\r|]", " "));
        detail.setMobileDevice(detail.getMobileDevice().replaceAll("[\n\r|]", " "));
        detail.setRemoteAddress(detail.getRemoteAddress().replaceAll("[\n\r|]", " "));
        list.add(detail);
        if (errors != null) {
            this.log.error((Object)String.format("invalid session archive detail truncated before logging to sessionarchive, %d error, %s, truncated detail=%s", errors.size(), StringUtil.join(errors, ", "), detail));
            return false;
        }
        return true;
    }

    @Override
    public void run() {
        ArrayList<SessionArchiveDetail> list = new ArrayList<SessionArchiveDetail>(this.batchSize);
        SessionArchiveDetail detail = null;
        while (this.running) {
            try {
                long startTime;
                boolean retry = false;
                while (list.size() < this.batchSize && (detail = (SessionArchiveDetail)this.sessions.poll()) != null) {
                    this.addDetailToList(list, detail);
                }
                for (long localMaxWaitTime = (long)(this.maxWaitTimeInSeconds * 1000); list.size() < this.batchSize && localMaxWaitTime > 0L; localMaxWaitTime -= System.currentTimeMillis() - startTime) {
                    startTime = System.currentTimeMillis();
                    detail = this.sessions.poll(localMaxWaitTime, TimeUnit.MILLISECONDS);
                    if (detail == null) continue;
                    this.addDetailToList(list, detail);
                }
                if (list.isEmpty()) continue;
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)("archive batch of size " + list.size()));
                }
                this.populateMigLevelForList(list);
                int retryCount = 1;
                int waitCount = 1;
                do {
                    try {
                        this.sessionArchiveDAO.bulkArchiveSession(list);
                        list.clear();
                        retry = false;
                    }
                    catch (Throwable t) {
                        this.log.error((Object)String.format("failed to persist session archive batch, we have a resource problem, retrying (%d) this batch until it succeeds", retryCount), t);
                        ++retryCount;
                        ArchiveSessionThread.sleep(500 * waitCount);
                        retry = true;
                        if ((double)this.sessions.remainingCapacity() > 0.75 * (double)this.maxQueueSize) {
                            ++waitCount;
                            continue;
                        }
                        if (!((double)this.sessions.remainingCapacity() < 0.25 * (double)this.maxQueueSize)) continue;
                        waitCount = 1;
                    }
                } while (retry && retryCount < 10);
                if (retryCount < 10) continue;
                this.log.error((Object)("failed to persist batch after 10 tries, aborting batch of size " + list.size()));
                for (int i = 0; i < list.size(); ++i) {
                    this.log.error((Object)String.format(" aborted entry %02d: %s", i, list.get(i)));
                }
                list.clear();
                this.abortedBatches.incrementAndGet();
            }
            catch (Throwable t) {
                this.log.error((Object)"failed to archive sessions", t);
            }
        }
        this.log.warn((Object)"Exiting run() method...");
    }
}

