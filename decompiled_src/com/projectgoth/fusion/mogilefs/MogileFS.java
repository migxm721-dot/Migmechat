/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.mogilefs;

import com.projectgoth.fusion.mogilefs.Backend;
import com.projectgoth.fusion.mogilefs.BadHostFormatException;
import com.projectgoth.fusion.mogilefs.MogileException;
import com.projectgoth.fusion.mogilefs.MogileOutputStream;
import com.projectgoth.fusion.mogilefs.NoTrackersException;
import com.projectgoth.fusion.mogilefs.StorageCommunicationException;
import com.projectgoth.fusion.mogilefs.TrackerCommunicationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MogileFS {
    private Logger log;
    private String domain;
    private List trackers;
    private Backend backend;
    private int maxRetries = -1;
    private int retrySleepTime = 2000;

    public MogileFS(String domain, String[] trackerStrings, boolean connectNow, Logger logger) throws NoTrackersException, BadHostFormatException {
        this.log = logger;
        this.trackers = this.parseHosts(trackerStrings);
        this.reload(domain, connectNow);
    }

    private List<InetSocketAddress> parseHosts(String[] hostStrings) throws BadHostFormatException {
        ArrayList<InetSocketAddress> list = new ArrayList<InetSocketAddress>(hostStrings.length);
        Pattern hostAndPortPattern = Pattern.compile("^(\\S+):(\\d+)$");
        if (hostStrings != null) {
            for (int i = 0; i < hostStrings.length; ++i) {
                Matcher m = hostAndPortPattern.matcher(hostStrings[i]);
                if (!m.matches()) {
                    throw new BadHostFormatException(hostStrings[i]);
                }
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)("parsed tracker " + hostStrings[i]));
                }
                InetSocketAddress addr = new InetSocketAddress(m.group(1), Integer.parseInt(m.group(2)));
                list.add(addr);
            }
        }
        return list;
    }

    public void reload(String domain, String[] trackerStrings, boolean connectNow) throws NoTrackersException, BadHostFormatException {
        this.trackers = this.parseHosts(trackerStrings);
        this.reload(domain, connectNow);
    }

    private void reload(String domain, boolean connectNow) throws NoTrackersException {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("connecting with domain " + domain));
        }
        this.domain = domain;
        this.backend = new Backend(this.trackers, connectNow, this.log);
    }

    public void reconnect() throws NoTrackersException {
        this.backend = new Backend(this.trackers, true, this.log);
    }

    public MogileOutputStream newFile(String key, String storageClass, long byteCount) throws NoTrackersException, TrackerCommunicationException, StorageCommunicationException {
        Map response = this.backend.doRequest("create_open", new String[]{"domain", this.domain, "class", storageClass, "key", key});
        if (response == null) {
            this.log.warn((Object)("null response from backend: " + this.backend.getLastErr() + ", " + this.backend.getLastErrStr()));
            throw new TrackerCommunicationException(this.backend.getLastErr() + ", " + this.backend.getLastErrStr());
        }
        if (response.get("path") == null || response.get("fid") == null) {
            this.log.warn((Object)("create_open response from backend " + this.backend.getTracker() + " missing fid or path"));
            throw new TrackerCommunicationException("create_open response from tracker " + this.backend.getTracker() + " missing fid or path (err:" + this.backend.getLastErr() + ", " + this.backend.getLastErrStr() + ")");
        }
        try {
            return new MogileOutputStream(this.backend, this.domain, (String)response.get("fid"), (String)response.get("path"), (String)response.get("devid"), key, byteCount);
        }
        catch (MalformedURLException e) {
            this.log.warn((Object)("error trying to store file with malformed url: " + response.get("path")));
            throw new TrackerCommunicationException("error trying to store file with malformed url: " + response.get("path"));
        }
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public void setRetryTimeout(int retrySleepTime) {
        this.retrySleepTime = retrySleepTime;
    }

    public void storeFile(String key, String storageClass, File file) throws MogileException {
        int attempt = 1;
        while (this.maxRetries == -1 || attempt++ <= this.maxRetries) {
            block11: {
                try {
                    Map response = this.backend.doRequest("create_open", new String[]{"domain", this.domain, "class", storageClass, "key", key});
                    if (response == null) {
                        this.log.warn((Object)("problem talking to backend: " + this.backend.getLastErrStr() + " (err: " + this.backend.getLastErr() + ")"));
                        break block11;
                    }
                    try {
                        MogileOutputStream out = new MogileOutputStream(this.backend, this.domain, (String)response.get("fid"), (String)response.get("path"), (String)response.get("devid"), key, file.length());
                        FileInputStream in = new FileInputStream(file);
                        byte[] buffer = new byte[4096];
                        int count = 0;
                        while ((count = in.read(buffer)) >= 0) {
                            out.write(buffer, 0, count);
                        }
                        out.close();
                        in.close();
                        return;
                    }
                    catch (MalformedURLException e) {
                        this.log.warn((Object)("error trying to retrieve file with malformed url: " + response.get("path")));
                    }
                    catch (IOException e) {
                        this.log.warn((Object)"error trying to store file", (Throwable)e);
                    }
                    if (this.retrySleepTime > 0) {
                        try {
                            Thread.sleep(this.retrySleepTime);
                        }
                        catch (Exception e) {}
                    }
                }
                catch (MogileException e) {
                    this.log.warn((Object)"problem trying to store file on mogile", (Throwable)e);
                }
            }
            this.log.info((Object)("Error storing file to mogile - attempting to reconnect and try again (attempt #" + attempt + ")"));
            this.reconnect();
        }
        throw new MogileException("Unable to store file on mogile after multiple attempts");
    }

    public File getFile(String key, File destination) throws NoTrackersException, TrackerCommunicationException, IOException, StorageCommunicationException {
        InputStream in = this.getFileStream(key);
        if (in == null) {
            return null;
        }
        FileOutputStream out = new FileOutputStream(destination);
        byte[] buffer = new byte[4096];
        int count = 0;
        while ((count = in.read(buffer)) >= 0) {
            out.write(buffer, 0, count);
        }
        out.close();
        in.close();
        return destination;
    }

    public byte[] getFileBytes(String key) throws NoTrackersException, TrackerCommunicationException, IOException, StorageCommunicationException {
        String[] paths = this.getPaths(key, false);
        if (paths == null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("couldn't find paths for " + key));
            }
            return null;
        }
        int startIndex = (int)Math.floor(Math.random() * (double)paths.length);
        int tries = paths.length;
        while (tries-- > 0) {
            String path = paths[startIndex++ % paths.length];
            try {
                URL pathURL = new URL(path);
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)("retrieving file from " + path + " (attempt #" + (paths.hashCode() - tries) + ")"));
                }
                HttpURLConnection conn = (HttpURLConnection)pathURL.openConnection();
                InputStream in = conn.getInputStream();
                byte[] bytes = new byte[conn.getContentLength()];
                int count = 0;
                for (int offset = 0; offset < bytes.length && (count = in.read(bytes, offset, bytes.length - offset)) > 0; offset += count) {
                }
                return bytes;
            }
            catch (IOException e) {
                this.log.warn((Object)("problem reading file from " + path));
            }
        }
        StringBuffer pathString = new StringBuffer();
        for (int i = 0; i < paths.length; ++i) {
            if (i > 0) {
                pathString.append(", ");
            }
            pathString.append(paths[i]);
        }
        throw new StorageCommunicationException("unable to retrieve file from any storage node: " + pathString);
    }

    public InputStream getFileStream(String key) throws NoTrackersException, TrackerCommunicationException, StorageCommunicationException {
        String[] paths = this.getPaths(key, false);
        if (paths == null) {
            return null;
        }
        int startIndex = (int)Math.floor(Math.random() * (double)paths.length);
        int tries = paths.length;
        while (tries-- > 0) {
            String path = paths[startIndex++ % paths.length];
            try {
                URL pathURL = new URL(path);
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)("retrieving file from " + path + " (attempt #" + (paths.hashCode() - tries) + ")"));
                }
                return pathURL.openStream();
            }
            catch (IOException e) {
                this.log.warn((Object)("problem reading file from " + path));
            }
        }
        StringBuffer pathString = new StringBuffer();
        for (int i = 0; i < paths.length; ++i) {
            if (i > 0) {
                pathString.append(", ");
            }
            pathString.append(paths[i]);
        }
        throw new StorageCommunicationException("unable to retrieve file with key '" + key + "' from any storage node: " + pathString);
    }

    public void delete(String key) throws NoTrackersException, NoTrackersException {
        int attempt = 1;
        while (this.maxRetries == -1 || attempt++ <= this.maxRetries) {
            try {
                this.backend.doRequest("delete", new String[]{"domain", this.domain, "key", key});
                return;
            }
            catch (TrackerCommunicationException e) {
                this.log.warn((Object)e);
                if (this.retrySleepTime > 0) {
                    try {
                        Thread.sleep(this.retrySleepTime);
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
                this.reconnect();
            }
        }
        throw new NoTrackersException();
    }

    public void sleep(int seconds) throws NoTrackersException, TrackerCommunicationException {
        this.backend.doRequest("sleep", new String[]{"duration", Integer.toString(seconds)});
    }

    public void rename(String fromKey, String toKey) throws NoTrackersException {
        int attempt = 1;
        while (this.maxRetries == -1 || attempt++ <= this.maxRetries) {
            try {
                this.backend.doRequest("rename", new String[]{"domain", this.domain, "from_key", fromKey, "to_key", toKey});
                return;
            }
            catch (TrackerCommunicationException e) {
                this.log.warn((Object)e);
                if (this.retrySleepTime > 0) {
                    try {
                        Thread.sleep(this.retrySleepTime);
                    }
                    catch (Exception e2) {
                        // empty catch block
                    }
                }
                this.reconnect();
            }
        }
        throw new NoTrackersException();
    }

    public String[] getPaths(String key, boolean noverify) throws NoTrackersException {
        int attempt = 1;
        while (this.maxRetries == -1 || attempt++ <= this.maxRetries) {
            try {
                Map response = this.backend.doRequest("get_paths", new String[]{"domain", this.domain, "key", key, "noverify", noverify ? "1" : "0"});
                if (response == null) {
                    return null;
                }
                int pathCount = Integer.parseInt((String)response.get("paths"));
                String[] paths = new String[pathCount];
                for (int i = 1; i <= pathCount; ++i) {
                    String path;
                    paths[i - 1] = path = (String)response.get("path" + i);
                }
                return paths;
            }
            catch (TrackerCommunicationException e) {
                this.log.warn((Object)e);
                if (this.retrySleepTime > 0) {
                    try {
                        Thread.sleep(this.retrySleepTime);
                    }
                    catch (Exception e2) {
                        // empty catch block
                    }
                }
                this.reconnect();
            }
        }
        throw new NoTrackersException();
    }

    public String getTracker() {
        return this.backend.getTracker();
    }
}

