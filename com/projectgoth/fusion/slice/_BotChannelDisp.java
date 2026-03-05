/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 *  Ice.DispatchStatus
 *  Ice.InputStream
 *  Ice.MarshalException
 *  Ice.Object
 *  Ice.ObjectImpl
 *  Ice.OperationMode
 *  Ice.OperationNotExistException
 *  Ice.OutputStream
 *  Ice.UserException
 *  IceInternal.BasicStream
 *  IceInternal.Incoming
 */
package com.projectgoth.fusion.slice;

import Ice.Current;
import Ice.DispatchStatus;
import Ice.InputStream;
import Ice.MarshalException;
import Ice.Object;
import Ice.ObjectImpl;
import Ice.OperationMode;
import Ice.OperationNotExistException;
import Ice.OutputStream;
import Ice.UserException;
import IceInternal.BasicStream;
import IceInternal.Incoming;
import com.projectgoth.fusion.slice.BotChannel;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.StringArrayHelper;
import java.util.Arrays;

public abstract class _BotChannelDisp
extends ObjectImpl
implements BotChannel {
    public static final String[] __ids = new String[]{"::Ice::Object", "::com::projectgoth::fusion::slice::BotChannel"};
    private static final String[] __all = new String[]{"botKilled", "getParticipants", "ice_id", "ice_ids", "ice_isA", "ice_ping", "isParticipant", "putBotMessage", "putBotMessageToAllUsers", "putBotMessageToUsers", "sendGamesHelpToUser", "sendMessageToBots", "startBot", "stopAllBots", "stopBot"};

    protected void ice_copyStateFrom(Object __obj) throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public boolean ice_isA(String s) {
        return Arrays.binarySearch(__ids, s) >= 0;
    }

    public boolean ice_isA(String s, Current __current) {
        return Arrays.binarySearch(__ids, s) >= 0;
    }

    public String[] ice_ids() {
        return __ids;
    }

    public String[] ice_ids(Current __current) {
        return __ids;
    }

    public String ice_id() {
        return __ids[1];
    }

    public String ice_id(Current __current) {
        return __ids[1];
    }

    public static String ice_staticId() {
        return __ids[1];
    }

    public final void botKilled(String botInstanceID) throws FusionException {
        this.botKilled(botInstanceID, null);
    }

    public final String[] getParticipants(String requestingUsername) {
        return this.getParticipants(requestingUsername, null);
    }

    public final boolean isParticipant(String username) throws FusionException {
        return this.isParticipant(username, null);
    }

    public final void putBotMessage(String botInstanceID, String username, String message, String[] emoticonHotKeys, boolean displayPopUp) throws FusionException {
        this.putBotMessage(botInstanceID, username, message, emoticonHotKeys, displayPopUp, null);
    }

    public final void putBotMessageToAllUsers(String botInstanceID, String message, String[] emoticonHotKeys, boolean displayPopUp) throws FusionException {
        this.putBotMessageToAllUsers(botInstanceID, message, emoticonHotKeys, displayPopUp, null);
    }

    public final void putBotMessageToUsers(String botInstanceID, String[] usernames, String message, String[] emoticonHotKeys, boolean displayPopUp) throws FusionException {
        this.putBotMessageToUsers(botInstanceID, usernames, message, emoticonHotKeys, displayPopUp, null);
    }

    public final void sendGamesHelpToUser(String username) throws FusionException {
        this.sendGamesHelpToUser(username, null);
    }

    public final void sendMessageToBots(String username, String message, long receivedTimestamp) throws FusionException {
        this.sendMessageToBots(username, message, receivedTimestamp, null);
    }

    public final void startBot(String username, String botCommandName) throws FusionException {
        this.startBot(username, botCommandName, null);
    }

    public final void stopAllBots(String username, int timeout) throws FusionException {
        this.stopAllBots(username, timeout, null);
    }

    public final void stopBot(String username, String botCommandName) throws FusionException {
        this.stopBot(username, botCommandName, null);
    }

    public static DispatchStatus ___startBot(BotChannel __obj, Incoming __inS, Current __current) {
        _BotChannelDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String username = __is.readString();
        String botCommandName = __is.readString();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            __obj.startBot(username, botCommandName, __current);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___stopBot(BotChannel __obj, Incoming __inS, Current __current) {
        _BotChannelDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String username = __is.readString();
        String botCommandName = __is.readString();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            __obj.stopBot(username, botCommandName, __current);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___stopAllBots(BotChannel __obj, Incoming __inS, Current __current) {
        _BotChannelDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String username = __is.readString();
        int timeout = __is.readInt();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            __obj.stopAllBots(username, timeout, __current);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___botKilled(BotChannel __obj, Incoming __inS, Current __current) {
        _BotChannelDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String botInstanceID = __is.readString();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            __obj.botKilled(botInstanceID, __current);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___sendMessageToBots(BotChannel __obj, Incoming __inS, Current __current) {
        _BotChannelDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String username = __is.readString();
        String message = __is.readString();
        long receivedTimestamp = __is.readLong();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            __obj.sendMessageToBots(username, message, receivedTimestamp, __current);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___putBotMessage(BotChannel __obj, Incoming __inS, Current __current) {
        _BotChannelDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String botInstanceID = __is.readString();
        String username = __is.readString();
        String message = __is.readString();
        String[] emoticonHotKeys = StringArrayHelper.read(__is);
        boolean displayPopUp = __is.readBool();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            __obj.putBotMessage(botInstanceID, username, message, emoticonHotKeys, displayPopUp, __current);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___putBotMessageToUsers(BotChannel __obj, Incoming __inS, Current __current) {
        _BotChannelDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String botInstanceID = __is.readString();
        String[] usernames = StringArrayHelper.read(__is);
        String message = __is.readString();
        String[] emoticonHotKeys = StringArrayHelper.read(__is);
        boolean displayPopUp = __is.readBool();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            __obj.putBotMessageToUsers(botInstanceID, usernames, message, emoticonHotKeys, displayPopUp, __current);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___putBotMessageToAllUsers(BotChannel __obj, Incoming __inS, Current __current) {
        _BotChannelDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String botInstanceID = __is.readString();
        String message = __is.readString();
        String[] emoticonHotKeys = StringArrayHelper.read(__is);
        boolean displayPopUp = __is.readBool();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            __obj.putBotMessageToAllUsers(botInstanceID, message, emoticonHotKeys, displayPopUp, __current);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___sendGamesHelpToUser(BotChannel __obj, Incoming __inS, Current __current) {
        _BotChannelDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String username = __is.readString();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            __obj.sendGamesHelpToUser(username, __current);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___isParticipant(BotChannel __obj, Incoming __inS, Current __current) {
        _BotChannelDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String username = __is.readString();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            boolean __ret = __obj.isParticipant(username, __current);
            __os.writeBool(__ret);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___getParticipants(BotChannel __obj, Incoming __inS, Current __current) {
        _BotChannelDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String requestingUsername = __is.readString();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        String[] __ret = __obj.getParticipants(requestingUsername, __current);
        StringArrayHelper.write(__os, __ret);
        return DispatchStatus.DispatchOK;
    }

    public DispatchStatus __dispatch(Incoming in, Current __current) {
        int pos = Arrays.binarySearch(__all, __current.operation);
        if (pos < 0) {
            throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
        }
        switch (pos) {
            case 0: {
                return _BotChannelDisp.___botKilled(this, in, __current);
            }
            case 1: {
                return _BotChannelDisp.___getParticipants(this, in, __current);
            }
            case 2: {
                return _BotChannelDisp.___ice_id((Object)this, (Incoming)in, (Current)__current);
            }
            case 3: {
                return _BotChannelDisp.___ice_ids((Object)this, (Incoming)in, (Current)__current);
            }
            case 4: {
                return _BotChannelDisp.___ice_isA((Object)this, (Incoming)in, (Current)__current);
            }
            case 5: {
                return _BotChannelDisp.___ice_ping((Object)this, (Incoming)in, (Current)__current);
            }
            case 6: {
                return _BotChannelDisp.___isParticipant(this, in, __current);
            }
            case 7: {
                return _BotChannelDisp.___putBotMessage(this, in, __current);
            }
            case 8: {
                return _BotChannelDisp.___putBotMessageToAllUsers(this, in, __current);
            }
            case 9: {
                return _BotChannelDisp.___putBotMessageToUsers(this, in, __current);
            }
            case 10: {
                return _BotChannelDisp.___sendGamesHelpToUser(this, in, __current);
            }
            case 11: {
                return _BotChannelDisp.___sendMessageToBots(this, in, __current);
            }
            case 12: {
                return _BotChannelDisp.___startBot(this, in, __current);
            }
            case 13: {
                return _BotChannelDisp.___stopAllBots(this, in, __current);
            }
            case 14: {
                return _BotChannelDisp.___stopBot(this, in, __current);
            }
        }
        assert (false);
        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
    }

    public void __write(BasicStream __os) {
        __os.writeTypeId(_BotChannelDisp.ice_staticId());
        __os.startWriteSlice();
        __os.endWriteSlice();
        super.__write(__os);
    }

    public void __read(BasicStream __is, boolean __rid) {
        if (__rid) {
            __is.readTypeId();
        }
        __is.startReadSlice();
        __is.endReadSlice();
        super.__read(__is, true);
    }

    public void __write(OutputStream __outS) {
        MarshalException ex = new MarshalException();
        ex.reason = "type com::projectgoth::fusion::slice::BotChannel was not generated with stream support";
        throw ex;
    }

    public void __read(InputStream __inS, boolean __rid) {
        MarshalException ex = new MarshalException();
        ex.reason = "type com::projectgoth::fusion::slice::BotChannel was not generated with stream support";
        throw ex;
    }
}

