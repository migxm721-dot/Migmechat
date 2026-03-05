/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.danga.MemCached.MemCachedClient
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.reputation.file;

import com.danga.MemCached.MemCachedClient;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.MemCachedUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.reputation.cache.ScoreFormulaParameters;
import com.projectgoth.fusion.reputation.domain.ScoreCategory;
import com.projectgoth.fusion.reputation.file.ScoreSummary;
import com.projectgoth.fusion.reputation.util.CSVUtils;
import com.projectgoth.fusion.reputation.util.DirectoryHolder;
import com.projectgoth.fusion.reputation.util.DirectoryUtils;
import com.projectgoth.fusion.reputation.util.stats.DailyScoreDistribution;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ScoreFinalSummary {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ScoreFinalSummary.class));
    private static final int METRIC_PROPORTIONATE_MAX = 100;
    private static final int CATEGORY_MAX = 100;
    public static final int SCORE_DISTRIBUTION_STEP = 20;
    public static MemCachedClient memCached = MemCachedUtils.getMemCachedClient(MemCachedUtils.Instance.broadcastList);
    private ScoreFormulaParameters scoreFormulaParameters;
    private double[] reasonableMaximums;
    private int[] weights;
    private SortedMap<Double, Integer> bonuses = new TreeMap<Double, Integer>();
    private DirectoryHolder directoryHolder;

    public ScoreFinalSummary(DirectoryHolder directoryHolder) {
        this.scoreFormulaParameters = (ScoreFormulaParameters)memCached.get(MemCachedUtils.getCacheKeyInNamespace("REP", "SCFP"));
        if (this.scoreFormulaParameters == null) {
            this.scoreFormulaParameters = new ScoreFormulaParameters();
            ScoreFormulaParameters.setScoreFormulaParameters(memCached, this.scoreFormulaParameters);
        }
        this.updateReasonableMaximums();
        this.updateWeights();
        this.updateBonuses();
        this.directoryHolder = directoryHolder;
    }

    private void updateReasonableMaximums() {
        this.reasonableMaximums = new double[ScoreSummary.EXPECTED_INPUT_FIELD_COUNT];
        log.info((Object)"reasonable maximums");
        this.reasonableMaximums[1] = this.scoreFormulaParameters.getChatRoomMessagesSentReasonableMax();
        log.info((Object)("chatroom messages sent: " + this.reasonableMaximums[1]));
        this.reasonableMaximums[2] = this.scoreFormulaParameters.getPrivateMessagesSentReasonableMax();
        log.info((Object)("private messages sent: " + this.reasonableMaximums[2]));
        this.reasonableMaximums[3] = this.scoreFormulaParameters.getTotalTimeReasonableMax();
        log.info((Object)("total time: " + this.reasonableMaximums[3]));
        this.reasonableMaximums[4] = this.scoreFormulaParameters.getPhotosUploadedReasonableMax();
        log.info((Object)("photos uploaded: " + this.reasonableMaximums[4]));
        this.reasonableMaximums[5] = this.scoreFormulaParameters.getKicksInitiatedReasonableMax();
        log.info((Object)("kicks: " + this.reasonableMaximums[5]));
        this.reasonableMaximums[6] = this.scoreFormulaParameters.getAuthenticatedReferralsReasonableMax();
        log.info((Object)("authenticated referrals: " + this.reasonableMaximums[6]));
        this.reasonableMaximums[7] = this.scoreFormulaParameters.getRechargedAmountReasonableMax();
        log.info((Object)("recharged amount: " + this.reasonableMaximums[7]));
        this.reasonableMaximums[8] = this.scoreFormulaParameters.getVirtualGiftsReceivedReasonableMax();
        log.info((Object)("virtual gifts received: " + this.reasonableMaximums[8]));
        this.reasonableMaximums[9] = this.scoreFormulaParameters.getVirtualGiftsSentReasonableMax();
        log.info((Object)("virtual gifts sent: " + this.reasonableMaximums[9]));
        this.reasonableMaximums[10] = this.scoreFormulaParameters.getPhoneCallDurationReasonableMax();
        log.info((Object)("call duration: " + this.reasonableMaximums[10]));
    }

    private void updateWeights() {
        this.weights = new int[ScoreSummary.EXPECTED_INPUT_FIELD_COUNT];
        this.weights[1] = this.scoreFormulaParameters.getChatRoomMessagesSentWeight();
        this.weights[2] = this.scoreFormulaParameters.getPrivateMessagesSentWeight();
        this.weights[3] = this.scoreFormulaParameters.getTotalTimeWeight();
        this.weights[4] = this.scoreFormulaParameters.getPhotosUploadedWeight();
        this.weights[5] = this.scoreFormulaParameters.getKicksInitiatedWeight();
        this.weights[6] = this.scoreFormulaParameters.getAuthenticatedReferralsWeight();
        this.weights[7] = this.scoreFormulaParameters.getRechargedAmountWeight();
        this.weights[8] = this.scoreFormulaParameters.getVirtualGiftsReceivedWeight();
        this.weights[9] = this.scoreFormulaParameters.getVirtualGiftsSentWeight();
        this.weights[10] = this.scoreFormulaParameters.getPhoneCallDurationWeight();
    }

    private void updateBonuses() {
        this.bonuses.clear();
        this.bonuses.put(0.5, this.scoreFormulaParameters.getReasonableMaximumMinus50());
        this.bonuses.put(0.45, this.scoreFormulaParameters.getReasonableMaximumMinus45());
        this.bonuses.put(0.4, this.scoreFormulaParameters.getReasonableMaximumMinus40());
        this.bonuses.put(0.35, this.scoreFormulaParameters.getReasonableMaximumMinus35());
        this.bonuses.put(0.3, this.scoreFormulaParameters.getReasonableMaximumMinus30());
        this.bonuses.put(0.25, this.scoreFormulaParameters.getReasonableMaximumMinus25());
        this.bonuses.put(0.2, this.scoreFormulaParameters.getReasonableMaximumMinus20());
        this.bonuses.put(0.15, this.scoreFormulaParameters.getReasonableMaximumMinus15());
        this.bonuses.put(0.1, this.scoreFormulaParameters.getReasonableMaximumMinus10());
    }

    private int[] score(List<String> parts) {
        int i;
        int i2;
        int[] scores = new int[ScoreSummary.EXPECTED_INPUT_FIELD_COUNT + 4 + 1];
        int[] categoryScores = new int[ScoreCategory.values().length];
        for (i2 = 1; i2 < parts.size(); ++i2) {
            scores[i2] = Integer.parseInt(parts.get(i2));
        }
        for (i2 = 1; i2 < ScoreSummary.EXPECTED_INPUT_FIELD_COUNT; ++i2) {
            scores[i2] = (int)Math.round((double)scores[i2] / this.reasonableMaximums[i2] * 100.0);
            if (scores[i2] >= 0) continue;
            scores[i2] = 0;
        }
        Set<Double> keys = this.bonuses.keySet();
        Iterator<Double> keyIterator = keys.iterator();
        double previousKey = 1.0;
        for (i = 1; i < ScoreSummary.EXPECTED_INPUT_FIELD_COUNT; ++i) {
            Double key;
            while (keyIterator.hasNext() && !((double)scores[i] < (key = keyIterator.next()) * this.reasonableMaximums[i])) {
                previousKey = key;
            }
            if (!(previousKey > this.bonuses.lastKey()) || !(previousKey <= this.bonuses.firstKey())) continue;
            log.debug((Object)("awarding bonus for part " + i + " " + scores[i] + " -> " + previousKey + " = " + this.bonuses.get(previousKey)));
            int n = i;
            scores[n] = scores[n] + (Integer)this.bonuses.get(previousKey);
        }
        for (i = 1; i < ScoreSummary.EXPECTED_INPUT_FIELD_COUNT; ++i) {
            if (scores[i] <= 100) continue;
            scores[i] = 100;
        }
        for (i = 1; i < ScoreSummary.EXPECTED_INPUT_FIELD_COUNT; ++i) {
            int n = i;
            scores[n] = scores[n] * this.weights[i];
        }
        categoryScores[ScoreCategory.TIME_IN_PRODUCT.value()] = scores[3];
        categoryScores[ScoreCategory.CREDITS_SPENT.value()] = scores[7];
        int n = ScoreCategory.CREDITS_SPENT.value();
        categoryScores[n] = categoryScores[n] + scores[9];
        int n2 = ScoreCategory.CREDITS_SPENT.value();
        categoryScores[n2] = categoryScores[n2] + scores[10];
        categoryScores[ScoreCategory.HUMAN_LIKELY_BEHAVIOUR.value()] = scores[4];
        int n3 = ScoreCategory.HUMAN_LIKELY_BEHAVIOUR.value();
        categoryScores[n3] = categoryScores[n3] + scores[8];
        int n4 = ScoreCategory.HUMAN_LIKELY_BEHAVIOUR.value();
        categoryScores[n4] = categoryScores[n4] + scores[6];
        categoryScores[ScoreCategory.BASIC_ACTIVITY.value()] = Math.min(scores[1], 75);
        int n5 = ScoreCategory.BASIC_ACTIVITY.value();
        categoryScores[n5] = categoryScores[n5] + Math.min(scores[2], 75);
        for (i = 0; i < categoryScores.length; ++i) {
            if (categoryScores[i] <= 100) continue;
            categoryScores[i] = 100;
        }
        scores[ScoreSummary.TIME_IN_PRODUCT_CATEGORY_INDEX] = categoryScores[ScoreCategory.TIME_IN_PRODUCT.value()];
        scores[ScoreSummary.CREDITS_SPENT_CATEGORY_INDEX] = categoryScores[ScoreCategory.CREDITS_SPENT.value()];
        scores[ScoreSummary.HUMAN_LIKE_BEHAVIOUR_CATEGORY_INDEX] = categoryScores[ScoreCategory.HUMAN_LIKELY_BEHAVIOUR.value()];
        scores[ScoreSummary.BASIC_ACTIVITY_CATEGORY_INDEX] = categoryScores[ScoreCategory.BASIC_ACTIVITY.value()];
        scores[ScoreSummary.TOTAL_SCORE_INDEX] = 0;
        for (i = 0; i < categoryScores.length; ++i) {
            int n6 = ScoreSummary.TOTAL_SCORE_INDEX;
            scores[n6] = scores[n6] + categoryScores[i];
        }
        if (scores[ScoreSummary.TOTAL_SCORE_INDEX] > this.scoreFormulaParameters.getDailyHardCap()) {
            scores[ScoreSummary.TOTAL_SCORE_INDEX] = this.scoreFormulaParameters.getDailyHardCap();
        }
        return scores;
    }

    public SortedMap<Integer, Integer> scoreFile(String filename) throws IOException {
        String line;
        log.info((Object)("scoring " + filename));
        TreeMap<Integer, Integer> scoreDistribution = new TreeMap<Integer, Integer>();
        DailyScoreDistribution.initializeMap(scoreDistribution, this.scoreFormulaParameters.getDailyHardCap(), 20);
        long start = System.currentTimeMillis();
        BufferedReader reader = new BufferedReader(new FileReader(this.directoryHolder.getDataDirectory() + filename));
        BufferedWriter writer = new BufferedWriter(new FileWriter(this.directoryHolder.getDataDirectory() + "scored." + filename));
        while ((line = reader.readLine()) != null) {
            List<String> parts = StringUtil.split(line, ',');
            if (parts.size() != ScoreSummary.EXPECTED_INPUT_FIELD_COUNT) {
                log.warn((Object)("found more or less parts [" + parts.size() + "] in this line [" + line + "], skipping"));
                continue;
            }
            int[] scores = this.score(parts);
            writer.write(CSVUtils.getColumnFromLine(line, 0, ','));
            writer.write(44);
            for (int i = 1; i < scores.length; ++i) {
                writer.write(Integer.toString(scores[i]));
                if (i >= scores.length - 1) continue;
                writer.write(44);
            }
            writer.newLine();
            DailyScoreDistribution.populateMap(scoreDistribution, scores[ScoreSummary.TOTAL_SCORE_INDEX]);
        }
        writer.flush();
        writer.close();
        long end = System.currentTimeMillis();
        log.info((Object)("done scoring " + filename + ", took " + (double)(end - start) / 1000.0 + " seconds"));
        scoreDistribution.remove(scoreDistribution.lastKey());
        return scoreDistribution;
    }

    public static void main(String[] args) {
        ScoreFinalSummary scorer = new ScoreFinalSummary(DirectoryUtils.getDirectoryHolder());
        try {
            scorer.scoreFile(args[0]);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }
}

