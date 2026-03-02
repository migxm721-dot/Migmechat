/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.rewardsystem;

import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.rewardsystem.instrumentation.MetricsSink;
import com.projectgoth.fusion.rewardsystem.instrumentation.ProcessingResultEnum;
import com.projectgoth.fusion.rewardsystem.instrumentation.RewardProgramTriggerSampleCategory;
import com.projectgoth.fusion.rewardsystem.instrumentation.Sample;
import com.projectgoth.fusion.rewardsystem.instrumentation.SampleCategory;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;

public class TriggerProcessingContext {
    private final RewardProgramData.TypeEnum triggerType;
    private volatile ProcessingResultEnum processingResult;
    private volatile long receivedTime;
    private volatile long dequeuedTime;
    private volatile long endTime;
    private volatile boolean received = false;
    private volatile boolean dequeued = false;
    private volatile boolean ended = false;

    public TriggerProcessingContext(RewardProgramTrigger trigger) {
        this.triggerType = trigger.programType;
    }

    public long getReceivedTime() {
        return this.receivedTime;
    }

    public long getDequeuedTime() {
        return this.dequeuedTime;
    }

    public long getEndTime() {
        return this.endTime;
    }

    public ProcessingResultEnum getProcessingResult() {
        return this.processingResult;
    }

    public RewardProgramData.TypeEnum getTriggerType() {
        return this.triggerType;
    }

    protected long getCurrentTime() {
        return System.currentTimeMillis();
    }

    private void end(ProcessingResultEnum processingResult) {
        this.processingResult = processingResult;
        this.endTime = this.getCurrentTime();
        this.ended = true;
        this.logMetrics();
    }

    public final void received() {
        if (this.received) {
            throw new IllegalStateException("Already received");
        }
        if (this.dequeued) {
            throw new IllegalStateException("Processing has started");
        }
        if (this.ended) {
            throw new IllegalStateException("Processing has ended");
        }
        this.received = true;
        this.receivedTime = this.getCurrentTime();
    }

    public final void dequeued() {
        if (!this.received) {
            throw new IllegalStateException("Not yet received");
        }
        if (this.dequeued) {
            throw new IllegalStateException("Processing has started");
        }
        if (this.ended) {
            throw new IllegalStateException("Processing has ended");
        }
        this.dequeued = true;
        this.dequeuedTime = this.getCurrentTime();
    }

    public final void dropped() {
        if (!this.received) {
            throw new IllegalStateException("Not yet received");
        }
        if (this.dequeued) {
            throw new IllegalStateException("Processing has started");
        }
        if (this.ended) {
            throw new IllegalStateException("Processing has ended");
        }
        this.end(ProcessingResultEnum.DROPPED);
    }

    public final void failed() {
        if (!this.received) {
            throw new IllegalStateException("Not yet received");
        }
        if (!this.dequeued) {
            throw new IllegalStateException("Processing has not started");
        }
        if (this.ended) {
            throw new IllegalStateException("Processing has ended");
        }
        this.end(ProcessingResultEnum.FAILED);
    }

    public final void successful() {
        if (!this.received) {
            throw new IllegalStateException("Not yet received");
        }
        if (!this.dequeued) {
            throw new IllegalStateException("Processing has not started");
        }
        if (this.ended) {
            throw new IllegalStateException("Processing has ended");
        }
        this.end(ProcessingResultEnum.SUCCESSFUL);
    }

    private Sample getSample() {
        return new Sample(){
            private final TriggerProcessingContext ctx;
            {
                this.ctx = TriggerProcessingContext.this;
            }

            public SampleCategory getSampleCategory() {
                return new RewardProgramTriggerSampleCategory(this.ctx.triggerType);
            }

            public long getReceivedTimestamp() {
                return this.ctx.getReceivedTime();
            }

            public ProcessingResultEnum getProcessingResult() {
                return this.ctx.getProcessingResult();
            }

            public long getEndProcessTimestamp() {
                return this.ctx.getEndTime();
            }

            public long getDequeuedTimestamp() {
                return this.ctx.getDequeuedTime();
            }

            public String toString() {
                return "TriggerProcessingContext.Sample:(Trigger Type:[" + this.ctx.getTriggerType() + "]. Result:[" + (Object)((Object)this.ctx.getProcessingResult()) + "])";
            }
        };
    }

    public final void logMetrics() {
        MetricsSink.getInstance().write(this.getSample());
    }
}

