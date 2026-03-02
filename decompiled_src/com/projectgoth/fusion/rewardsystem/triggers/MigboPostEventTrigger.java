/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.projectgoth.leto.common.event.post.MigboPostActionEvent
 *  com.projectgoth.leto.common.event.post.PostActionType
 *  com.projectgoth.leto.common.utils.enums.IEnumValueGetter
 *  com.projectgoth.leto.common.utils.enums.ValueToEnumMap
 */
package com.projectgoth.fusion.rewardsystem.triggers;

import com.projectgoth.fusion.common.Enums;
import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;
import com.projectgoth.leto.common.event.post.MigboPostActionEvent;
import com.projectgoth.leto.common.event.post.PostActionType;
import com.projectgoth.leto.common.utils.enums.IEnumValueGetter;
import com.projectgoth.leto.common.utils.enums.ValueToEnumMap;
import java.util.EnumSet;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MigboPostEventTrigger
extends RewardProgramTrigger
implements MigboPostActionEvent {
    public String postID;
    public PostEventTypeEnum eventType;
    public EnumSet<Enums.ThirdPartyEnum> shareToThirdParty;

    public MigboPostEventTrigger(UserData postAuthorUserData, String postID, PostEventTypeEnum eventType) {
        super(RewardProgramData.TypeEnum.MIGBO_POST_EVENT, postAuthorUserData);
        this.postID = postID;
        this.eventType = eventType;
    }

    public String getPostID() {
        return this.postID;
    }

    public PostActionType getActionType() {
        return this.eventType != null ? this.eventType.postActionType : null;
    }

    public Set<Enums.ThirdPartyEnum> getSharedToSites() {
        return this.shareToThirdParty;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum PostEventTypeEnum implements IEnumValueGetter<Integer>
    {
        REPLIED_TO(PostActionType.REPLIED_TO),
        RESHARED(PostActionType.RESHARED),
        SUBSCRIBED(PostActionType.SUBSCRIBED),
        EMOTIONAL_FOOTPRINTED(PostActionType.EMOTIONAL_FOOTPRINTED);

        private final PostActionType postActionType;

        private PostEventTypeEnum(PostActionType postActionType) {
            this.postActionType = postActionType;
        }

        public int getType() {
            return this.postActionType.getEnumValue();
        }

        public static boolean isValid(int type) {
            return PostEventTypeEnum.fromType(type) != null;
        }

        public static PostEventTypeEnum fromType(int type) {
            return (PostEventTypeEnum)ValueToEnumMapInstance.INSTANCE.toEnum((Object)type);
        }

        public Integer getEnumValue() {
            return this.postActionType.getEnumValue();
        }

        private static final class ValueToEnumMapInstance {
            public static final ValueToEnumMap<Integer, PostEventTypeEnum> INSTANCE = new ValueToEnumMap(PostEventTypeEnum.class);

            private ValueToEnumMapInstance() {
            }
        }
    }
}

