/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.common;

import com.projectgoth.fusion.common.Base64Util;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Captcha {
    private final String id;
    private final String answer;
    private final BufferedImage image;

    public Captcha(String id, String answer, BufferedImage image) {
        this.id = id;
        this.answer = answer;
        this.image = image;
    }

    public String getId() {
        return this.id;
    }

    public String getAnswer() {
        return this.answer;
    }

    public BufferedImage getImage() {
        return this.image;
    }

    public byte[] getImageByteArray(String format) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write((RenderedImage)this.image, format, out);
        return out.toByteArray();
    }

    public String getBase64EncodedString(String format) throws IOException {
        return Base64Util.encode(this.getImageByteArray(format));
    }
}

