/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.json.JSONObject
 */
package com.projectgoth.fusion.search;

import com.projectgoth.fusion.search.ElasticSearch;
import com.projectgoth.fusion.search.VersionConflictException;
import java.util.Iterator;
import org.json.JSONObject;

public abstract class BaseIndex {
    private static final int MAX_UPDATE_ATTEMPTS = 20;
    private static final int SLEEP_BETWEEN_ATTEMPTS = 100;

    private static JSONObject getDocumentForUpdate(ElasticSearch.IndexType indexType, ElasticSearch.DocumentType documentType, int id, JSONObject defaultFields) throws Exception {
        JSONObject document = ElasticSearch.get(indexType, documentType, id);
        if (document == null) {
            document = new JSONObject();
            document.put("_version", -1);
            document.put("_source", (Object)new JSONObject());
            JSONObject source = document.getJSONObject("_source");
            Iterator it = defaultFields.keys();
            while (it.hasNext()) {
                String key = (String)it.next();
                source.put(key, defaultFields.get(key));
            }
        }
        return document;
    }

    protected static void updateDocument(ElasticSearch.IndexType indexType, ElasticSearch.DocumentType documentType, int id, JSONObject fieldsToUpdate, JSONObject defaultFields) throws Exception {
        for (int attempt = 1; attempt <= 20; ++attempt) {
            try {
                JSONObject document = BaseIndex.getDocumentForUpdate(indexType, documentType, id, defaultFields);
                JSONObject source = document.getJSONObject("_source");
                if (fieldsToUpdate != null) {
                    Iterator it = fieldsToUpdate.keys();
                    while (it.hasNext()) {
                        String key = (String)it.next();
                        source.put(key, fieldsToUpdate.get(key));
                    }
                }
                ElasticSearch.index(indexType, documentType, id, document.getInt("_version"), source);
            }
            catch (VersionConflictException e) {
                Thread.sleep(100L);
                continue;
            }
            return;
        }
        throw new Exception("Update timed out (too many VersionConflictExceptions)");
    }
}

