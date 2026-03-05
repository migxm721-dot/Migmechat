/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.common;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.MemCachedClientWrapper;
import com.projectgoth.fusion.common.MemCachedKeySpaces;
import com.projectgoth.fusion.common.StringUtil;
import org.apache.log4j.Logger;

public class MemCachedHelper {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(MemCachedHelper.class));

    public static void setUserAlias(String username, int userid, String alias) {
        MemCachedHelper.setUsernameIdMapping(username, userid);
        if (StringUtil.isBlank(alias)) {
            alias = "";
        }
        if (!StringUtil.isBlank(username)) {
            MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.USER_ALIAS_BY_USERNAME, username.toLowerCase(), alias);
        }
        if (userid != -1) {
            MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.USER_ALIAS_BY_USERID, "" + userid, alias);
        }
        if (!StringUtil.isBlank(alias)) {
            MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.USER_ID_BY_ALIAS, alias.toLowerCase(), userid);
            MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.USER_NAME_BY_ALIAS, alias.toLowerCase(), StringUtil.isBlank(username) ? "" : username);
        }
    }

    public static void clearUserAlias(String username, int userid, String alias) {
        MemCachedHelper.clearUsernameIdMapping(username, userid);
        if (!StringUtil.isBlank(username)) {
            MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.USER_ALIAS_BY_USERNAME, username.toLowerCase());
        }
        if (userid != -1) {
            MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.USER_ALIAS_BY_USERID, "" + userid);
        }
        if (!StringUtil.isBlank(alias)) {
            MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.USER_ID_BY_ALIAS, alias.toLowerCase());
            MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.USER_NAME_BY_ALIAS, alias.toLowerCase());
        }
    }

    public static void setUsernameIdMapping(String username, int userid) {
        if (StringUtil.isBlank(username) || userid == -1) {
            return;
        }
        MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.USER_ID, username.toLowerCase(), userid);
        MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.USER_NAME_BY_ID, "" + userid, username);
    }

    public static void clearUsernameIdMapping(String username, int userid) {
        if (!StringUtil.isBlank(username)) {
            MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.USER_ID, username.toLowerCase());
        }
        if (userid != -1) {
            MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.USER_NAME_BY_ID, "" + userid);
        }
    }
}

