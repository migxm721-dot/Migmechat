/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.app.dao;

import com.projectgoth.fusion.app.dao.DAOException;
import com.projectgoth.fusion.app.dao.DAOFactory;
import com.projectgoth.fusion.app.dao.base.MessageDAOChain;
import com.projectgoth.fusion.common.LazyLoader;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.AlertMessageData;
import com.projectgoth.fusion.fdl.enums.AlertContentType;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MessageDAO {
    private MessageDAOChain readChain;
    private MessageDAOChain writeChain;
    private static SecureRandom randomGen = new SecureRandom();
    private final LazyLoader<Map<Integer, String>> local_help_texts = new LazyLoader<Map<Integer, String>>("LOCAL_HELP_TEXTS", SystemProperty.getLong(SystemPropertyEntities.EJBCacheDuration.MIS_CLIENT_TEXT) * 1000L){

        @Override
        protected Map<Integer, String> fetchValue() throws DAOException {
            if (MessageDAO.this.readChain != null) {
                return MessageDAO.this.readChain.loadHelpTexts();
            }
            throw new DAOException("Unable to loadEmoticons");
        }
    };
    private final LazyLoader<Map<Integer, String>> local_info_texts = new LazyLoader<Map<Integer, String>>("LOCAL_INFO_TEXTS", SystemProperty.getLong(SystemPropertyEntities.EJBCacheDuration.MIS_CLIENT_TEXT) * 1000L){

        @Override
        protected Map<Integer, String> fetchValue() throws DAOException {
            if (MessageDAO.this.readChain != null) {
                return MessageDAO.this.readChain.loadInfoTexts();
            }
            throw new DAOException("Unable to loadEmoticons");
        }
    };

    public MessageDAO(MessageDAOChain readChain, MessageDAOChain writeChain) {
        this.readChain = readChain;
        this.writeChain = writeChain;
    }

    public Map<Integer, String> loadHelpTexts() throws DAOException {
        return this.local_help_texts.getValue();
    }

    public Map<Integer, String> loadInfoTexts() throws DAOException {
        return this.local_info_texts.getValue();
    }

    public String getInfoText(int infoID) throws DAOException {
        try {
            return this.loadInfoTexts().get(infoID);
        }
        catch (DAOException e) {
            return this.readChain.getInfoText(infoID);
        }
    }

    public List<AlertMessageData> getLatestAlertMessageList(int midletVersion, AlertMessageData.TypeEnum type, int countryId, Date minimumDate, AlertContentType alertContentType, int clientType) throws DAOException {
        return this.readChain.getLatestAlertMessageList(midletVersion, type, countryId, minimumDate, alertContentType, clientType);
    }

    public AlertMessageData getLatestAlertMessage(int midletVersion, AlertMessageData.TypeEnum type, int countryId, Date minimumDate, AlertContentType alertContentType, int clientType) throws DAOException {
        List<AlertMessageData> alertMessages = DAOFactory.getInstance().getMessageDAO().getLatestAlertMessageList(midletVersion, type, countryId, minimumDate, alertContentType, clientType);
        if (alertMessages.isEmpty()) {
            return null;
        }
        ArrayList<Double> accumWeightList = new ArrayList<Double>();
        double totalWeight = 0.0;
        for (AlertMessageData alertMessage : alertMessages) {
            boolean neverSeen;
            boolean expired = alertMessage.expiryDate.before(new Date());
            boolean bl = neverSeen = minimumDate == null || alertMessage.dateCreated.after(minimumDate);
            if (!(expired || alertMessage.onceOnly.booleanValue() && !neverSeen)) {
                totalWeight += alertMessage.weighting.doubleValue();
            }
            accumWeightList.add(totalWeight);
        }
        if (totalWeight > 0.0) {
            double pick = randomGen.nextDouble();
            for (int i = 0; i < accumWeightList.size(); ++i) {
                double accumWeight = (Double)accumWeightList.get(i);
                if (!(accumWeight > 0.0) || !(accumWeight / totalWeight >= pick)) continue;
                return alertMessages.get(i);
            }
        }
        return null;
    }
}

