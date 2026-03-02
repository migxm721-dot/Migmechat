/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.app.dao.db.fusiondb;

import com.projectgoth.fusion.app.dao.DAOException;
import com.projectgoth.fusion.app.dao.base.EmoAndStickerDAOChain;
import com.projectgoth.fusion.app.dao.db.fusiondb.DBUtils;
import com.projectgoth.fusion.data.EmoticonData;
import com.projectgoth.fusion.data.EmoticonPackData;
import com.projectgoth.fusion.data.StoreItemData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FusionDbEmoAndStickerDAOChain
extends EmoAndStickerDAOChain {
    private static final Logger log = Logger.getLogger(FusionDbEmoAndStickerDAOChain.class);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Map<Integer, EmoticonPackData> loadEmoticonPacks() throws DAOException {
        HashMap<Integer, EmoticonPackData> hashMap;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtils.getFusionReadConnection();
            ps = conn.prepareStatement("select ep.*, e.id as emoticonid, e.type as contenttype, s.catalogimage as thumbnailfile,s.catalogimage as catalogimage from emoticonpack ep join emoticon e on ep.id = e.emoticonpackid  left join storeitem s on (s.referenceid = ep.id and s.type = ?)  where ep.status = ?  and e.type in (1,2,3,4) order by ep.id");
            ps.setInt(1, StoreItemData.TypeEnum.STICKER.value());
            ps.setInt(2, EmoticonPackData.StatusEnum.ACTIVE.value());
            rs = ps.executeQuery();
            HashMap<Integer, EmoticonPackData> emoticonPacks = new HashMap<Integer, EmoticonPackData>();
            while (rs.next()) {
                EmoticonPackData emoticonPackData = this.readEmoticonPackFromResultSet(rs);
                emoticonPacks.put(emoticonPackData.getId(), emoticonPackData);
                rs.previous();
            }
            hashMap = emoticonPacks;
            Object var7_8 = null;
        }
        catch (Exception e) {
            Map<Integer, EmoticonPackData> map;
            try {
                log.error((Object)"Unable to loadEmoticonPacks from fusion db", (Throwable)e);
                map = super.loadEmoticonPacks();
                Object var7_9 = null;
            }
            catch (Throwable throwable) {
                Object var7_10 = null;
                DBUtils.closeResource(rs, ps, conn, log);
                throw throwable;
            }
            DBUtils.closeResource(rs, ps, conn, log);
            return map;
        }
        DBUtils.closeResource(rs, ps, conn, log);
        return hashMap;
    }

    private EmoticonPackData readEmoticonPackFromResultSet(ResultSet rs) throws SQLException {
        EmoticonPackData emoticonPack = new EmoticonPackData();
        emoticonPack.setId(rs.getInt("id"));
        emoticonPack.setType(EmoticonPackData.TypeEnum.fromValue(rs.getInt("type")));
        emoticonPack.setName(rs.getString("name"));
        emoticonPack.setDescription(rs.getString("description"));
        emoticonPack.setPrice(rs.getDouble("price"));
        emoticonPack.setServiceID(rs.getInt("ServiceID"));
        emoticonPack.setGroupID(rs.getInt("GroupID"));
        emoticonPack.setServiceID(rs.getInt("ServiceID"));
        emoticonPack.setGroupVIPOnly(rs.getBoolean("GroupVIPOnly"));
        emoticonPack.setForSale(rs.getBoolean("ForSale"));
        emoticonPack.setSortOrder(rs.getInt("SortOrder"));
        emoticonPack.setStatus(EmoticonPackData.StatusEnum.fromValue(rs.getInt("status")));
        emoticonPack.setVersion(rs.getInt("version"));
        emoticonPack.setThumbnailFile(rs.getString("thumbnailfile"));
        emoticonPack.setCatalogImage(rs.getString("catalogimage"));
        int contentType = rs.getInt("contenttype");
        if (contentType == 5) {
            emoticonPack.setContentType(EmoticonPackData.ContentTypeEnum.STICKER);
        } else {
            emoticonPack.setContentType(EmoticonPackData.ContentTypeEnum.EMOTICON);
        }
        do {
            emoticonPack.addEmoticonID(rs.getInt("emoticonid"));
        } while (rs.next() && emoticonPack.getId().equals(rs.getInt("id")));
        return emoticonPack;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Map<Integer, EmoticonData> loadEmoticons() throws DAOException {
        HashMap<Integer, EmoticonData> hashMap;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtils.getFusionReadConnection();
            ps = conn.prepareStatement("select e.*, ehk.type hotkeytype, ehk.hotkey from emoticon e left outer join emoticonhotkey ehk on e.id = ehk.emoticonid order by e.id");
            rs = ps.executeQuery();
            HashMap<Integer, EmoticonData> emoticons = new HashMap<Integer, EmoticonData>();
            while (rs.next()) {
                EmoticonData emoticonData = new EmoticonData(rs);
                do {
                    String hotKey;
                    if ((hotKey = rs.getString("hotKey")) == null || hotKey.length() <= 0) continue;
                    EmoticonData.HotKeyTypeEnum type = EmoticonData.HotKeyTypeEnum.fromValue(rs.getInt("hotkeytype"));
                    if (type == EmoticonData.HotKeyTypeEnum.PRIMARY) {
                        emoticonData.hotKey = hotKey;
                        continue;
                    }
                    emoticonData.alternateHotKeys.add(hotKey);
                } while (rs.next() && emoticonData.id.equals((Integer)rs.getObject("id")));
                rs.previous();
                emoticons.put(emoticonData.id, emoticonData);
            }
            hashMap = emoticons;
            Object var9_10 = null;
        }
        catch (Exception e) {
            Map<Integer, EmoticonData> map;
            try {
                log.error((Object)"Unable to loadEmoticonPacks from fusion db", (Throwable)e);
                map = super.loadEmoticons();
                Object var9_11 = null;
            }
            catch (Throwable throwable) {
                Object var9_12 = null;
                DBUtils.closeResource(rs, ps, conn, log);
                throw throwable;
            }
            DBUtils.closeResource(rs, ps, conn, log);
            return map;
        }
        DBUtils.closeResource(rs, ps, conn, log);
        return hashMap;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SortedSet<Integer> loadEmoticonHeights() throws DAOException {
        TreeSet<Integer> treeSet;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtils.getFusionReadConnection();
            ps = conn.prepareStatement("select distinct height from emoticon where type in (1,2,3,4) order by height");
            rs = ps.executeQuery();
            TreeSet<Integer> emoticonHeights = new TreeSet<Integer>();
            while (rs.next()) {
                emoticonHeights.add(rs.getInt(1));
            }
            treeSet = emoticonHeights;
            Object var7_8 = null;
        }
        catch (Exception e) {
            SortedSet<Integer> sortedSet;
            try {
                log.error((Object)"Unable to loadEmoticonHeights from fusion db", (Throwable)e);
                sortedSet = super.loadEmoticonHeights();
                Object var7_9 = null;
            }
            catch (Throwable throwable) {
                Object var7_10 = null;
                DBUtils.closeResource(rs, ps, conn, log);
                throw throwable;
            }
            DBUtils.closeResource(rs, ps, conn, log);
            return sortedSet;
        }
        DBUtils.closeResource(rs, ps, conn, log);
        return treeSet;
    }
}

