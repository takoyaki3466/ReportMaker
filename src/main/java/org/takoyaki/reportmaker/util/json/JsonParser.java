package org.takoyaki.reportmaker.util.json;

import java.math.BigDecimal;

public final class JsonParser {
    private final String source;
    private int position;

    private JsonParser(String source) {
        this.source = source;
    }

    public static JsonValue parse(String source) {
        JsonParser parser = new JsonParser(source);
        JsonValue value = parser.readValue();
        parser.skipWhitespace();
        if (parser.position != source.length()) {
            throw parser.error("Unexpected trailing content");
        }
        return value;
    }

    private JsonValue readValue() {
        skipWhitespace();
        if (position >= source.length()) {
            throw error("Expected a JSON value");
        }
        return switch (source.charAt(position)) {
            case '{' -> readObject();
            case '[' -> readArray();
            case '"' -> new JsonString(readString());
            case 't' -> readLiteral("true", new JsonBoolean(true));
            case 'f' -> readLiteral("false", new JsonBoolean(false));
            case 'n' -> readLiteral("null", JsonNull.INSTANCE);
            default -> readNumber();
        };
    }

    private JsonObject readObject() {
        expect('{');
        JsonObject object = new JsonObject();
        skipWhitespace();
        if (consume('}')) {
            return object;
        }
        do {
            skipWhitespace();
            String key = readString();
            skipWhitespace();
            expect(':');
            object.put(key, readValue());
            skipWhitespace();
        } while (consume(','));
        expect('}');
        return object;
    }

    private JsonArray readArray() {
        expect('[');
        JsonArray array = new JsonArray();
        skipWhitespace();
        if (consume(']')) {
            return array;
        }
        do {
            array.add(readValue());
            skipWhitespace();
        } while (consume(','));
        expect(']');
        return array;
    }

    private String readString() {
        expect('"');
        StringBuilder result = new StringBuilder();
        while (position < source.length()) {
            char character = source.charAt(position++);
            if (character == '"') {
                return result.toString();
            }
            if (character != '\\') {
                result.append(character);
                continue;
            }
            if (position >= source.length()) {
                throw error("Incomplete escape sequence");
            }
            char escaped = source.charAt(position++);
            switch (escaped) {
                case '"', '\\', '/' -> result.append(escaped);
                case 'b' -> result.append('\b');
                case 'f' -> result.append('\f');
                case 'n' -> result.append('\n');
                case 'r' -> result.append('\r');
                case 't' -> result.append('\t');
                case 'u' -> result.append(readUnicode());
                default -> throw error("Unknown escape sequence");
            }
        }
        throw error("Unterminated string");
    }

    private char readUnicode() {
        if (position + 4 > source.length()) {
            throw error("Incomplete Unicode escape");
        }
        String digits = source.substring(position, position + 4);
        position += 4;
        try {
            return (char) Integer.parseInt(digits, 16);
        } catch (NumberFormatException exception) {
            throw error("Invalid Unicode escape");
        }
    }

    private JsonNumber readNumber() {
        int start = position;
        if (peek('-')) position++;
        readDigits();
        if (peek('.')) {
            position++;
            readDigits();
        }
        if (peek('e') || peek('E')) {
            position++;
            if (peek('+') || peek('-')) position++;
            readDigits();
        }
        if (start == position) {
            throw error("Expected a number");
        }
        try {
            return new JsonNumber(new BigDecimal(source.substring(start, position)));
        } catch (NumberFormatException exception) {
            throw error("Invalid number");
        }
    }

    private void readDigits() {
        int start = position;
        while (position < source.length() && Character.isDigit(source.charAt(position))) position++;
        if (start == position) throw error("Expected a digit");
    }

    private JsonValue readLiteral(String literal, JsonValue value) {
        if (!source.startsWith(literal, position)) throw error("Invalid literal");
        position += literal.length();
        return value;
    }

    private void skipWhitespace() {
        while (position < source.length() && Character.isWhitespace(source.charAt(position))) position++;
    }

    private boolean consume(char expected) {
        if (!peek(expected)) return false;
        position++;
        return true;
    }

    private boolean peek(char expected) {
        return position < source.length() && source.charAt(position) == expected;
    }

    private void expect(char expected) {
        if (!consume(expected)) throw error("Expected '" + expected + "'");
    }

    private IllegalArgumentException error(String message) {
        return new IllegalArgumentException(message + " at character " + position);
    }
}
