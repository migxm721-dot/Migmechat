/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.chatnewsfeed.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class ExtensionFilter
implements FilenameFilter {
    private List<String> extensions;
    private List<String> exclusions;

    public ExtensionFilter(List<String> extensions, List<String> exclusions) {
        this.extensions = extensions == null ? new ArrayList() : extensions;
        this.exclusions = exclusions == null ? new ArrayList() : exclusions;
    }

    @Override
    public boolean accept(File dir, String name) {
        boolean extensionValid = false;
        for (String extension : this.extensions) {
            if (!name.endsWith(extension)) continue;
            extensionValid = true;
            break;
        }
        boolean exclude = false;
        for (String exclusion : this.exclusions) {
            if (!name.equals(exclusion)) continue;
            exclude = true;
            break;
        }
        return extensionValid && !exclude;
    }
}

