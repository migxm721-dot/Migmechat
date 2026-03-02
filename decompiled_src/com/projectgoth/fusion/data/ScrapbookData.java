/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.data;

import com.projectgoth.fusion.data.FileData;
import java.io.Serializable;
import java.util.Date;

public class ScrapbookData
implements Serializable {
    public Integer id;
    public String username;
    public String fileID;
    public Date dateCreated;
    public String receivedFrom;
    public String description;
    public StatusEnum status;
    public FileData file;

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum StatusEnum {
        INACTIVE(0),
        PRIVATE(1),
        PUBLIC(2),
        CONTACTS_ONLY(3),
        REPORTED(4);

        private int value;

        private StatusEnum(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static StatusEnum fromValue(int value) {
            for (StatusEnum e : StatusEnum.values()) {
                if (e.value() != value) continue;
                return e;
            }
            return null;
        }
    }
}

