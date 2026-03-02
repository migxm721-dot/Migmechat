/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.app.dao.base;

import com.projectgoth.fusion.app.dao.DAOException;
import com.projectgoth.fusion.app.dao.base.DAOChain;
import com.projectgoth.fusion.data.EmoticonData;
import com.projectgoth.fusion.data.EmoticonPackData;
import java.util.Map;
import java.util.SortedSet;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class EmoAndStickerDAOChain
implements DAOChain {
    private EmoAndStickerDAOChain nextRead;
    private EmoAndStickerDAOChain nextWrite;

    @Override
    public void setNextRead(DAOChain a) {
        this.nextRead = (EmoAndStickerDAOChain)a;
    }

    @Override
    public void setNextWrite(DAOChain a) {
        this.nextWrite = (EmoAndStickerDAOChain)a;
    }

    public Map<Integer, EmoticonPackData> loadEmoticonPacks() throws DAOException {
        if (this.nextRead != null) {
            return this.nextRead.loadEmoticonPacks();
        }
        throw new DAOException("Unable to loadEmoticonPacks");
    }

    public Map<Integer, EmoticonData> loadEmoticons() throws DAOException {
        if (this.nextRead != null) {
            return this.nextRead.loadEmoticons();
        }
        throw new DAOException("Unable to loadEmoticon datas");
    }

    public SortedSet<Integer> loadEmoticonHeights() throws DAOException {
        if (this.nextRead != null) {
            return this.nextRead.loadEmoticonHeights();
        }
        throw new DAOException("Unable to load Emoticon heights");
    }

    public int getOptimalEmoticonHeight(int fontHeight) throws DAOException {
        if (this.nextRead != null) {
            return this.nextRead.getOptimalEmoticonHeight(fontHeight);
        }
        throw new DAOException(String.format("Unable to load Emoticon heights for fontHeight:%s", fontHeight));
    }
}

