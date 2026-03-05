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
import com.projectgoth.fusion.slice.ByteArrayHelper;
import com.projectgoth.fusion.slice.ChatListIce;
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.ChatRoomPrxHelper;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GroupChatPrx;
import com.projectgoth.fusion.slice.GroupChatPrxHelper;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.MessageSwitchboardPrx;
import com.projectgoth.fusion.slice.MessageSwitchboardPrxHelper;
import com.projectgoth.fusion.slice.SessionMetricsIce;
import com.projectgoth.fusion.slice.StringArrayHelper;
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.slice.UserPrxHelper;
import com.projectgoth.fusion.slice._SessionDel;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class _SessionDelM
extends _ObjectDelM
implements _SessionDel {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void chatroomJoined(ChatRoomPrx roomProxy, String name, Map<String, String> __ctx) throws LocalExceptionWrapper {
        block10: {
            Outgoing __og = this.__handler.getOutgoing("chatroomJoined", OperationMode.Normal, __ctx);
            try {
                try {
                    BasicStream __os = __og.os();
                    ChatRoomPrxHelper.__write(__os, roomProxy);
                    __os.writeString(name);
                }
                catch (LocalException __ex) {
                    __og.abort(__ex);
                }
                boolean __ok = __og.invoke();
                if (__og.is().isEmpty()) break block10;
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
            finally {
                this.__handler.reclaimOutgoing(__og);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void endSession(Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("endSession", OperationMode.Normal, __ctx);
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
        }
        finally {
            this.__handler.reclaimOutgoing(__og);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void endSessionOneWay(Map<String, String> __ctx) throws LocalExceptionWrapper {
        block8: {
            Outgoing __og = this.__handler.getOutgoing("endSessionOneWay", OperationMode.Normal, __ctx);
            try {
                boolean __ok = __og.invoke();
                if (__og.is().isEmpty()) break block8;
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
            finally {
                this.__handler.reclaimOutgoing(__og);
            }
        }
    }

    @Override
    public GroupChatPrx findGroupChatObject(String groupChatID, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("findGroupChatObject", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(groupChatID);
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
                GroupChatPrx __ret = GroupChatPrxHelper.__read(__is);
                __is.endReadEncaps();
                GroupChatPrx groupChatPrx = __ret;
                return groupChatPrx;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
        }
        finally {
            this.__handler.reclaimOutgoing(__og);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void friendInvitedByPhoneNumber(Map<String, String> __ctx) throws LocalExceptionWrapper {
        block8: {
            Outgoing __og = this.__handler.getOutgoing("friendInvitedByPhoneNumber", OperationMode.Normal, __ctx);
            try {
                boolean __ok = __og.invoke();
                if (__og.is().isEmpty()) break block8;
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
            finally {
                this.__handler.reclaimOutgoing(__og);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void friendInvitedByUsername(Map<String, String> __ctx) throws LocalExceptionWrapper {
        block8: {
            Outgoing __og = this.__handler.getOutgoing("friendInvitedByUsername", OperationMode.Normal, __ctx);
            try {
                boolean __ok = __og.invoke();
                if (__og.is().isEmpty()) break block8;
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
            finally {
                this.__handler.reclaimOutgoing(__og);
            }
        }
    }

    @Override
    public int getChatListVersion(Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("getChatListVersion", OperationMode.Normal, __ctx);
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
                BasicStream __is = __og.is();
                __is.startReadEncaps();
                int __ret = __is.readInt();
                __is.endReadEncaps();
                int n = __ret;
                return n;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
        }
        finally {
            this.__handler.reclaimOutgoing(__og);
        }
    }

    @Override
    public short getClientVersionIce(Map<String, String> __ctx) throws LocalExceptionWrapper {
        Outgoing __og = this.__handler.getOutgoing("getClientVersionIce", OperationMode.Normal, __ctx);
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
                short __ret = __is.readShort();
                __is.endReadEncaps();
                short s = __ret;
                return s;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
        }
        finally {
            this.__handler.reclaimOutgoing(__og);
        }
    }

    @Override
    public int getDeviceTypeAsInt(Map<String, String> __ctx) throws LocalExceptionWrapper {
        Outgoing __og = this.__handler.getOutgoing("getDeviceTypeAsInt", OperationMode.Normal, __ctx);
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
                int n = __ret;
                return n;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
        }
        finally {
            this.__handler.reclaimOutgoing(__og);
        }
    }

    @Override
    public MessageSwitchboardPrx getMessageSwitchboard(Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("getMessageSwitchboard", OperationMode.Normal, __ctx);
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
                BasicStream __is = __og.is();
                __is.startReadEncaps();
                MessageSwitchboardPrx __ret = MessageSwitchboardPrxHelper.__read(__is);
                __is.endReadEncaps();
                MessageSwitchboardPrx messageSwitchboardPrx = __ret;
                return messageSwitchboardPrx;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
        }
        finally {
            this.__handler.reclaimOutgoing(__og);
        }
    }

    @Override
    public String getMobileDeviceIce(Map<String, String> __ctx) throws LocalExceptionWrapper {
        Outgoing __og = this.__handler.getOutgoing("getMobileDeviceIce", OperationMode.Normal, __ctx);
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
                String string = __ret;
                return string;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
        }
        finally {
            this.__handler.reclaimOutgoing(__og);
        }
    }

    @Override
    public String getParentUsername(Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("getParentUsername", OperationMode.Normal, __ctx);
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
                BasicStream __is = __og.is();
                __is.startReadEncaps();
                String __ret = __is.readString();
                __is.endReadEncaps();
                String string = __ret;
                return string;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
        }
        finally {
            this.__handler.reclaimOutgoing(__og);
        }
    }

    @Override
    public String getRemoteIPAddress(Map<String, String> __ctx) throws LocalExceptionWrapper {
        Outgoing __og = this.__handler.getOutgoing("getRemoteIPAddress", OperationMode.Normal, __ctx);
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
                String string = __ret;
                return string;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
        }
        finally {
            this.__handler.reclaimOutgoing(__og);
        }
    }

    @Override
    public String getSessionID(Map<String, String> __ctx) throws LocalExceptionWrapper {
        Outgoing __og = this.__handler.getOutgoing("getSessionID", OperationMode.Normal, __ctx);
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
                String string = __ret;
                return string;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
        }
        finally {
            this.__handler.reclaimOutgoing(__og);
        }
    }

    @Override
    public SessionMetricsIce getSessionMetrics(Map<String, String> __ctx) throws LocalExceptionWrapper {
        Outgoing __og = this.__handler.getOutgoing("getSessionMetrics", OperationMode.Normal, __ctx);
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
                SessionMetricsIce __ret = new SessionMetricsIce();
                __ret.__read(__is);
                __is.endReadEncaps();
                SessionMetricsIce sessionMetricsIce = __ret;
                return sessionMetricsIce;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
        }
        finally {
            this.__handler.reclaimOutgoing(__og);
        }
    }

    @Override
    public String getUserAgentIce(Map<String, String> __ctx) throws LocalExceptionWrapper {
        Outgoing __og = this.__handler.getOutgoing("getUserAgentIce", OperationMode.Normal, __ctx);
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
                String string = __ret;
                return string;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
        }
        finally {
            this.__handler.reclaimOutgoing(__og);
        }
    }

    @Override
    public UserPrx getUserProxy(String username, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("getUserProxy", OperationMode.Normal, __ctx);
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
                UserPrx __ret = UserPrxHelper.__read(__is);
                __is.endReadEncaps();
                UserPrx userPrx = __ret;
                return userPrx;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
        }
        finally {
            this.__handler.reclaimOutgoing(__og);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void groupChatJoined(String id, Map<String, String> __ctx) throws LocalExceptionWrapper {
        block10: {
            Outgoing __og = this.__handler.getOutgoing("groupChatJoined", OperationMode.Normal, __ctx);
            try {
                try {
                    BasicStream __os = __og.os();
                    __os.writeString(id);
                }
                catch (LocalException __ex) {
                    __og.abort(__ex);
                }
                boolean __ok = __og.invoke();
                if (__og.is().isEmpty()) break block10;
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
            finally {
                this.__handler.reclaimOutgoing(__og);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void groupChatJoinedMultiple(String id, int increment, Map<String, String> __ctx) throws LocalExceptionWrapper {
        block10: {
            Outgoing __og = this.__handler.getOutgoing("groupChatJoinedMultiple", OperationMode.Normal, __ctx);
            try {
                try {
                    BasicStream __os = __og.os();
                    __os.writeString(id);
                    __os.writeInt(increment);
                }
                catch (LocalException __ex) {
                    __og.abort(__ex);
                }
                boolean __ok = __og.invoke();
                if (__og.is().isEmpty()) break block10;
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
            finally {
                this.__handler.reclaimOutgoing(__og);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void notifyUserJoinedChatRoomOneWay(String chatroomname, String username, boolean isAdministrator, boolean isMuted, Map<String, String> __ctx) throws LocalExceptionWrapper {
        block10: {
            Outgoing __og = this.__handler.getOutgoing("notifyUserJoinedChatRoomOneWay", OperationMode.Normal, __ctx);
            try {
                try {
                    BasicStream __os = __og.os();
                    __os.writeString(chatroomname);
                    __os.writeString(username);
                    __os.writeBool(isAdministrator);
                    __os.writeBool(isMuted);
                }
                catch (LocalException __ex) {
                    __og.abort(__ex);
                }
                boolean __ok = __og.invoke();
                if (__og.is().isEmpty()) break block10;
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
            finally {
                this.__handler.reclaimOutgoing(__og);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void notifyUserJoinedGroupChat(String groupChatId, String username, boolean isMuted, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("notifyUserJoinedGroupChat", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(groupChatId);
                __os.writeString(username);
                __os.writeBool(isMuted);
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
        }
        finally {
            this.__handler.reclaimOutgoing(__og);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void notifyUserLeftChatRoomOneWay(String chatroomname, String username, Map<String, String> __ctx) throws LocalExceptionWrapper {
        block10: {
            Outgoing __og = this.__handler.getOutgoing("notifyUserLeftChatRoomOneWay", OperationMode.Normal, __ctx);
            try {
                try {
                    BasicStream __os = __og.os();
                    __os.writeString(chatroomname);
                    __os.writeString(username);
                }
                catch (LocalException __ex) {
                    __og.abort(__ex);
                }
                boolean __ok = __og.invoke();
                if (__og.is().isEmpty()) break block10;
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
            finally {
                this.__handler.reclaimOutgoing(__og);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void notifyUserLeftGroupChat(String groupChatId, String username, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("notifyUserLeftGroupChat", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(groupChatId);
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
        }
        finally {
            this.__handler.reclaimOutgoing(__og);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void photoUploaded(Map<String, String> __ctx) throws LocalExceptionWrapper {
        block8: {
            Outgoing __og = this.__handler.getOutgoing("photoUploaded", OperationMode.Normal, __ctx);
            try {
                boolean __ok = __og.invoke();
                if (__og.is().isEmpty()) break block8;
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
            finally {
                this.__handler.reclaimOutgoing(__og);
            }
        }
    }

    @Override
    public boolean privateChattedWith(String username, Map<String, String> __ctx) throws LocalExceptionWrapper {
        Outgoing __og = this.__handler.getOutgoing("privateChattedWith", OperationMode.Normal, __ctx);
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
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                BasicStream __is = __og.is();
                __is.startReadEncaps();
                boolean __ret = __is.readBool();
                __is.endReadEncaps();
                boolean bl = __ret;
                return bl;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
        }
        finally {
            this.__handler.reclaimOutgoing(__og);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void profileEdited(Map<String, String> __ctx) throws LocalExceptionWrapper {
        block8: {
            Outgoing __og = this.__handler.getOutgoing("profileEdited", OperationMode.Normal, __ctx);
            try {
                boolean __ok = __og.invoke();
                if (__og.is().isEmpty()) break block8;
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
            finally {
                this.__handler.reclaimOutgoing(__og);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void putAlertMessage(String message, String title, short timeout, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("putAlertMessage", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(message);
                __os.writeString(title);
                __os.writeShort(timeout);
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
        }
        finally {
            this.__handler.reclaimOutgoing(__og);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void putAlertMessageOneWay(String message, String title, short timeout, Map<String, String> __ctx) throws LocalExceptionWrapper {
        block10: {
            Outgoing __og = this.__handler.getOutgoing("putAlertMessageOneWay", OperationMode.Normal, __ctx);
            try {
                try {
                    BasicStream __os = __og.os();
                    __os.writeString(message);
                    __os.writeString(title);
                    __os.writeShort(timeout);
                }
                catch (LocalException __ex) {
                    __og.abort(__ex);
                }
                boolean __ok = __og.invoke();
                if (__og.is().isEmpty()) break block10;
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
            finally {
                this.__handler.reclaimOutgoing(__og);
            }
        }
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
        }
        finally {
            this.__handler.reclaimOutgoing(__og);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void putMessageOneWay(MessageDataIce message, Map<String, String> __ctx) throws LocalExceptionWrapper {
        block10: {
            Outgoing __og = this.__handler.getOutgoing("putMessageOneWay", OperationMode.Normal, __ctx);
            try {
                try {
                    BasicStream __os = __og.os();
                    message.__write(__os);
                }
                catch (LocalException __ex) {
                    __og.abort(__ex);
                }
                boolean __ok = __og.invoke();
                if (__og.is().isEmpty()) break block10;
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
            finally {
                this.__handler.reclaimOutgoing(__og);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void putSerializedPacket(byte[] packet, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("putSerializedPacket", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                ByteArrayHelper.write(__os, packet);
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
        }
        finally {
            this.__handler.reclaimOutgoing(__og);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void putSerializedPacketOneWay(byte[] packet, Map<String, String> __ctx) throws LocalExceptionWrapper {
        block10: {
            Outgoing __og = this.__handler.getOutgoing("putSerializedPacketOneWay", OperationMode.Normal, __ctx);
            try {
                try {
                    BasicStream __os = __og.os();
                    ByteArrayHelper.write(__os, packet);
                }
                catch (LocalException __ex) {
                    __og.abort(__ex);
                }
                boolean __ok = __og.invoke();
                if (__og.is().isEmpty()) break block10;
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
            finally {
                this.__handler.reclaimOutgoing(__og);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void sendGroupChatParticipantArrays(String groupChatId, byte imType, String[] participants, String[] mutedParticipants, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("sendGroupChatParticipantArrays", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(groupChatId);
                __os.writeByte(imType);
                StringArrayHelper.write(__os, participants);
                StringArrayHelper.write(__os, mutedParticipants);
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
        }
        finally {
            this.__handler.reclaimOutgoing(__og);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void sendGroupChatParticipants(String groupChatId, byte imType, String participants, String mutedParticipants, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("sendGroupChatParticipants", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(groupChatId);
                __os.writeByte(imType);
                __os.writeString(participants);
                __os.writeString(mutedParticipants);
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
        }
        finally {
            this.__handler.reclaimOutgoing(__og);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void sendMessage(MessageDataIce message, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("sendMessage", OperationMode.Normal, __ctx);
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
        }
        finally {
            this.__handler.reclaimOutgoing(__og);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void sendMessageBackToUserAsEmote(MessageDataIce message, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("sendMessageBackToUserAsEmote", OperationMode.Normal, __ctx);
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
        }
        finally {
            this.__handler.reclaimOutgoing(__og);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setChatListVersion(int version, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("setChatListVersion", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeInt(version);
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
        }
        finally {
            this.__handler.reclaimOutgoing(__og);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setCurrentChatListGroupChatSubset(ChatListIce ccl, Map<String, String> __ctx) throws LocalExceptionWrapper {
        block10: {
            Outgoing __og = this.__handler.getOutgoing("setCurrentChatListGroupChatSubset", OperationMode.Normal, __ctx);
            try {
                try {
                    BasicStream __os = __og.os();
                    ccl.__write(__os);
                }
                catch (LocalException __ex) {
                    __og.abort(__ex);
                }
                boolean __ok = __og.invoke();
                if (__og.is().isEmpty()) break block10;
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
            finally {
                this.__handler.reclaimOutgoing(__og);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setLanguage(String language, Map<String, String> __ctx) throws LocalExceptionWrapper {
        block10: {
            Outgoing __og = this.__handler.getOutgoing("setLanguage", OperationMode.Normal, __ctx);
            try {
                try {
                    BasicStream __os = __og.os();
                    __os.writeString(language);
                }
                catch (LocalException __ex) {
                    __og.abort(__ex);
                }
                boolean __ok = __og.invoke();
                if (__og.is().isEmpty()) break block10;
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
            finally {
                this.__handler.reclaimOutgoing(__og);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setPresence(int presence, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("setPresence", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeInt(presence);
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
        }
        finally {
            this.__handler.reclaimOutgoing(__og);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void silentlyDropIncomingPackets(Map<String, String> __ctx) throws LocalExceptionWrapper {
        block8: {
            Outgoing __og = this.__handler.getOutgoing("silentlyDropIncomingPackets", OperationMode.Normal, __ctx);
            try {
                boolean __ok = __og.invoke();
                if (__og.is().isEmpty()) break block8;
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
            finally {
                this.__handler.reclaimOutgoing(__og);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void statusMessageSet(Map<String, String> __ctx) throws LocalExceptionWrapper {
        block8: {
            Outgoing __og = this.__handler.getOutgoing("statusMessageSet", OperationMode.Normal, __ctx);
            try {
                boolean __ok = __og.invoke();
                if (__og.is().isEmpty()) break block8;
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
            finally {
                this.__handler.reclaimOutgoing(__og);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void themeUpdated(Map<String, String> __ctx) throws LocalExceptionWrapper {
        block8: {
            Outgoing __og = this.__handler.getOutgoing("themeUpdated", OperationMode.Normal, __ctx);
            try {
                boolean __ok = __og.invoke();
                if (__og.is().isEmpty()) break block8;
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
            finally {
                this.__handler.reclaimOutgoing(__og);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void touch(Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("touch", OperationMode.Normal, __ctx);
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
        }
        finally {
            this.__handler.reclaimOutgoing(__og);
        }
    }
}

