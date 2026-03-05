/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 *  com.google.gson.reflect.TypeToken
 */
package com.projectgoth.fusion.rewardsystem.stateprocessors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.projectgoth.fusion.rewardsystem.stateprocessors.Bag;
import java.lang.reflect.Type;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class StringBagJSONSerde {
    private static final Type stringBagType = new TypeToken<Bag<String>>(){}.getType();
    private static final JsonSerDe<Bag<String>> bagSerDe = new JsonSerDe<Bag<String>>(){

        public Bag<String> deserialize(JsonElement element, Type t, JsonDeserializationContext ctx) throws JsonParseException {
            Bag<String> bag = new Bag<String>();
            if (element.isJsonObject()) {
                JsonObject jsonObject = element.getAsJsonObject();
                for (Map.Entry entry : jsonObject.entrySet()) {
                    bag.setCount((String)entry.getKey(), ((JsonElement)entry.getValue()).getAsInt());
                }
            } else {
                throw new JsonParseException("Element:[" + element + "] not a jsonObject");
            }
            return bag;
        }

        public JsonElement serialize(Bag<String> bag, Type t, JsonSerializationContext ctx) {
            JsonObject jsonObject = new JsonObject();
            for (Map.Entry<String, Integer> e : bag.entrySet()) {
                jsonObject.addProperty(e.getKey(), (Number)e.getValue());
            }
            return jsonObject;
        }
    };

    public static Bag<String> fromString(String serializedFormat) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(stringBagType, bagSerDe);
        Gson gson = gsonBuilder.create();
        return (Bag)gson.fromJson(serializedFormat, stringBagType);
    }

    public static String toString(Bag<String> items) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(stringBagType, bagSerDe);
        Gson gson = gsonBuilder.create();
        return gson.toJson(items, stringBagType);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static interface JsonSerDe<T>
    extends JsonDeserializer<T>,
    JsonSerializer<T> {
    }
}

