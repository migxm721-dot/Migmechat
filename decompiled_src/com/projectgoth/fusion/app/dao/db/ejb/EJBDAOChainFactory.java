/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.app.dao.db.ejb;

import com.projectgoth.fusion.app.dao.base.BotDAOChain;
import com.projectgoth.fusion.app.dao.base.ChatRoomDAOChain;
import com.projectgoth.fusion.app.dao.base.DAOChainFactory;
import com.projectgoth.fusion.app.dao.base.EmoAndStickerDAOChain;
import com.projectgoth.fusion.app.dao.base.GroupDAOChain;
import com.projectgoth.fusion.app.dao.base.GuardsetDAOChain;
import com.projectgoth.fusion.app.dao.base.MessageDAOChain;
import com.projectgoth.fusion.app.dao.base.UserDataDAOChain;
import com.projectgoth.fusion.app.dao.db.ejb.EJBBotDAOChain;
import com.projectgoth.fusion.app.dao.db.ejb.EJBChatRoomDAOChain;
import com.projectgoth.fusion.app.dao.db.ejb.EJBEmoAndStickerDAOChain;
import com.projectgoth.fusion.app.dao.db.ejb.EJBGroupDAOChain;
import com.projectgoth.fusion.app.dao.db.ejb.EJBGuardsetDAOChain;
import com.projectgoth.fusion.app.dao.db.ejb.EJBMessageDAOChain;
import com.projectgoth.fusion.app.dao.db.ejb.EJBUserDataDAOChain;

public class EJBDAOChainFactory
extends DAOChainFactory {
    public UserDataDAOChain createUserDataDAOChain() {
        return new EJBUserDataDAOChain();
    }

    public GuardsetDAOChain createGuardsetDAOChain() {
        return new EJBGuardsetDAOChain();
    }

    public EmoAndStickerDAOChain createEmoAndStickerDAOChain() {
        return new EJBEmoAndStickerDAOChain();
    }

    public ChatRoomDAOChain createChatRoomDAOChain() {
        return new EJBChatRoomDAOChain();
    }

    public GroupDAOChain createGroupDAOChain() {
        return new EJBGroupDAOChain();
    }

    public BotDAOChain createBotDAOChain() {
        return new EJBBotDAOChain();
    }

    public MessageDAOChain createMessageDAOChain() {
        return new EJBMessageDAOChain();
    }
}

