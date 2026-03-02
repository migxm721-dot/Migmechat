/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.gateway.packet;

import com.projectgoth.fusion.common.ByteBufferHelper;
import com.projectgoth.fusion.data.MenuData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktDynamicMenuIcon;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionRequest;
import com.projectgoth.fusion.interfaces.MIS;
import com.projectgoth.fusion.interfaces.MISHome;
import com.projectgoth.fusion.packet.FusionPacket;
import java.io.File;
import java.util.LinkedList;

public class FusionPktGetDynamicMenuIcon
extends FusionRequest {
    public FusionPktGetDynamicMenuIcon() {
        super((short)929);
    }

    public FusionPktGetDynamicMenuIcon(short transactionId) {
        super((short)929, transactionId);
    }

    public FusionPktGetDynamicMenuIcon(FusionPacket packet) {
        super(packet);
    }

    public int getIconId() {
        return this.getIntField((short)1);
    }

    public void setIconId(int iconId) {
        this.setField((short)1, iconId);
    }

    public boolean sessionRequired() {
        return true;
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        try {
            int iconId = this.getIconId();
            MIS misEJB = (MIS)EJBHomeCache.getObject("ejb/Content", MISHome.class);
            MenuData menuData = misEJB.getMenu(iconId);
            LinkedList<FusionPktDynamicMenuIcon> packetsToReturn = new LinkedList<FusionPktDynamicMenuIcon>();
            FusionPktDynamicMenuIcon iconsPkt = new FusionPktDynamicMenuIcon(this.transactionId);
            iconsPkt.setIconId(iconId);
            iconsPkt.setIcon(ByteBufferHelper.readFile(new File(menuData.location)).array());
            packetsToReturn.add(iconsPkt);
            return packetsToReturn.toArray(new FusionPacket[packetsToReturn.size()]);
        }
        catch (Exception e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Unable to get dynamic menu icon - " + e.getMessage()).toArray();
        }
    }
}

