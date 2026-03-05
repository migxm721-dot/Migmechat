/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.reputation.util;

import com.projectgoth.fusion.data.ReputationLevelData;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class LevelTable {
    public static SortedMap<Integer, Integer> readLevelTable(Connection connection) throws SQLException {
        TreeMap<Integer, Integer> map = new TreeMap<Integer, Integer>(Collections.reverseOrder());
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("select score,level from ReputationScoreToLevel order by score desc");
        while (rs.next()) {
            map.put(rs.getInt(1), rs.getInt(2));
        }
        return map;
    }

    public static SortedMap<Integer, ReputationLevelData> readLevelDataTable(Connection connection) throws SQLException {
        TreeMap<Integer, ReputationLevelData> map = new TreeMap<Integer, ReputationLevelData>(Collections.reverseOrder());
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("select * from ReputationScoreToLevel order by score desc");
        while (rs.next()) {
            ReputationLevelData levelData = new ReputationLevelData(rs);
            map.put(levelData.level, levelData);
        }
        return map;
    }

    public static int getLevelForScore(int score, SortedMap<Integer, Integer> levelTable) {
        for (Integer key : levelTable.keySet()) {
            if (score < key) continue;
            return (Integer)levelTable.get(key);
        }
        return 1;
    }

    public static ReputationLevelData getLevelDataForScore(int score, SortedMap<Integer, ReputationLevelData> levelDataTable) {
        for (ReputationLevelData value : levelDataTable.values()) {
            if (score < value.score) continue;
            return value;
        }
        return null;
    }

    public static int getScoreForLevel(int level, SortedMap<Integer, Integer> levelTable) {
        for (Integer key : levelTable.keySet()) {
            if (level < (Integer)levelTable.get(key)) continue;
            return key;
        }
        return 0;
    }

    public static void main(String[] args) {
    }
}

