/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.data;

import com.projectgoth.fusion.data.RecommendationItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class RecommendationData
implements Serializable {
    private List<RecommendationItem> recommendations;

    public RecommendationData() {
        this.recommendations = new ArrayList<RecommendationItem>();
    }

    public RecommendationData(List<RecommendationItem> recommendations) {
        this.recommendations = recommendations;
    }

    public List<RecommendationItem> getRecommendations() {
        return this.recommendations;
    }

    public void setRecommendations(List<RecommendationItem> recommendations) {
        this.recommendations = recommendations;
    }

    public void addRecommendationItem(RecommendationItem item) {
        this.recommendations.add(item);
    }
}

