/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.app.dao.base;

import com.projectgoth.fusion.app.dao.DAOException;
import com.projectgoth.fusion.app.dao.base.DAOChain;
import com.projectgoth.fusion.data.CampaignData;
import com.projectgoth.fusion.data.CampaignParticipantData;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class CampaignDataDAOChain
implements DAOChain {
    private CampaignDataDAOChain nextRead;
    private CampaignDataDAOChain nextWrite;

    @Override
    public void setNextRead(DAOChain nextRead) {
        this.nextRead = (CampaignDataDAOChain)nextRead;
    }

    @Override
    public void setNextWrite(DAOChain nextWrite) {
        this.nextWrite = (CampaignDataDAOChain)nextWrite;
    }

    public CampaignData getCampaignData(int campaignid) throws DAOException {
        if (this.nextRead != null) {
            return this.nextRead.getCampaignData(campaignid);
        }
        throw new DAOException(String.format("Unable to retrieve campaign data for campaign id:%s", campaignid));
    }

    public CampaignParticipantData getCampaignParticipantData(int userid, int campaignid) throws DAOException {
        if (this.nextRead != null) {
            return this.nextRead.getCampaignParticipantData(userid, campaignid);
        }
        throw new DAOException(String.format("Unable to retrieve campaign data for campaign id:%s userid:%s", campaignid, userid));
    }

    public List<CampaignParticipantData> getActiveCampaignParticipantDataByType(int userid) throws DAOException {
        if (this.nextRead != null) {
            return this.nextRead.getActiveCampaignParticipantDataByType(userid);
        }
        throw new DAOException(String.format("Unable to retrieve campaign data for userid:%s", userid));
    }

    public List<CampaignParticipantData> getActiveCampaignParticipantDataByType(int userid, int type) throws DAOException {
        if (this.nextRead != null) {
            return this.nextRead.getActiveCampaignParticipantDataByType(userid, type);
        }
        throw new DAOException(String.format("Unable to retrieve campaign data for type id:%s userid:%s", type, userid));
    }

    public CampaignParticipantData joinCampaign(CampaignParticipantData campaignUserData) throws DAOException {
        if (this.nextWrite != null) {
            return this.nextWrite.joinCampaign(campaignUserData);
        }
        throw new DAOException(String.format("Unable to update campaign data for campaign id:%s userid:%s", campaignUserData.getCampaignId(), campaignUserData.getUserId()));
    }

    public CampaignParticipantData getCampaignParticipantDataByMobilePhone(String mobilePhone, int campaignId) throws DAOException {
        if (this.nextRead != null) {
            return this.nextRead.getCampaignParticipantDataByMobilePhone(mobilePhone, campaignId);
        }
        throw new DAOException(String.format("Unable to update campaign data for campaign id:%s mobile:%s", campaignId, mobilePhone));
    }
}

