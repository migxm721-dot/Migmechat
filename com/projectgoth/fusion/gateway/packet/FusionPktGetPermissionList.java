/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 */
package com.projectgoth.fusion.gateway.packet;

import com.projectgoth.fusion.common.RMIExceptionHelper;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktPermissionList;
import com.projectgoth.fusion.gateway.packet.FusionRequest;
import com.projectgoth.fusion.packet.FusionPacket;
import com.projectgoth.fusion.slice.UserPrx;
import java.rmi.RemoteException;
import javax.ejb.CreateException;

public class FusionPktGetPermissionList
extends FusionRequest {
    public FusionPktGetPermissionList() {
        super((short)415);
    }

    public FusionPktGetPermissionList(short transactionId) {
        super((short)415, transactionId);
    }

    public FusionPktGetPermissionList(FusionPacket packet) {
        super(packet);
    }

    public Byte getListType() {
        return this.getByteField((short)1);
    }

    public void setListType(byte listType) {
        this.setField((short)1, listType);
    }

    private String concatStrings(String[] l, String separator) {
        if (l == null || l.length == 0) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (String s : l) {
            builder.append(s).append(separator);
        }
        return builder.toString();
    }

    public boolean sessionRequired() {
        return true;
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        try {
            Byte byteVal = this.getListType();
            if (byteVal == null) {
                throw new Exception("You must specify a list type");
            }
            FusionPktPermissionList permissionPkt = new FusionPktPermissionList(this.transactionId);
            int listType = byteVal.intValue();
            if (listType == 2 || listType == 3) {
                UserPrx userPrx = connection.getUserPrx();
                if (userPrx == null) {
                    throw new Exception("You are no longer logged in");
                }
                String blockList = this.concatStrings(userPrx.getBlockList(), ";");
                if (blockList != null) {
                    permissionPkt.setBlockList(blockList);
                }
            }
            return new FusionPacket[]{permissionPkt};
        }
        catch (CreateException e) {
            FusionPktError pktError = new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to get permission list - Failed to create UserEJB");
            return new FusionPacket[]{pktError};
        }
        catch (RemoteException e) {
            FusionPktError pktError = new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to get permission list - " + RMIExceptionHelper.getRootMessage(e));
            return new FusionPacket[]{pktError};
        }
        catch (Exception e) {
            FusionPktError pktError = new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to get permission list - " + e.getMessage());
            return new FusionPacket[]{pktError};
        }
    }
}

