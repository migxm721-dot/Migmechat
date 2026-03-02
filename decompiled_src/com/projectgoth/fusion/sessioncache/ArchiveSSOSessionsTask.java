/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.springframework.transaction.TransactionStatus
 *  org.springframework.transaction.support.TransactionCallback
 *  org.springframework.transaction.support.TransactionCallbackWithoutResult
 *  org.springframework.transaction.support.TransactionTemplate
 *  redis.clients.jedis.Jedis
 *  redis.clients.jedis.PipelineBlock
 */
package com.projectgoth.fusion.sessioncache;

import com.projectgoth.fusion.clientsession.SSOLogin;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.Redis;
import com.projectgoth.fusion.dao.SessionArchiveDAO;
import com.projectgoth.fusion.dao.UserDAO;
import com.projectgoth.fusion.restapi.data.SSOSessionMetrics;
import com.projectgoth.fusion.restapi.util.RedisDataUtil;
import com.projectgoth.fusion.sessioncache.SessionCacheI;
import com.projectgoth.fusion.slice.FusionException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.PipelineBlock;

public class ArchiveSSOSessionsTask
extends TimerTask {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ArchiveSSOSessionsTask.class));
    private SessionCacheI sessionCacheI;
    private SessionArchiveDAO sessionArchiveDAO;
    private UserDAO userDAO;
    private int batchSize;
    private TransactionTemplate transactionTemplate;
    public static final String SSO_ARCHIVES_TABLE_NAME = "ssosessionarchives";

    public ArchiveSSOSessionsTask(SessionCacheI sessionCacheI, SessionArchiveDAO sessionArchiveDAO, UserDAO userDAO, int batchSize, TransactionTemplate transactionTemplate) {
        this.sessionCacheI = sessionCacheI;
        this.sessionArchiveDAO = sessionArchiveDAO;
        this.userDAO = userDAO;
        this.batchSize = batchSize;
        this.transactionTemplate = transactionTemplate;
    }

    public static String getDailyTableName() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return SSO_ARCHIVES_TABLE_NAME + sdf.format(date);
    }

    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(5, days);
        return cal.getTime();
    }

    public static String getNextDayDailyTableName() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return SSO_ARCHIVES_TABLE_NAME + sdf.format(ArchiveSSOSessionsTask.addDays(date, 1));
    }

    private void createTableIfNotExists() {
        this.transactionTemplate.execute((TransactionCallback)new TransactionCallbackWithoutResult(){

            protected void doInTransactionWithoutResult(TransactionStatus status) {
                ArchiveSSOSessionsTask.this.sessionArchiveDAO.createSSOSessionMetricsTableIfNotExists();
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int archiveSessions() {
        int numSessionsArchived = 0;
        Jedis slaveInst = null;
        try {
            slaveInst = Redis.getGamesSlaveInstance();
            Long resultLong = slaveInst.zcard("SSOM:INDEX");
            if (resultLong == null) {
                throw new FusionException("archiveSessions-zcard returns null");
            }
            int totalSessions = resultLong.intValue();
            log.info((Object)String.format("Found %d entries in SSO sessions index [%s]", totalSessions, "SSOM:INDEX"));
            if (totalSessions == 0) {
                log.info((Object)"No session entries found in SSO sessions index");
                int n = 0;
                return n;
            }
            int numLiveSessions = 0;
            int numExpiredSessions = 0;
            int numPending = totalSessions;
            for (int index = 0; index < totalSessions; index += this.batchSize) {
                Set ssoMetricKeys = slaveInst.zrange("SSOM:INDEX", (long)index, (long)(index + this.batchSize));
                LinkedList<String> sidOfScannedSessions = new LinkedList<String>();
                Iterator it = ssoMetricKeys.iterator();
                while (it.hasNext()) {
                    sidOfScannedSessions.add(RedisDataUtil.getSessionIDFromSSOSessionMetricKey((String)it.next()));
                }
                Map<String, String> memcacheData = SSOLogin.getMultipleLoginDataStrFromMemcache(sidOfScannedSessions.toArray(new String[ssoMetricKeys.size()]));
                String[] memcacheDataArr = memcacheData.keySet().toArray(new String[sidOfScannedSessions.size()]);
                for (int i = 0; i < memcacheDataArr.length; ++i) {
                    if (memcacheData.get(memcacheDataArr[i]) == null) continue;
                    sidOfScannedSessions.remove(memcacheDataArr[i]);
                }
                log.info((Object)String.format("SSO Sessions Breakdown: Total [%d], Live [%d], Expired [%d], Pending Scan [%d]", totalSessions, numLiveSessions += memcacheDataArr.length - sidOfScannedSessions.size(), numExpiredSessions += sidOfScannedSessions.size(), numPending -= ssoMetricKeys.size()));
                final LinkedList<String> ssoMetricKeysForExpiredSessions = new LinkedList<String>();
                for (String key : ssoMetricKeys) {
                    if (!sidOfScannedSessions.contains(RedisDataUtil.getSessionIDFromSSOSessionMetricKey(key))) continue;
                    ssoMetricKeysForExpiredSessions.add(key);
                }
                final Iterator iter = ssoMetricKeysForExpiredSessions.iterator();
                List pipelineResult = slaveInst.pipelined(new PipelineBlock(){

                    public void execute() {
                        while (iter.hasNext()) {
                            String redisKey = (String)iter.next();
                            this.hgetAll(redisKey);
                        }
                    }
                });
                final LinkedList<SSOSessionMetrics> metricsToArchive = new LinkedList<SSOSessionMetrics>();
                for (int i = 0; i < ssoMetricKeysForExpiredSessions.size(); ++i) {
                    String redisKey = (String)ssoMetricKeysForExpiredSessions.get(i);
                    try {
                        Map map = (Map)pipelineResult.get(i);
                        Long sessionStartTime = Long.parseLong((String)map.get("sessionStartTime"));
                        SSOSessionMetrics metrics = SSOSessionMetrics.fromMap(RedisDataUtil.getSessionIDFromSSOSessionMetricKey(redisKey), RedisDataUtil.getViewFromSSOSessionMetricKey(redisKey), sessionStartTime, map);
                        metricsToArchive.add(metrics);
                        continue;
                    }
                    catch (Exception e) {
                        log.warn((Object)String.format("Unable to archive session [%s]", redisKey), (Throwable)e);
                    }
                }
                this.transactionTemplate.execute((TransactionCallback)new TransactionCallbackWithoutResult(){

                    /*
                     * WARNING - Removed try catching itself - possible behaviour change.
                     */
                    protected void doInTransactionWithoutResult(TransactionStatus status) {
                        Jedis masterInst = null;
                        try {
                            ArchiveSSOSessionsTask.this.sessionArchiveDAO.bulkArchiveSSOSessionMetrics(metricsToArchive);
                            masterInst = Redis.getGamesMasterInstance();
                            final Iterator iterx = ssoMetricKeysForExpiredSessions.iterator();
                            masterInst.pipelined(new PipelineBlock(){

                                public void execute() {
                                    while (iterx.hasNext()) {
                                        String redisKey = (String)iterx.next();
                                        this.del(redisKey);
                                        this.zrem("SSOM:INDEX", new String[]{redisKey});
                                        log.debug((Object)String.format("Session Expired [%s]", redisKey));
                                    }
                                }
                            });
                            Redis.disconnect(masterInst, log);
                        }
                        catch (Exception e) {
                            try {
                                log.error((Object)"Unable to persist SSO session metrics", (Throwable)e);
                                status.setRollbackOnly();
                            }
                            catch (Throwable throwable) {
                                Redis.disconnect(masterInst, log);
                                throw throwable;
                            }
                            Redis.disconnect(masterInst, log);
                        }
                        Redis.disconnect(masterInst, log);
                    }
                });
                numSessionsArchived += numExpiredSessions;
            }
        }
        catch (Exception e) {
            log.error((Object)String.format("Unable to archive SSO session %s", e.getMessage()), (Throwable)e);
        }
        finally {
            Redis.disconnect(slaveInst, log);
        }
        return numSessionsArchived;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void run() {
        try {
            if (this.sessionCacheI.isSsoSessionArchiverTaskRunning()) {
                log.warn((Object)"Another SSOSessionArchiverTask is already running.");
                return;
            }
            this.sessionCacheI.setSsoSessionArchiverTaskRunning(true);
            this.createTableIfNotExists();
            log.info((Object)"Archiving SSO sessions metrics...");
            long now = System.currentTimeMillis();
            int numSessionsArchived = this.archiveSessions();
            log.info((Object)String.format("Done archiving [%d] SSO sessions in [%.2f] seconds", numSessionsArchived, (double)(System.currentTimeMillis() - now) / 1000.0));
        }
        catch (Exception e) {
            log.error((Object)"Unable to persist unique summary totals", (Throwable)e);
        }
        finally {
            this.sessionCacheI.setSsoSessionArchiverTaskRunning(false);
        }
    }
}

