package org.takoyaki.reportmaker.i18n;

import java.util.ResourceBundle;

public final class I18n {
    private static final I18nProvider PROVIDER =
            new JsonI18nProvider("/org/takoyaki/reportmaker/i18n/ja.json");

    private I18n() {
    }

    public static String text(String key, Object... arguments) {
        return PROVIDER.text(key, arguments);
    }

    public static ResourceBundle bundle() {
        return PROVIDER.bundle();
    }
}
