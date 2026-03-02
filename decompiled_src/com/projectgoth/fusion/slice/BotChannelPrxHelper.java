/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.FacetNotExistException
 *  Ice.LocalException
 *  Ice.ObjectPrx
 *  Ice.ObjectPrxHelperBase
 *  Ice._ObjectDel
 *  Ice._ObjectDelD
 *  Ice._ObjectDelM
 *  IceInternal.BasicStream
 *  IceInternal.LocalExceptionWrapper
 */
package com.projectgoth.fusion.slice;

import Ice.FacetNotExistException;
import Ice.LocalException;
import Ice.ObjectPrx;
import Ice.ObjectPrxHelperBase;
import Ice._ObjectDel;
import Ice._ObjectDelD;
import Ice._ObjectDelM;
import IceInternal.BasicStream;
import IceInternal.LocalExceptionWrapper;
import com.projectgoth.fusion.slice.BotChannelPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice._BotChannelDel;
import com.projectgoth.fusion.slice._BotChannelDelD;
import com.projectgoth.fusion.slice._BotChannelDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class BotChannelPrxHelper
extends ObjectPrxHelperBase
implements BotChannelPrx {
    @Override
    public void botKilled(String botInstanceID) throws FusionException {
        this.botKilled(botInstanceID, null, false);
    }

    @Override
    public void botKilled(String botInstanceID, Map<String, String> __ctx) throws FusionException {
        this.botKilled(botInstanceID, __ctx, true);
    }

    private void botKilled(String botInstanceID, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("botKilled");
                __delBase = this.__getDelegate(false);
                _BotChannelDel __del = (_BotChannelDel)__delBase;
                __del.botKilled(botInstanceID, __ctx);
                return;
            }
            catch (LocalExceptionWrapper __ex) {
                this.__handleExceptionWrapper(__delBase, __ex, null);
                continue;
            }
            catch (LocalException __ex) {
                __cnt = this.__handleException(__delBase, __ex, null, __cnt);
                continue;
            }
            break;
        }
    }

    @Override
    public String[] getParticipants(String requestingUsername) {
        return this.getParticipants(requestingUsername, null, false);
    }

    @Override
    public String[] getParticipants(String requestingUsername, Map<String, String> __ctx) {
        return this.getParticipants(requestingUsername, __ctx, true);
    }

    private String[] getParticipants(String requestingUsername, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getParticipants");
                __delBase = this.__getDelegate(false);
                _BotChannelDel __del = (_BotChannelDel)__delBase;
                return __del.getParticipants(requestingUsername, __ctx);
            }
            catch (LocalExceptionWrapper __ex) {
                this.__handleExceptionWrapper(__delBase, __ex, null);
                continue;
            }
            catch (LocalException __ex) {
                __cnt = this.__handleException(__delBase, __ex, null, __cnt);
                continue;
            }
            break;
        }
    }

    @Override
    public boolean isParticipant(String username) throws FusionException {
        return this.isParticipant(username, null, false);
    }

    @Override
    public boolean isParticipant(String username, Map<String, String> __ctx) throws FusionException {
        return this.isParticipant(username, __ctx, true);
    }

    private boolean isParticipant(String username, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("isParticipant");
                __delBase = this.__getDelegate(false);
                _BotChannelDel __del = (_BotChannelDel)__delBase;
                return __del.isParticipant(username, __ctx);
            }
            catch (LocalExceptionWrapper __ex) {
                this.__handleExceptionWrapper(__delBase, __ex, null);
                continue;
            }
            catch (LocalException __ex) {
                __cnt = this.__handleException(__delBase, __ex, null, __cnt);
                continue;
            }
            break;
        }
    }

    @Override
    public void putBotMessage(String botInstanceID, String username, String message, String[] emoticonHotKeys, boolean displayPopUp) throws FusionException {
        this.putBotMessage(botInstanceID, username, message, emoticonHotKeys, displayPopUp, null, false);
    }

    @Override
    public void putBotMessage(String botInstanceID, String username, String message, String[] emoticonHotKeys, boolean displayPopUp, Map<String, String> __ctx) throws FusionException {
        this.putBotMessage(botInstanceID, username, message, emoticonHotKeys, displayPopUp, __ctx, true);
    }

    private void putBotMessage(String botInstanceID, String username, String message, String[] emoticonHotKeys, boolean displayPopUp, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("putBotMessage");
                __delBase = this.__getDelegate(false);
                _BotChannelDel __del = (_BotChannelDel)__delBase;
                __del.putBotMessage(botInstanceID, username, message, emoticonHotKeys, displayPopUp, __ctx);
                return;
            }
            catch (LocalExceptionWrapper __ex) {
                this.__handleExceptionWrapper(__delBase, __ex, null);
                continue;
            }
            catch (LocalException __ex) {
                __cnt = this.__handleException(__delBase, __ex, null, __cnt);
                continue;
            }
            break;
        }
    }

    @Override
    public void putBotMessageToAllUsers(String botInstanceID, String message, String[] emoticonHotKeys, boolean displayPopUp) throws FusionException {
        this.putBotMessageToAllUsers(botInstanceID, message, emoticonHotKeys, displayPopUp, null, false);
    }

    @Override
    public void putBotMessageToAllUsers(String botInstanceID, String message, String[] emoticonHotKeys, boolean displayPopUp, Map<String, String> __ctx) throws FusionException {
        this.putBotMessageToAllUsers(botInstanceID, message, emoticonHotKeys, displayPopUp, __ctx, true);
    }

    private void putBotMessageToAllUsers(String botInstanceID, String message, String[] emoticonHotKeys, boolean displayPopUp, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("putBotMessageToAllUsers");
                __delBase = this.__getDelegate(false);
                _BotChannelDel __del = (_BotChannelDel)__delBase;
                __del.putBotMessageToAllUsers(botInstanceID, message, emoticonHotKeys, displayPopUp, __ctx);
                return;
            }
            catch (LocalExceptionWrapper __ex) {
                this.__handleExceptionWrapper(__delBase, __ex, null);
                continue;
            }
            catch (LocalException __ex) {
                __cnt = this.__handleException(__delBase, __ex, null, __cnt);
                continue;
            }
            break;
        }
    }

    @Override
    public void putBotMessageToUsers(String botInstanceID, String[] usernames, String message, String[] emoticonHotKeys, boolean displayPopUp) throws FusionException {
        this.putBotMessageToUsers(botInstanceID, usernames, message, emoticonHotKeys, displayPopUp, null, false);
    }

    @Override
    public void putBotMessageToUsers(String botInstanceID, String[] usernames, String message, String[] emoticonHotKeys, boolean displayPopUp, Map<String, String> __ctx) throws FusionException {
        this.putBotMessageToUsers(botInstanceID, usernames, message, emoticonHotKeys, displayPopUp, __ctx, true);
    }

    private void putBotMessageToUsers(String botInstanceID, String[] usernames, String message, String[] emoticonHotKeys, boolean displayPopUp, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("putBotMessageToUsers");
                __delBase = this.__getDelegate(false);
                _BotChannelDel __del = (_BotChannelDel)__delBase;
                __del.putBotMessageToUsers(botInstanceID, usernames, message, emoticonHotKeys, displayPopUp, __ctx);
                return;
            }
            catch (LocalExceptionWrapper __ex) {
                this.__handleExceptionWrapper(__delBase, __ex, null);
                continue;
            }
            catch (LocalException __ex) {
                __cnt = this.__handleException(__delBase, __ex, null, __cnt);
                continue;
            }
            break;
        }
    }

    @Override
    public void sendGamesHelpToUser(String username) throws FusionException {
        this.sendGamesHelpToUser(username, null, false);
    }

    @Override
    public void sendGamesHelpToUser(String username, Map<String, String> __ctx) throws FusionException {
        this.sendGamesHelpToUser(username, __ctx, true);
    }

    private void sendGamesHelpToUser(String username, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("sendGamesHelpToUser");
                __delBase = this.__getDelegate(false);
                _BotChannelDel __del = (_BotChannelDel)__delBase;
                __del.sendGamesHelpToUser(username, __ctx);
                return;
            }
            catch (LocalExceptionWrapper __ex) {
                this.__handleExceptionWrapper(__delBase, __ex, null);
                continue;
            }
            catch (LocalException __ex) {
                __cnt = this.__handleException(__delBase, __ex, null, __cnt);
                continue;
            }
            break;
        }
    }

    @Override
    public void sendMessageToBots(String username, String message, long receivedTimestamp) throws FusionException {
        this.sendMessageToBots(username, message, receivedTimestamp, null, false);
    }

    @Override
    public void sendMessageToBots(String username, String message, long receivedTimestamp, Map<String, String> __ctx) throws FusionException {
        this.sendMessageToBots(username, message, receivedTimestamp, __ctx, true);
    }

    private void sendMessageToBots(String username, String message, long receivedTimestamp, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("sendMessageToBots");
                __delBase = this.__getDelegate(false);
                _BotChannelDel __del = (_BotChannelDel)__delBase;
                __del.sendMessageToBots(username, message, receivedTimestamp, __ctx);
                return;
            }
            catch (LocalExceptionWrapper __ex) {
                this.__handleExceptionWrapper(__delBase, __ex, null);
                continue;
            }
            catch (LocalException __ex) {
                __cnt = this.__handleException(__delBase, __ex, null, __cnt);
                continue;
            }
            break;
        }
    }

    @Override
    public void startBot(String username, String botCommandName) throws FusionException {
        this.startBot(username, botCommandName, null, false);
    }

    @Override
    public void startBot(String username, String botCommandName, Map<String, String> __ctx) throws FusionException {
        this.startBot(username, botCommandName, __ctx, true);
    }

    private void startBot(String username, String botCommandName, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("startBot");
                __delBase = this.__getDelegate(false);
                _BotChannelDel __del = (_BotChannelDel)__delBase;
                __del.startBot(username, botCommandName, __ctx);
                return;
            }
            catch (LocalExceptionWrapper __ex) {
                this.__handleExceptionWrapper(__delBase, __ex, null);
                continue;
            }
            catch (LocalException __ex) {
                __cnt = this.__handleException(__delBase, __ex, null, __cnt);
                continue;
            }
            break;
        }
    }

    @Override
    public void stopAllBots(String username, int timeout) throws FusionException {
        this.stopAllBots(username, timeout, null, false);
    }

    @Override
    public void stopAllBots(String username, int timeout, Map<String, String> __ctx) throws FusionException {
        this.stopAllBots(username, timeout, __ctx, true);
    }

    private void stopAllBots(String username, int timeout, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("stopAllBots");
                __delBase = this.__getDelegate(false);
                _BotChannelDel __del = (_BotChannelDel)__delBase;
                __del.stopAllBots(username, timeout, __ctx);
                return;
            }
            catch (LocalExceptionWrapper __ex) {
                this.__handleExceptionWrapper(__delBase, __ex, null);
                continue;
            }
            catch (LocalException __ex) {
                __cnt = this.__handleException(__delBase, __ex, null, __cnt);
                continue;
            }
            break;
        }
    }

    @Override
    public void stopBot(String username, String botCommandName) throws FusionException {
        this.stopBot(username, botCommandName, null, false);
    }

    @Override
    public void stopBot(String username, String botCommandName, Map<String, String> __ctx) throws FusionException {
        this.stopBot(username, botCommandName, __ctx, true);
    }

    private void stopBot(String username, String botCommandName, Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("stopBot");
                __delBase = this.__getDelegate(false);
                _BotChannelDel __del = (_BotChannelDel)__delBase;
                __del.stopBot(username, botCommandName, __ctx);
                return;
            }
            catch (LocalExceptionWrapper __ex) {
                this.__handleExceptionWrapper(__delBase, __ex, null);
                continue;
            }
            catch (LocalException __ex) {
                __cnt = this.__handleException(__delBase, __ex, null, __cnt);
                continue;
            }
            break;
        }
    }

    public static BotChannelPrx checkedCast(ObjectPrx __obj) {
        BotChannelPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (BotChannelPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::BotChannel")) break block3;
                    BotChannelPrxHelper __h = new BotChannelPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static BotChannelPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        BotChannelPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (BotChannelPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::BotChannel", __ctx)) break block3;
                    BotChannelPrxHelper __h = new BotChannelPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static BotChannelPrx checkedCast(ObjectPrx __obj, String __facet) {
        BotChannelPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::BotChannel")) {
                    BotChannelPrxHelper __h = new BotChannelPrxHelper();
                    __h.__copyFrom(__bb);
                    __d = __h;
                }
            }
            catch (FacetNotExistException ex) {
                // empty catch block
            }
        }
        return __d;
    }

    public static BotChannelPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        BotChannelPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::BotChannel", __ctx)) {
                    BotChannelPrxHelper __h = new BotChannelPrxHelper();
                    __h.__copyFrom(__bb);
                    __d = __h;
                }
            }
            catch (FacetNotExistException ex) {
                // empty catch block
            }
        }
        return __d;
    }

    public static BotChannelPrx uncheckedCast(ObjectPrx __obj) {
        BotChannelPrx __d = null;
        if (__obj != null) {
            try {
                __d = (BotChannelPrx)__obj;
            }
            catch (ClassCastException ex) {
                BotChannelPrxHelper __h = new BotChannelPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static BotChannelPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        BotChannelPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            BotChannelPrxHelper __h = new BotChannelPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _BotChannelDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _BotChannelDelD();
    }

    public static void __write(BasicStream __os, BotChannelPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static BotChannelPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            BotChannelPrxHelper result = new BotChannelPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

