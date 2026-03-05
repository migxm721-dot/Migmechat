/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.emote;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.data.ChatRoomEmoteLogData;
import com.projectgoth.fusion.data.MessageData;
import com.projectgoth.fusion.data.PaidEmoteData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.emote.ChatSource;
import com.projectgoth.fusion.emote.EmoteCommand;
import com.projectgoth.fusion.emote.EmoteCommandData;
import com.projectgoth.fusion.emote.EmoteCommandState;
import com.projectgoth.fusion.interfaces.Content;
import com.projectgoth.fusion.interfaces.ContentHome;
import com.projectgoth.fusion.slice.ChatRoomDataIce;
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.SessionPrx;
import java.rmi.RemoteException;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import org.apache.log4j.Logger;

public class Guess
extends EmoteCommand {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(Guess.class));

    public Guess(EmoteCommandData emoteCommandData) {
        super(emoteCommandData);
    }

    protected EmoteCommand.ResultType execute(MessageData messageData, ChatSource chatSource) throws FusionException {
        String[] tokens = messageData.messageText.split(" ");
        if (tokens.length != 2) {
            throw new FusionException(String.format("Usage: /%s <input>", this.getEmoteCommandData().getCommandName()));
        }
        String input = tokens[1];
        if (input == null) {
            throw new FusionException(String.format("Usage: /%s <input>", this.getEmoteCommandData().getCommandName()));
        }
        boolean sufficientCredit = true;
        String privateMessage = "";
        boolean error = false;
        String sender = messageData.source;
        try {
            Content contentBean = (Content)EJBHomeCache.getObject("ejb/Content", ContentHome.class);
            SessionPrx sessionPrx = chatSource.getSessionPrx();
            sufficientCredit = contentBean.buyPaidEmote(sender, new PaidEmoteData(this.emoteCommandData), chatSource.getEmotePurchaseLocation().value(), new AccountEntrySourceData(sessionPrx.getRemoteIPAddress(), sessionPrx.getSessionID(), sessionPrx.getMobileDeviceIce(), sessionPrx.getUserAgentIce()));
            if (sufficientCredit) {
                log.debug((Object)String.format("User [%s] charged for Guess, amount [%s]", sender, this.emoteCommandData.getPriceWithCurrency()));
                privateMessage = String.format("[PVT] Thank you for participating. You have been charged %s", this.emoteCommandData.getPriceWithCurrency());
            } else {
                error = true;
                log.info((Object)String.format("Insufficient credits '%s' '%s' '%s'", sender, this.emoteCommandData.getCommandName(), this.emoteCommandData.getPriceWithCurrency()));
                privateMessage = String.format("[PVT] Insufficient credits. You need %s to participate.", this.emoteCommandData.getPriceWithCurrency());
            }
        }
        catch (CreateException e) {
            error = true;
            log.error((Object)String.format("Unable to create EJB to check whether user '%s' could afford to play Guess", sender), (Throwable)e);
            privateMessage = "[PVT] Unable to check your migCredits at the moment. Please try again later.";
        }
        catch (RemoteException e) {
            error = true;
            log.error((Object)String.format("Unable to check whether user '%s' could afford to play Guess, q='%s'", sender), (Throwable)e);
            privateMessage = "[PVT] Unable to check your migCredits at the moment. Please try again later.";
        }
        catch (EJBException e) {
            error = true;
            log.error((Object)String.format("Unable to deduct migCredits for user '%s' to play Guess", sender), (Throwable)e);
            privateMessage = "[PVT] Unable to deduct your migCredits at the moment. Please try again later.";
        }
        if (!error) {
            messageData.messageText = String.format(this.emoteCommandData.getMessageText(), input);
            this.emoteCommandData.updateMessageData(messageData);
            chatSource.sendMessageToAllUsersInChat(messageData);
        }
        if (!StringUtil.isBlank(privateMessage)) {
            messageData.messageText = privateMessage;
            chatSource.sendMessageToSender(messageData);
        }
        try {
            ChatRoomPrx chatRoomPrx = chatSource.castToLocalChatRoomChatSource().getChatRoomPrx();
            ChatRoomDataIce chatRoom = chatRoomPrx.getRoomData();
            ChatRoomEmoteLogData logData = new ChatRoomEmoteLogData(messageData.source, "", "/" + this.emoteCommandData.getCommandName(), chatRoom.id, chatRoom.groupID, -1, input);
            chatSource.getSessionI().logEmoteData(logData);
        }
        catch (Exception e) {
            log.error((Object)String.format("Unable to log emote '%s' for '%s' with input '%s'", this.emoteCommandData.getCommandName(), messageData.source, input), (Throwable)e);
        }
        return EmoteCommand.ResultType.HANDLED_AND_STOP;
    }

    public EmoteCommandState createDefaultState(ChatSource.ChatType chatType) {
        return null;
    }
}

