/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.objectcache;

import com.projectgoth.fusion.common.ImageInfo;
import com.projectgoth.fusion.data.FileData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.MIS;
import com.projectgoth.fusion.interfaces.MISHome;
import com.projectgoth.fusion.objectcache.ObjectCacheContext;
import java.io.ByteArrayInputStream;
import java.util.Date;

public class ChatContentStore {
    private ObjectCacheContext ctx;

    public ChatContentStore(ObjectCacheContext ctx) {
        this.ctx = ctx;
    }

    public String storeImage(String sender, byte[] image) throws Exception {
        MIS misBean = (MIS)EJBHomeCache.getObject("ejb/MIS", MISHome.class);
        FileData fileData = new FileData();
        fileData.id = misBean.newFileID();
        fileData.dateCreated = new Date(System.currentTimeMillis());
        fileData.size = image.length;
        fileData.uploadedBy = sender;
        ImageInfo imageInfo = new ImageInfo();
        imageInfo.setInput(new ByteArrayInputStream(image));
        if (!imageInfo.check()) {
            throw new Exception("Invalid image input");
        }
        fileData.mimeType = "image/" + imageInfo.getFormatName().toLowerCase();
        fileData.width = imageInfo.getWidth();
        fileData.height = imageInfo.getHeight();
        this.ctx.getMogileFSManager().storeFile(fileData.id, "image", image);
        misBean.saveFile(fileData, null);
        return fileData.id;
    }
}

