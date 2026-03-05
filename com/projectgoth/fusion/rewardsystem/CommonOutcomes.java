/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonParseException
 *  com.fasterxml.jackson.databind.JsonMappingException
 *  com.projectgoth.leto.common.impl.outcome.MMv2Outcomes
 *  com.projectgoth.leto.common.outcome.OutcomeMarshaller
 *  com.projectgoth.leto.common.outcome.Outcomes
 *  javax.ejb.CreateException
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.projectgoth.fusion.rewardsystem;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Content;
import com.projectgoth.fusion.interfaces.ContentHome;
import com.projectgoth.fusion.rewardsystem.RewardDispatcher;
import com.projectgoth.fusion.rewardsystem.outcomes.UserRewardOutcome;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.leto.common.impl.outcome.MMv2Outcomes;
import com.projectgoth.leto.common.outcome.OutcomeMarshaller;
import com.projectgoth.leto.common.outcome.Outcomes;
import java.io.IOException;
import java.rmi.RemoteException;
import javax.ejb.CreateException;
import org.json.JSONException;
import org.json.JSONObject;

public class CommonOutcomes {
    public static Outcomes deserialize(String jsonData) throws InvalidJSONException {
        try {
            JSONObject rewardOutcomeJSON = new JSONObject(jsonData);
            int outcomeType = rewardOutcomeJSON.optInt("type", 1);
            switch (outcomeType) {
                case 1: {
                    return new UserRewardOutcome().fromJSONObject(rewardOutcomeJSON);
                }
                case 2: {
                    return OutcomeMarshaller.getInstance().unmarshalToOutcomes(jsonData);
                }
            }
            throw new InvalidJSONException("Unsupported outcome type:[" + outcomeType + "].");
        }
        catch (JsonParseException e) {
            throw new InvalidJSONException((Exception)((Object)e));
        }
        catch (JsonMappingException e) {
            throw new InvalidJSONException((Exception)((Object)e));
        }
        catch (JSONException e) {
            throw new InvalidJSONException((Exception)((Object)e));
        }
        catch (IOException e) {
            throw new InvalidJSONException(e);
        }
    }

    public static void processOutcomes(MMv2Outcomes outcomes, AccountEntrySourceData accountEntrySource) throws CreateException, RemoteException {
        Content contentEJB = (Content)EJBHomeCache.getObject("ejb/Content", ContentHome.class);
        contentEJB.giveRewards(outcomes, new AccountEntrySourceData(RewardDispatcher.class));
    }

    public static void processOutcomes(int programId, UserRewardOutcome outcomes, AccountEntrySourceData accountEntrySource) throws CreateException, RemoteException {
        Content contentEJB = (Content)EJBHomeCache.getObject("ejb/Content", ContentHome.class);
        contentEJB.giveRewards(programId, outcomes.getUserid(), accountEntrySource, outcomes.getOutcomeDataList(), outcomes.getTemplateData());
    }

    public static class InvalidJSONException
    extends FusionException {
        public InvalidJSONException(Exception ex) {
            super(ex.toString());
            this.initCause(ex);
        }

        public InvalidJSONException(String msg) {
            super(msg);
        }
    }
}

