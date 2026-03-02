/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.emote;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.RMIExceptionHelper;
import com.projectgoth.fusion.data.MessageData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.emote.ChatSource;
import com.projectgoth.fusion.emote.EmoteCommand;
import com.projectgoth.fusion.emote.EmoteCommandData;
import com.projectgoth.fusion.emote.EmoteCommandState;
import com.projectgoth.fusion.interfaces.Message;
import com.projectgoth.fusion.interfaces.MessageHome;
import com.projectgoth.fusion.slice.FusionException;
import java.rmi.RemoteException;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;

public class Adult
extends EmoteCommand {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(Adult.class));

    public Adult(EmoteCommandData emoteCommandData) {
        super(emoteCommandData);
    }

    protected EmoteCommand.ResultType execute(MessageData messageData, ChatSource chatSource) throws FusionException {
        String[] args = messageData.getArgs();
        boolean adultOnly = args.length == 1 || !"off".equals(args[1]);
        String chatRoomName = messageData.messageDestinations.get((int)0).destination;
        try {
            Message messageBean = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
            messageBean.updateRoomAdultOnlyFlag(chatSource.getParentUsername(), chatRoomName, adultOnly);
        }
        catch (CreateException e) {
            throw new FusionException(e.getMessage());
        }
        catch (RemoteException e) {
            throw new FusionException(RMIExceptionHelper.getRootMessage(e));
        }
        if (adultOnly) {
            log.info((Object)("[" + chatSource.getParentUsername() + "] tagged room [" + chatRoomName + "] as adult only"));
            messageData.messageText = "This room has been tagged as adult only";
        } else {
            log.info((Object)("[" + chatSource.getParentUsername() + "] untagged room [" + chatRoomName + "] as adult only"));
            messageData.messageText = "This room has been untagged as adult only";
        }
        this.emoteCommandData.updateMessageData(messageData);
        chatSource.sendMessageToSender(messageData);
        return EmoteCommand.ResultType.HANDLED_AND_STOP;
    }

    public EmoteCommandState createDefaultState(ChatSource.ChatType chatType) {
        return null;
    }
}

