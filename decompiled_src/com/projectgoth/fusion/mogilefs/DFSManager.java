/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.mogilefs;

import com.projectgoth.fusion.mogilefs.NoTrackersException;
import com.projectgoth.fusion.mogilefs.StorageCommunicationException;
import com.projectgoth.fusion.mogilefs.TrackerCommunicationException;
import java.io.IOException;

public interface DFSManager {
    public void storeFile(String var1, String var2, byte[] var3) throws IOException, NoTrackersException, TrackerCommunicationException, StorageCommunicationException;
}

