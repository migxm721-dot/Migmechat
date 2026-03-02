/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.projectgoth.leto.common.impl.outcome.MMv2Outcomes
 *  com.projectgoth.leto.common.outcome.Outcomes
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.ext.Provider
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.restapi.resource;

import com.projectgoth.fusion.common.RedisQueue;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.common.log4j.Log4JUtils;
import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.restapi.data.FusionRestException;
import com.projectgoth.fusion.rewardsystem.CommonOutcomes;
import com.projectgoth.fusion.rewardsystem.RewardCentre;
import com.projectgoth.fusion.rewardsystem.outcomes.UserRewardOutcome;
import com.projectgoth.leto.common.impl.outcome.MMv2Outcomes;
import com.projectgoth.leto.common.outcome.Outcomes;
import java.util.UUID;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.ext.Provider;
import org.apache.log4j.Logger;

@Provider
@Path(value="/reward")
public class RewardResource {
    private static final Logger log = Log4JUtils.getLogger(RewardResource.class);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @POST
    @Path(value="/outcomes/{programId}")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public void dispatch(@PathParam(value="programId") int programId, @QueryParam(value="useAsync") boolean useAsync, String outcomeJsonString) throws FusionRestException {
        block17: {
            try {
                RewardProgramData rewardProgram;
                if (!SystemProperty.getBool(SystemPropertyEntities.RewardDispatcherSettings.ENABLE_REST_INTERFACE)) {
                    throw new FusionRestException(FusionRestException.RestException.DISABLED_FEATURE);
                }
                if (SystemProperty.getBool(SystemPropertyEntities.RewardDispatcherSettings.ENABLE_LOGGING_RECEIVED_OUTCOMES_FROM_REST_INTERFACE)) {
                    log.info((Object)("dispatch programId:[" + programId + "] useAsync:[" + useAsync + "] data:[" + outcomeJsonString + "]"));
                }
                if ((rewardProgram = RewardCentre.getInstance().getRewardProgram(programId)) == null) {
                    throw new FusionRestException(FusionRestException.RestException.UNKNOWN_REWARD_PROGRAM_ID);
                }
                Outcomes outcomes = CommonOutcomes.deserialize(outcomeJsonString);
                if (useAsync) {
                    RedisQueue redisQueue = RedisQueue.getInstance();
                    try {
                        RewardCentre.getInstance().dispatchRewardData(redisQueue, programId, outcomeJsonString);
                        Object var8_13 = null;
                    }
                    catch (Throwable throwable) {
                        Object var8_14 = null;
                        try {
                            redisQueue.disconnect();
                        }
                        catch (Exception ex) {
                            log.warn((Object)("Failed to disconnect redisQueue.Exception:" + ex), (Throwable)ex);
                        }
                        throw throwable;
                    }
                    try {
                        redisQueue.disconnect();
                    }
                    catch (Exception ex) {
                        log.warn((Object)("Failed to disconnect redisQueue.Exception:" + ex), (Throwable)ex);
                    }
                    break block17;
                }
                switch (outcomes.getOutcomeType()) {
                    case 1: {
                        CommonOutcomes.processOutcomes(programId, (UserRewardOutcome)outcomes, new AccountEntrySourceData(RewardResource.class));
                        break;
                    }
                    case 2: {
                        CommonOutcomes.processOutcomes((MMv2Outcomes)outcomes, new AccountEntrySourceData(RewardResource.class));
                        break;
                    }
                    default: {
                        throw new CommonOutcomes.InvalidJSONException("Unsupported outcome type:[" + outcomes.getOutcomeType() + "].");
                    }
                }
            }
            catch (FusionRestException ex) {
                throw ex;
            }
            catch (CommonOutcomes.InvalidJSONException ex) {
                String errorCode = "E-" + UUID.randomUUID().toString();
                log.warn((Object)("Invalid payload (" + errorCode + "). Exception:[" + (Object)((Object)ex) + "].Data:[" + outcomeJsonString + "]"), (Throwable)((Object)ex));
                throw new FusionRestException(FusionRestException.RestException.INVALID_PAYLOAD_DATA.getCode(), "Invalid payload (" + errorCode + ")");
            }
            catch (Exception ex) {
                String errorCode = "E-" + UUID.randomUUID().toString();
                String errorMessage = "Internal error (" + errorCode + "). Exception:[" + ex + "].Data:[" + outcomeJsonString + "]";
                log.error((Object)errorMessage, (Throwable)ex);
                throw new FusionRestException(FusionRestException.RestException.ERROR.getCode(), "Internal error (" + errorCode + ")");
            }
        }
    }
}

