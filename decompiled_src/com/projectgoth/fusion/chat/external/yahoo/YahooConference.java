/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.chat.external.yahoo;

import java.util.LinkedList;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class YahooConference {
    private List<String> participants = new LinkedList<String>();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addParticipant(String participant) {
        List<String> list = this.participants;
        synchronized (list) {
            if (!this.participants.contains(participant)) {
                this.participants.add(participant);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeParticipant(String participant) {
        List<String> list = this.participants;
        synchronized (list) {
            this.participants.remove(participant);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<String> getParticipants() {
        List<String> list = this.participants;
        synchronized (list) {
            return this.participants;
        }
    }
}

