/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.mogilefs;

import com.projectgoth.fusion.mogilefs.BadHostFormatException;
import com.projectgoth.fusion.mogilefs.DFSManager;
import com.projectgoth.fusion.mogilefs.MogileFS;
import com.projectgoth.fusion.mogilefs.MogileOutputStream;
import com.projectgoth.fusion.mogilefs.NoTrackersException;
import com.projectgoth.fusion.mogilefs.StorageCommunicationException;
import com.projectgoth.fusion.mogilefs.TrackerCommunicationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import org.apache.log4j.Logger;

public class MogileFSManager
implements DFSManager {
    private MogileFS[] mogileFS;
    private int numberOfTrackers;
    private int lastTrackerUsed = -1;

    public MogileFSManager(String domain, String[] trackers, int connectionsPerTracker, Logger logger) throws NoTrackersException, BadHostFormatException {
        ArrayList<MogileFS> l = new ArrayList<MogileFS>();
        for (int i = 0; i < connectionsPerTracker; ++i) {
            for (int j = 0; j < trackers.length; ++j) {
                try {
                    l.add(new MogileFS(domain, new String[]{trackers[j]}, true, logger));
                    continue;
                }
                catch (Exception e) {
                    logger.warn((Object)("Failed to initialize MogileFS tracker " + trackers[j] + ". Exception: " + e.toString()));
                }
            }
        }
        this.numberOfTrackers = l.size();
        this.mogileFS = l.toArray(new MogileFS[0]);
    }

    private synchronized int getLastTrackerUsed() {
        if (this.numberOfTrackers == 0) {
            return 0;
        }
        ++this.lastTrackerUsed;
        this.lastTrackerUsed %= this.numberOfTrackers;
        return this.lastTrackerUsed;
    }

    public void storeFile(String key, String storageClass, byte[] content) throws IOException, NoTrackersException, TrackerCommunicationException, StorageCommunicationException {
        int tracker = this.getLastTrackerUsed();
        for (int i = 0; i < this.numberOfTrackers; ++i) {
            try {
                MogileOutputStream out = this.mogileFS[++tracker % this.numberOfTrackers].newFile(key, storageClass, content.length);
                ((OutputStream)out).write(content);
                ((OutputStream)out).close();
                return;
            }
            catch (NoTrackersException e) {
                if (i != this.numberOfTrackers - 1) continue;
                throw e;
            }
            catch (TrackerCommunicationException e) {
                if (i != this.numberOfTrackers - 1) continue;
                throw e;
            }
            catch (StorageCommunicationException e) {
                if (i != this.numberOfTrackers - 1) continue;
                throw e;
            }
        }
        throw new NoTrackersException();
    }

    public byte[] getFile(String key) throws NoTrackersException, TrackerCommunicationException, StorageCommunicationException, IOException, FileNotFoundException {
        int tracker = this.getLastTrackerUsed();
        for (int i = 0; i < this.numberOfTrackers; ++i) {
            try {
                byte[] content = this.mogileFS[++tracker % this.numberOfTrackers].getFileBytes(key);
                if (content == null) {
                    throw new FileNotFoundException();
                }
                return content;
            }
            catch (NoTrackersException e) {
                if (i != this.numberOfTrackers - 1) continue;
                throw e;
            }
            catch (TrackerCommunicationException e) {
                if (i != this.numberOfTrackers - 1) continue;
                throw e;
            }
            catch (StorageCommunicationException e) {
                if (i != this.numberOfTrackers - 1) continue;
                throw e;
            }
            catch (FileNotFoundException e) {
                throw e;
            }
            catch (IOException e) {
                if (i != this.numberOfTrackers - 1) continue;
                throw e;
            }
        }
        throw new NoTrackersException();
    }
}

