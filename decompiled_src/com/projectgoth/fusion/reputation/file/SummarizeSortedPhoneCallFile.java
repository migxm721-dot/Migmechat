/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.reputation.file;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.reputation.domain.PhoneCallMetrics;
import com.projectgoth.fusion.reputation.util.DirectoryHolder;
import com.projectgoth.fusion.reputation.util.DirectoryUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.apache.log4j.Logger;

public class SummarizeSortedPhoneCallFile {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(SummarizeSortedPhoneCallFile.class));
    private int totalDuration = 0;
    private int totalUniqueUsers = 0;
    private DirectoryHolder directoryHolder;

    public SummarizeSortedPhoneCallFile(DirectoryHolder directoryHolder) {
        this.directoryHolder = directoryHolder;
    }

    private void dumpUserData(BufferedWriter writer, PhoneCallMetrics metrics) throws IOException {
        this.totalDuration += metrics.getDuration();
        writer.write(metrics.toLine());
        writer.newLine();
    }

    private void processFile(String filename) throws IOException {
        String line;
        log.info((Object)("summarizing [" + this.directoryHolder.getDataDirectory() + filename + "]"));
        long start = System.currentTimeMillis();
        BufferedReader reader = new BufferedReader(new FileReader(this.directoryHolder.getDataDirectory() + filename));
        BufferedWriter writer = new BufferedWriter(new FileWriter(this.directoryHolder.getDataDirectory() + filename + ".processed"));
        PhoneCallMetrics metrics = new PhoneCallMetrics();
        while ((line = reader.readLine()) != null) {
            List<String> parts = StringUtil.split(line, '|');
            if (parts.size() != 4) {
                log.info((Object)("found more or less parts [" + parts.size() + "] in this line [" + line + "], skipping"));
                continue;
            }
            String username = parts.get(1);
            if (metrics.getUsername() == null) {
                metrics.reset(username);
            }
            if (metrics.getUsername() != null) {
                int compareResult = username.compareTo(metrics.getUsername());
                if (compareResult > 0) {
                    ++this.totalUniqueUsers;
                    this.dumpUserData(writer, metrics);
                    metrics.reset(username);
                } else if (compareResult < 0) {
                    log.warn((Object)("found a smaller username after the previous one, is the file sorted incorrectly? line [" + line + "]"));
                }
            }
            metrics.addDuration(Integer.parseInt(parts.get(3)));
        }
        writer.flush();
        writer.close();
        long end = System.currentTimeMillis();
        log.info((Object)("done summarizing [" + this.directoryHolder.getDataDirectory() + filename + "], took " + (double)(end - start) / 1000.0 + " seconds"));
        log.info((Object)("total unique users " + this.totalUniqueUsers + " total duration " + this.totalDuration));
    }

    public void go(String filename) throws IOException {
        this.processFile(filename);
    }

    public static void main(String[] args) {
        SummarizeSortedPhoneCallFile summarizeFile = new SummarizeSortedPhoneCallFile(DirectoryUtils.getDirectoryHolder());
        try {
            summarizeFile.go(args[0]);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }
}

