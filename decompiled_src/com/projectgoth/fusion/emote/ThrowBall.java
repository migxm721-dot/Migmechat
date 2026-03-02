/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.emote;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.data.MessageData;
import com.projectgoth.fusion.emote.ChatSource;
import com.projectgoth.fusion.emote.EmoteCommand;
import com.projectgoth.fusion.emote.EmoteCommandData;
import com.projectgoth.fusion.emote.EmoteCommandState;
import com.projectgoth.fusion.slice.FusionException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

public class ThrowBall
extends EmoteCommand {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ThrowBall.class));
    private static final SecureRandom RANDOM_GENERATOR = new SecureRandom();
    private static final Pattern THROWBALL_COMMAND_PATTERN = Pattern.compile("/throwball(?:\\s+([a-z0-9._-]+))?", 2);
    private static final Pattern STEALBALL_COMMAND_PATTERN = Pattern.compile("/stealball\\s+([a-z0-9._-]+)", 2);

    public ThrowBall(EmoteCommandData emoteCommandData) {
        super(emoteCommandData);
    }

    public EmoteCommandState createDefaultState(ChatSource.ChatType chatType) {
        return new ThrowBallState();
    }

    private static String parseThrowballCommand(MessageData messageData, ChatSource chatSource, boolean pickTarget) throws FusionException {
        Matcher m = THROWBALL_COMMAND_PATTERN.matcher(messageData.messageText);
        if (!m.matches()) {
            throw new FusionException("Usage: /throwball [optional-username]");
        }
        String target = m.group(1);
        if (pickTarget && target == null) {
            String[] allUsers = chatSource.getVisibleUsernamesInChat(false);
            if (allUsers.length == 0) {
                throw new FusionException("There are no other users in the chat");
            }
            target = allUsers[RANDOM_GENERATOR.nextInt(allUsers.length)];
        }
        if (target != null) {
            if ((target = target.toLowerCase()).equals(messageData.source.toLowerCase())) {
                throw new FusionException("You can not throw the ball to yourself");
            }
            if (!chatSource.isUserVisibleInChat(target)) {
                throw new FusionException(target + " is not in the chat");
            }
        }
        return target;
    }

    private static String parseStealballCommand(MessageData messageData, ChatSource chatSource) throws FusionException {
        Matcher m = STEALBALL_COMMAND_PATTERN.matcher(messageData.messageText);
        if (!m.matches()) {
            throw new FusionException("Usage: /stealball [username]");
        }
        String target = m.group(1).toLowerCase();
        if (!chatSource.isUserInChat(target)) {
            throw new FusionException(target + " is not in the chat");
        }
        return target;
    }

    protected EmoteCommand.ResultType execute(MessageData messageData, ChatSource chatSource) throws FusionException {
        String[] args = messageData.messageText.toLowerCase().split(" ", 2);
        String command = args[0].substring(1);
        if ("throwball".equals(command)) {
            ThrowBall.parseThrowballCommand(messageData, chatSource, false);
        } else if (!"catchball".equals(command)) {
            if ("stealball".equals(command)) {
                ThrowBall.parseStealballCommand(messageData, chatSource);
            } else {
                return null;
            }
        }
        return chatSource.executeEmoteCommandWithState(this.emoteCommandData.getCommandName(), messageData.toIceObject());
    }

    public static class ThrowBallState
    extends EmoteCommandState {
        BallState currentState = BallState.IDLE;
        String userThrewBall;
        String userThrewAtBall;
        String userWithBall;
        long lastStateChangeTime = 0L;
        boolean ballCaught;
        private Map<String, BallData> dataMap = new HashMap<String, BallData>();
        private static final long DEFAULT_THROWBALL_WAITTIME = 40000L;
        private static final long DEFAULT_CATCHBALL_WAITTIME = 40000L;
        private static final long DEFAULT_CATCHBALL_EXPIRE_TIME = 80000L;

        private void changeState(BallState newState, String thrower, String target, String holder, BallCommand ballCmd) {
            this.currentState = newState;
            this.userThrewBall = thrower;
            this.userThrewAtBall = target;
            this.userWithBall = holder;
            this.lastStateChangeTime = System.currentTimeMillis();
            if (newState != BallState.IDLE) {
                if (ballCmd == BallCommand.CATCH) {
                    this.dataMap.remove(this.userThrewAtBall);
                } else if (ballCmd == BallCommand.THROW) {
                    this.dataMap.put(this.userThrewAtBall, new BallData(this.userThrewBall, this.lastStateChangeTime));
                    this.dataMap.remove(this.userThrewBall);
                } else if (ballCmd == BallCommand.STEAL) {
                    this.dataMap.remove(this.userWithBall);
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void cleanUp() {
            long curTime = System.currentTimeMillis();
            Map<String, BallData> map = this.dataMap;
            synchronized (map) {
                Iterator<Map.Entry<String, BallData>> iter = this.dataMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, BallData> e = iter.next();
                    if (curTime - e.getValue().timestamp <= 80000L) continue;
                    iter.remove();
                    log.info((Object)String.format("removed expired action %s", e.getValue()));
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public EmoteCommand.ResultType execute(EmoteCommandData emoteCommandData, MessageData messageData, ChatSource chatSource) throws FusionException {
            String[] args = messageData.messageText.toLowerCase().split(" ", 2);
            BallCommand ballCmd = BallCommand.fromString(args[0].substring(1));
            String message = null;
            Map<String, BallData> map = this.dataMap;
            synchronized (map) {
                if (this.currentState == BallState.IDLE) {
                    message = this.executeWhenIdle(ballCmd, emoteCommandData, messageData, chatSource);
                } else if (this.currentState == BallState.THROWN) {
                    message = this.executeWhenThrown(ballCmd, emoteCommandData, messageData, chatSource);
                } else if (this.currentState == BallState.AT_HAND) {
                    message = this.executeWhenAtHand(ballCmd, emoteCommandData, messageData, chatSource);
                }
            }
            if (message == null) {
                return EmoteCommand.ResultType.NOTHANDLED;
            }
            messageData.messageText = String.format("**Referee: %s**", message);
            emoteCommandData.updateMessageData(messageData);
            chatSource.sendMessageToAllUsersInChat(messageData);
            return EmoteCommand.ResultType.HANDLED_AND_STOP;
        }

        private String executeWhenThrown(BallCommand ballCmd, EmoteCommandData emoteCommandData, MessageData messageData, ChatSource chatSource) throws FusionException {
            long curTime = System.currentTimeMillis();
            if (ballCmd == BallCommand.THROW) {
                String thrower = messageData.source;
                if (curTime - this.lastStateChangeTime <= 40000L) {
                    if (thrower.equals(this.userThrewBall)) {
                        return String.format("%s, you've just thrown the ball. Wait for it to be caught.", thrower);
                    }
                    if (thrower.equals(this.userThrewAtBall)) {
                        return String.format("%s, the ball has been thrown at you. Catch it now!", thrower);
                    }
                    return String.format("%s, the ball has been thrown. Steal it now!", thrower);
                }
                String target = ThrowBall.parseThrowballCommand(messageData, chatSource, true);
                this.changeState(BallState.THROWN, thrower, target, this.userWithBall, ballCmd);
                return String.format(emoteCommandData.getMessageText(), thrower, target);
            }
            if (ballCmd == BallCommand.CATCH) {
                boolean catchLastThrownExpired;
                boolean expired;
                String catcher = messageData.source;
                boolean bl = expired = curTime - this.lastStateChangeTime > 40000L;
                if (!expired && catcher.equals(this.userThrewBall)) {
                    return String.format("Silly %s, you cannot catch a ball thrown by yourself.", catcher);
                }
                if (!expired && catcher.equals(this.userThrewAtBall)) {
                    this.changeState(BallState.AT_HAND, this.userThrewBall, catcher, catcher, ballCmd);
                    return String.format(emoteCommandData.getMessageText(), catcher);
                }
                BallData bd = this.dataMap.get(catcher);
                boolean bl2 = catchLastThrownExpired = bd != null && curTime - bd.timestamp <= 80000L;
                if (catcher.equals(this.userThrewAtBall) && catchLastThrownExpired) {
                    this.dataMap.remove(catcher);
                    return String.format("Too bad, %s, the ball has dropped. Be faster next time!", catcher);
                }
                if (!expired) {
                    if (catchLastThrownExpired) {
                        return String.format("Too bad, %s, %s has thrown the ball already. Be faster next time!", catcher, this.userThrewBall);
                    }
                    return String.format("Silly %s, the ball was not thrown at you. Steal it now!", catcher);
                }
                this.changeState(BallState.IDLE, this.userThrewBall, this.userThrewAtBall, this.userWithBall, ballCmd);
                if (catchLastThrownExpired) {
                    return String.format("Too bad, %s, %s has thrown the ball already. Be faster next time!", catcher, this.userThrewBall);
                }
                return String.format("%s, the ball is free. Throw it now!", catcher);
            }
            if (ballCmd == BallCommand.STEAL) {
                boolean expired;
                String stealer = messageData.source;
                String target = ThrowBall.parseStealballCommand(messageData, chatSource);
                boolean bl = expired = curTime - this.lastStateChangeTime > 40000L;
                if (!expired) {
                    if (stealer.equals(this.userThrewBall) && target.equals(this.userThrewAtBall)) {
                        return String.format("Silly %s, you cannot steal a ball that you just threw.", stealer);
                    }
                    if (stealer.equals(this.userThrewAtBall)) {
                        if (target.equals(this.userThrewAtBall)) {
                            return String.format("Silly %s, you cannot steal a ball from yourself.", stealer);
                        }
                        return String.format("Silly %s, the ball has been thrown at you. Why steal it from someone else?! Catch it now!", stealer);
                    }
                    if (target.equals(this.userThrewAtBall)) {
                        this.changeState(BallState.AT_HAND, this.userThrewBall, this.userThrewBall, stealer, ballCmd);
                        return String.format(emoteCommandData.getMessageText(), stealer, target);
                    }
                    return String.format("%s, the ball is not with %s, you cannot steal it.", stealer, target);
                }
                this.changeState(BallState.IDLE, this.userThrewBall, this.userThrewAtBall, this.userWithBall, ballCmd);
                return String.format("%s, the ball is free. Throw it now!", stealer);
            }
            return null;
        }

        private String executeWhenAtHand(BallCommand ballCmd, EmoteCommandData emoteCommandData, MessageData messageData, ChatSource chatSource) throws FusionException {
            long curTime = System.currentTimeMillis();
            if (ballCmd == BallCommand.THROW) {
                String thrower = messageData.source;
                if (thrower.equals(this.userWithBall)) {
                    String target = ThrowBall.parseThrowballCommand(messageData, chatSource, true);
                    this.changeState(BallState.THROWN, thrower, target, this.userWithBall, ballCmd);
                    return String.format(emoteCommandData.getMessageText(), thrower, target);
                }
                if (curTime - this.lastStateChangeTime <= 40000L) {
                    return String.format("%s, the ball is with %s. Wait for it to be thrown.", thrower, this.userWithBall);
                }
                String target = ThrowBall.parseThrowballCommand(messageData, chatSource, true);
                this.changeState(BallState.THROWN, thrower, target, this.userWithBall, ballCmd);
                return String.format(emoteCommandData.getMessageText(), thrower, target);
            }
            if (ballCmd == BallCommand.CATCH) {
                boolean expired;
                String catcher = messageData.source;
                boolean bl = expired = curTime - this.lastStateChangeTime > 40000L;
                if (!expired) {
                    if (catcher.equals(this.userWithBall)) {
                        return String.format("Silly %s, the ball is with you. Throw it now!", catcher);
                    }
                    BallData bd = this.dataMap.get(catcher);
                    if (bd != null && curTime - bd.timestamp <= 80000L) {
                        return String.format("Too bad, %s, %s has thrown the ball already. Be faster next time!", catcher, this.userThrewBall);
                    }
                    return String.format("%s, the ball is with %s. You cannot catch it now.", catcher, this.userWithBall);
                }
                this.changeState(BallState.IDLE, this.userThrewBall, this.userThrewAtBall, this.userWithBall, ballCmd);
                BallData bd = this.dataMap.get(catcher);
                if (bd != null && curTime - bd.timestamp <= 80000L) {
                    return String.format("Too bad, %s, %s has thrown the ball already. Be faster next time!", catcher, this.userThrewBall);
                }
                return String.format("%s, the ball is free. Throw it now!", catcher, this.userWithBall);
            }
            if (ballCmd == BallCommand.STEAL) {
                boolean expired;
                String stealer = messageData.source;
                String target = ThrowBall.parseStealballCommand(messageData, chatSource);
                boolean bl = expired = curTime - this.lastStateChangeTime > 40000L;
                if (!expired) {
                    if (stealer.equals(this.userWithBall)) {
                        if (target.equals(this.userWithBall)) {
                            return String.format("Silly %s, you cannot steal a ball from yourself.", stealer);
                        }
                        return String.format("Silly %s, the ball is with you. Why are you still trying to steal it from %s.", stealer, target);
                    }
                    if (target.equals(this.userWithBall)) {
                        return String.format("%s, the ball is with %s. Wait for it to be thrown.", stealer, this.userWithBall);
                    }
                    return String.format("%s, the ball is not with %s. You cannot steal it.", stealer, target);
                }
                this.changeState(BallState.IDLE, this.userThrewBall, this.userThrewAtBall, this.userWithBall, ballCmd);
                return String.format("%s, the ball is free. Throw it now!", stealer);
            }
            return null;
        }

        private String executeWhenIdle(BallCommand ballCmd, EmoteCommandData emoteCommandData, MessageData messageData, ChatSource chatSource) throws FusionException {
            if (ballCmd == BallCommand.THROW) {
                String target = ThrowBall.parseThrowballCommand(messageData, chatSource, true);
                String thrower = messageData.source;
                this.changeState(BallState.THROWN, thrower, target, this.userWithBall, ballCmd);
                return String.format(emoteCommandData.getMessageText(), thrower, target);
            }
            if (ballCmd == BallCommand.CATCH) {
                String catcher = messageData.source;
                return String.format("%s, the ball is free. Throw it Now!", catcher);
            }
            if (ballCmd == BallCommand.STEAL) {
                String catcher = messageData.source;
                return String.format("%s, the ball is free. Throw it Now!", catcher);
            }
            return null;
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        private static enum BallCommand {
            THROW,
            CATCH,
            STEAL;


            public static BallCommand fromString(String command) {
                if ("throwball".equals(command)) {
                    return THROW;
                }
                if ("catchball".equals(command)) {
                    return CATCH;
                }
                if ("stealball".equals(command)) {
                    return STEAL;
                }
                return null;
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        private static enum BallState {
            IDLE,
            THROWN,
            AT_HAND;

        }
    }

    public static class BallData {
        String username;
        long timestamp;

        public BallData(String username, long timestamp) {
            this.username = username;
            this.timestamp = timestamp;
        }

        public String toString() {
            return String.format("[BallData %s %s]", this.username, new Date(this.timestamp));
        }
    }
}

