/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.dao;

import com.projectgoth.fusion.data.BotData;
import java.util.List;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface BotDAO {
    public List<BotData> getBots();

    public Map<String, String> getBotConfig(long var1);

    public Map<String, String> getBotCommands(long var1, String var3);

    public Map<String, String> getBotMessages(long var1, String var3);
}

