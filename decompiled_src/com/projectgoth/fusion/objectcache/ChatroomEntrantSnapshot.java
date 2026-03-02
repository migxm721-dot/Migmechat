/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.objectcache;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ChatroomEntrantSnapshot {
    private int recentEntrantBufferSize = 50;
    private int lockPeriod = 45;
    private long chatroomLockExpiry = 0L;
    private ArrayList<Entrant> entrantsSnapshot = new ArrayList(this.recentEntrantBufferSize);
    private LinkedHashMap<String, Entrant> recentEntrants = new LinkedHashMap<String, Entrant>(){

        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Entrant> eldest) {
            return this.size() > ChatroomEntrantSnapshot.this.recentEntrantBufferSize;
        }
    };

    public ChatroomEntrantSnapshot(int entrantBufferSize, int lockPeriod) {
        this.recentEntrantBufferSize = entrantBufferSize;
        this.lockPeriod = lockPeriod;
    }

    public void addEntrant(String username, int level, String ip) {
        Entrant entrant = new Entrant(username, level, ip);
        this.recentEntrants.put(username, entrant);
    }

    public String getEntrantListStr(int size, int startIndex) {
        String entrantDetails = "";
        int count = 0;
        for (int i = startIndex; i < this.entrantsSnapshot.size() && count < size; ++count, ++i) {
            Entrant entrant = this.entrantsSnapshot.get(i);
            entrantDetails = entrantDetails + (i + 1) + ". " + entrant.getUsername() + " " + entrant.getLevel() + " \n";
        }
        return entrantDetails;
    }

    public void initLockExpiry() {
        this.clearSnapshot();
        int i = 1;
        for (Map.Entry<String, Entrant> entry : this.recentEntrants.entrySet()) {
            this.entrantsSnapshot.add(entry.getValue());
            ++i;
        }
        this.chatroomLockExpiry = System.currentTimeMillis() + (long)(this.lockPeriod * 1000);
    }

    public boolean hasLockExpired() {
        return System.currentTimeMillis() > this.chatroomLockExpiry;
    }

    public boolean isCurrentSnapshotRunning() {
        return this.entrantsSnapshot != null && this.entrantsSnapshot.size() > 0;
    }

    public void clearSnapshot() {
        this.entrantsSnapshot.clear();
    }

    public String[] getSnapshotUsernamesFromIndexes(int[] indexes) {
        ArrayList<String> bannedList = new ArrayList<String>(indexes.length);
        for (int i = 0; i < indexes.length; ++i) {
            try {
                String username = this.entrantsSnapshot.get(indexes[i] - 1).username;
                bannedList.add(username);
                continue;
            }
            catch (IndexOutOfBoundsException e) {
                // empty catch block
            }
        }
        String[] bannedUsernames = new String[bannedList.size()];
        bannedList.toArray(bannedUsernames);
        return bannedUsernames;
    }

    private class Entrant {
        private String username;
        private int level;
        private String ip;

        public Entrant(String username, int level, String ip) {
            this.username = username;
            this.level = level;
            this.ip = ip;
        }

        public String getUsername() {
            return this.username;
        }

        public int getLevel() {
            return this.level;
        }

        public String getIp() {
            return this.ip;
        }
    }
}

