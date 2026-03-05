/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.axis.utils.StringUtils
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.chatnewsfeed.cricket.feed;

import com.projectgoth.fusion.chatnewsfeed.CricketFeed;
import com.projectgoth.fusion.chatnewsfeed.cricket.data.Match;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.DateTimeUtils;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.axis.utils.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MatchFeedHelper {
    private static final Logger logger = Logger.getLogger((String)ConfigUtils.getLoggerName(CricketFeed.class));

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void processMatches(Node node, Document xmlDocument, Map<String, Match> matches, List<String> seriesNames) {
        NodeList nodeList = xmlDocument.getElementsByTagName("match");
        if (nodeList != null) {
            HashMap<String, Match> updatedMatches = new HashMap<String, Match>();
            for (Node matchNode = nodeList.item(0); matchNode != null; matchNode = matchNode.getNextSibling()) {
                this.processMatchNode(matchNode, updatedMatches, CricketFeed.filterBySeries, seriesNames);
            }
            for (Match updatedMatch : updatedMatches.values()) {
                boolean isJustEnded;
                Match existingMatch = matches.get(updatedMatch.getMatchIdentifier());
                boolean bl = isJustEnded = existingMatch != null && !updatedMatch.isLive() && !existingMatch.isResultReported();
                if (!updatedMatch.isLive() && !isJustEnded && !updatedMatch.isUpcoming()) continue;
                Map<String, Match> map = matches;
                synchronized (map) {
                    matches.put(updatedMatch.getMatchIdentifier(), updatedMatch);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void processMatchNode(Node node, Map<String, Match> updatedMatches, boolean checkSeries, List<String> seriesNames) {
        if (node.getNodeType() == 1) {
            boolean isMatchInSeries;
            Match match = new Match();
            NamedNodeMap attributesMap = node.getAttributes();
            this.getDeterminantMatchAttributes(match, attributesMap);
            boolean bl = isMatchInSeries = !checkSeries || checkSeries && seriesNames.contains(match.getSeriesName());
            if (match.getMatchIdentifier() != null && isMatchInSeries) {
                String matchId = match.getMatchIdentifier();
                this.getOtherAttributes(match, attributesMap);
                MatchFeedHelper matchFeedHelper = this;
                synchronized (matchFeedHelper) {
                    updatedMatches.put(matchId, match);
                }
            }
        }
    }

    private void getDeterminantMatchAttributes(Match match, NamedNodeMap attributes) {
        Node seriesName;
        Node live;
        Node id = attributes.getNamedItem("matchfile");
        if (id != null) {
            match.setMatchIdentifier(id.getTextContent());
        }
        if ((live = attributes.getNamedItem("live")) != null) {
            match.setLive(Integer.parseInt(live.getTextContent()) != 0);
        }
        if ((seriesName = attributes.getNamedItem("seriesname")) != null) {
            match.setSeriesName(seriesName.getTextContent());
        }
    }

    private void getOtherAttributes(Match match, NamedNodeMap attributes) {
        Node matchYear;
        Node matchMonth;
        Node matchTime;
        Node matchDate;
        Node matchNumber;
        Node venue;
        Node result;
        Node teamB;
        Node teamA;
        Node state;
        Node upcoming = attributes.getNamedItem("upcoming");
        if (upcoming != null) {
            match.setUpcoming(Integer.parseInt(upcoming.getTextContent()) != 0);
        }
        if ((state = attributes.getNamedItem("delayed")) != null) {
            match.setState(state.getTextContent());
        }
        if ((teamA = attributes.getNamedItem("teama")) != null) {
            match.setTeamA(teamA.getTextContent());
        }
        if ((teamB = attributes.getNamedItem("teamb")) != null) {
            match.setTeamB(teamB.getTextContent());
        }
        if ((result = attributes.getNamedItem("matchresult")) != null) {
            match.setResult(result.getTextContent());
        }
        if ((venue = attributes.getNamedItem("venue")) != null) {
            match.setVenue(venue.getTextContent());
        }
        if ((matchNumber = attributes.getNamedItem("matchnumber")) != null) {
            match.setMatchNumber(matchNumber.getTextContent());
        }
        if ((matchDate = attributes.getNamedItem("matchdate")) != null) {
            match.setMatchDate(matchDate.getTextContent());
        }
        if ((matchTime = attributes.getNamedItem("matchtime")) != null) {
            match.setMatchTime(matchTime.getTextContent().substring(0, 5));
            match.setCalendarTimeZone(matchTime.getTextContent().substring(7, 10));
            if (match.getMatchStartTime() == null) {
                match.setMatchStartTime(this.getStartTime(match));
            }
        }
        if ((matchMonth = attributes.getNamedItem("matchmonth")) != null) {
            match.setMatchMonth(matchMonth.getTextContent());
        }
        if ((matchYear = attributes.getNamedItem("matchyear")) != null) {
            match.setMatchYear(matchYear.getTextContent());
        }
    }

    public Date getStartTime(Match match) {
        Date date = match.getMatchStartTime();
        if (date == null && !StringUtils.isEmpty((String)match.getMatchDate()) && !StringUtils.isEmpty((String)match.getMatchTime())) {
            String dateString = match.getMatchDate() + " " + match.getMatchTime();
            try {
                date = DateTimeUtils.stringToDate(dateString, "MM/dd/yyyy HH:mm", match.getCalendarTimeZone());
                match.setMatchStartTime(date);
            }
            catch (Exception e) {
                logger.error((Object)("Invalid start time:" + dateString + " for match ID: " + match.getMatchIdentifier()));
                e.printStackTrace();
            }
        }
        return date;
    }
}

