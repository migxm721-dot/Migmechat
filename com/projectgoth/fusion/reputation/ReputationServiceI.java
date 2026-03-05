/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 *  com.danga.MemCached.MemCachedClient
 *  net.sf.ehcache.CacheManager
 *  net.sf.ehcache.Ehcache
 *  net.sf.ehcache.Element
 *  net.sf.ehcache.constructs.blocking.CacheEntryFactory
 *  net.sf.ehcache.constructs.blocking.SelfPopulatingCache
 *  org.apache.log4j.Logger
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.util.StringUtils
 */
package com.projectgoth.fusion.reputation;

import Ice.Current;
import com.danga.MemCached.MemCachedClient;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.DateTimeUtils;
import com.projectgoth.fusion.common.ExternalizedQueries;
import com.projectgoth.fusion.common.IcePrxFinder;
import com.projectgoth.fusion.common.MemCachedUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.dao.ReputationDAO;
import com.projectgoth.fusion.reputation.GatherRawData;
import com.projectgoth.fusion.reputation.UpdateScoreTable;
import com.projectgoth.fusion.reputation.cache.ReputationLastRan;
import com.projectgoth.fusion.reputation.cache.ScoreCacheEntryFactory;
import com.projectgoth.fusion.reputation.file.CombineSummarizedResults;
import com.projectgoth.fusion.reputation.file.MergeSummaryFiles;
import com.projectgoth.fusion.reputation.file.ScoreFinalSummary;
import com.projectgoth.fusion.reputation.file.ScoreSummary;
import com.projectgoth.fusion.reputation.file.SortBigFile;
import com.projectgoth.fusion.reputation.file.SummarizeSortedAccountEntryFile;
import com.projectgoth.fusion.reputation.file.SummarizeSortedPhoneCallFile;
import com.projectgoth.fusion.reputation.file.SummarizeSortedSessionArchiveFile;
import com.projectgoth.fusion.reputation.file.SummarizeSortedVirtualGiftByReceiverFile;
import com.projectgoth.fusion.reputation.file.SummarizeSortedVirtualGiftBySenderFile;
import com.projectgoth.fusion.reputation.file.comparator.AccountEntryFileEntryComparator;
import com.projectgoth.fusion.reputation.file.comparator.AccountEntryStringListComparator;
import com.projectgoth.fusion.reputation.file.comparator.PhoneCallFileEntryComparator;
import com.projectgoth.fusion.reputation.file.comparator.PhoneCallStringListComparator;
import com.projectgoth.fusion.reputation.file.comparator.ScoreSummaryByScoreFileEntryComparator;
import com.projectgoth.fusion.reputation.file.comparator.ScoreSummaryByScoreStringListComparator;
import com.projectgoth.fusion.reputation.file.comparator.ScoreSummaryFileEntryComparator;
import com.projectgoth.fusion.reputation.file.comparator.ScoreSummaryStringListComparator;
import com.projectgoth.fusion.reputation.file.comparator.SessionArchiveFileEntryComparator;
import com.projectgoth.fusion.reputation.file.comparator.SessionArchiveStringListComparator;
import com.projectgoth.fusion.reputation.file.comparator.VirtualGiftReceivedByReceiverFileEntryComparator;
import com.projectgoth.fusion.reputation.file.comparator.VirtualGiftReceivedByReceiverStringListComparator;
import com.projectgoth.fusion.reputation.file.comparator.VirtualGiftReceivedBySenderFileEntryComparator;
import com.projectgoth.fusion.reputation.file.comparator.VirtualGiftReceivedBySenderStringListComparator;
import com.projectgoth.fusion.reputation.util.DailyPeriodBoundaryTask;
import com.projectgoth.fusion.reputation.util.DailyTableTask;
import com.projectgoth.fusion.reputation.util.DirectoryHolder;
import com.projectgoth.fusion.reputation.util.DirectoryUtils;
import com.projectgoth.fusion.reputation.util.FileLocation;
import com.projectgoth.fusion.reputation.util.LevelTable;
import com.projectgoth.fusion.reputation.util.stats.LevelDistribution;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.ScoreAndLevel;
import com.projectgoth.fusion.slice._ReputationServiceDisp;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import javax.sql.DataSource;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ReputationServiceI
extends _ReputationServiceDisp
implements InitializingBean {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ReputationServiceI.class));
    private static MemCachedClient commonMemCached = MemCachedUtils.getMemCachedClient(MemCachedUtils.Instance.broadcastList);
    public static SimpleDateFormat FILE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final int SESSION_ARCHIVE_LAST_LINE = 0;
    private static final int ACCOUNT_ENTRY_LAST_LINE = 1;
    private static final int VIRTUAL_GIFT_RECEIVED_LAST_LINE = 2;
    private static final int VIRTUAL_GIFT_SENT_LAST_LINE = 3;
    private static final int PHONE_CALL_LAST_LINE = 4;
    private static final int[] LAST_LINES = new int[]{0, 1, 2, 3, 4};
    private String dataDirectory;
    private String scratchDirectory;
    private String dumpDirectory;
    private DirectoryHolder directoryHolder;
    private SortedMap<Integer, Integer> levelTable;
    private String runDateString;
    private Date endOfPeriodObserving = DateTimeUtils.midnightToday();
    private DataSource masterDataSource;
    private DataSource repuDataSource;
    private DataSource repuODSDataSource;
    private DataSource olapDataSource;
    private ReputationDAO reputationDAO;
    private int processing = 0;
    private boolean updateScoreTable = true;
    private CacheManager cacheManager;
    private SelfPopulatingCache scoreCache;
    private IcePrxFinder icePrxFinder;

    public ReputationServiceI() throws IOException {
        this.scheduleTasks();
        this.initializeCacheManager();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void initializeCacheManager() throws IOException {
        FileInputStream fis = new FileInputStream(new File(System.getProperty("config.dir", "/usr/fusion/etc/") + "ehcache.repu.xml").getAbsolutePath());
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

    private void scheduleTasks() {
        Timer dailyTaskTimer = new Timer(true);
        dailyTaskTimer.schedule((TimerTask)new DailyTableTask(commonMemCached), DateTimeUtils.midnightTomorrow(), 86400000L);
        dailyTaskTimer.schedule((TimerTask)new DailyPeriodBoundaryTask(this), DateTimeUtils.midnightTomorrow(), 86400000L);
    }

    public void afterPropertiesSet() throws Exception {
        this.directoryHolder = new DirectoryHolder(this.dataDirectory, this.scratchDirectory, this.dumpDirectory);
        this.levelTable = this.reputationDAO.readLevelTable();
        for (Integer key : this.levelTable.keySet()) {
            log.info((Object)(key + " -> " + this.levelTable.get(key)));
        }
        this.scoreCache = new SelfPopulatingCache((Ehcache)this.cacheManager.getCache("scoreCache"), (CacheEntryFactory)new ScoreCacheEntryFactory(this.reputationDAO));
    }

    void shutdown() {
    }

    private void updateLastIds(String[] lastLines) {
        int id;
        int id2;
        List<String> lastLineParts = StringUtil.split(lastLines[0], '|');
        if (lastLineParts.size() >= 26) {
            id2 = Integer.parseInt(lastLineParts.get(0));
            log.info((Object)("setting last id for session archive to " + id2));
            ReputationLastRan.setSessionArchiveLastId(commonMemCached, id2);
        } else {
            log.error((Object)("last line processed does not look like a valid sessionarchive line! [" + lastLines[0] + "]"));
        }
        lastLineParts = StringUtil.split(lastLines[1], '|');
        if (lastLineParts.size() >= 13) {
            id2 = Integer.parseInt(lastLineParts.get(0));
            log.info((Object)("setting last id for account entry to " + id2));
            ReputationLastRan.setAccountEntryLastId(commonMemCached, id2);
        } else {
            log.error((Object)("last line processed does not look like a valid accountentry line! [" + lastLines[1] + "]"));
        }
        int virtualGiftId = 0;
        lastLineParts = StringUtil.split(lastLines[2], '|');
        if (lastLineParts.size() >= 8) {
            virtualGiftId = Integer.parseInt(lastLineParts.get(0));
        } else {
            log.error((Object)("last line processed does not look like a valid virtualgift line! [" + lastLines[2] + "]"));
        }
        lastLineParts = StringUtil.split(lastLines[3], '|');
        if (lastLineParts.size() >= 8) {
            id = Integer.parseInt(lastLineParts.get(0));
            if (id > virtualGiftId) {
                virtualGiftId = id;
            }
        } else {
            log.error((Object)("last line processed does not look like a valid virtualgiftsent line! [" + lastLines[3] + "]"));
        }
        if (virtualGiftId > 0) {
            log.info((Object)("setting last id for virtual gift to " + virtualGiftId));
            ReputationLastRan.setVirtualGiftLastId(commonMemCached, virtualGiftId);
        }
        if ((lastLineParts = StringUtil.split(lastLines[4], '|')).size() >= 4) {
            id = Integer.parseInt(lastLineParts.get(0));
            log.info((Object)("setting last id for phone call to " + id));
            ReputationLastRan.setPhoneCallLastId(commonMemCached, id);
        } else {
            log.error((Object)("last line processed does not look like a valid phone call line! [" + lastLines[4] + "]"));
        }
    }

    private String[] sortData(String runDateString, boolean updateLastLines) throws Exception {
        log.info((Object)("sorting data for " + runDateString + " updateLastLines [" + updateLastLines + "]"));
        String[] lastLines = new String[LAST_LINES.length];
        SortBigFile sort = new SortBigFile(this.directoryHolder);
        lastLines[0] = sort.go(new FileLocation(this.directoryHolder.getDumpDirectory(), "sessionarchive." + runDateString + ".csv"), new FileLocation(this.directoryHolder.getDataDirectory(), "sessionarchive." + runDateString + ".csv"), new SessionArchiveFileEntryComparator(), new SessionArchiveStringListComparator(), 26, '|');
        lastLines[1] = sort.go(new FileLocation(this.directoryHolder.getDumpDirectory(), "accountentry." + runDateString + ".csv"), new FileLocation(this.directoryHolder.getDataDirectory(), "accountentry." + runDateString + ".csv"), new AccountEntryFileEntryComparator(), new AccountEntryStringListComparator(), 13, '|');
        lastLines[2] = sort.go(new FileLocation(this.directoryHolder.getDumpDirectory(), "virtualgiftreceived." + runDateString + ".csv"), new FileLocation(this.directoryHolder.getDataDirectory(), "virtualgiftreceived." + runDateString + ".csv"), new VirtualGiftReceivedByReceiverFileEntryComparator(), new VirtualGiftReceivedByReceiverStringListComparator(), 8, '|', false);
        lastLines[3] = sort.go(new FileLocation(this.directoryHolder.getDumpDirectory(), "virtualgiftreceived." + runDateString + ".csv"), new FileLocation(this.directoryHolder.getDataDirectory(), "virtualgiftsent." + runDateString + ".csv"), new VirtualGiftReceivedBySenderFileEntryComparator(), new VirtualGiftReceivedBySenderStringListComparator(), 8, '|', false);
        lastLines[4] = sort.go(new FileLocation(this.directoryHolder.getDumpDirectory(), "phonecall." + runDateString + ".csv"), new FileLocation(this.directoryHolder.getDataDirectory(), "phonecall." + runDateString + ".csv"), new PhoneCallFileEntryComparator(), new PhoneCallStringListComparator(), 8, '|');
        if (updateLastLines) {
            this.updateLastIds(lastLines);
        }
        return lastLines;
    }

    private void cleanupIntermediateFiles(String runDateString) {
        File file = new File(this.directoryHolder.getDataDirectory() + this.directoryHolder.getMergedSessionArchiveAccountEntryFilename(runDateString));
        file.delete();
        file = new File(this.directoryHolder.getDataDirectory() + this.directoryHolder.getMergedVirtualGiftsFilename(runDateString));
        file.delete();
        file = new File(this.directoryHolder.getDataDirectory() + this.directoryHolder.getMergedSessionArchiveAccountEntryVirtualGiftFilename(runDateString));
        file.delete();
    }

    private SortedMap<Integer, Integer> processData(String runDateString) throws Exception {
        log.info((Object)("processing data for " + runDateString));
        SummarizeSortedSessionArchiveFile summarizeSAFile = new SummarizeSortedSessionArchiveFile(this.directoryHolder);
        summarizeSAFile.go("sessionarchive." + runDateString + ".csv.sorted");
        SummarizeSortedAccountEntryFile summarizeAEFile = new SummarizeSortedAccountEntryFile(this.directoryHolder);
        summarizeAEFile.go("accountentry." + runDateString + ".csv.sorted");
        SummarizeSortedVirtualGiftByReceiverFile summarizeVGRFile = new SummarizeSortedVirtualGiftByReceiverFile(this.directoryHolder);
        summarizeVGRFile.go("virtualgiftreceived." + runDateString + ".csv.sorted");
        SummarizeSortedVirtualGiftBySenderFile summarizeVGSFile = new SummarizeSortedVirtualGiftBySenderFile(this.directoryHolder);
        summarizeVGSFile.go("virtualgiftsent." + runDateString + ".csv.sorted");
        SummarizeSortedPhoneCallFile summarizePCFile = new SummarizeSortedPhoneCallFile(this.directoryHolder);
        summarizePCFile.go("phonecall." + runDateString + ".csv.sorted");
        MergeSummaryFiles merger = new MergeSummaryFiles(this.directoryHolder);
        merger.mergeJoinFiles(this.directoryHolder.getMergedSessionArchiveAccountEntryFilename(runDateString), "sessionarchive." + runDateString + ".csv.sorted.processed", 0, 4, "accountentry." + runDateString + ".csv.sorted.processed", 0, 3);
        merger.mergeJoinFiles(this.directoryHolder.getMergedVirtualGiftsFilename(runDateString), "virtualgiftreceived." + runDateString + ".csv.sorted.processed", 0, 1, "virtualgiftsent." + runDateString + ".csv.sorted.processed", 0, 1);
        merger.mergeJoinFiles(this.directoryHolder.getMergedSessionArchiveAccountEntryVirtualGiftFilename(runDateString), this.directoryHolder.getMergedSessionArchiveAccountEntryFilename(runDateString), 0, 7, this.directoryHolder.getMergedVirtualGiftsFilename(runDateString), 0, 2);
        merger.mergeJoinFiles(this.directoryHolder.getFinalMergedResultsFilename(runDateString), this.directoryHolder.getMergedSessionArchiveAccountEntryVirtualGiftFilename(runDateString), 0, 9, this.directoryHolder.getPhoneCallSummaryFilename(runDateString), 0, 1);
        ScoreFinalSummary scorer = new ScoreFinalSummary(this.directoryHolder);
        SortedMap<Integer, Integer> scoreDistribution = scorer.scoreFile(this.directoryHolder.getFinalMergedResultsFilename(runDateString));
        try {
            this.cleanupIntermediateFiles(runDateString);
            SortBigFile sort = new SortBigFile(this.directoryHolder);
            sort.go(new FileLocation(this.directoryHolder.getDataDirectory(), this.directoryHolder.getScoreFilename(runDateString)), new FileLocation(this.directoryHolder.getDataDirectory(), this.directoryHolder.getScoreFilename(runDateString)), new ScoreSummaryByScoreFileEntryComparator(), new ScoreSummaryByScoreStringListComparator(), ScoreSummary.EXPECTED_FIELD_COUNT, ',');
        }
        catch (Exception e) {
            log.error((Object)"failed to sort final score file", (Throwable)e);
        }
        return scoreDistribution;
    }

    private SortedMap<Integer, Integer> gatherAndProcessRunDateString(String runDateString, boolean updateScoreTable) throws FusionException {
        SortedMap<Integer, Integer> scoreDistribution = null;
        try {
            GatherRawData gather = new GatherRawData(this.repuDataSource, this.repuODSDataSource, this.endOfPeriodObserving);
            gather.gather(runDateString);
            this.sortData(runDateString, true);
            scoreDistribution = this.processData(runDateString);
            if (updateScoreTable) {
                UpdateScoreTable updater = new UpdateScoreTable(this.masterDataSource, this.directoryHolder, this.scoreCache, this.icePrxFinder);
                updater.process(runDateString);
            }
        }
        catch (Exception e) {
            log.error((Object)"failed to process", (Throwable)e);
            throw new FusionException("failed to process");
        }
        return scoreDistribution;
    }

    private void processDailyLevelDistribution() {
        try {
            LevelDistribution levelDistributionCalc = new LevelDistribution(this.olapDataSource, this.levelTable);
            SortedMap<Integer, Integer> levelDistribution = levelDistributionCalc.getDistribution();
            this.reputationDAO.updateDailyLevelDistribution(new Date(), levelDistribution);
        }
        catch (SQLException e) {
            log.error((Object)"failed to process daily level distribution", (Throwable)e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void gatherAndProcess(Current __current) throws FusionException {
        try {
            ++this.processing;
            this.runDateString = FILE_DATE_FORMAT.format(this.endOfPeriodObserving);
            SortedMap<Integer, Integer> scoreDistribution = this.gatherAndProcessRunDateString(this.runDateString, this.updateScoreTable);
            ReputationLastRan.setLastRunDate(commonMemCached, System.currentTimeMillis());
            CombineSummarizedResults combiner = new CombineSummarizedResults(this.directoryHolder);
            SortBigFile sort = new SortBigFile(this.directoryHolder);
            try {
                String combinedOutputFile = combiner.combine(this.endOfPeriodObserving);
                sort.go(new FileLocation(this.directoryHolder.getDataDirectory(), combinedOutputFile), new FileLocation(this.directoryHolder.getDataDirectory(), combinedOutputFile), new ScoreSummaryFileEntryComparator(), new ScoreSummaryStringListComparator(), ScoreSummary.EXPECTED_FIELD_COUNT, ',');
            }
            catch (IOException e) {
                log.error((Object)"failed to combine recent summarized results", (Throwable)e);
            }
            Object var7_7 = null;
            --this.processing;
        }
        catch (Throwable throwable) {
            Object var7_8 = null;
            --this.processing;
            throw throwable;
        }
    }

    @Override
    public void processPreviouslyDumpedData(String runDateString, Current __current) throws FusionException {
        try {
            try {
                ++this.processing;
                this.sortData(runDateString, false);
                this.processData(runDateString);
            }
            catch (Exception e) {
                log.error((Object)"failed to process", (Throwable)e);
                throw new FusionException("failed to process");
            }
            Object var5_3 = null;
            --this.processing;
        }
        catch (Throwable throwable) {
            Object var5_4 = null;
            --this.processing;
            throw throwable;
        }
    }

    @Override
    public void processPreviouslySortedData(String runDateString, Current __current) throws FusionException {
        try {
            try {
                ++this.processing;
                this.processData(runDateString);
            }
            catch (Exception e) {
                log.error((Object)"failed to process", (Throwable)e);
                throw new FusionException("failed to process");
            }
            Object var5_3 = null;
            --this.processing;
        }
        catch (Throwable throwable) {
            Object var5_4 = null;
            --this.processing;
            throw throwable;
        }
    }

    @Override
    public void updateScoreFromPreviouslyProcessedData(String runDateString, Current __current) throws FusionException {
        try {
            try {
                ++this.processing;
                if (this.runDateString != null && !this.runDateString.equals(runDateString)) {
                    log.warn((Object)("supplied runDateString [" + runDateString + "] is not the same as the most recent one [" + this.runDateString + "]"));
                }
                UpdateScoreTable updater = new UpdateScoreTable(this.masterDataSource, this.directoryHolder, this.scoreCache, this.icePrxFinder);
                updater.process(runDateString);
            }
            catch (Exception e) {
                log.error((Object)"failed to process", (Throwable)e);
                throw new FusionException("failed to process");
            }
            Object var5_5 = null;
            --this.processing;
        }
        catch (Throwable throwable) {
            Object var5_6 = null;
            --this.processing;
            throw throwable;
        }
    }

    @Override
    public void updateLastRunDate(Current __current) throws FusionException {
        ReputationLastRan.setLastRunDate(commonMemCached, System.currentTimeMillis());
    }

    @Override
    public int getUserLevel(String username, Current __current) throws FusionException {
        Element element = this.scoreCache.get((Serializable)((Object)username));
        if (log.isDebugEnabled()) {
            log.debug((Object)this.scoreCache.getStatistics().toString());
        }
        int score = (Integer)element.getValue();
        return LevelTable.getLevelForScore(score, this.levelTable);
    }

    @Override
    public ScoreAndLevel[] getUserScoreAndLevels(int[] userIDs, Current __current) throws FusionException {
        if (userIDs == null || userIDs.length == 0) {
            return new ScoreAndLevel[0];
        }
        int[] uncachedUserIDs = new int[userIDs.length];
        int index = 0;
        for (int userID : userIDs) {
            if (this.scoreCache.isKeyInCache((Object)userID)) continue;
            uncachedUserIDs[index++] = userID;
        }
        Map<Integer, Integer> scores = this.reputationDAO.getUserScores(userIDs);
        ScoreAndLevel[] scoreAndLevels = new ScoreAndLevel[userIDs.length];
        index = 0;
        if (scoreAndLevels != null && scoreAndLevels.length > 0) {
            int[] arr$ = userIDs;
            int len$ = arr$.length;
            for (int i$ = 0; i$ < len$; ++i$) {
                Integer userID = arr$[i$];
                int score = scores.get(userID);
                if (log.isDebugEnabled()) {
                    log.debug((Object)("score retrieved " + score));
                }
                scoreAndLevels[index++] = new ScoreAndLevel(score, LevelTable.getLevelForScore(score, this.levelTable));
            }
            return scoreAndLevels;
        }
        return new ScoreAndLevel[0];
    }

    public void setDataDirectory(String dataDirectory) {
        this.dataDirectory = !StringUtils.hasLength((String)dataDirectory) ? "/reputation/" : DirectoryUtils.getValidDirectory(dataDirectory);
    }

    public void setScratchDirectory(String scratchDirectory) {
        this.scratchDirectory = !StringUtils.hasLength((String)scratchDirectory) ? "/reputation/scratch/" : DirectoryUtils.getValidDirectory(scratchDirectory);
    }

    public void setDumpDirectory(String dumpDirectory) {
        this.dumpDirectory = !StringUtils.hasLength((String)dumpDirectory) ? "/reputation/dump/" : DirectoryUtils.getValidDirectory(dumpDirectory);
    }

    public Date getEndOfPeriodObserving() {
        return this.endOfPeriodObserving;
    }

    public void setEndOfPeriodObserving(Date endOfPeriodObserving) {
        this.endOfPeriodObserving = endOfPeriodObserving;
    }

    public void setMasterDataSource(DataSource masterDataSource) {
        this.masterDataSource = masterDataSource;
    }

    public void setRepuDataSource(DataSource repuDataSource) {
        this.repuDataSource = repuDataSource;
    }

    public void setRepuODSDataSource(DataSource repuODSDataSource) {
        this.repuODSDataSource = repuODSDataSource;
    }

    public void setOlapDataSource(DataSource olapDataSource) {
        this.olapDataSource = olapDataSource;
    }

    public long getLastTimeRunCompleted() {
        return ReputationLastRan.getLastRunDate(commonMemCached);
    }

    public boolean isProcessing() {
        return this.processing > 0;
    }

    public void setUpdateScoreTable(boolean updateScoreTable) {
        this.updateScoreTable = updateScoreTable;
    }

    public void setReputationDAO(ReputationDAO reputationDAO) {
        this.reputationDAO = reputationDAO;
    }

    public void setQueries(ExternalizedQueries queries) {
    }

    public void setIcePrxFinder(IcePrxFinder icePrxFinder) {
        this.icePrxFinder = icePrxFinder;
    }
}

