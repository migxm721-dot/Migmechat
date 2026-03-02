/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.maintenance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class ImportVirtualGifts {
    private static Set<String> giftNames = new HashSet<String>();

    public static void main(String[] args) {
        File startingDirectory = new File("/Users/dave/Documents/mig33/Nov 2009 Emoticons and Gifts/07-31 December 2009_Release");
        ImportVirtualGifts.getGiftNames(startingDirectory);
        ImportVirtualGifts.createInsertStatements();
        System.out.println("\n" + giftNames.size() + " gifts");
    }

    private static void getGiftNames(File dir) {
        File[] filesAndDirs;
        for (File file : filesAndDirs = dir.listFiles()) {
            if (file.isDirectory()) {
                ImportVirtualGifts.getGiftNames(file);
                continue;
            }
            if (!file.isFile() || !file.getName().endsWith("gif") && !file.getName().endsWith("png")) continue;
            giftNames.add(file.getName().substring(0, file.getName().length() - 7));
            try {
                ImportVirtualGifts.copy(file.getPath(), "/Users/dave/Documents/mig33/Nov 2009 Emoticons and Gifts/All Gift Images/" + file.getName());
            }
            catch (IOException e) {
                System.err.println("Unable to copy " + file.getName() + ": " + e.getMessage());
            }
        }
    }

    private static void createInsertStatements() {
        System.out.println("INSERT INTO virtualgift (name, hotkey, price, currency, numsold, groupid, groupviponly, location12x12gif, location12x12png, location14x14gif, location14x14png, location16x16gif, location16x16png, location64x64png, status) VALUES ");
        for (String s : giftNames) {
            System.out.println("('" + s + "', '(vg_" + s + ")', $, 'USD', 0, NULL, NULL, " + "'/usr/fusion/emoticons/virtualgifts/" + s + "_12.gif', " + "'/usr/fusion/emoticons/virtualgifts/" + s + "_12.png', " + "'/usr/fusion/emoticons/virtualgifts/" + s + "_14.gif', " + "'/usr/fusion/emoticons/virtualgifts/" + s + "_14.png', " + "'/usr/fusion/emoticons/virtualgifts/" + s + "_16.gif', " + "'/usr/fusion/emoticons/virtualgifts/" + s + "_16.png', " + "'/usr/fusion/emoticons/virtualgifts/" + s + "_64.png', 0),");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void copy(String fromFileName, String toFileName) throws IOException {
        IOException e22;
        FileOutputStream to;
        block24: {
            File fromFile = new File(fromFileName);
            File toFile = new File(toFileName);
            if (!fromFile.exists()) {
                throw new IOException("FileCopy: no such source file: " + fromFileName);
            }
            if (!fromFile.isFile()) {
                throw new IOException("FileCopy: can't copy directory: " + fromFileName);
            }
            if (!fromFile.canRead()) {
                throw new IOException("FileCopy: source file is unreadable: " + fromFileName);
            }
            if (toFile.isDirectory()) {
                toFile = new File(toFile, fromFile.getName());
            }
            if (toFile.exists()) {
                if (!toFile.canWrite()) {
                    throw new IOException("FileCopy: destination file is unwriteable: " + toFileName);
                }
                System.out.print("Overwrite existing file " + toFile.getName() + "? (Y/N): ");
                System.out.flush();
                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                String response = in.readLine();
                if (!response.equals("Y") && !response.equals("y")) {
                    throw new IOException("FileCopy: existing file was not overwritten.");
                }
            } else {
                File dir;
                String parent = toFile.getParent();
                if (parent == null) {
                    parent = System.getProperty("user.dir");
                }
                if (!(dir = new File(parent)).exists()) {
                    throw new IOException("FileCopy: destination directory doesn't exist: " + parent);
                }
                if (dir.isFile()) {
                    throw new IOException("FileCopy: destination is not a directory: " + parent);
                }
                if (!dir.canWrite()) {
                    throw new IOException("FileCopy: destination directory is unwriteable: " + parent);
                }
            }
            FileInputStream from = null;
            to = null;
            try {
                int bytesRead;
                from = new FileInputStream(fromFile);
                to = new FileOutputStream(toFile);
                byte[] buffer = new byte[4096];
                while ((bytesRead = from.read(buffer)) != -1) {
                    to.write(buffer, 0, bytesRead);
                }
                Object var9_8 = null;
                if (from == null) break block24;
            }
            catch (Throwable throwable) {
                IOException e22;
                Object var9_9 = null;
                if (from != null) {
                    try {
                        from.close();
                    }
                    catch (IOException e22) {
                        // empty catch block
                    }
                }
                if (to != null) {
                    try {
                        to.close();
                    }
                    catch (IOException e22) {
                        // empty catch block
                    }
                }
                throw throwable;
            }
            try {
                from.close();
            }
            catch (IOException e22) {
                // empty catch block
            }
        }
        if (to != null) {
            try {
                to.close();
            }
            catch (IOException e22) {}
        }
    }
}

