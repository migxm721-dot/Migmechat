/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.LocalException
 *  Ice.OperationMode
 *  Ice.UnknownUserException
 *  Ice.UserException
 *  Ice._ObjectDelM
 *  IceInternal.BasicStream
 *  IceInternal.LocalExceptionWrapper
 *  IceInternal.Outgoing
 */
package com.projectgoth.fusion.slice;

import Ice.LocalException;
import Ice.OperationMode;
import Ice.UnknownUserException;
import Ice.UserException;
import Ice._ObjectDelM;
import IceInternal.BasicStream;
import IceInternal.LocalExceptionWrapper;
import IceInternal.Outgoing;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.IntArrayHelper;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.SessionPrx;
import com.projectgoth.fusion.slice.SessionPrxHelper;
import com.projectgoth.fusion.slice.StringArrayHelper;
import com.projectgoth.fusion.slice._GroupChatDel;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class _GroupChatDelM
extends _ObjectDelM
implements _GroupChatDel {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void botKilled(String botInstanceID, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("botKilled", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(botInstanceID);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                __og.is().skipEmptyEncaps();
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var7_10 = null;
        }
        catch (Throwable throwable) {
            Object var7_11 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public String[] getParticipants(String requestingUsername, Map<String, String> __ctx) throws LocalExceptionWrapper {
        String[] stringArray;
        Outgoing __og = this.__handler.getOutgoing("getParticipants", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(requestingUsername);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                BasicStream __is = __og.is();
                __is.startReadEncaps();
                String[] __ret = StringArrayHelper.read(__is);
                __is.endReadEncaps();
                stringArray = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var9_12 = null;
        }
        catch (Throwable throwable) {
            Object var9_13 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return stringArray;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public boolean isParticipant(String username, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        boolean bl;
        Outgoing __og = this.__handler.getOutgoing("isParticipant", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(username);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                BasicStream __is = __og.is();
                __is.startReadEncaps();
                boolean __ret = __is.readBool();
                __is.endReadEncaps();
                bl = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var9_13 = null;
        }
        catch (Throwable throwable) {
            Object var9_14 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void putBotMessage(String botInstanceID, String username, String message, String[] emoticonHotKeys, boolean displayPopUp, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("putBotMessage", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(botInstanceID);
                __os.writeString(username);
                __os.writeString(message);
                StringArrayHelper.write(__os, emoticonHotKeys);
                __os.writeBool(displayPopUp);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                __og.is().skipEmptyEncaps();
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var11_14 = null;
        }
        catch (Throwable throwable) {
            Object var11_15 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void putBotMessageToAllUsers(String botInstanceID, String message, String[] emoticonHotKeys, boolean displayPopUp, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("putBotMessageToAllUsers", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(botInstanceID);
                __os.writeString(message);
                StringArrayHelper.write(__os, emoticonHotKeys);
                __os.writeBool(displayPopUp);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                __og.is().skipEmptyEncaps();
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var10_13 = null;
        }
        catch (Throwable throwable) {
            Object var10_14 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void putBotMessageToUsers(String botInstanceID, String[] usernames, String message, String[] emoticonHotKeys, boolean displayPopUp, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("putBotMessageToUsers", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(botInstanceID);
                StringArrayHelper.write(__os, usernames);
                __os.writeString(message);
                StringArrayHelper.write(__os, emoticonHotKeys);
                __os.writeBool(displayPopUp);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                __og.is().skipEmptyEncaps();
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var11_14 = null;
        }
        catch (Throwable throwable) {
            Object var11_15 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void sendGamesHelpToUser(String username, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("sendGamesHelpToUser", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(username);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                __og.is().skipEmptyEncaps();
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var7_10 = null;
        }
        catch (Throwable throwable) {
            Object var7_11 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void sendMessageToBots(String username, String message, long receivedTimestamp, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("sendMessageToBots", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(username);
                __os.writeString(message);
                __os.writeLong(receivedTimestamp);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                __og.is().skipEmptyEncaps();
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var10_12 = null;
        }
        catch (Throwable throwable) {
            Object var10_13 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void startBot(String username, String botCommandName, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("startBot", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(username);
                __os.writeString(botCommandName);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                __og.is().skipEmptyEncaps();
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var8_11 = null;
        }
        catch (Throwable throwable) {
            Object var8_12 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void stopAllBots(String username, int timeout, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("stopAllBots", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(username);
                __os.writeInt(timeout);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                __og.is().skipEmptyEncaps();
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var8_11 = null;
        }
        catch (Throwable throwable) {
            Object var8_12 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void stopBot(String username, String botCommandName, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("stopBot", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(username);
                __os.writeString(botCommandName);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                __og.is().skipEmptyEncaps();
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var8_11 = null;
        }
        catch (Throwable throwable) {
            Object var8_12 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addParticipant(String inviterUsername, String inviteeUsername, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("addParticipant", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(inviterUsername);
                __os.writeString(inviteeUsername);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                __og.is().skipEmptyEncaps();
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var8_11 = null;
        }
        catch (Throwable throwable) {
            Object var8_12 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addParticipantInner(String inviterUsername, String inviteeUsername, boolean debug, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("addParticipantInner", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(inviterUsername);
                __os.writeString(inviteeUsername);
                __os.writeBool(debug);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                __og.is().skipEmptyEncaps();
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var9_12 = null;
        }
        catch (Throwable throwable) {
            Object var9_13 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addParticipants(String inviterUsername, String[] inviteeUsernames, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("addParticipants", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(inviterUsername);
                StringArrayHelper.write(__os, inviteeUsernames);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                __og.is().skipEmptyEncaps();
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var8_11 = null;
        }
        catch (Throwable throwable) {
            Object var8_12 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addUserToGroupChatDebug(String participant, boolean b, boolean c, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("addUserToGroupChatDebug", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(participant);
                __os.writeBool(b);
                __os.writeBool(c);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                __og.is().skipEmptyEncaps();
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var9_12 = null;
        }
        catch (Throwable throwable) {
            Object var9_13 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public int executeEmoteCommandWithState(String emoteCommand, MessageDataIce message, SessionPrx sessionProxy, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        int n;
        Outgoing __og = this.__handler.getOutgoing("executeEmoteCommandWithState", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(emoteCommand);
                message.__write(__os);
                SessionPrxHelper.__write(__os, sessionProxy);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                BasicStream __is = __og.is();
                __is.startReadEncaps();
                int __ret = __is.readInt();
                __is.endReadEncaps();
                n = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var11_15 = null;
        }
        catch (Throwable throwable) {
            Object var11_16 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return n;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public int getCreatorUserID(Map<String, String> __ctx) throws LocalExceptionWrapper {
        int n;
        Outgoing __og = this.__handler.getOutgoing("getCreatorUserID", OperationMode.Normal, __ctx);
        try {
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                BasicStream __is = __og.is();
                __is.startReadEncaps();
                int __ret = __is.readInt();
                __is.endReadEncaps();
                n = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var8_9 = null;
        }
        catch (Throwable throwable) {
            Object var8_10 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return n;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public String getCreatorUsername(Map<String, String> __ctx) throws LocalExceptionWrapper {
        String string;
        Outgoing __og = this.__handler.getOutgoing("getCreatorUsername", OperationMode.Normal, __ctx);
        try {
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                BasicStream __is = __og.is();
                __is.startReadEncaps();
                String __ret = __is.readString();
                __is.endReadEncaps();
                string = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var8_9 = null;
        }
        catch (Throwable throwable) {
            Object var8_10 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return string;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public String getId(Map<String, String> __ctx) throws LocalExceptionWrapper {
        String string;
        Outgoing __og = this.__handler.getOutgoing("getId", OperationMode.Normal, __ctx);
        try {
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                BasicStream __is = __og.is();
                __is.startReadEncaps();
                String __ret = __is.readString();
                __is.endReadEncaps();
                string = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var8_9 = null;
        }
        catch (Throwable throwable) {
            Object var8_10 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return string;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public int getNumParticipants(Map<String, String> __ctx) throws LocalExceptionWrapper {
        int n;
        Outgoing __og = this.__handler.getOutgoing("getNumParticipants", OperationMode.Normal, __ctx);
        try {
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                BasicStream __is = __og.is();
                __is.startReadEncaps();
                int __ret = __is.readInt();
                __is.endReadEncaps();
                n = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var8_9 = null;
        }
        catch (Throwable throwable) {
            Object var8_10 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return n;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public int[] getParticipantUserIDs(Map<String, String> __ctx) throws LocalExceptionWrapper {
        int[] nArray;
        Outgoing __og = this.__handler.getOutgoing("getParticipantUserIDs", OperationMode.Normal, __ctx);
        try {
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                BasicStream __is = __og.is();
                __is.startReadEncaps();
                int[] __ret = IntArrayHelper.read(__is);
                __is.endReadEncaps();
                nArray = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var8_9 = null;
        }
        catch (Throwable throwable) {
            Object var8_10 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return nArray;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public int getPrivateChatPartnerUserID(Map<String, String> __ctx) throws LocalExceptionWrapper {
        int n;
        Outgoing __og = this.__handler.getOutgoing("getPrivateChatPartnerUserID", OperationMode.Normal, __ctx);
        try {
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                BasicStream __is = __og.is();
                __is.startReadEncaps();
                int __ret = __is.readInt();
                __is.endReadEncaps();
                n = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var8_9 = null;
        }
        catch (Throwable throwable) {
            Object var8_10 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return n;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public String listOfParticipants(Map<String, String> __ctx) throws LocalExceptionWrapper {
        String string;
        Outgoing __og = this.__handler.getOutgoing("listOfParticipants", OperationMode.Normal, __ctx);
        try {
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                BasicStream __is = __og.is();
                __is.startReadEncaps();
                String __ret = __is.readString();
                __is.endReadEncaps();
                string = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var8_9 = null;
        }
        catch (Throwable throwable) {
            Object var8_10 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return string;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void putFileReceived(MessageDataIce message, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("putFileReceived", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                message.__write(__os);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                __og.is().skipEmptyEncaps();
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var7_10 = null;
        }
        catch (Throwable throwable) {
            Object var7_11 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void putMessage(MessageDataIce message, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("putMessage", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                message.__write(__os);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                __og.is().skipEmptyEncaps();
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var7_10 = null;
        }
        catch (Throwable throwable) {
            Object var7_11 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public boolean removeParticipant(String username, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        boolean bl;
        Outgoing __og = this.__handler.getOutgoing("removeParticipant", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(username);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                BasicStream __is = __og.is();
                __is.startReadEncaps();
                boolean __ret = __is.readBool();
                __is.endReadEncaps();
                bl = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var9_13 = null;
        }
        catch (Throwable throwable) {
            Object var9_14 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void sendInitialMessages(Map<String, String> __ctx) throws LocalExceptionWrapper {
        Outgoing __og = this.__handler.getOutgoing("sendInitialMessages", OperationMode.Normal, __ctx);
        try {
            boolean __ok = __og.invoke();
            if (!__og.is().isEmpty()) {
                try {
                    if (!__ok) {
                        try {
                            __og.throwUserException();
                        }
                        catch (UserException __ex) {
                            throw new UnknownUserException(__ex.ice_name());
                        }
                    }
                    __og.is().skipEmptyEncaps();
                }
                catch (LocalException __ex) {
                    throw new LocalExceptionWrapper(__ex, false);
                }
            }
            Object var6_6 = null;
        }
        catch (Throwable throwable) {
            Object var6_7 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public boolean supportsBinaryMessage(String usernameToExclude, Map<String, String> __ctx) throws LocalExceptionWrapper {
        boolean bl;
        Outgoing __og = this.__handler.getOutgoing("supportsBinaryMessage", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(usernameToExclude);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                BasicStream __is = __og.is();
                __is.startReadEncaps();
                boolean __ret = __is.readBool();
                __is.endReadEncaps();
                bl = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var9_12 = null;
        }
        catch (Throwable throwable) {
            Object var9_13 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return bl;
    }
}

