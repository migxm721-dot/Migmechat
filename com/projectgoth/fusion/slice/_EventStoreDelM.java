/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.LocalException
 *  Ice.Object
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
import Ice.Object;
import Ice.OperationMode;
import Ice.UnknownUserException;
import Ice.UserException;
import Ice._ObjectDelM;
import IceInternal.BasicStream;
import IceInternal.LocalExceptionWrapper;
import IceInternal.Outgoing;
import com.projectgoth.fusion.slice.EventPrivacySettingIce;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.UserEventIce;
import com.projectgoth.fusion.slice.UserEventIceArrayHelper;
import com.projectgoth.fusion.slice._EventStoreDel;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class _EventStoreDelM
extends _ObjectDelM
implements _EventStoreDel {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void deleteUserEvents(String username, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("deleteUserEvents", OperationMode.Normal, __ctx);
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
            java.lang.Object var7_10 = null;
        }
        catch (Throwable throwable) {
            java.lang.Object var7_11 = null;
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
    public EventPrivacySettingIce getPublishingPrivacyMask(String username, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        EventPrivacySettingIce eventPrivacySettingIce;
        Outgoing __og = this.__handler.getOutgoing("getPublishingPrivacyMask", OperationMode.Normal, __ctx);
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
                EventPrivacySettingIce __ret = new EventPrivacySettingIce();
                __ret.__read(__is);
                __is.endReadEncaps();
                eventPrivacySettingIce = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            java.lang.Object var9_13 = null;
        }
        catch (Throwable throwable) {
            java.lang.Object var9_14 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return eventPrivacySettingIce;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public EventPrivacySettingIce getReceivingPrivacyMask(String username, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        EventPrivacySettingIce eventPrivacySettingIce;
        Outgoing __og = this.__handler.getOutgoing("getReceivingPrivacyMask", OperationMode.Normal, __ctx);
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
                EventPrivacySettingIce __ret = new EventPrivacySettingIce();
                __ret.__read(__is);
                __is.endReadEncaps();
                eventPrivacySettingIce = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            java.lang.Object var9_13 = null;
        }
        catch (Throwable throwable) {
            java.lang.Object var9_14 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return eventPrivacySettingIce;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public UserEventIce[] getUserEventsForUser(String username, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        UserEventIce[] userEventIceArray;
        Outgoing __og = this.__handler.getOutgoing("getUserEventsForUser", OperationMode.Normal, __ctx);
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
                UserEventIce[] __ret = UserEventIceArrayHelper.read(__is);
                __is.readPendingObjects();
                __is.endReadEncaps();
                userEventIceArray = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            java.lang.Object var9_13 = null;
        }
        catch (Throwable throwable) {
            java.lang.Object var9_14 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return userEventIceArray;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public UserEventIce[] getUserEventsGeneratedByUser(String username, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        UserEventIce[] userEventIceArray;
        Outgoing __og = this.__handler.getOutgoing("getUserEventsGeneratedByUser", OperationMode.Normal, __ctx);
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
                UserEventIce[] __ret = UserEventIceArrayHelper.read(__is);
                __is.readPendingObjects();
                __is.endReadEncaps();
                userEventIceArray = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            java.lang.Object var9_13 = null;
        }
        catch (Throwable throwable) {
            java.lang.Object var9_14 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return userEventIceArray;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setPublishingPrivacyMask(String username, EventPrivacySettingIce mask, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("setPublishingPrivacyMask", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(username);
                mask.__write(__os);
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
            java.lang.Object var8_11 = null;
        }
        catch (Throwable throwable) {
            java.lang.Object var8_12 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setReceivingPrivacyMask(String username, EventPrivacySettingIce mask, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("setReceivingPrivacyMask", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(username);
                mask.__write(__os);
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
            java.lang.Object var8_11 = null;
        }
        catch (Throwable throwable) {
            java.lang.Object var8_12 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void storeGeneratorEvent(String username, UserEventIce event, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("storeGeneratorEvent", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(username);
                __os.writeObject((Object)event);
                __os.writePendingObjects();
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
            java.lang.Object var8_11 = null;
        }
        catch (Throwable throwable) {
            java.lang.Object var8_12 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void storeUserEvent(String username, UserEventIce event, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("storeUserEvent", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(username);
                __os.writeObject((Object)event);
                __os.writePendingObjects();
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
            java.lang.Object var8_11 = null;
        }
        catch (Throwable throwable) {
            java.lang.Object var8_12 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }
}

