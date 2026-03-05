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
import com.projectgoth.fusion.data.MessageData;
import com.projectgoth.fusion.data.PaidEmoteData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.emote.ChatSource;
import com.projectgoth.fusion.emote.EmoteCommand;
import com.projectgoth.fusion.emote.EmoteCommandData;
import com.projectgoth.fusion.emote.EmoteCommandState;
import com.projectgoth.fusion.emote.EmoteCommandUtils;
import com.projectgoth.fusion.interfaces.Content;
import com.projectgoth.fusion.interfaces.ContentHome;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.SessionPrx;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import org.apache.log4j.Logger;

public class CrystalBall
extends EmoteCommand {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(CrystalBall.class));
    private static final long DEFAULT_RETRY_COOLDOWN_PERIOD = 60000L;
    private static final long DEFAULT_CONFIRMATION_WAITTIME = 10000L;
    private static final long DEFAULT_GENERATION_WAITTIME = 1000L;
    private static final long DEFAULT_ANSWER_WAITTIME = 3000L;
    private static final Pattern CONFIRMATION_PATTERN = Pattern.compile("^\\s*(.+)\\s+\\*[yY]\\s*$");
    private static final String DEFAULT_GENERATION_TEXTS = EmoteCommandUtils.createRandomTextCandidateString(new String[]{"The fog is lifting...", "The spirits are speaking to me...", "I'm seeing something...", "It's materialising...", "The answer's on its way...", "The spirits are present...", "Foretelling...", "Psychic powers working...", "Electricity is pulsing...", "Meditating for you...", "The answer's becoming clear...", "Powerful forces working...", "Feeling the warmth...", "Spirit wheel spinning...", "Ohmmmm, Ohmmmm...", "Metaphysical powers flowing...", "I see... I see... something's coming...", "The orb is glowing...", "Strange light from within...", "Smoke swirling within...", "The crystal pulses with eerie light...", "The fogginess is fading...", "Ghostly vapours swirling...", "Spirits are clearing the vale...", "Shapes appearing in the mist...", "I fear to watch, yet I can't turn away...", "The orb glows hotter...", "Hissing sounds from all around. The spirits are speaking...", "Oh! Amazing light! It's becoming clear...", "The horrible, black void grows brighter...", "Lightning flashes within! Mists dissipating...", "Arcane knowledge flowing and swirling...", "Let me see.. hmmm..."});
    private static final String DEFAULT_ANSWER_TEXTS = EmoteCommandUtils.createRandomTextCandidateString(new String[]{"As I see it, yes.", "It is certain.", "It is decidedly so.", "Most likely.", "Outlook good.", "Signs point to yes.", "Without a doubt.", "Yes.", "Yes - definitely.", "You may rely on it.", "Reply hazy, try again.", "Ask again later.", "Better not tell you now.", "Concentrate and ask again.", "Don't count on it.", "My reply is no.", "My sources say no.", "Outlook not so good.", "Very doubtful.", "Probably not.", "Highly unlikely.", "Forget about it!", "No way!", "Chances are slim."});

    public CrystalBall(EmoteCommandData emoteCommandData) {
        super(emoteCommandData);
    }

    public EmoteCommandState createDefaultState(ChatSource.ChatType chatType) {
        return new CrystalBallState();
    }

    private static CommandData parseCommand(MessageData messageData, ChatSource chatSource) {
        String[] args = messageData.messageText.split("\\s+", 2);
        if (args.length < 2 || StringUtil.isBlank(args[1])) {
            return null;
        }
        CommandData cd = new CommandData();
        Matcher m = CONFIRMATION_PATTERN.matcher(args[1]);
        if (m.matches()) {
            cd.question = m.group(1);
            cd.withConfirmation = true;
        } else {
            cd.question = args[1];
            if ("y".equalsIgnoreCase(cd.question)) {
                cd.isAnswerYes = true;
                cd.isQuestion = false;
            } else if ("n".equalsIgnoreCase(cd.question)) {
                cd.isAnswerYes = false;
                cd.isQuestion = false;
            }
        }
        return cd;
    }

    protected String[] getHelpMessage(ChatSource chatSource) throws FusionException {
        return new String[]{"Usage: /cb [a yes/no question]"};
    }

    protected EmoteCommand.ResultType execute(MessageData messageData, ChatSource chatSource) throws FusionException {
        if (this.handleHelpCommand(messageData, chatSource)) {
            return EmoteCommand.ResultType.HANDLED_AND_STOP;
        }
        return chatSource.executeEmoteCommandWithState(this.emoteCommandData.getCommandName(), messageData.toIceObject());
    }

    private static class CrystalBallState
    extends EmoteCommandState {
        CBState currentState = CBState.IDLE;
        String currentUsername = null;
        String currentQuestion = null;
        long currentAskedTimestamp = 0L;
        private Map<String, Long> dataMap = new HashMap<String, Long>();
        private Timer cbTimer;

        private CrystalBallState() {
        }

        private void intoCoolDown() {
            this.currentState = CBState.IDLE;
            log.debug((Object)String.format("Adding user '%s' to cool down, start '%s', expiring '%s'", this.currentUsername, new Date(this.currentAskedTimestamp + 10000L), new Date(this.currentAskedTimestamp + 10000L + 60000L)));
            this.dataMap.put(this.currentUsername, this.currentAskedTimestamp + 10000L);
        }

        private MessageInfo tryEnterAskedState(EmoteCommandData emoteCommandData, MessageData messageData, ChatSource chatSource) {
            boolean sufficientCredit = true;
            String sender = messageData.source;
            MessageInfo mi = null;
            try {
                Content contentBean = (Content)EJBHomeCache.getObject("ejb/Content", ContentHome.class);
                SessionPrx sessionPrx = chatSource.getSessionPrx();
                sufficientCredit = contentBean.buyPaidEmote(sender, new PaidEmoteData(emoteCommandData), chatSource.getEmotePurchaseLocation().value(), new AccountEntrySourceData(sessionPrx.getRemoteIPAddress(), sessionPrx.getSessionID(), sessionPrx.getMobileDeviceIce(), sessionPrx.getUserAgentIce()));
                if (log.isDebugEnabled()) {
                    log.debug((Object)String.format("User [%s] charged for Crystal Ball, amount [%s]", sender, emoteCommandData.getPriceWithCurrency()));
                }
            }
            catch (CreateException e) {
                log.error((Object)String.format("Unable to create EJB to check whether user '%s' could afford to play Crystal Ball, q='%s'", sender, this.currentQuestion), (Throwable)e);
                mi = this.formatPrivateMessage(String.format("Unable to check your migCredits at the moment. Please try again later.", new Object[0]), emoteCommandData);
            }
            catch (RemoteException e) {
                log.error((Object)String.format("Unable to check whether user '%s' could afford to play Crystal Ball, q='%s'", sender, this.currentQuestion), (Throwable)e);
                mi = this.formatPrivateMessage(String.format("Unable to check your migCredits at the moment. Please try again later.", new Object[0]), emoteCommandData);
            }
            catch (EJBException e) {
                log.error((Object)String.format("Unable to deduct migCredits for user '%s' to buy Crystal Ball emote, q='%s'", sender, this.currentQuestion), (Throwable)e);
                mi = this.formatPrivateMessage(String.format("Unable to deduct your migCredits at the moment. Please try again later.", new Object[0]), emoteCommandData);
            }
            if (mi == null) {
                if (!sufficientCredit) {
                    mi = this.formatPrivateMessage(String.format("Insufficient migCredits. Please get more and try again.", new Object[0]), emoteCommandData);
                    this.intoCoolDown();
                } else {
                    this.currentState = CBState.ASKED;
                    mi = this.formatAskMessage(this.currentQuestion, this.currentUsername, emoteCommandData);
                    if (this.cbTimer == null) {
                        this.cbTimer = new Timer("Crystal Ball timer", true);
                    }
                    String text = EmoteCommandUtils.getRandomText(DEFAULT_GENERATION_TEXTS);
                    SendGenerationTextTask task = new SendGenerationTextTask(this.formatNormalMessage((String)text, (EmoteCommandData)emoteCommandData).message, emoteCommandData, messageData, chatSource);
                    this.cbTimer.schedule((TimerTask)task, 1000L);
                }
            }
            return mi;
        }

        private MessageInfo formatNormalMessage(String message, EmoteCommandData emoteCommandData) {
            return new MessageInfo(String.format(emoteCommandData.getMessageText(), "", "", message), false, false, null);
        }

        private MessageInfo formatPrivateMessage(String message, EmoteCommandData emoteCommandData) {
            String msgText = emoteCommandData.getMessageText();
            return new MessageInfo(String.format(msgText.substring(2, msgText.length() - 2), "", "", "[PVT] " + message));
        }

        private boolean isPrivateMessage(String message) {
            return !message.startsWith("**") && message.contains("[PVT]");
        }

        private MessageInfo formatAskMessage(String message, String username, EmoteCommandData emoteCommandData) {
            return new MessageInfo(String.format(emoteCommandData.getMessageText(), username + " asked ", "", "\"%s?\""), false, true, message);
        }

        private MessageInfo formatAnswerMessage(String message, String username, EmoteCommandData emoteCommandData) {
            return new MessageInfo(String.format(emoteCommandData.getMessageText(), "", " to " + username, message), false, false, null);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public EmoteCommand.ResultType execute(EmoteCommandData emoteCommandData, MessageData messageData, ChatSource chatSource) throws FusionException {
            CommandData cd = CrystalBall.parseCommand(messageData, chatSource);
            String sender = messageData.source;
            MessageInfo messageInfo = null;
            Map<String, Long> map = this.dataMap;
            synchronized (map) {
                boolean expiredWaiting;
                long curTime = System.currentTimeMillis();
                boolean bl = expiredWaiting = this.currentState == CBState.WAITING && curTime - this.currentAskedTimestamp > 10000L;
                if (this.currentState != CBState.IDLE && !expiredWaiting && !sender.equals(this.currentUsername)) {
                    messageInfo = this.formatPrivateMessage(String.format("%s is consulting the Crystal Ball now. Please ask again in a moment.", this.currentUsername), emoteCommandData);
                } else {
                    if (expiredWaiting) {
                        this.intoCoolDown();
                    }
                    switch (this.currentState) {
                        case IDLE: {
                            if (this.dataMap.containsKey(sender) && curTime - this.dataMap.get(sender) < 60000L) {
                                messageInfo = this.formatPrivateMessage(String.format("Please wait %d seconds before consulting the Crystal Ball again.", 60L), emoteCommandData);
                                break;
                            }
                            if (cd.isQuestion) {
                                this.currentUsername = sender;
                                this.currentQuestion = cd.question;
                                this.currentAskedTimestamp = curTime;
                                if (cd.withConfirmation) {
                                    messageInfo = this.tryEnterAskedState(emoteCommandData, messageData, chatSource);
                                    break;
                                }
                                messageInfo = this.formatPrivateMessage(String.format("Consulting the Crystal Ball: %s. Continue? Send /cb y to continue and /cb n to stop. %d seconds...", emoteCommandData.getPriceWithCurrency(), 10L), emoteCommandData);
                                this.currentState = CBState.WAITING;
                                if (this.cbTimer == null) {
                                    this.cbTimer = new Timer("Crystal Ball timer", true);
                                }
                                String text = this.formatPrivateMessage((String)"Timed out! Crystal Ball cancelled. No migCredits were deducted.", (EmoteCommandData)emoteCommandData).message;
                                SendTimeoutTextTask task = new SendTimeoutTextTask(this.currentUsername, this.currentAskedTimestamp, text, emoteCommandData, messageData, chatSource);
                                this.cbTimer.schedule((TimerTask)task, 10100L);
                                break;
                            }
                            messageInfo = this.formatPrivateMessage(String.format("Please enter the question first before answering /cb y or /cb n to proceed.", new Object[0]), emoteCommandData);
                            break;
                        }
                        case WAITING: {
                            if (cd.isQuestion) {
                                messageInfo = this.formatPrivateMessage(String.format("Please send /cb y to continue and /cb n to stop.", new Object[0]), emoteCommandData);
                                break;
                            }
                            if (cd.isAnswerYes) {
                                messageInfo = this.tryEnterAskedState(emoteCommandData, messageData, chatSource);
                                break;
                            }
                            this.intoCoolDown();
                            messageInfo = this.formatPrivateMessage("Crystal Ball cancelled. No migCredits were deducted.", emoteCommandData);
                            break;
                        }
                        case ASKED: 
                        case MUMBLED: {
                            log.error((Object)"Should not reach this part");
                        }
                    }
                }
            }
            if (messageInfo == null) {
                log.error((Object)String.format("message is null after processing /cb. something is wrong. curState='%s', curUser='%s', curQ='%s', curAskedTS='%s'", this.currentState.name(), this.currentUsername, this.currentQuestion, new Date(this.currentAskedTimestamp)));
                throw new FusionException("Unable to process the request");
            }
            messageData.messageText = messageInfo.message;
            emoteCommandData.updateMessageData(messageData);
            if (messageInfo.isPrivate) {
                chatSource.sendMessageToSender(messageData);
            } else if (messageInfo.requireTruncation) {
                chatSource.sendMessageWithTruncationToAllUsersInChat(messageData, messageInfo.subMessageToTruncate);
            } else {
                chatSource.sendMessageToAllUsersInChat(messageData);
            }
            return EmoteCommand.ResultType.HANDLED_AND_STOP;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void cleanUp() {
            long curTime = System.currentTimeMillis();
            Map<String, Long> map = this.dataMap;
            synchronized (map) {
                Iterator<Map.Entry<String, Long>> iter = this.dataMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, Long> e = iter.next();
                    if (curTime - e.getValue() <= 60000L) continue;
                    iter.remove();
                    log.info((Object)String.format("removed expired cool down record %s, %d", e.getKey(), e.getValue()));
                }
            }
        }

        private class MessageInfo {
            String message = null;
            boolean isPrivate = true;
            boolean requireTruncation = false;
            String subMessageToTruncate = null;

            MessageInfo() {
            }

            MessageInfo(String message) {
                this.message = message;
            }

            MessageInfo(String message, boolean isPrivate, boolean requireTruncation, String subMessage) {
                this.message = message;
                this.isPrivate = isPrivate;
                this.requireTruncation = requireTruncation;
                this.subMessageToTruncate = subMessage;
            }
        }

        private class SendAnswerTextTask
        extends TimerTask {
            MessageData messageData;
            ChatSource chatSource;

            SendAnswerTextTask(String message, EmoteCommandData emoteCommandData, MessageData messageData, ChatSource chatSource) {
                this.messageData = new MessageData(messageData.toIceObject());
                emoteCommandData.updateMessageData(this.messageData);
                this.messageData.messageText = message;
                this.chatSource = chatSource;
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             * Enabled aggressive block sorting
             * Enabled unnecessary exception pruning
             * Enabled aggressive exception aggregation
             */
            public void run() {
                try {
                    try {
                        this.chatSource.sendMessageToAllUsersInChat(this.messageData);
                    }
                    catch (FusionException e) {
                        log.error((Object)String.format("Failed to broadcast message to chatSource, msg='%s'", this.messageData), (Throwable)((Object)e));
                        Object var3_2 = null;
                        Map map2 = CrystalBallState.this.dataMap;
                        synchronized (map2) {
                            CrystalBallState.this.currentState = CBState.IDLE;
                            return;
                        }
                    }
                    Object var3_1 = null;
                }
                catch (Throwable throwable) {
                    Object var3_3 = null;
                    Map map = CrystalBallState.this.dataMap;
                    synchronized (map) {
                        CrystalBallState.this.currentState = CBState.IDLE;
                        throw throwable;
                    }
                }
                Map map = CrystalBallState.this.dataMap;
                synchronized (map) {
                    CrystalBallState.this.currentState = CBState.IDLE;
                    return;
                }
            }
        }

        private class SendGenerationTextTask
        extends TimerTask {
            MessageData messageData;
            ChatSource chatSource;
            EmoteCommandData emoteCommandData;

            SendGenerationTextTask(String message, EmoteCommandData emoteCommandData, MessageData messageData, ChatSource chatSource) {
                this.messageData = new MessageData(messageData.toIceObject());
                this.messageData.messageText = message;
                this.chatSource = chatSource;
                this.emoteCommandData = emoteCommandData;
                emoteCommandData.updateMessageData(this.messageData);
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             * Enabled aggressive block sorting
             * Enabled unnecessary exception pruning
             * Enabled aggressive exception aggregation
             */
            public void run() {
                try {
                    try {
                        this.chatSource.sendMessageToAllUsersInChat(this.messageData);
                    }
                    catch (FusionException e) {
                        log.error((Object)String.format("Failed to broadcast message to chatSource, msg='%s'", this.messageData), (Throwable)((Object)e));
                        Object var3_2 = null;
                        Map map2 = CrystalBallState.this.dataMap;
                        synchronized (map2) {
                            CrystalBallState.this.currentState = CBState.MUMBLED;
                            MessageInfo mi = CrystalBallState.this.formatAnswerMessage(EmoteCommandUtils.getRandomText(DEFAULT_ANSWER_TEXTS), this.messageData.source, this.emoteCommandData);
                            SendAnswerTextTask task = new SendAnswerTextTask(mi.message, this.emoteCommandData, this.messageData, this.chatSource);
                            CrystalBallState.this.cbTimer.schedule((TimerTask)task, 3000L);
                            return;
                        }
                    }
                    Object var3_1 = null;
                }
                catch (Throwable throwable) {
                    Object var3_3 = null;
                    Map map = CrystalBallState.this.dataMap;
                    synchronized (map) {
                        CrystalBallState.this.currentState = CBState.MUMBLED;
                        MessageInfo mi = CrystalBallState.this.formatAnswerMessage(EmoteCommandUtils.getRandomText(DEFAULT_ANSWER_TEXTS), this.messageData.source, this.emoteCommandData);
                        SendAnswerTextTask task = new SendAnswerTextTask(mi.message, this.emoteCommandData, this.messageData, this.chatSource);
                        CrystalBallState.this.cbTimer.schedule((TimerTask)task, 3000L);
                        throw throwable;
                    }
                }
                Map map = CrystalBallState.this.dataMap;
                synchronized (map) {
                    CrystalBallState.this.currentState = CBState.MUMBLED;
                    MessageInfo mi = CrystalBallState.this.formatAnswerMessage(EmoteCommandUtils.getRandomText(DEFAULT_ANSWER_TEXTS), this.messageData.source, this.emoteCommandData);
                    SendAnswerTextTask task = new SendAnswerTextTask(mi.message, this.emoteCommandData, this.messageData, this.chatSource);
                    CrystalBallState.this.cbTimer.schedule((TimerTask)task, 3000L);
                    return;
                }
            }
        }

        private class SendTimeoutTextTask
        extends TimerTask {
            String originalSender;
            long originalAskedTimestamp;
            MessageData messageData;
            ChatSource chatSource;

            SendTimeoutTextTask(String sender, long askedTimestamp, String message, EmoteCommandData emoteCommandData, MessageData messageData, ChatSource chatSource) {
                this.originalAskedTimestamp = askedTimestamp;
                this.originalSender = sender;
                this.messageData = new MessageData(messageData.toIceObject());
                emoteCommandData.updateMessageData(this.messageData);
                this.messageData.messageText = message;
                this.chatSource = chatSource;
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void run() {
                long curTime = System.currentTimeMillis();
                Map map = CrystalBallState.this.dataMap;
                synchronized (map) {
                    if (CrystalBallState.this.currentState != CBState.WAITING || this.originalAskedTimestamp != CrystalBallState.this.currentAskedTimestamp || !this.originalSender.equals(CrystalBallState.this.currentUsername)) {
                        return;
                    }
                    CrystalBallState.this.intoCoolDown();
                }
                try {
                    this.chatSource.sendMessageToSender(this.messageData);
                }
                catch (FusionException e) {
                    log.error((Object)String.format("Failed to send message to sender, msg='%s'", this.messageData), (Throwable)((Object)e));
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        private static enum CBState {
            IDLE,
            WAITING,
            ASKED,
            MUMBLED;

        }
    }

    private static class CommandData {
        String question;
        boolean isQuestion = true;
        boolean isAnswerYes = false;
        boolean withConfirmation = false;

        private CommandData() {
        }
    }
}

