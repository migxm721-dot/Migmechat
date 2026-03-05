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
import com.projectgoth.fusion.slice.StringArrayHelper;
import com.projectgoth.fusion.slice._BotChannelDel;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class _BotChannelDelM
extends _ObjectDelM
implements _BotChannelDel {
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
}

