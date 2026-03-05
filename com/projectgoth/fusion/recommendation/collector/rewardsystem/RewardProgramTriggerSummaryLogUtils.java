/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.recommendation.collector.rewardsystem;

import com.projectgoth.fusion.common.DateTimeUtils;
import com.projectgoth.fusion.slice.CollectedRewardProgramTriggerSummaryDataIce;
import java.text.ParseException;
import java.util.Date;
import java.util.StringTokenizer;

public class RewardProgramTriggerSummaryLogUtils {
    public static final char FIELD_SEPARATOR = '\t';
    private static final Parser[] parsers = new Parser[]{new Parser(){

        public void parse(CollectedRewardProgramTriggerSummaryDataIce context, int tokenNumber, String token) throws Throwable {
            context.id = token;
        }
    }, new Parser(){

        public void parse(CollectedRewardProgramTriggerSummaryDataIce context, int tokenNumber, String token) throws Throwable {
            context.createTimestamp = DateTimeUtils.getTimestamp(token).getTime();
        }
    }, new Parser(){

        public void parse(CollectedRewardProgramTriggerSummaryDataIce context, int tokenNumber, String token) throws Throwable {
            if (context.createTimestamp != Long.parseLong(token)) {
                throw new ParseException("token 0 and token 1 are not the same", tokenNumber);
            }
        }
    }, new Parser(){

        public void parse(CollectedRewardProgramTriggerSummaryDataIce context, int tokenNumber, String token) throws Throwable {
            context.host = token;
        }
    }, new Parser(){

        public void parse(CollectedRewardProgramTriggerSummaryDataIce context, int tokenNumber, String token) throws Throwable {
            context.instance = token;
        }
    }, new Parser(){

        public void parse(CollectedRewardProgramTriggerSummaryDataIce context, int tokenNumber, String token) throws Throwable {
            context.programType = Integer.parseInt(token);
        }
    }, new Parser(){

        public void parse(CollectedRewardProgramTriggerSummaryDataIce context, int tokenNumber, String token) throws Throwable {
            context.minReceivedTimestamp = Long.parseLong(token);
        }
    }, new Parser(){

        public void parse(CollectedRewardProgramTriggerSummaryDataIce context, int tokenNumber, String token) throws Throwable {
            context.maxReceivedTimestamp = Long.parseLong(token);
        }
    }, new Parser(){

        public void parse(CollectedRewardProgramTriggerSummaryDataIce context, int tokenNumber, String token) throws Throwable {
            context.receivedCount = Long.parseLong(token);
        }
    }, new Parser(){

        public void parse(CollectedRewardProgramTriggerSummaryDataIce context, int tokenNumber, String token) throws Throwable {
            context.droppedCount = Long.parseLong(token);
        }
    }, new Parser(){

        public void parse(CollectedRewardProgramTriggerSummaryDataIce context, int tokenNumber, String token) throws Throwable {
            context.successfulCount = Long.parseLong(token);
        }
    }, new Parser(){

        public void parse(CollectedRewardProgramTriggerSummaryDataIce context, int tokenNumber, String token) throws Throwable {
            context.failedCount = Long.parseLong(token);
        }
    }, new Parser(){

        public void parse(CollectedRewardProgramTriggerSummaryDataIce context, int tokenNumber, String token) throws Throwable {
            context.dequeuedCount = Long.parseLong(token);
        }
    }, new Parser(){

        public void parse(CollectedRewardProgramTriggerSummaryDataIce context, int tokenNumber, String token) throws Throwable {
            context.minTimeSpentInQueue = Long.parseLong(token);
        }
    }, new Parser(){

        public void parse(CollectedRewardProgramTriggerSummaryDataIce context, int tokenNumber, String token) throws Throwable {
            context.maxTimeSpentInQueue = Long.parseLong(token);
        }
    }, new Parser(){

        public void parse(CollectedRewardProgramTriggerSummaryDataIce context, int tokenNumber, String token) throws Throwable {
            context.meanTimeSpentInQueue = Double.parseDouble(token);
        }
    }, new Parser(){

        public void parse(CollectedRewardProgramTriggerSummaryDataIce context, int tokenNumber, String token) throws Throwable {
            context.varianceProcessingTimeAfterDequeue = Double.parseDouble(token);
        }
    }, new Parser(){

        public void parse(CollectedRewardProgramTriggerSummaryDataIce context, int tokenNumber, String token) throws Throwable {
            context.minProcessingTimeAfterDequeue = Long.parseLong(token);
        }
    }, new Parser(){

        public void parse(CollectedRewardProgramTriggerSummaryDataIce context, int tokenNumber, String token) throws Throwable {
            context.maxProcessingTimeAfterDequeue = Long.parseLong(token);
        }
    }, new Parser(){

        public void parse(CollectedRewardProgramTriggerSummaryDataIce context, int tokenNumber, String token) throws Throwable {
            context.meanProcessingTimeAfterDequeue = Double.parseDouble(token);
        }
    }, new Parser(){

        public void parse(CollectedRewardProgramTriggerSummaryDataIce context, int tokenNumber, String token) throws Throwable {
            context.varianceTimeSpentInQueue = Double.parseDouble(token);
        }
    }};

    public static String toString(CollectedRewardProgramTriggerSummaryDataIce record) {
        StringBuilder builder = new StringBuilder();
        builder.append(record.id).append('\t').append(DateTimeUtils.getStringForTimestamp(new Date(record.createTimestamp))).append('\t').append(record.createTimestamp).append('\t').append(record.host).append('\t').append(record.instance).append('\t').append(record.programType).append('\t').append(record.minReceivedTimestamp).append('\t').append(record.maxReceivedTimestamp).append('\t').append(record.receivedCount).append('\t').append(record.droppedCount).append('\t').append(record.successfulCount).append('\t').append(record.failedCount).append('\t').append(record.dequeuedCount).append('\t').append(record.minTimeSpentInQueue).append('\t').append(record.maxTimeSpentInQueue).append('\t').append(record.meanTimeSpentInQueue).append('\t').append(record.varianceProcessingTimeAfterDequeue).append('\t').append(record.minProcessingTimeAfterDequeue).append('\t').append(record.maxProcessingTimeAfterDequeue).append('\t').append(record.meanProcessingTimeAfterDequeue).append('\t').append(record.varianceTimeSpentInQueue);
        return builder.toString();
    }

    public static CollectedRewardProgramTriggerSummaryDataIce fromString(String lineRecord) throws ParseException {
        CollectedRewardProgramTriggerSummaryDataIce record = new CollectedRewardProgramTriggerSummaryDataIce();
        StringTokenizer tokenizer = new StringTokenizer(lineRecord, "\t");
        int tokenNumber = 0;
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            System.out.println(tokenNumber + "=" + token);
            try {
                parsers[tokenNumber].parse(record, tokenNumber, token);
            }
            catch (Throwable t) {
                ParseException e = new ParseException("Unable to parse token number [" + tokenNumber + "] token [" + token + "]", tokenNumber);
                e.initCause(t);
                throw e;
            }
            ++tokenNumber;
        }
        return record;
    }

    private static interface Parser {
        public void parse(CollectedRewardProgramTriggerSummaryDataIce var1, int var2, String var3) throws Throwable;
    }
}

