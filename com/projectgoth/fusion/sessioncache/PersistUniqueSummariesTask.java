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
import com.projectgoth.fusion.dao.UserDAO;
import com.projectgoth.fusion.domain.CountryLogins;
import com.projectgoth.fusion.sessioncache.SessionCacheI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class PersistUniqueSummariesTask
extends TimerTask {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(PersistUniqueSummariesTask.class));
    private SessionCacheI sessionCacheI;
    private SessionSummaryDAO sessionSummaryDAO;
    private UserDAO misUserDAO;
    private List<Integer> periods;
    private TransactionTemplate transactionTemplate;
    private boolean startupRun = false;

    public PersistUniqueSummariesTask(SessionCacheI sessionCacheI, SessionSummaryDAO sessionSummaryDAO, UserDAO userDAO, TransactionTemplate transactionTemplate, List<Integer> periods) {
        this.sessionCacheI = sessionCacheI;
        this.sessionSummaryDAO = sessionSummaryDAO;
        this.misUserDAO = userDAO;
        this.periods = periods;
        this.transactionTemplate = transactionTemplate;
    }

    public PersistUniqueSummariesTask(SessionCacheI sessionCacheI, SessionSummaryDAO sessionSummaryDAO, UserDAO userDAO, TransactionTemplate transactionTemplate, List<Integer> periods, boolean startupRun) {
        this.sessionCacheI = sessionCacheI;
        this.sessionSummaryDAO = sessionSummaryDAO;
        this.misUserDAO = userDAO;
        this.periods = periods;
        this.transactionTemplate = transactionTemplate;
        this.startupRun = startupRun;
    }

    private void persistSummaries() throws Exception {
        final Date midnightYesterday = DateTimeUtils.midnightYesterday();
        final Date now = new Date();
        final Date startDateSource = this.startupRun ? DateTimeUtils.midnightToday() : new Date();
        if (this.startupRun && this.sessionSummaryDAO.yesterdaysTotalsExists(midnightYesterday)) {
            log.info((Object)"No need to run PersistUniqueSummariesTask on startup since we've already ran for yesterday, returning...");
            return;
        }
        final HashMap authenticatedLogins = new HashMap();
        final HashMap nonAuthenticatedLogins = new HashMap();
        log.info((Object)("Examing " + this.periods.size() + " periods"));
        for (Integer period : this.periods) {
            List<CountryLogins> list = this.misUserDAO.findRecentLoginsGroupedByCountry(startDateSource, now, period);
            log.info((Object)("Got " + (list == null ? 0 : list.size()) + " country login records for period " + period));
            authenticatedLogins.put(period, new ArrayList());
            nonAuthenticatedLogins.put(period, new ArrayList());
            for (CountryLogins countryLogin : list) {
                if (countryLogin.getMobileVerified().booleanValue()) {
                    ((List)authenticatedLogins.get(period)).add(countryLogin);
                    continue;
                }
                ((List)nonAuthenticatedLogins.get(period)).add(countryLogin);
            }
        }
        this.transactionTemplate.execute((TransactionCallback)new TransactionCallbackWithoutResult(){

            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    if (!PersistUniqueSummariesTask.this.sessionSummaryDAO.rowsExist(midnightYesterday)) {
                        if (log.isDebugEnabled()) {
                            log.debug((Object)("creating new daily rows for [" + midnightYesterday + "]"));
                        }
                        PersistUniqueSummariesTask.this.sessionSummaryDAO.createDailyRows(midnightYesterday);
                    }
                    for (Integer period : PersistUniqueSummariesTask.this.periods) {
                        if (log.isDebugEnabled()) {
                            log.debug((Object)("calculating unique totals from [" + DateTimeUtils.minusDays(startDateSource, period) + "] until [" + now + "] " + "for period date starting [" + midnightYesterday + "] and period [" + period + "]"));
                        }
                        PersistUniqueSummariesTask.this.sessionSummaryDAO.updateUniqueTotals(midnightYesterday, period, true, (List)authenticatedLogins.get(period));
                        PersistUniqueSummariesTask.this.sessionSummaryDAO.updateUniqueTotals(midnightYesterday, period, false, (List)nonAuthenticatedLogins.get(period));
                    }
                }
                catch (Exception e) {
                    log.error((Object)("Unable to persist unique totals from SOURCE [" + startDateSource + "] to [" + now + "] for period [" + midnightYesterday + "]"), (Throwable)e);
                    status.setRollbackOnly();
                }
            }
        });
    }

    @Override
    public void run() {
        try {
            this.sessionCacheI.setUniqueSummariesTaskRunning(true);
            log.info((Object)("persisting summary uniques, startupRun [" + this.startupRun + "]"));
            long now = System.currentTimeMillis();
            this.persistSummaries();
            log.info((Object)("done persisting summary uniques, it took " + (System.currentTimeMillis() - now) / 60000L + " minutes"));
            this.sessionCacheI.setUniqueSummariesTaskRunning(false);
        }
        catch (Exception e) {
            log.error((Object)"Unable to persist unique summary totals", (Throwable)e);
        }
    }
}

