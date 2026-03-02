/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.common;

import com.projectgoth.fusion.common.Sampler;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MultiSampler {
    private Map<String, Sampler> samplers = new ConcurrentHashMap<String, Sampler>();

    public void add(String category, long duration) {
        Sampler sampler = this.samplers.get(category);
        if (sampler == null && !this.samplers.containsKey(category)) {
            sampler = new Sampler(category);
            this.samplers.put(category, sampler);
        }
        sampler.add(duration);
    }

    public List<Sampler.Summary> summarize() {
        LinkedList<Sampler.Summary> summaries = new LinkedList<Sampler.Summary>();
        for (Map.Entry<String, Sampler> e : this.samplers.entrySet()) {
            Sampler.Summary summary = e.getValue().summarize();
            if (summary.count <= 0) continue;
            summaries.add(summary);
        }
        return summaries;
    }
}

