/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.reputation.file;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.reputation.domain.VirtualGiftMetrics;
import com.projectgoth.fusion.reputation.util.DirectoryHolder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.apache.log4j.Logger;

public abstract class AbstractSummarizeSortedVirtualGiftFile {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(AbstractSummarizeSortedVirtualGiftFile.class));
    protected int totalGiftsReceived = 0;
    protected int totalGiftsSent = 0;
    protected int totalUniqueUsers = 0;
    protected DirectoryHolder directoryHolder;

    public AbstractSummarizeSortedVirtualGiftFile(DirectoryHolder directoryHolder) {
        this.directoryHolder = directoryHolder;
    }

    protected void dumpUserData(BufferedWriter writer, VirtualGiftMetrics metrics) throws IOException {
        if (metrics.hasMetrics()) {
            ++this.totalUniqueUsers;
            this.totalGiftsReceived += metrics.getVirtualGiftsReceived();
            this.totalGiftsSent += metrics.getVirtualGiftsSent();
            writer.write(metrics.toLine());
            writer.newLine();
        }
    }

    protected abstract void updateMetrics(VirtualGiftMetrics var1);

    protected abstract int usernameIndex();

    protected void processFile(String filename) throws IOException {
        String line;
        log.info((Object)("summarizing " + filename));
        long start = System.currentTimeMillis();
        BufferedReader reader = new BufferedReader(new FileReader(this.directoryHolder.getDataDirectory() + filename));
        BufferedWriter writer = new BufferedWriter(new FileWriter(this.directoryHolder.getDataDirectory() + filename + ".processed"));
        VirtualGiftMetrics metrics = new VirtualGiftMetrics();
        while ((line = reader.readLine()) != null) {
            List<String> parts = StringUtil.split(line, '|');
            if (parts.size() < 7 || parts.size() > 8) {
                log.error((Object)("found more or less parts [" + parts.size() + "] in this line [" + line + "], skipping"));
                continue;
            }
            String username = parts.get(this.usernameIndex());
            if (metrics.getUsername() == null) {
                metrics.reset(username);
            }
            if (metrics.getUsername() != null && !username.equals(metrics.getUsername())) {
                int compareResult = username.compareTo(metrics.getUsername());
                if (compareResult > 0) {
                    this.dumpUserData(writer, metrics);
                    metrics.reset(username);
                } else if (compareResult < 0) {
                    log.error((Object)("WARNING, found a smaller username after the previous one, is the file sorted incorrectly? line [" + line + "]"));
                }
            }
            this.updateMetrics(metrics);
        }
        writer.flush();
        writer.close();
        long end = System.currentTimeMillis();
        log.info((Object)("done summarizing " + filename + ", took " + (double)(end - start) / 1000.0 + " seconds"));
        log.info((Object)("total unique users " + this.totalUniqueUsers + " total gifts received " + this.totalGiftsReceived + " total gifts sent " + this.totalGiftsSent));
    }

    public void go(String filename) throws IOException {
        this.processFile(filename);
    }
}

