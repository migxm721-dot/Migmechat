/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.data;

import com.projectgoth.fusion.data.RewardProgramData;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class RewardProgramProcessorMappingData
implements Serializable {
    private static final long serialVersionUID = 1L;
    RewardProgramData.TypeEnum rewardProgramType;
    List<String> mapping;

    public RewardProgramProcessorMappingData(RewardProgramData.TypeEnum programType) {
        this.rewardProgramType = programType;
        this.mapping = new LinkedList<String>();
    }

    public void addProcessorClass(String processorClassName) {
        this.mapping.add(processorClassName);
    }

    public RewardProgramData.TypeEnum getProgramType() {
        return this.rewardProgramType;
    }

    public List<String> getProcessorList() {
        return this.mapping;
    }
}

