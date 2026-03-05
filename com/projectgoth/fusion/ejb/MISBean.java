/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.projectgoth.hashtag.HashTagAPI
 *  com.projectgoth.hashtag.data.HashTagData
 *  javax.ejb.CreateException
 *  javax.ejb.EJBException
 *  javax.ejb.SessionBean
 *  javax.ejb.SessionContext
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.ejb;

import com.projectgoth.fusion.accesscontrol.AuthenticatedAccessControlData;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.Enums;
import com.projectgoth.fusion.common.LazyLoader;
import com.projectgoth.fusion.common.MemCachedClientWrapper;
import com.projectgoth.fusion.common.MemCachedKeySpaces;
import com.projectgoth.fusion.common.MemCachedKeyUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.data.AlertMessageData;
import com.projectgoth.fusion.data.ApplicationMenuOptionData;
import com.projectgoth.fusion.data.CashReceiptData;
import com.projectgoth.fusion.data.CountryData;
import com.projectgoth.fusion.data.CurrencyData;
import com.projectgoth.fusion.data.FileData;
import com.projectgoth.fusion.data.HandsetData;
import com.projectgoth.fusion.data.HandsetInstructionsData;
import com.projectgoth.fusion.data.HandsetVendorPrefixesData;
import com.projectgoth.fusion.data.MenuData;
import com.projectgoth.fusion.data.PromotedPostData;
import com.projectgoth.fusion.data.ReputationLevelData;
import com.projectgoth.fusion.data.ResellerData;
import com.projectgoth.fusion.data.ScrapbookData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.ejb.EJBIcePrxFinder;
import com.projectgoth.fusion.ejb.FusionEJBException;
import com.projectgoth.fusion.ejb.util.LookupUtil;
import com.projectgoth.fusion.emote.EmoteCommandData;
import com.projectgoth.fusion.interfaces.AccountLocal;
import com.projectgoth.fusion.interfaces.AccountLocalHome;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import com.projectgoth.fusion.interfaces.UserLocal;
import com.projectgoth.fusion.interfaces.UserLocalHome;
import com.projectgoth.fusion.rewardsystem.RewardCentre;
import com.projectgoth.fusion.rewardsystem.triggers.PhotoUploadTrigger;
import com.projectgoth.fusion.slice.EmailUserNotification;
import com.projectgoth.hashtag.HashTagAPI;
import com.projectgoth.hashtag.data.HashTagData;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.sql.DataSource;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MISBean
implements SessionBean {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(MISBean.class));
    private DataSource dataSourceMaster;
    private DataSource dataSourceSlave;
    private static List<CountryData> countries;
    private static List<CurrencyData> currencies;
    private static List<MenuData> menus;
    private static Map<Integer, String> helpTexts;
    private static Map<Integer, String> infoTexts;
    private HashTagAPI hashTagAPI;
    private static long countriesNextUpdate;
    private static long currenciesNextUpdate;
    private static long menusNextUpdate;
    private static long clientTextsNextUpdate;
    private static final Object countriesLock;
    private static final Object currenciesLock;
    private static final Object clientTextsLock;
    private static final Object menusLock;
    private SessionContext context;

    public void setSessionContext(SessionContext newContext) throws EJBException {
        this.context = newContext;
    }

    public void ejbRemove() throws EJBException, RemoteException {
    }

    public void ejbActivate() throws EJBException, RemoteException {
    }

    public void ejbPassivate() throws EJBException, RemoteException {
    }

    public void ejbCreate() throws CreateException {
        try {
            this.dataSourceMaster = LookupUtil.getFusionMasterDataSource();
            this.dataSourceSlave = LookupUtil.getFusionSlaveDataSource();
            SystemProperty.ejbInit(this.dataSourceSlave);
            this.hashTagAPI = new HashTagAPI();
        }
        catch (Exception e) {
            log.error((Object)"Unable to create MIS EJB", (Throwable)e);
            throw new CreateException("Unable to create MIS EJB: " + e.getMessage());
        }
    }

    /*
     * Loose catch block
     */
    public List<ApplicationMenuOptionData> getApplicationMenuOptions(int menuVersionId) throws EJBException {
        LinkedList<ApplicationMenuOptionData> menuOptionList;
        block23: {
            String key = String.valueOf(menuVersionId);
            menuOptionList = (LinkedList<ApplicationMenuOptionData>)MemCachedClientWrapper.get(MemCachedKeySpaces.CommonKeySpace.APPMENU_OPTIONS, key);
            if (menuOptionList == null) {
                Connection connSlave = null;
                Statement ps = null;
                ResultSet rs = null;
                menuOptionList = new LinkedList<ApplicationMenuOptionData>();
                connSlave = this.dataSourceSlave.getConnection();
                String sql = "select * from appmenuoption where id=?";
                ps = connSlave.prepareStatement(sql);
                ps.setInt(1, menuVersionId);
                rs = ps.executeQuery();
                while (rs.next()) {
                    menuOptionList.add(new ApplicationMenuOptionData(rs));
                }
                MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.APPMENU_OPTIONS, key, menuOptionList);
                Object var9_9 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (connSlave != null) {
                        connSlave.close();
                    }
                    break block23;
                }
                catch (SQLException e) {
                    connSlave = null;
                }
                break block23;
                {
                    catch (SQLException e) {
                        throw new EJBException(e.getMessage());
                    }
                }
                catch (Throwable throwable) {
                    Object var9_10 = null;
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                    }
                    catch (SQLException e) {
                        rs = null;
                    }
                    try {
                        if (ps != null) {
                            ps.close();
                        }
                    }
                    catch (SQLException e) {
                        ps = null;
                    }
                    try {
                        if (connSlave != null) {
                            connSlave.close();
                        }
                    }
                    catch (SQLException e) {
                        connSlave = null;
                    }
                    throw throwable;
                }
            }
        }
        return menuOptionList;
    }

    /*
     * Loose catch block
     */
    public int getApplicationMenuVersion(int clientType, short clientVersion, String vasTrackingId) throws EJBException {
        Integer versionId;
        block25: {
            String key;
            if (vasTrackingId == null) {
                vasTrackingId = "";
            }
            if ((versionId = MemCachedClientWrapper.getInt(MemCachedKeySpaces.CommonKeySpace.APPMENU, key = MemCachedKeyUtils.getFullKeyFromStrings(String.valueOf(clientType), String.valueOf(clientVersion), vasTrackingId))) == null) {
                ResultSet rs;
                PreparedStatement ps;
                Connection connSlave;
                block22: {
                    connSlave = null;
                    ps = null;
                    rs = null;
                    connSlave = this.dataSourceSlave.getConnection();
                    String sql = "select max(id) as id from appmenu where clientType=? and minVersion<=? and maxVersion>=? and vasTrackingId=? and status=?";
                    ps = connSlave.prepareStatement(sql);
                    ps.setInt(1, clientType);
                    ps.setShort(2, clientVersion);
                    ps.setShort(3, clientVersion);
                    ps.setString(4, vasTrackingId);
                    ps.setInt(5, 1);
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        versionId = rs.getInt("id");
                        MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.APPMENU, key, versionId);
                        break block22;
                    }
                    versionId = 0;
                }
                Object var11_11 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (connSlave != null) {
                        connSlave.close();
                    }
                    break block25;
                }
                catch (SQLException e) {
                    connSlave = null;
                }
                break block25;
                {
                    catch (SQLException e) {
                        throw new EJBException(e.getMessage());
                    }
                }
                catch (Throwable throwable) {
                    Object var11_12 = null;
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                    }
                    catch (SQLException e) {
                        rs = null;
                    }
                    try {
                        if (ps != null) {
                            ps.close();
                        }
                    }
                    catch (SQLException e) {
                        ps = null;
                    }
                    try {
                        if (connSlave != null) {
                            connSlave.close();
                        }
                    }
                    catch (SQLException e) {
                        connSlave = null;
                    }
                    throw throwable;
                }
            }
        }
        return versionId;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    private void loadClientText() throws EJBException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Object object = clientTextsLock;
        synchronized (object) {
            block29: {
                if (helpTexts == null || infoTexts == null || helpTexts.isEmpty() || infoTexts.isEmpty() || clientTextsNextUpdate <= System.currentTimeMillis()) {
                    if (helpTexts == null) {
                        helpTexts = new HashMap<Integer, String>();
                    }
                    if (infoTexts == null) {
                        infoTexts = new HashMap<Integer, String>();
                    }
                    infoTexts.clear();
                    helpTexts.clear();
                    conn = this.dataSourceSlave.getConnection();
                    stmt = conn.prepareStatement("select * from clienttext");
                    rs = stmt.executeQuery();
                    while (rs.next()) {
                        int type = rs.getInt("Type");
                        if (type == 1) {
                            helpTexts.put(rs.getInt("ID"), rs.getString("Text"));
                            continue;
                        }
                        if (type != 2) continue;
                        infoTexts.put(rs.getInt("ID"), rs.getString("Text"));
                    }
                    clientTextsNextUpdate = System.currentTimeMillis() + SystemProperty.getLong(SystemPropertyEntities.EJBCacheDuration.MIS_CLIENT_TEXT) * 1000L;
                    Object var7_7 = null;
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                    }
                    catch (SQLException e) {
                        rs = null;
                    }
                    try {
                        if (stmt != null) {
                            stmt.close();
                        }
                    }
                    catch (SQLException e) {
                        stmt = null;
                    }
                    try {
                        if (conn != null) {
                            conn.close();
                        }
                        break block29;
                    }
                    catch (SQLException e) {
                        conn = null;
                    }
                    break block29;
                    {
                        catch (SQLException e) {
                            throw new EJBException("Unable to load client text: " + e.getMessage());
                        }
                    }
                    catch (Throwable throwable) {
                        Object var7_8 = null;
                        try {
                            if (rs != null) {
                                rs.close();
                            }
                        }
                        catch (SQLException e) {
                            rs = null;
                        }
                        try {
                            if (stmt != null) {
                                stmt.close();
                            }
                        }
                        catch (SQLException e) {
                            stmt = null;
                        }
                        try {
                            if (conn != null) {
                                conn.close();
                            }
                        }
                        catch (SQLException e) {
                            conn = null;
                        }
                        throw throwable;
                    }
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    private List<CountryData> getCountriesList() throws EJBException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Object object = countriesLock;
        synchronized (object) {
            block29: {
                if (countries == null || countries.isEmpty() || countriesNextUpdate <= System.currentTimeMillis()) {
                    if (countries == null) {
                        countries = new LinkedList<CountryData>();
                    } else {
                        countries.clear();
                    }
                    conn = this.dataSourceSlave.getConnection();
                    stmt = conn.prepareStatement("select * from country order by name");
                    rs = stmt.executeQuery();
                    while (rs.next()) {
                        countries.add(new CountryData(rs));
                    }
                    if (countries.size() == 0) {
                        throw new EJBException("Unable to load country details. No records found");
                    }
                    countriesNextUpdate = System.currentTimeMillis() + SystemProperty.getLong(SystemPropertyEntities.EJBCacheDuration.MIS_COUNTRIES) * 1000L;
                    Object var7_5 = null;
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                    }
                    catch (SQLException e) {
                        rs = null;
                    }
                    try {
                        if (stmt != null) {
                            stmt.close();
                        }
                    }
                    catch (SQLException e) {
                        stmt = null;
                    }
                    try {
                        if (conn != null) {
                            conn.close();
                        }
                        break block29;
                    }
                    catch (SQLException e) {
                        conn = null;
                    }
                    break block29;
                    {
                        catch (SQLException e) {
                            throw new EJBException("Unable to load country details: " + e.getMessage());
                        }
                    }
                    catch (Throwable throwable) {
                        Object var7_6 = null;
                        try {
                            if (rs != null) {
                                rs.close();
                            }
                        }
                        catch (SQLException e) {
                            rs = null;
                        }
                        try {
                            if (stmt != null) {
                                stmt.close();
                            }
                        }
                        catch (SQLException e) {
                            stmt = null;
                        }
                        try {
                            if (conn != null) {
                                conn.close();
                            }
                        }
                        catch (SQLException e) {
                            conn = null;
                        }
                        throw throwable;
                    }
                }
            }
        }
        return countries;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    private List<CurrencyData> getCurrencyList() throws EJBException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Object object = currenciesLock;
        synchronized (object) {
            block29: {
                if (currencies == null || currencies.isEmpty() || currenciesNextUpdate <= System.currentTimeMillis()) {
                    if (currencies == null) {
                        currencies = new LinkedList<CurrencyData>();
                    } else {
                        currencies.clear();
                    }
                    conn = this.dataSourceSlave.getConnection();
                    stmt = conn.prepareStatement("select * from currency order by Code");
                    rs = stmt.executeQuery();
                    while (rs.next()) {
                        currencies.add(new CurrencyData(rs));
                    }
                    if (currencies.size() == 0) {
                        throw new EJBException("Unable to load currency details. No records found");
                    }
                    currenciesNextUpdate = System.currentTimeMillis() + SystemProperty.getLong(SystemPropertyEntities.EJBCacheDuration.MIS_CURRENCIES) * 1000L;
                    Object var7_5 = null;
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                    }
                    catch (SQLException e) {
                        rs = null;
                    }
                    try {
                        if (stmt != null) {
                            stmt.close();
                        }
                    }
                    catch (SQLException e) {
                        stmt = null;
                    }
                    try {
                        if (conn != null) {
                            conn.close();
                        }
                        break block29;
                    }
                    catch (SQLException e) {
                        conn = null;
                    }
                    break block29;
                    {
                        catch (SQLException e) {
                            throw new EJBException("Unable to load currency details: " + e.getMessage());
                        }
                    }
                    catch (Throwable throwable) {
                        Object var7_6 = null;
                        try {
                            if (rs != null) {
                                rs.close();
                            }
                        }
                        catch (SQLException e) {
                            rs = null;
                        }
                        try {
                            if (stmt != null) {
                                stmt.close();
                            }
                        }
                        catch (SQLException e) {
                            stmt = null;
                        }
                        try {
                            if (conn != null) {
                                conn.close();
                            }
                        }
                        catch (SQLException e) {
                            conn = null;
                        }
                        throw throwable;
                    }
                }
            }
        }
        return currencies;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    private List<MenuData> getMenuList() throws EJBException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Object object = menusLock;
        synchronized (object) {
            block29: {
                if (menus == null || menus.isEmpty() || menusNextUpdate <= System.currentTimeMillis()) {
                    if (menus == null) {
                        menus = new LinkedList<MenuData>();
                    } else {
                        menus.clear();
                    }
                    conn = this.dataSourceSlave.getConnection();
                    stmt = conn.prepareStatement("select * from menu");
                    rs = stmt.executeQuery();
                    while (rs.next()) {
                        menus.add(new MenuData(rs));
                    }
                    if (menus.size() == 0) {
                        throw new EJBException("Unable to load menu items. No records found");
                    }
                    menusNextUpdate = System.currentTimeMillis() + SystemProperty.getLong(SystemPropertyEntities.EJBCacheDuration.MIS_MIDLET_MENU_ITEMS) * 1000L;
                    Object var7_5 = null;
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                    }
                    catch (SQLException e) {
                        rs = null;
                    }
                    try {
                        if (stmt != null) {
                            stmt.close();
                        }
                    }
                    catch (SQLException e) {
                        stmt = null;
                    }
                    try {
                        if (conn != null) {
                            conn.close();
                        }
                        break block29;
                    }
                    catch (SQLException e) {
                        conn = null;
                    }
                    break block29;
                    {
                        catch (SQLException e) {
                            throw new EJBException("Unable to load menus: " + e.getMessage());
                        }
                    }
                    catch (Throwable throwable) {
                        Object var7_6 = null;
                        try {
                            if (rs != null) {
                                rs.close();
                            }
                        }
                        catch (SQLException e) {
                            rs = null;
                        }
                        try {
                            if (stmt != null) {
                                stmt.close();
                            }
                        }
                        catch (SQLException e) {
                            stmt = null;
                        }
                        try {
                            if (conn != null) {
                                conn.close();
                            }
                        }
                        catch (SQLException e) {
                            conn = null;
                        }
                        throw throwable;
                    }
                }
            }
        }
        return menus;
    }

    public void resetCachedSystemProperties() {
        SystemProperty.resetCachedProperties();
    }

    /*
     * Loose catch block
     */
    public Map<String, String> getSystemProperties() throws EJBException {
        Connection connSlave = null;
        Statement ps = null;
        ResultSet rs = null;
        HashMap<String, String> properties = (HashMap<String, String>)MemCachedClientWrapper.get(MemCachedKeySpaces.CommonKeySpace.SYSTEM_PROPERTY, "");
        if (properties == null) {
            properties = new HashMap<String, String>();
            connSlave = this.dataSourceSlave.getConnection();
            ps = connSlave.prepareStatement("select * from system");
            rs = ps.executeQuery();
            while (rs.next()) {
                properties.put(rs.getString("propertyname"), rs.getString("propertyvalue"));
            }
            MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.SYSTEM_PROPERTY, "", properties);
        }
        HashMap<String, String> hashMap = properties;
        Object var7_7 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (connSlave != null) {
                connSlave.close();
            }
        }
        catch (SQLException e2) {
            connSlave = null;
        }
        return hashMap;
        catch (SQLException e) {
            try {
                throw new EJBException("Unable to load system properties: " + e.getMessage());
            }
            catch (Throwable throwable) {
                Object var7_8 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (connSlave != null) {
                        connSlave.close();
                    }
                }
                catch (SQLException e2) {
                    connSlave = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public void updateSystemProperty(String propertyName, String value) throws EJBException {
        block24: {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            if (StringUtil.isBlank(propertyName)) {
                throw new EJBException("Null system property provided");
            }
            if (value == null) {
                throw new EJBException("Null system property value provided");
            }
            propertyName = propertyName.trim();
            value = value.trim();
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("insert into system (propertyname, propertyvalue) VALUES(?,?) on duplicate key update propertyvalue=?");
            ps.setString(1, propertyName);
            ps.setString(2, value);
            ps.setString(3, value);
            if (ps.executeUpdate() < 1) {
                throw new EJBException("Unable to update system property");
            }
            Object var8_6 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block24;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block24;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var8_7 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    public String getHelpText(int helpID) throws EJBException {
        this.loadClientText();
        return helpTexts.get(helpID);
    }

    public String getInfoText(int infoID) throws EJBException {
        this.loadClientText();
        return infoTexts.get(infoID);
    }

    public List<CountryData> getCountries() throws EJBException {
        return this.getCountriesList();
    }

    /*
     * Loose catch block
     */
    private static List<CountryData> getCountriesSupportedHashtag(DataSource dataSource) throws EJBException {
        ArrayList<CountryData> countryData;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<ArrayList<CountryData>> result = new ArrayList<ArrayList<CountryData>>();
        conn = dataSource.getConnection();
        ps = conn.prepareStatement("select * from country c inner join countrysupportedhashtag csh on csh.countryid=c.id where enabled=1");
        rs = ps.executeQuery();
        while (rs.next()) {
            countryData = new CountryData(rs);
            result.add(countryData);
        }
        countryData = result;
        Object var7_7 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return countryData;
        catch (Exception e) {
            try {
                throw new EJBException(e.getMessage(), e);
            }
            catch (Throwable throwable) {
                Object var7_8 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    public List<CountryData> getCountriesSupportedHashtag() throws EJBException {
        return (List)SingletonHolder.COUNTRIES_SUPPORTED_HASHTAG_LOADER.getValue();
    }

    public List<CurrencyData> getCurrencies() throws EJBException {
        return this.getCurrencyList();
    }

    public List<MenuData> getMenus(int countryID, short clientVersion, int clientType) throws EJBException {
        ArrayList<MenuData> list = new ArrayList<MenuData>();
        for (MenuData menu : this.getMenuList()) {
            if (clientType != menu.clientType || clientVersion < menu.minVersion || clientVersion > menu.maxVersion || menu.countryID != null && menu.countryID != countryID) continue;
            list.add(menu);
        }
        return list;
    }

    public MenuData getMenu(int menuId) throws EJBException {
        for (MenuData menu : this.getMenuList()) {
            if (menu.id != menuId) continue;
            return menu;
        }
        return null;
    }

    public CountryData getCountry(int countryID) throws EJBException {
        for (CountryData country : this.getCountriesList()) {
            if (country.id != countryID) continue;
            return country;
        }
        return null;
    }

    public CountryData getCountryByISOCode(String isoCountryCode) throws EJBException {
        if (!StringUtil.isBlank(isoCountryCode)) {
            for (CountryData country : this.getCountriesList()) {
                if (!isoCountryCode.equalsIgnoreCase(country.isoCountryCode)) continue;
                return country;
            }
        }
        return null;
    }

    /*
     * Loose catch block
     */
    public CountryData getCountryByLocation(int locationID) throws EJBException {
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block26: {
            CountryData countryData;
            conn = null;
            ps = null;
            rs = null;
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select * from country c left join location l on l.countryid=c.id where l.id=? ");
            ps.setInt(1, locationID);
            rs = ps.executeQuery();
            if (!rs.next()) break block26;
            CountryData countryData2 = countryData = new CountryData(rs);
            Object var8_9 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e2) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e2) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (SQLException e2) {
                conn = null;
            }
            return countryData2;
        }
        CountryData countryData = null;
        Object var8_10 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return countryData;
        catch (Exception e) {
            try {
                throw new EJBException(e.getMessage(), e);
            }
            catch (Throwable throwable) {
                Object var8_11 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    public CountryData getCountryByIDDCode(int iddCode, String mobilePhone) throws EJBException {
        if (iddCode == 1) {
            if (mobilePhone.length() >= 4 && "1204;1226;1250;1289;1306;1403;1416;1418;1438;1450;1506;1514;1519;1604;1613;1647;1705;1709;1778;1780;1807;1819;1867;1902;1905".indexOf(mobilePhone.substring(0, 4)) >= 0 || mobilePhone.length() >= 7 && "1204131;1226131;1250131;1289131;1306131;1403131;1416131;1418131;1438131;1450131;1506131;1514131;1519131;1604131;1613131;1647131;1705131;1709131;1778131;1780131;1807131;1819131;1867131;1902131;1905131".indexOf(mobilePhone.substring(0, 7)) >= 0) {
                return this.getCountry(40);
            }
            return this.getCountry(231);
        }
        if (iddCode == 7) {
            if (mobilePhone.length() >= 3 && "731;732".indexOf(mobilePhone.substring(0, 3)) >= 0 || mobilePhone.length() >= 4 && "7333;7570;7571;7573;7700;7701;7702;7705;7707;7777;7336".indexOf(mobilePhone.substring(0, 4)) >= 0 || mobilePhone.length() >= 5 && "73272;73172;73212".indexOf(mobilePhone.substring(0, 5)) >= 0) {
                return this.getCountry(116);
            }
            return this.getCountry(185);
        }
        if (iddCode == 39) {
            return this.getCountry(112);
        }
        if (iddCode == 269) {
            return this.getCountry(51);
        }
        for (CountryData country : this.getCountriesList()) {
            if (country.iddCode != iddCode) continue;
            return country;
        }
        return null;
    }

    /*
     * Loose catch block
     */
    public CountryData getCountryFromIPNumber(Double ipNumber) throws EJBException {
        String isoCountryCode;
        block33: {
            ResultSet rs;
            PreparedStatement ps;
            Connection conn;
            block27: {
                conn = null;
                ps = null;
                rs = null;
                conn = this.dataSourceSlave.getConnection();
                ps = conn.prepareStatement("select isocountrycode from countrytoip where beginIPNum <= ? and endIPNum >= ?");
                ps.setDouble(1, ipNumber);
                ps.setDouble(2, ipNumber);
                rs = ps.executeQuery();
                if (rs.next()) {
                    isoCountryCode = rs.getString(1);
                    break block27;
                }
                CountryData countryData = null;
                Object var8_9 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                return countryData;
            }
            try {
                Object var8_10 = null;
            }
            catch (Throwable throwable) {
                Object var8_11 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block33;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block33;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
        }
        for (CountryData country : this.getCountriesList()) {
            if (country.isoCountryCode == null || !country.isoCountryCode.equalsIgnoreCase(isoCountryCode)) continue;
            return country;
        }
        return null;
    }

    public double getVoiceRate(int countryIDFrom, int typeFrom, int countryIDTo, int typeTo) throws EJBException, Exception {
        double rate;
        block10: {
            rate = 0.0;
            CountryData countryFrom = this.getCountry(countryIDFrom);
            CountryData countryTo = this.getCountry(countryIDTo);
            try {
                if (countryFrom == null || countryTo == null) {
                    throw new Exception("Country selected not recognised.");
                }
                if (typeFrom == 1 && countryFrom.callRate != null) {
                    rate += countryFrom.callRate.doubleValue();
                } else if (typeFrom == 2 && countryFrom.mobileRate != null) {
                    rate += countryFrom.mobileRate.doubleValue();
                } else {
                    throw new Exception("There is currently no call route from your source number.");
                }
                if (typeTo == 1 && countryTo.callRate != null) {
                    rate += countryTo.callRate.doubleValue();
                    break block10;
                }
                if (typeTo == 2 && countryTo.mobileRate != null) {
                    rate += countryTo.mobileRate.doubleValue();
                    break block10;
                }
                throw new Exception("There is currently no call route to your destination number.");
            }
            catch (SQLException e) {
                throw new EJBException(e.getMessage());
            }
            catch (Exception ex) {
                throw new EJBException(ex.getMessage());
            }
        }
        return rate;
    }

    /*
     * Loose catch block
     */
    public CurrencyData getCurrency(String code) throws EJBException {
        Connection conn = null;
        Statement ps = null;
        ResultSet rs = null;
        CurrencyData currencyData = (CurrencyData)MemCachedClientWrapper.get(MemCachedKeySpaces.CommonKeySpace.CURRENCY, code);
        if (currencyData == null) {
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("select * from currency where code = ?");
            ps.setString(1, code);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new EJBException("Unable to find the currency code " + code);
            }
            currencyData = new CurrencyData(rs);
            MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.CURRENCY, code, currencyData);
        }
        CurrencyData currencyData2 = currencyData;
        Object var8_8 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return currencyData2;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var8_9 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public void setCurrency(CurrencyData currencyData) throws EJBException {
        block24: {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            currencyData.lastUpdated = new java.util.Date();
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("select * from currency where code = ?");
            ps.setString(1, currencyData.code);
            rs = ps.executeQuery();
            if (rs.next()) {
                rs.close();
                ps.close();
                ps = conn.prepareStatement("update currency set name = ?, exchangerate = ?, lastupdated = ? where code = ?");
                ps.setString(1, currencyData.name);
                ps.setObject(2, currencyData.exchangeRate);
                ps.setTimestamp(3, new Timestamp(currencyData.lastUpdated.getTime()));
                ps.setString(4, currencyData.code);
                ps.executeUpdate();
            } else {
                rs.close();
                ps.close();
                ps = conn.prepareStatement("insert into currency (code, name, exchangerate, lastupdated) values (?,?,?,?)");
                ps.setString(1, currencyData.code);
                ps.setString(2, currencyData.name);
                ps.setObject(3, currencyData.exchangeRate);
                ps.setTimestamp(4, new Timestamp(currencyData.lastUpdated.getTime()));
                ps.executeUpdate();
            }
            rs.close();
            ps.close();
            ps = conn.prepareStatement("select * from exchangeratehistory where code = ? and datecreated = ?");
            ps.setString(1, currencyData.code);
            ps.setDate(2, new Date(currencyData.lastUpdated.getTime()));
            rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                ps = conn.prepareStatement("insert into exchangeratehistory (code, datecreated, exchangerate) values (?,?,?)");
                ps.setString(1, currencyData.code);
                ps.setTimestamp(2, new Timestamp(currencyData.lastUpdated.getTime()));
                ps.setDouble(3, currencyData.exchangeRate);
                ps.executeUpdate();
            }
            MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.CURRENCY, currencyData.code);
            Object var7_5 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block24;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block24;
            {
                catch (SQLException e) {
                    throw new EJBException("Unable to set currency: " + e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var7_6 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    public double getSMSRate(String currency) throws EJBException, Exception {
        try {
            if (currency == null) {
                throw new Exception("Please select a currency.");
            }
            for (CountryData country : this.getCountriesList()) {
                if (!currency.equals(country.currency)) continue;
                return country.smsCost;
            }
            throw new Exception("There is no SMS rate for the chosen currency.");
        }
        catch (SQLException e) {
            throw new EJBException(e.getMessage());
        }
        catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }
    }

    public double getPremiumCost(int country) throws EJBException, Exception {
        double cost = 0.0;
        CountryData countryData = this.getCountry(country);
        try {
            if (countryData == null) {
                throw new Exception("Country selected not recognised.");
            }
            if (countryData.premiumSMSAmount == null) {
                throw new Exception("There is currently no premium SMS recharge option in the country specified.");
            }
            cost = countryData.premiumSMSAmount;
        }
        catch (SQLException e) {
            throw new EJBException(e.getMessage());
        }
        catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }
        return cost;
    }

    public double getPremiumFee(int country) throws EJBException, Exception {
        double cost = 0.0;
        CountryData countryData = this.getCountry(country);
        try {
            if (countryData == null) {
                throw new Exception("Country selected not recognised.");
            }
            if (countryData.premiumSMSFee == null) {
                throw new Exception("There is currently no premium SMS recharge option in the country specified.");
            }
            cost = countryData.premiumSMSFee;
        }
        catch (SQLException e) {
            throw new EJBException(e.getMessage());
        }
        catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }
        return cost;
    }

    public String getCountryCurrency(int country) throws EJBException, Exception {
        String currency = "";
        CountryData countryData = this.getCountry(country);
        try {
            if (countryData == null) {
                throw new Exception("Country selected not recognised.");
            }
            if (countryData.currency == null) {
                throw new Exception("There is currently no currency for this country.");
            }
            currency = countryData.currency;
        }
        catch (SQLException e) {
            throw new EJBException(e.getMessage());
        }
        catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }
        return currency;
    }

    /*
     * Loose catch block
     */
    public void addToMailList(String email) throws EJBException {
        block16: {
            Connection conn = null;
            PreparedStatement ps = null;
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("insert into mailinglist (email) values (?)");
            ps.setString(1, email);
            if (ps.executeUpdate() != 1) {
                throw new EJBException("Failed to insert email to mailing list for " + email);
            }
            Object var6_4 = null;
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block16;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block16;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var6_5 = null;
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public boolean isVASBuild(String userAgent) throws EJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Integer isVASBuild = MemCachedClientWrapper.getInt(MemCachedKeySpaces.CommonKeySpace.VAS_BUILD, userAgent);
        if (isVASBuild == null) {
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select pb.id from partnerbuild pb, partneragreementbuild pab, partneragreement pa where pb.id = pab.partnerbuildid and pab.partneragreementid = pa.id and now() >= pa.startdate and now() <= pa.enddate and pb.useragent = ?");
            ps.setString(1, userAgent);
            rs = ps.executeQuery();
            isVASBuild = rs.next() ? 1 : 0;
            MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.VAS_BUILD, userAgent, isVASBuild);
        }
        boolean bl = isVASBuild == 1;
        Object var8_8 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return bl;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var8_9 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public CashReceiptData getCashReceiptData(int id) {
        CashReceiptData cashreceiptData;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        conn = this.dataSourceSlave.getConnection();
        ps = conn.prepareStatement("SELECT * FROM cashreceipt where id = ?");
        ps.setInt(1, id);
        rs = ps.executeQuery();
        if (!rs.next()) {
            throw new SQLException("Failed to find cashreceipt id:" + id);
        }
        CashReceiptData cashReceiptData = cashreceiptData = new CashReceiptData(rs);
        Object var8_9 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return cashReceiptData;
        catch (Exception e) {
            try {
                UUID errorID = UUID.randomUUID();
                log.error((Object)("[" + errorID + "] Unable to get cash receipt  from table: " + e.getMessage()));
                throw new EJBException("[" + errorID + "] Unable to get cash receipt  from table", e);
            }
            catch (Throwable throwable) {
                Object var8_10 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    public CashReceiptData createCashReceipt(CashReceiptData cashReceiptData, AccountEntrySourceData accountEntrySourceData) throws EJBException {
        if (SystemProperty.getBool(SystemPropertyEntities.CashReceipt.ENABLE_CASH_RECEIPT_ID_WITH_NEW_ID_RETRIEVAL)) {
            return this.createCashReceipt_NewIDRetrieval(cashReceiptData, accountEntrySourceData);
        }
        return this.createCashReceipt_OldIDRetrieval(cashReceiptData, accountEntrySourceData);
    }

    /*
     * Loose catch block
     */
    private CashReceiptData createCashReceipt_OldIDRetrieval(CashReceiptData cashReceiptData, AccountEntrySourceData accountEntrySourceData) throws EJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        log.info((Object)("createCashReceipt for data:[" + cashReceiptData + "]"));
        conn = this.dataSourceMaster.getConnection();
        cashReceiptData.dateCreated = new java.util.Date();
        ps = conn.prepareStatement("select providerTransactionID from cashReceipt where providerTransactionID = ? AND status NOT IN (2,3) ");
        ps.setString(1, cashReceiptData.providerTransactionID);
        rs = ps.executeQuery();
        if (rs.next()) {
            throw new EJBException("Failed to add new cash receipt: That Transaction Reference No already exists!");
        }
        ps.close();
        String statement = "insert into cashreceipt ";
        statement = statement + "(datecreated, EnteredBy, datereceived, amountSent, amountReceived, amountCredited, type, MatchedBy, datematched, status, providerTransactionID, senderUsername, comments, paymentDetails,mobilePhone,referenceCashReceiptID,bonus) ";
        statement = statement + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        ps = conn.prepareStatement(statement);
        ps.setTimestamp(1, new Timestamp(cashReceiptData.dateCreated.getTime()));
        ps.setString(2, cashReceiptData.enteredBy);
        ps.setTimestamp(3, new Timestamp(cashReceiptData.dateReceived.getTime()));
        ps.setDouble(4, cashReceiptData.amountSent);
        ps.setDouble(5, cashReceiptData.amountReceived);
        ps.setObject(6, cashReceiptData.amountCredited);
        ps.setObject(7, cashReceiptData.type.value());
        ps.setString(8, null);
        ps.setTimestamp(9, null);
        ps.setInt(10, 0);
        ps.setString(11, cashReceiptData.providerTransactionID);
        ps.setString(12, cashReceiptData.senderUsername);
        ps.setString(13, cashReceiptData.comments);
        ps.setString(14, cashReceiptData.paymentDetails);
        ps.setString(15, cashReceiptData.mobilePhone);
        ps.setObject(16, cashReceiptData.referenceCashReceiptId);
        ps.setObject(17, cashReceiptData.bonus);
        System.out.println(ps.toString());
        if (ps.executeUpdate() != 1) {
            throw new EJBException("Failed to add a new cash receipt: Database error.");
        }
        ps.close();
        statement = "select id from cashreceipt ";
        statement = statement + "where providerTransactionID = ?";
        ps = conn.prepareStatement(statement);
        ps.setString(1, cashReceiptData.providerTransactionID);
        rs = ps.executeQuery();
        rs.next();
        cashReceiptData.id = rs.getInt("ID");
        if (SystemProperty.getBool(SystemPropertyEntities.CashReceipt.MATCH_UPON_CREATION_ENABLED) && cashReceiptData.senderUsername != null) {
            try {
                cashReceiptData.matchedBy = cashReceiptData.enteredBy;
                this.matchCashReceipt(cashReceiptData, accountEntrySourceData);
            }
            catch (Exception e) {
                throw new EJBException(e.getMessage(), e);
            }
        }
        CashReceiptData cashReceiptData2 = cashReceiptData;
        Object var9_10 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return cashReceiptData2;
        catch (SQLException e) {
            try {
                throw new EJBException("Failed to add a new cash receipt: Most likely either the user entered does not exist or you have entered a duplicate reference ID!" + e, (Exception)e);
            }
            catch (Throwable throwable) {
                Object var9_11 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private CashReceiptData createCashReceipt_NewIDRetrieval(CashReceiptData cashReceiptData, AccountEntrySourceData accountEntrySourceData) throws EJBException {
        log.info((Object)("createCashReceipt for data:[" + cashReceiptData + "]"));
        try {
            Connection conn = this.dataSourceMaster.getConnection();
            try {
                Timestamp now = new Timestamp(System.currentTimeMillis());
                cashReceiptData.dateCreated = now;
                cashReceiptData.status = CashReceiptData.StatusEnum.UNMATCHED;
                cashReceiptData.matchedBy = null;
                cashReceiptData.dateMatched = null;
                PreparedStatement ps = conn.prepareStatement("select providerTransactionID from cashReceipt where providerTransactionID = ? AND status NOT IN (2,3) ");
                try {
                    ps.setString(1, cashReceiptData.providerTransactionID);
                    ResultSet rs = ps.executeQuery();
                    try {
                        if (rs.next()) {
                            throw new EJBException("Failed to add new cash receipt: That Transaction Reference No already exists!");
                        }
                        Object var8_9 = null;
                    }
                    catch (Throwable throwable) {
                        Object var8_10 = null;
                        rs.close();
                        throw throwable;
                    }
                    rs.close();
                    Object var10_13 = null;
                }
                catch (Throwable throwable) {
                    Object var10_14 = null;
                    ps.close();
                    throw throwable;
                }
                ps.close();
                String statement = "insert into cashreceipt (datecreated, EnteredBy, datereceived, amountSent, amountReceived, amountCredited, type, MatchedBy, datematched, status, providerTransactionID, senderUsername, comments, paymentDetails,mobilePhone,referenceCashReceiptID,bonus) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                PreparedStatement ps2 = conn.prepareStatement("insert into cashreceipt (datecreated, EnteredBy, datereceived, amountSent, amountReceived, amountCredited, type, MatchedBy, datematched, status, providerTransactionID, senderUsername, comments, paymentDetails,mobilePhone,referenceCashReceiptID,bonus) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", 1);
                try {
                    ps2.setTimestamp(1, now);
                    ps2.setString(2, cashReceiptData.enteredBy);
                    ps2.setTimestamp(3, new Timestamp(cashReceiptData.dateReceived.getTime()));
                    ps2.setDouble(4, cashReceiptData.amountSent);
                    ps2.setDouble(5, cashReceiptData.amountReceived);
                    ps2.setObject(6, cashReceiptData.amountCredited);
                    ps2.setObject(7, cashReceiptData.type.value());
                    ps2.setString(8, null);
                    ps2.setTimestamp(9, null);
                    ps2.setInt(10, cashReceiptData.status.value());
                    ps2.setString(11, cashReceiptData.providerTransactionID);
                    ps2.setString(12, cashReceiptData.senderUsername);
                    ps2.setString(13, cashReceiptData.comments);
                    ps2.setString(14, cashReceiptData.paymentDetails);
                    ps2.setString(15, cashReceiptData.mobilePhone);
                    ps2.setObject(16, cashReceiptData.referenceCashReceiptId);
                    ps2.setObject(17, cashReceiptData.bonus);
                    if (log.isDebugEnabled()) {
                        log.debug((Object)ps2.toString());
                    }
                    if (ps2.executeUpdate() != 1) {
                        throw new EJBException("Failed to add a new cash receipt: Database error.");
                    }
                    ResultSet rs = ps2.getGeneratedKeys();
                    try {
                        if (!rs.next()) {
                            throw new EJBException("Failed to retrieve new cashreceipt id. Database error.");
                        }
                        cashReceiptData.id = rs.getInt(1);
                        Object var12_16 = null;
                    }
                    catch (Throwable throwable) {
                        Object var12_17 = null;
                        rs.close();
                        throw throwable;
                    }
                    rs.close();
                    Object var14_19 = null;
                }
                catch (Throwable throwable) {
                    Object var14_20 = null;
                    ps2.close();
                    throw throwable;
                }
                ps2.close();
                Object var16_22 = null;
            }
            catch (Throwable throwable) {
                Object var16_23 = null;
                conn.close();
                throw throwable;
            }
            conn.close();
            if (SystemProperty.getBool(SystemPropertyEntities.CashReceipt.MATCH_UPON_CREATION_ENABLED) && cashReceiptData.senderUsername != null) {
                try {
                    cashReceiptData.matchedBy = cashReceiptData.enteredBy;
                    cashReceiptData.dateMatched = new Timestamp(System.currentTimeMillis());
                    this.matchCashReceipt(cashReceiptData, accountEntrySourceData);
                }
                catch (Exception e) {
                    throw new EJBException(e.getMessage(), e);
                }
            }
            return cashReceiptData;
        }
        catch (SQLException e) {
            throw new EJBException("Failed to add a new cash receipt: Most likely either the user entered does not exist or you have entered a duplicate reference ID!" + e, (Exception)e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void matchCashReceipt(CashReceiptData cashReceiptData, AccountEntrySourceData accountEntrySourceData) throws EJBException {
        log.info((Object)("matchCashReceipt for data:[" + cashReceiptData + "]"));
        try {
            Connection conn = this.dataSourceMaster.getConnection();
            try {
                String senderusername;
                double amountCredited;
                double amountSent;
                int updatedCount;
                boolean validateCashReceiptStatusBeforeMatch;
                block18: {
                    String statement;
                    long now = System.currentTimeMillis();
                    cashReceiptData.dateMatched = new Timestamp(now);
                    cashReceiptData.status = CashReceiptData.StatusEnum.MATCHED;
                    validateCashReceiptStatusBeforeMatch = SystemProperty.getBool(SystemPropertyEntities.CashReceipt.VALIDATE_CASH_RECEIPT_STATUS_BEFORE_MATCH);
                    if (validateCashReceiptStatusBeforeMatch) {
                        statement = "update cashreceipt set  MatchedBy = ?, datematched = ?, status = ?,  comments = ?  where ID = ? and status = ?";
                        PreparedStatement ps = conn.prepareStatement("update cashreceipt set  MatchedBy = ?, datematched = ?, status = ?,  comments = ?  where ID = ? and status = ?");
                        try {
                            ps.setString(1, cashReceiptData.matchedBy);
                            ps.setTimestamp(2, new Timestamp(now));
                            ps.setInt(3, cashReceiptData.status.value());
                            ps.setString(4, cashReceiptData.comments);
                            ps.setInt(5, cashReceiptData.id);
                            ps.setInt(6, CashReceiptData.StatusEnum.UNMATCHED.value());
                            updatedCount = ps.executeUpdate();
                            Object var11_14 = null;
                        }
                        catch (Throwable throwable) {
                            Object var11_15 = null;
                            ps.close();
                            throw throwable;
                        }
                        ps.close();
                        {
                            break block18;
                        }
                    }
                    statement = "update cashreceipt set  MatchedBy = ?, datematched = ?, status = ?,  comments = ?  where ID = ?";
                    PreparedStatement ps = conn.prepareStatement("update cashreceipt set  MatchedBy = ?, datematched = ?, status = ?,  comments = ?  where ID = ?");
                    try {
                        ps.setString(1, cashReceiptData.matchedBy);
                        ps.setTimestamp(2, new Timestamp(now));
                        ps.setInt(3, cashReceiptData.status.value());
                        ps.setString(4, cashReceiptData.comments);
                        ps.setInt(5, cashReceiptData.id);
                        updatedCount = ps.executeUpdate();
                        Object var13_18 = null;
                    }
                    catch (Throwable throwable) {
                        Object var13_19 = null;
                        ps.close();
                        throw throwable;
                    }
                    ps.close();
                    {
                    }
                }
                if (updatedCount == 0) {
                    throw new EJBException("Failed to update cash receipt: No rows matched the criteria for update. Cash receiptId:[" + cashReceiptData.id + "].validateCashReceiptStatusBeforeMatch=[" + validateCashReceiptStatusBeforeMatch + "]");
                }
                if (updatedCount > 1) {
                    throw new EJBException("Too many rows to update.Cash receiptId:[" + cashReceiptData.id + "]");
                }
                PreparedStatement ps = conn.prepareStatement("select senderusername,amountsent,amountcredited from cashreceipt where id=?");
                try {
                    ps.setInt(1, cashReceiptData.id);
                    ResultSet rs = ps.executeQuery();
                    try {
                        if (!rs.next()) {
                            throw new EJBException("Cash receipt id [" + cashReceiptData.id + "] does not exist");
                        }
                        amountSent = rs.getDouble("amountsent");
                        amountCredited = rs.getDouble("amountcredited");
                        senderusername = rs.getString("senderusername");
                        Object var16_23 = null;
                    }
                    catch (Throwable throwable) {
                        Object var16_24 = null;
                        rs.close();
                        throw throwable;
                    }
                    rs.close();
                    Object var18_26 = null;
                }
                catch (Throwable throwable) {
                    Object var18_27 = null;
                    ps.close();
                    throw throwable;
                }
                ps.close();
                AccountLocal accountBean = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
                accountBean.creditAndNotifyUser(senderusername, amountSent, amountCredited, Integer.toString(cashReceiptData.id), accountEntrySourceData, true);
                Object var20_29 = null;
            }
            catch (Throwable throwable) {
                Object var20_30 = null;
                conn.close();
                throw throwable;
            }
            conn.close();
            {
            }
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (SQLException e) {
            throw new EJBException("Failed to match cash receipt: Database error! Cash Receipt Id:[" + cashReceiptData.id + "]");
        }
        catch (Exception e) {
            throw new EJBException("Failed to match cash receipt: Cash Receipt Id:[" + cashReceiptData.id + "] Internal error:[" + e + "]", e);
        }
    }

    /*
     * Loose catch block
     */
    public void deleteCashReceipt(String providerTransactionID) throws EJBException {
        block22: {
            Connection conn = null;
            Statement ps = null;
            ResultSet rs = null;
            conn = this.dataSourceMaster.getConnection();
            String statement = "update cashreceipt set ";
            statement = statement + "status = ? ";
            statement = statement + "where providerTransactionID = ?";
            ps = conn.prepareStatement(statement);
            ps.setObject(1, CashReceiptData.StatusEnum.DELETED.value());
            ps.setString(2, providerTransactionID);
            System.out.println("AND THE CASH RECEIPT OBJECT READS: " + CashReceiptData.StatusEnum.DELETED.value());
            if (ps.executeUpdate() != 1) {
                throw new EJBException("Failed to delete cash receipt: Database error.");
            }
            Object var7_7 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block22;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block22;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var7_8 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public void logMISLogin(String misUsername, String ipAddress, boolean loginSuccessFul) throws EJBException {
        block24: {
            Connection conn = null;
            Statement ps = null;
            ResultSet rs = null;
            conn = this.dataSourceMaster.getConnection();
            if (misUsername.length() < 1) {
                throw new EJBException("You must specify the MIS Username");
            }
            if (ipAddress.length() < 1) {
                throw new EJBException("You must specify an IP address");
            }
            ps = conn.prepareStatement("insert into stafflogin (staffusername, ipaddress, datecreated, loginsuccessful) VALUES (?,?,?,?)");
            ps.setString(1, misUsername);
            ps.setString(2, ipAddress);
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.setObject(4, loginSuccessFul);
            if (ps.executeUpdate() < 1) {
                throw new EJBException("Failed to log MIS user login");
            }
            Object var9_7 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block24;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block24;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var9_8 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    public double calculateBonusCredit(double amount) throws EJBException {
        return 0.0;
    }

    /*
     * Loose catch block
     */
    public void allowUserToReRegister(String username) throws EJBException {
        block22: {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("update user set mobilephone=?, mobileverified=?, status=? where username=?");
            ps.setString(1, null);
            ps.setInt(2, 0);
            ps.setInt(3, 0);
            ps.setString(4, username);
            if (ps.executeUpdate() != 1) {
                throw new EJBException("Failed to remove mobile phone and deactivate account for " + username);
            }
            Object var7_5 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block22;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block22;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var7_6 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public AlertMessageData getAlertMessageData(String id) throws EJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        if (id.length() < 1) {
            throw new EJBException("You must specify an ID");
        }
        conn = this.dataSourceMaster.getConnection();
        ps = conn.prepareStatement("select * from alertmessage where id = ?");
        ps.setString(1, id);
        rs = ps.executeQuery();
        if (!rs.next()) {
            throw new EJBException("Invalid alert message ID " + id);
        }
        AlertMessageData alertMessageData = new AlertMessageData(rs);
        Object var7_7 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return alertMessageData;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var7_8 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public void createAlertMessage(AlertMessageData amd) throws EJBException {
        block25: {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            conn = this.dataSourceMaster.getConnection();
            if (amd.content.length() < 1) {
                throw new EJBException("The alert message text cannot be empty");
            }
            ps = conn.prepareStatement("insert into alertmessage (countryid, datecreated, startdate, expirydate, type, onceonly, weighting, minmidletversion, maxmidletversion, contenttype, content, url, status) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
            ps.setObject(1, amd.countryID);
            ps.setDate(2, new Date(System.currentTimeMillis()));
            ps.setTimestamp(3, new Timestamp(amd.startDate.getTime()));
            ps.setTimestamp(4, new Timestamp(amd.expiryDate.getTime()));
            ps.setInt(5, amd.type.value());
            if (amd.onceOnly.booleanValue()) {
                ps.setInt(6, 1);
            } else {
                ps.setInt(6, 0);
            }
            ps.setDouble(7, amd.weighting);
            ps.setInt(8, amd.minMidletVersion);
            ps.setInt(9, amd.maxMidletVersion);
            ps.setInt(10, amd.contentType.value());
            ps.setString(11, amd.content);
            ps.setString(12, amd.url);
            ps.setInt(13, amd.status.value());
            if (ps.executeUpdate() < 1) {
                throw new EJBException("Could not create the new alert message");
            }
            Object var7_5 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block25;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block25;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var7_6 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public void updateAlertMessage(AlertMessageData amd) throws EJBException {
        block26: {
            Connection conn = null;
            Statement ps = null;
            ResultSet rs = null;
            conn = this.dataSourceMaster.getConnection();
            if (amd.content.length() < 1) {
                throw new EJBException("The alert message text cannot be empty");
            }
            if (amd.id < 1) {
                throw new EJBException("You must specify an ID to update");
            }
            ps = conn.prepareStatement("update alertmessage set countryid = ?, datecreated = ?, startdate = ?, expirydate = ?, type = ?, onceonly = ?, weighting = ?, minmidletversion = ?, maxmidletversion = ?, contenttype = ?, content = ?, url = ?, status = ? WHERE id = ? ");
            ps.setObject(1, amd.countryID);
            ps.setDate(2, new Date(System.currentTimeMillis()));
            ps.setTimestamp(3, new Timestamp(amd.startDate.getTime()));
            ps.setTimestamp(4, new Timestamp(amd.expiryDate.getTime()));
            ps.setInt(5, amd.type.value());
            if (amd.onceOnly.booleanValue()) {
                ps.setInt(6, 1);
            } else {
                ps.setInt(6, 0);
            }
            ps.setDouble(7, amd.weighting);
            ps.setInt(8, amd.minMidletVersion);
            ps.setInt(9, amd.maxMidletVersion);
            ps.setInt(10, amd.contentType.value());
            ps.setString(11, amd.content);
            ps.setString(12, amd.url);
            ps.setInt(13, amd.status.value());
            ps.setInt(14, amd.id);
            if (ps.executeUpdate() < 1) {
                throw new EJBException("Could not update the alert message");
            }
            Object var7_5 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block26;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block26;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var7_6 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public ResellerData getResellerData(String id) throws EJBException {
        ResellerData rd;
        block23: {
            ResultSet rs;
            PreparedStatement ps;
            Connection conn;
            block20: {
                conn = null;
                ps = null;
                rs = null;
                if (id.length() < 1) {
                    throw new EJBException("You must specify an ID");
                }
                rd = new ResellerData();
                conn = this.dataSourceMaster.getConnection();
                ps = conn.prepareStatement("select * from reseller where id = ?");
                ps.setString(1, id);
                rs = ps.executeQuery();
                if (!rs.next()) break block20;
                rd.id = rs.getInt("id");
                rd.countryID = rs.getInt("countryid");
                rd.state = rs.getString("state");
                rd.city = rs.getString("city");
                rd.name = rs.getString("name");
                rd.address = rs.getString("address");
                rd.phoneNumber = rs.getString("phonenumber");
                rd.phoneNumberToDisplay = rs.getString("phonenumbertodisplay");
                rd.phoneNumber2 = rs.getString("phonenumber2");
                rd.phoneNumber2ToDisplay = rs.getString("phonenumber2todisplay");
                rd.status = ResellerData.StatusEnum.fromValue(rs.getInt("status"));
            }
            Object var8_6 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block23;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block23;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var8_7 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
        return rd;
    }

    /*
     * Loose catch block
     */
    public void createReseller(ResellerData rd) throws EJBException {
        block22: {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("insert into reseller (countryid, state, city, name, address, phonenumber, phonenumbertodisplay, phonenumber2, phonenumber2todisplay, status) VALUES (?,?,?,?,?,?,?,?,?,?)");
            ps.setObject(1, rd.countryID);
            ps.setObject(2, rd.state);
            ps.setObject(3, rd.city);
            ps.setObject(4, rd.name);
            ps.setObject(5, rd.address);
            ps.setObject(6, rd.phoneNumber);
            ps.setObject(7, rd.phoneNumberToDisplay);
            ps.setObject(8, rd.phoneNumber2);
            ps.setObject(9, rd.phoneNumber2ToDisplay);
            ps.setInt(10, rd.status.value());
            if (ps.executeUpdate() < 1) {
                throw new EJBException("Could not create the new reseller");
            }
            Object var7_5 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block22;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block22;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var7_6 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public void updateReseller(ResellerData rd) throws EJBException {
        block23: {
            Connection conn = null;
            Statement ps = null;
            ResultSet rs = null;
            conn = this.dataSourceMaster.getConnection();
            if (rd.id == null || rd.id < 1) {
                throw new EJBException("You must specify an ID to update");
            }
            ps = conn.prepareStatement("update reseller set countryid=?, state=?, city=?, name=?, address=?, phonenumber=?, phonenumbertodisplay=?, phonenumber2=?, phonenumber2todisplay=?, status=? WHERE id=? ");
            ps.setObject(1, rd.countryID);
            ps.setObject(2, rd.state);
            ps.setObject(3, rd.city);
            ps.setObject(4, rd.name);
            ps.setObject(5, rd.address);
            ps.setObject(6, rd.phoneNumber);
            ps.setObject(7, rd.phoneNumberToDisplay);
            ps.setObject(8, rd.phoneNumber2);
            ps.setObject(9, rd.phoneNumber2ToDisplay);
            ps.setInt(10, rd.status.value());
            ps.setInt(11, rd.id);
            if (ps.executeUpdate() < 1) {
                throw new EJBException("Could not update the reseller");
            }
            Object var7_5 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block23;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block23;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var7_6 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public void updateCountry(CountryData cd) throws EJBException {
        block23: {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            if (cd.id < 1) {
                throw new EJBException("You must specify the country ID to update");
            }
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("update country set activationcredit = ?, referralcredit = ?, callrate = ?, mobilerate = ?, callthroughrate = ?, CallSignallingFee = ?, MobileSignallingFee = ?, CallThroughSignallingFee = ?, Population = ?, AllowCreditCard = ?, AllowBankTransfer = ?, AllowWesternUnion = ?, allowZeroAfterIDDCode = ?, allowEmail = ?, allowPhoneCall = ?, allowUserTransferToOtherCountry = ?, lowASRDestination = ?, callRetries = ?, userBonusProgramID = ?, merchantBonusProgramID = ? WHERE id = ?");
            ps.setDouble(1, cd.activationCredit);
            ps.setDouble(2, cd.referralCredit);
            ps.setDouble(3, cd.callRate);
            ps.setDouble(4, cd.mobileRate);
            ps.setDouble(5, cd.callThroughRate);
            ps.setDouble(6, cd.callSignallingFee);
            ps.setDouble(7, cd.mobileSignallingFee);
            ps.setDouble(8, cd.callThroughSignallingFee);
            ps.setInt(9, cd.population);
            ps.setInt(10, cd.allowCreditCard.value());
            ps.setInt(11, cd.allowBankTransfer);
            ps.setInt(12, cd.allowWesternUnion);
            ps.setObject(13, cd.allowZeroAfterIddCode == null ? null : Integer.valueOf(cd.allowZeroAfterIddCode != false ? 1 : 0));
            ps.setObject(14, cd.allowEmail == null ? null : Integer.valueOf(cd.allowEmail != false ? 1 : 0));
            ps.setObject(15, cd.allowPhoneCall == null ? null : Integer.valueOf(cd.allowPhoneCall != false ? 1 : 0));
            ps.setObject(16, cd.allowUserTransferToOtherCountry == null ? null : Integer.valueOf(cd.allowUserTransferToOtherCountry != false ? 1 : 0));
            ps.setObject(17, cd.lowASRDestination == null ? null : Integer.valueOf(cd.lowASRDestination != false ? 1 : 0));
            ps.setInt(18, cd.callRetries);
            ps.setObject(19, cd.userBonusProgramID);
            ps.setObject(20, cd.merchantBonusProgramID);
            ps.setInt(21, cd.id);
            if (ps.executeUpdate() < 1) {
                throw new EJBException("Could not update the country data");
            }
            Object var7_5 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block23;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block23;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var7_6 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public void changeStaffPassword(String staffUsername, String oldPassword, String newPassword) throws EJBException {
        block25: {
            Connection conn = null;
            Statement ps = null;
            ResultSet rs = null;
            conn = this.dataSourceMaster.getConnection();
            if (oldPassword.length() < 1 || newPassword.length() < 1) {
                throw new EJBException("Passwords cannot be blank");
            }
            ps = conn.prepareStatement("select password from staff where username = ?");
            ps.setString(1, staffUsername);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new EJBException("Staff user does not exist");
            }
            String currentPassword = rs.getString("password");
            if (!oldPassword.equals(currentPassword)) {
                throw new EJBException("Old password is not correct");
            }
            ps = conn.prepareStatement("update staff set password = ? where username = ?");
            ps.setString(1, newPassword);
            ps.setString(2, staffUsername);
            if (ps.executeUpdate() < 1) {
                throw new EJBException("Could not set the new password");
            }
            Object var9_9 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block25;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block25;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var9_10 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    public String newFileID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /*
     * Loose catch block
     */
    public FileData getFile(String fileID) throws EJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        conn = this.dataSourceMaster.getConnection();
        ps = conn.prepareStatement("select * from file where id = ?");
        ps.setString(1, fileID);
        rs = ps.executeQuery();
        if (!rs.next()) {
            throw new EJBException("Invalid file ID");
        }
        FileData fileData = new FileData();
        fileData.id = rs.getString("id");
        fileData.dateCreated = rs.getTimestamp("dateCreated");
        fileData.mimeType = rs.getString("mimeType");
        fileData.size = (Integer)rs.getObject("size");
        fileData.width = (Integer)rs.getObject("width");
        fileData.height = (Integer)rs.getObject("height");
        fileData.length = (Integer)rs.getObject("length");
        fileData.uploadedBy = rs.getString("uploadedBy");
        FileData fileData2 = fileData;
        Object var8_8 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return fileData2;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var8_9 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public ScrapbookData saveFile(FileData fileData, String description) throws EJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        description = StringUtil.stripHTML(description);
        conn = this.dataSourceMaster.getConnection();
        ps = conn.prepareStatement("insert into file (id, datecreated, mimetype, size, width, height, length, uploadedby) values (?,?,?,?,?,?,?,?)");
        ps.setString(1, fileData.id);
        ps.setTimestamp(2, new Timestamp(fileData.dateCreated.getTime()));
        ps.setString(3, fileData.mimeType);
        ps.setInt(4, fileData.size);
        ps.setObject(5, fileData.width);
        ps.setObject(6, fileData.height);
        ps.setObject(7, fileData.length);
        ps.setString(8, fileData.uploadedBy);
        if (ps.executeUpdate() != 1) {
            throw new EJBException("Failed to save file summary to database");
        }
        ps.close();
        ScrapbookData scrapbookData = new ScrapbookData();
        scrapbookData.username = fileData.uploadedBy;
        scrapbookData.fileID = fileData.id;
        scrapbookData.dateCreated = new java.util.Date();
        scrapbookData.receivedFrom = fileData.uploadedBy;
        scrapbookData.description = description;
        scrapbookData.status = ScrapbookData.StatusEnum.PRIVATE;
        ps = conn.prepareStatement("insert into scrapbook (username, fileid, datecreated, receivedfrom, description, status) values (?,?,?,?,?,?)", 1);
        ps.setString(1, scrapbookData.username);
        ps.setString(2, scrapbookData.fileID);
        ps.setTimestamp(3, new Timestamp(scrapbookData.dateCreated.getTime()));
        ps.setString(4, scrapbookData.receivedFrom);
        ps.setString(5, scrapbookData.description);
        ps.setInt(6, scrapbookData.status.value());
        if (ps.executeUpdate() != 1) {
            throw new EJBException("Failed to save file to sender's scrapbook");
        }
        rs = ps.getGeneratedKeys();
        if (!rs.next()) {
            throw new EJBException("Failed to create scrapbook entry");
        }
        scrapbookData.id = rs.getInt(1);
        MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.SCRAPBOOK, scrapbookData.username);
        try {
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            UserData uploaderUserData = userEJB.loadUser(scrapbookData.username, false, false);
            PhotoUploadTrigger trigger = new PhotoUploadTrigger(uploaderUserData);
            trigger.amountDelta = 0.0;
            trigger.quantityDelta = 1;
            RewardCentre.getInstance().sendTrigger(trigger);
        }
        catch (Exception e) {
            log.error((Object)("Unable to send PhotoUploadTrigger for user [" + scrapbookData.username + "] :" + e.getMessage()), (Throwable)e);
        }
        ScrapbookData scrapbookData2 = scrapbookData;
        Object var11_12 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return scrapbookData2;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var11_13 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public void saveFileToScrapbooks(String sender, String[] destinations, String fileID, String description) throws EJBException {
        block32: {
            Connection conn = null;
            PreparedStatement psFileExists = null;
            Statement psAddToScrapbook = null;
            ResultSet rs = null;
            description = StringUtil.stripHTML(description);
            conn = this.dataSourceMaster.getConnection();
            psFileExists = conn.prepareStatement("select id from scrapbook where username = ? and fileid = ? and receivedfrom = ? and status <> ?");
            psFileExists.setString(2, fileID);
            psFileExists.setString(3, sender);
            psFileExists.setInt(4, ScrapbookData.StatusEnum.INACTIVE.value());
            psAddToScrapbook = conn.prepareStatement("insert into scrapbook (username, fileid, datecreated, receivedfrom, description, status) values (?,?,?,?,?,?)");
            psAddToScrapbook.setString(2, fileID);
            psAddToScrapbook.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            psAddToScrapbook.setString(4, sender);
            psAddToScrapbook.setString(5, description == null || description.length() == 0 ? null : description);
            if (destinations.length == 1 && "wall200712041".equalsIgnoreCase(destinations[0])) {
                psAddToScrapbook.setInt(6, ScrapbookData.StatusEnum.PUBLIC.value());
            } else {
                psAddToScrapbook.setInt(6, ScrapbookData.StatusEnum.PRIVATE.value());
            }
            for (String destination : destinations) {
                if (destination.equals(sender)) continue;
                psFileExists.setString(1, destination);
                rs = psFileExists.executeQuery();
                if (!rs.next()) {
                    psAddToScrapbook.setString(1, destination);
                    if (psAddToScrapbook.executeUpdate() != 1) {
                        throw new EJBException("Failed to save file to " + destination + "'s scrapbook");
                    }
                    MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.SCRAPBOOK, destination);
                }
                rs.close();
            }
            Object var14_14 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (psFileExists != null) {
                    psFileExists.close();
                }
            }
            catch (SQLException e) {
                psFileExists = null;
            }
            try {
                if (psAddToScrapbook != null) {
                    psAddToScrapbook.close();
                }
            }
            catch (SQLException e) {
                psAddToScrapbook = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block32;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block32;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var14_15 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (psFileExists != null) {
                        psFileExists.close();
                    }
                }
                catch (SQLException e) {
                    psFileExists = null;
                }
                try {
                    if (psAddToScrapbook != null) {
                        psAddToScrapbook.close();
                    }
                }
                catch (SQLException e) {
                    psAddToScrapbook = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public void updateFileFromScrapbook(ScrapbookData scrapbookData) throws EJBException {
        block19: {
            Connection conn = null;
            Statement ps = null;
            if (scrapbookData.status == ScrapbookData.StatusEnum.PUBLIC) {
                UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                ReputationLevelData levelData = userEJB.getReputationLevel(scrapbookData.username);
                if (levelData.publishPhoto == null || !levelData.publishPhoto.booleanValue()) {
                    throw new EJBException("You current mig level is not high enough to make a photo public");
                }
            }
            scrapbookData.description = StringUtil.stripHTML(scrapbookData.description);
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("update scrapbook set description = ?, status = ? where id = ?");
            ps.setString(1, scrapbookData.description);
            ps.setInt(2, scrapbookData.status.value());
            ps.setInt(3, scrapbookData.id);
            if (ps.executeUpdate() != 1) {
                throw new EJBException("Unable to update file " + scrapbookData.id + " in scrapbook");
            }
            MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.SCRAPBOOK, scrapbookData.id.toString());
            MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.SCRAPBOOK, scrapbookData.username);
            Object var7_8 = null;
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block19;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block19;
            {
                catch (CreateException e) {
                    throw new EJBException(e.getMessage());
                }
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var7_9 = null;
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    private void onPublishedScrapbookFile(String username, int id, String description) {
    }

    /*
     * Loose catch block
     */
    public void publishFileFromScrapbook(String username, int id, String description, boolean contactsOnly) throws EJBException {
        block16: {
            Connection conn = null;
            Statement ps = null;
            description = StringUtil.stripHTML(description);
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("update scrapbook set description = ?, status = ? where id = ?");
            ps.setString(1, description);
            ps.setInt(2, contactsOnly ? ScrapbookData.StatusEnum.CONTACTS_ONLY.value() : ScrapbookData.StatusEnum.PUBLIC.value());
            ps.setInt(3, id);
            this.onPublishedScrapbookFile(username, id, description);
            if (ps.executeUpdate() != 1) {
                throw new EJBException("Unable to publish " + id + " from scrapbook");
            }
            MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.SCRAPBOOK, String.valueOf(id));
            MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.SCRAPBOOK, username);
            Object var9_7 = null;
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block16;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block16;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var9_8 = null;
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public void unpublishFileFromScrapbook(String username, int id) throws EJBException {
        block16: {
            Connection conn = null;
            PreparedStatement ps = null;
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("update scrapbook set status = ? where id = ?");
            ps.setInt(1, ScrapbookData.StatusEnum.PRIVATE.value());
            ps.setInt(2, id);
            if (ps.executeUpdate() != 1) {
                throw new EJBException("Unable to unpublish " + id + " from scrapbook");
            }
            MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.SCRAPBOOK, String.valueOf(id));
            MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.SCRAPBOOK, username);
            Object var7_5 = null;
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block16;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block16;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var7_6 = null;
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public void deleteFileFromScrapbook(String username, int id) throws EJBException {
        block16: {
            Connection conn = null;
            PreparedStatement ps = null;
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("update scrapbook set status = ? where id = ?");
            ps.setInt(1, ScrapbookData.StatusEnum.INACTIVE.value());
            ps.setInt(2, id);
            if (ps.executeUpdate() != 1) {
                throw new EJBException("Unable to remove " + id + " from scrapbook");
            }
            MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.SCRAPBOOK, String.valueOf(id));
            MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.SCRAPBOOK, username);
            Object var7_5 = null;
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block16;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block16;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var7_6 = null;
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public void setFileReportedFromScrapbook(String username, int id) throws EJBException {
        block16: {
            Connection conn = null;
            PreparedStatement ps = null;
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("update scrapbook set status = ? where id = ?");
            ps.setInt(1, ScrapbookData.StatusEnum.REPORTED.value());
            ps.setInt(2, id);
            if (ps.executeUpdate() != 1) {
                throw new EJBException("Unable to set " + id + " from scrapbook to reported");
            }
            MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.SCRAPBOOK, String.valueOf(id));
            MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.SCRAPBOOK, username);
            Object var7_5 = null;
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block16;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block16;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var7_6 = null;
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public void setFilePrivateFromScrapbook(String fileID) throws EJBException {
        block28: {
            Connection conn = null;
            PreparedStatement ps = null;
            Statement psSetFilePrivate = null;
            ResultSet rs = null;
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("select id, username from scrapbook where fileid = ? and status != ?");
            ps.setString(1, fileID);
            ps.setInt(2, ScrapbookData.StatusEnum.PRIVATE.value());
            psSetFilePrivate = conn.prepareStatement("update scrapbook set Status = ? where id = ?");
            psSetFilePrivate.setInt(1, ScrapbookData.StatusEnum.PRIVATE.value());
            rs = ps.executeQuery();
            while (rs.next()) {
                psSetFilePrivate.setInt(2, rs.getInt("id"));
                if (psSetFilePrivate.executeUpdate() <= 0) continue;
                MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.SCRAPBOOK, String.valueOf(rs.getInt("id")));
                MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.SCRAPBOOK, rs.getString("username"));
            }
            Object var8_6 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (psSetFilePrivate != null) {
                    psSetFilePrivate.close();
                }
            }
            catch (SQLException e) {
                psSetFilePrivate = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block28;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block28;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var8_7 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (psSetFilePrivate != null) {
                        psSetFilePrivate.close();
                    }
                }
                catch (SQLException e) {
                    psSetFilePrivate = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public void removeAllInstancesOfFile(String fileID) throws EJBException {
        block31: {
            ResultSet rs;
            Statement psRemoveFile;
            PreparedStatement ps;
            Connection conn;
            block27: {
                conn = null;
                ps = null;
                psRemoveFile = null;
                rs = null;
                conn = this.dataSourceMaster.getConnection();
                ps = conn.prepareStatement("select id, username from scrapbook where fileid = ? and status != ?");
                ps.setString(1, fileID);
                ps.setInt(2, ScrapbookData.StatusEnum.INACTIVE.value());
                psRemoveFile = conn.prepareStatement("update scrapbook set status = ? where id = ?");
                psRemoveFile.setInt(1, ScrapbookData.StatusEnum.INACTIVE.value());
                rs = ps.executeQuery();
                while (rs.next()) {
                    psRemoveFile.setInt(2, rs.getInt("id"));
                    if (psRemoveFile.executeUpdate() <= 0) continue;
                    MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.SCRAPBOOK, String.valueOf(rs.getInt("id")));
                    MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.SCRAPBOOK, rs.getString("username"));
                }
                rs.close();
                psRemoveFile.close();
                ps.close();
                ps = conn.prepareStatement("select username from user where displaypicture = ?");
                ps.setString(1, fileID);
                rs = ps.executeQuery();
                if (!rs.next()) break block27;
                UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                do {
                    userEJB.updateDisplayPicture(rs.getString("username"), null);
                } while (rs.next());
            }
            Object var8_9 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (psRemoveFile != null) {
                    psRemoveFile.close();
                }
            }
            catch (SQLException e) {
                psRemoveFile = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block31;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block31;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
                catch (CreateException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var8_10 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (psRemoveFile != null) {
                        psRemoveFile.close();
                    }
                }
                catch (SQLException e) {
                    psRemoveFile = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    public ScrapbookData getFileFromScrapbook(int id) throws EJBException {
        List<ScrapbookData> scrapbook = this.getScrapbook(id, null, null);
        if (scrapbook.size() != 1) {
            throw new EJBException("Invalid scrapbook ID " + id);
        }
        return scrapbook.get(0);
    }

    public List<ScrapbookData> getScrapbook(String username) throws EJBException {
        return this.getScrapbook(null, username, new ScrapbookData.StatusEnum[]{ScrapbookData.StatusEnum.PRIVATE, ScrapbookData.StatusEnum.CONTACTS_ONLY, ScrapbookData.StatusEnum.PUBLIC});
    }

    public List<ScrapbookData> getScrapbook(String username, boolean publishedOnly) throws EJBException {
        if (publishedOnly) {
            return this.getScrapbook(null, username, new ScrapbookData.StatusEnum[]{ScrapbookData.StatusEnum.CONTACTS_ONLY, ScrapbookData.StatusEnum.PUBLIC});
        }
        return this.getScrapbook(null, username, new ScrapbookData.StatusEnum[]{ScrapbookData.StatusEnum.PRIVATE, ScrapbookData.StatusEnum.CONTACTS_ONLY, ScrapbookData.StatusEnum.PUBLIC});
    }

    /*
     * Loose catch block
     */
    public List<ScrapbookData> getGallery(String requestingUsername, String targetUsername) throws EJBException {
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block26: {
            conn = null;
            ps = null;
            rs = null;
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select id from contact where username = ? and fusionusername = ?");
            ps.setString(1, targetUsername);
            ps.setString(2, requestingUsername);
            rs = ps.executeQuery();
            if (!rs.next()) break block26;
            ps.setString(1, requestingUsername);
            ps.setString(2, targetUsername);
            rs.close();
            rs = ps.executeQuery();
            if (!rs.next()) break block26;
            List<ScrapbookData> list = this.getScrapbook(null, targetUsername, new ScrapbookData.StatusEnum[]{ScrapbookData.StatusEnum.CONTACTS_ONLY, ScrapbookData.StatusEnum.PUBLIC, ScrapbookData.StatusEnum.REPORTED});
            Object var8_9 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e2) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e2) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (SQLException e2) {
                conn = null;
            }
            return list;
        }
        List<ScrapbookData> list = this.getScrapbook(null, targetUsername, new ScrapbookData.StatusEnum[]{ScrapbookData.StatusEnum.PUBLIC, ScrapbookData.StatusEnum.REPORTED});
        Object var8_10 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return list;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var8_11 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    private List<ScrapbookData> getScrapbook(Integer id, String username, ScrapbookData.StatusEnum[] statusArray) throws EJBException {
        Connection conn = null;
        Statement ps = null;
        ResultSet rs = null;
        String key = id == null ? username : id.toString();
        ArrayList<ScrapbookData> scrapbook = (ArrayList<ScrapbookData>)MemCachedClientWrapper.get(MemCachedKeySpaces.CommonKeySpace.SCRAPBOOK, key);
        if (scrapbook == null) {
            conn = this.dataSourceSlave.getConnection();
            String sql = "select scrapbook.id, scrapbook.username, scrapbook.fileid, scrapbook.datecreated as scrapbookdatecreated, scrapbook.receivedfrom, scrapbook.status, file.size, file.datecreated as filedatecreated, file.mimetype, file.width, file.height, file.length, file.uploadedby, scrapbook.description from scrapbook, file where scrapbook.fileid = file.id ";
            if (id != null) {
                sql = sql + "and scrapbook.id = ? ";
            }
            if (username != null) {
                sql = sql + "and scrapbook.username = ? ";
            }
            sql = sql + "order by scrapbook.id desc";
            ps = conn.prepareStatement(sql);
            int i = 0;
            if (id != null) {
                ps.setInt(++i, id);
            }
            if (username != null) {
                ps.setString(++i, username);
            }
            rs = ps.executeQuery();
            scrapbook = new ArrayList<ScrapbookData>();
            while (rs.next()) {
                FileData fileData = new FileData();
                fileData.id = rs.getString("fileID");
                fileData.dateCreated = rs.getTimestamp("fileDateCreated");
                fileData.mimeType = rs.getString("mimeType");
                fileData.size = (Integer)rs.getObject("size");
                fileData.width = (Integer)rs.getObject("width");
                fileData.height = (Integer)rs.getObject("height");
                fileData.length = rs.getInt("length");
                fileData.uploadedBy = rs.getString("uploadedBy");
                ScrapbookData scrapbookData = new ScrapbookData();
                scrapbookData.id = rs.getInt("id");
                scrapbookData.username = rs.getString("username");
                scrapbookData.fileID = rs.getString("fileID");
                scrapbookData.receivedFrom = rs.getString("receivedFrom");
                scrapbookData.dateCreated = rs.getTimestamp("scrapbookDateCreated");
                scrapbookData.file = fileData;
                scrapbookData.description = StringUtil.stripHTML(rs.getString("description"));
                Integer intVal = (Integer)rs.getObject("status");
                if (intVal != null) {
                    scrapbookData.status = ScrapbookData.StatusEnum.fromValue(intVal);
                }
                scrapbook.add(scrapbookData);
            }
            MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.SCRAPBOOK, key, scrapbook);
        }
        if (statusArray != null) {
            List<ScrapbookData.StatusEnum> statusList = Arrays.asList(statusArray);
            Iterator i = scrapbook.iterator();
            while (i.hasNext()) {
                ScrapbookData scrapbookData = (ScrapbookData)i.next();
                if (statusList.contains((Object)scrapbookData.status)) continue;
                i.remove();
            }
        }
        ArrayList<ScrapbookData> arrayList = scrapbook;
        Object var15_16 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return arrayList;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var15_17 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public Double getScrapbookSize(String username) throws EJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Double size = null;
        conn = this.dataSourceSlave.getConnection();
        String sql = "select sum(file.size) totalsize from scrapbook, file where scrapbook.fileid = file.id and scrapbook.username = ? and scrapbook.status IN (?,?)";
        ps = conn.prepareStatement(sql);
        ps.setString(1, username);
        ps.setInt(2, ScrapbookData.StatusEnum.PRIVATE.value());
        ps.setInt(3, ScrapbookData.StatusEnum.PUBLIC.value());
        rs = ps.executeQuery();
        if (rs.next()) {
            size = rs.getDouble("totalsize");
        }
        Double d = size;
        Object var9_9 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return d;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var9_10 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public List<String> getHandsetVendors() throws EJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        conn = this.dataSourceMaster.getConnection();
        ps = conn.prepareStatement("select distinct vendor from handsetvendorprefixes");
        rs = ps.executeQuery();
        ArrayList<String> handsetVendors = new ArrayList<String>();
        while (rs.next()) {
            handsetVendors.add(rs.getString("vendor"));
        }
        ArrayList<String> arrayList = handsetVendors;
        Object var7_7 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return arrayList;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var7_8 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public List<HandsetInstructionsData> getHandsetInstructions() throws EJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        conn = this.dataSourceMaster.getConnection();
        ps = conn.prepareStatement("select * from handsetinstructions");
        rs = ps.executeQuery();
        ArrayList<HandsetInstructionsData> handsetInstructions = new ArrayList<HandsetInstructionsData>();
        while (rs.next()) {
            HandsetInstructionsData handsetInstruction = new HandsetInstructionsData();
            handsetInstruction.id = rs.getInt("id");
            handsetInstruction.instructionText = rs.getString("instructiontext");
            handsetInstruction.description = rs.getString("description");
            handsetInstructions.add(handsetInstruction);
        }
        ArrayList<HandsetInstructionsData> arrayList = handsetInstructions;
        Object var7_7 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return arrayList;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var7_8 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public HandsetInstructionsData getHandsetInstruction(int id) throws EJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        conn = this.dataSourceMaster.getConnection();
        ps = conn.prepareStatement("select * from handsetinstructions where id = ?");
        ps.setDouble(1, id);
        rs = ps.executeQuery();
        HandsetInstructionsData handsetInstruction = new HandsetInstructionsData();
        if (rs.next()) {
            handsetInstruction.id = rs.getInt("id");
            handsetInstruction.instructionText = rs.getString("instructiontext");
            handsetInstruction.description = rs.getString("description");
        }
        HandsetInstructionsData handsetInstructionsData = handsetInstruction;
        Object var8_8 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return handsetInstructionsData;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var8_9 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public List<HandsetVendorPrefixesData> getHandsetVendorPrefixes() throws EJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        conn = this.dataSourceMaster.getConnection();
        ps = conn.prepareStatement("select * from handsetvendorprefixes");
        rs = ps.executeQuery();
        ArrayList<HandsetVendorPrefixesData> handsetVendorPrefixes = new ArrayList<HandsetVendorPrefixesData>();
        while (rs.next()) {
            HandsetVendorPrefixesData handsetVendorPrefixesData = new HandsetVendorPrefixesData();
            handsetVendorPrefixesData.vendor = rs.getString("vendor");
            handsetVendorPrefixesData.prefix = rs.getString("prefix");
            handsetVendorPrefixes.add(handsetVendorPrefixesData);
        }
        ArrayList<HandsetVendorPrefixesData> arrayList = handsetVendorPrefixes;
        Object var7_7 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return arrayList;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var7_8 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public List<HandsetData> getHandsetDetails(String vendor) throws EJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        conn = this.dataSourceMaster.getConnection();
        ps = conn.prepareStatement("select * from handsets left join handsetinstructions on handsets.instructionId = handsetinstructions.id where handsets.vendor = ? and phonemodel is not null");
        ps.setString(1, vendor);
        rs = ps.executeQuery();
        ArrayList<HandsetData> handsetDetails = new ArrayList<HandsetData>();
        while (rs.next()) {
            handsetDetails.add(new HandsetData(rs));
        }
        ArrayList<HandsetData> arrayList = handsetDetails;
        Object var8_8 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return arrayList;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var8_9 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public List<HandsetData> getDefaultHandsetDetails() throws EJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        conn = this.dataSourceMaster.getConnection();
        ps = conn.prepareStatement("select * from handsets left join handsetinstructions on handsets.instructionId = handsetinstructions.id where handsets.phonemodel is null");
        rs = ps.executeQuery();
        ArrayList<HandsetData> handsetDetails = new ArrayList<HandsetData>();
        while (rs.next()) {
            handsetDetails.add(new HandsetData(rs));
        }
        ArrayList<HandsetData> arrayList = handsetDetails;
        Object var7_7 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return arrayList;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var7_8 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public HandsetData getHandsetDetail(String vendor, String model) throws EJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        conn = this.dataSourceMaster.getConnection();
        ps = conn.prepareStatement("select * from handsets left join handsetinstructions on handsets.instructionId = handsetinstructions.id where handsets.vendor = ? and handsets.phonemodel = ?");
        ps.setString(1, vendor);
        ps.setString(2, model);
        rs = ps.executeQuery();
        if (!rs.next()) {
            throw new EJBException("That model of phone/vendor does not exist");
        }
        HandsetData handsetData = new HandsetData(rs);
        Object var8_8 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return handsetData;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var8_9 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public HandsetData getHandsetDetail(Integer id) throws EJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        conn = this.dataSourceMaster.getConnection();
        ps = conn.prepareStatement("select * from handsets left join handsetinstructions on handsets.instructionId = handsetinstructions.id where handsets.id = ?");
        ps.setDouble(1, id.intValue());
        rs = ps.executeQuery();
        if (!rs.next()) {
            throw new EJBException("That handset id does not exist");
        }
        HandsetData handsetData = new HandsetData(rs);
        Object var7_7 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return handsetData;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var7_8 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public void createHandset(HandsetData handset) throws EJBException {
        block48: {
            Connection conn = null;
            Statement ps = null;
            ResultSet rs = null;
            conn = this.dataSourceMaster.getConnection();
            if (handset.vendor.length() < 1) {
                throw new EJBException("You must specify a Vendor");
            }
            if (handset.phoneModel == null) {
                throw new EJBException("You must specify a Phone Model");
            }
            ps = conn.prepareStatement("insert into handsets (vendor, phonemodel, instructionid, midletversion, midletaccepttype, midp, cldc,camerasupport, filesystemsupport, pngsupport, gifsupport, jpegsupport, signedmidletsupport, screenwidth, screenheight, comments) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            if (handset.vendor != null) {
                ps.setString(1, handset.vendor);
            } else {
                ps.setObject(1, null);
            }
            if (handset.phoneModel != null) {
                ps.setString(2, handset.phoneModel);
            } else {
                ps.setObject(2, null);
            }
            if (handset.instructionId != null) {
                ps.setDouble(3, handset.instructionId.intValue());
            } else {
                ps.setObject(3, null);
            }
            ps.setString(4, handset.midletVersion);
            ps.setInt(5, handset.midletAcceptType.value());
            ps.setString(6, handset.midp);
            ps.setString(7, handset.cldc);
            if (handset.cameraSupport.booleanValue()) {
                ps.setInt(8, 1);
            } else {
                ps.setInt(8, 0);
            }
            if (handset.fileSystemSupport.booleanValue()) {
                ps.setInt(9, 1);
            } else {
                ps.setInt(9, 0);
            }
            if (handset.pngSupport.booleanValue()) {
                ps.setInt(10, 1);
            } else {
                ps.setInt(10, 0);
            }
            if (handset.gifSupport.booleanValue()) {
                ps.setInt(11, 1);
            } else {
                ps.setInt(11, 0);
            }
            if (handset.jpegSupport.booleanValue()) {
                ps.setInt(12, 1);
            } else {
                ps.setInt(12, 0);
            }
            if (handset.signedMidletSupport.booleanValue()) {
                ps.setInt(13, 1);
            } else {
                ps.setInt(13, 0);
            }
            if (handset.screenWidth != null) {
                ps.setDouble(14, handset.screenWidth.intValue());
            } else {
                ps.setObject(14, null);
            }
            if (handset.screenHeight != null) {
                ps.setDouble(15, handset.screenHeight.intValue());
            } else {
                ps.setObject(15, null);
            }
            if (handset.comments != null) {
                ps.setString(16, handset.comments);
            } else {
                ps.setObject(16, null);
            }
            if (ps.executeUpdate() < 1) {
                throw new EJBException("Could not create the new Handset");
            }
            Object var7_5 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block48;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block48;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var7_6 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public void updateHandsetDetail(HandsetData handset) throws EJBException {
        block46: {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("update handsets set vendor = ?, phonemodel = ?, instructionid = ?, midletversion = ?, midletaccepttype = ?, midp = ?, cldc = ?,camerasupport = ?, filesystemsupport = ?, pngsupport = ?, gifsupport = ?, jpegsupport = ?, signedmidletsupport = ?, screenwidth = ?, screenheight = ?, comments = ? where id = ?");
            if (handset.vendor != null) {
                ps.setString(1, handset.vendor);
            } else {
                ps.setObject(1, null);
            }
            if (handset.phoneModel != null) {
                ps.setString(2, handset.phoneModel);
            } else {
                ps.setObject(2, null);
            }
            if (handset.instructionId != null) {
                ps.setDouble(3, handset.instructionId.intValue());
            } else {
                ps.setObject(3, null);
            }
            ps.setString(4, handset.midletVersion);
            ps.setInt(5, handset.midletAcceptType.value());
            ps.setString(6, handset.midp);
            ps.setString(7, handset.cldc);
            if (handset.cameraSupport.booleanValue()) {
                ps.setInt(8, 1);
            } else {
                ps.setInt(8, 0);
            }
            if (handset.fileSystemSupport.booleanValue()) {
                ps.setInt(9, 1);
            } else {
                ps.setInt(9, 0);
            }
            if (handset.pngSupport.booleanValue()) {
                ps.setInt(10, 1);
            } else {
                ps.setInt(10, 0);
            }
            if (handset.gifSupport.booleanValue()) {
                ps.setInt(11, 1);
            } else {
                ps.setInt(11, 0);
            }
            if (handset.jpegSupport.booleanValue()) {
                ps.setInt(12, 1);
            } else {
                ps.setInt(12, 0);
            }
            if (handset.signedMidletSupport.booleanValue()) {
                ps.setInt(13, 1);
            } else {
                ps.setInt(13, 0);
            }
            if (handset.screenWidth != null) {
                ps.setDouble(14, handset.screenWidth.intValue());
            } else {
                ps.setObject(14, null);
            }
            if (handset.screenHeight != null) {
                ps.setDouble(15, handset.screenHeight.intValue());
            } else {
                ps.setObject(15, null);
            }
            if (handset.comments != null) {
                ps.setString(16, handset.comments);
            } else {
                ps.setObject(16, null);
            }
            ps.setDouble(17, handset.id.intValue());
            if (ps.executeUpdate() < 1) {
                throw new EJBException("Could not update the Handset Details");
            }
            Object var7_5 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block46;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block46;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var7_6 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public void createHandsetInstructions(HandsetInstructionsData handsetInstructions) throws EJBException {
        block24: {
            Connection conn = null;
            Statement ps = null;
            ResultSet rs = null;
            conn = this.dataSourceMaster.getConnection();
            if (handsetInstructions.description.length() < 1 || handsetInstructions.description == null) {
                throw new EJBException("You must provide a Description");
            }
            if (handsetInstructions.instructionText.length() < 1 || handsetInstructions.instructionText == null) {
                throw new EJBException("You must provide some Instruction Text");
            }
            ps = conn.prepareStatement("insert into handsetinstructions (description, instructiontext) VALUES (?,?)");
            ps.setString(1, handsetInstructions.description);
            ps.setString(2, handsetInstructions.instructionText);
            if (ps.executeUpdate() < 1) {
                throw new EJBException("Could not create the instructions");
            }
            Object var7_5 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block24;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block24;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var7_6 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public void updateHandsetInstructions(HandsetInstructionsData handsetInstructions) throws EJBException {
        block24: {
            Connection conn = null;
            Statement ps = null;
            ResultSet rs = null;
            conn = this.dataSourceMaster.getConnection();
            if (handsetInstructions.description.length() < 1) {
                throw new EJBException("You must provide a Description");
            }
            if (handsetInstructions.instructionText.length() < 1) {
                throw new EJBException("You must provide some Instruction Text");
            }
            ps = conn.prepareStatement("update handsetinstructions set description = ?, instructiontext = ? where id = ?");
            ps.setString(1, handsetInstructions.description);
            ps.setString(2, handsetInstructions.instructionText);
            ps.setDouble(3, handsetInstructions.id.intValue());
            if (ps.executeUpdate() < 1) {
                throw new EJBException("Could not create the instructions");
            }
            Object var7_5 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block24;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block24;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var7_6 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public void removeMerchantPinAuthentication(int userId) {
        block21: {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("delete from merchantpin where userid = ?");
            ps.setInt(1, userId);
            ps.execute();
            Object var7_5 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block21;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block21;
            {
                catch (Exception e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var7_6 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public String getMerchantPinEmail(int userId) {
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block26: {
            conn = null;
            ps = null;
            rs = null;
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("select email from merchantpin where userid = ?");
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            if (!rs.next()) break block26;
            String string = rs.getString("email");
            Object var7_8 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e2) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e2) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (SQLException e2) {
                conn = null;
            }
            return string;
        }
        String string = "";
        Object var7_9 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return string;
        catch (Exception e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var7_10 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    public void sendMerchantResetPinNotification(int userId) {
        String email = this.getMerchantPinEmail(userId);
        if (email.length() == 0) {
            return;
        }
        try {
            User userBean = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
            UserData userData = userBean.loadUserFromID(userId);
            EmailUserNotification note = new EmailUserNotification();
            note.emailAddress = email;
            note.subject = "Merchant PIN reset";
            note.message = "Hi " + userData.username + ",\n\n" + "We have just resetted your merchant PIN.  If you wish you can reuse the Merchant PIN creation wizard to get a new PIN.\n" + "Thanks,\n" + "migme Team";
            EJBIcePrxFinder.getUserNotificationServiceProxy().notifyUserViaEmail(note);
        }
        catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    /*
     * Loose catch block
     */
    public List<EmoteCommandData> getEmoteCommands() throws EJBException {
        Connection connSlave = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<EmoteCommandData> commands = new ArrayList<EmoteCommandData>();
        connSlave = this.dataSourceSlave.getConnection();
        ps = connSlave.prepareStatement("select * from emotecommand where status=1 order by id asc");
        rs = ps.executeQuery();
        while (rs.next()) {
            EmoteCommandData ecd = new EmoteCommandData(rs);
            commands.add(ecd);
        }
        ArrayList<EmoteCommandData> arrayList = commands;
        Object var7_7 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (connSlave != null) {
                connSlave.close();
            }
        }
        catch (SQLException e2) {
            connSlave = null;
        }
        return arrayList;
        catch (SQLException e) {
            try {
                throw new EJBException("Unable to load emote command: " + e.getMessage());
            }
            catch (Throwable throwable) {
                Object var7_8 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (connSlave != null) {
                        connSlave.close();
                    }
                }
                catch (SQLException e2) {
                    connSlave = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public List<AuthenticatedAccessControlData> getAuthenticatedAccessControlData() throws EJBException {
        Connection connSlave = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<AuthenticatedAccessControlData> dataList = new ArrayList<AuthenticatedAccessControlData>();
        connSlave = this.dataSourceSlave.getConnection();
        ps = connSlave.prepareStatement("select * from authenticatedaccesscontrol where status=1");
        rs = ps.executeQuery();
        while (rs.next()) {
            AuthenticatedAccessControlData ecd = new AuthenticatedAccessControlData(rs);
            dataList.add(ecd);
        }
        ArrayList<AuthenticatedAccessControlData> arrayList = dataList;
        Object var7_7 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (connSlave != null) {
                connSlave.close();
            }
        }
        catch (SQLException e2) {
            connSlave = null;
        }
        return arrayList;
        catch (SQLException e) {
            try {
                throw new EJBException("Unable to load authenticated access control: " + e.getMessage());
            }
            catch (Throwable throwable) {
                Object var7_8 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (connSlave != null) {
                        connSlave.close();
                    }
                }
                catch (SQLException e2) {
                    connSlave = null;
                }
                throw throwable;
            }
        }
    }

    private String generateHashTagDataCacheKey(String hashtag, int countryid) {
        return String.format("%s:%d", hashtag, countryid);
    }

    public HashTagData getHashTagData(String hashtag, int countryId) throws EJBException {
        try {
            return this.hashTagAPI.getHashTagData(this.dataSourceSlave.getConnection(), hashtag, countryId);
        }
        catch (Exception e) {
            throw new EJBException(e);
        }
    }

    public void updateHashTagData(String hashtag, int countryId, String description) throws EJBException {
        try {
            this.hashTagAPI.updateHashTagData(this.dataSourceMaster.getConnection(), hashtag, countryId, description);
        }
        catch (Exception e) {
            throw new EJBException(e);
        }
    }

    /*
     * Loose catch block
     */
    public void updatePromotedPost(List<PromotedPostData> promotedPostDataList) throws EJBException {
        block22: {
            Connection connMaster = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            String sql = "replace into promotedpost  values(?,?,?,?,?,?)";
            connMaster = this.dataSourceMaster.getConnection();
            ps = connMaster.prepareStatement(sql);
            for (PromotedPostData promotedPostData : promotedPostDataList) {
                ps.setString(1, promotedPostData.getUrl());
                ps.setInt(2, promotedPostData.getSlot());
                ps.setBoolean(3, promotedPostData.isStatus());
                ps.setTimestamp(4, new Timestamp(promotedPostData.getStartDate().getTime()));
                ps.setTimestamp(5, new Timestamp(promotedPostData.getEndDate().getTime()));
                ps.setInt(6, promotedPostData.getCountryID());
                ps.addBatch();
            }
            ps.executeBatch();
            Object var9_9 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (connMaster != null) {
                    connMaster.close();
                }
                break block22;
            }
            catch (SQLException e) {
                connMaster = null;
            }
            break block22;
            {
                catch (Exception e) {
                    throw new EJBException(e.getMessage(), e);
                }
            }
            catch (Throwable throwable) {
                Object var9_10 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (connMaster != null) {
                        connMaster.close();
                    }
                }
                catch (SQLException e) {
                    connMaster = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public List<PromotedPostData> getPromotedPost(int limit, boolean archived, int slot) throws EJBException {
        Connection connSlave = null;
        Statement ps = null;
        ResultSet rs = null;
        String sql = "select * from promotedpost where status=? ";
        if (slot != -1) {
            sql = sql + " and slot=?";
        }
        sql = sql + " order by startdate limit ?";
        connSlave = this.dataSourceSlave.getConnection();
        ps = connSlave.prepareStatement(sql);
        int i = 0;
        ps.setInt(++i, archived ? 0 : 1);
        if (slot != -1) {
            ps.setInt(++i, slot);
        }
        ps.setInt(++i, limit);
        rs = ps.executeQuery();
        ArrayList<PromotedPostData> promotedPostDatas = new ArrayList<PromotedPostData>();
        while (rs.next()) {
            PromotedPostData promotedPostData = new PromotedPostData(rs);
            promotedPostDatas.add(promotedPostData);
        }
        ArrayList<PromotedPostData> arrayList = promotedPostDatas;
        Object var12_12 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (connSlave != null) {
                connSlave.close();
            }
        }
        catch (SQLException e2) {
            connSlave = null;
        }
        return arrayList;
        catch (Exception e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var12_13 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (connSlave != null) {
                        connSlave.close();
                    }
                }
                catch (SQLException e2) {
                    connSlave = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public ReputationLevelData getReputationLevelDataForLevel(int level) throws EJBException {
        Connection connSlave = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ReputationLevelData repuData = null;
        String sql = "select * from reputationscoretolevel where level = ?";
        connSlave = this.dataSourceSlave.getConnection();
        ps = connSlave.prepareStatement(sql);
        ps.setInt(1, level);
        rs = ps.executeQuery();
        if (!rs.next()) {
            throw new EJBException("Unable to retrieve ReputationLevelData for level " + level);
        }
        repuData = new ReputationLevelData(rs);
        MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.REPUTATION_LEVEL_DATA, "" + level, repuData);
        ReputationLevelData reputationLevelData = repuData;
        Object var9_9 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (connSlave != null) {
                connSlave.close();
            }
        }
        catch (SQLException e2) {
            connSlave = null;
        }
        return reputationLevelData;
        catch (Exception e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var9_10 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (connSlave != null) {
                        connSlave.close();
                    }
                }
                catch (SQLException e2) {
                    connSlave = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public int getMaximumSystemMigLevel() throws EJBException {
        Connection connSlave = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Integer maxLevel = MemCachedClientWrapper.getInt(MemCachedKeySpaces.CommonKeySpace.MAX_SYSTEM_MIGLEVEL, "");
        if (maxLevel == null) {
            String sql = "select max(level) as maxlevel from reputationscoretolevel";
            connSlave = this.dataSourceSlave.getConnection();
            ps = connSlave.prepareStatement(sql);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new EJBException("Unable to retrieve maximum level ");
            }
            maxLevel = rs.getInt("maxlevel");
            MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.MAX_SYSTEM_MIGLEVEL, "", maxLevel);
        }
        int sql = maxLevel;
        Object var7_8 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (connSlave != null) {
                connSlave.close();
            }
        }
        catch (SQLException e2) {
            connSlave = null;
        }
        return sql;
        catch (Exception e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var7_9 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (connSlave != null) {
                        connSlave.close();
                    }
                }
                catch (SQLException e2) {
                    connSlave = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public boolean logMig33UserAction(int objectID, Enums.Mig33UserActionMisLogEnum logEnum, String desc) throws Exception {
        block23: {
            PreparedStatement ps;
            Connection connMaster;
            block19: {
                connMaster = null;
                ps = null;
                String sql = "INSERT INTO mislog(datecreated, objectID, description, action, section, comment) VALUES(NOW(), ?, ?, ?, ?, 'User initiated action.')";
                connMaster = this.dataSourceMaster.getConnection();
                ps = connMaster.prepareStatement(sql);
                ps.setInt(1, objectID);
                ps.setString(2, desc);
                ps.setString(3, logEnum.getAction());
                ps.setString(4, logEnum.getSection());
                if (ps.executeUpdate() <= 0) break block19;
                boolean bl = true;
                Object var9_10 = null;
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (connMaster != null) {
                        connMaster.close();
                    }
                }
                catch (SQLException e) {
                    connMaster = null;
                }
                return bl;
            }
            try {
                Object var9_11 = null;
            }
            catch (Throwable throwable) {
                Object var9_12 = null;
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (connMaster != null) {
                        connMaster.close();
                    }
                }
                catch (SQLException e) {
                    connMaster = null;
                }
                throw throwable;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (connMaster != null) {
                    connMaster.close();
                }
                break block23;
            }
            catch (SQLException e) {
                connMaster = null;
            }
            break block23;
            {
                catch (SQLException e) {
                    log.error((Object)String.format("unable to insert mislog entry (%d, %s, %s) due to sql exception", objectID, logEnum.name(), desc), (Throwable)e);
                    throw new FusionEJBException(e.getMessage());
                }
                catch (Exception e) {
                    throw new FusionEJBException(e.getMessage());
                }
            }
        }
        return false;
    }

    static {
        countriesNextUpdate = -1L;
        currenciesNextUpdate = -1L;
        menusNextUpdate = -1L;
        clientTextsNextUpdate = -1L;
        countriesLock = new Object();
        currenciesLock = new Object();
        clientTextsLock = new Object();
        menusLock = new Object();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class SingletonHolder {
        private static LazyLoader<List<CountryData>> COUNTRIES_SUPPORTED_HASHTAG_LOADER = new LazyLoader<List<CountryData>>(300000L){

            @Override
            protected List<CountryData> fetchValue() throws Exception {
                DataSource dataSourceSlave = LookupUtil.getFusionSlaveDataSource();
                return MISBean.getCountriesSupportedHashtag(dataSourceSlave);
            }
        };

        public static LazyLoader<List<CountryData>> getCountriesSupportedHashtagLoader() {
            return COUNTRIES_SUPPORTED_HASHTAG_LOADER;
        }
    }
}

