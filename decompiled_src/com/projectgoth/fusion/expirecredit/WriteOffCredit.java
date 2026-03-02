/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Application
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.expirecredit;

import Ice.Application;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.MemCachedClientWrapper;
import com.projectgoth.fusion.common.MemCachedKeySpaces;
import com.projectgoth.fusion.common.RequestCounter;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.AccountEntryData;
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
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

public class WriteOffCredit
extends Application {
    private static final String APP_NAME = "WriteOffCredit";
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
    static String dateParam;

    public static void main(String[] args) {
        log.info((Object)"WriteOffCredit starting");
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
            if (!args[i].equalsIgnoreCase("-date")) continue;
            dateParam = args[i + 1];
        }
        if (dbMasterHost == null || dbMasterUsername == null || dbMasterPassword == null || dbSlaveHost == null || dbSlaveUsername == null || dbSlavePassword == null) {
            log.error((Object)"Usage: WriteOffCredit [-s] [-r starting_prefix] [-peakstart hour] [-peakend hour] [-numthreads n] -mh master_db_host -mu master_db_username -mp master_db_password -sh slave_db_host -su slave_db_username -sp slave_db_password");
            log.error((Object)"   -s : Run in simulation mode");
            log.error((Object)"   -r : Start writing-off credit from users with username starting with starting_prefix");
            log.error((Object)" -peakstart : Starting time of peak (when database queries will be slowed down). Specify the hour. e.g., for 14:00 specify 14. Default: 14");
            log.error((Object)" -peakend : Ending time of peak (when database queries will no longer be slowed down). Default: 18 (for 18:00)");
            log.error((Object)" -numthreads : Number of threads we want writing-off users' credit concurrently. Default: 2");
            log.error((Object)"e.g. WriteOffCredit -s -r BE -mh 10.3.1.111 -mu fusion -mp fusionpass -sh 10.3.1.19 -su readonly -sp slavepass");
            return;
        }
        WriteOffCredit app = new WriteOffCredit();
        int status = app.main(APP_NAME, args);
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
            String prefix;
            usernamesToExclude = WriteOffCredit.communicator().getProperties().getProperty("UsernamesToExclude");
            if (StringUtil.isBlank(usernamesToExclude)) {
                usernamesToExclude = "''";
            }
            if (simulationMode) {
                log.info((Object)"** Running in simulation mode **");
            }
            if ((prefix = this.readUsernamePrefixFromFile()) != null) {
                startChar1 = prefix.toUpperCase().charAt(0);
                startChar2 = prefix.toUpperCase().charAt(1);
                if (startChar1 < '!' || startChar2 < '!' || startChar1 > '~' || startChar2 > '~') {
                    log.error((Object)("Invalid starting prefix: " + prefix));
                    return 1;
                }
            }
            if ((writer = WriteOffCredit.openNewLogFile()) == null) {
                return 1;
            }
            log.info((Object)("Will connect to master DB on host " + dbMasterHost + " as " + dbMasterUsername));
            try {
                conn = WriteOffCredit.getMasterConnection();
                conn.close();
            }
            catch (Exception e) {
                log.fatal((Object)"Unable to connect to to master DB", (Throwable)e);
                return 1;
            }
            log.info((Object)("Will connect to slave DB on host " + dbSlaveHost + " as " + dbSlaveUsername));
            try {
                conn = WriteOffCredit.getSlaveConnection();
                conn.close();
            }
            catch (Exception e) {
                log.fatal((Object)"Unable to connect to to slave DB", (Throwable)e);
                return 1;
            }
            MemCachedClientWrapper.get(MemCachedKeySpaces.CommonKeySpace.ACCOUNT_BALANCE, "test");
            executorService = (ThreadPoolExecutor)Executors.newFixedThreadPool(numThreads);
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
            log.info((Object)"Credit write-off complete");
            log.info((Object)("" + numProcessed + " users processed"));
            log.info((Object)("Total credit written off: " + df2.format(totalCreditExpired)));
            log.info((Object)("Total funded credit written off: " + df2.format(totalFundedCreditExpired)));
            Object var6_10 = null;
            try {
                writer.close();
            }
            catch (IOException e2) {
                log.error((Object)("Unable to close output file: " + e2.getMessage()));
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
                    catch (IOException e2) {
                        log.error((Object)("Unable to close output file: " + e2.getMessage()));
                    }
                }
            }
            catch (Throwable throwable) {
                Object var6_12 = null;
                try {
                    writer.close();
                }
                catch (IOException e2) {
                    log.error((Object)("Unable to close output file: " + e2.getMessage()));
                }
                throw throwable;
            }
        }
        return 0;
    }

    private static BufferedWriter openNewLogFile() {
        try {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String logFileName = APP_NAME + (simulationMode ? "_SIMULATION_" : "") + dateFormatter.format(new java.util.Date()) + ".csv";
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

    private synchronized void writeLineToOutputFile(String line) {
        try {
            writer.write(line);
            writer.newLine();
            writer.flush();
        }
        catch (IOException e) {
            log.error((Object)"Unable to write to log file", (Throwable)e);
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
        return DriverManager.getConnection("jdbc:mysql://" + dbMasterHost + "/fusion", dbMasterUsername, dbMasterPassword);
    }

    private static Connection getSlaveConnection() throws Exception {
        return DriverManager.getConnection("jdbc:mysql://" + dbSlaveHost + "/fusion", dbSlaveUsername, dbSlavePassword);
    }

    static /* synthetic */ Connection access$200() throws Exception {
        return WriteOffCredit.getMasterConnection();
    }

    static /* synthetic */ void access$300(WriteOffCredit x0, String x1) {
        x0.writeLineToOutputFile(x1);
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
        startChar1 = (char)33;
        startChar2 = (char)33;
        requestCounter = new RequestCounter();
        log = Logger.getLogger((String)ConfigUtils.getLoggerName(WriteOffCredit.class));
        startOfPeak = 14;
        endOfPeak = 18;
        numThreads = 2;
        writer = null;
        dateParam = "2010-07-01";
    }

    private class ProcessUsersWithPrefix
    implements Runnable {
        private String prefix;

        public ProcessUsersWithPrefix(String prefix) {
            this.prefix = prefix;
        }

        public void run() {
            try {
                PreparedStatement psCandidateUsers;
                int numCandidates = 0;
                log.info((Object)("Processing users with username starting with '" + this.prefix + "' (" + numProcessed + " users processed, " + numExpired + " written off so far)"));
                WriteOffCredit.this.writeUsernamePrefixToFile(this.prefix);
                Connection connSlave = WriteOffCredit.getSlaveConnection();
                if (SystemPropertyEntities.Temp.Cache.ER74_ENABLED.getValue().booleanValue()) {
                    psCandidateUsers = connSlave.prepareStatement("SELECT Username FROM user WHERE Username LIKE ? AND Balance > 0 AND Status = 0 AND LastLoginDate < ? AND user.Username NOT IN (?)");
                    psCandidateUsers.setString(1, this.prefix + '%');
                    psCandidateUsers.setDate(2, Date.valueOf(dateParam));
                    psCandidateUsers.setString(3, usernamesToExclude);
                } else {
                    psCandidateUsers = connSlave.prepareStatement("SELECT Username FROM user WHERE Username LIKE ? AND Balance > 0 AND Status = 0 AND LastLoginDate < '2010-07-01' AND user.Username NOT IN (?)");
                    psCandidateUsers.setString(1, this.prefix + '%');
                    psCandidateUsers.setString(2, usernamesToExclude);
                }
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
                String msg = "Prefix \"" + this.prefix + "\" done. Processed " + df2.format(requestCounter.getRequestsPerSecond()) + "/s (" + (numExpired - numExpiredSoFar) + " written off). ";
                msg = msg + "Written off so far: $" + df2.format(totalCreditExpired) + " ($" + df2.format(totalFundedCreditExpired) + " funded)";
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
            block17: {
                block16: {
                    try {
                        ++WriteOffCredit.numProcessed;
                        escapedUsername = username.replaceAll("'", "\\\\'");
                        sql = "SELECT balance.*, maxid.* FROM (SELECT Balance, FundedBalance, user.Currency UserCurrency, Exchangerate, user.Type FROM user, currency WHERE Username = '" + escapedUsername + "' AND user.Currency = currency.Code" + ") balance, " + "(" + "SELECT MAX(ID) MaxAccountEntryID " + "FROM accountentry " + "WHERE Username='" + escapedUsername + "'" + ") maxid";
                        connSlave = WriteOffCredit.access$100();
                        rsSlaveData = connSlave.createStatement().executeQuery(sql);
                        if (rsSlaveData.next()) break block18;
                        var26_6 = null;
                    }
                    catch (Throwable var25_38) {
                        var26_10 = null;
                        now = Calendar.getInstance();
                        currentHour = now.get(11);
                        if (currentHour >= WriteOffCredit.startOfPeak && currentHour < WriteOffCredit.endOfPeak) {
                            Thread.sleep(1000L);
                        }
                        throw var25_38;
                    }
                    now = Calendar.getInstance();
                    currentHour = now.get(11);
                    if (currentHour < WriteOffCredit.startOfPeak || currentHour >= WriteOffCredit.endOfPeak) ** GOTO lbl-1000
                    Thread.sleep(1000L);
lbl-1000:
                    // 2 sources

                    {
                        block18: {
                            return;
                        }
                        balance = rsSlaveData.getDouble("Balance");
                        if (!(balance <= 0.0)) break block19;
                    }
                    var26_7 = null;
                    now = Calendar.getInstance();
                    currentHour = now.get(11);
                    if (currentHour < WriteOffCredit.startOfPeak || currentHour >= WriteOffCredit.endOfPeak) ** GOTO lbl-1000
                    Thread.sleep(1000L);
lbl-1000:
                    // 2 sources

                    {
                        block19: {
                            return;
                        }
                        fundedBalance = rsSlaveData.getDouble("FundedBalance");
                        userCurrency = rsSlaveData.getString("UserCurrency");
                        exchangeRate = rsSlaveData.getDouble("ExchangeRate");
                        maxSlaveAccountEntryID = rsSlaveData.getLong("MaxAccountEntryID");
                        rsSlaveData.close();
                        connSlave.close();
                        expiration = balance;
                        fundedExpiration = fundedBalance;
                        connMaster = null;
                        if (WriteOffCredit.simulationMode) ** GOTO lbl82
                        connMaster = WriteOffCredit.access$200();
                        connMaster.setAutoCommit(false);
                        rsMaxMasterAccountEntryID = connMaster.createStatement().executeQuery("SELECT MAX(ID) FROM accountentry WHERE username='" + escapedUsername + "' FOR UPDATE");
                        if (!rsMaxMasterAccountEntryID.next()) break block15;
                        if (rsMaxMasterAccountEntryID.getLong(1) == maxSlaveAccountEntryID) ** GOTO lbl59
                        WriteOffCredit.log.warn((Object)("User did a transaction during write-off: " + username));
                        var24_31 = null;
                        if (connMaster == null) break block16;
                        connMaster.close();
                    }
                }
                var26_8 = null;
                now = Calendar.getInstance();
                currentHour = now.get(11);
                if (currentHour < WriteOffCredit.startOfPeak || currentHour >= WriteOffCredit.endOfPeak) ** GOTO lbl-1000
                Thread.sleep(1000L);
lbl-1000:
                // 2 sources

                {
                    block15: {
                        return;
                    }
                    try {
                        throw new Exception("Unable to check max accountentry ID on master: " + username);
lbl59:
                        // 1 sources

                        psInsertAccountEntry = connMaster.prepareStatement("INSERT INTO AccountEntry (Username, DateCreated, Type, Reference, Description, Amount, FundedAmount, Tax, Currency, ExchangeRate) VALUES (?,now(),?,?,'Credit Adjustment',?,?,0,?,?)");
                        psInsertAccountEntry.setString(1, username);
                        psInsertAccountEntry.setInt(2, AccountEntryData.TypeEnum.CREDIT_WRITE_OFF.value());
                        psInsertAccountEntry.setString(3, username);
                        psInsertAccountEntry.setDouble(4, -expiration);
                        psInsertAccountEntry.setDouble(5, -fundedExpiration);
                        psInsertAccountEntry.setString(6, userCurrency);
                        psInsertAccountEntry.setDouble(7, exchangeRate);
                        if (psInsertAccountEntry.executeUpdate() != 1) {
                            throw new Exception("ERROR: Unable to insert a write-off account entry. Username: " + username);
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
lbl82:
                        // 2 sources

                        sb = new StringBuilder();
                        sb.append("\"");
                        sb.append(username.replaceAll("\"", "\"\""));
                        sb.append("\",\"");
                        sb.append(WriteOffCredit.dateFormatter.format(new java.util.Date()));
                        sb.append("\",");
                        sb.append(WriteOffCredit.df6.format(balance));
                        sb.append(',');
                        sb.append(WriteOffCredit.df6.format(fundedBalance));
                        sb.append(',');
                        sb.append(WriteOffCredit.df6.format(exchangeRate));
                        sb.append(",\"");
                        sb.append(userCurrency);
                        sb.append("\",");
                        sb.append(WriteOffCredit.df6.format(balance / exchangeRate));
                        sb.append(',');
                        sb.append(WriteOffCredit.df6.format(fundedBalance / exchangeRate));
                        WriteOffCredit.access$300(WriteOffCredit.this, sb.toString());
                        ++WriteOffCredit.numExpired;
                        WriteOffCredit.totalCreditExpired += expiration / exchangeRate;
                        WriteOffCredit.totalFundedCreditExpired += fundedExpiration / exchangeRate;
                        var24_32 = null;
                        ** if (connMaster == null) goto lbl-1000
                    }
                    catch (Throwable var23_37) {
                        var24_34 = null;
                        if (connMaster != null) {
                            connMaster.close();
                        }
                        throw var23_37;
                    }
lbl-1000:
                    // 1 sources

                    {
                        connMaster.close();
                    }
lbl-1000:
                    // 2 sources

                    {
                        break block17;
                        catch (Exception e) {
                            if (connMaster != null) {
                                connMaster.rollback();
                            }
                            WriteOffCredit.log.error((Object)("Exception processing the user " + username + ": " + e.getMessage()));
                            var24_33 = null;
                            if (connMaster != null) {
                                connMaster.close();
                            }
                        }
                    }
                }
            }
            var26_9 = null;
            now = Calendar.getInstance();
            currentHour = now.get(11);
            if (currentHour >= WriteOffCredit.startOfPeak && currentHour < WriteOffCredit.endOfPeak) {
                Thread.sleep(1000L);
            }
        }
    }
}

