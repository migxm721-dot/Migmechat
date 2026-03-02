/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.chatnewsfeed.cricket.feed;

import java.util.HashMap;
import java.util.Map;

public class CricketFeedConstants {
    public static final String ELEMENT_TAG_CALENDAR = "calendar";
    public static final String ELEMENT_TAG_MATCH = "match";
    public static final String ELEMENT_TAG_PREVIEW = "Preview";
    public static final String ELEMENT_TAG_INNINGS = "Innings";
    public static final String ELEMENT_TAG_INNINGS_NODE = "Node";
    public static final String ELEMENT_TAG_COMMENTARY = "Commentary";
    public static final String ELEMENT_TAG_SCORE = "Score";
    public static final String ELEMENT_TAG_THIS_OVER = "ThisOver";
    public static final String ELEMENT_TAG_DETAILS = "Details";
    public static final String INNINGS_FIRST = "First";
    public static final String INNINGS_SECOND = "Second";
    public static final String INNINGS_THIRD = "Third";
    public static final String INNINGS_FOURTH = "Fourth";
    public static final String ELEMENT_TAG_MATCH_DETAIL = "Matchdetail";
    public static final String ELEMENT_TAG_EQUATION = "Equation";
    public static final String ELEMENT_TAG_OVER_DETAIL = "OverDetail";
    public static final String ELEMENT_TAG_PREFIX_OVER = "Over";
    public static final String ELEMENT_TAG_PREFIX_INNINGS_FIRST = "FI";
    public static final String ELEMENT_TAG_PREFIX_INNINGS_SECOND = "SI";
    public static final String ELEMENT_TAG_PREFIX_INNINGS_THIRD = "TI";
    public static final String ELEMENT_TAG_PREFIX_INNINGS_FOURTH = "FOI";
    public static final String ELEMENT_TAG_BATSMEN = "Batsmen";
    public static final String ELEMENT_TAG_BATSMAN = "Batsman";
    public static final String ELEMENT_TAG_BOWLERS = "Bowlers";
    public static final String ELEMENT_TAG_BOWLER = "Bowler";
    public static final String TEXT_BATTING = "Batting";
    public static final String TEXT_YES = "Yes";
    public static Map<String, String> inningsMappings = new HashMap<String, String>();
    public static Map<String, Integer> inningsNumberMappings = new HashMap<String, Integer>();
    public static Map<Integer, String> inningsReversePrefixMappings = new HashMap<Integer, String>();
    public static Map<Integer, String> inningsReverseNameMappings = new HashMap<Integer, String>();
    public static final int NUM_SCORES_TO_BACKFILL = 5;
    public static final String CRICKET_FAN_ZONE = "Cricket FanZone";
    public static final String VERSUS_KEYWORD = " v ";
    public static final String TEXT_LIVE_MATCH = "Live match in progress: ";
    public static final String TEXT_MATCH_ENDED = "Match Ended";
    public static final String TEXT_SIXER = "Sixer";
    public static final String TEXT_FOUR = "Boundary";
    public static final String TEXT_PREFIX_CAUGHT_AND_BOWLED = "c ";
    public static final String TEXT_PREFIX_BOWLED = "b ";
    public static final String TEXT_PREFIX_RUN_OUT = "run out ";
    public static final String EMOTE_SIXER = "(6s)";
    public static final String EMOTE_FOUR = "(4s)";
    public static final String EMOTE_OUT = "(bowled-wicket)";

    static {
        inningsMappings.put(INNINGS_FIRST, ELEMENT_TAG_PREFIX_INNINGS_FIRST);
        inningsMappings.put(INNINGS_SECOND, ELEMENT_TAG_PREFIX_INNINGS_SECOND);
        inningsMappings.put(INNINGS_THIRD, ELEMENT_TAG_PREFIX_INNINGS_THIRD);
        inningsMappings.put(INNINGS_FOURTH, ELEMENT_TAG_PREFIX_INNINGS_FOURTH);
        inningsNumberMappings.put(INNINGS_FIRST, 1);
        inningsNumberMappings.put(INNINGS_SECOND, 2);
        inningsNumberMappings.put(INNINGS_THIRD, 3);
        inningsNumberMappings.put(INNINGS_FOURTH, 4);
        inningsReversePrefixMappings.put(1, ELEMENT_TAG_PREFIX_INNINGS_FIRST);
        inningsReversePrefixMappings.put(2, ELEMENT_TAG_PREFIX_INNINGS_SECOND);
        inningsReversePrefixMappings.put(3, ELEMENT_TAG_PREFIX_INNINGS_THIRD);
        inningsReversePrefixMappings.put(4, ELEMENT_TAG_PREFIX_INNINGS_FOURTH);
        inningsReverseNameMappings.put(1, INNINGS_FIRST);
        inningsReverseNameMappings.put(2, INNINGS_SECOND);
        inningsReverseNameMappings.put(3, INNINGS_THIRD);
        inningsReverseNameMappings.put(4, INNINGS_FOURTH);
    }
}

