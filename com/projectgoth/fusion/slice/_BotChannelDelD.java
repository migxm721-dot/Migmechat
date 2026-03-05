/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.BooleanHolder
 *  Ice.Current
 *  Ice.DispatchStatus
 *  Ice.Object
 *  Ice.OperationMode
 *  Ice.OperationNotExistException
 *  Ice.SystemException
 *  Ice.UserException
 *  Ice._ObjectDelD
 *  IceInternal.Direct
 *  IceInternal.LocalExceptionWrapper
 */
package com.projectgoth.fusion.slice;

import Ice.BooleanHolder;
import Ice.Current;
import Ice.DispatchStatus;
import Ice.Object;
import Ice.OperationMode;
import Ice.OperationNotExistException;
import Ice.SystemException;
import Ice.UserException;
import Ice._ObjectDelD;
import IceInternal.Direct;
import IceInternal.LocalExceptionWrapper;
import com.projectgoth.fusion.slice.BotChannel;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.StringArrayHolder;
import com.projectgoth.fusion.slice._BotChannelDel;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class _BotChannelDelD
extends _ObjectDelD
implements _BotChannelDel {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void botKilled(final String botInstanceID, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "botKilled", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Object __obj) {
                    BotChannel __servant = null;
                    try {
                        __servant = (BotChannel)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.botKilled(botInstanceID, __current);
                        return DispatchStatus.DispatchOK;
                    }
                    catch (UserException __ex) {
                        this.setUserException(__ex);
                        return DispatchStatus.DispatchUserException;
                    }
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                java.lang.Object var7_9 = null;
            }
            catch (Throwable throwable) {
                java.lang.Object var7_10 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            {
            }
        }
        catch (FusionException __ex) {
            throw __ex;
        }
        catch (SystemException __ex) {
            throw __ex;
        }
        catch (Throwable __ex) {
            LocalExceptionWrapper.throwWrapper((Throwable)__ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public String[] getParticipants(final String requestingUsername, Map<String, String> __ctx) throws LocalExceptionWrapper {
        final Current __current = new Current();
        this.__initCurrent(__current, "getParticipants", OperationMode.Normal, __ctx);
        final StringArrayHolder __result = new StringArrayHolder();
        Direct __direct = null;
        try {
            String[] stringArray;
            __direct = new Direct(__current){

                public DispatchStatus run(Object __obj) {
                    BotChannel __servant = null;
                    try {
                        __servant = (BotChannel)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __result.value = __servant.getParticipants(requestingUsername, __current);
                    return DispatchStatus.DispatchOK;
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                stringArray = __result.value;
                java.lang.Object var9_10 = null;
            }
            catch (Throwable throwable) {
                java.lang.Object var9_11 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return stringArray;
        }
        catch (SystemException __ex) {
            throw __ex;
        }
        catch (Throwable __ex) {
            LocalExceptionWrapper.throwWrapper((Throwable)__ex);
            return __result.value;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public boolean isParticipant(final String username, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "isParticipant", OperationMode.Normal, __ctx);
        final BooleanHolder __result = new BooleanHolder();
        Direct __direct = null;
        try {
            boolean bl;
            __direct = new Direct(__current){

                public DispatchStatus run(Object __obj) {
                    BotChannel __servant = null;
                    try {
                        __servant = (BotChannel)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __result.value = __servant.isParticipant(username, __current);
                        return DispatchStatus.DispatchOK;
                    }
                    catch (UserException __ex) {
                        this.setUserException(__ex);
                        return DispatchStatus.DispatchUserException;
                    }
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                bl = __result.value;
                java.lang.Object var9_11 = null;
            }
            catch (Throwable throwable) {
                java.lang.Object var9_12 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return bl;
        }
        catch (FusionException __ex) {
            throw __ex;
        }
        catch (SystemException __ex) {
            throw __ex;
        }
        catch (Throwable __ex) {
            LocalExceptionWrapper.throwWrapper((Throwable)__ex);
            return __result.value;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void putBotMessage(final String botInstanceID, final String username, final String message, final String[] emoticonHotKeys, final boolean displayPopUp, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "putBotMessage", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Object __obj) {
                    BotChannel __servant = null;
                    try {
                        __servant = (BotChannel)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.putBotMessage(botInstanceID, username, message, emoticonHotKeys, displayPopUp, __current);
                        return DispatchStatus.DispatchOK;
                    }
                    catch (UserException __ex) {
                        this.setUserException(__ex);
                        return DispatchStatus.DispatchUserException;
                    }
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                java.lang.Object var11_13 = null;
            }
            catch (Throwable throwable) {
                java.lang.Object var11_14 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            {
            }
        }
        catch (FusionException __ex) {
            throw __ex;
        }
        catch (SystemException __ex) {
            throw __ex;
        }
        catch (Throwable __ex) {
            LocalExceptionWrapper.throwWrapper((Throwable)__ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void putBotMessageToAllUsers(final String botInstanceID, final String message, final String[] emoticonHotKeys, final boolean displayPopUp, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "putBotMessageToAllUsers", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Object __obj) {
                    BotChannel __servant = null;
                    try {
                        __servant = (BotChannel)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.putBotMessageToAllUsers(botInstanceID, message, emoticonHotKeys, displayPopUp, __current);
                        return DispatchStatus.DispatchOK;
                    }
                    catch (UserException __ex) {
                        this.setUserException(__ex);
                        return DispatchStatus.DispatchUserException;
                    }
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                java.lang.Object var10_12 = null;
            }
            catch (Throwable throwable) {
                java.lang.Object var10_13 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            {
            }
        }
        catch (FusionException __ex) {
            throw __ex;
        }
        catch (SystemException __ex) {
            throw __ex;
        }
        catch (Throwable __ex) {
            LocalExceptionWrapper.throwWrapper((Throwable)__ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void putBotMessageToUsers(final String botInstanceID, final String[] usernames, final String message, final String[] emoticonHotKeys, final boolean displayPopUp, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "putBotMessageToUsers", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Object __obj) {
                    BotChannel __servant = null;
                    try {
                        __servant = (BotChannel)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.putBotMessageToUsers(botInstanceID, usernames, message, emoticonHotKeys, displayPopUp, __current);
                        return DispatchStatus.DispatchOK;
                    }
                    catch (UserException __ex) {
                        this.setUserException(__ex);
                        return DispatchStatus.DispatchUserException;
                    }
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                java.lang.Object var11_13 = null;
            }
            catch (Throwable throwable) {
                java.lang.Object var11_14 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            {
            }
        }
        catch (FusionException __ex) {
            throw __ex;
        }
        catch (SystemException __ex) {
            throw __ex;
        }
        catch (Throwable __ex) {
            LocalExceptionWrapper.throwWrapper((Throwable)__ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void sendGamesHelpToUser(final String username, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "sendGamesHelpToUser", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Object __obj) {
                    BotChannel __servant = null;
                    try {
                        __servant = (BotChannel)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.sendGamesHelpToUser(username, __current);
                        return DispatchStatus.DispatchOK;
                    }
                    catch (UserException __ex) {
                        this.setUserException(__ex);
                        return DispatchStatus.DispatchUserException;
                    }
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                java.lang.Object var7_9 = null;
            }
            catch (Throwable throwable) {
                java.lang.Object var7_10 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            {
            }
        }
        catch (FusionException __ex) {
            throw __ex;
        }
        catch (SystemException __ex) {
            throw __ex;
        }
        catch (Throwable __ex) {
            LocalExceptionWrapper.throwWrapper((Throwable)__ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void sendMessageToBots(final String username, final String message, final long receivedTimestamp, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "sendMessageToBots", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Object __obj) {
                    BotChannel __servant = null;
                    try {
                        __servant = (BotChannel)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.sendMessageToBots(username, message, receivedTimestamp, __current);
                        return DispatchStatus.DispatchOK;
                    }
                    catch (UserException __ex) {
                        this.setUserException(__ex);
                        return DispatchStatus.DispatchUserException;
                    }
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                java.lang.Object var10_11 = null;
            }
            catch (Throwable throwable) {
                java.lang.Object var10_12 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            {
            }
        }
        catch (FusionException __ex) {
            throw __ex;
        }
        catch (SystemException __ex) {
            throw __ex;
        }
        catch (Throwable __ex) {
            LocalExceptionWrapper.throwWrapper((Throwable)__ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void startBot(final String username, final String botCommandName, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "startBot", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Object __obj) {
                    BotChannel __servant = null;
                    try {
                        __servant = (BotChannel)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.startBot(username, botCommandName, __current);
                        return DispatchStatus.DispatchOK;
                    }
                    catch (UserException __ex) {
                        this.setUserException(__ex);
                        return DispatchStatus.DispatchUserException;
                    }
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                java.lang.Object var8_10 = null;
            }
            catch (Throwable throwable) {
                java.lang.Object var8_11 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            {
            }
        }
        catch (FusionException __ex) {
            throw __ex;
        }
        catch (SystemException __ex) {
            throw __ex;
        }
        catch (Throwable __ex) {
            LocalExceptionWrapper.throwWrapper((Throwable)__ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void stopAllBots(final String username, final int timeout, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "stopAllBots", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Object __obj) {
                    BotChannel __servant = null;
                    try {
                        __servant = (BotChannel)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.stopAllBots(username, timeout, __current);
                        return DispatchStatus.DispatchOK;
                    }
                    catch (UserException __ex) {
                        this.setUserException(__ex);
                        return DispatchStatus.DispatchUserException;
                    }
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                java.lang.Object var8_10 = null;
            }
            catch (Throwable throwable) {
                java.lang.Object var8_11 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            {
            }
        }
        catch (FusionException __ex) {
            throw __ex;
        }
        catch (SystemException __ex) {
            throw __ex;
        }
        catch (Throwable __ex) {
            LocalExceptionWrapper.throwWrapper((Throwable)__ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void stopBot(final String username, final String botCommandName, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "stopBot", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Object __obj) {
                    BotChannel __servant = null;
                    try {
                        __servant = (BotChannel)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.stopBot(username, botCommandName, __current);
                        return DispatchStatus.DispatchOK;
                    }
                    catch (UserException __ex) {
                        this.setUserException(__ex);
                        return DispatchStatus.DispatchUserException;
                    }
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                java.lang.Object var8_10 = null;
            }
            catch (Throwable throwable) {
                java.lang.Object var8_11 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            {
            }
        }
        catch (FusionException __ex) {
            throw __ex;
        }
        catch (SystemException __ex) {
            throw __ex;
        }
        catch (Throwable __ex) {
            LocalExceptionWrapper.throwWrapper((Throwable)__ex);
        }
    }
}

