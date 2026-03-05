/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.rewardsystem.outcomes;

import com.projectgoth.fusion.rewardsystem.outcomes.RewardProgramOutcomeProcessor;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;

public class RewardProgramOutcomeProcessorInstances {
    private ConcurrentHashMap<String, RewardProgramOutcomeProcessor> outcomeProcessorInstances = new ConcurrentHashMap();

    public RewardProgramOutcomeProcessor getInstance(String className) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        RewardProgramOutcomeProcessor processor = this.outcomeProcessorInstances.get(className);
        if (processor == null) {
            Class<?> processorClass = Class.forName(className);
            RewardProgramOutcomeProcessor newProcessor = (RewardProgramOutcomeProcessor)processorClass.newInstance();
            RewardProgramOutcomeProcessor oldProcessor = this.outcomeProcessorInstances.putIfAbsent(className, newProcessor);
            processor = oldProcessor == null ? newProcessor : oldProcessor;
        }
        return processor;
    }

    public boolean canInstantiate(Logger logger, String className) {
        try {
            this.getInstance(className);
            return true;
        }
        catch (Exception ex) {
            logger.error((Object)("Unable to instantiate [" + className + "].Exception:[" + ex + "]"), (Throwable)ex);
            return false;
        }
    }
}

