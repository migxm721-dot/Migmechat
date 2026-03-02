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
import Ice.ObjectImpl;
import Ice.OperationMode;
import Ice.OperationNotExistException;
import Ice.OutputStream;
import Ice.UserException;
import IceInternal.BasicStream;
import IceInternal.Incoming;
import com.projectgoth.fusion.slice.AMD_ObjectCache_createUserObject;
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.ChatRoomPrxHelper;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GroupChatArrayHelper;
import com.projectgoth.fusion.slice.GroupChatPrx;
import com.projectgoth.fusion.slice.GroupChatPrxHelper;
import com.projectgoth.fusion.slice.MessageSwitchboardPrx;
import com.projectgoth.fusion.slice.MessageSwitchboardPrxHelper;
import com.projectgoth.fusion.slice.ObjectCache;
import com.projectgoth.fusion.slice.ObjectExistsException;
import com.projectgoth.fusion.slice.StringArrayHelper;
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.slice.UserPrxHelper;
import com.projectgoth.fusion.slice._AMD_ObjectCache_createUserObject;
import java.util.Arrays;

public abstract class _ObjectCacheDisp
extends ObjectImpl
implements ObjectCache {
    public static final String[] __ids = new String[]{"::Ice::Object", "::com::projectgoth::fusion::slice::ObjectCache"};
    private static final String[] __all = new String[]{"createChatRoomObject", "createGroupChatObject", "createUserObject", "createUserObjectNonAsync", "getAllGroupChats", "getMessageSwitchboard", "ice_id", "ice_ids", "ice_isA", "ice_ping", "purgeGroupChatObject", "purgeUserObject", "sendAlertMessageToAllUsers"};

    protected void ice_copyStateFrom(Ice.Object __obj) throws CloneNotSupportedException {
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

    public final ChatRoomPrx createChatRoomObject(String name) throws FusionException, ObjectExistsException {
        return this.createChatRoomObject(name, null);
    }

    public final GroupChatPrx createGroupChatObject(String id, String creator, String privateChatPartner, String[] otherPartyList) throws FusionException, ObjectExistsException {
        return this.createGroupChatObject(id, creator, privateChatPartner, otherPartyList, null);
    }

    public final void createUserObject_async(AMD_ObjectCache_createUserObject __cb, String username) throws FusionException, ObjectExistsException {
        this.createUserObject_async(__cb, username, null);
    }

    public final UserPrx createUserObjectNonAsync(String username) throws FusionException, ObjectExistsException {
        return this.createUserObjectNonAsync(username, null);
    }

    public final GroupChatPrx[] getAllGroupChats() throws FusionException {
        return this.getAllGroupChats(null);
    }

    public final MessageSwitchboardPrx getMessageSwitchboard() throws FusionException {
        return this.getMessageSwitchboard(null);
    }

    public final void purgeGroupChatObject(String id) {
        this.purgeGroupChatObject(id, null);
    }

    public final void purgeUserObject(String username) {
        this.purgeUserObject(username, null);
    }

    public final void sendAlertMessageToAllUsers(String message, String title, short timeout) throws FusionException {
        this.sendAlertMessageToAllUsers(message, title, timeout, null);
    }

    public static DispatchStatus ___createUserObject(ObjectCache __obj, Incoming __inS, Current __current) {
        _ObjectCacheDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String username = __is.readString();
        __is.endReadEncaps();
        _AMD_ObjectCache_createUserObject __cb = new _AMD_ObjectCache_createUserObject(__inS);
        try {
            __obj.createUserObject_async(__cb, username, __current);
        }
        catch (Exception ex) {
            __cb.ice_exception(ex);
        }
        return DispatchStatus.DispatchAsync;
    }

    public static DispatchStatus ___createUserObjectNonAsync(ObjectCache __obj, Incoming __inS, Current __current) {
        _ObjectCacheDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String username = __is.readString();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            UserPrx __ret = __obj.createUserObjectNonAsync(username, __current);
            UserPrxHelper.__write(__os, __ret);
            return DispatchStatus.DispatchOK;
        }
        catch (ObjectExistsException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___createChatRoomObject(ObjectCache __obj, Incoming __inS, Current __current) {
        _ObjectCacheDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String name = __is.readString();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            ChatRoomPrx __ret = __obj.createChatRoomObject(name, __current);
            ChatRoomPrxHelper.__write(__os, __ret);
            return DispatchStatus.DispatchOK;
        }
        catch (ObjectExistsException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___createGroupChatObject(ObjectCache __obj, Incoming __inS, Current __current) {
        _ObjectCacheDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id = __is.readString();
        String creator = __is.readString();
        String privateChatPartner = __is.readString();
        String[] otherPartyList = StringArrayHelper.read(__is);
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            GroupChatPrx __ret = __obj.createGroupChatObject(id, creator, privateChatPartner, otherPartyList, __current);
            GroupChatPrxHelper.__write(__os, __ret);
            return DispatchStatus.DispatchOK;
        }
        catch (ObjectExistsException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___sendAlertMessageToAllUsers(ObjectCache __obj, Incoming __inS, Current __current) {
        _ObjectCacheDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String message = __is.readString();
        String title = __is.readString();
        short timeout = __is.readShort();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            __obj.sendAlertMessageToAllUsers(message, title, timeout, __current);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___getAllGroupChats(ObjectCache __obj, Incoming __inS, Current __current) {
        _ObjectCacheDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        __inS.is().skipEmptyEncaps();
        BasicStream __os = __inS.os();
        try {
            GroupChatPrx[] __ret = __obj.getAllGroupChats(__current);
            GroupChatArrayHelper.write(__os, __ret);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___purgeUserObject(ObjectCache __obj, Incoming __inS, Current __current) {
        _ObjectCacheDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String username = __is.readString();
        __is.endReadEncaps();
        __obj.purgeUserObject(username, __current);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___purgeGroupChatObject(ObjectCache __obj, Incoming __inS, Current __current) {
        _ObjectCacheDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id = __is.readString();
        __is.endReadEncaps();
        __obj.purgeGroupChatObject(id, __current);
        return DispatchStatus.DispatchOK;
    }

    public static DispatchStatus ___getMessageSwitchboard(ObjectCache __obj, Incoming __inS, Current __current) {
        _ObjectCacheDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        __inS.is().skipEmptyEncaps();
        BasicStream __os = __inS.os();
        try {
            MessageSwitchboardPrx __ret = __obj.getMessageSwitchboard(__current);
            MessageSwitchboardPrxHelper.__write(__os, __ret);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public DispatchStatus __dispatch(Incoming in, Current __current) {
        int pos = Arrays.binarySearch(__all, __current.operation);
        if (pos < 0) {
            throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
        }
        switch (pos) {
            case 0: {
                return _ObjectCacheDisp.___createChatRoomObject(this, in, __current);
            }
            case 1: {
                return _ObjectCacheDisp.___createGroupChatObject(this, in, __current);
            }
            case 2: {
                return _ObjectCacheDisp.___createUserObject(this, in, __current);
            }
            case 3: {
                return _ObjectCacheDisp.___createUserObjectNonAsync(this, in, __current);
            }
            case 4: {
                return _ObjectCacheDisp.___getAllGroupChats(this, in, __current);
            }
            case 5: {
                return _ObjectCacheDisp.___getMessageSwitchboard(this, in, __current);
            }
            case 6: {
                return _ObjectCacheDisp.___ice_id((Ice.Object)this, (Incoming)in, (Current)__current);
            }
            case 7: {
                return _ObjectCacheDisp.___ice_ids((Ice.Object)this, (Incoming)in, (Current)__current);
            }
            case 8: {
                return _ObjectCacheDisp.___ice_isA((Ice.Object)this, (Incoming)in, (Current)__current);
            }
            case 9: {
                return _ObjectCacheDisp.___ice_ping((Ice.Object)this, (Incoming)in, (Current)__current);
            }
            case 10: {
                return _ObjectCacheDisp.___purgeGroupChatObject(this, in, __current);
            }
            case 11: {
                return _ObjectCacheDisp.___purgeUserObject(this, in, __current);
            }
            case 12: {
                return _ObjectCacheDisp.___sendAlertMessageToAllUsers(this, in, __current);
            }
        }
        assert (false);
        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
    }

    public void __write(BasicStream __os) {
        __os.writeTypeId(_ObjectCacheDisp.ice_staticId());
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
        ex.reason = "type com::projectgoth::fusion::slice::ObjectCache was not generated with stream support";
        throw ex;
    }

    public void __read(InputStream __inS, boolean __rid) {
        MarshalException ex = new MarshalException();
        ex.reason = "type com::projectgoth::fusion::slice::ObjectCache was not generated with stream support";
        throw ex;
    }
}

