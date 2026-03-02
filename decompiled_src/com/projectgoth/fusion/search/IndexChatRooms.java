/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Application
 *  org.apache.log4j.Logger
 *  org.apache.log4j.xml.DOMConfigurator
 *  org.json.JSONArray
 *  org.json.JSONObject
 */
package com.projectgoth.fusion.search;

import Ice.Application;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.RequestCounter;
import com.projectgoth.fusion.data.ChatRoomData;
import com.projectgoth.fusion.search.ChatRoomsIndex;
import com.projectgoth.fusion.search.ElasticSearch;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Properties;
import java.util.Vector;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.json.JSONArray;
import org.json.JSONObject;

public class IndexChatRooms
extends Application {
    private static final String APP_NAME = "IndexChatRooms";
    static int numChatRoomsProcessed = 0;
    static int numChatRoomsInserted = 0;
    static int numChatRoomsUpdated = 0;
    static RequestCounter requestCounter = new RequestCounter();
    static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(IndexChatRooms.class));
    static int startID = 0;
    static String dbSlaveURL;
    static String dbSlaveUsername;
    static String dbSlavePassword;
    static DecimalFormat df2;
    static int daysAgo;

    public static void main(String[] args) {
        DOMConfigurator.configureAndWatch((String)ConfigUtils.getDefaultLog4jConfigFilename());
        boolean showHelp = false;
        for (int i = 0; i < args.length; ++i) {
            if (args[i].equalsIgnoreCase("-startid")) {
                startID = Integer.parseInt(args[i + 1]);
                continue;
            }
            if (args[i].equalsIgnoreCase("-daysago")) {
                daysAgo = Integer.parseInt(args[i + 1]);
                continue;
            }
            if (!args[i].equalsIgnoreCase("-?") && !args[i].equalsIgnoreCase("-h")) continue;
            showHelp = true;
        }
        if (showHelp) {
            log.error((Object)"Usage: IndexChatRooms [-startid id] [-daysago days]");
            log.error((Object)" -startid : Index chat rooms in chronological order based on their ID, starting from this ID (default 0)");
            log.error((Object)" -daysago : Only migrate chat rooms that have been accessed in the last 'days' days (default 90)");
            log.error((Object)"e.g. IndexChatRooms -startid 23717 -daysago 60");
            return;
        }
        log.info((Object)"IndexChatRooms starting.");
        IndexChatRooms app = new IndexChatRooms();
        int status = app.main(APP_NAME, args);
        log.info((Object)"Terminating");
        System.exit(status);
    }

    public int run(String[] arg0) {
        Integer lastIDMigrated = this.readLastIDMigratedFromFile();
        if (lastIDMigrated != null) {
            startID = lastIDMigrated;
            log.info((Object)("Found \"lastidmigrated\" file. Will start migrating from ID " + lastIDMigrated));
        }
        log.info((Object)("Starting from chat room ID " + startID));
        try {
            this.loadDBProperties();
        }
        catch (Exception e) {
            log.error((Object)("Unable to load database configuration file: " + e.getMessage()));
            return 1;
        }
        log.info((Object)("Will connect to slave DB at " + dbSlaveURL + " as " + dbSlaveUsername));
        try {
            Connection conn = this.getSlaveConnection();
            conn.close();
        }
        catch (Exception e) {
            log.fatal((Object)"Unable to connect to to slave DB", (Throwable)e);
            return 1;
        }
        boolean finished = false;
        try {
            while (!finished) {
                String sql = "select rooms.*, group_concat(keyword.keyword) as keywords from (select * from chatroom where id > " + startID + " and status=1 and datelastaccessed > date_sub(now(), interval " + daysAgo + " day) order by id limit 5000) rooms " + " left outer join chatroomkeyword on rooms.id=chatroomkeyword.chatroomid " + "left join keyword on chatroomkeyword.keywordid=keyword.id " + "group by id, name, description, type, creator, chatroomcategoryid, primarycountryid, secondarycountryid, locationid, groupid, adultonly, maximumsize, " + "userowned, allowkicking, allowuserkeywords, allowBots, language, datecreated, datelastaccessed, status, chatroomthemeid order by id;";
                Connection connSlave = this.getSlaveConnection();
                ResultSet rsChatRooms = connSlave.createStatement().executeQuery(sql);
                Vector<ChatRoomData> rooms = new Vector<ChatRoomData>();
                while (rsChatRooms.next()) {
                    rooms.add(new ChatRoomData(rsChatRooms));
                }
                rsChatRooms.close();
                connSlave.close();
                if (rooms.size() == 0) {
                    finished = true;
                    break;
                }
                for (int i = 0; i < rooms.size(); ++i) {
                    ChatRoomData room = (ChatRoomData)rooms.elementAt(i);
                    boolean needsUpdate = false;
                    JSONObject result = ElasticSearch.get(ElasticSearch.IndexType.CHAT_ROOMS, ElasticSearch.DocumentType.CHAT_ROOM, room.id);
                    if (result != null) {
                        JSONObject roomJson = result.getJSONObject("_source");
                        JSONObject fieldsToUpdate = new JSONObject();
                        if (roomJson.getString(ChatRoomsIndex.Field.NAME.toString()) == null || !roomJson.getString(ChatRoomsIndex.Field.NAME.toString()).equalsIgnoreCase(room.name)) {
                            fieldsToUpdate.put(ChatRoomsIndex.Field.NAME.toString(), (Object)room.name);
                            needsUpdate = true;
                        }
                        if (!roomJson.has(ChatRoomsIndex.Field.ADULT_ONLY.toString()) || roomJson.get(ChatRoomsIndex.Field.ADULT_ONLY.toString()) == null || roomJson.getBoolean(ChatRoomsIndex.Field.ADULT_ONLY.toString()) != room.adultOnly.booleanValue()) {
                            fieldsToUpdate.put(ChatRoomsIndex.Field.ADULT_ONLY.toString(), (Object)room.adultOnly);
                            needsUpdate = true;
                        }
                        if (!(room.dateLastAccessed == null || roomJson.has(ChatRoomsIndex.Field.DATE_LAST_ACCESSED.toString()) && roomJson.get(ChatRoomsIndex.Field.DATE_LAST_ACCESSED.toString()) != null && roomJson.getLong(ChatRoomsIndex.Field.DATE_LAST_ACCESSED.toString()) >= room.dateLastAccessed.getTime())) {
                            fieldsToUpdate.put(ChatRoomsIndex.Field.DATE_LAST_ACCESSED.toString(), room.dateLastAccessed.getTime());
                            needsUpdate = true;
                        }
                        if (!(room.primaryCountryID == null || roomJson.has(ChatRoomsIndex.Field.PRIMARY_COUNTRY_ID.toString()) && roomJson.get(ChatRoomsIndex.Field.PRIMARY_COUNTRY_ID.toString()) != null && roomJson.getLong(ChatRoomsIndex.Field.PRIMARY_COUNTRY_ID.toString()) == (long)room.primaryCountryID.intValue())) {
                            fieldsToUpdate.put(ChatRoomsIndex.Field.PRIMARY_COUNTRY_ID.toString(), (Object)room.primaryCountryID);
                            needsUpdate = true;
                        }
                        if (room.secondaryCountryID != null && (roomJson.has(ChatRoomsIndex.Field.SECONDARY_COUNTRY_ID.toString()) || roomJson.get(ChatRoomsIndex.Field.SECONDARY_COUNTRY_ID.toString()) == null || roomJson.getLong(ChatRoomsIndex.Field.SECONDARY_COUNTRY_ID.toString()) != (long)room.secondaryCountryID.intValue())) {
                            fieldsToUpdate.put(ChatRoomsIndex.Field.SECONDARY_COUNTRY_ID.toString(), (Object)room.secondaryCountryID);
                            needsUpdate = true;
                        }
                        if (room.keywords != null && room.keywords.length > 0) {
                            HashSet<String> keywordsInES = new HashSet<String>();
                            if (roomJson.has(ChatRoomsIndex.Field.TAGS.toString())) {
                                JSONArray keywordsJson = roomJson.getJSONArray(ChatRoomsIndex.Field.TAGS.toString());
                                for (int j = 0; j < keywordsJson.length(); ++j) {
                                    keywordsInES.add(keywordsJson.getString(j));
                                }
                            }
                            HashSet<String> keywordsInDB = new HashSet<String>();
                            for (String keyword : room.keywords) {
                                keywordsInDB.add(keyword);
                            }
                            if (!keywordsInES.equals(keywordsInDB)) {
                                fieldsToUpdate.put(ChatRoomsIndex.Field.TAGS.toString(), (Object)new JSONArray(keywordsInDB));
                                needsUpdate = true;
                            }
                        }
                        if (needsUpdate) {
                            ChatRoomsIndex.updateChatRoom(room.id, room.name, fieldsToUpdate);
                            ++numChatRoomsUpdated;
                        }
                    } else {
                        JSONObject fieldsToUpdate = new JSONObject();
                        fieldsToUpdate.put(ChatRoomsIndex.Field.ADULT_ONLY.toString(), (Object)room.adultOnly);
                        if (room.keywords != null && room.keywords.length > 0) {
                            HashSet<String> set = new HashSet<String>();
                            for (String keyword : room.keywords) {
                                set.add(keyword);
                            }
                            fieldsToUpdate.put(ChatRoomsIndex.Field.TAGS.toString(), (Object)new JSONArray(set));
                        }
                        if (room.primaryCountryID != null) {
                            fieldsToUpdate.put(ChatRoomsIndex.Field.PRIMARY_COUNTRY_ID.toString(), (Object)room.primaryCountryID);
                        }
                        if (room.secondaryCountryID != null) {
                            fieldsToUpdate.put(ChatRoomsIndex.Field.SECONDARY_COUNTRY_ID.toString(), (Object)room.secondaryCountryID);
                        }
                        if (room.dateLastAccessed != null) {
                            fieldsToUpdate.put(ChatRoomsIndex.Field.DATE_LAST_ACCESSED.toString(), room.dateLastAccessed.getTime());
                        }
                        ChatRoomsIndex.updateChatRoom(room.id, room.name, fieldsToUpdate);
                        ++numChatRoomsInserted;
                    }
                    ++numChatRoomsProcessed;
                    requestCounter.add();
                    this.writeLastIDMigratedToFile(room.id);
                    startID = room.id;
                }
                log.info((Object)("Processed " + numChatRoomsProcessed + " rooms (" + numChatRoomsInserted + " inserted, " + numChatRoomsUpdated + " updated). Processed " + df2.format(requestCounter.getRequestsPerSecond()) + "/s"));
            }
        }
        catch (Exception e) {
            log.error((Object)"An exception occurred", (Throwable)e);
            e.printStackTrace();
            return 1;
        }
        log.info((Object)"Complete");
        this.writeLastIDMigratedToFile(0);
        return 0;
    }

    private Integer readLastIDMigratedFromFile() {
        try {
            File f = new File("lastidmigrated");
            if (f.exists()) {
                FileReader fr = new FileReader(f);
                BufferedReader in = new BufferedReader(fr);
                int id = Integer.parseInt(in.readLine());
                return id;
            }
            return null;
        }
        catch (Exception e) {
            log.warn((Object)("Unable to read 'lastidmigrated' file: " + e.getMessage()));
            return null;
        }
    }

    private void writeLastIDMigratedToFile(int id) {
        try {
            FileWriter fileWriter = new FileWriter("lastidmigrated", false);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            writer.write(Integer.toString(id));
            writer.flush();
            writer.close();
        }
        catch (Exception e) {
            log.warn((Object)("Unable to write last ID migrated " + id + " to 'lastidmigrated' file: " + e.getMessage()));
        }
    }

    private Connection getSlaveConnection() throws Exception {
        return DriverManager.getConnection(dbSlaveURL, dbSlaveUsername, dbSlavePassword);
    }

    private void loadDBProperties() throws Exception {
        Properties properties = new Properties();
        String propertiesLocation = ConfigUtils.getConfigDirectory() + "database.properties";
        log.info((Object)("Loading database configuration file " + propertiesLocation));
        FileInputStream inputStream = new FileInputStream(new File(propertiesLocation));
        properties.load(inputStream);
        dbSlaveURL = properties.getProperty("mis.database.jdbcUrl");
        if (dbSlaveURL == null) {
            throw new Exception("mis.database.jdbcUrl not specified");
        }
        dbSlaveUsername = properties.getProperty("mis.database.username");
        if (dbSlaveUsername == null) {
            throw new Exception("mis.database.username not specified");
        }
        dbSlavePassword = properties.getProperty("mis.database.password");
        if (dbSlavePassword == null) {
            throw new Exception("mis.database.password not specified");
        }
        log.info((Object)"Database configuration successfully loaded");
    }

    static {
        df2 = new DecimalFormat("0.00");
        daysAgo = 90;
    }
}

