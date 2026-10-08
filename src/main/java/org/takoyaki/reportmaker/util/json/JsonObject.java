package org.takoyaki.reportmaker.util.json;

import java.util.LinkedHashMap;
import java.util.Map;

public final class JsonObject implements JsonValue {
    private final Map<String, JsonValue> values = new LinkedHashMap<>();

    public JsonObject put(String key, JsonValue value) {
        values.put(key, value == null ? JsonNull.INSTANCE : value);
        return this;
    }

    public JsonObject put(String key, String value) {
        return put(key, new JsonString(value == null ? "" : value));
    }

    public JsonObject put(String key, int value) {
        return put(key, new JsonNumber(value));
    }

    public JsonValue get(String key) {
        JsonValue value = values.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Missing JSON property: " + key);
        }
        return value;
    }

    public String string(String key) {
        return ((JsonString) get(key)).value();
    }

    public int integer(String key) {
        return ((JsonNumber) get(key)).value().intValueExact();
    }

    public JsonArray array(String key) {
        return (JsonArray) get(key);
    }

    public JsonObject object(String key) {
        return (JsonObject) get(key);
    }

    public Map<String, JsonValue> values() {
        return Map.copyOf(values);
    }
}
