/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.imageserver;

import com.projectgoth.fusion.common.ImageInfo;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.concurrent.Semaphore;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;

public class ImageItem {
    private static Semaphore imageIOSemaphore = new Semaphore(1);
    private String format;
    private byte[] bytes;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private BufferedImage readFromImageIO(InputStream input) throws IOException {
        try {
            imageIOSemaphore.acquireUninterruptibly();
            BufferedImage bufferedImage = ImageIO.read(input);
            Object var4_3 = null;
            imageIOSemaphore.release();
            return bufferedImage;
        }
        catch (Throwable throwable) {
            Object var4_4 = null;
            imageIOSemaphore.release();
            throw throwable;
        }
    }

    private ImageInfo getImageInfo(byte[] rawImage) throws IOException {
        ImageInfo info = new ImageInfo();
        info.setInput(new ByteArrayInputStream(rawImage));
        if (!info.check()) {
            throw new IOException("Invalid image input");
        }
        return info;
    }

    private BufferedImage copy(BufferedImage image, boolean alpha) {
        BufferedImage i = new BufferedImage(image.getWidth(), image.getHeight(), alpha ? 2 : 1);
        Graphics2D g = i.createGraphics();
        g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
        g.dispose();
        return i;
    }

    private BufferedImage scale(BufferedImage image, int width, int height, boolean alpha) {
        BufferedImage scaled = new BufferedImage(width, height, alpha ? 2 : 1);
        Graphics2D g = scaled.createGraphics();
        g.drawImage(image.getScaledInstance(width, height, 4), 0, 0, width, height, null);
        g.dispose();
        return scaled;
    }

    private BufferedImage cropAndScale(BufferedImage image, int width, int height, boolean alpha) {
        BufferedImage cropped;
        int imageHeight;
        double heightRatio;
        int imageWidth = image.getWidth();
        double widthRatio = (double)imageWidth / (double)width;
        if (widthRatio <= (heightRatio = (double)(imageHeight = image.getHeight()) / (double)height)) {
            int cropHeight = (int)((double)height * widthRatio);
            int cropY = (imageHeight - cropHeight) / 2;
            cropped = image.getSubimage(0, cropY, imageWidth, cropHeight);
        } else {
            int cropWidth = (int)((double)width * heightRatio);
            int cropX = (imageWidth - cropWidth) / 2;
            cropped = image.getSubimage(cropX, 0, cropWidth, imageHeight);
        }
        return this.scale(cropped, width, height, alpha);
    }

    private byte[] encode(BufferedImage image, String format, float compressionQuality) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        if ("jpeg".equals(format) && compressionQuality > 0.0f && compressionQuality <= 1.0f) {
            Iterator<ImageWriter> i = ImageIO.getImageWritersByFormatName(format);
            ImageWriter writer = i.next();
            ImageWriteParam iwp = writer.getDefaultWriteParam();
            iwp.setCompressionMode(2);
            iwp.setCompressionQuality(compressionQuality);
            MemoryCacheImageOutputStream output = new MemoryCacheImageOutputStream(out);
            writer.setOutput(output);
            IIOImage iio = new IIOImage(image, null, null);
            writer.write(null, iio, iwp);
        } else {
            ImageIO.write((RenderedImage)image, this.format, out);
        }
        byte[] ba = out.toByteArray();
        out.close();
        return ba;
    }

    private void load(byte[] rawImage, String format, int width, int height, boolean keepAspectRatio, boolean crop, float compressionQualtiy) throws IOException {
        boolean alpha;
        ImageInfo info = this.getImageInfo(rawImage);
        this.format = format == null ? (info.getFormat() == 2 ? info.getFormatName().toLowerCase() : "jpeg") : format;
        boolean convertFormat = !this.format.equalsIgnoreCase(info.getFormatName());
        boolean scaleImage = width > 0 && height > 0 && (width != info.getWidth() || height != info.getHeight());
        boolean bl = alpha = !convertFormat && info.getFormat() == 2;
        if (scaleImage) {
            if (keepAspectRatio) {
                if (crop) {
                    this.bytes = this.encode(this.cropAndScale(this.readFromImageIO(new ByteArrayInputStream(rawImage)), width, height, alpha), this.format, compressionQualtiy);
                } else {
                    double newRatio;
                    double originalRatio = (double)info.getWidth() / (double)info.getHeight();
                    if (originalRatio > (newRatio = (double)width / (double)height)) {
                        height = (int)((double)width / originalRatio);
                    } else if (originalRatio < newRatio) {
                        width = (int)((double)height * originalRatio);
                    }
                    this.bytes = this.encode(this.scale(this.readFromImageIO(new ByteArrayInputStream(rawImage)), width, height, alpha), this.format, compressionQualtiy);
                }
            } else {
                this.bytes = this.encode(this.scale(this.readFromImageIO(new ByteArrayInputStream(rawImage)), width, height, alpha), this.format, compressionQualtiy);
            }
        } else {
            this.bytes = convertFormat ? this.encode(this.copy(this.readFromImageIO(new ByteArrayInputStream(rawImage)), alpha), this.format, compressionQualtiy) : rawImage;
        }
    }

    public ImageItem(byte[] rawImage) throws IOException {
        this.load(rawImage, null, -1, -1, false, false, 0.0f);
    }

    public ImageItem(byte[] rawImage, String format) throws IOException {
        this.load(rawImage, format, -1, -1, false, false, 0.0f);
    }

    public ImageItem(byte[] rawImage, String format, int width, int height, boolean keepAspectRatio, boolean crop, float compressionQuality) throws IOException {
        this.load(rawImage, format, width, height, keepAspectRatio, crop, compressionQuality);
    }

    public String getFormat() {
        return this.format;
    }

    public byte[] getBytes() {
        return this.bytes;
    }

    public int size() {
        return this.bytes.length;
    }

    public static void setImageIOSemaphore(Semaphore imageIOSemaphore) {
        ImageItem.imageIOSemaphore = imageIOSemaphore;
    }
}

