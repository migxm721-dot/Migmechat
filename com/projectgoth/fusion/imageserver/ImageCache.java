/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.imageserver;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.imageserver.ImageItem;
import com.projectgoth.fusion.mogilefs.MogileFSManager;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.log4j.Logger;

public class ImageCache {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ImageCache.class));
    private MogileFSManager mogileFSManager;
    private Map<String, ImageItem> cache = new LinkedHashMap<String, ImageItem>();
    private int cacheSize;
    private int maxCacheSize;
    private boolean cacheOriginalImage;
    private boolean cacheScaledImage;
    private int hits;
    private int misses;
    private Object performanceCounterLock = new Object();

    public ImageCache(MogileFSManager mogileFSManager, int maxCacheSize, boolean cacheOriginalImage, boolean cacheScaledImage) {
        this.mogileFSManager = mogileFSManager;
        this.maxCacheSize = maxCacheSize;
        this.cacheOriginalImage = cacheOriginalImage;
        this.cacheScaledImage = cacheScaledImage;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String getPerformanceSummary() {
        Object object = this.performanceCounterLock;
        synchronized (object) {
            double hitRate = this.hits + this.misses == 0 ? 0.0 : (double)this.hits / (double)(this.hits + this.misses);
            return this.cache.size() + " files " + new DecimalFormat("0.0%").format(hitRate) + " hit rate (" + this.hits + " hits, " + this.misses + " misses)";
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void hit() {
        Object object = this.performanceCounterLock;
        synchronized (object) {
            if (++this.hits == Integer.MAX_VALUE) {
                this.hits = 0;
                this.misses = 0;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void miss() {
        Object object = this.performanceCounterLock;
        synchronized (object) {
            if (++this.misses == Integer.MAX_VALUE) {
                this.hits = 0;
                this.misses = 0;
            }
        }
    }

    public ImageItem getImage(String id, String format) throws Exception {
        ImageItem imageItem;
        if (log.isDebugEnabled()) {
            log.debug((Object)("Get image id = " + id + ", format = " + format));
        }
        if ((imageItem = this.getImageFromCache(id)) == null) {
            this.miss();
            imageItem = new ImageItem(this.mogileFSManager.getFile(id), format);
            if (this.cacheOriginalImage) {
                this.cacheImage(id, imageItem);
            }
        } else {
            this.hit();
            if (!imageItem.getFormat().equalsIgnoreCase(format)) {
                imageItem = new ImageItem(imageItem.getBytes(), format);
            }
        }
        return imageItem;
    }

    public ImageItem getImage(String id, String format, int width, int height, boolean keepAspectRatio, boolean crop, float compressionQuality) throws Exception {
        String key;
        ImageItem imageItem;
        if (log.isDebugEnabled()) {
            log.debug((Object)("Get image id = " + id + ", format = " + format + ", width = " + width + ", height = " + height + ", keep ratio = " + keepAspectRatio + ", crop = " + crop + ", compression quality = " + compressionQuality));
        }
        if ((imageItem = this.getImageFromCache(key = id + ";" + format + ";" + width + ";" + height + ";" + keepAspectRatio + ";" + crop + ";" + compressionQuality)) == null) {
            this.miss();
            imageItem = this.getImageFromCache(id);
            if (imageItem == null) {
                imageItem = new ImageItem(this.mogileFSManager.getFile(id));
                if (this.cacheOriginalImage) {
                    this.cacheImage(id, imageItem);
                }
            }
            imageItem = new ImageItem(imageItem.getBytes(), format, width, height, keepAspectRatio, crop, compressionQuality);
            if (this.cacheScaledImage) {
                this.cacheImage(key, imageItem);
            }
        } else {
            this.hit();
        }
        return imageItem;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ImageItem getImageFromCache(String key) {
        Map<String, ImageItem> map = this.cache;
        synchronized (map) {
            return this.cache.get(key);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void cacheImage(String key, ImageItem imageItem) {
        int imageSize = imageItem.size();
        if (this.maxCacheSize > 0 && imageSize < this.maxCacheSize) {
            Map<String, ImageItem> map = this.cache;
            synchronized (map) {
                if (!this.cache.containsKey(key)) {
                    Iterator<ImageItem> i = this.cache.values().iterator();
                    while (i.hasNext() && imageSize + this.cacheSize > this.maxCacheSize) {
                        this.cacheSize -= i.next().size();
                        i.remove();
                    }
                    this.cache.put(key, imageItem);
                    this.cacheSize += imageSize;
                }
            }
        }
    }
}

