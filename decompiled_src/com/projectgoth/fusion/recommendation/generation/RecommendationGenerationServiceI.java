/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.recommendation.generation;

import Ice.Current;
import com.projectgoth.fusion.app.dao.DAOFactory;
import com.projectgoth.fusion.common.IcePrxFinder;
import com.projectgoth.fusion.common.ServiceStatsFactory;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.recommendation.RecommendationTransform;
import com.projectgoth.fusion.recommendation.generation.RecommendationGenerationService;
import com.projectgoth.fusion.recommendation.generation.RecommendationGenerationTaskHS2;
import com.projectgoth.fusion.slice.BaseServiceStats;
import com.projectgoth.fusion.slice.RecommendationGenerationServiceStats;
import com.projectgoth.fusion.slice._RecommendationGenerationServiceDisp;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.Logger;

public class RecommendationGenerationServiceI
extends _RecommendationGenerationServiceDisp {
    private static final Logger log = Logger.getLogger(RecommendationGenerationServiceI.class);
    private IcePrxFinder icePrxFinder;
    private List<RecommendationTransform> transforms;
    private List<Timer> scheduledTimers;
    private final RecommendationGenerationServiceStats stats = new RecommendationGenerationServiceStats();

    public void runTransformation(int transfmID, Current current) {
        log.info((Object)("INVOKED VIA JOB SCHEDULER: transformation" + transfmID));
        RecommendationGenerationTaskHS2 task = new RecommendationGenerationTaskHS2(transfmID);
        task.transform();
    }

    public void initialize() throws Exception {
        this.transforms = DAOFactory.getInstance().getRecommendationDAO().getActiveRecommendationTransforms();
        if (log.isDebugEnabled()) {
            log.debug((Object)("Loaded " + this.transforms.size() + " transformations"));
        }
        this.scheduleTasks();
    }

    public void reinitialize() throws Exception {
        boolean reschedule;
        List<RecommendationTransform> newTransforms = DAOFactory.getInstance().getRecommendationDAO().getActiveRecommendationTransforms();
        boolean bl = reschedule = this.transforms.size() != newTransforms.size();
        if (!reschedule) {
            for (int i = 0; i < newTransforms.size(); ++i) {
                RecommendationTransform newT = newTransforms.get(i);
                RecommendationTransform oldT = this.transforms.get(i);
                if (newT.getID() == oldT.getID() && newT.getRunInterval() == oldT.getRunInterval()) continue;
                reschedule = true;
                break;
            }
        }
        if (reschedule) {
            for (Timer timer : this.scheduledTimers) {
                timer.cancel();
            }
            this.transforms = newTransforms;
            this.scheduleTasks();
        }
    }

    private void scheduleTasks() {
        try {
            int gap = SystemProperty.getInt(SystemPropertyEntities.RecommendationServiceSettings.RGS_INTERNAL_TIMER_SCHEDULE_GAP_SECONDS);
            this.scheduledTimers = new ArrayList<Timer>();
            int delayIndex = 0;
            for (RecommendationTransform transform : this.transforms) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Processing recommendation transformation: id=" + transform.getID()));
                }
                Timer timer = new Timer(true);
                RecommendationGenerationTaskHS2 task = new RecommendationGenerationTaskHS2(transform.getID());
                timer.schedule((TimerTask)task, delayIndex++ * gap * 1000, transform.getRunInterval() * 1000L);
                this.scheduledTimers.add(timer);
                log.info((Object)("Scheduled task for transform " + transform.getID() + "(" + transform.getName() + ") with interval=" + transform.getRunInterval()));
            }
        }
        catch (Exception e) {
            log.error((Object)("Exception scheduling Recommendation Generation tasks: e=" + e), (Throwable)e);
        }
    }

    void shutdown() {
        log.info((Object)"RecommendationGenerationService interrupted, shutting down...");
    }

    public void setIcePrxFinder(IcePrxFinder icePrxFinder) {
        this.icePrxFinder = icePrxFinder;
    }

    public RecommendationGenerationServiceStats getStats() {
        BaseServiceStats baseStats = ServiceStatsFactory.getBaseServiceStats(RecommendationGenerationService.startTime);
        this.stats.hostname = baseStats.hostname;
        this.stats.version = baseStats.version;
        this.stats.jvmTotalMemory = baseStats.jvmTotalMemory;
        this.stats.jvmFreeMemory = baseStats.jvmFreeMemory;
        this.stats.uptime = baseStats.uptime;
        RecommendationGenerationTaskHS2.getStats(this.stats);
        return this.stats;
    }
}

