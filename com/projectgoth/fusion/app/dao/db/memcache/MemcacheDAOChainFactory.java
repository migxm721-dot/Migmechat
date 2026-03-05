/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.app.dao.db.memcache;

import com.projectgoth.fusion.app.dao.base.CampaignDataDAOChain;
import com.projectgoth.fusion.app.dao.base.ChatRoomDAOChain;
import com.projectgoth.fusion.app.dao.base.DAOChainFactory;
import com.projectgoth.fusion.app.dao.base.GuardsetDAOChain;
import com.projectgoth.fusion.app.dao.base.MessageDAOChain;
import com.projectgoth.fusion.app.dao.base.UserDataDAOChain;
import com.projectgoth.fusion.app.dao.db.memcache.MemcacheCampaignDataDAOChain;
import com.projectgoth.fusion.app.dao.db.memcache.MemcacheChatRoomDAOChain;
import com.projectgoth.fusion.app.dao.db.memcache.MemcacheGuardsetDAOChain;
import com.projectgoth.fusion.app.dao.db.memcache.MemcacheMessageDAOChain;
import com.projectgoth.fusion.app.dao.db.memcache.MemcacheUserDataDAOChain;

public class MemcacheDAOChainFactory
extends DAOChainFactory {
    public UserDataDAOChain createUserDataDAOChain() {
        return new MemcacheUserDataDAOChain();
    }

    public GuardsetDAOChain createGuardsetDAOChain() {
        return new MemcacheGuardsetDAOChain();
    }

    public ChatRoomDAOChain createChatRoomDAOChain() {
        return new MemcacheChatRoomDAOChain();
    }

    public MessageDAOChain createMessageDAOChain() {
        return new MemcacheMessageDAOChain();
    }

    public CampaignDataDAOChain createCampaignDataDAOChain() {
        return new MemcacheCampaignDataDAOChain();
    }
}

