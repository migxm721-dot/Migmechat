/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.app.dao.db.ejb;

import com.projectgoth.fusion.app.dao.DAOException;
import com.projectgoth.fusion.app.dao.base.EmoAndStickerDAOChain;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Content;
import com.projectgoth.fusion.interfaces.ContentHome;
import org.apache.log4j.Logger;

public class EJBEmoAndStickerDAOChain
extends EmoAndStickerDAOChain {
    private static final Logger log = Logger.getLogger(EJBEmoAndStickerDAOChain.class);

    public int getOptimalEmoticonHeight(int fontHeight) throws DAOException {
        if (!SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_EJB_NODE_GETOPTIMALEMOTICONHEIGHT)) {
            return super.getOptimalEmoticonHeight(fontHeight);
        }
        try {
            Content contentEJB = (Content)EJBHomeCache.getObject("ejb/Content", ContentHome.class);
            return contentEJB.getOptimalEmoticonHeight(null, fontHeight);
        }
        catch (Exception e) {
            log.warn((Object)String.format("Failed to get OptimalEmoticonHeight for fontHeight:%s", fontHeight), (Throwable)e);
            return super.getOptimalEmoticonHeight(fontHeight);
        }
    }
}

