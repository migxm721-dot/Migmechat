/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.app.dao.db.elasticsearch;

import com.projectgoth.fusion.app.dao.DAOException;
import com.projectgoth.fusion.app.dao.base.ChatRoomDAOChain;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.data.ChatRoomData;
import com.projectgoth.fusion.search.ChatRoomsIndex;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ElasticSearchChatRoomDAOChain
extends ChatRoomDAOChain {
    @Override
    public List<ChatRoomData> getChatRooms(int countryID, String search, String language, boolean includeAdultOnly, boolean searchKeywords) throws DAOException {
        if (SystemProperty.getBool("DisableElasticSearch", false) || SystemProperty.getBool("DisableElasticSearchQueries", false)) {
            try {
                return ChatRoomsIndex.searchChatRooms(countryID, search, language, includeAdultOnly, searchKeywords, SystemProperty.getInt("MaxChatRoomsReturned"));
            }
            catch (Exception e) {
                return super.getChatRooms(countryID, search, language, includeAdultOnly, searchKeywords);
            }
        }
        return super.getChatRooms(countryID, search, language, includeAdultOnly, searchKeywords);
    }
}

