/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.reflect.TypeToken
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.rewardsystem.stateprocessors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.rewardsystem.stateprocessors.RewardProgramStateHandler;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;
import com.projectgoth.fusion.rewardsystem.triggers.VGReceivedTrigger;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class VGReceivedStateHandler
extends RewardProgramStateHandler {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(VGReceivedStateHandler.class));
    private static final Type hashMapType = new TypeToken<Map<Integer, Integer>>(){}.getType();

    private Map<Integer, Integer> makeVgIdToCountMap(List<String> param) throws NumberFormatException {
        HashMap<Integer, Integer> vgIds = new HashMap<Integer, Integer>();
        for (String vgId : param) {
            Integer id;
            vgIds.put(id, 1 + (vgIds.containsKey(id = new Integer(vgId)) ? (Integer)vgIds.get(id) : 0));
        }
        return vgIds;
    }

    private static int toIntOrDefault(Integer i, int defaultVal) {
        return i == null ? defaultVal : i;
    }

    @Override
    public RewardProgramStateHandler.PerformReturn perform(RewardProgramData program, RewardProgramTrigger trigger, String curState) {
        int newCountForMatchedVgId;
        Map<Integer, Integer> matchedGiftsCounters;
        Map<Integer, Integer> expectedVgIdToCountMap;
        if (!(trigger instanceof VGReceivedTrigger)) {
            return RewardProgramStateHandler.PerformReturn.NOTHING;
        }
        VGReceivedTrigger rt = (VGReceivedTrigger)trigger;
        if (rt.quantityDelta <= 0) {
            return RewardProgramStateHandler.PerformReturn.NOTHING;
        }
        List<String> giftParamList = program.getStringListParam("virtualGiftIDList");
        if (giftParamList == null || giftParamList.isEmpty()) {
            return RewardProgramStateHandler.PerformReturn.NOTHING;
        }
        try {
            expectedVgIdToCountMap = this.makeVgIdToCountMap(giftParamList);
        }
        catch (NumberFormatException e) {
            log.error((Object)("Program[" + program.id + "] has a non numeric VGID defined.[" + giftParamList + "]"), (Throwable)e);
            return RewardProgramStateHandler.PerformReturn.NOTHING;
        }
        Integer receivedVirtualGiftID = rt.virtualGiftID;
        if (!expectedVgIdToCountMap.containsKey(receivedVirtualGiftID)) {
            return RewardProgramStateHandler.PerformReturn.NOTHING;
        }
        Gson gson = new GsonBuilder().create();
        if (StringUtil.isBlank(curState)) {
            matchedGiftsCounters = new HashMap(1);
            newCountForMatchedVgId = rt.quantityDelta;
        } else {
            matchedGiftsCounters = (Map)gson.fromJson(curState, hashMapType);
            newCountForMatchedVgId = VGReceivedStateHandler.toIntOrDefault((Integer)matchedGiftsCounters.get(receivedVirtualGiftID), 0) + rt.quantityDelta;
        }
        matchedGiftsCounters.put(receivedVirtualGiftID, newCountForMatchedVgId);
        MatchedVGIDsToCountMap consumedMatchedGiftsCounters = VGReceivedStateHandler.tryConsumeMatchedGiftsCounters(expectedVgIdToCountMap, matchedGiftsCounters);
        return RewardProgramStateHandler.PerformReturn.saveState(consumedMatchedGiftsCounters.isConsumed(), gson.toJson((Object)consumedMatchedGiftsCounters));
    }

    private static MatchedVGIDsToCountMap tryConsumeMatchedGiftsCounters(Map<Integer, Integer> expectedVgIdToCountMap, Map<Integer, Integer> matchedGiftsCounters) {
        MatchedVGIDsToCountMap nextConsumedMatchedGiftsCounters = new MatchedVGIDsToCountMap(expectedVgIdToCountMap.size(), true);
        MatchedVGIDsToCountMap nextNonConsumedMatchedGiftsCounters = new MatchedVGIDsToCountMap(expectedVgIdToCountMap.size(), false);
        boolean foundNonConsumable = false;
        for (Map.Entry<Integer, Integer> expectedVgIdToCountEntry : expectedVgIdToCountMap.entrySet()) {
            Integer expectedVgId = expectedVgIdToCountEntry.getKey();
            Integer matchedCount = matchedGiftsCounters.get(expectedVgId);
            if (matchedCount != null) {
                nextNonConsumedMatchedGiftsCounters.put(expectedVgId, matchedCount);
                if (foundNonConsumable) continue;
                Integer expectedCount = expectedVgIdToCountEntry.getValue();
                Integer remainingMatches = matchedCount - expectedCount;
                if (remainingMatches >= 0) {
                    if (remainingMatches <= 0) continue;
                    nextConsumedMatchedGiftsCounters.put(expectedVgId, remainingMatches);
                    continue;
                }
                foundNonConsumable = true;
                continue;
            }
            foundNonConsumable = true;
        }
        if (foundNonConsumable) {
            return nextNonConsumedMatchedGiftsCounters;
        }
        return nextConsumedMatchedGiftsCounters;
    }

    @Override
    public String getStateKeySuffix() {
        return "vgrcvd";
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class MatchedVGIDsToCountMap
    extends HashMap<Integer, Integer> {
        private final boolean consumed;

        public MatchedVGIDsToCountMap(boolean consumed) {
            this.consumed = consumed;
        }

        public MatchedVGIDsToCountMap(int capacity, boolean consumed) {
            super(capacity);
            this.consumed = consumed;
        }

        public boolean isConsumed() {
            return this.consumed;
        }
    }
}

