/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.gateway.packet;

import com.projectgoth.fusion.packet.FusionPacket;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FusionPktChatRoomTheme
extends FusionPacket {
    private static final String KEY_BACKGROUND_COLOR = "background_color";
    private static final String KEY_BACKGROUND_IMG_URL = "background_img_url";
    private static final String KEY_BACKGROUND_IMG_ALIGNMENT = "background_img_alignment";
    private static final String KEY_SENDER_USERNAME_COLOR = "sender_username_color";
    private static final String KEY_SENDER_MESSAGE_COLOR = "sender_message_color";
    private static final String KEY_RECP_USERNAME_COLOR = "recp_username_color";
    private static final String KEY_RECP_MESSAGE_COLOR = "recp_message_color";
    private static final String KEY_ADMIN_USERNAME_COLOR = "admin_username_color";
    private static final String KEY_ADMIN_MESSAGE_COLOR = "admin_message_color";
    private static final String KEY_EMOTE_MESSAGE_COLOR = "emote_message_color";
    private static final String KEY_ERROR_MESSAGE_COLOR = "error_message_color";
    private static final String KEY_SERVER_USERNAME_COLOR = "server_username_color";
    private static final String KEY_SERVER_MESSAGE_COLOR = "server_message_color";
    private static final String KEY_CLIENT_MESSAGE_COLOR = "client_message_color";

    public FusionPktChatRoomTheme() {
        super((short)719);
    }

    public FusionPktChatRoomTheme(short transactionId) {
        super((short)719, transactionId);
    }

    public FusionPktChatRoomTheme(FusionPacket packet) {
        super(packet);
    }

    public FusionPktChatRoomTheme(short transactionId, int themeID, Map<String, String> theme) {
        this(transactionId);
        this.setThemeId(themeID);
        if (theme.get(KEY_BACKGROUND_COLOR) != null) {
            this.setBackgroundColor(Integer.parseInt(theme.get(KEY_BACKGROUND_COLOR)));
        }
        if (theme.get(KEY_BACKGROUND_IMG_URL) != null) {
            this.setBackgroundImgURL(theme.get(KEY_BACKGROUND_IMG_URL));
        }
        if (theme.get(KEY_BACKGROUND_IMG_ALIGNMENT) != null) {
            this.setBackgroundImgAlignment(Integer.parseInt(theme.get(KEY_BACKGROUND_IMG_ALIGNMENT)));
        }
        if (theme.get(KEY_SENDER_USERNAME_COLOR) != null) {
            this.setSenderUsernameColor(Integer.parseInt(theme.get(KEY_SENDER_USERNAME_COLOR)));
        }
        if (theme.get(KEY_SENDER_MESSAGE_COLOR) != null) {
            this.setSenderMsgColor(Integer.parseInt(theme.get(KEY_SENDER_MESSAGE_COLOR)));
        }
        if (theme.get(KEY_RECP_USERNAME_COLOR) != null) {
            this.setRecpUsernameColor(Integer.parseInt(theme.get(KEY_RECP_USERNAME_COLOR)));
        }
        if (theme.get(KEY_RECP_MESSAGE_COLOR) != null) {
            this.setRecpMsgColor(Integer.parseInt(theme.get(KEY_RECP_MESSAGE_COLOR)));
        }
        if (theme.get(KEY_ADMIN_USERNAME_COLOR) != null) {
            this.setAdminUsernameColor(Integer.parseInt(theme.get(KEY_ADMIN_USERNAME_COLOR)));
        }
        if (theme.get(KEY_ADMIN_MESSAGE_COLOR) != null) {
            this.setAdminMsgColor(Integer.parseInt(theme.get(KEY_ADMIN_MESSAGE_COLOR)));
        }
        if (theme.get(KEY_EMOTE_MESSAGE_COLOR) != null) {
            this.setEmoteMsgColor(Integer.parseInt(theme.get(KEY_EMOTE_MESSAGE_COLOR)));
        }
        if (theme.get(KEY_ERROR_MESSAGE_COLOR) != null) {
            this.setErrorMsgColor(Integer.parseInt(theme.get(KEY_ERROR_MESSAGE_COLOR)));
        }
        if (theme.get(KEY_SERVER_USERNAME_COLOR) != null) {
            this.setServerUsernameColor(Integer.parseInt(theme.get(KEY_SERVER_USERNAME_COLOR)));
        }
        if (theme.get(KEY_SERVER_MESSAGE_COLOR) != null) {
            this.setServerMsgColor(Integer.parseInt(theme.get(KEY_SERVER_MESSAGE_COLOR)));
        }
        if (theme.get(KEY_CLIENT_MESSAGE_COLOR) != null) {
            this.setClientMsgColor(Integer.parseInt(theme.get(KEY_CLIENT_MESSAGE_COLOR)));
        }
    }

    public Integer getThemeId() {
        return this.getIntField((short)1);
    }

    public void setThemeId(int themeId) {
        this.setField((short)1, themeId);
    }

    public Integer getBackgroundColor() {
        return this.getIntField((short)2);
    }

    public void setBackgroundColor(int backgroundColor) {
        this.setField((short)2, backgroundColor);
    }

    public String getBackgroundImgURL() {
        return this.getStringField((short)3);
    }

    public void setBackgroundImgURL(String backgroundImgURL) {
        this.setField((short)3, backgroundImgURL);
    }

    public int getBackgroundImgAlignment() {
        return this.getIntField((short)4);
    }

    public void setBackgroundImgAlignment(int backgroundImgAlignment) {
        this.setField((short)4, backgroundImgAlignment);
    }

    public int getSenderUsernameColor() {
        return this.getIntField((short)5);
    }

    public void setSenderUsernameColor(int senderUsernameColor) {
        this.setField((short)5, senderUsernameColor);
    }

    public int getSenderMsgColor() {
        return this.getIntField((short)6);
    }

    public void setSenderMsgColor(int senderMsgColor) {
        this.setField((short)6, senderMsgColor);
    }

    public int getRecpUsernameColor() {
        return this.getIntField((short)7);
    }

    public void setRecpUsernameColor(int recpUsernameColor) {
        this.setField((short)7, recpUsernameColor);
    }

    public int getRecpMsgColor() {
        return this.getIntField((short)8);
    }

    public void setRecpMsgColor(int recpMsgColor) {
        this.setField((short)8, recpMsgColor);
    }

    public int getAdminUsernameColor() {
        return this.getIntField((short)9);
    }

    public void setAdminUsernameColor(int adminUsernameColor) {
        this.setField((short)9, adminUsernameColor);
    }

    public int getAdminMsgColor() {
        return this.getIntField((short)10);
    }

    public void setAdminMsgColor(int adminMsgColor) {
        this.setField((short)10, adminMsgColor);
    }

    public int getEmoteMsgColor() {
        return this.getIntField((short)11);
    }

    public void setEmoteMsgColor(int emoteMsgColor) {
        this.setField((short)11, emoteMsgColor);
    }

    public int getErrorMsgColor() {
        return this.getIntField((short)12);
    }

    public void setErrorMsgColor(int errorMsgColor) {
        this.setField((short)12, errorMsgColor);
    }

    public int getServerUsernameColor() {
        return this.getIntField((short)13);
    }

    public void setServerUsernameColor(int serverUsernameColor) {
        this.setField((short)13, serverUsernameColor);
    }

    public int getServerMsgColor() {
        return this.getIntField((short)14);
    }

    public void setServerMsgColor(int serverMsgColor) {
        this.setField((short)14, serverMsgColor);
    }

    public int getClientMsgColor() {
        return this.getIntField((short)15);
    }

    public void setClientMsgColor(int clientMsgColor) {
        this.setField((short)15, clientMsgColor);
    }
}

