/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBException
 *  org.apache.log4j.Logger
 *  org.quartz.Job
 *  org.quartz.JobExecutionContext
 *  org.quartz.JobExecutionException
 *  redis.clients.jedis.Jedis
 */
package com.projectgoth.fusion.jobscheduling.job;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.Redis;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Message;
import com.projectgoth.fusion.interfaces.MessageHome;
import com.projectgoth.fusion.paintwars.ItemData;
import com.projectgoth.fusion.paintwars.Painter;
import com.projectgoth.fusion.slice.FusionException;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import redis.clients.jedis.Jedis;

public class PaintWarsWeeklyRaffles
implements Job {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(PaintWarsWeeklyRaffles.class));
    private static final int NUM_OF_WINNERS = 5;
    private static final String RAFFLE_CANDIDATES_KEY = "PaintWars:RaffleCandidates";
    private static final String CURRENT_WEEK_WINNERS_KEY = "PaintWars:CurrentWeekWinners";
    private static final String PREVIOUS_WEEK_WINNERS_KEY = "PaintWars:PreviousWeekWinners";

    public void execute(JobExecutionContext context) throws JobExecutionException {
        String[] winners = this.pickWinners();
        String message = "List of Paint Wars weekly raffle winners: \n";
        for (int i = 0; i < winners.length; ++i) {
            this.giveItem(winners[i]);
            message = message + winners[i] + "\n";
        }
        log.info((Object)message);
        this.updateWinnerStats(winners);
    }

    private String[] pickWinners() {
        String[] raffleCandidates = this.getRaffleCandidates();
        HashSet<String> winners = new HashSet<String>();
        if (raffleCandidates.length <= 5) {
            for (int i = 0; i < raffleCandidates.length; ++i) {
                winners.add(raffleCandidates[i]);
            }
        } else {
            Random randomizer = new Random();
            while (winners.size() < 5) {
                int winningIndex = randomizer.nextInt(raffleCandidates.length);
                String winner = raffleCandidates[winningIndex];
                winners.add(winner);
            }
        }
        return winners.toArray(new String[0]);
    }

    private String[] getRaffleCandidates() {
        Jedis slaveInstance = null;
        try {
            slaveInstance = Redis.getGamesSlaveInstance();
        }
        catch (Exception e) {
            log.warn((Object)"Unable to load game configuration");
        }
        if (slaveInstance == null) {
            log.warn((Object)"Unable to get games slave instance");
            return new String[0];
        }
        Set raffleCandidates = slaveInstance.zrange(RAFFLE_CANDIDATES_KEY, 0L, -1L);
        Redis.disconnect(slaveInstance, log);
        return raffleCandidates.toArray(new String[0]);
    }

    private void giveItem(String username) {
        try {
            if (!Painter.isInventoryFull(username)) {
                int itemId = this.pickRandomItem();
                Painter.addItemToInventory(username, itemId);
                log.info((Object)("Giving item [" + itemId + "] to [" + username + "]"));
                this.sendEmailNotification(username, itemId);
            } else {
                log.info((Object)("Unable to give away Paint Wars prize to: [" + username + "] because the inventory was full"));
            }
        }
        catch (FusionException e) {
            log.warn((Object)("Unable to give away Paint Wars prize to: " + username));
            log.warn((Object)e.getMessage());
        }
    }

    private int pickRandomItem() throws FusionException {
        Vector<ItemData> specialItems = Painter.getSpecialItems();
        Random generator = new Random();
        int randomIndex = generator.nextInt(specialItems.size() - 1);
        ItemData item = specialItems.get(randomIndex);
        int itemId = item.getId();
        return itemId;
    }

    private void sendEmailNotification(String username, int itemId) {
        try {
            String itemName = Painter.getSpecialItemName(itemId);
            if (itemName == null) {
                log.warn((Object)"Unable to find special item name");
                return;
            }
            Message messageBean = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
            String destination = messageBean.getUserEmailAddress(username);
            String subject = "Paint Wars Weekly Raffles";
            String content = "Congratulations! You are a Winner of the Paint Wars Weekly Raffles! You won " + itemName + ". Go to your Game Page to check it out now!";
            messageBean.sendEmailFromNoReply(destination, subject, content);
        }
        catch (FusionException e) {
            log.warn((Object)e.getMessage());
        }
        catch (CreateException e) {
            log.warn((Object)e.getMessage());
        }
        catch (EJBException e) {
            log.warn((Object)e.getMessage());
        }
        catch (RemoteException e) {
            log.warn((Object)e.getMessage());
        }
    }

    private void updateWinnerStats(String[] winners) {
        Jedis masterInstance = null;
        try {
            masterInstance = Redis.getGamesMasterInstance();
        }
        catch (Exception e) {
            log.warn((Object)"Unable to load game configuration");
        }
        if (masterInstance != null) {
            masterInstance.del(RAFFLE_CANDIDATES_KEY);
            String currentTime = Long.toString(Calendar.getInstance().getTimeInMillis() / 1000L);
            masterInstance.hset("PaintWars", "LastRaffleTime", currentTime);
            masterInstance.del(PREVIOUS_WEEK_WINNERS_KEY);
            masterInstance.sunionstore(PREVIOUS_WEEK_WINNERS_KEY, new String[]{CURRENT_WEEK_WINNERS_KEY});
            masterInstance.del(CURRENT_WEEK_WINNERS_KEY);
            for (int i = 0; i < winners.length; ++i) {
                masterInstance.sadd(CURRENT_WEEK_WINNERS_KEY, new String[]{winners[i]});
            }
            Redis.disconnect(masterInstance, log);
        } else {
            log.error((Object)"Unable to clear the raffle candidates");
        }
    }
}

