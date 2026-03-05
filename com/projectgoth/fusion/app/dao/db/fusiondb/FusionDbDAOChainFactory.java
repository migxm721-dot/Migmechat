/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.app.dao.db.fusiondb;

import com.projectgoth.fusion.app.dao.base.BotDAOChain;
import com.projectgoth.fusion.app.dao.base.CampaignDataDAOChain;
import com.projectgoth.fusion.app.dao.base.ChatRoomDAOChain;
import com.projectgoth.fusion.app.dao.base.DAOChainFactory;
import com.projectgoth.fusion.app.dao.base.EmailDAOChain;
import com.projectgoth.fusion.app.dao.base.EmoAndStickerDAOChain;
import com.projectgoth.fusion.app.dao.base.GroupDAOChain;
import com.projectgoth.fusion.app.dao.base.GuardsetDAOChain;
import com.projectgoth.fusion.app.dao.base.MessageDAOChain;
import com.projectgoth.fusion.app.dao.base.RecommendationDAOChain;
import com.projectgoth.fusion.app.dao.base.UserDataDAOChain;
import com.projectgoth.fusion.app.dao.db.fusiondb.FusionDbBotDAOChain;
import com.projectgoth.fusion.app.dao.db.fusiondb.FusionDbCampaignDataDAOChain;
import com.projectgoth.fusion.app.dao.db.fusiondb.FusionDbChatRoomDAOChain;
import com.projectgoth.fusion.app.dao.db.fusiondb.FusionDbEmailDAOChain;
import com.projectgoth.fusion.app.dao.db.fusiondb.FusionDbEmoAndStickerDAOChain;
import com.projectgoth.fusion.app.dao.db.fusiondb.FusionDbGroupDAOChain;
import com.projectgoth.fusion.app.dao.db.fusiondb.FusionDbGuardsetDAOChain;
import com.projectgoth.fusion.app.dao.db.fusiondb.FusionDbMessageDAOChain;
import com.projectgoth.fusion.app.dao.db.fusiondb.FusionDbRecommendationDAOChain;
import com.projectgoth.fusion.app.dao.db.fusiondb.FusionDbUserDataDAOChain;
import org.apache.log4j.Logger;

public class FusionDbDAOChainFactory
extends DAOChainFactory {
    private static final Logger log = Logger.getLogger(FusionDbDAOChainFactory.class);

    public UserDataDAOChain createUserDataDAOChain() {
        return new FusionDbUserDataDAOChain();
    }

    public CampaignDataDAOChain createCampaignDataDAOChain() {
        return new FusionDbCampaignDataDAOChain();
    }

    public GuardsetDAOChain createGuardsetDAOChain() {
        return new FusionDbGuardsetDAOChain();
    }

    public EmoAndStickerDAOChain createEmoAndStickerDAOChain() {
        return new FusionDbEmoAndStickerDAOChain();
    }

    public ChatRoomDAOChain createChatRoomDAOChain() {
        return new FusionDbChatRoomDAOChain();
    }

    public GroupDAOChain createGroupDAOChain() {
        return new FusionDbGroupDAOChain();
    }

    public BotDAOChain createBotDAOChain() {
        return new FusionDbBotDAOChain();
    }

    public MessageDAOChain createMessageDAOChain() {
        return new FusionDbMessageDAOChain();
    }

    public EmailDAOChain createEmailDAOChain() {
        return new FusionDbEmailDAOChain();
    }

    public RecommendationDAOChain createRecommendationDAOChain() {
        log.info((Object)"Creating FusionDbRecommendationDAOChain");
        return new FusionDbRecommendationDAOChain();
    }
}

