/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.emote;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.MemCachedClientWrapper;
import com.projectgoth.fusion.common.MemCachedKeySpaces;
import com.projectgoth.fusion.data.BasicMerchantTagDetailsData;
import com.projectgoth.fusion.data.MessageData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.emote.ChatSource;
import com.projectgoth.fusion.emote.EmoteCommand;
import com.projectgoth.fusion.emote.EmoteCommandData;
import com.projectgoth.fusion.emote.EmoteCommandState;
import com.projectgoth.fusion.interfaces.Account;
import com.projectgoth.fusion.interfaces.AccountHome;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import com.projectgoth.fusion.slice.FusionException;
import java.util.Map;
import org.apache.log4j.Logger;

public class Merchant
extends EmoteCommand {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(Merchant.class));

    public Merchant(EmoteCommandData emoteCommandData) {
        super(emoteCommandData);
    }

    protected EmoteCommand.ResultType execute(MessageData messageData, ChatSource chatSource) throws FusionException {
        String[] args = messageData.getArgs();
        if (args.length > 1 && "tag".equals(args[1]) && chatSource.getSessionI().getUserType() == UserData.TypeEnum.MIG33_TOP_MERCHANT) {
            this.handleMerchantTagCommand(messageData, chatSource);
            return EmoteCommand.ResultType.HANDLED_AND_STOP;
        }
        return EmoteCommand.ResultType.NOTHANDLED;
    }

    public EmoteCommandState createDefaultState(ChatSource.ChatType chatType) {
        return null;
    }

    private void handleMerchantTagCommand(MessageData messageData, ChatSource chatSource) throws FusionException {
        String[] participants = chatSource.getVisibleUsernamesInChat(false);
        try {
            Map<String, String> tags = MemCachedClientWrapper.getMultiString(MemCachedKeySpaces.CommonKeySpace.MERCHANT_TAG, participants);
            Account accountBean = (Account)EJBHomeCache.getObject("ejb/Account", AccountHome.class);
            User userBean = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
            for (String participant : participants) {
                String merchant = tags.get(participant);
                if (merchant == null) {
                    boolean fromSlave = true;
                    BasicMerchantTagDetailsData merchantTagData = accountBean.getMerchantTagFromUsername(null, participant, fromSlave);
                    if (merchantTagData == null) {
                        chatSource.getSessionI().sendMessageBackToUserAsEmote(messageData, participant + " _");
                        continue;
                    }
                    UserData merchantUserData = userBean.loadUserFromID(merchantTagData.merchantUserID);
                    merchant = merchantUserData.username;
                    MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.MERCHANT_TAG, participant, merchant);
                }
                if (merchant.equals(chatSource.getSessionI().getUsername())) {
                    chatSource.getSessionI().sendMessageBackToUserAsEmote(messageData, participant + " *");
                    continue;
                }
                chatSource.getSessionI().sendMessageBackToUserAsEmote(messageData, participant + " O");
            }
        }
        catch (Exception e) {
            log.error((Object)("Unable to retrive merchant tag information: " + e.getMessage()), (Throwable)e);
            throw new FusionException("Unable to retrieve tag information now. Please try again later.");
        }
    }
}

