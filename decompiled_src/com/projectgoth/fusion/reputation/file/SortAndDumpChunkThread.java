/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.reputation.file;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.StringUtil;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SortAndDumpChunkThread
extends Thread {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(SortAndDumpChunkThread.class));
    private List<String> lines;
    private int expectedFieldCount;
    private Comparator<List<String>> comparator;
    private BufferedWriter writer;
    private char delimeter;
    private boolean checkExpectedFieldCount;

    public SortAndDumpChunkThread(List<String> lines, int expectedFieldCount, Comparator<List<String>> comparator, BufferedWriter writer, char delimeter, boolean checkExpectedFieldCount) {
        this.lines = lines;
        this.expectedFieldCount = expectedFieldCount;
        this.comparator = comparator;
        this.writer = writer;
        this.delimeter = delimeter;
        this.checkExpectedFieldCount = checkExpectedFieldCount;
    }

    @Override
    public void run() {
        ArrayList<List<String>> linesParts = new ArrayList<List<String>>();
        for (String string : this.lines) {
            List<String> list = StringUtil.split(string, this.delimeter);
            if (this.checkExpectedFieldCount && list.size() != this.expectedFieldCount) {
                log.warn((Object)("found more or less parts [" + list.size() + "] in this line [" + list + "]"));
            }
            linesParts.add(list);
        }
        Collections.sort(linesParts, this.comparator);
        try {
            for (List list : linesParts) {
                for (int i = 0; i < list.size(); ++i) {
                    this.writer.write((String)list.get(i));
                    if (i >= list.size() - 1) continue;
                    this.writer.write(this.delimeter);
                }
                this.writer.newLine();
            }
            this.writer.flush();
            this.writer.close();
        }
        catch (IOException e) {
            log.error((Object)"failed to write chunk!", (Throwable)e);
        }
    }
}

