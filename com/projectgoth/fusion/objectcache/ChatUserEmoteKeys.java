/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.objectcache;

import com.projectgoth.fusion.app.dao.DAOException;
import com.projectgoth.fusion.app.dao.DAOFactory;
import com.projectgoth.fusion.app.dao.data.UserObject;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.EmoticonData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Content;
import com.projectgoth.fusion.interfaces.ContentHome;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ChatUserEmoteKeys {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ChatUserEmoteKeys.class));
    private final Set<String> hotKeys = new LinkedHashSet<String>();
    private final Set<String> alternateKeys = new LinkedHashSet<String>();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ChatUserEmoteKeys(String username) throws Exception {
        List emoticons = null;
        if (SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_USERDATA_DAO)) {
            emoticons = new UserObject(username).getEmoticons();
            if (log.isDebugEnabled()) {
                log.debug((Object)String.format("DAO: get emotions for user:%s, data:%s", username, emoticons));
            }
        } else {
            Content contentEJB = (Content)EJBHomeCache.getObject("ejb/Content", ContentHome.class);
            emoticons = contentEJB.getEmoticons(username);
        }
        if (emoticons != null) {
            Set<String> set = this.hotKeys;
            synchronized (set) {
                for (EmoticonData emoticon : emoticons) {
                    this.hotKeys.add(emoticon.hotKey);
                    this.alternateKeys.addAll(emoticon.alternateHotKeys);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String[] getEmoticonHotKeys() {
        Set<String> set = this.hotKeys;
        synchronized (set) {
            return this.hotKeys.toArray(new String[this.hotKeys.size()]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String[] getEmoticonAlternateKeys() {
        Set<String> set = this.hotKeys;
        synchronized (set) {
            return this.alternateKeys.toArray(new String[this.alternateKeys.size()]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<String> getEmoticonKeysInMessage(String msg) {
        ArrayList<String> keys = new ArrayList<String>();
        Set<String> set = this.hotKeys;
        synchronized (set) {
            for (String emoticonKey : this.hotKeys) {
                if (StringUtil.isBlank(emoticonKey) || !msg.contains(emoticonKey)) continue;
                keys.add(emoticonKey);
            }
            for (String emoticonKey : this.alternateKeys) {
                if (StringUtil.isBlank(emoticonKey) || !msg.contains(emoticonKey)) continue;
                keys.add(emoticonKey);
            }
        }
        return keys;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void packPurchased(int emoticonPackId) throws Exception {
        List emoticons = null;
        if (SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_EMOANDSTICKER_DAO)) {
            try {
                emoticons = DAOFactory.getInstance().getEmoAndStickerDAO().getEmoticonPack(emoticonPackId);
            }
            catch (DAOException e) {
                log.error((Object)String.format("DAO: Failed to get EmoticonPack for pack:%s", emoticonPackId), (Throwable)e);
            }
        } else {
            Content contentEJB = (Content)EJBHomeCache.getObject("ejb/Content", ContentHome.class);
            emoticons = contentEJB.getEmoticonPack(emoticonPackId);
        }
        Set<String> set = this.hotKeys;
        synchronized (set) {
            if (emoticons != null) {
                for (EmoticonData emoticon : emoticons) {
                    this.hotKeys.add(emoticon.hotKey);
                    this.alternateKeys.addAll(emoticon.alternateHotKeys);
                }
            }
        }
    }
}

