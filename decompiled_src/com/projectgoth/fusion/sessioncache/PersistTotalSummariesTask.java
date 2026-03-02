/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.springframework.transaction.TransactionStatus
 *  org.springframework.transaction.support.TransactionCallback
 *  org.springframework.transaction.support.TransactionCallbackWithoutResult
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.projectgoth.fusion.sessioncache;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.DateTimeUtils;
import com.projectgoth.fusion.dao.SessionSummaryDAO;
import com.projectgoth.fusion.sessioncache.TotalCountrySessions;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class PersistTotalSummariesTask
extends TimerTask {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(PersistTotalSummariesTask.class));
    private Map<Integer, TotalCountrySessions> totalSessions;
    private SessionSummaryDAO sessionSummaryDAO;
    private TransactionTemplate transactionTemplate;

    public PersistTotalSummariesTask(Map<Integer, TotalCountrySessions> totalSessions, SessionSummaryDAO sessionSummaryDAO, TransactionTemplate transactionTemplate) {
        this.totalSessions = totalSessions;
        this.sessionSummaryDAO = sessionSummaryDAO;
        this.transactionTemplate = transactionTemplate;
    }

    private void persistSummaries() throws Exception {
        final Date midnightToday = DateTimeUtils.midnightToday();
        this.transactionTemplate.execute((TransactionCallback)new TransactionCallbackWithoutResult(){
            private final Map<Integer, TotalCountrySessions> tempSessions;
            private final Map<Integer, TotalCountrySessions> completedSessions;
            private Integer country;
            private int totalAuthenticated;
            private int totalNonAuthenticated;
            {
                this.tempSessions = new HashMap<Integer, TotalCountrySessions>(PersistTotalSummariesTask.this.totalSessions.size() + 1);
                this.completedSessions = new HashMap<Integer, TotalCountrySessions>(PersistTotalSummariesTask.this.totalSessions.size() + 1);
                this.country = null;
                this.totalAuthenticated = 0;
                this.totalNonAuthenticated = 0;
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            private void persistData(TransactionStatus status) {
                block7: {
                    try {
                        for (Integer this.country : this.tempSessions.keySet()) {
                            TotalCountrySessions previousTotalSessions = this.tempSessions.get(this.country);
                            this.totalAuthenticated = previousTotalSessions.getTotalAuthenticatedSessions().intValue();
                            this.totalNonAuthenticated = previousTotalSessions.getTotalNonAuthenticatedSessions().intValue();
                            if (this.totalAuthenticated <= 0 && this.totalNonAuthenticated <= 0) continue;
                            PersistTotalSummariesTask.this.sessionSummaryDAO.updateTotalSession(midnightToday, this.country, this.totalAuthenticated, this.totalNonAuthenticated);
                            this.completedSessions.put(this.country, new TotalCountrySessions(this.totalAuthenticated, this.totalNonAuthenticated));
                        }
                    }
                    catch (Exception e) {
                        log.error((Object)("Unable to persist total sessions for today [" + midnightToday + "], rolling back"), (Throwable)e);
                        status.setRollbackOnly();
                        if (this.completedSessions.isEmpty()) break block7;
                        Map map = PersistTotalSummariesTask.this.totalSessions;
                        synchronized (map) {
                            for (Integer this.country : this.completedSessions.keySet()) {
                                ((TotalCountrySessions)PersistTotalSummariesTask.this.totalSessions.get(this.country)).getTotalAuthenticatedSessions().addAndGet(this.completedSessions.get(this.country).getTotalAuthenticatedSessions().intValue());
                                ((TotalCountrySessions)PersistTotalSummariesTask.this.totalSessions.get(this.country)).getTotalNonAuthenticatedSessions().addAndGet(this.completedSessions.get(this.country).getTotalNonAuthenticatedSessions().intValue());
                            }
                        }
                    }
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("we have totals data for " + PersistTotalSummariesTask.this.totalSessions.keySet().size() + " countries"));
                }
                if (!PersistTotalSummariesTask.this.sessionSummaryDAO.rowsExist(midnightToday)) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("creating new daily rows for [" + midnightToday + "]"));
                    }
                    PersistTotalSummariesTask.this.sessionSummaryDAO.createDailyRows(midnightToday);
                }
                Map map = PersistTotalSummariesTask.this.totalSessions;
                synchronized (map) {
                    for (Integer this.country : PersistTotalSummariesTask.this.totalSessions.keySet()) {
                        TotalCountrySessions previousTotalSessions = (TotalCountrySessions)PersistTotalSummariesTask.this.totalSessions.get(this.country);
                        this.totalAuthenticated = previousTotalSessions.getTotalAuthenticatedSessions().getAndSet(0);
                        this.totalNonAuthenticated = previousTotalSessions.getTotalNonAuthenticatedSessions().getAndSet(0);
                        this.tempSessions.put(this.country, new TotalCountrySessions(this.totalAuthenticated, this.totalNonAuthenticated));
                    }
                }
                this.persistData(status);
            }
        });
    }

    @Override
    public void run() {
        try {
            log.info((Object)"persisting totals summary");
            this.persistSummaries();
        }
        catch (Exception e) {
            log.error((Object)"Unable to persist totals summary", (Throwable)e);
        }
    }
}

