/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.data.pot;

import com.projectgoth.fusion.data.pot.GameSpenderData;
import com.projectgoth.fusion.data.pot.GameWinnerData;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class PayoutData
implements Serializable {
    private double totalPayoutPerUser = 0.0;
    private int botId;
    private final List<GameWinnerData> gameWinnerDataList = new ArrayList<GameWinnerData>();
    private final List<GameSpenderData> gameSpenderData = new ArrayList<GameSpenderData>();
    private final Collection<GameWinnerData> readOnlyGameWinnerDataList = Collections.unmodifiableList(this.gameWinnerDataList);
    private final Collection<GameSpenderData> readOnlyGameSpenderData = Collections.unmodifiableList(this.gameSpenderData);

    public int getBotId() {
        return this.botId;
    }

    public void setBotId(int botId) {
        this.botId = botId;
    }

    public Collection<GameWinnerData> add(GameWinnerData potWinner) {
        this.gameWinnerDataList.add(potWinner);
        return this.readOnlyGameWinnerDataList;
    }

    public Collection<GameSpenderData> add(GameSpenderData gameSpending) {
        this.gameSpenderData.add(gameSpending);
        return this.readOnlyGameSpenderData;
    }

    public Collection<GameSpenderData> getGameSpenderData() {
        return this.readOnlyGameSpenderData;
    }

    public Collection<GameWinnerData> getGameWinnerDataList() {
        return this.readOnlyGameWinnerDataList;
    }

    public void setTotalPayoutPerUser(double totalPayoutPerUser) {
        this.totalPayoutPerUser = totalPayoutPerUser;
    }

    public double getTotalPayoutPerUser() {
        return this.totalPayoutPerUser;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PayoutData [totalPayoutPerUser=");
        builder.append(this.totalPayoutPerUser);
        builder.append(", botId=");
        builder.append(this.botId);
        builder.append(", gameWinnerDataList=");
        builder.append(this.gameWinnerDataList);
        builder.append(", gameSpenderData=");
        builder.append(this.gameSpenderData);
        builder.append(", readOnlyGameWinnerDataList=");
        builder.append(this.readOnlyGameWinnerDataList);
        builder.append(", readOnlyGameSpenderData=");
        builder.append(this.readOnlyGameSpenderData);
        builder.append("]");
        return builder.toString();
    }
}

