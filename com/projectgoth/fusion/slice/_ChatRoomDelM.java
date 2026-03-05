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
import com.projectgoth.fusion.slice.ChatRoomDataIce;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.IntArrayHelper;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.ParamMapHelper;
import com.projectgoth.fusion.slice.SessionPrx;
import com.projectgoth.fusion.slice.SessionPrxHelper;
import com.projectgoth.fusion.slice.StringArrayHelper;
import com.projectgoth.fusion.slice.UserDataIce;
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.slice.UserPrxHelper;
import com.projectgoth.fusion.slice._ChatRoomDel;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class _ChatRoomDelM
extends _ObjectDelM
implements _ChatRoomDel {
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
    public void addGroupModerator(String instigator, String targetUser, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("addGroupModerator", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(instigator);
                __os.writeString(targetUser);
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
    public void addModerator(String username, Map<String, String> __ctx) throws LocalExceptionWrapper {
        Outgoing __og = this.__handler.getOutgoing("addModerator", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(username);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
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
            Object var7_9 = null;
        }
        catch (Throwable throwable) {
            Object var7_10 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addParticipant(UserPrx userProxy, UserDataIce userData, SessionPrx sessionProxy, String sessionID, String ipAddress, String mobileDevice, String userAgent, short clientVersion, int deviceType, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("addParticipant", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                UserPrxHelper.__write(__os, userProxy);
                userData.__write(__os);
                SessionPrxHelper.__write(__os, sessionProxy);
                __os.writeString(sessionID);
                __os.writeString(ipAddress);
                __os.writeString(mobileDevice);
                __os.writeString(userAgent);
                __os.writeShort(clientVersion);
                __os.writeInt(deviceType);
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
            Object var15_18 = null;
        }
        catch (Throwable throwable) {
            Object var15_19 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addParticipantOld(UserPrx userProxy, UserDataIce userData, SessionPrx sessionProxy, String sessionID, String ipAddress, String mobileDevice, String userAgent, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("addParticipantOld", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                UserPrxHelper.__write(__os, userProxy);
                userData.__write(__os);
                SessionPrxHelper.__write(__os, sessionProxy);
                __os.writeString(sessionID);
                __os.writeString(ipAddress);
                __os.writeString(mobileDevice);
                __os.writeString(userAgent);
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
            Object var13_16 = null;
        }
        catch (Throwable throwable) {
            Object var13_17 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void adminAnnounce(String announceMessage, int waitTime, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("adminAnnounce", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(announceMessage);
                __os.writeInt(waitTime);
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
    public void announceOff(String announcer, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("announceOff", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(announcer);
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
    public void announceOn(String announcer, String announceMessage, int waitTime, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("announceOn", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(announcer);
                __os.writeString(announceMessage);
                __os.writeInt(waitTime);
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
    public void banGroupMembers(String[] banList, String instigator, int reasonCode, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("banGroupMembers", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                StringArrayHelper.write(__os, banList);
                __os.writeString(instigator);
                __os.writeInt(reasonCode);
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
    public void banIndexes(int[] indexes, String bannedBy, int reasonCode, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("banIndexes", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                IntArrayHelper.write(__os, indexes);
                __os.writeString(bannedBy);
                __os.writeInt(reasonCode);
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
    public void banMultiIds(String username, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("banMultiIds", OperationMode.Normal, __ctx);
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
    public void banUser(String username, Map<String, String> __ctx) throws LocalExceptionWrapper {
        Outgoing __og = this.__handler.getOutgoing("banUser", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(username);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
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
            Object var7_9 = null;
        }
        catch (Throwable throwable) {
            Object var7_10 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void broadcastMessage(String instigator, String message, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("broadcastMessage", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(instigator);
                __os.writeString(message);
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
    public void bumpUser(String instigator, String target, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("bumpUser", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(instigator);
                __os.writeString(target);
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
    public void changeOwner(String oldOwnerUsername, String newOwnerUsername, Map<String, String> __ctx) throws LocalExceptionWrapper {
        Outgoing __og = this.__handler.getOutgoing("changeOwner", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(oldOwnerUsername);
                __os.writeString(newOwnerUsername);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
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
            Object var8_10 = null;
        }
        catch (Throwable throwable) {
            Object var8_11 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void clearUserKick(String instigator, String target, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("clearUserKick", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(instigator);
                __os.writeString(target);
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
    public void convertIntoGroupChatRoom(int groupID, String groupName, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("convertIntoGroupChatRoom", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeInt(groupID);
                __os.writeString(groupName);
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
    public void convertIntoUserOwnedChatRoom(Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("convertIntoUserOwnedChatRoom", OperationMode.Normal, __ctx);
        try {
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
            Object var6_7 = null;
        }
        catch (Throwable throwable) {
            Object var6_8 = null;
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
    public String[] getAdministrators(String requestingUsername, Map<String, String> __ctx) throws LocalExceptionWrapper {
        String[] stringArray;
        Outgoing __og = this.__handler.getOutgoing("getAdministrators", OperationMode.Normal, __ctx);
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
    public String[] getAllParticipants(String requestingUsername, Map<String, String> __ctx) throws LocalExceptionWrapper {
        String[] stringArray;
        Outgoing __og = this.__handler.getOutgoing("getAllParticipants", OperationMode.Normal, __ctx);
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
    public String[] getGroupModerators(String instigator, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        String[] stringArray;
        Outgoing __og = this.__handler.getOutgoing("getGroupModerators", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(instigator);
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
                String[] __ret = StringArrayHelper.read(__is);
                __is.endReadEncaps();
                stringArray = __ret;
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
        return stringArray;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public int getMaximumMessageLength(String sender, Map<String, String> __ctx) throws LocalExceptionWrapper {
        int n;
        Outgoing __og = this.__handler.getOutgoing("getMaximumMessageLength", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(sender);
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
                int __ret = __is.readInt();
                __is.endReadEncaps();
                n = __ret;
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
        return n;
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
    public ChatRoomDataIce getRoomData(Map<String, String> __ctx) throws LocalExceptionWrapper {
        ChatRoomDataIce chatRoomDataIce;
        Outgoing __og = this.__handler.getOutgoing("getRoomData", OperationMode.Normal, __ctx);
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
                ChatRoomDataIce __ret = new ChatRoomDataIce();
                __ret.__read(__is);
                __is.endReadEncaps();
                chatRoomDataIce = __ret;
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
        return chatRoomDataIce;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public Map<String, String> getTheme(Map<String, String> __ctx) throws LocalExceptionWrapper {
        Map<String, String> map;
        Outgoing __og = this.__handler.getOutgoing("getTheme", OperationMode.Normal, __ctx);
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
                Map<String, String> __ret = ParamMapHelper.read(__is);
                __is.endReadEncaps();
                map = __ret;
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
        return map;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void inviteUserToGroup(String invitee, String inviter, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("inviteUserToGroup", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(invitee);
                __os.writeString(inviter);
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
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public boolean isLocked(Map<String, String> __ctx) throws LocalExceptionWrapper {
        boolean bl;
        Outgoing __og = this.__handler.getOutgoing("isLocked", OperationMode.Normal, __ctx);
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
                boolean __ret = __is.readBool();
                __is.endReadEncaps();
                bl = __ret;
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
        return bl;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public boolean isVisibleParticipant(String username, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        boolean bl;
        Outgoing __og = this.__handler.getOutgoing("isVisibleParticipant", OperationMode.Normal, __ctx);
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
    public void kickIndexes(int[] indexes, String bannedBy, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("kickIndexes", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                IntArrayHelper.write(__os, indexes);
                __os.writeString(bannedBy);
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
    public void listParticipants(String requestingUsername, int size, int startIndex, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("listParticipants", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(requestingUsername);
                __os.writeInt(size);
                __os.writeInt(startIndex);
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
    public void lock(String locker, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("lock", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(locker);
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
    public void mute(String username, String target, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("mute", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(username);
                __os.writeString(target);
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
    public void putMessage(MessageDataIce message, String sessionID, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("putMessage", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                message.__write(__os);
                __os.writeString(sessionID);
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
    public void putSystemMessage(String messageText, String[] emoticonKeys, Map<String, String> __ctx) throws LocalExceptionWrapper {
        Outgoing __og = this.__handler.getOutgoing("putSystemMessage", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(messageText);
                StringArrayHelper.write(__os, emoticonKeys);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
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
            Object var8_10 = null;
        }
        catch (Throwable throwable) {
            Object var8_11 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void putSystemMessageWithColour(String messageText, String[] emoticonKeys, int messageColour, Map<String, String> __ctx) throws LocalExceptionWrapper {
        Outgoing __og = this.__handler.getOutgoing("putSystemMessageWithColour", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(messageText);
                StringArrayHelper.write(__os, emoticonKeys);
                __os.writeInt(messageColour);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
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
            Object var9_11 = null;
        }
        catch (Throwable throwable) {
            Object var9_12 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeGroupModerator(String instigator, String targetUser, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("removeGroupModerator", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(instigator);
                __os.writeString(targetUser);
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
    public void removeModerator(String username, Map<String, String> __ctx) throws LocalExceptionWrapper {
        Outgoing __og = this.__handler.getOutgoing("removeModerator", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(username);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
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
            Object var7_9 = null;
        }
        catch (Throwable throwable) {
            Object var7_10 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeParticipant(String username, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
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
    public void removeParticipantOneWay(String username, boolean removeFromUsersChatRoomList, Map<String, String> __ctx) throws LocalExceptionWrapper {
        Outgoing __og = this.__handler.getOutgoing("removeParticipantOneWay", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(username);
                __os.writeBool(removeFromUsersChatRoomList);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
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
            Object var8_10 = null;
        }
        catch (Throwable throwable) {
            Object var8_11 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setAdultOnly(boolean adultOnly, Map<String, String> __ctx) throws LocalExceptionWrapper {
        Outgoing __og = this.__handler.getOutgoing("setAdultOnly", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeBool(adultOnly);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
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
            Object var7_9 = null;
        }
        catch (Throwable throwable) {
            Object var7_10 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setAllowKicking(boolean allowKicking, Map<String, String> __ctx) throws LocalExceptionWrapper {
        Outgoing __og = this.__handler.getOutgoing("setAllowKicking", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeBool(allowKicking);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
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
            Object var7_9 = null;
        }
        catch (Throwable throwable) {
            Object var7_10 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setDescription(String description, Map<String, String> __ctx) throws LocalExceptionWrapper {
        Outgoing __og = this.__handler.getOutgoing("setDescription", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(description);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
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
            Object var7_9 = null;
        }
        catch (Throwable throwable) {
            Object var7_10 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setMaximumSize(int maximumSize, Map<String, String> __ctx) throws LocalExceptionWrapper {
        Outgoing __og = this.__handler.getOutgoing("setMaximumSize", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeInt(maximumSize);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
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
            Object var7_9 = null;
        }
        catch (Throwable throwable) {
            Object var7_10 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setNumberOfFakeParticipants(String username, int number, Map<String, String> __ctx) throws LocalExceptionWrapper {
        Outgoing __og = this.__handler.getOutgoing("setNumberOfFakeParticipants", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(username);
                __os.writeInt(number);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
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
            Object var8_10 = null;
        }
        catch (Throwable throwable) {
            Object var8_11 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void silence(String username, int timeout, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("silence", OperationMode.Normal, __ctx);
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
    public void silenceUser(String instigator, String target, int timeout, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("silenceUser", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(instigator);
                __os.writeString(target);
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
    public void submitGiftAllTask(int giftId, String giftMessage, MessageDataIce message, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("submitGiftAllTask", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeInt(giftId);
                __os.writeString(giftMessage);
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
    public void unbanGroupMember(String target, String instigator, int reasonCode, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("unbanGroupMember", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(target);
                __os.writeString(instigator);
                __os.writeInt(reasonCode);
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
    public void unbanUser(String username, Map<String, String> __ctx) throws LocalExceptionWrapper {
        Outgoing __og = this.__handler.getOutgoing("unbanUser", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(username);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
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
            Object var7_9 = null;
        }
        catch (Throwable throwable) {
            Object var7_10 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unlock(String unlocker, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("unlock", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(unlocker);
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
    public void unmute(String username, String target, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("unmute", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(username);
                __os.writeString(target);
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
    public void unsilence(String username, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("unsilence", OperationMode.Normal, __ctx);
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
    public void unsilenceUser(String instigator, String target, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("unsilenceUser", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(instigator);
                __os.writeString(target);
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
    public void updateDescription(String instigator, String description, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("updateDescription", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(instigator);
                __os.writeString(description);
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
    public void updateExtraData(ChatRoomDataIce data, Map<String, String> __ctx) throws LocalExceptionWrapper {
        Outgoing __og = this.__handler.getOutgoing("updateExtraData", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                data.__write(__os);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
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
            Object var7_9 = null;
        }
        catch (Throwable throwable) {
            Object var7_10 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void updateGroupModeratorStatus(String username, boolean promote, Map<String, String> __ctx) throws LocalExceptionWrapper {
        Outgoing __og = this.__handler.getOutgoing("updateGroupModeratorStatus", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(username);
                __os.writeBool(promote);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
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
            Object var8_10 = null;
        }
        catch (Throwable throwable) {
            Object var8_11 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void voteToKickUser(String voter, String target, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("voteToKickUser", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(voter);
                __os.writeString(target);
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
    public void warnUser(String instigator, String target, String message, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("warnUser", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(instigator);
                __os.writeString(target);
                __os.writeString(message);
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
}

