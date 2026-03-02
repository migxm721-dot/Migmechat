/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  sun.misc.BASE64Encoder
 */
package com.projectgoth.fusion.chat.external.msn;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import sun.misc.BASE64Encoder;

public class MSNObject {
    private String creator;
    private Type type;
    private String friendlyName;
    private String fileLocation;
    private byte[] fileContent;
    private String sha1d;
    private String sha1c;

    public MSNObject(String creator, String friendlyName, Type type, String fileLocation) throws IOException, NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
        BASE64Encoder encoder = new BASE64Encoder();
        this.creator = creator;
        this.type = type;
        this.friendlyName = friendlyName;
        this.fileLocation = "TFR2C.tmp";
        File file = new File(fileLocation);
        FileInputStream in = new FileInputStream(file);
        this.fileContent = new byte[(int)file.length()];
        int bytesRead = 0;
        int available = in.available();
        while (available > 0) {
            bytesRead += in.read(this.fileContent, bytesRead, available);
            available = in.available();
        }
        in.close();
        this.sha1d = encoder.encode(messageDigest.digest(this.fileContent));
        this.sha1c = encoder.encode(messageDigest.digest(this.getChecksumContent().getBytes()));
    }

    public byte[] getContent() {
        return this.fileContent;
    }

    private String getChecksumContent() {
        StringBuilder builder = new StringBuilder("Creator");
        builder.append(this.creator);
        builder.append("Size");
        builder.append(this.fileContent.length);
        builder.append("Type");
        builder.append(this.type.value);
        builder.append("Location");
        builder.append(this.fileLocation);
        builder.append("Friendly");
        builder.append(this.friendlyName);
        builder.append("SHA1D");
        builder.append(this.sha1d);
        return builder.toString();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("<msnobj Creator=\"");
        builder.append(this.creator);
        builder.append("\" Size=\"");
        builder.append(this.fileContent.length);
        builder.append("\" Type=\"");
        builder.append(this.type.value);
        builder.append("\" Location=\"");
        builder.append(this.fileLocation);
        builder.append("\" Friendly=\"");
        builder.append(this.friendlyName);
        builder.append("\" SHA1D=\"");
        builder.append(this.sha1d);
        builder.append("\" SHA1C=\"");
        builder.append(this.sha1c);
        builder.append("\"/>");
        return builder.toString();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Type {
        EMOTICON(2),
        AVATAR(3),
        BACKGROUND(5),
        WINK(8);

        private int value;

        private Type(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }
    }
}

