/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.rewardsystem;

import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.rewardsystem.RewardCentre;
import com.projectgoth.fusion.rewardsystem.outcomes.RewardProgramOutcomeData;
import com.projectgoth.fusion.rewardsystem.outcomes.RewardProgramOutcomeProcessor;
import com.projectgoth.fusion.rewardsystem.outcomes.RewardProgramOutcomeProcessorInstances;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class RewardProgramOutcomes {
    public static final RewardProgramOutcomes EMPTY = new RewardProgramOutcomes(Collections.EMPTY_LIST, false);
    private final List<RewardProgramOutcomeData> outcomeList;
    private final boolean templateDataRequired;

    protected RewardProgramOutcomes(List<RewardProgramOutcomeData> outcomeList, boolean requiresTemplateData) {
        this.outcomeList = outcomeList == null ? Collections.EMPTY_LIST : outcomeList;
        this.templateDataRequired = requiresTemplateData;
    }

    public List<RewardProgramOutcomeData> getOutcomeList() {
        return this.outcomeList;
    }

    public boolean isTemplateDataRequired() {
        return this.templateDataRequired;
    }

    public static RewardProgramOutcomes getOutcomes(RewardProgramOutcomeProcessorInstances outcomeProcessorInstances, RewardProgramData program, RewardProgramTrigger trigger) throws Exception {
        List<RewardProgramOutcomeProcessor> rewardOutcomeProcessors = RewardProgramOutcomes.getOutcomeProcessors(outcomeProcessorInstances, program);
        if (rewardOutcomeProcessors != null && !rewardOutcomeProcessors.isEmpty()) {
            boolean requiresTemplateData = false;
            ArrayList<RewardProgramOutcomeData> outcomeDataList = new ArrayList<RewardProgramOutcomeData>(rewardOutcomeProcessors.size());
            for (RewardProgramOutcomeProcessor outcomeProcessor : rewardOutcomeProcessors) {
                RewardProgramOutcomeData outcomeData = outcomeProcessor.getOutcome(program, trigger);
                if (outcomeData == null) continue;
                outcomeDataList.add(outcomeData);
                requiresTemplateData |= outcomeData.requiresTemplateData();
            }
            return new RewardProgramOutcomes(outcomeDataList, requiresTemplateData);
        }
        return EMPTY;
    }

    private static List<RewardProgramOutcomeProcessor> getOutcomeProcessors(RewardProgramOutcomeProcessorInstances outcomeProcessorInstances, RewardProgramData programData) {
        List<String> processorClassNames = programData.getOutcomeProcessorClassNames();
        ArrayList<RewardProgramOutcomeProcessor> outcomeProcessors = new ArrayList<RewardProgramOutcomeProcessor>(processorClassNames.size());
        for (String processorClassName : processorClassNames) {
            try {
                outcomeProcessors.add(outcomeProcessorInstances.getInstance(processorClassName));
            }
            catch (Exception ex) {
                RewardCentre.log.error((Object)("Unable to get outcome processor :[" + processorClassName + "]  Exception:[" + ex + "]"), (Throwable)ex);
            }
        }
        return outcomeProcessors;
    }
}

