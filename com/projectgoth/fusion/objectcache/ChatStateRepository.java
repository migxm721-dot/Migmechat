/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.objectcache;

import com.projectgoth.fusion.datagrid.DataGrid;
import com.projectgoth.fusion.datagrid.DataGridFactory;
import com.projectgoth.fusion.objectcache.ChatUserState;
import com.projectgoth.fusion.slice.FusionException;
import java.util.Map;

public class ChatStateRepository {
    DataGrid grid;
    Map<String, ChatUserState> userStates;

    public ChatStateRepository() throws FusionException {
        DataGridFactory.getInstance().getGrid().prepare();
        this.grid = DataGridFactory.getInstance().getGrid();
        this.userStates = this.grid.getUsersMap();
    }

    public ChatUserState getUserState(String username) {
        try {
            ChatUserState state = this.userStates.get(username);
            return state;
        }
        catch (Exception exception) {
            return null;
        }
    }

    public void setUserState(ChatUserState state) {
        try {
            this.userStates.put(state.username, state);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}

