/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.app.dao;

import com.projectgoth.fusion.app.dao.BotDAO;
import com.projectgoth.fusion.app.dao.CampaignDataDAO;
import com.projectgoth.fusion.app.dao.ChatRoomDAO;
import com.projectgoth.fusion.app.dao.EmailDAO;
import com.projectgoth.fusion.app.dao.EmoAndStickerDAO;
import com.projectgoth.fusion.app.dao.GroupDAO;
import com.projectgoth.fusion.app.dao.GuardsetDAO;
import com.projectgoth.fusion.app.dao.MessageDAO;
import com.projectgoth.fusion.app.dao.RecommendationDAO;
import com.projectgoth.fusion.app.dao.UserDataDAO;
import com.projectgoth.fusion.app.dao.base.DAOChain;
import com.projectgoth.fusion.app.dao.base.DAOChainFactory;
import com.projectgoth.fusion.app.dao.db.ejb.EJBDAOChainFactory;
import com.projectgoth.fusion.app.dao.db.elasticsearch.ElasticSearchDAOChainFactory;
import com.projectgoth.fusion.app.dao.db.fusiondb.FusionDbDAOChainFactory;
import com.projectgoth.fusion.app.dao.db.memcache.MemcacheDAOChainFactory;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DAOFactory {
    private static final Logger log = Logger.getLogger(DAOFactory.class);
    private static DAOFactory instance = new DAOFactory();

    public static DAOFactory getInstance() {
        return instance;
    }

    private DAOFactory() {
        log.info((Object)"Loading initial DAO Configuration");
        try {
            this.updateConfiguration();
        }
        catch (Exception e) {
            log.error((Object)String.format("Error occurred during DAO setup: %s. Fix dao.properties.", e.getMessage()), (Throwable)e);
        }
    }

    public EmailDAO getEmailDAO() {
        return (EmailDAO)DAOChainFactory.DAOChainType.EMAIL.dao;
    }

    public MessageDAO getMessageDAO() {
        return (MessageDAO)DAOChainFactory.DAOChainType.MESSAGE.dao;
    }

    public BotDAO getBotDAO() {
        return (BotDAO)DAOChainFactory.DAOChainType.BOT.dao;
    }

    public UserDataDAO getUserDataDAO() {
        return (UserDataDAO)DAOChainFactory.DAOChainType.USER_DATA.dao;
    }

    public GuardsetDAO getGuardsetDAO() {
        return (GuardsetDAO)DAOChainFactory.DAOChainType.GUARDSET.dao;
    }

    public CampaignDataDAO getCampaignDAO() {
        return (CampaignDataDAO)DAOChainFactory.DAOChainType.CAMPAIGN_DATA.dao;
    }

    public EmoAndStickerDAO getEmoAndStickerDAO() {
        return (EmoAndStickerDAO)DAOChainFactory.DAOChainType.EMO_AND_STICKER.dao;
    }

    public ChatRoomDAO getChatRoomDAO() {
        return (ChatRoomDAO)DAOChainFactory.DAOChainType.CHATROOM.dao;
    }

    public GroupDAO getGroupDAO() {
        return (GroupDAO)DAOChainFactory.DAOChainType.GROUP.dao;
    }

    public RecommendationDAO getRecommendationDAO() {
        return (RecommendationDAO)DAOChainFactory.DAOChainType.RECOMMENDATION.dao;
    }

    public void updateConfiguration() {
        HashMap<String, DAOChainFactory> factoryMap = new HashMap<String, DAOChainFactory>();
        factoryMap.put("fusiondb", new FusionDbDAOChainFactory());
        factoryMap.put("null", new DAOChainFactory());
        factoryMap.put("memcache", new MemcacheDAOChainFactory());
        factoryMap.put("ejb", new EJBDAOChainFactory());
        factoryMap.put("elasticsearch", new ElasticSearchDAOChainFactory());
        for (DAOChainFactory.DAOChainType type : DAOChainFactory.DAOChainType.values()) {
            type.dao = this.createDAOChains(factoryMap, type);
        }
    }

    private Object createDAOChains(Map<String, DAOChainFactory> factoryMap, DAOChainFactory.DAOChainType type) {
        DAOChain newNode;
        String db;
        int i;
        log.info((Object)("Creating DAO Chains for type=" + (Object)((Object)type)));
        List<Object> readConfiguration = new ArrayList(type.readConfiguration.get());
        List<Object> writeConfiguration = new ArrayList(type.writeConfiguration.get());
        if (readConfiguration == null || readConfiguration.size() == 0) {
            readConfiguration = Arrays.asList("null");
        }
        if (writeConfiguration == null || writeConfiguration.size() == 0) {
            writeConfiguration = Arrays.asList("null");
        }
        DAOChain readChain = null;
        DAOChain writeChain = null;
        log.info((Object)String.format("Setting up DAO chain %s, Read: %s, Write: %s", new Object[]{type, readConfiguration, writeConfiguration}));
        ArrayList<DAOChain> readDaoArray = new ArrayList<DAOChain>(readConfiguration.size());
        for (i = 0; i < readConfiguration.size(); ++i) {
            readDaoArray.add(null);
        }
        for (i = readConfiguration.size() - 1; i >= 0; --i) {
            db = (String)readConfiguration.get(i);
            log.info((Object)String.format("Creating DAO Chain for type:%s, db:%s", new Object[]{type, db}));
            if (db.startsWith("!")) {
                readConfiguration.set(i, null);
                continue;
            }
            newNode = factoryMap.get(db).createDAOChain(type);
            if (newNode != null) {
                newNode.setNextRead(readChain);
                readDaoArray.set(i, newNode);
                readChain = newNode;
                continue;
            }
            log.error((Object)String.format("DAOChainFactory failed to create a ReadChain for: %s. Please update DAOChainFactory:createDAOChain()", new Object[]{type}));
        }
        for (i = writeConfiguration.size() - 1; i >= 0; --i) {
            db = (String)writeConfiguration.get(i);
            if (db.startsWith("!")) continue;
            newNode = null;
            for (int j = readConfiguration.size() - 1; j >= 0 && newNode == null; --j) {
                if (!db.equals(readConfiguration.get(j))) continue;
                newNode = (DAOChain)readDaoArray.get(j);
                readConfiguration.set(j, null);
                break;
            }
            if (newNode == null) {
                newNode = factoryMap.get(db).createDAOChain(type);
            }
            newNode.setNextWrite(writeChain);
            writeChain = newNode;
        }
        assert (readChain != null) : String.format("ReadChain for %s is null", new Object[]{type});
        assert (writeChain != null) : String.format("WriteChain for %s is null", new Object[]{type});
        try {
            Constructor<?>[] constructors = type.daoClass.getDeclaredConstructors();
            assert (constructors.length == 1 && constructors[0].getParameterTypes().length == 2);
            return constructors[0].newInstance(readChain, writeChain);
        }
        catch (Exception e) {
            log.error((Object)String.format("Unable to construct dao type: %s", new Object[]{type}), (Throwable)e);
            return null;
        }
    }
}

