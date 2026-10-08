package org.takoyaki.reportmaker.util.json;

import java.util.ArrayList;
import java.util.List;

public final class JsonArray implements JsonValue {
    private final List<JsonValue> values = new ArrayList<>();

    public JsonArray add(JsonValue value) {
        values.add(value == null ? JsonNull.INSTANCE : value);
        return this;
    }

    public List<JsonValue> values() {
        return List.copyOf(values);
    }
}
