/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
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

import Ice.Current;
import Ice.DispatchStatus;
import Ice.OperationMode;
import Ice.OperationNotExistException;
import Ice.SystemException;
import Ice.UserException;
import Ice._ObjectDelD;
import IceInternal.Direct;
import IceInternal.LocalExceptionWrapper;
import com.projectgoth.fusion.slice.ConnectionPrx;
import com.projectgoth.fusion.slice.EventPrivacySettingIce;
import com.projectgoth.fusion.slice.EventPrivacySettingIceHolder;
import com.projectgoth.fusion.slice.EventSystem;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.UserEventIce;
import com.projectgoth.fusion.slice.UserEventIceArrayHolder;
import com.projectgoth.fusion.slice._EventSystemDel;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class _EventSystemDelD
extends _ObjectDelD
implements _EventSystemDel {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addedFriend(final String username, final String friend, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "addedFriend", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    EventSystem __servant = null;
                    try {
                        __servant = (EventSystem)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.addedFriend(username, friend, __current);
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
            }
            finally {
                __direct.destroy();
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
    public void createdPublicChatroom(final String username, final String chatroomName, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "createdPublicChatroom", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    EventSystem __servant = null;
                    try {
                        __servant = (EventSystem)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.createdPublicChatroom(username, chatroomName, __current);
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
            }
            finally {
                __direct.destroy();
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
    public void deleteUserEvents(final String username, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "deleteUserEvents", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    EventSystem __servant = null;
                    try {
                        __servant = (EventSystem)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.deleteUserEvents(username, __current);
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
            }
            finally {
                __direct.destroy();
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
    public void genericApplicationEvent(final String username, final String appId, final String text, final Map<String, String> customDeviceURL, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "genericApplicationEvent", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    EventSystem __servant = null;
                    try {
                        __servant = (EventSystem)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.genericApplicationEvent(username, appId, text, customDeviceURL, __current);
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
            }
            finally {
                __direct.destroy();
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
    public EventPrivacySettingIce getPublishingPrivacyMask(final String username, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        EventPrivacySettingIce eventPrivacySettingIce;
        final Current __current = new Current();
        this.__initCurrent(__current, "getPublishingPrivacyMask", OperationMode.Normal, __ctx);
        final EventPrivacySettingIceHolder __result = new EventPrivacySettingIceHolder();
        Direct __direct = null;
        __direct = new Direct(__current){

            public DispatchStatus run(Ice.Object __obj) {
                EventSystem __servant = null;
                try {
                    __servant = (EventSystem)__obj;
                }
                catch (ClassCastException __ex) {
                    throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                }
                try {
                    __result.value = __servant.getPublishingPrivacyMask(username, __current);
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
            eventPrivacySettingIce = __result.value;
        }
        catch (Throwable throwable) {
            try {
                __direct.destroy();
                throw throwable;
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
        __direct.destroy();
        return eventPrivacySettingIce;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public EventPrivacySettingIce getReceivingPrivacyMask(final String username, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        EventPrivacySettingIce eventPrivacySettingIce;
        final Current __current = new Current();
        this.__initCurrent(__current, "getReceivingPrivacyMask", OperationMode.Normal, __ctx);
        final EventPrivacySettingIceHolder __result = new EventPrivacySettingIceHolder();
        Direct __direct = null;
        __direct = new Direct(__current){

            public DispatchStatus run(Ice.Object __obj) {
                EventSystem __servant = null;
                try {
                    __servant = (EventSystem)__obj;
                }
                catch (ClassCastException __ex) {
                    throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                }
                try {
                    __result.value = __servant.getReceivingPrivacyMask(username, __current);
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
            eventPrivacySettingIce = __result.value;
        }
        catch (Throwable throwable) {
            try {
                __direct.destroy();
                throw throwable;
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
        __direct.destroy();
        return eventPrivacySettingIce;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public UserEventIce[] getUserEventsForUser(final String username, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        UserEventIce[] userEventIceArray;
        final Current __current = new Current();
        this.__initCurrent(__current, "getUserEventsForUser", OperationMode.Normal, __ctx);
        final UserEventIceArrayHolder __result = new UserEventIceArrayHolder();
        Direct __direct = null;
        __direct = new Direct(__current){

            public DispatchStatus run(Ice.Object __obj) {
                EventSystem __servant = null;
                try {
                    __servant = (EventSystem)__obj;
                }
                catch (ClassCastException __ex) {
                    throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                }
                try {
                    __result.value = __servant.getUserEventsForUser(username, __current);
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
            userEventIceArray = __result.value;
        }
        catch (Throwable throwable) {
            try {
                __direct.destroy();
                throw throwable;
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
        __direct.destroy();
        return userEventIceArray;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public UserEventIce[] getUserEventsGeneratedByUser(final String username, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        UserEventIce[] userEventIceArray;
        final Current __current = new Current();
        this.__initCurrent(__current, "getUserEventsGeneratedByUser", OperationMode.Normal, __ctx);
        final UserEventIceArrayHolder __result = new UserEventIceArrayHolder();
        Direct __direct = null;
        __direct = new Direct(__current){

            public DispatchStatus run(Ice.Object __obj) {
                EventSystem __servant = null;
                try {
                    __servant = (EventSystem)__obj;
                }
                catch (ClassCastException __ex) {
                    throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                }
                try {
                    __result.value = __servant.getUserEventsGeneratedByUser(username, __current);
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
            userEventIceArray = __result.value;
        }
        catch (Throwable throwable) {
            try {
                __direct.destroy();
                throw throwable;
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
        __direct.destroy();
        return userEventIceArray;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void giftShowerEvent(final String username, final String recipient, final String giftName, final int virtualGiftReceivedId, final int totalRecipients, Map<String, String> __ctx) throws LocalExceptionWrapper {
        final Current __current = new Current();
        this.__initCurrent(__current, "giftShowerEvent", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    EventSystem __servant = null;
                    try {
                        __servant = (EventSystem)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __servant.giftShowerEvent(username, recipient, giftName, virtualGiftReceivedId, totalRecipients, __current);
                    return DispatchStatus.DispatchOK;
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
            }
            finally {
                __direct.destroy();
            }
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
    public void groupAnnouncement(final String username, final int groupId, final int groupAnnoucementId, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "groupAnnouncement", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    EventSystem __servant = null;
                    try {
                        __servant = (EventSystem)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.groupAnnouncement(username, groupId, groupAnnoucementId, __current);
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
            }
            finally {
                __direct.destroy();
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
    public void groupDonation(final String username, final int groupId, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "groupDonation", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    EventSystem __servant = null;
                    try {
                        __servant = (EventSystem)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.groupDonation(username, groupId, __current);
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
            }
            finally {
                __direct.destroy();
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
    public void groupJoined(final String username, final int groupId, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "groupJoined", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    EventSystem __servant = null;
                    try {
                        __servant = (EventSystem)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.groupJoined(username, groupId, __current);
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
            }
            finally {
                __direct.destroy();
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
    public void madeGroupUserPost(final String username, final int userPostId, final int groupId, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "madeGroupUserPost", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    EventSystem __servant = null;
                    try {
                        __servant = (EventSystem)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.madeGroupUserPost(username, userPostId, groupId, __current);
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
            }
            finally {
                __direct.destroy();
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
    public void madePhotoPublic(final String username, final int scrapbookid, final String title, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "madePhotoPublic", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    EventSystem __servant = null;
                    try {
                        __servant = (EventSystem)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.madePhotoPublic(username, scrapbookid, title, __current);
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
            }
            finally {
                __direct.destroy();
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
    public void purchasedVirtualGoods(final String username, final byte itemType, final int itemid, final String itemName, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "purchasedVirtualGoods", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    EventSystem __servant = null;
                    try {
                        __servant = (EventSystem)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.purchasedVirtualGoods(username, itemType, itemid, itemName, __current);
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
            }
            finally {
                __direct.destroy();
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
    public void setProfileStatus(final String username, final String status, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "setProfileStatus", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    EventSystem __servant = null;
                    try {
                        __servant = (EventSystem)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.setProfileStatus(username, status, __current);
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
            }
            finally {
                __direct.destroy();
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
    public void setPublishingPrivacyMask(final String username, final EventPrivacySettingIce mask, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "setPublishingPrivacyMask", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    EventSystem __servant = null;
                    try {
                        __servant = (EventSystem)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.setPublishingPrivacyMask(username, mask, __current);
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
            }
            finally {
                __direct.destroy();
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
    public void setReceivingPrivacyMask(final String username, final EventPrivacySettingIce mask, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "setReceivingPrivacyMask", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    EventSystem __servant = null;
                    try {
                        __servant = (EventSystem)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.setReceivingPrivacyMask(username, mask, __current);
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
            }
            finally {
                __direct.destroy();
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
    public void streamEventsToLoggingInUser(final String username, final ConnectionPrx connectionProxy, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "streamEventsToLoggingInUser", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    EventSystem __servant = null;
                    try {
                        __servant = (EventSystem)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.streamEventsToLoggingInUser(username, connectionProxy, __current);
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
            }
            finally {
                __direct.destroy();
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
    public void updateAllowList(final String username, final String[] watchers, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "updateAllowList", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    EventSystem __servant = null;
                    try {
                        __servant = (EventSystem)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.updateAllowList(username, watchers, __current);
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
            }
            finally {
                __direct.destroy();
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
    public void updatedProfile(final String username, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "updatedProfile", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    EventSystem __servant = null;
                    try {
                        __servant = (EventSystem)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.updatedProfile(username, __current);
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
            }
            finally {
                __direct.destroy();
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
    public void userWallPost(final String username, final String wallOwnerUsername, final String postContent, final int userWallPostId, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "userWallPost", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    EventSystem __servant = null;
                    try {
                        __servant = (EventSystem)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.userWallPost(username, wallOwnerUsername, postContent, userWallPostId, __current);
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
            }
            finally {
                __direct.destroy();
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
    public void virtualGift(final String username, final String recipient, final String giftName, final int virtualGiftReceivedId, Map<String, String> __ctx) throws LocalExceptionWrapper {
        final Current __current = new Current();
        this.__initCurrent(__current, "virtualGift", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    EventSystem __servant = null;
                    try {
                        __servant = (EventSystem)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __servant.virtualGift(username, recipient, giftName, virtualGiftReceivedId, __current);
                    return DispatchStatus.DispatchOK;
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
            }
            finally {
                __direct.destroy();
            }
        }
        catch (SystemException __ex) {
            throw __ex;
        }
        catch (Throwable __ex) {
            LocalExceptionWrapper.throwWrapper((Throwable)__ex);
        }
    }
}

