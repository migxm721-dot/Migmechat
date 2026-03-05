/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.net.ftp.FTPClient
 *  org.apache.commons.net.ftp.FTPClientConfig
 *  org.apache.commons.net.ftp.FTPFile
 *  org.apache.commons.net.ftp.FTPReply
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.chatnewsfeed.util;

import com.projectgoth.fusion.chatnewsfeed.util.ExtensionFilter;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.DateTimeUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FileUtil {
    private static final Logger logger = Logger.getLogger((String)ConfigUtils.getLoggerName(FileUtil.class));

    public static void downloadFile(String resourceURL, File file) {
        try {
            String line;
            URL url = new URL(resourceURL);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            while ((line = reader.readLine()) != null) {
                writer.write(line);
            }
            writer.close();
            reader.close();
            logger.info((Object)("Successfully downloaded " + file.getName()));
        }
        catch (MalformedURLException e) {
            logger.error((Object)("Invalid URL :" + resourceURL + "\n" + e.getMessage()));
        }
        catch (IOException e) {
            logger.error((Object)("Error downloading file \n" + e.getMessage()));
        }
    }

    public static Document getDocument(DocumentBuilderFactory xmlFactory, File xmlSourceFile) {
        Document xmlDocument = null;
        try {
            xmlDocument = xmlFactory.newDocumentBuilder().parse(xmlSourceFile);
        }
        catch (SAXException e) {
            logger.error((Object)("Parsing error on file " + xmlSourceFile), (Throwable)e);
        }
        catch (IOException e) {
            logger.error((Object)("Parsing error on file " + xmlSourceFile), (Throwable)e);
        }
        catch (ParserConfigurationException e) {
            logger.error((Object)("Parsing error on file " + xmlSourceFile), (Throwable)e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug((Object)("Successfully retrieved XML from " + xmlSourceFile));
        }
        return xmlDocument;
    }

    public static void deleteOldLocalFiles(String directory, List<String> fileTypes, List<String> filesToExclude) {
        logger.info((Object)"Starting local directory cleanup... ");
        Date midnightYesterday = DateTimeUtils.midnightYesterday();
        ExtensionFilter filter = new ExtensionFilter(fileTypes, filesToExclude);
        File dir = new File(directory);
        String[] files = dir.list(filter);
        if (files != null) {
            if (logger.isInfoEnabled()) {
                logger.info((Object)("Files matched at: " + directory + " for extensions: " + fileTypes));
                FileUtil.listFiles(files);
            }
            for (int i = 0; i < files.length; ++i) {
                File file = new File(directory, files[i]);
                String fileName = file.getName();
                Date lastModifiedDate = new Date(file.lastModified());
                if (!lastModifiedDate.before(midnightYesterday)) continue;
                if (logger.isInfoEnabled()) {
                    logger.info((Object)("'" + fileName + "' - dated " + DateTimeUtils.dateToString(lastModifiedDate, "MM/dd/yyyy HH:mm:ss")));
                }
                boolean deleted = false;
                deleted = file.delete();
                if (deleted) {
                    logger.info((Object)("Deleted: " + fileName));
                    continue;
                }
                logger.error((Object)("Could not delete '" + fileName + "'"));
            }
        } else {
            logger.info((Object)"No matches found in local directory");
        }
        if (logger.isInfoEnabled()) {
            FileUtil.drawLine();
            logger.info((Object)"Local directory after cleanup:");
            FileUtil.listFiles(dir.listFiles());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public static void deleteOldRemoteFiles(String ftpServer, String serverTimeZone, String path, String user, String password, List<String> fileTypes, List<String> filesToExclude) {
        block15: {
            FTPClient client = new FTPClient();
            boolean isConnected = false;
            logger.info((Object)("Starting remote directory cleanup. Remote server timezone is: " + serverTimeZone + "..."));
            try {
                isConnected = FileUtil.configureFTPConnection(ftpServer, user, password, client);
            }
            catch (Exception e) {
                logger.error((Object)("Error connecting to FTP server " + ftpServer));
            }
            if (isConnected) {
                FTPFile[] files = client.listFiles(path);
                Date midnightYesterday = DateTimeUtils.midnightYesterday();
                if (files != null) {
                    if (logger.isInfoEnabled()) {
                        logger.info((Object)("Remote directory before cleanup: " + ftpServer + path));
                        FileUtil.listFiles(files);
                        logger.info((Object)"Checking files...");
                    }
                    for (int i = 0; i < files.length; ++i) {
                        boolean isDeletable;
                        if (files[i] == null || !files[i].isFile()) continue;
                        String fileName = files[i].getName();
                        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
                        Calendar lastModifiedDate = files[i].getTimestamp();
                        Date lastModifiedDateAdjusted = DateTimeUtils.getDateCurrentTimeZone(lastModifiedDate, serverTimeZone.trim());
                        boolean bl = isDeletable = FileUtil.isValidFileExtension(fileTypes, fileExtension) && filesToExclude != null && !filesToExclude.isEmpty() && !filesToExclude.contains(fileName.trim()) && lastModifiedDateAdjusted.before(midnightYesterday);
                        if (!isDeletable) continue;
                        FileUtil.deleteRemoteFile(client, path.substring(path.indexOf("/") + 1), fileName, lastModifiedDate, lastModifiedDateAdjusted);
                    }
                }
                if (logger.isInfoEnabled()) {
                    FileUtil.drawLine();
                    logger.info((Object)"Remote directory after cleanup: ");
                    FileUtil.listFiles(client.listFiles(path));
                }
                client.logout();
                Object var18_19 = null;
                try {
                    client.disconnect();
                }
                catch (IOException e2) {
                    e2.printStackTrace();
                }
                break block15;
                {
                    catch (IOException e) {
                        e.printStackTrace();
                        Object var18_20 = null;
                        try {
                            client.disconnect();
                        }
                        catch (IOException e2) {
                            e2.printStackTrace();
                        }
                    }
                }
                catch (Throwable throwable) {
                    Object var18_21 = null;
                    try {
                        client.disconnect();
                    }
                    catch (IOException e2) {
                        e2.printStackTrace();
                    }
                    throw throwable;
                }
            }
        }
    }

    private static void drawLine() {
        logger.info((Object)"-------------------------------------------------------");
    }

    private static boolean configureFTPConnection(String ftpServer, String user, String password, FTPClient client) throws SocketException, IOException {
        FTPClientConfig conf = new FTPClientConfig("UNIX");
        conf.setLenientFutureDates(true);
        client.configure(conf);
        client.setConnectTimeout(30000);
        client.connect(ftpServer);
        logger.info((Object)("Connected to " + ftpServer));
        client.login(user, password);
        int reply = client.getReplyCode();
        if (!FTPReply.isPositiveCompletion((int)reply)) {
            client.disconnect();
            logger.error((Object)("Remote FTP server " + ftpServer + " refused connection for user: " + user));
            return false;
        }
        logger.info((Object)("Logged in to " + ftpServer));
        return true;
    }

    private static void deleteRemoteFile(FTPClient client, String path, String fileName, Calendar lastModifiedDate, Date lastModifiedDateAdjusted) {
        if (logger.isInfoEnabled()) {
            logger.info((Object)("Attempt to delete: '" + fileName + "' - dated " + DateTimeUtils.dateToString(lastModifiedDateAdjusted, "MM/dd/yyyy HH:mm:ss,") + "(Server timestamp: " + DateTimeUtils.dateToString(lastModifiedDate.getTime(), "MM/dd/yyyy HH:mm:ss") + ")"));
        }
        try {
            boolean deleted = client.deleteFile(path + "/" + fileName);
            if (deleted) {
                logger.info((Object)("Deleted '" + fileName + "'"));
            } else {
                logger.info((Object)("Could not delete '" + path + "/" + fileName + "'"));
            }
        }
        catch (Exception e) {
            logger.error((Object)("Could not delete '" + path + "/" + fileName + "'"), (Throwable)e);
        }
    }

    private static boolean isValidFileExtension(List<String> fileTypes, String type) {
        boolean isValidExtension = false;
        if (fileTypes != null) {
            for (String fileType : fileTypes) {
                if (!fileType.equals(type)) continue;
                isValidExtension = true;
                break;
            }
        }
        return isValidExtension;
    }

    private static void listFiles(File[] files) {
        if (files != null) {
            for (File file : files) {
                logger.info((Object)file.getName());
            }
        }
        FileUtil.drawLine();
    }

    private static void listFiles(String[] files) {
        if (files != null) {
            for (String fileName : files) {
                logger.info((Object)fileName);
            }
        }
        FileUtil.drawLine();
    }

    private static void listFiles(FTPFile[] files) {
        if (files != null) {
            for (FTPFile file : files) {
                logger.info((Object)file.getName());
            }
        }
        FileUtil.drawLine();
    }

    public static void main(String[] args) {
        String server = "kundulu.com";
        String path = "/feeds-test";
        String user = "sportzinteractive";
        String password = "W57pTD.a";
        ArrayList<String> fileTypes = new ArrayList<String>();
        fileTypes.add(".xml");
        ArrayList<String> filesToExclude = new ArrayList<String>();
        filesToExclude.add("calendar_new.xml");
        server = "ftp.sportzinteractive.feed.mig33.com";
        path = "/feeds/cricket";
        user = "sportz";
        password = "steer78_pact";
        for (int i = 0; i < 20; ++i) {
            logger.info((Object)("Running iteration :" + i));
            FileUtil.deleteOldRemoteFiles(server, "Canada/Central", path, user, password, fileTypes, filesToExclude);
        }
    }
}

