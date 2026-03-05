/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.StringUtils
 */
package com.projectgoth.fusion.reputation.util;

import com.projectgoth.fusion.reputation.util.DirectoryHolder;
import org.springframework.util.StringUtils;

public abstract class DirectoryUtils {
    public static String getValidDirectory(String directory) {
        if (!StringUtils.hasLength((String)directory)) {
            return directory;
        }
        if (!directory.endsWith("/")) {
            return directory + "/";
        }
        return directory;
    }

    public static String getDataDirectory() {
        String dataDirectory = System.getProperty("rep.data.dir");
        if (!StringUtils.hasLength((String)dataDirectory)) {
            dataDirectory = "/reputation/";
        }
        return DirectoryUtils.getValidDirectory(dataDirectory);
    }

    public static String getScratchDirectory() {
        String scratchDirectory = System.getProperty("rep.scratch.dir");
        if (!StringUtils.hasLength((String)scratchDirectory)) {
            scratchDirectory = "/reputation/scratch/";
        }
        return DirectoryUtils.getValidDirectory(scratchDirectory);
    }

    public static String getDumpDirectory() {
        String dumpDirectory = System.getProperty("rep.dump.dir");
        if (!StringUtils.hasLength((String)dumpDirectory)) {
            dumpDirectory = "/reputation/dump/";
        }
        return DirectoryUtils.getValidDirectory(dumpDirectory);
    }

    public static DirectoryHolder getDirectoryHolder() {
        DirectoryHolder directoryHolder = new DirectoryHolder(DirectoryUtils.getDataDirectory(), DirectoryUtils.getScratchDirectory(), DirectoryUtils.getDumpDirectory());
        return directoryHolder;
    }
}

