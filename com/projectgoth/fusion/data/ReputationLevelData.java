/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.data;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReputationLevelData
implements Serializable {
    public Integer score;
    public Integer level;
    public String name;
    public String image;
    public Boolean createChatRoom;
    public Integer chatRoomSize;
    public Boolean createGroup;
    public Integer groupSize;
    public Integer numGroupChatRooms;
    public Boolean publishPhoto;
    public Boolean postCommentLikeUserWall;
    public Boolean addToPhotoWall;
    public Boolean enterPot;
    public int numGroupModerators;

    public ReputationLevelData(ResultSet rs) throws SQLException {
        this.score = (Integer)rs.getObject("score");
        this.level = (Integer)rs.getObject("level");
        this.name = rs.getString("name");
        this.image = rs.getString("image");
        this.chatRoomSize = (Integer)rs.getObject("chatRoomSize");
        this.groupSize = (Integer)rs.getObject("groupSize");
        this.numGroupChatRooms = (Integer)rs.getObject("numGroupChatRooms");
        Integer intVal = (Integer)rs.getObject("createChatRoom");
        if (intVal != null) {
            this.createChatRoom = intVal != 0;
        }
        if ((intVal = (Integer)rs.getObject("createGroup")) != null) {
            this.createGroup = intVal != 0;
        }
        if ((intVal = (Integer)rs.getObject("publishPhoto")) != null) {
            this.publishPhoto = intVal != 0;
        }
        if ((intVal = (Integer)rs.getObject("postCommentLikeUserWall")) != null) {
            this.postCommentLikeUserWall = intVal != 0;
        }
        if ((intVal = (Integer)rs.getObject("addToPhotoWall")) != null) {
            this.addToPhotoWall = intVal != 0;
        }
        if ((intVal = (Integer)rs.getObject("enterPot")) != null) {
            this.enterPot = intVal != 0;
        }
        this.numGroupModerators = rs.getInt("numGroupModerators");
    }
}

