package org.takoyaki.reportmaker.util.json;

public record JsonString(String value) implements JsonValue {
    public JsonString {
        value = value == null ? "" : value;
    }
}
