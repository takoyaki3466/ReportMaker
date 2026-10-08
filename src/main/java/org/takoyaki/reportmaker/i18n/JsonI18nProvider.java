package org.takoyaki.reportmaker.i18n;

import org.takoyaki.reportmaker.util.json.JsonObject;
import org.takoyaki.reportmaker.util.json.JsonParser;
import org.takoyaki.reportmaker.util.json.JsonString;
import org.takoyaki.reportmaker.util.json.JsonValue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class JsonI18nProvider implements I18nProvider {
    private final ResourceBundle bundle;

    public JsonI18nProvider(String resourcePath) {
        bundle = new JsonResourceBundle(loadMessages(resourcePath));
    }

    @Override
    public String text(String key, Object... arguments) {
        String pattern = bundle.getString(key);
        return arguments.length == 0 ? pattern : MessageFormat.format(pattern, arguments);
    }

    @Override
    public ResourceBundle bundle() {
        return bundle;
    }

    private Map<String, String> loadMessages(String resourcePath) {
        try (InputStream input = JsonI18nProvider.class.getResourceAsStream(resourcePath)) {
            if (input == null) {
                throw new MissingResourceException("I18n resource not found", getClass().getName(), resourcePath);
            }
            String json = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            JsonObject root = (JsonObject) JsonParser.parse(json);
            Map<String, String> messages = new LinkedHashMap<>();
            for (Map.Entry<String, JsonValue> entry : root.values().entrySet()) {
                if (!(entry.getValue() instanceof JsonString string)) {
                    throw new IllegalArgumentException("I18n value must be a string: " + entry.getKey());
                }
                messages.put(entry.getKey(), string.value());
            }
            return messages;
        } catch (IOException exception) {
            throw new MissingResourceException(exception.getMessage(), getClass().getName(), resourcePath);
        }
    }
}
