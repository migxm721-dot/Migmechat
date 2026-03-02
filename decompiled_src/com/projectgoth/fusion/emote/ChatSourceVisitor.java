/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.emote;

import com.projectgoth.fusion.emote.ChatSource;
import com.projectgoth.fusion.slice.FusionException;

public interface ChatSourceVisitor {
    public void visit(ChatSource.LocalChatRoomChatSource var1) throws FusionException;

    public void visit(ChatSource.RemoteChatRoomChatSource var1) throws FusionException;

    public void visit(ChatSource.LocalGroupChatChatSource var1) throws FusionException;

    public void visit(ChatSource.RemoteGroupChatChatSource var1) throws FusionException;

    public void visit(ChatSource.LocalPrivateChatChatSource var1) throws FusionException;

    public void visit(ChatSource.RemotePrivateChatChatSource var1) throws FusionException;
}

