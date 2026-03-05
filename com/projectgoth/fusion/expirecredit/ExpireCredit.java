/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Application
 *  Ice.Properties
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.expirecredit;

import Ice.Application;
import Ice.Properties;
import com.projectgoth.fusion.app.dao.db.fusiondb.DBUtils;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.MemCachedClientWrapper;
import com.projectgoth.fusion.common.MemCachedKeySpaces;
import com.projectgoth.fusion.common.RequestCounter;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.UserData;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

public class ExpireCredit
extends Application {
    private static final String APP_NAME = "Expire Credit";
    private static final String CONFIG_FILE = "ExpireCredit.cfg";
    public static Properties properties = null;
    static String dbMasterHost;
    static String dbMasterUsername;
    static String dbMasterPassword;
    static String dbSlaveHost;
    static String dbSlaveUsername;
    static String dbSlavePassword;
    static volatile int numProcessed;
    static volatile int numExpired;
    static SimpleDateFormat dateFormatter;
    static DecimalFormat df2;
    static DecimalFormat df6;
    static boolean simulationMode;
    static volatile double totalCreditExpired;
    static volatile double totalFundedCreditExpired;
    static char startChar1;
    static char startChar2;
    static String usernamesToExclude;
    static RequestCounter requestCounter;
    static final Logger log;
    static int startOfPeak;
    static int endOfPeak;
    static int numThreads;
    static ExecutorService executorService;
    static BufferedWriter writer;
    static double minExpiryAmount;
    static boolean inactive;

    public static void main(String[] args) {
        log.info((Object)"Expire Credit starting");
        for (int i = 0; i < args.length; ++i) {
            if (args[i].equalsIgnoreCase("-s")) {
                simulationMode = true;
                continue;
            }
            if (args[i].equalsIgnoreCase("-mh")) {
                dbMasterHost = args[i + 1];
                continue;
            }
            if (args[i].equalsIgnoreCase("-mu")) {
                dbMasterUsername = args[i + 1];
                continue;
            }
            if (args[i].equalsIgnoreCase("-mp")) {
                dbMasterPassword = args[i + 1];
                continue;
            }
            if (args[i].equalsIgnoreCase("-sh")) {
                dbSlaveHost = args[i + 1];
                continue;
            }
            if (args[i].equalsIgnoreCase("-su")) {
                dbSlaveUsername = args[i + 1];
                continue;
            }
            if (args[i].equalsIgnoreCase("-sp")) {
                dbSlavePassword = args[i + 1];
                continue;
            }
            if (args[i].equalsIgnoreCase("-r")) {
                startChar1 = args[i + 1].toUpperCase().charAt(0);
                startChar2 = args[i + 1].toUpperCase().charAt(1);
                if (startChar1 >= '!' && startChar2 >= '!' && startChar1 <= '~' && startChar2 <= '~') continue;
                log.error((Object)"Invalid starting prefix");
                return;
            }
            if (args[i].equalsIgnoreCase("-peakstart")) {
                startOfPeak = Integer.parseInt(args[i + 1]);
                continue;
            }
            if (args[i].equalsIgnoreCase("-peakend")) {
                endOfPeak = Integer.parseInt(args[i + 1]);
                continue;
            }
            if (args[i].equalsIgnoreCase("-numthreads")) {
                numThreads = Integer.parseInt(args[i + 1]);
                continue;
            }
            if (args[i].equalsIgnoreCase("-minexpiryamount")) {
                minExpiryAmount = Double.parseDouble(args[i + 1]);
                continue;
            }
            if (!args[i].equalsIgnoreCase("-inactive")) continue;
            inactive = true;
        }
        if (dbMasterHost == null || dbMasterUsername == null || dbMasterPassword == null || dbSlaveHost == null || dbSlaveUsername == null || dbSlavePassword == null) {
            log.error((Object)"Usage: ExpireCredit [-s] [-r starting_prefix] [-peakstart hour] [-peakend hour] [-numthreads n] [-inactive] -mh master_db_host -mu master_db_username -mp master_db_password -sh slave_db_host -su slave_db_username -sp slave_db_password");
            log.error((Object)"   -s : Run in simulation mode");
            log.error((Object)"   -r : Start expiring credit from users with username starting with starting_prefix");
            log.error((Object)" -peakstart : Starting time of peak (when database queries will be slowed down). Specify the hour. e.g., for 14:00 specify 14. Default: 14");
            log.error((Object)" -peakend : Ending time of peak (when database queries will no longer be slowed down). Default: 18 (for 18:00)");
            log.error((Object)" -numthreads : Number of threads we want expiring users' credit concurrently. Default: 2");
            log.error((Object)" -minexpiryamount : If amount to be expired for a user is less than this, the expiry would be skipped for the user. Default: 0.01");
            log.error((Object)"e.g. ExpireCredit -s -r BE -mh 10.3.1.111 -mu fusion -mp fusionpass -sh 10.3.1.19 -su readonly -sp slavepass");
            return;
        }
        ExpireCredit app = new ExpireCredit();
        int status = app.main(APP_NAME, args, CONFIG_FILE);
        log.info((Object)"Terminating");
        System.exit(status);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public int run(String[] arg0) {
        block22: {
            Connection conn;
            usernamesToExclude = ExpireCredit.communicator().getProperties().getProperty("UsernamesToExclude");
            if (StringUtil.isBlank(usernamesToExclude)) {
                usernamesToExclude = "''";
            }
            if (simulationMode) {
                log.info((Object)"** Running in simulation mode **");
            }
            log.info((Object)("Minimum amount to expire: " + minExpiryAmount));
            writer = ExpireCredit.openNewLogFile();
            if (writer == null) {
                return 1;
            }
            String prefix = this.readUsernamePrefixFromFile();
            if (prefix != null) {
                startChar1 = prefix.toUpperCase().charAt(0);
                startChar2 = prefix.toUpperCase().charAt(1);
                if (startChar1 < '!' || startChar2 < '!' || startChar1 > '~' || startChar2 > '~') {
                    log.error((Object)("Invalid starting prefix: " + prefix));
                    return 1;
                }
            }
            log.info((Object)("Will connect to master DB on host " + dbMasterHost + " as " + dbMasterUsername));
            try {
                conn = ExpireCredit.getMasterConnection();
                conn.close();
            }
            catch (Exception e) {
                log.fatal((Object)"Unable to connect to to master DB", (Throwable)e);
                return 1;
            }
            log.info((Object)("Will connect to slave DB on host " + dbSlaveHost + " as " + dbSlaveUsername));
            try {
                conn = ExpireCredit.getSlaveConnection();
                conn.close();
            }
            catch (Exception e) {
                log.fatal((Object)"Unable to connect to to slave DB", (Throwable)e);
                return 1;
            }
            MemCachedClientWrapper.get(MemCachedKeySpaces.CommonKeySpace.ACCOUNT_BALANCE, "test");
            executorService = (ThreadPoolExecutor)Executors.newFixedThreadPool(numThreads);
            log.info((Object)("Running with " + numThreads + " threads"));
            for (char c1 = startChar1; c1 <= '~'; c1 = (char)(c1 + '\u0001')) {
                char c2;
                if (c1 == '\\' || c1 > '`' && c1 < '{') continue;
                char c = c2 = c1 == startChar1 ? startChar2 : (char)'!';
                while (c2 <= '~') {
                    if (c2 != '\\' && (c2 <= '`' || c2 >= '{')) {
                        prefix = c1 == '%' || c1 == '_' ? "\\" + Character.toString(c1) : Character.toString(c1);
                        prefix = c2 == '%' || c2 == '_' ? prefix + "\\" + Character.toString(c2) : prefix + Character.toString(c2);
                        executorService.execute(new ProcessUsersWithPrefix(prefix));
                    }
                    c2 = (char)(c2 + 1);
                }
            }
            executorService.shutdown();
            try {
                executorService.awaitTermination(2592000L, TimeUnit.SECONDS);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.deletePrefixFile();
            log.info((Object)"Credit expiration complete");
            log.info((Object)("" + numProcessed + " users processed"));
            log.info((Object)("Total credit expired: " + df2.format(totalCreditExpired)));
            log.info((Object)("Total funded credit expired: " + df2.format(totalFundedCreditExpired)));
            Object var6_10 = null;
            try {
                writer.close();
            }
            catch (IOException ioe) {
                log.error((Object)("Unable to close output file: " + ioe.getMessage()));
            }
            break block22;
            {
                catch (Exception e) {
                    log.error((Object)("An exception occurred: " + e.toString()));
                    e.printStackTrace();
                    Object var6_11 = null;
                    try {
                        writer.close();
                    }
                    catch (IOException ioe) {
                        log.error((Object)("Unable to close output file: " + ioe.getMessage()));
                    }
                }
            }
            catch (Throwable throwable) {
                Object var6_12 = null;
                try {
                    writer.close();
                }
                catch (IOException ioe) {
                    log.error((Object)("Unable to close output file: " + ioe.getMessage()));
                }
                throw throwable;
            }
        }
        return 0;
    }

    private static BufferedWriter openNewLogFile() {
        try {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String logFileName = "ExpireCredit" + (simulationMode ? "_SIMULATION_" : "") + dateFormatter.format(new Date()) + ".csv";
            File logFile = new File(logFileName);
            logFile.createNewFile();
            log.info((Object)("Creating output file " + logFileName));
            return new BufferedWriter(new OutputStreamWriter((OutputStream)new FileOutputStream(logFile, true), "UTF-8"));
        }
        catch (Exception e) {
            log.error((Object)"Unable to create output file. Exception:");
            e.printStackTrace();
            return null;
        }
    }

    private String readUsernamePrefixFromFile() {
        try {
            File f = new File("prefix");
            if (f.exists()) {
                FileReader fr = new FileReader(f);
                BufferedReader in = new BufferedReader(fr);
                String prefix = in.readLine();
                log.info((Object)("Prefix file found. Starting from username prefix: " + prefix));
                return prefix;
            }
            return null;
        }
        catch (Exception e) {
            log.warn((Object)("Unable to read 'prefix' file: " + e.getMessage()));
            return null;
        }
    }

    private void writeUsernamePrefixToFile(String prefix) {
        try {
            FileWriter fileWriter = new FileWriter("prefix", false);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            writer.write(prefix);
            writer.flush();
            writer.close();
        }
        catch (Exception e) {
            log.warn((Object)("Unable to write prefix " + prefix + " to 'prefix' file: " + e.getMessage()));
        }
    }

    private void deletePrefixFile() {
        File f = new File("prefix");
        if (f.exists()) {
            f.delete();
        }
    }

    private static Connection getMasterConnection() throws Exception {
        if (SystemPropertyEntities.Temp.Cache.SE433_ENABLED.getValue().booleanValue()) {
            return DBUtils.getFusionWriteConnection();
        }
        return DriverManager.getConnection("jdbc:mysql://" + dbMasterHost + "/fusion", dbMasterUsername, dbMasterPassword);
    }

    private static Connection getSlaveConnection() throws Exception {
        if (SystemPropertyEntities.Temp.Cache.SE433_ENABLED.getValue().booleanValue()) {
            return DBUtils.getOlapReadConnection();
        }
        return DriverManager.getConnection("jdbc:mysql://" + dbSlaveHost + "/fusion", dbSlaveUsername, dbSlavePassword);
    }

    static /* synthetic */ Connection access$200() throws Exception {
        return ExpireCredit.getMasterConnection();
    }

    static {
        numProcessed = 0;
        numExpired = 0;
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df2 = new DecimalFormat("0.00");
        df6 = new DecimalFormat("0.000000");
        simulationMode = false;
        totalCreditExpired = 0.0;
        totalFundedCreditExpired = 0.0;
        startChar1 = (char)65;
        startChar2 = (char)45;
        requestCounter = new RequestCounter();
        log = Logger.getLogger((String)ConfigUtils.getLoggerName(ExpireCredit.class));
        startOfPeak = 14;
        endOfPeak = 18;
        numThreads = 4;
        writer = null;
        minExpiryAmount = 0.01;
        inactive = false;
    }

    private class ProcessUsersWithPrefix
    implements Runnable {
        private String prefix;

        public ProcessUsersWithPrefix(String prefix) {
            this.prefix = prefix;
        }

        public void run() {
            try {
                int numCandidates = 0;
                log.info((Object)("Processing users with username starting with '" + this.prefix + "' (" + numProcessed + " users processed, " + numExpired + " expired so far)"));
                ExpireCredit.this.writeUsernamePrefixToFile(this.prefix);
                Connection connSlave = ExpireCredit.getSlaveConnection();
                PreparedStatement psCandidateUsers = inactive ? connSlave.prepareStatement("SELECT Username FROM user WHERE Username LIKE ? AND Balance > 0 AND DateRegistered < DATE_SUB(now(), INTERVAL 90 DAY) AND user.Username NOT IN (?)") : connSlave.prepareStatement("SELECT Username FROM user WHERE Username LIKE ? AND Balance > 0 AND Status = 1 AND DateRegistered < DATE_SUB(now(), INTERVAL 90 DAY) AND user.Username NOT IN (?)");
                psCandidateUsers.setString(1, this.prefix + '%');
                psCandidateUsers.setString(2, usernamesToExclude);
                ResultSet rsCandidateUsers = psCandidateUsers.executeQuery();
                Vector<String> usernames = new Vector<String>();
                while (rsCandidateUsers.next()) {
                    usernames.add(rsCandidateUsers.getString(1));
                }
                rsCandidateUsers.close();
                psCandidateUsers.close();
                connSlave.close();
                numCandidates = usernames.size();
                if (numCandidates == 0) {
                    log.info((Object)("No candidates found for prefix: " + this.prefix));
                    return;
                }
                log.info((Object)("Processing " + numCandidates + " candidates for prefix: " + this.prefix));
                int numExpiredSoFar = numExpired;
                for (String username : usernames) {
                    this.processUser(username);
                    requestCounter.add();
                }
                String msg = "Prefix \"" + this.prefix + "\" done. Processed " + df2.format(requestCounter.getRequestsPerSecond()) + "/s (" + (numExpired - numExpiredSoFar) + " expired). ";
                msg = msg + "Expired so far: $" + df2.format(totalCreditExpired) + " ($" + df2.format(totalFundedCreditExpired) + " funded)";
                log.info((Object)msg);
            }
            catch (Exception e) {
                log.error((Object)("An exception occurred processing prefix \"" + this.prefix + "\": " + e.getMessage()), (Throwable)e);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Unable to fully structure code
         */
        private void processUser(String username) throws Exception {
            block21: {
                block20: {
                    block18: {
                        try {
                            ++ExpireCredit.numProcessed;
                            sql = "SELECT balance.*, maxid.*, newcredit.* FROM (SELECT Balance, FundedBalance, user.Currency UserCurrency, Exchangerate, user.Type FROM user, currency WHERE Username = '" + username + "' AND user.Currency = currency.Code" + ") balance, " + "(" + "SELECT MAX(ID) MaxAccountEntryID, 1 as dummy " + "FROM accountentry " + "WHERE Username='" + username + "'" + ") maxid LEFT OUTER JOIN " + "(" + "SELECT Currency AccountEntryCurrency, SUM(Amount) NewCredit, 1 as dummy " + "FROM accountentry " + "WHERE Username = '" + username + "' AND accountentry.DateCreated >= DATE_SUB(now(), INTERVAL 90 DAY) " + "AND accountentry.Amount > 0 AND accountentry.Type <> 19 " + "GROUP BY Currency" + ") newcredit ON maxid.dummy=newcredit.dummy";
                            connSlave = ExpireCredit.access$100();
                            rsSlaveData = connSlave.createStatement().executeQuery(sql);
                            if (rsSlaveData.next()) break block22;
                            var34_5 = null;
                        }
                        catch (Throwable var33_60) {
                            var34_14 = null;
                            now = Calendar.getInstance();
                            currentHour = now.get(11);
                            if (currentHour >= ExpireCredit.startOfPeak && currentHour < ExpireCredit.endOfPeak) {
                                Thread.sleep(1000L);
                            }
                            throw var33_60;
                        }
                        now = Calendar.getInstance();
                        currentHour = now.get(11);
                        if (currentHour < ExpireCredit.startOfPeak || currentHour >= ExpireCredit.endOfPeak) ** GOTO lbl-1000
                        Thread.sleep(1000L);
lbl-1000:
                        // 2 sources

                        {
                            block22: {
                                return;
                            }
                            balance = rsSlaveData.getDouble("Balance");
                            if (!(balance <= 0.0)) break block23;
                        }
                        var34_6 = null;
                        now = Calendar.getInstance();
                        currentHour = now.get(11);
                        if (currentHour < ExpireCredit.startOfPeak || currentHour >= ExpireCredit.endOfPeak) ** GOTO lbl-1000
                        Thread.sleep(1000L);
lbl-1000:
                        // 2 sources

                        {
                            block23: {
                                return;
                            }
                            fundedBalance = rsSlaveData.getDouble("FundedBalance");
                            userCurrency = rsSlaveData.getString("UserCurrency");
                            exchangeRate = rsSlaveData.getDouble("ExchangeRate");
                            userType = UserData.TypeEnum.fromValue(rsSlaveData.getInt("Type"));
                            maxSlaveAccountEntryID = rsSlaveData.getLong("MaxAccountEntryID");
                            newCredit = 0.0;
                            if (rsSlaveData.getObject("NewCredit") == null) break block24;
                            newCredit = rsSlaveData.getDouble("NewCredit");
                            if (userCurrency.equalsIgnoreCase(rsSlaveData.getString("AccountEntryCurrency"))) break block25;
                            ExpireCredit.log.warn((Object)("Currency mismatch exception: " + username));
                        }
                        var34_7 = null;
                        now = Calendar.getInstance();
                        currentHour = now.get(11);
                        if (currentHour < ExpireCredit.startOfPeak || currentHour >= ExpireCredit.endOfPeak) ** GOTO lbl-1000
                        Thread.sleep(1000L);
lbl-1000:
                        // 2 sources

                        {
                            block25: {
                                return;
                            }
                            if (!rsSlaveData.next()) break block24;
                            ExpireCredit.log.warn((Object)("Multiple currency exception: " + username));
                        }
                        var34_8 = null;
                        now = Calendar.getInstance();
                        currentHour = now.get(11);
                        if (currentHour < ExpireCredit.startOfPeak || currentHour >= ExpireCredit.endOfPeak) ** GOTO lbl-1000
                        Thread.sleep(1000L);
lbl-1000:
                        // 2 sources

                        {
                            block24: {
                                return;
                            }
                            rsSlaveData.close();
                            connSlave.close();
                            if (!(newCredit / exchangeRate >= 100.0)) break block26;
                            if (userType == UserData.TypeEnum.MIG33_MERCHANT) break block18;
                            if (userType != UserData.TypeEnum.MIG33_TOP_MERCHANT) break block26;
                        }
                    }
                    var34_9 = null;
                    now = Calendar.getInstance();
                    currentHour = now.get(11);
                    if (currentHour < ExpireCredit.startOfPeak || currentHour >= ExpireCredit.endOfPeak) ** GOTO lbl-1000
                    Thread.sleep(1000L);
lbl-1000:
                    // 2 sources

                    {
                        block26: {
                            return;
                        }
                        expiration = 0.0;
                        fundedExpiration = 0.0;
                        if (newCredit < balance) {
                            fundedExpiration = newCredit > fundedBalance ? 0.0 : fundedBalance - newCredit;
                            expiration = balance - newCredit;
                        }
                        if (!SystemPropertyEntities.Temp.Cache.SE433_ENABLED.getValue().booleanValue()) break block27;
                        if (!(expiration <= 0.0)) break block28;
                        ExpireCredit.log.debug((Object)("Not expiring user [" + username + "] expiration [" + expiration + "] is less than minimal amount [0]"));
                    }
                    var34_10 = null;
                    now = Calendar.getInstance();
                    currentHour = now.get(11);
                    if (currentHour < ExpireCredit.startOfPeak || currentHour >= ExpireCredit.endOfPeak) ** GOTO lbl-1000
                    Thread.sleep(1000L);
lbl-1000:
                    // 2 sources

                    {
                        block27: {
                            return;
                        }
                        if (!(expiration / exchangeRate < ExpireCredit.minExpiryAmount)) break block28;
                        ExpireCredit.log.debug((Object)("Not expiring user [" + username + "] expiration [" + expiration / exchangeRate + "] is less than minimal amount [" + ExpireCredit.minExpiryAmount + "]"));
                    }
                    var34_11 = null;
                    now = Calendar.getInstance();
                    currentHour = now.get(11);
                    if (currentHour < ExpireCredit.startOfPeak || currentHour >= ExpireCredit.endOfPeak) ** GOTO lbl-1000
                    Thread.sleep(1000L);
lbl-1000:
                    // 2 sources

                    {
                        block28: {
                            return;
                        }
                        connMaster = null;
                        if (ExpireCredit.simulationMode) ** GOTO lbl140
                        connMaster = ExpireCredit.access$200();
                        connMaster.setAutoCommit(false);
                        rsMaxMasterAccountEntryID = connMaster.createStatement().executeQuery("SELECT MAX(ID) FROM accountentry WHERE username='" + username + "' AND ID >= " + maxSlaveAccountEntryID + " FOR UPDATE");
                        if (!rsMaxMasterAccountEntryID.next()) break block19;
                        if (rsMaxMasterAccountEntryID.getLong(1) == maxSlaveAccountEntryID) ** GOTO lbl118
                        ExpireCredit.log.warn((Object)("User did a transaction during expiration: " + username));
                        var32_48 = null;
                        if (connMaster == null) break block20;
                        connMaster.close();
                    }
                }
                var34_12 = null;
                now = Calendar.getInstance();
                currentHour = now.get(11);
                if (currentHour < ExpireCredit.startOfPeak || currentHour >= ExpireCredit.endOfPeak) ** GOTO lbl-1000
                Thread.sleep(1000L);
lbl-1000:
                // 2 sources

                {
                    block19: {
                        return;
                    }
                    try {
                        throw new Exception("Unable to check max accountentry ID on master: " + username);
lbl118:
                        // 1 sources

                        psInsertAccountEntry = connMaster.prepareStatement("INSERT INTO AccountEntry (Username, DateCreated, Type, Reference, Description, Amount, FundedAmount, Tax, Currency, ExchangeRate) VALUES (?,now(),24,?,'Expiration of credit older than 90 days',?,?,0,?,?)");
                        psInsertAccountEntry.setString(1, username);
                        psInsertAccountEntry.setString(2, username);
                        psInsertAccountEntry.setDouble(3, -expiration);
                        psInsertAccountEntry.setDouble(4, -fundedExpiration);
                        psInsertAccountEntry.setString(5, userCurrency);
                        psInsertAccountEntry.setDouble(6, exchangeRate);
                        if (psInsertAccountEntry.executeUpdate() != 1) {
                            throw new Exception("ERROR: Unable to insert an expiration account entry. Username: " + username);
                        }
                        psInsertAccountEntry.close();
                        psUpdateBalance = connMaster.prepareStatement("UPDATE User SET Balance = ?, FundedBalance = ? WHERE Username = ?");
                        psUpdateBalance.setDouble(1, balance - expiration);
                        psUpdateBalance.setDouble(2, fundedBalance - fundedExpiration);
                        psUpdateBalance.setString(3, username);
                        if (psUpdateBalance.executeUpdate() != 1) {
                            throw new Exception("ERROR: Unable to update a user's balance. Username: " + username);
                        }
                        psUpdateBalance.close();
                        MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.ACCOUNT_BALANCE, username);
                        connMaster.commit();
                        connMaster.close();
                        connMaster = null;
lbl140:
                        // 2 sources

                        balanceBeforeExpiration = balance / exchangeRate;
                        fundedBalanceBeforeExpiration = fundedBalance / exchangeRate;
                        balanceAfterExpiration = (balance - expiration) / exchangeRate;
                        fundedBalanceAfterExpiration = (fundedBalance - fundedExpiration) / exchangeRate;
                        try {
                            sb = new StringBuilder();
                            sb.append("\"");
                            sb.append(username.replaceAll("\"", "\"\""));
                            sb.append("\",");
                            sb.append(ExpireCredit.df6.format(balanceBeforeExpiration));
                            sb.append(',');
                            sb.append(ExpireCredit.df6.format(fundedBalanceBeforeExpiration));
                            sb.append(',');
                            sb.append(ExpireCredit.df6.format(balanceAfterExpiration));
                            sb.append(',');
                            sb.append(ExpireCredit.df6.format(fundedBalanceAfterExpiration));
                            ExpireCredit.writer.write(sb.toString());
                            ExpireCredit.writer.newLine();
                            ExpireCredit.writer.flush();
                        }
                        catch (IOException e) {
                            throw new Exception("Unable to log credit expiry. Username: " + username + " Exception: " + e.getMessage());
                        }
                        ++ExpireCredit.numExpired;
                        ExpireCredit.totalCreditExpired += expiration / exchangeRate;
                        ExpireCredit.totalFundedCreditExpired += fundedExpiration / exchangeRate;
                        var32_49 = null;
                        ** if (connMaster == null) goto lbl-1000
                    }
                    catch (Throwable var31_59) {
                        var32_51 = null;
                        if (connMaster != null) {
                            connMaster.close();
                        }
                        throw var31_59;
                    }
lbl-1000:
                    // 1 sources

                    {
                        connMaster.close();
                    }
lbl-1000:
                    // 2 sources

                    {
                        break block21;
                        catch (Exception e) {
                            if (connMaster != null) {
                                connMaster.rollback();
                            }
                            ExpireCredit.log.error((Object)("Exception processing the user " + username + ": " + e.getMessage()));
                            var32_50 = null;
                            if (connMaster != null) {
                                connMaster.close();
                            }
                        }
                    }
                }
            }
            var34_13 = null;
            now = Calendar.getInstance();
            currentHour = now.get(11);
            if (currentHour >= ExpireCredit.startOfPeak && currentHour < ExpireCredit.endOfPeak) {
                Thread.sleep(1000L);
            }
        }
    }
}

