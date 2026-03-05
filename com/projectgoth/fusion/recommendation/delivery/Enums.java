/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.recommendation.delivery;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.EnumUtils;
import com.projectgoth.fusion.common.Redis;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.ChatRoomData;
import com.projectgoth.fusion.data.RecommendationItem;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Message;
import com.projectgoth.fusion.interfaces.MessageHome;
import com.projectgoth.fusion.recommendation.delivery.RecommendationDeliveryUtils;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;

public class Enums {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(Enums.class));

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum UserRecommendationEnum {
        FOLLOWINGS_FOLLOWING("FL"),
        FOLLOWERS_FOLLOWING("FLR"),
        FRIENDS_FOLLOWING("FR");

        private String value;

        private UserRecommendationEnum(String value) {
            this.value = value;
        }

        public String value() {
            return this.value;
        }

        public static UserRecommendationEnum fromValue(String value) {
            for (UserRecommendationEnum e : UserRecommendationEnum.values()) {
                if (e.value() != value) continue;
                return e;
            }
            return null;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum RecommendationTypeEnum implements EnumUtils.IEnumValueGetter<String>
    {
        HASH_TAGS("HT", new RecommendationDeliveryUtils.RecommendationTypeEvaluator<String>(){

            @Override
            public String evaluate(RecommendationItem item) {
                return item.getValue();
            }
        }),
        CHATROOMS("CR", new RecommendationDeliveryUtils.RecommendationTypeEvaluator<ChatRoomData>(){

            @Override
            public ChatRoomData evaluate(RecommendationItem item) {
                int chatroomId;
                try {
                    chatroomId = Integer.parseInt(item.getValue());
                }
                catch (NumberFormatException nfe) {
                    if (SystemProperty.getBool(SystemPropertyEntities.RecommendationServiceSettings.REDIS_LOCATION_ERROR_LOGGING_ENABLED)) {
                        log.error((Object)("RecommendationItem contains invalid data! Value=" + item.getValue() + (item.getRedisLocation() != null ? " redis location=" + item.getRedisLocation() : "") + " ex=" + nfe), (Throwable)nfe);
                    }
                    throw nfe;
                }
                try {
                    Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
                    return messageEJB.getSimpleChatRoomData(chatroomId, null);
                }
                catch (CreateException e) {
                    log.error((Object)"Unabled to create bean to extract chatroom details", (Throwable)e);
                    return null;
                }
                catch (RemoteException e) {
                    log.error((Object)("Unabled to get chatroom data for chatroom id " + item.getValue()), (Throwable)e);
                    return null;
                }
            }
        }),
        ADDRESSBOOKCONTACTS("ABC", new RecommendationDeliveryUtils.RecommendationTypeEvaluator<String>(){

            @Override
            public String evaluate(RecommendationItem item) {
                return item.getValue();
            }
        }),
        USERRECOMMENDATION("UR", new RecommendationDeliveryUtils.RecommendationTypeEvaluator<String>(){

            @Override
            public String evaluate(RecommendationItem item) {
                return item.getValue();
            }
        });

        String value;
        RecommendationDeliveryUtils.RecommendationTypeEvaluator evaluator;

        private RecommendationTypeEnum(String value, RecommendationDeliveryUtils.RecommendationTypeEvaluator transform) {
            this.value = value;
            this.evaluator = transform;
        }

        public String value() {
            return this.value;
        }

        public static RecommendationTypeEnum fromValue(String v) {
            return SingletonHolder.getLookupMap().get(v);
        }

        public RecommendationDeliveryUtils.RecommendationTypeEvaluator getEvaluator() {
            return this.evaluator;
        }

        public String getEnumValue() {
            return this.value();
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        private static class SingletonHolder {
            private static final Map<String, RecommendationTypeEnum> LOOKUP_MAP = EnumUtils.buildLookUpMap(new HashMap(), RecommendationTypeEnum.class);

            private SingletonHolder() {
            }

            public static Map<String, RecommendationTypeEnum> getLookupMap() {
                return LOOKUP_MAP;
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum RecommendationTargetEnum implements EnumUtils.IEnumValueGetter<String>
    {
        INDIVIDUAL("user", Redis.KeySpace.USER_ENTITY),
        COUNTRY("country", Redis.KeySpace.COUNTRY_ENTITY);

        String value;
        Redis.KeySpace redisKeySpace;

        private RecommendationTargetEnum(String value, Redis.KeySpace redisKeySpace) {
            this.value = value;
            this.redisKeySpace = redisKeySpace;
        }

        public String value() {
            return this.value;
        }

        public static RecommendationTargetEnum fromValue(String v) {
            return SingletonHolder.getLookupMap().get(v);
        }

        public String getEnumValue() {
            return this.value();
        }

        public Redis.KeySpace getRedisKeySpace() {
            return this.redisKeySpace;
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        private static class SingletonHolder {
            private static final Map<String, RecommendationTargetEnum> LOOKUP_MAP = EnumUtils.buildLookUpMap(new HashMap(), RecommendationTargetEnum.class);

            private SingletonHolder() {
            }

            public static Map<String, RecommendationTargetEnum> getLookupMap() {
                return LOOKUP_MAP;
            }
        }
    }
}

