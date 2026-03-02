/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.ext.Provider
 *  org.apache.log4j.Logger
 *  org.json.JSONObject
 */
package com.projectgoth.fusion.restapi.resource;

import com.projectgoth.fusion.clientsession.SSOLogin;
import com.projectgoth.fusion.clientsession.SSOLoginData;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.data.ListDataWrapper;
import com.projectgoth.fusion.data.StoreCategoryData;
import com.projectgoth.fusion.data.StoreItemData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.data.VirtualGiftData;
import com.projectgoth.fusion.data.VirtualGiftReceivedData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.fdl.enums.ClientType;
import com.projectgoth.fusion.interfaces.ContentLocal;
import com.projectgoth.fusion.interfaces.ContentLocalHome;
import com.projectgoth.fusion.interfaces.UserLocal;
import com.projectgoth.fusion.interfaces.UserLocalHome;
import com.projectgoth.fusion.restapi.data.DataHolder;
import com.projectgoth.fusion.restapi.data.FusionRestException;
import com.projectgoth.fusion.restapi.data.SSOEnums;
import com.projectgoth.fusion.restapi.util.ResourceUtil;
import com.projectgoth.fusion.slice.ConnectionPrx;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.ext.Provider;
import org.apache.log4j.Logger;
import org.json.JSONObject;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Provider
@Path(value="/store")
public class StoreResource {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(StoreResource.class));

    @POST
    @Path(value="/item/{id}/purchase")
    @Produces(value={"application/json"})
    public DataHolder<StoreItemData> purchaseFusionSession(@QueryParam(value="sessionId") String sessionId, @QueryParam(value="requestingUserid") int requestingUserId, @PathParam(value="id") int id, String jsonStr) throws FusionRestException {
        if (SystemProperty.getBool(SystemPropertyEntities.Temp.ER62_ENABLED)) {
            ConnectionPrx prx = ResourceUtil.getConnectionProxy(sessionId);
            UserData userData = new UserData(prx.getUserObject().getUserData());
            if (!userData.userID.equals(requestingUserId)) {
                log.error((Object)String.format("User session id for %s is not the same with user %s", userData.username, requestingUserId));
                throw new FusionRestException(FusionRestException.RestException.ERROR, String.format("Invalid session id for user %s", requestingUserId));
            }
            return this.purchase(requestingUserId, id, jsonStr, prx.getDeviceTypeAsInt(), new AccountEntrySourceData(prx.getSessionObject()));
        }
        return this.purchaseOld(sessionId, requestingUserId, id, jsonStr);
    }

    @POST
    @Path(value="/item/{id}/purchaseWap")
    @Produces(value={"application/json"})
    public DataHolder<StoreItemData> purchaseWapSession(@QueryParam(value="ipAddress") String ipAddress, @QueryParam(value="sessionId") String sessionId, @QueryParam(value="mobileDevice") String mobileDevice, @QueryParam(value="userAgent") String userAgent, @QueryParam(value="requestingUserid") int requestingUserId, @PathParam(value="id") int id, String jsonStr) throws FusionRestException {
        SSOLoginData loginData = SSOLogin.getLoginDataFromMemcache(ResourceUtil.getSID(sessionId));
        if (loginData == null || !loginData.userID.equals(requestingUserId)) {
            throw new FusionRestException(FusionRestException.RestException.ERROR, String.format("Invalid session id for user %s", requestingUserId));
        }
        return this.purchase(requestingUserId, id, jsonStr, ClientType.WAP.value(), new AccountEntrySourceData(ipAddress, sessionId, mobileDevice, userAgent));
    }

    public DataHolder<StoreItemData> purchaseOld(@QueryParam(value="sessionId") String sessionId, @QueryParam(value="requestingUserid") int requestingUserId, @PathParam(value="id") int id, String jsonStr) throws FusionRestException {
        ConnectionPrx prx = ResourceUtil.getConnectionProxy(sessionId);
        UserData userData = new UserData(prx.getUserObject().getUserData());
        if (!userData.userID.equals(requestingUserId)) {
            log.error((Object)String.format("User session id for %s is not the same with user %s", userData.username, requestingUserId));
            throw new FusionRestException(FusionRestException.RestException.ERROR, String.format("Invalid session id for user %s", requestingUserId));
        }
        AccountEntrySourceData accountEntrySourceData = new AccountEntrySourceData(prx.getSessionObject());
        try {
            ContentLocal contentBean = (ContentLocal)EJBHomeCache.getLocalObject("ContentLocal", ContentLocalHome.class);
            DataHolder<StoreItemData> dataHolder = this.getStoreItem(requestingUserId, id);
            StoreItemData sid = (StoreItemData)dataHolder.data;
            if (sid == null) {
                log.error((Object)String.format("Failed to retrieved store item id for %d", id));
                throw new FusionRestException(FusionRestException.RestException.ERROR, "Unable to find the store item id for " + id);
            }
            switch (sid.type) {
                case VIRTUAL_GIFT: {
                    JSONObject json = new JSONObject(jsonStr);
                    String[] recepients = !StringUtil.isBlank(json.getString("to")) ? json.getString("to").split(",") : null;
                    String message = json.getString("message");
                    boolean privateGifts = json.getBoolean("private");
                    VirtualGiftData vgd = (VirtualGiftData)sid.referenceData;
                    Map virtualGiftReceivedIdMap = contentBean.buyVirtualGiftForMultipleUsers(userData.username, Arrays.asList(recepients), vgd, VirtualGiftReceivedData.PurchaseLocationEnum.STORE.value(), privateGifts, message, false, false, accountEntrySourceData);
                    UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                    HashMap recipientUserDataList = new HashMap();
                    ClientType deviceEnum = ClientType.fromValue(prx.getDeviceTypeAsInt());
                    String receiverStr = "";
                    for (Map.Entry entry : virtualGiftReceivedIdMap.entrySet()) {
                        String recipient = (String)entry.getKey();
                        UserData recipientUserData = userBean.loadUser(recipient, false, false);
                        if (recipientUserData == null) continue;
                        receiverStr = receiverStr + " @" + recipient;
                        recipientUserDataList.put(entry.getValue(), recipientUserData);
                    }
                    if (json.getBoolean("postToMiniblog") && virtualGiftReceivedIdMap.keySet().size() != 0) {
                        String msg = SystemProperty.getBool(SystemPropertyEntities.Temp.WW422_ENABLED) && !StringUtil.isBlank(message) ? String.format(SystemProperty.get(SystemPropertyEntities.ContentService.VIRTUAL_GIFT_POST_MESSAGE_CUSTOM), message, vgd.getName(), receiverStr, vgd.getGiftUrl(VirtualGiftData.ImageFormatType.PNG, (short)SystemProperty.getInt(SystemPropertyEntities.ContentService.VIRTUAL_GIFT_RESOLUTIONS_DEFAULT))) : (virtualGiftReceivedIdMap.keySet().size() == 1 ? String.format(SystemProperty.get(SystemPropertyEntities.ContentService.VIRTUAL_GIFT_POST_MESSAGE), vgd.getName(), receiverStr, vgd.getGiftUrl(VirtualGiftData.ImageFormatType.PNG, (short)SystemProperty.getInt(SystemPropertyEntities.ContentService.VIRTUAL_GIFT_RESOLUTIONS_DEFAULT))) : String.format(SystemProperty.get(SystemPropertyEntities.ContentService.VIRTUAL_GIFT_POST_MESSAGES), vgd.getName(), receiverStr, vgd.getGiftUrl(VirtualGiftData.ImageFormatType.PNG, (short)SystemProperty.getInt(SystemPropertyEntities.ContentService.VIRTUAL_GIFT_RESOLUTIONS_DEFAULT))));
                        contentBean.createMigboTextPostForUser(userData.userID, msg, null, null, "1", deviceEnum, SSOEnums.View.fromFusionDeviceEnum(deviceEnum));
                    }
                    contentBean.onPurchaseVirtualGift(userData.username, recipientUserDataList, vgd, privateGifts, message, null);
                    break;
                }
                case AVATAR: {
                    contentBean.buyAvatarItem(userData.userID, userData.userID, sid.referenceID, accountEntrySourceData);
                    break;
                }
                case STICKER: 
                case EMOTICON: 
                case SUPER_EMOTICON: {
                    contentBean.buyEmoticonPack(userData.username, sid.referenceID, accountEntrySourceData);
                    break;
                }
                case THEME: {
                    break;
                }
            }
            return dataHolder;
        }
        catch (FusionRestException fre) {
            throw fre;
        }
        catch (Exception e) {
            log.error((Object)"Failed to purchase store item.", (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.ERROR, e.getMessage());
        }
    }

    public DataHolder<StoreItemData> purchase(int requestingUserId, int id, String jsonStr, int device, AccountEntrySourceData accountEntrySourceData) throws FusionRestException {
        try {
            ContentLocal contentBean = (ContentLocal)EJBHomeCache.getLocalObject("ContentLocal", ContentLocalHome.class);
            DataHolder<StoreItemData> dataHolder = this.getStoreItem(requestingUserId, id);
            StoreItemData sid = (StoreItemData)dataHolder.data;
            if (sid == null) {
                log.error((Object)String.format("Failed to retrieved store item id for %d", id));
                throw new FusionRestException(FusionRestException.RestException.ERROR, "Unable to find the store item id for " + id);
            }
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            String requestingUsername = userBean.getUsernameByUserid(requestingUserId, null);
            if (requestingUsername == null) {
                throw new FusionRestException(FusionRestException.RestException.ERROR, "invalid requesting user Id: " + requestingUserId);
            }
            switch (sid.type) {
                case VIRTUAL_GIFT: {
                    JSONObject json = new JSONObject(jsonStr);
                    String[] recepients = !StringUtil.isBlank(json.getString("to")) ? json.getString("to").split(",") : null;
                    String message = json.getString("message");
                    boolean privateGifts = json.getBoolean("private");
                    VirtualGiftData vgd = (VirtualGiftData)sid.referenceData;
                    Map virtualGiftReceivedIdMap = contentBean.buyVirtualGiftForMultipleUsers(requestingUsername, Arrays.asList(recepients), vgd, VirtualGiftReceivedData.PurchaseLocationEnum.STORE.value(), privateGifts, message, false, false, accountEntrySourceData);
                    HashMap recipientUserDataList = new HashMap();
                    ClientType deviceEnum = ClientType.fromValue(device);
                    String receiverStr = "";
                    for (Map.Entry entry : virtualGiftReceivedIdMap.entrySet()) {
                        String recipient = (String)entry.getKey();
                        UserData recipientUserData = userBean.loadUser(recipient, false, false);
                        if (recipientUserData == null) continue;
                        receiverStr = receiverStr + " @" + recipient;
                        recipientUserDataList.put(entry.getValue(), recipientUserData);
                    }
                    if (json.getBoolean("postToMiniblog") && virtualGiftReceivedIdMap.keySet().size() != 0) {
                        String msg = SystemProperty.getBool(SystemPropertyEntities.Temp.WW422_ENABLED) && !StringUtil.isBlank(message) ? String.format(SystemProperty.get(SystemPropertyEntities.ContentService.VIRTUAL_GIFT_POST_MESSAGE_CUSTOM), vgd.getName(), receiverStr, vgd.getGiftUrl(VirtualGiftData.ImageFormatType.PNG, (short)SystemProperty.getInt(SystemPropertyEntities.ContentService.VIRTUAL_GIFT_RESOLUTIONS_DEFAULT)), message) : (virtualGiftReceivedIdMap.keySet().size() == 1 ? String.format(SystemProperty.get(SystemPropertyEntities.ContentService.VIRTUAL_GIFT_POST_MESSAGE), vgd.getName(), receiverStr, vgd.getGiftUrl(VirtualGiftData.ImageFormatType.PNG, (short)SystemProperty.getInt(SystemPropertyEntities.ContentService.VIRTUAL_GIFT_RESOLUTIONS_DEFAULT))) : String.format(SystemProperty.get(SystemPropertyEntities.ContentService.VIRTUAL_GIFT_POST_MESSAGES), vgd.getName(), receiverStr, vgd.getGiftUrl(VirtualGiftData.ImageFormatType.PNG, (short)SystemProperty.getInt(SystemPropertyEntities.ContentService.VIRTUAL_GIFT_RESOLUTIONS_DEFAULT))));
                        contentBean.createMigboTextPostForUser(requestingUserId, msg, null, null, "1", deviceEnum, SSOEnums.View.fromFusionDeviceEnum(deviceEnum));
                    }
                    contentBean.onPurchaseVirtualGift(requestingUsername, recipientUserDataList, vgd, privateGifts, message, null);
                    break;
                }
                case AVATAR: {
                    contentBean.buyAvatarItem(requestingUserId, requestingUserId, sid.referenceID, accountEntrySourceData);
                    break;
                }
                case STICKER: 
                case EMOTICON: 
                case SUPER_EMOTICON: {
                    contentBean.buyEmoticonPack(requestingUsername, sid.referenceID, accountEntrySourceData);
                    break;
                }
                case THEME: {
                    break;
                }
            }
            return dataHolder;
        }
        catch (FusionRestException fre) {
            throw fre;
        }
        catch (Exception e) {
            log.error((Object)"Failed to purchase store item.", (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.ERROR, e.getMessage());
        }
    }

    @GET
    @Path(value="/category/{id}/items")
    @Produces(value={"application/json"})
    public DataHolder<ListDataWrapper<StoreItemData>> getStoreItemCategory(@QueryParam(value="requestingUserid") int requestingUserId, @PathParam(value="id") int id, @QueryParam(value="sortby") String sortby, @QueryParam(value="sortorder") String sortorder, @QueryParam(value="offset") int offset, @QueryParam(value="limit") int limit, @QueryParam(value="featured") boolean featured) throws FusionRestException {
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            String requestingUsername = userBean.getUsernameByUserid(requestingUserId, null);
            if (requestingUsername == null) {
                throw new FusionRestException(FusionRestException.RestException.ERROR, "Unable to find requesting user id ");
            }
            ContentLocal contentBean = (ContentLocal)EJBHomeCache.getLocalObject("ContentLocal", ContentLocalHome.class);
            ListDataWrapper sidw = contentBean.getStoreItemsByCategory(requestingUsername, id, sortby, sortorder, offset, limit, featured);
            return new DataHolder<ListDataWrapper<StoreItemData>>(sidw);
        }
        catch (Exception e) {
            log.error((Object)String.format("Failed to get store item category id: %d", id), (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.ERROR, e.getMessage());
        }
    }

    @GET
    @Path(value="/type/{id}/items")
    @Produces(value={"application/json"})
    public DataHolder<ListDataWrapper<StoreItemData>> getStoreItemType(@QueryParam(value="requestingUserid") int requestingUserId, @PathParam(value="id") int typeId, @QueryParam(value="sortby") String sortby, @QueryParam(value="sortorder") String sortorder, @QueryParam(value="offset") int offset, @QueryParam(value="limit") int limit, @QueryParam(value="featured") boolean featured) throws FusionRestException {
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            String requestingUsername = userBean.getUsernameByUserid(requestingUserId, null);
            if (requestingUsername == null) {
                throw new FusionRestException(FusionRestException.RestException.ERROR, "Unable to find requesting user id ");
            }
            ContentLocal contentBean = (ContentLocal)EJBHomeCache.getLocalObject("ContentLocal", ContentLocalHome.class);
            ListDataWrapper sidw = contentBean.getStoreItemsByType(requestingUsername, typeId, sortby, sortorder, offset, limit, featured);
            return new DataHolder<ListDataWrapper<StoreItemData>>(sidw);
        }
        catch (Exception e) {
            log.error((Object)String.format("Failed to get store item type id: %d", typeId), (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.ERROR, e.getMessage());
        }
    }

    @GET
    @Path(value="/item/{id}")
    @Produces(value={"application/json"})
    public DataHolder<StoreItemData> getStoreItem(@QueryParam(value="requestingUserid") int requestingUserId, @PathParam(value="id") int id) throws FusionRestException {
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            String requestingUsername = userBean.getUsernameByUserid(requestingUserId, null);
            if (requestingUsername == null) {
                throw new FusionRestException(FusionRestException.RestException.ERROR, "Unable to find requesting user id ");
            }
            ContentLocal contentBean = (ContentLocal)EJBHomeCache.getLocalObject("ContentLocal", ContentLocalHome.class);
            StoreItemData sid = contentBean.getStoreItem(requestingUsername, id);
            if (sid == null) {
                log.warn((Object)String.format("Failed to retrieve store item id %d", id));
                throw new FusionRestException(FusionRestException.RestException.ERROR, "Unable to get Store Item.");
            }
            return new DataHolder<StoreItemData>(sid);
        }
        catch (FusionRestException fre) {
            throw fre;
        }
        catch (Exception e) {
            log.error((Object)String.format("Failed to retrieve store item id: %d", id), (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.ERROR, e.getMessage());
        }
    }

    @GET
    @Path(value="/category/{id}")
    @Produces(value={"application/json"})
    public DataHolder<StoreCategoryData> getStoreCategory(@PathParam(value="id") int id, @QueryParam(value="requestingUserid") int requestingUserId, @QueryParam(value="sortorder") boolean sortorder) throws FusionRestException {
        try {
            ContentLocal contentBean = (ContentLocal)EJBHomeCache.getLocalObject("ContentLocal", ContentLocalHome.class);
            return new DataHolder<StoreCategoryData>(contentBean.getStoreCategory(requestingUserId, id, sortorder));
        }
        catch (Exception e) {
            log.error((Object)String.format("Failed to retrieve store category id: %d", id), (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.ERROR, e.getMessage());
        }
    }

    @GET
    @Path(value="/categories/{id}")
    @Produces(value={"application/json"})
    public DataHolder<List<StoreCategoryData>> getStoreCategories(@PathParam(value="id") int id, @QueryParam(value="requestingUserid") int requestingUserId, @QueryParam(value="sortorder") boolean sortorder) throws FusionRestException {
        try {
            ContentLocal contentBean = (ContentLocal)EJBHomeCache.getLocalObject("ContentLocal", ContentLocalHome.class);
            List scd = contentBean.getStoreCategories(requestingUserId, id, sortorder);
            return new DataHolder<List<StoreCategoryData>>(scd);
        }
        catch (Exception e) {
            log.error((Object)String.format("Failed to retrieve store category id: %d", id), (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.ERROR, e.getMessage());
        }
    }

    @GET
    @Path(value="/search/items")
    @Produces(value={"application/json"})
    public DataHolder<ListDataWrapper<StoreItemData>> searchStoreItems(@QueryParam(value="requestingUserid") int requestingUserId, @QueryParam(value="query") String query, @QueryParam(value="type") int type, @QueryParam(value="categoryId") Integer categoryId, @QueryParam(value="minPrice") double minPrice, @QueryParam(value="maxPrice") double maxPrice, @QueryParam(value="sortby") String sortby, @QueryParam(value="sortorder") String sortorder, @QueryParam(value="offset") int offset, @QueryParam(value="limit") int limit, @QueryParam(value="featured") boolean featured) throws FusionRestException {
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            String requestingUsername = userBean.getUsernameByUserid(requestingUserId, null);
            ContentLocal contentBean = (ContentLocal)EJBHomeCache.getLocalObject("ContentLocal", ContentLocalHome.class);
            ListDataWrapper sidw = contentBean.searchStoreItems(requestingUsername, query, StoreItemData.TypeEnum.fromValue(type), categoryId, minPrice, maxPrice, sortby, sortorder, offset, limit, featured);
            return new DataHolder<ListDataWrapper<StoreItemData>>(sidw);
        }
        catch (Exception e) {
            log.error((Object)String.format("Failed to search store item id.", new Object[0]));
            throw new FusionRestException(FusionRestException.RestException.ERROR, e.getMessage());
        }
    }
}

