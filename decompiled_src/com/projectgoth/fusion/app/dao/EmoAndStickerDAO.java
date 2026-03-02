/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.app.dao;

import com.projectgoth.fusion.app.dao.DAOException;
import com.projectgoth.fusion.app.dao.base.EmoAndStickerDAOChain;
import com.projectgoth.fusion.common.LazyLoader;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.EmoticonData;
import com.projectgoth.fusion.data.EmoticonPackData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class EmoAndStickerDAO {
    private EmoAndStickerDAOChain readChain;
    private EmoAndStickerDAOChain writeChain;
    private final LazyLoader<Map<Integer, EmoticonPackData>> local_emoticon_packs = new LazyLoader<Map<Integer, EmoticonPackData>>("LOCAL_EMOTICON_PACKS", SystemProperty.getLong(SystemPropertyEntities.DAOSettings.EMOTICON_PACKS_REFRESH_PERIOD_IN_MS)){

        @Override
        protected Map<Integer, EmoticonPackData> fetchValue() throws DAOException {
            if (EmoAndStickerDAO.this.readChain != null) {
                return EmoAndStickerDAO.this.readChain.loadEmoticonPacks();
            }
            throw new DAOException("Unable to loadEmoticonPacks");
        }
    };
    private final LazyLoader<Map<Integer, EmoticonData>> local_emoticons = new LazyLoader<Map<Integer, EmoticonData>>("LOCAL_EMOTICONS", SystemProperty.getLong(SystemPropertyEntities.DAOSettings.EMOTICONS_REFRESH_PERIOD_IN_MS)){

        @Override
        protected Map<Integer, EmoticonData> fetchValue() throws DAOException {
            if (EmoAndStickerDAO.this.readChain != null) {
                return EmoAndStickerDAO.this.readChain.loadEmoticons();
            }
            throw new DAOException("Unable to loadEmoticons");
        }
    };
    private final LazyLoader<SortedSet<Integer>> local_emoticon_heights = new LazyLoader<SortedSet<Integer>>("LOCAL_EMOTICON_HEIGHTS", Long.MAX_VALUE){

        @Override
        protected SortedSet<Integer> fetchValue() throws DAOException {
            if (EmoAndStickerDAO.this.readChain != null) {
                return EmoAndStickerDAO.this.readChain.loadEmoticonHeights();
            }
            throw new DAOException("Unable to loadEmoticonHeights");
        }
    };

    public EmoAndStickerDAO(EmoAndStickerDAOChain readChain, EmoAndStickerDAOChain writeChain) {
        this.readChain = readChain;
        this.writeChain = writeChain;
    }

    public SortedSet<Integer> loadEmoticonHeights() throws DAOException {
        return this.local_emoticon_heights.getValue();
    }

    public Map<Integer, EmoticonPackData> loadEmoticonPacks() throws DAOException {
        return this.local_emoticon_packs.getValue();
    }

    public Map<Integer, EmoticonData> loadEmoticons() throws DAOException {
        return this.local_emoticons.getValue();
    }

    public List<EmoticonData> getEmoticonPack(int emoticonPackId) throws DAOException {
        EmoticonPackData emoticonPackData = this.loadEmoticonPacks().get(emoticonPackId);
        if (emoticonPackData != null) {
            List<Integer> emoticonIDs = emoticonPackData.getEmoticonIDs();
            return this.getEmoticons(emoticonIDs);
        }
        return Collections.emptyList();
    }

    private List<EmoticonData> getEmoticons(List<Integer> emoticonIDList) throws DAOException {
        ArrayList<EmoticonData> emoticonDatas = new ArrayList<EmoticonData>(emoticonIDList.size());
        Map<Integer, EmoticonData> emoMap = this.loadEmoticons();
        for (Integer emoticonID : emoticonIDList) {
            EmoticonData emoticonData = emoMap.get(emoticonID);
            if (emoticonData == null) continue;
            emoticonDatas.add(emoticonData);
        }
        return emoticonDatas;
    }

    public int getOptimalEmoticonHeight(int fontHeight) throws DAOException {
        try {
            Integer previous = 0;
            for (Integer height : this.loadEmoticonHeights()) {
                if (height > fontHeight) {
                    return previous == 0 ? this.loadEmoticonHeights().first() : previous;
                }
                previous = height;
            }
            return previous;
        }
        catch (DAOException e) {
            return this.readChain.getOptimalEmoticonHeight(fontHeight);
        }
    }
}

