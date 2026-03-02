/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.springframework.context.ApplicationContext
 *  redis.clients.jedis.Jedis
 *  redis.clients.jedis.PipelineBlock
 */
package com.projectgoth.fusion.fashionshow;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.Redis;
import com.projectgoth.fusion.dao.FashionShowDAO;
import com.projectgoth.fusion.jobscheduling.JobSchedulingServiceContext;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.PipelineBlock;

public class AvatarCandidates {
    private static final String KEY = "AvatarCandidates";
    private static final String KEY_TMP = "AvatarCandidates_tmp";
    private static final String FSHOW_KEY = "FashionShow";
    private static final String FIELD_LEVEL = "ReqdLevel";
    private static final String FIELD_ITEMS = "ReqdAvtrItems";
    private static final String FIELD_DAYS = "ReqdActiveDays";
    private static final String DEFAULT_MIG_LEVEL = "1";
    private static final String DEFAULT_DAYS = "14";
    private static final String DEFAULT_ITEMS = "2";
    private static final int CHUNK_SIZE = 250;
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(AvatarCandidates.class));

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static void populateAvatarCandidates() throws Exception {
        String item_count;
        String active_days;
        String reqd_level;
        Jedis masterInst;
        block13: {
            masterInst = null;
            reqd_level = DEFAULT_MIG_LEVEL;
            active_days = DEFAULT_DAYS;
            item_count = DEFAULT_ITEMS;
            try {
                Object var6_6;
                try {
                    masterInst = Redis.getGamesSlaveInstance();
                    Map param = masterInst.hgetAll(FSHOW_KEY);
                    reqd_level = (String)param.get(FIELD_LEVEL);
                    active_days = (String)param.get(FIELD_DAYS);
                    item_count = (String)param.get(FIELD_ITEMS);
                    if (reqd_level == null || reqd_level.length() == 0) {
                        reqd_level = DEFAULT_MIG_LEVEL;
                    }
                    if (active_days == null || active_days.length() == 0) {
                        active_days = DEFAULT_DAYS;
                    }
                    if (item_count == null || item_count.length() == 0) {
                        item_count = DEFAULT_ITEMS;
                    }
                    log.info((Object)("Required migLevel: " + reqd_level));
                    log.info((Object)("Logged in the past " + active_days + " day(s)"));
                    log.info((Object)("Required number of avatar items: " + item_count));
                }
                catch (Exception e) {
                    log.error((Object)("Error while reading the mig level/active days/item count for fashion show: " + e.getMessage()));
                    var6_6 = null;
                    Redis.disconnect(masterInst, log);
                    break block13;
                }
                var6_6 = null;
            }
            catch (Throwable throwable) {
                Object var6_7 = null;
                Redis.disconnect(masterInst, log);
                throw throwable;
            }
            Redis.disconnect(masterInst, log);
        }
        ApplicationContext context = JobSchedulingServiceContext.getContext();
        FashionShowDAO fashionShowDAO = (FashionShowDAO)context.getBean("fashionShowDAO");
        try {
            try {
                final List<String> users = fashionShowDAO.getAvatarCandidatesForRedis(Integer.parseInt(active_days), Integer.parseInt(reqd_level), Integer.parseInt(item_count));
                masterInst = Redis.getGamesMasterInstance();
                log.info((Object)("Number of candidates in the pool before population: " + masterInst.zcard(KEY)));
                masterInst.del(KEY_TMP);
                for (int i = 0; i < users.size(); i += 250) {
                    final int idx = i;
                    masterInst.pipelined(new PipelineBlock(){

                        public void execute() {
                            int j = idx;
                            for (int k = 0; k < 250 && j + k < users.size(); ++k) {
                                this.zadd(AvatarCandidates.KEY_TMP, Math.random(), (String)users.get(j + k));
                            }
                        }
                    });
                }
                masterInst.rename(KEY_TMP, KEY);
                log.info((Object)("Number of candidates in the pool after population: " + masterInst.zcard(KEY)));
            }
            catch (Exception e) {
                log.error((Object)("Pipeline execution error: " + e.getMessage()));
                Object var10_14 = null;
                Redis.disconnect(masterInst, log);
                return;
            }
            Object var10_13 = null;
        }
        catch (Throwable throwable) {
            Object var10_15 = null;
            Redis.disconnect(masterInst, log);
            throw throwable;
        }
        Redis.disconnect(masterInst, log);
    }
}

