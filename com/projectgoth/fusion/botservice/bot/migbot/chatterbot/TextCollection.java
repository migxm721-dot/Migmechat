/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.botservice.bot.migbot.chatterbot;

import com.projectgoth.fusion.botservice.bot.migbot.chatterbot.Text;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.MessageBundle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.log4j.Logger;

public abstract class TextCollection {
    static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(TextCollection.class));
    protected ArrayList<Text> texts = new ArrayList();
    private int nextTextIndex = 0;
    private boolean shuffled = false;
    private String code;
    private String displayName;

    public String getCode() {
        return this.code;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public TextCollection(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public Text getNextText() {
        if (!this.shuffled) {
            Collections.shuffle(this.texts);
            this.shuffled = true;
        }
        Text next = this.texts.get(this.nextTextIndex);
        ++this.nextTextIndex;
        if (this.nextTextIndex >= this.texts.size()) {
            Collections.shuffle(this.texts);
            this.nextTextIndex = 0;
        }
        return next;
    }

    public void loadTexts(String bundleName, Locale locale) {
        ResourceBundle textsRb = MessageBundle.getBundle(bundleName, locale);
        if (textsRb != null) {
            Enumeration<String> keys = textsRb.getKeys();
            while (keys.hasMoreElements()) {
                String key = keys.nextElement();
                String value = textsRb.getString(key);
                try {
                    int textNumber = Integer.parseInt(key);
                    Text text = new Text(textNumber, value);
                    if (text == null) continue;
                    this.texts.add(text);
                }
                catch (NumberFormatException nfe) {
                    log.warn((Object)("Unable to parse the question number: " + key));
                }
            }
        } else {
            log.warn((Object)"Couldn't retrieve resource bundle for question pack");
        }
    }
}

