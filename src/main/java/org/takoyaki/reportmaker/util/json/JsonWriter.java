package org.takoyaki.reportmaker.util.json;

import java.util.Iterator;
import java.util.Map;

public final class JsonWriter {
    private static final String INDENT = "  ";

    private JsonWriter() {
    }

    public static String write(JsonValue value) {
        StringBuilder output = new StringBuilder();
        append(value, output, 0);
        output.append('\n');
        return output.toString();
    }

    private static void append(JsonValue value, StringBuilder output, int depth) {
        if (value instanceof JsonObject object) appendObject(object, output, depth);
        else if (value instanceof JsonArray array) appendArray(array, output, depth);
        else if (value instanceof JsonString string) appendString(string.value(), output);
        else if (value instanceof JsonNumber number) output.append(number.value().toPlainString());
        else if (value instanceof JsonBoolean bool) output.append(bool.value());
        else output.append("null");
    }

    private static void appendObject(JsonObject object, StringBuilder output, int depth) {
        output.append('{');
        Iterator<Map.Entry<String, JsonValue>> entries = object.values().entrySet().iterator();
        if (entries.hasNext()) output.append('\n');
        while (entries.hasNext()) {
            Map.Entry<String, JsonValue> entry = entries.next();
            indent(output, depth + 1);
            appendString(entry.getKey(), output);
            output.append(": ");
            append(entry.getValue(), output, depth + 1);
            output.append(entries.hasNext() ? ",\n" : "\n");
        }
        if (!object.values().isEmpty()) indent(output, depth);
        output.append('}');
    }

    private static void appendArray(JsonArray array, StringBuilder output, int depth) {
        output.append('[');
        Iterator<JsonValue> values = array.values().iterator();
        if (values.hasNext()) output.append('\n');
        while (values.hasNext()) {
            indent(output, depth + 1);
            append(values.next(), output, depth + 1);
            output.append(values.hasNext() ? ",\n" : "\n");
        }
        if (!array.values().isEmpty()) indent(output, depth);
        output.append(']');
    }

    private static void appendString(String value, StringBuilder output) {
        output.append('"');
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            switch (character) {
                case '"' -> output.append("\\\"");
                case '\\' -> output.append("\\\\");
                case '\b' -> output.append("\\b");
                case '\f' -> output.append("\\f");
                case '\n' -> output.append("\\n");
                case '\r' -> output.append("\\r");
                case '\t' -> output.append("\\t");
                default -> {
                    if (character < 0x20) output.append("\\u%04x".formatted((int) character));
                    else output.append(character);
                }
            }
        }
        output.append('"');
    }

    private static void indent(StringBuilder output, int depth) {
        output.append(INDENT.repeat(depth));
    }
}
