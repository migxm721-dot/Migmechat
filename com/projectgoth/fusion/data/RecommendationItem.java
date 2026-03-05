/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  redis.clients.jedis.Jedis
 *  redis.clients.jedis.Tuple
 */
package com.projectgoth.fusion.data;

import com.projectgoth.fusion.common.ConfigUtils;
import java.io.Serializable;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class RecommendationItem
implements Serializable,
Comparable<RecommendationItem> {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(RecommendationItem.class));
    private String value;
    private int score;
    private String reason;
    private String source;
    private boolean feature;
    private String description;
    private String redisLocation;

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RecommendationItem(String recommendation, int score) {
        this.value = recommendation;
        this.score = score;
    }

    public RecommendationItem() {
    }

    public RecommendationItem(Tuple tuple) {
        this.value = tuple.getElement();
        this.score = (int)tuple.getScore();
    }

    public RecommendationItem(Tuple tuple, Jedis redisInstance, String redisKey) {
        this(tuple);
        try {
            if (redisInstance != null && redisInstance.getClient() != null) {
                this.redisLocation = redisInstance.getClient().getHost() + ":" + redisInstance.getClient().getPort() + "//" + redisKey;
            }
        }
        catch (Exception e) {
            log.warn((Object)("Unable to set RecommendationItem.redisLocation: e=" + e), (Throwable)e);
        }
    }

    public RecommendationItem(Tuple tuple, String reason, String source) {
        this.value = tuple.getElement();
        this.score = (int)tuple.getScore();
        this.reason = reason;
        this.source = source;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isFeature() {
        return this.feature;
    }

    public void setFeature(boolean feature) {
        this.feature = feature;
    }

    @Override
    public int compareTo(RecommendationItem other) {
        return new Integer(this.score).compareTo(new Integer(other.getScore()));
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[value=");
        builder.append(this.value);
        builder.append(", score=");
        builder.append(this.score);
        builder.append("]");
        return builder.toString();
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (!(o instanceof RecommendationItem)) {
            return false;
        }
        return this.value.equals(((RecommendationItem)o).getValue());
    }

    public int hashCode() {
        return this.value == null ? -1 : this.value.hashCode();
    }

    public String getRedisLocation() {
        return this.redisLocation;
    }
}

