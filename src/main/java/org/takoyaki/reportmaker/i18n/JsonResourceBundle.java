package org.takoyaki.reportmaker.i18n;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.ResourceBundle;

public final class JsonResourceBundle extends ResourceBundle {
    private final Map<String, String> messages;

    public JsonResourceBundle(Map<String, String> messages) {
        this.messages = Map.copyOf(messages);
    }

    @Override
    protected Object handleGetObject(String key) {
        return messages.get(key);
    }

    @Override
    public Enumeration<String> getKeys() {
        return Collections.enumeration(messages.keySet());
    }
}
