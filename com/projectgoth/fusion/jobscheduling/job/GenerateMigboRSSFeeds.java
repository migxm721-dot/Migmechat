/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.json.JSONArray
 *  org.json.JSONObject
 *  org.quartz.Job
 *  org.quartz.JobExecutionContext
 *  org.quartz.JobExecutionException
 */
package com.projectgoth.fusion.jobscheduling.job;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.restapi.util.MigboApiUtil;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Semaphore;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class GenerateMigboRSSFeeds
implements Job {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(GenerateMigboRSSFeeds.class));
    private static final Semaphore semaphore = new Semaphore(1);

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        boolean semaphoreAquired = false;
        try {
            try {
                semaphoreAquired = semaphore.tryAcquire();
                if (!semaphoreAquired) {
                    log.warn((Object)"Another job is still generating RSS Feeds. Exiting...");
                    Object var19_3 = null;
                    if (!semaphoreAquired) return;
                    semaphore.release();
                    return;
                }
                log.info((Object)"RSS Feeds Generation [START]");
                MigboApiUtil apiUtil = MigboApiUtil.getInstance();
                log.info((Object)"Retrieving list of RSSFeedForUsers from Migbo Dataservice");
                JSONObject obj = apiUtil.get("/user/@all/rss");
                log.debug((Object)String.format("Received JSON Response fro migbo-datsvc : %s ", obj.toString()));
                JSONObject data = obj.getJSONObject("data");
                JSONArray rssList = data.getJSONArray("rss");
                HashMap<String, HashSet<String>> map = new HashMap<String, HashSet<String>>();
                for (int i = 0; i < rssList.length(); ++i) {
                    JSONObject rssFeedForUser = rssList.getJSONObject(i);
                    int userid = rssFeedForUser.getInt("userId");
                    String feedURL = rssFeedForUser.getString("url");
                    log.debug((Object)String.format("Found userid[%d] url [%s]", userid, feedURL));
                    HashSet<String> s = (HashSet<String>)map.get("" + userid);
                    if (s == null) {
                        s = new HashSet<String>();
                        map.put("" + userid, s);
                    }
                    s.add(feedURL);
                }
                log.info((Object)String.format("Found %d users to Process", map.size()));
                for (String useridstr : map.keySet()) {
                    Set urls = (Set)map.get(useridstr);
                    int userid = StringUtil.toIntOrDefault(useridstr, -1);
                    if (userid == -1) {
                        log.warn((Object)String.format("Error parsing userid [%s] invalid userid", useridstr));
                        continue;
                    }
                    StringBuffer buffer = new StringBuffer();
                    Iterator iter = urls.iterator();
                    buffer.append("{ 'urls':[");
                    while (iter.hasNext()) {
                        buffer.append("'");
                        buffer.append((String)iter.next());
                        buffer.append("'");
                        if (!iter.hasNext()) continue;
                        buffer.append(",");
                    }
                    buffer.append("] }");
                    String postData = buffer.toString();
                    String pathPrefix = String.format("/user/%d/rss", userid);
                    boolean success = false;
                    try {
                        success = apiUtil.postAndCheckOk(pathPrefix, postData);
                    }
                    catch (MigboApiUtil.MigboApiException mapie) {
                        log.error((Object)("Unable to generate RSS Feeds -  MigboApiException: " + mapie.getMessage()), (Throwable)mapie);
                    }
                    if (success) {
                        log.info((Object)String.format("Successfully generated RSS Feeds [%s] for [%d] via migbo-datasvc [%s]", postData, userid, pathPrefix));
                        continue;
                    }
                    log.warn((Object)String.format("Failed to generate RSS Feeds [%s] for [%d] via  migbo-datasvc [%s]", postData, userid, pathPrefix));
                }
                log.info((Object)"RSS Feeds Generation [COMPLETE]");
            }
            catch (Exception e) {
                log.error((Object)("Unable to generate RSS Feeds - UnexpectedException: " + e.getMessage() + ". Terminating Job."), (Throwable)e);
                throw new JobExecutionException((Throwable)e);
            }
        }
        catch (Throwable throwable) {
            Object var19_5 = null;
            if (!semaphoreAquired) throw throwable;
            semaphore.release();
            throw throwable;
        }
        Object var19_4 = null;
        if (!semaphoreAquired) return;
        semaphore.release();
    }
}

